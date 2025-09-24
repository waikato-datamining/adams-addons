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
 * AudioAnnotationsFileWriter.java
 * Copyright (C) 2018-2025 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.data.audioannotations.AudioAnnotations;
import adams.data.io.output.DataContainerWriter;
import adams.data.io.output.SimpleAudioAnnotationsWriter;

/**
 <!-- globalinfo-start -->
 * Saves audio annotations to disk with the specified writer and passes the absolute filename on.<br>
 * As filename&#47;directory name (depending on the writer) the ID of the trail is used (below the specified output directory).
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.trail.Trail<br>
 * &nbsp;&nbsp;&nbsp;adams.data.trail.Trail[]<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
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
 * &nbsp;&nbsp;&nbsp;default: TrailFileWriter
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
 * <pre>-writer &lt;adams.data.io.output.DataContainerWriter&gt; (property: writer)
 * &nbsp;&nbsp;&nbsp;The writer to use for saving the data.
 * &nbsp;&nbsp;&nbsp;default: adams.data.io.output.SimpleTrailWriter
 * </pre>
 * 
 * <pre>-dir &lt;adams.core.io.PlaceholderDirectory&gt; (property: outputDir)
 * &nbsp;&nbsp;&nbsp;The output directory for the data.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 * <pre>-file-name-generation &lt;AUTOMATIC|DATABASE_ID|ID|SUPPLIED&gt; (property: fileNameGeneration)
 * &nbsp;&nbsp;&nbsp;Defines how to generate the file name.
 * &nbsp;&nbsp;&nbsp;default: AUTOMATIC
 * </pre>
 * 
 * <pre>-supplied-file-name &lt;java.lang.String&gt; (property: suppliedFileName)
 * &nbsp;&nbsp;&nbsp;The file name (without path) to use when using SUPPLIED (including extension
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default: out.trail
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class AudioAnnotationsFileWriter
  extends AbstractDataContainerFileWriter<AudioAnnotations> {

  /** for serialization. */
  private static final long serialVersionUID = -7990944411836957831L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Saves audio annotations to disk with the "
      + "specified writer and passes the absolute filename on.\n"
      + "As filename/directory name (depending on the writer) the "
      + "ID of the annotations is used (below the specified output directory).";
  }

  /**
   * Returns the default writer to use.
   *
   * @return		the default writer
   */
  @Override
  protected DataContainerWriter<AudioAnnotations> getDefaultWriter() {
    return new SimpleAudioAnnotationsWriter();
  }

  /**
   * Returns the data container class in use.
   *
   * @return		the container class
   */
  @Override
  protected Class getDataContainerClass() {
    return AudioAnnotations.class;
  }
}
