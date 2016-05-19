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
 * FileSplitConfigurator.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.ml.dl4j.inputsplit;

import adams.core.Randomizable;
import adams.core.base.BaseObject;
import adams.core.base.BaseString;
import adams.core.io.PlaceholderDirectory;
import org.canova.api.split.FileSplit;
import org.canova.api.split.InputSplit;

import java.util.Random;

/**
 <!-- globalinfo-start -->
 * Configures a org.canova.api.split.FileSplit instance.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-root-dir &lt;adams.core.io.PlaceholderDirectory&gt; (property: rootDir)
 * &nbsp;&nbsp;&nbsp;The root directory to use.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 * <pre>-allow-format &lt;adams.core.base.BaseString&gt; [-allow-format ...] (property: allowFormat)
 * &nbsp;&nbsp;&nbsp;The acceptable extensions (without dot); all if 0-length array.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-recursive &lt;boolean&gt; (property: recursive)
 * &nbsp;&nbsp;&nbsp;If enabled, files are listed recursively.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-randomize &lt;boolean&gt; (property: randomize)
 * &nbsp;&nbsp;&nbsp;If enabled, randomization is used (automatically uses recursion).
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-seed &lt;long&gt; (property: seed)
 * &nbsp;&nbsp;&nbsp;The seed value to use for randomization.
 * &nbsp;&nbsp;&nbsp;default: 1
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FileSplitConfigurator
  extends AbstractInputSplitConfigurator
  implements Randomizable {

  private static final long serialVersionUID = -384107247374795362L;

  /** the root directory. */
  protected PlaceholderDirectory m_RootDir;

  /** the allowed formats (not dots). */
  protected BaseString[] m_AllowFormat;

  /** whether to search recursively. */
  protected boolean m_Recursive;

  /** whether to randomize. */
  protected boolean m_Randomize;

  /** the seed value. */
  protected long m_Seed;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Configures a " + FileSplit.class.getName() + " instance.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "root-dir", "rootDir",
      new PlaceholderDirectory());

    m_OptionManager.add(
      "allow-format", "allowFormat",
      new BaseString[0]);

    m_OptionManager.add(
      "recursive", "recursive",
      false);

    m_OptionManager.add(
      "randomize", "randomize",
      false);

    m_OptionManager.add(
      "seed", "seed",
      1L);
  }

  /**
   * Sets the root directory to use.
   *
   * @param value	the directory
   */
  public void setRootDir(PlaceholderDirectory value) {
    m_RootDir = value;
    reset();
  }

  /**
   * Returns the root directory to use.
   *
   * @return 		the directory
   */
  public PlaceholderDirectory getRootDir() {
    return m_RootDir;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String rootDirTipText() {
    return "The root directory to use.";
  }

  /**
   * Sets the acceptable extensions; no filtering if 0-length array.
   *
   * @param value	the acceptable extensions (no dot)
   */
  public void setAllowFormat(BaseString[] value) {
    m_AllowFormat = value;
    reset();
  }

  /**
   * Returns the acceptable extensions; no filtering if 0-length array.
   *
   * @return 		the acceptable extensions (no dot)
   */
  public BaseString[] getAllowFormat() {
    return m_AllowFormat;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String allowFormatTipText() {
    return "The acceptable extensions (without dot); all if 0-length array.";
  }

  /**
   * Sets whether to list the files recursively.
   *
   * @param value	the true if recursive
   */
  public void setRecursive(boolean value) {
    m_Recursive = value;
    reset();
  }

  /**
   * Returns whether to list the files recursively.
   *
   * @return 		true if recursive
   */
  public boolean getRecursive() {
    return m_Recursive;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String recursiveTipText() {
    return "If enabled, files are listed recursively.";
  }

  /**
   * Sets whether to randomize.
   *
   * @param value	the true if to randomize
   */
  public void setRandomize(boolean value) {
    m_Randomize = value;
    reset();
  }

  /**
   * Returns whether to randomize.
   *
   * @return 		true if to randomize
   */
  public boolean getRandomize() {
    return m_Randomize;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String randomizeTipText() {
    return "If enabled, randomization is used (automatically uses recursion).";
  }

  /**
   * Sets the seed value.
   *
   * @param value	the seed
   */
  public void setSeed(long value) {
    m_Seed = value;
    reset();
  }

  /**
   * Returns the seed value.
   *
   * @return  		the seed
   */
  public long getSeed() {
    return m_Seed;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String seedTipText() {
    return "The seed value to use for randomization.";
  }

  /**
   * Configures the actual {@link InputSplit} and returns it.
   *
   * @return		the input split
   */
  @Override
  protected InputSplit doConfigureInputSplit() {
    if (m_Randomize)
      return new FileSplit(
	m_RootDir.getAbsoluteFile(),
	m_AllowFormat.length > 0 ? BaseObject.toStringArray(m_AllowFormat) : null,
	new Random(m_Seed)
      );
    else
      return new FileSplit(
	m_RootDir.getAbsoluteFile(),
	m_AllowFormat.length > 0 ? BaseObject.toStringArray(m_AllowFormat) : null,
	m_Recursive
      );
  }
}
