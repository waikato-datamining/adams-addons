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
 * BindingParameterPanel.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.annotator;

import adams.gui.core.ParameterPanel;

import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JTextField;
import javax.swing.text.MaskFormatter;
import java.security.InvalidKeyException;
import java.text.NumberFormat;

/**
 * A Parameter Panel that does the work of taking user input and turning it into a binding
 *
 * @author sjb90
 * @version $Revision$
 */
public class BindingParameterPanel extends ParameterPanel {

  private static final long serialVersionUID = -3909613160168503521L;

  private static final int DEFAULT_TIMEOUT = 1000;

  /** a text field for the name */
  protected JTextField m_NameField;

  /** a text field for the key press */
  protected KeyPressTextField m_BindingField;

  /** a checkbox to indicate if the binding is toggleable or not */
  protected JCheckBox m_Toggleable;

  /** a checkbox to indicate if the binding is inverted */
  protected JCheckBox m_Inverted;

  /** an input field for the interval for this binding */
  protected JFormattedTextField m_Interval;

  /** a mask formatter to make sure the input is valid */
  protected MaskFormatter m_MaskFormat;

  @Override
  protected void initGUI() {
    super.initGUI();
    try {
      m_MaskFormat = new MaskFormatter("U");
    }
    catch (java.text.ParseException e) {
      System.err.println("formatter is bad: " + e.getMessage());
    }

    m_NameField = new JTextField();
    m_BindingField = new KeyPressTextField();
    m_Toggleable = new JCheckBox();
    m_Inverted = new JCheckBox();
    m_Interval = new JFormattedTextField(NumberFormat.getNumberInstance());
    m_Interval.setValue(DEFAULT_TIMEOUT);

    addParameter(false, "Name", m_NameField);
    addParameter(false, "Binding", m_BindingField);
    addParameter(false, "Toggleable", m_Toggleable);
    addParameter(false, "Interval (in milliseconds)", m_Interval);
    addParameter(false, "Inverted", m_Inverted);
  }

  /**
   * Clears all fields so we can enter fresh data.
   */
  public void clearFields() {
    m_NameField.setText("");
    m_BindingField.setText("");
    m_Toggleable.setSelected(false);
    m_Inverted.setSelected(false);
    m_Interval.setValue(DEFAULT_TIMEOUT);
  }

  /**
   * Returns the binding based on the currently entered info
   * @return the binding generated or null if not valid inputs
   */
  public Binding getBinding() {
    long interval = ((Number)m_Interval.getValue()).longValue();
    try {
      Binding b = new Binding(m_NameField.getText(), m_BindingField.getLastPressed(), m_Toggleable.isSelected(),
	interval, m_Inverted.isSelected());
      clearFields();
      System.out.println("Binding in Parameter Panel " + b.toString() + " Interval " + b.getInterval());
      return b;
    }
    catch(InvalidKeyException e) {
      System.err.println(e.getMessage());
      return null;
    }
  }

  /**
   * Loads a binding into the panel for editing
   * @param binding the binding to be edited
   */
  public void loadBinding(Binding binding) {
    m_NameField.setText(binding.getName());
    m_BindingField.setText(binding.getBinding().toString());
    m_BindingField.setLastPressed(binding.getBinding());
    m_Toggleable.setSelected(binding.isToggleable());
    m_Inverted.setSelected(binding.isInverted());
    m_Interval.setValue(binding.getInterval());
  }
}
