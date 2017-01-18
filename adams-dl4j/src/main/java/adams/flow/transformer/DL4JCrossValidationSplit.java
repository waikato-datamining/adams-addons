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
 * WekaCrossValidationSplit.java
 * Copyright (C) 2010-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.flow.container.DL4JTrainTestSetContainer;
import adams.flow.core.Token;
import adams.flow.provenance.ActorType;
import adams.flow.provenance.Provenance;
import adams.flow.provenance.ProvenanceContainer;
import adams.flow.provenance.ProvenanceInformation;
import adams.flow.provenance.ProvenanceSupporter;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.KFoldIterator;

import java.util.Hashtable;

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
public class DL4JCrossValidationSplit
  extends AbstractTransformer
  implements ProvenanceSupporter {

  /** for serialization. */
  private static final long serialVersionUID = 4026105903223741240L;

  /** the key for the generator. */
  public final static String BACKUP_GENERATOR = "Generator";

  /** the number of folds to generate. */
  protected int m_Folds;

  /** the generator to use. */
  protected transient KFoldIterator m_Generator;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Generates train/test pairs like during a cross-validation run.\n"
	+ "It is essential that a class attribute is set. The training set can "
	+ "be accessed in the container with '" + DL4JTrainTestSetContainer.VALUE_TRAIN + "' "
	+ "and the test set with '" + DL4JTrainTestSetContainer.VALUE_TEST + "'.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "folds", "folds",
      10);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "folds", m_Folds, "folds: ");
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->weka.core.Instances.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{DataSet.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->adams.flow.container.WekaTrainTestSetContainer.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{DL4JTrainTestSetContainer.class};
  }

  /**
   * Sets the number of folds to use.
   *
   * @param value	the folds, use -1 for LOOCV
   */
  public void setFolds(int value) {
    if (getOptionManager().isValid("folds", value)) {
      m_Folds = value;
      reset();
    }
  }

  /**
   * Returns the number of folds to generate.
   *
   * @return  		the folds, 1 in case of LOOCV
   */
  public int getFolds() {
    return m_Folds;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String foldsTipText() {
    return "The folds to use.";
  }

  /**
   * Removes entries from the backup.
   */
  @Override
  protected void pruneBackup() {
    super.pruneBackup();

    pruneBackup(BACKUP_GENERATOR);
  }

  /**
   * Backs up the current state of the actor before update the variables.
   *
   * @return		the backup
   */
  @Override
  protected Hashtable<String,Object> backupState() {
    Hashtable<String,Object>	result;

    result = super.backupState();

    if (m_Generator != null)
      result.put(BACKUP_GENERATOR, m_Generator);

    return result;
  }

  /**
   * Restores the state of the actor before the variables got updated.
   *
   * @param state	the backup of the state to restore from
   */
  @Override
  protected void restoreState(Hashtable<String,Object> state) {
    if (state.containsKey(BACKUP_GENERATOR)) {
      m_Generator = (KFoldIterator) state.get(BACKUP_GENERATOR);
      state.remove(BACKUP_GENERATOR);
    }

    super.restoreState(state);
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Generator = null;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;

    result = null;
    try {
      m_Generator = new KFoldIterator(m_Folds, (DataSet) m_InputToken.getPayload());
    }
    catch (Exception e) {
      result = handleException("Failed to initialize fold generator!", e);
    }

    return result;
  }

  /**
   * Checks whether there is pending output to be collected after
   * executing the flow item.
   *
   * @return		true if there is pending output
   */
  @Override
  public boolean hasPendingOutput() {
    return (m_Generator != null) && m_Generator.hasNext();
  }

  /**
   * Returns the generated token.
   *
   * @return		the generated token
   */
  @Override
  public Token output() {
    Token	result;
    DataSet	train;
    DataSet	test;

    train  = m_Generator.next();
    test   = m_Generator.testFold();
    result = new Token(new DL4JTrainTestSetContainer(train, test));

    updateProvenance(result);

    return result;
  }

  /**
   * Updates the provenance information in the provided container.
   *
   * @param cont	the provenance container to update
   */
  public void updateProvenance(ProvenanceContainer cont) {
    if (Provenance.getSingleton().isEnabled()) {
      if (m_InputToken.hasProvenance())
	cont.setProvenance(m_InputToken.getProvenance().getClone());
      cont.addProvenance(new ProvenanceInformation(ActorType.DATAGENERATOR, m_InputToken.getPayload().getClass(), this, ((Token) cont).getPayload().getClass()));
    }
  }

  /**
   * Cleans up after the execution has finished.
   */
  @Override
  public void wrapUp() {
    m_Generator = null;

    super.wrapUp();
  }
}
