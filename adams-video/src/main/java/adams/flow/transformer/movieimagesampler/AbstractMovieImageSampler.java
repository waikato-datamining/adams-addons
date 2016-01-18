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

/**
 * AbstractMovieImageSampler.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.movieimagesampler;

import adams.core.Stoppable;
import adams.core.option.AbstractOptionHandler;
import adams.data.image.AbstractImageContainer;

import java.io.File;

/**
 * Ancestor for classes that sample images from movies.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractMovieImageSampler<T extends AbstractImageContainer>
  extends AbstractOptionHandler
  implements Stoppable {

  private static final long serialVersionUID = -8088693094272652760L;

  /** whether the sampler was stopped. */
  protected boolean m_Stopped;

  /**
   * Returns the type of image container the sample generates.
   *
   * @return		the image container class
   */
  public abstract Class generates();

  /**
   * Performs some checks on the movie file.
   * <br><br>
   * Default implementation only ensures that the file exists and is not a
   * directory.
   *
   * @param file	the file to check
   */
  protected void check(File file) {
    if (!file.exists())
      throw new IllegalStateException("Movie does not exist: " + file);
    if (file.isDirectory())
      throw new IllegalStateException("Movie file points to a directory: " + file);
  }

  /**
   * Samples images from a movie file.
   *
   * @param file	the movie to sample
   * @return		the samples, null if failed to sample
   */
  protected abstract T[] doSample(File file);

  /**
   * Samples images from a movie file.
   *
   * @param file	the movie to sample
   * @return		the samples, null if failed to sample
   */
  public T[] sample(File file) {
    T[]		result;

    m_Stopped = false;

    check(file);
    result = doSample(file);

    if (m_Stopped)
      result = null;

    return result;
  }

  /**
   * Stops the execution.
   */
  public void stopExecution() {
    m_Stopped = true;
  }
}
