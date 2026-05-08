package adams.flow.rest.sse;

import adams.flow.control.StorageName;
import adams.flow.control.StorageQueueHandler;
import adams.flow.core.QueueHelper;
import adams.flow.rest.AbstractRESTPluginWithFlowContext;
import org.apache.cxf.rs.security.cors.CrossOriginResourceSharing;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.OutboundSseEvent;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseEventSink;
import java.util.logging.Level;

@Path("/events")
@CrossOriginResourceSharing(allowAllOrigins = true)
public class SSEQueueBroadcastResource
  extends AbstractRESTPluginWithFlowContext {

  private static final long serialVersionUID = -5398548431850555138L;

  /** the name of the queue in the internal storage. */
  protected StorageName m_StorageName;

  /** the poll interval in msec. */
  protected int m_Interval;

  /** the name of the event. */
  protected String m_EventName;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Broadcasts the data from the specified queue.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "storage-name", "storageName",
      new StorageName("queue"));

    m_OptionManager.add(
      "interval", "interval",
      50, 1, null);

    m_OptionManager.add(
      "event-name", "eventName",
      "update");
  }

  /**
   * Sets the name for the queue in the internal storage.
   *
   * @param value	the name
   */
  public void setStorageName(String value) {
    setStorageName(new StorageName(value));
  }

  /**
   * Sets the name for the queue in the internal storage.
   *
   * @param value	the name
   */
  public void setStorageName(StorageName value) {
    m_StorageName = value;
    reset();
  }

  /**
   * Returns the name for the queue in the internal storage.
   *
   * @return		the name
   */
  public StorageName getStorageName() {
    return m_StorageName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String storageNameTipText() {
    return "The name of the queue in the internal storage.";
  }

  /**
   * Sets the polling interval in seconds.
   *
   * @param value	the interval
   */
  public void setInterval(int value) {
    m_Interval = value;
    reset();
  }

  /**
   * Returns the polling interval in milli-seconds.
   *
   * @return		the interval
   */
  public int getInterval() {
    return m_Interval;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String intervalTipText() {
    return "The polling interval in milli-seconds.";
  }

  /**
   * Sets the name to use for the events.
   *
   * @param value	the name
   */
  public void setEventName(String value) {
    m_EventName = value;
  }

  /**
   * Returns the name to use for the events.
   *
   * @return		the name
   */
  public String getEventName() {
    return m_EventName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String eventNameTipText() {
    return "The name to use for the events.";
  }

  @GET
  @Produces(MediaType.SERVER_SENT_EVENTS)
  public void startStream(@Context SseEventSink eventSink, @Context Sse sse) {
    // We run this in a separate thread so we don't block the JAX-RS container
    new Thread(() -> {
      try (eventSink) {
	StorageQueueHandler queue = QueueHelper.getQueue(getFlowContext(), m_StorageName);
	boolean stopped = false;
	if (queue != null) {
	  while (!stopped && !eventSink.isClosed()) {
	    try {
	      if (queue.canRemove()) {
		Object data = queue.remove();
		if (data != null) {
		  getLogger().info("Data obtained from queue: " + m_StorageName);
		  OutboundSseEvent event = sse.newEventBuilder()
					     .name(m_EventName)
					     .data(String.class, data.toString())
					     .build();
		  eventSink.send(event);
		  getLogger().info("Data sent as event: " + m_EventName);
		}
	      }
	      else {
		synchronized (this) {
		  wait(m_Interval);
		}
	      }
	    }
	    catch (Exception e) {
	      stopped = true;
	      getLogger().log(Level.INFO, "Exception occurred!", e);
	    }
	  }
	  getLogger().info("Exiting loop monitoring queue: " + m_StorageName);
	}
	else {
	  getLogger().warning("Queue not found: " + m_StorageName);
	}

	// Sending a 'closing' event
	eventSink.send(sse.newEvent("End of Stream"));
      }
      catch (Exception e) {
	getLogger().log(Level.SEVERE, "SSE error!", e);
      }
    }).start();
  }
}