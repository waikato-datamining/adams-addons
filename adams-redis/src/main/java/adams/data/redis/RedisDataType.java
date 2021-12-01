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
 * RedisDataType.java
 * Copyright (C) 2021 University of Waikato, Hamilton, New Zealand
 */

package adams.data.redis;

import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.StringCodec;

/**
 * Enumeration of support data types.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public enum RedisDataType {
  STRING(StringCodec.class, String.class),
  BYTE_ARRAY(ByteArrayCodec.class, byte[].class);

  /** the associated codec class. */
  private Class m_CodecClass;

  /** the data class. */
  private Class m_DataClass;

  /**
   * Initializes the enum entry.
   *
   * @param codecClass	the codec class to associate
   */
  private RedisDataType(Class codecClass, Class dataClass) {
    m_CodecClass = codecClass;
    m_DataClass = dataClass;
  }

  /**
   * Returns the associated codec class.
   *
   * @return		the codec class
   */
  public Class getCodecClass() {
    return m_CodecClass;
  }

  /**
   * Returns the associated data class.
   *
   * @return		the data class
   */
  public Class getDataClass() {
    return m_DataClass;
  }
}
