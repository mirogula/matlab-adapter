package sk.stuba.fei.jlab.matlabadapter.rest;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

@Path("/")
@Produces(MediaType.TEXT_PLAIN)
public class UtilService {
	
	@Context
	Request request;
	
	@GET
	@Path("/ping")
	public String ping() {
		return "pong";
	}
	
	@GET
	@Path("/info")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public Response getVersion() {
		Map<String, String> versionInfo = new HashMap<>();
		versionInfo.put("version", MatlabService.MATLAB_ADAPTER_VERSION);
		return Response.ok().type(MediaType.APPLICATION_JSON_TYPE).entity(versionInfo).build();
	}
	
}
