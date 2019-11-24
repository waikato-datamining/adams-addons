import adams.flow.rest.AbstractRESTPluginWithFlowContext

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces

class Echo extends AbstractRESTPluginWithFlowContext {

  @Override
  String globalInfo() {
    return "simple echo server"
  }

  @GET
  @Path("/echo/{input}")
  @Produces("text/plain")
  public String ping(@PathParam("input") String input) {
    getLogger().info("input: " + input)
    return input
  }

}