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

package adams.gui.visualization.annotator;

import adams.core.io.PlaceholderFile;
import adams.data.image.BufferedImageContainer;
import adams.data.image.multiimageoperation.AbstractBufferedImageMultiImageOperation;
import adams.data.image.multiimageoperation.Median;
import adams.flow.transformer.movieimagesampler.AbstractBufferedImageMovieImageSampler;
import adams.flow.transformer.movieimagesampler.AbstractMovieImageSampler;
import adams.flow.transformer.movieimagesampler.FixedIntervalBufferedImageSampler;
import adams.gui.core.BaseButton;
import adams.gui.core.MouseUtils;
import adams.gui.core.ParameterPanel;
import adams.gui.dialog.ApprovalDialog;
import adams.gui.goe.GenericObjectEditorPanel;
import adams.gui.visualization.image.ImagePanel;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.image.BufferedImage;

/**
 * Extracts the background from a video and stores it as an image
 *
 * @author sjb90
 * @version $Revision$
 */
public class ExtractBackgroundDialog extends ApprovalDialog {

  private static final long serialVersionUID = 9156143578421221640L;

  /** the sampler we're going to use */
  protected AbstractBufferedImageMovieImageSampler m_ImageSampler;

  /** the multi image operation we're going to use */
  protected AbstractBufferedImageMultiImageOperation m_ImageOperation;

  /** preview button */
  protected BaseButton m_ButtonPreview;

  /** Image Panel for displaying the preview */
  protected ImagePanel m_ImagePanel;

  /** editor panel */
  protected GenericObjectEditorPanel m_ImageSamplerSelectionPanel;

  /** image operation selection panel */
  protected GenericObjectEditorPanel m_MultiImageOperationSelectionPanel;
  /** current file */
  protected PlaceholderFile m_CurrentFile;

  /** the background image extracted */
  protected BufferedImage m_Background;

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

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    ParameterPanel ppanel;
    JPanel  panel;

    super.initGUI();

    setTitle("Extract background");

    ppanel = new ParameterPanel();

    panel = new GenericObjectEditorPanel(AbstractMovieImageSampler.class,
      new FixedIntervalBufferedImageSampler(),true);
    ppanel.addParameter("Image Sampler", panel);
    m_ImageSamplerSelectionPanel = (GenericObjectEditorPanel)panel;
    m_ImageSampler = (AbstractBufferedImageMovieImageSampler) m_ImageSamplerSelectionPanel.getCurrent();
    m_ImageSamplerSelectionPanel.addChangeListener(e -> m_ImageSampler =
      (AbstractBufferedImageMovieImageSampler) m_ImageSamplerSelectionPanel.getCurrent());

    panel = new GenericObjectEditorPanel(AbstractBufferedImageMultiImageOperation.class, new Median(), true);
    ppanel.addParameter("Image Operation", panel);
    m_MultiImageOperationSelectionPanel = (GenericObjectEditorPanel)panel;
    m_ImageOperation = (AbstractBufferedImageMultiImageOperation)m_MultiImageOperationSelectionPanel.getCurrent();
    m_MultiImageOperationSelectionPanel.addChangeListener(e -> m_ImageOperation =
      (AbstractBufferedImageMultiImageOperation)m_MultiImageOperationSelectionPanel.getCurrent());
    add(ppanel, BorderLayout.NORTH);
    m_ImagePanel = new ImagePanel();
    add(m_ImagePanel, BorderLayout.CENTER);

    m_ButtonPreview = new BaseButton("Preview");
    getButtonsPanel(false).add(m_ButtonPreview);
    getButtonsPanel(false).setComponentZOrder(m_ButtonPreview,0);
    m_ButtonPreview.addActionListener( e -> {
      extractBackground();
    });
  }

  /**
   * The current video file.
   *
   * @param file	the file
   */
  public void setCurrentFile(PlaceholderFile file) {
    m_CurrentFile = file;
    m_Background  = null;
    updateButtons();
  }

  /**
   * Returns the current video file.
   *
   * @return		the file
   */
  public PlaceholderFile getCurrentFile() {
    return m_CurrentFile;
  }

  /**
   * Extracts the background from a given video
   */
  protected void extractBackground() {
    SwingWorker 	worker;

    worker = new SwingWorker() {
      @Override
      protected Object doInBackground() throws Exception {
	MouseUtils.setWaitCursor(ExtractBackgroundDialog.this);
	BufferedImageContainer[] imageContainers = m_ImageSampler.sample(m_CurrentFile);
	BufferedImageContainer bufferedImageContainer = m_ImageOperation.process(imageContainers)[0];
	m_Background = bufferedImageContainer.getImage();
	m_ImagePanel.setCurrentImage(m_Background);
	SwingUtilities.invokeLater(() -> m_ImagePanel.setScale(-1));
	return null;
      }

      @Override
      protected void done() {
	super.done();
        MouseUtils.setDefaultCursor(ExtractBackgroundDialog.this);
	updateButtons();
      }
    };
    worker.execute();
  }

  /**
   * a getter for the background image
   * @return the calculated background fro the current video
   */
  public BufferedImage getBackgroundImage() {
    return m_Background;
  }

  /**
   * Updates the state of the buttons.
   */
  protected void updateButtons() {
    m_ButtonApprove.setEnabled(m_Background != null);
  }
}
