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
 * NewImage.java
 * Copyright (C) 2012-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.source;

import adams.core.QuickInfoHelper;
import adams.data.trail.Trail;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Generates an empty trail with the specified dimensions.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.trail.Trail<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: NewTrail
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-id &lt;java.lang.String&gt; (property: ID)
 * &nbsp;&nbsp;&nbsp;The ID of the trail; ignored if empty.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-width &lt;float&gt; (property: width)
 * &nbsp;&nbsp;&nbsp;The width of the trail.
 * &nbsp;&nbsp;&nbsp;default: 800.0
 * &nbsp;&nbsp;&nbsp;minimum: 1.0
 * </pre>
 * 
 * <pre>-height &lt;float&gt; (property: height)
 * &nbsp;&nbsp;&nbsp;The height of the trail.
 * &nbsp;&nbsp;&nbsp;default: 600.0
 * &nbsp;&nbsp;&nbsp;minimum: 1.0
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class NewTrail
  extends AbstractSimpleSource {

  /** for serialization. */
  private static final long serialVersionUID = -5718059337341470131L;

  /** the ID of the trail. */
  protected String m_ID;

  /** the width of the trail. */
  protected float m_Width;
  
  /** the height of the trail. */
  protected float m_Height;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates an empty trail with the specified dimensions.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "id", "ID",
      "");

    m_OptionManager.add(
      "width", "width",
      getDefaultWidth(), 1f, null);

    m_OptionManager.add(
      "height", "height",
      getDefaultHeight(), 1f, null);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "ID", (m_ID.isEmpty() ? "-none-" : m_ID), "ID: ");
    result += QuickInfoHelper.toString(this, "width", m_Width, ", ");
    result += " x ";
    result += QuickInfoHelper.toString(this, "height", m_Height);

    return result;
  }

  /**
   * Sets the ID of the trail.
   *
   * @param value	the ID
   */
  public void setID(String value) {
    m_ID = value;
    reset();
  }

  /**
   * Returns the ID of the trail.
   *
   * @return		the ID
   */
  public String getID() {
    return m_ID;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String IDTipText() {
    return "The ID of the trail; ignored if empty.";
  }

  /**
   * Returns the default width of the trail.
   *
   * @return		the default width
   */
  public float getDefaultWidth() {
    return 800f;
  }

  /**
   * Sets the width of the trail.
   *
   * @param value	the width
   */
  public void setWidth(float value) {
    if (getOptionManager().isValid("width", value)) {
      m_Width = value;
      reset();
    }
  }

  /**
   * Returns the width of the trail.
   *
   * @return		the width
   */
  public float getWidth() {
    return m_Width;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String widthTipText() {
    return "The width of the trail.";
  }

  /**
   * Returns the default height of the trail.
   *
   * @return		the default height
   */
  public float getDefaultHeight() {
    return 600f;
  }

  /**
   * Sets the height of the trail.
   *
   * @param value	the height
   */
  public void setHeight(float value) {
    if (getOptionManager().isValid("height", value)) {
      m_Height = value;
      reset();
    }
  }

  /**
   * Returns the height of the trail.
   *
   * @return		the height
   */
  public float getHeight() {
    return m_Height;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String heightTipText() {
    return "The height of the trail.";
  }

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public Class[] generates() {
    return new Class[]{Trail.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    Trail	trail;

    trail = new Trail();
    if (!m_ID.isEmpty())
      trail.setID(m_ID);
    trail.setWidth(m_Width);
    trail.setHeight(m_Height);
    m_OutputToken = new Token(trail);

    return null;
  }
}
