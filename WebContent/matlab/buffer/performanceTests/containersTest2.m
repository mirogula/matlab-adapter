% pocet opakoveni testu
ITERATIONS = 10
% pocet prvkov vlozenych pri teste do kontajnera
NUM_ELEMENTS = 10000

javaTimeDeltaArray = [];
nativeTimeDeltaArray = [];
for i=1:ITERATIONS
    fprintf('iteration: %d\n', i);
    fprintf('java:\n\n');
    javaHash = java.util.Hashtable;
    data = [5.0; 5.0; 5.0];
    tic;
    for j=1:NUM_ELEMENTS
        if(~javaHash.containsKey(1.0))
            [rows columns] = size(data);
            rows = rows+1;
            list = java.util.ArrayList;
            for k=1:rows
               list.add(java.util.LinkedList);
            end
            javaHash.put(1.0, list);
        end
        list = javaHash.get(1.0);
        list.get(0).add(4.0);
        [rows columns] = size(data);
        for k=1:rows
           list.get(k).add(data(k)); 
        end
    end
    javaTimeDelta = toc
    javaTimeDeltaArray = [javaTimeDeltaArray; javaTimeDelta];
    
    fprintf('native matlab:\n\n');
    javaHash = java.util.Hashtable;
    data = [5.0; 5.0; 5.0];
    tic;
    for j=1:NUM_ELEMENTS
       javaHash.put(1.0, [javaHash.get(1.0), [4.0; data]]);
    end
    nativeTimeDelta = toc
    nativeTimeDeltaArray = [nativeTimeDeltaArray; nativeTimeDelta];
end

fprintf('----------------------------------------------------------\n\n');
fprintf('RESULTS:\n');

% priemerny cas naplnenia java kontajnera
javaTimeDeltaAvg = sum(javaTimeDeltaArray)/ITERATIONS

% priemerny cas naplnenia nativneho Matlab kontajnera
nativeTimeDeltaAvg = sum(nativeTimeDeltaArray)/ITERATIONS