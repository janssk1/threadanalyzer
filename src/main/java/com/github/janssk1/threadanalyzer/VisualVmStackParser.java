// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   StackParser.java

package com.github.janssk1.threadanalyzer;


public class VisualVmStackParser {
/*
    private String line;
    private static final Pattern EJB_POOLED_VIEW = Pattern.compile("\\$\\$\\$view.*\\.");

    public VisualVmStackParser() {
    }

    public StackAggregateNode readStackTrace(File file)
            throws IOException {
        System.out.println((new StringBuilder()).append("Reading ").append(file).toString());
        return readStackTrace(new FileReader(file));
    }

    public StackAggregateNode readStackTrace(Reader r)
        throws IOException {
        BufferedReader reader = new BufferedReader(r);
        try {
            StackAggregateNode root = new StackAggregateNode("root");
            do
                line = reader.readLine();
            while (line != null && !line.startsWith("\""));
            do {
                Stack stack = readStack(reader);
                root.addStack(stack, 0);
            } while (line != null);
            StackAggregateNode child;
            for (Iterator i$ = root.childList.iterator(); i$.hasNext(); child.compress())
                child = (StackAggregateNode) i$.next();

            return root;
        } finally {
            reader.close();
        }
    }

    private Stack readStack(BufferedReader reader)
            throws IOException {
        StringBuffer raw = new StringBuffer(line);
        String stack[] = readStackTrace(reader, raw);
        if (line != null)
            do {
                raw.append("\n").append(line);
                line = reader.readLine();
            } while (line != null && !line.startsWith("\""));
        return new Stack(stack, raw.toString());
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
            if (!trimmed.startsWith("Local Variable:")) {
                res.add(line);
            }
        } while (true);
        Collections.reverse(res);
        return (String[]) res.toArray(new String[res.size()]);
    }
*/
}
