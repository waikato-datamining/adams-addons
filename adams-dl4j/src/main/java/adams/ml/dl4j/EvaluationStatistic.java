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
 * EvaluationStatistic.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */
package adams.ml.dl4j;

import adams.core.EnumWithCustomDisplay;
import adams.core.option.AbstractOption;

/**
 * The enumeration for the comparison field.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public enum EvaluationStatistic
  implements EnumWithCustomDisplay<EvaluationStatistic> {

  ACCURACY("Accuracy", false),
  CLASS_COUNT("Class count", true),
  F1("F1", false),
  F1_CLASS("F1", true),
  FALSE_ALARM_RATE("False alarm rate", false),
  FALSE_NEGATIVE_RATE("False negative rate", false),
  FALSE_NEGATIVE_RATE_CLASS("False negative rate", true),
  FALSE_POSITIVE_RATE("False positive rate", false),
  FALSE_POSITIVE_RATE_CLASS("False positive rate", true),
  PRECISION("Precision", false),
  PRECISION_CLASS("Precision", true),
  RECALL("Recall", false),
  RECALL_CLASS("Recall", true),
  ROW_COUNT("Row count", false);

  
  /** the display value. */
  private String m_Display;

  /** the commandline string. */
  private String m_Raw;

  /** whether the statistic is per class. */
  private boolean m_PerClass;

  /**
   * Initializes the element. Sets {@link #m_PerClass} to false
   *
   * @param display	the display value
   */
  private EvaluationStatistic(String display) {
    this(display, false);
  }

  /**
   * Initializes the element.
   *
   * @param display	the display value
   * @param perClass	whether this element is per class
   */
  private EvaluationStatistic(String display, boolean perClass) {
    m_Display     = display + (perClass ? " (class)" : "");
    m_Raw         = super.toString();
    m_PerClass    = perClass;
  }
  
  /**
   * Returns whether the statistic is a per-class one.
   * 
   * @return		true if per class
   */
  public boolean isPerClass() {
    return m_PerClass;
  }

  /**
   * Returns the display string, including nominal/numeric if it applies.
   *
   * @return		the display string
   */
  public String toDisplay() {
    return m_Display;
  }

  /**
   * Returns the raw enum string.
   *
   * @return		the raw enum string
   */
  public String toRaw() {
    return m_Raw;
  }

  /**
   * Parses the given string and returns the associated enum.
   *
   * @param s		the string to parse
   * @return		the enum or null if not found
   */
  public EvaluationStatistic parse(String s) {
    return (EvaluationStatistic) valueOf((AbstractOption) null, s);
  }

  /**
   * Returns the displays string.
   *
   * @return		the display string
   */
  @Override
  public String toString() {
    return m_Display;
  }

  /**
   * Returns the enum as string.
   *
   * @param option	the current option
   * @param object	the enum object to convert
   * @return		the generated string
   */
  public static String toString(AbstractOption option, Object object) {
    return ((EvaluationStatistic) object).toRaw();
  }

  /**
   * Returns an enum generated from the string.
   *
   * @param option	the current option
   * @param str		the string to convert to an enum
   * @return		the generated enum or null in case of error
   */
  public static EvaluationStatistic valueOf(AbstractOption option, String str) {
    EvaluationStatistic	result;

    result = null;

    // default parsing
    try {
      result = valueOf(str);
    }
    catch (Exception e) {
      // ignored
    }

    // try display
    if (result == null) {
      for (EvaluationStatistic f: values()) {
	if (f.toDisplay().equals(str)) {
	  result = f;
	  break;
	}
      }
    }
    
    return result;
  }
}