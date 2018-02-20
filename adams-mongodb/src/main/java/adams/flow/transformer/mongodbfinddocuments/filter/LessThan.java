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
 * LessThan.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.mongodbfinddocuments.filter;

import com.mongodb.client.model.Filters;
import org.bson.conversions.Bson;

/**
 * Value associated with the key in the document must be less than the filter value.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class LessThan
  extends AbstractMongoDbKeyValueDocumentFilter {

  private static final long serialVersionUID = 9100548349662348823L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Value associated with the key in the document must less than the filter value.";
  }

  /**
   * Configures the filter.
   *
   * @return		the filter
   */
  @Override
  protected Bson doConfigure() {
    Object	val;
    String	msg;

    m_ValueConversion.setInput(m_Value);
    msg = m_ValueConversion.convert();
    if (msg != null)
      throw new IllegalStateException(msg);
    val = m_ValueConversion.getOutput();
    return Filters.lt(m_Key, val);
  }
}
