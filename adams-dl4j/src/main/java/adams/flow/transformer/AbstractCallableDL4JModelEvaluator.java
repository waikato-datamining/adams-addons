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
 * AbstractCallableDL4JModelEvaluator.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer;

import adams.core.MessageCollection;
import adams.core.QuickInfoHelper;
import adams.flow.core.CallableActorHelper;
import adams.flow.core.CallableActorReference;
import adams.flow.source.DL4JModelConfigurator;
import adams.ml.dl4j.model.ModelConfigurator;

/**
 * Ancestor for model evaluators that make use of a callable model configurator.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractCallableDL4JModelEvaluator
  extends AbstractDL4JModelEvaluator {

  /** for serialization. */
  private static final long serialVersionUID = 3440872619963043115L;

  /** the name of the callable model configurator. */
  protected CallableActorReference m_Model;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "model", "model",
      new CallableActorReference(DL4JModelConfigurator.class.getSimpleName()));
  }

  /**
   * Sets the name of the callable model to use.
   *
   * @param value	the name
   */
  public void setModel(CallableActorReference value) {
    m_Model = value;
    reset();
  }

  /**
   * Returns the name of the callable model in use.
   *
   * @return		the name
   */
  public CallableActorReference getModel() {
    return m_Model;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public abstract String modelTipText();

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "model", m_Model);
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
      variable = getOptionManager().getVariableForProperty("model");
      if (variable == null) {
	if (m_Model.isEmpty())
	  result = "No model specified!";
      }
    }

    return result;
  }

  /**
   * Returns an instance of the callable model configurator.
   *
   * @return		the model configurator
   * @throws Exception  if fails to obtain model
   */
  protected ModelConfigurator getModelConfiguratorInstance() throws Exception {
    ModelConfigurator	result;
    MessageCollection	errors;

    errors = new MessageCollection();
    result = (ModelConfigurator) CallableActorHelper.getSetup(ModelConfigurator.class, m_Model, this, errors);
    if (result == null) {
      if (errors.isEmpty())
	throw new IllegalStateException("Failed to obtain model configurator from '" + m_Model + "'!");
      else
	throw new IllegalStateException("Failed to obtain model configurator from '" + m_Model + "':\n" + errors);
    }

    return result;
  }
}
