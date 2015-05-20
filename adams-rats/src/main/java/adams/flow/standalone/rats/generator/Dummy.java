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
 * Dummy.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.standalone.rats.generator;

import adams.flow.standalone.Rat;

/**
 <!-- globalinfo-start -->
 * Generates a dummy Rat setup.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-log &lt;adams.flow.core.CallableActorReference&gt; (property: log)
 * &nbsp;&nbsp;&nbsp;The name of the callable log actor to use (logging disabled if actor not 
 * &nbsp;&nbsp;&nbsp;found).
 * &nbsp;&nbsp;&nbsp;default: unknown
 * </pre>
 * 
 * <pre>-send-error-queue &lt;adams.flow.control.StorageName&gt; (property: sendErrorQueue)
 * &nbsp;&nbsp;&nbsp;The name of the (optional) queue in internal storage to feed with send errors.
 * &nbsp;&nbsp;&nbsp;default: senderrors
 * </pre>
 * 
 * <pre>-show-in-control &lt;boolean&gt; (property: showInControl)
 * &nbsp;&nbsp;&nbsp;If enabled, the generated Rat will be displayed in the adams.flow.standalone.RatControl 
 * &nbsp;&nbsp;&nbsp;control panel.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Dummy
  extends AbstractRatGenerator {

  /** for serialization. */
  private static final long serialVersionUID = 6791424184893304730L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates a dummy Rat setup.";
  }

  /**
   * Generates the actual setup.
   * 
   * @return		the generated setup
   */
  @Override
  protected Rat doGenerate() {
    Rat		result;
    
    result = new Rat();
    result.setLog(m_Log);
    result.setSendErrorQueue(m_SendErrorQueue);
    result.setShowInControl(m_ShowInControl);
    
    return result;
  }
}
