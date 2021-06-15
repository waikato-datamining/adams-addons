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
 * RenjinSettingsPanel.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package adams.gui.application;

import adams.core.management.RenjinClassPathAugmenter;

/**
 * Settings panel for Renjin.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class RenjinSettingsPanel
  extends AbstractBootstrappPreferencesPanel {

  private static final long serialVersionUID = -8689967386850342681L;

  /**
   * The title of the preferences.
   *
   * @return the title
   */
  @Override
  public String getTitle() {
    return "Renjin";
  }

  /**
   * The default info text to display (gets converted to HTML automatically).
   *
   * @return		the text, null to disable
   */
  @Override
  protected String getDefaultInfoText() {
    return "You can find artifact information on Renjin packages here:\n"
      + "http://packages.renjin.org/\n\n"
      + "The Maven repository for the Renjin packages is provided by betadriven:\n"
      + "https://nexus.bedatadriven.com/content/groups/public/";
  }

  /**
   * Returns the name of the properties file to load.
   *
   * @return the filename (no path)
   */
  @Override
  protected String getPropertiesFile() {
    return "Renjin.props";
  }

  /**
   * Returns the directory to use for the libraries.
   *
   * @return the full path
   */
  @Override
  protected String getOutputDir() {
    return new RenjinClassPathAugmenter().getBootstrappOutputDir();
  }
}
