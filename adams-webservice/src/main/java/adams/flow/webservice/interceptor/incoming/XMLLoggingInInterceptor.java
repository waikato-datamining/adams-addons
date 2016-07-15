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
package adams.flow.webservice.interceptor.incoming;

import adams.core.License;
import adams.core.annotation.MixedCopyright;
import adams.core.logging.LoggingHelper;
import org.apache.cxf.headers.Header;
import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
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
  url = "https://raw.githubusercontent.com/greenbird/xml-formatter-components/master/cxf/src/main/java/com/greenbird/xmlformatter/cxf/XMLLoggingInInterceptor.java",
  note = "simplified code"
)
public class XMLLoggingInInterceptor
  extends AbstractInInterceptor {

  private static final String LOCAL_NAME = "MessageID";

  private static final int PROPERTIES_SIZE = 128;

  protected Logger logger;

  public XMLLoggingInInterceptor() {
    this(LoggingHelper.getLogger(XMLLoggingInInterceptor.class));
  }

  public XMLLoggingInInterceptor(Logger logger) {
    super(Phase.RECEIVE);
    this.logger = logger;
  }

  public void handleMessage(Message message) throws Fault {
    InputStream in = message.getContent(InputStream.class);
    if (in == null)
      return;

    StringBuilder buffer;

    CachedOutputStream cache = new CachedOutputStream();
    try {
      InputStream origIn = in;
      IOUtils.copy(in, cache);

      if (cache.size() > 0)
	in = cache.getInputStream();
      else
	in = new ByteArrayInputStream(new byte[0]);

      // set the inputstream back as message payload
      message.setContent(InputStream.class, in);

      cache.close();
      origIn.close();

       int contentSize = (int) cache.size();

      buffer = new StringBuilder(contentSize + PROPERTIES_SIZE);

      cache.writeCacheTo(buffer, "UTF-8");
    }
    catch (IOException e) {
      throw new Fault(e);
    }

    // decode chars from bytes
    char[] chars = new char[buffer.length()];
    buffer.getChars(0, chars.length, chars, 0);

    logProperties(buffer, message);
    logger.log(Level.INFO, buffer.toString());
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
}
