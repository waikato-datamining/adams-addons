/*
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * DataExchangeHelper.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.rest.dex;

import adams.core.MessageCollection;
import adams.core.base.BaseKeyValuePair;
import adams.core.base.BaseURL;
import adams.core.net.HttpRequestHelper;
import adams.flow.container.HttpRequestResult;
import adams.flow.rest.dex.DataExchange.TokenMessage;
import adams.flow.rest.dex.clientauthentication.AbstractClientAuthentication;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.apache.tika.mime.MediaType;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Helper class for communicating with a Data Exchange server.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class DataExchangeHelper {

  /**
   * Uploads the file to the data exchange server.
   *
   * @param file	the file to upload
   * @param server	the server to upload to
   * @param auth	the authentication to use, needs to have the flow context set
   * @param errors	for collecting errors
   * @return		the token, null in case of error
   */
  public static String upload(File file, BaseURL server, AbstractClientAuthentication auth, MessageCollection errors) {
    BaseKeyValuePair[]	authPairs;
    HttpRequestResult	response;
    ObjectMapper	mapper;
    TokenMessage	tokenMsg;

    authPairs = auth.generate(errors);
    if (!errors.isEmpty())
      return null;

    try {
      response = HttpRequestHelper.post(server, authPairs, DataExchange.PARAMKEY_PAYLOAD, file);
      if (response.getValue(HttpRequestResult.VALUE_STATUSCODE, Integer.class) == 200) {
	mapper   = new ObjectMapper();
	tokenMsg = mapper.readValue("" + response.getValue(HttpRequestResult.VALUE_BODY), TokenMessage.class);
	return tokenMsg.getToken();
      }
      else {
	errors.add(HttpRequestResult.VALUE_STATUSCODE + ": " + response.getValue(HttpRequestResult.VALUE_STATUSCODE));
	errors.add(HttpRequestResult.VALUE_STATUSMESSAGE + ": " + response.getValue(HttpRequestResult.VALUE_STATUSMESSAGE));
	errors.add(HttpRequestResult.VALUE_BODY + ": " + response.getValue(HttpRequestResult.VALUE_BODY));
      }
    }
    catch (Exception e) {
      errors.add("Failed to upload file '" + file + "' to: " + server, e);
    }

    return null;
  }

  /**
   * Uploads the file to the data exchange server.
   *
   * @param data	the data to upload
   * @param server	the server to upload to
   * @param auth	the authentication to use, needs to have the flow context set
   * @param errors	for collecting errors
   * @return		the token, null in case of error
   */
  public static String upload(byte[] data, BaseURL server, AbstractClientAuthentication auth, MessageCollection errors) {
    BaseKeyValuePair[]		authPairs;
    ByteArrayInputStream	bis;
    HttpRequestResult		response;
    ObjectMapper		mapper;
    TokenMessage		tokenMsg;

    authPairs = auth.generate(errors);
    if (!errors.isEmpty())
      return null;

    try {
      bis      = new ByteArrayInputStream(data);
      response = HttpRequestHelper.post(server, authPairs, DataExchange.PARAMKEY_PAYLOAD, "data.ser", MediaType.OCTET_STREAM, bis);
      if (response.getValue(HttpRequestResult.VALUE_STATUSCODE, Integer.class) == 200) {
	mapper   = new ObjectMapper();
	tokenMsg = mapper.readValue("" + response.getValue(HttpRequestResult.VALUE_BODY), TokenMessage.class);
	return tokenMsg.getToken();
      }
      else {
	errors.add(HttpRequestResult.VALUE_STATUSCODE + ": " + response.getValue(HttpRequestResult.VALUE_STATUSCODE));
	errors.add(HttpRequestResult.VALUE_STATUSMESSAGE + ": " + response.getValue(HttpRequestResult.VALUE_STATUSMESSAGE));
	errors.add(HttpRequestResult.VALUE_BODY + ": " + response.getValue(HttpRequestResult.VALUE_BODY));
      }
    }
    catch (Exception e) {
      errors.add("Failed to upload data to: " + server, e);
    }

    return null;
  }

  /**
   * Downloads the data associated with the token.
   *
   * @param token	the token to use for downloading
   * @param server	the server to connect to
   * @param auth	the authentication to use
   * @param errors	for collecting  errors
   * @return		the data, null in case of an error
   */
  public static byte[] download(String token, BaseURL server, AbstractClientAuthentication auth, MessageCollection errors) {
    URL 			url;
    HttpURLConnection 		conn;
    BaseKeyValuePair[] 		authPairs;
    List<BaseKeyValuePair>	params;
    BufferedInputStream 	input;
    ByteArrayOutputStream 	bos;
    OutputStream 		out;
    OutputStreamWriter 		writer;
    String			boundary;

    authPairs = auth.generate(errors);
    if (!errors.isEmpty())
      return null;

    url  = server.urlValue();
    conn = null;
    params = new ArrayList<>();
    params.addAll(Arrays.asList(authPairs));
    params.add(new BaseKeyValuePair(DataExchange.PARAMVALUE_TOKEN, token));
    boundary = HttpRequestHelper.createBoundary();
    try {
      bos  = new ByteArrayOutputStream();
      conn = (HttpURLConnection) url.openConnection();
      conn.setDoOutput(true);
      conn.setRequestMethod("POST");
      conn.setRequestProperty("Content-Type", javax.ws.rs.core.MediaType.MULTIPART_FORM_DATA);

      out = conn.getOutputStream();
      writer = new OutputStreamWriter(out);
      writer.write("\n\n");

      // form parameters
      for (BaseKeyValuePair param : params) {
	writer.write("--" + boundary + "\r\n");
	writer.write("Content-Disposition: form-data; name=\"" + param.getPairKey() + "\"\r\n");
	writer.write("\r\n");
	writer.write(param.getPairValue());
	writer.write("\r\n");
      }

      // finish
      writer.write("\r\n--" + boundary + "--\r\n");
      writer.flush();
      writer.close();
      out.flush();
      out.close();

      input = new BufferedInputStream(conn.getInputStream());
      IOUtils.copy(input, bos);
      return bos.toByteArray();
    }
    catch (Exception e) {
      errors.add("Failed to download data with token '" + token + "' from: " + server, e);
    }

    if (conn != null)
      conn.disconnect();

    return null;
  }

  /**
   * Removes the data associated with the token.
   *
   * @param token	the token to remove the data for
   * @param server	the server to connect to
   * @param auth	the authentication to use
   * @param errors	for collecting  errors
   * @return		the request response, null in case of an error
   */
  public static HttpRequestResult remove(String token, BaseURL server, AbstractClientAuthentication auth, MessageCollection errors) {
    BaseKeyValuePair[] 		authPairs;
    List<BaseKeyValuePair> 	params;

    authPairs = auth.generate(errors);
    if (!errors.isEmpty())
      return null;

    params = new ArrayList<>();
    params.add(new BaseKeyValuePair(DataExchange.PARAMVALUE_TOKEN, token));
    params.addAll(Arrays.asList(authPairs));
    try {
      return HttpRequestHelper.post(server, params.toArray(new BaseKeyValuePair[0]));
    }
    catch (Exception e) {
      errors.add("Failed to remove data with token '" + token + "' from: " + server, e);
    }

    return null;
  }
}
