/*
 * InstancesAttributeType.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package weka.djl;

import weka.core.Attribute;

/**
 * Attribute types in Instances objects.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public enum InstancesAttributeType {
  NUMERIC,
  NOMINAL,
  STRING,
  DATE;

  /**
   * Turns the Weka attribute type into the correspond enum one.
   *
   * @param type	the type to convert
   * @return		the corresponding enum
   * @throws IllegalArgumentException if unsupported type
   */
  public static InstancesAttributeType toType(int type) {
    switch (type) {
      case Attribute.NUMERIC:
	return NUMERIC;
      case Attribute.DATE:
	return DATE;
      case Attribute.NOMINAL:
	return NOMINAL;
      case Attribute.STRING:
	return STRING;
      default:
	throw new IllegalArgumentException("Unsupported attribute type: " + Attribute.typeToString(type));
    }
  }
}
