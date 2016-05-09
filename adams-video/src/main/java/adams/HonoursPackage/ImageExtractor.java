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
 * ImageExtractor.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.HonoursPackage;

import adams.core.base.BaseTimeMsec;
import adams.core.io.PlaceholderFile;
import adams.data.image.BufferedImageContainer;
import adams.env.Environment;
import adams.flow.transformer.movieimagesampler.TimestampMovieSampler;

import javax.imageio.ImageIO;
import java.io.File;

/**
 * Extract images given a list of timestamps
 *
 * @author sjb90
 * @version $Revision$
 */
public class ImageExtractor {

  private BaseTimeMsec[] 	m_Timestamps;
  private TimestampMovieSampler m_Sampler;
  private PlaceholderFile	m_CurrentFile;


  public BaseTimeMsec[] getTimestamps() {
    return m_Timestamps;
  }

  public void setimestamps(BaseTimeMsec[] timestamps) {
    m_Timestamps = timestamps;
  }

  public void setFile(PlaceholderFile file) {
    m_CurrentFile = file;
  }

  public PlaceholderFile getFile() {
    return m_CurrentFile;
  }

  public ImageExtractor() {
    m_Sampler = new TimestampMovieSampler();
  }

  public BufferedImageContainer[] extract() {
    m_Sampler.setTimeStamps(m_Timestamps);
    return m_Sampler.sample(m_CurrentFile);
  }

  /**
   * Main method for running as standalone
   * @param args
   * @throws Exception
   */
  public static void main(String[] args) throws Exception {
    Environment.setEnvironmentClass(Environment.class);
    ImageExtractor extractor = new ImageExtractor();
    extractor.setFile(new PlaceholderFile(args[0]));
    BaseTimeMsec[] ts = new BaseTimeMsec[2];
    for (int i = 0; i < ts.length; i++) {
      ts[i] = new BaseTimeMsec("00:0" + i +":00");
    }
    extractor.setimestamps(ts);
    BufferedImageContainer[] images = extractor.extract();
    try {
      for (int i = 0; i < images.length; i++) {
        ImageIO.write(images[i].getImage(), "png", new File("/home/sjb90/Pictures/testImage" + i + ".png"));
      }
    }
    catch (Exception e) {

    }
  }


}
