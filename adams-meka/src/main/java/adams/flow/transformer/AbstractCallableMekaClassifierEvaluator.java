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
 * AbstractCallableMekaClassifierEvaluator.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.flow.core.CallableActorHelper;
import adams.flow.core.CallableActorReference;
import adams.flow.source.MekaClassifierSetup;

/**
 * Ancestor for Meka classifier evaluators that make use of a callable classifier.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 9206 $
 */
public abstract class AbstractCallableMekaClassifierEvaluator
  extends AbstractMekaClassifierEvaluator {

  /** for serialization. */
  private static final long serialVersionUID = 3440872619963043115L;

  /** the name of the callable Meka classifier. */
  protected CallableActorReference m_Classifier;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "classifier", "classifier",
	    new CallableActorReference(MekaClassifierSetup.class.getSimpleName()));
  }

  /**
   * Sets the name of the callable classifier to use.
   *
   * @param value	the name
   */
  public void setClassifier(CallableActorReference value) {
    m_Classifier = value;
    reset();
  }

  /**
   * Returns the name of the callable classifier in use.
   *
   * @return		the name
   */
  public CallableActorReference getClassifier() {
    return m_Classifier;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public abstract String classifierTipText();

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "classifier", m_Classifier);
  }

  /**
   * Initializes the item for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String	result;
    String	variable;

    result = super.setUp();

    if (result == null) {
      variable = getOptionManager().getVariableForProperty("classifier");
      if (variable == null) {
	if (m_Classifier.isEmpty())
	  result = "No classifier specified!";
      }
    }

    return result;
  }

  /**
   * Returns an instance of the callable classifier.
   *
   * @return		the classifier
   */
  protected meka.classifiers.multilabel.MultilabelClassifier getClassifierInstance() {
    return (meka.classifiers.multilabel.MultilabelClassifier) CallableActorHelper.getSetup(meka.classifiers.multilabel.MultilabelClassifier.class, m_Classifier, this);
  }
}
