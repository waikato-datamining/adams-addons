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
 * TrailFileReader.java
 * Copyright (C) 2015-2025 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.data.io.input.DataContainerReader;
import adams.data.io.input.SimpleTrailReader;
import adams.data.trail.Trail;

/**
 <!-- globalinfo-start -->
 * Loads a file containing a trail from disk with the specified reader and passes it on.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.trail.Trail<br>
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
 * &nbsp;&nbsp;&nbsp;default: TrailFileReader
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
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-reader &lt;adams.data.io.input.DataContainerReader&gt; (property: reader)
 * &nbsp;&nbsp;&nbsp;The reader to use for importing the data.
 * &nbsp;&nbsp;&nbsp;default: adams.data.io.input.SimpleTrailReader
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class TrailFileReader
  extends AbstractDataContainerFileReader<Trail> {

  /** for serialization. */
  private static final long serialVersionUID = 1429977151568224156L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Loads a file containing a trail from disk with the specified reader and passes it on.";
  }

  /**
   * Returns the default reader to use.
   *
   * @return		the default reader
   */
  @Override
  protected DataContainerReader getDefaultReader() {
    return new SimpleTrailReader();
  }

  /**
   * Returns the base class of the items.
   *
   * @return		the class
   */
  @Override
  protected Class getItemClass() {
    return Trail.class;
  }
}
