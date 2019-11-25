import adams.flow.rest.AbstractParametrizedGroovyRESTPlugin

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces

/**
 * Supports the "uppercase" boolean option. If set, returns the input
 * string in uppercase, otherwise as is.
 */
class Echo extends AbstractParametrizedGroovyRESTPlugin {

  @Override
  String globalInfo() {
    return "simple echo server with optional uppercasing of the input"
  }

  @GET
  @Path("/echo2/{input}")
  @Produces("text/plain")
  public String ping(@PathParam("input") String input) {
    getLogger().info("input: " + input)
    if (getAdditionalOptions().getBoolean("uppercase"))
      return input.toUpperCase()
    else
      return input
  }

}