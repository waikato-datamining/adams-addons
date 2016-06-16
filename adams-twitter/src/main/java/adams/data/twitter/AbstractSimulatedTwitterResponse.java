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
 * AbstractSimulatedTwitterResponse.java
 * Copyright (C) 2013-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.data.twitter;

import twitter4j.RateLimitStatus;
import twitter4j.TwitterResponse;

import java.io.Serializable;

/**
 * Ancestor for simulating twitter.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractSimulatedTwitterResponse
  implements TwitterResponse, Serializable {

  /** for serialization. */
  private static final long serialVersionUID = -93525519472329597L;

  /**
   * Initializes the response.
   */
  protected AbstractSimulatedTwitterResponse() {
    super();
    initialize();
  }

  /**
   * Initializes the members.
   */
  protected void initialize() {
  }

  /**
   * Returns the current rate limit status if available.
   *
   * @return current rate limit status - dummy object
   */
  @Override
  public RateLimitStatus getRateLimitStatus() {
    return new RateLimitStatus() {
      private static final long serialVersionUID = -8963524970513688810L;
      @Override
      public int getRemaining() {
	return 0;
      }
      @Override
      public int getLimit() {
	return 0;
      }
      @Override
      public int getResetTimeInSeconds() {
	return 0;
      }
      @Override
      public int getSecondsUntilReset() {
	return 0;
      }
    };
  }

  /**
   * Always {@link TwitterResponse#NONE}.
   *
   * @return application permission model
   */
  @Override
  public int getAccessLevel() {
    return TwitterResponse.NONE;
  }
}
