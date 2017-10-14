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
 * CNTKModelHandler.java
 * Copyright (C) 2017 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.previewbrowser;

import adams.core.Utils;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.Fonts;
import com.microsoft.CNTK.DeviceDescriptor;
import com.microsoft.CNTK.Function;
import com.microsoft.CNTK.Variable;

import javax.swing.JTextArea;
import java.io.File;

/**
 <!-- globalinfo-start -->
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class CNTKModelHandler
  extends AbstractContentHandler {

  /** for serialization. */
  private static final long serialVersionUID = -2780002972029225999L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Displays information about a CNTK model.";
  }

  /**
   * Returns the list of extensions (without dot) that this handler can
   * take care of.
   *
   * @return		the list of extensions (no dot)
   */
  @Override
  public String[] getExtensions() {
    return new String[]{"cmf", "dnn", "model"};
  }

  /**
   * Creates the actual view.
   *
   * @param file	the file to create the view for
   * @return		the view
   */
  @Override
  protected PreviewPanel createPreview(File file) {
    JTextArea 	area;
    Function		model;
    StringBuilder	info;

    area = new JTextArea();
    area.setFont(Fonts.getMonospacedFont());

    try {
      model = Function.load(file.getAbsolutePath(), DeviceDescriptor.useDefaultDevice());
      info  = new StringBuilder();
      info.append("Arguments\n=========");
      for (Variable var: model.getArguments()) {
        info.append("\n");
        info.append(var.toString());
      }
      info.append("\n\nInputs\n======");
      for (Variable var: model.getInputs()) {
        info.append("\n");
        info.append(var.toString());
      }
      info.append("\n\nOutputs\n=======");
      for (Variable var: model.getOutputs()) {
        info.append("\n");
        info.append(var.toString());
      }
      area.setText(info.toString());
    }
    catch (Exception e) {
      area.setText("Failed to open model: " + file + "\n" + Utils.throwableToString(e));
    }

    return new PreviewPanel(new BaseScrollPane(area), area);
  }
}
