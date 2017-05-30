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
 * TwitterUser.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.net.TwitterHelper;
import adams.flow.core.Token;
import twitter4j.User;

import java.util.HashMap;
import java.util.Map;

/**
 <!-- globalinfo-start -->
 * Outputs the user information for the user (screen name or ID).
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * &nbsp;&nbsp;&nbsp;java.lang.Long<br>
 * &nbsp;&nbsp;&nbsp;java.lang.Integer<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.util.Map<br>
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
 * &nbsp;&nbsp;&nbsp;default: TwitterUser
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
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TwitterUser
  extends AbstractTransformer {

  private static final long serialVersionUID = 5809210150081226732L;

  /** for accessing the twitter API. */
  protected twitter4j.Twitter m_Twitter;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Outputs the user information for the user (screen name or ID).";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{String.class, Long.class, Integer.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{Map.class};
  }

  /**
   * Initializes the item for flow execution. Also calls the reset() method
   * first before anything else.
   *
   * @return		null if everything is fine, otherwise error message
   * @see		#reset()
   */
  @Override
  public String setUp() {
    String	result;

    result = super.setUp();

    if (result == null)
      m_Twitter = TwitterHelper.getTwitterConnection(this);

    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    String		name;
    Long		id;
    User		user;
    Map<String,String>	map;

    result = null;

    id   = null;
    name = null;
    user = null;
    if (m_InputToken.getPayload() instanceof Long)
      id = (Long) m_InputToken.getPayload();
    else if (m_InputToken.getPayload() instanceof Integer)
      id = ((Integer) m_InputToken.getPayload()).longValue();
    else if (m_InputToken.getPayload() instanceof String)
      name = (String) m_InputToken.getPayload();
    try {
      if (name != null)
	user = m_Twitter.users().showUser(name);
      else if (id != null)
        user = m_Twitter.users().showUser(id);
      else
        result = "Neither name nor ID provided!";

      if (user != null) {
	map = new HashMap<>();
	map.put("id", "" + user.getId());
	map.put("name", user.getName());
	map.put("screenname", user.getScreenName());
	map.put("lang", user.getLang());
	map.put("description", user.getDescription());
	map.put("location", user.getLocation());
	map.put("timezone", user.getTimeZone());
	map.put("url", user.getURL());
	map.put("favoritescount", "" + user.getFavouritesCount());
	map.put("followerscount", "" + user.getFollowersCount());
	map.put("friendscount", "" + user.getFriendsCount());
	map.put("statusescount", "" + user.getStatusesCount());
	map.put("listedcount", "" + user.getListedCount());
	map.put("status", user.getStatus().getText());

	m_OutputToken = new Token(map);
      }
    }
    catch (Exception e) {
      result = handleException("Failed to determine ID for user: " + name, e);
    }

    return result;
  }
}
