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
 * WekaDatasetHelper.java
 * Copyright (C) 2013-2017 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.core;

import adams.core.DateFormat;
import adams.core.DateUtils;
import nz.ac.waikato.adams.webservice.weka.Attribute;
import nz.ac.waikato.adams.webservice.weka.Attributes;
import nz.ac.waikato.adams.webservice.weka.Body;
import nz.ac.waikato.adams.webservice.weka.Dataset;
import nz.ac.waikato.adams.webservice.weka.Header;
import nz.ac.waikato.adams.webservice.weka.Instance;
import nz.ac.waikato.adams.webservice.weka.InstanceType;
import nz.ac.waikato.adams.webservice.weka.Instances;
import nz.ac.waikato.adams.webservice.weka.Labels;
import nz.ac.waikato.adams.webservice.weka.Type;
import nz.ac.waikato.adams.webservice.weka.Value;
import nz.ac.waikato.adams.webservice.weka.YesNo;
import weka.classifiers.Evaluation;
import weka.core.DenseInstance;
import weka.core.SparseInstance;
import weka.core.Utils;
import weka.core.Version;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;

/**
 * Helper class for converting to and from xrff datasets
 * 
 * @author msf8
 * 
 */
public class WekaDatasetHelper {
  
  /** for formatting dates. */
  protected static DateFormat m_DateFormat = DateUtils.getTimestampFormatterMsecs();

  /**
   * static class that converts an instances object into an xrff dataset
   * 
   * @param inst 	 instances object to be converted
   * @return xrff dataset
   */
  public static Dataset fromInstances(weka.core.Instances inst) {
    Dataset xrffDataset = new Dataset();
    Header header = new Header();
    Body body = new Body();
    xrffDataset.setBody(body);
    xrffDataset.setHeader(header);

    xrffDataset.setName(inst.relationName());
    xrffDataset.setVersion(Version.VERSION);

    Attributes attributes = new Attributes();
    header.setAttributes(attributes);

    int classIndex = inst.classIndex();
    for (int i = 0; i < inst.numAttributes(); i++) {
      Attribute attribute = new Attribute();
      weka.core.Attribute wekaAttribute = inst.attribute(i);
      attribute.setName(wekaAttribute.name());
      // setting string as default for now
      Type type = Type.STRING;
      switch (wekaAttribute.type()) {
	case weka.core.Attribute.NOMINAL:
	  type = Type.NOMINAL;
	  // need to deal with labels for nominal
	  Labels l = new Labels();
	  attribute.setLabels(l);
	  Enumeration enm = wekaAttribute.enumerateValues();
	  while (enm.hasMoreElements()) {
	    l.getLabel().add((String) enm.nextElement());
	  }
	  break;
	case weka.core.Attribute.NUMERIC:
	  type = Type.NUMERIC;
	  break;
	case weka.core.Attribute.DATE:
	  type = Type.DATE;
	  attribute.setFormat(wekaAttribute.getDateFormat());
	  break;
	case weka.core.Attribute.STRING:
	  type = Type.STRING;
	  break;
	case weka.core.Attribute.RELATIONAL:
	  type = Type.RELATIONAL;
	  break;
      }
      attribute.setType(type);
      if (i == classIndex) {
	attribute.setClassAttribute(YesNo.YES);
      }
      attribute.setWeight(wekaAttribute.weight());
      // not sure about setting format
      // temp.setFormat(value) -- dates
      attributes.getAttribute().add(attribute);
      
    }
    nz.ac.waikato.adams.webservice.weka.Instances instances = new nz.ac.waikato.adams.webservice.weka.Instances();
    body.setInstances(instances);
    for (int i = 0; i < inst.size(); i++) {
      weka.core.Instance wekaInstance = inst.get(i);
      nz.ac.waikato.adams.webservice.weka.Instance instanceToAdd = new nz.ac.waikato.adams.webservice.weka.Instance();
      instanceToAdd.setInstanceWeight(wekaInstance.weight()); 
      boolean isSparse = (wekaInstance instanceof SparseInstance);
      instanceToAdd.setInstanceType(InstanceType.NORMAL);
      if (isSparse)
	instanceToAdd.setInstanceType(InstanceType.SPARSE);
      for (int v = 0; v < wekaInstance.numValues(); v++) {
	Value value = new Value();
	int index = wekaInstance.index(v);
	String attributeValue;
	if(wekaInstance.attribute(index).isNumeric()) {
	  Double val =  wekaInstance.value(index);
	  attributeValue = val.toString();
	}
	else {
	  attributeValue = wekaInstance.stringValue(index);
	}
	if (attributeValue != null)
	  value.setValue(attributeValue);
	else
	  value.setValueMissing(YesNo.YES);
	if (isSparse)
	  value.setValueIndex(index); 
	instanceToAdd.getValue().add(value);
      }
      instances.getInstance().add(instanceToAdd);
    }

    return xrffDataset;
  }

