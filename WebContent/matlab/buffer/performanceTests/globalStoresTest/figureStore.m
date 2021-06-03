function figureStore
    maStoreHandle = min(findobj('type','figure', 'name', 'maStore'));
    buffer = getappdata(maStoreHandle, 'buffer');

    newBuffer = java.util.Hashtable;
    scopeNames = buffer.keys;
    while scopeNames.hasNext
       scopeName = char(scopeNames.next);
       newBuffer.put(scopeName, java.util.Hashtable); 
    end
 
    setappdata(maStoreHandle, 'buffer', newBuffer);
    %excerpt.writeObject(buffer);
end

