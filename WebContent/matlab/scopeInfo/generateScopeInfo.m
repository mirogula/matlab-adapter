function generateScopeInfo(modelName)
    % check if simulink model exists
    if exist(modelName) ~= 4
        error('WebLab:invalidModel', 'simulink model "%s" does not exist', modelName);
    end
    
    % check if simulink model is opened
    if ~bdIsLoaded(modelName)
        error('WebLab:notLoaded', 'simulink model "%s" is not not loaded or opened', modelName);
    end

    scopeList = java.util.ArrayList;
    
    scopes = find_system('vrmaglev', 'BlockType', 'Scope');
    for i=1:numel(blocks)
       scopeName = scopes{i};
       scopeList.add(java.lang.String(scopeName))
    end
    
    global scopeInfoChronicle;

    excerpt =  modelInfoChronicle.createExcerpt();
    excerpt.startExcerpt(java.lang.Integer.MAX_VALUE);
    excerpt.writeObject(scopeList);
end

