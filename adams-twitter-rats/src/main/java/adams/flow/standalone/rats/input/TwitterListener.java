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
 * TwitterListener.java
 * Copyright (C) 2017-2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.standalone.rats.input;

import adams.core.QuickInfoHelper;
import adams.core.option.OptionUtils;
import adams.flow.source.twitterlistener.AbstractListener;
import adams.flow.source.twitterlistener.SampleListener;
import twitter4j.Status;

/**
 <!-- globalinfo-start -->
 * Listens to the twitter stream API using the specified listener.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-max-buffer &lt;int&gt; (property: maxBuffer)
 * &nbsp;&nbsp;&nbsp;The maximum number of items to buffer.
 * &nbsp;&nbsp;&nbsp;default: 65535
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-listener &lt;adams.flow.source.twitterlistener.AbstractListener&gt; (property: listener)
 * &nbsp;&nbsp;&nbsp;The listener to use for generating the status objects.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.source.twitterlistener.SampleListener
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TwitterListener
  extends AbstractBufferedRatInput {

  private static final long serialVersionUID = 7627032152241150448L;

  /** the listener for retrieving the status updates. */
  protected AbstractListener m_Listener;

  /** the actual listener. */
  protected AbstractListener m_ActualListener;

  /** for transferring the data. */
  protected Thread m_TransferThread;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Listens to the twitter stream API using the specified listener.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
            "listener", "listener",
            new SampleListener());
  }

  /**
   * Sets the status listener to use.
   *
   * @param value	the listener
   */
  public void setListener(AbstractListener value) {
    m_Listener = value;
    reset();
  }

  /**
   * Returns the status listener to use.
   *
   * @return		the listener
   */
  public AbstractListener getListener() {
    return m_Listener;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String listenerTipText() {
    return "The listener to use for generating the status objects.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "listener", m_Listener, "listener: ");
  }

  /**
   * Returns the type of data this scheme generates.
   *
   * @return		the type of data
   */
  @Override
  public Class generates() {
    return Status.class;
  }

  /**
   * Initializes the reception.
   *
   * @return		null if successfully initialized, otherwise error message
   */
  @Override
  public String initReception() {
    String	result;

    result = super.initReception();

    if (result == null) {
      if (m_ActualListener != null)
	m_ActualListener.stopExecution();
      m_ActualListener = (AbstractListener) OptionUtils.shallowCopy(m_Listener);
      m_ActualListener.setFlowContext(getOwner());
    }

    return result;
  }

  /**
   * Performs the actual reception of data.
   *
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doReceive() {
    String	result;
    int		count;

    result = null;

    if (!m_ActualListener.isListening()) {
      try {
	m_ActualListener.startExecution();
      }
      catch (IllegalThreadStateException ie) {
	// ignored
      }
      catch (Exception e) {
	result = handleException("Failed to start listener thread!", e);
      }

      // wait for thread to start up
      count = 0;
      while (!m_ActualListener.isListening()) {
	count++;
	try {
	  synchronized (this) {
	    wait(50);
	  }
	}
	catch (Exception e) {
	  // ignored
	}

	// problem with launching thread?
	if (count == 100) {
	  result = "Thread timed out??";
	  break;
	}
      }

      if (result == null) {
	m_TransferThread = new Thread(() -> {
	  while (m_ActualListener.isListening()) {
	    Status status = m_ActualListener.next();
	    if (status != null) {
	      bufferData(status);
	      if (isLoggingEnabled())
		getLogger().info(status.toString());
	    }
	  }
	});
	m_TransferThread.start();
      }
    }

    return result;
  }

  /**
   * Stops listening to the twitter stream.
   */
  protected void stopListening() {
    if (m_ActualListener != null)
      m_ActualListener.stopExecution();
    if (m_TransferThread != null) {
      m_TransferThread.stop();
      m_TransferThread = null;
    }
  }

  /**
   * Stops the execution.
   */
  @Override
  public void stopExecution() {
    stopListening();
    super.stopExecution();
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  @Override
  public void cleanUp() {
    stopListening();
    super.cleanUp();
  }
}
