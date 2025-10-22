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
 * DJLUtils.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.data.djl;

import ai.djl.engine.Engine;
import ai.djl.pytorch.engine.PtEngine;
import ai.djl.pytorch.engine.PtEngineProvider;

/**
 * Utility functions for DJL.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class DJLUtils {

  /** the environment variable to set the global random seed. */
  public final static String DJL_RANDOM_SEED = "DJL_RANDOM_SEED";

  private static boolean ClassLoaderUtilsExceptionShown = false;

  private static boolean EngineExceptionShown = false;

  /**
   * Sets the random seed to use for the PyTorch engine.
   *
   * @param seed	the seed
   */
  public static void setPyTorchSeed(int seed) {
    Engine.getEngine(PtEngine.ENGINE_NAME).setRandomSeed(seed);
  }

  /**
   * Returns the random seed used by the PyTorch engine.
   *
   * @return		the seed
   */
  public static int getPyTorchSeed() {
    return Engine.getEngine(PtEngine.ENGINE_NAME).getSeed();
  }

  /**
   * Registers the Pytorch engine.
   */
  public static void registerPytorch() {
    Engine.registerEngine(new PtEngineProvider());
  }
}
