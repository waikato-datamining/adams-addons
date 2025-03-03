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
 * MOAClustererModelLoader.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.flow.core;

import adams.core.MessageCollection;
import adams.core.SerializationHelper;
import adams.core.Utils;
import adams.flow.container.AbstractContainer;
import adams.flow.container.MOAModelContainer;
import moa.clusterers.Clusterer;

/**
 * Manages classifier models.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class MOAClustererModelLoader
  extends AbstractModelLoader<Clusterer> {

  private static final long serialVersionUID = -8296159861720133340L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Manages MOA Clusterer models.";
  }

  /**
   * Deserializes the model file.
   *
   * @param errors	for collecting errors
   * @return		the object read from the file, null if failed
   */
  @Override
  protected Object deserializeFile(MessageCollection errors) {
    Object	result;
    Object[]	objs;

    result = null;

    try {
      objs = SerializationHelper.readAll(m_ModelFile.getAbsolutePath());
      for (Object obj: objs) {
        if (obj instanceof Clusterer) {
          result = obj;
          break;
	}
      }
      if (result == null)
        errors.add("Failed to locate a " + Utils.classToString(Clusterer.class)
	  + " object in the objects loaded from: " + m_ModelFile);
    }
    catch (Exception e) {
      errors.add("Failed to deserialize '" + m_ModelFile + "': ", e);
    }

    return result;
  }

  /**
   * Retrieves the model from the container.
   *
   * @param cont	the container to get the model from
   * @param errors	for collecting errors
   * @return		the model, null if not in container
   */
  @Override
  protected Clusterer getModelFromContainer(AbstractContainer cont, MessageCollection errors) {
    if (cont instanceof MOAModelContainer)
      return (Clusterer) cont.getValue(MOAModelContainer.VALUE_MODEL);

    unhandledContainer(cont, errors);
    return null;
  }
}
