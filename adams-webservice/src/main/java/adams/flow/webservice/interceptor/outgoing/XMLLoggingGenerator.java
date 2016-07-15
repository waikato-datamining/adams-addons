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
 * XMLLoggingGenerator.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.webservice.interceptor.outgoing;

import adams.core.logging.LoggingHelper;
import adams.core.logging.LoggingLevel;

import java.util.logging.Logger;

/**
 * Generator for {@link XMLLoggingOutInterceptor}.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class XMLLoggingGenerator
  extends AbstractOutInterceptorGenerator<XMLLoggingOutInterceptor> {

  /** for serialization. */
  private static final long serialVersionUID = -8109018608359183466L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Generates a " + XMLLoggingOutInterceptor.class.getName() + " instance.\n"
	+ "Logs the messages using its logger instance in XML.";
  }

  /**
   * Returns the default logging level to use.
   *
   * @return		the logging level
   */
  @Override
  protected LoggingLevel getDefaultLoggingLevel() {
    return LoggingLevel.INFO;
  }

  /**
   * Generates the actual interceptor for incoming messages.
   * 
   * @return		the interceptor
   */
  @Override
  protected XMLLoggingOutInterceptor doGenerate() {
    Logger logger;

    logger = LoggingHelper.getLogger(XMLLoggingOutInterceptor.class);
    logger.setLevel(getLoggingLevel().getLevel());

    return new XMLLoggingOutInterceptor(logger);
  }
}
