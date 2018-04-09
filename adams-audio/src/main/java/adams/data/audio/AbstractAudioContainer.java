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
 * AbstractAudioContainer.java
 * Copyright (C) 2018 University of Waikato, Hamilton, New Zealand
 */
package adams.data.audio;

import adams.data.container.AbstractSimpleContainer;

/**
 * Ancestor for various audio format containers.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @param <T> the type of audio to handle
 */
public abstract class AbstractAudioContainer<T>
  extends AbstractSimpleContainer<T> {

  /** for serialization. */
  private static final long serialVersionUID = 2095394708673239275L;

  /** the key in the report for the URL. */
  public final static String URL = "URL";

  /** the key in the report for the file (full path). */
  public final static String FILE = "File";

  /**
   * Sets the audio to use.
   *
   * @param value	the audio
   */
  public void setAudio(T value) {
    setContent(value);
  }

  /**
   * Returns the stored audio.
   *
   * @return		the audio
   */
  public T getAudio() {
    return getContent();
  }
}
