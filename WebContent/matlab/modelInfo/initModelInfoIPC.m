function initModelInfoIPC(modelInfoDataFile)
    global modelInfoChronicle;
    
    %com.higherfrequencytrading.chronicle.tools.ChronicleTools.deleteOnExit(bufferFile);
    modelInfoChronicle = com.higherfrequencytrading.chronicle.impl.IndexedChronicle(modelInfoDataFile);
    modelInfoChronicle.useUnsafe(false);
end

