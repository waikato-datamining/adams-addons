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
 * PyroProxy.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package weka.classifiers.functions;

import adams.core.Utils;
import adams.core.base.BaseHostname;
import adams.data.wekapyroproxy.AbstractCommunicationProcessor;
import adams.data.wekapyroproxy.NullCommunicationProcessor;
import adams.env.Environment;
import adams.flow.core.Actor;
import adams.flow.core.ActorUtils;
import adams.flow.core.FlowContextHandler;
import adams.flow.standalone.PyroNameServer;
import net.razorvine.pyro.Config;
import net.razorvine.pyro.NameServerProxy;
import weka.classifiers.simple.AbstractSimpleClassifier;
import weka.core.BatchPredictor;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.PyroProxyObject;

/**
 * Proxy for a python model using Pyro4 for communication.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class PyroProxy
  extends AbstractSimpleClassifier
  implements PyroProxyObject, FlowContextHandler, BatchPredictor {

  private static final long serialVersionUID = -4578812400878994526L;

  /** the Pyro nameserver. */
  protected BaseHostname m_NameServer;

  /** the Pyro remote object. */
  protected String m_RemoteObjectName;

  /** the Pyro remote method for training. */
  protected String m_MethodNameTrain;

  /** the Pyro remote method for prediction. */
  protected String m_MethodNamePrediction;

  /** the model name. */
  protected String m_ModelName;

  /** the instance converter to use. */
  protected AbstractCommunicationProcessor m_Communication;

  /** whether to perform training. */
  protected boolean m_PerformTraining;

  /** the batch size. */
  protected int m_BatchSize;

  /** the flow context. */
  protected transient Actor m_FlowContext;

  /** the nameserver actor. */
  protected transient PyroNameServer m_NameServerActor;

  /** the nameserver. */
  protected transient NameServerProxy m_NameServerProxy;

  /** the remote object. */
  protected transient net.razorvine.pyro.PyroProxy m_RemoteObject;

  /**
   * Returns a string describing classifier.
   *
   * @return 		a description suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String globalInfo() {
    return
      "Proxy for a Python model using Pyro4 for communication.\n\n"
	+ "If a flow context is set and a " + Utils.classToString(PyroNameServer.class) + " "
	+ "can provide a Pyro NameServerProxy instance, then this will override "
	+ "the namerserver settings defined by the classifier.\n"
	+ "For more information see on Pyro:\n"
	+ "https://github.com/irmen/Pyro4";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "name-server", "nameServer",
      new BaseHostname("localhost:9090"));

    m_OptionManager.add(
      "remote-object-name", "remoteObjectName",
      "");

    m_OptionManager.add(
      "perform-training", "performTraining",
      false);

    m_OptionManager.add(
      "method-name-train", "methodNameTrain",
      "");

    m_OptionManager.add(
      "method-name-prediction", "methodNamePrediction",
      "");

    m_OptionManager.add(
      "model-name", "modelName",
      "");

    m_OptionManager.add(
      "communication", "communication",
      new NullCommunicationProcessor());

    m_OptionManager.add(
      "batch-size", "batchSize",
      "1");
  }

  /**
   * Sets the flow context.
   *
   * @param value the actor
   */
  public void setFlowContext(Actor value) {
    m_FlowContext = value;
  }

  /**
   * Returns the flow context, if any.
   *
   * @return the actor, null if none available
   */
  public Actor getFlowContext() {
    return m_FlowContext;
  }

  /**
   * Sets the address of the Pyro nameserver.
   *
   * @param value 	the address
   */
  public void setNameServer(BaseHostname value) {
    m_NameServer = value;
    reset();
  }

  /**
   * Returns the address of the Pyro nameserver.
   *
   * @return 		the address
   */
  public BaseHostname getNameServer() {
    return m_NameServer;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String nameServerTipText() {
    return "The address of the Pyro nameserver.";
  }

  /**
   * Sets the name of the remote object to use.
   *
   * @param value 	the name
   */
  public void setRemoteObjectName(String value) {
    m_RemoteObjectName = value;
    reset();
  }

  /**
   * Returns the name of the remote object to use.
   *
   * @return 		the name
   */
  public String getRemoteObjectName() {
    return m_RemoteObjectName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String remoteObjectNameTipText() {
    return "The name of the remote object to use.";
  }

  /**
   * Sets whether to train the model as well.
   *
   * @param value 	true if also train
   */
  public void setPerformTraining(boolean value) {
    m_PerformTraining = value;
    reset();
  }

  /**
   * Returns whether to train the model as well.
   *
   * @return 		true if also train
   */
  public boolean getPerformTraining() {
    return m_PerformTraining;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String performTrainingTipText() {
    return "If enabled, then training is performed.";
  }

  /**
   * Sets the name of the method to call for training.
   *
   * @param value 	the name
   */
  public void setMethodNameTrain(String value) {
    m_MethodNameTrain = value;
    reset();
  }

  /**
   * Returns the name of the method to call for training.
   *
   * @return 		the name
   */
  public String getMethodNameTrain() {
    return m_MethodNameTrain;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String methodNameTrainTipText() {
    return "The name of the method to call for training.";
  }

  /**
   * Sets the name of the method to call for predictions.
   *
   * @param value 	the name
   */
  public void setMethodNamePrediction(String value) {
    m_MethodNamePrediction = value;
    reset();
  }

  /**
   * Returns the name of the method to call for predictions.
   *
   * @return 		the name
   */
  public String getMethodNamePrediction() {
    return m_MethodNamePrediction;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String methodNamePredictionTipText() {
    return "The name of the method to call for predictions.";
  }

  /**
   * Sets the name of the model to use.
   *
   * @param value 	the name
   */
  public void setModelName(String value) {
    m_ModelName = value;
    reset();
  }

  /**
   * Returns the name of the model to use.
   *
   * @return 		the name
   */
  public String getModelName() {
    return m_ModelName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String modelNameTipText() {
    return "The name of the model to use.";
  }

  /**
   * Sets the model proxy to use for communication.
   *
   * @param value 	the proxy
   */
  public void setCommunication(AbstractCommunicationProcessor value) {
    m_Communication = value;
    reset();
  }

  /**
   * Returns the model proxy to use for communication.
   *
   * @return 		the proxy
   */
  public AbstractCommunicationProcessor getCommunication() {
    return m_Communication;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String communicationTipText() {
    return "Handles the communication with the remote model.";
  }

  /**
   * Set the batch size to use. The implementer will
   * prefer (but not necessarily expect) this many instances
   * to be passed in to distributionsForInstances().
   *
   * @param value the batch size to use
   */
  public void setBatchSize(String value) {
    int		intValue;

    intValue = Integer.parseInt(value);
    if (getOptionManager().isValid("batchSize", intValue)) {
      m_BatchSize = intValue;
      reset();
    }
  }

  /**
   * Get the batch size to use. The implementer will prefer (but not
   * necessarily expect) this many instances to be passed in to
   * distributionsForInstances(). Allows the preferred batch size
   * to be encapsulated with the client.
   *
   * @return the batch size to use
   */
  public String getBatchSize() {
    return "" + m_BatchSize;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String batchSizeTipText() {
    return "The batch size to use for generating multiple predictions (if possible).";
  }

  /**
   * Returns true if this BatchPredictor can generate batch predictions
   * in an efficient manner.
   *
   * @return true if batch predictions can be generated efficiently
   */
  public boolean implementsMoreEfficientBatchPrediction() {
    return m_Communication.supportsBatchPredictions();
  }

  /**
   * Returns the Capabilities of this classifier.
   *
   * @return the capabilities of this object
   * @see Capabilities
   */
  @Override
  public Capabilities getCapabilities() {
    Capabilities	result;

    result = new Capabilities(this);
    result.enable(Capability.NUMERIC_ATTRIBUTES);
    result.enable(Capability.STRING_ATTRIBUTES);
    result.enable(Capability.NUMERIC_CLASS);
    result.enable(Capability.MISSING_CLASS_VALUES);
    result.setMinimumNumberInstances(0);

    return result;
  }

  /**
   * Generates a classifier. Must initialize all fields of the classifier
   * that are not being set via options (ie. multiple calls of buildClassifier
   * must always lead to the same result). Must not change the dataset
   * in any way.
   *
   * @param data set of instances serving as training data
   * @throws Exception if the classifier has not been
   * generated successfully
   */
  @Override
  public void buildClassifier(Instances data) throws Exception {
    long	start;
    long	end;
    Object	train;

    getCapabilities().testWithFail(data);

    if (m_RemoteObjectName.trim().isEmpty())
      throw new IllegalStateException("Remote object name is empty!");
    if (m_PerformTraining && m_MethodNameTrain.trim().isEmpty())
      throw new IllegalStateException("Method name (train) is empty!");
    if (m_MethodNamePrediction.trim().isEmpty())
      throw new IllegalStateException("Method name (prediction) is empty!");
    if (m_ModelName.trim().isEmpty())
      throw new IllegalStateException("Model name is empty!");

    m_Communication.initialize(this, data);

    // nameserver from flow context
    m_NameServerProxy = null;
    m_NameServerActor = null;
    if (m_FlowContext != null) {
      if (isLoggingEnabled())
        getLogger().info("Using flow context (" + m_FlowContext.getFullName() + ") to determine nameserver...");
      m_NameServerActor = (PyroNameServer) ActorUtils.findClosestType(m_FlowContext, PyroNameServer.class, true);
      if (m_NameServerActor != null)
        m_NameServerProxy = m_NameServerActor.getNameServer();
      if (isLoggingEnabled())
        getLogger().info("Determined nameserver through flow context: " + (m_NameServerProxy != null));
    }

    // nameserver
    if (m_NameServerProxy == null) {
      try {
        if (isLoggingEnabled())
          getLogger().info("Connecting to: " + m_NameServer);
        start = System.currentTimeMillis();
        m_NameServerProxy = NameServerProxy.locateNS(
          m_NameServer.hostnameValue(), m_NameServer.portValue(Config.NS_PORT), null);
        end = System.currentTimeMillis();
        if (isLoggingEnabled())
          getLogger().info("duration/nameserver: " + ((double) (end - start) / 1000.0));
      }
      catch (Exception e) {
        throw new Exception("Failed to connect to Pyro nameserver: " + m_NameServer, e);
      }
    }

    // remoteobject
    try {
      if (isLoggingEnabled())
	getLogger().info("Obtaining remote object: " + m_RemoteObjectName);
      start = System.currentTimeMillis();
      m_RemoteObject = new net.razorvine.pyro.PyroProxy(m_NameServerProxy.lookup(m_RemoteObjectName));
      end = System.currentTimeMillis();
      if (isLoggingEnabled())
	getLogger().info("duration/remoteobject: " + ((double) (end - start) / 1000.0));
    }
    catch (Exception e) {
      throw new Exception("Failed to obtain remote object: " + m_RemoteObjectName, e);
    }

    if (m_PerformTraining) {
      train      = m_Communication.convertDataset(this, data);
      start      = System.currentTimeMillis();
      m_RemoteObject.call(m_MethodNameTrain, train);
      end        = System.currentTimeMillis();
      if (isLoggingEnabled())
	getLogger().info("duration/buildClassifier: " + ((double) (end - start) / 1000.0));
    }
  }

  /**
   * Classifies the given test instance. The instance has to belong to a dataset
   * when it's being classified.
   *
   * @param instance the instance to be classified
   * @return the predicted most likely class for the instance or
   *         Utils.missingValue() if no prediction is made
   * @throws Exception if an error occurred during the prediction
   */
  @Override
  public double[] distributionForInstance(Instance instance) throws Exception {
    Object	data;
    Object	prediction;
    long	start;
    long	end;

    if (m_RemoteObject == null)
      throw new IllegalStateException("No remote object available for remote calls!");

    data       = m_Communication.convertInstance(this, instance);
    start      = System.currentTimeMillis();
    prediction = m_RemoteObject.call(m_MethodNamePrediction, data);
    end        = System.currentTimeMillis();
    if (isLoggingEnabled())
      getLogger().info("duration/distributionForInstance: " + ((double) (end - start) / 1000.0));

    try {
      return m_Communication.parsePrediction(this, prediction);
    }
    catch (Exception e) {
      throw new Exception("Failed to process prediction:\n" + prediction, e);
    }
  }

  /**
   * Batch scoring method
   *
   * @param insts the instances to get predictions for
   * @return an array of probability distributions, one for each instance
   * @throws Exception if a problem occurs
   */
  public double[][] distributionsForInstances(Instances insts) throws Exception {
    Object	data;
    Object 	predictions;
    long	start;
    long	end;

    if (m_RemoteObject == null)
      throw new IllegalStateException("No remote object available for remote calls!");

    data        = m_Communication.convertDataset(this, insts);
    start       = System.currentTimeMillis();
    predictions = m_RemoteObject.call(m_MethodNamePrediction, data);
    end         = System.currentTimeMillis();
    if (isLoggingEnabled())
      getLogger().info("duration/distributionForInstance: " + ((double) (end - start) / 1000.0));

    try {
      return m_Communication.parsePredictions(this, predictions);
    }
    catch (Exception e) {
      throw new Exception("Failed to process predictions:\n" + predictions, e);
    }
  }

  /**
   * Returns a short description of the classifier.
   *
   * @return		the description
   */
  @Override
  public String toString() {
    return "Flow context: " + (m_FlowContext == null ? "-none-"  : m_FlowContext.getFullName()) + "\n"
      + "Nameserver: " + m_NameServer + "\n"
      + "Remote object name: " + m_RemoteObjectName + "\n"
      + "Perform training: " + m_PerformTraining + "\n"
      + "Method name (train): " + m_MethodNameTrain + "\n"
      + "Method name (prediction): " + m_MethodNamePrediction + "\n"
      + "Model name: " + m_ModelName + "\n"
      + "Connected: " + (m_RemoteObject != null);
  }

  /**
   * Main method for running this class.
   *
   * @param args the options
   */
  public static void main(String[] args) throws Exception {
    Environment.setEnvironmentClass(Environment.class);
    runClassifier(new PyroProxy(), args);
  }
}
