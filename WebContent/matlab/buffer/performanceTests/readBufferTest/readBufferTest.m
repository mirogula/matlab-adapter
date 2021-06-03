addpath('../../');

NUM_OF_READS = 20;

readToWorkspaceTimeArray = [];
readToHDDTimeArray = [];
readToMemoryTImeArray = [];

open_system('vrmaglev');

fprintf('READ TO WORKSPACE TEST:\n\n');
startBufferedSimulation('vrmaglev')
for i=1:NUM_OF_READS
    fprintf('test number: %d\n', i);
    tic;
    readBuffer_toWorkspace
    readToWorkspaceTime = toc
    readToWorkspaceTimeArray = [readToWorkspaceTimeArray; readToWorkspaceTime];
    pause2(1);
end
stopBufferedSimulation
pause2(1);
fprintf('----------------------------------------------\n\n');


fprintf('READ TO HDD TEST:\n\n');
startBufferedSimulation('vrmaglev')
for i=1:NUM_OF_READS
    fprintf('test number: %d\n', i);
    tic;
    readBuffer_toHDD
    readToHDDTime = toc
    readToHDDTimeArray = [readToHDDTimeArray; readToHDDTime];
    pause2(1);
end
stopBufferedSimulation
pause2(1);
fprintf('----------------------------------------------\n\n');


fprintf('READ TO MEMORY TEST:\n\n');
initBufferIPC;
startBufferedSimulation('vrmaglev')
for i=1:NUM_OF_READS
    fprintf('test number: %d\n', i);
    tic;
    readBuffer_toMemory
    readToMemoryTime = toc
    readToMemoryTImeArray = [readToMemoryTImeArray; readToMemoryTime];
    pause2(1);
end
stopBufferedSimulation
pause2(1);
fprintf('----------------------------------------------\n\n');
fprintf('RESULTS:\n');

readToWorkspaceTimeAvg = sum(readToWorkspaceTimeArray)/NUM_OF_READS

readToHDDTimeAvg = sum(readToHDDTimeArray)/NUM_OF_READS

readToMemoryTimeAvg = sum(readToMemoryTImeArray)/NUM_OF_READS
