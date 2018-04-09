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
 * Wave.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.audioinfo;

import adams.core.Utils;
import adams.core.base.BaseURL;
import adams.core.io.FileUtils;
import com.musicg.wave.WaveHeader;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Outputs information on the incoming Wave data.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Wave
  extends AbstractAudioInfoReader {

  private static final long serialVersionUID = 568010617982277726L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Outputs information on the incoming Wave data.";
  }

  /**
   * The accepted input types.
   *
   * @return		the input types
   */
  @Override
  public Class[] accepts() {
    return new Class[]{String.class, URL.class, BaseURL.class, com.musicg.wave.Wave.class};
  }

  /**
   * Reads the info from the input.
   *
   * @param input	the input data
   * @return		the generated info
   * @throws Exception	if reading fails
   */
  @Override
  protected Map<String, Object> doRead(Object input) throws Exception {
    Map<String, Object>		result;
    com.musicg.wave.Wave 	wave;
    WaveHeader			header;
    InputStream 		is;

    is   = null;
    wave = null;
    if (input instanceof String)
      is = new BufferedInputStream(new FileInputStream((String) input));
    else if (input instanceof URL)
      is = new BufferedInputStream(((URL) input).openStream());
    else if (input instanceof BaseURL)
      is = new BufferedInputStream(((BaseURL) input).urlValue().openStream());
    else if (input instanceof com.musicg.wave.Wave)
      wave = (com.musicg.wave.Wave) input;
    if (is != null)
      wave = new com.musicg.wave.Wave(is);

    if (wave == null)
      throw new IllegalStateException("Unhandled input data: " + Utils.classToString(input));

    result = new HashMap<>();
    header = wave.getWaveHeader();
    result.put("chunkId", header.getChunkId());
    result.put("chunkSize", header.getChunkSize());
    result.put("format", header.getFormat());
    result.put("subChunk1Id", header.getSubChunk1Id());
    result.put("subChunk1Size", header.getSubChunk1Size());
    result.put("audioFormat", header.getAudioFormat());
    result.put("channels", header.getChannels());
    result.put("sampleRate", header.getSampleRate());
    result.put("byteRate", header.getByteRate());
    result.put("blockAlign", header.getBlockAlign());
    result.put("bitsPerSample", header.getBitsPerSample());
    result.put("subChunk2Id", header.getSubChunk2Id());
    result.put("subChunk2Size", header.getSubChunk2Size());

    if (is != null)
      FileUtils.closeQuietly(is);

    return result;
  }
}
