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
 * TLSUtils.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.core;

import com.github.fracpete.javautils.struct.Struct3;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.configuration.jsse.TLSServerParameters;
import org.apache.cxf.transport.http.HTTPConduit;

/**
 * Helper class for TLS related operations.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class TLSUtils
  extends TLSHelper {

  public static final String FALLBACK_TLS_VERSION = "TLS";

  /**
   * Configures TLS client parameters based on KeyManager, TrustManager, SSLContext
   * actors.
   *
   * @param context	the flow context
   * @return		the parameters, null if failed to configure
   */
  public static TLSClientParameters configureClientTLS(Actor context) {
    TLSClientParameters 	result;
    Struct3<KeyManagerFactoryProvider,TrustManagerFactoryProvider,SSLContextProvider> actors;
    String			protocol;

    actors = locateActors(context, false);
    if (actors == null)
      return null;
    if ((actors.value1 == null) || (actors.value1.getKeyManagerFactory() == null))
      return null;
    if ((actors.value2 == null) || (actors.value2.getTrustManagerFactory() == null))
      return null;

    protocol = FALLBACK_TLS_VERSION;
    if (actors.value3 != null)
      protocol = actors.value3.getProtocol();

    result = new TLSClientParameters();
    result.setKeyManagers(actors.value1.getKeyManagerFactory().getKeyManagers());
    result.setTrustManagers(actors.value2.getTrustManagerFactory().getTrustManagers());
    result.setSecureSocketProtocol(protocol);

    return result;
  }

  /**
   * Sets TrustManager and KeyManager if within the context of the actor.
   *
   * @param owner	the owning actor
   * @param http	the HTTP conduit to configure
   * @return		whether successfully enabled TLS
   */
  public static boolean configureClientTLS(Actor owner, HTTPConduit http) {
    TLSClientParameters tlsParams;

    tlsParams = TLSUtils.configureClientTLS(owner);

    if (tlsParams != null) {
      http.setTlsClientParameters(tlsParams);
      return true;
    }

    return false;
  }

  /**
   * Configures TLS server parameters based on KeyManager, TrustManager, SSLContext
   * actors.
   *
   * @param context	the flow context
   * @return		the parameters, null if failed to configure
   */
  public static TLSServerParameters configureServerTLS(Actor context) {
    TLSServerParameters 	result;
    Struct3<KeyManagerFactoryProvider,TrustManagerFactoryProvider,SSLContextProvider> actors;
    String			protocol;

    actors = locateActors(context, false);
    if (actors == null)
      return null;
    if ((actors.value1 == null) || (actors.value1.getKeyManagerFactory() == null))
      return null;
    if ((actors.value2 == null) || (actors.value2.getTrustManagerFactory() == null))
      return null;

    protocol = FALLBACK_TLS_VERSION;
    if (actors.value3 != null)
      protocol = actors.value3.getProtocol();

    result = new TLSServerParameters();
    result.setKeyManagers(actors.value1.getKeyManagerFactory().getKeyManagers());
    result.setTrustManagers(actors.value2.getTrustManagerFactory().getTrustManagers());
    result.setSecureSocketProtocol(protocol);

    return result;
  }
}
