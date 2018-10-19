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
 * MongoDbConnectionPanel.java
 * Copyright (C) 2018 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.dialog;

import adams.core.ClassLister;
import adams.core.Constants;
import adams.core.StatusMessageHandler;
import adams.core.logging.LoggingLevel;
import adams.db.MongoDbConnection;
import adams.db.MongoDbConnectionParameters;
import adams.gui.core.BaseButton;
import adams.gui.core.BaseCheckBox;
import adams.gui.core.BaseComboBox;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseTextField;
import adams.gui.core.GUIHelper;
import adams.gui.core.ParameterPanel;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.List;


/**
 * A panel for connecting to a MongoDB.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class MongoDbConnectionPanel
  extends BasePanel
  implements StatusMessageHandler, Comparable<MongoDbConnectionPanel> {

  /** for serialization. */
  private static final long serialVersionUID = -8207475445903090661L;

  /** the panel itself. */
  protected MongoDbConnectionPanel m_Self;

  /** the combobox with the available connections. */
  protected BaseComboBox m_ComboBoxConnections;

  /** the edit field for the database URL. */
  protected BaseTextField m_TextURL;

  /** the edit field for the database user. */
  protected BaseTextField m_TextUser;

  /** the edit field for the database password. */
  protected JPasswordField m_TextPassword;

  /** the checkbox for showing the password. */
  protected BaseCheckBox m_CheckBoxShowPassword;

  /** the edit field for the authentication DB. */
  protected BaseTextField m_TextAuthDB;

  /** the combobox for the logging level. */
  protected BaseComboBox m_ComboBoxLoggingLevel;

  /** the checkbox for connecting on startup. */
  protected BaseCheckBox m_CheckBoxConnectOnStartUp;

  /** the button for making a connection the default one. */
  protected BaseButton m_ButtonMakeDefault;

  /** the button connecting/disconnecting the database. */
  protected BaseButton m_ButtonConnect;

  /** the label for status messages. */
  protected JLabel m_LabelStatus;

  /** for the parameters. */
  protected ParameterPanel m_PanelParameters;

  /** the change listeners. */
  protected HashSet<ChangeListener> m_ChangeListeners;

  /**
   * For initializing members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Self            = this;
    m_ChangeListeners = new HashSet<>();
  }

  /**
   * Returns the default database connection to use.
   *
   * @return		the database connection
   */
  protected MongoDbConnection getDefaultDatabaseConnection() {
    return MongoDbConnection.getSingleton();
  }

  /**
   * Initializes the GUI.
   */
  @Override
  protected void initGUI() {
    JPanel	panel;
    JPanel	panel2;

    super.initGUI();

    setLayout(new BorderLayout());

    m_PanelParameters = new ParameterPanel();
    add(m_PanelParameters, BorderLayout.NORTH);

    m_ComboBoxConnections = new BaseComboBox(getDatabaseConnection().getConnections().toArray());
    m_ComboBoxConnections.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	if (m_ComboBoxConnections.getSelectedIndex() == -1)
	  return;
	displayParameters((MongoDbConnectionParameters) m_ComboBoxConnections.getSelectedItem());
      }
    });
    m_PanelParameters.addParameter("_Connections", m_ComboBoxConnections);

    m_TextURL = new BaseTextField(20);
    m_PanelParameters.addParameter("_URL", m_TextURL);

    m_TextUser = new BaseTextField(20);
    m_PanelParameters.addParameter("U_ser", m_TextUser);

    m_TextPassword = new JPasswordField(20);
    m_TextPassword.setEchoChar(Constants.PASSWORD_CHAR);
    m_PanelParameters.addParameter("_Password", m_TextPassword);

    m_CheckBoxShowPassword = new BaseCheckBox();
    m_CheckBoxShowPassword.setSelected(false);
    m_CheckBoxShowPassword.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	if (m_CheckBoxShowPassword.isSelected())
	  m_TextPassword.setEchoChar((char) 0);
	else
	  m_TextPassword.setEchoChar(Constants.PASSWORD_CHAR);
      }
    });
    m_PanelParameters.addParameter("Sho_w password", m_CheckBoxShowPassword);

    m_TextAuthDB = new BaseTextField(20);
    m_PanelParameters.addParameter("Auth DB", m_TextAuthDB);

    m_ComboBoxLoggingLevel = new BaseComboBox(LoggingLevel.values());
    m_PanelParameters.addParameter("_Logging level", m_ComboBoxLoggingLevel);

    m_CheckBoxConnectOnStartUp = new BaseCheckBox();
    m_CheckBoxConnectOnStartUp.setSelected(false);
    m_PanelParameters.addParameter("Co_nnect on startup", m_CheckBoxConnectOnStartUp);

    panel2 = new JPanel(new BorderLayout());
    add(panel2, BorderLayout.SOUTH);

    // status
    m_LabelStatus = new JLabel();
    panel         = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panel.add(m_LabelStatus);
    panel2.add(panel, BorderLayout.WEST);

    // buttons
    panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

    m_ButtonMakeDefault = new BaseButton("Make default");
    panel.add(m_ButtonMakeDefault);
    m_ButtonMakeDefault.setMnemonic('m');
    m_ButtonMakeDefault.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	m_ButtonMakeDefault.setEnabled(false);
	makeDefault();
	m_ButtonMakeDefault.setEnabled(true);
	update();
      }
    });

    m_ButtonConnect = new BaseButton("Connect");
    panel.add(m_ButtonConnect);
    m_ButtonConnect.setMnemonic('C');
    m_ButtonConnect.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	doReconnect();
      }
    });
    panel2.add(panel, BorderLayout.EAST);
  }

  /**
   * Finishes the initialization.
   */
  @Override
  protected void finishInit() {
    super.finishInit();

    update();
  }

  /**
   * The title of the connection panel.
   *
   * @return		the title
   */
  protected String getTitle() {
    return "MongoDB";
  }

  /**
   * Displays the parameters.
   *
   * @param conn	the parameters to display
   */
  protected void displayParameters(MongoDbConnectionParameters conn) {
    m_TextURL.setText(conn.getURL());
    m_TextUser.setText(conn.getUser());
    m_TextPassword.setText(conn.getPassword().getValue());
    m_TextAuthDB.setText(conn.getAuthDB());
    m_ComboBoxLoggingLevel.setSelectedItem(conn.getLoggingLevel());
    m_CheckBoxConnectOnStartUp.setSelected(conn.getConnectOnStartUp());
  }

  /**
   * Displays the parameters.
   *
   * @param conn	the database connection to display
   */
  protected void displayParameters(MongoDbConnection conn) {
    boolean 				connected;
    List<MongoDbConnectionParameters> 	connections;
    MongoDbConnectionParameters 	current;
    int 				index;

    displayParameters(conn.getCurrentConnection());

    connected   = conn.isConnected();
    connections = conn.getConnections();
    current     = conn.getCurrentConnection();
    index       = connections.indexOf(current);
    m_ComboBoxConnections.setModel(new DefaultComboBoxModel(connections.toArray()));
    m_ComboBoxConnections.setSelectedIndex(index);

    m_ComboBoxConnections.setEnabled(!connected);
    m_TextURL.setEditable(!connected);
    m_TextUser.setEditable(!connected);
    m_TextPassword.setEditable(!connected);
    m_CheckBoxShowPassword.setEnabled(!connected);
    m_TextAuthDB.setEditable(!connected);
    m_ComboBoxLoggingLevel.setEnabled(!connected);
    m_CheckBoxConnectOnStartUp.setEnabled(!connected);

    m_ButtonMakeDefault.setEnabled(!getCurrentParameters().equals(conn.getDefaultConnection()));

    if (connected)
      m_ButtonConnect.setText("Disconnect");
    else
      m_ButtonConnect.setText("Connect");
  }

  /**
   * Performs the reconnection.
   */
  protected void doReconnect() {
    boolean		result;
    MongoDbConnection	conn;

    if (getDatabaseConnection().isConnected()) {
      getDatabaseConnection().disconnect();
    }
    else {
      try {
	conn = getCurrentParameters().toDatabaseConnection(MongoDbConnection.class);
	result = conn.connect();
	if (!result) {
	  GUIHelper.showErrorMessage(this, "Failed to connect!");
	}
	else {
	  // add connection
	  conn.addConnection(conn.getCurrentConnection());
	  // set as default (for session)
	  if (conn.getOwner() != null)
	    conn.getOwner().setDefault(conn);
	}
      }
      catch (Exception e) {
	GUIHelper.showErrorMessage(this, "Failed to connect!", e);
      }
    }

    notifyChangeListeners();
    showStatus("");
    update();
  }

  /**
   * Makes the current parameters the default.
   */
  protected void makeDefault() {
    if (!getDatabaseConnection().makeDefaultConnection(getCurrentParameters()))
      GUIHelper.showErrorMessage(m_Self, "Failed to make current connection the default one!");
    notifyChangeListeners();
    showStatus("");
    update();
  }

  /**
   * Returns a new instance of a ConnectionParameters object.
   *
   * @return		the empty parameters object
   */
  protected MongoDbConnectionParameters newConnectionParameters() {
    return new MongoDbConnectionParameters();
  }

  /**
   * Returns the current parameters as connection object.
   *
   * @return		the current setup
   */
  protected MongoDbConnectionParameters getCurrentParameters() {
    MongoDbConnectionParameters 	result;

    result = newConnectionParameters();
    result.setParameter(MongoDbConnectionParameters.PARAM_URL,              m_TextURL.getText());
    result.setParameter(MongoDbConnectionParameters.PARAM_USER,             m_TextUser.getText());
    result.setParameter(MongoDbConnectionParameters.PARAM_PASSWORD,         m_TextPassword.getText());
    result.setParameter(MongoDbConnectionParameters.PARAM_AUTHDB,           m_TextAuthDB.getText());
    result.setParameter(MongoDbConnectionParameters.PARAM_LOGGINGLEVEL,     m_ComboBoxLoggingLevel.getSelectedItem().toString());
    result.setParameter(MongoDbConnectionParameters.PARAM_CONNECTONSTARTUP, "" + m_CheckBoxConnectOnStartUp.isSelected());

    return result;
  }

  /**
   * Returns the currently used database connection object, can be null.
   *
   * @return		the current object
   */
  public MongoDbConnection getDatabaseConnection() {
    return MongoDbConnection.getSingleton();
  }

  /**
   * updates the enabled state content etc. of all the GUI elements, based on
   * the DatabaseConnection object of the scripting engine.
   */
  public void update() {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
	displayParameters(getDatabaseConnection());
      }
    });
  }

  /**
   * Sets the enabled state of the parameters.
   *
   * @param b		if true then the parameters will be enabled
   */
  protected void setEnabledState(boolean b) {
    m_ComboBoxConnections.setEnabled(b);
    m_TextURL.setEnabled(b);
    m_TextUser.setEnabled(b);
    m_TextPassword.setEnabled(b);
    m_CheckBoxShowPassword.setEnabled(b);
    m_TextAuthDB.setEnabled(b);
    m_ComboBoxLoggingLevel.setEnabled(b);
    m_CheckBoxConnectOnStartUp.setEnabled(b);
    m_ButtonMakeDefault.setEnabled(b);
    m_ButtonConnect.setEnabled(b);
  }

  /**
   * Sets the enabled state of the panel.
   *
   * @param b		if true then the panel will be enabled
   */
  @Override
  public void setEnabled(boolean b) {
    super.setEnabled(b);
    setEnabledState(b);
    update();
  }

  /**
   * Displays a message.
   *
   * @param msg		the message to display
   */
  public void showStatus(final String msg) {
    Runnable	run;

    run = new Runnable() {
      public void run() {
	m_LabelStatus.setText(msg);
      }
    };
    SwingUtilities.invokeLater(run);
  }

  /**
   * Compares this object with the specified object for order.  Returns a
   * negative integer, zero, or a positive integer as this object is less
   * than, equal to, or greater than the specified object.
   *
   * Merely uses the title of the panels for comparison.
   *
   * @param   o the object to be compared.
   * @return  a negative integer, zero, or a positive integer as this object
   *		is less than, equal to, or greater than the specified object.
   *
   * @throws ClassCastException if the specified object's type prevents it
   *         from being compared to this object.
   */
  public int compareTo(MongoDbConnectionPanel o) {
    return getTitle().compareTo(o.getTitle());
  }

  /**
   * Checks whether this object is equal to the specified one.
   *
   * @param o		the object to compare with
   * @return		true if the same (title)
   */
  @Override
  public boolean equals(Object o) {
    if (o instanceof MongoDbConnectionPanel)
      return (compareTo((MongoDbConnectionPanel) o) == 0);
    else
      return false;
  }

  /**
   * Adds the listener for changes in the connection.
   *
   * @param l		the listener to add
   */
  public void addChangeListener(ChangeListener l) {
    m_ChangeListeners.add(l);
  }

  /**
   * Removes the listener for changes in the connection.
   *
   * @param l		the listener to remove
   */
  public void removeChangeListener(ChangeListener l) {
    m_ChangeListeners.remove(l);
  }

  /**
   * Notifies all listeners about a change in the connection.
   */
  protected void notifyChangeListeners() {
    ChangeEvent	event;

    event = new ChangeEvent(this);
    for (ChangeListener l: m_ChangeListeners)
      l.stateChanged(event);
  }

  /**
   * Returns a list with classnames of panels.
   *
   * @return		the panel classnames
   */
  public static String[] getPanels() {
    return ClassLister.getSingleton().getClassnames(MongoDbConnectionPanel.class);
  }
}
