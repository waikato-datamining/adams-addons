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
 * JsonToObject.java
 * Copyright (C) 2019 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import adams.core.QuickInfoHelper;
import adams.core.base.BaseClassname;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;

/**
 <!-- globalinfo-start -->
 * Maps the JSON string to the specified class using Jackson's Databind functionality (https:&#47;&#47;github.com&#47;FasterXML&#47;jackson-databind).
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-classname &lt;adams.core.base.BaseClassname&gt; (property: classname)
 * &nbsp;&nbsp;&nbsp;The class to map the JSON string to.
 * &nbsp;&nbsp;&nbsp;default: java.util.HashMap
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class JsonToObject
  extends AbstractConversionFromString {

  /** for serialization. */
  private static final long serialVersionUID = 1383459505178870114L;

  /** the class to map the json to. */
  protected BaseClassname m_Classname;

  /** the mapper instance to use. */
  protected transient ObjectMapper m_Mapper;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "Maps the JSON string to the specified class using Jackson's Databind functionality (https://github.com/FasterXML/jackson-databind).";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "classname", "classname",
      new BaseClassname(HashMap.class));
  }

  /**
   * Sets the class to map the JSON string to.
   *
   * @param value	the classname
   */
  public void setClassname(BaseClassname value) {
    m_Classname = value;
    reset();
  }

  /**
   * Returns the class to map the JSON string to.
   *
   * @return		the classname
   */
  public BaseClassname getClassname() {
    return m_Classname;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String classnameTipText() {
    return "The class to map the JSON string to.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "classname", m_Classname);
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return		the class
   */
  @Override
  public Class generates() {
    return m_Classname.classValue();
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  protected Object doConvert() throws Exception {
    if (m_Mapper == null)
      m_Mapper = new ObjectMapper();

    return m_Mapper.readValue((String) m_Input, m_Classname.classValue());
  }
}
