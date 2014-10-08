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
 * SimpleContainerContent.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.standalone.rats.output;

import adams.core.Utils;
import adams.data.container.AbstractSimpleContainer;
import adams.flow.core.Compatibility;

/**
 <!-- globalinfo-start -->
 * Extracts the content from the 'simple' container (containers derived from adams.data.container.AbstractSimpleContainer) and passes this on to the base rat output scheme.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-output &lt;adams.flow.standalone.rats.RatOutput&gt; (property: output)
 * &nbsp;&nbsp;&nbsp;The transmitter to wrap.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.standalone.rats.DummyOutput
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 9831 $
 */
public class SimpleContainerContent
  extends AbstractMetaRatOutput {

  /** for serialization. */
  private static final long serialVersionUID = 4772521690328020172L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
	"Extracts the content from the 'simple' container (containers "
	+ "derived from " + AbstractSimpleContainer.class.getName() + ") and "
	+ "passes this on to the base rat output scheme.";
  }

  /**
   * Returns the type of data that gets accepted.
   *
   * @return		the type of data
   */
  @Override
  public Class[] accepts() {
    return new Class[]{AbstractSimpleContainer.class};
  }

  /**
   * Hook method that calls the base-input's transmit() method.
   *
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String callTransmit() {
    AbstractSimpleContainer	cont;
    Object			input;
    Compatibility		comp;

    cont  = (AbstractSimpleContainer) m_Input;
    input = cont.getContent();
    if (input != null) {
      comp = new Compatibility();
      if (comp.isCompatible(new Class[]{input.getClass()}, m_Output.accepts())) {
	m_Output.input(input);
	return m_Output.transmit();
      }
      else {
	return 
	    "Container content of type '" + input.getClass() + "' is not "
	    + "compatible with base outputs accepted input: " 
	    + Utils.classesToString(m_Output.accepts());
      }
    }
    else {
      return "No content stored in container!";
    }
  }
}