  /**
   * static class that converts an xrff dataset object into a weka instances object
   * @param d		dataset to convert
   * @return		A weka instances object representing the xrff dataset
   */
  public static weka.core.Instances toInstances(Dataset d) {
    weka.core.Instances data;
    int classIndex;
    ArrayList<weka.core.Attribute> attributes;
    ArrayList<String> attributeVals;
    double[] values; 
    boolean isSparse;
    Attribute a;
    int	n;

    attributes = new ArrayList<weka.core.Attribute>();
    classIndex = -1;

    for (n = 0; n < d.getHeader().getAttributes().getAttribute().size(); n++) {
      a = d.getHeader().getAttributes().getAttribute().get(n);
      if (a.getClassAttribute() == YesNo.YES)
	classIndex = n;
      weka.core.Attribute toAdd;
      switch (a.getType()) {
	case NOMINAL:
	  attributeVals = new ArrayList<String>();
	  for(String s: a.getLabels().getLabel()) {
	    attributeVals.add(s);
	  }
	  toAdd = new weka.core.Attribute   (a.getName(), attributeVals);
	  toAdd.setWeight(a.getWeight());
	  attributes.add(toAdd);
	  break;
	  
	case NUMERIC:
	   toAdd = new weka.core.Attribute(a.getName());
	  toAdd.setWeight(a.getWeight());
	  attributes.add(toAdd);
	  break;

	case DATE:
	  toAdd = new weka.core.Attribute(a.getName(), a.getFormat());
	  toAdd.setWeight(a.getWeight());
	  attributes.add(toAdd);
	  break;

	case STRING:
	  toAdd = new weka.core.Attribute(a.getName(), (ArrayList<String>)null);
	  toAdd.setWeight(a.getWeight());
	  attributes.add(toAdd);
	  break;

	case RELATIONAL:
	  //ignoring this type for now
	  break;
      }  
    }

    data = new weka.core.Instances(d.getName() , attributes, 0);
    for(nz.ac.waikato.adams.webservice.weka.Instance i : d.getBody().getInstances().getInstance()) {
      values = new double[i.getValue().size()];
      isSparse = (i.getInstanceType() == InstanceType.SPARSE);
      for(n = 0; n < i.getValue().size(); n++) {
	values[n] = Utils.missingValue();
	switch (d.getHeader().getAttributes().getAttribute().get(n).getType()) {
	  case NOMINAL:
	    //need to get list of values
	    values[n] = attributes.get(n).indexOfValue(i.getValue().get(n).getValue());
	    break;

	  case NUMERIC:
	    values[n] = new Double(i.getValue().get(n).getValue());
	    break;

	  case RELATIONAL:
	    //ignoring this
	    break;

	  case DATE:
	    try {
	      values[n] = data.attribute(n).parseDate(i.getValue().get(n).getValue());
	    }
	    catch (ParseException e) {
	      values[n] = Utils.missingValue();
	      e.printStackTrace();
	    }
	    break;

	  case STRING:
	    values[n] = data.attribute(n).addStringValue(i.getValue().get(n).getValue());
	    break;
	}
      }
      if (isSparse)
	data.add( new SparseInstance(1.0, values));
      else
	data.add( new DenseInstance(1.0, values));
    }

    data.setClassIndex(classIndex);

    return data;
  }

  /**
   * For outputting the dataset in debug mode.
   * 
   * @param dataset	the dataset to output
   */
  public static String datasetToString(Dataset dataset) {
    StringBuilder dataSetString = new StringBuilder();
    for(Attribute a : dataset.getHeader().getAttributes().getAttribute()) {
      dataSetString.append(a.getName() + "\t" + a.getType() + "\t");
      if(a.getType() == Type.NOMINAL) {
	dataSetString.append("{");
	int count = 0;
	for(String s : a.getLabels().getLabel()) {
	  if(count == 0)
	    dataSetString.append(s);
	  else
	    dataSetString.append(", " + s);
	  count ++;
	}
	dataSetString.append("}");
      }
      dataSetString.append("\n");
    }
    
    for(Instance i: dataset.getBody().getInstances().getInstance()) {
      dataSetString.append("Instance: \t ");
      for(Value v: i.getValue()) {
	dataSetString.append(v.getValue() + " ");
      }
      dataSetString.append("\n");
    }
    return dataSetString.toString();
  }

