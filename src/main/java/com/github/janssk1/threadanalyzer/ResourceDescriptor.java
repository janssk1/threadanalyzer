package com.github.janssk1.threadanalyzer;

import java.io.IOException;
import java.io.InputStream;

public interface ResourceDescriptor {

    String getName();

    InputStream openStream() throws IOException;
}
