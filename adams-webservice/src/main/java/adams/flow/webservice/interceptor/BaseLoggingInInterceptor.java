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
 * BaseLoggingInInterceptor.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.webservice.interceptor;

import java.io.InputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import adams.core.logging.LoggingHelper;
import adams.core.logging.LoggingLevel;
import adams.core.logging.LoggingLevelHandler;
import adams.core.logging.LoggingSupporter;

/**
 * Interceptor for imcoming messages.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BaseLoggingInInterceptor
  extends AbstractInInterceptor
  implements LoggingSupporter, LoggingLevelHandler {

  /** the logging level. */
  protected LoggingLevel m_LoggingLevel;

  /** the logger in use. */
  protected transient Logger m_Logger;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    initializeLogging();
  }
  
  /**
   * Pre-configures the logging.
   */
  protected void initializeLogging() {
    m_LoggingLevel = LoggingLevel.WARNING;
  }

  /**
   * Initializes the logger.
   * <p/>
   * Default implementation uses the class name.
   */
  protected void configureLogger() {
    m_Logger = LoggingHelper.getLogger(getClass());
    m_Logger.setLevel(m_LoggingLevel.getLevel());
  }

  /**
   * Returns the logger in use.
   * 
   * @return		the logger
   */
  public synchronized Logger getLogger() {
    if (m_Logger == null)
      configureLogger();
    return m_Logger;
  }

  /**
   * Sets the logging level.
   *
   * @param value 	the level
   */
  public void setLoggingLevel(LoggingLevel value) {
    m_LoggingLevel = value;
    configureLogger();
  }

  /**
   * Returns the logging level.
   *
   * @return 		the level
   */
  public LoggingLevel getLoggingLevel() {
    return m_LoggingLevel;
  }

  /**
   * Returns whether logging is enabled.
   * 
   * @return		true if at least {@link Level#INFO}
   */
  public boolean isLoggingEnabled() {
    return LoggingHelper.isAtLeast(m_LoggingLevel.getLevel(), Level.CONFIG);
  }

  /**
   * Intercepts a message. 
   * Interceptors should NOT invoke handleMessage or handleFault
   * on the next interceptor - the interceptor chain will
   * take care of this.
   * 
   * @param message
   */
  @Override
  public void handleMessage(Message message) throws Fault {
    if (!isLoggingEnabled())
      return;
    logging(getLogger(), message);
  }

  @MixedCopyright(
      copyright = "Apache CXF",
      license = License.APACHE2,
      note = "org.apache.cxf.interceptor.AbstractLoggingInterceptor#writePayload"
  )
  protected void writePayload(StringBuilder builder, CachedOutputStream cos, 
      String encoding, String contentType) 
	  throws Exception {
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

  @MixedCopyright(
      copyright = "Apache CXF",
      license = License.APACHE2,
      note = "org.apache.cxf.interceptor.LoggingInInterceptor#logging"
  )
  protected void logging(Logger logger, Message message) throws Fault {
    if (message.containsKey(LoggingMessage.ID_KEY)) {
      return;
    }
    String id = (String)message.getExchange().get(LoggingMessage.ID_KEY);
    if (id == null) {
      id = LoggingMessage.nextId();
      message.getExchange().put(LoggingMessage.ID_KEY, id);
    }
    message.put(LoggingMessage.ID_KEY, id);
    final LoggingMessage buffer 
    = new LoggingMessage("Inbound Message\n----------------------------", id);

    Integer responseCode = (Integer)message.get(Message.RESPONSE_CODE);
    if (responseCode != null) {
      buffer.getResponseCode().append(responseCode);
    }

    String encoding = (String)message.get(Message.ENCODING);

    if (encoding != null) {
      buffer.getEncoding().append(encoding);
    }
    String httpMethod = (String)message.get(Message.HTTP_REQUEST_METHOD);
    if (httpMethod != null) {
      buffer.getHttpMethod().append(httpMethod);
    }
    String ct = (String)message.get(Message.CONTENT_TYPE);
    if (ct != null) {
      buffer.getContentType().append(ct);
    }
    Object headers = message.get(Message.PROTOCOL_HEADERS);

    if (headers != null) {
      buffer.getHeader().append(headers);
    }
    String uri = (String)message.get(Message.REQUEST_URL);
    if (uri != null) {
      buffer.getAddress().append(uri);
      String query = (String)message.get(Message.QUERY_STRING);
      if (query != null) {
	buffer.getAddress().append("?").append(query);
      }
    }

    InputStream is = message.getContent(InputStream.class);
    if (is != null) {
      CachedOutputStream bos = new CachedOutputStream();
      try {
	// use the appropriate input stream and restore it later
	InputStream bis = is instanceof DelegatingInputStream 
	    ? ((DelegatingInputStream)is).getInputStream() : is;

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
	    writePayload(buffer.getPayload(), bos, encoding, ct); 

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
    logger.log(getLoggingLevel().getLevel(), buffer.toString());
  }
}
