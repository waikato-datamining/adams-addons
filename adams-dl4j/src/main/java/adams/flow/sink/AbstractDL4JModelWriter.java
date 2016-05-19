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
 * AbstractDL4JModelWriter.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import adams.core.SerializationHelper;
import adams.flow.container.DL4JModelContainer;
import adams.ml.dl4j.ModelSerialization;
import org.deeplearning4j.nn.api.Model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Ancestor for actors that serialize models.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractDL4JModelWriter
  extends AbstractFileWriter {

  /** for serialization. */
  private static final long serialVersionUID = -259222073894194923L;

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String outputFileTipText() {
    return "The filename to save the model in.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		adams.flow.container.WekaModelContainer.class + additional classes
   * @see		#getAdditionalAcceptedClasses()
   */
  public Class[] accepts() {
    List<Class> result;

    result = new ArrayList<>();
    result.add(DL4JModelContainer.class);
    result.addAll(Arrays.asList(getAdditionalAcceptedClasses()));

    return result.toArray(new Class[result.size()]);
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
    DL4JModelContainer	cont;
    Model		model;
    Object		obj;

    result = null;

    try {
      model = null;
      obj   = null;
      if (m_InputToken.getPayload() instanceof DL4JModelContainer) {
        cont  = (DL4JModelContainer) m_InputToken.getPayload();
	model = (Model) cont.getValue(DL4JModelContainer.VALUE_MODEL);
      }
      else {
	if (m_InputToken.getPayload() instanceof Model)
	  model = (Model) m_InputToken.getPayload();
	else
	  obj = m_InputToken.getPayload();
      }
      if (model != null) {
	ModelSerialization.write(m_OutputFile, model);
      }
      else if (obj != null) {
	SerializationHelper.write(
	    m_OutputFile.getAbsolutePath(),
	    obj);
      }
      else {
	result = "Don't know how to save object of type: " + m_InputToken.getPayload().getClass();
      }
    }
    catch (Exception e) {
      result = handleException("Failed to serialize model data to '" + m_OutputFile + "':", e);
    }

    return result;
  }
}