  /**
   * Adds an attribute to the dataset.
   * 
   * @param dataset	the dataset to add the attribute to
   * @param name	the name of the attribute
   * @param type	the type of the attribute
   */
  public static void addAttribute(Dataset dataset, String name, Type type) {
    Attributes	atts;
    Attribute	att;

    atts = dataset.getHeader().getAttributes();
    
    att = new Attribute();
    att.setName(name);
    att.setType(type);
    atts.getAttribute().add(att);
  }
  
  /**
   * Adds the string value to the {@link Instance}.
   * 
   * @param inst	the instance to add the value to
   * @param index	the attribute index
   * @param value	the value to set
   */
  public static void addValue(Instance inst, int index, String value) {
    Value	val;

    val = new Value();
    val.setValueIndex(0);
    val.setValue(value);
    inst.getValue().add(val);
  }
  
  /**
   * Adds the double value to the {@link Instance}.
   * 
   * @param inst	the instance to add the value to
   * @param index	the attribute index
   * @param value	the value to set
   */
  public static void addValue(Instance inst, int index, double value) {
    Value	val;

    val = new Value();
    val.setValueIndex(0);
    val.setValue("" + value);
    inst.getValue().add(val);
  }
  
  /**
   * Adds a statistic as key/value pair to the dataset as new data row.
   * 
   * @param dataset	the dataset to add the row to
   * @param name	the name of the statistic (key)
   * @param value	the value of the statistic
   */
  public static void addStatistic(Dataset dataset, String name, double value) {
    Instance	inst;
    
    inst = new Instance();
    inst.setInstanceType(InstanceType.NORMAL);
    inst.setInstanceWeight(1.0);
    dataset.getBody().getInstances().getInstance().add(inst);
    
    addValue(inst, 0, name);
    addValue(inst, 1, value);
  }
  
  /**
   * Turns the evaluation object into a {@link Dataset} structure.
   * 
   * @param eval	the evaluation to convert
   * @return		the generated data structure
   */
  public static Dataset evaluationToDataset(Evaluation eval) {
    Dataset	result;
    
    result = new Dataset();
    result.setName(eval.getHeader().relationName());
    result.setVersion(getDateFormat().format(new Date()));
    
    result.setHeader(new Header());
    result.getHeader().setAttributes(new Attributes());
    addAttribute(result, "Statistic", Type.STRING);
    addAttribute(result, "Value", Type.NUMERIC);
    result.setBody(new Body());
    result.getBody().setInstances(new Instances());
 
    if (eval.getHeader().classAttribute().isNominal()) {
      addStatistic(result, "Percent correct", eval.pctCorrect());
      addStatistic(result, "Percent incorrect", eval.pctIncorrect());
      addStatistic(result, "Num correct", eval.correct());
      addStatistic(result, "Num incorrect", eval.incorrect());
      addStatistic(result, "Kappa", eval.kappa());
      // TODO complexity
    }
    else {
      try {
	addStatistic(result, "Correlation coefficient", eval.correlationCoefficient());
      }
      catch (Exception e) {
	addStatistic(result, "Correlation coefficient", Double.NaN);
      }
    }
    addStatistic(result, "Mean absolute error", eval.meanAbsoluteError());
    addStatistic(result, "Root mean squared error", eval.rootMeanSquaredError());
    try {
      addStatistic(result, "Relative absolute error", eval.relativeAbsoluteError());
    }
    catch (Exception e) {
      addStatistic(result, "Relative absolute error", Double.NaN);
    }
    addStatistic(result, "Root relative squared error", eval.rootRelativeSquaredError());
    // TODO coverage
    addStatistic(result, "Unclassified instances", eval.unclassified());
    addStatistic(result, "Total number of instances", eval.numInstances());
    
    return result;
  }
  
  /**
   * Returns the {@link DateFormat} instance to use.
   * 
   * @return		the date formatter
   */
  public static DateFormat getDateFormat() {
   return m_DateFormat;
  }
}
