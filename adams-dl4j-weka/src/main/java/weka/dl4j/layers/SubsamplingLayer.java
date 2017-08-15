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
 *    SubsamplingLayer.java
 *    Copyright (C) 2016-2017 University of Waikato, Hamilton, New Zealand
 *
 */
package weka.dl4j.layers;

import java.io.Serializable;

/**
 * A version of DeepLearning4j's SubsamplingLayer that implements WEKA option handling.
 *
 * @author Christopher Beckham
 * @author Eibe Frank
 *
 * @version $Revision: 11711 $
 */
public class SubsamplingLayer extends org.deeplearning4j.nn.conf.layers.SubsamplingLayer implements Serializable{

  /** The ID used to serialize this class. */
  private static final long serialVersionUID = -699034028619492301L;

  /**
   * Global info.
   *
   * @return string describing this class.
   */
  public String globalInfo() {
    return "A subsampling layer from DeepLearning4J.";
  }

  /**
   * Constructor for setting some defaults.
   */
  public SubsamplingLayer() {
    setLayerName("Subsampling layer");
    setKernelSize(new int[] {1, 1});
    setStride(new int[] {2, 2});
    setPadding(new int[] {0, 0});
    setPoolingType(org.deeplearning4j.nn.conf.layers.PoolingType.MAX);
  }
}