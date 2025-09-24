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
 * HeatmapFileChooser.java
 * Copyright (C) 2011-2025 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.chooser;

import adams.core.classmanager.ClassManager;
import adams.data.heatmap.Heatmap;
import adams.data.io.input.AbstractHeatmapReader;
import adams.data.io.input.DataContainerReader;
import adams.data.io.output.AbstractHeatmapWriter;
import adams.data.io.output.DataContainerWriter;

import java.io.File;
import java.util.List;

/**
 * A specialized JFileChooser that lists all available file Readers and Writers
 * for heatmaps.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class HeatmapFileChooser
  extends AbstractDataContainerFileChooser<Heatmap, DataContainerReader<Heatmap>, DataContainerWriter<Heatmap>> {

  /** for serialization. */
  private static final long serialVersionUID = -5373058011025481738L;

  /**
   * Constructs a FileChooser pointing to the user's default directory.
   */
  public HeatmapFileChooser() {
    super();
  }

  /**
   * Constructs a FileChooser using the given File as the path.
   *
   * @param currentDirectory	the path to start in
   */
  public HeatmapFileChooser(File currentDirectory) {
    super(currentDirectory);
  }

  /**
   * Constructs a FileChooser using the given path.
   *
   * @param currentDirectory	the path to start in
   */
  public HeatmapFileChooser(String currentDirectory) {
    super(currentDirectory);
  }

  /**
   * Returns the default reader.
   *
   * @return		the default reader
   */
  @Override
  protected DataContainerReader<Heatmap> getDefaultReader() {
    return new adams.data.io.input.SimpleHeatmapReader();
  }

  /**
   * Returns the default writer.
   *
   * @return		the default writer
   */
  @Override
  protected DataContainerWriter<Heatmap> getDefaultWriter() {
    return new adams.data.io.output.SimpleHeatmapWriter();
  }

  /**
   * Returns the reader superclass for the GOE.
   *
   * @return		the reader class
   */
  @Override
  protected Class getReaderClass() {
    return AbstractHeatmapReader.class;
  }

  /**
   * Returns the writer superclass for the GOE.
   *
   * @return		the writer class
   */
  @Override
  protected Class getWriterClass() {
    return AbstractHeatmapWriter.class;
  }

  /**
   * Performs the actual initialization of the filters.
   */
  @Override
  protected void doInitializeFilters() {
    initFilters(this, true, AbstractHeatmapReader.getReaders());
    initFilters(this, false, AbstractHeatmapWriter.getWriters());
  }

  /**
   * Returns the reader for the specified file.
   *
   * @param file	the file to determine a reader for
   * @return		the reader, null if none found
   */
  public DataContainerReader<Heatmap> getReaderForFile(File file) {
    DataContainerReader	result;

    result = null;

    for (List<ExtensionFileFilterWithClass> list: m_ReaderFileFilters.values()) {
      // try filters that don't use "*.*"
      for (ExtensionFileFilterWithClass filter: list) {
	if (isAllFilter(filter))
	  continue;
	if (filter.accept(file)) {
	  try {
	    result = (DataContainerReader<Heatmap>) ClassManager.getSingleton().forName(filter.getClassname()).getDeclaredConstructor().newInstance();
	  }
	  catch (Exception e) {
            handleException("Failed to instantiate reader: " + filter.getClassname(), e);
	  }
	}
      }
      // try filters that use "*.*"
      if (result == null) {
	for (ExtensionFileFilterWithClass filter : list) {
	  if (!isAllFilter(filter))
	    continue;
	  if (filter.accept(file)) {
	    try {
	      result = (DataContainerReader<Heatmap>) ClassManager.getSingleton().forName(filter.getClassname()).getDeclaredConstructor().newInstance();
	    }
	    catch (Exception e) {
              handleException("Failed to instantiate reader: " + filter.getClassname(), e);
	    }
	  }
	}
      }
    }

    return result;
  }

  /**
   * Returns the writer for the specified file.
   *
   * @param file	the file to determine a reader for
   * @return		the writer, null if none found
   */
  public DataContainerWriter<Heatmap> getWriterForFile(File file) {
    DataContainerWriter<Heatmap>	result;

    result = null;

    for (List<ExtensionFileFilterWithClass> list: m_WriterFileFilters.values()) {
      // try filters that don't match "*.*"
      for (ExtensionFileFilterWithClass filter: list) {
	if (isAllFilter(filter))
	  continue;
	if (filter.accept(file)) {
	  try {
	    result = (DataContainerWriter<Heatmap>) ClassManager.getSingleton().forName(filter.getClassname()).getDeclaredConstructor().newInstance();
	  }
	  catch (Exception e) {
            handleException("Failed to instantiate writer: " + filter.getClassname(), e);
	  }
	}
      }
      // try filters that use "*.*"
      if (result == null) {
	for (ExtensionFileFilterWithClass filter: list) {
	  if (!isAllFilter(filter))
	    continue;
	  if (filter.accept(file)) {
	    try {
	      result = (DataContainerWriter<Heatmap>) ClassManager.getSingleton().forName(filter.getClassname()).getDeclaredConstructor().newInstance();
	    }
	    catch (Exception e) {
              handleException("Failed to instantiate writer: " + filter.getClassname(), e);
	    }
	  }
	}
      }
    }

    return result;
  }
}
