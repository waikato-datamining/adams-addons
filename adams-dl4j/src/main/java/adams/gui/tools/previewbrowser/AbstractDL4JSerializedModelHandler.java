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
 * AbstractDL4JSerializedModelHandler.java
 * Copyright (C) 2017 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.previewbrowser;

import adams.data.conversion.Conversion;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseTextArea;
import adams.gui.core.Fonts;
import org.deeplearning4j.util.ModelSerializer;

import java.io.File;

/**
 * Ancestor of DL4J model previewers.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 10824 $
 */
public abstract class AbstractDL4JSerializedModelHandler
  extends AbstractContentHandler {

  /** for serialization. */
  private static final long serialVersionUID = -1277627290853745369L;

  /**
   * Performs some checks on the file.
   *
   * @param file	the file to check
   * @return		null if check passed, otherwise error message
   */
  @Override
  protected String checkFile(File file) {
    String	result;

    result = super.checkFile(file);

    if (result == null) {
      try {
	ModelSerializer.restoreMultiLayerNetwork(file.getAbsoluteFile());
      }
      catch (Exception e1) {
	try {
	  ModelSerializer.restoreComputationGraph(file.getAbsoluteFile());
	  result = null;
	}
	catch (Exception e2) {
	  return "Neither a multi-layer network nor a computation graph!";
	}
      }
    }

    return result;
  }

  /**
   * Returns the conversion to use for the model.
   *
   * @return		the conversion
   */
  protected abstract Conversion getModelConversion();

  /**
   * Creates the actual view.
   *
   * @param file	the file to create the view for
   * @return		the view
   */
  @Override
  protected PreviewPanel createPreview(File file) {
    PreviewPanel	result;
    BaseTextArea 	textObject;
    Conversion	 	conv;
    String		msg;
    Object		obj;

    try {
      obj = ModelSerializer.restoreMultiLayerNetwork(file.getAbsoluteFile());
    }
    catch (Exception e1) {
      try {
	obj = ModelSerializer.restoreComputationGraph(file.getAbsoluteFile());
      }
      catch (Exception e2) {
	return null;
      }
    }
    conv = getModelConversion();
    conv.setInput(obj);
    msg = conv.convert();
    if (msg != null)
      return null;

    textObject = new BaseTextArea();
    textObject.setEditable(false);
    textObject.setFont(Fonts.getMonospacedFont());
    textObject.setText("" + conv.getOutput());
    result = new PreviewPanel(new BaseScrollPane(textObject), textObject);

    return result;
  }
}
