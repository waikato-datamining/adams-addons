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
 * AddTrailBackground.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.data.image.AbstractImageContainer;
import adams.data.trail.Trail;
import adams.flow.core.CallableActorHelper;
import adams.flow.core.CallableActorReference;
import adams.flow.core.Token;

import java.awt.image.BufferedImage;

/**
 <!-- globalinfo-start -->
 * Adds a step tp the trail passing through.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.trail.Trail<br>
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
 * &nbsp;&nbsp;&nbsp;default: AddTrailBackground
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
 * <pre>-background &lt;adams.flow.core.CallableActorReference&gt; (property: background)
 * &nbsp;&nbsp;&nbsp;The callable actor to obtain the background image from (java.awt.image.BufferedImage 
 * &nbsp;&nbsp;&nbsp;or adams.data.image.AbstractImageContainer)
 * &nbsp;&nbsp;&nbsp;default: unknown
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class AddTrailBackground
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 3690378527551302472L;

  /** the name of the callable actor. */
  protected CallableActorReference m_Background;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Adds a step tp the trail passing through.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "background", "background",
	    new CallableActorReference("unknown"));
  }

  /**
   * Sets the name of the callable background to use.
   *
   * @param value	the name
   */
  public void setBackground(CallableActorReference value) {
    m_Background = value;
    reset();
  }

  /**
   * Returns the name of the callable background in use.
   *
   * @return		the name
   */
  public CallableActorReference getBackground() {
    return m_Background;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String backgroundTipText() {
    return "The callable actor to obtain the background image from "
      + "(" + BufferedImage.class.getName() + " or " + AbstractImageContainer.class.getName() + ")";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "background", m_Background, "background: ");
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  public Class[] accepts() {
    return new Class[]{Trail.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{Trail.class};
  }

  /**
   * Returns the image obtained from the callable actor.
   *
   * @return		the background, null if failed to obtain
   */
  protected BufferedImage getBackgroundInstance() {
    BufferedImage	result;
    Object		obj;

    result = null;
    obj    = CallableActorHelper.getSetupFromSource(Object.class, m_Background, this);
    if (obj != null) {
      if (obj instanceof BufferedImage)
	result = (BufferedImage) obj;
      else if (obj instanceof AbstractImageContainer)
	result = ((AbstractImageContainer) obj).toBufferedImage();
    }

    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    Trail 		trail;
    BufferedImage	background;

    result = null;

    trail = (Trail) m_InputToken.getPayload();
    background = getBackgroundInstance();
    if (background != null)
      trail.setBackground(background);
    else
      result = "Failed to obtain image from '" + m_Background + "'!";

    m_OutputToken = new Token(trail);

    return result;
  }
}
