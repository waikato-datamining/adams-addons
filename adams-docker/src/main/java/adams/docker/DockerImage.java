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
 * DockerImage.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.docker;

import java.io.Serializable;
import java.util.Objects;

/**
 * Simple container for docker images.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class DockerImage
  implements Serializable, Comparable<DockerImage> {

  private static final long serialVersionUID = -687099907771835571L;

  /** the repository this image is from. */
  protected String m_Repository;

  /** the tag of this image. */
  protected String m_Tag;

  /** the image ID (hash). */
  protected String m_ImageID;

  /** when the image was created. */
  protected String m_Created;

  /** the size of the image. */
  protected String m_Size;

  /**
   * Initializes the container.
   *
   * @param repository	the repository
   * @param tag		the tag
   * @param imageID	the image ID (hash)
   * @param created	the created string
   * @param size	the size
   */
  public DockerImage(String repository, String tag, String imageID, String created, String size) {
    m_Repository = repository;
    m_Tag        = tag;
    m_ImageID    = imageID;
    m_Created    = created;
    m_Size       = size;
  }

  /**
   * Returns the repository.
   *
   * @return		the repository
   */
  public String getRepository() {
    return m_Repository;
  }

  /**
   * Returns the tag.
   *
   * @return		the tag
   */
  public String getTag() {
    return m_Tag;
  }

  /**
   * Returns the image ID (hash).
   *
   * @return		the hash
   */
  public String getImageID() {
    return m_ImageID;
  }

  /**
   * Returns the created information.
   *
   * @return		the created information
   */
  public String getCreated() {
    return m_Created;
  }

  /**
   * Returns the size.
   *
   * @return		the size
   */
  public String getSize() {
    return m_Size;
  }

  /**
   * Compares itself with the other image.
   *
   * @param o	the other image
   * @return	the comparison result
   */
  @Override
  public int compareTo(DockerImage o) {
    int		result;

    result = getRepository().compareTo(o.getRepository());
    if (result == 0)
      result = getTag().compareTo(o.getTag());
    if (result == 0)
      result = getImageID().compareTo(o.getImageID());
    if (result == 0)
      result = getCreated().compareTo(o.getCreated());
    if (result == 0)
      result = getSize().compareTo(o.getSize());

    return result;
  }

  /**
   * Checks whether the objects are equal.
   * 
   * @param o		the object to compare with
   * @return		true if the same content
   * @see		#compareTo(DockerImage)
   */
  @Override
  public boolean equals(Object o) {
    return (o instanceof DockerImage) && (compareTo((DockerImage) o) == 0);
  }

  /**
   * Generates a hash code.
   *
   * @return		the hash
   */
  @Override
  public int hashCode() {
    return Objects.hash(m_Repository, m_Tag, m_ImageID, m_Created, m_Size);
  }
}
