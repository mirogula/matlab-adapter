package sk.stuba.fei.jlab.matlabadapter.rest;


import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sk.stuba.fei.jlab.matlabadapter.matlab.MatlabConnector;
import sk.stuba.fei.jlab.matlabadapter.matlab.MatlabcontrolConnector;
import sk.stuba.fei.jlab.matlabadapter.matlab.SimulinkCommands;
import sk.stuba.fei.weblab.paramproperties.ParamProperties;

import com.higherfrequencytrading.chronicle.Excerpt;
import com.higherfrequencytrading.chronicle.impl.IndexedChronicle;


public final class MatlabFacade {
	
	private static final String BUFFER_FCN_DIR_SUFFIX = "buffer";
	private static final String SIMULINK_MODELS_DIR_SUFFIX = "models";
	private static final String BUFFER_DATA_FILE = BUFFER_FCN_DIR_SUFFIX+File.separator+"bufferData";
	private static final String MODEL_INFO_DIR = "modelInfo";
	private static final String MODEL_INFO_DATA_FILE = MODEL_INFO_DIR+File.separator+"modelInfoData";
	private static final String SCOPE_INFO_DIR = "scopeInfo";
	private static final String SCOPE_INFO_DATA_FILE = SCOPE_INFO_DIR+File.separator+"scopeInfoData";
	private static final String MATLAB_JAVA_LIBS_DIR_SUFFIX = "javaLibs";
	private static final String MATLAB_DIARY_FILE_NAME = "diary.txt";
	private static final String WEB_VIEW_TMP_DIR = "webViewTmp";
	
	private static MatlabFacade instance;
	
	private MatlabConnector<?> connection;
	private IndexedChronicle bufferChronicle;
	private IndexedChronicle modelInfoChronicle;
	private String baseDir;
	
	
	private MatlabFacade() {
	}
	
	private MatlabFacade(String connectionType, String baseDir) throws 
			InstantiationException,	IllegalAccessException,	ClassNotFoundException, IOException {
		this.baseDir = Paths.get(baseDir).toAbsolutePath().toString();
		connection = (MatlabConnector<?>)Class.forName(connectionType).newInstance();
		if(MatlabcontrolConnector.class.isInstance(connection)) {
			((MatlabcontrolConnector)connection).setMatlabOutputDiaryFile(new File(baseDir+File.separator+MATLAB_DIARY_FILE_NAME));
		}
		try {
			connection.connect();
			String bufferFcnDirectory = baseDir+File.separator+BUFFER_FCN_DIR_SUFFIX;
			String simulinkModelsDirectory = baseDir+File.separator+SIMULINK_MODELS_DIR_SUFFIX;
			String modelInfoDirectory = baseDir+File.separator+MODEL_INFO_DIR;
			String scopeInfoDIrectory = baseDir+File.separator+SCOPE_INFO_DIR;
			String matlabJavaDir = baseDir+File.separator+MATLAB_JAVA_LIBS_DIR_SUFFIX;
			connection.executeCommand("addpath('"+bufferFcnDirectory+"')");
			connection.executeCommand("addpath('"+simulinkModelsDirectory+"')");
			connection.executeCommand("addpath('"+modelInfoDirectory+"')");
			connection.executeCommand("addpath('"+scopeInfoDIrectory+"')");
			connection.executeCommand("addpath('"+matlabJavaDir+"')");
			connection.executeCommand("cd '"+baseDir+"'");
			connection.executeCommand("addToClasspath");
			String bufferDataFile = baseDir+File.separator+BUFFER_DATA_FILE;
			connection.executeCommand("initBufferIPC('"+bufferDataFile+"')");
			bufferChronicle = new IndexedChronicle(bufferDataFile);
			bufferChronicle.useUnsafe(false);
			
			String modelInfoDataFile = baseDir+File.separator+MODEL_INFO_DATA_FILE;
			connection.executeCommand("initModelInfoIPC('"+modelInfoDataFile+"')");
			modelInfoChronicle = new IndexedChronicle(modelInfoDataFile);
			modelInfoChronicle.useUnsafe(false);
			
			String scopeInfoDataFile = baseDir+File.separator+SCOPE_INFO_DATA_FILE;
			connection.executeCommand("initScopeInfoIPC('"+scopeInfoDataFile+"')");
			modelInfoChronicle = new IndexedChronicle(modelInfoDataFile);
			modelInfoChronicle.useUnsafe(false);
			
		} catch (IOException e) { // if we can't connect to matlab or properly initialized session, we clear all resources and rethrow IOException
			try {
				connection.disconnect();
			} catch (IOException e1) {	// if we can't disconnect by normal way, we kill whole matlab process
				try {
					Process p = Runtime.getRuntime().exec("taskkill /F /IM matlab.exe /T");
					p.waitFor();
				} catch (InterruptedException e2) {
					// Restore the interrupted status
					Thread.currentThread().interrupt();
				}
			}
			throw e;
		}
		
	}
	
