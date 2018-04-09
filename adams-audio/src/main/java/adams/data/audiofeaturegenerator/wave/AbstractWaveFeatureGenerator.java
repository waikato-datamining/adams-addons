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
 * AbstractWaveFeatureGenerator.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.data.audiofeaturegenerator.wave;

import adams.data.audio.WaveContainer;
import adams.data.audiofeaturegenerator.AbstractAudioFeatureGenerator;

/**
 * Ancestor for Wave feature generators.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractWaveFeatureGenerator
  extends AbstractAudioFeatureGenerator<WaveContainer> {

  private static final long serialVersionUID = 7139274188010786452L;
}
