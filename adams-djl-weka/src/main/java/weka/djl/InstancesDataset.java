/*
 * InstancesDataset.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package weka.djl;

import ai.djl.basicdataset.tabular.TabularDataset;
import ai.djl.util.Progress;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import weka.core.Instance;
import weka.core.Instances;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * {@code InstancesDataset} represents the dataset that is stored in an .arff[.gz] file.
 * Only supports NUMERIC, NOMINAL, STRING and DATE attributes.
 * By default, DATE and STRING attributes get ignored.
 * DATE attributes can be treated as NUMERIC ones: get parsed
 * and the epoch time is stored as NUMERIC string.
 * STRING attributes can be treated as NOMINAL ones.
 * Ignored columns, explicit or via regexps, should be set first.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class InstancesDataset extends TabularDataset {

  protected Instances data;
  protected JsonObject structure;

  protected InstancesDataset(InstancesBuilder<?> builder) {
    super(builder);
    data = builder.data;
    structure = builder.toJson();
  }

  /** {@inheritDoc} */
  @Override
  public String getCell(long rowIndex, String featureName) {
    Instance record = data.instance(Math.toIntExact(rowIndex));
    int index = data.attribute(featureName).index();
    if (data.attribute(index).isNumeric())
      return "" + record.value(index);
    else
      return record.stringValue(index);
  }

  /** {@inheritDoc} */
  @Override
  protected long availableSize() {
    return data.size();
  }

  /** {@inheritDoc} */
  @Override
  public void prepare(Progress progress) throws IOException {
    prepareFeaturizers();
  }

  /**
   * Creates a builder to build a {@link InstancesDataset}.
   *
   * @return a new builder
   */
  public static InstancesBuilder<?> builder() {
    return new InstancesBuilder<>();
  }

  /**
   * Returns the underlying data.
   *
   * @return the wrapped data
   */
  public Instances getData() {
    return data;
  }

  /**
   * Generates a string with information about the features/labels.
   *
   * @return the info
   */
  public String toInfo() {
    return data.toSummaryString();
  }

  /**
   * Returns the feature/labels as json.
   *
   * @return the generated json
   */
  public JsonObject toJson() {
    return structure;
  }

  /**
   * Writes the features/labels JSON representation to the specified path.
   *
   * @param filename		the file to write the representation to
   * @throws IOException	if writing fails
   */
  public void toJson(Path filename) throws IOException {
    try (FileWriter fw = new FileWriter(filename.toFile());
	 BufferedWriter bw = new BufferedWriter(fw)) {
      bw.write(toJson().toString());
    }
  }

  /** Used to build a {@link InstancesDataset}. */
  public static class InstancesBuilder<T extends InstancesBuilder<T>>
    extends BaseBuilder<T> {

    protected Instances data;

    protected int classIndex;

    protected Set<String> ignoredColumns;

    protected boolean allFeaturesAdded;

    protected Set<String> matchingFeaturesAdded;

    protected boolean stringColumnsAsNominal;

    protected boolean dateColumnsAsNumeric;

    protected JsonObject structure;

    /**
     * Initializes the builder.
     */
    protected InstancesBuilder() {
      super();

      classIndex             = -1;
      ignoredColumns         = new HashSet<>();
      allFeaturesAdded       = false;
      matchingFeaturesAdded  = new HashSet<>();
      stringColumnsAsNominal = false;
      dateColumnsAsNumeric   = false;
      structure              = new JsonObject();
      structure.add("options", new JsonObject());
      structure.get("options").getAsJsonObject().addProperty("dateColumnsAsNumeric", false);
      structure.get("options").getAsJsonObject().addProperty("stringColumnsAsNominal", false);
      structure.add("features", new JsonArray());
      structure.add("labels", new JsonArray());
      structure.addProperty("classIndex", -1);
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("unchecked")
    protected T self() {
      return (T) this;
    }

    /**
     * Sets the underlying Instances object.
     *
     * @param data the data to wrap
     * @return this builder
     */
    public T data(Instances data) {
      this.data = data;
      classIndex(data.classIndex());
      return self();
    }

    /**
     * Raises an exception if no data set.
     *
     * @throws IllegalStateException if no data set
     */
    protected void instancesRequired() {
      if (data == null)
	throw new IllegalStateException("No data set!");
    }

    /**
     * Sets whether to treat DATE columns as NUMERIC ones.
     *
     * @return this builder
     */
    public T dateColumnsAsNumeric() {
      dateColumnsAsNumeric = true;
      structure.get("options").getAsJsonObject().addProperty("dateColumnsAsNumeric", true);
      return self();
    }

    /**
     * Sets whether to treat STRING columns as NOMINAL ones.
     *
     * @return this builder
     */
    public T stringColumnsAsNominal() {
      stringColumnsAsNominal = true;
      structure.get("options").getAsJsonObject().addProperty("stringColumnsAsNominal", true);
      return self();
    }

    /**
     * Sets the index of the column to use as class attribute.
     *
     * @param index the 0-based index (based on column in Instances)
     * @return this builder
     */
    public T classIndex(int index) {
      instancesRequired();
      classIndex = index;
      labels.clear();
      data.setClassIndex(index);
      if (classIndex > -1) {
	addColumn(
	  data.attribute(classIndex).name(),
	  InstancesAttributeType.toType(data.attribute(classIndex).type()),
	  true);
      }
      structure.addProperty("classIndex", index);
      return self();
    }

    /**
     * Sets the flag that the class attribute is the first column.
     *
     * @return this builder
     */
    public T classIsFirst() {
      return classIndex(0);
    }

    /**
     * Sets the flag that the class attribute is the last column.
     *
     * @return this builder
     */
    public T classIsLast() {
      instancesRequired();
      classIndex(data.numAttributes() - 1);
      return self();
    }

    /**
     * Checks if the specified column is the class attribute.
     *
     * @param index the column index in the dataset
     * @return true if class column
     */
    protected boolean isClassColumn(int index) {
      instancesRequired();
      return data.classIndex() == index;
    }

    /**
     * Adds the column as feature or label.
     * Skips ignored columns.
     *
     * @param colName 		the name of the column
     * @param colType 		the type of the column
     * @param isClassColumn 	whether the column is a class attribute
     */
    protected void addColumn(String colName, InstancesAttributeType colType, boolean isClassColumn) {
      JsonObject	att;

      if (ignoredColumns.contains(colName))
	return;

      att = new JsonObject();
      att.addProperty("name", colName);
      att.addProperty("type", colType.toString());

      if (isClassColumn) {
	switch (colType) {
	  case NUMERIC:
	    addNumericLabel(colName);
	    structure.get("labels").getAsJsonArray().add(att);
	    break;
	  case DATE:
	    if (dateColumnsAsNumeric) {
	      addNumericLabel(colName);
	      structure.get("labels").getAsJsonArray().add(att);
	    }
	    break;
	  case NOMINAL:
	    addCategoricalLabel(colName);
	    structure.get("labels").getAsJsonArray().add(att);
	    break;
	  case STRING:
	    if (stringColumnsAsNominal) {
	      addCategoricalLabel(colName);
	      structure.get("labels").getAsJsonArray().add(att);
	    }
	    break;
	  default:
	    throw new IllegalStateException("Unhandled class attribute type: " + colType);
	}
      }
      else {
	switch (colType) {
	  case NUMERIC:
	    addNumericFeature(colName);
	    structure.get("features").getAsJsonArray().add(att);
	    break;
	  case DATE:
	    if (dateColumnsAsNumeric) {
	      addNumericFeature(colName);
	      structure.get("features").getAsJsonArray().add(att);
	    }
	    break;
	  case NOMINAL:
	    addCategoricalFeature(colName);
	    structure.get("features").getAsJsonArray().add(att);
	    break;
	  case STRING:
	    if (stringColumnsAsNominal) {
	      addCategoricalFeature(colName);
	      structure.get("features").getAsJsonArray().add(att);
	    }
	    break;
	  default:
	    throw new IllegalStateException("Unhandled class attribute type: " + colType);
	}
      }
    }

    /**
     * Adds the column as feature or label.
     * Skips ignored columns.
     *
     * @param index		the index of the column to add
     */
    protected void addColumn(int index) {
      InstancesAttributeType 	colType;
      String			colName;

      instancesRequired();
      colName = data.attribute(index).name();
      colType = InstancesAttributeType.toType(data.attribute(index).type());

      addColumn(colName, colType, isClassColumn(index));
    }

    /**
     * Adds the column name(s) to ignore when adding all features.
     *
     * @param colNames the name(s) of the column(s) to ignore
     * @return this builder
     */
    public T addIgnoredColumn(String... colNames) {
      ignoredColumns.addAll(Arrays.asList(colNames));
      return self();
    }

    /**
     * Ignores all column names that match the regexp(s).
     *
     * @param regexp 	the regular expression(s) to use
     * @return 		this builder
     */
    public T ignoreMatchingColumns(String... regexp) {
      int	i;
      String	colName;

      instancesRequired();
      for (String r: regexp) {
	for (i = 0; i < data.numAttributes(); i++) {
	  colName = data.attribute(i).name();
	  if (colName.matches(r))
	    addIgnoredColumn(colName);
	}
      }

      return self();
    }

    /**
     * Adds all features according to their types.
     * Skips ignored column names and class attribute(s).
     * Only gets executed once.
     *
     * @return this builder
     */
    public T addAllFeatures() {
      int		i;

      instancesRequired();

      if (allFeaturesAdded)
	return self();

      allFeaturesAdded = true;

      // add all columns
      for (i = 0; i < data.numAttributes(); i++) {
	if (isClassColumn(i))
	  continue;
	addColumn(i);
      }

      return self();
    }

    /**
     * Adds all feature columns which names match the regular expression(s).
     * Skips ignored column names and class attribute(s).
     * Only gets executed once per regular expression.
     *
     * @param regexp the regular expression(s) to apply
     * @return this builder
     */
    public T addMatchingFeatures(String... regexp) {
      int	i;
      String	colName;

      instancesRequired();

      for (String r: regexp) {
	if (matchingFeaturesAdded.contains(r))
	  continue;

	matchingFeaturesAdded.add(r);

	for (i = 0; i < data.numAttributes(); i++) {
	  if (isClassColumn(i))
	    continue;
	  colName = data.attribute(i).name();
	  if (colName.matches(r))
	    addColumn(i);
	}
      }

      return self();
    }

    /**
     * Builds the new {@link InstancesDataset}.
     *
     * @return the new {@link InstancesDataset}
     */
    public InstancesDataset build() {
      instancesRequired();
      return new InstancesDataset(this);
    }

    /**
     * Returns the structure of the dataset as JSON.
     *
     * @return		the structure
     */
    public JsonObject toJson() {
      return structure;
    }

    /**
     * Returns the underlying data.
     *
     * @return		the wrapped data
     */
    public Instances getData() {
      return data;
    }

    /**
     * Configures the builder based on the structure.
     *
     * @param path the path to the dataset structure JSON file to load and apply
     * @return this builder
     * @see #fromJson(JsonObject)
     */
    public T fromJson(Path path) throws IOException {
      JsonObject 	j;

      try (Reader r = new FileReader(path.toFile()); BufferedReader br = new BufferedReader(r)) {
	j = (JsonObject) JsonParser.parseReader(br);
	return fromJson(j);
      }
    }

    /**
     * Configures the builder based on the structure.
     *
     * @param json the JSON string to load the dataset structure from and apply
     * @return this builder
     * @see #fromJson(JsonObject)
     */
    public T fromJson(String json) throws IOException {
      JsonObject 	j;

      try (Reader r = new StringReader(json); BufferedReader br = new BufferedReader(r)) {
	j = (JsonObject) JsonParser.parseReader(br);
	return fromJson(j);
      }
    }

    /**
     * Configures the builder based on the structure.
     *
     * @param structure	the dataset structure to use
     * @return this builder
     * @see #toJson()
     */
    public T fromJson(JsonObject structure) {
      JsonObject		options;
      JsonArray			features;
      JsonArray			labels;
      JsonObject		feature;
      InstancesAttributeType 	type;
      int			i;

      // options
      if (structure.has("options")) {
	options = structure.getAsJsonObject("options");
	if (options.has("dateColumnsAsNumeric") && options.get("dateColumnsAsNumeric").getAsBoolean())
	  dateColumnsAsNumeric();
	if (options.has("stringColumnsAsNominal") && options.get("stringColumnsAsNominal").getAsBoolean())
	  stringColumnsAsNominal();
      }

      // features
      if (structure.has("features")) {
	features = structure.getAsJsonArray("features");
	for (i = 0; i < features.size(); i++) {
	  feature = features.get(i).getAsJsonObject();
	  type    = InstancesAttributeType.valueOf(feature.get("type").getAsString());
	  addColumn(feature.get("name").getAsString(), type, false);
	}
      }

      // labels
      if (structure.has("labels")) {
	labels = structure.getAsJsonArray("labels");
	for (i = 0; i < labels.size(); i++) {
	  feature = labels.get(i).getAsJsonObject();
	  type    = InstancesAttributeType.valueOf(feature.get("type").getAsString());
	  addColumn(feature.get("name").getAsString(), type, true);
	}
      }

      // classIndex
      if (structure.has("classIndex"))
	classIndex(structure.get("classIndex").getAsInt());

      return self();
    }
  }
}
