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
 * SpreadSheetRecordReader.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.ml.dl4j.recordreader;

import adams.core.io.FileUtils;
import adams.core.option.OptionUtils;
import adams.data.SharedStringsTable;
import adams.data.conversion.SpreadSheetToNumeric;
import adams.data.io.input.CsvSpreadSheetReader;
import adams.data.io.input.SpreadSheetReader;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import org.datavec.api.conf.Configuration;
import org.datavec.api.records.Record;
import org.datavec.api.records.metadata.RecordMetaData;
import org.datavec.api.records.metadata.RecordMetaDataLine;
import org.datavec.api.records.reader.BaseRecordReader;
import org.datavec.api.split.InputSplit;
import org.datavec.api.writable.DoubleWritable;
import org.datavec.api.writable.IntWritable;
import org.datavec.api.writable.LongWritable;
import org.datavec.api.writable.Writable;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Record reader that wraps around an ADAMS {@link SpreadSheetReader}.
 * For strings, uses the index from the shared strings table.
 * For missing values and non-supported cell types, NaN is used.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetRecordReader
  extends BaseRecordReader {

  private static final long serialVersionUID = -837128486046274089L;

  /** the key for the reader commandline. */
  public final static String READER_CMDLINE = "ReaderCmdLine";

  /** the key for the conversion commandline. */
  public final static String CONVERSION_CMDLINE = "ConversionCmdLine";

  /** the configuration. */
  protected Configuration m_Configuration;

  /** the spreadsheet reader to use. */
  protected SpreadSheetReader m_Reader;

  /** the conversion for turning non-numeric cells into numeric ones. */
  protected SpreadSheetToNumeric m_Conversion;

  /** the locations to read. */
  protected URI[] m_Locations;

  /** the input split to use. */
  protected InputSplit m_InputSplit;

  /** the loaded spreadsheet. */
  protected SpreadSheet m_Sheet;

  /** the row index. */
  protected int m_Row;

  /** the index for the locations. */
  protected int m_SplitIndex;

  /**
   * Initializes the reader.
   *
   * @param reader	the reader to use
   */
  public SpreadSheetRecordReader(SpreadSheetReader reader, SpreadSheetToNumeric conversion) {
    super();

    m_Reader     = reader;
    m_Conversion = conversion;
    m_SplitIndex = 0;
    m_Row        = 0;
  }

  @Override
  public void close() throws IOException {
    // ignored
  }

  /**
   * Called once at initialization.
   *
   * @param split the split that defines the range of records to read
   * @throws java.io.IOException
   * @throws InterruptedException
   */
  @Override
  public void initialize(InputSplit split) throws IOException, InterruptedException {
    m_InputSplit = split;
    m_Sheet      = null;
    m_Row        = 0;
    m_Locations  = split.locations();
    m_Sheet      = getSheet(0);
  }

  /**
   * Loads the spreadsheet from the specified location.
   *
   * @param index	the index in the locations
   * @return		the spreadsheet, null if invalid index
   * @throws IOException	if failed to read
   */
  protected SpreadSheet getSheet(int index) throws IOException {
    SpreadSheet 	result;
    InputStream 	stream;
    String		msg;

    result = null;

    if (m_Locations.length > index) {
      // try reading from file
      try {
	result = m_Reader.read(m_Locations[index].toURL().getFile());
      }
      catch (Exception e) {
	// ignored
      }
      // try reading from stream
      if (result == null) {
	stream = null;
	try {
	  stream = m_Locations[index].toURL().openStream();
	  result = m_Reader.read(stream);
	}
	finally {
	  FileUtils.closeQuietly(stream);
	}
      }
    }

    // ensure we only have numeric cells
    if (result != null) {
      m_Conversion.setInput(result);
      msg = m_Conversion.convert();
      if (msg != null) {
	result = null;
	System.err.println("Failed to convert spreadsheet to numeric:\n" + msg);
      }
      else {
	result = (SpreadSheet) m_Conversion.getOutput();
      }
      m_Conversion.cleanUp();
    }

    return result;
  }

  /**
   * Called once at initialization.
   *
   * @param conf  a configuration for initialization
   * @param split the split that defines the range of records to read
   * @throws java.io.IOException
   * @throws InterruptedException
   */
  @Override
  public void initialize(Configuration conf, InputSplit split) throws IOException, InterruptedException {
    try {
      m_Reader = (SpreadSheetReader) OptionUtils.forCommandLine(SpreadSheetReader.class, conf.get(READER_CMDLINE, new CsvSpreadSheetReader().toCommandLine()));
    }
    catch (Exception e) {
      m_Reader = new CsvSpreadSheetReader();
    }

    try {
      m_Conversion = (SpreadSheetToNumeric) OptionUtils.forCommandLine(SpreadSheetToNumeric.class, conf.get(CONVERSION_CMDLINE, new SpreadSheetToNumeric().toCommandLine()));
    }
    catch (Exception e) {
      m_Conversion = new SpreadSheetToNumeric();
    }
  }

  /**
   * Get the next record
   *
   * @return		the cells
   */
  @Override
  public List<Writable> next() {
    List<Writable>	cells;
    Row			row;
    int			i;
    Cell 		cell;
    SharedStringsTable	table;

    cells = null;

    // load next sheet?
    if (m_Row >= m_Sheet.getRowCount()) {
      m_Sheet = null;
      m_Row   = 0;
      if (m_SplitIndex < m_Locations.length - 1)
	m_SplitIndex++;
      else
	return null;

      try {
	m_Sheet = getSheet(m_SplitIndex);
      }
      catch (Exception e) {
	m_Sheet = null;
      }
    }

    if (m_Sheet != null) {
      table = m_Sheet.getSharedStringsTable();
      cells = new ArrayList<>();
      row   = m_Sheet.getRow(m_Row);
      for (i = 0; i < m_Sheet.getColumnCount(); i++) {
	if (!row.hasCell(i)) {
	  cells.add(new DoubleWritable(Double.NaN));
	}
	else {
	  cell = row.getCell(i);
	  switch (cell.getContentType()) {
	    case MISSING:
	      cells.add(new DoubleWritable(Double.NaN));
	      break;
	    case LONG:
	      cells.add(new LongWritable(cell.toLong()));
	      break;
	    case DOUBLE:
	      cells.add(new DoubleWritable(cell.toDouble()));
	      break;
	    case BOOLEAN:
	      cells.add(new IntWritable(cell.toBoolean() ? 1 : 0));
	      break;
	    case DATE:
	    case DATETIME:
	    case DATETIMEMSEC:
	    case TIME:
	    case TIMEMSEC:
	      cells.add(new LongWritable(cell.toAnyDateType().getTime()));
	      break;
	    case STRING:
	      cells.add(new IntWritable(table.getIndex(cell.getContent())));
	      break;
	    default:
	      cells.add(new DoubleWritable(Double.NaN));
	  }
	}
      }
      m_Row++;
    }

    return cells;
  }

  /**
   * Whether there are any more records
   *
   * @return		true if more records
   */
  @Override
  public boolean hasNext() {
    return (m_Sheet != null) && (m_Row < m_Sheet.getRowCount())  // not -1 because already incremented!
      || ((m_Locations != null) && (m_SplitIndex < m_Locations.length - 1));
  }

  /**
   * List of label strings
   *
   * @return		always null
   */
  @Override
  public List<String> getLabels() {
    return null;
  }

  /**
   * Reset record reader iterator
   */
  @Override
  public void reset() {
    if (m_InputSplit == null)
      throw new UnsupportedOperationException("Cannot reset without first initializing");
    try {
      initialize(m_InputSplit);
      m_SplitIndex = 0;
    }
    catch(Exception e) {
      throw new RuntimeException("Error during spreadsheet record reader reset",e);
    }
  }

  /**
   * Load the record from the given DataInputStream
   * Unlike {@link #next()} the internal state of the RecordReader is not modified
   * Implementations of this method should not close the DataInputStream
   *
   * @throws IOException if error occurs during reading from the input stream
   */
  @Override
  public List<Writable> record(URI uri, DataInputStream dataInputStream) throws IOException {
    // TODO
    return null;
  }

  /**
   * Similar to {@link #next()}, but returns a {@link Record} object, that may include metadata such as the source
   * of the data
   *
   * @return next record
   */
  @Override
  public Record nextRecord() {
    List<Writable> next = next();
    URI uri = (m_Locations == null || m_Locations.length < 1 ? null : m_Locations[m_SplitIndex]);
    RecordMetaData meta = new RecordMetaDataLine(m_Row - 1, uri, SpreadSheetRecordReader.class); // - 1 as row number has been incremented already...
    return new org.datavec.api.records.impl.Record(next, meta);
  }

  /**
   * Load a single record from the given {@link RecordMetaData} instance<br>
   * Note: that for data that isn't splittable (i.e., text data that needs to be scanned/split), it is more efficient to
   * load multiple records at once using {@link #loadFromMetaData(List)}
   *
   * @param recordMetaData Metadata for the record that we want to load from
   * @return Single record for the given RecordMetaData instance
   * @throws IOException If I/O error occurs during loading
   */
  @Override
  public Record loadFromMetaData(RecordMetaData recordMetaData) throws IOException {
    // TODO
    return null;
  }

  /**
   * Load multiple records from the given a list of {@link RecordMetaData} instances<br>
   *
   * @param recordMetaDatas Metadata for the records that we want to load from
   * @return Multiple records for the given RecordMetaData instances
   * @throws IOException If I/O error occurs during loading
   */
  @Override
  public List<Record> loadFromMetaData(List<RecordMetaData> recordMetaDatas) throws IOException {
    // TODO
    return null;
  }

  /** Set the configuration to be used by this object. */
  @Override
  public void setConf(Configuration conf) {
    m_Configuration = conf;
  }

  /** Return the configuration used by this object. */
  @Override
  public Configuration getConf() {
    return m_Configuration;
  }
}
