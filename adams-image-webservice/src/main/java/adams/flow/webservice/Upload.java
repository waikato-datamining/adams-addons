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
 * Upload.java
 * Copyright (C) 2014-2019 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.webservice;

import adams.core.net.MimeTypeHelper;
import nz.ac.waikato.adams.webservice.image.Image;
import nz.ac.waikato.adams.webservice.image.ImageFormat;
import nz.ac.waikato.adams.webservice.image.ImageService;
import nz.ac.waikato.adams.webservice.image.ImageServiceService;
import nz.ac.waikato.adams.webservice.image.UploadRequest;
import nz.ac.waikato.adams.webservice.image.UploadResponse;

import javax.activation.DataHandler;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.ws.BindingProvider;
import java.net.URL;

/**
 * Uploads an image.
 * 
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Upload 
  extends AbstractWebServiceClientSink<byte[]>{

  /** for serialization*/
  private static final long serialVersionUID = -338043583699608760L;
  
  /** the ID to use for the upload. */
  protected String m_ID;
  
  /** the image format. */
  protected ImageFormat m_Format;
  
  /** input image (as byte array). */
  protected byte[] m_ImageIn;

  /** the service instance. */
  protected transient ImageServiceService m_Service;

  /** the port instance. */
  protected transient ImageService m_Port;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Stores a spectrum using the Image web service.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "id", "ID",
	    "");

    m_OptionManager.add(
	    "format", "format",
	    ImageFormat.PNG);
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Service = null;
    m_Port    = null;
  }

  /**
   * Sets the ID of the image to upload.
   *
   * @param value 	the ID
   */
  public void setID(String value) {
    m_ID = value;
    reset();
  }

  /**
   * Returns the ID of the image to upload.
   *
   * @return 		the ID
   */
  public String getID() {
    return m_ID;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String IDTipText() {
    return "The ID of the image to upload.";
  }
  
  /**
   * Sets the format of the image to upload.
   *
   * @param value 	the format
   */
  public void setFormat(ImageFormat value) {
    m_Format = value;
    reset();
  }

  /**
   * Returns the format of the image to upload.
   *
   * @return 		the format
   */
  public ImageFormat getFormat() {
    return m_Format;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String formatTipText() {
    return "The image format.";
  }

  /**
   * Returns the classes that are accepted input.
   * 
   * @return		the classes that are accepted
   */
  @Override
  public Class[] accepts() {
    return new Class[]{byte[].class};
  }

  /**
   * Returns the WSDL location.
   * 
   * @return		the location
   */
  @Override
  public URL getWsdlLocation() {
    return getClass().getClassLoader().getResource("wsdl/image/ImageService.wsdl");
  }

  /**
   * Sets the data for the request, if any.
   * 
   * @param value	the request data
   */
  @Override
  public void setRequestData(byte[] value) {
    m_ImageIn = value;
  }

  /**
   * Performs the actual webservice query.
   * 
   * @throws Exception	if accessing webservice fails for some reason
   */
  @Override
  protected void doQuery() throws Exception {
    UploadRequest 		request;
    UploadResponse 		response;
    Image 			img;

    if (m_Service == null) {
      m_Service = new ImageServiceService(getWsdlLocation());
      m_Port = m_Service.getImageServicePort();
      WebserviceUtils.configureClient(
        m_Owner,
        m_Port,
        m_ConnectionTimeout,
        m_ReceiveTimeout,
        (getUseAlternativeURL() ? getAlternativeURL() : null),
        null,
        m_OutInterceptor);
      //check against schema
      WebserviceUtils.enableSchemaValidation(((BindingProvider) m_Port));
    }
   
    request = new UploadRequest();
    request.setId(m_ID);
    request.setFormat(m_Format);
    img = new Image();
    img.setData(new DataHandler(new ByteArrayDataSource(m_ImageIn, MimeTypeHelper.MIMETYPE_APPLICATION_OCTETSTREAM)));
    request.setImage(img);
    response = m_Port.upload(request);
    
    // failed to generate data?
    if (!response.isSuccess())
      throw new IllegalStateException(response.getMessage());
  }

  /**
   * Cleans up the client.
   */
  @Override
  public void cleanUp() {
    m_Service = null;
    m_Port    = null;

    super.cleanUp();
  }
}
