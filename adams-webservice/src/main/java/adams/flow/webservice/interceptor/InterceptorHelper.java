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

/**
 * InterceptorHelper.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.webservice.interceptor;

import java.io.InputStream;
import java.io.Reader;
import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.cxf.common.util.StringUtils;
import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.helpers.XMLUtils;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.LoggingMessage;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.io.CachedWriter;
import org.apache.cxf.io.DelegatingInputStream;
import org.apache.cxf.message.Message;

import adams.core.License;
import adams.core.annotation.MixedCopyright;
import adams.flow.webservice.interceptor.outgoing.BaseLogging;

/**
 * Helper class for interceptor related stuff.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
@MixedCopyright(
    copyright = "Apache CXF",
    license = License.APACHE2,
    note = "org.apache.cxf.interceptor.LoggingOutInterceptor#setupBuffer and org.apache.cxf.interceptor.AbstractLoggingInterceptor#writePayload and org.apache.cxf.interceptor.LoggingInInterceptor#logging"
)
public class InterceptorHelper {

  public static final String OUTGOING_BINARY_CONTENT_MESSAGE = "--- Binary Content ---";

  public static final String OUTGOING_LOG_SETUP = BaseLogging.class.getName() + ".log-setup";

  /**
   * Configures the buffer for the outgoing message.
   * 
   * @param message	the message to configure the buffer for
   * @return		the configured buffer
   */
  public static LoggingMessage setupOutgoingBuffer(Message message) {
    String id = (String)message.getExchange().get(LoggingMessage.ID_KEY);
    if (id == null) {
      id = LoggingMessage.nextId();
      message.getExchange().put(LoggingMessage.ID_KEY, id);
    }
    final LoggingMessage buffer = new LoggingMessage("Outbound Message\n---------------------------", id);

    Integer responseCode = (Integer)message.get(Message.RESPONSE_CODE);
    if (responseCode != null)
      buffer.getResponseCode().append(responseCode);

    String encoding = (String)message.get(Message.ENCODING);
    if (encoding != null)
      buffer.getEncoding().append(encoding);
    
    String httpMethod = (String)message.get(Message.HTTP_REQUEST_METHOD);
    if (httpMethod != null)
      buffer.getHttpMethod().append(httpMethod);
    
    String address = (String)message.get(Message.ENDPOINT_ADDRESS);
    if (address != null)
      buffer.getAddress().append(address);
    
    String ct = (String)message.get(Message.CONTENT_TYPE);
    if (ct != null)
      buffer.getContentType().append(ct);
    
    Object headers = message.get(Message.PROTOCOL_HEADERS);
    if (headers != null)
      buffer.getHeader().append(headers);
    
    return buffer;
  }

  /**
   * Writes the outgoing payload to a buffer.
   * 
   * @param builder	the buffer to write to
   * @param message	the message
   * @param cos		the cached output stream
   * @param encoding	the encoding to use
   * @param contentType	the content type
   */
  public static void writeOutgoingPayload(StringBuilder builder, Message message, CachedOutputStream cos, String encoding, String contentType) throws Exception {
    // Just transform the XML message when the cos has content
    if (((contentType != null) && (contentType.indexOf("xml") >= 0) 
	&& contentType.toLowerCase().indexOf("multipart/related") < 0) && (cos.size() > 0)) {
      Transformer serializer = XMLUtils.newTransformer(2);
      // Setup indenting to "pretty print"
      serializer.setOutputProperty(OutputKeys.INDENT, "yes");
      serializer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

      StringWriter swriter = new StringWriter();
      serializer.transform(new StreamSource(cos.getInputStream()), new StreamResult(swriter));
      builder.append(swriter.toString());
    }
    else {
      if (StringUtils.isEmpty(encoding))
	cos.writeCacheTo(builder);
      else
	cos.writeCacheTo(builder, encoding);
    }
  }

  /**
   * Writes the payload of a message to the buffer.
   * 
   * @param builder	the buffer to write to
   * @param cos		the cached output stream to use
   * @param encoding	the encoding to use
   * @param contentType	the content type
   */
  public static void writeIncomingPayload(StringBuilder builder, CachedOutputStream cos, String encoding, String contentType) throws Exception {
    // Just transform the XML message when the cos has content
    if ((contentType != null && contentType.indexOf("xml") >= 0 
	&& contentType.toLowerCase().indexOf("multipart/related") < 0) && cos.size() > 0) {
      Transformer serializer = XMLUtils.newTransformer(2);
      // Setup indenting to "pretty print"
      serializer.setOutputProperty(OutputKeys.INDENT, "yes");
      serializer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

      StringWriter swriter = new StringWriter();
      serializer.transform(new StreamSource(cos.getInputStream()), new StreamResult(swriter));
      builder.append(swriter.toString());
    } 
    else {
      if (StringUtils.isEmpty(encoding))
	cos.writeCacheTo(builder);
      else
	cos.writeCacheTo(builder, encoding);
    }
  }
  
  /**
   * Writes the message to a buffer.
   * 
   * @param message	the message to write to a buffer
   * @return		the generated buffer
   */
  public static LoggingMessage writeIncomingMessage(Message message) {
    if (message.containsKey(LoggingMessage.ID_KEY))
      return null;

    final LoggingMessage buffer = InterceptorHelper.setupIncomingBuffer(message);

    String encoding = (String)message.get(Message.ENCODING);
    if (encoding != null)
      buffer.getEncoding().append(encoding);
    
    String ct = (String)message.get(Message.CONTENT_TYPE);
    if (ct != null)
      buffer.getContentType().append(ct);

    InputStream is = message.getContent(InputStream.class);
    if (is != null) {
      CachedOutputStream bos = new CachedOutputStream();
      try {
	// use the appropriate input stream and restore it later
	InputStream bis = (is instanceof DelegatingInputStream) ? ((DelegatingInputStream)is).getInputStream() : is;

	IOUtils.copyAndCloseInput(bis, bos);
	bos.flush();
	bis = bos.getInputStream();

	// restore the delegating input stream or the input stream
	if (is instanceof DelegatingInputStream) {
	  ((DelegatingInputStream)is).setInputStream(bis);
	} 
	else {
	  message.setContent(InputStream.class, bis);
	}

	if (bos.getTempFile() != null) {
	  //large thing on disk...
	  buffer.getMessage().append("\nMessage (saved to tmp file):\n");
	  buffer.getMessage().append("Filename: " + bos.getTempFile().getAbsolutePath() + "\n");
	}
	InterceptorHelper.writeIncomingPayload(buffer.getPayload(), bos, encoding, ct); 

	bos.close();
      } 
      catch (Exception e) {
	throw new Fault(e);
      }
    } 
    else {
      Reader reader = message.getContent(Reader.class);
      if (reader != null) {
	try {
	  CachedWriter writer = new CachedWriter();
	  IOUtils.copyAndCloseInput(reader, writer);
	  message.setContent(Reader.class, writer.getReader());

	  if (writer.getTempFile() != null) {
	    //large thing on disk...
	    buffer.getMessage().append("\nMessage (saved to tmp file):\n");
	    buffer.getMessage().append("Filename: " + writer.getTempFile().getAbsolutePath() + "\n");
	  }
	  writer.writeCacheTo(buffer.getPayload());
	} 
	catch (Exception e) {
	  throw new Fault(e);
	}
      }
    }
    
    return buffer;
  }

  /**
   * Configures the buffer for the incoming message.
   * 
   * @param message	the message to configure the buffer for
   * @return		the generated buffer
   */
  public static LoggingMessage setupIncomingBuffer(Message message) {
    String id = (String)message.getExchange().get(LoggingMessage.ID_KEY);
    if (id == null) {
      id = LoggingMessage.nextId();
      message.getExchange().put(LoggingMessage.ID_KEY, id);
    }
    message.put(LoggingMessage.ID_KEY, id);
    final LoggingMessage buffer = new LoggingMessage("Inbound Message\n----------------------------", id);

    Integer responseCode = (Integer)message.get(Message.RESPONSE_CODE);
    if (responseCode != null)
      buffer.getResponseCode().append(responseCode);

    String encoding = (String)message.get(Message.ENCODING);
    if (encoding != null)
      buffer.getEncoding().append(encoding);
    
    String httpMethod = (String)message.get(Message.HTTP_REQUEST_METHOD);
    if (httpMethod != null)
      buffer.getHttpMethod().append(httpMethod);
    
    String ct = (String)message.get(Message.CONTENT_TYPE);
    if (ct != null)
      buffer.getContentType().append(ct);
    
    Object headers = message.get(Message.PROTOCOL_HEADERS);
    if (headers != null)
      buffer.getHeader().append(headers);
    
    String uri = (String)message.get(Message.REQUEST_URL);
    if (uri != null) {
      buffer.getAddress().append(uri);
      String query = (String)message.get(Message.QUERY_STRING);
      if (query != null) {
	buffer.getAddress().append("?").append(query);
      }
    }
    
    return buffer;
  }
}
