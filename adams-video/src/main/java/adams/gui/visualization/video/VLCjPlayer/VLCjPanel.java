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
 * VideoAnnotatorPanel.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.video.VLCjPlayer;

import adams.core.CleanUpHandler;
import adams.core.logging.LoggingHelper;
import adams.gui.chooser.BaseFileChooser;
import adams.gui.core.*;
import adams.gui.event.RecentItemEvent;
import adams.gui.event.RecentItemListener;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.lang.String;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A basic video player. Allows a user to open, play, and pause video
 *
 * @author sjb90
 * @version $Revision$
 */
public class VLCjPanel
  extends BasePanel
  implements MenuBarProvider, CleanUpHandler {

  private static final long serialVersionUID = -6333350276893620652L;


  /**
   * the file to store the recent files in.
   */
  public final static String SESSION_FILE = "VLCjVideoPlayerSession.props";


  /**
   * the menu bar, if used.
   */
  protected JMenuBar m_MenuBar;


  /**
   * the "load recent" submenu.
   */
  protected JMenu m_MenuFileLoadRecent;

  protected RecentFilesHandler<JMenu> m_RecentFilesHandler;

  /**
   * the menu item "close".
   */
  protected JMenuItem m_MenuItemFileClose;

  /**
   * the menu item "open".
   */
  protected JMenuItem m_MenuItemFileOpen;

  /**
   * the file chooser
   */
  protected BaseFileChooser m_FileChooser;

  /**
   * the menu item "show/hide controls"
   */
  protected JMenuItem m_MenuItemVideoShowHideControls;

  /**
   * the menu item "play"
   */
  protected JMenuItem m_MenuItemVideoPlay;

  /**
   * the menu item "pause"
   */
  protected JMenuItem m_MenuItemVideoPause;


  /**
   * The path to the file we're currently viewing
   * TODO: Change to place holder file
   */
  protected String m_CurrentFile;

  /**
   * the mediaplayer componenet used to play video
   */
  protected EmbeddedMediaPlayerComponent m_MediaPlayerComponent;
  /**
   * the logger used to output information to the adams console
   */
  protected Logger m_Logger;

  /**
   * the panel for our video controls
   */
  protected BasePanel m_ControlsPanel;

  /**
   * the "play" button
   */
  protected JButton m_PlayButton;

  /**
   * the "pause" button
   */
  protected JButton m_PauseButton;

  /**
   * the "stop" button
   */
  protected JButton m_StopButton;

  @Override
  public void initGUI() {
    super.initGUI();
    m_Logger = LoggingHelper.getLogger(getClass());
    m_MediaPlayerComponent = new EmbeddedMediaPlayerComponent();
    add(m_MediaPlayerComponent, BorderLayout.CENTER);

    // Controls
    m_ControlsPanel = new BasePanel(new FlowLayout());
    m_PlayButton  = new JButton("Play");
    m_PauseButton = new JButton("Pause");
    m_StopButton  = new JButton("Stop");
    m_ControlsPanel.add(m_PlayButton);
    m_ControlsPanel.add(m_PauseButton);
    m_ControlsPanel.add(m_StopButton);

    m_PlayButton.addActionListener(e -> m_MediaPlayerComponent.getMediaPlayer().play());
    m_PauseButton.addActionListener(e -> m_MediaPlayerComponent.getMediaPlayer().pause());
    m_StopButton.addActionListener(e -> m_MediaPlayerComponent.getMediaPlayer().stop());
    add(m_ControlsPanel, BorderLayout.SOUTH);

  }

  /**
   * Creates a menu bar (singleton per panel object). Can be used in frames.
   *
   * @return the menu bar
   */
  public JMenuBar getMenuBar() {
    JMenuBar result;
    JMenu menu;
    JMenu submenu;
    JMenuItem menuitem;
    if (m_MenuBar == null) {
      result = new JMenuBar();

      // File
      menu = new JMenu("File");
      result.add(menu);
      menu.setMnemonic('F');
      menu.addChangeListener(e -> updateMenu());


      // File/Open
      menuitem = new JMenuItem("Open...", GUIHelper.getIcon("open.gif"));
      menuitem.setMnemonic('O');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed O"));
      menuitem.addActionListener(e -> {
        m_CurrentFile = open();
        m_MediaPlayerComponent.getMediaPlayer().prepareMedia(m_CurrentFile);
        m_RecentFilesHandler.addRecentItem(new File(m_CurrentFile));
      });
      menu.add(menuitem);
      m_MenuItemFileOpen = menuitem;

      // File/Recent files
      submenu = new JMenu("Open recent");
      menu.add(submenu);
      m_RecentFilesHandler = new RecentFilesHandler<>(
        SESSION_FILE, 5, submenu);
      m_RecentFilesHandler.addRecentItemListener(new RecentItemListener<JMenu,File>() {
        @Override
        public void recentItemAdded(RecentItemEvent<JMenu,File> e) {
          // ignored
        }
        @Override
        public void recentItemSelected(RecentItemEvent<JMenu,File> e) {
          m_CurrentFile = e.getItem().getAbsolutePath();
          m_MediaPlayerComponent.getMediaPlayer().prepareMedia(m_CurrentFile);

        }
      });
      m_MenuFileLoadRecent = submenu;

      // File/Close
      menuitem = new JMenuItem("Close", GUIHelper.getIcon("exit.png"));
      menuitem.setMnemonic('C');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed Q"));
      menuitem.addActionListener(e -> close());
      m_MenuItemFileClose = menuitem;
      menu.add(menuitem);

      // Video
      menu = new JMenu("Video");
      result.add(menu);
      menu.setMnemonic('V');
      menu.addChangeListener(e -> updateMenu());

      // Video/Play
      menuitem = new JMenuItem("Play", GUIHelper.getIcon("run.gif"));
      menuitem.setMnemonic('P');
      menuitem.setAccelerator((GUIHelper.getKeyStroke("ctrl pressed P")));
      menuitem.addActionListener(e -> m_MediaPlayerComponent.getMediaPlayer().play());
      menuitem.setEnabled(false);
      m_MenuItemVideoPlay = menuitem;
      menu.add(menuitem);

      // Video/Pause
      menuitem = new JMenuItem("Pause", GUIHelper.getIcon("pause.gif"));
      menuitem.setMnemonic('U');
      menuitem.setAccelerator((GUIHelper.getKeyStroke("ctrl pressed U")));
      menuitem.addActionListener(e -> m_MediaPlayerComponent.getMediaPlayer().pause());
      menuitem.setEnabled(false);
      m_MenuItemVideoPause = menuitem;
      menu.add(menuitem);

      //Video/Show/Hide Controls
      menuitem = new JMenuItem(("Hide Controls"));
      menuitem.setMnemonic('H');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed H"));
      menuitem.addActionListener(e -> showHideControls());
      m_MenuItemVideoShowHideControls = menuitem;
      menu.add(menuitem);

      m_MenuBar = result;
    } else {
      result = m_MenuBar;
    }
    return result;
  }

  /**
   * Pops up dialog to open a file.
   *
   * @return the path to the selected file.
   */
  public String open() {
    int retVal;

    retVal = getFileChooser().showOpenDialog(this);
    if (retVal != BaseFileChooser.APPROVE_OPTION)
      return null;

    return getFileChooser().getSelectedFile().getAbsolutePath();
  }

  public BaseFileChooser getFileChooser() {
    if (m_FileChooser == null)
      m_FileChooser = new BaseFileChooser();

    return m_FileChooser;
  }

  /**
   * Closes the dialog, if possible.
   */
  protected void close() {
    if (getParentDialog() != null)
      getParentDialog().setVisible(false);
    else if (getParentFrame() != null)
      getParentFrame().setVisible(false);
    cleanUp();
    closeParent();
  }


  @Override
  public void cleanUp() {
    m_MediaPlayerComponent.release();
  }

  /**
   * Updates the state of the menu items.
   */
  protected void updateMenu() {
    System.out.println("Entering update!");
    Runnable	run;

    if (m_MenuBar == null)
      return;

    run = () -> {
      System.out.println("Updating menu");
      // Video
      if(m_MediaPlayerComponent.getMediaPlayer().isPlayable()){
        System.out.println("enabling things");
        m_MenuItemVideoPlay.setEnabled(true);
        m_MenuItemVideoPause.setEnabled(true);
      }
      else {
        System.out.println("Disabling things");
        m_MenuItemVideoPlay.setEnabled(false);
        m_MenuItemVideoPause.setEnabled(false);
      }
    };
    SwingUtilities.invokeLater(run);
  }

  /**
   * Safely hides or shows the control panel
   */
  private void showHideControls() {
    Runnable run;
    if (m_ControlsPanel == null)
      return;

    run = () -> m_ControlsPanel.setVisible(!m_ControlsPanel.isVisible());

    SwingUtilities.invokeLater(run);
  }
}
