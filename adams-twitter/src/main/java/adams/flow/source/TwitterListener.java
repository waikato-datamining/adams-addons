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
 * Copyright (C) 2010-2017 University of Waikato, Hamilton, New Zealand
 * Copyright (c) 2007-2010, Yusuke Yamamoto
 */

package adams.flow.source;

import adams.core.License;
import adams.core.QuickInfoHelper;
import adams.core.annotation.MixedCopyright;
import adams.core.option.OptionUtils;
import adams.flow.core.Token;
import adams.flow.source.twitterlistener.AbstractListener;
import adams.flow.source.twitterlistener.SampleListener;
import twitter4j.Status;

/**
 <!-- globalinfo-start -->
 * Uses the Twitter streaming API to retrieve tweets.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;twitter4j.Status<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 *
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to 
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: TwitterListener
 * </pre>
 *
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 *
 * <pre>-skip (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * </pre>
 *
 * <pre>-stop-flow-on-error (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * </pre>
 *
 * <pre>-max-updates &lt;int&gt; (property: maxStatusUpdates)
 * &nbsp;&nbsp;&nbsp;The maximum number of status updates to output; use &lt;=0 for unlimited.
 * &nbsp;&nbsp;&nbsp;default: 100
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @author  Yusuke Yamamoto
 * @version $Revision$
 */
@MixedCopyright(
        author = "Yusuke Yamamoto",
        copyright = "2007-2010 Yusuke Yamamoto",
        license = License.APACHE2,
        url = "http://twitter4j.org/en/code-examples.html"
)
public class TwitterListener
        extends AbstractSource {

  /** for serialization. */
  private static final long serialVersionUID = -7777610085728160967L;

  /** the listener for retrieving the status updates. */
  protected AbstractListener m_Listener;

  /** the actual listener. */
  protected AbstractListener m_ActualListener;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Uses the Twitter streaming API (and the specified listener) to retrieve tweets.";
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
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "listener", m_Listener, "listener: ");
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the classes
   */
  public Class[] generates() {
    return new Class[]{Status.class};
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
   * Initializes the sub-actors for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String	result;

    result = super.setUp();

    if (result == null) {
      if (m_ActualListener != null)
        m_ActualListener.stopExecution();
      m_ActualListener = (AbstractListener) OptionUtils.shallowCopy(m_Listener);
      m_ActualListener.setFlowContext(this);
    }

    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return		always null
   */
  @Override
  protected String doExecute() {
    String	result;
    int		count;

    result = null;

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
        synchronized(this) {
          wait(50);
        }
      }
      catch (Exception e) {
      }

      // problem with launching thread?
      if (count == 100) {
        result = "Thread timed out??";
        break;
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
   * Returns the generated token.
   *
   * @return		the generated token
   */
  public Token output() {
    Token	result;
    Status	status;

    result = null;
    status = m_ActualListener.next();
    if (status != null)
      result = new Token(status);

    return result;
  }

  /**
   * Checks whether there is pending output to be collected after
   * executing the flow item.
   *
   * @return		true if there is pending output
   */
  public boolean hasPendingOutput() {
    return m_Executed && (m_ActualListener != null) && (m_ActualListener.hasNext());
  }

  /**
   * Cleans up after the execution has finished.
   */
  @Override
  public void wrapUp() {
    super.wrapUp();

    m_ActualListener = null;
  }
}
