% pocet opakoveni testu
ITERATIONS = 50

globalVarTimeDeltaArray = [];
figureStoreTimeDeltaArray = [];
noGlobalStoreTimeDeltaArray = [];
global globalBuffer;
fh = findobj('type','figure', 'name', 'maStore')
if isempty(fh) 
   fh = figure('name', 'maStore', 'visible', 'off');
end

buffer1 = java.util.Hashtable;
buffer1.put('a', java.util.Hashtable);
buffer1.put('b', java.util.Hashtable);
buffer1.put('c', java.util.Hashtable);
globalBuffer = buffer1;

buffer2 = java.util.Hashtable;
buffer2.put('a', java.util.Hashtable);
buffer2.put('b', java.util.Hashtable);
buffer2.put('c', java.util.Hashtable);
setappdata(fh, 'buffer', buffer2);

buffer3 = java.util.Hashtable;
buffer3.put('a', java.util.Hashtable);
buffer3.put('b', java.util.Hashtable);
buffer3.put('c', java.util.Hashtable);
for i=1:ITERATIONS
    fprintf('iteration: %d\n', i);
    fprintf('global variable:\n\n');
    tic;
    globalVariableStore();
    globalVarTimeDelta = toc
    globalVarTimeDeltaArray = [globalVarTimeDeltaArray; globalVarTimeDelta];
    
    fprintf('figure store:\n\n');
    tic;
    figureStore();
    figureStoreTimeDelta = toc
    figureStoreTimeDeltaArray = [figureStoreTimeDeltaArray; figureStoreTimeDelta];
    
    fprintf('global variable:\n\n');
    tic;
    buffer3 = noGlobalStore(buffer3);
    noGlobalStoreTimeDelta = toc
    noGlobalStoreTimeDeltaArray = [noGlobalStoreTimeDeltaArray; noGlobalStoreTimeDelta];
end
close(fh);

fprintf('----------------------------------------------------------\n\n');
fprintf('RESULTS:\n');

% priemerny cas naplnenia java kontajnera
globalVarTimeDeltaAvg = sum(globalVarTimeDeltaArray)/ITERATIONS

% priemerny cas naplnenia nativneho Matlab kontajnera
figureStoreTimeDeltaAvg = sum(figureStoreTimeDeltaArray)/ITERATIONS

noGlobalStoreTimeDeltaAvg = sum(noGlobalStoreTimeDeltaArray)/ITERATIONS
