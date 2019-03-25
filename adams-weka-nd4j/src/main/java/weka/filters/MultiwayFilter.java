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
 * MultiwayFilter.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package weka.filters;

import adams.core.option.OptionUtils;
import adams.data.weka.columnfinder.Class;
import adams.data.weka.datasetsplitter.ColumnSplitter;
import adams.flow.transformer.wekadatasetsmerge.Simple;
import nz.ac.waikato.cms.adams.multiway.algorithm.api.AbstractAlgorithm;
import nz.ac.waikato.cms.adams.multiway.algorithm.api.Filter;
import nz.ac.waikato.cms.adams.multiway.algorithm.api.SupervisedAlgorithm;
import nz.ac.waikato.cms.adams.multiway.algorithm.api.UnsupervisedAlgorithm;
import nz.ac.waikato.cms.adams.multiway.algorithm.twoway.PLS2;
import nz.ac.waikato.cms.adams.multiway.data.tensor.Tensor;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.Utils;
import weka.core.WekaOptionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

/**
 <!-- globalinfo-start -->
 * Wrapper treating a multiway filter as a WEKA filter.
 * <br><br>
 <!-- globalinfo-end -->
 * <p>
 <!-- options-start -->
 * Valid options are: <p>
 *
 * <pre> -filter &lt;value&gt;
 *  The multiway filtering algorithm to use.
 *  (default: nz.ac.waikato.cms.adams.multiway.algorithm.twoway.PLS2 -debug false -num-components 5 -standardize-y true -array-stopping-criteria "nz.ac.waikato.cms.adams.multiway.algorithm.stopping.IterationCriterion -current-iteration 0 -max-iterations 250" -array-stopping-criteria "nz.ac.waikato.cms.adams.multiway.algorithm.stopping.ImprovementCriterion -tol 1.0E-7")</pre>
 *
 * <pre> -output-debug-info
 *  If set, filter is run in debug mode and
 *  may output additional info to the console</pre>
 *
 * <pre> -do-not-check-capabilities
 *  If set, filter capabilities are not checked before filter is built
 *  (use with caution).</pre>
 *
 <!-- options-end -->
 *
 * @author Corey Sterling (csterlin at waikato dot ac dot nz)
 */
public class MultiwayFilter extends SimpleBatchFilter {

  /** Auto-generated serialisation UID#. */
  private static final long serialVersionUID = -5490573675185624414L;

  /** The multiway filtering algorithm. */
  protected AbstractAlgorithm m_Filter = getDefaultFilter();

  /** Splitter for removing class attribute for supervised filters. */
  protected ColumnSplitter m_ClassSplitter = null;

  /** Template dataset for the filtered output. */
  protected Instances m_FilteredTemplate = null;

  /** Merger for rejoining class attribute for supervised filters. */
  protected Simple m_ClassMerger = null;

  /**
   * Gets an enumeration describing the available options.
   *
   * @return an enumeration of all the available options.
   */
  @Override
  public Enumeration<Option> listOptions() {
    Vector<Option> result = new Vector<>();

    WekaOptionUtils.addOption(result, filterTipText(), OptionUtils.getCommandLine(getDefaultFilter()), "filter");
    WekaOptionUtils.add(result, super.listOptions());

    return result.elements();
  }

  /**
   * returns the options of the current setup
   *
   * @return the current options
   */
  @Override
  public String[] getOptions() {
    List<String> result = new ArrayList<>();

    WekaOptionUtils.add(result, "filter", OptionUtils.getCommandLine(getFilter()));
    Collections.addAll(result, super.getOptions());

    return result.toArray(new String[0]);
  }

  /**
   * Parses the options for this object.
   *
   * @param options the options to use
   * @throws Exception if the option setting fails
   */
  @Override
  public void setOptions(String[] options) throws Exception {
    String filterCmd = OptionUtils.removeOption(options, "-filter");
    if (filterCmd != null) {
      setFilter((AbstractAlgorithm) OptionUtils.forAnyCommandLine(AbstractAlgorithm.class, filterCmd));
    } else {
      setFilter(getDefaultFilter());
    }
    super.setOptions(options);
    Utils.checkForRemainingOptions(options);
  }

