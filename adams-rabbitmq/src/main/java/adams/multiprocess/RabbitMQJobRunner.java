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
 * RabbitMQJobRunner.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.multiprocess;

import adams.core.MessageCollection;
import adams.core.QuickInfoHelper;
import adams.core.logging.LoggingHelper;
import adams.core.net.rabbitmq.RabbitMQHelper;
import adams.core.net.rabbitmq.connection.AbstractConnectionFactory;
import adams.core.net.rabbitmq.connection.GuestConnectionFactory;
import adams.event.JobCompleteEvent;
import adams.event.JobCompleteListener;
import com.github.fracpete.javautils.enumerate.Enumerated;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static com.github.fracpete.javautils.Enumerate.enumerate;

/**
 * JobRunner distributing jobs via a RabbitMQ broker.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class RabbitMQJobRunner<T extends Job>
  extends AbstractJobRunner<T> {

  private static final long serialVersionUID = 8430171807757802783L;

  /** the connection to use. */
  protected AbstractConnectionFactory m_ConnectionFactory;

  /** the prefetch count. */
  protected int m_PrefetchCount;

  /** the queue in use. */
  protected String m_Queue;

  /** the converter for sending. */
  protected adams.core.net.rabbitmq.send.AbstractConverter m_SendConverter;

  /** the converter for receiving. */
  protected adams.core.net.rabbitmq.receive.AbstractConverter m_ReceiveConverter;

  /** whether to distribute the jobs or run them on the same machine. */
  protected boolean m_DistributeJobs;

  /** the connection. */
  protected transient com.rabbitmq.client.Connection m_Connection;

  /** the channel action to use. */
  protected transient Channel m_Channel;

  /** call when job complete. */
  protected transient HashSet<JobCompleteListener> m_JobCompleteListeners;

  /** the callback queue. */
  protected String m_CallbackQueue;

  /** all the jobs. */
  protected List<T> m_Jobs;

  /** the jobs that are still being processed. */
  protected Set<Integer> m_Processing;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "JobRunner distributing jobs via a RabbitMQ broker.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "connection-factory", "connectionFactory",
      new GuestConnectionFactory());

    m_OptionManager.add(
      "prefetch-count", "prefetchCount",
      1, 0, null);

    m_OptionManager.add(
      "queue", "queue",
      "");

    m_OptionManager.add(
      "send-converter", "sendConverter",
      new adams.core.net.rabbitmq.send.BinaryConverter());

    m_OptionManager.add(
      "receive-converter", "receiveConverter",
      new adams.core.net.rabbitmq.receive.BinaryConverter());

    m_OptionManager.add(
      "distribute-jobs", "distributeJobs",
      false);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Jobs = new ArrayList<>();
  }

  /**
   * Sets the base connection factory to encrypt.
   *
   * @param value	the factory
   */
  public void setConnectionFactory(AbstractConnectionFactory value) {
    m_ConnectionFactory = value;
    reset();
  }

  /**
   * Returns the base connection factory to encrypt.
   *
   * @return		the factory
   */
  public AbstractConnectionFactory getConnectionFactory() {
    return m_ConnectionFactory;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String connectionFactoryTipText() {
    return "The base connection factory to encrypt.";
  }

  /**
   * Sets the maximum number of unacked jobs a client can pull off a queue.
   *
   * @param value	the count, 0 = unlimited, 1 = fair
   */
  public void setPrefetchCount(int value) {
    m_PrefetchCount = value;
    reset();
  }

  /**
   * Returns the maximum number of unacked jobs a client can pull off a queue.
   *
   * @return		the count, 0 = unlimited, 1 = fair
   */
  public int getPrefetchCount() {
    return m_PrefetchCount;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String prefetchCountTipText() {
    return "The number of un-acked jobs a client can pull off a queue; 0 = unlimited, 1 = fair.";
  }

  /**
   * Sets the name of the queue.
   *
   * @param value	the name
   */
  public void setQueue(String value) {
    m_Queue = value;
    reset();
  }

  /**
   * Returns the name of the queue.
   *
   * @return 		the name
   */
  public String getQueue() {
    return m_Queue;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String queueTipText() {
    return "The name of the queue.";
  }

  /**
   * Sets the converter to use for sending.
   *
   * @param value	the converter
   */
  public void setSendConverter(adams.core.net.rabbitmq.send.AbstractConverter value) {
    m_SendConverter = value;
    reset();
  }

  /**
   * Returns the converter to use for sending.
   *
   * @return 		the converter
   */
  public adams.core.net.rabbitmq.send.AbstractConverter getSendConverter() {
    return m_SendConverter;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String sendConverterTipText() {
    return "The converter to use for sending.";
  }

  /**
   * Sets the converter to use for receiving.
   *
   * @param value	the converter
   */
  public void setReceiveConverter(adams.core.net.rabbitmq.receive.AbstractConverter value) {
    m_ReceiveConverter = value;
    reset();
  }

  /**
   * Returns the converter to use for receiving.
   *
   * @return 		the converter
   */
  public adams.core.net.rabbitmq.receive.AbstractConverter getReceiveConverter() {
    return m_ReceiveConverter;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String receiveConverterTipText() {
    return "The converter to use for receiving data.";
  }

  /**
   * Sets whether to distribute the jobs via separate messages.
   *
   * @param value	true if to distribute
   */
  public void setDistributeJobs(boolean value) {
    m_DistributeJobs = value;
    reset();
  }

  /**
   * Returns whether to distribute the jobs via separate messages.
   *
   * @return		true if to distribute
   */
  public boolean getDistributeJobs() {
    return m_DistributeJobs;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String distributeJobsTipText() {
    return "If enabled, the jobs get distributed via separate messages.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "connectionFactory", m_ConnectionFactory, "connection: ");
    result += QuickInfoHelper.toString(this, "prefetchCount", (m_PrefetchCount == 0 ? "unlimited" : "" + m_PrefetchCount), ", prefetch: ");
    result += QuickInfoHelper.toString(this, "queue", m_Queue, ", queue: ");
    result += QuickInfoHelper.toString(this, "sendConverter", m_SendConverter, ", send: ");
    result += QuickInfoHelper.toString(this, "receiveConverter", m_ReceiveConverter, ", receive: ");
    result += QuickInfoHelper.toString(this, "distributeJobs", m_DistributeJobs, "distribute jobs", ", ");

    return result;
  }

  /**
   * Returns the database connection in use. Reconnects the database, to make
   * sure that the database connection is the correct one.
   *
   * @return		the connection object
   */
  protected com.rabbitmq.client.Connection retrieveConnection() {
    ConnectionFactory 	factory;
    MessageCollection	errors;

    errors = new MessageCollection();
    factory = m_ConnectionFactory.generate(errors);
    if (!errors.isEmpty())
      return null;

    try {
      return factory.newConnection();
    }
    catch (Exception e) {
      errors.add("Failed to connect to broker (" + m_ConnectionFactory + ")!", e);
      LoggingHelper.handleException(this, "Failed to connect to broker (" + m_ConnectionFactory + ")!", e);
      return null;
    }
  }

  /**
   * Before actual start up.
   *
   * @return		null if successful, otherwise error message
   */
  protected String preStart() {
    String	result;

    result = super.preStart();

    if (result == null) {
      m_Connection = retrieveConnection();
      if (m_Connection == null)
        result = "Failed to connect to broker (" + m_ConnectionFactory + ")!";
    }

    if (result == null) {
      try {
	m_Channel = m_Connection.createChannel();
	if (m_Channel == null)
	  result = "Failed to create a channel!";
	else
	  m_Channel.basicQos(m_PrefetchCount);
      }
      catch (Exception e) {
        result = LoggingHelper.handleException(this, "Failed to create channel!", e);
      }
    }

    return result;
  }

  /**
   * Performing actual start up.
   * Only gets executed if {@link #preStart()} was successful.
   *
   * @return		null if successful, otherwise error message
   * @see		#preStart()
   */
  @Override
  protected String doStart() {
    String		result;
    byte[]		ser;
    MessageCollection	errors;
    List<byte[]>	runners;
    LocalJobRunner 	runner;
    BasicProperties 	props;

    result = null;

    m_SendConverter.setFlowContext(getFlowContext());
    m_ReceiveConverter.setFlowContext(getFlowContext());

    // callback queue
    m_CallbackQueue = null;
    try {
      m_CallbackQueue = m_Channel.queueDeclare().getQueue();
    }
    catch (Exception e) {
      result = LoggingHelper.handleException(this, "Failed to create queue!", e);
    }

    // convert jobs
    m_Processing = new HashSet<>();
    runners      = new ArrayList<>();
    if (result == null) {
      errors  = new MessageCollection();
      if (m_DistributeJobs) {
	for (Enumerated<T> enm : enumerate(getJobs())) {
	  runner = new LocalJobRunner();
	  runner.getMetaData().put("index", enm.index);
	  runner.add(enm.value);
	  m_Processing.add(enm.index);
	  ser = m_SendConverter.convert(runner, errors);
	  if (ser != null)
	    runners.add(ser);
	}
      }
      else {
        runner = new LocalJobRunner();
        for (Job job: getJobs())
	  runner.add(job);
	runner.getMetaData().put("index", 0);
        m_Processing.add(0);
	ser = m_SendConverter.convert(runner, errors);
	if (ser != null)
	  runners.add(ser);
      }
      if (!errors.isEmpty())
        result = errors.toString();
    }

    // send jobs
    if (result == null) {
      props = new BasicProperties.Builder()
	.replyTo(m_CallbackQueue)
	.build();
      for (byte[] r: runners) {
	try {
	  m_Channel.basicPublish("", m_Queue, props, r);
	}
	catch (Exception e) {
	  result = LoggingHelper.handleException(this, "Failed to publish job (queue=" + m_Queue + ")!", e);
	}
	if (result != null)
	  break;
      }
    }

    return result;
  }

  /**
   * Purges the job queue.
   *
   * @return		null if successful or no channel available, otherwise error message
   */
  protected String purgeQueue() {
    String	result;

    result = null;

    if (m_Channel != null) {
      try {
	m_Channel.queuePurge(m_Queue);
      }
      catch (Exception e) {
        result = LoggingHelper.handleException(this, "Failed to purge queue: " + m_Queue, e);
      }
    }

    return result;
  }

  /**
   * Deletes the callback queue.
   *
   * @return		null if successful or no channel available, otherwise error message
   */
  protected String deleteCallbackQueue() {
    String	result;

    result = null;

    if (m_Channel != null) {
      try {
	m_Channel.queueDelete(m_CallbackQueue);
      }
      catch (Exception e) {
        result = LoggingHelper.handleException(this, "Failed to delete queue: " + m_CallbackQueue, e);
      }
    }

    return result;
  }

  /**
   * Closes the channel and connection.
   */
  protected void close() {
    RabbitMQHelper.closeQuietly(m_Channel);
    RabbitMQHelper.closeQuietly(m_Connection);
    m_Channel    = null;
    m_Connection = null;
  }

  /**
   * Performing actual stop.
   *
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doStop() {
    String		result;
    DeliverCallback 	deliverCallback;
    String		msg;

    result = null;

    // receive
    try {
      deliverCallback = (consumerTag, delivery) -> {
	byte[] dataRec = delivery.getBody();
	MessageCollection errorsRec = new MessageCollection();
	LocalJobRunner jobrunner = (LocalJobRunner) m_ReceiveConverter.convert(dataRec, errorsRec);
	if (jobrunner != null) {
	  Integer index = (Integer) jobrunner.getMetaData().get("index");
	  if (index != null) {
	    if (isLoggingEnabled())
	      getLogger().info("Job #" + index + " received");
	    if (m_DistributeJobs) {
	      m_Jobs.set(index, (T) jobrunner.getJobs().get(0));
	    }
	    else {
	      for (int i = 0; i < jobrunner.getJobs().size(); i++)
		m_Jobs.set(i, (T) jobrunner.getJobs().get(i));
	    }
	    m_Processing.remove(index);
	  }
	  else {
	    getLogger().warning("No job index stored in meta-data of jobrunner?");
	  }
	}
      };

      while (m_Processing.size() > 0)
	m_Channel.basicConsume(m_CallbackQueue, true, deliverCallback, consumerTag -> {});
    }
    catch (Exception e) {
      result = LoggingHelper.handleException(this, "Failed to receive data!", e);
    }

    msg = doTerminate(false);

    if (msg != null) {
      if (result == null)
        result = msg;
      else
        result += "\n" + msg;
    }

    return result;
  }

  /**
   * Performing actual terminate up.
   *
   * @param wait	ignored
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doTerminate(boolean wait) {
    String	result;
    String	msg;

    m_Processing.clear();

    result = purgeQueue();
    msg = deleteCallbackQueue();
    if (msg != null) {
      if (result == null)
        result = msg;
      else
        result += "\n" + msg;
    }
    close();

    return result;
  }

  /**
   * Adds the listener.
   *
   * @param l		the listener to add
   */
  public void addJobCompleteListener(JobCompleteListener l) {
    synchronized(m_JobCompleteListeners) {
      m_JobCompleteListeners.add(l);
    }
  }

  /**
   * Removes the listener.
   *
   * @param l		the listener to remove
   */
  public void removeJobCompleteListener(JobCompleteListener l) {
    synchronized(m_JobCompleteListeners) {
      m_JobCompleteListeners.remove(l);
    }
  }

  /**
   * Notifies all listeners with the given event.
   *
   * @param e		the event to send to the listeners
   */
  protected void notifyJobCompleteListeners(JobCompleteEvent e) {
    Iterator<JobCompleteListener> iter;

    synchronized(m_JobCompleteListeners) {
      iter = m_JobCompleteListeners.iterator();
      while (iter.hasNext())
	iter.next().jobCompleted(e);
    }
  }

  /**
   * Clears all jobs.
   */
  @Override
  public void clear() {
    m_Jobs.clear();
  }

  /**
   * Adds the job to the execution queue.
   *
   * @param job		the job to add
   */
  @Override
  public void add(T job) {
    m_Jobs.add(job);
  }

  /**
   * Adds the jobs to the execution queue.
   *
   * @param jobs	the jobs to add
   */
  @Override
  public void add(JobList<T> jobs) {
    m_Jobs.addAll(jobs);
  }

  /**
   * Returns the list of queued jobs.
   *
   * @return		the jobs
   */
  @Override
  public List<T> getJobs() {
    return m_Jobs;
  }

  /**
   * Job is complete, so check for more to add..
   *
   * @param j	job
   * @param jr	job result
   */
  public void complete(T j, JobResult jr) {
    notifyJobCompleteListeners(new JobCompleteEvent(this, j, jr));
    if (j.getJobCompleteListener() != null)
      j.getJobCompleteListener().jobCompleted(new JobCompleteEvent(this, j, jr));
  }
}
