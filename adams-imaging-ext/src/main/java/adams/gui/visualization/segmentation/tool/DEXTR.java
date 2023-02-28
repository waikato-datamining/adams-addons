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
import adams.core.Utils;
import adams.core.base.BaseHostname;
import adams.data.image.BufferedImageHelper;
import adams.data.statistics.StatUtils;
import adams.flow.standalone.RedisConnection;
import adams.gui.chooser.ColorChooserPanel;
import adams.gui.core.BaseFlatButton;
import adams.gui.core.BaseObjectTextField;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseTextField;
import adams.gui.core.GUIHelper;
import adams.gui.core.ImageManager;
import adams.gui.core.MouseUtils;
import adams.gui.core.NumberTextField;
import adams.gui.core.ParameterPanel;
import adams.gui.visualization.segmentation.ImageUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.StringCodec;
import io.lettuce.core.pubsub.RedisPubSubListener;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;

import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
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
  extends AbstractTool {

  private static final long serialVersionUID = 8374950649752446530L;

  /** the marker size. */
  protected NumberTextField m_TextMarkerSize;

  /** the marker color. */
  protected ColorChooserPanel m_PanelColor;

  /** the redis host/port. */
  protected BaseObjectTextField<BaseHostname> m_TextRedisHost;

  /** the redis channel for sending. */
  protected BaseTextField m_TextRedisSend;

  /** the redis channel for receiving. */
  protected BaseTextField m_TextRedisReceive;

  /** the redis timeout. */
  protected NumberTextField m_TextRedisTimeout;

  /** the apply button. */
  protected BaseFlatButton m_ButtonApply;

  /** the marker size. */
  protected int m_MarkerSize;

  /** the marker color. */
  protected Color m_MarkerColor;

  /** the redis host. */
  protected String m_RedisHost;

  /** the redis port. */
  protected int m_RedisPort;

  /** the redis database. */
  protected int m_RedisDB;

  /** the redis send channel. */
  protected String m_RedisSend;

  /** the redis receive channel. */
  protected String m_RedisReceive;

  /** the timeout in milli-seconds. */
  protected int m_RedisTimeout;

  /** the data received via Redis. */
  protected String m_ReceivedData;

  /** the client object. */
  protected transient RedisClient m_Client;

  /** the cached base image as base64 JPEG. */
  protected transient String m_BaseImageBase64;

  /** the pub/sub connection object. */
  protected transient StatefulRedisPubSubConnection<String,String> m_PubSubConnection;

  /** the connection object. */
  protected transient StatefulRedisConnection<String,String> m_Connection;

  /** the pub/sub listener. */
  protected transient RedisPubSubListener<String,String> m_PubSubListener;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "After connecting to the Redis server, click on four extreme points "
      + "with the left mouse button and then press ENTER to have a shape detected.\n"
      + "Click on the right mouse button to reset the selected points.\n"
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
	else if (MouseUtils.isRightClick(e) && MouseUtils.hasNoModifierKey(e)) {
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
	    applyDextr();
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

    result = super.checkBeforeApply();

    if (result == null) {
      if (m_TextRedisSend.getText().trim().isEmpty())
	result = "'Send' channel is empty!";
      else if (m_TextRedisReceive.getText().trim().isEmpty())
	result = "'Receive' channel is empty!";
    }

    return result;
  }

  /**
   * Applies the settings.
   */
  @Override
  protected void doApply() {
    m_MarkerSize   = m_TextMarkerSize.getValue().intValue();
    m_MarkerColor  = m_PanelColor.getCurrent();
    m_RedisHost    = m_TextRedisHost.getObject().hostnameValue();
    m_RedisPort    = m_TextRedisHost.getObject().portValue(RedisConnection.DEFAULT_PORT);
    m_RedisDB      = 0;
    m_RedisSend    = m_TextRedisSend.getText();
    m_RedisReceive = m_TextRedisReceive.getText();
    m_RedisTimeout = m_TextRedisTimeout.getValue().intValue();

    getLayerManager().getMarkers().setExtent(m_MarkerSize);
    getLayerManager().getMarkers().setColor(m_MarkerColor);

    if (m_Client != null) {
      m_Client.shutdown();
      m_Client = null;
    }

    try {
      m_Client = RedisClient.create(RedisURI.Builder.redis(m_RedisHost, m_RedisPort).withDatabase(m_RedisDB).build());
    }
    catch (Exception e) {
      GUIHelper.showErrorMessage(getCanvas(), "Failed to create Redis client: " + m_RedisHost + ":" + m_RedisPort + "/" + m_RedisDB, e);
    }
  }

  /**
   * Creates the panel for setting the options.
   *
   * @return the options panel
   */
  @Override
  protected BasePanel createOptionPanel() {
    ParameterPanel	result;
    JPanel		panel;

    result = new ParameterPanel();

    m_ButtonApply = createApplyButton();

    m_TextMarkerSize = new NumberTextField(NumberTextField.Type.INTEGER, 10);
    m_TextMarkerSize.setCheckModel(new NumberTextField.BoundedNumberCheckModel(NumberTextField.Type.INTEGER, 1, null, getLayerManager().getMarkers().getExtent()));
    m_TextMarkerSize.addAnyChangeListener((ChangeEvent e) -> setApplyButtonState(m_ButtonApply, true));
    result.addParameter("Marker size", m_TextMarkerSize);

    m_PanelColor = new ColorChooserPanel(getLayerManager().getMarkers().getColor());
    result.addParameter("- color", m_PanelColor);

    m_TextRedisHost = new BaseObjectTextField<>(new BaseHostname("localhost:" + RedisConnection.DEFAULT_PORT));
    m_TextRedisHost.addAnyChangeListener((ChangeEvent e) -> setApplyButtonState(m_ButtonApply, true));
    result.addParameter("Redis host", m_TextRedisHost);

    m_TextRedisSend = new BaseTextField("dextr_in", 10);
    m_TextRedisSend.addAnyChangeListener((ChangeEvent e) -> setApplyButtonState(m_ButtonApply, true));
    result.addParameter("- Send", m_TextRedisSend);

    m_TextRedisReceive = new BaseTextField("dextr_out", 10);
    m_TextRedisReceive.addAnyChangeListener((ChangeEvent e) -> setApplyButtonState(m_ButtonApply, true));
    result.addParameter("- Receive", m_TextRedisReceive);

    m_TextRedisTimeout = new NumberTextField(NumberTextField.Type.INTEGER, 10);
    m_TextRedisTimeout.setCheckModel(new NumberTextField.BoundedNumberCheckModel(NumberTextField.Type.INTEGER, 1, null, 2000));
    m_TextRedisTimeout.addAnyChangeListener((ChangeEvent e) -> setApplyButtonState(m_ButtonApply, true));
    result.addParameter(" - Timeout (msec)", m_TextRedisTimeout);

    panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panel.add(m_ButtonApply);
    result.addParameter("", panel);

    return result;
  }

  /**
   * Returns a new pub/sub listener for strings.
   *
   * @return		the listener
   */
  protected RedisPubSubListener<String, String> newStringListener() {
    return new RedisPubSubListener<>() {
      @Override
      public void message(String channel, String message) {
	m_ReceivedData = message;
	m_PubSubConnection.removeListener(m_PubSubListener);
	m_PubSubConnection.async().unsubscribe(m_RedisReceive);
	m_PubSubConnection = null;
	m_PubSubListener   = null;
      }
      @Override
      public void message(String pattern, String channel, String message) {
	message(channel, message);
      }
      @Override
      public void subscribed(String channel, long count) {
	if (isLoggingEnabled())
	  getLogger().info("Subscribed to channel: " + channel);
      }
      @Override
      public void psubscribed(String pattern, long count) {
	if (isLoggingEnabled())
	  getLogger().info("Subscribed to pattern: " + pattern);
      }
      @Override
      public void unsubscribed(String channel, long count) {
	if (isLoggingEnabled())
	  getLogger().info("Unsubscribed from channel: " + channel);
      }
      @Override
      public void punsubscribed(String pattern, long count) {
	if (isLoggingEnabled())
	  getLogger().info("Unsubscribed from pattern: " + pattern);
      }
    };
  }

  /**
   * Prepares the data to send to Redis.
   *
   * @param errors 	for recording errors
   * @return		the data to send
   */
  protected JsonObject toJson(MessageCollection errors) {
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

    return result;
  }

  /**
   * Interprets the data received from Redis.
   *
   * @param data	the received data, ignored if null
   */
  protected void fromJson(JsonObject data) {
    String 		maskStr;
    byte[]		bytes;
    BufferedImage 	maskImage;
    MessageCollection	errors;
    int[]		pixels;
    int[]		colors;
    int			width;
    int			height;
    BufferedImage	activeImage;

    if (data == null)
      return;

    maskStr   = data.get("mask").getAsString();
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
   * Communicates with DEXTR and updates the canvas.
   */
  protected void applyDextr() {
    SwingWorker		worker;
    MessageCollection 	errors;
    JsonElement 	out;

    m_ReceivedData = null;

    // generate json
    errors = new MessageCollection();
    out = toJson(errors);
    if (out == null) {
      if (errors.isEmpty())
	GUIHelper.showErrorMessage(getCanvas(), "Failed to generate data to send to Redis!");
      else
	GUIHelper.showErrorMessage(getCanvas(), "Failed to generate data to send to Redis:\n" + errors);
      return;
    }

    worker = new SwingWorker() {
      @Override
      protected Object doInBackground() throws Exception {
	long start;

	// send data
	m_PubSubListener   = newStringListener();
	m_PubSubConnection = m_Client.connectPubSub(StringCodec.UTF8);
	m_PubSubConnection.addListener(m_PubSubListener);
	m_PubSubConnection.async().subscribe(m_RedisReceive);
	m_Connection       = m_Client.connect(StringCodec.UTF8);
	m_Connection.async().publish(m_RedisSend, out.toString());

	// wait for data to arrive
	errors.clear();
	start = System.currentTimeMillis();
	while ((m_ReceivedData == null) && (System.currentTimeMillis() - start < m_RedisTimeout)) {
	  Utils.wait(DEXTR.this, 100, 100);
	}
	if (m_ReceivedData != null) {
	  try {
	    fromJson((JsonObject) JsonParser.parseString(m_ReceivedData));
	  }
	  catch (Exception e) {
	    errors.add("Failed to parse data received from Redis!", e);
	    GUIHelper.showErrorMessage(getCanvas(), errors.toString());
	  }
	}
	return null;
      }

      @Override
      protected void done() {
	super.done();
	m_ReceivedData = null;
	getLayerManager().getMarkers().clear();
	getLayerManager().update();
      }
    };
    worker.execute();
  }

  /**
   * Hook method for when new annotations have been set.
   */
  @Override
  public void annotationsChanged() {
    super.annotationsChanged();
    m_BaseImageBase64 = null;
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  @Override
  public void cleanUp() {
    if (m_Connection != null) {
      m_Connection.close();
      m_Connection = null;
    }

    if (m_PubSubConnection != null) {
      if (m_PubSubListener != null)
	m_PubSubConnection.removeListener(m_PubSubListener);
      m_PubSubConnection.async().unsubscribe(m_RedisReceive);
      m_PubSubConnection = null;
      m_PubSubListener   = null;
    }

    if (m_Client != null) {
      m_Client.shutdown();
      m_Client = null;
    }

    super.cleanUp();
  }
}
