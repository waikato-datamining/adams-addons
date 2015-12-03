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
 * TwitterNLPPos.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package weka.filters.unsupervised.attribute;

import cmu.arktweetnlp.Tagger;
import cmu.arktweetnlp.Tagger.TaggedToken;
import weka.core.Attribute;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.RevisionUtils;
import weka.core.SparseInstance;
import weka.core.Utils;
import weka.core.WekaException;
import weka.core.WekaOptionUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

/**
 * Adds attributes based on POS tags generate by TweetNLP POS tagger.
 * Original code taken from <a href="https://github.com/felipebravom/TwitterSentLex/blob/master/src/weka/filters/unsupervised/attribute/TwitterNlpPos.java">here</a>
 *
 * @author Felipe Bravo
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TwitterNLPPos
  extends AbstractTweetContentFilter {

  private static final long serialVersionUID = -6908047935900687249L;

  public static final String MODEL = "model";

  public static final String POS_PREFIX = "POS-";

  /** the model to use. */
  protected File m_Model = getDefaultModel();

  /** the tagger. */
  protected transient Tagger m_Tagger;

  /** the vocabulary. */
  protected transient List<String> m_Vocabulary;

  /**
   * Returns a string describing this filter.
   *
   * @return a description of the filter suitable for displaying in the
   *         explorer/experimenter gui
   */
  @Override
  public String globalInfo() {
    return "A simple batch filter that adds attributes for all the "
      + "Twitter-oriented POS tags of the TwitterNLP library.\n\n"
      + "For more information see:\n"
      + "http://www.ark.cs.cmu.edu/TweetNLP/\n"
      + "Original code taken from here:\n"
      + "https://github.com/felipebravom/TwitterSentLex/blob/master/src/weka/filters/unsupervised/attribute/TwitterNlpPos.java";
  }

  /**
   * Returns an enumeration describing the available options.
   *
   * @return an enumeration of all the available options.
   */
  @Override
  public Enumeration listOptions() {
    Vector result = new Vector();
    WekaOptionUtils.addOption(result, modelTipText(), "" + getDefaultModel(), MODEL);
    WekaOptionUtils.add(result, super.listOptions());
    return WekaOptionUtils.toEnumeration(result);
  }

  /**
   * Sets the OptionHandler's options using the given list. All options
   * will be set (or reset) during this call (i.e. incremental setting
   * of options is not possible).
   *
   * @param options the list of options as an array of strings
   * @throws Exception if an option is not supported
   */
  @Override
  public void setOptions(String[] options) throws Exception {
    setModel(WekaOptionUtils.parse(options, MODEL, getDefaultModel()));
    super.setOptions(options);
  }

  /**
   * Gets the current option settings for the OptionHandler.
   *
   * @return the list of current option settings as an array of strings
   */
  @Override
  public String[] getOptions() {
    List<String> result = new ArrayList<>();
    WekaOptionUtils.add(result, MODEL, getModel());
    WekaOptionUtils.add(result, super.getOptions());
    return WekaOptionUtils.toArray(result);
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Tagger     = null;
    m_Vocabulary = null;
  }

  /**
   * Returns the default model file.
   *
   * @return		the default
   */
  protected File getDefaultModel() {
    return new File(".");
  }

  /**
   * Sets the model file to load and use.
   *
   * @param value	the model
   */
  public void setModel(File value) {
    m_Model = value;
    reset();
  }

  /**
   * Returns the model file to load and use.
   *
   * @return		the model
   */
  public File getModel() {
    return m_Model;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String modelTipText() {
    return "The model file to load and use.";
  }

  /**
   * Returns whether to allow the determineOutputFormat(Instances) method access
   * to the full dataset rather than just the header.
   *
   * @return whether determineOutputFormat has access to the full input dataset
   */
  public boolean allowAccessToFullInputFormat() {
    return true;
  }

  /**
   * Returns the Capabilities of this filter. Derived filters have to override
   * this method to enable capabilities.
   *
   * @return the capabilities of this object
   * @see Capabilities
   */
  @Override
  public Capabilities getCapabilities() {
    Capabilities	result;

    result = new Capabilities(this);
    result.enableAll();
    result.enable(Capability.NO_CLASS);
    result.disable(Capability.RELATIONAL_CLASS);
    result.disable(Capability.RELATIONAL_ATTRIBUTES);
    result.setMinimumNumberInstances(0);

    return result;
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
   */
  @Override
  protected Instances determineOutputFormat(Instances inputFormat) throws Exception {
    checkInputFormat(inputFormat);

    if (this.m_Tagger == null) {
      if (!m_Model.exists())
        throw new WekaException("Model file does not exist: " + m_Model);
      if (m_Model.isDirectory())
        throw new WekaException("Model file points to a directory: " + m_Model);
      m_Tagger = new Tagger();
      m_Tagger.loadModel(m_Model.getAbsolutePath());
      // build vocabulary
      m_Vocabulary = new ArrayList<>();
      for (int tag = 0; tag < m_Tagger.model.labelVocab.size(); tag++)
	m_Vocabulary.add(m_Tagger.model.labelVocab.name(tag));
      Collections.sort(m_Vocabulary);
    }

    ArrayList<Attribute> att = new ArrayList<Attribute>();

    // add all attributes of the inputformat
    for (int i = 0; i < inputFormat.numAttributes(); i++)
      att.add((Attribute) inputFormat.attribute(i).copy());

    for (int tag = 0; tag < m_Vocabulary.size(); tag++)
      att.add(new Attribute(POS_PREFIX + m_Vocabulary.get(tag)));

    Instances result = new Instances("Twitter Sentiment Analysis", att, 0);

    // set the class index
    result.setClassIndex(inputFormat.classIndex());

    return result;
  }

  /**
   * Processes the given data (may change the provided dataset) and returns the
   * modified version. This method is called in batchFinished().
   *
   * @param instances the data to process
   * @return the modified data
   * @throws Exception in case the processing goes wrong
   */
  @Override
  protected Instances process(Instances instances) throws Exception {
    Instances result = new Instances(getOutputFormat(), instances.numInstances());

    // reference to the content of the tweet
    Attribute attrCont = instances.attribute(m_AttributeName);

    for (int inst = 0; inst < instances.numInstances(); inst++) {
      Instance instance = instances.instance(inst);
      double[] values = new double[result.numAttributes()];
      for (int att = 0; att < instance.numAttributes(); att++) {
	if (instance.isMissing(att))
	  values[att] = Utils.missingValue();
	else if (instance.attribute(att).isString())
	  values[att] = result.attribute(att).addStringValue(instance.stringValue(att));
	else
	  values[att] = instance.value(att);
      }

      String content = instances.instance(inst).stringValue(attrCont);
      List<TaggedToken> tokens = m_Tagger.tokenizeAndTag(content);
      HashMap<String,Double> freq = new HashMap<>();
      for (int tag = 0; tag < m_Vocabulary.size(); tag++)
	freq.put(m_Vocabulary.get(tag), 0.0);
      for (TaggedToken token: tokens)
      	freq.put(token.tag, freq.get(token.tag) + 1.0 / tokens.size());

      // add POS values
      for (int tag = 0; tag < m_Vocabulary.size(); tag++) {
	int index = result.attribute(POS_PREFIX + m_Vocabulary.get(tag)).index();
	values[index] = freq.get(m_Vocabulary.get(tag));
      }

      instance = new SparseInstance(1, values);
      result.add(instance);
    }

    return result;
  }

  /**
   * Returns the revision string.
   *
   * @return		the revision
   */
  @Override
  public String getRevision() {
    return RevisionUtils.extract("$Revision: 4521 $");
  }

  /**
   * Main method for testing this class.
   *
   * @param args 	should contain arguments to the filter: use -h for help
   */
  public static void main(String[] args) {
    runFilter(new TwitterNLPPos(), args);
  }
}
