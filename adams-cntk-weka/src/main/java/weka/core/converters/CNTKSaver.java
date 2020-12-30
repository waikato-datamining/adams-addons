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
 * CNTKSaver.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package weka.core.converters;

import adams.core.Index;
import adams.core.Range;
import adams.core.base.BaseString;
import adams.core.io.FileUtils;
import adams.core.management.LocaleHelper;
import adams.core.option.OptionUtils;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.RevisionUtils;
import weka.core.Utils;
import weka.core.WekaOptionUtils;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NominalToBinary;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

/**
 <!-- globalinfo-start -->
 * Writes the Instances to a CNTK text file.<br>
 * Automatically turns a nominal class attribute into CNTK's '1-hot encoding'.
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
 * <pre> -row-id &lt;value&gt;
 *  The (optional) attribute to use for the row ID.
 *  (default: )</pre>
 *
 * <pre> -inputs &lt;value&gt;
 *  The attribute ranges determining the inputs (eg for 'features' and 'class').
 *  (default: )</pre>
 *
 * <pre> -input-names &lt;value&gt;
 *  The names of the inputs (eg 'features' and 'class').
 *  (default: )</pre>
 *
 * <pre> -use-sparse-format &lt;value&gt;
 *  If enabled, sparse format is used instead (ie 'index:value').
 *  (default: no)</pre>
 *
 * <pre> -suppress-missing &lt;value&gt;
 *  If enabled, groups that contain at least one missing value get suppressed completely.
 *  (default: no)</pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class CNTKSaver
  extends AbstractFileSaver
  implements BatchConverter {

  private static final long serialVersionUID = 4351243795790752863L;

  /** whether to print some debug information */
  protected boolean m_Debug = false;

  /** the optional row id attribute. */
  protected Index m_RowID;

  /** the inputs. */
  protected Range[] m_Inputs;

  /** the names of the inputs. */
  protected BaseString[] m_InputNames;

  /** whether to output sparse format. */
  protected boolean m_UseSparseFormat;

  /** whether to suppress groups with missing values. */
  protected boolean m_SuppressMissing;
  
  /** the file to write to. */
  protected File m_OutputFile;

  /** the locale to use. */
  protected Locale m_Locale;

  /**
   * Constructor
   */
  public CNTKSaver(){
    resetOptions();
  }
  
  /**
   * Returns a string describing this Saver
   * 
   * @return 		a description of the Saver suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String globalInfo() {
    return
      "Writes the Instances to a CNTK text file.\n"
      + "Automatically turns a nominal class attribute into CNTK's '1-hot encoding'.";
  }

  @Override
  public void resetOptions() {
    super.resetOptions();

    m_RowID           = getDefaultRowID();
    m_Inputs          = getDefaultInputs();
    m_InputNames      = getDefaultInputNames();
    m_UseSparseFormat = false;
    m_SuppressMissing = false;
    m_OutputFile      = null;
    m_Locale          = LocaleHelper.getSingleton().getDefault();
  }

  /**
   * Returns an enumeration describing the available options.
   *
   * @return an enumeration of all the available options.
   */
  @Override
  public Enumeration listOptions() {
    Vector result = new Vector();

    WekaOptionUtils.addOption(result, debugTipText(), "off", "D");
    WekaOptionUtils.addOption(result, rowIDTipText(), getDefaultRowID(), "row-id");
    WekaOptionUtils.addOption(result, inputsTipText(), Utils.arrayToString(getDefaultInputs()), "inputs");
    WekaOptionUtils.addOption(result, inputNamesTipText(), Utils.arrayToString(getDefaultInputNames()), "input-names");
    WekaOptionUtils.addOption(result, useSparseFormatTipText(), "no", "use-sparse-format");
    WekaOptionUtils.addOption(result, suppressMissingTipText(), "no", "suppress-missing");

    return WekaOptionUtils.toEnumeration(result);
  }

  /**
   * Parses the options for this object.
   *
   * @param options	the options to use
   * @throws Exception	if setting of options fails
   */
  @Override
  public void setOptions(String[] options) throws Exception {
    setDebug(Utils.getFlag("D", options));
    setRowID(WekaOptionUtils.parse(options, "row-id", getDefaultRowID()));
    setInputs(WekaOptionUtils.parse(options, "inputs", getDefaultInputs()));
    setInputNames((BaseString[]) WekaOptionUtils.parse(options, "input-names", getDefaultInputNames()));
    setUseSparseFormat(Utils.getFlag("use-sparse-format", options));
    setSuppressMissing(Utils.getFlag("suppress-missing", options));
    super.setOptions(options);
  }

  /**
   * returns the options of the current setup
   *
   * @return		the current options
   */
  @Override
  public String[] getOptions(){
    List<String> result = new ArrayList<>();

    WekaOptionUtils.add(result, "D", getDebug());
    if (!getRowID().isEmpty())
      WekaOptionUtils.add(result, "row-id", getRowID());
    WekaOptionUtils.add(result, "inputs", getInputs());
    WekaOptionUtils.add(result, "input-names", getInputNames());
    WekaOptionUtils.add(result, "use-sparse-format", getUseSparseFormat());
    WekaOptionUtils.add(result, "suppress-missing", getSuppressMissing());
    result.addAll(Arrays.asList(super.getOptions()));

    return result.toArray(new String[result.size()]);
  }

  /**
   * Returns a description of the file type.
   *
   * @return a short file description
   */
  @Override
  public String getFileDescription() {
    return "CNTK Text file";
  }

  /**
   * Gets all the file extensions used for this type of file
   *
   * @return the file extensions
   */
  @Override
  public String[] getFileExtensions() {
    return new String[]{".txt"};
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
   * Returns the default attribute index to use as row ID.
   *
   * @return		the default
   */
  protected Index getDefaultRowID() {
    return new Index();
  }

  /**
   * Sets the (optional) attribute to use as row ID.
   *
   * @param value 	the index
   */
  public void setRowID(Index value) {
    m_RowID = value;
  }

  /**
   * Returns the (optional) attribute to use as row ID.
   *
   * @return 		the index
   */
  public Index getRowID() {
    return m_RowID;
  }

  /**
   * Returns the tip text for this property
   * 
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String rowIDTipText() {
    return "The (optional) attribute to use for the row ID.";
  }

  /**
   * Returns the default input ranges.
   *
   * @return		the default
   */
  protected Range[] getDefaultInputs() {
    return new Range[0];
  }

  /**
   * Sets the attribute ranges that make up the inputs (eg for 'features' and 'class').
   *
   * @param value	the attribute
   */
  public void setInputs(Range[] value) {
    m_Inputs     = value;
    m_InputNames = (BaseString[]) adams.core.Utils.adjustArray(m_InputNames, m_Inputs.length, new BaseString());
  }

  /**
   * Returns the attribute ranges that make up the inputs (eg for 'features' and 'class').
   *
   * @return 		the ranges
   */
  public Range[] getInputs() {
    return m_Inputs;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String inputsTipText() {
    return "The attribute ranges determining the inputs (eg for 'features' and 'class').";
  }

  /**
   * Returns the default input names.
   *
   * @return		the default
   */
  protected BaseString[] getDefaultInputNames() {
    return new BaseString[0];
  }

  /**
   * Sets the names for the inputs.
   *
   * @param value	the names
   */
  public void setInputNames(BaseString[] value) {
    m_InputNames = value;
    m_Inputs     = (Range[]) adams.core.Utils.adjustArray(m_Inputs, m_InputNames.length, new Range());
  }

  /**
   * Returns the names for the inputs.
   *
   * @return 		the names
   */
  public BaseString[] getInputNames() {
    return m_InputNames;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String inputNamesTipText() {
    return "The names of the inputs (eg 'features' and 'class').";
  }

  /**
   * Sets whether to use sparse format.
   *
   * @param value	true if to use sparse format
   */
  public void setUseSparseFormat(boolean value) {
    m_UseSparseFormat = value;
  }

  /**
   * Returns whether to use sparse format.
   *
   * @return 		true if to use sparse format
   */
  public boolean getUseSparseFormat() {
    return m_UseSparseFormat;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useSparseFormatTipText() {
    return "If enabled, sparse format is used instead (ie 'index:value').";
  }

  /**
   * Sets whether to suppress groups with missing values.
   *
   * @param value	true if to suppress missing
   */
  public void setSuppressMissing(boolean value) {
    m_SuppressMissing = value;
  }

  /**
   * Returns whether to suppress groups with missing values.
   *
   * @return 		true if to suppress missing
   */
  public boolean getSuppressMissing() {
    return m_SuppressMissing;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String suppressMissingTipText() {
    return "If enabled, groups that contain at least one missing value get suppressed completely.";
  }

  /** 
   * Returns the Capabilities of this saver.
   *
   * @return            the capabilities of this object
   * @see               Capabilities
   */
  @Override
  public Capabilities getCapabilities() {
    Capabilities result = super.getCapabilities();

    result.disableAll();

    // attributes
    result.enable(Capability.NUMERIC_ATTRIBUTES);
    
    // class
    result.enable(Capability.NOMINAL_CLASS);
    result.enable(Capability.NUMERIC_CLASS);
    result.enable(Capability.NO_CLASS);
    
    return result;
  }

  /**
   * Sets the destination file (and directories if necessary).
   * 
   * @param file the File
   * @exception IOException always
   */
  @Override
  public void setDestination(File file) throws IOException {
    m_OutputFile = file;
  }
  
  /**
   * Default implementation throws an IOException.
   *
   * @param output the OutputStream
   * @exception IOException always
   */
  @Override
  public void setDestination(OutputStream output) throws IOException {
    throw new IOException("Writing to an outputstream not supported");
  }

  /**
   * Formats the number according to the format and returns the generated
   * textual representation.
   *
   * @param value	the double value to turn into a string
   * @return		the generated string
   */
  protected synchronized String format(double value) {
    String	result;

    if (Double.isNaN(value)) {
      return adams.core.Utils.NAN;
    }
    else if (Double.isInfinite(value)) {
      if (value < 0)
        return adams.core.Utils.NEGATIVE_INFINITY;
      else
        return adams.core.Utils.POSITIVE_INFINITY;
    }

    result = adams.core.Utils.doubleToString(value, 12, m_Locale);

    return result;
  }

  /**
   * Writes a Batch of instances
   * 
   * @throws IOException 	throws IOException if saving in batch mode 
   * 				is not possible
   */
  @Override
  public void writeBatch() throws IOException {
    int			rowID;
    int[][]		inputs;
    int			i;
    int			n;
    int			m;
    int			r;
    String[]		names;
    Double		value;
    TIntList 		canOutput;
    boolean		missing;
    Instances		data;
    FileWriter		fwriter;
    BufferedWriter	writer;
    int			classIndex;
    int			numLabels;
    NominalToBinary	nom2bin;
    TIntList		affected;
    TIntList		fixed;

    if (getInstances() == null)
      throw new IOException("No instances to save!");
    data = getInstances();

    if (m_OutputFile == null)
      throw new IOException("No output file set!");
    
    if (getRetrieval() == INCREMENTAL)
      throw new IOException("Batch and incremental saving cannot be mixed.");
    
    setRetrieval(BATCH);
    setWriteMode(WRITE);

    if (m_Inputs.length == 0)
      throw new IllegalStateException("No input ranges defined!");

    // ensure same length
    m_InputNames = (BaseString[]) adams.core.Utils.adjustArray(m_InputNames, m_Inputs.length, new BaseString());
    names = new String[m_InputNames.length];
    for (i = 0; i < m_InputNames.length; i++) {
      names[i] = m_InputNames[i].getValue();
      if (names[i].isEmpty())
	names[i] = "input-" + (i+1);
    }

    // determine columns
    m_RowID.setMax(data.numAttributes());
    rowID = m_RowID.getIntIndex();
    if (getDebug())
      System.out.println("row ID col (0-based, ignored if -1): " + rowID);
    inputs = new int[m_Inputs.length][];
    if (getDebug())
      System.out.println("# of inputs: " + m_Inputs.length);
    for (i = 0; i < m_Inputs.length; i++) {
      m_Inputs[i].setMax(data.numAttributes());
      inputs[i] = m_Inputs[i].getIntIndices();
      if (getDebug())
	System.out.println("input " + (i+1) + " (0-based): " + adams.core.Utils.arrayToString(inputs[i]));
    }

    // nominal class? transformed data and fix indices
    classIndex = data.classIndex();
    if ((classIndex > -1) && (data.classAttribute().isNominal())) {
      // transform data
      numLabels   = data.classAttribute().numValues();
      data        = new Instances(data);
      data.setClassIndex(-1);
      nom2bin     = new NominalToBinary();
      nom2bin.setAttributeIndices("" + (classIndex + 1));
      nom2bin.setTransformAllValues(true);
      try {
	nom2bin.setInputFormat(data);
	data = Filter.useFilter(data, nom2bin);
      }
      catch (Exception e) {
        throw new IOException("Failed to binarize class attribute, using: " + OptionUtils.getCommandLine(nom2bin), e);
      }

      // fix indices
      affected = new TIntArrayList();
      for (i = 0; i < inputs.length; i++) {
        for (n = 0; n < inputs[i].length; n++) {
	  if (inputs[i][n] > classIndex) {
	    inputs[i][n] += (numLabels - 1);
	  }
	  else if (inputs[i][n] == classIndex) {
	    // flag affected arrays
	    if (!affected.contains(inputs[i][n]))
	      affected.add(i);
	  }
	}
      }
      if (getDebug())
        System.out.println("Arrays affected by binarization: " + affected);

      // insert additional indices in affected arrays
      for (i = 0; i < affected.size(); i++) {
        fixed = new TIntArrayList();
        for (n = 0; n < inputs[affected.get(i)].length; n++) {
          fixed.add(inputs[affected.get(i)][n]);
          if (inputs[affected.get(i)][n] == classIndex) {
            for (r = 1; r < numLabels; r++)
              fixed.add(classIndex + r);
	  }
	}
	if (getDebug())
	  System.out.println("Affected array #" + affected.get(i) + " (old): " + Utils.arrayToString(inputs[affected.get(i)]));
	inputs[affected.get(i)] = fixed.toArray();
	if (getDebug())
	  System.out.println("Affected array #" + affected.get(i) + " (fixed): " + Utils.arrayToString(inputs[affected.get(i)]));
      }
    }

    canOutput = new TIntArrayList();
    if (!m_SuppressMissing) {
      for (i = 0; i < m_Inputs.length; i++)
        canOutput.add(i);
    }

    // write data
    fwriter = new FileWriter(m_OutputFile);
    writer  = new BufferedWriter(fwriter);
    r       = 0;
    for (Instance row : data) {
      r++;
      // check which groups can be output when suppressing groups with missing values
      if (m_SuppressMissing) {
        canOutput.clear();
	for (i = 0; i < inputs.length; i++) {
	  missing = false;
	  for (n = 0; n < inputs[i].length; n++) {
	    if (row.isMissing(inputs[i][n])) {
	      missing = true;
	      break;
	    }
	  }
	  if (!missing)
	    canOutput.add(i);
	}
      }
      // skip whole row?
      if (m_SuppressMissing && getDebug())
        System.out.println("Row #" + r + " / inputs to output: " + canOutput);
      if (canOutput.size() == 0)
        continue;

      try {
	// ID
	if (rowID > -1) {
	  if (!row.isMissing(rowID)) {
	    writer.write(format(row.value(rowID)));
	    writer.write(" ");
	  }
	}

	// inputs
	for (m = 0; m < canOutput.size(); m++) {
	  i = canOutput.get(m);

	  // separator
	  writer.write("|");

	  // name
	  writer.write(names[i]);
	  writer.write(" ");

	  // values
	  for (n = 0; n < inputs[i].length; n++) {
	    if (row.isMissing(inputs[i][n]))
	      value = null;
	    else
	      value = row.value(inputs[i][n]);

	    if (m_UseSparseFormat) {
	      if ((value != null) && (value == 0))
		continue;
	      writer.write("" + inputs[i][n]);
	      writer.write(":");
	    }
	    if (value == null)
	      writer.write("?");
	    else
	      writer.write(format(value));
	    writer.write(" ");
	  }
	}

	writer.write("\n");
	writer.flush();
      }
      catch (Exception e) {
	System.err.println("Failed to write data: " + row);
	e.printStackTrace();
      }
    }

    FileUtils.closeQuietly(writer);
    FileUtils.closeQuietly(fwriter);
  }
  
  /**
   * Returns the revision string.
   * 
   * @return		the revision
   */
  public String getRevision() {
    return RevisionUtils.extract("$Revision$");
  }
  
  /**
   * Main method.
   *
   * @param args 	should contain the options of a Saver.
   */
  public static void main(String[] args) {
    adams.env.Environment.setEnvironmentClass(adams.env.Environment.class);
    runFileSaver(new CNTKSaver(), args);
  }
}
