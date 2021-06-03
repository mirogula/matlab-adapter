function readBuffer_toHDD
    global buffer;
    global scopeNames;
    
    newBuffer = java.util.Hashtable;
    for i=1:length(scopeNames)
       newBuffer.put(scopeNames{i}, java.util.Hashtable); 
    end
    
    tmpBuffer = buffer;
    buffer = newBuffer;
    oos = java.io.ObjectOutputStream(java.io.FileOutputStream('bufferData.bin'));
    oos.writeObject(tmpBuffer);
    oos.close();
end