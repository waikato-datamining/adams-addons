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
 * UserListener.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.flow.source.twitterlistener;

import adams.core.QuickInfoHelper;
import adams.core.net.TwitterHelper;
import twitter4j.DirectMessage;
import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.User;
import twitter4j.UserList;
import twitter4j.UserStreamListener;

import java.util.logging.Level;

/**
 * Listener for following tweets from a user.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 13567 $
 */
public class UserListener
  extends AbstractListener
  implements UserStreamListener {

  private static final long serialVersionUID = 5406360301457780558L;

  /** the screenname to track. */
  protected String m_User;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Outputs status updates obtained from the 'garden hose'.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
            "user", "user",
            "");
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = super.getQuickInfo();
    result += QuickInfoHelper.toString(this, "user", (m_User.isEmpty() ? "not set" : m_User), ", user: ");

    return result;
  }

  /**
   * Sets the user to follow.
   *
   * @param value	the user
   */
  public void setUser(String value) {
    m_User = value;
    reset();
  }

  /**
   * Returns the user to follow.
   *
   * @return		the user
   */
  public String getUser() {
    return m_User;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String userTipText() {
    return "The user to follow (screen name, e.g., '@TheAdamsFlow').";
  }

  /**
   * Starts the listening.
   */
  public void startExecution() {
    String	name;
    User	user;
    FilterQuery	filter;

    try {
      name = m_User;
      if (name.startsWith("@"))
	name = name.substring(1);
      if (name.isEmpty())
	throw new IllegalStateException("No screen name provided!");
      user = TwitterHelper.getTwitterConnection(getFlowContext()).users().showUser(name);
      if (user == null)
	throw new IllegalStateException("Failed to retrieve user data for name: " + name);
      getLogger().info("Following tweets from user: " + name + "/" + user.getId());
      m_Twitter.addListener(this);
      filter = new FilterQuery();
      filter.follow(user.getId());
      m_Twitter.filter(filter);
      m_Listening = true;
    }
    catch (Exception e) {
      m_Twitter.removeListener(this);
      getLogger().log(Level.SEVERE, "Failed to start listener!", e);
    }
  }

  @Override
  protected void removeListener() {
    m_Twitter.removeListener(this);
  }

  @Override
  public void onDeletionNotice(long directMessageId, long userId) {

  }

  @Override
  public void onFriendList(long[] friendIds) {

  }

  @Override
  public void onFavorite(User source, User target, Status favoritedStatus) {

  }

  @Override
  public void onUnfavorite(User source, User target, Status unfavoritedStatus) {

  }

  @Override
  public void onFollow(User source, User followedUser) {

  }

  @Override
  public void onUnfollow(User source, User unfollowedUser) {

  }

  @Override
  public void onDirectMessage(DirectMessage directMessage) {

  }

  @Override
  public void onUserListMemberAddition(User addedMember, User listOwner, UserList list) {

  }

  @Override
  public void onUserListMemberDeletion(User deletedMember, User listOwner, UserList list) {

  }

  @Override
  public void onUserListSubscription(User subscriber, User listOwner, UserList list) {

  }

  @Override
  public void onUserListUnsubscription(User subscriber, User listOwner, UserList list) {

  }

  @Override
  public void onUserListCreation(User listOwner, UserList list) {

  }

  @Override
  public void onUserListUpdate(User listOwner, UserList list) {

  }

  @Override
  public void onUserListDeletion(User listOwner, UserList list) {

  }

  @Override
  public void onUserProfileUpdate(User updatedUser) {

  }

  @Override
  public void onUserSuspension(long suspendedUser) {

  }

  @Override
  public void onUserDeletion(long deletedUser) {

  }

  @Override
  public void onBlock(User source, User blockedUser) {

  }

  @Override
  public void onUnblock(User source, User unblockedUser) {

  }

  @Override
  public void onRetweetedRetweet(User source, User target, Status retweetedStatus) {

  }

  @Override
  public void onFavoritedRetweet(User source, User target, Status favoritedRetweeet) {

  }

  @Override
  public void onQuotedTweet(User source, User target, Status quotingTweet) {

  }

  /**
   * When receiving a status.
   *
   * @param status	the status
   */
  @Override
  public void onStatus(Status status) {
    if (m_Listening && !m_Paused) {
      if ((getMaxStatusUpdates() > 0) && (m_Count >= getMaxStatusUpdates()))
	stopExecution();
      else
	m_Next = status;
    }
  }

  @Override
  public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {

  }

  @Override
  public void onTrackLimitationNotice(int numberOfLimitedStatuses) {

  }

  @Override
  public void onScrubGeo(long userId, long upToStatusId) {

  }

  @Override
  public void onStallWarning(StallWarning warning) {

  }

  @Override
  public void onException(Exception ex) {

  }
}
