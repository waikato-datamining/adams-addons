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
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
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
