function bufferValues(rto, eventData)
    global buffer;
    scopeFullName = getfullname(rto.BlockHandle);
    for i=1:rto.NumInputPorts
        buffer.get(scopeFullName).put(i,[buffer.get(scopeFullName).get(i),[rto.CurrentTime; rto.InputPort(i).Data]]);
    end
end