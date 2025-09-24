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
 * AudioAnnotationsFileChooser.java
 * Copyright (C) 2018-2025 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.chooser;

import adams.core.classmanager.ClassManager;
import adams.data.audioannotations.AudioAnnotations;
import adams.data.io.input.AbstractAudioAnnotationsReader;
import adams.data.io.input.DataContainerReader;
import adams.data.io.output.AbstractAudioAnnotationsWriter;
import adams.data.io.output.DataContainerWriter;

import java.io.File;
import java.util.List;

/**
 * A specialized JFileChooser that lists all available file Readers and Writers
 * for audio annotations.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class AudioAnnotationsFileChooser
  extends AbstractDataContainerFileChooser<AudioAnnotations, DataContainerReader<AudioAnnotations>, DataContainerWriter<AudioAnnotations>> {

  /** for serialization. */
  private static final long serialVersionUID = -5373058011025481738L;

  /**
   * Constructs a FileChooser pointing to the user's default directory.
   */
  public AudioAnnotationsFileChooser() {
    super();
  }

  /**
   * Constructs a FileChooser using the given File as the path.
   *
   * @param currentDirectory	the path to start in
   */
  public AudioAnnotationsFileChooser(File currentDirectory) {
    super(currentDirectory);
  }

  /**
   * Constructs a FileChooser using the given path.
   *
   * @param currentDirectory	the path to start in
   */
  public AudioAnnotationsFileChooser(String currentDirectory) {
    super(currentDirectory);
  }

  /**
   * Returns the default reader.
   *
   * @return		the default reader
   */
  @Override
  protected DataContainerReader<AudioAnnotations> getDefaultReader() {
    return new adams.data.io.input.SimpleAudioAnnotationsReader();
  }

  /**
   * Returns the default writer.
   *
   * @return		the default writer
   */
  @Override
  protected DataContainerWriter<AudioAnnotations> getDefaultWriter() {
    return new adams.data.io.output.SimpleAudioAnnotationsWriter();
  }

  /**
   * Returns the reader superclass for the GOE.
   *
   * @return		the reader class
   */
  @Override
  protected Class getReaderClass() {
    return AbstractAudioAnnotationsReader.class;
  }

  /**
   * Returns the writer superclass for the GOE.
   *
   * @return		the writer class
   */
  @Override
  protected Class getWriterClass() {
    return AbstractAudioAnnotationsWriter.class;
  }

  /**
   * Performs the actual initialization of the filters.
   */
  @Override
  protected void doInitializeFilters() {
    initFilters(this, true, AbstractAudioAnnotationsReader.getReaders());
    initFilters(this, false, AbstractAudioAnnotationsWriter.getWriters());
  }

  /**
   * Returns the reader for the specified file.
   *
   * @param file	the file to determine a reader for
   * @return		the reader, null if none found
   */
  public DataContainerReader<AudioAnnotations> getReaderForFile(File file) {
    DataContainerReader	result;

    result = null;

    for (List<ExtensionFileFilterWithClass> list: m_ReaderFileFilters.values()) {
      // try filters that don't use "*.*"
      for (ExtensionFileFilterWithClass filter: list) {
	if (isAllFilter(filter))
	  continue;
	if (filter.accept(file)) {
	  try {
	    result = (DataContainerReader<AudioAnnotations>) ClassManager.getSingleton().forName(filter.getClassname()).getDeclaredConstructor().newInstance();
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
	      result = (DataContainerReader<AudioAnnotations>) ClassManager.getSingleton().forName(filter.getClassname()).getDeclaredConstructor().newInstance();
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
  public DataContainerWriter<AudioAnnotations> getWriterForFile(File file) {
    DataContainerWriter<AudioAnnotations>	result;

    result = null;

    for (List<ExtensionFileFilterWithClass> list: m_WriterFileFilters.values()) {
      // try filters that don't match "*.*"
      for (ExtensionFileFilterWithClass filter: list) {
	if (isAllFilter(filter))
	  continue;
	if (filter.accept(file)) {
	  try {
	    result = (DataContainerWriter<AudioAnnotations>) ClassManager.getSingleton().forName(filter.getClassname()).getDeclaredConstructor().newInstance();
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
	      result = (DataContainerWriter<AudioAnnotations>) ClassManager.getSingleton().forName(filter.getClassname()).getDeclaredConstructor().newInstance();
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
