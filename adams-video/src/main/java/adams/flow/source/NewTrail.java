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
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class NewTrail
  extends AbstractSimpleSource {

  /** for serialization. */
  private static final long serialVersionUID = -5718059337341470131L;

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

    result  = QuickInfoHelper.toString(this, "width", m_Width);
    result += " x ";
    result += QuickInfoHelper.toString(this, "height", m_Height);

    return result;
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
    trail.setWidth(m_Width);
    trail.setHeight(m_Height);
    m_OutputToken = new Token(trail);

    return null;
  }
}
