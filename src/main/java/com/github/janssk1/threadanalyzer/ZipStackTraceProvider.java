package com.github.janssk1.threadanalyzer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipStackTraceProvider implements StackTraceProvider {
    @Override
    public void collectStackTraces(ResourceDescriptor input, StackTraceProviderCallback callback) throws IOException {
        final NonClosingZipInputStream stream = new NonClosingZipInputStream(input.openStream());
        try {
            ZipEntry nextEntry;
            while ((nextEntry = stream.getNextEntry()) != null) {
                if (nextEntry.isDirectory()) {
                    continue;
                }
                ResourceDescriptor descriptor = new MyResourceDescriptor(nextEntry, stream);

                final StackTraceProvider itemProvider = StackTraceProviderFactory.createProvider(descriptor);
                if (itemProvider != null) {
                    //if (!descriptor.getName().endsWith(".100")) {
                        System.out.println("Scanning " + descriptor.getName());
                        itemProvider.collectStackTraces(descriptor, callback);
                  //  } else {
                  //      System.out.println("Ignoring " + descriptor.getName());
                  //  }
                }
            }
        } finally {
            stream.fullClose();
        }
    }

    private static class NonClosingZipInputStream extends ZipInputStream {
        public NonClosingZipInputStream(InputStream stream) {
            super(stream);
        }

        @Override
        public void close() throws IOException {
            //do nothing
        }

        public void fullClose() throws IOException {
            super.close();
        }
    }

    private static class MyResourceDescriptor implements ResourceDescriptor {

        private final ZipEntry nextEntry;
        private final InputStream stream;


        private boolean opened = false;

        public MyResourceDescriptor(ZipEntry nextEntry, InputStream stream) {
            this.nextEntry = nextEntry;
            this.stream = stream;
        }

        @Override
        public String getName() {
            return nextEntry.getName();
        }

        @Override
        public InputStream openStream() throws IOException {
            if (!opened) {
                opened = true;
                return stream;
            } else {
                throw new IllegalStateException();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        StackAggregateNode.create(new FileResourceDescriptor(new File("C:\\Users\\janssk1\\Desktop\\error_03_19_2015.zip")));
    }
}
