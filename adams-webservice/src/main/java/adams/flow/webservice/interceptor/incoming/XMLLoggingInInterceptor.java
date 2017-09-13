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

import adams.core.DateFormat;
import adams.core.DateUtils;
import adams.core.License;
import adams.core.annotation.MixedCopyright;
import adams.core.io.FileUtils;
import adams.core.logging.Logger;
import adams.core.logging.LoggingHelper;
import adams.data.conversion.PrettyPrintXML;
import org.apache.cxf.headers.Header;
import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

/**
 * Default XML Logging interceptor.
 *
 * @author greenbird Integration Technology
 * @author FracPete (fracpete at waikato dot ac dot nz) - simplified, pretty printing
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

  /** whether to use pretty printing. */
  protected boolean prettyPrint;

  /** the file to output the XML messages to - disabled if null. */
  protected File outputFile;

  /** the conversion for pretty printing the XML. */
  protected PrettyPrintXML convert;

  /** for outputting timestamps in the output file. */
  protected transient DateFormat formatter;

  public XMLLoggingInInterceptor() {
    this(LoggingHelper.getLogger(XMLLoggingInInterceptor.class), false, null);
  }

  public XMLLoggingInInterceptor(Logger logger, boolean prettyPrint, File outputFile) {
    super(Phase.RECEIVE);
    this.logger = logger;
    this.prettyPrint = prettyPrint;
    this.outputFile = outputFile;
  }

  /**
   * Splits the message into (if possible): header, actual message and footer.
   *
   * @param msg		the message to split
   * @return		the split message
   */
  protected StringBuilder[] splitMessage(StringBuilder msg) {
    List<StringBuilder>	result;
    String[]		lines;
    int			i;
    StringBuilder	header;
    StringBuilder	message;
    StringBuilder	footer;
    int			part;

    result = new ArrayList<>();
    result.add(msg);

    lines     = msg.toString().split("\r\n");
    header    = new StringBuilder();
    message   = new StringBuilder();
    footer    = new StringBuilder();
    part      = 0;
    for (i = 0; i < lines.length; i++) {
      if (part == 0) {
	header.append(lines[i]);
	header.append("\r\n");
      }
      else if (part == 1) {
        message.append(lines[i]);
        part++;
      }
      else if (part == 2) {
        footer.append(lines[i]);
      }
      if (lines[i].trim().length() == 0)
	part++;
    }

    if (message.length() > 0) {
      result.clear();
      result.add(header);
      result.add(message);
      result.add(footer);
    }

    return result.toArray(new StringBuilder[result.size()]);
  }

  /**
   * Pretty prints the buffer, if enabled.
   *
   * @param buffer	the buffer to turn into "pretty" XML
   * @return		the (potentially) new buffer
   */
  protected StringBuilder prettyPrint(StringBuilder buffer) {
    StringBuilder	result;
    String		msg;
    StringBuilder[]	parts;

    if (prettyPrint) {
      // multipart message? try to extract actual message
      if (buffer.indexOf("Content-Type") != -1) {
	parts = splitMessage(buffer);
	if (parts.length == 3) {
	  result = new StringBuilder();
	  result.append(parts[0].toString());
	  result.append(prettyPrint(parts[1]));
	  result.append(parts[2].toString().trim());
	}
	else {
	  result = buffer;
	}
      }
      else {
	if (convert == null)
	  convert = new PrettyPrintXML();
	convert.setInput(buffer.toString());
	msg = convert.convert();
	if (msg == null)
	  result = new StringBuilder((String) convert.getOutput());
	else
	  result = buffer;
      }
    }
    else {
      result = buffer;
    }

    return result;
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

    buffer = prettyPrint(buffer);
    logProperties(buffer, message);
    if (outputFile != null) {
      if (formatter == null)
	formatter = DateUtils.getTimestampFormatterMsecs();
      FileUtils.writeToFile(outputFile.getAbsolutePath(), "\n--- " + formatter.format(new Date()) + " ---\n", true);
      FileUtils.writeToFile(outputFile.getAbsolutePath(), buffer, true);
    }
    else {
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