  /**
   * Gets the default multiway filtering algorithm to use.
   *
   * @return  The default filtering algorithm.
   */
  public AbstractAlgorithm getDefaultFilter() {
    return new PLS2();
  }

  /**
   * Sets the multiway filtering algorithm to use.
   *
   * @param value  The filtering algorithm.
   */
  public void setFilter(AbstractAlgorithm value) {
    if (!(value instanceof Filter)) return;
    if (!(value instanceof SupervisedAlgorithm || value instanceof UnsupervisedAlgorithm)) return;
    m_Filter = value;
  }

  /**
   * Gets the multiway filtering algorithm to use.
   *
   * @return  The filtering algorithm.
   */
  public AbstractAlgorithm getFilter() {
    return m_Filter;
  }

  /**
   * Gets the tip-text for the filter option.
   *
   * @return  The tip-text as a string.
   */
  public String filterTipText() {
    return "The multiway filtering algorithm to use.";
  }

  /**
   * Returns a string describing this filter.
   *
   * @return a description of the filter suitable for displaying in the
   *         explorer/experimenter gui
   */
  @Override
  public String globalInfo() {
    return "Wrapper treating a multiway filter as a WEKA filter.";
  }

  /**
   * Gets the filtering algorithm already cast to a Filter.
   *
   * @return  The filtering algorithm cast to a Filter.
   */
  protected Filter getFilterAsFilter() {
    return (Filter) getFilter();
  }

  /**
   * Returns whether to allow the determineOutputFormat(Instances) method access
   * to the full dataset rather than just the header.
   * <p/>
   * Default implementation returns false.
   *
   * @return whether determineOutputFormat has access to the full input dataset
   */
  @Override
  public boolean allowAccessToFullInputFormat() {
    return true;
  }

  /**
   * resets the filter, i.e., m_NewBatch to true and m_FirstBatchDone to false.
   *
   * @see #m_NewBatch
   * @see #m_FirstBatchDone
   */
  @Override
  protected void reset() {
    // Need to reset the filter as some implementations can't handle
    // resetting of internal state
    String[] currentOptions = getOptions();

    try {
      setOptions(currentOptions);
    } catch (Exception e) {
      // This should not happen if get/set options are symmettric
      e.printStackTrace();
    }
  }

  /**
   * Determines the output format based on the input format and returns this. In
   * case the output format cannot be returned immediately, i.e.,
   * immediateOutputFormat() returns false, then this method will be called from
   * batchFinished().
   *
   * @param inputFormat the input format to base the output format on
   * @return the output format
   * @throws Exception in case the determination goes wrong
   * @see #hasImmediateOutputFormat()
   * @see #batchFinished()
   */
  @Override
  protected Instances determineOutputFormat(Instances inputFormat) throws Exception {
    // Make sure we don't try to retrain the filter
    if (isFirstBatchDone()) return getOutputFormat();

    // Supervised and unsupervised filters need to be treated differently
    if (m_Filter instanceof UnsupervisedAlgorithm) {
      // Convert the input data
      Tensor input = instancesToTensor(inputFormat);

      // Build the filter
      ((UnsupervisedAlgorithm) m_Filter).build(input);

      // Return an empty dataset sized to match the filtered data
      return emptyInstancesForTensor((int) getFilterAsFilter().filter(input).size(1));
    } else { //m_Filter instanceof SupervisedAlgorithm
      // Create the class attribute splitter
      m_ClassSplitter = new ColumnSplitter();
      m_ClassSplitter.setColumnFinder(new Class());

      // Split the class attribute from the training data
      Instances[] split = m_ClassSplitter.split(inputFormat);

      // Convert the training and class data into tensors
      Tensor x = instancesToTensor(split[1]);
      Tensor y = instancesToTensor(split[0]);

      // Build the filter
      ((SupervisedAlgorithm) m_Filter).build(x, y);

      // Create the empty datasets for filtered and class data
      Instances filtered = emptyInstancesForTensor((int) getFilterAsFilter().filter(x).size(1));
      Instances classSet = new Instances(split[0], 0);

      // Return the merge of the two datasets
      return remergeClassAttribute(filtered, classSet);
    }
  }

