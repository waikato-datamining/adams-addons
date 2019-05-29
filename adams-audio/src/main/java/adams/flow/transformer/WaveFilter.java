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
 * WaveFilter.java
 * Copyright (C) 2018 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.data.audio.WaveContainer;
import adams.data.wavefilter.AbstractWaveFilter;
import adams.data.wavefilter.PassThrough;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class WaveFilter
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -1998955116780561587L;

  /** how to trim. */
  protected AbstractWaveFilter m_Filter;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Applies the filter to the incoming data.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "filter", "filter",
      new PassThrough());
  }

  /**
   * Sets the filter to use.
   *
   * @param value	the filter
   */
  public void setFilter(AbstractWaveFilter value) {
    m_Filter = value;
    reset();
  }

  /**
   * Returns the filter in use.
   *
   * @return		the filter
   */
  public AbstractWaveFilter getFilter() {
    return m_Filter;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String filterTipText() {
    return "The filter to apply to the data.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "filter", m_Filter, "filter: ");
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  public Class[] accepts() {
    return new Class[]{WaveContainer.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of objects that get generated
   */
  public Class[] generates() {
    return new Class[]{WaveContainer.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    WaveContainer 	data;
    WaveContainer 	filtered;

    result = null;

    data     = m_InputToken.getPayload(WaveContainer.class);
    filtered = null;
    try {
      filtered = m_Filter.filter(data);
    }
    catch (Exception e) {
      result = handleException("Failed to filter data: " + data, e);
    }

    if (filtered != null)
      m_OutputToken = new Token(filtered);

    return result;
  }
}
