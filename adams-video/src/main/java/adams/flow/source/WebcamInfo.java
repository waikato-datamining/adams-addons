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
 * WebcamInfo.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.source;

import adams.core.QuickInfoHelper;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.core.DataInfoActor;
import adams.flow.core.Token;
import com.github.sarxos.webcam.Webcam;

import java.awt.Dimension;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Outputs info on webcams.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.spreadsheet.SpreadSheet<br>
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
 * &nbsp;&nbsp;&nbsp;default: WebcamInfo
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
 * <pre>-webcam &lt;java.lang.String&gt; (property: webcam)
 * &nbsp;&nbsp;&nbsp;The webcam name (leave empty for default one).
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-type &lt;RESOLUTION|AVAILABLE_RESOLUTIONS|IS_OPEN&gt; (property: type)
 * &nbsp;&nbsp;&nbsp;The type of information to generate.
 * &nbsp;&nbsp;&nbsp;default: AVAILABLE_RESOLUTIONS
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WebcamInfo
  extends AbstractSimpleSource
  implements DataInfoActor {

  /** for serialization. */
  private static final long serialVersionUID = -5718059337341470131L;

  public enum InfoType {
    /** current resolution. */
    RESOLUTION,
    /** available resolutions. */
    AVAILABLE_RESOLUTIONS,
    /** whether webcam is in use. */
    IS_OPEN,
  }

  /** the webcam to output the info about. */
  protected String m_Webcam;

  /** the information to retrieve. */
  protected InfoType m_Type;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Outputs info on webcams.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "webcam", "webcam",
      "");

    m_OptionManager.add(
      "type", "type",
      InfoType.AVAILABLE_RESOLUTIONS);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "webcam", (m_Webcam.isEmpty() ? "-default-" : m_Webcam), "webcam: ");
    result += QuickInfoHelper.toString(this, "type", m_Type, ", type: ");

    return result;
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    switch (m_Type) {
      case RESOLUTION:
      case AVAILABLE_RESOLUTIONS:
	return new Class[]{SpreadSheet.class};
      case IS_OPEN:
	return new Class[]{Boolean.class};
      default:
	throw new IllegalArgumentException("Unhandled type: " + m_Type);
    }
  }

  /**
   * Sets the webcam name (leave empty for default one).
   *
   * @param value	the webcam
   */
  public void setWebcam(String value) {
    m_Webcam = value;
    reset();
  }

  /**
   * Returns the webcam name (leave empty for default one).
   *
   * @return		the webcam
   */
  public String getWebcam() {
    return m_Webcam;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String webcamTipText() {
    return "The webcam name (leave empty for default one).";
  }

  /**
   * Sets the type of information to generate.
   *
   * @param value	the type
   */
  public void setType(InfoType value) {
    m_Type = value;
    reset();
  }

  /**
   * Returns the type of information to generate.
   *
   * @return		the type
   */
  public InfoType getType() {
    return m_Type;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String typeTipText() {
    return "The type of information to generate.";
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    List<Webcam>	webcams;
    SpreadSheet		sheet;
    Row			row;
    Webcam		cam;
    Dimension 		dim;

    result = null;

    try {
      cam = null;
      if (m_Webcam.trim().isEmpty()) {
	cam = Webcam.getDefault();
      }
      else {
	webcams = Webcam.getWebcams();
	for (Webcam webcam: webcams) {
	  if (webcam.getName().equals(m_Webcam)) {
	    cam = webcam;
	    break;
	  }
	}
	if (cam == null)
	  result = "Failed to locate webcam '" + m_Webcam + "'!";
      }

      if (cam != null) {
	switch (m_Type) {
	  case RESOLUTION:
	    sheet = new SpreadSheet();
	    row   = sheet.getHeaderRow();
	    row.addCell("W").setContent("Width");
	    row.addCell("H").setContent("Height");
	    dim = cam.getViewSize();
	    if (dim != null) {
	      row = sheet.addRow();
	      row.addCell("W").setContent(dim.getWidth());
	      row.addCell("H").setContent(dim.getHeight());
	    }
	    else {
	      getLogger().warning("Failed to retrieve current resolution - webcam not yet open?");
	    }
	    m_OutputToken = new Token(sheet);
	    break;

	  case AVAILABLE_RESOLUTIONS:
	    sheet = new SpreadSheet();
	    row   = sheet.getHeaderRow();
	    row.addCell("W").setContent("Width");
	    row.addCell("H").setContent("Height");
	    for (Dimension d: cam.getViewSizes()) {
	      row = sheet.addRow();
	      row.addCell("W").setContent(d.getWidth());
	      row.addCell("H").setContent(d.getHeight());
	    }
	    m_OutputToken = new Token(sheet);
	    break;

	  case IS_OPEN:
	    m_OutputToken = new Token(cam.isOpen());
	    break;

	  default:
	    result = "Unhandled info type: " + m_Type;
	}
      }
    }
    catch (Throwable e) {
      result = handleException("Failed to get webcam info:", e);
    }

    return result;
  }
}
