function globalVariableStore
    global globalBuffer;

    newBuffer = java.util.Hashtable;
    scopeNames = globalBuffer.keys;
    while scopeNames.hasNext
       scopeName = char(scopeNames.next);
       newBuffer.put(scopeName, java.util.Hashtable); 
    end

    tmpBuffer = globalBuffer;
    globalBuffer = newBuffer;
    %excerpt.writeObject(tmpBuffer);

end

