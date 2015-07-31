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
 * BoofCVTLD.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.objecttracker;

import adams.core.TechnicalInformation;
import adams.core.TechnicalInformation.Field;
import adams.core.TechnicalInformation.Type;
import adams.core.TechnicalInformationHandler;
import boofcv.abst.tracker.TrackerObjectQuad;
import boofcv.factory.tracker.FactoryTrackerObjectQuad;
import boofcv.struct.image.ImageFloat32;
import boofcv.struct.image.ImageFloat64;
import boofcv.struct.image.ImageSInt16;
import boofcv.struct.image.ImageSInt32;
import boofcv.struct.image.ImageSInt64;
import boofcv.struct.image.ImageSInt8;
import boofcv.struct.image.ImageUInt16;
import boofcv.struct.image.ImageUInt8;

/**
 <!-- globalinfo-start -->
 * Tracking-Learning-Detection (TLD) [1] (a.k.a Predator) object tracker for video sequences.<br>
 * TLD tracks an object which is specified by a user using a rectangle. The description of the object is dynamically updated using P and N constraints.<br>
 * <br>
 * For more information see:<br>
 * Zdenek Kalal (2011). Tracking-Learning-Detection.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- technical-bibtex-start -->
 * <pre>
 * &#64;misc{Kalal2011,
 *    author = {Zdenek Kalal},
 *    month = {April},
 *    note = {Phd Thesis},
 *    organization = {University of Surrey},
 *    title = {Tracking-Learning-Detection},
 *    year = {2011}
 * }
 * </pre>
 * <br><br>
 <!-- technical-bibtex-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BoofCVTLD
  extends AbstractBoofCVObjectTracker
  implements TechnicalInformationHandler {

  private static final long serialVersionUID = 7061565466109634695L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Tracking-Learning-Detection (TLD) [1] (a.k.a Predator) object tracker for video sequences.\n"
        + "TLD tracks an object which is specified by a user using a rectangle. The description of the object is "
        + "dynamically updated using P and N constraints.\n"
        + "\n"
        + "For more information see:\n"
        + getTechnicalInformation();
  }

  /**
   * Returns technical information on autocorrelation.
   *
   * @return		the technical information
   */
  public TechnicalInformation getTechnicalInformation() {
    TechnicalInformation 	result;

    result = new TechnicalInformation(Type.MISC);
    result.setValue(Field.AUTHOR, "Zdenek Kalal");
    result.setValue(Field.TITLE, "Tracking-Learning-Detection");
    result.setValue(Field.ORGANIZATION, "University of Surrey");
    result.setValue(Field.YEAR, "2011");
    result.setValue(Field.MONTH, "April");
    result.setValue(Field.NOTE, "Phd Thesis");

    return result;
  }

  /**
   * Instantiates a new tracker.
   *
   * @return		the tracker
   */
  @Override
  protected TrackerObjectQuad newTracker() {
    switch (m_ImageType) {
      case FLOAT_32:
        return FactoryTrackerObjectQuad.tld(null, ImageFloat32.class);
      case FLOAT_64:
        return FactoryTrackerObjectQuad.tld(null, ImageFloat64.class);
      case SIGNED_INT_16:
        return FactoryTrackerObjectQuad.tld(null, ImageSInt16.class);
      case SIGNED_INT_32:
        return FactoryTrackerObjectQuad.tld(null, ImageSInt32.class);
      case SIGNED_INT_64:
        return FactoryTrackerObjectQuad.tld(null, ImageSInt64.class);
      case SIGNED_INT_8:
        return FactoryTrackerObjectQuad.tld(null, ImageSInt8.class);
      case UNSIGNED_INT_16:
        return FactoryTrackerObjectQuad.tld(null, ImageUInt16.class);
      case UNSIGNED_INT_8:
        return FactoryTrackerObjectQuad.tld(null, ImageUInt8.class);
      default:
        throw new IllegalStateException("Unhandled image type: " + m_ImageType);
    }
  }
}
