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
 * WordFrequencyAnalyzer.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.base.BaseCharset;
import adams.core.io.EncodingSupporter;
import adams.flow.control.StorageName;
import adams.flow.core.Token;
import com.kennycason.kumo.WordFrequency;
import com.kennycason.kumo.nlp.FrequencyAnalyzer;
import com.kennycason.kumo.nlp.normalize.Normalizer;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Generates a word frequency analyzer from the incoming text.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;com.kennycason.kumo.WordFrequency[]<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: WordFrequencyAnalyzer
 * </pre>
 *
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow execution at this level gets stopped in case this
 * &nbsp;&nbsp;&nbsp;actor encounters an error; the error gets propagated; useful for critical
 * &nbsp;&nbsp;&nbsp;actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-encoding &lt;adams.core.base.BaseCharset&gt; (property: encoding)
 * &nbsp;&nbsp;&nbsp;The type of encoding to use when writing to the file, use empty string for
 * &nbsp;&nbsp;&nbsp;default.
 * &nbsp;&nbsp;&nbsp;default: Default
 * </pre>
 *
 * <pre>-normalizer &lt;com.kennycason.kumo.nlp.normalize.Normalizer&gt; [-normalizer ...] (property: normalizers)
 * &nbsp;&nbsp;&nbsp;The normalizers to use.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-min-word-length &lt;int&gt; (property: minWordLength)
 * &nbsp;&nbsp;&nbsp;The minimum length for words.
 * &nbsp;&nbsp;&nbsp;default: 3
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 * <pre>-max-word-length &lt;int&gt; (property: maxWordLength)
 * &nbsp;&nbsp;&nbsp;The maximum length for words.
 * &nbsp;&nbsp;&nbsp;default: 32
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 * <pre>-num-frequencies &lt;int&gt; (property: numFrequencies)
 * &nbsp;&nbsp;&nbsp;The number of frequencies to return.
 * &nbsp;&nbsp;&nbsp;default: 50
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 * <pre>-stopwords &lt;adams.flow.control.StorageName&gt; (property: stopwords)
 * &nbsp;&nbsp;&nbsp;The storage item that holds the string array of stopwords to use.
 * &nbsp;&nbsp;&nbsp;default: storage
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class WordFrequencyAnalyzer
  extends AbstractTransformer
  implements EncodingSupporter {

  private static final long serialVersionUID = 8328022277704245871L;

  /** the encoding to use. */
  protected BaseCharset m_Encoding;

  /** the normalizers to use. */
  protected Normalizer[] m_Normalizers;

  /** the min word length. */
  protected int m_MinWordLength;

  /** the max word length. */
  protected int m_MaxWordLength;
  
  /** the number of requencies to return. */
  protected int m_NumFrequencies;

  /** the stopwords to retrieve from storage. */
  protected StorageName m_Stopwords;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates a word frequency analyzer from the incoming text.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "encoding", "encoding",
      new BaseCharset());

    m_OptionManager.add(
      "normalizer", "normalizers",
      new Normalizer[0]);

    m_OptionManager.add(
      "min-word-length", "minWordLength",
      FrequencyAnalyzer.DEFAULT_WORD_MIN_LENGTH, 1, null);

    m_OptionManager.add(
      "max-word-length", "maxWordLength",
      FrequencyAnalyzer.DEFAULT_WORD_MAX_LENGTH, 1, null);

    m_OptionManager.add(
      "num-frequencies", "numFrequencies",
      FrequencyAnalyzer.DEFAULT_WORD_FREQUENCIES_TO_RETURN, 1, null);

    m_OptionManager.add(
      "stopwords", "stopwords",
      new StorageName());
  }

  /**
   * Sets the encoding to use.
   *
   * @param value	the encoding, e.g. "UTF-8" or "UTF-16", empty string for default
   */
  public void setEncoding(BaseCharset value) {
    m_Encoding = value;
    reset();
  }

  /**
   * Returns the encoding to use.
   *
   * @return		the encoding, e.g. "UTF-8" or "UTF-16", empty string for default
   */
  public BaseCharset getEncoding() {
    return m_Encoding;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String encodingTipText() {
    return "The type of encoding to use when writing to the file, use empty string for default.";
  }

  /**
   * Sets the normalizers to use.
   *
   * @param value	the normalizers
   */
  public void setNormalizers(Normalizer[] value) {
    m_Normalizers = value;
    reset();
  }

  /**
   * Returns the normalizers to use.
   *
   * @return		the normalizers
   */
  public Normalizer[] getNormalizers() {
    return m_Normalizers;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String normalizersTipText() {
    return "The normalizers to use.";
  }

  /**
   * Sets the minimum length for words.
   *
   * @param value	the minimum
   */
  public void setMinWordLength(int value) {
    if (getOptionManager().isValid("minWordLength", value)) {
      m_MinWordLength = value;
      reset();
    }
  }

  /**
   * Returns the minimum length for words.
   *
   * @return		the minimum
   */
  public int getMinWordLength() {
    return m_MinWordLength;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String minWordLengthTipText() {
    return "The minimum length for words.";
  }

  /**
   * Sets the maximum length for words.
   *
   * @param value	the maximum
   */
  public void setMaxWordLength(int value) {
    if (getOptionManager().isValid("maxWordLength", value)) {
      m_MaxWordLength = value;
      reset();
    }
  }

  /**
   * Returns the maximum length for words.
   *
   * @return		the maximum
   */
  public int getMaxWordLength() {
    return m_MaxWordLength;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String maxWordLengthTipText() {
    return "The maximum length for words.";
  }

  /**
   * Sets the number of frequencies to return.
   *
   * @param value	the number of frequencies
   */
  public void setNumFrequencies(int value) {
    if (getOptionManager().isValid("numFrequencies", value)) {
      m_NumFrequencies = value;
      reset();
    }
  }

  /**
   * Returns the number of frequencies to return.
   *
   * @return		the number of frequencies
   */
  public int getNumFrequencies() {
    return m_NumFrequencies;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numFrequenciesTipText() {
    return "The number of frequencies to return.";
  }

  /**
   * Sets the storage item that holds the string array of stopwords to use.
   *
   * @param value	the storage name
   */
  public void setStopwords(StorageName value) {
    m_Stopwords = value;
    reset();
  }

  /**
   * Returns the storage item that holds the string array of stopwords to use.
   *
   * @return		the storage name
   */
  public StorageName getStopwords() {
    return m_Stopwords;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String stopwordsTipText() {
    return "The storage item that holds the string array of stopwords to use.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "minWordLength", m_MinWordLength, "min: ");
    result += QuickInfoHelper.toString(this, "maxWordLength", m_MaxWordLength, ", max: ");
    result += QuickInfoHelper.toString(this, "numFrequencies", m_NumFrequencies, ", #: ");
    result += QuickInfoHelper.toString(this, "stopwords", (m_Stopwords.isEmpty() ? "-none-" : m_Stopwords.getValue()), ", stopwords: ");

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{String.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{WordFrequency[].class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    String			text;
    ByteArrayInputStream	stream;
    FrequencyAnalyzer		analyzer;
    List<WordFrequency> 	frequencies;
    String[]			stopwords;

    result = null;
    text   = m_InputToken.getPayload(String.class);
    stream = new ByteArrayInputStream(text.getBytes(m_Encoding.charsetValue()));

    analyzer = new FrequencyAnalyzer();
    analyzer.clearNormalizers();
    for (Normalizer normalizer: m_Normalizers)
      analyzer.addNormalizer(normalizer);
    analyzer.setCharacterEncoding(m_Encoding.getValue());
    analyzer.setMinWordLength(m_MinWordLength);
    analyzer.setMaxWordLength(m_MaxWordLength);
    analyzer.setWordFrequenciesToReturn(m_NumFrequencies);
    if (!m_Stopwords.isEmpty() && getStorageHandler().getStorage().has(m_Stopwords)) {
      try {
	stopwords = (String[]) getStorageHandler().getStorage().get(m_Stopwords);
	analyzer.setStopWords(Arrays.asList(stopwords));
      }
      catch (Exception e) {
        result = handleException("Failed to retrieved stopwords string array from storage item: " + m_Stopwords, e);
      }
    }
    // TODO filters? require wrapper!
    try {
      frequencies   = analyzer.load(stream);
      m_OutputToken = new Token(frequencies.toArray(new WordFrequency[0]));
    }
    catch (Exception e) {
      result = handleException("Failed to generate word frequencies!", e);
    }

    return result;
  }
}
