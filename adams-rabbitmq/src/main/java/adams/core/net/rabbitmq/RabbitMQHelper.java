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
 * RabbitMQHelper.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.core.net.rabbitmq;

import adams.core.logging.LoggingHelper;
import adams.core.logging.LoggingSupporter;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.util.List;

/**
 * Helper class for RabbitMQ operations.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class RabbitMQHelper {

  /**
   * Creates a new channel and returns it.
   *
   * @param connection 	the connection to use
   * @param prefetchCount 	how many jobs a client can pull off queue before having to ack them, 0 is unlimited
   * @return		the channel, null if failed to create or no connection available
   */
  public static Channel createChannel(Connection connection, int prefetchCount) {
    return createChannel(null, connection, prefetchCount);
  }

  /**
   * Creates a new channel and returns it.
   *
   * @param logging 	for logging errors
   * @param connection 	the connection to use
   * @param prefetchCount 	how many jobs a client can pull off queue before having to ack them, 0 is unlimited
   * @return		the channel, null if failed to create or no connection available
   */
  public static Channel createChannel(LoggingSupporter logging, Connection connection, int prefetchCount) {
    Channel	result;

    result = null;
    if (prefetchCount < 0)
      prefetchCount = 0;

    try {
      if (connection != null) {
	result = connection.createChannel();
	result.basicQos(prefetchCount);
      }
    }
    catch (Exception e) {
      if (logging != null)
	LoggingHelper.handleException(logging, "Failed to create channel!", e);
    }

    return result;
  }

  /**
   * Closes the channel.
   *
   * @param channel	the channel to close, can be null
   */
  public static void closeQuietly(Channel channel) {
    if (channel != null) {
      try {
	channel.close();
      }
      catch (Exception e) {
	// ignored
      }
    }
  }

  /**
   * Closes the connection.
   *
   * @param connection	the connection to close
   */
  public static void closeQuietly(Connection connection) {
    closeQuietly(connection, null);
  }

  /**
   * Closes the connection and deletes any auto-created queues.
   *
   * @param connection	the connection to close
   * @param queues	the auto-created queues to close
   */
  public static void closeQuietly(Connection connection, List<String> queues) {
    Channel	channel;

    if (connection != null) {
      if (connection.isOpen()) {
        // delete auto-created queues
        if ((queues != null) && (queues.size() > 0)) {
          channel = createChannel(null, connection, 0);
          if (channel != null) {
	    for (String queue : queues) {
	      try {
		channel.queueDelete(queue);
	      }
	      catch (Exception e) {
	        // ignored
	      }
	    }
	    try {
	      channel.close();
	    }
	    catch (Exception e) {
	      // ignored
	    }
	  }
        }

        // close connection
	try {
	  connection.close();
	}
	catch (Exception e) {
	  // ignored
	}
      }
    }
  }
}
