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
 * BasicConfigurationGenerator.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.jooq;

import java.sql.DriverManager;

import org.jooq.SQLDialect;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.base.BaseRegExp;
import adams.core.io.PlaceholderDirectory;

/**
 <!-- globalinfo-start -->
 * Basic generator.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-output &lt;adams.core.io.PlaceholderFile&gt; (property: output)
 * &nbsp;&nbsp;&nbsp;The file to store the XML configuration in.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 * <pre>-provider &lt;adams.data.jooq.AbstractJOOQCodeGeneratorProvider&gt; (property: provider)
 * &nbsp;&nbsp;&nbsp;The provider for the jOOQ code generator to use.
 * &nbsp;&nbsp;&nbsp;default: adams.data.jooq.JavaCodeGeneratorProvider
 * </pre>
 * 
 * <pre>-dialect &lt;SQL99|CUBRID|DERBY|FIREBIRD|H2|HSQLDB|MARIADB|MYSQL|POSTGRES|SQLITE&gt; (property: dialect)
 * &nbsp;&nbsp;&nbsp;The SQL dialect to use.
 * &nbsp;&nbsp;&nbsp;default: MYSQL
 * </pre>
 * 
 * <pre>-schema &lt;java.lang.String&gt; (property: schema)
 * &nbsp;&nbsp;&nbsp;The database schema to use.
 * &nbsp;&nbsp;&nbsp;default: somedatabase
 * </pre>
 * 
 * <pre>-include &lt;adams.core.base.BaseRegExp&gt; [-include ...] (property: includes)
 * &nbsp;&nbsp;&nbsp;The regular expressions to use for including tables of the schema.
 * &nbsp;&nbsp;&nbsp;default: .*
 * </pre>
 * 
 * <pre>-exclude &lt;adams.core.base.BaseRegExp&gt; [-exclude ...] (property: excludes)
 * &nbsp;&nbsp;&nbsp;The regular expressions to use for excluding tables of the schema.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-package-name &lt;java.lang.String&gt; (property: packageName)
 * &nbsp;&nbsp;&nbsp;The Java package name to use.
 * &nbsp;&nbsp;&nbsp;default: adams.db.database
 * </pre>
 * 
 * <pre>-output-dir &lt;adams.core.io.PlaceholderDirectory&gt; (property: outputDir)
 * &nbsp;&nbsp;&nbsp;The directory to store the generated code in.
 * &nbsp;&nbsp;&nbsp;default: ${TMP}
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 8697 $
 */
