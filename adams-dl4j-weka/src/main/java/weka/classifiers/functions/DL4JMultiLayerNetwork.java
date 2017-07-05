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
 *    DL4JMultiLayerNetwork.java
 *    Copyright (C) 2016-2017 University of Waikato, Hamilton, New Zealand
 *
 */
package weka.classifiers.functions;

import adams.core.SerializationHelper;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.data.conversion.DL4JJsonToModel;
import adams.data.conversion.DL4JYamlToModel;
import adams.flow.container.DL4JModelContainer;
import adams.ml.dl4j.iterationlistener.IterationListenerConfigurator;
import adams.ml.dl4j.model.Dl4jMlpClassifier.DropType;
import adams.ml.dl4j.model.ModelType;
import adams.ml.dl4j.trainstopcriterion.AbstractTrainStopCriterion;
import adams.ml.dl4j.trainstopcriterion.MaxEpoch;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration.ListBuilder;
import org.deeplearning4j.nn.conf.layers.Layer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.optimize.api.IterationListener;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import weka.classifiers.DL4JFilteredMultiLayerNetworkProvider;
import weka.classifiers.DL4JMultiLayerNetworkProvider;
import weka.classifiers.RandomizableClassifier;
import weka.classifiers.rules.ZeroR;
import weka.core.Capabilities;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;
import weka.core.WekaOptionUtils;
import weka.dl4j.iterators.DefaultInstancesIterator;
import weka.dl4j.layers.DenseLayer;
import weka.dl4j.layers.OutputLayer;
import weka.filters.AllFilter;
import weka.filters.Filter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;
import java.util.Vector;

/**
 * A wrapper for DeepLearning4j that can be used to train a multi-layer
 * perceptron using that library.
 *
 * @author Christopher Beckham
 * @author Eibe Frank
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 11711 $
 */
