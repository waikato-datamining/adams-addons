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
 * AbstractBootstrappPreferences.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package adams.gui.application;

import adams.core.Properties;
import adams.core.Utils;
import adams.core.base.MavenArtifact;
import adams.core.base.MavenRepository;
import adams.core.bootstrapp.BootstrappUtils;
import adams.core.io.FileUtils;
import adams.core.logging.LoggingHelper;
import adams.env.Environment;
import adams.gui.core.ConsolePanel;
import adams.gui.core.ParameterPanel;
import adams.gui.goe.GenericArrayEditorPanel;
import com.github.fracpete.bootstrapp.Main;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

/**
 * Ancestor for panels that allow managing of dependencies for bootstrapp.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractBootstrappPreferencesPanel
  extends AbstractPreferencesPanel {

  private static final long serialVersionUID = -6716073907018119671L;

  /** the parameters. */
  protected ParameterPanel m_PanelParameters;

  /** the artifacts. */
  protected GenericArrayEditorPanel m_PanelArtifacts;

  /** the repositories. */
  protected GenericArrayEditorPanel m_PanelRepositories;

  /** the properties. */
  protected Properties m_Properties;

  /**
   * Initializes the members.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    setLayout(new BorderLayout());

    m_PanelParameters = new ParameterPanel();
    add(m_PanelParameters, BorderLayout.CENTER);

    m_PanelArtifacts = new GenericArrayEditorPanel(new MavenArtifact[0]);
    m_PanelArtifacts.setCurrent(getArtifacts());
    m_PanelParameters.addParameter("_Artifacts", m_PanelArtifacts);

    m_PanelRepositories = new GenericArrayEditorPanel(new MavenRepository[0]);
    m_PanelRepositories.setCurrent(getRepositories());
    m_PanelParameters.addParameter("Repositories", m_PanelRepositories);
  }

  /**
   * Returns the name of the properties file to load.
   *
   * @return		the filename (no path)
   */
  protected abstract String getPropertiesFile();

  /**
   * Returns the properties and reads them from disk if necessary.
   *
   * @return		the properties to use
   */
  protected Properties getProperties() {
    Properties	result;

    if (m_Properties != null) {
      result = m_Properties;
    }
    else {
      try {
	result = Properties.read(getPropertiesFile());
      }
      catch (Exception e) {
        ConsolePanel.getSingleton().append("Failed to read props file: " + getPropertiesFile(), e);
        result = new Properties();
      }
    }

    return result;
  }

  /**
   * Returns the artifacts to display.
   *
   * @return		the artifacts
   */
  protected MavenArtifact[] getArtifacts() {
    List<MavenArtifact> result;
    String[]		parts;
    MavenArtifact	artifact;

    result = new ArrayList<>();
    if (getProperties().hasKey(BootstrappUtils.KEY_DEPENDENCIES)) {
      parts = getProperties().getProperty(BootstrappUtils.KEY_DEPENDENCIES).split(",");
      for (String part: parts) {
        artifact = new MavenArtifact(part);
        if (!artifact.isEmpty())
          result.add(artifact);
      }
    }

    return result.toArray(new MavenArtifact[0]);
  }

  /**
   * Returns the repositories to display.
   *
   * @return		the repositories
   */
  protected MavenRepository[] getRepositories() {
    List<MavenRepository> result;
    String[]		parts;
    MavenRepository	artifact;

    result = new ArrayList<>();
    if (getProperties().hasKey(BootstrappUtils.KEY_REPOSITORIES)) {
      parts = getProperties().getProperty(BootstrappUtils.KEY_REPOSITORIES).split(",");
      for (String part: parts) {
        artifact = new MavenRepository(part);
        if (!artifact.isEmpty())
          result.add(artifact);
      }
    }

    return result.toArray(new MavenRepository[0]);
  }

  /**
   * Returns the directory to use for the libraries.
   *
   * @return		the full path
   */
  protected abstract String getOutputDir();

  /**
   * Turns the parameters in the GUI into a properties object.
   *
   * @return		the properties
   */
  protected Properties toProperties() {
    Properties	result;

    result = new Properties();

    result.setProperty(BootstrappUtils.KEY_DEPENDENCIES, Utils.flatten((MavenArtifact[]) m_PanelArtifacts.getCurrent(), ","));
    result.setProperty(BootstrappUtils.KEY_REPOSITORIES, Utils.flatten((MavenRepository[]) m_PanelRepositories.getCurrent(), ","));

    return result;
  }

  /**
   * Returns whether the panel requires a wrapper scrollpane/panel for display.
   *
   * @return		true if wrapper required
   */
  @Override
  public boolean requiresWrapper() {
    return true;
  }

  /**
   * Configures the bootstrap instance to use.
   *
   * @param props	the properties to use
   * @return		the configured instance
   */
  protected Main configureBootstrapp(Properties props) throws Exception {
    Main	result;

    props  = props.getClone();
    props.setPath(BootstrappUtils.KEY_OUTPUTDIR, getOutputDir());

    result = BootstrappUtils.propsToBootstrapp(props);
    result.clean(true);
    result.compressDirStructure(true);

    return result;
  }

  /**
   * Activates the setup.
   *
   * @return		null if successfully activated, otherwise error message
   */
  @Override
  public String activate() {
    String	result;
    boolean 	res;
    Properties	props;
    String 	filename;
    Main	bootstrapp;

    result   = null;
    props    = toProperties();
    filename = Environment.getInstance().createPropertiesFilename(getPropertiesFile());
    res = props.save(filename);
    if (res) {
      try {
        bootstrapp = configureBootstrapp(props);
        result     = bootstrapp.execute();
      }
      catch (Exception e) {
        result = "Failed to configure/execute bootstrapp:\n" + LoggingHelper.throwableToString(e);
      }
    }
    else {
      result = "Failed to save setup to " + filename + "!";
    }

    return result;
  }

  /**
   * Returns whether the panel supports resetting the options.
   *
   * @return		true if supported
   */
  public boolean canReset() {
    String 	filename;

    filename = Environment.getInstance().createPropertiesFilename(getPropertiesFile());
    return (filename != null) && FileUtils.fileExists(filename);
  }

  /**
   * Resets the settings to their default.
   *
   * @return		null if successfully reset, otherwise error message
   */
  public String reset() {
    String filename;

    filename = Environment.getInstance().createPropertiesFilename(getPropertiesFile());
    if ((filename != null) && FileUtils.fileExists(filename)) {
      if (!FileUtils.delete(filename))
	return "Failed to remove custom settings: " + filename;
    }

    return null;
  }
}
