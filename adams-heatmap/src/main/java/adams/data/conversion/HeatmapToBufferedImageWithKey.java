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
 * HeatmapToBufferedImageWithKey.java
 * Copyright (C) 2014 Soilcares Research, Wageningen, The Netherlands
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */
package adams.data.conversion;

import adams.core.annotation.ThirdPartyCopyright;
import adams.data.heatmap.Heatmap;
import adams.data.image.AbstractImageContainer;
import adams.data.image.BufferedImageContainer;
import adams.gui.visualization.core.AbstractColorGradientGenerator;
import adams.gui.visualization.core.BiColorGenerator;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

/**
 <!-- globalinfo-start -->
 * Turns a heatmap into a BufferedImage, allows the generation of a key in the image.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-generator &lt;adams.gui.visualization.core.AbstractColorGradientGenerator&gt; (property: generator)
 * &nbsp;&nbsp;&nbsp;The generator to use for creating the gradient colors.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.visualization.core.BiColorGenerator
 * </pre>
 *
 * <pre>-use-custom-range &lt;boolean&gt; (property: useCustomRange)
 * &nbsp;&nbsp;&nbsp;Whether to use a custom user defined range rather than the range from the
 * &nbsp;&nbsp;&nbsp;incoming heatmap
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-min-range &lt;double&gt; (property: minRange)
 * &nbsp;&nbsp;&nbsp;the minimum value of the heatmap colour range
 * &nbsp;&nbsp;&nbsp;default: -1.0
 * </pre>
 *
 * <pre>-max-range &lt;double&gt; (property: maxRange)
 * &nbsp;&nbsp;&nbsp;the maximum value to use in the colour range
 * &nbsp;&nbsp;&nbsp;default: 100.0
 * </pre>
 *
 * <pre>-show-key &lt;boolean&gt; (property: showKey)
 * &nbsp;&nbsp;&nbsp;Whether to display a key displaying the values corresponding to certain
 * &nbsp;&nbsp;&nbsp;colours on the heatmap
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-scale-factor &lt;int&gt; (property: scaleFactor)
 * &nbsp;&nbsp;&nbsp;scale factor with which to increase the size of the heatmap image
 * &nbsp;&nbsp;&nbsp;default: 1
 * </pre>
 *
 * <pre>-missing-color &lt;java.awt.Color&gt; (property: missingColor)
 * &nbsp;&nbsp;&nbsp;The color to use if a value in the heatmap is missing
 * &nbsp;&nbsp;&nbsp;default: #ffffff
 * </pre>
 *
 <!-- options-end -->
 *
 * @author michael.fowke
 * @version $Revision$
 */
