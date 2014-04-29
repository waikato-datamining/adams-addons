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
 * Rotate.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */

package adams.data.filter;

import adams.data.heatmap.Heatmap;

/**
 <!-- globalinfo-start -->
 * Rotates a heatmap by a specified number of degrees clockwise.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 *
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 * <pre>-rotation &lt;ROTATE_90_DEGREES|ROTATE_180_DEGREES|ROTATE_270_DEGREES&gt; (property: rotation)
 * &nbsp;&nbsp;&nbsp;The type of rotation to perform on the heatmap.
 * &nbsp;&nbsp;&nbsp;default: ROTATE_180_DEGREES
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Rotate
  extends AbstractFilter<Heatmap> {

  /** for serialization. */
  private static final long serialVersionUID = -1306518673446335794L;

  /**
   * Defines the type of rotation to perform.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum Rotation {
    /** 90 degrees. */
    ROTATE_90_DEGREES,
    /** 180 degrees. */
    ROTATE_180_DEGREES,
    /** 270 degrees. */
    ROTATE_270_DEGREES
  }

  /** the type of rotation to perform. */
  protected Rotation m_Rotation;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "Rotates a heatmap by a specified number of degrees clockwise.";
  }

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"rotation", "rotation",
	Rotation.ROTATE_180_DEGREES);
  }

  /**
   * Sets the type of rotation to perform.
   *
   * @param value 	the type
   */
  public void setRotation(Rotation value) {
    m_Rotation = value;
    reset();
  }

  /**
   * Returns the type of rotation to perform.
   *
   * @return 		the type
   */
  public Rotation getRotation() {
    return m_Rotation;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String rotationTipText() {
    return "The type of rotation to perform on the heatmap.";
  }

  /**
   * Performs the actual filtering.
   *
   * @param data	the data to filter
   * @return		the filtered data
   */
  protected Heatmap processData(Heatmap data) {
    Heatmap		result;
    int			x;
    int			y;

    if (m_Rotation == Rotation.ROTATE_90_DEGREES) {
      result = data.getHeader(data.getWidth(), data.getHeight());
      for (y = 0; y < data.getHeight(); y++) {
	for (x = 0; x < data.getWidth(); x++) {
	  result.set(x, result.getWidth() - y - 1, data.get(y, x));
	}
      }
    }
    else if (m_Rotation == Rotation.ROTATE_180_DEGREES) {
      result = data.getHeader(data.getHeight(), data.getWidth());
      for (y = 0; y < data.getHeight(); y++) {
	for (x = 0; x < data.getWidth(); x++) {
	  result.set(result.getHeight() - y - 1, result.getWidth() - x - 1, data.get(y, x));
	}
      }
    }
    else if (m_Rotation == Rotation.ROTATE_270_DEGREES) {
      result = data.getHeader(data.getWidth(), data.getHeight());
      for (y = 0; y < data.getHeight(); y++) {
	for (x = 0; x < data.getWidth(); x++) {
	  result.set(result.getHeight() - x - 1, y, data.get(y, x));
	}
      }
    }
    else {
      throw new IllegalStateException("Unhandled rotation: " + m_Rotation);
    }

    return result;
  }
}
