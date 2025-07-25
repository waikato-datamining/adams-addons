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
 * RESTUtils.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.dropwizard;

import adams.core.Utils;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class around REST webservices.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class RESTUtils {

  /**
   * Returns the additional for the class, if any.
   *
   * @param cls		the class to inspect
   * @return		the generated information, null if none available
   */
  protected static String getAdditionalInformation(Class cls) {
    StringBuilder	result;
    Annotation 		annotation;

    result = new StringBuilder();

    if (cls.isAnnotationPresent(Path.class)) {
      annotation = cls.getAnnotation(Path.class);
      result.append("- Path: ").append(annotation).append("\n");
    }
    if (cls.isAnnotationPresent(Consumes.class)) {
      annotation = cls.getAnnotation(Consumes.class);
      result.append("- Consumes: ").append(annotation).append("\n");
    }
    if (cls.isAnnotationPresent(Produces.class)) {
      annotation = cls.getAnnotation(Produces.class);
      result.append("- Produces: ").append(annotation).append("\n");
    }

    if (result.length() == 0)
      return null;
    else
      return result.toString();
  }

  /**
   * Returns the additional for the method, if any.
   *
   * @param method	the method to inspect
   * @return		the generated information, null if none available
   */
  protected static String getAdditionalInformation(Method method) {
    StringBuilder	result;
    Annotation 		annotation;
    List<String>	methods;
    int			index;

    result = new StringBuilder();

    if (method.isAnnotationPresent(Path.class)) {
      annotation = method.getAnnotation(Path.class);
      result.append("- Path: ").append(annotation).append("\n");
    }
    if (method.isAnnotationPresent(Consumes.class)) {
      annotation = method.getAnnotation(Consumes.class);
      result.append("- Consumes: ").append(annotation).append("\n");
    }
    if (method.isAnnotationPresent(Produces.class)) {
      annotation = method.getAnnotation(Produces.class);
      result.append("- Produces: ").append(annotation).append("\n");
    }
    index = 0;
    for (Annotation[] annotations: method.getParameterAnnotations()) {
      index++;
      if (annotations.length > 0)
        result.append("- Parameter #").append(index).append(": ").append(Utils.flatten(annotations, ", ")).append("\n");
    }
    methods = new ArrayList<>();
    if (method.isAnnotationPresent(GET.class))
      methods.add("GET");
    if (method.isAnnotationPresent(POST.class))
      methods.add("POST");
    if (!methods.isEmpty())
      result.append("- Method(s): ").append(Utils.flatten(methods, ", ")).append("\n");

    if (result.length() == 0)
      return null;
    else
      return result.toString();
  }

  /**
   * Generates information about the plugin, to be used for the information
   * return by {@link adams.core.AdditionalInformationHandler}.
   *
   * @param plugin	the plugin to generate the information for
   * @return		the information, null if none available
   */
  public static String getAdditionalInformation(RESTPlugin plugin) {
    StringBuilder	result;
    String		info;

    result = new StringBuilder();

    // class
    info = getAdditionalInformation(plugin.getClass());
    if (info != null)
      result.append("REST Class\n").append(info);

    // methods
    for (Method method: plugin.getClass().getDeclaredMethods()) {
      info = getAdditionalInformation(method);
      if (info != null) {
        if (result.length() > 0)
          result.append("\n");
        result.append("REST Method '").append(method.getName()).append("'\n");
        result.append(info);
      }
    }

    if (result.length() == 0)
      return null;
    else
      return result.toString();
  }
}
