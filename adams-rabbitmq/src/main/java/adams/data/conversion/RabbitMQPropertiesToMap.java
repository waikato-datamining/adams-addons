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
 * RabbitMQPropertiesToMap.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.data.conversion;

import adams.core.Utils;
import com.rabbitmq.client.AMQP.BasicProperties;

import java.util.HashMap;
import java.util.Map;

/**
 <!-- globalinfo-start -->
 * Converts RabbitMQ properties into a Java Map.<br>
 * Can be used to obtain the replyto queue name for sending back results.<br>
 * Available keys:<br>
 * - AppId<br>
 * - ClassId<br>
 * - ClassName<br>
 * - ClusterId<br>
 * - ContentEncoding<br>
 * - ContentType<br>
 * - CorrelationId<br>
 * - DeliveryMode<br>
 * - Expiration<br>
 * - Headers<br>
 * - MessageId<br>
 * - Priority<br>
 * - ReplyTo<br>
 * - Timestamp<br>
 * - Type<br>
 * - UserId<br>
 * - BodySize<br>
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class RabbitMQPropertiesToMap
  extends AbstractConversion {

  private static final long serialVersionUID = 6432647148987897719L;

  public static final String KEY_APP_ID = "AppId";

  public static final String KEY_CLASS_ID = "ClassId";

  public static final String KEY_CLASS_NAME = "ClassName";

  public static final String KEY_CLUSTER_ID = "ClusterId";

  public static final String KEY_CONTENT_ENCODING = "ContentEncoding";

  public static final String KEY_CONTENT_TYPE = "ContentType";

  public static final String KEY_CORRELATION_ID = "CorrelationId";

  public static final String KEY_DELIVERY_MODE = "DeliveryMode";

  public static final String KEY_EXPIRATION = "Expiration";

  public static final String KEY_HEADERS = "Headers";

  public static final String KEY_MESSAGE_ID = "MessageId";

  public static final String KEY_PRIORITY = "Priority";

  public static final String KEY_REPLY_TO = "ReplyTo";

  public static final String KEY_TIMESTAMP = "Timestamp";

  public static final String KEY_TYPE = "Type";

  public static final String KEY_USER_ID = "UserId";

  public static final String KEY_BODY_SIZE = "BodySize";

  public final static String[] KEYS = {
    KEY_APP_ID,
    KEY_CLASS_ID,
    KEY_CLASS_NAME,
    KEY_CLUSTER_ID,
    KEY_CONTENT_ENCODING,
    KEY_CONTENT_TYPE,
    KEY_CORRELATION_ID,
    KEY_DELIVERY_MODE,
    KEY_EXPIRATION,
    KEY_HEADERS,
    KEY_MESSAGE_ID,
    KEY_PRIORITY,
    KEY_REPLY_TO,
    KEY_TIMESTAMP,
    KEY_TYPE,
    KEY_USER_ID,
    KEY_BODY_SIZE,
  };

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Converts RabbitMQ properties into a Java Map.\n"
      + "Can be used to obtain the replyto queue name for sending back results.\n"
      + "Available keys:\n" + Utils.commentOut(Utils.flatten(KEYS, "\n"), "- ");
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return		the class
   */
  @Override
  public Class accepts() {
    return BasicProperties.class;
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return		the class
   */
  @Override
  public Class generates() {
    return Map.class;
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    Map<String,Object> 	result;
    BasicProperties	props;

    result = new HashMap<>();
    props  = (BasicProperties) m_Input;
    result.put(KEY_APP_ID, props.getAppId());
    result.put(KEY_CLASS_ID, props.getClassId());
    result.put(KEY_CLASS_NAME, props.getClassName());
    result.put(KEY_CLUSTER_ID, props.getClusterId());
    result.put(KEY_CONTENT_ENCODING, props.getContentEncoding());
    result.put(KEY_CONTENT_TYPE, props.getContentType());
    result.put(KEY_CORRELATION_ID, props.getCorrelationId());
    result.put(KEY_DELIVERY_MODE, props.getDeliveryMode());
    result.put(KEY_EXPIRATION, props.getExpiration());
    result.put(KEY_HEADERS, props.getHeaders());
    result.put(KEY_MESSAGE_ID, props.getMessageId());
    result.put(KEY_PRIORITY, props.getPriority());
    result.put(KEY_REPLY_TO, props.getReplyTo());
    result.put(KEY_TIMESTAMP, props.getTimestamp());
    result.put(KEY_TYPE, props.getType());
    result.put(KEY_USER_ID, props.getUserId());
    result.put(KEY_BODY_SIZE, props.getBodySize());

    return result;
  }
}
