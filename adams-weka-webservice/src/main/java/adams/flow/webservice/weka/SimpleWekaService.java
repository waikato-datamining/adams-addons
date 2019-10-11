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
 * SimpleWekaService.java
 * Copyright (C) 2013-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.webservice.weka;

import adams.core.SerializationHelper;
import adams.core.logging.LoggingHelper;
import adams.core.net.MimeTypeHelper;
import adams.core.option.AbstractOptionHandler;
import adams.core.option.OptionUtils;
import adams.core.option.WekaCommandLineHandler;
import adams.data.spreadsheet.LookUpHelper;
import adams.flow.control.StorageName;
import adams.flow.core.Actor;
import adams.flow.core.ActorUtils;
import adams.flow.core.CallableActorHelper;
import adams.flow.core.CallableActorReference;
import adams.flow.core.Compatibility;
import adams.flow.core.InputConsumer;
import adams.flow.core.OutputProducer;
import adams.flow.core.Token;
import adams.flow.core.WekaDatasetHelper;
import nz.ac.waikato.adams.webservice.weka.Attributes;
import nz.ac.waikato.adams.webservice.weka.Body;
import nz.ac.waikato.adams.webservice.weka.CrossValidateResponseObject;
import nz.ac.waikato.adams.webservice.weka.Dataset;
import nz.ac.waikato.adams.webservice.weka.DisplayClassifierResponseObject;
import nz.ac.waikato.adams.webservice.weka.DisplayClustererResponseObject;
import nz.ac.waikato.adams.webservice.weka.DownloadClassifierResponseObject;
import nz.ac.waikato.adams.webservice.weka.DownloadClustererResponseObject;
import nz.ac.waikato.adams.webservice.weka.Header;
import nz.ac.waikato.adams.webservice.weka.Instance;
import nz.ac.waikato.adams.webservice.weka.InstanceType;
import nz.ac.waikato.adams.webservice.weka.Instances;
import nz.ac.waikato.adams.webservice.weka.OptimizeReturnObject;
import nz.ac.waikato.adams.webservice.weka.PredictClassifierResponseObject;
import nz.ac.waikato.adams.webservice.weka.PredictClustererResponseObject;
import nz.ac.waikato.adams.webservice.weka.TestClassifierResponseObject;
import nz.ac.waikato.adams.webservice.weka.TrainClassifierResponseObject;
import nz.ac.waikato.adams.webservice.weka.TrainClustererResponseObject;
import nz.ac.waikato.adams.webservice.weka.TransformResponseObject;
import nz.ac.waikato.adams.webservice.weka.Type;
import nz.ac.waikato.adams.webservice.weka.WekaService;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.meta.MultiSearch;
import weka.classifiers.meta.multisearch.DefaultEvaluationMetrics;
import weka.clusterers.Clusterer;
import weka.core.SelectedTag;
import weka.core.setupgenerator.AbstractParameter;

