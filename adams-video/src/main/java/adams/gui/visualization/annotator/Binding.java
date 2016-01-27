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
import adams.gui.core.GUIHelper;

import javax.swing.*;
import java.security.InvalidKeyException;

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

  public static final long DEFAULT_INTERVAL = 0;

  /**
   * the name of the binding
   */
  protected String m_Name;
  /**
   * the keystroke the name is bound to
   */
  protected KeyStroke m_Binding;
  /**
   * is the binding toggleable?
   */
  protected boolean m_Toggleable;

  /**
   * is the binding inverted
   */
  protected boolean m_Inverted;

  /** the interval to repete a toggleable binding */
  protected long m_Interval;

  public Binding(Properties props, String prefix) throws InvalidKeyException{
      this(props.getProperty(prefix + ".Name"),	GUIHelper.getKeyStroke(props.getProperty(prefix + ".Binding")),
	props.getBoolean(prefix + ".Toggleable"), props.getLong(prefix + ".Interval"),
	props.getBoolean(prefix + ".Inverted"));
  }
  /**
   * Constructor for the binding class.
   * @param name the name of the binding
   * @param binding the key to bind to in String format
   * @param toggle is this binding toggleable
   * @param inverted is this binding inverted
   */
  public Binding(String name, String binding, boolean toggle, long interval, boolean inverted) throws InvalidKeyException {
    this(name, GUIHelper.getKeyStroke(binding.toUpperCase()), toggle, interval, inverted);
  }

  /**
   * Constructor for the binding class.
   * @param name the name of the binding
   * @param binding the key to bind to as a KeyStroke
   * @param toggle is this binding toggleable
   * @param inverted is this binding inverted
   */
  public Binding(String name, KeyStroke binding, boolean toggle, long interval, boolean inverted) throws InvalidKeyException {
    if(binding == null)
      throw new InvalidKeyException("Key entered is not valid");
    m_Name        	= name;
    m_Binding 		= binding;
    m_Toggleable 	= toggle;
    m_Inverted 		= inverted;
    m_Interval		= interval;
  }

  /**
   * getter for the name of the binding
   * @return the name of the binding
   */
  public String getName() {
    return m_Name;
  }

  /**
   * getter for the keystroke of the binding
   * @return the keystroke for this binding
   */
  public KeyStroke getBinding() {
    return m_Binding;
  }

  /**
   * getter for the toggleable option
   * @return true if this binding is toggleable
   */
  public boolean isToggleable() {
    return m_Toggleable;
  }

  /**
   * getter for the inverted option
   * @return true if this binding is inverted
   */
  public boolean isInverted() {
    return m_Inverted;
  }

  /**
   * Turns this Binding into a property
   * @return a Properties object representing this key binding.
   */
  public Properties toProperty(int prefix) {
    Properties props = new Properties();

    props.setProperty(prefix + ".Name", m_Name);
    props.setProperty(prefix + ".Binding", m_Binding.toString());
    props.setBoolean(prefix + ".Toggleable", m_Toggleable);
    props.setBoolean(prefix + ".Inverted", m_Inverted);
    props.setLong(prefix + ".Interval", m_Interval);

    return props;

  }

  @Override
  public String toString() {
    return "Name: " + m_Name + " Binding: " + m_Binding.toString() + " Toggleable: " + m_Toggleable;
  }

  public long getInterval() {
    return m_Interval;
  }
}
