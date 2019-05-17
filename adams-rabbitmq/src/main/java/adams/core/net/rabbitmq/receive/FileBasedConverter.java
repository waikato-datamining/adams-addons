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
 * FileBasedConverter.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.core.net.rabbitmq.receive;

import adams.core.MessageCollection;
import adams.core.QuickInfoHelper;
import adams.core.SerializationHelper;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderDirectory;

import java.io.File;

/**
 * Instead of sending potentially large payloads via a RabbitMQ, this
 * meta-converter loads the actual payload from the specified directory
 * using the file name (without path) received via RabbitMQ.
 * Of course, requires sending and receiving ends to have access to the
 * same directory.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class FileBasedConverter
  extends AbstractConverter {

  private static final long serialVersionUID = -736244897402323379L;

  /** the base converter. */
  protected AbstractConverter m_Converter;

  /** the directory to store the actual payload in. */
  protected PlaceholderDirectory m_PayloadDir;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Instead of sending potentially large payloads via a RabbitMQ, this "
      + "meta-converter loads the actual payload from the specified directory "
      + "using the file name (without path) received via RabbitMQ.\n"
      + "Of course, requires sending and receiving ends to have access to the "
      + "same directory.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "converter", "converter",
      new BinaryConverter());

    m_OptionManager.add(
      "payload-dir", "payloadDir",
      new PlaceholderDirectory());
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "converter", m_Converter, "converter: ");
    result += QuickInfoHelper.toString(this, "payloadDir", m_PayloadDir, ", dir: ");

    return result;
  }

  /**
   * Sets the base converter to use.
   *
   * @param value	the converter
   */
  public void setConverter(AbstractConverter value) {
    m_Converter = value;
    reset();
  }

  /**
   * Returns the base converter to use.
   *
   * @return 		the converter
   */
  public AbstractConverter getConverter() {
    return m_Converter;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String converterTipText() {
    return "The base converter for performing the actual conversion.";
  }

  /**
   * Sets the directory for storing the payloads in.
   *
   * @param value	the directory
   */
  public void setPayloadDir(PlaceholderDirectory value) {
    m_PayloadDir = value;
    reset();
  }

  /**
   * Returns the directory for storing the payloads in.
   *
   * @return 		the directory
   */
  public PlaceholderDirectory getPayloadDir() {
    return m_PayloadDir;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String payloadDirTipText() {
    return "The directory for saving the actual payload to.";
  }

  /**
   * Returns the class that the converter generates.
   *
   * @return		the class
   */
  @Override
  public Class generates() {
    return m_Converter.generates();
  }

  /**
   * Converts the payload.
   *
   * @param payload	the payload
   * @param errors	for recording errors
   * @return		null if failed to convert, otherwise byte array
   */
  @Override
  protected Object doConvert(byte[] payload, MessageCollection errors) {
    Object	result;
    Object 	filename;
    File	file;
    byte[]	data;

    filename = new StringConverter().convert(payload, errors);
    if (!errors.isEmpty())
      return null;

    file = new File(m_PayloadDir.getAbsolutePath() + File.separator + filename);
    try {
      data = (byte[]) SerializationHelper.read(file.getAbsolutePath());
      result = m_Converter.convert(data, errors);
      if (!FileUtils.delete(file))
        getLogger().warning("Failed to delete payload file: " + file);
    }
    catch (Exception e) {
      errors.add("Failed to deserialize payload from: " + file, e);
      result = null;
    }

    return result;
  }
}
