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
 * SimpleDockerHelper.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.docker;

import adams.core.logging.LoggingHelper;
import adams.core.management.CommandResult;
import com.github.fracpete.processoutput4j.output.CollectingProcessOutput;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Helper class for issuing docker commands.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class SimpleDockerHelper {

  /**
   * Performs a login.
   *
   * @param binary	the docker binary to use
   * @param registry	the registry to log into, empty/null for default
   * @param user	the user for the registry
   * @param pw		the password for the registry
   * @return		the result of the command
   */
  public static CommandResult login(String binary, String registry, String user, String pw) {
    CommandResult		result;
    ProcessBuilder		builder;
    List<String> 		cmd;
    CollectingProcessOutput	output;

    cmd = new ArrayList<>();
    cmd.add(binary);
    cmd.add("login");
    if ((user != null) && !user.isEmpty()) {
      cmd.add("-u");
      cmd.add(user);
      if ((pw != null) && !pw.isEmpty())
	cmd.add("--password-stdin");
    }
    if ((registry != null) && !registry.isEmpty())
      cmd.add(registry);
    builder = new ProcessBuilder();
    builder.command(cmd);

    try {
      output = new CollectingProcessOutput();
      // send password?
      if ((pw != null) && !pw.isEmpty())
        output.monitor(pw, builder);
      else
        output.monitor(builder);
      result = new CommandResult(cmd.toArray(new String[0]), output.getExitCode(), output.getStdOut(), output.getStdErr());
    }
    catch (Exception e) {
      result = new CommandResult(cmd.toArray(new String[0]), 1, null, "Failed to login:\n" + LoggingHelper.throwableToString(e));
    }

    return result;
  }

  /**
   * Performs a login.
   *
   * @param binary	the docker binary to use
   * @param registry	the registry to log into, empty/null for default
   * @return		the result of the command
   */
  public static CommandResult logout(String binary, String registry) {
    CommandResult		result;
    ProcessBuilder		builder;
    List<String> 		cmd;
    CollectingProcessOutput	output;

    cmd = new ArrayList<>();
    cmd.add(binary);
    cmd.add("logout");
    if ((registry != null) && !registry.isEmpty())
      cmd.add(registry);
    builder = new ProcessBuilder();
    builder.command(cmd);

    try {
      output = new CollectingProcessOutput();
      output.monitor(builder);
      result = new CommandResult(cmd.toArray(new String[0]), output.getExitCode(), output.getStdOut(), output.getStdErr());
    }
    catch (Exception e) {
      result = new CommandResult(cmd.toArray(new String[0]), 1, null, "Failed to logout:\n" + LoggingHelper.throwableToString(e));
    }

    return result;
  }

  /**
   * Performs a docker command.
   *
   * @param binary	the docker binary to use
   * @param command	the docker command and its parameters
   * @return		the result of the command
   */
  public static CommandResult command(String binary, String[] command) {
    return command(binary, Arrays.asList(command));
  }

  /**
   * Performs a docker command (waits for command to finish).
   *
   * @param binary	the docker binary to use
   * @param command	the docker command and its parameters
   * @return		the result of the command
   */
  public static CommandResult command(String binary, List<String> command) {
    CommandResult		result;
    ProcessBuilder		builder;
    List<String> 		cmd;
    CollectingProcessOutput	output;

    cmd = new ArrayList<>();
    cmd.add(binary);
    cmd.addAll(command);
    builder = new ProcessBuilder();
    builder.command(cmd);

    try {
      output = new CollectingProcessOutput();
      output.monitor(builder);
      result = new CommandResult(cmd.toArray(new String[0]), output.getExitCode(), output.getStdOut(), output.getStdErr());
    }
    catch (Exception e) {
      result = new CommandResult(cmd.toArray(new String[0]), 1, null, "Failed to execute docker command:\n" + LoggingHelper.throwableToString(e));
    }

    return result;
  }
}
