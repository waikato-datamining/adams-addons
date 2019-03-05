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
 * PyroProxyObject.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package weka.core;

import adams.core.base.BaseHostname;
import adams.data.wekapyroproxy.AbstractCommunicationProcessor;

/**
 * Interface for classes that make use of Pyro4.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public interface PyroProxyObject
  extends OptionHandler {

  /**
   * Sets the address of the Pyro nameserver.
   *
   * @param value 	the address
   */
  public void setNameServer(BaseHostname value);

  /**
   * Returns the address of the Pyro nameserver.
   *
   * @return 		the address
   */
  public BaseHostname getNameServer();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String nameServerTipText();

  /**
   * Sets the name of the remote object to use.
   *
   * @param value 	the name
   */
  public void setRemoteObjectName(String value);

  /**
   * Returns the name of the remote object to use.
   *
   * @return 		the name
   */
  public String getRemoteObjectName();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String remoteObjectNameTipText();

  /**
   * Sets the handler for the communication.
   *
   * @param value 	the handler
   */
  public void setCommunication(AbstractCommunicationProcessor value);

  /**
   * Returns the handler for the communication.
   *
   * @return 		the handler
   */
  public AbstractCommunicationProcessor getCommunication();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String communicationTipText();
}
