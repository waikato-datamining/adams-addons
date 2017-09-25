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
 * XMLGregorianCalendarImplPublic.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package org.apache.xerces.jaxp.datatype;

import javax.xml.datatype.DatatypeConstants;
import java.util.GregorianCalendar;

/**
 * Apache class is package protected, this derived class makes it public.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class XMLGregorianCalendarImplPublic
  extends XMLGregorianCalendarImpl {

  private static final long serialVersionUID = -4559981996070460268L;

  /**
   * <p>Create an instance with all date/time datatype fields set to
   * {@link DatatypeConstants#FIELD_UNDEFINED} or null respectively.</p>
   */
  public XMLGregorianCalendarImplPublic() {
    super();
  }

  /**
   * <p>Convert a <code>java.util.GregorianCalendar</code> to XML Schema 1.0
   * representation.</p>
   *
   * <table border="2" rules="all" cellpadding="2">
   *   <thead>
   *     <tr>
   *       <th align="center" colspan="2">
   *          Field by Field Conversion from
   *          <code>java.util.GregorianCalendar</code> to this class
   *       </th>
   *     </tr>
   *   </thead>
   *   <tbody>
   *     <tr>
   *        <th><code>javax.xml.datatype.XMLGregorianCalendar</code> field</th>
   *        <th><code>java.util.GregorianCalendar</code> field</th>
   *     </tr>
   *     <tr>
   *       <th>{@link #setYear(int)}</th>
   *       <th><code>ERA == GregorianCalendar.BC ? -YEAR : YEAR</code></th>
   *     </tr>
   *     <tr>
   *       <th>{@link #setMonth(int)}</th>
   *       <th><code>MONTH + 1</code></th>
   *     </tr>
   *     <tr>
   *       <th>{@link #setDay(int)}</th>
   *       <th><code>DAY_OF_MONTH</code></th>
   *     </tr>
   *     <tr>
   *       <th>{@link #setTime(int,int,int, BigDecimal)}</th>
   *       <th><code>HOUR_OF_DAY, MINUTE, SECOND, MILLISECOND</code></th>
   *     </tr>
   *     <tr>
   *       <th>{@link #setTimezone(int)}<i>*</i></th>
   *       <th><code>(ZONE_OFFSET + DST_OFFSET) / (60*1000)</code><br/>
   *       <i>(in minutes)</i>
   *       </th>
   *     </tr>
   *   </tbody>
   * </table>
   * <p><i>*</i>conversion loss of information. It is not possible to represent
   * a <code>java.util.GregorianCalendar</code> daylight savings timezone id in the
   * XML Schema 1.0 date/time datatype representation.</p>
   *
   * <p>To compute the return value's <code>TimeZone</code> field,
   * <ul>
   * <li>when <code>this.getTimezone() != DatatypeConstants.FIELD_UNDEFINED</code>,
   * create a <code>java.util.TimeZone</code> with a custom timezone id
   * using the <code>this.getTimezone()</code>.</li>
   * <li>else use the <code>GregorianCalendar</code> default timezone value
   * for the host is defined as specified by
   * <code>java.util.TimeZone.getDefault()</code>.</li></p>
   *
   * @param cal <code>java.util.GregorianCalendar</code> used to create <code>XMLGregorianCalendar</code>
   */
  public XMLGregorianCalendarImplPublic(GregorianCalendar cal) {
    super(cal);
  }
}
