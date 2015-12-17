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
 * Binding.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.annotator;


import adams.core.Properties;

import javax.swing.*;

/**
 * Represents a key binding
 * Bindings can be toggleable or not and can be inverted or not.
 * An inverted binding just means that when pressed it will output false rather than true
 * this makes it possible to represent a negative event X stops doing Y
 * as well as positive events X does Y in the final output.
 *
 * @author sjb90
 * @version $Revision$
 */
public class Binding {

  /**
   * the prefix number for the binding
   */
  int m_PrefixNumber;
  /**
   * the name of the binding
   */
  String m_Name;
  /**
   * the keystroke the name is bound to
   */
  KeyStroke m_Binding;
  /**
   * is the binding toggleable?
   */
  boolean m_Toggleable;

  /**
   * is the binding inverted
   */
  boolean m_Inverted;


  public Binding(int prefixNumber, String name, String binding, boolean toggle, boolean inverted) {
    this(prefixNumber, name, KeyStroke.getKeyStroke(binding), toggle, inverted);
  }

  /**
   * Constructor for the binding class.
   * @param prefixNumber the number to be prefixed onto this binding
   * @param name the name of the binding
   * @param binding the key to bind to
   * @param toggle is this binding toggleable
   * @param inverted is this binding inverted
   */
  public Binding(int prefixNumber, String name, KeyStroke binding, boolean toggle, boolean inverted) {
    m_PrefixNumber	= prefixNumber;
    m_Name        	= name;
    m_Binding 		= binding;
    m_Toggleable 	= toggle;
    m_Inverted 		= inverted;

  }

  public String getName() {
    return m_Name;
  }

  public KeyStroke getBinding() {
    return m_Binding;
  }

  public boolean isToggleable() {
    return m_Toggleable;
  }

  public boolean isInverted() {
    return m_Inverted;
  }

  /**
   * Turns this Binding into a property
   * @return a Properties object representing this key binding.
   */
  public Properties toProperty() {
    Properties props = new Properties();

    props.setProperty(m_PrefixNumber + "Name", m_Name);
    props.setProperty(m_PrefixNumber + "Name", m_Name);
    props.setBoolean(m_PrefixNumber + "Toggleable", m_Toggleable);
    props.setBoolean(m_PrefixNumber + "Inverted", m_Inverted);

    return props;

  }

  @Override
  public boolean equals(Object obj) {
    if(obj instanceof Binding) {
      if (((Binding) obj).m_PrefixNumber == m_PrefixNumber) {
	return true;
      }
    }
    return false;
  }
}
