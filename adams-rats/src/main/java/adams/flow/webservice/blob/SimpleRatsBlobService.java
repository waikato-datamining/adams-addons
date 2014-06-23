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
 * SimpleRatsBlobService.java
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.webservice.blob;

import nz.ac.waikato.adams.webservice.rats.blob.RatsBlobService;
import nz.ac.waikato.adams.webservice.rats.blob.UploadRequest;
import nz.ac.waikato.adams.webservice.rats.blob.UploadResponse;
import adams.core.option.AbstractOptionHandler;
import adams.data.blob.BlobContainer;
import adams.flow.core.ActorUtils;
import adams.flow.core.RatsBlobHelper;
import adams.flow.standalone.rats.WSBlobReception;


/**
 * Class that implements the RATS spectrum web service.  
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 2085 $
 */
public class SimpleRatsBlobService
  extends AbstractOptionHandler
  implements RatsBlobService, OwnedByRatsBlobServiceWS {

  /** for serialization. */
  private static final long serialVersionUID = -6102580694812360595L;

  /** web service object   */
  protected RatsBlobServiceWS m_Owner;

  /** the database connection. */
  protected adams.db.AbstractDatabaseConnection m_DatabaseConnection;
  
  /**
   * Default Constructor.
   * <p/>
   * NB: the owning webservice needs to get set before using this implemention,
   * using the {@link #setOwner(RatsBlobServiceWS)} method.
   */
  public SimpleRatsBlobService() {
    super();
    setOwner(null);
  }

  /**
   * Returns a string for the GUI that describes this object.
   * 
   * @return		the description
   */
  @Override
  public String globalInfo() {
    return "Simple implementation of a RATS spectrum webservice.";
  }

  /**
   * Determines the database connection in the flow.
   *
   * @return		the database connection to use
   */
  protected adams.db.AbstractDatabaseConnection getDatabaseConnection() {
    return ActorUtils.getDatabaseConnection(
	  m_Owner.getOwner(),
	  adams.flow.standalone.DatabaseConnection.class,
	  adams.db.DatabaseConnection.getSingleton());
  }

  /**
   * Sets the owner of this webservice.
   * 
   * @param value	the owner
   */
  public void setOwner(RatsBlobServiceWS value) {
    m_Owner = value;
    
    if ((m_Owner != null) && (m_Owner.getOwner() != null))
      m_DatabaseConnection = getDatabaseConnection();
    else
      m_DatabaseConnection = null;
  }
  
  /**
   * Returns the current owner of this webservice.
   * 
   * @return		the owner, null if none set
   */
  public RatsBlobServiceWS getOwner() {
    return m_Owner;
  }

  /**
   * Downloads a spectrum.
   */
  @Override
  public UploadResponse upload(UploadRequest parameters) {
    UploadResponse	result;
    BlobContainer	cont;

    m_Owner.getLogger().info("upload: " + parameters.getFilename() + "/" + parameters.getFormat());
    
    result = new UploadResponse();
    result.setFilename(parameters.getFilename());
    result.setFormat(parameters.getFormat());
    result.setSuccess(true);
    cont = RatsBlobHelper.webserviceToContainer(parameters.getBlob());
    if (isLoggingEnabled())
      getLogger().fine(cont.toString());

    if (getOwner().getRatInput() instanceof WSBlobReception)
      ((WSBlobReception) getOwner().getRatInput()).setData(cont);
    
    return result;
  }
}