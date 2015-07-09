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
 * MjpegImageSequence.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.base.QuadrilateralLocation;
import adams.data.image.AbstractImageContainer;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.flow.core.Token;
import adams.flow.transformer.objecttracker.BoofCVCirculant;
import adams.flow.transformer.objecttracker.ObjectTracker;

/**
 <!-- globalinfo-start -->
 * Tracks objects in images using the provided tracker algorithm.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.image.AbstractImageContainer<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.image.AbstractImageContainer<br>
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
 * &nbsp;&nbsp;&nbsp;default: TrackObjects
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
 * <pre>-algorithm &lt;adams.flow.transformer.objecttracker.ObjectTracker&gt; (property: algorithm)
 * &nbsp;&nbsp;&nbsp;The object tracking algorithm to use.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.transformer.objecttracker.BoofCVCirculant
 * </pre>
 * 
 * <pre>-init &lt;adams.data.report.Field&gt; (property: init)
 * &nbsp;&nbsp;&nbsp;The field with the initial object location.
 * &nbsp;&nbsp;&nbsp;default: Tracker.Init[S]
 * </pre>
 * 
 * <pre>-current &lt;adams.data.report.Field&gt; (property: current)
 * &nbsp;&nbsp;&nbsp;The field to store the current location of the object in.
 * &nbsp;&nbsp;&nbsp;default: Tracker.Current[S]
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TrackObjects
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 3690378527551302472L;

  /** the object tracker. */
  protected ObjectTracker m_Algorithm;

  /** the report field with the location to initialize the tracker with. */
  protected Field m_Init;

  /** the report field to store the tracked location in. */
  protected Field m_Current;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Tracks objects in images using the provided tracker algorithm.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "algorithm", "algorithm",
      new BoofCVCirculant());

    m_OptionManager.add(
      "init", "init",
      new Field("Tracker.Init", DataType.STRING));

    m_OptionManager.add(
      "current", "current",
      new Field("Tracker.Current", DataType.STRING));
  }

  /**
   * Sets the tracking algorithm.
   *
   * @param value	the algorithm
   */
  public void setAlgorithm(ObjectTracker value) {
    m_Algorithm = value;
    reset();
  }

  /**
   * Returns the tracking algorithm.
   *
   * @return		the algorithm
   */
  public ObjectTracker getAlgorithm() {
    return m_Algorithm;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String algorithmTipText() {
    return "The object tracking algorithm to use.";
  }

  /**
   * Sets the field with the location for initializing the tracker.
   *
   * @param value	the field
   */
  public void setInit(Field value) {
    m_Init = value;
    reset();
  }

  /**
   * Returns the field with the location for initializing the tracker.
   *
   * @return		the field
   */
  public Field getInit() {
    return m_Init;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String initTipText() {
    return "The field with the initial object location.";
  }

  /**
   * Sets the field to store the current location of the object in.
   *
   * @param value	the field
   */
  public void setCurrent(Field value) {
    m_Current = value;
    reset();
  }

  /**
   * Returns the field to store the current location of the object in.
   *
   * @return		the field
   */
  public Field getCurrent() {
    return m_Current;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String currentTipText() {
    return "The field to store the current location of the object in.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "algorithm", m_Algorithm, "algorithm: ");
    result += QuickInfoHelper.toString(this, "init", m_Init, ", init: ");
    result += QuickInfoHelper.toString(this, "current", m_Current, ", current: ");

    return result;
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
    return new Class[]{AbstractImageContainer.class};
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

    cont = (AbstractImageContainer) m_InputToken.getPayload();

    // init?
    if (cont.getReport().hasValue(m_Init)) {
      location = new QuadrilateralLocation(cont.getReport().getStringValue(m_Init));
      result = m_Algorithm.initTracking(cont, location);
    }
    // track?
    else if (m_Algorithm.isInitialized()) {
      location = m_Algorithm.trackObject(cont);
      if (location == null) {
	result = "Failed to track object!";
      }
      else {
	cont.getReport().addField(m_Current);
	cont.getReport().setValue(m_Current, location.toString());
      }
    }

    m_OutputToken = new Token(cont);

    return result;
  }
}
