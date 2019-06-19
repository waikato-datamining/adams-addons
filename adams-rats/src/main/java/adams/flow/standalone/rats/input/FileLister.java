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
 * FileLister.java
 * Copyright (C) 2014-2019 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.standalone.rats.input;

import adams.core.ArrayProvider;
import adams.core.AtomicMoveSupporter;
import adams.core.MessageCollection;
import adams.core.QuickInfoHelper;
import adams.core.base.BaseRegExp;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderDirectory;
import adams.core.io.PlaceholderFile;
import adams.core.io.fileuse.AbstractFileUseCheck;
import adams.core.io.fileuse.Default;
import adams.core.io.lister.LocalDirectoryLister;
import adams.core.io.lister.Sorting;
import adams.core.logging.LoggingLevel;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Polls files in a directory and forwards them.<br>
 * It can skip files that are currently flagged as 'in use'.<br>
 * Moving files to the specified target directory will continue, even if errors are occurred with some files (NB: you may end up with a very large error message if all files from a large list of files are failing).
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-source &lt;adams.core.io.PlaceholderDirectory&gt; (property: source)
 * &nbsp;&nbsp;&nbsp;The directory to watch for incoming files.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 * <pre>-regexp &lt;adams.core.base.BaseRegExp&gt; (property: regExp)
 * &nbsp;&nbsp;&nbsp;The regular expression that the files must match.
 * &nbsp;&nbsp;&nbsp;default: .*
 * </pre>
 * 
 * <pre>-max-files &lt;int&gt; (property: maxFiles)
 * &nbsp;&nbsp;&nbsp;The maximum number of files to list; -1 for unlimited.
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 * <pre>-sorting &lt;NO_SORTING|SORT_BY_NAME|SORT_BY_LAST_MODIFIED&gt; (property: sorting)
 * &nbsp;&nbsp;&nbsp;The type of sorting to perform.
 * &nbsp;&nbsp;&nbsp;default: NO_SORTING
 * </pre>
 * 
 * <pre>-sort-descending &lt;boolean&gt; (property: sortDescending)
 * &nbsp;&nbsp;&nbsp;If enabled, the sort direction is descending.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-wait-list &lt;int&gt; (property: waitList)
 * &nbsp;&nbsp;&nbsp;The number of milli-seconds to wait after listing the files.
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-move-files &lt;boolean&gt; (property: moveFiles)
 * &nbsp;&nbsp;&nbsp;If enabled, the files get moved to the specified directory first before 
 * &nbsp;&nbsp;&nbsp;being transmitted (with their new filename).
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-skip-in-use &lt;boolean&gt; (property: skipInUse)
 * &nbsp;&nbsp;&nbsp;If enabled, then files are that currently 'in use' get removed from the 
 * &nbsp;&nbsp;&nbsp;list.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-check &lt;adams.core.io.fileuse.AbstractFileUseCheck&gt; (property: check)
 * &nbsp;&nbsp;&nbsp;If scheme to use checking the 'in use' state of a file.
 * &nbsp;&nbsp;&nbsp;default: adams.core.io.fileuse.Default
 * </pre>
 * 
 * <pre>-atomic-move &lt;boolean&gt; (property: atomicMove)
 * &nbsp;&nbsp;&nbsp;If true, then an atomic move operation will be attempted (NB: not supported 
 * &nbsp;&nbsp;&nbsp;by all operating systems).
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-target &lt;adams.core.io.PlaceholderDirectory&gt; (property: target)
 * &nbsp;&nbsp;&nbsp;The directory to move the files to before transmitting their names.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 * <pre>-output-array &lt;boolean&gt; (property: outputArray)
 * &nbsp;&nbsp;&nbsp;If enabled, the files get output as array rather than one-by-one.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-report-moving-errors &lt;boolean&gt; (property: reportMovingErrors)
 * &nbsp;&nbsp;&nbsp;If enabled, errors encountered while moving files get reported rather than
 * &nbsp;&nbsp;&nbsp;just logged as warnings.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FileLister
  extends AbstractRatInput
  implements AtomicMoveSupporter, ArrayProvider {

  /** for serialization. */
  private static final long serialVersionUID = 4089376907540465883L;

  /** the lister for listing the files. */
  protected LocalDirectoryLister m_Lister;
  
  /** the located files. */
  protected List<String> m_Files;

  /** the waiting period in msec after listing the files. */
  protected int m_WaitList;

  /** whether to move the files before transmitting them. */
  protected boolean m_MoveFiles;

  /** whether to skip 'in-use' files. */
  protected boolean m_SkipInUse;

  /** the 'in use' check scheme. */
  protected AbstractFileUseCheck m_Check;

  /** whether to perform an atomic move. */
  protected boolean m_AtomicMove;

  /** the directory to move the files to. */
  protected PlaceholderDirectory m_Target;

  /** whether to output an array instead of single items. */
  protected boolean m_OutputArray;

  /** whether to return errors when moving files. */
  protected boolean m_ReportMovingErrors;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Polls files in a directory and forwards them.\n"
      + "It can skip files that are currently flagged as 'in use'.\n"
      + "Moving files to the specified target directory will continue, "
      + "even if errors are occurred with some files (NB: you may end up "
      + "with a very large error message if all files from a large list of "
      + "files are failing).";
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

    m_OptionManager.add(
      "move-files", "moveFiles",
      false);

    m_OptionManager.add(
      "skip-in-use", "skipInUse",
      false);

    m_OptionManager.add(
      "check", "check",
      new Default());

    m_OptionManager.add(
      "atomic-move", "atomicMove",
      false);

    m_OptionManager.add(
      "target", "target",
      new PlaceholderDirectory());

    m_OptionManager.add(
      "output-array", "outputArray",
      false);

    m_OptionManager.add(
      "report-moving-errors", "reportMovingErrors",
      false);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_Lister = new LocalDirectoryLister();
    m_Lister.setListDirs(false);
    m_Lister.setListFiles(true);
    m_Lister.setRecursive(false);
    
    m_Files = new ArrayList<>();
  }

  /**
   * Sets the logging level.
   *
   * @param value 	the level
   */
  @Override
  public synchronized void setLoggingLevel(LoggingLevel value) {
    super.setLoggingLevel(value);
    m_Lister.setLoggingLevel(value);
  }

  /**
   * Sets the incoming directory.
   *
   * @param value	the incoming directory
   */
  public void setSource(PlaceholderDirectory value) {
    m_Lister.setWatchDir(value.getAbsolutePath());
    reset();
  }

  /**
   * Returns the incoming directory.
   *
   * @return		the incoming directory.
   */
  public PlaceholderDirectory getSource() {
    return new PlaceholderDirectory(m_Lister.getWatchDir());
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
    return "The number of milli-seconds to wait after listing the files.";
  }

  /**
   * Sets whether to move the files to the specified target directory
   * before transmitting them.
   *
   * @param value	true if to move files
   */
  public void setMoveFiles(boolean value) {
    m_MoveFiles = value;
    reset();
  }

  /**
   * Returns whether to move the files to the specified target directory
   * before transmitting them.
   *
   * @return		true if to move files
   */
  public boolean getMoveFiles() {
    return m_MoveFiles;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String moveFilesTipText() {
    return 
	"If enabled, the files get moved to the specified directory first "
	+ "before being transmitted (with their new filename).";
  }

  /**
   * Sets whether to skip files that are currently in use.
   *
   * @param value	if true then 'in-use' files are skipped
   */
  public void setSkipInUse(boolean value) {
    m_SkipInUse = value;
    reset();
  }

  /**
   * Returns whether to skip files that are currently in use.
   *
   * @return 		true if to skip 'in-use' files
   */
  public boolean getSkipInUse() {
    return m_SkipInUse;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String skipInUseTipText() {
    return
        "If enabled, then files are that currently 'in use' get removed from the list.";
  }

  /**
   * Sets the file 'in use' check scheme.
   *
   * @param value	the check scheme
   */
  public void setCheck(AbstractFileUseCheck value) {
    m_Check = value;
    reset();
  }

  /**
   * Returns the file 'in use' check scheme.
   *
   * @return 		the check scheme
   */
  public AbstractFileUseCheck getCheck() {
    return m_Check;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String checkTipText() {
    return "If scheme to use checking the 'in use' state of a file.";
  }

  /**
   * Sets whether to attempt atomic move operation.
   *
   * @param value	if true then attempt atomic move operation
   */
  public void setAtomicMove(boolean value) {
    m_AtomicMove = value;
    reset();
  }

  /**
   * Returns whether to attempt atomic move operation.
   *
   * @return 		true if to attempt atomic move operation
   */
  public boolean getAtomicMove() {
    return m_AtomicMove;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String atomicMoveTipText() {
    return
        "If true, then an atomic move operation will be attempted "
	  + "(NB: not supported by all operating systems).";
  }

  /**
   * Sets the move-to directory.
   *
   * @param value	the move-to directory
   */
  public void setTarget(PlaceholderDirectory value) {
    m_Target = value;
    reset();
  }

  /**
   * Returns the move-to directory.
   *
   * @return		the move-to directory.
   */
  public PlaceholderDirectory getTarget() {
    return m_Target;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String targetTipText() {
    return "The directory to move the files to before transmitting their names.";
  }

  /**
   * Sets whether to generate data as array or as single objects.
   *
   * @param value	true if output is an array
   */
  public void setOutputArray(boolean value) {
    m_OutputArray = value;
    reset();
  }

  /**
   * Returns whether to generate the as array or as single objects.
   *
   * @return		true if output is an array
   */
  public boolean getOutputArray() {
    return m_OutputArray;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputArrayTipText() {
    return "If enabled, the files get output as array rather than one-by-one.";
  }

  /**
   * Sets whether to report errors encountered while moving files rather
   * than just logging them as warnings.
   *
   * @param value	true if to report
   */
  public void setReportMovingErrors(boolean value) {
    m_ReportMovingErrors = value;
    reset();
  }

  /**
   * Returns whether to report errors encountered while moving files rather
   * than just logging them as warnings.
   *
   * @return		true if to report
   */
  public boolean getReportMovingErrors() {
    return m_ReportMovingErrors;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String reportMovingErrorsTipText() {
    return "If enabled, errors encountered while moving files get reported rather than just logged as warnings.";
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
    result += QuickInfoHelper.toString(this, "moveFiles", (getMoveFiles() ? "move" : "keep"), ", ");
    result += QuickInfoHelper.toString(this, "target", getTarget(), ", target: ");
    result += QuickInfoHelper.toString(this, "outputArray", (getOutputArray() ? "as array" : "one-by-one"), ", ");

    return result;
  }
  
  /**
   * Hook method for performing checks. Makes sure that directories exist.
   *
   * @return		null if successful, otherwise error message
   */
  @Override
  public String check() {
    String	result;
    
    result = super.check();
    
    if ((result == null) && (!getSource().exists()))
      result = "Source directory does not exist: " + getSource();
    if ((result == null) && (!getSource().isDirectory()))
      result ="Source is not a directory: " + getSource();
    
    if (m_MoveFiles) {
      if ((result == null) && (!getTarget().exists()))
	result = "Target directory does not exist: " + getTarget();
      if ((result == null) && (!getTarget().isDirectory()))
	result ="Target is not a directory: " + getTarget();
    }
    
    return result;
  }

  /**
   * Returns the type of data this scheme generates.
   * 
   * @return		the type of data
   */
  @Override
  public Class generates() {
    if (m_OutputArray)
      return String[].class;
    else
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
    Object  result;

    if (m_OutputArray) {
      result = m_Files.toArray(new String[m_Files.size()]);
      m_Files.clear();
    }
    else {
      result = m_Files.remove(0);
    }

    return result;
  }

  /**
   * Performs the actual reception of data.
   * 
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doReceive() {
    String		result;
    MessageCollection 	errors;
    List<String>	files;
    List<String>	filesOut;
    int			i;
    PlaceholderFile	file;

    result = null;
    files  = new ArrayList<>(Arrays.asList(m_Lister.list()));
    if (isLoggingEnabled())
      getLogger().info("# files found: " + files.size());
    doWait(m_WaitList);

    if (files.size() > 0) {
      i = 0;
      while (i < files.size() && !isStopped()) {
        file = new PlaceholderFile(files.get(i));
        if (m_SkipInUse) {
          if (isLoggingEnabled())
            getLogger().fine("In use? " + file);
          if (m_Check.isInUse(file)) {
            if (isLoggingEnabled())
              getLogger().fine("File is in use: " + files.get(i));
            files.remove(i);
            continue;
          }
        }
	i++;
      }

      if (m_MoveFiles && !isStopped()) {
	errors   = new MessageCollection();
	filesOut = new ArrayList<>();
	for (i = 0; i < files.size(); i++) {
	  file = new PlaceholderFile(files.get(i));
	  try {
	    if (!FileUtils.move(file, m_Target, m_AtomicMove))
	      errors.add("Failed to move '" + file + "' to '" + m_Target + "'!");
	    else
	      filesOut.add(m_Target.getAbsolutePath() + File.separator + file.getName());
	  }
	  catch (Exception e) {
	    errors.add("Failed to move '" + file + "' to '" + m_Target + "': ", e);
	  }
	}
	if (!errors.isEmpty()) {
	  if (m_ReportMovingErrors)
	    result = errors.toString();
	  else
	    getLogger().warning(errors.toString());
	}
	files = filesOut;
      }

      if (!isStopped()) {
	if (isLoggingEnabled()) {
	  getLogger().info("# files before add: " + m_Files.size());
	  getLogger().info("# files to add: " + files.size());
	}
	m_Files.addAll(files);
	if (isLoggingEnabled())
	  getLogger().info("# files after add: " + m_Files.size());
      }
    }

    return result;
  }
}
