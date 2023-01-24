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

import adams.core.base.DockerDirectoryMapping;
import adams.core.logging.LoggingHelper;
import adams.core.management.CommandResult;
import com.github.fracpete.processoutput4j.output.CollectingProcessOutput;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

  /**
   * Fixes the path a bit, removing duplicate slashes, trailing slash.
   *
   * @param path	the path to fix
   * @return		the fixed path
   */
  public static String fixPath(String path) {
    String  result;

    result = path;

    // duplicate slashes
    while (result.contains("//"))
      result = result.replace("//", "/");

    // trailing slash
    if (result.endsWith("/"))
      result = result.substring(0, result.length() - 1);

    return result;
  }

  /**
   * Translates the local path into a container path. Skips a string that does not start with a forward slash.
   *
   * @param path		the local path
   * @return			the translated path
   * @throws IOException	if the path cannot be translated into a container one
   */
  public static String toContainerPath(List<DockerDirectoryMapping> mappings, String path) throws IOException {
    return toContainerPaths(mappings, new String[]{path})[0];
  }

  /**
   * Translates the local paths into container paths. Skips entries that do not start with a forward slash.
   *
   * @param paths		the local paths
   * @return			the translated paths
   * @throws IOException	if a path cannot be translated into a container one
   */
  public static String[] toContainerPaths(List<DockerDirectoryMapping> mappings, String[] paths) throws IOException {
    String[]				result;
    List<DockerDirectoryMapping>	sorted;
    int					i;
    Path				path;

    result = new String[paths.length];
    sorted = new ArrayList<>(mappings);
    sorted.sort(new Comparator<DockerDirectoryMapping>() {
      @Override
      public int compare(DockerDirectoryMapping o1, DockerDirectoryMapping o2) {
	return o1.length() - o2.length();
      }
    });

    for (i = 0; i < paths.length; i++) {
      if (paths[i].startsWith("/")) {
        path      = new File(paths[i]).toPath();
        result[i] = null;
        for (DockerDirectoryMapping mapping: sorted) {
          if (path.startsWith(mapping.localDir())) {
            result[i] = fixPath(mapping.containerDir() + "/" + paths[i].substring(Math.min(paths[i].length(), mapping.localDir().length())));
            break;
          }
        }
      }
      else {
        result[i] = paths[i];
      }
      if (result[i] == null)
	throw new IOException("Failed to translate local path '" + paths[i] + "' into container one using: " + mappings);
    }

    return result;
  }

  /**
   * Checks whether a mapping can be added to the current list of directory mappings.
   * Ensures that neither local nor container path are already in use.
   *
   * @param mappings	the mappings to far
   * @param newMapping	the new mapping to check
   * @return		null if it can be added, otherwise reason why not
   */
  public static String canAddMapping(List<DockerDirectoryMapping> mappings, DockerDirectoryMapping newMapping) {
    String				result;
    Map<File,DockerDirectoryMapping> 	dirs;
    File				f;

    result = null;

    // check local dirs
    dirs = new HashMap<>();
    for (DockerDirectoryMapping m: mappings)
      dirs.put(new File(m.localDir()), m);
    f = new File(newMapping.localDir());
    if (dirs.containsKey(f))
      result = "Local directory already defined by: " + dirs.get(f);

    // check container dirs
    if (result == null) {
      dirs = new HashMap<>();
      for (DockerDirectoryMapping m: mappings)
	dirs.put(new File(m.containerDir()), m);
      f = new File(newMapping.containerDir());
      if (dirs.containsKey(f))
	result = "Container directory already defined by: " + dirs.get(f);
    }

    return result;
  }

  /**
   * Adds the mapping, if possible.
   *
   * @param mappings	the mappings to add to
   * @param newMapping	the mapping to add
   * @return		true if added otherwise false
   */
  public static boolean addMapping(List<DockerDirectoryMapping> mappings, DockerDirectoryMapping newMapping) {
    if (canAddMapping(mappings, newMapping) == null) {
      mappings.add(newMapping);
      return true;
    }

    return false;
  }
}
