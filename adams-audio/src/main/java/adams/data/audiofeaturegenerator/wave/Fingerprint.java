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
 * Fingerprint.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.data.audiofeaturegenerator.wave;

import adams.data.audio.WaveContainer;
import adams.data.featureconverter.HeaderDefinition;
import adams.data.report.DataType;

import java.util.ArrayList;
import java.util.List;

/**
 * Generates a fingerprint from the Wave data.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Fingerprint
  extends AbstractWaveFeatureGenerator {

  private static final long serialVersionUID = 1096079057750734103L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates a fingerprint from the Wave data.";
  }

  /**
   * Creates the header from a template container.
   *
   * @param cont	the container to act as a template
   * @return		the generated header
   */
  @Override
  public HeaderDefinition createHeader(WaveContainer cont) {
    HeaderDefinition	result;
    byte[]		fingerprint;
    int			i;

    result      = new HeaderDefinition();
    fingerprint = cont.getAudio().getFingerprint();
    for (i = 0; i < fingerprint.length; i++)
      result.add("FP-" + (i+1), DataType.NUMERIC);

    return result;
  }

  /**
   * Performs the actual feature generation.
   *
   * @param cont	the container to process
   * @return		the generated features
   */
  @Override
  public List<Object>[] generateRows(WaveContainer cont) {
    List<Object>[]	result;
    byte[]		fingerprint;
    int			i;

    result      = new List[1];
    result[0]   = new ArrayList<>();
    fingerprint = cont.getAudio().getFingerprint();
    for (i = 0; i < fingerprint.length; i++)
      result[0].add((double) fingerprint[i]);

    return result;
  }
}
