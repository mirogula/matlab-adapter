function startListener
    global globalModelName;
    global scopeNames;
    global eh;
    eh = [];
    try     
        for i=1:length(scopeNames);
            eh = [eh, add_exec_event_listener(char(scopeNames(i)), 'PostOutputs', @bufferValues)];
        end
    catch ex
       globalModelName = '';
       rethrow ex;
    end
end