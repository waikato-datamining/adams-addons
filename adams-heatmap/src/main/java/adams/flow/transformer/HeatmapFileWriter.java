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
 * HeatmapFileWriter.java
 * Copyright (C) 2011-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.data.heatmap.Heatmap;
import adams.data.io.output.AbstractDataContainerWriter;
import adams.data.io.output.SpreadSheetHeatmapWriter;

/**
 <!-- globalinfo-start -->
 * Writes heat map files.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;adams.data.heatmap.Heatmap<br/>
 * &nbsp;&nbsp;&nbsp;adams.data.heatmap.Heatmap[]<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br/>
 * <p/>
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
 * &nbsp;&nbsp;&nbsp;default: HeatmapFileWriter
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
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
 * <pre>-writer &lt;adams.data.io.output.AbstractDataContainerWriter&gt; (property: writer)
 * &nbsp;&nbsp;&nbsp;The writer to use for saving the data.
 * &nbsp;&nbsp;&nbsp;default: adams.data.io.output.SpreadSheetHeatmapWriter -writer adams.data.io.output.CsvSpreadSheetWriter
 * </pre>
 * 
 * <pre>-dir &lt;adams.core.io.PlaceholderDirectory&gt; (property: outputDir)
 * &nbsp;&nbsp;&nbsp;The output directory for the data.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class HeatmapFileWriter
  extends AbstractDataContainerFileWriter<Heatmap> {

  /** for serialization. */
  private static final long serialVersionUID = 2418398959018563745L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Writes heat map files.";
  }

  /**
   * Returns the default writer to use.
   *
   * @return		the default writer
   */
  @Override
  protected AbstractDataContainerWriter getDefaultWriter() {
    return new SpreadSheetHeatmapWriter();
  }

  /**
   * Returns the data container class in use.
   *
   * @return		the container class
   */
  @Override
  protected Class getDataContainerClass() {
    return Heatmap.class;
  }
}
