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
 * GitSettingsPanel.java
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.application;

import adams.core.io.FileUtils;
import adams.env.Environment;
import adams.env.GitDefinition;
import adams.gui.core.PropertiesParameterPanel.PropertyType;

/**
 * Panel for configuring the git settings.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class GitSettingsPanel
  extends AbstractPropertiesPreferencesPanel {

  /** for serialization. */
  private static final long serialVersionUID = 3895159356677639564L;

  @Override
  protected void initGUI() {
    super.initGUI();

    addPropertyType("SshDir", PropertyType.DIRECTORY_ABSOLUTE);
    addPropertyType("KnownHosts", PropertyType.FILE_ABSOLUTE);
    addPropertyType("User", PropertyType.STRING);
    addPropertyType("Email", PropertyType.STRING);
    addPropertyType("LoggingLevel", PropertyType.LIST);
    addPropertyType("FlowEditorSupport", PropertyType.BOOLEAN);
    setList("LoggingLevel", new String[]{"OFF", "SEVERE", "WARNING", "INFO", "CONFIG", "FINE", "FINER", "FINEST"});
    setPropertyOrder(new String[]{
      "SshDir",
      "KnownHosts",
      "User",
      "Email",
      "LoggingLevel",
      "FlowEditorSupport",
    });
    setPreferences(Environment.getInstance().read(GitDefinition.KEY));
  }

  /**
   * The title of the preferences.
   *
   * @return		the title
   */
  @Override
  public String getTitle() {
    return "Git";
  }

  /**
   * Returns whether the panel requires a wrapper scrollpane/panel for display.
   *
   * @return		true if wrapper required
   */
  @Override
  public boolean requiresWrapper() {
    return false;
  }

  /**
   * Activates the settings.
   *
   * @return		null if successfully activated, otherwise error message
   */
  @Override
  public String activate() {
    if (Environment.getInstance().write(GitDefinition.KEY, getPreferences()))
      return null;
    else
      return "Failed to save git settings!";
  }

  /**
   * Returns whether the panel supports resetting the options.
   *
   * @return		true if supported
   */
  public boolean canReset() {
    String	props;

    props = Environment.getInstance().getCustomPropertiesFilename(GitDefinition.KEY);
    return (props != null) && FileUtils.fileExists(props);
  }

  /**
   * Resets the settings to their default.
   *
   * @return		null if successfully reset, otherwise error message
   */
  public String reset() {
    String	props;

    props = Environment.getInstance().getCustomPropertiesFilename(GitDefinition.KEY);
    if ((props != null) && FileUtils.fileExists(props)) {
      if (!FileUtils.delete(props))
	return "Failed to remove custom git properties: " + props;
    }

    return null;
  }
}
