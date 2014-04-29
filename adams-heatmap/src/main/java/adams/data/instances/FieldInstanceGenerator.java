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
 * FieldInstanceGenerator.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */

package adams.data.instances;

import java.util.ArrayList;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;
import adams.data.heatmap.Heatmap;
import adams.data.report.Field;

/**
 <!-- globalinfo-start -->
 * A generator for turning fields of a heatmap's report into weka.core.Instance objects.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 *
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 * <pre>-add-db-id (property: addDatabaseID)
 * &nbsp;&nbsp;&nbsp;If set to true, then the database ID will be added to the output.
 * </pre>
 *
 * <pre>-add-herd-number (property: addHerdNumber)
 * &nbsp;&nbsp;&nbsp;If set to true, then the herd number will be added to the output.
 * </pre>
 *
 * <pre>-add-cow-number (property: addCowNumber)
 * &nbsp;&nbsp;&nbsp;If set to true, then the cow number will be added to the output.
 * </pre>
 *
 * <pre>-field &lt;adams.data.report.Field&gt; [-field ...] (property: fields)
 * &nbsp;&nbsp;&nbsp;The fields to add to the output.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FieldInstanceGenerator
  extends AbstractFieldInstanceGenerator {

  /** for serialization. */
  private static final long serialVersionUID = 7579845592900079095L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return
        "A generator for turning fields of a heatmap's report "
      + "into weka.core.Instance objects.";
  }

  /**
   * Generates the header of the output data.
   *
   * @param data	the input data
   */
  protected void generateHeader(Heatmap data) {
    ArrayList<Attribute>	atts;
    StringBuilder		name;

    atts = new ArrayList<Attribute>();

    // fields
    name = new StringBuilder();
    for (Field target: m_Fields)
      atts.add(createAttribute(target, name));

    m_OutputHeader = new Instances(getClass().getName() + "-" + name.toString(), atts, 0);
  }

  /**
   * Generates the actual data.
   *
   * @param data	the input data to transform
   * @return		the generated data
   */
  protected Instance generateOutput(Heatmap data) {
    Instance		result;
    double[]		values;
    int			i;

    values = new double[m_OutputHeader.numAttributes()];

    for (i = 0; i < values.length; i++)
      values[i] = Utils.missingValue();

    // fields
    addFields(data, values);

    result = new DenseInstance(1.0, values);
    result.setDataset(m_OutputHeader);

    return result;
  }
}
