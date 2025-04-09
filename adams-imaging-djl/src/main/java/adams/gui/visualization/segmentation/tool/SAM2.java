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
 * SAM2.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.segmentation.tool;

import adams.gui.chooser.ColorChooserPanel;
import adams.gui.core.BaseComboBox;
import adams.gui.core.GUIHelper;
import adams.gui.core.ImageManager;
import adams.gui.core.KeyUtils;
import adams.gui.core.MouseUtils;
import adams.gui.core.NumberTextField;
import adams.gui.core.NumberTextField.Type;
import adams.gui.core.ParameterPanel;
import adams.gui.visualization.segmentation.ImageUtils;
import ai.djl.inference.Predictor;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.modality.cv.output.DetectedObjects;
import ai.djl.modality.cv.output.DetectedObjects.DetectedObject;
import ai.djl.modality.cv.output.Mask;
import ai.djl.modality.cv.translator.Sam2Translator.Sam2Input;
import ai.djl.modality.cv.translator.Sam2Translator.Sam2Input.Builder;
import ai.djl.modality.cv.translator.Sam2TranslatorFactory;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.training.util.ProgressBar;

import javax.swing.Icon;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

/**
 * Uses SAM (via docker and redis) to aid human in annotating.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class SAM2
  extends AbstractToolWithParameterPanel {

  private static final long serialVersionUID = 8374950649752446530L;

  /** the marker size. */
  protected NumberTextField m_TextMarkerSize;

  /** the marker color. */
  protected ColorChooserPanel m_PanelColor;

  /** the combobox for the model name. */
  protected BaseComboBox<String> m_ComboBoxModelName;

  /** the minimum detection probability. */
  protected NumberTextField m_TextMinProbabilityDetection;

  /** the minimum mask pixel probability. */
  protected NumberTextField m_TextMinProbabilityMask;

  /** the marker size. */
  protected int m_MarkerSize;

  /** the marker color. */
  protected Color m_MarkerColor;

  /** the model to use. */
  protected String m_ModelName;

  /** the minimum probability for the detections. */
  protected double m_MinProbabilityDetection;

  /** the minimum probability for the mask pixels. */
  protected double m_MinProbabilityMask;

  /** the current model. */
  protected transient ZooModel<Sam2Input, DetectedObjects> m_Model;

  /** the current predictor. */
  protected transient Predictor<Sam2Input, DetectedObjects> m_Predictor;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Click on one or more points with the left mouse button and then press ENTER to have a shape detected.\n"
      + "Left-Click while holding CTRL to reset the selected points.\n"
      + "\n"
      + "More information:\n"
      + "https://github.com/facebookresearch/sam2";
  }

  /**
   * The name of the tool.
   *
   * @return the name
   */
  @Override
  public String getName() {
    return "SAM2";
  }

  /**
   * The icon of the tool.
   *
   * @return the icon
   */
  @Override
  public Icon getIcon() {
    return ImageManager.getIcon("sam2.png");
  }

  /**
   * Creates the mouse cursor to use.
   *
   * @return the cursor
   */
  @Override
  protected Cursor createCursor() {
    return Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
  }

  /**
   * Creates the mouse listener to use.
   *
   * @return the listener, null if not applicable
   */
  @Override
  protected ToolMouseAdapter createMouseListener() {
    return new ToolMouseAdapter(this) {
      @Override
      public void mouseClicked(MouseEvent e) {
	if (MouseUtils.isLeftClick(e) && MouseUtils.hasNoModifierKey(e)) {
	  getLayerManager().getMarkers().add(
	    new Point(
	      (int) (e.getX() / getZoom()),
	      (int) (e.getY() / getZoom())));
	  e.consume();
	}
	else if (MouseUtils.isLeftClick(e) && KeyUtils.isCtrlDown(e.getModifiersEx())) {
	  getLayerManager().getMarkers().clear();
	  e.consume();
	}
	super.mouseClicked(e);
      }
    };
  }

  /**
   * Creates the mouse motion listener to use.
   *
   * @return the listener, null if not applicable
   */
  @Override
  protected ToolMouseMotionAdapter createMouseMotionListener() {
    return null;
  }

  /**
   * Creates the key listener to use.
   *
   * @return		the listener, null if not applicable
   */
  @Override
  protected ToolKeyAdapter createKeyListener() {
    return new ToolKeyAdapter(this) {
      @Override
      public void keyPressed(KeyEvent e) {
	if ((e.getKeyCode() == KeyEvent.VK_ENTER)) {
	  if (getLayerManager().getMarkers().size() >= 1) {
	    e.consume();
	    SwingWorker worker = new SwingWorker() {
	      @Override
	      protected Object doInBackground() throws Exception {
		getOwner().getCanvas().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		detect();
		return null;
	      }
	      @Override
	      protected void done() {
		super.done();
		getOwner().getCanvas().setCursor(createCursor());
	      }
	    };
	    worker.execute();
	  }
	}
	if (!e.isConsumed())
	  super.keyPressed(e);
      }
    };
  }

  /**
   * Applies the settings.
   */
  @Override
  protected void doApply() {
    m_MarkerSize              = m_TextMarkerSize.getValue().intValue();
    m_MarkerColor             = m_PanelColor.getCurrent();
    m_ModelName               = m_ComboBoxModelName.getSelectedItem();
    m_MinProbabilityDetection = m_TextMinProbabilityDetection.getValue().doubleValue();
    m_MinProbabilityMask      = m_TextMinProbabilityMask.getValue().floatValue();
    getLayerManager().getMarkers().setExtent(m_MarkerSize);
    getLayerManager().getMarkers().setColor(m_MarkerColor);
  }

  /**
   * Fills the parameter panel with the options.
   *
   * @param paramPanel  for adding the options to
   */
  @Override
  protected void addOptions(ParameterPanel paramPanel) {
    m_TextMarkerSize = new NumberTextField(NumberTextField.Type.INTEGER, 10);
    m_TextMarkerSize.setCheckModel(new NumberTextField.BoundedNumberCheckModel(NumberTextField.Type.INTEGER, 1, null, getLayerManager().getMarkers().getExtent()));
    m_TextMarkerSize.addAnyChangeListener((ChangeEvent e) -> setApplyButtonState(m_ButtonApply, true));
    paramPanel.addParameter("Marker size", m_TextMarkerSize);

    m_PanelColor = new ColorChooserPanel(getLayerManager().getMarkers().getColor());
    paramPanel.addParameter("- color", m_PanelColor);

    m_ComboBoxModelName = new BaseComboBox<>(new String[]{"sam2-hiera-tiny", "sam2-hiera-large"});
    m_ComboBoxModelName.setSelectedIndex(0);
    paramPanel.addParameter("Model", m_ComboBoxModelName);

    m_TextMinProbabilityDetection = new NumberTextField(Type.DOUBLE, 10);
    m_TextMinProbabilityDetection.setCheckModel(new NumberTextField.BoundedNumberCheckModel(Type.DOUBLE, 0.0, 1.0, 0.0));
    m_TextMinProbabilityDetection.addAnyChangeListener((ChangeEvent e) -> setApplyButtonState(m_ButtonApply, true));
    paramPanel.addParameter("Min detection prob", m_TextMinProbabilityDetection);

    m_TextMinProbabilityMask = new NumberTextField(Type.FLOAT, 10);
    m_TextMinProbabilityMask.setCheckModel(new NumberTextField.BoundedNumberCheckModel(Type.FLOAT, 0.0f, 1.0f, 1.0f));
    m_TextMinProbabilityMask.addAnyChangeListener((ChangeEvent e) -> setApplyButtonState(m_ButtonApply, true));
    paramPanel.addParameter("Min mask prob", m_TextMinProbabilityMask);
  }

  /**
   * Loads the model, if necessary.
   *
   * @throws Exception	if model loading fails
   */
  protected void loadModel() throws Exception {
    Criteria<Sam2Input, DetectedObjects> 	criteria;

    if (m_Model != null)
      return;

    criteria = Criteria.builder()
		 .setTypes(Sam2Input.class, DetectedObjects.class)
		 .optModelUrls("djl://ai.djl.pytorch/" + m_ModelName)
		 .optTranslatorFactory(new Sam2TranslatorFactory())
		 .optProgress(new ProgressBar())
		 .build();

    m_Model     = criteria.loadModel();
    m_Predictor = m_Model.newPredictor();
  }

  /**
   * Applies SAM2 to the image and points.
   */
  protected void detect() {
    Image 		image;
    Builder		builder;
    Sam2Input 		input;
    DetectedObjects 	detection;
    int			i;
    DetectedObject	detected;
    Mask		mask;
    BufferedImage	img;
    float[][] 		probs;
    int[]		pixels;
    int			w;
    int			h;
    int			x;
    int			y;
    int			active;
    int black;

    try {
      loadModel();
    }
    catch (Exception e) {
      GUIHelper.showErrorMessage(getCanvas().getOwner(), "Failed to load SAM2 model!", e);
      return;
    }

    image   = ImageFactory.getInstance().fromImage(getLayerManager().getImageLayer().getImage());
    builder = Sam2Input.builder(image);
    for (Point p: getLayerManager().getMarkers().getPoints())
      builder.addPoint((int) p.getX(), (int) p.getY());
    input = builder.build();

    try {
      detection = m_Predictor.predict(input);
      for (i = 0; i < detection.getNumberOfObjects(); i++) {
	detected = detection.item(i);
	if (detected.getProbability() < m_MinProbabilityDetection)
	  continue;
	if (detected.getBoundingBox() instanceof Mask) {
	  mask   = (Mask) detected.getBoundingBox();
	  w      = (int) mask.getBounds().getWidth();
	  h      = (int) mask.getBounds().getHeight();
	  probs  = mask.getProbDist();
	  pixels = new int[w * h];
	  active = getActiveColor().getRGB();
	  black  = new Color(0, 0, 0, 0).getRGB();
	  for (y = 0; y < h; y++) {
	    for (x = 0; x < w; x++) {
	      if (probs[y][x] >= m_MinProbabilityMask)
		pixels[y*w + x] = active;
	      else
		pixels[y*w + x] = black;
	    }
	  }
	  img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
	  img.setRGB(0, 0, w, h, pixels, 0, w);

	  if (isAutomaticUndoEnabled())
	    getCanvas().getOwner().addUndoPoint();
	  if (mask.isFullImageMask())
	    ImageUtils.combineImages(img, getActiveImage());
	  else
	    getLogger().severe("Can only combine full images at this stage!"); // TODO
	}
      }
    }
    catch (Exception e) {
      GUIHelper.showErrorMessage(getCanvas().getOwner(), "Failed to apply SAM2 model to model!", e);
      return;
    }

    getLayerManager().getMarkers().clear();
    getLayerManager().update();
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  @Override
  public void cleanUp() {
    if (m_Model != null) {
      m_Model.close();
      m_Model = null;
    }
    if (m_Predictor != null) {
      m_Predictor.close();
      m_Predictor = null;
    }

    super.cleanUp();
  }
}
