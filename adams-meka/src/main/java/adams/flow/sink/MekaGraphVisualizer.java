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
 * MekaGraphVisualizer.java
 * Copyright (C) 2016-2023 University of Waikato, Hamilton, NZ
 */

package adams.flow.sink;

import adams.flow.container.MekaResultContainer;
import adams.flow.core.Token;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseTabbedPane;
import meka.core.MultiLabelDrawable;
import weka.gui.graphvisualizer.GraphVisualizer;
import weka.gui.treevisualizer.Node;
import weka.gui.treevisualizer.PlaceNode2;
import weka.gui.treevisualizer.TreeBuild;
import weka.gui.treevisualizer.TreeVisualizer;

import javax.swing.JComponent;
import java.awt.BorderLayout;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;

/**
 <!-- globalinfo-start -->
 * Displays graphs obtained from a model that implements meka.core.MultiLabelDrawable.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;meka.core.MultiLabelDrawable<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.MekaResultContainer<br>
 * <br><br>
 * Container information:<br>
 * - adams.flow.container.MekaResultContainer: Result, Model
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: MekaGraphVisualizer
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-short-title &lt;boolean&gt; (property: shortTitle)
 * &nbsp;&nbsp;&nbsp;If enabled uses just the name for the title instead of the actor's full 
 * &nbsp;&nbsp;&nbsp;name.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-display-in-editor &lt;boolean&gt; (property: displayInEditor)
 * &nbsp;&nbsp;&nbsp;If enabled displays the panel in a tab in the flow editor rather than in 
 * &nbsp;&nbsp;&nbsp;a separate frame.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-width &lt;int&gt; (property: width)
 * &nbsp;&nbsp;&nbsp;The width of the dialog.
 * &nbsp;&nbsp;&nbsp;default: 800
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 * <pre>-height &lt;int&gt; (property: height)
 * &nbsp;&nbsp;&nbsp;The height of the dialog.
 * &nbsp;&nbsp;&nbsp;default: 600
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 * <pre>-x &lt;int&gt; (property: x)
 * &nbsp;&nbsp;&nbsp;The X position of the dialog (&gt;=0: absolute, -1: left, -2: center, -3: right
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -3
 * </pre>
 * 
 * <pre>-y &lt;int&gt; (property: y)
 * &nbsp;&nbsp;&nbsp;The Y position of the dialog (&gt;=0: absolute, -1: top, -2: center, -3: bottom
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -3
 * </pre>
 * 
 * <pre>-writer &lt;adams.gui.print.JComponentWriter&gt; (property: writer)
 * &nbsp;&nbsp;&nbsp;The writer to use for generating the graphics output.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.print.NullWriter
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class MekaGraphVisualizer
  extends AbstractGraphicalDisplay
  implements DisplayPanelProvider {

  private static final long serialVersionUID = -4346704926636614739L;

  /** the tabbed pane for the graphs. */
  protected BaseTabbedPane m_TabbedPane;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Displays graphs obtained from a model that implements "
      + MultiLabelDrawable.class.getName() + ".";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{MultiLabelDrawable.class, MekaResultContainer.class};
  }

  /**
   * Creates the panel to display in the dialog.
   *
   * @return		the panel
   */
  @Override
  protected BasePanel newPanel() {
    BasePanel   result;

    result       = new BasePanel(new BorderLayout());
    m_TabbedPane = new BaseTabbedPane();
    result.add(m_TabbedPane, BorderLayout.CENTER);

    return result;
  }

  /**
   * Whether "clear" is supported and shows up in the menu.
   *
   * @return		true if supported
   */
  @Override
  public boolean supportsClear() {
    return true;
  }

  /**
   * Clears the content of the panel.
   */
  @Override
  public void clearPanel() {
    if (m_TabbedPane != null)
      m_TabbedPane.removeAll();
  }

  /**
   * Displays the token (the panel and dialog have already been created at
   * this stage).
   *
   * @param token	the token to display
   */
  @Override
  protected void display(Token token) {
    MultiLabelDrawable  	model;
    Map<Integer,String> 	graphs;
    Map<Integer,Integer> 	types;
    java.util.List<Integer> 	keys;

    if (token.getPayload() instanceof MultiLabelDrawable)
      model = (MultiLabelDrawable) token.getPayload();
    else
      model = (MultiLabelDrawable) ((MekaResultContainer) token.getPayload()).getValue(MekaResultContainer.VALUE_MODEL);

    try {
      types  = model.graphType();
      graphs = model.graph();
      keys   = new ArrayList<Integer>(types.keySet());
    }
    catch (Exception ex) {
      getLogger().log(Level.SEVERE, "Failed to obtain graph(s):", ex);
      return;
    }

    for (Integer label: keys) {
      int type = types.get(label);
      JComponent comp = null;
      switch (type) {
	case MultiLabelDrawable.TREE:
	  TreeBuild b = new TreeBuild();
	  PlaceNode2 arrange = new PlaceNode2();
	  Node top = b.create(new StringReader(graphs.get(label)));
	  comp = new TreeVisualizer(null, top, arrange);
	  break;
	case MultiLabelDrawable.BayesNet:
	  GraphVisualizer g = new GraphVisualizer();
	  g.readDOT(new StringReader(graphs.get(label)));
	  comp = g;
	  break;
	default:
	  System.err.println("Unsupported graph type for label " + label + ": " + type);
      }
      if (comp != null)
	m_TabbedPane.addTab("" + label, comp);
    }
  }

  /**
   * Returns whether the created display panel requires a scroll pane or not.
   *
   * @return		true if the display panel requires a scroll pane
   */
  public boolean displayPanelRequiresScrollPane() {
    return false;
  }

  /**
   * Creates a new display panel for the token.
   *
   * @param token	the token to display in a new panel, can be null
   * @return		the generated panel
   */
  public DisplayPanel createDisplayPanel(Token token) {
    AbstractDisplayPanel	result;

    result = new AbstractComponentDisplayPanel(getClass().getSimpleName()) {
      private static final long serialVersionUID = 4356468458332186521L;
      protected BaseTabbedPane m_TabbedPane;
      @Override
      protected void initGUI() {
	super.initGUI();
	m_TabbedPane = new BaseTabbedPane();
	add(m_TabbedPane, BorderLayout.CENTER);
      }
      @Override
      public void display(Token token) {
	MultiLabelDrawable model;
	if (token.getPayload() instanceof MultiLabelDrawable)
	  model = (MultiLabelDrawable) token.getPayload();
	else
	  model = (MultiLabelDrawable) ((MekaResultContainer) token.getPayload()).getValue(MekaResultContainer.VALUE_MODEL);
	Map<Integer,String> graphs;
	Map<Integer,Integer> types;
	java.util.List<Integer> keys;
	try {
	  types  = model.graphType();
	  graphs = model.graph();
	  keys   = new ArrayList<Integer>(types.keySet());
	}
	catch (Exception ex) {
	  getLogger().log(Level.SEVERE, "Failed to obtain graph(s):", ex);
	  return;
	}
	for (Integer label: keys) {
	  int type = types.get(label);
	  JComponent comp = null;
	  switch (type) {
	    case MultiLabelDrawable.TREE:
	      TreeBuild b = new TreeBuild();
	      PlaceNode2 arrange = new PlaceNode2();
	      Node top = b.create(new StringReader(graphs.get(label)));
	      comp = new TreeVisualizer(null, top, arrange);
	      break;
	    case MultiLabelDrawable.BayesNet:
	      GraphVisualizer g = new GraphVisualizer();
	      g.readDOT(new StringReader(graphs.get(label)));
	      comp = g;
	      break;
	    default:
	      System.err.println("Unsupported graph type for label " + label + ": " + type);
	  }
	  if (comp != null)
	    m_TabbedPane.addTab("" + label, comp);
	}
      }
      @Override
      public void clearPanel() {
	m_TabbedPane.removeAll();
      }
      @Override
      public void cleanUp() {
	m_TabbedPane.removeAll();
      }
      @Override
      public JComponent supplyComponent() {
	return m_TabbedPane;
      }
    };

    if (token != null)
      result.display(token);

    return result;
  }
}
