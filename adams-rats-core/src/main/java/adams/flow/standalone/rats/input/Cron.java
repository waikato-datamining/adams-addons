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
 * Cron.java
 * Copyright (C) 2014-2022 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.standalone.rats.input;

import adams.core.QuickInfoHelper;
import adams.core.base.CronSchedule;
import adams.flow.core.ActorUtils;
import adams.flow.core.EventHelper;
import org.quartz.CronScheduleBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.ScheduleBuilder;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

import java.util.Date;

/**
 <!-- globalinfo-start -->
 * Uses a cronjob for defining the execution of the base rat input.<br>
 * For more information on the scheduler format see:<br>
 * http:&#47;&#47;www.quartz-scheduler.org&#47;docs&#47;tutorials&#47;crontrigger.html
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-input &lt;adams.flow.standalone.rats.input.RatInput&gt; (property: input)
 * &nbsp;&nbsp;&nbsp;The receiver to wrap.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.standalone.rats.input.DummyInput
 * </pre>
 * 
 * <pre>-schedule &lt;adams.core.base.CronSchedule&gt; (property: schedule)
 * &nbsp;&nbsp;&nbsp;The schedule for executing the base rat input; format 'SECOND MINUTE HOUR 
 * &nbsp;&nbsp;&nbsp;DAYOFMONTH MONTH WEEKDAY [YEAR]'.
 * &nbsp;&nbsp;&nbsp;default: 0 0 1 * * ?
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class Cron
  extends AbstractMetaRatInput {

  /** for serialization. */
  private static final long serialVersionUID = 1143927005847523885L;

  /** the dummy data to feed into the flow. */
  public static final String CRONDUMMY = "crondummy";

  /**
   * Encapsulates a job to run.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   */
  public static class CronJob
    implements Job {

    /**
     * Gets executed when the cron event gets triggered.
     *
     * @param context			the context of the execution
     * @throws JobExecutionException	if job fails
     */
    public void execute(JobExecutionContext context) throws JobExecutionException {
      String	result;
      Cron	owner;

      owner = (Cron) context.getJobDetail().getJobDataMap().get(KEY_OWNER);

      if (owner.isReceptionRunning()) {
        owner.getLogger().warning("Busy, skipping cron execution!");
        return;
      }

      // skip if paused or currently running
      if (owner.getOwner().isPaused() || !owner.canReceive() || owner.isReceptionRunning())
	return;
      
      result = owner.receiveData();
      if (result != null)
	owner.getLogger().warning(result);
    }
  }

  /** the key for the owner in the JobExecutionContent. */
  public final static String KEY_OWNER = "owner";

  /** the cron schedule. */
  protected CronSchedule m_Schedule;

  /** the scheduler. */
  protected Scheduler m_Scheduler;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Uses a cronjob for defining the execution of the base rat input."
	+ "\n"
	+ "For more information on the scheduler format see:\n"
	+ "http://www.quartz-scheduler.org/docs/tutorials/crontrigger.html";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "schedule", "schedule",
	    new CronSchedule(CronSchedule.DEFAULT));
  }

  /**
   * Sets the execution schedule.
   *
   * @param value 	the schedule
   */
  public void setSchedule(CronSchedule value) {
    m_Schedule = value;
    reset();
  }

  /**
   * Returns the execution schedule.
   *
   * @return 		the schedule
   */
  public CronSchedule getSchedule() {
    return m_Schedule;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String scheduleTipText() {
    return
        "The schedule for executing the base rat input; "
      + "format 'SECOND MINUTE HOUR DAYOFMONTH MONTH WEEKDAY [YEAR]'.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "schedule", m_Schedule.getValue());
  }

  /**
   * Returns the type of data this scheme generates.
   * 
   * @return		the type of data
   */
  @Override
  public Class generates() {
    return m_Input.generates();
  }

  /**
   * Returns whether the reception is currently running.
   *
   * @return		true if running
   */
  @Override
  public boolean isReceptionRunning() {
    return m_Input.isReceptionRunning();
  }

  /**
   * Retrieves data using the base input.
   * 
   * @return		null if successful, otherwise error message
   */
  protected String receiveData() {
    String	result;
    
    result = m_Input.receive();
    if (result == null) {
      if (m_Input instanceof DummyCronInput) {
	m_Data.add(CRONDUMMY);
      }
      else {
	while (m_Input.hasPendingOutput())
	  m_Data.add(m_Input.output());
      }
    }
    
    return result;
  }

  /**
   * Initializes the reception.
   *
   * @return		null if successfully initialized, otherwise error message
   */
  @Override
  public String initReception() {
    String		result;
    JobDetail		job;
    JobBuilder 		jBuilder;
    TriggerBuilder 	tBuilder;
    ScheduleBuilder 	sBuilder;
    Trigger 		trigger;
    Date		first;
    String		prefix;

    result = super.initReception();

    if (result == null) {
      try {
	if (m_Scheduler == null) {
	  m_Scheduler = EventHelper.getDefaultScheduler(ActorUtils.getFlowID(getOwner()));
	  prefix = ActorUtils.getFlowID(getOwner()) + "." + getFullName();
	  jBuilder = JobBuilder
	    .newJob(CronJob.class)
	    .storeDurably()
	    .withIdentity(prefix + ".job", prefix + ".group");
	  job = jBuilder.build();
	  job.getJobDataMap().put(KEY_OWNER, this);
	  sBuilder = CronScheduleBuilder.cronSchedule(m_Schedule.getValue());
	  tBuilder = TriggerBuilder
	    .newTrigger()
	    .withIdentity(prefix + ".trigger", prefix + ".group")
	    .forJob(prefix + ".job", prefix + ".group")
	    .withSchedule(sBuilder);
	  trigger = tBuilder.build();
	  m_Scheduler.addJob(job, true);
	  first = m_Scheduler.scheduleJob(trigger);
	  if (isLoggingEnabled())
	    getLogger().info("First execution of actor: " + first);
	  m_Scheduler.start();
	}
      }
      catch (Exception e) {
	result = handleException("Failed to set up cron job: ", e);
      }
    }

    return result;
  }

  /**
   * Hook method that calls the base-input's receive() method using a cronjob.
   * 
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String callReceive() {
    doWait(100);
    return null;
  }

  /**
   * Stops the internal cron scheduler, if possible.
   */
  protected void stopScheduler() {
    if (m_Scheduler != null) {
      try {
        getLogger().info("Shutting down scheduler for flow ID: " + ActorUtils.getFlowID(getOwner()));
	m_Scheduler.shutdown(true);
	m_Scheduler = null;
      }
      catch (Exception e) {
	handleException("Error shutting down scheduler:", e);
      }
    }
  }

  /**
   * Stops the execution.
   */
  @Override
  public synchronized void stopExecution() {
    stopScheduler();
    super.stopExecution();
  }
}
