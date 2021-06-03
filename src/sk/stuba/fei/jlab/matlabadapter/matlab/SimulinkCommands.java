package sk.stuba.fei.jlab.matlabadapter.matlab;


public class SimulinkCommands {
	
	// model name of schema for real equipment
//	private static final String MODEL_NAME = "schema_simple";	

	// model name for virtual schema
	@Deprecated
	private static final String MODEL_NAME = "vrmaglev";

	@Deprecated
	public static final String OPEN_MODEL = "open_system('"+MODEL_NAME+"');";
	 
	@Deprecated
	public static final String BUILD = "rtwbuild('"+MODEL_NAME+"');";
//	public static final String BUILD = "";
	
	@Deprecated
	public static final String CONNECT = "set_param('"+MODEL_NAME+"', 'simulationcommand', 'connect');";
	
	@Deprecated
	public static final String START = "set_param('"+MODEL_NAME+"', 'simulationcommand', 'start');";
	
	@Deprecated
	public static final String START_WITH_BUFFERING = "startBufferedSimulation('"+MODEL_NAME+"');";
	
	@Deprecated
	public static final String PAUSE = "set_param('"+MODEL_NAME+"', 'simulationcommand', 'pause');";
	
	@Deprecated
	public static final String STOP = "set_param('"+MODEL_NAME+"', 'simulationcommand', 'stop');";
	
	@Deprecated
	public static final String STOP_WITH_BUFFERING = "stopBufferedSimulation;";
	
	@Deprecated
	public static final String READ_BUFFER = "readBuffer";
	
	@Deprecated
	public static final String CLOSE_MODEL = "close_system('"+MODEL_NAME+"', 0);";
	
	
	
	public static String openModel(String modelName) {
		return "open_system('"+modelName+"');";
	}
	
	public static String loadModel(String modelName) {
		return "load_system('"+modelName+"')";
	}
	
	public static String buildModel(String modelName) {
		return "rtwbuild('"+modelName+"');";
	}
	
	public static String connect(String modelName) {
		return "set_param('"+modelName+"', 'simulationcommand', 'connect');";
	}
	
	public static String start(String modelName) {
		return "set_param('"+modelName+"', 'simulationcommand', 'start');";
	}
	
	public static String startBufferedSimulation(String modelName) {
		return "startBufferedSimulation('"+modelName+"');";
	}
	
	public static String pauseSim(String modelName) {
		return "set_param('"+modelName+"', 'simulationcommand', 'pause');";
	}
	
	public static String continueSim(String modelName) {
		return "set_param('"+modelName+"', 'simulationcommand', 'continue');";
	}	
	
	public static String togglePauseBufferedSimulation() {
		return "togglePauseBufferedSimulation;";
	}

	public static String stop(String modelName) {
		return "set_param('"+modelName+"', 'simulationcommand', 'stop');";
	}
	
	public static String stopBufferedSimulation() {
		return "stopBufferedSimulation;";
	}
	
	public static String readBuffer() {
		return "readBuffer";
	}
	
	public static String closeModel(String modelName) {
		return "close_system('"+modelName+"', 0);";
	}
	
	public static String closeAllModels() {
		return "bdclose('all')";
	}
	
		
	
	@Deprecated
	public static String buildSetParamCommandString(String object, String param, String value) {
		if(object == null) {
			object = "";
		}
		return "set_param('"+MODEL_NAME+"/"+object+"', '"+param+"', '"+value+"');";
	}
	
	@Deprecated
	public static String buildGetParamCommandString(String object, String param, String returnVarName) {
		if(object == null) {
			object = "";
		}
		return returnVarName+" = get_param('"+MODEL_NAME+"/"+object+"', '"+param+"');";
	}
	
	@Deprecated
	public static String buildSetStopTimeCommandString(String stopTimeStr) {
		try {
			if(Double.parseDouble(stopTimeStr) < 0) {
				stopTimeStr = "0";
			}
		} catch(NumberFormatException e) {
			stopTimeStr = "Inf";
		}
		return "set_param('"+MODEL_NAME+"', 'stopTime', '"+stopTimeStr+"');";
	}

	
	
	public static String setParamValue(String modelName, String blockName, String paramName, String paramValue) {
		if(blockName == null) {
			blockName = "";
		}
		return "set_param('"+modelName+"/"+blockName+"', '"+paramName+"', '"+paramValue+"');";
	}
	
	public static String getParamValue(String modelName, String blockName, String paramName, String returnVarName) {
		if(blockName == null) {
			blockName = "";
		}
		return returnVarName+" = get_param('"+modelName+"/"+blockName+"', '"+paramName+"');";
	}
	
	public static String setStopTime(String modelName, String stopTimeStr) {
		try {
			if(Double.parseDouble(stopTimeStr) < 0) {
				stopTimeStr = "0";
			}
		} catch(NumberFormatException e) {
			stopTimeStr = "Inf";
		}
		return "set_param('"+modelName+"', 'stopTime', '"+stopTimeStr+"');";
	}
	
	public static String generateWebView(String modelName, String returnVarName) {
		return returnVarName+" = slwebview('"+modelName+"', 'LookUnderMasks', 'all', 'FollowLinks', 'on', " +
				"'SearchScope', 'all', 'FollowModelReference', 'on', 'ShowProgressBar', false, 'ViewFile', false)";
	}
	
	public static String generateModelInfo(String modelName) {
		return "generateModelInfo('"+modelName+"')";
	}
	
	public static String isModelOpened(String modelName, String returnVarName) {
		return returnVarName+" = +bdIsLoaded('"+modelName+"')";
	}
	
}
