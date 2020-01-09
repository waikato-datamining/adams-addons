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
 * ClassListerMojo.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.maven;

import adams.core.ClassLister;
import adams.core.Utils;
import adams.core.management.Java;
import adams.core.management.ProcessUtils;
import com.github.fracpete.processoutput4j.output.CollectingProcessOutput;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Outputs the classes or packages as props files.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
@Mojo(name = "classlister",
        threadSafe = false,
        defaultPhase = LifecyclePhase.COMPILE,
        requiresDependencyResolution = ResolutionScope.COMPILE)
public class ClassListerMojo
  extends AbstractAdamsMojo {

  /**
   * Indicate if the Mojo execution should be skipped.
   */
  @Parameter(property = "skip", defaultValue = "false")
  private boolean skip;

  /**
   * The working directory where the generated props files will get stored.
   */
  @Parameter(defaultValue = "${project.build.directory}/classes", required = true)
  private File outputDirectory;

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean shouldExecutionBeSkipped() {
    return skip;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void addResource(Resource resource) {
    getProject().addResource(resource);
  }

  /**
   * <p>Java generation is required if any of the file products is outdated/stale.</p>
   * {@inheritDoc}
   */
  @Override
  protected boolean isReGenerationRequired() {
    return true;
  }

  /**
   * Executes the command.
   *
   * @param cmd the command to execute
   * @throws MojoExecutionException if command exits with code != 0 or an exception occurred
   */
  protected void execute(List<String> cmd) throws MojoExecutionException {
    getLog().debug("Executing: " + Utils.flatten(cmd, " "));
    try {
      CollectingProcessOutput output = ProcessUtils.execute(cmd.toArray(new String[0]));
      if (output.getExitCode() != 0) {
        if (!output.getStdErr().isEmpty())
          getLog().error(output.getStdErr());
	throw new Exception("Code generation failed with exit code: " + output.getExitCode());
      }
      else {
	getLog().info(output.getStdOut());
      }
    }
    catch (Exception e) {
      throw new MojoExecutionException("Failed to execute command: " + Utils.flatten(cmd, " "), e);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean performExecution() throws MojoExecutionException, MojoFailureException {
    System.out.println(Utils.flatten(getClasspath(), System.getProperty("path.separator")));

    // classes
    List<String> cmd = new ArrayList<>();
    cmd.add(Java.getJavaExecutable());
    cmd.add("-classpath");
    cmd.add(Utils.flatten(getClasspath(), System.getProperty("path.separator")));
    cmd.add(ClassLister.class.getName());
    cmd.add("-action");
    cmd.add("classes");
    cmd.add("-output");
    cmd.add(outputDirectory.getAbsolutePath() + File.separator + ClassLister.CLASSLISTER_CLASSES);
    execute(cmd);

    // packages
    cmd = new ArrayList<>();
    cmd.add(Java.getJavaExecutable());
    cmd.add("-classpath");
    cmd.add(Utils.flatten(getClasspath(), System.getProperty("path.separator")));
    cmd.add(ClassLister.class.getName());
    cmd.add("-action");
    cmd.add("packages");
    cmd.add("-output");
    cmd.add(outputDirectory.getAbsolutePath() + File.separator + ClassLister.CLASSLISTER_PACKAGES);
    execute(cmd);

    return true;
  }

  /**
   * Retrieves the directory where the generated files should be written to.
   *
   * @return the directory where the generated files should be written to.
   */
  @Override
  protected File getOutputDirectory() {
    return outputDirectory;
  }
}
