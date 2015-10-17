package com.github.janssk1.threadanalyzer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileResourceDescriptor implements ResourceDescriptor {

    private final File path;

    public FileResourceDescriptor(File path) {
        this.path = path;
    }

    @Override
    public String getName() {
        return path.getPath();
    }

    @Override
    public InputStream openStream() throws IOException {
        return new FileInputStream(getName());
    }
}
