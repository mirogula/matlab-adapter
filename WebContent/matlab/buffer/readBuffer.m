function readBuffer
    global buffer;
    global scopeNames;
    global bufferChronicle;
    
    newBuffer = java.util.Hashtable;
    for i=1:length(scopeNames)
       newBuffer.put(scopeNames{i}, java.util.Hashtable); 
    end
    
    excerpt = bufferChronicle.createExcerpt();
    excerpt.startExcerpt(java.lang.Integer.MAX_VALUE);
    
    tmpBuffer = buffer;
    buffer = newBuffer;
    excerpt.writeObject(tmpBuffer);
end