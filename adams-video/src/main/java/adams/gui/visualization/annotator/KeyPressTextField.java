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
 * KeyPressTextField.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.annotator;

import adams.gui.core.GUIHelper;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * A text field that reads key presses and displays them
 *
 * @author sjb90
 * @version $Revision$
 */
public class KeyPressTextField extends JTextField {

  /** Store the last pressed key */
  protected KeyStroke m_LastPressed;

  protected KeyListener m_KeyListener;


  public KeyPressTextField() {
    setEditable(false);
    m_KeyListener = new KeyListener() {
      @Override
      public void keyTyped(KeyEvent e) {

      }

      @Override
      public void keyPressed(KeyEvent e) {
	System.out.println("Might have worked");
	m_LastPressed = KeyStroke.getKeyStrokeForEvent(e);
	setText(m_LastPressed.toString());
      }

      @Override
      public void keyReleased(KeyEvent e) {

      }
    };
    addKeyListener(m_KeyListener);
  }

  public KeyStroke getLastPressed() {
    return m_LastPressed;
  }

  public void setLastPressed(KeyStroke lastPressed) {
    m_LastPressed = lastPressed;
  }
}
