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
 * RabbitMQRemoteTrigger.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.control;

import adams.flow.container.EncapsulatedActorsContainer;
import adams.flow.core.Actor;
import adams.flow.core.ActorExecution;
import adams.flow.core.ActorHandlerInfo;
import adams.flow.core.EncapsulateActors;
import adams.flow.core.InputConsumer;
import adams.flow.core.OutputProducer;
import adams.flow.core.Unknown;
import com.rabbitmq.client.DeliverCallback;

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
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class RabbitMQRemoteTrigger
  extends AbstractRabbitMQControlActor
  implements InputConsumer, OutputProducer {

  private static final long serialVersionUID = 5816569944356142679L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Encapsulates a sequence of actors to be executed remotely. The first actor must be "
	+ "a source.";
  }

  /**
   * Checks the tee actor before it is set.
   * Returns an error message if the actor is not acceptable, null otherwise.
   *
   * @param index	the index the actor gets set
   * @param actor	the actor to check
   * @return		null if accepted, otherwise error message
   */
  protected String checkSubActor(int index, Actor actor) {
    return null;
  }

  /**
   * Checks the tee actor before it is set via the setTeeActor method.
   * Returns an error message if the actor is not acceptable, null otherwise.
   *
   * @param actors	the actors to check
   * @return		null if accepted, otherwise error message
   */
  protected String checkSubActors(Actor[] actors) {
    int		i;

    if (actors.length > 0) {
      for (i = 0; i < actors.length; i++) {
	if (actors[i].getSkip())
	  continue;
	if (!(actors[i] instanceof InputConsumer))
	  return "You need to provide an actor that processes input, '" + actors[i].getName() + "'/" + actors[i].getClass().getName() + " doesn't!";
      }
    }

    return null;
  }

  /**
   * Returns some information about the actor handler, e.g., whether it can
   * contain standalones and the actor execution.
   *
   * @return		the info
   */
  @Override
  public ActorHandlerInfo getActorHandlerInfo() {
    return new ActorHandlerInfo()
      .allowStandalones(true)
      .allowSource(true)
      .actorExecution(ActorExecution.SEQUENTIAL)
      .forwardsInput(true);
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Unknown.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  public Class[] generates() {
    return new Class[]{Unknown.class};
  }

  /**
   * Encapsulates the sub-flow.
   *
   * @return		the generated container
   */
  protected EncapsulatedActorsContainer encapsulate() {
    Trigger	sub;
    int		i;

    // generate subflow
    sub = new Trigger();
    sub.setName("source: " + getFullName());
    for (i = 0; i < size(); i++)
      sub.add(get(i).shallowCopy(false));
    sub.setParent(getParent());  // for access to storage
    sub.setVariables(getVariables());

    // encapsulate
    return EncapsulateActors.wrap(sub, m_VariableNames, m_StorageNames, null);
  }

  /**
   * Generates the callback for the data coming back.
   *
   * @return		the callback
   */
  protected DeliverCallback generateDeliverCallback() {
    DeliverCallback 	result;

    result = (consumerTag, delivery) -> {
      m_OutputToken = m_InputToken;
      m_Processing = null;
    };

    return result;
  }
}
