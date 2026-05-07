package adams.flow.rest.sse;

import adams.flow.rest.AbstractRESTPlugin;
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
public class SSEBroadcastResource
  extends AbstractRESTPlugin {

  private static final long serialVersionUID = -5398548431850555138L;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Runs a continuous thread that sends 10 tickers in one go when contacted.";
  }

  @GET
  @Produces(MediaType.SERVER_SENT_EVENTS)
  public void startStream(@Context SseEventSink eventSink, @Context Sse sse) {
    // We run this in a separate thread so we don't block the JAX-RS container
    new Thread(() -> {
      try (eventSink) {
	for (int i = 1; i <= 10; i++) {
	  // Create an event with a name and data
	  OutboundSseEvent event = sse.newEventBuilder()
				     .name("ticker-update")
				     .data(String.class, "Price Update #" + i)
				     .comment("Optional metadata here")
				     .build();

	  eventSink.send(event);

	  // Simulate a delay between events
	  Thread.sleep(2000);
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