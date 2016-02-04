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
 * VideoUtilities.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.video.vlcjplayer;

import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;

import java.awt.*;

/**
 * A utility class that contains helpful methods for working with videos
 *
 * @author sjb90
 * @version $Revision$
 */
public class VideoUtilities {

  /**
   * Retrieves the dimensions of a video contained in the given file
   * @param fileName the file to use
   * @return
   */
  public static Dimension getVideoDimensions(String fileName) {
    Dimension result;
    result = new Dimension();
    IContainer container = IContainer.make();
    if(container.open(fileName, IContainer.Type.READ, null) < 0)
      return result;
    int max = container.getNumStreams();
    if(max == 0)
      return result;
    IStream stream;
    IStreamCoder coder;
    result.setSize(0,0);
    for (int i = 0; i < max; i++) {
      stream = container.getStream(i);
      coder = stream.getStreamCoder();
      if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO) {
	if (coder.getWidth() > result.getWidth() || coder.getHeight() > result.getHeight()) {
	  result.setSize(coder.getWidth(),coder.getHeight());
	}
      }

    }
    return result;
  }
}