  /**
   * Processes the given data (may change the provided dataset) and returns the
   * modified version. This method is called in batchFinished().
   *
   * @param instances the data to process
   * @return the modified data
   * @throws Exception in case the processing goes wrong
   * @see #batchFinished()
   */
  @Override
  protected Instances process(Instances instances) throws Exception {
    // Supervised and unsupervised algorithms must be treated differently
    if (m_Filter instanceof UnsupervisedAlgorithm) {
      // Convert the input
      Tensor input = instancesToTensor(instances);

      // Return the filtered output
      return tensorToInstances(getFilterAsFilter().filter(input));
    } else {
      // Remove the class attribute
      Instances[] split = m_ClassSplitter.split(instances);

      // Convert the other attributes
      Tensor input = instancesToTensor(split[1]);

      // Get the filtered data
      Instances filtered = tensorToInstances(getFilterAsFilter().filter(input));

      // Rejoin the class attribute and return
      return remergeClassAttribute(filtered, split[0]);
    }
  }

  /**
   * Remerges the class attribute with the filtered data for supervised filters.
   *
   * @param filtered  The filtered dataset.
   * @param classSet  The class attribute dataset.
   * @return  The merged dataset.
   */
  protected Instances remergeClassAttribute(Instances filtered, Instances classSet) {
    // Create the merger once
    if (m_ClassMerger == null)
      m_ClassMerger = new Simple();

    // Return the merged dataset
    return m_ClassMerger.merge(new Instances[]{ filtered, classSet });
  }

  /**
   * Creates a tensor representation of the given dataset.
   *
   * @param instances The dataset to convert.
   * @return  The tensor representation.
   */
  protected Tensor instancesToTensor(Instances instances) {
    // Create the raw array for the tensor
    double[][] data = new double[instances.numInstances()][instances.numAttributes()];

    // Fill in the data from the dataset
    for (int instanceIndex = 0; instanceIndex < instances.numInstances(); instanceIndex++) {
      Instance instance = instances.get(instanceIndex);
      for (int attributeIndex = 0; attributeIndex < instances.numAttributes(); attributeIndex++) {
        data[instanceIndex][attributeIndex] = instance.value(attributeIndex);
      }
    }

    // Return the tensor
    return Tensor.create(data);
  }

  /**
   * Creates a dataset from the given tensor.
   *
   * @param tensor  The tensor to convert.
   * @return  The dataset representation of the tensor.
   */
  protected Instances tensorToInstances(Tensor tensor) {
    // Create the required empty dataset
    Instances output = emptyInstancesForTensor((int) tensor.size(1));

    // Get the tensor raw data
    double[][] data = tensor.toArray2d();

    // Create instances for each tensor row
    for (int i = 0; i < tensor.size(0); i++) {
      double[] row = data[i];
      Instance instance = new DenseInstance(1.0, row);
      output.add(instance);
    }

    return output;
  }

  /**
   * Creates an empty dataset sized to match the given tensor size.
   *
   * @param size  The size of the tensor being converted.
   * @return  The empty dataset.
   */
  protected Instances emptyInstancesForTensor(int size) {
    // Create the template once
    if (m_FilteredTemplate == null) {
      // Create a set of dummy attributes
      ArrayList<Attribute> attributes = new ArrayList<>();

      for (int i = 1; i <= size; i++) {
        Attribute attribute = new Attribute(m_Filter.getClass().getSimpleName() + i);
        attributes.add(attribute);
      }

      m_FilteredTemplate = new Instances("output", attributes, 0);
    }

    // Return a copy of the template
    return new Instances(m_FilteredTemplate, 0);
  }
}