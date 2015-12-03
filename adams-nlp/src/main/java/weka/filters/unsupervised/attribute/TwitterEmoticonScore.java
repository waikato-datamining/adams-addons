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
 * TwitterEmoticonScore.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package weka.filters.unsupervised.attribute;

import adams.core.io.FileUtils;
import weka.core.Attribute;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.RevisionUtils;
import weka.core.Utils;
import weka.core.WekaException;
import weka.core.WekaOptionUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * Computes the happiness score of tweets using the supplied emoticon lexicon model.
 * It adds numeric attribute for the overall score, cumulative scores for happy
 * and sad, count of neutral ones, and a nominal attribute (happy, neutral, sad).
 * The emoticon lexicon is required to have two columns: emoticon string and
 * associated score. The lexicon file itself is tab-separated and has no header.
 *
 * @author Felipe Bravo
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TwitterEmoticonScore
  extends AbstractTweetContentFilter {

  private static final long serialVersionUID = -6908047935900687249L;

  public static final String MODEL = "model";

  /** the numeric attribute. */
  public static final String ATT_SCORE_OVERALL = "HappinessScore-Overall";

  /** the numeric attribute prefix. */
  public static final String ATT_SCORE_PREFIX = "HappinessScore-";

  /** the nominal attribute. */
  public static final String ATT_LABEL = "HappinessLabel";

  /** the happy label. */
  public static final String LABEL_HAPPY = "happy";

  /** the neutral label. */
  public static final String LABEL_NEUTRAL = "neutral";

  /** the sad label. */
  public static final String LABEL_SAD = "sad";

  /** the model to use. */
  protected File m_Model = getDefaultModel();

  /** the scores. */
  protected transient Map<String,Double> m_Scores;

  /**
   * Returns a string describing this filter.
   *
   * @return a description of the filter suitable for displaying in the
   *         explorer/experimenter gui
   */
  @Override
  public String globalInfo() {
    return "Computes the happiness score of tweets using the supplied emoticon lexicon model.\n"
      + "It adds numeric attribute for the overall score, cumulative scores for happy "
      + "and sad, count of neutral ones, and a nominal attribute (happy, neutral, sad).\n"
      + "The emoticon lexicon is required to have two columns: emoticon string and\n"
      + "associated score. The lexicon file itself is tab-separated and has no header.\n";
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

    m_Scores = null;
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

    if (this.m_Scores == null) {
      if (!m_Model.exists())
        throw new WekaException("Model file does not exist: " + m_Model);
      if (m_Model.isDirectory())
        throw new WekaException("Model file points to a directory: " + m_Model);
      m_Scores = new HashMap<>();
      List<String> lines = FileUtils.loadFromFile(m_Model);
      for (String line: lines) {
        String[] parts = line.split("\t");
        if (parts.length >= 2) {
          try {
            double score = Double.parseDouble(parts[1]);
            m_Scores.put(parts[0], score);
          }
          catch (Exception e) {
            System.err.println("Failed to parse: " + line);
          }
        }
      }
      if (getDebug())
        System.out.println("# emoticons: " + m_Scores.size());
    }

    ArrayList<Attribute> att = new ArrayList<Attribute>();

    // add all attributes of the inputformat
    for (int i = 0; i < inputFormat.numAttributes(); i++)
      att.add((Attribute) inputFormat.attribute(i).copy());

    // add additional attributes
    att.add(new Attribute(ATT_SCORE_OVERALL));
    att.add(new Attribute(ATT_SCORE_PREFIX + LABEL_HAPPY));
    att.add(new Attribute(ATT_SCORE_PREFIX + LABEL_NEUTRAL));
    att.add(new Attribute(ATT_SCORE_PREFIX + LABEL_SAD));
    ArrayList<String> labels = new ArrayList<>();
    labels.add(LABEL_HAPPY);
    labels.add(LABEL_NEUTRAL);
    labels.add(LABEL_SAD);
    att.add(new Attribute(ATT_LABEL, labels));

    Instances result = new Instances("Happiness Score", att, 0);

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
      List<String> tokens = cmu.arktweetnlp.Twokenize.tokenizeRawTweetText(content);
      double overall = 0;
      Map<String,Double> scores = new HashMap<>();
      scores.put(LABEL_HAPPY,   0.0);
      scores.put(LABEL_NEUTRAL, 0.0);
      scores.put(LABEL_SAD,     0.0);
      for (String token: tokens) {
	if (!m_Scores.containsKey(token))
	  continue;
	double score = m_Scores.get(token);
	overall += score;
	if (score < 0)
	  scores.put(LABEL_SAD, scores.get(LABEL_SAD) + score);
	else if (score > 0)
	  scores.put(LABEL_HAPPY, scores.get(LABEL_HAPPY) + score);
	else
	  scores.put(LABEL_NEUTRAL, scores.get(LABEL_NEUTRAL) + 1.0);
      }

      values[result.attribute(ATT_SCORE_OVERALL).index()] = overall;
      values[result.attribute(ATT_SCORE_PREFIX + LABEL_HAPPY).index()] = scores.get(LABEL_HAPPY);
      values[result.attribute(ATT_SCORE_PREFIX + LABEL_NEUTRAL).index()] = scores.get(LABEL_NEUTRAL);
      values[result.attribute(ATT_SCORE_PREFIX + LABEL_SAD).index()] = scores.get(LABEL_SAD);
      String label;
      if (overall > 0)
	label = LABEL_HAPPY;
      else if (overall < 0)
	label = LABEL_SAD;
      else
        label = LABEL_NEUTRAL;
      values[result.attribute(ATT_LABEL).index()] = result.attribute(ATT_LABEL).indexOfValue(label);

      instance = new DenseInstance(1.0, values);
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
    runFilter(new TwitterEmoticonScore(), args);
  }
}
