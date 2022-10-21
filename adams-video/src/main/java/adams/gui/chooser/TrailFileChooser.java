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
 * TrailFileChooser.java
 * Copyright (C) 2011-2020 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.chooser;

import adams.core.classmanager.ClassManager;
import adams.data.io.input.AbstractDataContainerReader;
import adams.data.io.input.AbstractTrailReader;
import adams.data.io.output.AbstractDataContainerWriter;
import adams.data.io.output.AbstractTrailWriter;
import adams.data.trail.Trail;

import java.io.File;
import java.util.List;

/**
 * A specialized JFileChooser that lists all available file Readers and Writers
 * for trails.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class TrailFileChooser
  extends AbstractDataContainerFileChooser<Trail, AbstractDataContainerReader<Trail>, AbstractDataContainerWriter<Trail>> {

  /** for serialization. */
  private static final long serialVersionUID = -5373058011025481738L;

  /**
   * Constructs a FileChooser pointing to the user's default directory.
   */
  public TrailFileChooser() {
    super();
  }

  /**
   * Constructs a FileChooser using the given File as the path.
   *
   * @param currentDirectory	the path to start in
   */
  public TrailFileChooser(File currentDirectory) {
    super(currentDirectory);
  }

  /**
   * Constructs a FileChooser using the given path.
   *
   * @param currentDirectory	the path to start in
   */
  public TrailFileChooser(String currentDirectory) {
    super(currentDirectory);
  }

  /**
   * Returns the default reader.
   *
   * @return		the default reader
   */
  @Override
  protected AbstractDataContainerReader<Trail> getDefaultReader() {
    return new adams.data.io.input.SimpleTrailReader();
  }

  /**
   * Returns the default writer.
   *
   * @return		the default writer
   */
  @Override
  protected AbstractDataContainerWriter<Trail> getDefaultWriter() {
    return new adams.data.io.output.SimpleTrailWriter();
  }

  /**
   * Returns the reader superclass for the GOE.
   *
   * @return		the reader class
   */
  @Override
  protected Class getReaderClass() {
    return AbstractTrailReader.class;
  }

  /**
   * Returns the writer superclass for the GOE.
   *
   * @return		the writer class
   */
  @Override
  protected Class getWriterClass() {
    return AbstractTrailWriter.class;
  }

  /**
   * Performs the actual initialization of the filters.
   */
  @Override
  protected void doInitializeFilters() {
    initFilters(this, true, AbstractTrailReader.getReaders());
    initFilters(this, false, AbstractTrailWriter.getWriters());
  }

  /**
   * Returns the reader for the specified file.
   *
   * @param file	the file to determine a reader for
   * @return		the reader, null if none found
   */
  public AbstractDataContainerReader<Trail> getReaderForFile(File file) {
    AbstractDataContainerReader	result;

    result = null;

    for (List<ExtensionFileFilterWithClass> list: m_ReaderFileFilters.values()) {
      // try filters that don't use "*.*"
      for (ExtensionFileFilterWithClass filter: list) {
	if (isAllFilter(filter))
	  continue;
	if (filter.accept(file)) {
	  try {
	    result = (AbstractDataContainerReader<Trail>) ClassManager.getSingleton().forName(filter.getClassname()).getDeclaredConstructor().newInstance();
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
	      result = (AbstractDataContainerReader<Trail>) ClassManager.getSingleton().forName(filter.getClassname()).getDeclaredConstructor().newInstance();
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
  public AbstractDataContainerWriter<Trail> getWriterForFile(File file) {
    AbstractDataContainerWriter<Trail>	result;

    result = null;

    for (List<ExtensionFileFilterWithClass> list: m_WriterFileFilters.values()) {
      // try filters that don't match "*.*"
      for (ExtensionFileFilterWithClass filter: list) {
	if (isAllFilter(filter))
	  continue;
	if (filter.accept(file)) {
	  try {
	    result = (AbstractDataContainerWriter<Trail>) ClassManager.getSingleton().forName(filter.getClassname()).getDeclaredConstructor().newInstance();
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
	      result = (AbstractDataContainerWriter<Trail>) ClassManager.getSingleton().forName(filter.getClassname()).getDeclaredConstructor().newInstance();
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
