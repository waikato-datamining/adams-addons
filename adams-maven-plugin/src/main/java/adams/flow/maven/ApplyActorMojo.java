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

import adams.core.MessageCollection;
import adams.core.Utils;
import adams.core.management.Java;
import adams.core.management.ProcessUtils;
import adams.core.option.ApplyActorProducer;
import adams.flow.core.Actor;
import adams.flow.core.ActorUtils;
import adams.flow.maven.shared.FileSystemUtilities;
import com.github.fracpete.processoutput4j.output.CollectingProcessOutput;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Calls the {@link ApplyActorProducer}.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
@Mojo(name = "apply",
        threadSafe = true,
        defaultPhase = LifecyclePhase.GENERATE_SOURCES,
        requiresDependencyResolution = ResolutionScope.COMPILE)
public class ApplyActorMojo
  extends AbstractJavaGeneratorMojo {

  /**
   * The classname of the generated code (without package).
   */
  @Parameter(defaultValue = "", required = true)
  private String simpleName;

  /**
   * The flow to convert to Java.
   */
  @Parameter(defaultValue = ".", required = true)
  private File flow;

  /**
   * The class (+ commandline options) for reading the flow.
   */
  @Parameter(defaultValue = "adams.core.option.CompactFlowConsumer", required = true)
  private String flowFormat;

  /**
   * The working directory where the generated Java source files are created.
   */
  @Parameter(defaultValue = "${project.build.directory}/generated-sources/apply", required = true)
  private File outputDirectory;

  /**
   * Indicate if the Mojo execution should be skipped.
   */
  @Parameter(property = "skip", defaultValue = "false")
  private boolean skip;

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean shouldExecutionBeSkipped() {
    return skip;
  }

  /**
   * Returns the directory for the class file (including the package).
   *
   * @return the directory
   */
  protected File classOutputDir() {
    return new File(getOutputDirectory().getAbsolutePath() + "/" + packageName.replace(".", "/"));
  }

  /**
   * Returns the full filename for the Java class to generate.
   *
   * @return the filename
   */
  protected File classOutputFile() {
    return new File(classOutputDir().getAbsolutePath() + "/" + simpleName + ".java");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean generateCode() throws MojoExecutionException {
    MessageCollection errors;
    MessageCollection warnings;
    Actor actor;

    // load flow
    errors = new MessageCollection();
    warnings = new MessageCollection();
    actor = ActorUtils.read(flow.getAbsolutePath(), errors, warnings);
    if (!warnings.isEmpty())
      getLog().warn("Warnings encountered loading flow '" + flow + "':\n" + warnings.toString());

    if (actor == null) {
      if (errors.isEmpty())
	throw new MojoExecutionException("Failed to load flow '" + flow + "'!");
      else
	throw new MojoExecutionException("Failed to load flow '" + flow + "':\n" + errors.toString());
    }

    // create output dir
    File outputDir = classOutputDir();
    if (!outputDir.exists()) {
      if (!outputDir.mkdirs())
        throw new MojoExecutionException("Failed to create output directory: " + outputDir);
    }

    List<String> cmd = new ArrayList<>();
    cmd.add(Java.getJavaExecutable());
    cmd.add("-classpath");
    cmd.add(Utils.flatten(getClasspath(), System.getProperty("path.separator")));
    cmd.add(ApplyActorProducer.class.getName());
    cmd.add("-env");
    cmd.add(environmentClass);
    cmd.add("-input");
    cmd.add(flow.getAbsolutePath());
    cmd.add("-format");
    cmd.add(flowFormat);
    cmd.add("-package");
    cmd.add(packageName);
    cmd.add("-simple-name");
    cmd.add(simpleName);
    cmd.add("-output");
    cmd.add(classOutputFile().getAbsolutePath());
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

    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void addResource(Resource resource) {
    getProject().addResource(resource);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected List<URL> getSources() {
    List<URL> result;

    result = new ArrayList<>();
    try {
      result.add(classOutputFile().toURI().toURL());
    }
    catch (Exception e) {
      getLog().error("Failed to generate URL from output file: " + classOutputFile(), e);
    }

    return result;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void addGeneratedSourcesToProjectSourceRoot() {
    getProject().addCompileSourceRoot(FileSystemUtilities.getCanonicalPath(getOutputDirectory()));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected File getOutputDirectory() {
    return outputDirectory;
  }
}
