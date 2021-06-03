function stopBufferedSimulation
    currentDir = fileparts(mfilename('fullpath'));
    libPath = strcat(currentDir, filesep, 'lib');
    addpath(libPath);
    global globalModelName;
    if ~isempty(globalModelName)
        set_param(globalModelName, 'simulationcommand', 'stop');
    end
    globalModelName = '';
end