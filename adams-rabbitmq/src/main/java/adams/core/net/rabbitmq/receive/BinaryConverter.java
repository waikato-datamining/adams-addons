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
 * BinaryConverter.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.core.net.rabbitmq.receive;

import adams.core.MessageCollection;
import adams.core.SerializationHelper;
import adams.flow.core.Unknown;

import java.io.ByteArrayInputStream;

/**
 * Uses serialization.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class BinaryConverter
  extends AbstractConverter {

  private static final long serialVersionUID = 1775246651405193885L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Uses serialization.";
  }

  /**
   * Returns the classes that the converter accepts.
   *
   * @return		the classes
   */
  @Override
  public Class generates() {
    return Unknown.class;
  }

  /**
   * Converts the payload.
   *
   * @param payload	the payload
   * @param errors	for recording errors
   * @return		null if failed to convert, otherwise byte array
   */
  @Override
  protected Object doConvert(byte[] payload, MessageCollection errors) {
    ByteArrayInputStream bis;

    try {
      bis = new ByteArrayInputStream(payload);
      return SerializationHelper.read(bis);
    }
    catch (Exception e) {
      errors.add("Failed to deserialize data!", e);
      return null;
    }
  }
}
