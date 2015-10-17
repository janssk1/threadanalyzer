package com.github.janssk1.threadanalyzer;

import java.util.regex.Pattern;

public class StackTraceProviderFactory {

    static Pattern LOG_SUFFIX = Pattern.compile("server.log");

    public static StackTraceProvider createProvider(ResourceDescriptor resource) {
        final String name = resource.getName();
        final String suffix = name.substring(name.indexOf("."));

        if (LOG_SUFFIX.matcher(suffix).find()) {
            return new MdmLogStackTraceProvider();
        } else if (suffix.endsWith("tar.gz") || suffix.endsWith("tgz")) {
            return new TarGzStackTraceProvider();
        } else if (suffix.endsWith("zip")) {
            return new ZipStackTraceProvider();
        } else {
            return new ThreadDumpProvider();
        }
    }
}