public class BasicConfigurationGenerator
  extends AbstractJOOQConfigurationGenerator {

  /** for serialization. */
  private static final long serialVersionUID = -3726686726236010246L;
  
  /** the SQL dialect. */
  protected SQLDialect m_Dialect;
  
  /** the schema. */
  protected String m_Schema;
  
  /** the includes. */
  protected BaseRegExp[] m_Includes;
  
  /** the excludes. */
  protected BaseRegExp[] m_Excludes;
  
  /** the package name. */
  protected String m_PackageName;
  
  /** the output directory. */
  protected PlaceholderDirectory m_OutputDir;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Basic generator.";
  }
  
  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "dialect", "dialect",
	    SQLDialect.MYSQL);

    m_OptionManager.add(
	    "schema", "schema",
	    "somedatabase");

    m_OptionManager.add(
	    "include", "includes",
	    new BaseRegExp[]{new BaseRegExp(BaseRegExp.MATCH_ALL)});

    m_OptionManager.add(
	    "exclude", "excludes",
	    new BaseRegExp[0]);

    m_OptionManager.add(
	    "package-name", "packageName",
	    "adams.db.database");

    m_OptionManager.add(
	    "output-dir", "outputDir",
	    new PlaceholderDirectory("${TMP}"));
  }
  
  /**
   * Sets the SQL dialect to use.
   *
   * @param value 	the dialect
   */
  public void setDialect(SQLDialect value) {
    m_Dialect = value;
    reset();
  }

  /**
   * Returns the SQL dialect to use.
   *
   * @return 		the dialect
   */
  public SQLDialect getDialect() {
    return m_Dialect;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String dialectTipText() {
    return "The SQL dialect to use.";
  }
  
  /**
   * Sets the schema to use.
   *
   * @param value 	the schema
   */
  public void setSchema(String value) {
    m_Schema = value;
    reset();
  }

  /**
   * Returns the schema to use.
   *
   * @return 		the schema
   */
  public String getSchema() {
    return m_Schema;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String schemaTipText() {
    return "The database schema to use.";
  }
  
  /**
   * Sets the regular expressions for including tables from the schema.
   *
   * @param value 	the includes
   */
  public void setIncludes(BaseRegExp[] value) {
    m_Includes = value;
    reset();
  }

  /**
   * Returns the regular expressions for including tables from the schema.
   *
   * @return 		the includes
   */
  public BaseRegExp[] getIncludes() {
    return m_Includes;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String includesTipText() {
    return "The regular expressions to use for including tables of the schema.";
  }
  
  /**
   * Sets the regular expressions for excluding tables from the schema.
   *
   * @param value 	the excludes
   */
  public void setExcludes(BaseRegExp[] value) {
    m_Excludes = value;
    reset();
  }

  /**
   * Returns the regular expressions for excluding tables from the schema.
   *
   * @return 		the excludes
   */
  public BaseRegExp[] getExcludes() {
    return m_Excludes;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String excludesTipText() {
    return "The regular expressions to use for excluding tables of the schema.";
  }
  
  /**
   * Sets the package name to use.
   *
   * @param value 	the package name
   */
  public void setPackageName(String value) {
    m_PackageName = value;
    reset();
  }

  /**
   * Returns the package name to use.
   *
   * @return 		the package name
   */
  public String getPackageName() {
    return m_PackageName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String packageNameTipText() {
    return "The Java package name to use.";
  }

  /**
   * Sets the directory to store the code in.
   *
   * @param value 	the output dir
   */
  public void setOutputDir(PlaceholderDirectory value) {
    m_OutputDir = value;
    reset();
  }

  /**
   * Returns the directory to store the code in.
   *
   * @return 		the output dir
   */
  public PlaceholderDirectory getOutputDir() {
    return m_OutputDir;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputDirTipText() {
    return "The directory to store the generated code in.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    
    result  = super.getQuickInfo();
    result += QuickInfoHelper.toString(this, "schema", m_Schema, ", schema: ");
    result += QuickInfoHelper.toString(this, "dialect", m_Dialect, ", dialect: ");
    result += QuickInfoHelper.toString(this, "outputDir", m_OutputDir, ", output-dir: ");
    result += QuickInfoHelper.toString(this, "packageName", m_PackageName, ", package: ");
    
    return result;
  }

  /**
   * Performs the actual generation.
   * 
   * @return		the XML content, null if failed to generate
   * @throws Exception	if an error occurs
   */
  @Override
  protected String doGenerate() throws Exception {
    StringBuilder	result;
    
    result = new StringBuilder();
    
    result.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n");
    result.append("<configuration xmlns=\"http://www.jooq.org/xsd/jooq-codegen-3.2.0.xsd\">\n");
    result.append("  <jdbc>\n");
    result.append("    <driver>" + DriverManager.getDriver(m_DatabaseConnection.getURL()).getClass().getName() + "</driver>\n");  // TODO?
    result.append("    <url>" + m_DatabaseConnection.getURL() + "</url>\n");
    result.append("    <user>" + m_DatabaseConnection.getUser() + "</user>\n");
    result.append("    <password>" + m_DatabaseConnection.getPassword().getValue() + "</password>\n");
    result.append("  </jdbc>\n");
    result.append("  <generator>\n");
    result.append("    <name>" + m_Provider.generate().getClass().getName() + "</name>\n");
    result.append("    <database>\n");
    result.append("      <name>org.jooq.util." + m_Dialect.getNameLC() + "." + m_Dialect.getName() + "Database</name>\n");
    result.append("      <inputSchema>" + m_Schema + "</inputSchema>\n");
    result.append("      <includes>" + Utils.flatten(m_Includes, "|") + "</includes>\n");
    result.append("      <excludes>" + Utils.flatten(m_Excludes, "|") + "</excludes>\n");
    result.append("    </database>\n");
    result.append("    <target>\n");
    result.append("      <packageName>" + m_PackageName + "</packageName>\n");
    result.append("      <directory>" + m_OutputDir.getAbsolutePath() + "</directory>\n");
    result.append("    </target>\n");
    result.append("  </generator>\n");
    result.append("</configuration>\n"); 
    
    return result.toString();
  }
}
