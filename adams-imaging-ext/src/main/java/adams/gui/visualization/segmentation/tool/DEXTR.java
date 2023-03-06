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
 * DEXTR.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.segmentation.tool;

import adams.core.MessageCollection;
import adams.data.image.BufferedImageHelper;
import adams.data.redis.RedisDataType;
import adams.data.statistics.StatUtils;
import adams.gui.chooser.ColorChooserPanel;
import adams.gui.core.ImageManager;
import adams.gui.core.KeyUtils;
import adams.gui.core.MouseUtils;
import adams.gui.core.NumberTextField;
import adams.gui.core.ParameterPanel;
import adams.gui.visualization.segmentation.ImageUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.swing.Icon;
import javax.swing.event.ChangeEvent;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Base64;

/**
 * Uses DEXTR (via docker and redis) to aid human in annotating.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class DEXTR
  extends AbstractRedisTool<String,String> {

  private static final long serialVersionUID = 8374950649752446530L;

  /** the marker size. */
  protected NumberTextField m_TextMarkerSize;

  /** the marker color. */
  protected ColorChooserPanel m_PanelColor;

  /** the marker size. */
  protected int m_MarkerSize;

  /** the marker color. */
  protected Color m_MarkerColor;

  /** the cached base image as base64 JPEG. */
  protected transient String m_BaseImageBase64;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "After connecting to the Redis server, click on four extreme points "
      + "with the left mouse button and then press ENTER to have a shape detected.\n"
      + "Left-Click while holding CTRL to reset the selected points.\n"
      + "Communicates with a DEXTR docker container suing Redis.\n"
      + "\n"
      + "More information:\n"
      + "https://github.com/waikato-datamining/pytorch/tree/master/dextr";
  }

  /**
   * The name of the tool.
   *
   * @return the name
   */
  @Override
  public String getName() {
    return "DEXTR";
  }

  /**
   * The icon of the tool.
   *
   * @return the icon
   */
  @Override
  public Icon getIcon() {
    return ImageManager.getIcon("dextr.png");
  }

  /**
   * Creates the mouse cursor to use.
   *
   * @return the cursor
   */
  @Override
  protected Cursor createCursor() {
    if (m_Client == null)
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
	if ((e.getKeyCode() == KeyEvent.VK_ENTER) && (m_Client != null)) {
	  if (getLayerManager().getMarkers().size() == 4) {
	    e.consume();
	    sendData();
	  }
	}
	if (!e.isConsumed())
	  super.keyPressed(e);
      }
    };
  }

  /**
   * Retrieves the parameters from the GUI.
   */
  protected void retrieveParameters() {
    super.retrieveParameters();

    m_MarkerSize  = m_TextMarkerSize.getValue().intValue();
    m_MarkerColor = m_PanelColor.getCurrent();
    getLayerManager().getMarkers().setExtent(m_MarkerSize);
    getLayerManager().getMarkers().setColor(m_MarkerColor);
  }

  /**
   * The channel to send the data on.
   *
   * @return		the redis channel
   */
  protected String getSendChannel() {
    return "dextr_in";
  }

  /**
   * The channel to receive the data on.
   *
   * @return		the redis channel
   */
  protected String getReceiveChannel() {
    return "dextr_out";
  }

  /**
   * Fills the parameter panel with the options.
   *
   * @param paramPanel  for adding the options to
   */
  @Override
  protected void addOptions(ParameterPanel paramPanel) {
    super.addOptions(paramPanel);

    m_TextMarkerSize = new NumberTextField(NumberTextField.Type.INTEGER, 10);
    m_TextMarkerSize.setCheckModel(new NumberTextField.BoundedNumberCheckModel(NumberTextField.Type.INTEGER, 1, null, getLayerManager().getMarkers().getExtent()));
    m_TextMarkerSize.addAnyChangeListener((ChangeEvent e) -> setApplyButtonState(m_ButtonApply, true));
    paramPanel.addParameter("Marker size", m_TextMarkerSize);

    m_PanelColor = new ColorChooserPanel(getLayerManager().getMarkers().getColor());
    paramPanel.addParameter("- color", m_PanelColor);
  }

  /**
   * Returns the type of data to send.
   *
   * @return the type of data
   */
  @Override
  protected RedisDataType getSendType() {
    return RedisDataType.STRING;
  }

  /**
   * Returns the type of data to receive.
   *
   * @return the type of data
   */
  @Override
  protected RedisDataType getReceiveType() {
    return RedisDataType.STRING;
  }

  /**
   * Method that assembles the data to send.
   *
   * @param errors 	for collecting errors
   * @return		the data to send (String or byte[])
   */
  protected String assembleSendData(MessageCollection errors) {
    JsonObject		result;
    JsonArray		points;
    JsonArray		coords;
    byte[]      	bytes;

    result = new JsonObject();

    // image
    if (m_BaseImageBase64 == null) {
      bytes = BufferedImageHelper.toBytes(getLayerManager().getImageLayer().getImage(), "JPG", errors);
      if (bytes != null)
	m_BaseImageBase64 = Base64.getEncoder().encodeToString(bytes);
      else
	return null;
    }
    result.addProperty("image", m_BaseImageBase64);

    // points
    points = new JsonArray();
    for (Point p: getLayerManager().getMarkers().getPoints()) {
      coords = new JsonArray();
      coords.add(p.getX());
      coords.add(p.getY());
      points.add(coords);
    }
    result.add("points", points);

    return result.toString();
  }

  /**
   * Parses the received data and updates the GUI.
   *
   * @param data	the data to parse (String or byte[])
   * @param errors	for collecting errors
   */
  protected void parseReceivedData(String data, MessageCollection errors) {
    JsonObject 		json;
    String 		maskStr;
    byte[]		bytes;
    BufferedImage 	maskImage;
    int[]		pixels;
    int[]		colors;
    int			width;
    int			height;
    BufferedImage	activeImage;

    if (data == null)
      return;

    json      = (JsonObject) JsonParser.parseString(data);
    maskStr   = json.get("mask").getAsString();
    bytes     = Base64.getMimeDecoder().decode(maskStr);
    errors    = new MessageCollection();
    maskImage = BufferedImageHelper.fromBytes(bytes, errors);
    width     = maskImage.getWidth();
    height    = maskImage.getHeight();
    pixels    = BufferedImageHelper.getPixels(maskImage);

    // use transparent black
    ImageUtils.replaceColor(pixels, Color.BLACK, new Color(0, 0, 0, 0));

    // replace other colors with one from layer
    colors = StatUtils.uniqueValues(pixels);
    for (int color: colors) {
      if (color != 0)
	ImageUtils.replaceColor(pixels, new Color(color), getActiveColor());
    }

    if (isAutomaticUndoEnabled())
      getCanvas().getOwner().addUndoPoint();

    // combine images
    activeImage = getActiveImage();
    maskImage   = new BufferedImage(width, height, activeImage.getType());
    maskImage.setRGB(0, 0, width, height, pixels, 0, width);
    ImageUtils.combineImages(maskImage, activeImage);
  }

  /**
   * Finishes up the request.
   */
  protected void finishedRequest() {
    super.finishedRequest();

    getLayerManager().getMarkers().clear();
    getLayerManager().update();
  }

  /**
   * Hook method for when new annotations have been set.
   */
  @Override
  public void annotationsChanged() {
    super.annotationsChanged();
    m_BaseImageBase64 = null;
  }
}
