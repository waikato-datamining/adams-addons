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

import adams.data.opencv.ContoursHelper;
import adams.data.sam2.SAM2Utils;
import adams.gui.chooser.ColorChooserPanel;
import adams.gui.core.BaseComboBox;
import adams.gui.core.GUIHelper;
import adams.gui.core.ImageManager;
import adams.gui.core.KeyUtils;
import adams.gui.core.MouseUtils;
import adams.gui.core.NumberTextField;
import adams.gui.core.NumberTextField.Type;
import adams.gui.core.ParameterPanel;
import ai.djl.inference.Predictor;
import ai.djl.modality.cv.output.DetectedObjects;
import ai.djl.modality.cv.translator.Sam2Translator.Sam2Input;
import ai.djl.repository.zoo.ZooModel;
import com.github.fracpete.javautils.struct.Struct2;
import org.bytedeco.opencv.opencv_core.MatVector;

import javax.swing.Icon;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.List;

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

  /** the minimum object size (width/height). */
  protected NumberTextField m_TextMinObjectSize;

  /** the maximum object size (width/height). */
  protected NumberTextField m_TextMaxObjectSize;

  /** the marker size. */
  protected int m_MarkerSize;

  /** the marker color. */
  protected Color m_MarkerColor;

  /** the model to use. */
  protected String m_ModelName;

  /** the minimum probability for the detections. */
  protected double m_MinProbabilityDetection;

  /** the minimum probability for the mask pixels. */
  protected float m_MinProbabilityMask;

  /** the minimum object size (width/height). */
  protected int m_MinObjectSize;

  /** the maximum object size (width/height). */
  protected int m_MaxObjectSize;

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
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_ModelName               = SAM2Utils.MODEL_NAMES[0];
    m_MinProbabilityDetection = 0.0;
    m_MinProbabilityMask      = 1.0f;
    m_MinObjectSize           = -1;
    m_MaxObjectSize           = -1;
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
    if (m_ModelName == null)
      return Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
    else
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
   * Checks the parameters before applying them.
   *
   * @return		null if checks passed, otherwise error message (gets displayed in GUI)
   */
  @Override
  protected String checkBeforeApply() {
    String	result;
    int		min;
    int		max;

    result = super.checkBeforeApply();

    if (result == null) {
      min = m_TextMinObjectSize.getValue().intValue();
      max = m_TextMaxObjectSize.getValue().intValue();
      if ((min > 0) && (max > 0)) {
	if (max <= min)
	  result = "Maximum object size must be larger than minimum size, but: min=" + min + " and max=" + max;
      }
    }

    return result;
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
    m_MinObjectSize           = m_TextMinObjectSize.getValue().intValue();
    m_MaxObjectSize           = m_TextMaxObjectSize.getValue().intValue();
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
    m_TextMarkerSize.setToolTipText("The size of markers in pixel when selecting prompt points");
    paramPanel.addParameter("Marker size", m_TextMarkerSize);

    m_PanelColor = new ColorChooserPanel(getLayerManager().getMarkers().getColor());
    m_PanelColor.setToolTipText("The color to use for the markers");
    paramPanel.addParameter("- color", m_PanelColor);

    m_ComboBoxModelName = new BaseComboBox<>(SAM2Utils.MODEL_NAMES);
    m_ComboBoxModelName.setSelectedItem(m_ModelName);
    m_ComboBoxModelName.setToolTipText("The model to use for generating the masks");
    paramPanel.addParameter("Model", m_ComboBoxModelName);

    m_TextMinProbabilityDetection = new NumberTextField(Type.DOUBLE, 10);
    m_TextMinProbabilityDetection.setCheckModel(new NumberTextField.BoundedNumberCheckModel(Type.DOUBLE, 0.0, 1.0, m_MinProbabilityDetection));
    m_TextMinProbabilityDetection.addAnyChangeListener((ChangeEvent e) -> setApplyButtonState(m_ButtonApply, true));
    m_TextMinProbabilityDetection.setToolTipText("The minimum probability that detections must have to be considered; ignored if <=0");
    paramPanel.addParameter("Min detection prob", m_TextMinProbabilityDetection);

    m_TextMinProbabilityMask = new NumberTextField(Type.FLOAT, 10);
    m_TextMinProbabilityMask.setCheckModel(new NumberTextField.BoundedNumberCheckModel(Type.FLOAT, 0.0f, 1.0f, m_MinProbabilityMask));
    m_TextMinProbabilityMask.addAnyChangeListener((ChangeEvent e) -> setApplyButtonState(m_ButtonApply, true));
    m_TextMinProbabilityMask.setToolTipText("The minimum probability that mask pixels must have to be included; ignored if <=0");
    paramPanel.addParameter("Min mask prob", m_TextMinProbabilityMask);

    m_TextMinObjectSize = new NumberTextField(NumberTextField.Type.INTEGER, 10);
    m_TextMinObjectSize.setCheckModel(new NumberTextField.BoundedNumberCheckModel(NumberTextField.Type.INTEGER, -1, null, m_MinObjectSize));
    m_TextMinObjectSize.addAnyChangeListener((ChangeEvent e) -> setApplyButtonState(m_ButtonApply, true));
    m_TextMinObjectSize.setToolTipText("The minimum object size; fulfilled if either width or height at least this amount; ignored if <1");
    paramPanel.addParameter("Min. object size", m_TextMinObjectSize);

    m_TextMaxObjectSize = new NumberTextField(NumberTextField.Type.INTEGER, 10);
    m_TextMaxObjectSize.setCheckModel(new NumberTextField.BoundedNumberCheckModel(NumberTextField.Type.INTEGER, -1, null, m_MaxObjectSize));
    m_TextMaxObjectSize.addAnyChangeListener((ChangeEvent e) -> setApplyButtonState(m_ButtonApply, true));
    m_TextMaxObjectSize.setToolTipText("The maximum object size; fulfilled if either width or height at most this amount; ignored if <1");
    paramPanel.addParameter("Max. object size", m_TextMaxObjectSize);
  }

  /**
   * Loads the model, if necessary.
   *
   * @throws Exception	if model loading fails
   */
  protected void loadModel() throws Exception {
    Struct2<ZooModel<Sam2Input, DetectedObjects>, Predictor<Sam2Input, DetectedObjects>> data;

    if (m_Model != null)
      return;

    data        = SAM2Utils.loadModel(m_ModelName);
    m_Model     = data.value1;
    m_Predictor = data.value2;
  }

  /**
   * Applies SAM2 to the image and points.
   */
  protected void detect() {
    DetectedObjects 	detection;
    BufferedImage	img;
    List<float[][]> 	probDists;
    MatVector 		contours;
    List<Polygon>	polys;
    Graphics2D		g2d;

    if (m_ModelName == null) {
      GUIHelper.showErrorMessage(getCanvas().getOwner(), "Please apply options first!");
      return;
    }

    try {
      loadModel();
    }
    catch (Exception e) {
      GUIHelper.showErrorMessage(getCanvas().getOwner(), "Failed to load SAM2 model!", e);
      return;
    }

    try {
      detection = SAM2Utils.detectObjects(m_Predictor, getLayerManager().getImageLayer().getImage(), getLayerManager().getMarkers().getPoints());
      probDists = SAM2Utils.probabilityDistributions(detection, m_MinProbabilityDetection, m_MinProbabilityMask);
      img       = SAM2Utils.combineProbabilityDistributions(probDists, Color.WHITE);
      if (img == null) {
	GUIHelper.showErrorMessage(getCanvas().getOwner(), "Failed to generate image from SAM2 detections!");
	return;
      }
      contours = ContoursHelper.findContours(img, 127, false);
      polys    = ContoursHelper.contoursToPolygons(contours, m_MinObjectSize, m_MaxObjectSize);
      contours.close();
    }
    catch (Exception e) {
      GUIHelper.showErrorMessage(getCanvas().getOwner(), "Failed to apply SAM2 model to image!", e);
      return;
    }

    if (isAutomaticUndoEnabled())
      getCanvas().getOwner().addUndoPoint();

    img = getActiveImage();
    g2d = img.createGraphics();
    g2d.setColor(getActiveColor());
    for (Polygon poly: polys)
      g2d.fillPolygon(poly);
    g2d.dispose();

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
