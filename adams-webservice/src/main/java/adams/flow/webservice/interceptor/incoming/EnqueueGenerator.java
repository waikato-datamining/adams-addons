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
 * EnqueueGenerator.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.webservice.interceptor.incoming;

import adams.flow.control.StorageName;
import adams.flow.core.NullToken;

/**
 * Generator for {@link Enqueue}.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class EnqueueGenerator
  extends AbstractInInterceptorGenerator<Enqueue> {

  /** for serialization. */
  private static final long serialVersionUID = -8109018608359183466L;

  /** the queue to enqueue the token in. */
  protected StorageName m_StorageName;
  
  /** whether to enqueue the message or just a {@link NullToken}. */
  protected boolean m_EnqueueMessage;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates an " + Enqueue.class.getName() + " instance.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "storage-name", "storageName",
	    new StorageName("queue"));

    m_OptionManager.add(
	    "enqueue-message", "enqueueMessage",
	    false);
  }

  /**
   * Sets the queue to use.
   * 
   * @param value	the queue
   */
  public void setStorageName(StorageName value) {
    m_StorageName = value;
    reset();
  }
  
  /**
   * Returns the queue in use.
   * 
   * @return		the queue
   */
  public StorageName getStorageName() {
    return m_StorageName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String storageNameTipText() {
    return "The name of the queue to send data to.";
  }

  /**
   * Sets whether to enqueue the whole message or just a {@link NullToken}.
   * 
   * @param value	true if to enqueue whole message
   */
  public void setEnqueueMessage(boolean value) {
    m_EnqueueMessage = value;
    reset();
  }
  
  /**
   * Returns whether to enqueue the whole message or just a {@link NullToken}.
   * 
   * @return		true if to enqueue whole message
   */
  public boolean getEnqueueMessage() {
    return m_EnqueueMessage;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String enqueueMessageTipText() {
    return "If enabled, the complete message gets enqueued, otherwise just a " + NullToken.class.getName() + ".";
  }

  /**
   * Generates the actual interceptor for incoming messages.
   * 
   * @return		the interceptor
   */
  @Override
  protected Enqueue doGenerate() {
    Enqueue	result;
    
    result = new Enqueue();
    result.setStorageName(getStorageName());
    result.setEnqueueMessage(getEnqueueMessage());
    
    return result;
  }
}
