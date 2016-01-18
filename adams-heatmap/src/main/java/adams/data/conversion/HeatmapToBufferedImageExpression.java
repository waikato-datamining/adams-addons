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
 * HeatmapToBufferedImageExpression.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import adams.core.QuickInfoHelper;
import adams.core.base.BaseString;
import adams.data.heatmap.Heatmap;
import adams.data.image.AbstractImageContainer;
import adams.data.image.BufferedImageContainer;
import adams.gui.visualization.core.AbstractColorGradientGenerator;
import adams.gui.visualization.core.BiColorGenerator;
import adams.parser.GrammarSupplier;
import adams.parser.MathematicalExpression;
import adams.parser.MathematicalExpressionText;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 <!-- globalinfo-start -->
 * Turns a heatmap into a BufferedImage, using the provided expression for generating greyscale values (0-255).<br>
 * <br>
 * The following grammar is used:<br>
 * <br>
 * expr_list ::= '=' expr_list expr_part | expr_part ;<br>
 * expr_part ::=  expr ;<br>
 * <br>
 * expr      ::=   ( expr )<br>
 * <br>
 * # data types<br>
 *               | number<br>
 *               | string<br>
 *               | boolean<br>
 *               | date<br>
 * <br>
 * # constants<br>
 *               | true<br>
 *               | false<br>
 *               | pi<br>
 *               | e<br>
 *               | now()<br>
 *               | today()<br>
 * <br>
 * # negating numeric value<br>
 *               | -expr<br>
 * <br>
 * # comparisons<br>
 *               | expr &lt; expr<br>
 *               | expr &lt;= expr<br>
 *               | expr &gt; expr<br>
 *               | expr &gt;= expr<br>
 *               | expr = expr<br>
 *               | expr != expr (or: expr &lt;&gt; expr)<br>
 * <br>
 * # boolean operations<br>
 *               | ! expr (or: not expr)<br>
 *               | expr &amp; expr (or: expr and expr)<br>
 *               | expr | expr (or: expr or expr)<br>
 *               | if[else] ( expr , expr (if true) , expr (if false) )<br>
 *               | ifmissing ( variable , expr (default value if variable is missing) )<br>
 *               | isNaN ( expr )<br>
 * <br>
 * # arithmetics<br>
 *               | expr + expr<br>
 *               | expr - expr<br>
 *               | expr * expr<br>
 *               | expr &#47; expr<br>
 *               | expr ^ expr (power of)<br>
 *               | expr % expr (modulo)<br>
 *               ;<br>
 * <br>
 * # numeric functions<br>
 *               | abs ( expr )<br>
 *               | sqrt ( expr )<br>
 *               | cbrt ( expr )<br>
 *               | log ( expr )<br>
 *               | log10 ( expr )<br>
 *               | exp ( expr )<br>
 *               | sin ( expr )<br>
 *               | sinh ( expr )<br>
 *               | cos ( expr )<br>
 *               | cosh ( expr )<br>
 *               | tan ( expr )<br>
 *               | tanh ( expr )<br>
 *               | atan ( expr )<br>
 *               | atan2 ( exprY , exprX )<br>
 *               | hypot ( exprX , exprY )<br>
 *               | signum ( expr )<br>
 *               | rint ( expr )<br>
 *               | floor ( expr )<br>
 *               | pow[er] ( expr , expr )<br>
 *               | ceil ( expr )<br>
 *               | min ( expr1 , expr2 )<br>
 *               | max ( expr1 , expr2 )<br>
 *               | year ( expr )<br>
 *               | month ( expr )<br>
 *               | day ( expr )<br>
 *               | hour ( expr )<br>
 *               | minute ( expr )<br>
 *               | second ( expr )<br>
 *               | weekday ( expr )<br>
 *               | weeknum ( expr )<br>
 * <br>
 * # string functions<br>
 *               | substr ( expr , start [, end] )<br>
 *               | left ( expr , len )<br>
 *               | mid ( expr , start , len )<br>
 *               | right ( expr , len )<br>
 *               | rept ( expr , count )<br>
 *               | concatenate ( expr1 , expr2 [, expr3-5] )<br>
 *               | lower[case] ( expr )<br>
 *               | upper[case] ( expr )<br>
 *               | trim ( expr )<br>
 *               | matches ( expr , regexp )<br>
 *               | trim ( expr )<br>
 *               | len[gth] ( str )<br>
 *               | find ( search , expr [, pos] )<br>
 *               | replace ( str , pos , len , newstr )<br>
 *               | substitute ( str , find , replace [, occurrences] )<br>
 *               ;<br>
 * <br>
 * Notes:<br>
 * - Variables are either all upper case letters (e.g., "ABC") or any character   apart from "]" enclosed by "[" and "]" (e.g., "[Hello World]").<br>
 * - 'start' and 'end' for function 'substr' are indices that start at 1.<br>
 * - Index 'end' for function 'substr' is excluded (like Java's 'String.substring(int,int)' method)<br>
 * - Line comments start with '#'.<br>
 * - Semi-colons (';') or commas (',') can be used as separator in the formulas,<br>
 *   e.g., 'pow(2,2)' is equivalent to 'pow(2;2)'<br>
 * - dates have to be of format 'yyyy-MM-dd' or 'yyyy-MM-dd HH:mm:ss'<br>
 * - times have to be of format 'HH:mm:ss' or 'yyyy-MM-dd HH:mm:ss'<br>
 * - the characters in square brackets in function names are optional:<br>
 *   e.g. 'len("abc")' is the same as 'length("abc")'<br>
 * <br>
 * A lot of the functions have been modeled after LibreOffice:<br>
 *   https:&#47;&#47;help.libreoffice.org&#47;Calc&#47;Functions_by_Category<br>
 * <br>
 * Additional functions:<br>
 * - env(String): String<br>
 * &nbsp;&nbsp;&nbsp;First argument is the name of the environment variable to retrieve.<br>
 * &nbsp;&nbsp;&nbsp;The result is the value of the environment variable.<br>
 * <br>
 * Additional procedures:<br>
 * - println(...)<br>
 * &nbsp;&nbsp;&nbsp;One or more arguments are printed as comma-separated list to stdout.<br>
 * &nbsp;&nbsp;&nbsp;If no argument is provided, a simple line feed is output.<br>
 * <br><br>
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
 * <pre>-missing-value-color &lt;java.awt.Color&gt; (property: missingValueColor)
 * &nbsp;&nbsp;&nbsp;The color to use for missing values.
 * &nbsp;&nbsp;&nbsp;default: #00ffffff
 * </pre>
 * 
 * <pre>-expression &lt;adams.parser.MathematicalExpressionText&gt; (property: expression)
 * &nbsp;&nbsp;&nbsp;The expression to use for generating the greyscale values.
 * &nbsp;&nbsp;&nbsp;default: (X - 273.15) &#47; 50
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class HeatmapToBufferedImageExpression
  extends AbstractConversion
  implements GrammarSupplier {

  /** for serialization. */
  private static final long serialVersionUID = 2535421741524997185L;

  /** the generator to use. */
  protected AbstractColorGradientGenerator m_Generator;

  /** the color for missing values. */
  protected Color m_MissingValueColor;

  /** the math expression. */
  protected MathematicalExpressionText m_Expression;

  /** the gradient colors. */
  protected Color[] m_GradientColors;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Turns a heatmap into a BufferedImage, using the provided expression "
        + "for generating greyscale values (0-255).\n\n"
        + "The following grammar is used:\n\n"
        + getGrammar();
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
      "missing-value-color", "missingValueColor",
      new Color(255, 255, 255, 0));

    m_OptionManager.add(
      "expression", "expression",
      new MathematicalExpressionText("(X - 273.15) / 50"));
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
   * Returns a string representation of the grammar.
   *
   * @return		the grammar, null if not available
   */
  public String getGrammar() {
    return new MathematicalExpression().getGrammar();
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "expression", m_Expression.getValue());
  }

  /**
   * Sets the color generator.
   *
   * @param value	the generator
   */
  public void setGenerator(AbstractColorGradientGenerator value) {
    m_Generator = value;
    reset();
  }

  /**
   * Returns the color generator.
   *
   * @return		the generator
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
   * Sets the color for missing values.
   *
   * @param value	the color
   */
  public void setMissingValueColor(Color value) {
    m_MissingValueColor = value;
    reset();
  }

  /**
   * Returns the color for missing values.
   *
   * @return		the color
   */
  public Color getMissingValueColor() {
    return m_MissingValueColor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String missingValueColorTipText() {
    return "The color to use for missing values.";
  }

  /**
   * Sets the mathematical expression to use for generating the greyscale values.
   *
   * @param value	the expression
   */
  public void setExpression(MathematicalExpressionText value) {
    m_Expression = value;
    reset();
  }

  /**
   * Returns the mathematical expression to use for generating the greyscale values.
   *
   * @return		the expression
   */
  public MathematicalExpressionText getExpression() {
    return m_Expression;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String expressionTipText() {
    return "The expression to use for generating the greyscale values.";
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return		the class
   */
  @Override
  public Class accepts() {
    return Heatmap.class;
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return		the class
   */
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
    if (m_GradientColors == null)
      m_GradientColors = m_Generator.generate();

    return m_GradientColors;
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    BufferedImageContainer	result;
    BufferedImage		image;
    Color[]			colors;
    Heatmap			map;
    int				x;
    int				y;
    Graphics2D			g;
    Color			color;
    double			val;
    MathematicalExpression	expr;

    map    = (Heatmap) m_Input;
    colors = getGradientColors();
    image  = new BufferedImage(map.getWidth(), map.getHeight(), BufferedImage.TYPE_INT_RGB);
    g      = image.createGraphics();
    expr   = new MathematicalExpression();
    expr.setExpression(m_Expression.getValue());
    for (y = 0; y < map.getHeight(); y++) {
      for (x = 0; x < map.getWidth(); x++) {
	if (map.isMissing(y, x)) {
	  color = m_MissingValueColor;
	}
	else {
	  expr.setSymbols(new BaseString[]{new BaseString("X=" + map.get(y, x))});
	  val   = expr.evaluate();
	  color = colors[(int) (val * (colors.length - 2)) + 1];
	}
	g.setColor(color);
	g.drawLine(x, y, x, y);
      }
    }

    result = new BufferedImageContainer();
    result.setImage(image);
    
    return result;
  }
}
