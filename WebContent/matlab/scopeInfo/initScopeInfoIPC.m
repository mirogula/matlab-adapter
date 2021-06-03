function initModelInfoIPC(scopeInfoDataFile)
    global scopeInfoChronicle;
    
    %com.higherfrequencytrading.chronicle.tools.ChronicleTools.deleteOnExit(bufferFile);
    scopeInfoChronicle = com.higherfrequencytrading.chronicle.impl.IndexedChronicle(scopeInfoDataFile);
    scopeInfoChronicle.useUnsafe(false);
end

