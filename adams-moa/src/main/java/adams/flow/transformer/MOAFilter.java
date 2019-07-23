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
 * MOAFilter.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.flow.core.Token;
import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.instances.Instances;
import com.yahoo.labs.samoa.instances.InstancesHeader;
import moa.AbstractMOAObject;
import moa.core.Example;
import moa.core.InstanceExample;
import moa.options.ClassOption;
import moa.streams.ExampleStream;
import moa.streams.InstanceStream;
import moa.streams.filters.AddNoiseFilter;
import moa.streams.filters.StreamFilter;
import weka.core.MOAUtils;

/**
 <!-- globalinfo-start -->
 * Applies a MOA stream filter to the incoming MOA instances.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;com.yahoo.labs.samoa.instances.Instance<br>
 * &nbsp;&nbsp;&nbsp;com.yahoo.labs.samoa.instances.Instances<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;com.yahoo.labs.samoa.instances.Instance<br>
 * &nbsp;&nbsp;&nbsp;com.yahoo.labs.samoa.instances.Instances<br>
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
 * &nbsp;&nbsp;&nbsp;default: MOAFilter
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
 * <pre>-filter &lt;moa.options.ClassOption&gt; (property: streamFilter)
 * &nbsp;&nbsp;&nbsp;The stream filter to use for filtering the instances.
 * &nbsp;&nbsp;&nbsp;default: moa.streams.filters.AddNoiseFilter
 * </pre>
 *
 <!-- options-end -->
 *
 * Applies a MOA stream filter to the incoming MOA instances.
 *
 * @author Corey Sterling (csterlin at waikato dot ac dot nz)
 */
public class MOAFilter
  extends AbstractTransformer {

  /** The stream filter to use. */
  protected ClassOption m_StreamFilter;

  /** The actual stream filter object. */
  protected StreamFilter m_ActualStreamFilter;

  /** The fixed instance stream to stream the incoming instances. */
  protected FixedInstanceStream m_FixedInstanceStream;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Applies a MOA stream filter to the incoming MOA instances.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "filter", "streamFilter",
      getDefaultOption());
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void reset() {
    super.reset();

    m_ActualStreamFilter = null;
    m_FixedInstanceStream = null;
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_StreamFilter = getDefaultOption();
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "filter", getCurrentStreamFilter().getClass());

    return result;
  }

  /**
   * Returns the default stream filter.
   *
   * @return		the filter
   */
  protected ExampleStream getDefaultFilter() {
    return new AddNoiseFilter();
  }

  /**
   * Returns the default class option.
   *
   * @return		the option
   */
  protected ClassOption getDefaultOption() {
    return new ClassOption(
      "filter",
      'f',
      "The MOA stream filter to use from within ADAMS.",
      StreamFilter.class,
      getDefaultFilter().getClass().getName().replace("moa.streams.filters.", ""),
      getDefaultFilter().getClass().getName());
  }

  /**
   * Sets the stream filter to use.
   *
   * @param value	the stream filter
   */
  public void setStreamFilter(ClassOption value) {
    m_StreamFilter.setValueViaCLIString(value.getValueAsCLIString());
    reset();
  }

  /**
   * Returns the data filter in use.
   *
   * @return		the data filter
   */
  public ClassOption getStreamFilter() {
    return m_StreamFilter;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String streamFilterTipText() {
    return "The stream filter to use for filtering the instances.";
  }

  /**
   * Returns the current stream filter, based on the class option.
   *
   * @return		the stream filter
   * @see		#getStreamFilter()
   */
  protected StreamFilter getCurrentStreamFilter() {
    return (StreamFilter) MOAUtils.fromOption(m_StreamFilter);
  }

  @Override
  protected String doExecute() {
    // Initialise the actual filter
    if (m_ActualStreamFilter == null)
      m_ActualStreamFilter = getCurrentStreamFilter();

    // Initialise the dummy stream
    if (m_FixedInstanceStream == null) {
      m_FixedInstanceStream = new FixedInstanceStream();
      m_ActualStreamFilter.setInputStream(m_FixedInstanceStream);
    }

    // Let the dummy stream know about the new instance/s
    if (m_InputToken.hasPayload(Instance.class)) {
      m_FixedInstanceStream.setData((Instance) m_InputToken.getPayload());
    } else {
      m_FixedInstanceStream.setData((Instances) m_InputToken.getPayload());
    }

    // Apply the stream filter
    Object result = null;
    while (m_ActualStreamFilter.hasMoreInstances()) {
      Instance instance = (Instance) m_ActualStreamFilter.nextInstance().getData();
      if (result == null)
        result = instance;
      else if (result instanceof Instance) {
        Instances instances = new Instances(((Instance) result).dataset(), 2);
        instances.add((Instance) result);
        instances.add(instance);
        result = instances;
      } else {
        ((Instances) result).add(instance);
      }
    }

    // Set the output token
    m_OutputToken = new Token(result);

    return null;
  }

  @Override
  public Class[] accepts() {
    return new Class[] {Instance.class, Instances.class};
  }

  @Override
  public Class[] generates() {
    return new Class[] {Instance.class, Instances.class};
  }

  public class FixedInstanceStream extends AbstractMOAObject implements InstanceStream {
    protected boolean m_Single;
    protected Instance m_Instance;
    protected Instances m_Instances;
    protected int m_Left;

    public void setData(Instance instance) {
      m_Single = true;
      m_Instance = instance;
      m_Instances = null;
      m_Left = 1;
    }

    public void setData(Instances instances) {
      m_Single = false;
      m_Instance = null;
      m_Instances = instances;
      m_Left = instances.numInstances();
    }

    @Override
    public InstancesHeader getHeader() {
      if (m_Single)
        return new InstancesHeader(m_Instance.dataset());
      else
        return new InstancesHeader(m_Instances);
    }

    @Override
    public long estimatedRemainingInstances() {
      return m_Left;
    }

    @Override
    public boolean hasMoreInstances() {
      return m_Left != 0;
    }

    @Override
    public Example<Instance> nextInstance() {
      if (hasMoreInstances()) {
        if (m_Single) {
          m_Left = 0;
          return new InstanceExample(m_Instance);
        } else {
          int next = m_Instances.numInstances() - m_Left;
          InstanceExample example = new InstanceExample(m_Instances.get(next));
          m_Left -= 1;
          return example;
        }
      }

      return null;
    }

    @Override
    public boolean isRestartable() {
      return false;
    }

    @Override
    public void restart() {
    }

    @Override
    public void getDescription(StringBuilder sb, int indent) {

    }
  }
}