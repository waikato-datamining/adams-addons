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
 * RenjinClassPathAugmenter.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package adams.core.management;

import adams.env.Environment;

/**
 * Classpath augmenter for Renjin (uses bootstrapp to pull in dependencies).
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class RenjinClassPathAugmenter
  extends AbstractBootstrappClassPathAugmenter {

  private static final long serialVersionUID = 7003445505711413086L;

  /** the directory to use in the ADAMS home dir. */
  public final static String DIR = "renjin";

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Classpath augmenter for Renjin (uses bootstrapp to pull in dependencies).";
  }

  /**
   * Returns whether bootstrapp compresses the directory structure.
   *
   * @return true if compressed
   */
  @Override
  protected boolean usesCompressedDirStructure() {
    return true;
  }

  /**
   * Returns the directory that bootstrapp uses to pull in the libraries.
   *
   * @return the dir above 'target' or 'lib' (if compressed)
   */
  @Override
  public String getBootstrappOutputDir() {
    return Environment.getInstance().getHome() + "/" + DIR;
  }
}
