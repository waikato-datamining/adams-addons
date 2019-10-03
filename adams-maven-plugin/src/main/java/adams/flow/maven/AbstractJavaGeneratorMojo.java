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
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;

import java.net.URL;
import java.util.List;

/**
 * <p>Abstract superclass for Mojos generating Java source</p>
 * Based on jaxb2-maven-plugin code.
 *
 * @author <a href="mailto:lj@jguru.se">Lennart J&ouml;relid</a>
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractJavaGeneratorMojo
  extends AbstractAdamsMojo {

  /**
   * <p>The ADAMS environment class to use.</p>
   */
  @Parameter(defaultValue = "adams.env.Environment", required = true)
  protected String environmentClass;

  /**
   * <p>The package under which the source files will be generated.</p>
   */
  @Parameter
  protected String packageName;

  /**
   * <p>Java generation is required if any of the file products is outdated/stale.</p>
   * {@inheritDoc}
   */
  @Override
  protected boolean isReGenerationRequired() {
    return true;
  }

  /**
   * Generates the actual code.
   *
   * @return true if successfully generated
   * @throws MojoExecutionException if code generation fails
   */
  protected abstract boolean generateCode() throws MojoExecutionException;

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean performExecution() throws MojoExecutionException, MojoFailureException {
    boolean result;

    try {
      // Ensure that the outputDirectory exists, but only clear it if does not already
      FileSystemUtilities.createDirectory(getOutputDirectory(), false);

      // Check the system properties.
      logSystemPropertiesAndBasedir();

      // ensure that an ADAMS environment is set
      if ((adams.env.Environment.getEnvironmentClass() == null)
	|| !adams.env.Environment.getEnvironmentClass().getClass().getName().equals(environmentClass)) {
	adams.env.Environment.setEnvironmentClass(Class.forName(environmentClass));
      }

      // generate code
      result = generateCode();

      // Indicate that the output directory was updated.
      getBuildContext().refresh(getOutputDirectory());

      // Add the generated source root to the project, enabling tooling and other plugins to see them.
      addGeneratedSourcesToProjectSourceRoot();
    }
    catch (MojoExecutionException e) {
      throw e;
    }
    catch (Exception e) {
      throw new MojoExecutionException(e.getMessage(), e);
    }

    return result;
  }

  /**
   * Override this method to acquire a List holding all URLs to the sources for which this
   * AbstractJavaGeneratorMojo should generate Java files. Sources are assumed to be in the form given by
   * the {@code sourceType} value.
   *
   * @return A non-null List holding URLs to sources for the code generation.
   */
  @Override
  protected abstract List<URL> getSources();

  /**
   * Adds any directories containing the generated classes to the appropriate Project compilation sources;
   * either {@code TestCompileSourceRoot} or {@code CompileSourceRoot} depending on the exact Mojo implementation
   * of this AbstractJavaGeneratorMojo.
   */
  protected abstract void addGeneratedSourcesToProjectSourceRoot();
}
