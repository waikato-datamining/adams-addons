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
 * Reject.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.core.net.rabbitmq.deliveryaction;

import adams.core.QuickInfoHelper;
import adams.core.logging.LoggingHelper;
import com.rabbitmq.client.Channel;

import java.util.ArrayList;
import java.util.List;

/**
 * Rejects the processing of the message with the specified delivery tag.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Reject
  extends AbstractDeliveryAction {

  private static final long serialVersionUID = 4328085615118918815L;

  /** whether to acknowledge all messages up to delivery tag. */
  protected boolean m_Multiple;

  /** whether to queue messages again. */
  protected boolean m_Requeue;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Rejects the processing of the message with the specified delivery tag.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "multiple", "multiple",
      false);

    m_OptionManager.add(
      "requeue", "requeue",
      false);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String		result;
    List<String> 	options;

    options = new ArrayList<>();
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "multiple", m_Multiple, "multiple"));
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "requeue", m_Requeue, "requeue"));
    result = QuickInfoHelper.flatten(options);

    return result;
  }

  /**
   * Sets whether to reject all messages up to and including delivery tag.
   *
   * @param value	true if reject all
   */
  public void setMultiple(boolean value) {
    m_Multiple = value;
    reset();
  }

  /**
   * Returns whether to reject all messages up to and including delivery tag.
   *
   * @return 		true if reject all
   */
  public boolean getMultiple() {
    return m_Multiple;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String multipleTipText() {
    return "If enabled, rejects all messages up to and including the delivery tag.";
  }

  /**
   * Sets whether to requeue the rejected messages again.
   *
   * @param value	true if requeue
   */
  public void setRequeue(boolean value) {
    m_Requeue = value;
    reset();
  }

  /**
   * Returns whether to requeue the rejected messages again.
   *
   * @return 		true if requeue
   */
  public boolean getRequeue() {
    return m_Requeue;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String requeueTipText() {
    return "If enabled, rejected messages get requeued again.";
  }

  /**
   * Performs the action.
   *
   * @param channel	the channel to operate on
   * @param tag 	the delivery tag
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doPerformAction(Channel channel, long tag) {
    try {
      channel.basicAck(tag, m_Multiple);
      return null;
    }
    catch (Exception e) {
      return LoggingHelper.handleException(this, "Failed to reject tag " + tag + " (multiple=" + m_Multiple + ", requeue=" + m_Requeue + ")!", e);
    }
  }
}