import javax.activation.DataHandler;
import javax.mail.util.ByteArrayDataSource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Class that implements the weka web service.  
 *
 * @author msf8
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SimpleWekaService
  extends AbstractOptionHandler
  implements WekaService, OwnedByWekaServiceWS {

  /** for serialization. */
  private static final long serialVersionUID = -6102580694812360595L;

  public static final String PREFIX_CLASSIFIER = "classifier.";

  public static final String PREFIX_CLUSTERER = "clusterer.";

  /** web service object   */
  protected WekaServiceWS m_Owner;

  /** the name of the lookup table in the internal storage. */
  protected StorageName m_StorageName;
  
  /**
   * Default Constructor.
   * <br><br>
   * NB: the owning webservice needs to get set before using this implemention,
   * using the {@link #setOwner(WekaServiceWS)} method.
   */
  public SimpleWekaService() {
    super();
    setOwner(null);
  }

  /**
   * Returns a string for the GUI that describes this object.
   * 
   * @return		the description
   */
  @Override
  public String globalInfo() {
    return
      "Simple implementation of a WEKA webservice. Not multi-threaded.\n"
      + "Stores classifier models in look up table with prefix '" + PREFIX_CLASSIFIER + "' "
      + "and cluster models with prefix '" + PREFIX_CLUSTERER + "'.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "storage-name", "storageName",
      new StorageName("lookup"));
  }

  /**
   * Sets the name for the lookup table in the internal storage.
   *
   * @param value	the name
   */
  public void setStorageName(StorageName value) {
    m_StorageName = value;
    reset();
  }

  /**
   * Returns the name for the lookup table in the internal storage.
   *
   * @return		the name
   */
  public StorageName getStorageName() {
    return m_StorageName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String storageNameTipText() {
    return "The name for the lookup table in the internal storage.";
  }

  /**
   * Sets the owner of this webservice.
   * 
   * @param value	the owner
   */
  public void setOwner(WekaServiceWS value) {
    m_Owner = value;
    if ((m_Owner != null) && (m_Owner.getFlowContext() != null)) {
      if (!m_Owner.getFlowContext().getStorageHandler().getStorage().has(m_StorageName))
        throw new IllegalStateException("Lookup table for models not available: " + m_StorageName);
    }
  }
  
  /**
   * Returns the current owner of this webservice.
   * 
   * @return		the owner, null if none set
   */
  public WekaServiceWS getOwner() {
    return m_Owner;
  }

  /**
   * Stores the model in internal storage.
   *
   * @param name	the name of the model
   * @param model   	the model
   * @param classifier 	whether a classifier of clusterer
   */
  protected void store(String name, Object model, boolean classifier) {
    HashMap<String,Object> table;

    table = LookUpHelper.getTable(m_Owner.getFlowContext(), m_StorageName);
    table.put((classifier ? PREFIX_CLASSIFIER : PREFIX_CLUSTERER) + name, model);
  }

  /**
   * Retrieves the model from storage.
   *
   * @param name	the name of the model
   * @param classifier 	whether a classifier of clusterer
   * @return		the classifier, null if not found
   */
  protected Object retrieve(String name, boolean classifier) {
    HashMap<String,Object>	table;

    table = LookUpHelper.getTable(m_Owner.getFlowContext(), m_StorageName);
    return table.get((classifier ? PREFIX_CLASSIFIER : PREFIX_CLUSTERER) + name);
  }

  /**
   * Lists the models in storage.
   *
   * @param classifier 	whether to list classifiers of clusterers
   * @return		the list
   */
  protected List<String> list(boolean classifier) {
    List<String>		result;
    HashMap<String,Object>	table;

    result = new ArrayList<>();
    table  = LookUpHelper.getTable(m_Owner.getFlowContext(), m_StorageName);
    for (String key: table.keySet()) {
      if (classifier && key.startsWith(PREFIX_CLASSIFIER))
        result.add(key.substring(PREFIX_CLASSIFIER.length()));
      else if (!classifier && key.startsWith(PREFIX_CLUSTERER))
        result.add(key.substring(PREFIX_CLUSTERER.length()));
    }

    Collections.sort(result);

    return result;
  }

  /**
   * Performs training of a classifier and stores it in the model cache.
   * 
   * @param dataset	the dataset to use for training
   * @param classifier	the classifier setup
   * @param name	the identifier for the model
   * @return		null if OK, otherwise error message
   */
  @Override
  public TrainClassifierResponseObject trainClassifier(nz.ac.waikato.adams.webservice.weka.Dataset dataset,java.lang.String classifier,java.lang.String name) { 
    TrainClassifierResponseObject	result;
    weka.core.Instances	data;
    Classifier		cls;

    result = new TrainClassifierResponseObject();
    
    m_Owner.getLogger().info("training classifier");
    displayString(dataset);
    m_Owner.getLogger().info(dataset.toString());
    m_Owner.getLogger().info(classifier);
    m_Owner.getLogger().info(name);
    try {
      data = WekaDatasetHelper.toInstances(dataset);
      cls  = (Classifier) OptionUtils.forAnyCommandLine(Classifier.class, classifier);
      cls.buildClassifier(data);
      store(name, cls, true);
      result.setModel(cls.toString());
    } 
    catch (java.lang.Exception ex) {
      result.setErrorMessage(LoggingHelper.handleException(m_Owner, "Failed to train classifier: " + classifier, ex));
    }
    
    return result;
  }

  /**
   * Tests a previously trained model.
   * 
   * @param dataset	the dataset to use for testing
   * @param modelName	the name of the model to use
   * @return		the response
   */
  @Override
  public TestClassifierResponseObject testClassifier(nz.ac.waikato.adams.webservice.weka.Dataset dataset,java.lang.String modelName) { 
    TestClassifierResponseObject	result;
    Evaluation				eval;
    Classifier				cls;
    weka.core.Instances			data;

    result = new TestClassifierResponseObject();

    m_Owner.getLogger().info("testing classifier");
    displayString(dataset);
    m_Owner.getLogger().info(dataset.toString());
    m_Owner.getLogger().info(modelName);
    cls = (Classifier) retrieve(modelName, true);
    if (cls == null) {
      result.setErrorMessage("Failed to test model '" + modelName + "', as it is not available!");
      return result;
    }

    try {
      data = WekaDatasetHelper.toInstances(dataset);
      eval = new Evaluation(data);
      eval.evaluateModel(cls, data);
      result.setReturnDataset(WekaDatasetHelper.evaluationToDataset(eval));
    } 
    catch (java.lang.Exception ex) {
      result.setErrorMessage(LoggingHelper.handleException(m_Owner, "Failed to test model '" + modelName + "'!", ex));
    }

    return result;
  }

  /**
   * Cross-validates a classifier on a dataset.
   * 
   * @param dataset	the dataset to use for cross-validation
   * @param seed	the seed for randomizing the data
   * @param folds	the number of folds to use
   * @param classifier	the classifier setup
   * @return		the response
   */
  @Override
  public CrossValidateResponseObject crossValidateClassifier(nz.ac.waikato.adams.webservice.weka.Dataset dataset,int seed,int folds,java.lang.String classifier) { 
    CrossValidateResponseObject	result;
    Evaluation		eval;
    Classifier		cls;
    weka.core.Instances	data;

    result = new CrossValidateResponseObject();

    m_Owner.getLogger().info("cross-validation");
    displayString(dataset);
    m_Owner.getLogger().info(dataset.toString());
    m_Owner.getLogger().info("" + seed);
    m_Owner.getLogger().info("" + folds);
    m_Owner.getLogger().info(classifier);

    try {
      data = WekaDatasetHelper.toInstances(dataset);
      cls  = (Classifier) OptionUtils.forAnyCommandLine(Classifier.class, classifier);
      eval = new Evaluation(data);
      eval.crossValidateModel(cls, data, folds, new Random(seed));
      result.setReturnDataset(WekaDatasetHelper.evaluationToDataset(eval));
    } 
    catch (java.lang.Exception ex) {
      result.setErrorMessage(LoggingHelper.handleException(m_Owner, "Failed to cross-validate classifier '" + classifier + "'!", ex));
    }

    return result;
  }
  
  /**
   * Makes predictions using a previously generated model.
   * 
   * @param dataset	the data to use for the predictions
   * @param modelName	the model to use
   * @return		the response
   */
  @Override
  public PredictClassifierResponseObject predictClassifier(nz.ac.waikato.adams.webservice.weka.Dataset dataset,java.lang.String modelName) { 
    PredictClassifierResponseObject	result;
    weka.core.Instances		data;
    Classifier			cls;
    int				i;
    int				n;
    Dataset			pred;
    weka.core.Instance		inst;
    double 			classification;
    double[]			distribution;
    boolean			nominal;
    weka.core.Attribute		wAtt;
    Instance			in;

    result = new PredictClassifierResponseObject();

    m_Owner.getLogger().info("predicting using classifier");
    displayString(dataset);
    m_Owner.getLogger().info(dataset.toString());
    m_Owner.getLogger().info(modelName);
    cls = (Classifier) retrieve(modelName, true);

    // no model
    if (cls == null) {
      result.setErrorMessage("Failed to make predictions using classifier model '" + modelName + "', as it is not available!");
      return result;
    }

    data = WekaDatasetHelper.toInstances(dataset);
    // no class
    if (data.classIndex() == -1) {
      result.setErrorMessage("No class attribute set!");
      return result;
    }

    try {
      nominal = data.classAttribute().isNominal();
      wAtt    = data.classAttribute();
      pred    = new Dataset();
      result.setReturnDataset(pred);
      pred.setName("Predictions on '" + data.relationName() + "' using " + "'" + modelName + "'");
      pred.setVersion(WekaDatasetHelper.getDateFormat().format(new Date()));
      pred.setHeader(new Header());
      pred.getHeader().setAttributes(new Attributes());
      if (nominal) {
	WekaDatasetHelper.addAttribute(pred, "Classification", Type.STRING);
	for (i = 0; i < wAtt.numValues(); i++)
	  WekaDatasetHelper.addAttribute(pred, "Distribution (" + wAtt.value(i) + ")", Type.NUMERIC);
      }
      else {
	WekaDatasetHelper.addAttribute(pred, "Classification", Type.NUMERIC);
      }
      pred.setBody(new Body());
      pred.getBody().setInstances(new Instances());
      for (i = 0; i < data.numInstances(); i++) {
	inst = data.instance(i);
	inst.setClassMissing();
	in = new Instance();
	in.setInstanceType(InstanceType.NORMAL);
	in.setInstanceWeight(1.0);
	pred.getBody().getInstances().getInstance().add(in);
	if (nominal) {
	  classification = cls.classifyInstance(inst);
	  WekaDatasetHelper.addValue(in, 0, wAtt.value((int) classification));
	  distribution = cls.distributionForInstance(inst);
	  for (n = 0; n < distribution.length; n++)
	    WekaDatasetHelper.addValue(in, 1 + n, distribution[n]);
	}
	else {
	  classification = cls.classifyInstance(inst);
	  WekaDatasetHelper.addValue(in, 0, classification);
	}
      }
    } 
    catch (java.lang.Exception ex) {
      result.setErrorMessage(LoggingHelper.handleException(m_Owner, "Failed to make predictions with classifier model '" + modelName + "'!", ex));
    }

    return result;
  }

  /**
   * Downloads a previously generated model.
   * 
   * @param modelName	the model to download
   * @return		the response
   */
  @Override
  public DownloadClassifierResponseObject downloadClassifier(String modelName) {
    DownloadClassifierResponseObject	result;
    Classifier				cls;

    result = new DownloadClassifierResponseObject();

    m_Owner.getLogger().info("downloading classifier");
    m_Owner.getLogger().info(modelName);
    cls = (Classifier) retrieve(modelName, true);

    // no model
    if (cls == null) {
      result.setErrorMessage("No Classifier available named: " + modelName);
      return result;
    }
    
    try {
      result.setModelData(new DataHandler(new ByteArrayDataSource(SerializationHelper.toByteArray(cls), MimeTypeHelper.MIMETYPE_APPLICATION_OCTETSTREAM)));
    }
    catch (Exception e) {
      result.setErrorMessage(LoggingHelper.handleException(this, "Failed to serialize classifier: " + modelName, e));
    }
    
    return result;
  }

  /**
   * Downloads a previously generated model.
   * 
   * @param modelName	the model to download
   * @return		the response
   */
  @Override
  public DownloadClustererResponseObject downloadClusterer(String modelName) {
    DownloadClustererResponseObject	result;
    Clusterer 				cls;

    result = new DownloadClustererResponseObject();

    m_Owner.getLogger().info("downloading clusterer");
    m_Owner.getLogger().info(modelName);
    cls = (Clusterer) retrieve(modelName, false);

    // no model
    if (cls == null) {
      result.setErrorMessage("No Clusterer available named: " + modelName);
      return result;
    }
    
    try {
      result.setModelData(new DataHandler(new ByteArrayDataSource(SerializationHelper.toByteArray(cls), "application/octet-stream")));
    }
    catch (Exception e) {
      result.setErrorMessage(LoggingHelper.handleException(this, "Failed to serialize clusterer: " + modelName, e));
    }
    
    return result;
  }

  /**
   * Transforms a dataset using a callable actor on the server.
   * 
   * @param dataset	the data to transform
   * @param actorName	the callable actor to use
   * @return		the response with the transformed data or an error message
   */
  @Override
  public TransformResponseObject transform(nz.ac.waikato.adams.webservice.weka.Dataset dataset,java.lang.String actorName) { 
    TransformResponseObject	result;
    CallableActorHelper		helper;
    Actor 			callable;
    Compatibility		comp;
    weka.core.Instances		data;
    String			msg;
    Token			output;
    
    result = new TransformResponseObject();
    
    m_Owner.getLogger().info("transform");
    helper = new CallableActorHelper();
    callable = helper.findCallableActor(m_Owner.getFlowContext().getRoot(), new CallableActorReference(actorName));
    // not found
    if (callable == null) {
      result.setErrorMessage("Failed to find callable actor '" + actorName + "'!");
      return result;
    }
    // not a transformer
    if (!ActorUtils.isTransformer(callable)) {
      result.setErrorMessage("Callable actor '" + actorName + "' is not a transformer!");
      return result;
    }
    // wrong input/output
    comp = new Compatibility();
    if (!comp.isCompatible(new Class[]{weka.core.Instances.class}, ((InputConsumer) callable).accepts())) {
      result.setErrorMessage("Callable transformer '" + actorName + "' does not accept " + weka.core.Instances.class.getName() + "!");
      return result;
    }
    if (!comp.isCompatible(((OutputProducer) callable).generates(), new Class[]{weka.core.Instances.class})) {
      result.setErrorMessage("Callable transformer '" + actorName + "' does not generate " + weka.core.Instances.class.getName() + "!");
      return result;
    }
    data = WekaDatasetHelper.toInstances(dataset);
    
    try {
      synchronized(callable) {
	((InputConsumer) callable).input(new Token(data));
	msg = callable.execute();
	if (msg != null) {
	  result.setErrorMessage(msg);
	  return result;
	}
	else {
	  if (((OutputProducer) callable).hasPendingOutput()) {
	    output = ((OutputProducer) callable).output();
	    data   = (weka.core.Instances) output.getPayload();
	    result.setReturnDataset(WekaDatasetHelper.fromInstances(data));
	  }
	  else {
	    result.setErrorMessage("Callable transformer '" + actorName + "' did not produce any output!");
	    return result;
	  }
	}
      }
    } 
    catch (java.lang.Exception ex) {
      result.setErrorMessage(LoggingHelper.handleException(m_Owner, "Failed to transform data using callable transformer '" + actorName + "'!", ex));
    }
    
    return result;
  }
  
 
  

  /**
   * Trains a cluster algorithm.
   * 
   * @param dataset	the data to use
   * @param clusterer	the clusterer setup to use
   * @param modelName	the name to store the model under
   * @return		the response
   */
  @Override
  public TrainClustererResponseObject trainClusterer(nz.ac.waikato.adams.webservice.weka.Dataset dataset, String clusterer, String modelName) { 
    TrainClustererResponseObject	result;
    weka.core.Instances			data;
    Clusterer				cls;

    result = new TrainClustererResponseObject();
    
    m_Owner.getLogger().info("training clusterer");
    displayString(dataset);
    m_Owner.getLogger().info(dataset.toString());
    m_Owner.getLogger().info(clusterer);
    m_Owner.getLogger().info(modelName);
    try {
      data = WekaDatasetHelper.toInstances(dataset);
      cls  = (Clusterer) OptionUtils.forAnyCommandLine(Clusterer.class, clusterer);
      cls.buildClusterer(data);
      store(modelName, cls, false);
      result.setModel(cls.toString());
    } 
    catch (java.lang.Exception ex) {
      result.setErrorMessage(LoggingHelper.handleException(m_Owner, "Failed to train clusterer: " + clusterer, ex));
    }
    
    return result;
  }

  /**
   * Uses a previously built clusterer model to predict clusters for the
   * provided data.
   * 
   * @param dataset	the data to predict the clusters for
   * @param modelName	the name of the clusterer to use
   * @return		the predictions
   */
  @Override
  public PredictClustererResponseObject predictClusterer(nz.ac.waikato.adams.webservice.weka.Dataset dataset, String modelName) { 
    PredictClustererResponseObject	result;
    weka.core.Instances			data;
    Clusterer				cls;
    int					i;
    int					n;
    Dataset				pred;
    weka.core.Instance			inst;
    double 				cluster;
    double[]				distribution;
    Instance				in;
    int					numClusters;

    result = new PredictClustererResponseObject();

    m_Owner.getLogger().info("predicting using clusterer");
    displayString(dataset);
    m_Owner.getLogger().info(dataset.toString());
    m_Owner.getLogger().info(modelName);
    cls = (Clusterer) retrieve(modelName, false);

    // no model
    if (cls == null) {
      result.setErrorMessage("Failed to make predictions using clusterer model '" + modelName + "', as it is not available!");
      return result;
    }

    data = WekaDatasetHelper.toInstances(dataset);
    // class set
    if (data.classIndex() != -1) {
      result.setErrorMessage("Dataset cannot have class attribute set!");
      return result;
    }

    try {
      pred = new Dataset();
      result.setReturnDataset(pred);
      pred.setName("Predictions on '" + data.relationName() + "' using " + "'" + modelName + "'");
      pred.setVersion(WekaDatasetHelper.getDateFormat().format(new Date()));
      pred.setHeader(new Header());
      pred.getHeader().setAttributes(new Attributes());
      WekaDatasetHelper.addAttribute(pred, "Cluster", Type.NUMERIC);
      numClusters = cls.numberOfClusters();
      for (i = 0; i < numClusters; i++)
	WekaDatasetHelper.addAttribute(pred, "Cluster membership " + (i+1), Type.NUMERIC);
      pred.setBody(new Body());
      pred.getBody().setInstances(new Instances());
      for (i = 0; i < data.numInstances(); i++) {
	inst = data.instance(i);
	in = new Instance();
	in.setInstanceType(InstanceType.NORMAL);
	in.setInstanceWeight(1.0);
	pred.getBody().getInstances().getInstance().add(in);
	cluster = cls.clusterInstance(inst);
	WekaDatasetHelper.addValue(in, 0, cluster + 1);
	distribution = cls.distributionForInstance(inst);
	for (n = 0; n < distribution.length; n++)
	  WekaDatasetHelper.addValue(in, 1 + n, distribution[n]);
      }
    } 
    catch (java.lang.Exception ex) {
      result.setErrorMessage(LoggingHelper.handleException(m_Owner, "Failed to make predictions with model '" + modelName + "'!", ex));
    }

    return result;
  }

  /**
   * Returns the string representation of the specified classifier.
   * 
   * @param model	the model name to return the string representation for
   * @return		the response
   */
  @Override
  public DisplayClassifierResponseObject displayClassifier(java.lang.String model) { 
    DisplayClassifierResponseObject	result;
    Classifier				cls;
    
    m_Owner.getLogger().info("displaying classifier: " + model);

    result = new DisplayClassifierResponseObject();
    cls    = (Classifier) retrieve(model, true);
    if (cls != null) {
      result.setDisplayString(cls.toString());
    }
    else {
      result.setErrorMessage("Classifier model '" + model + "' not available!");
    }
    
    return result;
  }

  /**
   * Returns the string representation of the specified clusterer.
   * 
   * @param model	the model name to return the string representation for
   * @return		the response
   */
  @Override
  public DisplayClustererResponseObject displayClusterer(java.lang.String model) { 
    DisplayClustererResponseObject	result;
    Clusterer				cls;
    
    m_Owner.getLogger().info("displaying clusterer: " + model);

    result = new DisplayClustererResponseObject();
    cls    = (Clusterer) retrieve(model, false);
    if (cls != null) {
      result.setDisplayString(cls.toString());
    }
    else {
      result.setErrorMessage("Clusterer model '" + model + "' not available!");
    }
    
    return result;
  }

  /**
   * Returns a list of all classifier models currently stored on the server.
   * 
   * @return		the list of classifier models
   */
  @Override
  public java.util.List<java.lang.String> listClassifiers() { 
    List<String>	result;
    
    m_Owner.getLogger().info("listing classifiers");
    
    result = list(true);

    if (m_Owner.isLoggingEnabled())
      m_Owner.getLogger().info("current classifiers" + result);
    
    return result;
  }

  /**
   * Returns a list of all clusterer models currently stored on the server.
   * 
   * @return		the list of clusterer models
   */
  @Override
  public java.util.List<java.lang.String> listClusterers() { 
    List<String>	result;
    
    m_Owner.getLogger().info("listing clusterers");
    
    result = list(false);

    if (m_Owner.isLoggingEnabled())
      m_Owner.getLogger().info("current clusterers: " + result);
    
    return result;
  }
  
  /**
   * Optimizes the parameters of a base classifier using {@link MultiSearch}.
   * 
   * @param classifier		the base classifier
   * @param searchParameters	the search parameters for the optimization
   * @param dataset		the dataset to evaluate on
   * @param evaluation		the statistic to use for performance evaluation
   * @return			the result of the optimization
   */
  public OptimizeReturnObject optimizeClassifierMultiSearch(java.lang.String classifier,java.util.List<java.lang.String> searchParameters, Dataset dataset, String evaluation) { 
    OptimizeReturnObject	result;
    MultiSearch			search;
    WekaCommandLineHandler	handler;
    AbstractParameter[]		params;
    int				i;

    m_Owner.getLogger().info("optimizing classifiers using MultiSearch");

    result  = new OptimizeReturnObject();
    
    handler = new WekaCommandLineHandler();
    search  = new MultiSearch();
    params = new AbstractParameter[searchParameters.size()];
    for (i = 0; i < params.length; i++)
      params[i] = (AbstractParameter) handler.fromCommandLine(searchParameters.get(i));
    search.setSearchParameters(params);
    if (evaluation.equals("ACC"))
      search.setEvaluation(new SelectedTag(DefaultEvaluationMetrics.EVALUATION_ACC, new DefaultEvaluationMetrics().getTags()));
    else if (evaluation.equals("COMBINED"))
      search.setEvaluation(new SelectedTag(DefaultEvaluationMetrics.EVALUATION_COMBINED, new DefaultEvaluationMetrics().getTags()));
    else if (evaluation.equals("CC"))
      search.setEvaluation(new SelectedTag(DefaultEvaluationMetrics.EVALUATION_CC, new DefaultEvaluationMetrics().getTags()));
    else if (evaluation.equals("KAPPA"))
      search.setEvaluation(new SelectedTag(DefaultEvaluationMetrics.EVALUATION_KAPPA, new DefaultEvaluationMetrics().getTags()));
    else if (evaluation.equals("MAE"))
      search.setEvaluation(new SelectedTag(DefaultEvaluationMetrics.EVALUATION_MAE, new DefaultEvaluationMetrics().getTags()));
    else if (evaluation.equals("RAE"))
      search.setEvaluation(new SelectedTag(DefaultEvaluationMetrics.EVALUATION_RAE, new DefaultEvaluationMetrics().getTags()));
    else if (evaluation.equals("RMSE"))
      search.setEvaluation(new SelectedTag(DefaultEvaluationMetrics.EVALUATION_RMSE, new DefaultEvaluationMetrics().getTags()));
    else if (evaluation.equals("RRSE"))
      search.setEvaluation(new SelectedTag(DefaultEvaluationMetrics.EVALUATION_RRSE, new DefaultEvaluationMetrics().getTags()));
    else
      result.setErrorMessage("Unhandled evaluation: " + evaluation);
    search.setClassifier((weka.classifiers.Classifier) handler.fromCommandLine(classifier));

    if (result.getErrorMessage() == null) {
      try {
	search.buildClassifier(WekaDatasetHelper.toInstances(dataset));
	result.setBestClassifierSetup(handler.toCommandLine(search.getBestClassifier()));
      } 
      catch (java.lang.Exception ex) {
	result.setErrorMessage(LoggingHelper.handleException(m_Owner, "Failed to optimize classifier!", ex));
      }
    }
    
    return result;
  }
  
  /**
   * For outputting the dataset in debug mode.
   * 
   * @param dataset	the dataset to output
   */
  protected void displayString(Dataset dataset) {
    if (!m_Owner.isLoggingEnabled())
      return;
    m_Owner.getLogger().info("Number of instances: \t " + dataset.getBody().getInstances().getInstance().size());
    m_Owner.getLogger().info(WekaDatasetHelper.datasetToString(dataset));
  }
}