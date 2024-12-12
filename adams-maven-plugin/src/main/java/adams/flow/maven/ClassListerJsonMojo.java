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
 * ClassListerJsonMojo.java
 * Copyright (C) 2024 University of Waikato, Hamilton, NZ
 */

package adams.flow.maven;

import adams.core.ClassListerJson;
import adams.core.Utils;
import adams.core.management.Java;
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
 * Outputs the classes or packages as json files.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
@Mojo(name = "classlisterjson",
        threadSafe = false,
        defaultPhase = LifecyclePhase.COMPILE,
        requiresDependencyResolution = ResolutionScope.COMPILE)
public class ClassListerJsonMojo
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
   * {@inheritDoc}
   */
  @Override
  protected boolean performExecution() throws MojoExecutionException, MojoFailureException {
    // classes
    List<String> cmd = new ArrayList<>();
    cmd.add(Java.getJavaExecutable());
    cmd.add("-classpath");
    cmd.add(Utils.flatten(getClasspath(), System.getProperty("path.separator")));
    cmd.add(ClassListerJson.class.getName());
    cmd.add("-action");
    cmd.add("classes");
    cmd.add("-output");
    cmd.add(outputDirectory.getAbsolutePath() + File.separator + ClassListerJson.CLASSLISTERJSON_CLASSES);
    cmd.add("-format");
    cmd.add("json");
    execute(cmd);

    // packages
    cmd = new ArrayList<>();
    cmd.add(Java.getJavaExecutable());
    cmd.add("-classpath");
    cmd.add(Utils.flatten(getClasspath(), System.getProperty("path.separator")));
    cmd.add(ClassListerJson.class.getName());
    cmd.add("-action");
    cmd.add("packages");
    cmd.add("-output");
    cmd.add(outputDirectory.getAbsolutePath() + File.separator + ClassListerJson.CLASSLISTERJSON_PACKAGES);
    cmd.add("-format");
    cmd.add("json");
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
