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
 * OpenAPIFeature.java
 * Copyright (C) 2026 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.rest.feature;

import adams.flow.rest.RESTPlugin;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.openapi.OpenApiFeature;

import java.util.HashSet;
import java.util.Set;

/**
 * Enables OpenAPI documentation at <URL>/api-docs/?url=/openapi.json
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class OpenAPIFeature
  extends AbstractFeature {

  private static final long serialVersionUID = -2311121309380593978L;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Enables OpenAPI documentation at <URL>/api-docs/?url=/openapi.json";
  }

  /**
   * Applies the feature to the server factory bean.
   *
   * @param factory the factory to update
   * @param plugins the REST plugins that are to be used
   * @return true if successfully applied
   */
  @Override
  public boolean applyFeature(JAXRSServerFactoryBean factory, RESTPlugin[] plugins) {
    OpenApiFeature 		openApiFeature;
    Set<String> 		resourceClasses;

    openApiFeature = new OpenApiFeature();
    openApiFeature.setTitle("ADAMS");
    resourceClasses = new HashSet<>();
    for (RESTPlugin plugin : plugins)
      resourceClasses.add(plugin.getClass().getName());
    openApiFeature.setResourceClasses(resourceClasses);
    factory.getFeatures().add(openApiFeature);
    return true;
  }
}
