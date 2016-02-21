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
 * StanfordParseTreeToSpreadSheet.java
 * Copyright (C) 2013-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import adams.core.Utils;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import edu.stanford.nlp.trees.Tree;

/**
 <!-- globalinfo-start -->
 * Turns the leaves of a Stanford parse tree into a spreadsheet.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 10826 $
 */
public class StanfordParseTreeToSpreadSheet
  extends AbstractConversion {

  /** for serialization. */
  private static final long serialVersionUID = -309330039614297403L;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Turns the leaves of a Stanford parse tree into a spreadsheet.";
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return		the class
   */
  @Override
  public Class accepts() {
    return Tree.class;
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return		the class
   */
  @Override
  public Class generates() {
    return SpreadSheet.class;
  }
  
  /**
   * Traverses the tree and adds the leaf data to the spreadsheet.
   * 
   * @param sheet	the sheet to add the data to
   * @param parentTree	the tree to process
   * @param path	the path of types
   */
  protected void traverseTree(SpreadSheet sheet, Tree parentTree, String[] path) {
    Tree	childTree;
    int		i;
    Row		row;
    String[]	newPath;
    
    for (i = 0; i < parentTree.children().length; i++) {
      childTree = parentTree.children()[i];
      newPath = new String[path.length + 1];
      System.arraycopy(path, 0, newPath, 0, path.length);
      newPath[newPath.length - 1] = parentTree.label().value();
      if (childTree.isLeaf()) {
	row       = sheet.addRow();
	row.addCell("W").setContent(childTree.label().value());
	row.addCell("T").setContent(parentTree.label().value());
	row.addCell("P").setContent(Utils.flatten(newPath, "."));
	row.addCell("S").setContent(parentTree.score());
      }
      traverseTree(sheet, childTree, newPath);
    }
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    SpreadSheet		result;
    Tree		tree;
    Row			row;
    
    result = new DefaultSpreadSheet();
    row    = result.getHeaderRow();
    row.addCell("W").setContent("Word");
    row.addCell("T").setContent("Type");
    row.addCell("P").setContent("Path");
    row.addCell("S").setContent("Score");
    tree   = (Tree) m_Input;
    traverseTree(result, tree, new String[0]);
    
    return result;
  }
}
