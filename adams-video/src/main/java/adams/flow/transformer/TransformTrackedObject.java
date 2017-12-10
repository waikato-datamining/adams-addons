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
 * TransformTrackedObject.java
 * Copyright (C) 2015-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.base.QuadrilateralLocation;
import adams.data.image.AbstractImageContainer;
import adams.data.image.BufferedImageContainer;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.flow.core.Actor;
import adams.flow.core.ActorUtils;
import adams.flow.core.CallableActorHelper;
import adams.flow.core.CallableActorReference;
import adams.flow.core.CallableActorUser;
import adams.flow.core.Compatibility;
import adams.flow.core.InputConsumer;
import adams.flow.core.OutputProducer;
import adams.flow.core.Token;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Hashtable;

/**
 <!-- globalinfo-start -->
 * Transforms the tracked object, as specified in the report, using the specified callable transformer.
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
 * &nbsp;&nbsp;&nbsp;default: TransformTrackedObject
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
 * <pre>-transformer &lt;adams.flow.core.CallableActorReference&gt; (property: transformer)
 * &nbsp;&nbsp;&nbsp;The callable transformer to apply to the tracked object (optional).
 * &nbsp;&nbsp;&nbsp;default: unknown
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TransformTrackedObject
  extends AbstractTransformer
  implements CallableActorUser {

  /** for serialization. */
  private static final long serialVersionUID = 3690378527551302472L;

  /** the key for backing up the callable actor. */
  public final static String BACKUP_CALLABLEACTOR = "callable actor";

  /** the report field to store the tracked location in. */
  protected Field m_Location;

  /** the callable transformer to apply to the tracked object. */
  protected CallableActorReference m_Transformer;

  /** the helper class. */
  protected CallableActorHelper m_Helper;

  /** the callable actor. */
  protected Actor m_CallableActor;

  /** for compatibility comparisons. */
  protected Compatibility m_Compatibility;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Transforms the tracked object, as specified in the report, using the "
	+ "specified callable transformer.";
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

    m_OptionManager.add(
      "transformer", "transformer",
      new CallableActorReference(CallableActorReference.UNKNOWN));
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Helper = new CallableActorHelper();
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_CallableActor = null;
    m_Compatibility = null;
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
   * Sets the reference to the callable transformer to apply to tracked
   * object (optional).
   *
   * @param value	the reference
   */
  public void setTransformer(CallableActorReference value) {
    m_Transformer = value;
    reset();
  }

  /**
   * Returns the reference to the callable transformer to apply to tracked
   * object (optional).
   *
   * @return		the reference
   */
  public CallableActorReference getTransformer() {
    return m_Transformer;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String transformerTipText() {
    return "The callable transformer to apply to the tracked object (optional).";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "location", m_Location, "location: ");
    result += QuickInfoHelper.toString(this, "transformer", m_Transformer, ", transformer: ");

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
   * Tries to find the callable actor referenced by its callable name.
   *
   * @return		the callable actor or null if not found
   */
  protected Actor findCallableActor() {
    return m_Helper.findCallableActorRecursive(this, getTransformer());
  }

  /**
   * Checks whether a reference to the callable actor is currently available.
   *
   * @return		true if a reference is available
   * @see		#getCallableActor()
   */
  public boolean hasCallableActor() {
    return (m_CallableActor != null);
  }

  /**
   * Returns the currently set callable actor.
   *
   * @return		the actor, can be null
   */
  @Override
  public Actor getCallableActor() {
    return m_CallableActor;
  }

  /**
   * Removes entries from the backup.
   */
  @Override
  protected void pruneBackup() {
    super.pruneBackup();
    pruneBackup(BACKUP_CALLABLEACTOR);
  }

  /**
   * Backs up the current state of the actor before update the variables.
   *
   * @return		the backup
   */
  @Override
  protected Hashtable<String,Object> backupState() {
    Hashtable<String,Object>	result;

    result = super.backupState();

    if (m_CallableActor != null)
      result.put(BACKUP_CALLABLEACTOR, m_CallableActor);

    return result;
  }

  /**
   * Restores the state of the actor before the variables got updated.
   *
   * @param state	the backup of the state to restore from
   */
  @Override
  protected void restoreState(Hashtable<String,Object> state) {
    super.restoreState(state);

    if (state.containsKey(BACKUP_CALLABLEACTOR)) {
      m_CallableActor = (Actor) state.get(BACKUP_CALLABLEACTOR);
      state.remove(BACKUP_CALLABLEACTOR);
    }
  }

  /**
   * Configures the callable actor.
   *
   * @return		null if successful, otherwise error message
   */
  protected String setUpCallableActor() {
    String		result;
    HashSet<String> 	variables;
    Compatibility	comp;
    Class[]		accepts;
    Class[]		generates;
    Class[]		acceptsExp;
    Class[]		generatesExp;

    result = null;

    m_CallableActor = findCallableActor();
    if (m_CallableActor != null) {
      if (ActorUtils.isTransformer(m_CallableActor)) {
	// compatible?
	comp         = new Compatibility();
	accepts      = ((InputConsumer) m_CallableActor).accepts();
	generates    = ((OutputProducer) m_CallableActor).generates();
	acceptsExp   = new Class[]{AbstractImageContainer.class, BufferedImageContainer.class};
	generatesExp = new Class[]{AbstractImageContainer.class, BufferedImageContainer.class};
	if (!comp.isCompatible(acceptsExp, accepts)) {
	  result = "Callable actor '" + m_Transformer + "' does not accept "
	    + Utils.classesToString(acceptsExp) + ", but "
	    + Utils.classesToString(accepts) + ".";
	}
	else if (!comp.isCompatible(generates, generatesExp)) {
	  result = "Callable actor '" + m_Transformer + "' does not generate "
	    + Utils.classesToString(generatesExp) + ", but "
	    + Utils.classesToString(generates) + ".";
	}
	// check for variables
	if (result == null) {
	  variables = findVariables(m_CallableActor);
	  m_DetectedVariables.addAll(variables);
	  if (m_DetectedVariables.size() > 0)
	    getVariables().addVariableChangeListener(this);
	}
      }
      else {
	result = "Callable actor '" + getTransformer() + "' is not a transformer!";
      }
    }
    else {
      m_CallableActor = new PassThrough();
    }

    return result;
  }

  /**
   * Initializes the item for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String	result;
    String	variable;

    result = super.setUp();

    if (result == null) {
      // do we have to wait till execution time because of attached variable?
      variable = getOptionManager().getVariableForProperty("transformer");
      if (variable == null)
	result = setUpCallableActor();
    }

    return result;
  }

  /**
   * Applies the callable transformer to the tracked object and updates
   * the container.
   *
   * @param cont	the image with the tracked object
   * @param location	the location of the object
   * @return		the updated container
   */
  protected AbstractImageContainer transformTrackedObject(AbstractImageContainer cont, QuadrilateralLocation location) {
    AbstractImageContainer	result;
    AbstractImageContainer	trans;
    BufferedImageContainer	objCont;
    BufferedImage 		objImg;
    BufferedImage 		img;
    Rectangle 			objLoc;
    String			msg;
    Graphics2D 			g;
    int				x;
    int				y;
    int				width;
    int				height;

    objCont = new BufferedImageContainer();
    img    = cont.toBufferedImage();
    objLoc = location.rectangleValue();
    x      = Math.max(0, objLoc.x);
    y      = Math.max(0, objLoc.y);
    width  = objLoc.width  - (x - objLoc.x);
    height = objLoc.height - (y - objLoc.y);
    objImg = img.getSubimage(x, y, width, height);
    objCont.setImage(objImg);

    ((InputConsumer) m_CallableActor).input(new Token(objCont));
    msg = m_CallableActor.execute();
    if (msg != null) {
      getLogger().warning("Failed to apply transformer: " + msg);
      result = cont;
    }
    else {
      trans = (AbstractImageContainer) ((OutputProducer) m_CallableActor).output().getPayload();
      objImg = trans.toBufferedImage();
      g      = img.createGraphics();
      g.drawImage(objImg, x, y, null);
      g.dispose();
      result = new BufferedImageContainer();
      result.setImage(img);
      result.setReport(cont.getReport().getClone());
      result.setNotes(cont.getNotes().getClone());
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
    String			result;
    AbstractImageContainer	cont;
    QuadrilateralLocation	location;

    result = null;

    // is variable attached?
    if (m_CallableActor == null)
      result = setUpCallableActor();

    cont     = (AbstractImageContainer) m_InputToken.getPayload();
    location = null;

    if (cont.getReport().hasValue(m_Location))
      location = new QuadrilateralLocation(cont.getReport().getStringValue(m_Location));

    // transform tracked object?
    if (!(m_CallableActor instanceof PassThrough) && (location != null))
      cont = transformTrackedObject(cont, location);

    m_OutputToken = new Token(cont);

    return result;
  }
}
