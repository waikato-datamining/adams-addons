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
 * TrailWindow.java
 * Copyright (C) 2015-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.data.filter;

import adams.core.DateUtils;
import adams.core.base.BaseTimeMsec;
import adams.data.audiotrail.AudioStep;
import adams.data.audiotrail.AudioTrail;

import java.util.Date;

/**
 <!-- globalinfo-start -->
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class AudioTrailWindow
  extends AbstractFilter<AudioTrail> {

  /** for serialization. */
  private static final long serialVersionUID = 2616498525816421178L;
  
  /** the starting point. */
  protected BaseTimeMsec m_Start;
  
  /** the end point. */
  protected BaseTimeMsec m_End;
  
  /** whether to invert the matching. */
  protected boolean m_Invert;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Leaves only the specified window in the trail (borders included).\n"
	+ "The matching can be inverted, i.e., everything but the window is returned.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "start", "start",
	    new BaseTimeMsec(BaseTimeMsec.INF_PAST));

    m_OptionManager.add(
	    "end", "end",
	    new BaseTimeMsec(BaseTimeMsec.INF_FUTURE));

    m_OptionManager.add(
	    "invert", "invert",
	    false);
  }

  /**
   * Sets the start timestamp for trail.
   *
   * @param value	the timestamp
   */
  public void setStart(BaseTimeMsec value) {
    m_Start = value;
    reset();
  }

  /**
   * The start timestamp of trail.
   *
   * @return		the timestamp
   */
  public BaseTimeMsec getStart() {
    return m_Start;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String startTipText() {
    return "The timestamp for the first data point in the trail to keep.";
  }

  /**
   * Sets the end timestamp for trail.
   *
   * @param value	the timestamp
   */
  public void setEnd(BaseTimeMsec value) {
    m_End = value;
    reset();
  }

  /**
   * The end timestamp of trail.
   *
   * @return		the timestamp
   */
  public BaseTimeMsec getEnd() {
    return m_End;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String endTipText() {
    return "The timestamp for the last data point in the trail to keep.";
  }

  /**
   * Sets whether to invert the matching.
   *
   * @param value	true if to invert
   */
  public void setInvert(boolean value) {
    m_Invert = value;
    reset();
  }

  /**
   * Returns whether the matching is inverted.
   *
   * @return		true if inverted
   */
  public boolean getInvert() {
    return m_Invert;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String invertTipText() {
    return "If enabled, everything but the window is kept.";
  }

  /**
   * Performs the actual filtering.
   *
   * @param data	the data to filter
   * @return		the filtered data
   */
  @Override
  protected AudioTrail processData(AudioTrail data) {
    AudioTrail	result;
    AudioStep   step;
    int		i;
    Date	start;
    Date	end;

    result = (AudioTrail) data.getHeader();
    start  = m_Start.dateValue();
    end    = m_End.dateValue();
    for (i = 0; i < data.size(); i++) {
      step = data.toList().get(i);
      if (m_Invert) {
	if (!(DateUtils.isBefore(start, step.getTimestamp()) || DateUtils.isAfter(end, step.getTimestamp())))
	  continue;
      }
      else {
	if (DateUtils.isBefore(start, step.getTimestamp()) || DateUtils.isAfter(end, step.getTimestamp()))
	  continue;
      }
      result.add(new AudioStep(step.getTimestamp(), step.getMetaData()));
    }
    
    return result;
  }
}
