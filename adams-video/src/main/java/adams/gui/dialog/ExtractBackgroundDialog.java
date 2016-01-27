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
 * ExtractBackgroundDialog.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.dialog;

import adams.core.io.PlaceholderFile;
import adams.data.image.BufferedImageContainer;
import adams.data.image.multiimageoperation.Median;
import adams.flow.transformer.movieimagesampler.AbstractBufferedImageMovieImageSampler;
import adams.flow.transformer.movieimagesampler.FixedIntervalBufferedImageSampler;
import adams.gui.goe.GenericObjectEditorPanel;
import adams.gui.visualization.image.ImagePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Extracts the background from a video and stores it as an image
 *
 * @author sjb90
 * @version $Revision$
 */
public class ExtractBackgroundDialog extends ApprovalDialog {

  /** the sampler we're going to use */
  AbstractBufferedImageMovieImageSampler m_ImageSampler;

  /** preview button */
  JButton m_ButtonPreview;

  /** Image Panel for displaying the preview */
  ImagePanel m_ImagePanel;

  /** editor panel */
  GenericObjectEditorPanel m_EditorPanel;

  /** current file */
  PlaceholderFile m_CurrentFile;

  /** the background image extracted */
  java.awt.image.BufferedImage m_Background;

  /**
   * Creates a modeless dialog without a title with the specified Dialog as
   * its owner.
   *
   * @param owner the owning dialog
   */
  public ExtractBackgroundDialog(Dialog owner) {
    super(owner);
  }

  /**
   * Creates a dialog with the specified owner Dialog and modality.
   *
   * @param owner    the owning dialog
   * @param modality the type of modality
   */
  public ExtractBackgroundDialog(Dialog owner, ModalityType modality) {
    super(owner, modality);
  }

  /**
   * Creates a modeless dialog with the specified title and with the specified
   * owner dialog.
   *
   * @param owner the owning dialog
   * @param title the title of the dialog
   */
  public ExtractBackgroundDialog(Dialog owner, String title) {
    super(owner, title);
  }

  /**
   * Creates a dialog with the specified title, modality and the specified
   * owner Dialog.
   *
   * @param owner    the owning dialog
   * @param title    the title of the dialog
   * @param modality the type of modality
   */
  public ExtractBackgroundDialog(Dialog owner, String title, ModalityType modality) {
    super(owner, title, modality);
  }

  /**
   * Creates a modeless dialog without a title with the specified Frame as
   * its owner.
   *
   * @param owner the owning frame
   */
  public ExtractBackgroundDialog(Frame owner) {
    super(owner);
  }

  /**
   * Creates a dialog with the specified owner Frame, modality and an empty
   * title.
   *
   * @param owner the owning frame
   * @param modal whether the dialog is modal or not
   */
  public ExtractBackgroundDialog(Frame owner, boolean modal) {
    super(owner, modal);
  }

  /**
   * Creates a modeless dialog with the specified title and with the specified
   * owner frame.
   *
   * @param owner the owning frame
   * @param title the title of the dialog
   */
  public ExtractBackgroundDialog(Frame owner, String title) {
    super(owner, title);
  }

  /**
   * Creates a dialog with the specified owner Frame, modality and title.
   *
   * @param owner the owning frame
   * @param title the title of the dialog
   * @param modal whether the dialog is modal or not
   */
  public ExtractBackgroundDialog(Frame owner, String title, boolean modal) {
    super(owner, title, modal);
  }

  public void setCurrentFile(PlaceholderFile file) {
    m_CurrentFile = file;
  }
  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    JPanel  panel;
    JButton button;
    super.initGUI();

    panel = new GenericObjectEditorPanel(AbstractBufferedImageMovieImageSampler.class,
      new FixedIntervalBufferedImageSampler(),true);
    add(panel, BorderLayout.NORTH);
    m_EditorPanel = (GenericObjectEditorPanel)panel;
    m_ImageSampler = (AbstractBufferedImageMovieImageSampler)m_EditorPanel.getCurrent();

    panel = new ImagePanel();
    add(panel, BorderLayout.CENTER);
    m_ImagePanel = (ImagePanel) panel;

    button = new JButton("Preview");
    getButtonsPanel().add(button);
    button.addActionListener( e -> {
      extractBackground();
      m_ImagePanel.setCurrentImage(m_Background);
      setSize(m_Background.getWidth(), m_Background.getHeight());
    });

    getApproveButton().addActionListener(e -> extractBackground());
  }

  /**
   * Extracts the background from a given video
   */
  protected void extractBackground() {
    BufferedImageContainer[] imageContainers = m_ImageSampler.sample(m_CurrentFile);
    BufferedImageContainer bufferedImageContainer = new Median().process(imageContainers)[0];
    m_Background = bufferedImageContainer.getImage();
  }

  /**
   * a getter for the background image
   * @return the calculated background fro the current video
   */
  public BufferedImage getBackgroundImage() {
    return m_Background;
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
  }
}
