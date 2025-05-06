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
 * FixedID.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.data.djl.idgenerator;

/**
 * Just uses the supplied ID.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class FixedID
  extends AbstractIDGenerator {

  private static final long serialVersionUID = 6971300631570600112L;

  /** the ID to use. */
  protected String m_ID;

  /**
   * Default constructor.
   */
  public FixedID() {
    super();
  }

  /**
   * Instantiates the generator and sets the ID to use.
   *
   * @param id		the ID to use
   */
  public FixedID(String id) {
    super();
    setID(id);
  }

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Just uses the supplied ID.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "id", "ID",
      "djl");
  }

  /**
   * Sets the ID/prefix for saving the model.
   *
   * @param value 	the ID/prefix
   */
  public void setID(String value) {
    m_ID = value;
    reset();
  }

  /**
   * Gets the ID/prefix for saving the model.
   *
   * @return 		the ID/prefix
   */
  public String getID() {
    return m_ID;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String IDTipText() {
    return "The ID to use.";
  }

  /**
   * Generates the ID.
   *
   * @return the ID
   */
  @Override
  public String generate() {
    return m_ID;
  }
}
