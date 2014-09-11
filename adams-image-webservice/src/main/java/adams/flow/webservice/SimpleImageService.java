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
 * SimpleImageService.java
 * Copyright (C) 2014 Image BV, Wageningen, NL
 */

package adams.flow.webservice;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.logging.Level;

import nz.ac.waikato.adams.webservice.image.ImageService;
import nz.ac.waikato.adams.webservice.image.UploadRequest;
import nz.ac.waikato.adams.webservice.image.UploadResponse;
import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderDirectory;
import adams.core.option.AbstractOptionHandler;
import adams.flow.core.ActorUtils;

/**
 * Class that implements the Image web service.  
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 91 $
 */
public class SimpleImageService
  extends AbstractOptionHandler
  implements ImageService, OwnedByImageServiceWS {

  /** for serialization. */
  private static final long serialVersionUID = -6102580694812360595L;

  /** web service object   */
  protected ImageServiceWS m_Owner;

  /** the database connection. */
  protected adams.db.AbstractDatabaseConnection m_DatabaseConnection;
  
  /** the upload directory. */
  protected PlaceholderDirectory m_UploadDir;
  
  /**
   * Default Constructor.
   * <p/>
   * NB: the owning webservice needs to get set before using this implemention,
   * using the {@link #setOwner(ImageServiceWS)} method.
   */
  public SimpleImageService() {
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
    return "Simple implementation of a Image webservice.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "upload-dir", "uploadDir",
	    new PlaceholderDirectory("${TMP}"));
  }
  
  /**
   * Sets the directory to store the images in.
   *
   * @param value 	the directory
   */
  public void setUploadDir(PlaceholderDirectory value) {
    m_UploadDir = value;
    reset();
  }

  /**
   * Returns the directory to store the images in.
   *
   * @return 		the directory
   */
  public PlaceholderDirectory getUploadDir() {
    return m_UploadDir;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String uploadDirTipText() {
    return "The upload directory to store the images in.";
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
  public void setOwner(ImageServiceWS value) {
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
  public ImageServiceWS getOwner() {
    return m_Owner;
  }

  /**
   * Uploads an image.
   */
  @Override
  public UploadResponse upload(UploadRequest parameters) {
    UploadResponse		result;
    String			filename;
    BufferedOutputStream	bos;
    InputStream			ins;
    int				data;

    m_Owner.getLogger().info("upload: " + parameters.getId() + "/" + parameters.getFormat());

    result   = new UploadResponse();
    result.setId(parameters.getId());
    result.setFormat(parameters.getFormat());
    filename = parameters.getId() + "." + parameters.getFormat().toString().toLowerCase();
    filename = m_UploadDir.getAbsolutePath() + File.separator + FileUtils.createFilename(filename, "_");
    try {
      bos      = new BufferedOutputStream(new FileOutputStream(filename));
      ins      = parameters.getImage().getData().getInputStream();
      while ((data = ins.read()) != -1)
	bos.write(data);
      bos.flush();
      bos.close();
      result.setSuccess(true);
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to store image '" + parameters.getId() + "/" + parameters.getFormat() + "' as '" + filename + "'!", e);
      result.setMessage("Failed to store image '" + parameters.getId() + "/" + parameters.getFormat() + ":\n" + Utils.throwableToString(e));
      result.setSuccess(true);
    }
    
    return result;
  }
}