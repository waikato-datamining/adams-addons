package weka.filters.supervised.attribute;

import adams.core.io.PlaceholderFile;
import adams.ml.dl4j.trainstopcriterion.MaxEpoch;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.FeedForwardLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import weka.classifiers.functions.DL4JMultiLayerNetwork;
import weka.core.Attribute;
import weka.core.Capabilities;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.RevisionUtils;
import weka.core.TechnicalInformation;
import weka.core.TechnicalInformationHandler;
import weka.core.Utils;
import weka.core.WekaOptionUtils;
import weka.dl4j.iterators.DefaultInstancesIterator;
import weka.filters.Filter;
import weka.filters.SimpleBatchFilter;
import weka.filters.SupervisedFilter;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

/**
 * Created by dale on 15/05/2017.
 */
public class DL4JMultiLayerNetworkFilter extends SimpleBatchFilter implements SupervisedFilter,
  TechnicalInformationHandler {

  /** for serialization */
  static final long serialVersionUID = -3335106965521265631L;

  // options
  protected static String SEED="seed";
  protected static String PRE_TRAINED="pre-trained";
  protected static String MODEL_FILE="model-file";
  protected static String LOG_FILE="log-file";
  protected static String EPOCHS="epochs";
  protected Filter m_Filter=null;

  protected DL4JMultiLayerNetwork m_model;
  protected boolean m_model_loaded =false;
  DefaultInstancesIterator m_Iterator = new DefaultInstancesIterator();

  protected int m_epochs=500;
  protected int m_seed=1;
  protected PlaceholderFile m_modelFile=new PlaceholderFile();
  protected PlaceholderFile m_logFile=new PlaceholderFile();
  protected boolean m_preTrained=true;

  /**
   * Load model from serialised file.
   * Return success/failure
   * @return
   */
  protected boolean loadModel(){
    if (!m_model_loaded) {
      try {
        m_model = (DL4JMultiLayerNetwork)adams.core.SerializationHelper.read(getModelFile().getAbsolutePath());
        m_Filter= m_model.getPreFilter();
        //m_model.setLogFile(new File(getLogFile().getAbsolutePath()));
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
  public DL4JMultiLayerNetworkFilter() {
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
  public PlaceholderFile getLogFile(){
    return(m_logFile);
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
  public void setLogFile(PlaceholderFile mf){
    m_logFile=mf;
  }
  public void setEpochs(int e){m_epochs=e;};

  //TipTexxt
  public String modelFileTipText() {return "Model file to load";}
  public String logFileTipText() {return "Log file for Weka Classifier";}
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

    WekaOptionUtils.addOption(result, logFileTipText(), "" + getLogFile(), LOG_FILE);
    WekaOptionUtils.addOption(result, modelFileTipText(), "" + getModelFile(), MODEL_FILE);
    WekaOptionUtils.addOption(result, preTrainedTipText(), ""+getPreTrained(), PRE_TRAINED);
    WekaOptionUtils.addOption(result, seedTipText(), ""+getSeed(), SEED);
    WekaOptionUtils.addOption(result, epochsTipText(), ""+getEpochs(), EPOCHS);


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
    WekaOptionUtils.add(result, EPOCHS, getEpochs());
    WekaOptionUtils.add(result, PRE_TRAINED, getPreTrained());
    WekaOptionUtils.add(result, MODEL_FILE, getModelFile());
    WekaOptionUtils.add(result, LOG_FILE, getLogFile());
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
    setEpochs((int) WekaOptionUtils.parse(options, EPOCHS, 500));
    setModelFile(new PlaceholderFile(WekaOptionUtils.parse(options, MODEL_FILE, new PlaceholderFile())));
    setLogFile(new PlaceholderFile(WekaOptionUtils.parse(options, LOG_FILE, new PlaceholderFile())));
    setPreTrained(Utils.getFlag(PRE_TRAINED,options));
    super.setOptions(options);
  }

  public int getUnitsFinalLayer(){
    MultiLayerNetwork mln=m_model.getMultiLayerNetwork();
    org.deeplearning4j.nn.api.Layer[] layers=mln.getLayers();//((MultiLayerNetwork)m_model).getLayers();
    int numLayers=layers.length;
    List<NeuralNetConfiguration> ll=mln.getLayerWiseConfigurations().getConfs();
    int numAtts =((FeedForwardLayer)ll.get(ll.size()-1).getLayer()).getNIn();
    return(numAtts);
  }

  public synchronized List<INDArray> getUnitScores(Instance i) throws Exception{
    //double[] values=new double[header.numAttributes()];
    Instance tf;
    MultiLayerNetwork mln=m_model.getMultiLayerNetwork();

    //synchronized (this) {

    //}
    Instances t_insts = new Instances(i.dataset(), 0);
    t_insts.add(i);
    if (m_Filter != null) {
      t_insts = Filter.useFilter(t_insts, m_Filter);
    }
    DataSet ds = m_Iterator.getIterator(t_insts, getSeed(), 1).next();

    List<INDArray> list_of_vals = mln.feedForward(ds.getFeatureMatrix());
    return(list_of_vals);
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
    int numAtts=getUnitsFinalLayer();

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
    result.enable(Capabilities.Capability.STRING_ATTRIBUTES);
    result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
    // class
    result.enable(Capabilities.Capability.NUMERIC_CLASS);
    result.enable(Capabilities.Capability.NOMINAL_CLASS);
    return result;
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
      MaxEpoch trainstop = new MaxEpoch();
      trainstop.setNumEpochs(getEpochs());
      m_model.setTrainStop(trainstop);
      m_model.buildClassifier(instances);
    }


    // restore original class values
    Instances header=getOutputFormat();
    result = new Instances(header, 0);

    for (int i = 0; i < instances.numInstances(); i++) {
      double[] values=new double[header.numAttributes()];
      List<INDArray> list_of_vals=getUnitScores(instances.get(i));
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
    runFilter(new DL4JMultiLayerNetworkFilter(), args);
  }
}

