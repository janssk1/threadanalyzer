package com.github.janssk1.threadanalyzer;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

public class TarGzStackTraceProvider implements StackTraceProvider {
    @Override
    public void collectStackTraces(ResourceDescriptor input, StackTraceProviderCallback callback) throws IOException {
        final NonClosingTarArchiveInputStream stream = new NonClosingTarArchiveInputStream(new GZIPInputStream(input.openStream()));
        try {
            TarArchiveEntry nextTarEntry;
            while ((nextTarEntry = stream.getNextTarEntry()) != null) {
                if (nextTarEntry.isDirectory()) {
                    continue;
                }
                ResourceDescriptor descriptor = new MyResourceDescriptor(nextTarEntry, stream);

                final StackTraceProvider itemProvider = StackTraceProviderFactory.createProvider(descriptor);
                if (itemProvider != null) {
                    if (!descriptor.getName().endsWith(".100")) {
                        System.out.println("Scanning " + descriptor.getName());
                        itemProvider.collectStackTraces(descriptor, callback);
                    } else {
                        System.out.println("Ignoring " + descriptor.getName());
                    }
                }
            }
        } finally {
            stream.fullClose();
        }
    }

    private static class NonClosingTarArchiveInputStream extends TarArchiveInputStream {
        public NonClosingTarArchiveInputStream(GZIPInputStream is) {
            super(is);
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

        private final TarArchiveEntry nextTarEntry;
        private final TarArchiveInputStream stream;


        private boolean opened = false;

        public MyResourceDescriptor(TarArchiveEntry nextTarEntry, TarArchiveInputStream stream) {
            this.nextTarEntry = nextTarEntry;
            this.stream = stream;
        }

        @Override
        public String getName() {
            return nextTarEntry.getName();
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
        StackAggregateNode.create(new FileResourceDescriptor(new File("C:\\Users\\janssk1\\Desktop\\mdm102.logs.09.03.15-12.25.40.tar.gz")));
    }
}
