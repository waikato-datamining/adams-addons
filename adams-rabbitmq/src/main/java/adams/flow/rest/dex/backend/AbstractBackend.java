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
 * AbstractBackend.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.rest.dex.backend;

import adams.core.MessageCollection;
import adams.core.UniqueIDs;
import adams.core.io.MessageDigestType;
import adams.core.option.AbstractOptionHandler;

/**
 * Ancestor for backend schemes.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractBackend
  extends AbstractOptionHandler {

  private static final long serialVersionUID = 3157981057620546957L;

  /** the time to live for items. */
  protected int m_TimeToLive;

  /** whether to use sequential tokens (for testing only). */
  protected boolean m_SequentialTokens;

  /** whether the backed has been initialized. */
  protected boolean m_Initialized;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "time-to-live", "timeToLive",
      3600, 1, null);

    m_OptionManager.add(
      "sequential-tokens", "sequentialTokens",
      false);
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();
    m_Initialized = false;
  }

  /**
   * Sets the time to live for uploaded items.
   *
   * @param value	the time to live (seconds)
   */
  public void setTimeToLive(int value) {
    m_TimeToLive = value;
    reset();
  }

  /**
   * Returns the time to live for uploaded items.
   *
   * @return		the time to live (seconds)
   */
  public int getTimeToLive() {
    return m_TimeToLive;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String timeToLiveTipText() {
    return "The time to live (in seconds) for the data items before they expire.";
  }

  /**
   * Sets whether to generate sequential tokens (for testing only).
   *
   * @param value	true if sequential
   */
  public void setSequentialTokens(boolean value) {
    m_SequentialTokens = value;
    reset();
  }

  /**
   * Returns whether to generate sequential tokens (for testing only).
   *
   * @return		true if sequential
   */
  public boolean getSequentialTokens() {
    return m_SequentialTokens;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String sequentialTokensTipText() {
    return "If enabled, sequential tokens are generated (for testing only).";
  }

  /**
   * Generates the next token.
   *
   * @return		the token
   */
  protected String nextToken() {
    long		id;
    MessageCollection	errors;

    errors = new MessageCollection();
    id     = UniqueIDs.nextLong();
    if (!m_SequentialTokens)
      id += System.currentTimeMillis();
    return MessageDigestType.SHA256.digest(Long.toHexString(id), errors);
  }

  /**
   * Initializes the backend.
   *
   * @return		null if sucessfully initialized, otherwise error message
   */
  protected abstract String doInitBackend();

  /**
   * Initializes the backend, if necessary.
   *
   * @return		null if sucessfully initialized, otherwise error message
   */
  public String initBackend() {
    String	result;

    if (m_Initialized)
      return null;

    if (isLoggingEnabled())
      getLogger().info("Initializing backend...");
    result = doInitBackend();
    if (result == null)
      purge();

    return result;
  }

  /**
   * Removes all items.
   */
  protected abstract void doClear();

  /**
   * Removes all items.
   */
  public void clear() {
    if (isLoggingEnabled())
      getLogger().info("Clearing");
    doClear();
  }

  /**
   * Purges any expired items.
   */
  protected abstract void doPurge();

  /**
   * Purges any expired items.
   */
  public void purge() {
    if (isLoggingEnabled())
      getLogger().info("Purge");
    doPurge();
  }

  /**
   * Checks whether the item is present.
   *
   * @param token	the token to check
   * @return		true if available
   */
  protected abstract boolean hasItem(String token);

  /**
   * Checks whether the item is present.
   *
   * @param token	the token to check
   * @return		true if available
   */
  public boolean has(String token) {
    return hasItem(token);
  }

  /**
   * Gets the item, if present.
   *
   * @param token	the token to get
   * @return		the item, null if not available
   */
  protected abstract byte[] getItem(String token);

  /**
   * Gets the item, if present.
   *
   * @param token	the token to get
   * @return		the item, null if not available
   */
  public byte[] get(String token) {
    return getItem(token);
  }

  /**
   * Adds the item, returns the generated token.
   *
   * @param data	the data to add
   * @return		the token, null if failed to add
   */
  protected abstract String addItem(byte[] data);

  /**
   * Adds the item, returns the generated token.
   *
   * @param data	the data to add
   * @return		the token, null if failed to add
   */
  public String add(byte[] data) {
    return addItem(data);
  }

  /**
   * Removes the data associated with the token.
   *
   * @param token	the token to remove the data for
   * @return		true if removed
   */
  protected abstract boolean removeItem(String token);

  /**
   * Removes the data associated with the token.
   *
   * @param token	the token to remove the data for
   * @return		true if removed
   */
  public boolean remove(String token) {
    return removeItem(token);
  }
}
