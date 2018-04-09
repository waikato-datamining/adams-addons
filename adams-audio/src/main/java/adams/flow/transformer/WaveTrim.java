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
 * WaveTrim.java
 * Copyright (C) 2018 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.data.InPlaceProcessing;
import adams.data.audio.WaveContainer;
import adams.flow.core.Token;
import adams.flow.provenance.ActorType;
import adams.flow.provenance.Provenance;
import adams.flow.provenance.ProvenanceContainer;
import adams.flow.provenance.ProvenanceInformation;
import adams.flow.provenance.ProvenanceSupporter;

/**
 <!-- globalinfo-start -->
 * Trims the Wave object left and&#47;or right, using either sample number of time in seconds.<br>
 * Only works if 'subChunk2Id' is 'data' not 'LIST'.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.audio.WaveContainer<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.audio.WaveContainer<br>
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
 * &nbsp;&nbsp;&nbsp;default: WaveTrim
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
 * &nbsp;&nbsp;&nbsp;If set to true, the flow execution at this level gets stopped in case this
 * &nbsp;&nbsp;&nbsp;actor encounters an error; the error gets propagated; useful for critical
 * &nbsp;&nbsp;&nbsp;actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-type &lt;SAMPLES|SECONDS&gt; (property: type)
 * &nbsp;&nbsp;&nbsp;How to interpret the left&#47;right values.
 * &nbsp;&nbsp;&nbsp;default: SAMPLES
 * </pre>
 *
 * <pre>-left &lt;double&gt; (property: left)
 * &nbsp;&nbsp;&nbsp;The starting point of the trimming.
 * &nbsp;&nbsp;&nbsp;default: 0.0
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * </pre>
 *
 * <pre>-right &lt;double&gt; (property: right)
 * &nbsp;&nbsp;&nbsp;The end point of the trimming; ignored if 0.
 * &nbsp;&nbsp;&nbsp;default: 0.0
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * </pre>
 *
 * <pre>-no-copy &lt;boolean&gt; (property: noCopy)
 * &nbsp;&nbsp;&nbsp;If enabled, no copy of the Wave is created before trimming it.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class WaveTrim
  extends AbstractTransformer
  implements ProvenanceSupporter, InPlaceProcessing {

  /** for serialization. */
  private static final long serialVersionUID = -1998955116780561587L;

  public enum TrimType {
    SAMPLES,
    SECONDS,
  }

  /** how to trim. */
  protected TrimType m_Type;

  /** the left trim. */
  protected double m_Left;

  /** the right trim. */
  protected double m_Right;

  /** whether to skip creating a copy of the image. */
  protected boolean m_NoCopy;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Trims the Wave object left and/or right, using either sample number of time in seconds.\n"
      + "Only works if 'subChunk2Id' is 'data' not 'LIST'.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "type", "type",
      TrimType.SAMPLES);

    m_OptionManager.add(
      "left", "left",
      0.0, 0.0, null);

    m_OptionManager.add(
      "right", "right",
      0.0, 0.0, null);

    m_OptionManager.add(
      "no-copy", "noCopy",
      false);
  }

  /**
   * Sets the trim type to use.
   *
   * @param value	the type
   */
  public void setType(TrimType value) {
    m_Type = value;
    reset();
  }

  /**
   * Returns the trim type in use.
   *
   * @return		the type
   */
  public TrimType getType() {
    return m_Type;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String typeTipText() {
    return "How to interpret the left/right values.";
  }

  /**
   * Sets the starting point of the trim.
   *
   * @param value	the starting point
   */
  public void setLeft(double value) {
    m_Left = value;
    reset();
  }

  /**
   * Returns the starting point of the trim.
   *
   * @return		the starting point
   */
  public double getLeft() {
    return m_Left;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String leftTipText() {
    return "The starting point of the trimming.";
  }

  /**
   * Sets the end point of the trim.
   *
   * @param value	the end point
   */
  public void setRight(double value) {
    m_Right = value;
    reset();
  }

  /**
   * Returns the end point of the trim.
   *
   * @return		the end point
   */
  public double getRight() {
    return m_Right;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String rightTipText() {
    return "The end point of the trimming; ignored if 0.";
  }

  /**
   * Sets whether to skip creating a copy of the wave before trimming.
   *
   * @param value	true if to skip creating copy
   */
  public void setNoCopy(boolean value) {
    m_NoCopy = value;
    reset();
  }

  /**
   * Returns whether to skip creating a copy of the wave before trimming.
   *
   * @return		true if copying is skipped
   */
  public boolean getNoCopy() {
    return m_NoCopy;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String noCopyTipText() {
    return "If enabled, no copy of the Wave is created before trimming it.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "type", m_Type, "type: ");
    result += QuickInfoHelper.toString(this, "left", m_Left, ", left: ");
    result += QuickInfoHelper.toString(this, "right", m_Right, ", right: ");
    result += QuickInfoHelper.toString(this, "noCopy", m_NoCopy, "no-copy", ", ");

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  public Class[] accepts() {
    return new Class[]{WaveContainer.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of objects that get generated
   */
  public Class[] generates() {
    return new Class[]{WaveContainer.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    WaveContainer	cont;
    WaveContainer	contNew;

    result = null;

    cont = m_InputToken.getPayload(WaveContainer.class);
    if (!m_NoCopy)
      contNew = (WaveContainer) cont.getClone();
    else
      contNew = cont;

    switch (m_Type) {
      case SAMPLES:
        contNew.getAudio().trim((int) m_Left, (int) m_Right);
        break;
      case SECONDS:
        contNew.getAudio().trim(m_Left, m_Right);
        break;
      default:
        result = "Unhandled trim type: " + m_Type;
    }

    if (result == null) {
      m_OutputToken = new Token(contNew);
      updateProvenance(m_OutputToken);
    }

    return result;
  }

  /**
   * Updates the provenance information in the provided container.
   *
   * @param cont	the provenance container to update
   */
  public void updateProvenance(ProvenanceContainer cont) {
    if (Provenance.getSingleton().isEnabled()) {
      if (m_InputToken.hasProvenance())
	cont.setProvenance(m_InputToken.getProvenance().getClone());
      cont.addProvenance(new ProvenanceInformation(ActorType.PREPROCESSOR, m_InputToken.getPayload().getClass(), this, ((Token) cont).getPayload().getClass()));
    }
  }
}
