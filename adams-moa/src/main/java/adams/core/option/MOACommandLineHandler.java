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
 * MOACommandLineHandler.java
 * Copyright (C) 2012-2024 University of Waikato, Hamilton, New Zealand
 */
package adams.core.option;

import adams.core.MessageCollection;
import adams.core.Utils;
import moa.MOAObject;
import nz.ac.waikato.cms.locator.ClassLocator;
import weka.core.MOAUtils;

import java.util.logging.Level;

/**
 * Handles objects of classes that implement the weka.core.OptionHandler
 * interface.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @see weka.core.OptionHandler
 */
public class MOACommandLineHandler
  extends AbstractCommandLineHandler {

  /** for serialization. */
  private static final long serialVersionUID = -5233496867185402778L;

  /**
   * Generates an object from the specified commandline.
   *
   * @param cmd		the commandline to create the object from
   * @param errors 	for recording errors
   * @return		the created object, null in case of error
   */
  @Override
  public Object fromCommandLine(String cmd, MessageCollection errors) {
    Object	result;

    try {
      result = MOAUtils.fromCommandLine(Object.class, cmd);
    }
    catch (Exception e) {
      errors.add("Failed to process commandline '" + cmd + "':", e);
      getLogger().log(Level.SEVERE, "Failed to process commandline '" + cmd + "':", e);
      result = null;
    }

    return result;
  }

  /**
   * Generates an object from the commandline options.
   *
   * @param args	the commandline options to create the object from
   * @param errors 	for recording errors
   * @return		the created object, null in case of error
   */
  @Override
  public Object fromArray(String[] args, MessageCollection errors) {
    Object	result;

    result = MOAUtils.fromCommandLine(MOAObject.class, joinOptions(args));
    if (result == null)
      errors.add("Failed to process array (fromArray): " + Utils.arrayToString(args));

    return result;
  }

  /**
   * Generates a commandline from the specified object.
   *
   * @param obj		the object to create the commandline for
   * @return		the generated commandline
   */
  @Override
  public String toCommandLine(Object obj) {
    return MOAUtils.toCommandLine((MOAObject) obj);
  }

  /**
   * Generates a commandline from the specified object. Uses a shortened
   * format, e.g., removing the package from the class.
   *
   * @param obj		the object to create the commandline for
   * @return		the generated commandline
   */
  @Override
  public String toShortCommandLine(Object obj) {
    String	result;
    
    result = toCommandLine(obj);
    result = result.substring(obj.getClass().getPackage().getName().length() + 1);
    
    return result;
  }

  /**
   * Generates an options array from the specified object.
   *
   * @param obj		the object to create the array for
   * @return		the generated array
   */
  @Override
  public String[] toArray(Object obj) {
    return splitOptions(toCommandLine(obj));
  }

  /**
   * Returns the commandline options (without classname) of the specified object.
   *
   * @param obj		the object to get the options from
   * @return		the options
   */
  @Override
  public String[] getOptions(Object obj) {
    String[]	result;
    String[]	all;
    
    all = toArray(obj);
    if (all.length > 1) {
      result = new String[all.length - 1];
      System.arraycopy(all, 1, result, 0, all.length - 1);
    }
    else {
      result = new String[0];
    }
    
    return result;
  }

  /**
   * Sets the options of the specified object.
   *
   * @param obj		the object to set the options for
   * @param args	the options
   * @return		true if options successfully set
   */
  @Override
  public boolean setOptions(Object obj, String[] args) {
    moa.options.OptionHandler	handler;
    
    if (obj instanceof moa.options.OptionHandler) {
      handler = (moa.options.OptionHandler) obj;
      handler.getOptions().setViaCLIString(joinOptions(args));
      return true;
    }
    
    return false;
  }

  /**
   * Splits the commandline into an array.
   *
   * @param cmdline	the commandline to split
   * @return		the generated array of options
   */
  @Override
  public String[] splitOptions(String cmdline) {
    String[]	result;

    try {
      result = OptionUtils.splitOptions(cmdline);
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to split options (splitOptions):", e);
      result = new String[0];
    }

    return result;
  }

  /**
   * Turns the option array back into a commandline.
   *
   * @param args	the options to turn into a commandline
   * @return		the generated commandline
   */
  @Override
  public String joinOptions(String[] args) {
    return OptionUtils.joinOptions(args);
  }

  /**
   * Checks whether the given class can be processed.
   *
   * @param cls		the class to inspect
   * @return		true if the handler can process the class
   */
  @Override
  public boolean handles(Class cls) {
    return (ClassLocator.hasInterface(moa.options.OptionHandler.class, cls));
  }
}
