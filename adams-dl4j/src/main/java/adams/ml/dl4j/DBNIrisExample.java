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
 * DBNIrisExample.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.ml.dl4j;

import org.apache.commons.io.FileUtils;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.deeplearning4j.datasets.iterator.impl.IrisDataSetIterator;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.api.Layer;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.conf.layers.RBM;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.params.DefaultParamInitializer;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.SplitTestAndTrain;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

public class DBNIrisExample {

  private static Logger log = LoggerFactory.getLogger(DBNIrisExample.class);

  public static void main(String[] args) throws Exception {
    // Customizing params
    Nd4j.MAX_SLICES_TO_PRINT = -1;
    Nd4j.MAX_ELEMENTS_PER_SLICE = -1;

    final int numRows = 4;
    final int numColumns = 1;
    int outputNum = 3;
    int numSamples = 150;
    int batchSize = 150;
    int iterations = 5;
    int splitTrainNum = (int) (batchSize * .8);
    int seed = 123;
    int listenerFreq = 1;

    log.info("Load data....");
    DataSetIterator iter = new IrisDataSetIterator(batchSize, numSamples);
    DataSet next = iter.next();
    next.shuffle();
    next.normalizeZeroMeanZeroUnitVariance();

    log.info("Split data....");
    SplitTestAndTrain testAndTrain = next.splitTestAndTrain(splitTrainNum, new Random(seed));
    DataSet train = testAndTrain.getTrain();
    DataSet test = testAndTrain.getTest();
    Nd4j.ENFORCE_NUMERICAL_STABILITY = true;

    log.info("Build model....");
    MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
      .seed(seed) // Locks in weight initialization for tuning
      .iterations(iterations) // # training iterations predict/classify & backprop
      .learningRate(1e-6f) // Optimization step size
      .optimizationAlgo(OptimizationAlgorithm.CONJUGATE_GRADIENT) // Backprop to calculate gradients
      .l1(1e-1).regularization(true).l2(2e-4)
      .useDropConnect(true)
      .list()
      .layer(0, new RBM.Builder(RBM.HiddenUnit.RECTIFIED, RBM.VisibleUnit.GAUSSIAN)
	  .nIn(numRows * numColumns) // # input nodes
	  .nOut(3) // # fully connected hidden layer nodes. Add list if multiple layers.
	  .weightInit(WeightInit.XAVIER) // Weight initialization
	  .k(1) // # contrastive divergence iterations
	  .activation("relu") // Activation function type
	  .lossFunction(LossFunctions.LossFunction.RMSE_XENT) // Loss function type
	  .updater(Updater.ADAGRAD)
	  .dropOut(0.5)
	  .build()
      ) // NN layer type
      .layer(1, new OutputLayer.Builder(LossFunctions.LossFunction.MCXENT)
	  .nIn(3) // # input nodes
	  .nOut(outputNum) // # output nodes
	  .activation("softmax")
	  .build()
      ) // NN layer type
      .build();
    MultiLayerNetwork model = new MultiLayerNetwork(conf);
    System.out.println(model.conf().toYaml());
    model.init();

    model.setListeners(new ScoreIterationListener(listenerFreq));
    log.info("Train model....");
    model.fit(train);

    log.info("Evaluate weights....");
    for(org.deeplearning4j.nn.api.Layer layer : model.getLayers()) {
      INDArray w = layer.getParam(DefaultParamInitializer.WEIGHT_KEY);
      log.info("Weights: " + w);
    }

    log.info("Evaluate model....");
    Evaluation eval = new Evaluation(outputNum);
    eval.eval(test.getLabels(), model.output(test.getFeatureMatrix(), Layer.TrainingMode.TEST));
    log.info(eval.stats());

    log.info("****************Example finished********************");

    OutputStream fos = Files.newOutputStream(Paths.get("coefficients.bin"));
    DataOutputStream dos = new DataOutputStream(fos);
    Nd4j.write(model.params(), dos);
    dos.flush();
    dos.close();
    FileUtils.writeStringToFile(new File("conf.json"), model.getLayerWiseConfigurations().toJson());

    MultiLayerConfiguration confFromJson = MultiLayerConfiguration.fromJson(FileUtils.readFileToString(new File("conf.json")));
    DataInputStream dis = new DataInputStream(new FileInputStream("coefficients.bin"));
    INDArray newParams = Nd4j.read(dis);
    dis.close();
    MultiLayerNetwork savedNetwork = new MultiLayerNetwork(confFromJson);
    savedNetwork.init();
    savedNetwork.setParams(newParams);
    System.out.println("Original network params " + model.params());
    System.out.println(savedNetwork.params());
  }
}