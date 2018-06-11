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
 * AudioFileChooser.java
 * Copyright (C) 2018 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.chooser;

import adams.gui.core.ExtensionFileFilter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A specialized JFileChooser loading/saving audio files.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class AudioFileChooser
  extends AbstractExtensionFileFilterFileChooser<ExtensionFileFilter> {

  /** for serialization. */
  private static final long serialVersionUID = -5373058011025481738L;

  /** the list of audio filters. */
  protected static List<ExtensionFileFilter> m_Filters;

  /**
   * Constructs a FileChooser pointing to the user's default directory.
   */
  public AudioFileChooser() {
    super();
  }

  /**
   * Constructs a FileChooser using the given File as the path.
   *
   * @param currentDirectory	the path to start in
   */
  public AudioFileChooser(File currentDirectory) {
    super(currentDirectory);
  }

  /**
   * Constructs a FileChooser using the given path.
   *
   * @param currentDirectory	the path to start in
   */
  public AudioFileChooser(String currentDirectory) {
    super(currentDirectory);
  }

  /**
   * Returns whether the filters have already been initialized.
   *
   * @return		true if the filters have been initialized
   */
  @Override
  protected boolean getFiltersInitialized() {
    return (m_Filters != null);
  }

  /**
   * Performs the actual initialization of the filters.
   */
  @Override
  protected void doInitializeFilters() {
    List<ExtensionFileFilter>	filters;

    filters = new ArrayList<>();
    filters.add(new ExtensionFileFilter("WAV file", "wav"));
    filters.add(new ExtensionFileFilter("AIFF file", "aiff"));
    filters.add(new ExtensionFileFilter("AU file", "au"));

    m_Filters = filters;
  }

  /**
   * Returns the file filters for opening files.
   *
   * @return		the file filters
   */
  @Override
  protected List<ExtensionFileFilter> getOpenFileFilters() {
    return m_Filters;
  }

  /**
   * Returns the file filters for writing files.
   *
   * @return		the file filters
   */
  @Override
  protected List<ExtensionFileFilter> getSaveFileFilters() {
    return m_Filters;
  }
}
