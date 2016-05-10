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
 * Blah.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.HonoursPackage;

import adams.core.io.PlaceholderFile;
import adams.data.io.input.SimpleTrailReader;
import adams.data.io.output.SimpleTrailWriter;
import adams.data.trail.Step;
import adams.data.trail.Trail;
import adams.env.Environment;

/**
 * Takes two trail files and combines them into one
 *
 * @author steven
 * @version $Revision$
 */
public class Test {
  public static void main(String[] args) throws Exception {
    Environment.setEnvironmentClass(Environment.class);
    // read in trail files
    Trail trackingTrail;
    Trail annotationTrail;
    Trail result = new Trail();
    SimpleTrailReader tReader = new SimpleTrailReader();
    tReader.setInput(new PlaceholderFile(args[0]));
    trackingTrail = tReader.read().get(0);
    tReader.setInput(new PlaceholderFile(args[1]));
    annotationTrail = tReader.read().get(0);
    // Combine trail files
    result.addAll(annotationTrail);
    for(Step s : trackingTrail) {
      Step match = result.getStep(s.getTimestamp());
      if(match != null) {
	if(match.hasMetaData()) {
	  s.getMetaData().putAll(match.getMetaData());
	}
      }
	result.add(s);
    }
    //result.addAll(annotationTrail);
    SimpleTrailWriter tWriter = new SimpleTrailWriter();
    // Write out the combined trails
    tWriter.setOutput(new PlaceholderFile(args[2]));
    tWriter.write(result);
    for(Step s : result) {
      System.out.println(s.getTimestamp().toString());
    }

  }
}
