function generateModelInfo(modelName)
    % check if simulink model exists
    if exist(modelName) ~= 4
        error('WebLab:invalidModel', 'simulink model "%s" does not exist', modelName);
    end
    
    % check if simulink model is opened
    if ~bdIsLoaded(modelName)
        error('WebLab:notLoaded', 'simulink model "%s" is not not loaded or opened', modelName);
    end

    modelInfo = java.util.Hashtable;
    
    blocks = find_system(modelName, 'FollowLinks', 'on');
    for i=2:numel(blocks)
       blockName = blocks{i};
       blockParamsStruct = get_param(blocks{i}, 'DialogParameters');
       paramNames = fieldnames(blockParamsStruct);
       paramsList = java.util.LinkedList;
       for j=1:numel(paramNames);
           paramPropertiesStruct = blockParamsStruct.(paramNames{j});
           paramType = char(paramPropertiesStruct.Type);
           if strcmpi(paramType, 'string') || strcmpi(paramType, 'boolean') || strcmpi(paramType, 'enum')
               blockName = blocks{i};
               paramName = paramNames{j};
               paramPropertiesStruct = blockParamsStruct.(paramNames{j});
               paramValue = get_param(blocks{i}, paramNames{j});
               
               enumValuesList = java.util.LinkedList;
               if strcmpi(paramType, 'enum')
                   enumValues = paramPropertiesStruct.Enum;
                  for k=1:numel(enumValues)
                      enumValuesList.add(java.lang.String(enumValues{k}));
                  end
               end
               
               paramProperties = sk.stuba.fei.weblab.paramproperties.ParamProperties;
               paramProperties.setSimulinkName(java.lang.String(paramName));
               paramProperties.setDisplayName(java.lang.String(paramPropertiesStruct.Prompt));
               paramTypeConstant = sk.stuba.fei.weblab.paramproperties.ParamType.valueOf(upper(paramType));
               paramProperties.setType(paramTypeConstant);       
               paramProperties.setEnumList(enumValuesList);
               paramProperties.setDefaultValue(java.lang.String(paramValue));
               
               paramsList.add(paramProperties);
           end
       end
       blockName = java.lang.String(blockName);
       blockName = blockName.substring(blockName.indexOf('/')+1,blockName.length());
       blockName = blockName.replaceAll('(\r|\n)', ' ');
       modelInfo.put(blockName, paramsList);
    end
    
    global modelInfoChronicle;

    excerpt =  modelInfoChronicle.createExcerpt();
    excerpt.startExcerpt(java.lang.Integer.MAX_VALUE);
    excerpt.writeObject(modelInfo);
end

