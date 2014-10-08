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
 * ContainerValuePicker.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.standalone.rats.output;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.flow.container.AbstractContainer;
import adams.flow.core.Compatibility;

/**
 <!-- globalinfo-start -->
 * Extracts the specified value from the container and passes this on to the base rat output scheme.
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
 * <pre>-value &lt;java.lang.String&gt; (property: valueName)
 * &nbsp;&nbsp;&nbsp;The name of the value to extract.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ContainerValuePicker
  extends AbstractMetaRatOutput {

  /** for serialization. */
  private static final long serialVersionUID = 4772521690328020172L;

  /** the value to pick. */
  protected String m_ValueName;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
	"Extracts the specified value from the container and passes this on "
	+ "to the base rat output scheme.";
  }
  
  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "value", "valueName",
	    "");
  }

  /**
   * Sets the name of the value to tee off.
   *
   * @param value	the name
   */
  public void setValueName(String value) {
    m_ValueName = value;
    reset();
  }

  /**
   * Returns the name of the value to tee off.
   *
   * @return		the name
   */
  public String getValueName() {
    return m_ValueName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String valueNameTipText() {
    return "The name of the value to extract.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "valueName", m_ValueName);
  }

  /**
   * Returns the type of data that gets accepted.
   *
   * @return		the type of data
   */
  @Override
  public Class[] accepts() {
    return new Class[]{AbstractContainer.class};
  }

  /**
   * Hook method that calls the base-input's transmit() method.
   *
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String callTransmit() {
    AbstractContainer	cont;
    Object		input;
    Compatibility		comp;

    cont  = (AbstractContainer) m_Input;
    input = cont.getValue(m_ValueName);
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
      return "No value named '" + m_ValueName + "' in container!";
    }
  }
}
