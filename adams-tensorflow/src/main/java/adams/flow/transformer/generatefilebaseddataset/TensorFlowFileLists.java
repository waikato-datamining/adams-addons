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
 * TensorFlowFileLists.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.generatefilebaseddataset;

import adams.core.QuickInfoHelper;
import adams.core.base.BaseObject;
import adams.core.base.BaseString;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderDirectory;
import adams.core.io.PlaceholderFile;
import adams.flow.container.FileBasedDatasetContainer;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Generates simple file lists in the specified directory.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class TensorFlowFileLists
  extends AbstractFileBasedDatasetGeneration<String[]> {

  private static final long serialVersionUID = -8577022998677528239L;

  /** the values in the container to use. */
  protected BaseString[] m_Values;

  /** the output directory for the lists. */
  protected PlaceholderDirectory m_OutputDir;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates simple file lists in the specified directory.\n"
      + "Forwards the names of the generated list files.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "value", "values",
      new BaseString[]{new BaseString(FileBasedDatasetContainer.VALUE_TRAIN)});

    m_OptionManager.add(
      "output-dir", "outputDir",
      new PlaceholderDirectory());
  }

  /**
   * Sets the name(s) of the container value(s) to save.
   *
   * @param value	the value(s)
   */
  public void setValues(BaseString[] value) {
    m_Values = value;
    reset();
  }

  /**
   * Returns the name(s) of the container value(s) to save.
   *
   * @return		the value(s)
   */
  public BaseString[] getValues() {
    return m_Values;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String valueTipText() {
    return "The name(s) of the value(s) in the container to use.";
  }

  /**
   * Sets the output directory.
   *
   * @param value	the dir
   */
  public void setOutputDir(PlaceholderDirectory value) {
    m_OutputDir = value;
    reset();
  }

  /**
   * Returns the output directory.
   *
   * @return		the dir
   */
  public PlaceholderDirectory getOutputDir() {
    return m_OutputDir;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputDirTipText() {
    return "The directory to save the file lists in.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "values", m_Values, "values: ");
    result += QuickInfoHelper.toString(this, "outputDir", m_OutputDir, ", output: ");

    return result;
  }

  /**
   * Returns the class that gets generated.
   *
   * @return		the generated class
   */
  @Override
  public Class generates() {
    return String[].class;
  }

  /**
   * The keys of the values that need to be present in the container.
   *
   * @return		the keys
   */
  @Override
  protected String[] requiredValues() {
    return BaseObject.toStringArray(m_Values);
  }

  /**
   * Performs checks on the container.
   *
   * @param cont	the container to check
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String check(FileBasedDatasetContainer cont) {
    String	result;

    result = super.check(cont);

    if (result == null) {
      if (!m_OutputDir.exists())
        result = "Output dir does not exist: " + m_OutputDir;
      else if (!m_OutputDir.isDirectory())
        result = "Output does not point to a directory: " + m_OutputDir;
    }

    return result;
  }

  /**
   * Generates the dataset.
   *
   * @param cont	the container to use
   * @return		the generated output
   */
  @Override
  protected String[] doGenerate(FileBasedDatasetContainer cont) {
    List<String> 	result;
    String		msg;
    String[]		files;
    PlaceholderFile	outfile;

    result = new ArrayList<>();

    for (BaseString value: m_Values) {
      files   = cont.getValue(value.getValue(), String[].class);
      outfile = new PlaceholderFile(m_OutputDir.getAbsolutePath() + File.separator + value.getValue().toLowerCase() + ".list");
      if (isLoggingEnabled())
        getLogger().info("Storing " + files.length + " files (" + value + ") in " + outfile);
      msg = FileUtils.saveToFileMsg(Arrays.asList(files), outfile, null);
      if (msg != null)
	throw new IllegalStateException(msg);
      result.add(outfile.getAbsolutePath());
    }

    return result.toArray(new String[0]);
  }
}
