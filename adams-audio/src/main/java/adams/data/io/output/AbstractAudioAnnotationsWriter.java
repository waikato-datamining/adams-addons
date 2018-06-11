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
 * AbstractAudioAnnotationsWriter.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.data.io.output;

import adams.core.ClassLister;
import adams.data.audioannotations.AudioAnnotations;

/**
 * Ancestor for annotations writers.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractAudioAnnotationsWriter
  extends AbstractDataContainerWriter<AudioAnnotations> {

  private static final long serialVersionUID = -6258698538028259568L;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    m_OutputIsFile = true;
  }

  /**
   * Returns a list with classnames of writers.
   *
   * @return the writer classnames
   */
  public static String[] getWriters() {
    return ClassLister.getSingleton().getClassnames(AbstractAudioAnnotationsWriter.class);
  }
}
