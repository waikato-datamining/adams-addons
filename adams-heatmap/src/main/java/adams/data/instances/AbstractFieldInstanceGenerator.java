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
 * AbstractFieldInstanceGenerator.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */

package adams.data.instances;

import weka.core.Attribute;

import java.util.ArrayList;
import java.util.List;

import adams.data.heatmap.Heatmap;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.data.report.Report;
import adams.db.AbstractDatabaseConnection;
import adams.db.DatabaseConnection;

/**
 * Abstract base class for schemes that turn heatmaps into weka.core.Instance
 * objects.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractFieldInstanceGenerator
  extends AbstractInstanceGenerator<Heatmap> {

  /** for serialization. */
  private static final long serialVersionUID = -5751743124545585684L;

  /** fields to add to the output data. */
  protected Field[] m_Fields;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "field", "fields",
	    new Field[0]);
  }

  /**
   * Returns the default database connection.
   *
   * @return		the default database connection
   */
  @Override
  protected AbstractDatabaseConnection getDefaultDatabaseConnection() {
    return new DatabaseConnection();
  }

  /**
   * Sets the targets to add.
   *
   * @param value	the targets
   */
  public void setFields(Field[] value) {
    m_Fields = value;
    reset();
  }

  /**
   * Returns the targets to add.
   *
   * @return		the targets
   */
  public Field[] getFields() {
    return m_Fields;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String fieldsTipText() {
    return "The fields to add to the output.";
  }

  /**
   * Checks whether the number of waves are the same.
   *
   * @param data	the input data
   */
  @Override
  protected void checkHeader(Heatmap data) {
    int		size;

    size = m_OutputHeader.numAttributes();
    if (m_AddDatabaseID)
      size--;

    if (size != m_Fields.length)
      throw new IllegalStateException(
	  "Number of fields and output attributes differ (" + data + "): "
	  + m_Fields.length + " != " + size);
  }
  
  /**
   * Creates an attribute from the specified field.
   * 
   * @param field	the field to turn into an attribute
   * @param names	for appending the field name
   * @return		the Attribute
   */
  protected Attribute createAttribute(Field field, StringBuilder names) {
    ArrayList<String>	attValues;

    // append name
    if (names.length() > 0)
      names.append(",");
    names.append(HeatmapArffUtils.getFieldName(field));

    // create attribute
    if (field.getDataType() == DataType.NUMERIC) {
      return new Attribute(HeatmapArffUtils.getFieldName(field));
    }
    else if (field.getDataType() == DataType.BOOLEAN) {
      attValues = new ArrayList<String>();
      attValues.add(LABEL_FALSE);
      attValues.add(LABEL_TRUE);
      return new Attribute(HeatmapArffUtils.getFieldName(field), attValues);
    }
    else {
      return new Attribute(HeatmapArffUtils.getFieldName(field), (List<String>) null);
    }
  }
  
  /**
   * Adds the fields of the heatmap to the data array.
   * 
   * @param data	the heatmap to get the field values from
   * @param values	the data for the instance
   */
  protected void addFields(Heatmap data, double[] values) {
    int		index;
    Report	report;
    
    if (data.hasReport()) {
      report = data.getReport();
      
      for (Field target: m_Fields) {
	index         = m_OutputHeader.attribute(HeatmapArffUtils.getFieldName(target)).index();
	values[index] = weka.core.Utils.missingValue();
	if (report.hasValue(target)) {
	  if (target.getDataType() == DataType.NUMERIC)
	    values[index] = report.getDoubleValue(target);
	  else if (target.getDataType() == DataType.BOOLEAN)
	    values[index] = m_OutputHeader.attribute(index).indexOfValue((report.getBooleanValue(target) ? LABEL_TRUE : LABEL_FALSE));
	  else
	    values[index] = m_OutputHeader.attribute(index).addStringValue("" + report.getValue(target));
	}
      }
    }
  }
}
