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
 * DL4JDatasetAppend.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.flow.core.Token;
import adams.flow.provenance.ActorType;
import adams.flow.provenance.Provenance;
import adams.flow.provenance.ProvenanceContainer;
import adams.flow.provenance.ProvenanceInformation;
import adams.flow.provenance.ProvenanceSupporter;
import adams.ml.dl4j.DataSetHelper;
import org.nd4j.linalg.dataset.DataSet;

import java.util.Arrays;

/**
 <!-- globalinfo-start -->
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DL4JDatasetAppend
  extends AbstractTransformer
  implements ProvenanceSupporter {

  /** for serialization. */
  private static final long serialVersionUID = -268487303904639474L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Creates one large dataset by appending all, one after the other.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->java.lang.String[].class, java.io.File[].class, weka.core.Instances[].class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{DataSet[].class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->weka.core.Instances.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{DataSet.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    int			i;
    int			n;
    DataSet[] 		data;
    String		msg;

    result = null;

    // get filenames
    if (m_InputToken.getPayload() instanceof DataSet[])
      data = (DataSet[]) m_InputToken.getPayload();
    else
      throw new IllegalStateException("Unhandled input type: " + m_InputToken.getPayload().getClass());

    // test compatibility
    for (i = 0; i < data.length - 1; i++) {
      for (n = i + 1; n < data.length; n++) {
	msg = DataSetHelper.equalStructureMsg(data[i], data[n]);
	if (msg != null) {
	  result = "Dataset #" + (i+1) + " and #" + (n+1) + " are not compatible:\n" + msg;
	  break;
	}
      }
      if (result != null)
	break;
    }

    // append
    if (result == null)
      m_OutputToken = new Token(DataSet.merge(Arrays.asList(data)));

    return result;
  }

  /**
   * Updates the provenance information in the provided container.
   *
   * @param cont	the provenance container to update
   */
  public void updateProvenance(ProvenanceContainer cont) {
    if (Provenance.getSingleton().isEnabled())
      cont.addProvenance(new ProvenanceInformation(ActorType.DATAGENERATOR, m_InputToken.getPayload().getClass(), this, m_OutputToken.getPayload().getClass()));
  }
}
