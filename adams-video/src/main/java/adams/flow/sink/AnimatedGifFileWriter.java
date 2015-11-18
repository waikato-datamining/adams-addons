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
 * AnimatedGifFileWriter.java
 * Copyright (C) 2015 FracPete (fracpete at gmail dot com)
 *
 */

package adams.flow.sink;

import adams.core.QuickInfoHelper;
import adams.core.TechnicalInformation;
import adams.core.TechnicalInformation.Field;
import adams.core.TechnicalInformation.Type;
import adams.core.TechnicalInformationHandler;
import adams.core.io.PlaceholderFile;
import adams.data.image.AbstractImageContainer;
import adams.data.video.GifSequenceWriter;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Array;

/**
 <!-- globalinfo-start -->
 * Creates an animated GIF from the incoming images, using Elliot Kroo's GifSequenceWriter class.<br>
 * <br>
 * For more information see:<br>
 * Elliot Kroo. . URL http:&#47;&#47;elliot.kroo.net&#47;software&#47;java&#47;GifSequenceWriter&#47;GifSequenceWriter.java.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.image.AbstractImageContainer[]<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String[]<br>
 * &nbsp;&nbsp;&nbsp;java.io.File[]<br>
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
 * &nbsp;&nbsp;&nbsp;default: AnimatedGifFileWriter
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
 * <pre>-output &lt;adams.core.io.PlaceholderFile&gt; (property: outputFile)
 * &nbsp;&nbsp;&nbsp;The GIF file to create.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 * <pre>-delay &lt;int&gt; (property: delay)
 * &nbsp;&nbsp;&nbsp;The amount of milli-seconds between frames.
 * &nbsp;&nbsp;&nbsp;default: 1000
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-loop &lt;boolean&gt; (property: loop)
 * &nbsp;&nbsp;&nbsp;If enabled, the GIF loops indefinitely.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author fracpete
 */
public class AnimatedGifFileWriter
  extends AbstractFileWriter
  implements TechnicalInformationHandler {

  /** the time between frames in msec. */
  protected int m_Delay;

  /** whether to loop continuously. */
  protected boolean m_Loop;

  @Override
  public String globalInfo() {
    return
      "Creates an animated GIF from the incoming images, using Elliot Kroo's "
        + "GifSequenceWriter class.\n\n"
        + "For more information see:\n"
        + getTechnicalInformation();
  }

  /**
   * Returns an instance of a TechnicalInformation object, containing
   * detailed information about the technical background of this class,
   * e.g., paper reference or book this class is based on.
   *
   * @return the technical information about this class
   */
  @Override
  public TechnicalInformation getTechnicalInformation() {
    TechnicalInformation  result;

    result = new TechnicalInformation(Type.MISC);
    result.setValue(Field.AUTHOR, "Elliot Kroo");
    result.setValue(Field.URL, "http://elliot.kroo.net/software/java/GifSequenceWriter/GifSequenceWriter.java");

    return result;
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "delay", "delay",
      1000, 1, null);

    m_OptionManager.add(
      "loop", "loop",
      false);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    String	value;

    result  = super.getQuickInfo();
    result += QuickInfoHelper.toString(this, "delay", m_Delay, ", delay: ");
    value   = QuickInfoHelper.toString(this, "loop", m_Loop, "loop", ", ");
    if (value != null)
      result += value;

    return result;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String outputFileTipText() {
    return "The GIF file to create.";
  }

  /**
   * Sets the delay in msec between frames.
   *
   * @param value	the amount in msec
   */
  public void setDelay(int value) {
    m_Delay = value;
    reset();
  }

  /**
   * Returns the delay in msec between frames.
   *
   * @return		the amount in msec
   */
  public int getDelay() {
    return m_Delay;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String delayTipText() {
    return "The amount of milli-seconds between frames.";
  }

  /**
   * Sets whether to loop indefinitely.
   *
   * @param value	true if to loop
   */
  public void setLoop(boolean value) {
    m_Loop = value;
    reset();
  }

  /**
   * Returns whether to loop indefinitely.
   *
   * @return		true if to loop
   */
  public boolean getLoop() {
    return m_Loop;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String loopTipText() {
    return "If enabled, the GIF loops indefinitely.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{AbstractImageContainer[].class, String[].class, File[].class};
  }

  /**
   * Returns the specified image from the input.
   *
   * @param obj		the object (= array) to get the image from
   * @param index	the 0-based index of the image to retrieve
   * @return		the image, null if failed to retrieve
   * @throws Exception  if retrieval fails, e.g., file not found
   */
  protected BufferedImage getImage(Object obj, int index) throws Exception {
    BufferedImage     result;
    Object            item;

    result = null;
    item   = Array.get(obj, index);

    if (item instanceof AbstractImageContainer)
      result = ((AbstractImageContainer) item).toBufferedImage();
    else if (item instanceof String)
      result = ImageIO.read(new PlaceholderFile((String) item).getAbsoluteFile());
    else if (item instanceof File)
      result = ImageIO.read(((File) item).getAbsoluteFile());

    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String                result;
    GifSequenceWriter     writer;
    FileImageOutputStream output;
    int                   num;
    BufferedImage         img;
    int                   i;

    result = null;
    output = null;
    writer = null;
    num    = Array.getLength(m_InputToken.getPayload());
    try {
      img    = getImage(m_InputToken.getPayload(), 0);
      output = new FileImageOutputStream(m_OutputFile.getAbsoluteFile());
      writer = new GifSequenceWriter(output, img.getType(), m_Delay, m_Loop);
      writer.writeToSequence(img);
      for (i = 1; i < num; i++)
        writer.writeToSequence(getImage(m_InputToken.getPayload(), i));
    }
    catch (Exception e) {
      result = handleException("Failed to create GIF!", e);
    }
    finally {
      if (writer != null) {
        try {
          writer.close();
        }
        catch (Exception e) {
          // ignored
        }
      }
      if (output != null) {
        try {
          output.flush();
          output.close();
        }
        catch (Exception e) {
          // ignored
        }
      }
    }

    return result;
  }
}
