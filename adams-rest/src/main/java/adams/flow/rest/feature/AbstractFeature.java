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
 * AbstractFeature.java
 * Copyright (C) 2026 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.rest.feature;

import adams.core.option.AbstractOptionHandler;
import adams.flow.rest.GenericServer;
import adams.flow.rest.RESTPlugin;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;

/**
 * Ancestor for plugins that add features to the {@link GenericServer}.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 * @see GenericServer
 */
public abstract class AbstractFeature
  extends AbstractOptionHandler {

  private static final long serialVersionUID = 2952142585860728879L;

  /**
   * Applies the feature to the server factory bean.
   *
   * @param factory	the factory to update
   * @param plugins	the REST plugins that are to be used
   * @return		true if successfully applied
   */
  public abstract boolean applyFeature(JAXRSServerFactoryBean factory, RESTPlugin[] plugins);
}
