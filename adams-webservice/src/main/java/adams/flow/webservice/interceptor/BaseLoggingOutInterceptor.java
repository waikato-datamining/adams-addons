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
 * BaseLoggingOutInterceptor.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.webservice.interceptor;

import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.cxf.common.util.StringUtils;
import org.apache.cxf.helpers.XMLUtils;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.LoggingMessage;
import org.apache.cxf.io.CacheAndWriteOutputStream;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.io.CachedOutputStreamCallback;
import org.apache.cxf.message.Message;

import adams.core.License;
import adams.core.annotation.MixedCopyright;
import adams.core.logging.LoggingHelper;
import adams.core.logging.LoggingLevel;
import adams.core.logging.LoggingLevelHandler;
import adams.core.logging.LoggingSupporter;

/**
 * Interceptor for outgoing messages.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BaseLoggingOutInterceptor
  extends AbstractOutInterceptor
  implements LoggingSupporter, LoggingLevelHandler {

  protected static final String BINARY_CONTENT_MESSAGE = "--- Binary Content ---";

  protected static final String LOG_SETUP = BaseLoggingOutInterceptor.class.getName() + ".log-setup";
  
  class LoggingCallback implements CachedOutputStreamCallback {

    private final Message message;
    private final OutputStream origStream;

    public LoggingCallback(final Logger logger, final Message msg, final OutputStream os) {
      this.message = msg;
      this.origStream = os;
    }

    public void onFlush(CachedOutputStream cos) {  
    }

    public void onClose(CachedOutputStream cos) {
      LoggingMessage buffer = setupBuffer(message);

      String ct = (String)message.get(Message.CONTENT_TYPE);

      if (cos.getTempFile() == null) {
	//buffer.append("Outbound Message:\n");
      } else {
	buffer.getMessage().append("Outbound Message (saved to tmp file):\n");
	buffer.getMessage().append("Filename: " + cos.getTempFile().getAbsolutePath() + "\n");
      }
      try {
	String encoding = (String)message.get(Message.ENCODING);
	writePayload(buffer.getPayload(), cos, encoding, ct); 
      } catch (Exception ex) {
	//ignore
      }

      getLogger().log(getLoggingLevel().getLevel(), buffer.toString());
      try {
	//empty out the cache
	cos.lockOutputStream();
	cos.resetOut(null, false);
      } catch (Exception ex) {
	//ignore
      }
      message.setContent(OutputStream.class, origStream);
    }
  }

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
    
    final OutputStream os = message.getContent(OutputStream.class);
    final Writer iowriter = message.getContent(Writer.class);
    if (os == null && iowriter == null) {
      return;
    }
    Logger logger = getLogger();
    if (isLoggingEnabled()) {
      // Write the output while caching it for the log message
      boolean hasLogged = message.containsKey(LOG_SETUP);
      if (!hasLogged) {
	message.put(LOG_SETUP, Boolean.TRUE);
	if (os != null) {
	  final CacheAndWriteOutputStream newOut = new CacheAndWriteOutputStream(os);
	  message.setContent(OutputStream.class, newOut);
	  newOut.registerCallback(new LoggingCallback(logger, message, os));
	}
      }
    }
  }

  @MixedCopyright(
      copyright = "Apache CXF",
      license = License.APACHE2,
      note = "org.apache.cxf.interceptor.LoggingOutInterceptor#setupBuffer"
  )
  protected LoggingMessage setupBuffer(Message message) {
      String id = (String)message.getExchange().get(LoggingMessage.ID_KEY);
      if (id == null) {
          id = LoggingMessage.nextId();
          message.getExchange().put(LoggingMessage.ID_KEY, id);
      }
      final LoggingMessage buffer 
          = new LoggingMessage("Outbound Message\n---------------------------",
                               id);
      
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
      String address = (String)message.get(Message.ENDPOINT_ADDRESS);
      if (address != null) {
          buffer.getAddress().append(address);
      }
      String ct = (String)message.get(Message.CONTENT_TYPE);
      if (ct != null) {
          buffer.getContentType().append(ct);
      }
      Object headers = message.get(Message.PROTOCOL_HEADERS);
      if (headers != null) {
          buffer.getHeader().append(headers);
      }
      return buffer;
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
}
