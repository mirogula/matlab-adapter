function stopListener
    global globalModelName;
    global eh;
    if ~isempty(globalModelName)
        if ~isempty(get_param(globalModelName, 'StartFcn'))
            set_param(globalModelName, 'StartFcn', '');
        end
        if ~isempty(get_param(globalModelName, 'StopFcn'))
            set_param(globalModelName, 'StopFcn', '');
        end
    end
    
    for i=1:length(eh)
        if ishandle(eh(i))
            delete(eh(i))
        end
    end
    globalModelName = '';
end