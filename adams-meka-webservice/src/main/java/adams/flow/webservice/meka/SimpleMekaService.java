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
 * SimpleMekaService.java
 * Copyright (C) 2013-2019 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.webservice.meka;

import adams.core.SerializationHelper;
import adams.core.logging.LoggingHelper;
import adams.core.net.MimeTypeHelper;
import adams.core.option.AbstractOptionHandler;
import adams.core.option.OptionUtils;
import adams.data.spreadsheet.LookUpHelper;
import adams.flow.control.StorageName;
import adams.flow.core.Actor;
import adams.flow.core.ActorUtils;
import adams.flow.core.CallableActorHelper;
import adams.flow.core.CallableActorReference;
import adams.flow.core.Compatibility;
import adams.flow.core.InputConsumer;
import adams.flow.core.MekaDatasetHelper;
import adams.flow.core.OutputProducer;
import adams.flow.core.Token;
import meka.classifiers.multilabel.Evaluation;
import meka.core.MLUtils;
import nz.ac.waikato.adams.webservice.meka.Attributes;
import nz.ac.waikato.adams.webservice.meka.Body;
import nz.ac.waikato.adams.webservice.meka.CrossValidateResponseObject;
import nz.ac.waikato.adams.webservice.meka.Dataset;
import nz.ac.waikato.adams.webservice.meka.DisplayClassifierResponseObject;
import nz.ac.waikato.adams.webservice.meka.DownloadClassifierResponseObject;
import nz.ac.waikato.adams.webservice.meka.Header;
import nz.ac.waikato.adams.webservice.meka.Instance;
import nz.ac.waikato.adams.webservice.meka.InstanceType;
import nz.ac.waikato.adams.webservice.meka.Instances;
import nz.ac.waikato.adams.webservice.meka.MekaService;
import nz.ac.waikato.adams.webservice.meka.PredictClassifierResponseObject;
import nz.ac.waikato.adams.webservice.meka.TestClassifierResponseObject;
import nz.ac.waikato.adams.webservice.meka.TrainClassifierResponseObject;
import nz.ac.waikato.adams.webservice.meka.TransformResponseObject;
import nz.ac.waikato.adams.webservice.meka.Type;

import javax.activation.DataHandler;
import javax.mail.util.ByteArrayDataSource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

