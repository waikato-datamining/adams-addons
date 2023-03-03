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
 * OPEXPredictions.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.object.tools;

import adams.core.MessageCollection;
import adams.core.Shortening;
import adams.data.image.BufferedImageHelper;
import adams.data.redis.RedisDataType;
import adams.data.report.Report;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;
import adams.flow.transformer.locateobjects.ObjectPrefixHandler;
import adams.gui.core.ImageManager;
import adams.gui.core.KeyUtils;
import adams.gui.core.MouseUtils;
import opex4j.ObjectPrediction;
import opex4j.ObjectPredictions;

import javax.swing.Icon;
import java.awt.Cursor;
import java.awt.event.MouseEvent;

/**
 * Sends the image to Redis and expects object predictions in OPEX back.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class OPEX
  extends AbstractRedisTool<byte[],byte[]> {

  private static final long serialVersionUID = -6810912012034001551L;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Sends the image to Redis and expects object predictions in OPEX back.\n"
      + "Click left while holding the SHIFT key to send the data and obtain predictions.";
  }

  /**
   * The name of the tool.
   *
   * @return the name
   */
  @Override
  public String getName() {
    return "OPEX";
  }

  /**
   * The icon of the tool.
   *
   * @return the icon
   */
  @Override
  public Icon getIcon() {
    return ImageManager.getIcon("opex.png");
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
	if (MouseUtils.isLeftClick(e) && KeyUtils.isShiftDown(e.getModifiersEx())) {
	  e.consume();
	  sendData();
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
   * Returns the type of data to send.
   *
   * @return the type of data
   */
  @Override
  protected RedisDataType getSendType() {
    return RedisDataType.BYTE_ARRAY;
  }

  /**
   * Returns the type of data to receive.
   *
   * @return the type of data
   */
  @Override
  protected RedisDataType getReceiveType() {
    return RedisDataType.BYTE_ARRAY;
  }

  /**
   * The channel to send the data on.
   *
   * @return		the redis channel
   */
  protected String getSendChannel() {
    return "opex_in";
  }

  /**
   * The channel to receive the data on.
   *
   * @return		the redis channel
   */
  protected String getReceiveChannel() {
    return "opex_out";
  }

  /**
   * Method that assembles the data to send.
   *
   * @param errors for collecting errors
   * @return the data to send (String or byte[])
   */
  @Override
  protected byte[] assembleSendData(MessageCollection errors) {
    return BufferedImageHelper.toBytes(getCanvas().getImage(), "JPG", errors);
  }

  /**
   * Parses the received data and updates the GUI.
   *
   * @param data   the data to parse (String or byte[])
   * @param errors for collecting errors
   */
  @Override
  protected void parseReceivedData(byte[] data, MessageCollection errors) {
    String		json;
    ObjectPredictions	preds;
    LocatedObjects	lobjs;
    LocatedObject	lobj;
    String		prefix;
    Report		report;

    json  = new String(data);
    preds = null;
    try {
      preds = ObjectPredictions.newInstance(json);
    }
    catch (Exception e) {
      errors.add("Failed to parse received predictions: " + Shortening.shortenStart(json, 100), e);
    }
    if (preds == null)
      return;

    lobjs = new LocatedObjects();
    for (ObjectPrediction pred: preds.getObjects()) {
      lobj = new LocatedObject(pred.getBBox().toRectangle());
      if (pred.getPolygon().size() > 4)
	lobj.setPolygon(pred.getPolygon().toPolygon());
      lobj.getMetaData().putAll(pred.getMeta());
      lobj.getMetaData().put("type", pred.getLabel());
      if (pred.getScore() !=  null)
	lobj.getMetaData().put("score", pred.getScore());
      lobjs.add(lobj);
    }
    prefix = LocatedObjects.DEFAULT_PREFIX;
    if (getCanvas().getOwner().getAnnotator() instanceof ObjectPrefixHandler)
      prefix = ((ObjectPrefixHandler) getCanvas().getOwner().getAnnotator()).getPrefix();
    report = lobjs.toReport(prefix);
    getCanvas().getOwner().setReport(report);
    getCanvas().getOwner().annotationsChanged(this);
  }
}