public class DL4JMultiLayerNetwork
  extends RandomizableClassifier
  implements DL4JFilteredMultiLayerNetworkProvider {

  /** The ID used for serializing this class. */
  protected static final long serialVersionUID = -6363254116597574265L;

  public final static String LAYER = "layer";

  public final static String OPTIMIZATION_ALGORITHM = "optimization-algorithm";

  public final static String TRAIN_STOP = "train-stop";

  public final static String MINI_BATCH_SIZE = "mini-batch-size";

  public final static String RANDOMIZE_BETWEEN_EPOCHS = "randomize-between-epochs";

  public final static String DROP_TYPE = "drop-type";

  public final static String DROP_OUT = "drop-out";

  public final static String ITERATION_LISTENER = "iteration-listener";

  public final static String MODEL_FILE = "model-file";

  public final static String MODEL_FILE_TYPE = "model-file-type";

  public final static String FILTER = "filter";

  public enum ModelFileType {
    YAML,
    JSON,
    DL4J_SERIALIZED_MODEL,
    WEKA_SERIALIZED_MODEL,
  }

  /**
   * ZeroR classifier, just in case we don't actually have any data to train a
   * network.
   */
  protected ZeroR m_ZeroR;

  /** The actual neural network model. **/
  protected transient MultiLayerNetwork m_Model;

  /** the model data. */
  protected byte[] m_ModelData = null;

  /** whether the model was trained. */
  protected boolean m_Trained = false;

  /** The layers of the network. */
  protected Layer[] m_Layers = getDefaultLayers();

  /** The configuration parameters of the network. */
  protected OptimizationAlgorithm m_Algorithm = getDefaultOptimizationAlgorithm();

  /** the criterion for stopping training. */
  protected AbstractTrainStopCriterion m_TrainStop = getDefaultTrainStop();

  /** the minibatch size. */
  protected int m_MiniBatchSize = getDefaultMiniBatchSize();

  /** whether to randomize the data between epochs. */
  protected boolean m_RandomizeBetweenEpochs = getDefaultRandomizeBetweenEpochs();

  /** whether to use regularization. */
  protected boolean m_UseRegularization = getDefaultUseRegularization();

  /** the drop type. */
  protected DropType m_DropType = getDefaultDropType();

  /** the drop out value. */
  protected double m_DropOut = getDefaultDropOut();

  /** the iteration listeners to use. */
  protected IterationListenerConfigurator[] m_IterationListeners = getDefaultIterationListeners();

  /** the file to load the model from. */
  protected PlaceholderFile m_ModelFile = getDefaultModelFile();

  /** the type of the model file. */
  protected ModelFileType m_ModelFileType = getDefaultModelFileType();

  /** the filter to use. */
  protected Filter m_Filter = getDefaultFilter();

  /** the actual filter in use. */
  protected Filter m_ActualFilter;

  /** the iterator. */
  protected DefaultInstancesIterator m_Iterator = null;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "Classification and regression with multilayer perceptrons using DeepLearning4J.";
  }

  /**
   * Returns an enumeration describing the available options.
   *
   * @return 		an enumeration of all the available options.
   */
  public Enumeration listOptions() {
    Vector result;

    result = new Vector();

    WekaOptionUtils.addOption(result, layersTipText(), Utils.arrayToString(getDefaultLayers()), LAYER);
    WekaOptionUtils.addOption(result, optimizationAlgorithmTipText(), "" + getDefaultOptimizationAlgorithm(), OPTIMIZATION_ALGORITHM);
    WekaOptionUtils.addOption(result, trainStopTipText(), "" + getDefaultTrainStop(), TRAIN_STOP);
    WekaOptionUtils.addOption(result, miniBatchSizeTipText(), "" + getDefaultMiniBatchSize(), MINI_BATCH_SIZE);
    WekaOptionUtils.addOption(result, randomizeBetweenEpochsTipText(), "" + getDefaultRandomizeBetweenEpochs(), RANDOMIZE_BETWEEN_EPOCHS);
    WekaOptionUtils.addOption(result, dropTypeTipText(), "" + getDefaultDropType(), DROP_TYPE);
    WekaOptionUtils.addOption(result, dropOutTipText(), "" + getDefaultDropOut(), DROP_OUT);
    WekaOptionUtils.addOption(result, iterationListenersTipText(), Utils.arrayToString(getDefaultIterationListeners()), ITERATION_LISTENER);
    WekaOptionUtils.addOption(result, modelFileTipText(), "" + getDefaultModelFile(), MODEL_FILE);
    WekaOptionUtils.addOption(result, modelFileTypeTipText(), "" + getDefaultModelFileType(), MODEL_FILE_TYPE);
    WekaOptionUtils.addOption(result, filterTipText(), getDefaultFilter(), FILTER);
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
    setLayers((Layer[]) WekaOptionUtils.parse(options, LAYER, getDefaultLayers(), Layer.class));
    setOptimizationAlgorithm((OptimizationAlgorithm) WekaOptionUtils.parse(options, OPTIMIZATION_ALGORITHM, getDefaultOptimizationAlgorithm()));
    setTrainStop((AbstractTrainStopCriterion) WekaOptionUtils.parse(options, TRAIN_STOP, getDefaultTrainStop()));
    setMiniBatchSize(WekaOptionUtils.parse(options, MINI_BATCH_SIZE, getDefaultMiniBatchSize()));
    setRandomizeBetweenEpochs(Utils.getFlag(RANDOMIZE_BETWEEN_EPOCHS, options));
    setDropType((DropType) WekaOptionUtils.parse(options, DROP_TYPE, getDefaultDropType()));
    setDropOut(WekaOptionUtils.parse(options, DROP_OUT, getDefaultDropOut()));
    setIterationListeners((IterationListenerConfigurator[]) WekaOptionUtils.parse(options, ITERATION_LISTENER, getDefaultIterationListeners(), IterationListenerConfigurator.class));
    setModelFile(WekaOptionUtils.parse(options, MODEL_FILE, getDefaultModelFile()));
    setModelFileType((ModelFileType) WekaOptionUtils.parse(options, MODEL_FILE_TYPE, getDefaultModelFileType()));
    setFilter((Filter) WekaOptionUtils.parse(options, FILTER, getDefaultFilter()));
    super.setOptions(options);
  }

  /**
   * Gets the current settings of the classifier.
   *
   * @return 		an array of strings suitable for passing to setOptions
   */
  public String [] getOptions() {
    List<String> result = new ArrayList<>();
    if (getModelFile().isDirectory()) {
      WekaOptionUtils.add(result, LAYER, getLayers());
      WekaOptionUtils.add(result, OPTIMIZATION_ALGORITHM, getOptimizationAlgorithm());
      WekaOptionUtils.add(result, DROP_TYPE, getDropType());
      WekaOptionUtils.add(result, DROP_OUT, getDropOut());
    }
    else {
      WekaOptionUtils.add(result, MODEL_FILE, getModelFile());
      WekaOptionUtils.add(result, MODEL_FILE_TYPE, getModelFileType());
    }
    WekaOptionUtils.add(result, TRAIN_STOP, getTrainStop());
    WekaOptionUtils.add(result, MINI_BATCH_SIZE, getMiniBatchSize());
    WekaOptionUtils.add(result, RANDOMIZE_BETWEEN_EPOCHS, getRandomizeBetweenEpochs());
    WekaOptionUtils.add(result, ITERATION_LISTENER, getIterationListeners());
    WekaOptionUtils.add(result, FILTER, getFilter());
    WekaOptionUtils.add(result, super.getOptions());
    return WekaOptionUtils.toArray(result);
  }

  /**
   * Returns default capabilities of the classifier.
   *
   * @return the capabilities of this classifier
   */
  public Capabilities getCapabilities() {
    Capabilities 	result;

    if (m_Filter instanceof AllFilter) {
      result = super.getCapabilities();
      result.disableAll();

      // attributes
      result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
      result.enable(Capabilities.Capability.DATE_ATTRIBUTES);

      // class
      result.enable(Capabilities.Capability.NOMINAL_CLASS);
      result.enable(Capabilities.Capability.NUMERIC_CLASS);
      result.enable(Capabilities.Capability.DATE_CLASS);
      result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
    }
    else {
      result = m_Filter.getCapabilities();
      result.setOwner(this);
    }

    return result;
  }

  /**
   * Custom serialization method.
   * <br>
   * Simply serializing into the stream doesn't work, as models cannot be
   * deserialized anymore if the classifier is inside a FilteredClassifier.
   *
   * @param oos the object output stream
   * @throws IOException
   * @see #m_ModelData
   */
  private void writeObject(ObjectOutputStream oos) throws IOException {
    ByteArrayOutputStream 	bos;

    if (m_Trained) {
      bos = new ByteArrayOutputStream();
      ModelSerializer.writeModel(m_Model, bos, false);
      m_ModelData = bos.toByteArray();
    }

    oos.defaultWriteObject();

    // free up memory
    m_ModelData = null;
  }

  /**
   * Custom deserialization method
   *
   * @param ois the object input stream
   * @throws ClassNotFoundException
   * @throws IOException
   * @see #m_ModelData
   */
  private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
    ByteArrayInputStream 	bis;

    ois.defaultReadObject();

    if (m_ModelData != null) {
      bis         = new ByteArrayInputStream(m_ModelData);
      m_Model     = ModelSerializer.restoreMultiLayerNetwork(bis, false);
      // free up memory
      m_ModelData = null;
    }
  }

  /**
   * Returns the default layers.
   *
   * @return		the default
   */
  protected Layer[] getDefaultLayers() {
    return new Layer[]{new OutputLayer()};
  }

  /**
   * Sets the layers to use.
   *
   * @param value	the layers
   */
  public void setLayers(Layer[] value) {
    m_Layers = value;
  }

  /**
   * Returns the layers.
   *
   * @return		the layers
   */
  public Layer[] getLayers() {
    return m_Layers;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String layersTipText() {
    return "The layers for the network (last one must be an output layer).";
  }

  /**
   * Returns the default number of epochs.
   *
   * @return		the default
   */
  protected AbstractTrainStopCriterion getDefaultTrainStop() {
    return new MaxEpoch();
  }

  /**
   * Sets the number of epochs to train.
   *
   * @param value	the number of epochs
   */
  public void setTrainStop(AbstractTrainStopCriterion value) {
    m_TrainStop = value;
  }

  /**
   * Returns the number of epochs to train.
   *
   * @return		the number of epochs
   */
  public AbstractTrainStopCriterion getTrainStop() {
    return m_TrainStop;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String trainStopTipText() {
    return "The criterion for stopping training.";
  }

  /**
   * Returns the default mini-batch size.
   *
   * @return		the default
   */
  protected int getDefaultMiniBatchSize() {
    return 100;
  }

  /**
   * Sets the size for the mini-batches.
   *
   * @param value	the size
   */
  public void setMiniBatchSize(int value) {
    m_MiniBatchSize = value;
  }

  /**
   * Returns the size for the mini-batches.
   *
   * @return		the size
   */
  public int getMiniBatchSize() {
    return m_MiniBatchSize;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String miniBatchSizeTipText() {
    return "The size to use for mini-batches.";
  }

  /**
   * Returns whether to randomize mini-batches between epochs.
   *
   * @return		the default
   */
  protected boolean getDefaultRandomizeBetweenEpochs() {
    return false;
  }

  /**
   * Sets whether to randomize the data between epochs.
   *
   * @param value	true if to randomize
   */
  public void setRandomizeBetweenEpochs(boolean value) {
    m_RandomizeBetweenEpochs = value;
  }

  /**
   * Returns whether to randomize the data between epochs.
   *
   * @return		true if to randomize
   */
  public boolean getRandomizeBetweenEpochs() {
    return m_RandomizeBetweenEpochs;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String randomizeBetweenEpochsTipText() {
    return "If enabled, the data gets randomized between epochs.";
  }

  /**
   * Returns the default optimization algorithm.
   *
   * @return		the default
   */
  protected OptimizationAlgorithm getDefaultOptimizationAlgorithm() {
    return OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT;
  }

  /**
   * Sets the optimization algorithm to use.
   *
   * @param value	the algorithm
   */
  public void setOptimizationAlgorithm(OptimizationAlgorithm value) {
    m_Algorithm = value;
  }

  /**
   * Returns the optimization algorithm.
   *
   * @return		the algorithm
   */
  public OptimizationAlgorithm getOptimizationAlgorithm() {
    return m_Algorithm;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String optimizationAlgorithmTipText() {
    return "The optimization algorithm to use.";
  }

  /**
   * Returns the default for whether to use regularization.
   *
   * @return		the default
   */
  protected boolean getDefaultUseRegularization() {
    return false;
  }

  /**
   * Sets whether to use regularization.
   *
   * @param value	true if to use regularization
   */
  public void setUseRegularization(boolean value) {
    m_UseRegularization = value;
  }

  /**
   * Returns whether to use regularization.
   *
   * @return  		true if to use regularization
   */
  public boolean getUseRegularization() {
    return m_UseRegularization;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useRegularizationTipText() {
    return "If enabled, regularization is used.";
  }

  /**
   * Returns the default drop type.
   *
   * @return		the default
   */
  protected DropType getDefaultDropType() {
    return DropType.NONE;
  }

  /**
   * Sets the drop type
   *
   * @param value	the type
   */
  public void setDropType(DropType value) {
    m_DropType = value;
  }

  /**
   * Returns the drop type.
   *
   * @return  		the type
   */
  public DropType getDropType() {
    return m_DropType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String dropTypeTipText() {
    return "The type of drop to use.";
  }

  /**
   * Returns the default drop out value.
   *
   * @return		the default
   */
  protected double getDefaultDropOut() {
    return 0.0;
  }

  /**
   * Sets the drop-out value.
   *
   * @param value	the drop-out
   */
  public void setDropOut(double value) {
    if ((value >= 0.0) && (value <= 1.0))
      m_DropOut = value;
  }

  /**
   * Returns the drop-out value.
   *
   * @return  		the drop-out
   */
  public double getDropOut() {
    return m_DropOut;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String dropOutTipText() {
    return "The drop-out value.";
  }

  /**
   * Returns the default iteration listeners.
   *
   * @return		the default
   */
  protected IterationListenerConfigurator[] getDefaultIterationListeners() {
    return new IterationListenerConfigurator[0];
  }

  /**
   * Sets the configurators for the iteration listeners.
   *
   * @param value	the listener configurators
   */
  public void setIterationListeners(IterationListenerConfigurator[] value) {
    m_IterationListeners = value;
  }

  /**
   * Returns the configurators for the iteration listeners.
   *
   * @return		the listener configurators
   */
  public IterationListenerConfigurator[] getIterationListeners() {
    return m_IterationListeners;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String iterationListenersTipText() {
    return "The configurators for iteration listeners to use.";
  }

  /**
   * Returns the default filter.
   *
   * @return		the default
   */
  protected Filter getDefaultFilter() {
    return new AllFilter();
  }

  /**
   * Sets the filter to apply to the data.
   *
   * @param value	the filter
   */
  public void setFilter(Filter value) {
    m_Filter = value;
  }

  /**
   * Returns the filter to apply to the data.
   *
   * @return		the filter
   */
  public Filter getFilter() {
    return m_Filter;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String filterTipText() {
    return "The filter to apply to the data, ignored if " + AllFilter.class.getName() + ".";
  }

  /**
   * Returns the default model file.
   *
   * @return		the default
   */
  protected PlaceholderFile getDefaultModelFile() {
    return new PlaceholderFile();
  }

  /**
   * Sets the model file to load; ignored if directory.
   *
   * @param value	the file
   */
  public void setModelFile(PlaceholderFile value) {
    m_ModelFile = value;
  }

  /**
   * Returns the model file to load; ignored if directory.
   *
   * @return		the file
   */
  public PlaceholderFile getModelFile() {
    return m_ModelFile;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String modelFileTipText() {
    return "The (optional) file to load a model from (trained or just structure); ignored if pointing to a directory.";
  }

  /**
   * Returns the default model file type.
   *
   * @return		the default
   */
  protected ModelFileType getDefaultModelFileType() {
    return ModelFileType.YAML;
  }

  /**
   * Sets the type of the model file.
   *
   * @param value	the type
   */
  public void setModelFileType(ModelFileType value) {
    m_ModelFileType = value;
  }

  /**
   * Returns the type of the model file.
   *
   * @return		the type
   */
  public ModelFileType getModelFileType() {
    return m_ModelFileType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String modelFileTypeTipText() {
    return
      "The type of the model file; in case of '" + ModelFileType.WEKA_SERIALIZED_MODEL + "', "
	+ "the object must implement '" + DL4JMultiLayerNetworkProvider.class.getName() + "'.";
  }

  /**
   * Get the current number of units for a particular layer. Returns -1 for
   * anything that is not a DenseLayer or an OutputLayer.
   *
   * @param layer the layer
   * @return the number of units
   */
  protected int getNumUnits(Layer layer) {
    if (layer instanceof DenseLayer)
      return ((DenseLayer) layer).getNOut();
    else if (layer instanceof OutputLayer)
      return ((OutputLayer) layer).getNOut();
    return -1;
  }

  /**
   * Sets the number of incoming connections for the nodes in the given layer.
   *
   * @param layer the layer
   * @param numInputs the number of inputs
   */
  protected void setNumIncoming(Layer layer, int numInputs) {
    if (layer instanceof DenseLayer)
      ((DenseLayer) layer).setNIn(numInputs);
    else if (layer instanceof OutputLayer)
      ((OutputLayer) layer).setNIn(numInputs);
  }

  /**
   * Loads the model from disk, if possible.
   *
   * @return		the model
   * @throws Exception	if failed to load
   */
  protected MultiLayerNetwork loadModel() throws Exception {
    MultiLayerNetwork			result;
    DL4JYamlToModel			yaml;
    DL4JJsonToModel 			json;
    String				str;
    String				msg;
    DL4JMultiLayerNetworkProvider	provider;

    result = null;

    if (getDebug())
      System.out.println("Loading model (" + m_ModelFileType + "): " + m_ModelFile);

    switch (m_ModelFileType) {
      case YAML:
	str  = adams.core.Utils.flatten(FileUtils.loadFromFile(m_ModelFile), "\n");
	yaml = new DL4JYamlToModel();
	yaml.setType(ModelType.MULTI_LAYER_NETWORK);
	yaml.setInput(str);
	msg = yaml.convert();
	if (msg != null)
	  throw new Exception("Failed to convert YAML model loaded from '" + m_ModelFile + "': " + msg);
	result = (MultiLayerNetwork) yaml.getOutput();
	break;

      case JSON:
	str  = adams.core.Utils.flatten(FileUtils.loadFromFile(m_ModelFile), "\n");
	json = new DL4JJsonToModel();
	json.setType(ModelType.MULTI_LAYER_NETWORK);
	json.setInput(str);
	msg = json.convert();
	if (msg != null)
	  throw new Exception("Failed to convert JSON model loaded from '" + m_ModelFile + "': " + msg);
	result = (MultiLayerNetwork) json.getOutput();
	break;

      case WEKA_SERIALIZED_MODEL:
	provider = (DL4JMultiLayerNetworkProvider) SerializationHelper.read(m_ModelFile.getAbsolutePath());
	result   = provider.getMultiLayerNetwork();
	break;

      case DL4J_SERIALIZED_MODEL:
	result = ModelSerializer.restoreMultiLayerNetwork(m_ModelFile.getAbsoluteFile());
	break;

      default:
	throw new IllegalStateException("Unhandled model file type: " + m_ModelFileType);
    }

    return result;
  }

  /**
   * Generates the model using the layers and parameters specified.
   *
   * @return		the model
   * @throws Exception	if failed to build or incorrect parameters
   */
  protected MultiLayerNetwork generateModel(Instances data) throws Exception {
    MultiLayerNetwork result;
    NeuralNetConfiguration.Builder 	builder;
    ListBuilder 			listbuilder;
    int 				numInputAtts;
    MultiLayerConfiguration 		conf;
    int					i;

    if (getDebug())
      System.out.println("Generating model...");

    // Check basic network structure
    if (m_Layers.length == 0)
      throw new Exception("No layers have been defined!");
    if (!(m_Layers[m_Layers.length - 1] instanceof OutputLayer))
      throw new Exception("Last layer in network must be an output layer!");

    builder = new NeuralNetConfiguration.Builder();
    builder.setOptimizationAlgo(getOptimizationAlgorithm());
    builder.setSeed(getSeed());
    if (m_UseRegularization)
      builder.setUseRegularization(true);

    switch (m_DropType) {
      case NONE:
	builder.setDropOut(0.0);
	break;
      case DROP_OUT:
	builder.setDropOut(m_DropOut);
	break;
      case DROP_CONNECT:
	builder.setUseDropConnect(true);
	builder.setDropOut(m_DropOut);
	break;
      default:
	throw new IllegalStateException("Unhandled drop type: " + m_DropType);
    }

    // Construct the mlp configuration
    listbuilder  = builder.list(getLayers());
    numInputAtts = m_Iterator.getNumAttributes(data);

    // Connect up the layers appropriately
    for (i = 0; i < m_Layers.length; i++) {
      // Is this the first hidden layer?
      if (i == 0)
	setNumIncoming(m_Layers[i], numInputAtts);
      else
	setNumIncoming(m_Layers[i], getNumUnits(m_Layers[i - 1]));

      // Is this the output layer?
      if (i == m_Layers.length - 1)
	((OutputLayer) m_Layers[i]).setNOut(data.numClasses());
      listbuilder = listbuilder.layer(i, m_Layers[i]);
    }

    listbuilder = listbuilder
      .pretrain(false)
      .backprop(true);
    conf = listbuilder.build();

    // initialize
    result = new MultiLayerNetwork(conf);
    result.init();

    return result;
  }

  /**
   * The method used to train the classifier.
   *
   * @param data set of instances serving as training data
   * @throws Exception if something goes wrong in the training process
   */
  @Override
  public void buildClassifier(Instances data) throws Exception {
    ArrayList<IterationListener> 	listeners;
    DataSetIterator 			iter;
    int					i;
    Random				rand;
    int					seed;

    // Can classifier handle the data?
    getCapabilities().testWithFail(data);

    m_Iterator = new DefaultInstancesIterator();

    // Remove instances with missing class and check that instances and
    // predictor attributes remain.
    data = new Instances(data);
    data.deleteWithMissingClass();
    m_ZeroR = null;
    if ((data.numInstances() == 0) || (data.numAttributes() < 2)) {
      System.err.println("Not enough data, using ZeroR model!");
      m_ZeroR = new ZeroR();
      m_ZeroR.buildClassifier(data);
      return;
    }

    // filter data
    m_ActualFilter = null;
    if (!(m_Filter instanceof AllFilter)) {
      m_ActualFilter = Filter.makeCopy(m_Filter);
      m_ActualFilter.setInputFormat(data);
      data = Filter.useFilter(data, m_ActualFilter);
    }

    // load model from file?
    if (m_ModelFile.exists() && !m_ModelFile.isDirectory())
      m_Model = loadModel();
    else
      m_Model = generateModel(data);

    // initialized?
    if (!m_Model.isInitCalled())
      m_Model.init();

    if (getDebug())
      System.out.println("Network:\n" + m_Model.getLayerWiseConfigurations().toYaml());

    // listeners
    listeners = new ArrayList<>();
    for (IterationListenerConfigurator l: m_IterationListeners)
      listeners.addAll(l.configureIterationListeners());
    m_Model.setListeners(listeners);

    // build
    rand = new Random(getSeed());
    seed = getSeed();
    i    = 0;
    do {
      if (m_RandomizeBetweenEpochs)
	seed = rand.nextInt();
      if (m_MiniBatchSize < 1)
	iter = m_Iterator.getIterator(data, seed);
      else
	iter = m_Iterator.getIterator(data, seed, m_MiniBatchSize);
      m_Model.fit(iter);
      if (getDebug() && ((i+1) % 100 == 0))
	System.out.println("Epoch #" + (i+1) + " finished");
      i++;
    }
    while (!m_TrainStop.checkStopping(new DL4JModelContainer(m_Model, null, i)));


    m_Trained = true;
  }

  /**
   * The method to use when making predictions for a test instance.
   *
   * @param inst the instance to get a prediction for
   * @return the class probability estimates (if the class is nominal) or the
   *         numeric prediction (if it is numeric)
   * @throws Exception if something goes wrong at prediction time
   */
  @Override
  public double[] distributionForInstance(Instance inst) throws Exception {
    Instances 	insts;
    DataSet 	dataset;
    INDArray 	predicted;
    double[] 	preds;
    int		i;

    // Do we only have a ZeroR model?
    if (m_ZeroR != null)
      return m_ZeroR.distributionForInstance(inst);

    // filter instance
    if (m_ActualFilter != null) {
      m_ActualFilter.input(inst);
      m_ActualFilter.batchFinished();
      inst = m_ActualFilter.output();
    }

    insts = new Instances(inst.dataset(), 0);
    insts.add(inst);
    dataset = m_Iterator.getIterator(insts, getSeed(), 1).next();
    predicted = m_Model.output(dataset.getFeatureMatrix(), false);
    predicted = predicted.getRow(0);
    preds = new double[inst.numClasses()];
    for (i = 0; i < preds.length; i++)
      preds[i] = predicted.getDouble(i);

    // only normalise if we're dealing with classification
    if (preds.length > 1)
      Utils.normalize(preds);

    return preds;
  }

  /**
   * Returns the network.
   *
   * @return		the network, null if none available
   */
  @Override
  public MultiLayerNetwork getMultiLayerNetwork() {
    return m_Model;
  }

  /**
   * Returns the filter that is used to filter the data before presenting it
   * to the network.
   *
   * @return		the filter in use
   */
  public Filter getPreFilter() {
    return m_ActualFilter;
  }

  /**
   * Returns a string describing the model.
   *
   * @return the model string
   */
  @Override
  public String toString() {
    return m_Model.getLayerWiseConfigurations().toYaml();
  }

  /**
   * The main method for running this class.
   *
   * @param args the command-line arguments
   */
  public static void main(String[] args) {
    runClassifier(new DL4JMultiLayerNetwork(), args);
  }
}