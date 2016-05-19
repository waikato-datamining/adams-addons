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
 * DataSetIteratorWithScriptedConfiguration.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.ml.dl4j.datasetpreprocessor;

import adams.core.scripting.AbstractScriptingHandler;
import adams.core.scripting.Dummy;
import org.nd4j.linalg.dataset.api.DataSetPreProcessor;

/**
 <!-- globalinfo-start -->
 * A meta-preprocessor that uses any scripting handler for generating the preprocessor in the specified script file.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-script &lt;adams.core.io.PlaceholderFile&gt; (property: scriptFile)
 * &nbsp;&nbsp;&nbsp;The script file to load and execute.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 * <pre>-options &lt;adams.core.base.BaseText&gt; (property: scriptOptions)
 * &nbsp;&nbsp;&nbsp;The options for the script; must consist of 'key=value' pairs separated 
 * &nbsp;&nbsp;&nbsp;by blanks; the value of 'key' can be accessed via the 'getAdditionalOptions
 * &nbsp;&nbsp;&nbsp;().getXYZ("key")' method in the script actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-handler &lt;adams.core.scripting.AbstractScriptingHandler&gt; (property: handler)
 * &nbsp;&nbsp;&nbsp;The handler to use for scripting.
 * &nbsp;&nbsp;&nbsp;default: adams.core.scripting.Dummy
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 13193 $
 */
public class DataSetPreProcessorWithScriptedConfiguration
  extends AbstractScriptedDataSetPreProcessorConfigurator
  implements DataSetPreProcessor {

  /** for serialization. */
  private static final long serialVersionUID = 1304903578667689350L;

  /** the loaded script object. */
  protected transient DataSetPreProcessorConfigurator m_DataSetPreProcessorConfiguratorObject;

  /** the configured preprocessor to use. */
  protected DataSetPreProcessor m_DataSetPreProcessor;

  /** the scripting handler to use. */
  protected AbstractScriptingHandler m_Handler;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "A meta-preprocessor that uses any scripting handler for generating the "
	+ "preprocessor in the specified script file.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "handler", "handler",
      new Dummy());
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  @Override
  public String scriptOptionsTipText() {
    return
      "The options for the script; must consist of 'key=value' pairs "
	+ "separated by blanks; the value of 'key' can be accessed via the "
	+ "'getAdditionalOptions().getXYZ(\"key\")' method in the script actor.";
  }

  /**
   * Sets the handler to use for scripting.
   *
   * @param value 	the handler
   */
  public void setHandler(AbstractScriptingHandler value) {
    m_Handler = value;
    reset();
  }

  /**
   * Gets the handler to use for scripting.
   *
   * @return 		the handler
   */
  public AbstractScriptingHandler getHandler() {
    return m_Handler;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String handlerTipText() {
    return "The handler to use for scripting.";
  }

  /**
   * Loads the scripts object and sets its options.
   *
   * @return		null if OK, otherwise the error message
   */
  @Override
  protected String loadScriptObject() {
    Object[]	result;

    result = m_Handler.loadScriptObject(
      DataSetPreProcessorConfigurator.class,
      m_ScriptFile,
      m_ScriptOptions,
      getOptionManager().getVariables());
    m_ScriptObject = result[1];

    return (String) result[0];
  }

  /**
   * Checks the script object.
   *
   * @return		null if OK, otherwise the error message
   */
  @Override
  protected String checkScriptObject() {
    // TODO checks?
    return null;
  }

  /**
   * Hook method for checks before the actual execution.
   *
   * @return		null if checks passed, otherwise error message
   */
  @Override
  protected String check() {
    String	result;

    result = super.check();

    if (result == null)
      m_DataSetPreProcessorConfiguratorObject = (DataSetPreProcessorConfigurator) m_ScriptObject;

    return result;
  }

  /**
   * Frees up memory in a "destructive" non-reversible way.
   */
  @Override
  public void destroy() {
    super.destroy();

    m_DataSetPreProcessorConfiguratorObject = null;
  }

  /**
   * Configures the iterator and returns it.
   *
   * @return		the iterator
   */
  @Override
  public DataSetPreProcessor configureDataSetPreProcessor() {
    String	msg;

    if (m_DataSetPreProcessorConfiguratorObject == null) {
      msg = check();
      if (msg != null)
	throw new IllegalStateException(msg);
    }
    if (m_DataSetPreProcessorConfiguratorObject != null)
      return m_DataSetPreProcessorConfiguratorObject.configureDataSetPreProcessor();
    else
      throw new IllegalStateException("No dataset iterator generator available!");
  }

  /**
   * Instantiates the preprocessor if necessary.
   *
   * @return		the preprocessor
   * @see		#configureDataSetPreProcessor()
   */
  protected synchronized DataSetPreProcessor getDataSetPreProcessor() {
    if (m_DataSetPreProcessor == null)
      m_DataSetPreProcessor = configureDataSetPreProcessor();
    return m_DataSetPreProcessor;
  }

  /**
   * Pre process a dataset
   *
   * @param toPreProcess the data set to pre process
   */
  @Override
  public void preProcess(org.nd4j.linalg.dataset.api.DataSet toPreProcess) {
    getDataSetPreProcessor().preProcess(toPreProcess);
  }
}
