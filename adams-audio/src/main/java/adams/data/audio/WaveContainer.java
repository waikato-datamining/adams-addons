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
 * WaveContainer.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.data.audio;

import com.musicg.wave.Wave;

/**
 * Container for WAV data.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class WaveContainer
  extends AbstractAudioContainer<Wave>
  implements RawAudioDataSupporter {

  private static final long serialVersionUID = -8772703305133388148L;

  /**
   * Returns a clone of the content.
   *
   * @return		the clone
   */
  @Override
  protected Wave cloneContent() {
    return new Wave(getContent().getWaveHeader(), getContent().getBytes());
  }

  /**
   * Returns the raw data.
   *
   * @return		the raw data
   */
  public byte[] getRawData() {
    return getContent().getBytes();
  }
}
