function startBufferedSimulation(modelName)
    % check if simulink model exists
    if exist(modelName) ~= 4
        error('WebLab:invalidModel', 'simulink model "%s" does not exist', modelName);
    end
    
    % check if simulink model is opened
    if ~bdIsLoaded(modelName)
        error('WebLab:notLoaded', 'simulink model "%s" is not not loaded or opened', modelName);
    end
    
    % add path to lib files, which contains helper functions
    currentDir = fileparts(mfilename('fullpath'));
    libPath = strcat(currentDir, filesep, 'lib');
    addpath(libPath);
    
    % define global variables
    global globalModelName;
    global scopeNames;
    global buffer;
    
    if ~isempty(globalModelName)
        if strcmpi(globalModelName, modelName)
            error('WebLab:alreadyRunning', 'model "%s" is already running', globalModelName);
        end
        error('WebLab:bufferInUse', 'only one model can use buffer at the same time');
    end
    globalModelName = modelName;
      
    scopeNames = find_system(globalModelName, 'BlockType', 'Scope');
    
    buffer = java.util.Hashtable;
    for i=1:length(scopeNames)
       buffer.put(scopeNames{i}, java.util.Hashtable); 
    end
    
    try
        set_param(globalModelName, 'StartFcn', 'startListener');
        set_param(globalModelName, 'StopFcn', 'stopListener');
        set_param(globalModelName, 'simulationcommand', 'connect');
        set_param(globalModelName, 'simulationcommand', 'start');
    catch ex
        globalModelName = '';
        rethrow(ex);
    end
end
