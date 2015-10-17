package com.github.janssk1.threadanalyzer;

import java.io.IOException;

public interface StackTraceProvider {

    void collectStackTraces(ResourceDescriptor input, StackTraceProviderCallback callback) throws IOException;
}
