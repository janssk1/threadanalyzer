// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   StackParser.java

package com.github.janssk1.threadanalyzer;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ThreadDumpProvider implements StackTraceProvider {

    private String line;
    private static final Pattern MONITOR_ID = Pattern.compile("<.*>");

    private static final Pattern EJB_POOLED_VIEW = Pattern.compile("\\$\\$\\$view.*\\.");
    private static final Pattern GENERATED_METHOD = Pattern.compile("sun.reflect.GeneratedMethodAccessor[0-9]*");
    private static final Pattern PROXY = Pattern.compile("sun.proxy.\\$Proxy[0-9]*");


    public ThreadDumpProvider() {
    }


    @Override
    public void collectStackTraces(ResourceDescriptor input, StackTraceProviderCallback callback) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input.openStream()))) {
            do
                line = reader.readLine();
            while (line != null && !line.startsWith("\""));
            do {
                Stack stack = readStack(reader);
                callback.stackDetected(stack);
            } while (line != null);
        }
    }

    private class Pair {
        private final String normalized;
        private final String raw;

        public Pair(String normalized, String raw) {
            this.normalized = normalized;
            this.raw = raw;
        }
    }
    private Stack readStack(BufferedReader reader)
            throws IOException {
        //StringBuffer raw = new StringBuffer(line);
        final Stack res = new Stack();
        res.addLine("HEADER", line);
        List<Pair> lines = new LinkedList<>();
        readStackTrace(reader, lines);
        Collections.reverse(lines);
        for (Pair pair : lines) {
            res.addLine(pair.normalized, pair.raw);
        }
        if (line != null)
            do {
                //raw.append("\n").append(line);
                line = reader.readLine();
            } while (line != null && !line.startsWith("\""));

        return res;
    }

    private void readStackTrace(BufferedReader reader, List<Pair> res)
            throws IOException {
        do {
            if ((line = reader.readLine()) == null)
                break;
            String trimmed = line.trim();
            if (trimmed.length() == 0)
                break;
            String normalized = trimmed;
            //trimmed.startsWith("java.lang.Thread.State")
            if (trimmed.startsWith("- ")) {
                //lock mgnt
                normalized = normalizePattern(MONITOR_ID, normalized);
            } else {
                for (Pattern pattern : new Pattern[]{EJB_POOLED_VIEW, PROXY, GENERATED_METHOD}) {
                    normalized = normalizePattern(pattern,  normalized);
                }
            }
            res.add(new Pair(normalized, line));
        } while (true);
    }

    private String normalizePattern(Pattern pattern, String input) {
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            StringBuffer sb = new StringBuffer();
            matcher.appendReplacement(sb, pattern.toString());
            matcher.appendTail(sb);
            return sb.toString();
        } else {
            return input;
        }
    }

}
