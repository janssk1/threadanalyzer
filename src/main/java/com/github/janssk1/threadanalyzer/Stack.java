// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Stack.java

package com.github.janssk1.threadanalyzer;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

class Stack {

    final Map<Integer, String> overrides = new HashMap<>();
    final List<String> lines = new LinkedList<>();

    public void addLine(String normalized) {
        addLine(normalized, null);
    }
    public void addLine(String normalized, String raw) {
        lines.add(normalized);
        if (raw != null && !raw.equals(normalized)) {
            overrides.put(lines.size() - 1, raw);
        }
    }
}
