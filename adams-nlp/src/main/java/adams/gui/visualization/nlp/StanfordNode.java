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
 * StanfordNode.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.nlp;

import adams.gui.core.BaseTreeNode;
import edu.stanford.nlp.trees.Tree;

/**
 * Specialized tree node.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 8629 $
 */
public class StanfordNode
  extends BaseTreeNode {

  /** for serialization. */
  private static final long serialVersionUID = 9062259637831548370L;

  /** the label for the root node in case of multiple hierarchies. */
  public final static String ROOT = "root";

  /** the underlying value. */
  protected Tree m_Value;

  /**
   * Initializes the node with the specified label.
   *
   * @param label	the label for this node
   * @param value	the parse sub-tree to attach, can be null
   */
  public StanfordNode(String label, Tree value) {
    super(label);

    m_Value = value;
  }

  /**
   * Returns the label for this node.
   *
   * @return		the label
   */
  public String getLabel() {
    return (String) getUserObject();
  }

  /**
   * Checks whether there is any JSON object attached.
   * 
   * @return		true if a value is attached
   */
  public boolean hasValue() {
    return (m_Value != null);
  }
  
  /**
   * Returns the value for this node.
   *
   * @return		the value
   */
  public Tree getValue() {
    return m_Value;
  }
}