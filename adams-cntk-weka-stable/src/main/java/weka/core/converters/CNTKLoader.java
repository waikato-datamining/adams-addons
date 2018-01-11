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
 * CNTKLoader.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package weka.core.converters;

import adams.data.io.input.CNTKSpreadSheetReader;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Environment;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.OptionHandler;
import weka.core.RevisionUtils;
import weka.core.SparseInstance;
import weka.core.Utils;
import weka.core.WekaOptionUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import java.util.regex.Pattern;

/**
 <!-- globalinfo-start -->
 * Reads CNTK data files in text format.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p>
 *
 * <pre> -D &lt;value&gt;
 *  Whether to print additional debug information to the console.
 *  (default: off)</pre>
 *
 * <pre> -class-group &lt;value&gt;
 *  The group representing the class attribute.
 *  (default: )</pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class CNTKLoader
  extends AbstractFileLoader
  implements BatchConverter, OptionHandler {

  private static final long serialVersionUID = 532290589164256915L;

  /** Holds the determined structure (header) of the data set. */
  protected Instances m_structure = null;

  /** the actual data. */
  protected Instances m_Data = null;

  /** Holds the source of the data set. */
  protected File m_sourceFile = new File(System.getProperty("user.dir"));

  /** whether to print some debug information */
  protected boolean m_Debug = false;

  /** the group that represents the class attribute. */
  protected String m_ClassGroup = "";

  public CNTKLoader() {
    setRetrieval(NONE);
  }

  /**
   * Returns a string suitable for the GUI.
   *
   * @return		the string
   */
  public String globalInfo() {
    return "Reads CNTK data files in text format.";
  }

  /**
   * Lists the available options
   *
   * @return 		an enumeration of the available options
   */
  public Enumeration listOptions() {
    Vector result;

    result = new Vector();

    WekaOptionUtils.addOption(result, debugTipText(), "off", "D");
    WekaOptionUtils.addOption(result, classGroupTipText(), getDefaultClassGroup(), "class-group");

    return WekaOptionUtils.toEnumeration(result);
  }

  /**
   * Parses a given list of options.
   *
   * @param options the options
   * @throws Exception if options cannot be set
   */
  public void setOptions(String[] options) throws Exception {
    setDebug(Utils.getFlag("D", options));
    setClassGroup(WekaOptionUtils.parse(options, "class-group", getDefaultClassGroup()));
  }

  /**
   * Gets the setting
   *
   * @return the current setting
   */
  public String[] getOptions() {
    List<String> result = new ArrayList<>();
    WekaOptionUtils.add(result, "D", getDebug());
    WekaOptionUtils.add(result, "class-group", getClassGroup());
    return WekaOptionUtils.toArray(result);
  }

  /**
   * Sets whether to print some debug information.
   *
   * @param value	if true additional debug information will be printed.
   */
  public void setDebug(boolean value) {
    m_Debug = value;
  }

  /**
   * Gets whether additional debug information is printed.
   *
   * @return		true if additional debug information is printed
   */
  public boolean getDebug() {
    return m_Debug;
  }

  /**
   * the tip text for this property
   *
   * @return 		the tip text
   */
  public String debugTipText(){
    return "Whether to print additional debug information to the console.";
  }

  /**
   * Returns the default name of the group representing the class attribute.
   *
   * @return		the default
   */
  public String getDefaultClassGroup() {
    return "";
  }

  /**
   * Sets the name of the group representing the class attribute.
   *
   * @param value	the name
   */
  public void setClassGroup(String value) {
    m_ClassGroup = value;
  }

  /**
   * Returns the name of the group representing the class attribute.
   *
   * @return		the name
   */
  public String getClassGroup() {
    return m_ClassGroup;
  }

  /**
   * the tip text for this property
   *
   * @return 		the tip text
   */
  public String classGroupTipText(){
    return "The group representing the class attribute.";
  }

  /**
   * Get the file extension used for this type of file
   *
   * @return the file extension
   */
  @Override
  public String getFileExtension() {
    return getFileExtensions()[0];
  }

  /**
   * Gets all the file extensions used for this type of file
   *
   * @return the file extensions
   */
  @Override
  public String[] getFileExtensions() {
    return new String[]{"txt"};
  }

  /**
   * Get a one line description of the type of file
   *
   * @return a description of the file type
   */
  @Override
  public String getFileDescription() {
    return "CNTK Text file";
  }

  /**
   * Resets the loader ready to read a new data set
   */
  @Override
  public void reset() throws IOException {
    super.reset();

    m_structure = null;
    m_Data      = null;
  }

  /**
   * Resets the Loader object and sets the source of the data set to be
   * the supplied File object.
   *
   * @param file 		the source file.
   * @throws IOException 	if an error occurs
   */
  @Override
  public void setSource(File file) throws IOException {
    File original = file;
    m_structure = null;

    setRetrieval(NONE);

    if (file == null)
      throw new IOException("Source file object is null!");

    String fName = file.getPath();
    try {
      if (m_env == null) {
	m_env = Environment.getSystemWide();
      }
      fName = m_env.substitute(fName);
    } catch (Exception e) {
      // ignore any missing environment variables at this time
      // as it is possible that these may be set by the time
      // the actual file is processed
    }
    file = new File(fName);
    // set the source only if the file exists
    if (file.exists() && file.isFile())
      m_sourceFile = file;
    else
      throw new IOException("File '" + file + "' not found or not an actual file!");

    if (m_useRelativePath) {
      try {
	m_sourceFile = Utils.convertToRelativePath(original);
	m_File = m_sourceFile.getPath();
      } catch (Exception ex) {
	m_sourceFile = original;
	m_File       = m_sourceFile.getPath();
      }
    } else {
      m_sourceFile = original;
      m_File       = m_sourceFile.getPath();
    }
  }

  /**
   * Determines and returns (if possible) the structure (internally the
   * header) of the data set as an empty set of instances.
   *
   * @return 			the structure of the data set as an empty
   * 				set of Instances
   * @throws IOException 	if an error occurs
   */
  @Override
  public Instances getStructure() throws IOException {
    CNTKSpreadSheetReader	reader;
    SpreadSheet			sheet;
    ArrayList<Attribute>	atts;
    TIntList			classCols;
    int				i;
    Pattern 			classCol;
    List<String>		labels;
    Instance			inst;
    double[]			values;
    int				labelIndex;
    int				missing;

    if (m_structure == null) {
      // load file
      if (m_Debug)
	System.out.println("Loading data from '" + m_sourceFile + "'...");

      reader = new CNTKSpreadSheetReader();
      sheet  = reader.read(m_sourceFile.getAbsolutePath());
      if (sheet == null)
        throw new IOException("Failed to read: " + m_sourceFile);

      // determine indices of label columns
      classCols = new TIntArrayList();
      if (!m_ClassGroup.isEmpty()) {
        classCol = Pattern.compile(m_ClassGroup + "-[0-9]+");
        for (i = 0; i < sheet.getColumnCount(); i++) {
          if (classCol.matcher(sheet.getHeaderRow().getContent(i)).matches())
            classCols.add(i);
	}
	if (getDebug())
	  System.out.println("Class group '" + m_ClassGroup + "' consists of " + classCols.size() + " columns");
      }

      // dataset structure
      atts = new ArrayList<>();
      for (i = 0; i < sheet.getColumnCount(); i++) {
        if (classCols.contains(i))
          continue;
        atts.add(new Attribute(sheet.getColumnName(i)));
      }
      if (classCols.size() == 1) {
        atts.add(new Attribute(m_ClassGroup));
	if (getDebug())
	  System.out.println("Class attribute: " + atts.get(atts.size() - 1));
      }
      else if (classCols.size() > 1) {
        labels = new ArrayList<>();
        for (i = 0; i < classCols.size(); i++)
          labels.add("" + (i+1));
        atts.add(new Attribute(m_ClassGroup, labels));
	if (getDebug())
	  System.out.println("Class attribute: " + atts.get(atts.size() - 1));
      }
      m_Data = new Instances(m_sourceFile.getName(), atts, sheet.getRowCount());
      if (classCols.size() > 0)
        m_Data.setClassIndex(m_Data.numAttributes() - 1);

      // add data
      for (Row row: sheet.rows()) {
        values  = new double[m_Data.numAttributes()];
        missing = 0;
	for (i = 0; i < values.length; i++) {
	  values[i] = Utils.missingValue();
	  if (classCols.contains(i))
	    continue;
	  if (row.hasCell(i) && !row.getCell(i).isMissing())
	    values[i] = row.getCell(i).toDouble();
	  else
	    missing++;
	}
	// class attribute
	if (classCols.size() == 1) {
	  i = classCols.get(0);
	  if (row.hasCell(i) && !row.getCell(i).isMissing())
	    values[values.length - 1] = row.getCell(i).toDouble();
	}
	else if (classCols.size() > 1) {
	  labelIndex = 0;
	  for (i = 0; i < classCols.size(); i++) {
	    if (row.hasCell(i) && !row.getCell(i).isMissing() && (row.getCell(i).toDouble() > 0)) {
	      values[values.length - 1] = labelIndex;
	      break;
	    }
	    labelIndex++;
	  }
	}
	if (missing > 0)
	  inst = new SparseInstance(1.0, values);
	else
	  inst = new DenseInstance(1.0, values);
	m_Data.add(inst);
      }

      m_structure = new Instances(this.m_Data, 0);
    }

    return m_structure;
  }

  /**
   * Return the full data set. If the structure hasn't yet been determined
   * by a call to getStructure then method should do so before processing
   * the rest of the data set.
   *
   * @return the structure of the data set as an empty set of Instances
   * @throws IOException if there is no source or parsing fails
   */
  @Override
  public Instances getDataSet() throws IOException {
    // make sure that data has been read
    getStructure();
    return m_Data;
  }

  /**
   * CNTKLoader is unable to process a data set incrementally.
   *
   * @param structure ignored
   * @return never returns without throwing an exception
   * @throws IOException always. AdamsCsvLoader is unable to process a data
   * set incrementally.
   */
  @Override
  public Instance getNextInstance(Instances structure) throws IOException {
    throw new IOException("CNTKLoader can't read data sets incrementally.");
  }

  /**
   * Returns the revision string.
   *
   * @return		the revision
   */
  @Override
  public String getRevision() {
    return RevisionUtils.extract("$Revision$");
  }

  /**
   * Main method.
   *
   * @param args should contain the name of an input file.
   */
  public static void main(String[] args) {
    adams.env.Environment.setEnvironmentClass(adams.env.Environment.class);
    runFileLoader(new CNTKLoader(), args);
  }
}
