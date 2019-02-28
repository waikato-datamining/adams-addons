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

import adams.core.base.BaseHostname;
import adams.data.wekapyroproxy.AbstractCommunicationProcessor;
import adams.data.wekapyroproxy.JsonAttributeBlocksCommunicationProcessor;
import adams.env.Environment;
import net.razorvine.pyro.Config;
import net.razorvine.pyro.NameServerProxy;
import weka.classifiers.simple.AbstractSimpleClassifier;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Proxy for a python model using Pyro4 for communication.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class PyroProxy
  extends AbstractSimpleClassifier {

  private static final long serialVersionUID = -4578812400878994526L;

  /** the Pyro nameserver. */
  protected BaseHostname m_NameServer;

  /** the Pyro remote object. */
  protected String m_RemoteObjectName;

  /** the Pyro remote method. */
  protected String m_MethodName;

  /** the model name. */
  protected String m_ModelName;

  /** the instance converter to use. */
  protected AbstractCommunicationProcessor m_ModelProxy;

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
      + "For more information see:\n"
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
      "method-name", "methodName",
      "");

    m_OptionManager.add(
      "model-name", "modelName",
      "");

    m_OptionManager.add(
      "model-proxy", "modelProxy",
      new JsonAttributeBlocksCommunicationProcessor());
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
   * Sets the name of the method to call.
   *
   * @param value 	the name
   */
  public void setMethodName(String value) {
    m_MethodName = value;
    reset();
  }

  /**
   * Returns the name of the method to call.
   *
   * @return 		the name
   */
  public String getMethodName() {
    return m_MethodName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String methodNameTipText() {
    return "The name of the method to call.";
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
  public void setModelProxy(AbstractCommunicationProcessor value) {
    m_ModelProxy = value;
    reset();
  }

  /**
   * Returns the model proxy to use for communication.
   *
   * @return 		the proxy
   */
  public AbstractCommunicationProcessor getModelProxy() {
    return m_ModelProxy;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String modelProxyTipText() {
    return "The model proxy to use for communication with the remote model.";
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

    getCapabilities().testWithFail(data);

    if (m_RemoteObjectName.trim().isEmpty())
      throw new IllegalStateException("Remote object name is empty!");
    if (m_MethodName.trim().isEmpty())
      throw new IllegalStateException("Method name is empty!");
    if (m_ModelName.trim().isEmpty())
      throw new IllegalStateException("Model name is empty!");

    m_ModelProxy.initialize(this, data);

    // nameserver
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

    data       = m_ModelProxy.convertInstance(this, instance);
    start      = System.currentTimeMillis();
    prediction = m_RemoteObject.call(m_MethodName, data);
    end        = System.currentTimeMillis();
    if (isLoggingEnabled())
      getLogger().info("duration/distributionForInstance: " + ((double) (end - start) / 1000.0));

    return m_ModelProxy.parsePrediction(this, prediction);
  }

  /**
   * Returns a short description of the classifier.
   *
   * @return		the description
   */
  @Override
  public String toString() {
    return "Nameserver: " + m_NameServer + "\n"
      + "Remote object name: " + m_RemoteObjectName + "\n"
      + "Method name: " + m_MethodName + "\n"
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
