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
 * SimpleImageHeatmapWriter.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.output;

import adams.data.conversion.HeatmapToBufferedImage;
import adams.data.heatmap.Heatmap;
import adams.data.image.AbstractImageContainer;
import adams.data.io.input.SimpleImageHeatmapReader;

import java.util.List;
import java.util.logging.Level;

/**
 <!-- globalinfo-start -->
 * Turns the heatmap into an image and uses the specified image writer for saving it to a file.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-output &lt;adams.core.io.PlaceholderFile&gt; (property: output)
 * &nbsp;&nbsp;&nbsp;The file to write the container to.
 * &nbsp;&nbsp;&nbsp;default: ${TMP}&#47;out.tmp
 * </pre>
 * 
 * <pre>-conversion &lt;adams.data.conversion.HeatmapToBufferedImage&gt; (property: conversion)
 * &nbsp;&nbsp;&nbsp;The conversion for turning the heatmap into an image before writing it to 
 * &nbsp;&nbsp;&nbsp;a file.
 * &nbsp;&nbsp;&nbsp;default: adams.data.conversion.HeatmapToBufferedImage -generator adams.gui.visualization.core.BiColorGenerator
 * </pre>
 * 
 * <pre>-writer &lt;adams.data.io.output.AbstractImageWriter&gt; (property: writer)
 * &nbsp;&nbsp;&nbsp;The image writer to use.
 * &nbsp;&nbsp;&nbsp;default: adams.data.io.output.JAIImageWriter
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SimpleImageHeatmapWriter
  extends AbstractHeatmapWriter {

  /** for serialization. */
  private static final long serialVersionUID = -7828811375813385465L;

  /** for converting the heatmap into an image. */
  protected HeatmapToBufferedImage m_Conversion;

  /** the writer to use. */
  protected AbstractImageWriter m_Writer;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Turns the heatmap into an image and uses the specified image writer for saving it to a file.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "conversion", "conversion",
      new HeatmapToBufferedImage());

    m_OptionManager.add(
      "writer", "writer",
      new JAIImageWriter());
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_OutputIsFile = true;
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return a description suitable for displaying in the file chooser
   */
  @Override
  public String getFormatDescription() {
    return new SimpleImageHeatmapReader().getFormatDescription();
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return the extension(s) (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new SimpleImageHeatmapReader().getFormatExtensions();
  }

  /**
   * Sets the conversion for converting the heatmap into an image for the
   * image writer.
   *
   * @param value 	the conversion
   */
  public void setConversion(HeatmapToBufferedImage value) {
    m_Conversion = value;
    reset();
  }

  /**
   * Returns the conversion to use for converting the heatmap into an image
   * for the image writer.
   *
   * @return 		the conversion
   */
  public HeatmapToBufferedImage getConversion() {
    return m_Conversion;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String conversionTipText() {
    return "The conversion for turning the heatmap into an image before writing it to a file.";
  }

  /**
   * Sets the image writer to use.
   *
   * @param value 	the writer
   */
  public void setWriter(AbstractImageWriter value) {
    m_Writer = value;
    reset();
  }

  /**
   * Returns the image writer to use.
   *
   * @return 		the writer
   */
  public AbstractImageWriter getWriter() {
    return m_Writer;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String writerTipText() {
    return "The image writer to use.";
  }

  /**
   * Performs the actual writing.
   *
   * @param data	the data to write
   * @return		true if successfully written
   */
  @Override
  protected boolean writeData(List<Heatmap> data) {
    boolean			result;
    String			msg;
    AbstractImageContainer	cont;

    result = false;

    try {
      m_Conversion.setInput(data.get(0));
      msg = m_Conversion.convert();
      if (msg == null) {
	cont   = (AbstractImageContainer) m_Conversion.getOutput();
	msg    = m_Writer.write(m_Output, cont);
	result = (msg == null);
      }
      if (msg != null)
	getLogger().severe("Failed to write heatmap to '" + m_Output + "': " + msg);
      m_Conversion.cleanUp();
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to write heatmap to '" + m_Output + "':", e);
    }

    return result;
  }
}
