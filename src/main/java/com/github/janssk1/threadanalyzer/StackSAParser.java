// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   StackParser.java

package com.github.janssk1.threadanalyzer;


public class StackSAParser {
/*
    private String line;
    private static final Pattern EJB_POOLED_VIEW = Pattern.compile("\\$\\$\\$view.*\\.");

    @Override
    public void collectStackTraces(ResourceDescriptor input, StackTraceProviderCallback callback) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input.openStream()))) {
            do
                line = reader.readLine();
            while (line != null && !line.startsWith("Thread "));
            do {
                StackI stack = readStack(reader);
                callback.stackDetected(stack);
            } while (line != null);
        }
    }

    private StackI readStack(BufferedReader reader)
            throws IOException {
        StringBuffer raw = new StringBuffer(line);
        String stack[] = readStackTrace(reader, raw);
        if (line != null)
            do {
                raw.append("\n").append(line);
                line = reader.readLine();
            } while (line != null && !line.startsWith("Thread "));
        return new StackI(stack, raw.toString());
    }

    private String[] readStackTrace(BufferedReader reader, StringBuffer raw)
            throws IOException {
        List res = new LinkedList();
        do {
            if ((line = reader.readLine()) == null)
                break;
            String trimmed = line.trim();
            raw.append("\n").append(line);
            if (trimmed.length() == 0)
                break;
          //  if (!trimmed.startsWith("- ") && !trimmed.startsWith("java.lang.Thread.State")) {
                Matcher matcher = EJB_POOLED_VIEW.matcher(line);
                if (matcher.find()) {
                    StringBuffer sb = new StringBuffer();
                    matcher.appendReplacement(sb, "\\$\\$\\$view-x.");
                    matcher.appendTail(sb);
                    line = sb.toString();
                }
                res.add(line);
          //  }
        } while (true);
        Collections.reverse(res);
        return (String[]) res.toArray(new String[res.size()]);
    }
*/
}
