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
 * FromStorage.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.ml.dl4j.model;

import adams.data.InPlaceProcessing;
import adams.flow.control.StorageName;
import adams.flow.control.StorageUser;
import org.deeplearning4j.nn.api.Model;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Retrieves existing model simply from storage.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FromStorage
  extends AbstractModelConfigurator
  implements StorageUser, InPlaceProcessing {

  private static final long serialVersionUID = -5856765502127602083L;

  /** the name of the LRU cache. */
  protected String m_Cache;

  /** the name of the stored value. */
  protected StorageName m_StorageName;

  /** whether to skip creating a copy of the model. */
  protected boolean m_NoCopy;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Retrieves the model simply from storage and forwards it.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "cache", "cache",
	    "");

    m_OptionManager.add(
	    "storage-name", "storageName",
	    new StorageName());

    m_OptionManager.add(
	    "no-copy", "noCopy",
	    false);
  }

  /**
   * Sets the name of the LRU cache to use, regular storage if left empty.
   *
   * @param value	the cache
   */
  public void setCache(String value) {
    m_Cache = value;
    reset();
  }

  /**
   * Returns the name of the LRU cache to use, regular storage if left empty.
   *
   * @return		the cache
   */
  public String getCache() {
    return m_Cache;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String cacheTipText() {
    return "The name of the cache to retrieve the value from; uses the regular storage if left empty.";
  }

  /**
   * Sets the name of the stored value.
   *
   * @param value	the name
   */
  public void setStorageName(StorageName value) {
    m_StorageName = value;
    reset();
  }

  /**
   * Returns the name of the stored value.
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
    return "The name of the stored value to retrieve.";
  }

  /**
   * Sets whether to skip creating a copy of the spreadsheet before setting value.
   *
   * @param value	true if to skip creating copy
   */
  public void setNoCopy(boolean value) {
    m_NoCopy = value;
    reset();
  }

  /**
   * Returns whether to skip creating a copy of the spreadsheet before setting value.
   *
   * @return		true if copying is skipped
   */
  public boolean getNoCopy() {
    return m_NoCopy;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String noCopyTipText() {
    return "If enabled, no copy of the model is created before returning it.";
  }

  /**
   * Returns whether storage items are being used.
   *
   * @return		true if storage items are used
   */
  public boolean isUsingStorage() {
    return true;
  }

  /**
   * Hook method before configuring the model.
   *
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String check() {
    String	result;

    result = super.check();

    if (result == null) {
      if (!m_FlowContext.getStorageHandler().getStorage().has(m_StorageName))
	result = "Model not available from storage: " + m_StorageName;
    }

    return result;
  }

  /**
   * Configures the actual {@link Model} and returns it.
   *
   * @param numInput	the number of input nodes
   * @param numOutput	the number of output nodes
   * @return		the model
   */
  @Override
  protected Model doConfigureModel(int numInput, int numOutput) {
    Model			result;
    ByteArrayOutputStream	bos;
    ByteArrayInputStream	bis;

    if (m_Cache.length() == 0)
      result = (Model) m_FlowContext.getStorageHandler().getStorage().get(m_StorageName);
    else
      result = (Model) m_FlowContext.getStorageHandler().getStorage().get(m_Cache, m_StorageName);

    if (!m_NoCopy) {
      bos = new ByteArrayOutputStream();
      try {
        if (result instanceof MultiLayerNetwork) {
          if (!((MultiLayerNetwork) result).isInitCalled())
            ((MultiLayerNetwork) result).init();
        }
	ModelSerializer.writeModel(result, bos, true);
	bis = new ByteArrayInputStream(bos.toByteArray());
	if (result instanceof MultiLayerNetwork)
          result = ModelSerializer.restoreMultiLayerNetwork(bis);
	else if (result instanceof ComputationGraph)
          result = ModelSerializer.restoreComputationGraph(bis);
	else
	  throw new IllegalStateException("Unhandled model type: " + result.getClass().getName());
      }
      catch (Exception e) {
	throw new IllegalStateException("Failed to create copy of model!", e);
      }
    }

    return result;
  }
}
