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
 * RemoveEmoticons.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package weka.core.tokenizers.cleaners;

import adams.core.io.FileUtils;
import weka.core.WekaOptionUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * Replaces words with clusters.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WordCluster
  extends AbstractTokenCleaner {

  private static final long serialVersionUID = 4367295660326278568L;

  public static final String MODEL = "model";

  /** in case the word has no cluster mapping. */
  public static final String UNKNOWN_WORD = "???";

  /** the model to use. */
  protected File m_Model = getDefaultModel();

  /** the clusters (word -> cluster). */
  protected transient Map<String,String> m_Clusters;

  /**
   * Returns a string describing the cleaner.
   *
   * @return a description suitable for displaying in the explorer/experimenter
   *         gui
   */
  public String globalInfo() {
    return "Replaces words with clusters.";
  }

  /**
   * Returns an enumeration describing the available options.
   *
   * @return an enumeration of all the available options.
   */
  @Override
  public Enumeration listOptions() {
    Vector result = new Vector();
    WekaOptionUtils.addOption(result, modelTipText(), "" + getDefaultModel(), MODEL);
    WekaOptionUtils.add(result, super.listOptions());
    return WekaOptionUtils.toEnumeration(result);
  }

  /**
   * Sets the OptionHandler's options using the given list. All options
   * will be set (or reset) during this call (i.e. incremental setting
   * of options is not possible).
   *
   * @param options the list of options as an array of strings
   * @throws Exception if an option is not supported
   */
  @Override
  public void setOptions(String[] options) throws Exception {
    setModel(WekaOptionUtils.parse(options, MODEL, getDefaultModel()));
    super.setOptions(options);
  }

  /**
   * Gets the current option settings for the OptionHandler.
   *
   * @return the list of current option settings as an array of strings
   */
  @Override
  public String[] getOptions() {
    List<String> result = new ArrayList<>();
    WekaOptionUtils.add(result, MODEL, getModel());
    WekaOptionUtils.add(result, super.getOptions());
    return WekaOptionUtils.toArray(result);
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Clusters = null;
  }

  /**
   * Returns the default model file.
   *
   * @return		the default
   */
  protected File getDefaultModel() {
    return new File(".");
  }

  /**
   * Sets the model file to load and use.
   *
   * @param value	the model
   */
  public void setModel(File value) {
    m_Model = value;
    reset();
  }

  /**
   * Returns the model file to load and use.
   *
   * @return		the model
   */
  public File getModel() {
    return m_Model;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String modelTipText() {
    return "The tab-separated model file to load and use; no header; 1st column is cluster, 2nd column is word.";
  }

  /**
   * Determines whether a token is clean or not.
   *
   * @param token	the token to check
   * @return		the clean token or null to ignore
   */
  @Override
  public String clean(String token) {
    if (m_Clusters == null) {
      if (!m_Model.exists())
	throw new IllegalStateException("Cluster model file does not exist: " + m_Model);
      if (m_Model.isDirectory())
	throw new IllegalStateException("Cluster model file points to a directory: " + m_Model);
      m_Clusters = new HashMap<>();
      List<String> lines = FileUtils.loadFromFile(m_Model);
      for (String line: lines) {
        String[] parts = line.split("\t");
        if (parts.length >= 2)
          m_Clusters.put(parts[1], parts[0]);
      }
    }

    if (m_Clusters.containsKey(token))
      return m_Clusters.get(token);
    else
      return UNKNOWN_WORD;
  }
}
