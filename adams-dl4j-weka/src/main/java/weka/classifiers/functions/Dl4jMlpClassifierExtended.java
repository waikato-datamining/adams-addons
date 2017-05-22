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
 *    Dl4jMlpClassifier.java
 *    Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 *
 */
package weka.classifiers.functions;

import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration.ListBuilder;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.FeedForwardLayer;
import org.deeplearning4j.nn.conf.layers.Layer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.optimize.api.IterationListener;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weka.classifiers.RandomizableClassifier;
import weka.classifiers.rules.ZeroR;
import weka.core.*;
import weka.dl4j.FileIterationListener;
import weka.dl4j.iterators.AbstractDataSetIterator;
import weka.dl4j.iterators.ConvolutionalInstancesIterator;
import weka.dl4j.iterators.DefaultInstancesIterator;
import weka.dl4j.iterators.ImageDataSetIterator;
import weka.dl4j.layers.DenseLayer;
import weka.dl4j.layers.OutputLayer;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NominalToBinary;
import weka.filters.unsupervised.attribute.Normalize;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;
import weka.filters.unsupervised.attribute.Standardize;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A wrapper for DeepLearning4j that can be used to train a multi-layer
 * perceptron using that library.
 *
 * @author Christopher Beckham
 * @author Eibe Frank
 *
 * @version $Revision: 11711 $
 */
public class Dl4jMlpClassifierExtended extends Dl4jMlpClassifier {

  public int getUnitsFinalLayer(){
    org.deeplearning4j.nn.api.Layer[] layers=((MultiLayerNetwork)m_model).getLayers();
    int numLayers=layers.length;
    List<NeuralNetConfiguration> ll=((MultiLayerNetwork)m_model).getLayerWiseConfigurations().getConfs();
    int numAtts =((FeedForwardLayer)ll.get(ll.size()-1).getLayer()).getNIn();
    return(numAtts);
  }

  public synchronized List<INDArray> getUnitScores(Instance i) throws Exception{
    //double[] values=new double[header.numAttributes()];
    Instance tf;
    //synchronized (this) {
      m_normalize.input(i);
      tf = m_normalize.output();
    //}
    Instances t_insts = new Instances(i.dataset(), 0);
    t_insts.add(tf);
    DataSet ds = getDataSetIterator().getIterator(t_insts, getSeed(), 1).next();

    List<INDArray> list_of_vals = ((MultiLayerNetwork)m_model).feedForward(ds.getFeatureMatrix());
    return(list_of_vals);
  }
}