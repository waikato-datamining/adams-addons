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
 * GenerateWordCloud.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.MessageCollection;
import adams.core.QuickInfoHelper;
import adams.core.option.OptionUtils;
import adams.data.image.BufferedImageContainer;
import adams.flow.core.Token;
import adams.flow.transformer.wordcloud.AbstractBackground;
import adams.flow.transformer.wordcloud.AbstractColorPalette;
import adams.flow.transformer.wordcloud.AbstractFontScalar;
import adams.flow.transformer.wordcloud.DefaultBackground;
import adams.flow.transformer.wordcloud.DefaultColorPalette;
import adams.flow.transformer.wordcloud.DefaultFontScalar;
import com.kennycason.kumo.CollisionMode;
import com.kennycason.kumo.PolarBlendMode;
import com.kennycason.kumo.PolarWordCloud;
import com.kennycason.kumo.WordCloud;
import com.kennycason.kumo.WordFrequency;
import com.kennycason.kumo.bg.Background;
import com.kennycason.kumo.font.scale.FontScalar;
import com.kennycason.kumo.palette.ColorPalette;

import java.awt.Dimension;
import java.util.Arrays;

/**
 <!-- globalinfo-start -->
 * Generates a word cloud from the incoming word frequencies.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;com.kennycason.kumo.WordFrequency[]<br>
 * &nbsp;&nbsp;&nbsp;com.kennycason.kumo.WordFrequency[][]<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.image.BufferedImageContainer<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: GenerateWordCloud
 * </pre>
 *
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow execution at this level gets stopped in case this
 * &nbsp;&nbsp;&nbsp;actor encounters an error; the error gets propagated; useful for critical
 * &nbsp;&nbsp;&nbsp;actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-width &lt;int&gt; (property: width)
 * &nbsp;&nbsp;&nbsp;The width for the image.
 * &nbsp;&nbsp;&nbsp;default: 600
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 * <pre>-height &lt;int&gt; (property: height)
 * &nbsp;&nbsp;&nbsp;The height for the image.
 * &nbsp;&nbsp;&nbsp;default: 300
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 * <pre>-collision-mode &lt;RECTANGLE|PIXEL_PERFECT&gt; (property: collisionMode)
 * &nbsp;&nbsp;&nbsp;The collision mode to use.
 * &nbsp;&nbsp;&nbsp;default: PIXEL_PERFECT
 * </pre>
 *
 * <pre>-polar-blend-mode &lt;EVEN|BLUR&gt; (property: polarBlendMode)
 * &nbsp;&nbsp;&nbsp;The polar blend mode to use.
 * &nbsp;&nbsp;&nbsp;default: BLUR
 * </pre>
 *
 * <pre>-padding &lt;int&gt; (property: padding)
 * &nbsp;&nbsp;&nbsp;The padding to use.
 * &nbsp;&nbsp;&nbsp;default: 2
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 * <pre>-color-palette &lt;adams.flow.transformer.wordcloud.AbstractColorPalette&gt; (property: colorPalette)
 * &nbsp;&nbsp;&nbsp;The color palette to use.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.transformer.wordcloud.DefaultColorPalette
 * </pre>
 *
 * <pre>-font-scalar &lt;adams.flow.transformer.wordcloud.AbstractFontScalar&gt; (property: fontScalar)
 * &nbsp;&nbsp;&nbsp;The font scalar to use.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.transformer.wordcloud.DefaultFontScalar
 * </pre>
 *
 * <pre>-background &lt;adams.flow.transformer.wordcloud.AbstractBackground&gt; (property: background)
 * &nbsp;&nbsp;&nbsp;The background to use.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.transformer.wordcloud.DefaultBackground
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class GenerateWordCloud
  extends AbstractTransformer {

  private static final long serialVersionUID = 3133377526712815523L;

  /** the width. */
  protected int m_Width;
  
  /** the height. */
  protected int m_Height;

  /** the collision mode. */
  protected CollisionMode m_CollisionMode;

  /** the blendmode when combining two frequency lists. */
  protected PolarBlendMode m_PolarBlendMode;

  /** the padding. */
  protected int m_Padding;

  /** the color palette to use. */
  protected AbstractColorPalette m_ColorPalette;

  /** the font scalar to use. */
  protected AbstractFontScalar m_FontScalar;

  /** the background. */
  protected AbstractBackground m_Background;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates a word cloud from the incoming word frequencies.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "width", "width",
      600, 1, null);

    m_OptionManager.add(
      "height", "height",
      300, 1, null);

    m_OptionManager.add(
      "collision-mode", "collisionMode",
      CollisionMode.PIXEL_PERFECT);

    m_OptionManager.add(
      "polar-blend-mode", "polarBlendMode",
      PolarBlendMode.BLUR);

    m_OptionManager.add(
      "padding", "padding",
      2, 0, null);

    m_OptionManager.add(
      "color-palette", "colorPalette",
      new DefaultColorPalette());

    m_OptionManager.add(
      "font-scalar", "fontScalar",
      new DefaultFontScalar());

    m_OptionManager.add(
      "background", "background",
      new DefaultBackground());
  }

  /**
   * Sets the width for the image.
   *
   * @param value	the width
   */
  public void setWidth(int value) {
    if (getOptionManager().isValid("width", value)) {
      m_Width = value;
      reset();
    }
  }

  /**
   * Returns the width for the image.
   *
   * @return		the width
   */
  public int getWidth() {
    return m_Width;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String widthTipText() {
    return "The width for the image.";
  }

  /**
   * Sets the height for the image.
   *
   * @param value	the height
   */
  public void setHeight(int value) {
    if (getOptionManager().isValid("height", value)) {
      m_Height = value;
      reset();
    }
  }

  /**
   * Returns the height for the image.
   *
   * @return		the height
   */
  public int getHeight() {
    return m_Height;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String heightTipText() {
    return "The height for the image.";
  }

  /**
   * Sets the collision mode to use.
   *
   * @param value	the mode
   */
  public void setCollisionMode(CollisionMode value) {
    m_CollisionMode = value;
    reset();
  }

  /**
   * Returns the collision mode in use.
   *
   * @return		the mode
   */
  public CollisionMode getCollisionMode() {
    return m_CollisionMode;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String collisionModeTipText() {
    return "The collision mode to use.";
  }

  /**
   * Sets the polar blend mode to use.
   *
   * @param value	the mode
   */
  public void setPolarBlendMode(PolarBlendMode value) {
    m_PolarBlendMode = value;
    reset();
  }

  /**
   * Returns the polar blend mode in use.
   *
   * @return		the mode
   */
  public PolarBlendMode getPolarBlendMode() {
    return m_PolarBlendMode;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String polarBlendModeTipText() {
    return "The polar blend mode to use.";
  }

  /**
   * Sets the padding to use.
   *
   * @param value	the padding
   */
  public void setPadding(int value) {
    if (getOptionManager().isValid("padding", value)) {
      m_Padding = value;
      reset();
    }
  }

  /**
   * Returns the padding in use.
   *
   * @return		the padding
   */
  public int getPadding() {
    return m_Padding;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String paddingTipText() {
    return "The padding to use.";
  }

  /**
   * Sets the color palette to use.
   *
   * @param value	the palette
   */
  public void setColorPalette(AbstractColorPalette value) {
    m_ColorPalette = value;
    reset();
  }

  /**
   * Returns the color palette to use.
   *
   * @return		the palette
   */
  public AbstractColorPalette getColorPalette() {
    return m_ColorPalette;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String colorPaletteTipText() {
    return "The color palette to use.";
  }

  /**
   * Sets the font scalar to use.
   *
   * @param value	the font scalar
   */
  public void setFontScalar(AbstractFontScalar value) {
    m_FontScalar = value;
    reset();
  }

  /**
   * Returns the font scalar to use.
   *
   * @return		the font scalar
   */
  public AbstractFontScalar getFontScalar() {
    return m_FontScalar;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String fontScalarTipText() {
    return "The font scalar to use.";
  }

  /**
   * Sets the background to use.
   *
   * @param value	the background
   */
  public void setBackground(AbstractBackground value) {
    m_Background = value;
    reset();
  }

  /**
   * Returns the background to use.
   *
   * @return		the background
   */
  public AbstractBackground getBackground() {
    return m_Background;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String backgroundTipText() {
    return "The background to use.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "width", m_Width, "width: ");
    result += QuickInfoHelper.toString(this, "height", m_Height, ", height: ");
    result += QuickInfoHelper.toString(this, "collisionMode", m_CollisionMode, ", collision-mode: ");
    result += QuickInfoHelper.toString(this, "polarBlendMode", m_PolarBlendMode, ", polar-blend-mode: ");
    result += QuickInfoHelper.toString(this, "padding", m_Padding, ", padding: ");
    result += QuickInfoHelper.toString(this, "colorPalette", m_ColorPalette, ", palette: ");
    result += QuickInfoHelper.toString(this, "fontScalar", m_FontScalar, ", font: ");
    result += QuickInfoHelper.toString(this, "background", m_Background, ", background: ");

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{WordFrequency[].class, WordFrequency[][].class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{BufferedImageContainer.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    WordCloud			cloud;
    WordFrequency[] 		freqs1;
    WordFrequency[] 		freqs2;
    WordFrequency[][] 		matrix;
    BufferedImageContainer	cont;
    MessageCollection		errors;
    ColorPalette		palette;
    FontScalar			scalar;
    Background			bg;

    result = null;

    freqs1 = null;
    freqs2 = null;
    if (m_InputToken.hasPayload(WordFrequency[].class)) {
      freqs1 = m_InputToken.getPayload(WordFrequency[].class);
      freqs2 = null;
    }
    else {
      matrix = m_InputToken.getPayload(WordFrequency[][].class);
      if (matrix.length == 2) {
        freqs1 = matrix[0];
        freqs2 = matrix[1];
      }
      else {
        result = "For a polar word cloud, exactly two word frequency arrays are required, found: " + matrix.length;
      }
    }

    if (result == null) {
      if (freqs2 == null)
	cloud = new WordCloud(new Dimension(m_Width, m_Height), m_CollisionMode);
      else
	cloud = new PolarWordCloud(new Dimension(m_Width, m_Height), m_CollisionMode, m_PolarBlendMode);
      cloud.setPadding(m_Padding);

      errors = new MessageCollection();
      palette = m_ColorPalette.generate(errors);
      if (palette != null) {
	cloud.setColorPalette(palette);
      }
      else {
	if (!errors.isEmpty())
	  getLogger().warning("Failed to generate color palette with " + OptionUtils.getCommandLine(m_ColorPalette) + ":\n" + errors);
      }

      errors = new MessageCollection();
      scalar = m_FontScalar.generate(errors);
      if (scalar != null) {
	cloud.setFontScalar(scalar);
      }
      else {
	if (!errors.isEmpty())
	  getLogger().warning("Failed to generate font scalar with " + OptionUtils.getCommandLine(m_FontScalar) + ":\n" + errors);
      }

      errors = new MessageCollection();
      bg = m_Background.generate(errors);
      if (bg != null) {
	cloud.setBackground(bg);
      }
      else {
	if (!errors.isEmpty())
	  getLogger().warning("Failed to generate background with " + OptionUtils.getCommandLine(m_Background) + ":\n" + errors);
      }

      if (freqs2 == null)
	cloud.build(Arrays.asList(freqs1));
      else
	((PolarWordCloud) cloud).build(Arrays.asList(freqs1), Arrays.asList(freqs2));

      cont = new BufferedImageContainer();
      cont.setContent(cloud.getBufferedImage());
      m_OutputToken = new Token(cont);
    }

    return result;
  }
}
