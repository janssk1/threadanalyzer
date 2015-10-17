// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   StackParser.java

package com.github.janssk1.threadanalyzer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Extract stacktraces from MDM log files
 */
public class MdmLogStackTraceProvider implements StackTraceProvider {

    private String previousLine;
    private String currentLine;
    private BufferedReader reader;

    private String readLine() throws IOException {
        previousLine = currentLine;
        currentLine = reader.readLine();
        return currentLine;
    }

    @Override
    public void collectStackTraces(ResourceDescriptor input, StackTraceProviderCallback callback) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input.openStream()))) {
            this.reader = reader;
            while ((readLine()) != null) {
                if (isPartOfStack(currentLine)) {
                    callback.stackDetected(readStack());
                }
            }
        }
    }

    private Stack readStack() throws IOException {
        Stack stack = new Stack();
        if (previousLine != null) {
            /*int index = previousLine.indexOf("] [A:");
            if (index != -1) {
                index = previousLine.indexOf("]", index + 5);

                String firstLine = previousLine.substring(index + 2);*/
                stack.addLine("ERRORMSG", previousLine);//ERROR message is typically unique
            //}
        }
        stack.addLine(currentLine);
        while ((readLine()) != null && isPartOfStack(currentLine)) {
            stack.addLine(isNestedMessage(currentLine) ? "CAUSED BY" : currentLine, currentLine);
        }
        return stack;
    }

    private boolean isPartOfStack(String s) {
        return s.startsWith("\t") || isNestedMessage(s);
    }

    private boolean isNestedMessage(String s) {
        return s.startsWith("Caused by");
    }

}
