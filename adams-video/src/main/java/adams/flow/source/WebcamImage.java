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
 * WebcamImage.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.source;

import adams.core.QuickInfoHelper;
import adams.data.conversion.BufferedImageToBufferedImage;
import adams.data.conversion.BufferedImageToOtherFormatConversion;
import adams.data.image.AbstractImageContainer;
import adams.data.image.BufferedImageContainer;
import adams.flow.core.Token;
import boofcv.io.webcamcapture.UtilWebcamCapture;
import com.github.sarxos.webcam.Webcam;

import java.util.List;

/**
 <!-- globalinfo-start -->
 * Grabs a frame from the specified webcam.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
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
 * &nbsp;&nbsp;&nbsp;default: WebcamImage
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
 * <pre>-width &lt;int&gt; (property: width)
 * &nbsp;&nbsp;&nbsp;The width of the image.
 * &nbsp;&nbsp;&nbsp;default: 640
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-height &lt;int&gt; (property: height)
 * &nbsp;&nbsp;&nbsp;The height of the image.
 * &nbsp;&nbsp;&nbsp;default: 480
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-conversion &lt;adams.data.conversion.BufferedImageToOtherFormatConversion&gt; (property: conversion)
 * &nbsp;&nbsp;&nbsp;The conversion for turning the adams.data.image.BufferedImageContainer into 
 * &nbsp;&nbsp;&nbsp;another format if necessary.
 * &nbsp;&nbsp;&nbsp;default: adams.data.conversion.BufferedImageToBufferedImage
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class WebcamImage
  extends AbstractSource {

  /** for serialization. */
  private static final long serialVersionUID = -5718059337341470131L;

  /** the width of the image. */
  protected int m_Width;
  
  /** the height of the image. */
  protected int m_Height;

  /** the conversion to perform. */
  protected BufferedImageToOtherFormatConversion m_Conversion;

  /** the webcam to access. */
  protected String m_Webcam;

  /** the webcam. */
  protected transient Webcam m_Current;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Grabs a frame from the specified webcam.";
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
      "width", "width",
      getDefaultWidth(), 1, null);

    m_OptionManager.add(
      "height", "height",
      getDefaultHeight(), 1, null);

    m_OptionManager.add(
      "conversion", "conversion",
      new BufferedImageToBufferedImage());
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
    result += QuickInfoHelper.toString(this, "width", m_Width, ", resolution: ");
    result += " x ";
    result += QuickInfoHelper.toString(this, "height", m_Height);

    return result;
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
   * Returns the default width of the image.
   *
   * @return		the default width
   */
  public int getDefaultWidth() {
    return 640;
  }

  /**
   * Sets the width of the image.
   *
   * @param value	the width
   */
  public void setWidth(int value) {
    m_Width = value;
    reset();
  }

  /**
   * Returns the width of the image.
   *
   * @return		the width
   */
  public int getWidth() {
    return m_Width;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String widthTipText() {
    return "The width of the image.";
  }

  /**
   * Returns the default height of the image.
   *
   * @return		the default height
   */
  public int getDefaultHeight() {
    return 480;
  }

  /**
   * Sets the height of the image.
   *
   * @param value	the height
   */
  public void setHeight(int value) {
    m_Height = value;
    reset();
  }

  /**
   * Returns the height of the image.
   *
   * @return		the height
   */
  public int getHeight() {
    return m_Height;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String heightTipText() {
    return "The height of the image.";
  }

  /**
   * Sets the conversion for converting the {@link BufferedImageContainer}
   * into another format if necessary.
   *
   * @param value	the conversion
   */
  public void setConversion(BufferedImageToOtherFormatConversion value) {
    m_Conversion = value;
    reset();
  }

  /**
   * Returns the conversion for converting the {@link BufferedImageContainer}
   * into another format if necessary.
   *
   * @return		the conversion
   */
  public BufferedImageToOtherFormatConversion getConversion() {
    return m_Conversion;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String conversionTipText() {
    return
	"The conversion for turning the " + BufferedImageContainer.class.getName()
	+ " into another format if necessary.";
  }

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public Class[] generates() {
    return new Class[]{m_Conversion.generates()};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    List<Webcam> 	webcams;

    result = null;

    if (m_Current == null) {
      try {
	if (m_Webcam.trim().isEmpty()) {
	  m_Current = Webcam.getDefault();
	}
	else {
	  webcams = Webcam.getWebcams();
	  for (Webcam webcam: webcams) {
	    if (webcam.getName().equals(m_Webcam)) {
	      m_Current = webcam;
	      break;
	    }
	  }
	  if (m_Current == null) {
	    result = "Failed to locate webcam '" + m_Webcam + "'!";
	  }
	}
	if (m_Current != null) {
	  UtilWebcamCapture.adjustResolution(m_Current, getWidth(), getHeight());
	  m_Current.open();
	}
      }
      catch (Throwable e) {
	result = handleException("Failed to access webcam:", e);
      }
    }

    return result;
  }

  /**
   * Checks whether there is pending output to be collected after
   * executing the flow item.
   *
   * @return		true if there is pending output
   */
  @Override
  public boolean hasPendingOutput() {
    return (m_Current != null);
  }

  /**
   * Returns the generated token.
   *
   * @return		the generated token
   */
  @Override
  public Token output() {
    Token			result;
    String			msg;
    AbstractImageContainer	cont;

    result = null;

    if (m_Current != null) {
      cont = new BufferedImageContainer();
      cont.setImage(m_Current.getImage());

      if (!(m_Conversion instanceof BufferedImageToBufferedImage)) {
	m_Conversion.setInput(cont);
	msg = m_Conversion.convert();
	if (msg == null)
	  result = new Token(m_Conversion.getOutput());
	m_Conversion.cleanUp();
      }
      else {
	result = new Token(cont);
      }
    }

    return result;
  }

  /**
   * Cleans up after the execution has finished. Graphical output is left
   * untouched.
   */
  @Override
  public void wrapUp() {
    if (m_Current != null) {
      m_Current.close();
      m_Current = null;
    }
    super.wrapUp();
  }
}
