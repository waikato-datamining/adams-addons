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
 * AbstractFileBasedAudioRecorder.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.source.audiorecorder;

import adams.core.QuickInfoHelper;
import adams.core.io.FileWriter;
import adams.core.io.PlaceholderFile;
import adams.flow.core.RunnableWithLogging;

import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;
import java.util.logging.Level;

/**
 * Ancestor for file-based audio recorders.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractFileBasedAudioRecorder
  extends AbstractAudioRecorder<String>
  implements FileWriter {

  private static final long serialVersionUID = 4890769027613827691L;

  /** the output file. */
  protected PlaceholderFile m_OutputFile;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "output-file", "outputFile",
      getDefaultOutputFile());
  }

  /**
   * Returns the default output file.
   *
   * @return		the default
   */
  protected abstract PlaceholderFile getDefaultOutputFile();

  /**
   * Sets the output file.
   *
   * @param value	the file
   */
  public void setOutputFile(PlaceholderFile value) {
    m_OutputFile = value;
    reset();
  }

  /**
   * Returns the output file.
   *
   * @return		the file
   */
  public PlaceholderFile getOutputFile() {
    return m_OutputFile;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public abstract String outputFileTipText();

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String result;

    result = super.getQuickInfo();
    result += QuickInfoHelper.toString(this, "outputFile", m_OutputFile, ", output: ");

    return result;
  }

  /**
   * Returns the type of data that it outputs.
   *
   * @return		the data type
   */
  @Override
  public Class generates() {
    return String.class;
  }

  /**
   * Returns worker runnable for recording audio.
   *
   * @param info 	the line info
   * @param format	the format to record in
   * @param type	the file type
   * @return		the runnable
   */
  protected RunnableWithLogging getRecordingWorker(final DataLine.Info info, final AudioFormat format, final Type type) {
    RunnableWithLogging	result;

    result = new RunnableWithLogging() {
      protected TargetDataLine microphone;
      @Override
      protected void doRun() {
	try {
	  microphone = (TargetDataLine) AudioSystem.getLine(info);
	  microphone.open(format);
	  microphone.start();
	  AudioInputStream ais = new AudioInputStream(microphone);
	  if (isLoggingEnabled())
	    getLogger().info("Recording type " + type + " to: " + m_OutputFile);
	  if (!isStopped() && microphone.isOpen())
	    AudioSystem.write(ais, type, m_OutputFile.getAbsoluteFile());
	}
	catch (Exception e) {
	  getLogger().log(Level.SEVERE, "Failed to record!", e);
	}
      }
      @Override
      public void stopExecution() {
        microphone.stop();
        microphone.close();
	super.stopExecution();
      }
    };
    result.setLoggingLevel(getLoggingLevel());

    return result;
  }

  /**
   * Records the audio.
   *
   * @param output	the file to record to
   * @return		null if successful, otherwise error message
   */
  protected abstract String doRecordTo(PlaceholderFile output);

  /**
   * Records the audio.
   *
   * @return		the generated data
   */
  @Override
  protected String doRecord() {
    String	msg;

    msg = doRecordTo(m_OutputFile);
    if (msg != null)
      throw new IllegalStateException("Failed to record to '" + m_OutputFile + "': " + msg);

    if (isStopped())
      return null;

    return m_OutputFile.getAbsolutePath();
  }
}
