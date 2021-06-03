function initBufferIPC(bufferFile)
    global bufferChronicle;

    %com.higherfrequencytrading.chronicle.tools.ChronicleTools.deleteOnExit(bufferFile);
    bufferChronicle = com.higherfrequencytrading.chronicle.impl.IndexedChronicle(bufferFile);
    bufferChronicle.useUnsafe(false);
end