function retBuffer = readBuffer_toWorkspace
    global buffer;
    global scopeNames;
    
    newBuffer = java.util.Hashtable;
    for i=1:length(scopeNames)
       newBuffer.put(scopeNames{i}, java.util.Hashtable); 
    end
    
    retBuffer = buffer;
    buffer = newBuffer;
end