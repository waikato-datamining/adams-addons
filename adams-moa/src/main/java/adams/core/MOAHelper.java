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
 * MOAHelper.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */
package adams.core;

import com.yahoo.labs.samoa.instances.Attribute;
import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.instances.InstanceImpl;
import com.yahoo.labs.samoa.instances.Instances;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Helper class for MOA related stuff.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MOAHelper {

  /**
   * Fixes the votes array if the length differs from the number of class
   * labels defined in the header information of the dataset.
   *
   * @param votes	the votes to fix
   * @param inst	the instance to get the dataset information from
   * @return		the (potentially) fixed votes array
   */
  public static double[] fixVotes(double[] votes, Instance inst) {
    double[]	result;

    // no class attribute information, can't do anything
    if (inst.classIndex() == -1)
      return votes;

    // nothing to fix
    if (votes.length == inst.numClasses())
      return votes;

    result = new double[inst.numClasses()];
    System.arraycopy(votes, 0, result, 0, votes.length);

    return result;
  }

  /**
   * Converts a set of WEKA instances into a set of MOA instances.
   *
   * @param instances   The WEKA instances to convert.
   * @return            The MOA instances.
   */
  public static Instances toMOAInstances(weka.core.Instances instances) {
    // Null converts to null
    if (instances == null)
      return null;

    // Convert the array of attributes
    Attribute[] moaAttributes = new Attribute[instances.numAttributes()];
    for (int i = 0; i < instances.numAttributes(); i++)
      moaAttributes[i] = toMOAAttribute(instances.attribute(i));

    // Create the instances
    Instances moaInstances = new Instances(instances.relationName(), moaAttributes, instances.numInstances());
    moaInstances.setClassIndex(instances.classIndex());

    // Convert each instance
    for (int i = 0; i < instances.numInstances(); i++) {
      Instance moaInstance = toMOAInstance(instances.get(i));
      moaInstance.setDataset(moaInstances);
      moaInstances.add(moaInstance);
    }

    return moaInstances;
  }

  /**
   * Converts a WEKA instance into a MOA instance.
   *
   * @param inst            The WEKA instance to convert.
   * @return                A MOA instance.
   */
  public static Instance toMOAInstance(weka.core.Instance inst) {
    // Null converts to null
    if (inst == null)
      return null;

    // Create the MOA instance
    return new InstanceImpl(inst.weight(), inst.toDoubleArray());
  }

  /**
   * Converts a WEKA attribute to a MOA attribute. Only handles nominal,
   * numeric and date attributes.
   *
   * @param attribute   The WEKA attribute to convert.
   * @return            The MOA attribute.
   */
  public static Attribute toMOAAttribute(weka.core.Attribute attribute) {
    // Null converts to null
    if (attribute == null)
      return null;

    if (attribute.isNumeric())
      return new Attribute(attribute.name());
    else if (attribute.isNominal()) {
      // Get the list of nominal values
      List<String> nominalValues = new ArrayList<>(attribute.numValues());
      Enumeration<Object> nominalValueEnumeration = attribute.enumerateValues();
      while (nominalValueEnumeration.hasMoreElements())
        nominalValues.add((String) nominalValueEnumeration.nextElement());

      return new Attribute(attribute.name(), nominalValues);
    } else if (attribute.isDate())
      return new Attribute(attribute.name(), attribute.getDateFormat());
    else
      throw new IllegalArgumentException("toMOAAttribute can only handle numeric," +
        "nominal and date attributes.");
  }

  /**
   * Converts a set of MOA instances into a set of WEKA instances.
   *
   * @param instances   The instances to convert.
   * @return            The WEKA instances.
   */
  public static weka.core.Instances toWEKAInstances(Instances instances) {
    // Null converts to null
    if (instances == null)
      return null;

    // Convert the attributes
    ArrayList<weka.core.Attribute> wekaAttributes = new ArrayList<>(instances.numAttributes());
    for (int i = 0; i < instances.numAttributes(); i++)
      wekaAttributes.add(toWEKAAttribute(instances.attribute(i)));

    // Create the instances
    weka.core.Instances wekaInstances = new weka.core.Instances(instances.getRelationName(), wekaAttributes, instances.numInstances());
    wekaInstances.setClassIndex(instances.classIndex());

    // Convert each instance
    for (int i = 0; i < instances.numInstances(); i++) {
      weka.core.Instance wekaInstance = toWEKAInstance(instances.get(i));
      wekaInstance.setDataset(wekaInstances);
      wekaInstances.add(wekaInstance);
    }

    return wekaInstances;
  }

  /**
   * Converts a MOA instance into a WEKA instance.
   *
   * @param inst            The MOA instance to convert.
   * @return                A WEKA instance.
   */
  public static weka.core.Instance toWEKAInstance(Instance inst) {
    // Null converts to null
    if (inst == null)
      return null;

    // Convert the instance
    return new weka.core.DenseInstance(inst.weight(), inst.toDoubleArray());
  }

  /**
   * Converts a MOA attribute into a WEKA attribute.
   *
   * @param attribute   The MOA attribute to convert.
   * @return            The WEKA attribute.
   */
  public static weka.core.Attribute toWEKAAttribute(Attribute attribute) {
    // Null converts to null
    if (attribute == null)
      return null;

    if (attribute.isNumeric())
      return new weka.core.Attribute(attribute.name());
    else if (attribute.isNominal()) {
      return new weka.core.Attribute(attribute.name(), attribute.getAttributeValues());
    } else {
      throw new IllegalArgumentException("toWEKAAttribute can only handle numeric and" +
        "nominal attributes");
    }
  }

  /**
   * Checks to see if the headers for two instances are equal.
   *
   * @param inst1   One instance header.
   * @param inst2   The other instance header.
   * @return        Whether the headers are the same.
   */
  public static boolean equalHeaders(Instances inst1, Instances inst2) {
    // Not equal if either is null
    if (inst1 == null || inst2 == null)
      return false;

    // Must have same class index
    if (inst1.classIndex() != inst2.classIndex())
      return false;

    // Must have the same number of attributes
    if (inst1.numAttributes() != inst2.numAttributes())
      return false;

    // All attributes must be equal
    for (int i = 0; i < inst1.numAttributes(); i++) {
      if (!equalAttributes(inst1.attribute(i), inst2.attribute(i)))
        return false;
    }

    // All tests passed
    return true;
  }

  /**
   * Checks to see if both attributes are the same.
   *
   * @param attr1   One attribute to check.
   * @param attr2   The other attribute to check.
   * @return        Whether the attributes are the same.
   */
  public static boolean equalAttributes(Attribute attr1, Attribute attr2) {
    // If either attribute is null, they are not the same
    if (attr1 == null || attr2 == null)
      return false;

    // Check there strings are equal
    return attr1.toString().equals(attr2.toString());
  }
}
