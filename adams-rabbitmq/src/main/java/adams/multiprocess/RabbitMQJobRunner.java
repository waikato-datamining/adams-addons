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
import adams.core.Utils;
import adams.core.base.BasePassword;
import adams.core.net.rabbitmq.RabbitMQHelper;
import adams.event.JobCompleteEvent;
import adams.event.JobCompleteListener;
import com.github.fracpete.javautils.enumerate.Enumerated;
import com.rabbitmq.client.AMQP;
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

  /** the SSH host. */
  protected String m_Host;

  /** the SSH port. */
  protected int m_Port;

  /** database username. */
  protected String m_User;

  /** database password. */
  protected BasePassword m_Password;

  /** the queue in use. */
  protected String m_Queue;

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
      "host", "host",
      "");

    m_OptionManager.add(
      "port", "port",
      AMQP.PROTOCOL.PORT, 1, 65535);

    m_OptionManager.add(
      "user", "user",
      "", false);

    m_OptionManager.add(
      "password", "password",
      new BasePassword(), false);

    m_OptionManager.add(
      "queue", "queue",
      "");
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
   * Sets the host to connect to.
   *
   * @param value	the host name/ip
   */
  public void setHost(String value) {
    m_Host = value;
    reset();
  }

  /**
   * Returns the host to connect to.
   *
   * @return		the host name/ip
   */
  public String getHost() {
    return m_Host;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String hostTipText() {
    return "The host (name/IP address) to connect to.";
  }

  /**
   * Sets the port to connect to.
   *
   * @param value	the port
   */
  public void setPort(int value) {
    if (getOptionManager().isValid("port", value)) {
      m_Port = value;
      reset();
    }
  }

  /**
   * Returns the port to connect to.
   *
   * @return 		the port
   */
  public int getPort() {
    return m_Port;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String portTipText() {
    return "The port to connect to.";
  }

  /**
   * Sets the database user.
   *
   * @param value	the user
   */
  public void setUser(String value) {
    m_User = value;
    reset();
  }

  /**
   * Returns the database user.
   *
   * @return 		the user
   */
  public String getUser() {
    return m_User;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String userTipText() {
    return "The database user to connect with.";
  }

  /**
   * Sets the database password.
   *
   * @param value	the password
   */
  public void setPassword(BasePassword value) {
    m_Password = value;
    reset();
  }

  /**
   * Returns the database password.
   *
   * @return 		the password
   */
  public BasePassword getPassword() {
    return m_Password;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String passwordTipText() {
    return "The password of the database user.";
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
   * Returns the database connection in use. Reconnects the database, to make
   * sure that the database connection is the correct one.
   *
   * @return		the connection object
   */
  protected com.rabbitmq.client.Connection retrieveConnection() {
    ConnectionFactory factory;

    factory = new ConnectionFactory();
    factory.setHost(m_Host);
    factory.setPort(m_Port);
    if (!m_User.isEmpty()) {
      factory.setUsername(m_User);
      factory.setPassword(m_Password.getValue());
    }
    try {
      return factory.newConnection();
    }
    catch (Exception e) {
      Utils.handleException(this, "Failed to connect to broker " + m_Host + ":" + m_Port, e);
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
        result = "Failed to connect to broker " + m_Host + ":" + m_Port;
    }

    if (result == null) {
      try {
	m_Channel = m_Connection.createChannel();
	if (m_Channel == null)
	  result = "Failed to create a channel!";
      }
      catch (Exception e) {
        result = Utils.handleException(this, "Failed to create channel!", e);
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
    String						result;
    adams.core.net.rabbitmq.send.BinaryConverter 	convSnd;
    byte[]						ser;
    MessageCollection					errors;
    List<byte[]>					runners;
    LocalJobRunner 					runner;
    BasicProperties 					props;

    result = null;

    // callback queue
    m_CallbackQueue = null;
    try {
      m_CallbackQueue = m_Channel.queueDeclare().getQueue();
    }
    catch (Exception e) {
      result = Utils.handleException(this, "Failed to create queue!", e);
    }

    // convert jobs
    m_Processing = new HashSet<>();
    runners      = new ArrayList<>();
    if (result == null) {
      convSnd = new adams.core.net.rabbitmq.send.BinaryConverter();
      errors  = new MessageCollection();
      for (Enumerated<T> enm : enumerate(getJobs())) {
        runner = new LocalJobRunner();
        runner.getMetaData().put("index", enm.index);
        runner.add(enm.value);
        m_Processing.add(enm.index);
        ser = convSnd.convert(runner, errors);
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
	  result = Utils.handleException(this, "Failed to publish job (queue=" + m_Queue + ")!", e);
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
        result = Utils.handleException(this, "Failed to purge queue: " + m_Queue, e);
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
        result = Utils.handleException(this, "Failed to delete queue: " + m_CallbackQueue, e);
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
  }

  /**
   * Performing actual stop.
   *
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doStop() {
    String						result;
    adams.core.net.rabbitmq.receive.BinaryConverter	convRec;
    DeliverCallback 					deliverCallback;
    String						msg;

    result = null;

    // receive
    convRec = new adams.core.net.rabbitmq.receive.BinaryConverter();
    try {
      deliverCallback = (consumerTag, delivery) -> {
	byte[] dataRec = delivery.getBody();
	MessageCollection errorsRec = new MessageCollection();
	LocalJobRunner jobrunner = (LocalJobRunner) convRec.convert(dataRec, errorsRec);
	if (jobrunner != null) {
	  Integer index = (Integer) jobrunner.getMetaData().get("index");
	  if (index != null) {
	    if (isLoggingEnabled())
	      getLogger().info("Job #" + index + " received");
	    m_Jobs.set(index, (T) jobrunner.getJobs().get(0));
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
      result = Utils.handleException(this, "Failed to receive data!", e);
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
