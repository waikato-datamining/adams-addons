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
 * WEKAInstancesToMOAInstances.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.data.conversion;

import adams.core.MOAHelper;
import com.yahoo.labs.samoa.instances.Instances;

/**
 <!-- globalinfo-start -->
 * Converts WEKA instances into MOA instances.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 <!-- options-end -->
 *
 * Converts WEKA instances into MOA instances.
 *
 * @author Corey Sterling (csterlin at waikato dot ac dot nz)
 */
public class WEKAInstancesToMOAInstances
  extends AbstractConversion {

  @Override
  public String globalInfo() {
    return "Converts WEKA instances into MOA instances.";
  }

  @Override
  public Class accepts() {
    return weka.core.Instances.class;
  }

  @Override
  public Class generates() {
    return Instances.class;
  }

  @Override
  protected Object doConvert() throws Exception {
    return MOAHelper.toMOAInstances((weka.core.Instances) m_Input);
  }
}