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
 * RecordReaderDataSetIterator.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.ml.dl4j.datasetiterator;

import adams.ml.dl4j.recordreader.ImageRecordReaderConfigurator;
import adams.ml.dl4j.recordreader.RecordReaderConfigurator;
import org.canova.api.io.converters.SelfWritableConverter;
import org.deeplearning4j.datasets.canova.RecordReaderDataSetIterator;
import org.deeplearning4j.datasets.iterator.DataSetIterator;

/**
 <!-- globalinfo-start -->
 * Configures a org.deeplearning4j.datasets.canova.RecordReaderDataSetIterator instance, using the specified base record reader configurator.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-record-reader &lt;adams.ml.dl4j.recordreader.RecordReaderConfigurator&gt; (property: recordReader)
 * &nbsp;&nbsp;&nbsp;The record reader configurator to use.
 * &nbsp;&nbsp;&nbsp;default: adams.ml.dl4j.recordreader.ImageRecordReaderConfigurator
 * </pre>
 * 
 * <pre>-batch-size &lt;int&gt; (property: batchSize)
 * &nbsp;&nbsp;&nbsp;The batch size to use.
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-label-index &lt;int&gt; (property: labelIndex)
 * &nbsp;&nbsp;&nbsp;The index of the attribute with the labels (0-based).
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-num-possible-labels &lt;int&gt; (property: numPossibleLabels)
 * &nbsp;&nbsp;&nbsp;The number of possible labels.
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-regression &lt;boolean&gt; (property: regression)
 * &nbsp;&nbsp;&nbsp;Whether regression or classification.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RecordReaderDataSetIteratorConfigurator
  extends AbstractDataSetIteratorConfigurator {

  private static final long serialVersionUID = 7600213724265033440L;

  /** the record reader configurator. */
  protected RecordReaderConfigurator m_RecordReader;

  /** the batch size. */
  protected int m_BatchSize;

  /** the label index. */
  protected int m_LabelIndex;

  /** the number of possible labels. */
  protected int m_NumPossibleLabels;

  /** whether regression or classification. */
  protected boolean m_Regression;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Configures a " + RecordReaderDataSetIterator.class.getName() + " instance, using the specified base record reader configurator.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "record-reader", "recordReader",
      new ImageRecordReaderConfigurator());

    m_OptionManager.add(
      "batch-size", "batchSize",
      1, 1, null);

    m_OptionManager.add(
      "label-index", "labelIndex",
      1, 1, null);

    m_OptionManager.add(
      "num-possible-labels", "numPossibleLabels",
      1, 1, null);

    m_OptionManager.add(
      "regression", "regression",
      false);
  }

  /**
   * Sets the record reader to use.
   *
   * @param value	the reader
   */
  public void setRecordReader(RecordReaderConfigurator value) {
    m_RecordReader = value;
    reset();
  }

  /**
   * Returns the record reader to use.
   *
   * @return 		the reader
   */
  public RecordReaderConfigurator getRecordReader() {
    return m_RecordReader;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String recordReaderTipText() {
    return "The record reader configurator to use.";
  }

  /**
   * Sets the batch size.
   *
   * @param value	the size
   */
  public void setBatchSize(int value) {
    if (getOptionManager().isValid("batchSize", value)) {
      m_BatchSize = value;
      reset();
    }
  }

  /**
   * Returns the batch size.
   *
   * @return 		the size
   */
  public int getBatchSize() {
    return m_BatchSize;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String batchSizeTipText() {
    return "The batch size to use.";
  }

  /**
   * Sets the index of the attribute with the labels.
   *
   * @param value	the index (0-based)
   */
  public void setLabelIndex(int value) {
    if (getOptionManager().isValid("labelIndex", value)) {
      m_LabelIndex = value;
      reset();
    }
  }

  /**
   * Returns the index of the attribute with the labels.
   *
   * @return 		the index (0-based)
   */
  public int getLabelIndex() {
    return m_LabelIndex;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String labelIndexTipText() {
    return "The index of the attribute with the labels (0-based).";
  }

  /**
   * Sets the number of possible labels.
   *
   * @param value	the number
   */
  public void setNumPossibleLabels(int value) {
    if (getOptionManager().isValid("numPossibleLabels", value)) {
      m_NumPossibleLabels = value;
      reset();
    }
  }

  /**
   * Returns the number of possible labels.
   *
   * @return 		the number
   */
  public int getNumPossibleLabels() {
    return m_NumPossibleLabels;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String numPossibleLabelsTipText() {
    return "The number of possible labels.";
  }

  /**
   * Sets whether regression or classification.
   *
   * @param value	true if regression
   */
  public void setRegression(boolean value) {
    m_Regression = value;
    reset();
  }

  /**
   * Returns whether regression or classification.
   *
   * @return 		true if regression
   */
  public boolean getRegression() {
    return m_Regression;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String regressionTipText() {
    return "Whether regression or classification.";
  }

  /**
   * Configures the actual {@link DataSetIterator} and returns it.
   *
   * @return		the iterator
   */
  @Override
  protected DataSetIterator doConfigureDataSetIterator() {
    return new RecordReaderDataSetIterator(
      m_RecordReader.configureRecordReader(),
      new SelfWritableConverter(),
      m_BatchSize,
      m_LabelIndex,
      m_NumPossibleLabels,
      m_Regression
    );
  }
}
