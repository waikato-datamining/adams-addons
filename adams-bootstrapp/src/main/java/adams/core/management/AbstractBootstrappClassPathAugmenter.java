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
 * AbstractBootstrappClassPathAugmenter.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package adams.core.management;

import adams.core.io.FileUtils;

/**
 * Ancestor for classpath augmenters that make use of bootstrapp to pull
 * in dependencies.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractBootstrappClassPathAugmenter
  extends AbstractClassPathAugmenter {

  private static final long serialVersionUID = 8816415001041923890L;

  /**
   * Returns whether bootstrapp compresses the directory structure.
   *
   * @return		true if compressed
   */
  protected abstract boolean usesCompressedDirStructure();

  /**
   * Returns the directory that bootstrapp uses to pull in the libraries.
   *
   * @return		the dir above 'target' or 'lib' (if compressed)
   */
  protected abstract String getBootstrappOutputDir();

  /**
   * Returns the actual directory with jars.
   *
   * @return		the directory
   */
  public String getActualLibDir() {
    String 	result;
    String 	prefix;

    if (usesCompressedDirStructure())
      prefix = "";
    else
      prefix = "/target";
    result = getBootstrappOutputDir() + prefix + "/lib";

    return result;
  }

  /**
   * Returns the classpath parts (jars, directories) to add to the classpath.
   *
   * @return the additional classpath parts
   */
  public String[] getClassPathAugmentation() {
    String 	dir;

    dir  = getActualLibDir();
    if (FileUtils.dirExists(dir))
      return new String[]{dir + "/*"};
    else
      return new String[0];
  }
}
