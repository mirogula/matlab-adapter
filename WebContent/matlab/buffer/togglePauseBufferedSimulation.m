function togglePauseBufferedSimulation
    currentDir = fileparts(mfilename('fullpath'));
    libPath = strcat(currentDir, filesep, 'lib');
    addpath(libPath);
    global globalModelName;
    if ~isempty(globalModelName)
        status = get_param(globalModelName, 'simulationstatus');
        if strcmpi(status, 'paused')
            set_param(globalModelName, 'simulationcommand', 'continue');
        elseif strcmpi(status, 'running')
            set_param(globalModelName, 'simulationcommand', 'pause');
        end
    end
end