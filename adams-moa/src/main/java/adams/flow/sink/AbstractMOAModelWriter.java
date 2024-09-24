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
 * AbstractMOAModelWriter.java
 * Copyright (C) 2011-2024 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import adams.core.SerializationHelper;
import adams.flow.container.MOAModelContainer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Ancestor for actors that serialize models.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @author  Corey Sterling (coreytsterling at gmail dot com)
 */
public abstract class AbstractMOAModelWriter
  extends AbstractFileWriter {

  /** for serialization. */
  private static final long serialVersionUID = 3350160676590555361L;

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String outputFileTipText() {
    return "The filename to save the model (and optional header) in.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		adams.flow.container.MOAModelContainer.class + additional classes
   * @see		#getAdditionalAcceptedClasses()
   */
  public Class[] accepts() {
    List<Class> result;

    result = new ArrayList<>();
    result.add(MOAModelContainer.class);
    result.addAll(Arrays.asList(getAdditionalAcceptedClasses()));

    return result.toArray(new Class[0]);
  }

  /**
   * Returns additional classes that are accepted as input.
   * <br><br>
   * Default implementation returns a zero-length array.
   *
   * @return		the additional classes
   */
  protected Class[] getAdditionalAcceptedClasses() {
    return new Class[0];
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    MOAModelContainer	cont;

    result = null;

    try {
      if (m_InputToken.getPayload() instanceof MOAModelContainer) {
	cont = (MOAModelContainer) m_InputToken.getPayload();
	if (cont.hasValue(MOAModelContainer.VALUE_HEADER)) {
	  SerializationHelper.writeAll(
	    m_OutputFile.getAbsolutePath(),
	    new Object[]{
	      cont.getValue(MOAModelContainer.VALUE_MODEL),
	      cont.getValue(MOAModelContainer.VALUE_HEADER)
	    });
	}
	else {
	  SerializationHelper.write(
	    m_OutputFile.getAbsolutePath(),
	    cont.getValue(MOAModelContainer.VALUE_MODEL));
	}
      }
      else {
	SerializationHelper.write(
	  m_OutputFile.getAbsolutePath(),
	  m_InputToken.getPayload());
      }
    }
    catch (Exception e) {
      result = handleException("Failed to serialize model data to '" + m_OutputFile + "':", e);
    }

    return result;
  }
}
