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
 * NewSection.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.doc.latex.generator;

/**
 <!-- globalinfo-start -->
 * Adds a new section to the document with an optional label for cross-referencing.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-no-var-expansion &lt;boolean&gt; (property: noVariableExpansion)
 * &nbsp;&nbsp;&nbsp;If enabled, variable expansion gets skipped.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-page-action &lt;NONE|NEWPAGE|CLEARPAGE&gt; (property: pageAction)
 * &nbsp;&nbsp;&nbsp;The page action to insert.
 * &nbsp;&nbsp;&nbsp;default: NONE
 * </pre>
 * 
 * <pre>-type &lt;PART|CHAPTER|SECTION|SUBSECTION|SUBSUBSECTION|PARAGRAPH|SUBPARAGRAPH&gt; (property: type)
 * &nbsp;&nbsp;&nbsp;The type of the section.
 * &nbsp;&nbsp;&nbsp;default: SECTION
 * </pre>
 * 
 * <pre>-title &lt;java.lang.String&gt; (property: title)
 * &nbsp;&nbsp;&nbsp;The title of the section.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-label &lt;java.lang.String&gt; (property: label)
 * &nbsp;&nbsp;&nbsp;The optional label of the section (for cross-referencing).
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class NewSection
  extends AbstractCodeGenerator {

  private static final long serialVersionUID = 7225514457280622837L;

  /**
   * The page action.
   */
  public enum PageAction {
    NONE,
    NEWPAGE,
    CLEARPAGE,
  }

  /**
   * The types of sections.
   */
  public enum SectionType {
    PART,
    CHAPTER,
    SECTION,
    SUBSECTION,
    SUBSUBSECTION,
    PARAGRAPH,
    SUBPARAGRAPH,
  }

  /** the page action. */
  protected PageAction m_PageAction;

  /** the type of section. */
  protected SectionType m_Type;
  
  /** the title of the section. */
  protected String m_Title;

  /** the (optional) label for the section. */
  protected String m_Label;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Adds a new section to the document with an optional label for cross-referencing.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "page-action", "pageAction",
      PageAction.NONE);

    m_OptionManager.add(
      "type", "type",
      SectionType.SECTION);

    m_OptionManager.add(
      "title", "title",
      "");

    m_OptionManager.add(
      "label", "label",
      "");
  }

  /**
   * Sets the page action.
   *
   * @param value	the action
   */
  public void setPageAction(PageAction value) {
    m_PageAction = value;
    reset();
  }

  /**
   * Returns the page action.
   *
   * @return		the action
   */
  public PageAction getPageAction() {
    return m_PageAction;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String pageActionTipText() {
    return "The page action to insert.";
  }

  /**
   * Sets the type.
   *
   * @param value	the type
   */
  public void setType(SectionType value) {
    m_Type = value;
    reset();
  }

  /**
   * Returns the type.
   *
   * @return		the type
   */
  public SectionType getType() {
    return m_Type;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String typeTipText() {
    return "The type of the section.";
  }

  /**
   * Sets the title.
   *
   * @param value	the title
   */
  public void setTitle(String value) {
    m_Title = value;
    reset();
  }

  /**
   * Returns the title.
   *
   * @return		the title
   */
  public String getTitle() {
    return m_Title;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String titleTipText() {
    return "The title of the section.";
  }

  /**
   * Sets the label.
   *
   * @param value	the label
   */
  public void setLabel(String value) {
    m_Label = value;
    reset();
  }

  /**
   * Returns the label.
   *
   * @return		the label
   */
  public String getLabel() {
    return m_Label;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String labelTipText() {
    return "The optional label of the section (for cross-referencing).";
  }

  /**
   * Generates the actual code.
   *
   * @return		the generated code
   */
  @Override
  protected String doGenerate() {
    StringBuilder	result;

    result = new StringBuilder("\n");

    if (m_PageAction != PageAction.NONE)
      result.append("\\").append(m_PageAction.toString().toLowerCase()).append("\n");

    result.append("\\").append(m_Type.toString().toLowerCase()).append("{");
    result.append(expand(m_Title));
    result.append("}\n");
    if (!m_Label.isEmpty())
      result.append("\\label{").append(expand(m_Label)).append("}\n");
    result.append("\n");

    return result.toString();
  }
}
