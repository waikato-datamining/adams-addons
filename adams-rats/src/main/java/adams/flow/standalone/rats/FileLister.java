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
 * FileLister.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.standalone.rats;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import adams.core.QuickInfoHelper;
import adams.core.base.BaseRegExp;
import adams.core.io.DirectoryLister;
import adams.core.io.DirectoryLister.Sorting;
import adams.core.io.PlaceholderDirectory;

/**
 <!-- globalinfo-start -->
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FileLister
  extends AbstractRatInput {

  /** for serialization. */
  private static final long serialVersionUID = 4089376907540465883L;

  /** the lister for listing the files. */
  protected DirectoryLister m_Lister;
  
  /** the located files. */
  protected List<String> m_Files;

  /** the waiting period in msec after listing the files. */
  protected int m_WaitList;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Polls files in a directory and forwards them.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "source", "source",
	    new PlaceholderDirectory());

    m_OptionManager.add(
	    "regexp", "regExp",
	    new BaseRegExp(BaseRegExp.MATCH_ALL));

    m_OptionManager.add(
	    "max-files", "maxFiles",
	    -1, -1, null);

    m_OptionManager.add(
	    "sorting", "sorting",
	    Sorting.NO_SORTING);

    m_OptionManager.add(
	    "sort-descending", "sortDescending",
	    false);

    m_OptionManager.add(
	    "wait-list", "waitList",
	    0, 0, null);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_Lister = new DirectoryLister();
    m_Lister.setListDirs(false);
    m_Lister.setListFiles(true);
    m_Lister.setRecursive(false);
    
    m_Files = new ArrayList<String>();
  }

  /**
   * Sets the incoming directory.
   *
   * @param value	the incoming directory
   */
  public void setSource(PlaceholderDirectory value) {
    m_Lister.setWatchDir(value);
    reset();
  }

  /**
   * Returns the incoming directory.
   *
   * @return		the incoming directory.
   */
  public PlaceholderDirectory getSource() {
    return m_Lister.getWatchDir();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String sourceTipText() {
    return "The directory to watch for incoming files.";
  }

  /**
   * Sets the regular expression for the files.
   *
   * @param value	the regular expression
   */
  public void setRegExp(BaseRegExp value) {
    m_Lister.setRegExp(value);
    reset();
  }

  /**
   * Returns the regular expression for the files.
   *
   * @return		the regular expression
   */
  public BaseRegExp getRegExp() {
    return m_Lister.getRegExp();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String regExpTipText() {
    return "The regular expression that the files must match.";
  }

  /**
   * Sets the maximum number of files to list.
   *
   * @param value	the maximum, -1 for unlimited
   */
  public void setMaxFiles(int value) {
    m_Lister.setMaxItems(value);
    reset();
  }

  /**
   * Returns the maximum number of files to list.
   *
   * @return		the maximum, -1 for unlimited
   */
  public int getMaxFiles() {
    return m_Lister.getMaxItems();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String maxFilesTipText() {
    return "The maximum number of files to list; -1 for unlimited.";
  }

  /**
   * Sets the sorting type.
   *
   * @param value 	the sorting
   */
  public void setSorting(Sorting value) {
    m_Lister.setSorting(value);
    reset();
  }

  /**
   * Returns the sorting type.
   *
   * @return 		the sorting
   */
  public Sorting getSorting() {
    return m_Lister.getSorting();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String sortingTipText() {
    return "The type of sorting to perform.";
  }

  /**
   * Sets whether to sort in descending manner.
   *
   * @param value 	true if desending sort manner
   */
  public void setSortDescending(boolean value) {
    m_Lister.setSortDescending(value);
    reset();
  }

  /**
   * Returns whether to sort in descending manner.
   *
   * @return 		true if descending sort manner
   */
  public boolean getSortDescending() {
    return m_Lister.getSortDescending();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String sortDescendingTipText() {
    return "If enabled, the sort direction is descending.";
  }

  /**
   * Sets the number of milli-seconds to wait after listing the files.
   *
   * @param value	the number of milli-seconds
   */
  public void setWaitList(int value) {
    if (value >= 0) {
      m_WaitList = value;
      reset();
    }
    else {
      getLogger().warning("Number of milli-seconds to wait must be >=0, provided: " + value);
    }
  }

  /**
   * Returns the number of milli-seconds to wait after listing the files.
   *
   * @return		the number of milli-seconds
   */
  public int getWaitList() {
    return m_WaitList;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String waitListTipText() {
    return "The number of milli-seconds to wait after listing the fails.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    
    result  = QuickInfoHelper.toString(this, "source", getSource(), "source: ");
    result += QuickInfoHelper.toString(this, "regExp", getRegExp(), ", regexp: ");
    result += QuickInfoHelper.toString(this, "waitList", getWaitList(), ", wait-list: ");
    
    return result;
  }
  
  /**
   * Hook method for performing checks. Makes sure that directories exist.
   * 
   * @throws Exception	if checks fail
   */
  @Override
  public String check() {
    String	result;
    
    result = super.check();
    
    if ((result == null) && (!getSource().exists()))
      result = "Source directory does not exist: " + getSource();
    if ((result == null) && (!getSource().isDirectory()))
      result ="Source is not a directory: " + getSource();
    
    return result;
  }

  /**
   * Returns the type of data this scheme generates.
   * 
   * @return		the type of data
   */
  @Override
  public Class generates() {
    return String.class;
  }

  /**
   * Checks whether any output can be collected.
   * 
   * @return		true if output available
   */
  @Override
  public boolean hasPendingOutput() {
    return (m_Files.size() > 0);
  }

  /**
   * Returns the received data.
   * 
   * @return		the data
   */
  @Override
  public Object output() {
    return m_Files.remove(0);
  }

  /**
   * Performs the actual reception of data.
   * 
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doReceive() {
    String[]	files;
    
    files = m_Lister.list();
    m_Files.addAll(Arrays.asList(files));
    
    doWait(m_WaitList);
    
    return null;
  }
}
