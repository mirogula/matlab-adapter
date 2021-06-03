function modelInfoTest(modelName)
    % check if simulink model exists
    if exist(modelName) ~= 4
        error('WebLab:invalidModel', 'simulink model "%s" does not exist', modelName);
    end
    
    % check if simulink model is opened
    if ~bdIsLoaded(modelName)
        error('WebLab:notLoaded', 'simulink model "%s" is not not loaded or opened', modelName);
    end

    blocks = find_system(modelName, 'FollowLinks', 'on');
    
    for i=2:numel(blocks)
       blockName = blocks{i};
       blockParamsStruct = get_param(blocks{i}, 'DialogParameters');
       paramNames = fieldnames(blockParamsStruct);
       for j=1:numel(paramNames);
           paramName = paramNames{j};
           paramPropertiesStruct = blockParamsStruct.(paramNames{j});
           paramValue = get_param(blocks{i}, paramNames{j});
           %disp(paramPropertiesStruct.Type)
           if strcmpi(paramPropertiesStruct.Type, 'string') | ...
                   strcmpi(paramPropertiesStruct.Type, 'boolean') | ...
                   strcmpi(paramPropertiesStruct.Type, 'enum')
               blockName = blocks{i}
               paramName = paramNames{j}
               paramPropertiesStruct = blockParamsStruct.(paramNames{j})
               paramValue = get_param(blocks{i}, paramNames{j})
           end
       end
    end
end