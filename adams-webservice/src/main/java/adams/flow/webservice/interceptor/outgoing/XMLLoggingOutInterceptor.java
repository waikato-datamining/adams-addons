/***************************************************************************
 * Copyright 2014 greenbird Integration Technology, http://www.greenbird.com/
 *
 * This file is part of the 'xml-formatter' project available at
 * http://greenbird.github.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package adams.flow.webservice.interceptor.outgoing;

import adams.core.License;
import adams.core.annotation.MixedCopyright;
import adams.core.logging.LoggingHelper;
import org.apache.cxf.headers.Header;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.StaxOutInterceptor;
import org.apache.cxf.io.CacheAndWriteOutputStream;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.io.CachedOutputStreamCallback;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;

import java.io.OutputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Default XML Logging interceptor.
 *
 * @author greenbird Integration Technology
 * @author FracPete (fracpete at waikato dot ac dot nz) - simplified
 */
@MixedCopyright(
  license = License.APACHE2,
  author = "greenbird Integration Technology, http://www.greenbird.com/",
  url = "https://raw.githubusercontent.com/greenbird/xml-formatter-components/master/cxf/src/main/java/com/greenbird/xmlformatter/cxf/XMLLoggingOutInterceptor.java",
  note = "simplified code"
)
public class XMLLoggingOutInterceptor
  extends AbstractOutInterceptor {

  private static final String LOCAL_NAME = "MessageID";

  private static final int PROPERTIES_SIZE = 128;

  protected Logger logger = null;

  public XMLLoggingOutInterceptor() {
    this(LoggingHelper.getLogger(XMLLoggingOutInterceptor.class));
  }

  public XMLLoggingOutInterceptor(Logger logger) {
    super(Phase.PRE_STREAM);
    addBefore(StaxOutInterceptor.class.getName());
    this.logger = logger;
  }

  public void handleMessage(Message message) throws Fault {
    final OutputStream os = message.getContent(OutputStream.class);
    if (os == null)
      return;

    // Write the output while caching it for the log message
    final CacheAndWriteOutputStream newOut = new CacheAndWriteOutputStream(os);
    message.setContent(OutputStream.class, newOut);

    int contentSize = -1;
    String contentSizeString = getHeader(message, "Content-Size");
    if (contentSizeString != null)
      contentSize = Integer.parseInt(contentSizeString);
    if (contentSize == -1)
      contentSize = 8 * 1024;

    StringBuilder buffer = new StringBuilder(contentSize + PROPERTIES_SIZE);

    logProperties(buffer, message);

    newOut.registerCallback(new SimpleLoggingCallback(buffer));
  }

  /**
   * Method intended for use within subclasses. Log custom field here.
   *
   * @param message message
   */

  protected void logProperties(StringBuilder buffer, Message message) {
    final String messageId = getIdHeader(message);
    if (messageId != null) {
      buffer.append(" MessageId=");
      buffer.append(messageId);
    }
  }

  protected class SimpleLoggingCallback
    implements CachedOutputStreamCallback {

    private StringBuilder buffer;

    public SimpleLoggingCallback(StringBuilder buffer) {
      this.buffer = buffer;;
    }

    public void onFlush(CachedOutputStream cos) {
    }

    public void onClose(CachedOutputStream cos) {
      int length = buffer.length();
      try {
	cos.writeCacheTo(buffer);
      }
      catch (Exception ex) {
	// ignore
      }

      // decode chars from bytes
      char[] chars = new char[buffer.length() - length];
      buffer.getChars(length, buffer.length(), chars, 0);

      logger.log(Level.INFO, buffer.toString());
    }
  }

  /**
   * Gets theMessageID header in the list of headers.
   *
   */
  protected String getIdHeader(Message message) {
    return getHeader(message, LOCAL_NAME);
  }

  protected String getHeader(Message message, String name) {
    List<Header> headers = (List<Header>) message.get(Header.HEADER_LIST);

    if (headers != null) {
      for (Header header:headers) {
	if (header.getName().getLocalPart().equalsIgnoreCase(name))
	  return header.getObject().toString();
      }
    }
    return null;
  }
}
