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
 * Copyright (C) 2023-2025 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.object.tools;

import adams.core.MessageCollection;
import adams.data.image.BufferedImageHelper;
import adams.data.redis.RedisDataType;
import adams.data.report.Report;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;
import adams.flow.transformer.locateobjects.ObjectPrefixHandler;
import adams.gui.chooser.ColorChooserPanel;
import adams.gui.core.ImageManager;
import adams.gui.core.NumberTextField;
import adams.gui.core.ParameterPanel;
import adams.gui.visualization.object.annotator.DEXTRMarkers;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import opex4j.Polygon;

import javax.swing.Icon;
import javax.swing.event.ChangeEvent;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

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

  /** the minimum object size (width/height). */
  protected NumberTextField m_TextMinObjectSize;

  /** the maximum object size (width/height). */
  protected NumberTextField m_TextMaxObjectSize;

  /** the marker size. */
  protected int m_MarkerSize;

  /** the marker color. */
  protected Color m_MarkerColor;

  /** the minimum object size (width/height). */
  protected int m_MinObjectSize;

  /** the maximum object size (width/height). */
  protected int m_MaxObjectSize;
  
  /** the cached base image as base64 JPEG. */
  protected transient String m_BaseImageBase64;

  /** the points to send. */
  protected List<Point> m_Points;

  /** the internally used annotator. */
  protected DEXTRMarkers m_Annotator;

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
      + "Communicates with a DEXTR docker container using Redis.\n"
      + "\n"
      + "More information:\n"
      + "https://github.com/waikato-datamining/pytorch/tree/master/dextr";
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Annotator = new DEXTRMarkers();
    m_Annotator.setTool(this);
    
    m_MinObjectSize = -1;
    m_MaxObjectSize = -1;
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
    return null;
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
    return null;
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
   * Retrieves the parameters from the GUI.
   */
  protected void retrieveParameters() {
    super.retrieveParameters();

    m_MarkerSize    = m_TextMarkerSize.getValue().intValue();
    m_MarkerColor   = m_PanelColor.getCurrent();
    m_MinObjectSize = m_TextMinObjectSize.getValue().intValue();
    m_MaxObjectSize = m_TextMaxObjectSize.getValue().intValue();
    m_Annotator.setColor(m_MarkerColor);
    m_Annotator.setExtent(m_MarkerSize);
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
    m_TextMarkerSize.setCheckModel(new NumberTextField.BoundedNumberCheckModel(NumberTextField.Type.INTEGER, 1, null, m_Annotator.getExtent()));
    m_TextMarkerSize.addAnyChangeListener((ChangeEvent e) -> setApplyButtonState(m_ButtonApply, true));
    m_TextMarkerSize.setToolTipText("The size of markers in pixel when selecting extreme points");
    paramPanel.addParameter("Marker size", m_TextMarkerSize);

    m_PanelColor = new ColorChooserPanel(m_Annotator.getColor());
    m_PanelColor.setToolTipText("The color to use for the markers");
    paramPanel.addParameter("- color", m_PanelColor);

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
   * Sets the points to send.
   *
   * @param value	the points
   */
  public void setPoints(List<Point> value) {
    if (value != null)
      m_Points = new ArrayList<>(value);
    else
      m_Points = null;
  }

  /**
   * Returns the points to send.
   *
   * @return		the point, null if none set
   */
  public List<Point> getPoints() {
    return m_Points;
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

    if (m_Points == null) {
      errors.add("No extreme points provided!");
      return null;
    }
    if (m_Points.size() != 4) {
      errors.add("Expected four extreme points but received: " + m_Points.size());
      return null;
    }

    result = new JsonObject();

    // image
    if (m_BaseImageBase64 == null) {
      bytes = BufferedImageHelper.toBytes(getCanvas().getImage(), "JPG", errors);
      if (bytes != null)
	m_BaseImageBase64 = Base64.getEncoder().encodeToString(bytes);
      else
	return null;
    }
    result.addProperty("image", m_BaseImageBase64);

    // points
    points = new JsonArray();
    for (Point p: getPoints()) {
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
    JsonArray		contours;
    JsonArray		contour;
    JsonArray		coords;
    int			i;
    int			n;
    LocatedObjects	lobjs;
    LocatedObject	lobj;
    List<Point> 	points;
    Polygon		poly;
    String		prefix;
    Report		report;
    Rectangle		rect;

    if (data == null)
      return;

    json     = (JsonObject) JsonParser.parseString(data);
    contours = json.get("contours").getAsJsonArray();
    lobjs    = new LocatedObjects(getCanvas().getOwner().getObjects());
    for (i = 0; i < contours.size(); i++) {
      // assemble polygon
      contour = contours.get(i).getAsJsonArray();
      points  = new ArrayList<>();
      for (n = 0; n < contour.size(); n++) {
	coords = contour.get(n).getAsJsonArray();
	points.add(new Point((int) coords.get(0).getAsDouble(), (int) coords.get(1).getAsDouble()));
      }
      poly = new Polygon(points);

      // check bounds
      if ((m_MinObjectSize > 0) || (m_MaxObjectSize > 0)) {
	rect = poly.toBBox().toRectangle();
	if (m_MinObjectSize > 0) {
	  if ((rect.width < m_MinObjectSize) && (rect.height < m_MinObjectSize))
	    continue;
	}
	if (m_MaxObjectSize > 0) {
	  if ((rect.width > m_MaxObjectSize) && (rect.height > m_MaxObjectSize))
	    continue;
	}
      }

      // add object
      lobj = new LocatedObject(null, poly.toBBox().toRectangle());
      lobj.setPolygon(poly.toPolygon());
      lobjs.add(lobj);
    }

    prefix = LocatedObjects.DEFAULT_PREFIX;
    if (getCanvas().getOwner().getAnnotator() instanceof ObjectPrefixHandler)
      prefix = ((ObjectPrefixHandler) getCanvas().getOwner().getAnnotator()).getPrefix();
    report = lobjs.toReport(prefix);
    getCanvas().getOwner().addUndoPoint("DEXTR predictions");
    getCanvas().getOwner().setReport(report);
    getCanvas().getOwner().annotationsChanged(this);
  }

  /**
   * Finishes up the request.
   */
  protected void finishedRequest() {
    super.finishedRequest();
    m_Points = null;
  }

  /**
   * Gets called to activate the tool.
   */
  @Override
  public void activate() {
    super.activate();
    getCanvas().getOwner().setAnnotator(m_Annotator);
  }
}
