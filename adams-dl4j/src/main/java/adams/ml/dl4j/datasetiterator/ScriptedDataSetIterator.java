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
 * ScriptedInputSplit.java
 * Copyright (C) 2016-2017 University of Waikato, Hamilton, New Zealand
 */

package adams.ml.dl4j.datasetiterator;

import adams.core.scripting.AbstractScriptingHandler;
import adams.core.scripting.Dummy;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.DataSetPreProcessor;

import java.util.List;
import java.util.NoSuchElementException;

/**
 <!-- globalinfo-start -->
 * A meta dataset iterator that uses any scripting handler for managing the  in the specified script file.
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
 * @version $Revision: 13195 $
 */
public class ScriptedDataSetIterator
  extends AbstractScriptedDataSetIterator {

  /** for serialization. */
  private static final long serialVersionUID = 1304903578667689350L;

  /** the loaded script object. */
  protected transient DataSetIterator m_DataSetIteratorObject;

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
      "A meta dataset iterator that uses any scripting handler for managing the "
        + "dataset iterator in the specified script file.";
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
      DataSetIterator.class,
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
      m_DataSetIteratorObject = (DataSetIterator) m_ScriptObject;

    return result;
  }

  /**
   * Frees up memory in a "destructive" non-reversible way.
   */
  @Override
  public void destroy() {
    super.destroy();

    m_DataSetIteratorObject = null;
  }

  /**
   * Returns the iterator. Raises an {@link IllegalStateException} if not
   * model object loaded.
   *
   * @return		the iterator
   */
  protected synchronized DataSetIterator getDataSetIterator() {
    if (m_DataSetIteratorObject != null)
      return m_DataSetIteratorObject;
    else
      throw new IllegalStateException("No input split script loaded!");
  }

  /**
   * Like the standard next method but allows a
   * customizable number of examples returned
   *
   * @param i the number of examples
   * @return the next data applyTransformToDestination
   */
  @Override
  public DataSet next(int i) {
    return getDataSetIterator().next(i);
  }

  /**
   * Total examples in the iterator
   *
   * @return
   */
  @Override
  public int totalExamples() {
    return getDataSetIterator().totalExamples();
  }

  /**
   * Input columns for the dataset
   *
   * @return
   */
  @Override
  public int inputColumns() {
    return getDataSetIterator().inputColumns();
  }

  /**
   * The number of labels for the dataset
   *
   * @return
   */
  @Override
  public int totalOutcomes() {
    return getDataSetIterator().totalOutcomes();
  }

  /**
   * Is resetting supported by this DataSetIterator? Many DataSetIterators do support resetting,
   * but some don't
   *
   * @return true if reset method is supported; false otherwise
   */
  @Override
  public boolean resetSupported() {
    return getDataSetIterator().resetSupported();
  }

  /**
   * Does this DataSetIterator support asynchronous prefetching of multiple DataSet objects?
   * Most DataSetIterators do, but in some cases it may not make sense to wrap this iterator in an
   * iterator that does asynchronous prefetching. For example, it would not make sense to use asynchronous
   * prefetching for the following types of iterators:
   * (a) Iterators that store their full contents in memory already
   * (b) Iterators that re-use features/labels arrays (as future next() calls will overwrite past contents)
   * (c) Iterators that already implement some level of asynchronous prefetching
   * (d) Iterators that may return different data depending on when the next() method is called
   *
   * @return true if asynchronous prefetching from this iterator is OK; false if asynchronous prefetching should not
   * be used with this iterator
   */
  @Override
  public boolean asyncSupported() {
    return getDataSetIterator().asyncSupported();
  }

  /**
   * Batch size
   *
   * @return
   */
  @Override
  public int batch() {
    return getDataSetIterator().batch();
  }

  /**
   * The current cursor if applicable
   *
   * @return
   */
  @Override
  public int cursor() {
    return getDataSetIterator().cursor();
  }

  /**
   * Total number of examples in the dataset
   *
   * @return
   */
  @Override
  public int numExamples() {
    return getDataSetIterator().numExamples();
  }

  /**
   * Set a pre processor
   *
   * @param preProcessor a pre processor to set
   */
  @Override
  public void setPreProcessor(DataSetPreProcessor preProcessor) {
    getDataSetIterator().setPreProcessor(preProcessor);
  }

  /**
   * Returns preprocessors, if defined
   *
   * @return
   */
  @Override
  public DataSetPreProcessor getPreProcessor() {
    return getDataSetIterator().getPreProcessor();
  }

  /**
   * Get dataset iterator record reader labels
   *
   */
  @Override
  public List<String> getLabels() {
    return getDataSetIterator().getLabels();
  }

  /**
   * Returns {@code true} if the iteration has more elements.
   * (In other words, returns {@code true} if {@link #next} would
   * return an element rather than throwing an exception.)
   *
   * @return {@code true} if the iteration has more elements
   */
  @Override
  public boolean hasNext() {
    return getDataSetIterator().hasNext();
  }

  /**
   * Returns the next element in the iteration.
   *
   * @return the next element in the iteration
   * @throws NoSuchElementException if the iteration has no more elements
   */
  @Override
  public DataSet next() {
    return getDataSetIterator().next();
  }
}
