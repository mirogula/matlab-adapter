function readBuffer_toMemory
    global buffer;
    global scopeNames;
    global chronicle;
    
    newBuffer = java.util.Hashtable;
    for i=1:length(scopeNames)
       newBuffer.put(scopeNames{i}, java.util.Hashtable); 
    end
    
    excerpt = chronicle.createExcerpt();
    excerpt.startExcerpt(java.lang.Integer.MAX_VALUE);
    
    tmpBuffer = buffer;
    buffer = newBuffer;
    excerpt.writeObject(tmpBuffer);
end