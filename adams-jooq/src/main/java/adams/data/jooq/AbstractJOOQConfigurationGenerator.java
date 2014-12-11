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
 * AbstractJOOQConfigurationGenerator.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.jooq;

import java.util.logging.Level;

import adams.core.QuickInfoHelper;
import adams.core.QuickInfoSupporter;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.core.option.AbstractOptionHandler;
import adams.db.AbstractDatabaseConnection;
import adams.db.DatabaseConnectionHandler;

/**
 * Ancestor for jOOQ XML configuration file generators.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 8697 $
 */
public abstract class AbstractJOOQConfigurationGenerator
  extends AbstractOptionHandler
  implements DatabaseConnectionHandler, QuickInfoSupporter {

  /** for serialization. */
  private static final long serialVersionUID = -6440641992533849403L;

  /** the XML file to generate. */
  protected PlaceholderFile m_Output;
  
  /** the provider for the code generator to use. */
  protected AbstractJOOQCodeGeneratorProvider m_Provider;

  /** the connection to use. */
  protected AbstractDatabaseConnection m_DatabaseConnection;
  
  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "output", "output",
	    new PlaceholderFile("."));

    m_OptionManager.add(
	    "provider", "provider",
	    new JavaCodeGeneratorProvider());
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_DatabaseConnection = null;
  }
  
  /**
   * Sets the file to store the XML configuration in.
   *
   * @param value 	the output file
   */
  public void setOutput(PlaceholderFile value) {
    m_Output = value;
    reset();
  }

  /**
   * Returns the file to store the XML configuration in.
   *
   * @return 		the output file
   */
  public PlaceholderFile getOutput() {
    return m_Output;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputTipText() {
    return "The file to store the XML configuration in.";
  }

  /**
   * Sets the provider for the code generator to use.
   *
   * @param value 	the provider
   */
  public void setProvider(AbstractJOOQCodeGeneratorProvider value) {
    m_Provider = value;
    reset();
  }

  /**
   * Returns the provider for the code generator to use.
   *
   * @return 		the provider
   */
  public AbstractJOOQCodeGeneratorProvider getProvider() {
    return m_Provider;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String providerTipText() {
    return "The provider for the jOOQ code generator to use.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    String	result;
    
    result  = QuickInfoHelper.toString(this, "output", m_Output, "output: ");
    result += QuickInfoHelper.toString(this, "provider", m_Provider, ", provider: ");
    
    return result;
  }

  /**
   * Hook method for performing checks.
   */
  protected void check() {
    if (m_DatabaseConnection == null)
      throw new IllegalStateException("No database connection set!");
    
    if (m_Provider == null)
      throw new IllegalStateException("No jOOQ code generator set!");
    
    if (m_Output.isDirectory())
      throw new IllegalStateException("Output points to directory: " + m_Output);
    
    if (!m_Output.getParentFile().exists() || !m_Output.getParentFile().isDirectory())
      throw new IllegalStateException("Output directory either does not exist or is not a directory: " + m_Output.getParentFile());
  }

  /**
   * Returns the currently used database connection object, can be null.
   *
   * @return		the current object
   */
  public AbstractDatabaseConnection getDatabaseConnection() {
    return m_DatabaseConnection;
  }

  /**
   * Sets the database connection object to use.
   *
   * @param value	the object to use
   */
  public void setDatabaseConnection(AbstractDatabaseConnection value) {
    m_DatabaseConnection = value;
  }
  
  /**
   * Performs the actual generation.
   * 
   * @return		the XML content, null if failed to generate
   * @throws Exception	if an error occurs
   */
  protected abstract String doGenerate() throws Exception;
  
  /**
   * Generates the XML configuration.
   * 
   * @return		the generated XML file or null in case of an error
   */
  public PlaceholderFile generate() {
    String	content;
    
    check();
    
    try {
      content = doGenerate();
    }
    catch (Exception e) {
      content = null;
      getLogger().log(Level.SEVERE, "Failed to generate XML configuration", e);
      return null;
    }
    
    if (content == null) {
      getLogger().severe("Failed to generate XML configuration");
      return null;
    }
    if (!FileUtils.writeToFile(m_Output.getAbsolutePath(), content, false)) {
      getLogger().severe("Failed to write XML configuration to: " + m_Output);
      return null;
    }
    
    return m_Output;
  }
}
