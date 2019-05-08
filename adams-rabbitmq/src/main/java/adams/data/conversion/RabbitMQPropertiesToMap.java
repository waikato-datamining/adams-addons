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

import com.rabbitmq.client.AMQP.BasicProperties;

import java.util.HashMap;
import java.util.Map;

/**
 <!-- globalinfo-start -->
 * Converts RabbitMQ properties into a Java Map.
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

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Converts RabbitMQ properties into a Java Map.";
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
    result.put("AppId", props.getAppId());
    result.put("ClassId", props.getClassId());
    result.put("ClassName", props.getClassName());
    result.put("ClusterId", props.getClusterId());
    result.put("ContentEncoding", props.getContentEncoding());
    result.put("ContentType", props.getContentType());
    result.put("CorrelationId", props.getCorrelationId());
    result.put("DeliveryMode", props.getDeliveryMode());
    result.put("Expiration", props.getExpiration());
    result.put("Headers", props.getHeaders());
    result.put("MessageId", props.getMessageId());
    result.put("Priority", props.getPriority());
    result.put("ReplyTo", props.getReplyTo());
    result.put("Timestamp", props.getTimestamp());
    result.put("Type", props.getType());
    result.put("UserId", props.getUserId());
    result.put("BodySize", props.getBodySize());

    return result;
  }
}
