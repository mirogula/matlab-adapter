package sk.stuba.fei.jlab.matlabadapter.rest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

import sk.stuba.fei.jlab.matlabadapter.MatlabAdapterUtils;
import sk.stuba.fei.jlab.matlabadapter.ZipCompressor;
import sk.stuba.fei.jlab.matlabadapter.matlab.SimulinkCommands;
import sk.stuba.fei.weblab.paramproperties.ParamProperties;


/**
 * @author miro
 *
 */
@Path("/matlab")
@Produces(MediaType.TEXT_PLAIN)
public class MatlabService {
	
	public static final String MATLAB_ADAPTER_VERSION = "1.6";
	
	@Context
	private ServletContext servletCtx;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MatlabService.class);

	
	private static final String MATLAB_ADAPTER_ERROR_PREFIX = "matlabadapterError";
	private static final String ERROR_FIELD_SEPARATOR = ":;:";
	
	// general unspecified error
	private static final int ERROR_CODE_GENERAL = 0;
	// when MatlabFacade can not recognize or create class from class name provided by the user
	private static final int ERROR_CODE_INIT_REFLECTION = 1;
	// when MatlabAdapter can not properly comunicate with matlab, because of some internal error in connection with matlab
	private static final int ERROR_CODE_MATLAB_COMM = 2;
	// when user is trying to connect to matlab and MatlabFacade is already connected
	private static final int ERROR_CODE_ALREADY_CONNECTED = 3;
	// when user is trying to communicate with matlab without connecting first
	private static final int ERROR_CODE_NOT_CONNECTED = 4;
	// when upload of model file fails
	private static final int ERROR_CODE_MODEL_UPLOAD_FAILED = 5;
	// when compression of web view file fails
	private static final int ERROR_CODE_COMPRESSION_FAILED = 6;
	// if there was some error while deleting file or directory
	private static final int ERROR_CODE_CANNOT_DELETE_CONTENT = 7;
	
	private static final int ERROR_CODE_FILE_NOT_FOUND = 8;
	
	private static final String ERROR_MESSAGE_GENERAL = "general error";
	private static final String ERROR_MESSAGE_INIT_REFLECTION = "can not recognize or create class";
	private static final String ERROR_MESSAGE_MATLAB_COMM = "cummunication with matlab faild";
	private static final String ERROR_MESSAGE_ALREADY_CONNECTED = "already connected to matlab";
	private static final String ERROR_MESSAGE_NOT_CONNECTED = "not connected to matlab";
	private static final String ERROR_MESSAGE_MODEL_UPLOAD_FAILED = "uploading model file failed";
	private static final String ERROR_MESSAGE_COMPRESSION_FAILED = "can not create or populate zip archive";
	private static final String ERROR_MESSAGE_CANNOT_DELETE_CONTENT = "can not delete file or directory";
	private static final String ERROR_MESSAGE_FILE_NOT_FOUND = "there is no such file";
	

	/**
	 * metoda: get
	 * url: http://server/matlab/startMatlab[?connectionClass=...&workTimeout=...]
	 * 
	 * Spusti instanciu matlabu.
	 * 
	 * @param connectionClass [nepovinny parameter] - retazec znakov (String) reprezentujuci triedu 
	 * pomocou ktorej sa uskutocni spojenie s matlabom. Momentalne su podporovane 2 implementacie: <br>
	 * "sk.stuba.fei.jlab.matlabadapter.matlab.JMatlinkConnection" - spojenie s matlabom sa uskutocni pomocou kniznice JMatLink <br>
	 * "sk.stuba.fei.jlab.matlabadapter.matlab.MatlabcontrolConnection" - spojenie s matlabom sa uskutocni pomocou kniznice Matalbcontrol
	 * @param workTimeout [nepovinny parameter] - ciselna hodnota reprezentujuca ziadanu dobu prace s matlabom.
	 * Po uplinuti tohto casu sa instancia matlabu automaticky zavrie.<br>
	 * Ak hodnota nie je zadana ostane matlab spusteny, az pokial nebude vykonany prikaz: stopMatlab.
	 * @return
	 */
	@GET
	@Path("/startMatlab")
	public Response startMatlab(
			@QueryParam("connectionClass") @DefaultValue("sk.stuba.fei.jlab.matlabadapter.matlab.MatlabcontrolConnector") String connectionClass,
			@QueryParam("workTimeout") @DefaultValue("0") int workTimeout,
			@QueryParam("idleTimeout") @DefaultValue("0") int idleTimeout) throws IOException {

		System.out.println(servletCtx.getInitParameter("sk.stuba.fei.jlab.matlabadapter.matlabdir"));
		
		String baseDir = servletCtx.getInitParameter("sk.stuba.fei.jlab.matlabadapter.matlabdir");
		java.nio.file.Path baseDirPath = Paths.get(baseDir).toAbsolutePath();
		
		
		if(Files.notExists(baseDirPath) || !Files.isDirectory(baseDirPath)) {
			try {
				String templateBaseDir = servletCtx.getRealPath("")+File.separator+"matlab";
				java.nio.file.Path templateBaseDirPath = Paths.get(templateBaseDir);
				Files.createDirectories(baseDirPath);
			} catch (IOException e) {
				LOGGER.error("Error occured during creation and initialization of matlab base directory ("+baseDir+")", e);
				throw e;
			}			
		}

		
		return Response.status(Status.SERVICE_UNAVAILABLE).build();
		
//		String baseDir = servletCtx.getRealPath("")+File.separator+"matlab";
//		
//		try {
//			MatlabFacade.initInstance(connectionClass, baseDir);
//		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
//			LOGGER.error(ERROR_MESSAGE_INIT_REFLECTION+" "+connectionClass, e);
//			String errorMessage = buildErrorMessage(ERROR_CODE_INIT_REFLECTION, ERROR_MESSAGE_INIT_REFLECTION+" "+connectionClass, e);
//			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(errorMessage).build();
//		} catch (IOException e) {
//			LOGGER.error(ERROR_MESSAGE_MATLAB_COMM, e);
//			String errorMessage = buildErrorMessage(ERROR_CODE_MATLAB_COMM, ERROR_MESSAGE_MATLAB_COMM, e);
//			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(errorMessage).build();
//		} catch (IllegalStateException e) {
//			LOGGER.error(ERROR_MESSAGE_ALREADY_CONNECTED, e);
//			String errorMessage = buildErrorMessage(ERROR_CODE_ALREADY_CONNECTED, ERROR_MESSAGE_ALREADY_CONNECTED, e);
//			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(errorMessage).build();
//		}catch (Exception e) {
//			LOGGER.error(ERROR_MESSAGE_GENERAL, e);
//			String errorMessage = buildErrorMessage(ERROR_CODE_GENERAL, ERROR_MESSAGE_GENERAL, e);
//			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(errorMessage).build();
//		}
//		
//		return Response.ok().build();
	}
	
	/**
	 * @return
	 */
	@GET
	@Path("/stopMatlab")
	public Response stopMatlab() {
		try {
			MatlabFacade.destroyInstance();
		} catch (IOException e) {
			LOGGER.error(ERROR_MESSAGE_MATLAB_COMM, e);
			String errorMessage = buildErrorMessage(ERROR_CODE_MATLAB_COMM, ERROR_MESSAGE_MATLAB_COMM, e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(errorMessage).build();
		} catch (IllegalStateException e) {
			LOGGER.error(ERROR_MESSAGE_NOT_CONNECTED, e);
			String errorMessage = buildErrorMessage(ERROR_CODE_NOT_CONNECTED, ERROR_MESSAGE_NOT_CONNECTED, e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(errorMessage).build();
		} catch (Exception e) {
			LOGGER.error(ERROR_MESSAGE_GENERAL, e);
			String errorMessage = buildErrorMessage(ERROR_CODE_GENERAL, ERROR_MESSAGE_GENERAL, e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(errorMessage).build();
		}
		
		return Response.ok().build();
	}

	//	@GET
	//	@Path("/getModelInfo")
	//	@Produces({MediaType.APPLICATION_OCTET_STREAM, MediaType.TEXT_PLAIN})
	//	public Response getModelInfo(
	//			@QueryParam("modelName") String modelName) {
	//		Hashtable<String, List<ParamProperties>> modelInfoData = null;
	//		try {
	//			modelInfoData = MatlabFacade.getInstance().getModelInfo(modelName);
	//		} catch (IOException e) {
	//			LOGGER.error(ERROR_MESSAGE_MATLAB_COMM, e);
	//			String errorMessage = buildErrorMessage(ERROR_CODE_MATLAB_COMM, ERROR_MESSAGE_MATLAB_COMM, e);
	//			return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.TEXT_PLAIN_TYPE).entity(errorMessage).build();
	//		} catch (IllegalStateException e) {
	//			LOGGER.error(ERROR_MESSAGE_NOT_CONNECTED, e);
	//			String errorMessage = buildErrorMessage(ERROR_CODE_NOT_CONNECTED, ERROR_MESSAGE_NOT_CONNECTED, e);
	//			return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.TEXT_PLAIN_TYPE).entity(errorMessage).build();
	//		} catch (Exception e) {
	//			LOGGER.error(ERROR_MESSAGE_GENERAL, e);
	//			String errorMessage = buildErrorMessage(ERROR_CODE_GENERAL, ERROR_MESSAGE_GENERAL, e);
	//			return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.TEXT_PLAIN_TYPE).entity(errorMessage).build();
	//		}
	//		
	//		byte outputByteBuffer[] = null;
	//		try {
	//			ByteArrayOutputStream baos = new ByteArrayOutputStream();
	//			ObjectOutputStream oos = new ObjectOutputStream(baos);
	//			oos.writeObject(modelInfoData);
	//			oos.close();
	//			outputByteBuffer = baos.toByteArray();
	//		} catch (Exception e) {
	//			LOGGER.error(ERROR_MESSAGE_GENERAL, e);
	//			String errorMessage = buildErrorMessage(ERROR_CODE_GENERAL, ERROR_MESSAGE_GENERAL, e);
	//			return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.TEXT_PLAIN_TYPE).entity(errorMessage).build();
	//		}
	//			
	//		return Response.ok().type(MediaType.APPLICATION_OCTET_STREAM_TYPE).entity(outputByteBuffer).build();
	//	}
		
		/**
		 * @return
		 */
		@GET
		@Path("/isMatlabRunning")
		public Response isMatlabRunning() {
			return Response.ok().entity(Boolean.toString(MatlabFacade.isInitialized())).build();
		}

	/**
	 * @return
	 */
	@GET
	@Path("/reinitializeMatlabWorkspace")
	public Response reinitializeMatlabWorkspace() {
		
		try {
			MatlabFacade.getInstance().reinitializeMatlabWorkspace();
		} catch (IOException e) {
			LOGGER.error(ERROR_MESSAGE_MATLAB_COMM, e);
			String errorMessage = buildErrorMessage(ERROR_CODE_MATLAB_COMM, ERROR_MESSAGE_MATLAB_COMM, e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(errorMessage).build();
		} catch (IllegalStateException e) {
			LOGGER.error(ERROR_MESSAGE_NOT_CONNECTED, e);
			String errorMessage = buildErrorMessage(ERROR_CODE_NOT_CONNECTED, ERROR_MESSAGE_NOT_CONNECTED, e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(errorMessage).build();
		} catch (Exception e) {
			LOGGER.error(ERROR_MESSAGE_GENERAL, e);
			String errorMessage = buildErrorMessage(ERROR_CODE_GENERAL, ERROR_MESSAGE_GENERAL, e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(errorMessage).build();
		}
		
		return Response.ok().build();
	}

	/**
	 * @param modelName
	 * @param build
	 * @return
	 */
	@GET
	@Path("/simulink/openModel")
	public Response openModel(
			@QueryParam("modelName") String modelName,
			@QueryParam("build") @DefaultValue("false") boolean build) {
		try {
			MatlabFacade.getInstance().openModel(modelName, build);		
		} catch (IOException e) {
			LOGGER.error(ERROR_MESSAGE_MATLAB_COMM, e);
			String errorMessage = buildErrorMessage(ERROR_CODE_MATLAB_COMM, ERROR_MESSAGE_MATLAB_COMM, e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(errorMessage).build();
		} catch (IllegalStateException e) {
			LOGGER.error(ERROR_MESSAGE_NOT_CONNECTED, e);
			String errorMessage = buildErrorMessage(ERROR_CODE_NOT_CONNECTED, ERROR_MESSAGE_NOT_CONNECTED, e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(errorMessage).build();
		} catch (Exception e) {
			LOGGER.error(ERROR_MESSAGE_GENERAL, e);
			String errorMessage = buildErrorMessage(ERROR_CODE_GENERAL, ERROR_MESSAGE_GENERAL, e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(errorMessage).build();
		}
		
		return Response.ok().build();
	}
	
	/**
	 * @param modelName
	 * @return
	 */
	@GET
	@Path("/simulink/closeModel")
	public Response closeModel(
			@QueryParam("modelName") String modelName) {
		try {
			MatlabFacade.getInstance().closeModel(modelName);
		} catch (IOException e) {
			LOGGER.error(ERROR_MESSAGE_MATLAB_COMM, e);
			String errorMessage = buildErrorMessage(ERROR_CODE_MATLAB_COMM, ERROR_MESSAGE_MATLAB_COMM, e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(errorMessage).build();
		} catch (IllegalStateException e) {
			LOGGER.error(ERROR_MESSAGE_NOT_CONNECTED, e);
			String errorMessage = buildErrorMessage(ERROR_CODE_NOT_CONNECTED, ERROR_MESSAGE_NOT_CONNECTED, e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(errorMessage).build();
		} catch (Exception e) {
			LOGGER.error(ERROR_MESSAGE_GENERAL, e);
			String errorMessage = buildErrorMessage(ERROR_CODE_GENERAL, ERROR_MESSAGE_GENERAL, e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(errorMessage).build();
		}
		
		return Response.ok().build();
	}

	/**
	 * @param modelName
	 * @return
	 */
	@GET
	@Path("/simulink/isModelOpened")
	public Response isModelOpened(
			@QueryParam("modelName") String modelName) {
		boolean result;
		try {
			result = MatlabFacade.getInstance().isModelOpened(modelName);
		} catch (IOException e) {
			LOGGER.error(ERROR_MESSAGE_MATLAB_COMM, e);
			String errorMessage = buildErrorMessage(ERROR_CODE_MATLAB_COMM, ERROR_MESSAGE_MATLAB_COMM, e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.TEXT_PLAIN_TYPE).entity(errorMessage).build();
		} catch (IllegalStateException e) {
			LOGGER.error(ERROR_MESSAGE_NOT_CONNECTED, e);
			String errorMessage = buildErrorMessage(ERROR_CODE_NOT_CONNECTED, ERROR_MESSAGE_NOT_CONNECTED, e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.TEXT_PLAIN_TYPE).entity(errorMessage).build();
		} catch (Exception e) {
			LOGGER.error(ERROR_MESSAGE_GENERAL, e);
			String errorMessage = buildErrorMessage(ERROR_CODE_GENERAL, ERROR_MESSAGE_GENERAL, e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.TEXT_PLAIN_TYPE).entity(errorMessage).build();
		}
		
		return Response.ok().entity(Boolean.toString(result)).build();
	}

	/**
	 * @param modelName
	 * @param simulationTime
	 * @return
	 */
	@GET
	@Path("/simulink/startSimulation")
	public Response startSimulation(
			@QueryParam("modelName") String modelName,
			@QueryParam("simulationTime") @DefaultValue("0") long simulationTime) {
		try {
			MatlabFacade.getInstance().startBufferedSimulation(modelName);		
		} catch (IOException e) {
			LOGGER.error(ERROR_MESSAGE_MATLAB_COMM, e);
			String errorMessage = buildErrorMessage(ERROR_CODE_MATLAB_COMM, ERROR_MESSAGE_MATLAB_COMM, e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(errorMessage).build();
		} catch (IllegalStateException e) {
			LOGGER.error(ERROR_MESSAGE_NOT_CONNECTED, e);
			String errorMessage = buildErrorMessage(ERROR_CODE_NOT_CONNECTED, ERROR_MESSAGE_NOT_CONNECTED, e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(errorMessage).build();
		} catch (Exception e) {
			LOGGER.error(ERROR_MESSAGE_GENERAL, e);
			String errorMessage = buildErrorMessage(ERROR_CODE_GENERAL, ERROR_MESSAGE_GENERAL, e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(errorMessage).build();
		}
		
		return Response.ok().build();
	}
	
	
	/**
	 * @return
	 */
	@GET
	@Path("/simulink/stopSimulation")
	public Response stopSimulation() {
		try {
			MatlabFacade.getInstance().stopBufferedSimulation();
		} catch (IOException e) {
			LOGGER.error(ERROR_MESSAGE_MATLAB_COMM, e);
			String errorMessage = buildErrorMessage(ERROR_CODE_MATLAB_COMM, ERROR_MESSAGE_MATLAB_COMM, e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(errorMessage).build();
		} catch (IllegalStateException e) {
			LOGGER.error(ERROR_MESSAGE_NOT_CONNECTED, e);
			String errorMessage = buildErrorMessage(ERROR_CODE_NOT_CONNECTED, ERROR_MESSAGE_NOT_CONNECTED, e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(errorMessage).build();
		} catch (Exception e) {
			LOGGER.error(ERROR_MESSAGE_GENERAL, e);
			String errorMessage = buildErrorMessage(ERROR_CODE_GENERAL, ERROR_MESSAGE_GENERAL, e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(errorMessage).build();
		}
		
		return Response.ok().build();
	}

	/**
	 * @return
	 */
	@GET
	@Path("/togglePauseSimulation")
	public Response togglePauseSimulation() {
		try {
			MatlabFacade.getInstance().togglePauseBufferedSimulation();		
		} catch (IOException e) {
			LOGGER.error(ERROR_MESSAGE_MATLAB_COMM, e);
			String errorMessage = buildErrorMessage(ERROR_CODE_MATLAB_COMM, ERROR_MESSAGE_MATLAB_COMM, e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(errorMessage).build();
		} catch (IllegalStateException e) {
			LOGGER.error(ERROR_MESSAGE_NOT_CONNECTED, e);
			String errorMessage = buildErrorMessage(ERROR_CODE_NOT_CONNECTED, ERROR_MESSAGE_NOT_CONNECTED, e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(errorMessage).build();
		} catch (Exception e) {
			LOGGER.error(ERROR_MESSAGE_GENERAL, e);
			String errorMessage = buildErrorMessage(ERROR_CODE_GENERAL, ERROR_MESSAGE_GENERAL, e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(errorMessage).build();
		}
		
		return Response.ok().build();
	}
	
	/**
	 * @param modelName
	 * @param blockName
	 * @param paramName
	 * @param paramValue
	 * @return
	 */
	@GET
	@Path("/simulink/setParamValue")
	public Response setParamValue(
			@QueryParam("modelName") String modelName,
			@QueryParam("blockName") String blockName, 
			@QueryParam("paramName") String paramName, 
			@QueryParam("paramValue") String paramValue) {
		String newValue = "";
		try {
			newValue = MatlabFacade.getInstance().setParamValue(modelName, blockName, paramName, paramValue);
		} catch (IOException e) {
			LOGGER.error(ERROR_MESSAGE_MATLAB_COMM, e);
			String errorMessage = buildErrorMessage(ERROR_CODE_MATLAB_COMM, ERROR_MESSAGE_MATLAB_COMM, e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(errorMessage).build();
		} catch (IllegalStateException e) {
			LOGGER.error(ERROR_MESSAGE_NOT_CONNECTED, e);
			String errorMessage = buildErrorMessage(ERROR_CODE_NOT_CONNECTED, ERROR_MESSAGE_NOT_CONNECTED, e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(errorMessage).build();
		} catch (Exception e) {
			LOGGER.error(ERROR_MESSAGE_GENERAL, e);
			String errorMessage = buildErrorMessage(ERROR_CODE_GENERAL, ERROR_MESSAGE_GENERAL, e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(errorMessage).build();
		}
		
		return Response.ok().entity(newValue).build();
	}
	
	/**
	 * @param modelName
	 * @param blocksParamsValuesMap
	 * @return
	 */
	@POST
	@Path("/simulink/setParamsValues")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
	public Response setParamsValues(
			@QueryParam("modelName") String modelName, 
			Map<String, Map<String, String>> blocksParamsValuesMap) {
		Map<String, Map<String, String>> blocksParamsNewValuesMap;
		try {
			blocksParamsNewValuesMap = MatlabFacade.getInstance().setParamsValues(modelName, blocksParamsValuesMap);
		} catch (IOException e) {
			LOGGER.error(ERROR_MESSAGE_MATLAB_COMM, e);
			String errorMessage = buildErrorMessage(ERROR_CODE_MATLAB_COMM, ERROR_MESSAGE_MATLAB_COMM, e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.TEXT_PLAIN_TYPE).entity(errorMessage).build();
		} catch (IllegalStateException e) {
			LOGGER.error(ERROR_MESSAGE_NOT_CONNECTED, e);
			String errorMessage = buildErrorMessage(ERROR_CODE_NOT_CONNECTED, ERROR_MESSAGE_NOT_CONNECTED, e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.TEXT_PLAIN_TYPE).entity(errorMessage).build();
		} catch (Exception e) {
			LOGGER.error(ERROR_MESSAGE_GENERAL, e);
			String errorMessage = buildErrorMessage(ERROR_CODE_GENERAL, ERROR_MESSAGE_GENERAL, e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.TEXT_PLAIN_TYPE).entity(errorMessage).build();
		}
		
		return Response.ok().type(MediaType.APPLICATION_JSON_TYPE).entity(blocksParamsNewValuesMap).build();
	}
	
	/**
	 * @param modelName
	 * @param blockName
	 * @param paramName
	 * @return
	 */
	@GET
	@Path("/simulink/getParamValue")
	public Response getParamValue(
			@QueryParam("modelName") String modelName,
			@QueryParam("blockName") String blockName, 
			@QueryParam("paramName") String paramName) {
		String retVal = "";
		try {
			retVal = MatlabFacade.getInstance().getParamValue(modelName, blockName, paramName);
		} catch (IOException e) {
			LOGGER.error(ERROR_MESSAGE_MATLAB_COMM, e);
			String errorMessage = buildErrorMessage(ERROR_CODE_MATLAB_COMM, ERROR_MESSAGE_MATLAB_COMM, e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(errorMessage).build();
		} catch (IllegalStateException e) {
			LOGGER.error(ERROR_MESSAGE_NOT_CONNECTED, e);
			String errorMessage = buildErrorMessage(ERROR_CODE_NOT_CONNECTED, ERROR_MESSAGE_NOT_CONNECTED, e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(errorMessage).build();
		} catch (Exception e) {
			LOGGER.error(ERROR_MESSAGE_GENERAL, e);
			String errorMessage = buildErrorMessage(ERROR_CODE_GENERAL, ERROR_MESSAGE_GENERAL, e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(errorMessage).build();
		}
		
		return Response.ok().entity(retVal).build();
	}
	
	/**
	 * @param modelName
	 * @param blocksParamsMap
	 * @return
	 */
	@POST
	@Path("/simulink/getParamsValues")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
	public Response getParamsValues(
			@QueryParam("modelName") String modelName,
			Map<String, List<String>> blocksParamsMap) {
		Map<String, Map<String, String>> blocksParamsValuesMap;
		try {
			blocksParamsValuesMap = MatlabFacade.getInstance().getParamsValues(modelName, blocksParamsMap);
		} catch (IOException e) {
			LOGGER.error(ERROR_MESSAGE_MATLAB_COMM, e);
			String errorMessage = buildErrorMessage(ERROR_CODE_MATLAB_COMM, ERROR_MESSAGE_MATLAB_COMM, e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.TEXT_PLAIN_TYPE).entity(errorMessage).build();
		} catch (IllegalStateException e) {
			LOGGER.error(ERROR_MESSAGE_NOT_CONNECTED, e);
			String errorMessage = buildErrorMessage(ERROR_CODE_NOT_CONNECTED, ERROR_MESSAGE_NOT_CONNECTED, e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.TEXT_PLAIN_TYPE).entity(errorMessage).build();
		} catch (Exception e) {
			LOGGER.error(ERROR_MESSAGE_GENERAL, e);
			String errorMessage = buildErrorMessage(ERROR_CODE_GENERAL, ERROR_MESSAGE_GENERAL, e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.TEXT_PLAIN_TYPE).entity(errorMessage).build();
		}
		
		return Response.ok().type(MediaType.APPLICATION_JSON_TYPE).entity(blocksParamsValuesMap).build();
	}
	
	/**
	 * @return
	 */
	@GET
	@Path("/simulink/getScopeData")
	@Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
	public Response getGraphData() {
		Hashtable<String, Hashtable<Double, Object>> bufferData = null;
		try {
			bufferData = MatlabFacade.getInstance().getGraphData();
		} catch (IOException e) {
			LOGGER.error(ERROR_MESSAGE_MATLAB_COMM, e);
			String errorMessage = buildErrorMessage(ERROR_CODE_MATLAB_COMM, ERROR_MESSAGE_MATLAB_COMM, e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.TEXT_PLAIN_TYPE).entity(errorMessage).build();
		} catch (IllegalStateException e) {
			LOGGER.error(ERROR_MESSAGE_NOT_CONNECTED, e);
			String errorMessage = buildErrorMessage(ERROR_CODE_NOT_CONNECTED, ERROR_MESSAGE_NOT_CONNECTED, e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.TEXT_PLAIN_TYPE).entity(errorMessage).build();
		} catch (Exception e) {
			LOGGER.error(ERROR_MESSAGE_GENERAL, e);
			String errorMessage = buildErrorMessage(ERROR_CODE_GENERAL, ERROR_MESSAGE_GENERAL, e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.TEXT_PLAIN_TYPE).entity(errorMessage).build();
		}
		
		return Response.ok().type(MediaType.APPLICATION_JSON_TYPE).entity(bufferData).build();
	}
	
	/**
	 * @param command
	 * @return
	 */
	@GET
	@Path("/executeCommand")
	public Response executeCommand(
			@QueryParam("command") String command) {
		String output;
		try {
			output = MatlabFacade.getInstance().executeCommandWithOutput(command);
		} catch (IOException e) {
			LOGGER.error(ERROR_MESSAGE_MATLAB_COMM, e);
			String errorMessage = buildErrorMessage(ERROR_CODE_MATLAB_COMM, ERROR_MESSAGE_MATLAB_COMM, e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(errorMessage).build();
		} catch (IllegalStateException e) {
			LOGGER.error(ERROR_MESSAGE_NOT_CONNECTED, e);
			String errorMessage = buildErrorMessage(ERROR_CODE_NOT_CONNECTED, ERROR_MESSAGE_NOT_CONNECTED, e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(errorMessage).build();
		} catch (Exception e) {
			LOGGER.error(ERROR_MESSAGE_GENERAL, e);
			String errorMessage = buildErrorMessage(ERROR_CODE_GENERAL, ERROR_MESSAGE_GENERAL, e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(errorMessage).build();
		}
		
		if(output == null) {
			output = "";
		}

		return Response.status(Status.OK).entity(output).build();
	}
	
	
	
	
//	@POST
//	@Path("/uploadModel")
//	@Consumes(MediaType.MULTIPART_FORM_DATA)
//	public Response uploadModel(
//			@FormDataParam("file") InputStream uploadedInputStream,
//			@FormDataParam("file") FormDataContentDisposition fileInfo) {
//		try {
//			String uploadedFileLocalPath = 
//					MatlabFacade.getInstance().getSimulinkModelsDir()+File.separator+fileInfo.getFileName();
//			writeToFile(uploadedInputStream, uploadedFileLocalPath);
//		} catch (IOException e) {
//			LOGGER.error(ERROR_MESSAGE_MODEL_UPLOAD_FAILED, e);
//			String errorMessage = buildErrorMessage(ERROR_CODE_MODEL_UPLOAD_FAILED, ERROR_MESSAGE_MODEL_UPLOAD_FAILED, e);
//			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(errorMessage).build();
//		}catch (IllegalStateException e) {
//			LOGGER.error(ERROR_MESSAGE_NOT_CONNECTED, e);
//			String errorMessage = buildErrorMessage(ERROR_CODE_NOT_CONNECTED, ERROR_MESSAGE_NOT_CONNECTED, e);
//			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(errorMessage).build();
//		} catch (Exception e) {
//			LOGGER.error(ERROR_MESSAGE_GENERAL, e);
//			String errorMessage = buildErrorMessage(ERROR_CODE_GENERAL, ERROR_MESSAGE_GENERAL, e);
//			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(errorMessage).build();
//		} finally {
//			try {
//				uploadedInputStream.close();
//			} catch (IOException e) {
//				LOGGER.error(ERROR_MESSAGE_MODEL_UPLOAD_FAILED, e);
//			}
//		}
//		
//		return Response.ok().build();
//	}
	
	
	/**
	 * Moznost nahrat lubovolny subor do pracovneho priecinka MatlabAdaptera. Najcastejsie bude sluzit na upload simulink modelu realnej sustavy.
	 * Pripadne je moznost nahrat matlab skripty so specialnym nazvom: <br> 
	 * <modelName>_prerun.m - tento skript zbehne automaticky pred otvorenim samotneho modelu <br>
	 * <modelName>_postrun.m - tento skript zbehne automaticky po zatvoreni modelu <br>
	 * Tieto skripty sluzia na na inicializaciu a clean up pre dany model.
	 * 
	 * @param fileInputStream
	 * @param contentDisposition
	 * @return
	 */
	@POST
	@Path("/uploadFile")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadFile(
			@FormDataParam("file") InputStream fileInputStream,
			@FormDataParam("file") FormDataContentDisposition contentDisposition,
			@FormDataParam("location") @DefaultValue("") String location) {
		
		try {
//			String uploadedFileLocalPath = MatlabFacade.getInstance().getSimulinkModelsDir()
//					+ File.separator + MatlabFacade.extractModelName(modelName) + ".mdl";
			
			String uploadedFileLocalPath;
			if(location.equals("")) {
				uploadedFileLocalPath = MatlabFacade.getInstance().getSimulinkModelsDir()
					+File.separator+contentDisposition.getFileName();
			} else {
				uploadedFileLocalPath = MatlabFacade.getInstance().getSimulinkModelsDir()
						+File.separator+location+File.separator+contentDisposition.getFileName();
			}
			writeToFile(fileInputStream, uploadedFileLocalPath);
		} catch (IOException e) {
			LOGGER.error(ERROR_MESSAGE_MODEL_UPLOAD_FAILED, e);
			String errorMessage = buildErrorMessage(
					ERROR_CODE_MODEL_UPLOAD_FAILED,
					ERROR_MESSAGE_MODEL_UPLOAD_FAILED, e);
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(errorMessage).build();
		} catch (IllegalStateException e) {
			LOGGER.error(ERROR_MESSAGE_NOT_CONNECTED, e);
			String errorMessage = buildErrorMessage(ERROR_CODE_NOT_CONNECTED,
					ERROR_MESSAGE_NOT_CONNECTED, e);
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(errorMessage).build();
		} catch (Exception e) {
			LOGGER.error(ERROR_MESSAGE_GENERAL, e);
			String errorMessage = buildErrorMessage(ERROR_CODE_GENERAL,
					ERROR_MESSAGE_GENERAL, e);
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(errorMessage).build();
		} finally {
			try {
				if(fileInputStream != null) {
					fileInputStream.close();
				}
			} catch (IOException e) {
				LOGGER.error(ERROR_MESSAGE_MODEL_UPLOAD_FAILED, e);
			}
		}

		return Response.ok().build();
	}
	
	
	@GET
	@Path("/downloadFile")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response downloadFile(@QueryParam("filePath") String filePath) {
		java.nio.file.Path p = Paths.get(MatlabFacade.getInstance().getSimulinkModelsDir(), filePath);
		if(!Files.exists(p)) {
			String errorMessage = buildErrorMessage(ERROR_CODE_FILE_NOT_FOUND, ERROR_MESSAGE_FILE_NOT_FOUND, null);
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(errorMessage).build();
		} else {
			File file = p.toAbsolutePath().toFile();
			return Response.ok(file).header("Content-Disposition", "attachment; filename=\""+file.getName()+"\"").build();
		}
	}
	
	
//	@POST
//	@Path("/uploadModel")
//	@Consumes(MediaType.APPLICATION_OCTET_STREAM)
//	public Response uploadModel(
//			@QueryParam("modelName") String modelName,
//			InputStream uploadedInputStream) {
//		try {
//			String uploadedFileLocalPath = 
//					MatlabFacade.getInstance().getSimulinkModelsDir()+File.separator+MatlabFacade.extractModelName(modelName)+".mdl";
//			writeToFile(uploadedInputStream, uploadedFileLocalPath);
//		} catch (IOException e) {
//			LOGGER.error(ERROR_MESSAGE_MODEL_UPLOAD_FAILED, e);
//			String errorMessage = buildErrorMessage(ERROR_CODE_MODEL_UPLOAD_FAILED, ERROR_MESSAGE_MODEL_UPLOAD_FAILED, e);
//			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(errorMessage).build();
//		}catch (IllegalStateException e) {
//			LOGGER.error(ERROR_MESSAGE_NOT_CONNECTED, e);
//			String errorMessage = buildErrorMessage(ERROR_CODE_NOT_CONNECTED, ERROR_MESSAGE_NOT_CONNECTED, e);
//			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(errorMessage).build();
//		} catch (Exception e) {
//			LOGGER.error(ERROR_MESSAGE_GENERAL, e);
//			String errorMessage = buildErrorMessage(ERROR_CODE_GENERAL, ERROR_MESSAGE_GENERAL, e);
//			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(errorMessage).build();
//		} finally {
//			try {
//				uploadedInputStream.close();
//			} catch (IOException e) {
//				LOGGER.error(ERROR_MESSAGE_MODEL_UPLOAD_FAILED, e);
//			}
//		}
//		
//		return Response.ok().build();
//	}
//	
//	@POST
//	@Path("/uploadInitScript")
//	@Consumes(MediaType.APPLICATION_OCTET_STREAM)
//	public Response uploadInitScript(
//			@QueryParam("forModel") String forModel,
//			InputStream uploadedInputStream) {
//		try {
//			String uploadedFileLocalPath = 
//					MatlabFacade.getInstance().getSimulinkModelsDir()+File.separator+MatlabFacade.extractModelName(forModel)+"_init.m";
//			writeToFile(uploadedInputStream, uploadedFileLocalPath);
//		} catch (IOException e) {
//			LOGGER.error(ERROR_MESSAGE_MODEL_UPLOAD_FAILED, e);
//			String errorMessage = buildErrorMessage(ERROR_CODE_MODEL_UPLOAD_FAILED, ERROR_MESSAGE_MODEL_UPLOAD_FAILED, e);
//			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(errorMessage).build();
//		}catch (IllegalStateException e) {
//			LOGGER.error(ERROR_MESSAGE_NOT_CONNECTED, e);
//			String errorMessage = buildErrorMessage(ERROR_CODE_NOT_CONNECTED, ERROR_MESSAGE_NOT_CONNECTED, e);
//			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(errorMessage).build();
//		} catch (Exception e) {
//			LOGGER.error(ERROR_MESSAGE_GENERAL, e);
//			String errorMessage = buildErrorMessage(ERROR_CODE_GENERAL, ERROR_MESSAGE_GENERAL, e);
//			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(errorMessage).build();
//		} finally {
//			try {
//				uploadedInputStream.close();
//			} catch (IOException e) {
//				LOGGER.error(ERROR_MESSAGE_MODEL_UPLOAD_FAILED, e);
//			}
//		}
//		
//		return Response.ok().build();
//	}
//	
//	@POST
//	@Path("/uploadCleanupScript")
//	@Consumes(MediaType.APPLICATION_OCTET_STREAM)
//	public Response uploadCleanupScript(
//			@QueryParam("forModel") String forModel,
//			InputStream uploadedInputStream) {
//		try {
//			String uploadedFileLocalPath = 
//					MatlabFacade.getInstance().getSimulinkModelsDir()+File.separator+MatlabFacade.extractModelName(forModel)+"_cleanup.m";
//			writeToFile(uploadedInputStream, uploadedFileLocalPath);
//		} catch (IOException e) {
//			LOGGER.error(ERROR_MESSAGE_MODEL_UPLOAD_FAILED, e);
//			String errorMessage = buildErrorMessage(ERROR_CODE_MODEL_UPLOAD_FAILED, ERROR_MESSAGE_MODEL_UPLOAD_FAILED, e);
//			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(errorMessage).build();
//		}catch (IllegalStateException e) {
//			LOGGER.error(ERROR_MESSAGE_NOT_CONNECTED, e);
//			String errorMessage = buildErrorMessage(ERROR_CODE_NOT_CONNECTED, ERROR_MESSAGE_NOT_CONNECTED, e);
//			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(errorMessage).build();
//		} catch (Exception e) {
//			LOGGER.error(ERROR_MESSAGE_GENERAL, e);
//			String errorMessage = buildErrorMessage(ERROR_CODE_GENERAL, ERROR_MESSAGE_GENERAL, e);
//			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(errorMessage).build();
//		} finally {
//			try {
//				uploadedInputStream.close();
//			} catch (IOException e) {
//				LOGGER.error(ERROR_MESSAGE_MODEL_UPLOAD_FAILED, e);
//			}
//		}
//		
//		return Response.ok().build();
//	}
	
	@GET
	@Path("/deleteFile")
	public Response deleteFile(
			@QueryParam("file")String filePath) {
		try {
//			String abstractFilePath = MatlabFacade.getInstance().getSimulinkModelsDir()
//					+File.separator+MatlabFacade.extractModelName(fileName); 
//			String filePath = abstractFilePath+".mdl";
			
			java.nio.file.Path path = Paths.get(MatlabFacade.getInstance().getSimulinkModelsDir(), filePath);

			if(Files.exists(path)) {
				Files.delete(path);
			}
//			String initScriptFilePath = abstractModelPath+"_init.m";
//			String cleanupScriptFilePath = abstractModelPath+"_cleanup.m";
//			File initScriptFile = new File(initScriptFilePath);
//			File cleanupScriptFile = new File(cleanupScriptFilePath);
//			if(initScriptFile.exists()) {
//				Files.delete(initScriptFile.toPath());
//			}
//			if(cleanupScriptFile.exists()) {
//				Files.delete(cleanupScriptFile.toPath());
//			}
		} catch (IOException e) {
			LOGGER.error(ERROR_MESSAGE_CANNOT_DELETE_CONTENT, e);
			String errorMessage = buildErrorMessage(ERROR_CODE_CANNOT_DELETE_CONTENT, ERROR_MESSAGE_CANNOT_DELETE_CONTENT, e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(errorMessage).build();
		}catch (IllegalStateException e) {
			LOGGER.error(ERROR_MESSAGE_NOT_CONNECTED, e);
			String errorMessage = buildErrorMessage(ERROR_CODE_NOT_CONNECTED, ERROR_MESSAGE_NOT_CONNECTED, e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(errorMessage).build();
		} catch (Exception e) {
			LOGGER.error(ERROR_MESSAGE_GENERAL, e);
			String errorMessage = buildErrorMessage(ERROR_CODE_GENERAL, ERROR_MESSAGE_GENERAL, e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(errorMessage).build();
		}
		
		return Response.ok().build();
	}

//	@GET
//	@Path("/simulink/deleteModel")
//	public Response deleteFile(
//			@QueryParam("file")String modelName) {
//		try {
//			String abstractModelPath = MatlabFacade.getInstance().getSimulinkModelsDir()
//					+File.separator+MatlabFacade.extractModelName(modelName); 
//			String modelFilePath = abstractModelPath+".mdl";
//			File modelFile = new File(modelFilePath);
//			if(modelFile.exists()) {
//				Files.delete(modelFile.toPath());
//			}
//			String initScriptFilePath = abstractModelPath+"_init.m";
//			String cleanupScriptFilePath = abstractModelPath+"_cleanup.m";
//			File initScriptFile = new File(initScriptFilePath);
//			File cleanupScriptFile = new File(cleanupScriptFilePath);
//			if(initScriptFile.exists()) {
//				Files.delete(initScriptFile.toPath());
//			}
//			if(cleanupScriptFile.exists()) {
//				Files.delete(cleanupScriptFile.toPath());
//			}
//		} catch (IOException e) {
//			LOGGER.error(ERROR_MESSAGE_CANNOT_DELETE_CONTENT, e);
//			String errorMessage = buildErrorMessage(ERROR_CODE_CANNOT_DELETE_CONTENT, ERROR_MESSAGE_CANNOT_DELETE_CONTENT, e);
//			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(errorMessage).build();
//		}catch (IllegalStateException e) {
//			LOGGER.error(ERROR_MESSAGE_NOT_CONNECTED, e);
//			String errorMessage = buildErrorMessage(ERROR_CODE_NOT_CONNECTED, ERROR_MESSAGE_NOT_CONNECTED, e);
//			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(errorMessage).build();
//		} catch (Exception e) {
//			LOGGER.error(ERROR_MESSAGE_GENERAL, e);
//			String errorMessage = buildErrorMessage(ERROR_CODE_GENERAL, ERROR_MESSAGE_GENERAL, e);
//			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(errorMessage).build();
//		}
//		
//		return Response.ok().build();
//	}
	
	/**
	 * 
	 * 
	 * @param modelName
	 * @return Zazipovany subor obsahujuci webview pre dany model.
	 */
	@GET
	@Path("/simulink/generateWebView")
	@Produces({MediaType.APPLICATION_OCTET_STREAM, MediaType.TEXT_PLAIN})
	public Response generateWebView(
			@QueryParam("modelName") String modelName) {
		String webViewTmpDir = "";
		List<String> filesToCompress = new ArrayList<>();
		try {		
			webViewTmpDir = MatlabFacade.getInstance().getWebViewTmpDir();
			String webViewHtmlFilePath = MatlabFacade.getInstance().generateWebView(modelName);
			String webViewFilesDirPath = webViewHtmlFilePath.substring(0, webViewHtmlFilePath.length()-5)+"_files";
			filesToCompress.add(webViewHtmlFilePath);
			filesToCompress.add(webViewFilesDirPath);
		} catch (IOException e) {
			LOGGER.error(ERROR_MESSAGE_MATLAB_COMM, e);
			String errorMessage = buildErrorMessage(ERROR_CODE_MATLAB_COMM, ERROR_MESSAGE_MATLAB_COMM, e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.TEXT_PLAIN_TYPE).entity(errorMessage).build();
		} catch (IllegalStateException e) {
			LOGGER.error(ERROR_MESSAGE_NOT_CONNECTED, e);
			String errorMessage = buildErrorMessage(ERROR_CODE_NOT_CONNECTED, ERROR_MESSAGE_NOT_CONNECTED, e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.TEXT_PLAIN_TYPE).entity(errorMessage).build();
		} catch (Exception e) {
			LOGGER.error(ERROR_MESSAGE_GENERAL, e);
			String errorMessage = buildErrorMessage(ERROR_CODE_GENERAL, ERROR_MESSAGE_GENERAL, e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.TEXT_PLAIN_TYPE).entity(errorMessage).build();
		}
		
		String zipFilePath = webViewTmpDir+File.separator+modelName+".zip";
		File zipedFile = new File(zipFilePath);
		try {
			ZipCompressor zc = new ZipCompressor(webViewTmpDir, filesToCompress, zipFilePath);
			zc.compress();
		} catch (IOException e) {
			LOGGER.error(ERROR_MESSAGE_COMPRESSION_FAILED, e);
			String errorMessage = buildErrorMessage(ERROR_CODE_COMPRESSION_FAILED, ERROR_MESSAGE_COMPRESSION_FAILED, e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.TEXT_PLAIN_TYPE).entity(errorMessage).build();
		} catch (Exception e) {
			LOGGER.error(ERROR_MESSAGE_GENERAL, e);
			String errorMessage = buildErrorMessage(ERROR_CODE_GENERAL, ERROR_MESSAGE_GENERAL, e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.TEXT_PLAIN_TYPE).entity(errorMessage).build();
		}
		
		return Response.ok().type(MediaType.APPLICATION_OCTET_STREAM_TYPE)
				.header("Content-Disposition", "attachment; filename="+zipedFile.getName()).entity(zipedFile).build();
	}
	
	
//	/**
//	 * @return
//	 */
//	@GET
//	@Path("/cleanTmpDir")
//	public Response cleanWebViewTmpDir() {
//		String webViewTmpDir = "";
//		try {
//			webViewTmpDir = MatlabFacade.getInstance().getWebViewTmpDir();
//			FileUtils.cleanDirectory(new File(webViewTmpDir));
//		} catch (IOException e) {
//			LOGGER.error(ERROR_MESSAGE_CANNOT_DELETE_CONTENT, e);
//			String errorMessage = buildErrorMessage(ERROR_CODE_CANNOT_DELETE_CONTENT, ERROR_MESSAGE_CANNOT_DELETE_CONTENT, e);
//			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(errorMessage).build();
//		} catch (IllegalStateException e) {
//			LOGGER.error(ERROR_MESSAGE_NOT_CONNECTED, e);
//			String errorMessage = buildErrorMessage(ERROR_CODE_NOT_CONNECTED, ERROR_MESSAGE_NOT_CONNECTED, e);
//			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(errorMessage).build();
//		} catch (Exception e) {
//			LOGGER.error(ERROR_MESSAGE_GENERAL, e);
//			String errorMessage = buildErrorMessage(ERROR_CODE_GENERAL, ERROR_MESSAGE_GENERAL, e);
//			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(errorMessage).build();
//		}
//		
//		return Response.ok().build();
//	}
	
	/**
	 * @param modelName
	 * @return
	 */
	@GET
	@Path("/simulink/getModelInfo")
	@Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
	public Response getModelInfo(
			@QueryParam("modelName") String modelName) {
		Hashtable<String, List<ParamProperties>> modelInfoData = null;
		try {
			modelInfoData = MatlabFacade.getInstance().getModelInfo(modelName);
		} catch (IOException e) {
			LOGGER.error(ERROR_MESSAGE_MATLAB_COMM, e);
			String errorMessage = buildErrorMessage(ERROR_CODE_MATLAB_COMM, ERROR_MESSAGE_MATLAB_COMM, e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.TEXT_PLAIN_TYPE).entity(errorMessage).build();
		} catch (IllegalStateException e) {
			LOGGER.error(ERROR_MESSAGE_NOT_CONNECTED, e);
			String errorMessage = buildErrorMessage(ERROR_CODE_NOT_CONNECTED, ERROR_MESSAGE_NOT_CONNECTED, e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.TEXT_PLAIN_TYPE).entity(errorMessage).build();
		} catch (Exception e) {
			LOGGER.error(ERROR_MESSAGE_GENERAL, e);
			String errorMessage = buildErrorMessage(ERROR_CODE_GENERAL, ERROR_MESSAGE_GENERAL, e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.TEXT_PLAIN_TYPE).entity(errorMessage).build();
		}
		
		return Response.ok().type(MediaType.APPLICATION_JSON_TYPE).entity(modelInfoData).build();
	}
	
	
	@GET
	@Path("/simulink/getListOfScopes")
	@Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
	public Response getListOfScopes(
			@QueryParam("modelName") String modelName) {
		List<String> listOfScopes = null;
		try {
			listOfScopes = MatlabFacade.getInstance().getListOfScopes(modelName);
		} catch (IOException e) {
			LOGGER.error(ERROR_MESSAGE_MATLAB_COMM, e);
			String errorMessage = buildErrorMessage(ERROR_CODE_MATLAB_COMM, ERROR_MESSAGE_MATLAB_COMM, e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.TEXT_PLAIN_TYPE).entity(errorMessage).build();
		} catch (IllegalStateException e) {
			LOGGER.error(ERROR_MESSAGE_NOT_CONNECTED, e);
			String errorMessage = buildErrorMessage(ERROR_CODE_NOT_CONNECTED, ERROR_MESSAGE_NOT_CONNECTED, e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.TEXT_PLAIN_TYPE).entity(errorMessage).build();
		} catch (Exception e) {
			LOGGER.error(ERROR_MESSAGE_GENERAL, e);
			String errorMessage = buildErrorMessage(ERROR_CODE_GENERAL, ERROR_MESSAGE_GENERAL, e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.TEXT_PLAIN_TYPE).entity(errorMessage).build();
		}
		
		return Response.ok().type(MediaType.APPLICATION_JSON_TYPE).entity(listOfScopes).build();
	}
	
	
	/**
	 * @param modelName
	 * @return
	 */
	@GET
	@Path("/simulink/getScopeInfo")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getScopeInfo(
			@QueryParam("modelName") String modelName) {
		
		// TODO implement this method
		
		return Response.ok().build();
	}
	
	
private void writeToFile(InputStream uploadedInputStream, 
			String uploadedFileLocalPath) throws IOException {
		OutputStream out = null;;
		try {
			out = new FileOutputStream(new File(uploadedFileLocalPath));
			int read = 0;
			byte[] bytes = new byte[1024];
			while ((read = uploadedInputStream.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
		} finally {
			if(out != null) {
				out.flush();
				out.close();
			}
		}
	}
	
	private String buildErrorMessage(int maErrorCode, String maErrorMessage, Throwable e) {
		StringBuilder errorMessageBuilder = new StringBuilder();
		errorMessageBuilder.append(MATLAB_ADAPTER_ERROR_PREFIX).append(ERROR_FIELD_SEPARATOR);
		errorMessageBuilder.append(maErrorCode).append(ERROR_FIELD_SEPARATOR);
		errorMessageBuilder.append(maErrorMessage).append(ERROR_FIELD_SEPARATOR);
		if(e == null) {
			errorMessageBuilder.append(" ").append(ERROR_FIELD_SEPARATOR).append(" ");
		} else {
			errorMessageBuilder.append(e.getMessage()).append(ERROR_FIELD_SEPARATOR).append(MatlabAdapterUtils.getStackTrace(e));
		}
		
		return errorMessageBuilder.toString();
	}
}