@ThirdPartyCopyright(
    author = "Michael Fowke",
    copyright = "2014 Soilcares Research, Wageningen, The Netherlands"
)
public class HeatmapToBufferedImageWithKey
  extends AbstractConversion {

  /** for serialization*/
  private static final long serialVersionUID = -9065168080375023679L;

  /** The generator to use */
  protected AbstractColorGradientGenerator m_Generator;

  /** Whether to use a use defined range */
  protected boolean m_UseCustomRange;

  /** Minimum value of the range */
  protected double m_MinRange;

  /** Maximum value of the range */
  protected double m_MaxRange;

  /** Whether to display the colour key on the heatmap */
  protected boolean m_ShowKey;

  /** scale factor to enlarge the heatmap */
  protected int m_ScaleFactor;

  /** colour to use to display a missing value */
  protected Color m_MissingColor;

  /** the gradient colors */
  protected Color[] m_GradientColors;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
	"Turns a heatmap into a BufferedImage, allows the generation of a key in the image.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"generator", "generator",
	new BiColorGenerator());

    m_OptionManager.add(
	"use-custom-range", "useCustomRange",
	false);
    m_OptionManager.add(
	"min-range", "minRange",
	-1.0);

    m_OptionManager.add(
	"max-range", "maxRange",
	100.0);
    m_OptionManager.add(
	"show-key", "showKey",
	false);

    m_OptionManager.add(
	"scale-factor", "scaleFactor",
	1);

    m_OptionManager.add(
	"missing-color", "missingColor",
	Color.WHITE);
  }

  /**
   * Resets the object.
   */
  @Override
  protected void reset() {
    super.reset();

    m_GradientColors = null;
  }

  /**
   * Sets the number of gradient colors to use.
   *
   * @param value	the number of colors
   */
  public void setGenerator(AbstractColorGradientGenerator value) {
    m_Generator = value;
    reset();
  }

  /**
   * Returns the number of gradient colors to use.
   *
   * @return		the number of colors
   */
  public AbstractColorGradientGenerator getGenerator() {
    return m_Generator;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String generatorTipText() {
    return "The generator to use for creating the gradient colors.";
  }


  /**
   * Set whether to use a custom range
   * @param value		Whether to use a user defined range
   */
  public void setUseCustomRange(boolean value) {
    m_UseCustomRange = value;
    reset();
  }

  /**
   * Set whether to use a custom range
   * @return			Whether to use a user defined range
   */
  public boolean getUseCustomRange(){
    return m_UseCustomRange;
  }

  /**
   * Tip text for this property
   * @return	Description for displaying in the GUI
   */
  public String useCustomRangeTipText(){
    return "Whether to use a custom user defined range rather than the range from the incoming heatmap";
  }

  /**
   * Set the minimum value in the user defined range
   * @param value			Minimum value
   */
  public void setMinRange(double value) {
    m_MinRange = value;
    reset();
  }

  /**
   * Get the mimum value in the user defined range
   * @return				Minimum value
   */
  public double getMinRange() {
    return m_MinRange;
  }

  /**
   * Tip text for this property
   * @return			Description for displaying in the GUI
   */
  public String minRangeTipText(){
    return "the minimum value of the heatmap colour range";
  }

  /**
   * Set the maximum value in the user defined range
   * @param value			maximum value
   */
  public void setMaxRange(double value){
    m_MaxRange = value;
    reset();
  }

  /**
   * Get the maximum value in the user defined range
   * @return			The maximum value
   */
  public double getMaxRange() {
    return m_MaxRange;
  }

  /**
   * Tip text for this property
   * @return		Description for displaying in the GUI
   */
  public String maxRangeTipText(){
    return "the maximum value to use in the colour range";
  }

  /**
   * Set whether to display the colour key with the heatmap
   * @param value				Whether to display key
   */
  public void setShowKey(boolean value) {
    m_ShowKey = value;
    reset();
  }

  /**
   * Get whether to display the colour key with the heatmap
   * @return				Whether to display the key
   */
  public boolean getShowKey(){
    return m_ShowKey;
  }

  /**
   * Tip text for this property
   * @return			Description for displaying in the GUI
   */
  public String showKeyTipText(){
    return "Whether to display a key displaying the values corresponding to certain colours on the heatmap";
  }

  /**
   * Set the scale factor to enlarge the heatmap by
   * @param val			Scale factor
   */
  public void setScaleFactor(int val) {
    m_ScaleFactor = val;
    reset();
  }

  /**
   * Get the scale factor to enlarge the heatmap by
   * @return			Scale factor
   */
  public int getScaleFactor() {
    return m_ScaleFactor;
  }

  /**
   * Tip text for this property
   * @return			Description for displaying in the GUI
   */
  public String scaleFactorTipText() {
    return "scale factor with which to increase the size of the heatmap image";
  }

  /**
   * Set the colour to use for a missing value
   * @param val			Colour for representing a missing value
   */
  public void setMissingColor(Color val) {
    m_MissingColor = val;
    reset();
  }

  /**
   * Get the colour to use for a missing value
   * @return			Colour for representing a missing value
   */
  public Color getMissingColor() {
    return m_MissingColor;
  }

  /**
   * Tip text for this property
   * @return			Description for displaying in the GUI
   */
  public String missingColorTipText(){
    return "The color to use if a value in the heatmap is missing";
  }

  @Override
  public Class accepts() {
    return Heatmap.class;
  }

  @Override
  public Class generates() {
    return AbstractImageContainer.class;
  }

  /**
   * Generates the gradient colors.
   *
   * @return		the colors
   */
  protected Color[] getGradientColors() {
    if(m_GradientColors == null)
      m_GradientColors = m_Generator.generate();


    return m_GradientColors;
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted object
   * @throws Exception	if conversion fails
   */
  @Override
  protected Object doConvert() throws Exception {
    BufferedImageContainer 	result;
    BufferedImage		image;
    Color[] 			colors;
    Heatmap			map;
    double 			min;
    double		 	max;
    double 			range;
    int				x;
    int 			y;
    Graphics2D			g;
    Color			color;

    map = (Heatmap) m_Input;
    colors = getGradientColors();
    if(m_UseCustomRange) {
      min = m_MinRange;
      max = m_MaxRange;
    }
    else {
      min = Double.MAX_VALUE;
      max = Double.MIN_VALUE;
      for (y = 0; y < map.getHeight(); y++) {
	for (x = 0; x < map.getWidth(); x++) {
	  if (map.get(y, x) > 0.0)
	    min = Math.min(map.get(y, x), min);   // we don't want zeroes
	  max = Math.max(map.get(y, x), max);
	}
      }
    }

    range = max - min;
    image = null;

    //if displaying the key on the heatmap.
    //Added to the right hand side of the heatmap
    if(m_ShowKey) {
      int spaceTop = 10;
      int spaceBottom = 10;

      int keyWidth = 20;
      int afterHeatMap = 20;
      int afterKey = 50;
      int tickWidth  = 10;
      int endHeatMap = map.getWidth() * m_ScaleFactor;
      int heightMap = map.getHeight() * m_ScaleFactor;
      int totalHeight = map.getHeight() * m_ScaleFactor + spaceTop + spaceBottom;

      int endFirstWhite = endHeatMap + afterHeatMap;
      int endKey = endFirstWhite + keyWidth;
      int totalWidth = endKey + afterKey;
      int startText = endKey + tickWidth;

      image = new BufferedImage(totalWidth, totalHeight, BufferedImage.TYPE_INT_RGB);
      g		= image.createGraphics();
      g.setColor(Color.WHITE);
      g.fillRect(0, 0, totalWidth, totalHeight);

      //heatmap part
      for (y = 0; y < map.getHeight(); y++) {
	for (x = 0; x < map.getWidth(); x++) {
	  if ((map.get(y, x) == 0.0) || map.isMissing(y, x))
	    color = m_MissingColor;
	  else
	    color = colors[(int) (((map.get(y, x) - min) / range) * (colors.length - 2)) + 1];
	  g.setColor(color);
	  g.fillRect(x*m_ScaleFactor , y* m_ScaleFactor + spaceTop, m_ScaleFactor, m_ScaleFactor);
	}
      }

      // key/legend part
      for(int i = 0; i< heightMap; i++) {
	double percen = (double)i/heightMap;
	double index = percen * (colors.length-2) +1;
	color = colors[(int) index];
	g.setColor(color);
	g.drawLine(endFirstWhite , i+spaceTop, endKey, i+spaceTop);
      }

      g.setColor(Color.BLACK);
      g.drawRect(endFirstWhite, spaceTop, keyWidth, heightMap);

      int numTicks = 3;
      Font curF = g.getFont();
      Font newF = new Font(curF.getName(), Font.PLAIN, curF.getSize()-2);
      g.setFont(newF);

      for(int i = 0; i <numTicks; i++) {
	double segment = heightMap/ (numTicks-1);
	double val = ((max-min) * i / 2) + min;
	DecimalFormat df = new DecimalFormat("#.###");
	String valFormatted  = df.format(val);
	g.drawLine(endKey, (int)(segment *i) + spaceTop,startText, (int)(segment *i) + spaceTop);
	g.drawString(valFormatted,startText,(float)((segment *i)+spaceTop));
      }
    }

    //If not displaying the key
    else {
      image = new BufferedImage(map.getWidth()* m_ScaleFactor, map.getHeight()*m_ScaleFactor, BufferedImage.TYPE_INT_RGB);
      g		= image.createGraphics();

      //draw heatmap
      for (y = 0; y < map.getHeight(); y++) {
	for (x = 0; x < map.getWidth(); x++) {
	  if (map.get(y, x) == 0.0)
	    color = m_MissingColor;
	  else
	    color = colors[(int) (((map.get(y, x) - min) / range) * (colors.length - 2)) + 1];
	  g.setColor(color);
	  g.fillRect(x*m_ScaleFactor , y* m_ScaleFactor, m_ScaleFactor, m_ScaleFactor);
	}
      }
    }

    result = new BufferedImageContainer();
    result.setImage(image);

    return result;
  }
}
