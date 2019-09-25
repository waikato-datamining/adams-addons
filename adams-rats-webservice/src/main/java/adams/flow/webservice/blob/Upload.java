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
 * Upload.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.webservice.blob;

import adams.core.net.MimeTypeHelper;
import adams.data.blob.BlobContainer;
import adams.flow.core.RatsBlobHelper;
import adams.flow.webservice.AbstractWebServiceClientSink;
import adams.flow.webservice.WebserviceUtils;
import nz.ac.waikato.adams.webservice.rats.blob.RatsBlobService;
import nz.ac.waikato.adams.webservice.rats.blob.RatsBlobServiceService;
import nz.ac.waikato.adams.webservice.rats.blob.UploadRequest;
import nz.ac.waikato.adams.webservice.rats.blob.UploadResponse;

import javax.xml.ws.BindingProvider;
import java.net.URL;

/**
 * Uploads a BlobContainer.
 * 
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 2085 $
 */
public class Upload 
  extends AbstractWebServiceClientSink<BlobContainer>{

  /** for serialization*/
  private static final long serialVersionUID = -338043583699608760L;
  
  /** input container */
  protected BlobContainer m_ContainerIn;

  /** the format. */
  protected String m_Format;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Stores a BlobContainer using the RATS text webservice.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "format", "format",
	    MimeTypeHelper.MIMETYPE_APPLICATION_OCTETSTREAM);
  }
  
  /**
   * Sets the mime format.
   *
   * @param value	the format
   */
  public void setFormat(String value) {
    m_Format = value;
    reset();
  }

  /**
   * Returns the mime format.
   *
   * @return		the format
   */
  public String getFormat() {
    return m_Format;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String formatTipText() {
    return "The mime format type.";
  }

  /**
   * Returns the classes that are accepted input.
   * 
   * @return		the classes that are accepted
   */
  @Override
  public Class[] accepts() {
    return new Class[]{BlobContainer.class};
  }

  /**
   * Returns the WSDL location.
   * 
   * @return		the location
   */
  @Override
  public URL getWsdlLocation() {
    return getClass().getClassLoader().getResource("wsdl/adams/RatsBlobService.wsdl");
  }

  /**
   * Sets the data for the request, if any.
   * 
   * @param value	the request data
   */
  @Override
  public void setRequestData(BlobContainer value) {
    m_ContainerIn = value;
  }

  /**
   * Performs the actual webservice query.
   * 
   * @throws Exception	if accessing webservice fails for some reason
   */
  @Override
  protected void doQuery() throws Exception {
    RatsBlobServiceService ratsServiceService;
    RatsBlobService ratsService;
    ratsServiceService = new RatsBlobServiceService(getWsdlLocation());
    ratsService = ratsServiceService.getRatsBlobServicePort();
    WebserviceUtils.configureClient(
	m_Owner,
	ratsService, 
	m_ConnectionTimeout, 
	m_ReceiveTimeout, 
	(getUseAlternativeURL() ? getAlternativeURL() : null),
	null,
	m_OutInterceptor);
    //check against schema
    WebserviceUtils.enableSchemaValidation(((BindingProvider) ratsService));
   
    UploadRequest request = new UploadRequest();
    request.setFilename(m_ContainerIn.getID());  // TODO
    request.setFormat(m_Format);
    request.setBlob(RatsBlobHelper.containerToWebservice(m_ContainerIn));
    UploadResponse response = ratsService.upload(request);
    
    // failed to generate data?
    if (!response.isSuccess())
      throw new IllegalStateException(response.getMessage());
  }
}
