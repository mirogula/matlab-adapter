% pocet opakoveni testu
ITERATIONS = 10
% pocet prvkov vlozenych pri teste do kontajnera
NUM_ELEMENTS = 10000

javaTimeDeltaArray = [];
nativeTimeDeltaArray = [];
for i=1:ITERATIONS
    fprintf('iteration: %d\n', i);
    fprintf('java hash table\n');
    javaHash = java.util.Hashtable;
    tic;
    for j=1:NUM_ELEMENTS
        javaHash.put(3.7, [javaHash.get(3.7); rand*10]);
    end
    javaTimeDelta = toc
    javaTimeDeltaArray = [javaTimeDeltaArray; javaTimeDelta];
    
    fprintf('native matlab map\n\n');
    nativeHash = containers.Map(0.0,[0.0, 0.0]);
    nativeHash.remove(0.0);
    nativeHash(6.2) = [];
    tic;
    for j=1:NUM_ELEMENTS
        nativeHash(6.2) = [nativeHash(6.2); rand*10];
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