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
 * Copyright (C) 2011-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.chooser;

import adams.data.heatmap.Heatmap;
import adams.data.io.input.AbstractDataContainerReader;
import adams.data.io.input.AbstractHeatmapReader;
import adams.data.io.output.AbstractDataContainerWriter;
import adams.data.io.output.AbstractHeatmapWriter;

import java.io.File;
import java.util.List;

/**
 * A specialized JFileChooser that lists all available file Readers and Writers
 * for heatmaps.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class HeatmapFileChooser
  extends AbstractDataContainerFileChooser<Heatmap, AbstractDataContainerReader<Heatmap>, AbstractDataContainerWriter<Heatmap>> {

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
  protected AbstractDataContainerReader<Heatmap> getDefaultReader() {
    return new adams.data.io.input.SimpleHeatmapReader();
  }

  /**
   * Returns the default writer.
   *
   * @return		the default writer
   */
  @Override
  protected AbstractDataContainerWriter<Heatmap> getDefaultWriter() {
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
  public AbstractDataContainerReader<Heatmap> getReaderForFile(File file) {
    AbstractDataContainerReader	result;

    result = null;

    for (List<ExtensionFileFilterWithClass> list: m_ReaderFileFilters.values()) {
      // try filters that don't use "*.*"
      for (ExtensionFileFilterWithClass filter: list) {
	if (isAllFilter(filter))
	  continue;
	if (filter.accept(file)) {
	  try {
	    result = (AbstractDataContainerReader<Heatmap>) Class.forName(filter.getClassname()).newInstance();
	  }
	  catch (Exception e) {
	    System.err.println("Failed to instantiate reader '" + filter.getClassname() + "':");
	    e.printStackTrace();
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
	      result = (AbstractDataContainerReader<Heatmap>) Class.forName(filter.getClassname()).newInstance();
	    }
	    catch (Exception e) {
	      System.err.println("Failed to instantiate reader '" + filter.getClassname() + "':");
	      e.printStackTrace();
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
  public AbstractDataContainerWriter<Heatmap> getWriterForFile(File file) {
    AbstractDataContainerWriter<Heatmap>	result;

    result = null;

    for (List<ExtensionFileFilterWithClass> list: m_WriterFileFilters.values()) {
      // try filters that don't match "*.*"
      for (ExtensionFileFilterWithClass filter: list) {
	if (isAllFilter(filter))
	  continue;
	if (filter.accept(file)) {
	  try {
	    result = (AbstractDataContainerWriter<Heatmap>) Class.forName(filter.getClassname()).newInstance();
	  }
	  catch (Exception e) {
	    System.err.println("Failed to instantiate writer '" + filter.getClassname() + "':");
	    e.printStackTrace();
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
	      result = (AbstractDataContainerWriter<Heatmap>) Class.forName(filter.getClassname()).newInstance();
	    }
	    catch (Exception e) {
	      System.err.println("Failed to instantiate writer '" + filter.getClassname() + "':");
	      e.printStackTrace();
	    }
	  }
	}
      }
    }

    return result;
  }
}
