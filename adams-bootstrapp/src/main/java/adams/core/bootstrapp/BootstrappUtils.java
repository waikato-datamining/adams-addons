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
 * BootstrappUtils.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package adams.core.bootstrapp;

import adams.core.Properties;
import adams.core.Utils;
import adams.core.io.PlaceholderDirectory;
import adams.core.io.PlaceholderFile;
import com.github.fracpete.bootstrapp.Main;

import java.util.Arrays;

/**
 * Helper class for Bootstrapp related operations.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class BootstrappUtils {

  /** the key for the dependencies (comma-separated 'group:artifact:version'). */
  public final static String KEY_DEPENDENCIES = "Dependencies";

  /** the key for the excluded dependencies (comma-separated 'group:artifact:'). */
  public final static String KEY_EXCLUSIONS = "Exclusions";

  /** the key for the repositories (comma-separated 'id;name;url'). */
  public final static String KEY_REPOSITORIES = "Repositories";

  /** the key for the output dir. */
  public final static String KEY_OUTPUTDIR = "OutputDir";

  /** the key for the maven user settings. */
  public final static String KEY_MAVENUSERSETTINGS = "MavenUserSettings";

  /** the key for the alternative maven home. */
  public final static String KEY_MAVENHOME = "MavenHome";

  /** the key for whether to compress the directory structure. */
  public final static String KEY_COMPRESSDIRSTRUCTURE = "CompressDirStructure";

  /**
   * Instantiates a bootstrapp Main object using the provided properties.
   *
   * @param props	the properties to use
   * @return		the configured instance
   * @throws Exception	if congfiguration fails
   */
  public static Main propsToBootstrapp(Properties props) throws Exception {
    Main	result;

    result = new Main();

    if (!props.hasKey(KEY_DEPENDENCIES))
      throw new IllegalArgumentException("Missing property for dependencies: " + KEY_DEPENDENCIES);
    result.dependencies(Arrays.asList(props.getProperty(KEY_DEPENDENCIES).split(",")));

    if (props.hasKey(KEY_EXCLUSIONS))
      result.exclusions(Arrays.asList(props.getProperty(KEY_EXCLUSIONS).split(",")));

    if (!props.hasKey(KEY_OUTPUTDIR))
      throw new IllegalArgumentException("Missing property for output directory: " + KEY_OUTPUTDIR);
    result.outputDir(new PlaceholderDirectory(props.getPath(KEY_OUTPUTDIR)).getAbsoluteFile());

    if (props.hasKey(KEY_REPOSITORIES))
      result.repositories(Arrays.asList(props.getProperty(KEY_REPOSITORIES).split(",")));

    if (props.hasKey(KEY_MAVENUSERSETTINGS))
      result.mavenUserSettings(new PlaceholderFile(props.getPath(KEY_MAVENUSERSETTINGS)).getAbsoluteFile());

    if (props.hasKey(KEY_MAVENHOME))
      result.mavenHome(new PlaceholderDirectory(props.getPath(KEY_MAVENHOME)).getAbsoluteFile());

    if (props.hasKey(KEY_COMPRESSDIRSTRUCTURE))
      result.compressDirStructure(props.getBoolean(KEY_COMPRESSDIRSTRUCTURE, false));

    return result;
  }

  /**
   * Turns the bootstrapp Main object into a Properties object.
   *
   * @param main	the main instance to convert
   * @return		the generated properties
   */
  public static Properties bootstrappToProps(Main main) {
    Properties	result;

    result = new Properties();
    result.setProperty(KEY_DEPENDENCIES, Utils.flatten(main.getDependencies(), ","));
    if (main.getExclusions() != null)
      result.setProperty(KEY_EXCLUSIONS, Utils.flatten(main.getExclusions(), ","));
    if (main.getRepositories() != null)
      result.setProperty(KEY_REPOSITORIES, Utils.flatten(main.getRepositories(), ","));
    result.setProperty(KEY_OUTPUTDIR, main.getOutputDir().getAbsolutePath());
    if (main.getMavenUserSettings() != null)
      result.setProperty(KEY_MAVENUSERSETTINGS, main.getMavenUserSettings().getAbsolutePath());
    if (main.getMavenHome() != null)
      result.setProperty(KEY_MAVENHOME, main.getMavenHome().getAbsolutePath());
    if (main.getCompressDirStructure())
      result.setBoolean(KEY_COMPRESSDIRSTRUCTURE, main.getCompressDirStructure());

    return result;
  }
}
