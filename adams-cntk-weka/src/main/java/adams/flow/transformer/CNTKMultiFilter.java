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
 * Copyright (C) 2018-2020 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.ObjectCopyHelper;
import adams.core.QuickInfoHelper;
import adams.core.Range;
import adams.core.Utils;
import adams.core.base.BaseRegExp;
import adams.core.base.BaseString;
import adams.core.option.OptionUtils;
import adams.data.json.JsonHelper;
import adams.data.weka.WekaAttributeIndex;
import adams.data.weka.WekaAttributeRange;
import adams.flow.container.CNTKMultiFilterResultContainer;
import adams.flow.core.Token;
import com.google.gson.JsonObject;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import weka.core.Instance;
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
 * Applies the filters to the incoming data (also adds a numeric ID column) and outputs this new dataset alongside Python code for CNTK.
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
 * - adams.flow.container.CNTKMultiFilterResultContainer: Dataset, IDs, Saver, Definition
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
 * <pre>-domain-name &lt;java.lang.String&gt; (property: domainName)
 * &nbsp;&nbsp;&nbsp;The name for the domain.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-domain-type &lt;java.lang.String&gt; (property: domainType)
 * &nbsp;&nbsp;&nbsp;The type for the domain.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-filter &lt;weka.filters.Filter&gt; [-filter ...] (property: filters)
 * &nbsp;&nbsp;&nbsp;The filters to apply individually to the data (excluding targets and sample
 * &nbsp;&nbsp;&nbsp;ID).
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-regexp &lt;adams.core.base.BaseRegExp&gt; [-regexp ...] (property: regExps)
 * &nbsp;&nbsp;&nbsp;The regular expression to apply to the attribute names to identify numeric
 * &nbsp;&nbsp;&nbsp;attributes to use for a filter.
 * &nbsp;&nbsp;&nbsp;default:
 * &nbsp;&nbsp;&nbsp;more: https:&#47;&#47;docs.oracle.com&#47;javase&#47;tutorial&#47;essential&#47;regex&#47;
 * &nbsp;&nbsp;&nbsp;https:&#47;&#47;docs.oracle.com&#47;javase&#47;8&#47;docs&#47;api&#47;java&#47;util&#47;regex&#47;Pattern.html
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
 * <pre>-input-id-att &lt;adams.data.weka.WekaAttributeIndex&gt; (property: inputIDAttribute)
 * &nbsp;&nbsp;&nbsp;The attribute index in the input dataset that contains the unique ID for
 * &nbsp;&nbsp;&nbsp;which to generate the ID mapping.
 * &nbsp;&nbsp;&nbsp;default: first
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; apart from attribute names (case-sensitive), the following placeholders can be used as well: first, second, third, last_2, last_1, last; numeric indices can be enforced by preceding them with '#' (eg '#12'); attribute names can be surrounded by double quotes.
 * </pre>
 *
 * <pre>-output-id-att &lt;java.lang.String&gt; (property: outputIDAttribute)
 * &nbsp;&nbsp;&nbsp;The attribute name in the output dataset that contains the numeric unique
 * &nbsp;&nbsp;&nbsp;ID for which the ID mapping was generated.
 * &nbsp;&nbsp;&nbsp;default: ID
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

  /** the domain name. */
  protected String m_DomainName;
  
  /** the domain type. */
  protected String m_DomainType;

  /** the filters to apply. */
  protected Filter[] m_Filters;

  /** the regular expressions to identify the attributes to use for the filters. */
  protected BaseRegExp[] m_RegExps;

  /** the prefixes to use. */
  protected BaseString[] m_Prefixes;

  /** the range of attributes to use as targets. */
  protected WekaAttributeRange m_Targets;

  /** the name of the attribute in the input with the unique ID. */
  protected WekaAttributeIndex m_InputIDAttribute;

  /** the name of the attribute in the output with the unique ID. */
  protected String m_OutputIDAttribute;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Applies the filters to the incoming data (also adds a numeric ID "
      + "column) and outputs this new dataset alongside Python code for CNTK.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "domain-name", "domainName",
      "");

    m_OptionManager.add(
      "domain-type", "domainType",
      "");

    m_OptionManager.add(
      "filter", "filters",
      new Filter[0]);

    m_OptionManager.add(
      "regexp", "regExps",
      new BaseRegExp[0]);

    m_OptionManager.add(
      "prefix", "prefixes",
      new BaseString[0]);

    m_OptionManager.add(
      "targets", "targets",
      new WekaAttributeRange(WekaAttributeRange.LAST));

    m_OptionManager.add(
      "input-id-att", "inputIDAttribute",
      new WekaAttributeIndex(WekaAttributeIndex.FIRST));

    m_OptionManager.add(
      "output-id-att", "outputIDAttribute",
      "ID");
  }

  /**
   * Sets the domain name.
   *
   * @param value	the name
   */
  public void setDomainName(String value) {
    m_DomainName  = value;
    reset();
  }

  /**
   * Returns the domain name.
   *
   * @return 		the name
   */
  public String getDomainName() {
    return m_DomainName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String domainNameTipText() {
    return "The name for the domain.";
  }

  /**
   * Sets the domain type.
   *
   * @param value	the type
   */
  public void setDomainType(String value) {
    m_DomainType  = value;
    reset();
  }

  /**
   * Returns the domain type.
   *
   * @return 		the type
   */
  public String getDomainType() {
    return m_DomainType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String domainTypeTipText() {
    return "The type for the domain.";
  }

  /**
   * Sets the filters to apply individually to the data (excluding targets and sample ID).
   *
   * @param value	the filters
   */
  public void setFilters(Filter[] value) {
    m_Filters  = value;
    m_Prefixes = (BaseString[]) Utils.adjustArray(m_Prefixes, m_Filters.length, new BaseString());
    m_RegExps  = (BaseRegExp[]) Utils.adjustArray(m_RegExps, m_Filters.length, new BaseRegExp());
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
   * Sets the regular expression to apply to the attribute names to identify
   * numeric attributes to use for a filter.
   *
   * @param value	the expressions
   */
  public void setRegExps(BaseRegExp[] value) {
    m_RegExps  = value;
    m_Filters  = (Filter[]) Utils.adjustArray(m_Filters, m_RegExps.length, new AllFilter());
    m_Prefixes = (BaseString[]) Utils.adjustArray(m_Prefixes, m_RegExps.length, new BaseString());
    reset();
  }

  /**
   * Returns the regular expression to apply to the attribute names to identify
   * numeric attributes to use for a filter.
   *
   * @return 		the expressions
   */
  public BaseRegExp[] getRegExps() {
    return m_RegExps;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String regExpsTipText() {
    return "The regular expression to apply to the attribute names to identify numeric attributes to use for a filter.";
  }

  /**
   * Sets the prefixes to use for the filters (- gets added automatically).
   *
   * @param value	the prefixes
   */
  public void setPrefixes(BaseString[] value) {
    m_Prefixes = value;
    m_Filters  = (Filter[]) Utils.adjustArray(m_Filters, m_Prefixes.length, new AllFilter());
    m_RegExps  = (BaseRegExp[]) Utils.adjustArray(m_RegExps, m_Prefixes.length, new BaseRegExp());
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
   * Sets the attribute index in the input dataset that contains the unique ID
   * for which to generate the ID mapping.
   *
   * @param value	the attribute index
   */
  public void setInputIDAttribute(WekaAttributeIndex value) {
    m_InputIDAttribute = value;
    reset();
  }

  /**
   * Returns the attribute index in the input dataset that contains the unique
   * ID for which to generate the ID mapping.
   *
   * @return 		the attribute index
   */
  public WekaAttributeIndex getInputIDAttribute() {
    return m_InputIDAttribute;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String inputIDAttributeTipText() {
    return "The attribute index in the input dataset that contains the unique ID for which to generate the ID mapping.";
  }

  /**
   * Sets the attribute name in the output dataset that contains the numeric
   * unique ID for which the ID mapping was generated.
   *
   * @param value	the attribute name
   */
  public void setOutputIDAttribute(String value) {
    m_OutputIDAttribute = value;
    reset();
  }

  /**
   * Returns the attribute name in the output dataset that contains the numeric
   * unique ID for which the ID mapping was generated.
   *
   * @return 		the attribute name
   */
  public String getOutputIDAttribute() {
    return m_OutputIDAttribute;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String outputIDAttributeTipText() {
    return "The attribute name in the output dataset that contains the numeric unique ID for which the ID mapping was generated.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "domainName", (m_DomainName.isEmpty() ? "-none-" : m_DomainName), "name: ");
    result += QuickInfoHelper.toString(this, "domainType", (m_DomainType.isEmpty() ? "-none-" : m_DomainType), ", type: ");
    result += QuickInfoHelper.toString(this, "filters", m_Filters.length + " filters", "");
    result += QuickInfoHelper.toString(this, "targets", m_Targets, ", targets: ");
    result += QuickInfoHelper.toString(this, "inputIDAttribute", m_InputIDAttribute, ", input ID: ");
    result += QuickInfoHelper.toString(this, "outputIDAttribute", m_OutputIDAttribute, ", output ID: ");

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
   * Generates the set of blacklisted attributes (non-numeric and targets),
   * to be used for the filters.
   *
   * @param data	the input data
   * @return		the blacklisted 0-based attribute indices
   */
  protected TIntSet generateAttributeBlacklist(Instances data) {
    TIntSet 	result;
    TIntList	nonNumeric;
    int		i;

    m_Targets.setData(data);
    result     = new TIntHashSet(m_Targets.getIntIndices());
    nonNumeric = new TIntArrayList();
    for (i = 0; i < data.numAttributes(); i++) {
      if (!data.attribute(i).isNumeric())
	nonNumeric.add(i);
    }
    result.addAll(nonNumeric);

    return result;
  }

  /**
   * Determines the attribute indices for each filter, using the regular expressions.
   *
   * @param data	the input data
   * @param blacklist 	the blacklisted attributes indices
   * @return		the attributes indices corresponding with the filters
   * @throws Exception	if generation fails
   */
  protected TIntList[] generateAttributeIndices(Instances data, TIntSet blacklist) throws Exception {
    TIntList[]	result;
    int		i;
    int		n;

    result = new TIntList[m_Filters.length];
    for (n = 0; n < result.length; n++) {
      result[n] = new TIntArrayList();
      for (i = 0; i < data.numAttributes(); i++) {
        if (blacklist.contains(i))
          continue;
        if (m_RegExps[n].isMatch(data.attribute(i).name()))
          result[n].add(i);
      }
    }

    return result;
  }

  /**
   * Generates the filter to use.
   *
   * @param data	the input data
   * @param attIndices 	the attribute indices for each filter
   * @return		the filter
   * @throws Exception	if generation fails
   */
  protected Filter generateFilter(Instances data, TIntList[] attIndices) throws Exception {
    MultiFilter 		result;
    PartitionedMultiFilter2	part;
    Range			dataAtts;
    int				i;
    String			dataRange;
    List<Filter> 		filters;
    List<weka.core.Range>	ranges;
    List<BaseString>		prefixes;
    AddID			addID;

    // partitionedmultifilter
    filters = new ArrayList<>();
    for (i = 0; i < m_Filters.length; i++)
      filters.add(ObjectCopyHelper.copyObject(m_Filters[i]));
    filters.add(new AllFilter());  // targets
    ranges = new ArrayList<>();
    for (i = 0; i < m_Filters.length; i++) {
      dataAtts = new Range();
      dataAtts.setMax(data.numAttributes());
      dataAtts.setIndices(attIndices[i].toArray());
      dataRange = dataAtts.toExplicitRange();
      ranges.add(new weka.core.Range(dataRange));
    }
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

    // AddID
    addID = new AddID();
    addID.setAttributeName(m_OutputIDAttribute);

    // multifilter
    result = new MultiFilter();
    result.setFilters(new Filter[]{part, addID});

    if (isLoggingEnabled())
      getLogger().info("MultiFilter: " + OptionUtils.getCommandLine(result));

    return result;
  }

  /**
   * Generates the filtered data.
   *
   * @param filter	the filter to use
   * @param data	the input data
   * @return		the filtered data
   * @throws Exception	if filtering fails
   */
  protected Instances filter(Filter filter, Instances data) throws Exception {
    Instances 	result;

    filter.setInputFormat(data);
    result = Filter.useFilter(data, filter);

    return result;
  }

  /**
   * Generates the dataset with the IDs (row id, sample id).
   *
   * @param data	the input data
   * @return		the IDs data
   * @throws Exception	if generation fails
   */
  protected String generateIDs(Instances data) throws Exception {
    JsonObject 	result;
    Instances 	filtered;
    int		id;
    Remove 	remove;
    MultiFilter	multi;

    data = new Instances(data);
    data.setClassIndex(-1);

    m_InputIDAttribute.setData(data);
    id = m_InputIDAttribute.getIntIndex();
    if (id == -1)
      throw new IllegalStateException("Failed to locate unique ID attribute in input data: " + m_InputIDAttribute);

    remove = new Remove();
    remove.setAttributeIndicesArray(new int[]{id});
    remove.setInvertSelection(true);

    multi = new MultiFilter();
    multi.setFilters(new Filter[]{remove, new AddID()});

    multi.setInputFormat(data);
    filtered = Filter.useFilter(data, multi);

    result = new JsonObject();
    for (Instance inst: filtered)
      result.addProperty("" + ((int) inst.value(0)), inst.stringValue(1));

    return JsonHelper.prettyPrint(result);
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

    inputs     = new ArrayList<>();
    inputNames = new ArrayList<>();

    // ID attribute
    m_InputIDAttribute.setData(filtered);
    inputs.add(new Range("" + (filtered.attribute(m_OutputIDAttribute).index() + 1)));
    inputNames.add(new BaseString(m_OutputIDAttribute));

    // filters
    for (BaseString name: m_Prefixes)
      inputNames.add(new BaseString(name.getValue()));
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
    result.setInputs(inputs.toArray(new Range[0]));
    result.setInputNames(inputNames.toArray(new BaseString[0]));

    return result;
  }

  /**
   * Generates a JSON definition for the dataset.
   *
   * @param saver	the configured saver to use
   * @param filter	the filter
   * @param filtered	the filtered dataset
   * @param attIndices	the attribute indices used by the filters
   * @return		the JSON definition
   */
  protected String generateDefinition(CNTKSaver saver, Filter filter, Instances filtered, TIntList[] attIndices) {
    JsonObject		result;
    JsonObject		map;
    int			i;
    Range		range;

    result = new JsonObject();

    // domain
    map = new JsonObject();
    map.addProperty("Name", m_DomainName);
    map.addProperty("Type", m_DomainType);
    result.add("Domain", map);

    // unique ID
    result.addProperty("UniqueID", m_OutputIDAttribute);

    // inputs
    map = new JsonObject();
    for (i = 0; i < saver.getInputNames().length; i++) {
      range = saver.getInputs()[i].getClone();
      range.setMax(filtered.numAttributes());
      map.addProperty(saver.getInputNames()[i].getValue(), range.getIntIndices().length);
    }
    result.add("Inputs", map);

    // filter
    result.addProperty("Filter", OptionUtils.getCommandLine(filter));

    // ranges
    map = new JsonObject();
    for (i = 0; i < m_Filters.length; i++) {
      range = new Range();
      range.setMax(filtered.numAttributes());
      range.setIndices(attIndices[i].toArray());
      map.addProperty(m_Prefixes[i].getValue(), range.toExplicitRange());
    }
    result.add("AttributeRanges", map);

    return JsonHelper.prettyPrint(result);
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
    TIntSet				blacklist;
    TIntList[] 				attIndices;
    Filter				filter;
    Instances				filtered;
    String				ids;
    CNTKMultiFilterResultContainer	cont;
    CNTKSaver				saver;
    String 				def;

    result = null;
    data   = m_InputToken.getPayload(Instances.class);

    try {
      blacklist  = generateAttributeBlacklist(data);
      attIndices = generateAttributeIndices(data, blacklist);
      filter     = generateFilter(data, attIndices);
      filtered   = filter(filter, data);
      ids        = generateIDs(data);
      saver      = generateSaver(filtered);
      def        = generateDefinition(saver, filter, filtered, attIndices);

      // container
      cont = new CNTKMultiFilterResultContainer(
        filtered, ids, OptionUtils.getCommandLine(saver), def);
      m_OutputToken = new Token(cont);
    }
    catch (Exception e) {
      result = handleException("Failed to filter data!", e);
    }

    return result;
  }
}
