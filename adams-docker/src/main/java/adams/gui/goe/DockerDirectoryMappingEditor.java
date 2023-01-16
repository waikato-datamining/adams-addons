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
 *    DockerDirectoryMappingEditor.java
 *    Copyright (C) 2010-2022 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.goe;

import adams.core.base.BaseObject;
import adams.core.base.DockerDirectoryMapping;
import adams.core.io.PlaceholderDirectory;
import adams.core.option.parsing.DockerDirectoryMappingParsing;
import adams.gui.chooser.DirectoryChooserPanel;
import adams.gui.core.BaseButton;
import adams.gui.core.BaseTextField;
import adams.gui.core.GUIHelper;
import adams.gui.core.ParameterPanel;

import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * A PropertyEditor for DockerDirectoryMapping objects that lets the user select
 * a directory.
 * <br><br>
 * Based on <code>weka.gui.FileEditor</code>.
 *
 * @author Len Trigg (trigg@cs.waikato.ac.nz)
 */
public class DockerDirectoryMappingEditor
  extends BaseObjectEditor
  implements MultiSelectionEditor {

  /** The text field with the key. */
  protected DirectoryChooserPanel m_TextLocal;

  /**
   * Accepts the input and closes the dialog.
   */
  protected void acceptInput() {
    String 	localDir;
    String 	containerDir;
    String	pair;

    localDir = m_TextLocal.getCurrent().getAbsolutePath();
    containerDir = m_TextValue.getText();
    pair  = localDir + ":" + containerDir;
    if (isValid(pair) && !isUnchanged(pair))
      setValue(parse(pair));
    closeDialog(APPROVE_OPTION);
  }

  /**
   * Gets the custom editor component.
   *
   * @return 		the editor
   */
  @Override
  protected JComponent createCustomEditor() {
    JPanel panelAll;
    ParameterPanel panelPair;
    JPanel 		panelButtons;
    BaseButton buttonClear;
    BaseButton 		buttonOK;
    BaseButton 		buttonClose;

    panelAll  = new JPanel(new BorderLayout());
    panelPair = new ParameterPanel();
    panelAll.add(panelPair, BorderLayout.CENTER);

    m_TextLocal = new DirectoryChooserPanel();
    m_TextValue = new BaseTextField(30);

    panelPair.addParameter("_Local", m_TextLocal);
    panelPair.addParameter("_Container", m_TextValue);

    panelButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    panelAll.add(panelButtons, BorderLayout.SOUTH);

    buttonClear = new BaseButton("Clear");
    buttonClear.setMnemonic('l');
    buttonClear.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	m_TextLocal.setCurrent(new PlaceholderDirectory());
	m_TextValue.setText("");
      }
    });
    panelButtons.add(buttonClear);

    buttonOK = new BaseButton("OK");
    buttonOK.setMnemonic('O');
    buttonOK.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	acceptInput();
      }
    });
    panelButtons.add(buttonOK);

    buttonClose = new BaseButton("Cancel");
    buttonClose.setMnemonic('C');
    buttonClose.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	discardInput();
      }
    });
    panelButtons.add(buttonClose);

    return panelAll;
  }

  /**
   * Initializes the display of the value.
   */
  @Override
  protected void initForDisplay() {
    DockerDirectoryMapping value;

    resetChosenOption();

    value = (DockerDirectoryMapping) getValue();

    if (!m_TextLocal.getCurrent().getAbsolutePath().equals(value.localDir()))
      m_TextLocal.setCurrent(new PlaceholderDirectory(value.localDir()));
    if (!m_TextValue.getText().equals(value.containerDir()))
      m_TextValue.setText(value.containerDir());
    m_TextLocal.setToolTipText(((BaseObject) getValue()).getTipText());
    m_TextValue.setToolTipText(((BaseObject) getValue()).getTipText());
    m_TextLocal.grabFocus();
  }

  /**
   * Returns the string to paint.
   *
   * @return		the string
   * @see		#paintValue(Graphics, Rectangle)
   */
  @Override
  protected String getStringToPaint() {
    return ((DockerDirectoryMapping) getValue()).getValue();
  }

  /**
   * Returns a custom string representation of the object.
   *
   * @param obj		the object to turn into a string
   * @return		the string representation
   */
  @Override
  public String toCustomStringRepresentation(Object obj) {
    return DockerDirectoryMappingParsing.toString(null, obj);
  }

  /**
   * Returns an object created from the custom string representation.
   *
   * @param str		the string to turn into an object
   * @return		the object
   */
  @Override
  public Object fromCustomStringRepresentation(String str) {
    return DockerDirectoryMappingParsing.valueOf(null, str);
  }

  /**
   * Returns the selected objects.
   *
   * @param parent	the parent container
   * @return		the objects
   */
  @Override
  public Object[] getSelectedObjects(Container parent) {
    DockerDirectoryMapping[]		result;
    MultiLineValueDialog	dialog;
    List<String> lines;
    int				i;

    if (GUIHelper.getParentDialog(parent) != null)
      dialog = new MultiLineValueDialog(GUIHelper.getParentDialog(parent));
    else
      dialog = new MultiLineValueDialog(GUIHelper.getParentFrame(parent));
    dialog.setInfoText("Enter the local/container dir pairs, one per line (separator ':'):");
    dialog.setLocationRelativeTo(parent);
    dialog.setVisible(true);

    lines  = dialog.getValues();
    result = new DockerDirectoryMapping[lines.size()];
    for (i = 0; i < lines.size(); i++)
      result[i] = (DockerDirectoryMapping) parse(lines.get(i));

    return result;
  }
}
