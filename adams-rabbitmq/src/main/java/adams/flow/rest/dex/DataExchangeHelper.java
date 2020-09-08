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
import adams.flow.container.HttpRequestResult;
import adams.flow.rest.dex.DataExchange.TokenMessage;
import adams.flow.rest.dex.clientauthentication.AbstractClientAuthentication;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fracpete.requests4j.Requests;
import com.github.fracpete.requests4j.core.MediaTypeHelper;
import com.github.fracpete.requests4j.request.Request;
import com.github.fracpete.requests4j.response.BasicResponse;

import java.io.ByteArrayInputStream;
import java.io.File;

/**
 * Helper class for communicating with a Data Exchange server.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class DataExchangeHelper {

  protected static Request initRequest(BaseURL server, AbstractClientAuthentication auth, MessageCollection errors) {
    Request 		result;
    BaseKeyValuePair[]	authPairs;

    authPairs = auth.generate(errors);
    if (!errors.isEmpty())
      return null;

    result = Requests.post(server.urlValue());
    for (BaseKeyValuePair authPair: authPairs)
      result.formData().add(authPair.getPairKey(), authPair.getPairValue());

    return result;
  }

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
    Request		request;
    BasicResponse 	response;
    ObjectMapper	mapper;
    TokenMessage	tokenMsg;

    request = initRequest(server, auth, errors);
    if (request == null)
      return null;

    try {
      request.formData().addFile(DataExchange.PARAMKEY_PAYLOAD, file.getAbsolutePath());
      response = request.execute();
      if (response.statusCode() == 200) {
	mapper   = new ObjectMapper();
	tokenMsg = mapper.readValue("" + response.text(), TokenMessage.class);
	return tokenMsg.getToken();
      }
      else {
	errors.add(HttpRequestResult.VALUE_STATUSCODE + ": " + response.statusCode());
	errors.add(HttpRequestResult.VALUE_STATUSMESSAGE + ": " + response.statusMessage());
	errors.add(HttpRequestResult.VALUE_BODY + ": " + response.text());
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
    Request		request;
    BasicResponse 	response;
    ObjectMapper	mapper;
    TokenMessage	tokenMsg;

    request = initRequest(server, auth, errors);
    if (request == null)
      return null;

    try {
      request.formData().addStream(DataExchange.PARAMKEY_PAYLOAD, "data.ser", MediaTypeHelper.OCTECT_STREAM, new ByteArrayInputStream(data));
      response = request.execute();
      if (response.statusCode() == 200) {
	mapper   = new ObjectMapper();
	tokenMsg = mapper.readValue("" + response.text(), TokenMessage.class);
	return tokenMsg.getToken();
      }
      else {
	errors.add(HttpRequestResult.VALUE_STATUSCODE + ": " + response.statusCode());
	errors.add(HttpRequestResult.VALUE_STATUSMESSAGE + ": " + response.statusMessage());
	errors.add(HttpRequestResult.VALUE_BODY + ": " + response.text());
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
    Request			request;
    BasicResponse 		response;

    request = initRequest(server, auth, errors);
    if (request == null)
      return null;

    try {
      request.formData().add(DataExchange.PARAMVALUE_TOKEN, token);
      response = request.execute();
      if (response.statusCode() == 200) {
	return response.body();
      }
      else {
        errors.add("Failed to download data with token '" + token + "' from server '" + server + "': " + response);
	return null;
      }
    }
    catch (Exception e) {
      errors.add("Failed to download data with token '" + token + "' from: " + server, e);
    }

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
    Request		request;
    BasicResponse 	response;

    request = initRequest(server, auth, errors);
    if (request == null)
      return null;

    try {
      request.formData().add(DataExchange.PARAMVALUE_TOKEN, token);
      response = request.execute();
      return new HttpRequestResult(response.statusCode(), response.statusMessage(), response.text());
    }
    catch (Exception e) {
      errors.add("Failed to remove data with token '" + token + "' from: " + server, e);
    }

    return null;
  }

  /**
   * Builds the actual URL to use.
   *
   * @param server 	the server URL
   * @param path  	the path to append to the server's URL
   * @return		the complete URL
   */
  public static BaseURL buildURL(BaseURL server, String path) {
    String	url;

    if (path.startsWith("/"))
      path = path.substring(1);
    url = server.getValue();
    if (!url.endsWith("/"))
      url += "/";
    url += path;

    return new BaseURL(url);
  }
}
