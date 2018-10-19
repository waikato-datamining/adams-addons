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
 * KeyPressTextField.java
 * Copyright (C) 2016-2018 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.tools.audioannotator;

import adams.gui.core.BaseTextField;

import javax.swing.KeyStroke;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * A text field that reads key presses and displays them
 *
 * @author sjb90
 */
public class KeyPressTextField extends BaseTextField {

  private static final long serialVersionUID = -1292173919485929114L;

  /** Store the last pressed key */
  protected KeyStroke m_LastPressed;

  /** the listener that retrieves the keypress */
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

  /**
   * a getter for the keypress currently stored in the text field
   * @return the last key pressed
   */
  public KeyStroke getLastPressed() {
    return m_LastPressed;
  }

  /**
   * a setter for the keypress stored in the field
   * @param lastPressed the key to use as the last pressed.
   */
  public void setLastPressed(KeyStroke lastPressed) {
    m_LastPressed = lastPressed;
  }
}
