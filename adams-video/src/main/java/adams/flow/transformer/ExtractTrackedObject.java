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
 * ExtractTrackedObject.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.base.QuadrilateralLocation;
import adams.data.image.AbstractImageContainer;
import adams.data.image.BufferedImageContainer;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.flow.core.Token;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

/**
 <!-- globalinfo-start -->
 * Extracts the tracked object and forwards it as a new image container.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.image.AbstractImageContainer<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.image.BufferedImageContainer<br>
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
 * &nbsp;&nbsp;&nbsp;default: ExtractTrackedObject
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
 * <pre>-location &lt;adams.data.report.Field&gt; (property: location)
 * &nbsp;&nbsp;&nbsp;The field to retrieve the current location of the object from.
 * &nbsp;&nbsp;&nbsp;default: Tracker.Current[S]
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ExtractTrackedObject
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 3690378527551302472L;

  /** the report field with the tracked location. */
  protected Field m_Location;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Extracts the tracked object and forwards it as a new image container.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "location", "location",
      new Field("Tracker.Current", DataType.STRING));
  }

  /**
   * Sets the field to store the retrieve location of the object from.
   *
   * @param value	the field
   */
  public void setLocation(Field value) {
    m_Location = value;
    reset();
  }

  /**
   * Returns the field to retrieve the current location of the object from.
   *
   * @return		the field
   */
  public Field getLocation() {
    return m_Location;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String locationTipText() {
    return "The field to retrieve the current location of the object from.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "location", m_Location, "location: ");
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  public Class[] accepts() {
    return new Class[]{AbstractImageContainer.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{BufferedImageContainer.class};
  }

  /**
   * Extracts the tracked image.
   *
   * @param cont	the image with the tracked object
   * @param location	the location of the object
   * @return		the updated container
   */
  protected BufferedImageContainer extract(AbstractImageContainer cont, QuadrilateralLocation location) {
    BufferedImageContainer	result;
    BufferedImage 		objImg;
    BufferedImage 		img;
    Rectangle 			objLoc;
    int				x;
    int				y;
    int				width;
    int				height;

    result = new BufferedImageContainer();
    img    = cont.toBufferedImage();
    objLoc = location.rectangleValue();
    x      = Math.max(0, objLoc.x);
    y      = Math.max(0, objLoc.y);
    width  = objLoc.width  - (x - objLoc.x);
    height = objLoc.height - (y - objLoc.y);
    objImg = img.getSubimage(x, y, width, height);
    result.setImage(objImg);
    result.setReport(cont.getReport().getClone());
    result.setNotes(cont.getNotes().getClone());

    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    AbstractImageContainer	cont;
    QuadrilateralLocation	location;

    result = null;

    cont     = (AbstractImageContainer) m_InputToken.getPayload();
    location = null;

    if (cont.getReport().hasValue(m_Location))
      location = new QuadrilateralLocation(cont.getReport().getStringValue(m_Location));

    // transform tracked object?
    if (location != null)
      cont = extract(cont, location);

    m_OutputToken = new Token(cont);

    return result;
  }
}
