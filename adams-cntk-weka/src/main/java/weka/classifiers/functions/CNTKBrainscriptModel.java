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
 * CNTKBrainscriptModel.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package weka.classifiers.functions;

import adams.core.Range;
import adams.core.Utils;
import adams.core.Variables;
import adams.core.base.BaseKeyValuePair;
import adams.core.base.BaseRegExp;
import adams.core.base.BaseString;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderDirectory;
import adams.core.io.PlaceholderFile;
import adams.core.io.TempUtils;
import adams.core.io.lister.LocalDirectoryLister;
import adams.core.logging.LoggingLevel;
import adams.flow.core.RunnableWithLogging;
import adams.ml.cntk.CNTK;
import adams.ml.cntk.CNTKPredictionWrapper;
import adams.ml.cntk.DeviceType;
import adams.ml.cntk.predictionpostprocessor.Normalize;
import com.github.fracpete.processoutput4j.core.StreamingProcessOutputType;
import com.github.fracpete.processoutput4j.core.StreamingProcessOwner;
import com.github.fracpete.processoutput4j.output.StreamingProcessOutput;
import gnu.trove.list.TFloatList;
import gnu.trove.list.array.TFloatArrayList;
import weka.classifiers.AbstractClassifier;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.WekaOptionUtils;
import weka.core.converters.CNTKSaver;
import weka.core.converters.ConverterUtils.DataSink;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

