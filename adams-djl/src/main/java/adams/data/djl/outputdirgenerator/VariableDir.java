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
 * VariableDir.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.data.djl.outputdirgenerator;

import adams.core.base.BaseString;
import adams.core.io.PlaceholderDirectory;

/**
 * Expands any variables in the supplied string and returns that as directory.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class VariableDir
  extends AbstractOutputDirGenerator {

  private static final long serialVersionUID = -8875224901669801709L;

  /** the text to use as dir. */
  protected BaseString m_OutputDir;

  /**
   * Default constructor.
   */
  public VariableDir() {
    super();
  }

  /**
   * Instantiates the generator and sets the dir to use.
   *
   * @param dir		the dir to use
   */
  public VariableDir(String dir) {
    this(new BaseString(dir));
  }

  /**
   * Instantiates the generator and sets the dir to use.
   *
   * @param dir		the dir to use
   */
  public VariableDir(BaseString dir) {
    super();
    setOutputDir(dir);
  }

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Expands any variables in the supplied string and returns that as directory.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "output-dir", "outputDir",
      new BaseString("."));
  }

  /**
   * Sets the output dir.
   *
   * @param value 	the dir
   */
  public void setOutputDir(BaseString value) {
    m_OutputDir = value;
    reset();
  }

  /**
   * Gets the output dir.
   *
   * @return 		the dir
   */
  public BaseString getOutputDir() {
    return m_OutputDir;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String outputDirTipText() {
    return "The output directory to use, can contain variables.";
  }

  /**
   * Returns whether flow context is required.
   *
   * @return		true if required
   */
  @Override
  protected boolean requiresFlowContext() {
    return true;
  }

  /**
   * Generates the output directory.
   *
   * @return the directory
   */
  @Override
  public PlaceholderDirectory generate() {
    return new PlaceholderDirectory(m_FlowContext.getVariables().expand(m_OutputDir.getValue()));
  }
}
