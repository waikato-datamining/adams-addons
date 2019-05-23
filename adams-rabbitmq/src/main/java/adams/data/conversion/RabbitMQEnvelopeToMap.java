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
 * RabbitMQEnvelopeToMap.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.data.conversion;

import adams.core.Utils;
import com.rabbitmq.client.Envelope;

import java.util.HashMap;
import java.util.Map;

/**
 <!-- globalinfo-start -->
 * Converts RabbitMQ envelope into a Java Map.<br>
 * Can be used to obtain the delivery tag of a message to acknowledge successful consumption.<br>
 * Available keys:<br>
 * - DeliveryTag<br>
 * - Exchange<br>
 * - RoutingKey<br>
 * - IsRedeliver<br>
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
public class RabbitMQEnvelopeToMap
  extends AbstractConversion {

  private static final long serialVersionUID = 6432647148987897719L;

  public static final String KEY_DELIVERY_TAG = "DeliveryTag";

  public static final String KEY_EXCHANGE = "Exchange";

  public static final String KEY_ROUTING_KEY = "RoutingKey";

  public static final String KEY_IS_REDELIVER = "IsRedeliver";

  public final static String[] KEYS = {
    KEY_DELIVERY_TAG,
    KEY_EXCHANGE,
    KEY_ROUTING_KEY,
    KEY_IS_REDELIVER,
  };

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Converts RabbitMQ envelope into a Java Map.\n"
      + "Can be used to obtain the delivery tag of a message to acknowledge successful consumption.\n"
      + "Available keys:\n" + Utils.commentOut(Utils.flatten(KEYS, "\n"), "- ");
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return		the class
   */
  @Override
  public Class accepts() {
    return Envelope.class;
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
    Envelope 		env;

    result = new HashMap<>();
    env = (Envelope) m_Input;
    result.put(KEY_DELIVERY_TAG, env.getDeliveryTag());
    result.put(KEY_EXCHANGE, env.getExchange());
    result.put(KEY_ROUTING_KEY, env.getRoutingKey());
    result.put(KEY_IS_REDELIVER, env.isRedeliver());

    return result;
  }
}
