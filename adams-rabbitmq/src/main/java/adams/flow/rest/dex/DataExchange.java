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
 * DataExchange.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.rest.dex;

import adams.flow.rest.AbstractRESTPlugin;
import adams.flow.rest.dex.authentication.AbstractAuthentication;
import adams.flow.rest.dex.authentication.NoAuthenticationRequired;
import adams.flow.rest.dex.backend.AbstractBackend;
import adams.flow.rest.dex.backend.InMemory;
import com.fasterxml.jackson.databind.ObjectMapper;
import gnu.trove.list.TByteList;
import gnu.trove.list.array.TByteArrayList;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 <!-- globalinfo-start -->
 <!-- globalinfo-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class DataExchange
  extends AbstractRESTPlugin {

  private static final long serialVersionUID = -5218893638471880150L;

  public static final String PARAMKEY_NAME = "name";

  public static final String PARAMKEY_PAYLOAD = "payload";

  public static final String PARAMVALUE_TOKEN = "token";

  /**
   * Wrapper class for the token.
   */
  public static class TokenMessage {

    protected String m_Token;

    public TokenMessage() {
      this(null);
    }

    public TokenMessage(String token) {
      setToken(token);
    }

    public void setToken(String value) {
      m_Token = value;
    }

    public String getToken() {
      return m_Token;
    }
  }

  /** the authentication scheme. */
  protected AbstractAuthentication m_Authentication;

  /** the backend in use. */
  protected AbstractBackend m_Backend;

  /** the object mapper to use. */
  protected transient ObjectMapper m_Mapper;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return
      "Allows clients to upload/download data.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "authentication", "authentication",
      new NoAuthenticationRequired());

    m_OptionManager.add(
      "backend", "backend",
      new InMemory());
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Mapper = null;
  }

  /**
   * Sets the authentication scheme.
   *
   * @param value	the scheme
   */
  public void setAuthentication(AbstractAuthentication value) {
    m_Authentication = value;
    reset();
  }

  /**
   * Returns the authentication scheme.
   *
   * @return		the scheme
   */
  public AbstractAuthentication getAuthentication() {
    return m_Authentication;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String authenticationTipText() {
    return "The scheme to use for authenticating clients.";
  }

  /**
   * Sets the backend scheme.
   *
   * @param value	the scheme
   */
  public void setBackend(AbstractBackend value) {
    m_Backend = value;
    reset();
  }

  /**
   * Returns the backend scheme.
   *
   * @return		the scheme
   */
  public AbstractBackend getBackend() {
    return m_Backend;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String backendTipText() {
    return "The scheme to use for managing the uploaded data.";
  }

  /**
   * Logs the error and generates a JSON response with the error.
   *
   * @param msg		the error message
   * @return		the generated JSON
   */
  protected Response handleError(String msg) {
    getLogger().severe(msg);
    return Response.status(500, msg).build();
  }

  /**
   * Handles the upload of data.
   *
   * @param body	the form data
   * @return		the generated JSON reply.
   * 			Either contains an element "error" if failed
   * 			or "token" with the generated that the data item can
   * 			now be accessed with.
   */
  @POST
  @Path("/upload")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_JSON)
  public Response upload(MultipartBody body) {
    Map<String,String> 	parameters;
    TByteList 		payload;
    String 		name;
    InputStream		is;
    int			data;
    String		token;
    String		msg;
    String		json;

    parameters = new HashMap<>();
    payload    = new TByteArrayList();

    // get parameters and payload
    for (Attachment att: body.getAllAttachments()) {
      name = att.getContentDisposition().getParameter(PARAMKEY_NAME);
      if (name != null) {
        if (name.equals(PARAMKEY_PAYLOAD)) {
          try {
	    is = att.getDataHandler().getInputStream();
	    while ((data = is.read()) != -1)
	      payload.add((byte) data);
	  }
	  catch (Exception e) {
            payload = null;
	  }
	}
	else {
          parameters.put(name, att.getObject(String.class).trim());
	}
      }
    }

    if (isLoggingEnabled())
      getLogger().fine("Parameters: " + parameters);

    // authenticate request
    if (!(m_Authentication instanceof NoAuthenticationRequired)) {
      msg = m_Authentication.authenticate(parameters);
      if (msg != null)
	return handleError(msg);
      else if (isLoggingEnabled())
	getLogger().info("Authentication successful!");
    }

    // no payload?
    if (payload == null)
      return handleError("No payload provided!");

    // store in backend
    m_Backend.initBackend();
    token = m_Backend.add(payload.toArray());
    if (token == null)
      return handleError("Failed to add payload!");
    else if (isLoggingEnabled())
      getLogger().info("Data stored under: " + token);

    if (m_Mapper == null)
      m_Mapper = new ObjectMapper();
    try {
      json = m_Mapper.writeValueAsString(new TokenMessage(token));
    }
    catch (Exception e) {
      return handleError("Failed to generate response with token!");
    }
    return Response.ok(json, MediaType.APPLICATION_JSON).build();
  }

  /**
   * Handles the download of data.
   *
   * @param body	the form data
   * @return		the generated JSON reply.
   * 			Returns the requested data.
   */
  @POST
  @Path("/download")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_OCTET_STREAM)
  public Response download(MultipartBody body) {
    Map<String,String> 	parameters;
    byte[] 		data;
    String 		name;
    String		token;
    String		msg;

    parameters = new HashMap<>();
    token      = null;

    // get parameters and payload
    for (Attachment att: body.getAllAttachments()) {
      name = att.getContentDisposition().getParameter(PARAMKEY_NAME);
      if (name != null) {
        if (name.equals(PARAMVALUE_TOKEN)) {
          token = att.getObject(String.class).trim();
	}
	else {
          parameters.put(name, att.getObject(String.class).trim());
	}
      }
    }

    if (isLoggingEnabled())
      getLogger().fine("Parameters: " + parameters);

    // authenticate request
    if (!(m_Authentication instanceof NoAuthenticationRequired)) {
      msg = m_Authentication.authenticate(parameters);
      if (msg != null)
	return handleError(msg);
      else if (isLoggingEnabled())
	getLogger().info("Authentication successful!");
    }

    // no token?
    if (token == null)
      return handleError("No token provided!");

    // get data from backend
    m_Backend.purge();
    data = m_Backend.get(token);
    if (data == null)
      return handleError("No data for token available: " + token);
    else if (isLoggingEnabled())
      getLogger().info("Data retrieved for: " + token);

    return Response.ok(data, MediaType.APPLICATION_OCTET_STREAM).build();
  }

  /**
   * Handles the removal of data.
   *
   * @param body	the form data
   * @return		the generated JSON reply.
   */
  @POST
  @Path("/remove")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  public Response remove(MultipartBody body) {
    Map<String,String> 	parameters;
    String 		name;
    String		token;
    String		msg;

    parameters = new HashMap<>();
    token      = null;

    // get parameters and payload
    for (Attachment att: body.getAllAttachments()) {
      name = att.getContentDisposition().getParameter(PARAMKEY_NAME);
      if (name != null) {
        if (name.equals(PARAMVALUE_TOKEN)) {
          token = att.getObject(String.class).trim();
	}
	else {
          parameters.put(name, att.getObject(String.class).trim());
	}
      }
    }

    if (isLoggingEnabled())
      getLogger().fine("Parameters: " + parameters);

    // authenticate request
    if (!(m_Authentication instanceof NoAuthenticationRequired)) {
      msg = m_Authentication.authenticate(parameters);
      if (msg != null)
	return handleError(msg);
      else if (isLoggingEnabled())
	getLogger().info("Authentication successful!");
    }

    // no token?
    if (token == null)
      return handleError("No token provided!");

    // get data from backend
    m_Backend.purge();
    m_Backend.remove(token);
    if (isLoggingEnabled())
      getLogger().info("Data removed for: " + token);

    return Response.ok().build();
  }
}
