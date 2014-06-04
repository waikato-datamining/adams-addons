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
 * MekaClassSelector.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import gnu.trove.list.array.TIntArrayList;
import meka.filters.unsupervised.attribute.MekaClassAttributes;
import weka.filters.Filter;
import adams.core.QuickInfoHelper;
import adams.core.base.BaseRegExp;
import adams.data.weka.WekaAttributeRange;
import adams.flow.core.Token;
import adams.flow.provenance.ActorType;
import adams.flow.provenance.Provenance;
import adams.flow.provenance.ProvenanceContainer;
import adams.flow.provenance.ProvenanceInformation;
import adams.flow.provenance.ProvenanceSupporter;

/**
 <!-- globalinfo-start -->
 * Determines which attributes to use as class attributes using either a regular expression or an attribute index range.<br/>
 * In case the attribute range is a non-empty string, this will take precedence over the regular expression.<br/>
 * Anything that follows a ':' or ' ' (blank) gets removed from the original relation name in order to create a valid MEKA one.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;weka.core.Instances<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;weka.core.Instances<br/>
 * <p/>
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
 * &nbsp;&nbsp;&nbsp;default: MekaClassSelector
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
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-range &lt;adams.data.weka.WekaAttributeRange&gt; (property: range)
 * &nbsp;&nbsp;&nbsp;The attribute range.
 * &nbsp;&nbsp;&nbsp;default: 
 * &nbsp;&nbsp;&nbsp;example: A range is a comma-separated list of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(...)' inverts the range '...'; apart from attribute names (case-sensitive), the following placeholders can be used as well: first, second, third, last_2, last_1, last
 * </pre>
 * 
 * <pre>-regex &lt;adams.core.base.BaseRegExp&gt; (property: regex)
 * &nbsp;&nbsp;&nbsp;The regular expression used for selecting the class attributes (matched 
 * &nbsp;&nbsp;&nbsp;against the attribute names).
 * &nbsp;&nbsp;&nbsp;default: .*
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 8065 $
 */
public class MekaClassSelector
  extends AbstractTransformer
  implements ProvenanceSupporter {

  /** for serialization. */
  private static final long serialVersionUID = -3019442578354930841L;

  /** the attribute range. */
  protected WekaAttributeRange m_Range;
  
  /** the regular expression on the attribute for selecting the sub-set of attributes. */
  protected BaseRegExp m_Regex;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Determines which attributes to use as class attributes using either a "
	+ "regular expression or an attribute index range.\n"
        + "In case the attribute range is a non-empty string, this will take "
	+ "precedence over the regular expression.\n"
        + "Anything that follows a ':' or ' ' (blank) gets removed from the "
	+ "original relation name in order to create a valid MEKA one.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "range", "range",
	    new WekaAttributeRange());

    m_OptionManager.add(
	    "regex", "regex",
	    new BaseRegExp(BaseRegExp.MATCH_ALL));
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "range", m_Range, "range: ");
    result += QuickInfoHelper.toString(this, "regex", m_Regex, ", name: ");

    return result;
  }

  /**
   * Sets the attribute range.
   *
   * @param value	the range
   */
  public void setRange(WekaAttributeRange value) {
    m_Range = value;
    reset();
  }

  /**
   * Returns the attribute range.
   *
   * @return		the range
   */
  public WekaAttributeRange getRange() {
    return m_Range;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String rangeTipText() {
    return "The attribute range.";
  }

  /**
   * Sets the regular expression for selecting the attributes.
   *
   * @param value	the regex
   */
  public void setRegex(BaseRegExp value) {
    m_Regex = value;
    reset();
  }

  /**
   * Returns the regular expression for selecting the attributes.
   *
   * @return		the regex
   */
  public BaseRegExp getRegex() {
    return m_Regex;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String regexTipText() {
    return "The regular expression used for selecting the class attributes (matched against the attribute names).";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		weka.core.Instance, weka.core.Instances, adams.data.instance.Instance
   */
  public Class[] accepts() {
    return new Class[]{weka.core.Instances.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		weka.core.Instance, weka.core.Instances, adams.data.instance.Instance
   */
  public Class[] generates() {
    return new Class[]{weka.core.Instances.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    weka.core.Instances		inst;
    TIntArrayList		atts;
    int				i;
    int[]			indices;
    MekaClassAttributes		filter;
    StringBuilder		range;
    String			oldName;
    String			newName;

    result = null;

    inst = (weka.core.Instances) m_InputToken.getPayload();
    inst = new weka.core.Instances(inst);

    if (m_Range.getRange().isEmpty()) {
      atts = new TIntArrayList();
      for (i = 0; i < inst.numAttributes(); i++) {
	if (m_Regex.isMatch(inst.attribute(i).name()))
	  atts.add(i);
      }
      indices = atts.toArray();
    }
    else {
      m_Range.setData(inst);
      indices = m_Range.getIntIndices();
    }

    range = new StringBuilder();
    for (i = 0; i < indices.length; i++) {
      if (i > 0)
	range.append(",");
      range.append("" + (indices[i] + 1));
    }

    try {
      filter = new MekaClassAttributes();
      filter.setAttributeIndices(range.toString());
      filter.setInputFormat(inst);
      oldName = inst.relationName().replaceAll(":.*", "").replaceAll(" .*", "");
      inst    = Filter.useFilter(inst, filter);
      newName = oldName + ": -C " + indices.length;
      inst.setRelationName(newName);
      m_OutputToken = new Token(inst);
      updateProvenance(m_OutputToken);
    }
    catch (Exception e) {
      result = handleException("Failed to set class attributes!", e);
    }

    return result;
  }

  /**
   * Updates the provenance information in the provided container.
   *
   * @param cont	the provenance container to update
   */
  public void updateProvenance(ProvenanceContainer cont) {
    if (Provenance.getSingleton().isEnabled()) {
      if (m_InputToken.hasProvenance())
	cont.setProvenance(m_InputToken.getProvenance().getClone());
      cont.addProvenance(new ProvenanceInformation(ActorType.PREPROCESSOR, m_InputToken.getPayload().getClass(), this, m_OutputToken.getPayload().getClass()));
    }
  }
}
