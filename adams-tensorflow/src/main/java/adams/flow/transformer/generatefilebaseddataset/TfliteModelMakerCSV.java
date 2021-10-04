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
 * TfliteModelMakerCSV.java
 * Copyright (C) 2021 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer.generatefilebaseddataset;

import adams.core.QuickInfoHelper;
import adams.core.base.BaseObject;
import adams.core.base.BaseString;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.data.image.BufferedImageContainer;
import adams.data.image.BufferedImageHelper;
import adams.data.io.input.DefaultSimpleReportReader;
import adams.data.objectfinder.AllFinder;
import adams.data.objectfinder.ObjectFinder;
import adams.data.report.Report;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.container.FileBasedDatasetContainer;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;

import java.util.List;

/**
 * Generates a spreadsheet from the incoming files (to be saved as CSV).
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class TfliteModelMakerCSV
  extends AbstractFileBasedDatasetGeneration<SpreadSheet> {

  private static final long serialVersionUID = 590645786308393421L;

  /** the values in the container to use. */
  protected BaseString[] m_Values;

  /** the object finder to use. */
  protected ObjectFinder m_Finder;

  /** the meta-data key for the type. */
  protected String m_MetaDataKeyType;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates a spreadsheet from the incoming files (to be saved as CSV).\n"
	+ "Expects the annotations to be in ADAMS format alongside the image files.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
        "value", "values",
        new BaseString[]{new BaseString(FileBasedDatasetContainer.VALUE_TRAIN)});

    m_OptionManager.add(
	"finder", "finder",
	new AllFinder());

    m_OptionManager.add(
	"meta-data-key-type", "metaDataKeyType",
	"type");
  }

  /**
   * Sets the name(s) of the container value(s) to save.
   *
   * @param value	the value(s)
   */
  public void setValues(BaseString[] value) {
    m_Values = value;
    reset();
  }

  /**
   * Returns the name(s) of the container value(s) to save.
   *
   * @return		the value(s)
   */
  public BaseString[] getValues() {
    return m_Values;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String valueTipText() {
    return "The name(s) of the value(s) in the container to use.";
  }

  /**
   * Sets the finder to use for locating the objects.
   *
   * @param value	the finder
   */
  public void setFinder(ObjectFinder value) {
    m_Finder = value;
    reset();
  }

  /**
   * Returns the finder to use for locating the objects.
   *
   * @return		the finder
   */
  public ObjectFinder getFinder() {
    return m_Finder;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String finderTipText() {
    return "The object finder to use.";
  }

  /**
   * Sets the meta-data key containing the object label.
   *
   * @param value	the key
   */
  public void setMetaDataKeyType(String value) {
    m_MetaDataKeyType = value;
    reset();
  }

  /**
   * Returns the meta-data key containing the object label.
   *
   * @return		the key
   */
  public String getMetaDataKeyType() {
    return m_MetaDataKeyType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String metaDataKeyTypeTipText() {
    return "The meta-data key for the type (= label).";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "values", m_Values, "values: ");
    result += QuickInfoHelper.toString(this, "finder", m_Finder, ", finder: ");
    result += QuickInfoHelper.toString(this, "metaDataKeyType", m_MetaDataKeyType, ", type: ");

    return result;
  }

  /**
   * Returns the class that gets generated.
   *
   * @return the generated class
   */
  @Override
  public Class generates() {
    return SpreadSheet.class;
  }

  /**
   * The keys of the values that need to be present in the container.
   *
   * @return		the keys
   */
  @Override
  protected String[] requiredValues() {
    return BaseObject.toStringArray(m_Values);
  }

  /**
   * Loads the associated report.
   *
   * @param file 	the file to load the report for
   * @return		the report or null if failed to load
   */
  protected Report loadReport(String file) {
    Report			result;
    DefaultSimpleReportReader	reader;
    List<Report> 		reports;

    result = null;

    file = FileUtils.replaceExtension(file, ".report");
    if (FileUtils.fileExists(file)) {
      reader = new DefaultSimpleReportReader();
      reader.setInput(new PlaceholderFile(file));
      reports = reader.read();
      if (reports.size() > 0)
        result = reports.get(0);
    }

    return result;
  }

  /**
   * Generates the dataset.
   *
   * @param cont the container to use
   * @return the generated output
   */
  @Override
  protected SpreadSheet doGenerate(FileBasedDatasetContainer cont) {
    SpreadSheet			result;
    Row				row;
    String[]			files;
    String 			type;
    Report              	report;
    BufferedImageContainer 	img;
    int				width;
    int				height;
    LocatedObjects		objects;

    result = new DefaultSpreadSheet();

    // header
    row = result.getHeaderRow();
    row.addCell("T").setContentAsString("type");
    row.addCell("F").setContentAsString("file");
    row.addCell("L").setContentAsString("label");
    row.addCell("TLX").setContentAsString("top-left-x");
    row.addCell("TLY").setContentAsString("top-left-y");
    row.addCell("TRX").setContentAsString("top-right-x");
    row.addCell("TRY").setContentAsString("top-right-y");
    row.addCell("BRX").setContentAsString("bottom-right-x");
    row.addCell("BRY").setContentAsString("bottom-right-y");
    row.addCell("BLX").setContentAsString("bottom-left-x");
    row.addCell("BLY").setContentAsString("bottom-left-y");

    for (BaseString value: m_Values) {
      // annotation type
      type = value.getValue();
      if (type.equals(FileBasedDatasetContainer.VALUE_TRAIN))
        type = "Training";
      type = type.toUpperCase();

      // iterate files
      files = cont.getValue(value.getValue(), String[].class);
      for (String file: files) {
        // load image
        if (!FileUtils.fileExists(file)) {
	  getLogger().warning("Image does not exist: " + file);
	  continue;
	}
        img    = BufferedImageHelper.read(new PlaceholderFile(file));
        width  = img.getWidth();
        height = img.getHeight();

        report = loadReport(file);
        if (report == null) {
          getLogger().warning("No report found for: " + file);
          continue;
        }
        objects = m_Finder.findObjects(report);

        for (LocatedObject object: objects) {
	  row = result.addRow();
	  row.addCell("T").setContentAsString(type);
	  row.addCell("F").setContentAsString(file);
	  if (object.getMetaData().containsKey(m_MetaDataKeyType))
	    row.addCell("L").setContentAsString("" + object.getMetaData().get(m_MetaDataKeyType));
	  row.addCell("TLX").setContent((double) object.getX() / width);
	  row.addCell("TLY").setContent((double) object.getY() / height);
	  row.addCell("BRX").setContent((double) (object.getX() + object.getWidth() - 1) / width);
	  row.addCell("BRY").setContent((double) (object.getY() + object.getHeight() - 1) / height);
	}
      }
    }

    return result;
  }
}
