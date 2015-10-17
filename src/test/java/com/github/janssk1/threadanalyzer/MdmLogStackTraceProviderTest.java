package com.github.janssk1.threadanalyzer;

import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;

public class MdmLogStackTraceProviderTest  {

    private final MdmLogStackTraceProvider provider = new MdmLogStackTraceProvider();

    @Test
    public void testSimpleStack() throws IOException {
        final StackAggregateNode node = StackAggregateNode.create(new ClasspathResourceDescriptor("mdm.log"), provider);
        assertEquals(4, node.getStackCount());
        assertEquals(4, node.getAllStacks(10).size());
        assertEquals(1, node.childList.size());
        final List<StackAggregateNode> childList = node.childList.get(0).childList;
        assertEquals(2, childList.size());
        assertEquals(2, childList.get(0).getStackCount());
        assertThat(childList.get(0).toString(), containsString("HttpProducer.populateHttpOperationFailedException(HttpProducer.java:229)"));
        assertEquals(2, childList.get(1).getStackCount());
        assertThat(childList.get(1).toString(), containsString("at java.net.SocketInputStream.read(SocketInputStream.java:189)"));
    //    assertThat(node.childList.get(0).toString(), containsString("java.lang.Thread.run(Thread.java:745)"));
    //    assertEquals(5, node.childList.size());
        // node.
        //node.getAStack()
    }


}