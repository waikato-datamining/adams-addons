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
 * SSEFeature.java
 * Copyright (C) 2026 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.rest.feature;

import adams.core.base.BaseObject;
import adams.core.base.BaseString;
import adams.flow.rest.RESTPlugin;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.sse.SseFeature;
import org.apache.cxf.rs.security.cors.CrossOriginResourceSharingFilter;

import java.util.Arrays;

/**
 * Adds Server Sent Events (SSE) support.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class SSEFeature
  extends AbstractFeature {

  private static final long serialVersionUID = 6396648452070524909L;

  /** the origins to allow. */
  protected BaseString[] m_Origins;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Adds Server Sent Events (SSE) support.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "origin", "origins",
      new BaseString[]{new BaseString("*")});
  }

  /**
   * Sets the origins to allow.
   *
   * @param value	the origins
   */
  public void setOrigins(BaseString[] value) {
    m_Origins = value;
    reset();
  }

  /**
   * Returns the origins to allow.
   *
   * @return		the origins
   */
  public BaseString[] getOrigins() {
    return m_Origins;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String originsTipText() {
    return "The URLs of the origins to allow, use '*' for any.";
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
    CrossOriginResourceSharingFilter corsFilter;

    corsFilter = new CrossOriginResourceSharingFilter();
    corsFilter.setAllowOrigins(Arrays.asList(BaseObject.toStringArray(m_Origins)));
    factory.setProviders(Arrays.asList(corsFilter));

    factory.getFeatures().add(new SseFeature());

    return true;
  }
}
