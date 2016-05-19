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
 * ModelSerialization.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.ml.dl4j;

import adams.core.ClassLocator;
import adams.core.SerializationHelper;
import org.deeplearning4j.nn.api.Model;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.layers.BaseOutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.io.File;
import java.lang.reflect.Constructor;

/**
 * Helper class for model serialization and deserialization.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ModelSerialization {

  /**
   * Serializes a model to disk.
   *
   * @param file	the file to save the model to
   * @param model	the model to serialize
   * @throws Exception	if serialization fails
   */
  public static void write(File file, Model model) throws Exception {
    if (model instanceof MultiLayerNetwork) {
      SerializationHelper.writeAll(
	file.getAbsolutePath(),
	new Object[]{
	  model.getClass().getName(),
	  ((MultiLayerNetwork) model).getLayerWiseConfigurations().toJson(),
	  model.params()
	});
    }
    else {
      SerializationHelper.writeAll(
	file.getAbsolutePath(),
	new Object[]{
	  model.getClass().getName(),
	  model.conf().toJson(),
	  model.params()
	});
    }
  }

  /**
   * Deserializes a model from disk.
   *
   * @param file	the file to load the model from
   * @return		the model
   * @throws Exception	if deserializing failed
   */
  public static Model read(File file) throws Exception {
    Model 	result;
    Object[]	data;
    Class	cls;
    Constructor	constr;

    data = SerializationHelper.readAll(file.getAbsolutePath());
    if (data.length == 3) {
      cls  = Class.forName((String) data[0]);
      if (cls == MultiLayerNetwork.class) {
	result = new MultiLayerNetwork(MultiLayerConfiguration.fromJson((String) data[1]));
	((MultiLayerNetwork) result).init();
	result.setParams((INDArray) data[2]);
	return result;
      }
      else if (ClassLocator.isSubclass(BaseOutputLayer.class, cls)) {
	constr = cls.getConstructor(NeuralNetConfiguration.class);
	if (constr != null) {
	  result = (Model) constr.newInstance(NeuralNetConfiguration.fromJson((String) data[1]));
	  result.setParams((INDArray) data[2]);
	  return result;
	}
	else {
	  throw new IllegalStateException(
	    "Failed to find constructor in class " + cls.getName()
	      + " that takes a " + NeuralNetConfiguration.class.getName() + " object!");
	}
      }
      else {
	throw new IllegalStateException("Don't know how to re-instantiate: " + cls.getName());
      }
    }
    else {
      throw new IllegalStateException("Unexpected number of objects in serialized file (instead of 3): " + data.length);
    }
  }
}
