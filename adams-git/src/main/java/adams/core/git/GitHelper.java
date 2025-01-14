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
 * GitHelper.java
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 */

package adams.core.git;

import adams.core.DateUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.RebaseResult;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.RemoteRefUpdate;

import java.util.Map;

/**
 * Helper class for git.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class GitHelper {

  public final static String PH_TIMESTAMP = "{TIMESTAMP}";

  public final static String PH_COMMITTER = "{COMMITTER}";

  public final static String PH_HASH = "{HASH}";

  public final static String PH_FULL_MESSAGE = "{FULL_MESSAGE}";

  public final static String PH_SHORT_MESSAGE = "{SHORT_MESSAGE}";

  public final static String FORMAT_REVCOMMIT_LONG =
      "Hash.....: " + PH_HASH + "\n"
    + "Timestamp: " + PH_TIMESTAMP + "\n"
    + "Committer: " + PH_COMMITTER + "\n"
    + "Message..: " + PH_FULL_MESSAGE + "\n";

  public final static String FORMAT_REVCOMMIT_SHORT = PH_TIMESTAMP + "/" + PH_COMMITTER + ": " + PH_SHORT_MESSAGE + "\n";

  /**
   * Returns a commit as a string.
   * <br>
   * Supported placeholders:
   * {@link #PH_HASH}, {@link #PH_COMMITTER}, {@link #PH_FULL_MESSAGE}, {@link #PH_SHORT_MESSAGE}, {@link #PH_TIMESTAMP}
   *
   * @param commit	the commit to format
   * @param format 	the format to use
   * @return		the formatted string
   */
  public static String format(RevCommit commit, String format) {
    String	result;
    PersonIdent ident;

    ident  = commit.getCommitterIdent();
    result = format;
    result = result.replace(PH_HASH, commit.name());
    result = result.replace(PH_COMMITTER, ident.getName());
    result = result.replace(PH_FULL_MESSAGE, commit.getFullMessage());
    result = result.replace(PH_SHORT_MESSAGE, commit.getShortMessage());
    result = result.replace(PH_TIMESTAMP, DateUtils.getTimestampFormatter().format(ident.getWhen()));

    return result;
  }

  /**
   * Turns the update into a string.
   *
   * @param update	the update to format
   * @return		the generated string
   */
  public static String format(RemoteRefUpdate update) {
    String	result;

    result = update.getSrcRef() + " -> " + update.getRemoteName() + ": " + update.getStatus();
    result += "\n" + (update.getExpectedOldObjectId() != null ? update.getExpectedOldObjectId().name() : "(null)")
		+ "..."
		+ (update.getNewObjectId() != null ? update.getNewObjectId().name() : "(null)");
    if (update.getMessage() != null)
      result += "\nMessage: " + update.getMessage();
    return result;
  }

  /**
   * Formats a fetch result.
   *
   * @param fetch	the result to format
   * @return		the generated string
   */
  public static String format(FetchResult fetch) {
    StringBuilder		result;
    Map<String, FetchResult>	submods;

    result  = new StringBuilder();
    submods = fetch.submoduleResults();
    for (String key: submods.keySet()) {
      if (result.length() > 0)
	result.append(", ");
      result.append(key);
    }

    return result.toString();
  }

  /**
   * Formats a merge result.
   *
   * @param merge	the result to format
   * @return		the generated string
   */
  public static String format(MergeResult merge) {
    return merge.toString();
  }

  /**
   * Formats a rebase result.
   *
   * @param rebase	the result to format
   * @return		the generated string
   */
  public static String format(RebaseResult rebase) {
    return rebase.toString();
  }

  /**
   * Turns the pull result into a legible string.
   *
   * @param pull	the result to format
   * @return		the generated string
   */
  public static String format(PullResult pull) {
    StringBuilder 	result;

    result = new StringBuilder();

    // fetch
    if (pull.getFetchResult() != null)
      result.append(format(pull.getFetchResult()));
    else
      result.append("No fetch result");
    if (result.length() > 0)
      result.append("\n");

    // merge or rebase
    if (pull.getMergeResult() != null)
      result.append(format(pull.getMergeResult()));
    else if (pull.getRebaseResult() != null)
      result.append(format(pull.getRebaseResult()));
    else
      result.append("No update result");

    return result.toString();
  }

  /**
   * Checks whether the repo is a remote one.
   *
   * @param git		the repo to check
   * @return		true if remote
   */
  public static boolean isRemoteRepo(Git git) {
    return (git.getRepository().getConfig().getString("remote", "origin", "url") != null);
  }
}
