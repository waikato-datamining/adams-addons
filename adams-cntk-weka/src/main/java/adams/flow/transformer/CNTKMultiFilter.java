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
 * CNTKMultiFilter.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.Index;
import adams.core.ObjectCopyHelper;
import adams.core.QuickInfoHelper;
import adams.core.Range;
import adams.core.Utils;
import adams.core.base.BaseString;
import adams.core.option.OptionUtils;
import adams.data.weka.WekaAttributeRange;
import adams.flow.container.CNTKMultiFilterResultContainer;
import adams.flow.core.Token;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import weka.core.Instances;
import weka.core.converters.CNTKSaver;
import weka.filters.AllFilter;
import weka.filters.Filter;
import weka.filters.MultiFilter;
import weka.filters.unsupervised.attribute.AddID;
import weka.filters.unsupervised.attribute.PartitionedMultiFilter2;
import weka.filters.unsupervised.attribute.Remove;

import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Applies the filters to the incoming data (also adds a numeric ID column) and output this new dataset along side Python code for CNTK.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;weka.core.Instances<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.CNTKMultiFilterResultContainer<br>
 * <br><br>
 * Container information:<br>
 * - adams.flow.container.CNTKMultiFilterResultContainer: Dataset, IDs, Saver, Dimensions, Reader, Input vars, Input map
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
 * &nbsp;&nbsp;&nbsp;default: CNTKMultiFilter
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
 * <pre>-filter &lt;weka.filters.Filter&gt; [-filter ...] (property: filters)
 * &nbsp;&nbsp;&nbsp;The filters to apply individually to the data (excluding targets and sample
 * &nbsp;&nbsp;&nbsp;ID).
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-prefix &lt;adams.core.base.BaseString&gt; [-prefix ...] (property: prefixes)
 * &nbsp;&nbsp;&nbsp;The prefixes for the attributes to use (- gets added automatically).
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-targets &lt;adams.data.weka.WekaAttributeRange&gt; (property: targets)
 * &nbsp;&nbsp;&nbsp;The attributes in the dataset that are considered targets.
 * &nbsp;&nbsp;&nbsp;default: last
 * &nbsp;&nbsp;&nbsp;example: A range is a comma-separated list of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(...)' inverts the range '...'; apart from attribute names (case-sensitive), the following placeholders can be used as well: first, second, third, last_2, last_1, last; numeric indices can be enforced by preceding them with '#' (eg '#12'); attribute names can be surrounded by double quotes.
 * </pre>
 *
 * <pre>-reader-name &lt;java.lang.String&gt; (property: readerName)
 * &nbsp;&nbsp;&nbsp;The name of the reader in the Python code.
 * &nbsp;&nbsp;&nbsp;default: test_reader
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class CNTKMultiFilter
  extends AbstractTransformer {

  private static final long serialVersionUID = 9077096252192331835L;

  public static final String PREFIX_TARGETS = "targets";

  public static final String PREFIX_FILTERED = "filtered";

  /** the filters to apply. */
  protected Filter[] m_Filters;

  /** the prefixes to use. */
  protected BaseString[] m_Prefixes;

  /** the range of attributes to use as targets. */
  protected WekaAttributeRange m_Targets;

  /** the name of the reader. */
  protected String m_ReaderName;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Applies the filters to the incoming data (also adds a numeric ID "
      + "column) and output this new dataset along side Python code for CNTK.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "filter", "filters",
      new Filter[0]);

    m_OptionManager.add(
      "prefix", "prefixes",
      new BaseString[0]);

    m_OptionManager.add(
      "targets", "targets",
      new WekaAttributeRange(WekaAttributeRange.LAST));

    m_OptionManager.add(
      "reader-name", "readerName",
      "test_reader");
  }

  /**
   * Sets the filters to apply individually to the data (excluding targets and sample ID).
   *
   * @param value	the filters
   */
  public void setFilters(Filter[] value) {
    m_Filters  = value;
    m_Prefixes = (BaseString[]) Utils.adjustArray(m_Prefixes, m_Filters.length, new BaseString());
    reset();
  }

  /**
   * Returns the filters to apply individually to the data (excluding targets and sample ID).
   *
   * @return 		the filters
   */
  public Filter[] getFilters() {
    return m_Filters;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String filtersTipText() {
    return "The filters to apply individually to the data (excluding targets and sample ID).";
  }

  /**
   * Sets the prefixes to use for the filters (- gets added automatically).
   *
   * @param value	the prefixes
   */
  public void setPrefixes(BaseString[] value) {
    m_Prefixes = value;
    m_Filters  = (Filter[]) Utils.adjustArray(m_Filters, m_Prefixes.length, new AllFilter());
    reset();
  }

  /**
   * Returns the prefixes to use for the filters (- gets added automatically).
   *
   * @return 		the prefixes
   */
  public BaseString[] getPrefixes() {
    return m_Prefixes;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String prefixesTipText() {
    return "The prefixes for the attributes to use (- gets added automatically).";
  }

  /**
   * Sets the attributes in the dataset that are considered targets.
   *
   * @param value	the range
   */
  public void setTargets(WekaAttributeRange value) {
    m_Targets = value;
    reset();
  }

  /**
   * Returns the attributes in the dataset that are considered targets.
   *
   * @return 		the range
   */
  public WekaAttributeRange getTargets() {
    return m_Targets;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String targetsTipText() {
    return "The attributes in the dataset that are considered targets.";
  }

  /**
   * Sets the name of the Python reader.
   *
   * @param value	the name
   */
  public void setReaderName(String value) {
    m_ReaderName = value;
    reset();
  }

  /**
   * Returns the name of the Python reader.
   *
   * @return 		the name
   */
  public String getReaderName() {
    return m_ReaderName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String readerNameTipText() {
    return "The name of the reader in the Python code.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "filters", m_Filters.length + " filters", "");
    result += QuickInfoHelper.toString(this, "targets", m_Targets, ", targets: ");
    result += QuickInfoHelper.toString(this, "readerName", m_ReaderName, ", reader: ");

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Instances.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{CNTKMultiFilterResultContainer.class};
  }

  /**
   * Extracts the actual target name from the attribute name.
   *
   * @param attName	the attribute name from PartitionedMultiFilter
   * @return		the target name
   */
  protected String extractTarget(String attName) {
    return attName.replaceAll(PREFIX_TARGETS + "-[0-9]+-", "");
  }

  /**
   * Generates the filtered data.
   *
   * @param data	the input data
   * @return		the filtered data
   * @throws Exception	if filtering fails
   */
  protected Instances filter(Instances data) throws Exception {
    Instances			filtered;
    PartitionedMultiFilter2	part;
    Range			dataAtts;
    TIntList			attList;
    TIntList			nonNumeric;
    TIntSet			blacklist;
    int				i;
    String			dataRange;
    List<Filter> 		filters;
    List<weka.core.Range>	ranges;
    List<BaseString>		prefixes;
    MultiFilter			multi;

    // determine data attributes
    m_Targets.setData(data);
    blacklist  = new TIntHashSet(m_Targets.getIntIndices());
    nonNumeric = new TIntArrayList();
    for (i = 0; i < data.numAttributes(); i++) {
      if (!data.attribute(i).isNumeric())
	nonNumeric.add(i);
    }
    blacklist.addAll(nonNumeric);

    attList = new TIntArrayList();
    for (i = 0; i < data.numAttributes(); i++) {
      if (!blacklist.contains(i))
        attList.add(i);
    }
    dataAtts = new Range();
    dataAtts.setMax(data.numAttributes());
    dataAtts.setIndices(attList.toArray());
    dataRange = dataAtts.toExplicitRange();

    // partitionedmultifilter
    filters = new ArrayList<>();
    for (i = 0; i < m_Filters.length; i++)
      filters.add(ObjectCopyHelper.copyObject(m_Filters[i]));
    filters.add(new AllFilter());  // targets
    ranges = new ArrayList<>();
    for (i = 0; i < m_Filters.length; i++)
      ranges.add(new weka.core.Range(dataRange));
    ranges.add(new weka.core.Range(m_Targets.toExplicitRange())); // targets
    prefixes = new ArrayList<>();
    for (i = 0; i < m_Prefixes.length; i++)
      prefixes.add(new BaseString(m_Prefixes[i].getValue()));
    prefixes.add(new BaseString(PREFIX_TARGETS));
    part = new PartitionedMultiFilter2();
    part.setFilters(filters.toArray(new Filter[0]));
    part.setRanges(ranges.toArray(new weka.core.Range[0]));
    part.setPrefixes(prefixes.toArray(new BaseString[0]));
    part.setRemoveUnused(true);

    // multifilter
    multi = new MultiFilter();
    multi.setFilters(new Filter[]{part, new AddID()});

    if (isLoggingEnabled())
      getLogger().info("MultiFilter: " + OptionUtils.getCommandLine(multi));

    // filter
    multi.setInputFormat(data);
    filtered = Filter.useFilter(data, multi);

    return filtered;
  }

  /**
   * Generates the dataset with the IDs (row id, sample id).
   *
   * @param data	the input data
   * @return		the IDs data
   * @throws Exception	if generation fails
   */
  protected Instances generateIDs(Instances data) throws Exception {
    Instances 	filtered;
    TIntList 	nonNumeric;
    int 	i;
    Remove 	remove;
    MultiFilter	multi;

    data = new Instances(data);
    data.setClassIndex(-1);

    nonNumeric = new TIntArrayList();
    for (i = 0; i < data.numAttributes(); i++) {
      if (!data.attribute(i).isNumeric())
	nonNumeric.add(i);
    }

    remove = new Remove();
    remove.setAttributeIndicesArray(nonNumeric.toArray());
    remove.setInvertSelection(true);

    multi = new MultiFilter();
    multi.setFilters(new Filter[]{remove, new AddID()});

    multi.setInputFormat(data);
    filtered = Filter.useFilter(data, multi);

    return filtered;
  }

  /**
   * Generates the saver setup.
   *
   * @param filtered	the filtered data to base the saver on
   * @return		the setup
   */
  protected CNTKSaver generateSaver(Instances filtered) {
    CNTKSaver		result;
    List<Range> 	inputs;
    Range 		input;
    TIntList		atts;
    int			i;
    int			n;
    String		prefix;
    List<BaseString> 	inputNames;

    inputNames = new ArrayList<>();
    for (BaseString name: m_Prefixes)
      inputNames.add(new BaseString(name.getValue()));

    // filters
    inputs = new ArrayList<>();
    for (i = 0; i < m_Filters.length; i++) {
      atts = new TIntArrayList();
      prefix = m_Prefixes[i].getValue();
      if (prefix.trim().isEmpty())
        prefix = PREFIX_FILTERED;
      prefix += "-" + i + "-";
      for (n = 0; n < filtered.numAttributes(); n++) {
        if (filtered.attribute(n).name().startsWith(prefix))
          atts.add(n);
      }
      input = new Range();
      input.setMax(filtered.numAttributes());
      input.setIndices(atts.toArray());
      inputs.add(input);
    }

    // targets
    for (n = 0; n < filtered.numAttributes(); n++) {
      if (filtered.attribute(n).name().startsWith(PREFIX_TARGETS)) {
        input = new Range();
        input.setMax(filtered.numAttributes());
        input.setIndices(new int[]{n});
	inputs.add(input);
	inputNames.add(new BaseString(extractTarget(filtered.attribute(n).name())));
      }
    }

    result = new CNTKSaver();
    result.setRowID(new Index("1"));
    result.setInputs(inputs.toArray(new Range[0]));
    result.setInputNames(inputNames.toArray(new BaseString[0]));

    return result;
  }

  /**
   * Generates a python snippet for the dimensions.
   *
   * @param filtered	the filtered data to inspect
   * @return		the python snippet
   */
  protected String generateDims(Instances filtered) {
    StringBuilder	result;
    int			i;
    int			count;

    result = new StringBuilder();
    result.append("labelDim = 1\n");

    for (BaseString prefix: m_Prefixes) {
      count = 0;
      for (i = 0; i < filtered.numAttributes(); i++) {
        if (filtered.attribute(i).name().startsWith(prefix.getValue()))
          count++;
      }
      result.append(prefix.getValue()).append("Dim = " + count + "\n");
    }

    return result.toString();
  }

  /**
   * Generates a python snippet for the reader.
   *
   * @param filtered	the filtered data to inspect
   * @return		the python snippet
   */
  protected String generateReader(Instances filtered) {
    StringBuilder	result;
    int			i;
    String		target;

    result = new StringBuilder();
    result.append("def create_reader(path, is_training):\n");
    result.append("    return C.io.MinibatchSource(C.io.CTFDeserializer(path, C.io.StreamDefs(\n");

    // features
    for (BaseString prefix: m_Prefixes)
      result.append("        " + prefix + "_features=C.io.StreamDef(field='" + prefix + "', shape=" + prefix + "Dim),\n");

    // targets
    for (i = 0; i < filtered.numAttributes(); i++) {
      if (filtered.attribute(i).name().startsWith(PREFIX_TARGETS)) {
        target = extractTarget(filtered.attribute(i).name());
        result.append("        " + target + "=C.io.StreamDef(field='" + target + "', shape=labelDim),\n");
      }
    }

    result.append("    )), randomize=is_training, max_sweeps=C.io.INFINITELY_REPEAT if is_training else 1)\n");

    return result.toString();
  }

  /**
   * Generates a python snippet for the input vars.
   *
   * @param filtered	the filtered data to inspect
   * @return		the python snippet
   */
  protected String generateInputVars(Instances filtered) {
    StringBuilder	result;
    String		target;
    int			i;

    result = new StringBuilder();

    // features
    for (BaseString prefix: m_Prefixes)
      result.append(prefix + " = C.input_variable(" + prefix + "Dim, name='" + prefix + "')\n");

    // targets
    for (i = 0; i < filtered.numAttributes(); i++) {
      if (filtered.attribute(i).name().startsWith(PREFIX_TARGETS)) {
        target = extractTarget(filtered.attribute(i).name());
        result.append(target + " = C.input_variable(labelDim, name='" + target + "')\n");
      }
    }

    return result.toString();
  }

  /**
   * Generates a python snippet for the input map.
   *
   * @param filtered	the filtered data to inspect
   * @return		the python snippet
   */
  protected String generateInputMap(Instances filtered) {
    StringBuilder	result;
    String		target;
    int			i;

    result = new StringBuilder();
    result.append(m_ReaderName + "_map = {\n");

    // features
    for (BaseString prefix: m_Prefixes)
      result.append("    " + prefix + ": " + m_ReaderName + "." + prefix + "_features,\n");

    // targets
    for (i = 0; i < filtered.numAttributes(); i++) {
      if (filtered.attribute(i).name().startsWith(PREFIX_TARGETS)) {
        target = extractTarget(filtered.attribute(i).name());
	result.append("    " + target + ": " + m_ReaderName + "." + target + ",\n");
      }
    }

    result.append("}\n");

    return result.toString();
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String				result;
    Instances				data;
    Instances				filtered;
    Instances				ids;
    CNTKMultiFilterResultContainer	cont;
    CNTKSaver				saver;
    String				dims;
    String				reader;
    String				inputVars;
    String				inputMap;

    result = null;
    data   = m_InputToken.getPayload(Instances.class);

    try {
      // data
      filtered = filter(data);
      ids      = generateIDs(data);
      saver    = generateSaver(filtered);

      // python snippets
      dims      = generateDims(filtered);
      reader    = generateReader(filtered);
      inputVars = generateInputVars(filtered);
      inputMap  = generateInputMap(filtered);

      // container
      cont = new CNTKMultiFilterResultContainer(
        filtered, ids, OptionUtils.getCommandLine(saver), dims, reader, inputVars, inputMap);
      m_OutputToken = new Token(cont);
    }
    catch (Exception e) {
      result = handleException("Failed to filter data!", e);
    }

    return result;
  }
}