/**
 <!-- globalinfo-start -->
 * Builds a CNTK model using the supplied Brainscript and then applies the model to the data for making predictions.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p>
 *
 * <pre> -script &lt;value&gt;
 *  The BrainScript to run.
 *  (default: ${CWD})</pre>
 *
 * <pre> -train-file &lt;value&gt;
 *  The training file used by the Brainscript; the training instances will get saved to that file.
 *  (default: ${CWD})</pre>
 *
 * <pre> -model-directory &lt;value&gt;
 *  The directory containing the models, temp models and checkpoint files.
 *  (default: ${CWD})</pre>
 *
 * <pre> -model-extensions &lt;value&gt;
 *  The file extension used by the models (incl dot).
 *  (default: .cmf)</pre>
 *
 * <pre> -model &lt;value&gt;
 *  The prebuilt CNTK model to use.
 *  (default: ${CWD})</pre>
 *
 * <pre> -device-type &lt;value&gt;
 *  The device type to use.
 *  (default: DEFAULT)</pre>
 *
 * <pre> -gpu-device-id &lt;value&gt;
 *  The GPU device ID.
 *  (default: 0)</pre>
 *
 * <pre> -inputs &lt;value&gt;
 *  The column ranges determining the inputs (eg for 'features' and 'class').
 *  (default: )</pre>
 *
 * <pre> -input-names &lt;value&gt;
 *  The names of the inputs (eg 'features' and 'class').
 *  (default: )</pre>
 *
 * <pre> -class-name &lt;value&gt;
 *  The name of the class attribute in the model, in case it cannot be determined automatically.
 *  (default: )</pre>
 *
 * <pre> -output-name &lt;value&gt;
 *  The name of the output variable in the model, in case it cannot be determined automatically based on its dimension.
 *  (default: )</pre>
 *
 * <pre> -variables &lt;value&gt;
 *  The key-value pairs representing variables and their associated values to be replaced in the script.
 *  (default: )</pre>
 *
 * <pre> -output-debug-info
 *  If set, classifier is run in debug mode and
 *  may output additional info to the console</pre>
 *
 * <pre> -do-not-check-capabilities
 *  If set, classifier capabilities are not checked before classifier is built
 *  (use with caution).</pre>
 *
 * <pre> -num-decimal-places
 *  The number of decimal places for the output of numbers in the model (default 2).</pre>
 *
 * <pre> -batch-size
 *  The desired batch size for batch prediction  (default 100).</pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class CNTKBrainscriptModel
  extends AbstractClassifier
  implements StreamingProcessOwner {

  private static final long serialVersionUID = 7732345053235983381L;

  protected static String SCRIPT = "script";

  protected static String TRAINFILE = "train-file";

  protected static String MODELDIR = "model-directory";

  protected static String MODELEXT = "model-extensions";

  protected static String MODEL = "model";

  protected static String DEVICETYPE = "device-type";

  protected static String GPUDEVICEID = "gpu-device-id";

  protected static String INPUTS = "inputs";

  protected static String INPUTNAMES = "input-names";

  protected static String CLASSNAME = "class-name";

  protected static String OUTPUTNAME = "output-name";

  protected static String VARIABLES = "variables";

  /** the brainscript to execute. */
  protected PlaceholderFile m_Script = getDefaultScript();

  /** the tmp brainscript to execute (if necessary). */
  protected PlaceholderFile m_TmpScript;

  /** the CNTK training file used by the Brainscript. */
  protected PlaceholderFile m_TrainFile = getDefaultTrainFile();

  /** the directory containing the models. */
  protected PlaceholderDirectory m_ModelDirectory = getDefaultModelDirectory();

  /** the model to load. */
  protected PlaceholderFile m_Model = getDefaultModel();

  /** the extension used by the models. */
  protected String m_ModelExtension = getDefaultModelExtension();

  /** the device to use. */
  protected DeviceType m_DeviceType = getDefaultDeviceType();

  /** the GPU device ID. */
  protected long m_GPUDeviceID = getDefaultGPUDeviceID();

  /** the variables and their values to expand in the script. */
  protected BaseKeyValuePair[] m_Variables = getDefaultVariables();

  /** the model wrapper. */
  protected CNTKPredictionWrapper m_Wrapper = new CNTKPredictionWrapper();

  /** for normalizing the predictions. */
  protected Normalize m_Normalize = new Normalize();

  /** for identifying the temp and checkpoint files. */
  protected LocalDirectoryLister m_Lister;

  /** the process monitor. */
  protected StreamingProcessOutput m_ProcessOutput;

  /** the runnable executing the command. */
  protected RunnableWithLogging m_Monitor;

  /** in case an exception occurred executing the command (gets rethrown). */
  protected IllegalStateException m_ExecutionFailure;

  /**
   * Returns a string describing classifier.
   *
   * @return 		a description suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String globalInfo() {
    return
      "Builds a CNTK model using the supplied Brainscript and then applies "
	+ "the model to the data for making predictions.";
  }

  /**
   * Returns an enumeration describing the available options.
   *
   * @return 		an enumeration of all the available options.
   */
  public Enumeration listOptions() {
    Vector result;

    result = new Vector();

    WekaOptionUtils.addOption(result, scriptTipText(), "" + getDefaultScript(), SCRIPT);
    WekaOptionUtils.addOption(result, trainFileTipText(), "" + getDefaultTrainFile(), TRAINFILE);
    WekaOptionUtils.addOption(result, modelDirectoryTipText(), "" + getDefaultModelDirectory(), MODELDIR);
    WekaOptionUtils.addOption(result, modelExtensionTipText(), "" + getDefaultModelExtension(), MODELEXT);
    WekaOptionUtils.addOption(result, modelTipText(), "" + getDefaultModel(), MODEL);
    WekaOptionUtils.addOption(result, deviceTypeTipText(), "" + getDefaultDeviceType(), DEVICETYPE);
    WekaOptionUtils.addOption(result, GPUDeviceIDTipText(), "" + getDefaultGPUDeviceID(), GPUDEVICEID);
    WekaOptionUtils.addOption(result, inputsTipText(), Utils.arrayToString(getDefaultInputs()), INPUTS);
    WekaOptionUtils.addOption(result, inputNamesTipText(), Utils.arrayToString(getDefaultInputNames()), INPUTNAMES);
    WekaOptionUtils.addOption(result, classNameTipText(), getDefaultClassName(), CLASSNAME);
    WekaOptionUtils.addOption(result, outputNameTipText(), getDefaultOutputName(), OUTPUTNAME);
    WekaOptionUtils.addOption(result, variablesTipText(), Utils.arrayToString(getDefaultVariables()), VARIABLES);
    WekaOptionUtils.add(result, super.listOptions());
    return WekaOptionUtils.toEnumeration(result);
  }

  /**
   * Parses a given list of options.
   *
   * @param options 	the list of options as an array of strings
   * @throws Exception 	if an option is not supported
   */
  public void setOptions(String[] options) throws Exception {
    setScript(WekaOptionUtils.parse(options, SCRIPT, getDefaultScript()));
    setTrainFile(WekaOptionUtils.parse(options, TRAINFILE, getDefaultTrainFile()));
    setModelDirectory(WekaOptionUtils.parse(options, MODELDIR, getDefaultModelDirectory()));
    setModelExtension(WekaOptionUtils.parse(options, MODELEXT, getDefaultModelExtension()));
    setModel(WekaOptionUtils.parse(options, MODEL, getDefaultModel()));
    setDeviceType((DeviceType) WekaOptionUtils.parse(options, DEVICETYPE, getDefaultDeviceType()));
    setGPUDeviceID(WekaOptionUtils.parse(options, GPUDEVICEID, getDefaultGPUDeviceID()));
    setInputs(WekaOptionUtils.parse(options, INPUTS, getDefaultInputs()));
    setInputNames((BaseString[]) WekaOptionUtils.parse(options, INPUTNAMES, getDefaultInputNames()));
    setClassName(WekaOptionUtils.parse(options, CLASSNAME, getDefaultClassName()));
    setOutputName(WekaOptionUtils.parse(options, OUTPUTNAME, getDefaultOutputName()));
    setVariables((BaseKeyValuePair[]) WekaOptionUtils.parse(options, VARIABLES, getDefaultVariables()));
    super.setOptions(options);
  }

  /**
   * Gets the current settings of the classifier.
   *
   * @return 		an array of strings suitable for passing to setOptions
   */
  public String [] getOptions() {
    List<String> result = new ArrayList<>();
    WekaOptionUtils.add(result, SCRIPT, getScript());
    WekaOptionUtils.add(result, TRAINFILE, getTrainFile());
    WekaOptionUtils.add(result, MODELDIR, getModelDirectory());
    WekaOptionUtils.add(result, MODELEXT, getModelExtension());
    WekaOptionUtils.add(result, MODEL, getModel());
    WekaOptionUtils.add(result, DEVICETYPE, getDeviceType());
    if (getDeviceType() == DeviceType.GPU)
      WekaOptionUtils.add(result, GPUDEVICEID, getGPUDeviceID());
    WekaOptionUtils.add(result, INPUTS, getInputs());
    WekaOptionUtils.add(result, INPUTNAMES, getInputNames());
    WekaOptionUtils.add(result, CLASSNAME, getClassName());
    WekaOptionUtils.add(result, OUTPUTNAME, getOutputName());
    WekaOptionUtils.add(result, VARIABLES, getVariables());
    WekaOptionUtils.add(result, super.getOptions());
    return WekaOptionUtils.toArray(result);
  }

  /**
   * Set debugging mode.
   *
   * @param debug true if debug output should be printed
   */
  @Override
  public void setDebug(boolean debug) {
    super.setDebug(debug);
    m_Wrapper.setLoggingLevel(debug ? LoggingLevel.INFO : LoggingLevel.WARNING);
  }

  /**
   * Returns the default script.
   *
   * @return		the default
   */
  protected PlaceholderFile getDefaultScript() {
    return new PlaceholderFile();
  }

  /**
   * Sets the script to run.
   *
   * @param value	the script
   */
  public void setScript(PlaceholderFile value) {
    m_Script = value;
  }

  /**
   * Returns the script to run.
   *
   * @return 		the script
   */
  public PlaceholderFile getScript() {
    return m_Script;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String scriptTipText() {
    return "The BrainScript to run.";
  }

  /**
   * Returns the default training file used by the Brainscript.
   * The training instances will get saved to that file.
   *
   * @return		the default
   */
  protected PlaceholderFile getDefaultTrainFile() {
    return new PlaceholderFile();
  }

  /**
   * Sets the training file used by the Brainscript.
   * The training instances will get saved to that file.
   *
   * @param value	the file
   */
  public void setTrainFile(PlaceholderFile value) {
    m_TrainFile = value;
  }

  /**
   * Returns the training file used by the Brainscript.
   *
   * @return 		the file
   */
  public PlaceholderFile getTrainFile() {
    return m_TrainFile;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String trainFileTipText() {
    return "The training file used by the Brainscript; the training instances will get saved to that file.";
  }

  /**
   * Returns the default directory containing the models.
   *
   * @return		the default
   */
  protected PlaceholderDirectory getDefaultModelDirectory() {
    return new PlaceholderDirectory();
  }

  /**
   * Sets the directory containing the models, temp models and checkpoint files.
   *
   * @param value	the directory
   */
  public void setModelDirectory(PlaceholderDirectory value) {
    m_ModelDirectory = value;
  }

  /**
   * Returns the directory containing the models, temp models and checkpoint files.
   *
   * @return 		the directory
   */
  public PlaceholderDirectory getModelDirectory() {
    return m_ModelDirectory;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String modelDirectoryTipText() {
    return "The directory containing the models, temp models and checkpoint files.";
  }

  /**
   * Returns the default model file.
   *
   * @return		the default
   */
  protected PlaceholderFile getDefaultModel() {
    return new PlaceholderFile();
  }

  /**
   * Sets the prebuilt model to use.
   *
   * @param value	the model
   */
  public void setModel(PlaceholderFile value) {
    m_Model = value;
  }

  /**
   * Returns the prebuilt model to use.
   *
   * @return		the model
   */
  public PlaceholderFile getModel() {
    return m_Model;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String modelTipText() {
    return "The prebuilt CNTK model to use.";
  }

  /**
   * Returns the default model extension.
   *
   * @return		the default
   */
  protected String getDefaultModelExtension() {
    return ".cmf";
  }

  /**
   * Sets the extension that the models use (incl dot).
   *
   * @param value	the extension
   */
  public void setModelExtension(String value) {
    m_ModelExtension = value;
  }

  /**
   * Returns the extension that the models use (incl dot).
   *
   * @return 		the extension
   */
  public String getModelExtension() {
    return m_ModelExtension;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String modelExtensionTipText() {
    return "The file extension used by the models (incl dot).";
  }

  /**
   * Returns the default device to use.
   *
   * @return  		the device
   */
  protected DeviceType getDefaultDeviceType() {
    return DeviceType.DEFAULT;
  }

  /**
   * Sets the device to use.
   *
   * @param value	the device
   */
  public void setDeviceType(DeviceType value) {
    m_DeviceType = value;
  }

  /**
   * Returns the device to use.
   *
   * @return  		the device
   */
  public DeviceType getDeviceType() {
    return m_DeviceType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String deviceTypeTipText() {
    return "The device type to use.";
  }

  /**
   * Returns the default GPU device ID.
   *
   * @return  		the ID
   */
  protected long getDefaultGPUDeviceID() {
    return 0;
  }

  /**
   * Sets the GPU device ID.
   *
   * @param value	the ID
   */
  public void setGPUDeviceID(long value) {
    m_GPUDeviceID = value;
  }

  /**
   * Returns the GPU device ID.
   *
   * @return  		the ID
   */
  public long getGPUDeviceID() {
    return m_GPUDeviceID;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String GPUDeviceIDTipText() {
    return "The GPU device ID.";
  }

  /**
   * Returns the default inputs.
   *
   * @return		the default
   */
  protected Range[] getDefaultInputs() {
    return new Range[0];
  }

  /**
   * Sets the column ranges that make up the inputs (eg for 'features' and 'class').
   *
   * @param value	the column
   */
  public void setInputs(Range[] value) {
    m_Wrapper.setInputs(value);
  }

  /**
   * Returns the column ranges that make up the inputs (eg for 'features' and 'class').
   *
   * @return 		the ranges
   */
  public Range[] getInputs() {
    return m_Wrapper.getInputs();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String inputsTipText() {
    return m_Wrapper.inputsTipText();
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
    m_Wrapper.setInputNames(value);
  }

  /**
   * Returns the names for the inputs.
   *
   * @return 		the names
   */
  public BaseString[] getInputNames() {
    return m_Wrapper.getInputNames();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String inputNamesTipText() {
    return m_Wrapper.inputNamesTipText();
  }

  /**
   * Returns the default name of the class attribute in the model.
   *
   * @return		the default
   */
  protected String getDefaultClassName() {
    return "";
  }

  /**
   * Sets the name of the class attribute in the model, in case it cannot
   * be determined automatically.
   *
   * @param value	the name
   */
  public void setClassName(String value) {
    m_Wrapper.setClassName(value);
  }

  /**
   * Returns the name of the class attribute in the model, in case it cannot
   * be determined automatically.
   *
   * @return		the name
   */
  public String getClassName() {
    return m_Wrapper.getClassName();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String classNameTipText() {
    return m_Wrapper.classNameTipText();
  }

  /**
   * Returns the default name of the class attribute in the model.
   *
   * @return		the default
   */
  protected String getDefaultOutputName() {
    return "";
  }

  /**
   * Sets the name of the output variable in the model, in case it cannot be
   * determined automatically based on its dimension.
   *
   * @param value	the name
   */
  public void setOutputName(String value) {
    m_Wrapper.setOutputName(value);
  }

  /**
   * Returns the name of the output variable in the model, in case it cannot
   * be determined automatically based on its dimension.
   *
   * @return		the name
   */
  public String getOutputName() {
    return m_Wrapper.getOutputName();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String outputNameTipText() {
    return m_Wrapper.outputNameTipText();
  }

  /**
   * Returns the default variables to use.
   *
   * @return		the default
   */
  protected BaseKeyValuePair[] getDefaultVariables() {
    return new BaseKeyValuePair[0];
  }

  /**
   * Sets the key-value pairs representing variables and their associated
   * values to be replaced in the script.
   *
   * @param value	the variables
   */
  public void setVariables(BaseKeyValuePair[] value) {
    m_Variables = value;
  }

  /**
   * Returns the key-value pairs representing variables and their associated
   * values to be replaced in the script.
   *
   * @return		the variables
   */
  public BaseKeyValuePair[] getVariables() {
    return m_Variables;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String variablesTipText() {
    return "The key-value pairs representing variables and their associated values to be replaced in the script.";
  }

  /**
   * Returns the Capabilities of this classifier. Maximally permissive
   * capabilities are allowed by default. Derived classifiers should override
   * this method and first disable all capabilities and then enable just those
   * capabilities that make sense for the scheme.
   *
   * @return the capabilities of this object
   * @see Capabilities
   */
  @Override
  public Capabilities getCapabilities() {
    Capabilities 	result;

    result = super.getCapabilities();

    result.disableAll();
    result.enable(Capability.NUMERIC_ATTRIBUTES);

    result.disableAllClasses();
    result.enable(Capability.NOMINAL_CLASS);
    result.enable(Capability.NUMERIC_CLASS);
    result.enable(Capability.MISSING_CLASS_VALUES);

    return result;
  }

  /**
   * Returns what output from the process to forward.
   *
   * @return 		the output type
   */
  public StreamingProcessOutputType getOutputType() {
    return StreamingProcessOutputType.BOTH;
  }

  /**
   * Processes the incoming line.
   *
   * @param line	the line to process
   * @param stdout	whether stdout or stderr
   */
  public void processOutput(String line, boolean stdout) {
    String[]	files;

    if (stdout)
      System.out.println(line);
    else
      System.err.println(line);

    // remove temp/checkpoint files
    if (m_Lister != null) {
      files = m_Lister.list();
      for (String file : files)
	FileUtils.delete(file);
    }
  }

  /**
   * Preprocesses the script if necessary.
   *
   * @return		the script filename
   */
  protected String preprocessScript() {
    String		result;
    List<String>	lines;
    String		content;
    String		msg;
    Variables		vars;

    result      = m_Script.getAbsolutePath();
    m_TmpScript = null;

    if (m_Variables.length > 0) {
      vars = new Variables();
      for (BaseKeyValuePair var: m_Variables)
        vars.set(var.getPairKey(), var.getPairValue());
      lines = FileUtils.loadFromFile(m_Script);
      if (lines != null) {
        result  = TempUtils.createTempFile("adams-cntk-weka-bs-", ".bs").getAbsolutePath();
	content = Utils.flatten(lines, "\n");
	content = vars.expand(content);
	msg     = FileUtils.writeToFileMsg(result, content, false, null);
	if (msg != null)
	  throw new IllegalStateException("Failed to write expanded script!\n" + msg);
	m_TmpScript = new PlaceholderFile(result);
      }
    }

    return result;
  }

  /**
   * Builds the model.
   *
   * @param data	the training data
   * @throws Exception	if building fails
   */
  protected void buildModel(Instances data) throws Exception {
    String		cmd;
    final String	fCmd;
    CNTKSaver		saver;
    String		script;

    getCapabilities().testWithFail(data);

    // save data as CNTK text file
    if (getDebug())
      System.out.println("Saving data to: " + m_TrainFile);
    saver = new CNTKSaver();
    saver.setSuppressMissing(true);
    saver.setUseSparseFormat(false);
    saver.setInputs(getInputs());
    saver.setInputNames(getInputNames());
    saver.setDestination(m_TrainFile.getAbsoluteFile());
    DataSink.write(saver, data);
    if (getDebug())
      System.out.println("Saved data to: " + m_TrainFile);

    // preprocess script
    script = preprocessScript();

    // preprocess command
    cmd = CNTK.getBinary().getAbsolutePath();
    cmd += " configFile=" + script;
    fCmd = cmd;
    if (getDebug())
      System.out.println("Command: " + cmd);

    // for deleting tmp models
    m_Lister = new LocalDirectoryLister();
    m_Lister.setWatchDir(m_ModelDirectory.getAbsolutePath());
    m_Lister.setRegExp(new BaseRegExp(".*\\" + m_ModelExtension + "\\.([0-9]+|ckp)$"));
    m_Lister.setRecursive(false);
    m_Lister.setListDirs(false);
    m_Lister.setListFiles(true);

    // setup thread
    m_ExecutionFailure = null;
    m_ProcessOutput = new StreamingProcessOutput(this);
    m_Monitor = new RunnableWithLogging() {
      private static final long serialVersionUID = -4475355379511760429L;
      protected Process m_Process;
      @Override
      protected void doRun() {
        try {
	  m_Process = Runtime.getRuntime().exec(fCmd, null, null);
	  m_ProcessOutput.monitor(fCmd, null, m_Process);
	  if (m_ProcessOutput.getExitCode() != 0)
	    m_ExecutionFailure = new IllegalStateException("Exit code " + m_ProcessOutput.getExitCode() + " when executing: " + fCmd);
	}
	catch (Exception e) {
          m_ExecutionFailure = new IllegalStateException("Failed to execute: " + fCmd, e);
	}
        m_ProcessOutput = null;
        // leave tmp script for inspection in case of error
        if ((m_TmpScript != null) && (m_ExecutionFailure == null))
	  m_TmpScript.delete();
      }
      @Override
      public void stopExecution() {
        if (m_Process != null)
          m_Process.destroy();
	super.stopExecution();
      }
    };
    new Thread(m_Monitor).start();

    // wait for thread to start
    while (!m_Monitor.isStopped() && !m_Monitor.isRunning() && (m_ExecutionFailure == null))
      Utils.wait(m_Monitor, 1000, 100);

    // wait for script to finish
    while (m_Monitor.isRunning())
      Utils.wait(m_Monitor, 1000, 100);

    m_Monitor = null;

    // error occurred?
    if (m_ExecutionFailure != null)
      throw m_ExecutionFailure;
  }

  /**
   * Initializes the model.
   *
   * @param data	the training data
   * @throws Exception	if loading fails
   */
  protected void initModel(Instances data) throws Exception {
    m_Wrapper.initDevice(m_DeviceType, m_GPUDeviceID);
    m_Wrapper.loadModel(m_Model);
    m_Wrapper.initModel(data.numClasses());
  }

  /**
   * Generates a classifier. Must initialize all fields of the classifier
   * that are not being set via options (ie. multiple calls of buildClassifier
   * must always lead to the same result). Must not change the dataset
   * in any way.
   *
   * @param data set of instances serving as training data
   * @throws Exception if the classifier has not been generated successfully
   */
  @Override
  public void buildClassifier(Instances data) throws Exception {
    getCapabilities().testWithFail(data);

    data = new Instances(data);
    data.deleteWithMissingClass();

    buildModel(data);
    initModel(data);
  }

  /**
   * Predicts the class memberships for a given instance. If an instance is
   * unclassified, the returned array elements must be all zero. If the class is
   * numeric, the array must consist of only one element, which contains the
   * predicted value.
   *
   * @param instance the instance to be classified
   * @return an array containing the estimated membership probabilities of the
   *         test instance in each class or the numeric prediction
   * @throws Exception if distribution could not be computed successfully
   */
  @Override
  public double[] distributionForInstance(Instance instance) throws Exception {
    double[]	result;
    float[]	scores;
    TFloatList 	values;
    int		i;

    if (!m_Wrapper.isInitialized())
      initModel(instance.dataset());

    values = new TFloatArrayList();
    for (i = 0; i < instance.numAttributes(); i++)
      values.add((float) instance.value(i));

    scores = m_Wrapper.predict(values.toArray());
    result = new double[scores.length];
    if (scores.length > 1) {
      scores = m_Normalize.postProcessPrediction(scores);
      for (i = 0; i < scores.length; i++)
	result[i] = scores[i];
    }
    else if (scores.length == 1) {
      result[0] = scores[0];
    }

    return result;
  }

  /**
   * Returns a string representation of the built model.
   *
   * @return		the representation
   */
  @Override
  public String toString() {
    if (m_Wrapper.getModel() == null)
      return "No model loaded yet!";
    return m_Wrapper.getModel().toString();
  }
}
