package adams.flow.maven;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import adams.flow.maven.shared.FileSystemUtilities;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.sonatype.plexus.build.incremental.BuildContext;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Abstract Mojo which collects common infrastructure, required and needed
 * by all subclass Mojos in the adams maven plugin codebase.
 * Based on jaxb2-maven-plugin code.
 *
 * @author <a href="mailto:lj@jguru.se">Lennart J&ouml;relid</a>
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractAdamsMojo
  extends AbstractMojo {

  /**
   * Platform-independent newline control string.
   */
  public static final String NEWLINE = System.getProperty("line.separator");

  /**
   * The Plexus BuildContext is used to identify files or directories modified since last build,
   * implying functionality used to define if java generation must be performed again.
   */
  @Component
  private BuildContext buildContext;

  /**
   * The injected Maven project.
   */
  @Parameter(defaultValue = "${project}", readonly = true)
  private MavenProject project;

  /**
   * Note that the execution parameter will be injected ONLY if this plugin is executed as part
   * of a maven standard lifecycle - as opposed to directly invoked with a direct invocation.
   * When firing this mojo directly (i.e. {@code mvn adams:something}, the
   * {@code execution} object will not be injected.
   */
  @Parameter(defaultValue = "${mojoExecution}", readonly = true)
  private MojoExecution execution;

  /**
   * Adds the supplied Resource to the project using the appropriate scope (i.e. resource or testResource)
   * depending on the exact implementation of this AbstractAdamsMojo.
   *
   * @param resource The resource to add.
   */
  protected abstract void addResource(final Resource resource);

  /**
   * The Plexus BuildContext is used to identify files or directories modified since last build,
   * implying functionality used to define if java generation must be performed again.
   *
   * @return the active Plexus BuildContext.
   */
  protected final BuildContext getBuildContext() {
    return getInjectedObject(buildContext, "buildContext");
  }

  /**
   * @return The active MavenProject.
   */
  protected final MavenProject getProject() {
    return getInjectedObject(project, "project");
  }

  /**
   * @return The active MojoExecution.
   */
  public MojoExecution getExecution() {
    return getInjectedObject(execution, "execution");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final void execute() throws MojoExecutionException, MojoFailureException {
    // 0) Get the log and its relevant level
    final Log log = getLog();
    final boolean isDebugEnabled = log.isDebugEnabled();
    final boolean isInfoEnabled = log.isInfoEnabled();

    // 1) Should we skip execution?
    if (shouldExecutionBeSkipped()) {

      if (isDebugEnabled) {
	log.debug("Skipping execution, as instructed.");
      }
      return;
    }

    // 3) Are generated files stale?
    if (isReGenerationRequired()) {

      if (performExecution()) {

	// Hack to support M2E
	buildContext.refresh(getOutputDirectory());

      }
      else if (isInfoEnabled) {
	log.info("Not updating staleFile timestamp as instructed.");
      }
    }
    else if (isInfoEnabled) {
      log.info("No changes detected in schema or binding files - skipping code generation.");
    }

    // 4) If the output directories exist, add them to the MavenProject's source directories
    if (getOutputDirectory().exists() && getOutputDirectory().isDirectory()) {
      final String canonicalPathToOutputDirectory = FileSystemUtilities.getCanonicalPath(getOutputDirectory());

      if (log.isDebugEnabled()) {
	log.debug("Adding existing code outputDirectory [" + canonicalPathToOutputDirectory + "] to Maven's sources.");
      }

      // Add the output Directory.
      getProject().addCompileSourceRoot(canonicalPathToOutputDirectory);
    }
  }

  /**
   * Implement this method to check if this AbstractAdamsMojo should skip executing altogether.
   *
   * @return {@code true} to indicate that this AbstractAdamsMojo should bail out of its execute method.
   */
  protected abstract boolean shouldExecutionBeSkipped();

  /**
   * @return {@code true} to indicate that this AbstractAdamsMojo should be run since its generated files were
   * either stale or not present, and {@code false} otherwise.
   */
  protected abstract boolean isReGenerationRequired();

  /**
   * <p>Implement this method to perform this Mojo's execution.
   * This method will only be called if {@code !shouldExecutionBeSkipped() && isReGenerationRequired()}.</p>
   *
   * @return {@code true} if the timestamp of the stale file should be updated.
   * @throws MojoExecutionException if an unexpected problem occurs.
   *                                Throwing this exception causes a "BUILD ERROR" message to be displayed.
   * @throws MojoFailureException   if an expected problem (such as a compilation failure) occurs.
   *                                Throwing this exception causes a "BUILD FAILURE" message to be displayed.
   */
  protected abstract boolean performExecution() throws MojoExecutionException, MojoFailureException;

  /**
   * Override this method to acquire a List holding all URLs to the sources which this
   * AbstractAdamsMojo should use to produce its output.
   *
   * @return A non-null List holding URLs to sources used by this AbstractAdamsMojo to produce its output.
   */
  protected abstract List<URL> getSources();

  /**
   * Retrieves the directory where the generated files should be written to.
   *
   * @return the directory where the generated files should be written to.
   */
  protected abstract File getOutputDirectory();

  /**
   * Returns the classpath parts that can be used for launching a Java process.
   *
   * @return the classpath parts
   */
  protected List<String> getClasspath() throws MojoExecutionException {
    try {
      return getProject().getCompileClasspathElements();
    } catch (DependencyResolutionRequiredException e) {
      throw new MojoExecutionException("Could not retrieve Compile classpath.", e);
    }
  }

  /**
   * Convenience method to invoke when some plugin configuration is incorrect.
   * Will output the problem as a warning with some degree of log formatting.
   *
   * @param propertyName The name of the problematic property.
   * @param description  The problem description.
   */
  @SuppressWarnings("all")
  protected void warnAboutIncorrectPluginConfiguration(final String propertyName, final String description) {
    final StringBuilder builder = new StringBuilder();
    builder.append("\n+=================== [Incorrect Plugin Configuration Detected]\n");
    builder.append("|\n");
    builder.append("| Property : " + propertyName + "\n");
    builder.append("| Problem  : " + description + "\n");
    builder.append("|\n");
    builder.append("+=================== [End Incorrect Plugin Configuration Detected]\n\n");
    getLog().warn(builder.toString().replace("\n", NEWLINE));
  }

  private <T> T getInjectedObject(final T objectOrNull, final String objectName) {
    if (objectOrNull == null) {
      getLog().error(
	"Found null '" + objectName + "', implying that Maven @Component injection was not done properly.");
    }

    return objectOrNull;
  }

  /**
   * Prints out the system properties to the Maven Log at Debug level.
   */
  protected void logSystemPropertiesAndBasedir() {
    if (getLog().isDebugEnabled()) {
      final StringBuilder builder = new StringBuilder();

      builder.append("\n+=================== [System properties]\n");
      builder.append("|\n");

      // Sort the system properties
      final SortedMap<String, Object> props = new TreeMap<String, Object>();
      props.put("basedir", FileSystemUtilities.getCanonicalPath(getProject().getBasedir()));

      for (Map.Entry<Object, Object> current : System.getProperties().entrySet()) {
	props.put("" + current.getKey(), current.getValue());
      }
      for (Map.Entry<String, Object> current : props.entrySet()) {
	builder.append("| [" + current.getKey() + "]: " + current.getValue() + "\n");
      }

      builder.append("|\n");
      builder.append("+=================== [End System properties]\n");

      // All done.
      getLog().debug(builder.toString().replace("\n", NEWLINE));
    }
  }
}
