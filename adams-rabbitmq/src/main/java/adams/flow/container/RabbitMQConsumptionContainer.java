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
 * RabbitMQConsumptionContainer.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.container;

import com.rabbitmq.client.AMQP.BasicProperties;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Container for received/consumed data.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class RabbitMQConsumptionContainer
  extends AbstractContainer {

  private static final long serialVersionUID = 6669185450978836875L;

  /** the key for the data. */
  public final static String VALUE_DATA = "Data";

  /** the key for the properties. */
  public final static String VALUE_PROPERTIES = "Properties";

  /**
   * Default constructor.
   */
  public RabbitMQConsumptionContainer() {
    this(null, null);
  }

  /**
   * Initializes the container with only data.
   *
   * @param data	the data
   */
  public RabbitMQConsumptionContainer(Object data) {
    this(data, null);
  }

  /**
   * Initializes the container with data and properties.
   *
   * @param data	the data
   * @param properties	the properties, can be null
   */
  public RabbitMQConsumptionContainer(Object data, BasicProperties properties) {
    super();
    store(VALUE_DATA, data);
    store(VALUE_PROPERTIES, properties);
  }

  /**
   * Initializes the help strings.
   */
  protected void initHelp() {
    super.initHelp();

    addHelp(VALUE_DATA, "the payload data", Object.class);
    addHelp(VALUE_PROPERTIES, "optional properties that were received", BasicProperties.class);
  }

  /**
   * Returns all value names that can be used (theoretically).
   *
   * @return		enumeration over all possible value names
   */
  @Override
  public Iterator<String> names() {
    List<String> result;

    result = new ArrayList<>();

    result.add(VALUE_DATA);
    result.add(VALUE_PROPERTIES);

    return result.iterator();
  }

  /**
   * Checks whether the setup of the container is valid.
   *
   * @return		true if all the necessary values are available
   */
  @Override
  public boolean isValid() {
    return hasValue(VALUE_DATA);
  }
}