/**
 * Class that implements the meka web service.  
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class SimpleMekaService
  extends AbstractOptionHandler
  implements MekaService, OwnedByMekaServiceWS {

  /** for serialization. */
  private static final long serialVersionUID = -6102580694812360595L;

  /** web service object   */
  protected MekaServiceWS m_Owner;

  /** the name of the lookup table in the internal storage. */
  protected StorageName m_StorageName;

  /**
   * Default Constructor.
   * <br><br>
   * NB: the owning webservice needs to get set before using this implemention,
   * using the {@link #setOwner(MekaServiceWS)} method.
   */
  public SimpleMekaService() {
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
    return "Simple implementation of a WEKA webservice. Not multi-threaded.";
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
  public void setOwner(MekaServiceWS value) {
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
  public MekaServiceWS getOwner() {
    return m_Owner;
  }

  /**
   * Stores the classifier in internal storage.
   *
   * @param name	the name of the classifier
   * @param cls		the classifier
   */
  protected void store(String name, meka.classifiers.multilabel.MultiLabelClassifier cls) {
    HashMap<String,Object>	table;

    table = LookUpHelper.getTable(m_Owner.getFlowContext(), m_StorageName);
    table.put(name, cls);
  }

  /**
   * Retrieves the classifier from storage.
   *
   * @param name	the name of the classifier
   * @return		the classifier, null if not found
   */
  protected meka.classifiers.multilabel.MultiLabelClassifier retrieve(String name) {
    HashMap<String,Object>	table;

    table = LookUpHelper.getTable(m_Owner.getFlowContext(), m_StorageName);
    return (meka.classifiers.multilabel.MultiLabelClassifier) table.get(name);
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
  public TrainClassifierResponseObject trainClassifier(nz.ac.waikato.adams.webservice.meka.Dataset dataset,java.lang.String classifier,java.lang.String name) { 
    TrainClassifierResponseObject			result;
    weka.core.Instances					data;
    meka.classifiers.multilabel.MultiLabelClassifier	cls;

    result = new TrainClassifierResponseObject();
    
    m_Owner.getLogger().info("training classifier");
    displayString(dataset);
    m_Owner.getLogger().info(dataset.toString());
    m_Owner.getLogger().info(classifier);
    m_Owner.getLogger().info(name);
    try {
      data = MekaDatasetHelper.toInstances(dataset);
      cls  = (meka.classifiers.multilabel.MultiLabelClassifier) OptionUtils.forAnyCommandLine(meka.classifiers.multilabel.MultiLabelClassifier.class, classifier);
      cls.buildClassifier(data);
      store(name, cls);
      result.setModel(classifier + "\n" + cls.getModel());
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
  public TestClassifierResponseObject testClassifier(nz.ac.waikato.adams.webservice.meka.Dataset dataset,java.lang.String modelName) { 
    TestClassifierResponseObject			result;
    meka.classifiers.multilabel.MultiLabelClassifier	cls;
    weka.core.Instances					data;
    meka.core.Result					res;

    result = new TestClassifierResponseObject();

    m_Owner.getLogger().info("testing classifier");
    displayString(dataset);
    m_Owner.getLogger().info(dataset.toString());
    m_Owner.getLogger().info(modelName);
    cls = retrieve(modelName);
    if (cls == null) {
      result.setErrorMessage("Failed to test model '" + modelName + "', as it is not available from storage!");
      return result;
    }

    try {
      data = MekaDatasetHelper.toInstances(dataset);
      res   = Evaluation.evaluateModel(cls, data, "0.0", "3");
      result.setReturnDataset(MekaDatasetHelper.evaluationToDataset(res));
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
  public CrossValidateResponseObject crossValidateClassifier(nz.ac.waikato.adams.webservice.meka.Dataset dataset,int seed,int folds,java.lang.String classifier) { 
    CrossValidateResponseObject				result;
    meka.classifiers.multilabel.MultiLabelClassifier	cls;
    weka.core.Instances					data;
    meka.core.Result					res;

    result = new CrossValidateResponseObject();

    m_Owner.getLogger().info("cross-validation");
    displayString(dataset);
    m_Owner.getLogger().info(dataset.toString());
    m_Owner.getLogger().info("" + seed);
    m_Owner.getLogger().info("" + folds);
    m_Owner.getLogger().info(classifier);

    try {
      data = MekaDatasetHelper.toInstances(dataset);
      data.randomize(new Random(seed));
      cls  = (meka.classifiers.multilabel.MultiLabelClassifier) OptionUtils.forAnyCommandLine(meka.classifiers.multilabel.MultiLabelClassifier.class, classifier);
      res  = Evaluation.cvModel(cls, data, folds, "PCut1", "3");
      result.setReturnDataset(MekaDatasetHelper.evaluationToDataset(res));
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
  public PredictClassifierResponseObject predictClassifier(nz.ac.waikato.adams.webservice.meka.Dataset dataset,java.lang.String modelName) { 
    PredictClassifierResponseObject			result;
    weka.core.Instances					data;
    meka.classifiers.multilabel.MultiLabelClassifier	cls;
    int							i;
    int							n;
    Dataset						pred;
    weka.core.Instance					inst;
    double[]						distribution;
    Instance						in;

    result = new PredictClassifierResponseObject();

    m_Owner.getLogger().info("predicting using classifier");
    displayString(dataset);
    m_Owner.getLogger().info(dataset.toString());
    m_Owner.getLogger().info(modelName);
    cls = retrieve(modelName);

    // no model
    if (cls == null) {
      result.setErrorMessage("Failed to make predictions using classifier model '" + modelName + "', as it is not available from storage!");
      return result;
    }

    data = MekaDatasetHelper.toInstances(dataset);

    try {
      MLUtils.prepareData(data);
      pred = new Dataset();
      result.setReturnDataset(pred);
      pred.setName("Predictions on '" + data.relationName() + "' using " + "'" + modelName + "'");
      pred.setVersion(MekaDatasetHelper.getDateFormat().format(new Date()));
      pred.setHeader(new Header());
      pred.getHeader().setAttributes(new Attributes());
      for (i = 0; i < data.classIndex(); i++)
	MekaDatasetHelper.addAttribute(pred, "Distribution (" + data.attribute(i).name() + ")", Type.NUMERIC);
      pred.setBody(new Body());
      pred.getBody().setInstances(new Instances());
      for (i = 0; i < data.numInstances(); i++) {
	inst = data.instance(i);
	inst.setClassMissing();
	in = new Instance();
	in.setInstanceType(InstanceType.NORMAL);
	in.setInstanceWeight(1.0);
	pred.getBody().getInstances().getInstance().add(in);
	distribution = cls.distributionForInstance(inst);
	for (n = 0; n < distribution.length; n++)
	  MekaDatasetHelper.addValue(in, n, distribution[n]);
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
    DownloadClassifierResponseObject			result;
    meka.classifiers.multilabel.MultiLabelClassifier	cls;

    result = new DownloadClassifierResponseObject();

    m_Owner.getLogger().info("downloading classifier");
    m_Owner.getLogger().info(modelName);
    cls = retrieve(modelName);

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
   * Transforms a dataset using a callable actor on the server.
   * 
   * @param dataset	the data to transform
   * @param actorName	the callable actor to use
   * @return		the response with the transformed data or an error message
   */
  @Override
  public TransformResponseObject transform(nz.ac.waikato.adams.webservice.meka.Dataset dataset,java.lang.String actorName) { 
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
    data = MekaDatasetHelper.toInstances(dataset);
    
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
	    result.setReturnDataset(MekaDatasetHelper.fromInstances(data));
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
   * Returns the string representation of the specified classifier.
   * 
   * @param model	the model name to return the string representation for
   * @return		the response
   */
  @Override
  public DisplayClassifierResponseObject displayClassifier(java.lang.String model) { 
    DisplayClassifierResponseObject			result;
    meka.classifiers.multilabel.MultiLabelClassifier	cls;

    m_Owner.getLogger().info("displaying classifier: " + model);

    result = new DisplayClassifierResponseObject();
    cls    = retrieve(model);
    if (cls != null) {
      result.setDisplayString(cls.getModel());
    }
    else {
      result.setErrorMessage("Classifier model '" + model + "' not available from storage!");
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
    ArrayList<String>		result;
    HashMap<String,Object>	table;

    m_Owner.getLogger().info("listing classifiers");
    
    table  = LookUpHelper.getTable(m_Owner.getFlowContext(), m_StorageName);
    result = new ArrayList<>(table.keySet());
    Collections.sort(result);

    if (m_Owner.isLoggingEnabled())
      m_Owner.getLogger().info("current classifiers" + result);
    
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
    m_Owner.getLogger().info(MekaDatasetHelper.datasetToString(dataset));
  }
}
