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
 * RObjectInspector.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package adams.core;

import adams.core.io.FileUtils;
import adams.core.logging.Logger;
import adams.core.logging.LoggingHelper;
import org.renjin.serialization.RDataReader;
import org.renjin.sexp.PairList;
import org.renjin.sexp.PairList.Node;
import org.renjin.sexp.SEXP;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.zip.GZIPInputStream;

/**
 * Helper class for inspection R objects (SEXP).
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class RObjectInspector {

  /** for logging errors etc. */
  protected static Logger LOGGER;

  /**
   * Returns the logger instance to use.
   *
   * @return		the logger
   */
  protected static synchronized Logger getLogger() {
    if (LOGGER == null)
      LOGGER = LoggingHelper.getLogger(RObjectInspector.class);
    return LOGGER;
  }

  /**
   * Returns the paths for all objects in the provided object.
   *
   * @param parent 	the parent path, use null for root
   * @param robj	the R object to inspect
   * @return		the object paths
   */
  protected static List<RObjectPath> list(RObjectPath parent, SEXP robj) {
    List<RObjectPath> 	result;
    PairList		pairlist;

    result = new ArrayList<>();

    if (robj instanceof PairList) {
      try {
	pairlist = (PairList) robj;
	for (Node node : pairlist.nodes()) {
	  if (parent == null)
	    result.add(new RObjectPath(new String[]{node.getName()}));
	  else
	    result.add(parent.addChild(node.getName()));
	  if (node.getValue() instanceof PairList)
	    result.addAll(list(result.get(result.size() - 1), node.getValue()));
	}
      }
      catch (Exception e) {
	getLogger().log(Level.SEVERE, "Failed to list object paths!", e);
      }
    }

    return result;
  }

  /**
   * Returns the paths for all objects in the provided object.
   *
   * @param robj	the R object to inspect
   * @return		the object paths
   */
  public static List<RObjectPath> list(SEXP robj) {
    return list(null, robj);
  }

  /**
   * Returns the object associated with the path.
   *
   * @param robj	the R object to extract the object from
   * @param path 	the path for the object to extract
   * @return		the object, null if failed to locate
   */
  public static SEXP get(SEXP robj, RObjectPath path) {
    SEXP	result;
    SEXP	current;
    PairList	pairlist;
    int		i;

    result  = null;
    current = robj;

    i = 0;
    while (i < path.getPathCount()) {
      if (current instanceof PairList) {
        pairlist = (PairList) current;
        for (Node node: pairlist.nodes()) {
          if (node.getName().equals(path.getPathComponent(i))) {
            current = node.getValue();
            if (i == path.getPathCount() - 1)
              result = current;
            i++;
            break;
	  }
	}
      }
      else {
        getLogger().warning("Failed to locate R object: " + path);
        return null;
      }
    }

    return result;
  }

  // For testing only
  public static void main(String[] args) throws Exception {
    ScriptEngineManager manager = new ScriptEngineManager();
    ScriptEngine engine = manager.getEngineByName("Renjin");
    if(engine == null)
      throw new RuntimeException("Some error msg");

    FileInputStream in = new FileInputStream("/home/fracpete/temp/spectral/rdata/Data.WHEAT1.Rdata");
    GZIPInputStream zin = new GZIPInputStream(in);
    RDataReader reader = new RDataReader(zin);
    SEXP robj = reader.readFile();
    List<RObjectPath> paths = list(robj);
    for (RObjectPath path: paths) {
      System.out.println("\n--> " + path);
      System.out.println(get(robj, path));
    }
    reader.close();
    FileUtils.closeQuietly(zin);
    FileUtils.closeQuietly(in);
  }
}
