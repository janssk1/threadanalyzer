// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   StackAggregateNode.java

package com.github.janssk1.threadanalyzer;

import java.io.IOException;
import java.util.*;


class StackAggregateNode implements StackNode, StackTraceProviderCallback {

    public static StackAggregateNode create(ResourceDescriptor resource) throws IOException {
        final StackTraceProvider provider = StackTraceProviderFactory.createProvider(resource);
        if (provider == null) {
            throw new IOException("Unsupported file type: " + resource.getName());
        } else {
            return create(resource, provider);
        }
    }

    public static StackAggregateNode create(ResourceDescriptor resource, StackTraceProvider provider) throws IOException {
        StackAggregateNode res = new StackAggregateNode("root");
        System.out.println((new StringBuilder()).append("Reading ").append(resource.getName()).toString());
        provider.collectStackTraces(resource, res);
        for (StackAggregateNode child : res.childList) {
            child.compress();
        }
        return res;
    }

    public StackAggregateNode(String name) {
        lines = new LinkedList<>();
        children = new LinkedHashMap<>();
        childList = new LinkedList<>();
        lines.add(name);
    }

    public String toString() {
        return (new StringBuilder()).append("[").append(getStackCount()).append("]").append(lines.get(0)).toString();
    }

    public List<String> getAllStacks(int max) {
        List<String> res = new LinkedList<>();
        List<String> parentLines = new LinkedList<>();
        collectParentLines(this.parent, parentLines);
        collectStacks(res, parentLines.toArray(new String[parentLines.size()]), max);
        return res;
    }

    private void collectParentLines(StackAggregateNode n, List<String> parentLines) {
        if (n != null) {
            collectParentLines(n.parent, parentLines);
            parentLines.addAll(n.lines);
        }
    }

    private void collectStacks(Collection<String> stacks, String[] parentLines, int max) {
        if (stacks.size() >= max) {
            return;
        }
        String[] allLines = new String[parentLines.length + this.lines.size()];
        System.arraycopy(parentLines, 0, allLines, 0, parentLines.length);
        final String[] thisLines = this.lines.toArray(new String[this.lines.size()]);
        System.arraycopy(thisLines, 0, allLines, parentLines.length, thisLines.length);
        for (Map<Integer, String> stackOverride : stackOverrides) {
            StringBuilder stackString = new StringBuilder();
            for (int i = 1; i < allLines.length;i++) {//skip ROOT
                String normalizedLine = allLines[i];
                final String override = stackOverride.get(i-1);
                stackString.append(override != null ? override : normalizedLine).append("\n");
            }
            stacks.add(stackString.toString());
            if (stacks.size() == max) {
                return;
            }
        }
        for (StackAggregateNode stackAggregateNode : childList) {
            stackAggregateNode.collectStacks(stacks, allLines, max);
        }
    }

    public int getStackCount() {
        int count = stackOverrides.size();
        for (StackAggregateNode child : children.values()) {
            count += child.getStackCount();
        }

        return count;
    }

    public void compress() {
        StackAggregateNode childToBeRemoved;
        for (; children.size() == 1; stackOverrides.addAll(childToBeRemoved.stackOverrides)) {
            childToBeRemoved = childList.get(0);
            lines.addAll(childToBeRemoved.lines);
            children = childToBeRemoved.children;
            childList = childToBeRemoved.childList;
            for (StackAggregateNode stackAggregateNode : childList) {
                stackAggregateNode.parent = this;
            }
        }

        StackAggregateNode child;
        for (Iterator i$ = childList.iterator(); i$.hasNext(); child.compress())
            child = (StackAggregateNode) i$.next();

    }

    public void addStack(Stack s, int index) {
        if (index == s.lines.size()) {
            stackOverrides.add(s.overrides);
        } else {
            String line = s.lines.get(index);
            StackAggregateNode child = children.get(line);
            if (child == null) {
                child = new StackAggregateNode(line);
                children.put(line, child);
                child.parent = this;
                childList.add(child);
            }
            child.addStack(s, index + 1);
        }
    }

    @Override
    public void stackDetected(Stack stack) {
        System.out.println("stack.lines.get(0) = " + stack.lines.get(0));
        addStack(stack, 0);
    }

    private final List<String> lines;
    private final List<Map<Integer, String>> stackOverrides = new LinkedList<>();
    private Map<String, StackAggregateNode> children;
    List<StackAggregateNode> childList;
    private StackAggregateNode parent;

}