	public MatlabConnector<?> getConnection() {
		return connection;
	}

	public String getBaseDir() {
		return baseDir;
	}
	
	public String getSimulinkModelsDir() {
		return baseDir+File.separator+SIMULINK_MODELS_DIR_SUFFIX;
	}
	
	public String getWebViewTmpDir() {
		return baseDir+File.separator+WEB_VIEW_TMP_DIR;
	}
	
	public String getBufferDir() {
		return baseDir+File.separator+BUFFER_FCN_DIR_SUFFIX;
	}
	
	public static boolean isInitialized() {
		if(instance != null) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @param connectionType - fully qualified name of java class representing connection to matlab, 
	 * 		it must implement <i>sk.stuba.fei.jlab.matlabadapter.matlab.MatlabConnection</i> interface, currently there are 2 connection types:
	 * 		<i>sk.stuba.fei.jlab.matlabadapter.matlab.MatlabcontrolConnection</i> and <i>sk.stuba.fei.jlab.matlabadapter.matlab.JMatlinkConnection</i>
	 * @param baseDir - base directory for matlab workspace, in this directory there must be present buffer directory which provides buffer 
	 * 		functionality for graph data, and also in this directory there are simulink models
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws IOException 
	 * @throws IllegalStateException - if singleton instance alerady exists, this exception is thrown when application call init method twice, 
	 * 		without destroying instance before second call
	 */
	public static MatlabFacade initInstance(String connectionType, String baseDir) throws 
			InstantiationException, IllegalAccessException, ClassNotFoundException, IOException, IllegalStateException {
		if(instance != null) {
			throw new IllegalStateException("singleton instance already exists");
		}
		instance = new MatlabFacade(connectionType, baseDir);
		return instance;
	}
	
	public static MatlabFacade getInstance() throws IllegalStateException {
		if(instance == null) {
			throw new IllegalStateException("instance is not initialized");
		}
		return instance;
	}
	
	/**
	 * Destroys current singleton instance. 
	 * 
	 * @throws IOException
	 */
	public static void destroyInstance() throws IOException, IllegalStateException {
		if(instance == null) {
			throw new IllegalStateException("there is no active instance, call initInstance(...) method to initialized and obtain new instance");
		}
		try {
			instance.connection.executeCommand(SimulinkCommands.stopBufferedSimulation());
			instance.connection.executeCommand(SimulinkCommands.closeAllModels());
			instance.connection.disconnect();
		} catch (IOException e) { 	// if we can't disconnect by normal way, we kill whole matlab process
			try {
				Process p = Runtime.getRuntime().exec("taskkill /F /IM matlab.exe /T");
				p.waitFor();
			} catch (InterruptedException e1) {
				// Restore the interrupted status
				Thread.currentThread().interrupt();
			}
			throw e;
		} finally {
//			instance.connection = null;
			instance = null;
		}
	}
	
	
	public void openModel(String modelName, boolean build) throws IOException, IllegalStateException {
		if(instance == null) {
			throw new IllegalStateException("there is no active instance, call initInstance(...) method to initialized and obtain new instance");
		}
		String extractedModelName = extractModelName(modelName);
		String simulinkModelsDir = baseDir+File.separator+SIMULINK_MODELS_DIR_SUFFIX;
		try {
			String initScriptName = extractedModelName+"_init";
			String fullInitScriptPath = simulinkModelsDir+File.separator+initScriptName+".m";
			if(new File(fullInitScriptPath).exists()) {
				connection.executeCommand(initScriptName);
			}
			
			connection.executeCommand(SimulinkCommands.openModel(extractedModelName+".mdl"));
			if(build) {
				connection.executeCommand(SimulinkCommands.buildModel(extractedModelName));
			}		
		} catch (IOException e) {	// if something bad happened while opening model, we try close it to maintain consistency 
			connection.executeCommand(SimulinkCommands.closeModel(extractedModelName));
			
			String cleanupScriptName = extractedModelName+"_cleanup";
			String fullCleanupScriptPath = simulinkModelsDir+File.separator+cleanupScriptName+".m";
			if(new File(fullCleanupScriptPath).exists()) {
				connection.executeCommand(cleanupScriptName);
			}

			throw e;
		}
	}
	

	public void startBufferedSimulation(String modelName) throws IOException, IllegalStateException {
		if(instance == null) {
			throw new IllegalStateException("there is no active instance, call initInstance(...) method to initialized and obtain new instance");
		}
		connection.executeCommand(SimulinkCommands.startBufferedSimulation(extractModelName(modelName)));
	}
	

	public void togglePauseBufferedSimulation() throws IOException, IllegalStateException {
		if(instance == null) {
			throw new IllegalStateException("there is no active instance, call initInstance(...) method to initialized and obtain new instance");
		}
		connection.executeCommand(SimulinkCommands.togglePauseBufferedSimulation());
	}
	
	public String setParamValue(String modelName, String blockName, 
			String paramName, String paramValue) throws IOException, IllegalStateException {
		if(instance == null) {
			throw new IllegalStateException("there is no active instance, call initInstance(...) method to initialized and obtain new instance");
		}
		String extractedModelName = extractModelName(modelName);
		String command = SimulinkCommands.setParamValue(extractedModelName, blockName, paramName, paramValue);
		connection.executeCommand(command);
		command = SimulinkCommands.getParamValue(extractedModelName, blockName, paramName, "value");
		connection.executeCommand(command);
		return connection.getString("value");
	}
	
	public Map<String, Map<String, String>> setParamsValues(String modelName, 
			Map<String, Map<String, String>> blocksParamsValuesMap) throws IOException, IllegalStateException {
		if(instance == null) {
			throw new IllegalStateException("there is no active instance, call initInstance(...) method to initialized and obtain new instance");
		}	
		String extractedModelName = extractModelName(modelName);
		Map<String, Map<String, String>> blocksParamsNewValuesMap = new HashMap<>();
		for (Entry<String, Map<String, String>> blocksParamsValuesEntrySet : blocksParamsValuesMap.entrySet()) {
			String blockName = blocksParamsValuesEntrySet.getKey();
			Map<String, String> paramsValuesMap = blocksParamsValuesEntrySet.getValue();
			Map<String, String> paramsNewValuesMap = new HashMap<>();
			for (Entry<String, String> paramsValuesEntrySet : paramsValuesMap.entrySet()) {
				String paramName = paramsValuesEntrySet.getKey();
				String paramValue = paramsValuesEntrySet.getValue();
				String command = SimulinkCommands.setParamValue(extractedModelName, blockName, paramName, paramValue);
				connection.executeCommand(command);
				command = SimulinkCommands.getParamValue(extractedModelName, blockName, paramName, "value");
				connection.executeCommand(command);
				String result = connection.getString("value");
				paramsNewValuesMap.put(paramName, result);
			}
			blocksParamsNewValuesMap.put(blockName, paramsNewValuesMap);
		}
		
		return blocksParamsNewValuesMap;
	}

	public String getParamValue(String modelName, 
			String blockName, String paramName) throws IOException, IllegalStateException {
		if(instance == null) {
			throw new IllegalStateException("there is no active instance, call initInstance(...) method to initialized and obtain new instance");
		}	
		String command = SimulinkCommands.getParamValue(extractModelName(modelName), blockName, paramName, "value");
		connection.executeCommand(command);
		return connection.getString("value");
	}
	
	public Map<String, Map<String, String>> getParamsValues(String modelName, 
			Map<String, List<String>> blocksParamsMap) throws IOException, IllegalStateException {
		if(instance == null) {
			throw new IllegalStateException("there is no active instance, call initInstance(...) method to initialized and obtain new instance");
		}	
		String extractedModelName = extractModelName(modelName);
		Map<String, Map<String, String>> blocksParamsValuesMap = new HashMap<>();
		for (Entry<String, List<String>> blocksParamsEntrySet : blocksParamsMap.entrySet()) {
			String blockName = blocksParamsEntrySet.getKey();
			List<String> paramsList = blocksParamsEntrySet.getValue();
			Map<String, String> paramsValuesMap = new HashMap<>();
			for (String paramName : paramsList) {
				String command = SimulinkCommands.getParamValue(extractedModelName, blockName, paramName, "value");
				connection.executeCommand(command);
				String result = connection.getString("value");
				paramsValuesMap.put(paramName, result);
			}
			blocksParamsValuesMap.put(blockName, paramsValuesMap);
		}
		
		return blocksParamsValuesMap;
	}
		
	@SuppressWarnings("unchecked")
	public Hashtable<String, Hashtable<Double, Object>> getGraphData() throws IOException, IllegalStateException {
		if(instance == null) {
			throw new IllegalStateException("there is no active instance, call initInstance(...) method to initialized and obtain new instance");
		}
		Hashtable<String, Hashtable<Double, Object>> bufferData = null;
		connection.executeCommand(SimulinkCommands.readBuffer());
		try {
			Excerpt excerpt = bufferChronicle.createExcerpt();
			excerpt.startExcerpt(Integer.MAX_VALUE);
			bufferData = (Hashtable<String, Hashtable<Double, Object>>)excerpt.readObject();
		} catch(Exception e) {
			throw new IOException(e);
		}
		
		return bufferData;
	}
	
	
	public void stopBufferedSimulation() throws IOException, IllegalStateException {
		if(instance == null) {
			throw new IllegalStateException("there is no active instance, call initInstance(...) method to initialized and obtain new instance");
		}
		connection.executeCommand(SimulinkCommands.stopBufferedSimulation());
	}
	
	public void closeModel(String modelName) throws IOException, IllegalStateException {
		if(instance == null) {
			throw new IllegalStateException("there is no active instance, call initInstance(...) method to initialized and obtain new instance");
		}
		String extractedModelName = extractModelName(modelName);
		connection.executeCommand(SimulinkCommands.stopBufferedSimulation());
		connection.executeCommand(SimulinkCommands.closeModel(extractedModelName));

		String cleanupScriptName = extractedModelName+"_cleanup";
		String simulinkModelsDir = baseDir+File.separator+SIMULINK_MODELS_DIR_SUFFIX;
		String fullCleanupScriptPath = simulinkModelsDir+File.separator+cleanupScriptName+".m";
		if(new File(fullCleanupScriptPath).exists()) {
			connection.executeCommand(cleanupScriptName);
		}
	}
	
	public void executeCommand(String command) throws IOException, IllegalStateException {
		if(instance == null) {
			throw new IllegalStateException("there is no active instance, call initInstance(...) method to initialized and obtain new instance");
		}
		connection.executeCommand(command);
	}
	
	public String executeCommandWithOutput(String command) throws IOException, IllegalStateException {
		if(instance == null) {
			throw new IllegalStateException("there is no active instance, call initInstance(...) method to initialized and obtain new instance");
		}		
		String result = connection.executeCommandWithOutput(command);
		
		return result;
	}
	
	public String generateWebView(String modelName) throws IOException, IllegalStateException {
		if(instance == null) {
			throw new IllegalStateException("there is no active instance, call initInstance(...) method to initialized and obtain new instance");
		}
		File tmpDir = new File(baseDir+File.separator+WEB_VIEW_TMP_DIR);
		if(!tmpDir.isDirectory()) {
			tmpDir.mkdir();
		}
		boolean wasModelOpened = false;
		try {
			wasModelOpened = isModelOpened(modelName);
			if(!wasModelOpened) {
				openModel(modelName, false);
			}
			connection.executeCommand("cd 'webViewTmp'");
			connection.executeCommand(SimulinkCommands.generateWebView(extractModelName(modelName), "filePath"));
			String webViewHtmlFilePath = connection.getString("filePath");
			
			return webViewHtmlFilePath;
		} finally {
			connection.executeCommand("cd '"+baseDir+"'");
			if(!wasModelOpened) {
				closeModel(modelName);
			}
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public Hashtable<String, List<ParamProperties>> getModelInfo(String modelName) throws IOException, IllegalStateException {
		if(instance == null) {
			throw new IllegalStateException("there is no active instance, call initInstance(...) method to initialized and obtain new instance");
		}
		Hashtable<String, List<ParamProperties>> modelInfoData = null;
		boolean wasModelOpened = false;
		try {
			wasModelOpened = isModelOpened(modelName);
			if(!wasModelOpened) {
				openModel(modelName, false);
			}
			connection.executeCommand(SimulinkCommands.generateModelInfo(extractModelName(modelName)));
		} finally {
			if(!wasModelOpened) {
				closeModel(modelName);
			}
		}
		try {
			Excerpt excerpt = modelInfoChronicle.createExcerpt();
			excerpt.startExcerpt(Integer.MAX_VALUE);
			modelInfoData = (Hashtable<String, List<ParamProperties>>)excerpt.readObject();
		} catch(Exception e) {
			throw new IOException(e);
		}
		
		return modelInfoData;
	}
	
	public List<String> getListOfScopes(String modelName) throws IllegalStateException, IOException {
		if(instance == null) {
			throw new IllegalStateException("there is no active instance, call initInstance(...) method to initialized and obtain new instance");
		}
		List<String> scopes = null;
		boolean wasModelOpened = false;
		try {
			wasModelOpened = isModelOpened(modelName);
			if(!wasModelOpened) {
				openModel(modelName, false);
			}
			connection.executeCommand("generateScopeInfo('"+modelName+"')");
		} finally {
			if(!wasModelOpened) {
				closeModel(modelName);
			}
		}
		try {
			Excerpt excerpt = modelInfoChronicle.createExcerpt();
			excerpt.startExcerpt(Integer.MAX_VALUE);
			scopes = (List<String>) excerpt.readObject();
		} catch(Exception e) {
			throw new IOException(e);
		}
		
		return scopes;
	}
	
	public boolean isModelOpened(String modelName) throws IOException, IllegalStateException {
		if(instance == null) {
			throw new IllegalStateException("there is no active instance, call initInstance(...) method to initialized and obtain new instance");
		}
		connection.executeCommand(SimulinkCommands.isModelOpened(extractModelName(modelName), "isOpened"));
		double result = connection.getScalar("isOpened");
		if(result == 0) {
			return false;
		} else {
			return true;
		}
	}
	
	public void reinitializeMatlabWorkspace() throws IllegalStateException, IOException {
		executeCommand(SimulinkCommands.closeAllModels());
		executeCommand("cd '"+baseDir+"'");
	}
	
	public static String extractModelName(String modelPath) {
//		String goodModelPath = modelPath.replaceAll("\\\\", "/");
//		String modelName = goodModelPath;
//		int lastSeparatorPosition = goodModelPath.lastIndexOf("/");
//		if(lastSeparatorPosition != -1) {
//			modelName = goodModelPath.substring(lastSeparatorPosition+1);
//		}
		
		String modelName = new File(modelPath).getName();
		
		if (modelName.endsWith(".mdl")) {
			modelName = modelName.substring(0, modelName.length()-4);
		}
		
		return modelName;
	}
	
}
