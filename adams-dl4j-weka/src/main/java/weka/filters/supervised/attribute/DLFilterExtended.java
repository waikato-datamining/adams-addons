package weka.filters.supervised.attribute;

import adams.core.io.PlaceholderFile;
import org.deeplearning4j.nn.api.Layer;
import org.deeplearning4j.nn.api.Model;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.FeedForwardLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import weka.classifiers.functions.Dl4jMlpClassifierExtended;
import weka.core.*;
import weka.dl4j.iterators.AbstractDataSetIterator;
import weka.dl4j.iterators.DefaultInstancesIterator;
import weka.filters.Filter;
import weka.filters.SimpleBatchFilter;
import weka.filters.SupervisedFilter;
import weka.filters.unsupervised.attribute.Normalize;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;
import weka.filters.unsupervised.attribute.Standardize;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.*;

/**
 * Created by dale on 15/05/2017.
 */
public class DLFilterExtended extends SimpleBatchFilter implements SupervisedFilter,
  TechnicalInformationHandler {

  /** for serialization */
  static final long serialVersionUID = -3335106965521265631L;

  // options
  protected static String SEED="seed";
  protected static String PRE_TRAINED="pre-trained";
  protected static String MODEL_FILE="model-file";
  protected static String EPOCHS="epochs";


  protected Dl4jMlpClassifierExtended m_model;
  protected boolean m_model_loaded =false;

  protected int m_epochs=500;
  protected int m_seed=1;
  protected PlaceholderFile m_modelFile=new PlaceholderFile();
  protected boolean m_preTrained=true;

  /** The dataset iterator to use. */
  protected AbstractDataSetIterator m_iterator =  new DefaultInstancesIterator();


  /**
   * Load model from serialised file.
   * Return success/failure
   * @return
   */
  protected boolean loadModel(){
    if (!m_model_loaded) {
      try {
        FileInputStream inputFileStream = new FileInputStream(getModelFile());
        ObjectInputStream objectInputStream = new ObjectInputStream(inputFileStream);
        m_model = (Dl4jMlpClassifierExtended) objectInputStream.readObject();
        m_model.setLogFile(new File("c:/tmp/logfile")); //TODO
        objectInputStream.close();
        inputFileStream.close();
        m_model_loaded = true;
      } catch (Exception e) {
        System.err.println(e.toString());
        return (false);
      }
      return (true);
    }
    return(true);
  }

  /**
   * default constructor
   */
  public DLFilterExtended() {
    super();

  }

  /**
   * Returns a string describing this classifier.
   *
   * @return a description of the classifier suitable for displaying in the
   *         explorer/experimenter gui
   */
  @Override
  public String globalInfo() {
    return "\n\n" + getTechnicalInformation().toString();
  }

  /**
   * Returns an instance of a TechnicalInformation object, containing detailed
   * information about the technical background of this class, e.g., paper
   * reference or book this class is based on.
   *
   * @return the technical information about this class
   */
  @Override
  public TechnicalInformation getTechnicalInformation() {
    TechnicalInformation result;
    TechnicalInformation additional;

    result = new TechnicalInformation(TechnicalInformation.Type.BOOK);

    return result;
  }


  // gets
  public boolean getPreTrained(){
    return(m_preTrained);
  }
  public int getSeed(){
    return(m_seed);
  }
  public PlaceholderFile getModelFile(){
    return(m_modelFile);
  }
  public int getEpochs() {return(m_epochs);};

  // sets
  public void setPreTrained(boolean b){
    m_preTrained=b;
  }
  public void setSeed(int seed){
    m_seed=seed;
  }
  public void setModelFile(PlaceholderFile mf){
    m_modelFile=mf;
  }
  public void setEpochs(int e){m_epochs=e;};

  //TipTexxt
  public String modelFileTipText() {return "Model file to load";}
  public String seedTipText() {
    return "Seed";
  }
  public String preTrainedTipText() {
    return "Is the model already built?";
  }
  public String epochsTipText(){return"epochs to train with if not pre-trained";};

  /**
   * Gets an enumeration describing the available options.
   *
   * @return an enumeration of all the available options.
   */
  @Override
  public Enumeration<Option> listOptions() {

    Vector result;

    result = new Vector();

    WekaOptionUtils.addOption(result, modelFileTipText(), "" + getModelFile(), MODEL_FILE);
    WekaOptionUtils.addOption(result, preTrainedTipText(), ""+getPreTrained(), PRE_TRAINED);
    WekaOptionUtils.addOption(result, seedTipText(), ""+getSeed(), SEED);

    // Serialised trained model - file
    // use pre-trained - boolean
    // DLWeka- classifier setup if training
    // Number of epochs after which to output trained model, num -1 for at end
    //

    WekaOptionUtils.add(result, super.listOptions());
    return WekaOptionUtils.toEnumeration(result);

  }

  /**
   * returns the options of the current setup
   *
   * @return the current options
   */
  @Override
  public String[] getOptions() {

    List<String> result = new ArrayList<>();
    WekaOptionUtils.add(result, SEED, getSeed());
    WekaOptionUtils.add(result, PRE_TRAINED, getPreTrained());
    WekaOptionUtils.add(result, MODEL_FILE, getModelFile());
    WekaOptionUtils.add(result, super.getOptions());
    return WekaOptionUtils.toArray(result);
  }

  /**
   * Parses the options for this object.
   * <p/>
   *
   * <!-- options-start --> Valid options are:
   * <p/>
   *
   * <pre>
   * -D
   *  Turns on output of debugging information.
   * </pre>
   *
   * <pre>
   * -C &lt;num&gt;
   *  The number of components to compute.
   *  (default: 20)
   * </pre>
   *
   * <pre>
   * -U
   *  Updates the class attribute as well.
   *  (default: off)
   * </pre>
   *
   * <pre>
   * -M
   *  Turns replacing of missing values on.
   *  (default: off)
   * </pre>
   *
   * <pre>
   * -A &lt;SIMPLS|PLS1&gt;
   *  The algorithm to use.
   *  (default: PLS1)
   * </pre>
   *
   * <pre>
   * -P &lt;none|center|standardize&gt;
   *  The type of preprocessing that is applied to the data.
   *  (default: center)
   * </pre>
   *
   * <!-- options-end -->
   *
   * @param options the options to use
   * @throws Exception if the option setting fails
   */
  @Override
  public void setOptions(String[] options) throws Exception {
    setSeed((int) WekaOptionUtils.parse(options, SEED, 1));
    setModelFile(new PlaceholderFile(WekaOptionUtils.parse(options, MODEL_FILE, new PlaceholderFile())));
    setPreTrained(Utils.getFlag(PRE_TRAINED,options));
    super.setOptions(options);
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
  protected Instances determineOutputFormat(Instances inputFormat)
    throws Exception {
    loadModel();
    // generate header
    ArrayList<Attribute> atts = new ArrayList<Attribute>();
    int numAtts=m_model.getUnitsFinalLayer();

    String prefix = "unit";
    for (int i = 0; i < numAtts; i++) {
      atts.add(new Attribute(prefix + "_" + (i + 1)));
    }
    atts.add(new Attribute("Class"));
    Instances result = new Instances(prefix, atts, 0);
    result.setClassIndex(result.numAttributes() - 1);

    return result;
  }


  /**
   * Returns the Capabilities of this filter.
   *
   * @return the capabilities of this object
   * @see Capabilities
   */
  @Override
  public Capabilities getCapabilities() {
    Capabilities result = super.getCapabilities();
    result.disableAll();

    // attributes
    result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
    result.enable(Capabilities.Capability.DATE_ATTRIBUTES);
    result.enable(Capabilities.Capability.MISSING_VALUES);

    // class
    result.enable(Capabilities.Capability.NUMERIC_CLASS);

    return result;
  }

  @OptionMetadata(description = "The dataset iterator to use",
    displayName = "dataset iterator", commandLineParamName = "iterator",
    commandLineParamSynopsis = "-iterator <string>", displayOrder = 6)

  public AbstractDataSetIterator getDataSetIterator() {
    return m_iterator;
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
    Instances result;
    double clsValue;
    double[] clsValues;

    result = null;

    loadModel();

    if (!getPreTrained() && !isFirstBatchDone()){
      // train
      m_model.setNumEpochs(getEpochs());
      m_model.buildClassifier(instances);
    }


    //Iterator<DataSet> it=getDataSetIterator().getIterator(instances, getSeed(), 1);
    //while(it.hasNext()){

//    }
    // restore original class values
    Instances header=getOutputFormat();
    result = new Instances(header, 0);

    for (int i = 0; i < instances.numInstances(); i++) {
      double[] values=new double[header.numAttributes()];
      List<INDArray> list_of_vals=m_model.getUnitScores(instances.get(i));
      // break here to see what comes out of this!!
      INDArray ia=list_of_vals.get(list_of_vals.size()-2);
      for (int j=0;j<ia.length();j++){
        values[j]=ia.getDouble(j);
      }
      values[values.length-1]=instances.get(i).classValue();
      result.add(new DenseInstance(1.0, values));
    }

    return result;
  }

  /**
   * Returns the revision string.
   *
   * @return the revision
   */
  @Override
  public String getRevision() {
    return RevisionUtils.extract("$Revision: 10364 $");
  }

  /**
   * runs the filter with the given arguments.
   *
   * @param args the commandline arguments
   */
  public static void main(String[] args) {
    runFilter(new DLFilterExtended(), args);
  }
}

