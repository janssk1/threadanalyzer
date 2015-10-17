package com.github.janssk1.threadanalyzer;

import java.io.IOException;
import java.io.InputStream;

/**
* Created by janssk1 on 3/12/2015.
*/
class ClasspathResourceDescriptor implements ResourceDescriptor {
    private final String resource;

    ClasspathResourceDescriptor(String resource) {
        this.resource = resource;
    }

    @Override
    public String getName() {
        return resource;
    }

    @Override
    public InputStream openStream() throws IOException {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
    }
}
