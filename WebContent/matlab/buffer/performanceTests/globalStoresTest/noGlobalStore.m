function newBuffer = noGlobalStore(buffer)
    newBuffer = java.util.Hashtable;
    scopeNames = buffer.keys;
    while scopeNames.hasNext
       scopeName = char(scopeNames.next);
       newBuffer.put(scopeName, java.util.Hashtable); 
    end
 
    %excerpt.writeObject(buffer);
end