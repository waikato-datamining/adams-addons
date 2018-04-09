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
 * MP3.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.audioinfo;

import adams.core.License;
import adams.core.Utils;
import adams.core.annotation.MixedCopyright;
import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Outputs information on the incoming Wave data.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
@MixedCopyright(
  author = "Michael Patricios",
  url = "https://raw.githubusercontent.com/mpatric/mp3agic-examples/master/src/main/java/com/mpatric/mp3agic/app/Mp3Details.java",
  license = License.MIT
)
public class MP3
  extends AbstractAudioInfoReader {

  private static final long serialVersionUID = 568010617982277726L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Outputs information on the incoming MP3 file.";
  }

  /**
   * The accepted input types.
   *
   * @return		the input types
   */
  @Override
  public Class[] accepts() {
    return new Class[]{String.class, Mp3File.class};
  }

  protected String formatTime(long seconds) {
    return Long.toString(seconds / 60) + ":" + String.format("%02d", seconds % 60);
  }

  protected Map<String,Object> getMp3Fields(Mp3File mp3file) {
    Map<String,Object> result;

    result = new HashMap<>();
    showField(result, "Filename", new File(mp3file.getFilename()).getName());
    showField(result, "Size", Long.toString(mp3file.getLength()));
    showField(result, "Length", formatTime(mp3file.getLengthInSeconds()));
    showField(result, "Version", mp3file.getVersion());
    showField(result, "Layer", mp3file.getLayer());
    showField(result, "Sample rate", Integer.toString(mp3file.getSampleRate()), "Hz");
    showField(result, "Bitrate", Integer.toString(mp3file.getBitrate()), "kbps (" + vbrString(mp3file.isVbr()) + ")");
    showField(result, "Channel mode", mp3file.getChannelMode());

    return result;
  }

  protected String vbrString(boolean vbr) {
    if (vbr)
      return "VBR";
    else
      return "CBR";
  }

  protected Map<String,Object> getId3v1Fields(Mp3File mp3file) {
    Map<String,Object> result;

    result = new HashMap<>();
    ID3v1 id3v1tag = mp3file.getId3v1Tag();
    if (id3v1tag != null) {
      showField(result, "Track", id3v1tag.getTrack());
      showField(result, "Artist", id3v1tag.getArtist());
      showField(result, "Title", id3v1tag.getTitle());
      showField(result, "Album", id3v1tag.getAlbum());
      showField(result, "Year", id3v1tag.getYear());
      showField(result, "Genre", id3v1tag.getGenreDescription());
      showField(result, "Comment", id3v1tag.getComment());
    }
    
    return result;
  }

  protected Map<String,Object> getId3v2Fields(Mp3File mp3file) {
    Map<String,Object> result;

    result = new HashMap<>();
    ID3v2 id3v2tag = mp3file.getId3v2Tag();
    if (id3v2tag != null) {
      showField(result, "Track", id3v2tag.getTrack());
      showField(result, "Artist", id3v2tag.getArtist());
      showField(result, "Album", id3v2tag.getAlbum());
      showField(result, "Title", id3v2tag.getTitle());
      showField(result, "Year", id3v2tag.getYear());
      showField(result, "Genre", id3v2tag.getGenreDescription());
      showField(result, "Comment", id3v2tag.getComment());
      showField(result, "Composer", id3v2tag.getComposer());
      showField(result, "Original Artist", id3v2tag.getOriginalArtist());
      showField(result, "Copyright", id3v2tag.getCopyright());
      showField(result, "Url", id3v2tag.getUrl());
      showField(result, "Encoder", id3v2tag.getEncoder());
      showField(result, "Album Image", id3v2tag.getAlbumImageMimeType());
    }
    
    return result;
  }

  protected void showField(Map<String,Object> info, String name, String field) {
    showField(info, name, field, false, null);
  }

  protected void showField(Map<String,Object> info, String name, String field, String units) {
    showField(info, name, field, false, units);
  }

  protected void showField(Map<String,Object> info, String name, String field, boolean last, String units) {
    info.put(name, field + (units != null ? units : ""));
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
    Map<String, Object> 	result;
    Mp3File 			mp3;

    if (input instanceof String)
      mp3 = new Mp3File((String) input);
    else if (input instanceof Mp3File)
      mp3 = (Mp3File) input;
    else
      throw new IllegalStateException("Unhandled input data: " + Utils.classToString(input));

    result = new HashMap<>();
    result.putAll(getMp3Fields(mp3));
    result.putAll(getId3v1Fields(mp3));
    result.putAll(getId3v2Fields(mp3));

    return result;
  }
}
