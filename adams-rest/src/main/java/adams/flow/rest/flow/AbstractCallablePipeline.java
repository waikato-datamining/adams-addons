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
 * AbstractCallablePipeline.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.rest.flow;

import adams.core.MessageCollection;
import adams.core.Utils;
import adams.flow.control.LocalScopeTransformer;
import adams.flow.control.ScopeHandler.ScopeHandling;
import adams.flow.core.AbstractCallableActor;
import adams.flow.core.Actor;
import adams.flow.core.ActorUtils;
import adams.flow.core.CallableActorHelper;
import adams.flow.core.CallableActorReference;
import adams.flow.core.CallableActorUser;
import adams.flow.core.Token;
import adams.flow.core.actorfilter.SuperclassOrInterface;
import adams.flow.rest.AbstractRESTPluginWithFlowContext;

import java.util.List;

/**
 * Ancestor for pipeline that process data with a callable pipeline template.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractCallablePipeline<T>
  extends AbstractRESTPluginWithFlowContext
  implements CallableActorUser {

  private static final long serialVersionUID = -4960256014415499156L;

  /** the callable transformer to use as processing pipeline. */
  protected CallableActorReference m_Pipeline;

  /** the actual actor. */
  protected transient Actor m_PipelineActor;

  /** whether the pipeline actor has been set up. */
  protected boolean m_PipelineActorInitialized;

  /** how to handle the variables. */
  protected ScopeHandling m_ScopeHandlingVariables;

  /** how to handle the storage. */
  protected ScopeHandling m_ScopeHandlingStorage;

  /** the helper class. */
  protected CallableActorHelper m_Helper;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  protected String globalInfoBase() {
    return "Uses the specified callable transformer as template for processing "
      + "the incoming data and sending back the resulting data.\n"
      + "A copy of the callable transformer gets created with each request. "
      + "The transformer itself gets wrapped in a " + Utils.classToString(LocalScopeTransformer.class)
      + ", with the specified handling of variables and storage.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "pipeline", "pipeline",
      new CallableActorReference());

    m_OptionManager.add(
      "scope-handling-variables", "scopeHandlingVariables",
      ScopeHandling.EMPTY);

    m_OptionManager.add(
      "scope-handling-storage", "scopeHandlingStorage",
      ScopeHandling.EMPTY);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Helper = new CallableActorHelper();
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_PipelineActor = null;
  }

  /**
   * Sets the name of the callable actor to use as pipeline.
   *
   * @param value 	the callable name
   */
  public void setPipeline(CallableActorReference value) {
    m_Pipeline = value;
    reset();
  }

  /**
   * Returns the name of the callable actor to use as pipeline.
   *
   * @return 		the callable name
   */
  public CallableActorReference getPipeline() {
    return m_Pipeline;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String pipelineTipText() {
    return "The name of the callable actor to use as pipeline for processing data.";
  }

  /**
   * Sets how to handle variables into the local scope.
   *
   * @param value	the scope handling
   */
  public void setScopeHandlingVariables(ScopeHandling value) {
    m_ScopeHandlingVariables = value;
    reset();
  }

  /**
   * Returns how variables are handled in the local scope.
   *
   * @return		the scope handling
   */
  public ScopeHandling getScopeHandlingVariables() {
    return m_ScopeHandlingVariables;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String scopeHandlingVariablesTipText() {
    return
	"Defines how variables are handled in the local scope; whether to "
	+ "start with empty set, a copy of the outer scope variables or "
	+ "share variables with the outer scope.";
  }

  /**
   * Sets how to handle storage in the local scope.
   *
   * @param value	the scope handling
   */
  public void setScopeHandlingStorage(ScopeHandling value) {
    m_ScopeHandlingStorage = value;
    reset();
  }

  /**
   * Returns how storage is handled in the local scope.
   *
   * @return		the scope handling
   */
  public ScopeHandling getScopeHandlingStorage() {
    return m_ScopeHandlingStorage;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String scopeHandlingStorageTipText() {
    return
	"Defines how storage is handled in the local scope; whether to "
	+ "start with empty set, a (deep) copy of the outer scope storage or "
	+ "share the storage with the outer scope.";
  }

  /**
   * Returns the currently set callable actor.
   *
   * @return		the actor, can be null
   */
  @Override
  public Actor getCallableActor() {
    return m_PipelineActor;
  }

  /**
   * Hook method to add compatibility checks.
   * <br>
   * Default implementation does nothing.
   *
   * @return		null if compatible, otherwise error message
   */
  protected String checkCompatibility() {
    return null;
  }

  /**
   * Initializes the pipeline.
   *
   * @return		null if successfully initialized
   */
  protected String initPipeline() {
    String		result;
    List<Actor>		callables;

    result = null;

    if (!m_PipelineActorInitialized) {
      m_PipelineActor = m_Helper.findCallableActorRecursive(m_FlowContext, getPipeline());
      if (m_PipelineActor == null) {
	result = "Couldn't find pipeline actor '" + getPipeline() + "'!";
      }
      else {
	if (!ActorUtils.isTransformer(m_PipelineActor))
	  result = "Pipeline actor '" + getPipeline() + "' is not a transformer!";
	else
	  result = checkCompatibility();
      }
      // nested callable actors are not supported/allowed
      if (result == null) {
        callables = ActorUtils.enumerate(m_PipelineActor, new SuperclassOrInterface(AbstractCallableActor.class));
        if (callables.size() > 0) {
	  result = "Using callable actors is not permitted as part of the pipeline! The following callable actors were found:\n";
	  for (Actor callable: callables)
	    result += "\n" + callable.getFullName();
	}
      }
      m_PipelineActorInitialized = true;
    }
    else {
      if (m_PipelineActor == null)
	result = "No pipeline actor available!";
    }

    return result;
  }

  /**
   * Processes the incoming data.
   *
   * @param input	the input data
   * @param errors	for collecting errors
   * @return		the generated output, null if none generated
   */
  protected Object doProcess(Object input, MessageCollection errors) {
    Object			result;
    String			msg;
    LocalScopeTransformer	scope;
    Actor			pipeline;

    result = null;

    msg = initPipeline();
    if (msg != null) {
      errors.add(msg);
      return null;
    }

    scope = null;
    try {
      pipeline = m_PipelineActor.shallowCopy();
      scope = new LocalScopeTransformer();
      scope.removeAll();
      scope.add(pipeline);
      scope.setScopeHandlingVariables(m_ScopeHandlingVariables);
      scope.setScopeHandlingStorage(m_ScopeHandlingStorage);
      scope.setParent(getFlowContext());
      msg = scope.setUp();
      if (msg != null) {
        errors.add("Failed to setup pipeline (" + getPipeline() + "):\n" + msg);
        return null;
      }

      scope.input(new Token(input));
      msg = scope.execute();
      if (msg != null) {
        errors.add("Failed to execute pipeline (" + getPipeline() + "):\n" + msg);
        return null;
      }

      if (scope.hasPendingOutput())
        result = scope.output().getPayload();
      if (result == null)
        errors.add("Pipeline generated no output (" + getPipeline() + ")!");
    }
    catch (Exception e) {
      errors.add("Failed to process data: " + input, e);
    }
    finally {
      if (scope != null)
        scope.cleanUp();
    }

    return result;
  }
}
