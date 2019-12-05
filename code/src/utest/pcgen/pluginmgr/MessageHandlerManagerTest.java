/*
 * Copyright James Dempsey, 2014
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.pluginmgr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * The Class {@code MessageHandlerManagerTest} checks that MessageHandlerManager is
 * working correctly.
 */
class MessageHandlerManagerTest
{

    /**
     * Test method for {@link pcgen.pluginmgr.MessageHandlerManager#addMember(pcgen.pluginmgr.PCGenMessageHandler)}.
     */
    @Test
    public void testAddMember()
    {
        MessageHandlerManager cor = new MessageHandlerManager();
        MessageRecorder mr = new MessageRecorder();
        assertEquals(0, mr.messageCount, "No messages exepected before add");
        cor.addMember(mr);
        assertNotNull(mr.lastMsg, "MessageHandlerManager should have sent out message");
        assertEquals(cor, mr.lastMsg.getSource(), "MessageHandlerManager should have sent out message");
        assertEquals(1, mr.messageCount, "One messages expected after add");

        int prevCount = mr.messageCount;
        mr.lastMsg = null;
        PCGenMessage msg = new PCGenMessage(this);
        cor.getPostbox().handleMessage(msg);
        assertEquals(msg, mr.lastMsg, "Expected message to be delivered");
        assertEquals(prevCount + 1, mr.messageCount, "Expected one further message");
    }

    /**
     * Test method for {@link pcgen.pluginmgr.MessageHandlerManager#removeMember(pcgen.pluginmgr.PCGenMessageHandler)}.
     */
    @Test
    public void testRemoveMember()
    {
        MessageHandlerManager cor = new MessageHandlerManager();
        MessageRecorder mr = new MessageRecorder();
        assertEquals(0, mr.messageCount, "No messages exepected before add");
        cor.addMember(mr);
        int prevCount = mr.messageCount;
        mr.lastMsg = null;
        cor.removeMember(mr);
        assertNotNull(mr.lastMsg, "MessageHandlerManager should have sent out message");
        assertEquals(cor, mr.lastMsg.getSource(), "MessageHandlerManager should have sent out message");
        assertEquals(prevCount + 1, mr.messageCount, "One extra message expected after add");

        mr.lastMsg = null;
        cor.getPostbox().handleMessage(new PCGenMessage(this));
        assertNull(mr.lastMsg, "Expected no further messages to removed handler");
        assertEquals(prevCount + 1, mr.messageCount, "Expected no further messages to removed handler");
    }

    /**
     * Check that the postbox used by MessageHandlerManager send out messages in the right order.
     */
    @Test
    public void testPostboxMessageDistributionOrder()
    {
        MessageHandlerManager cor = new MessageHandlerManager();
        assertNotNull(cor.getPostbox(), "Postbox should be available");

        MessageRecorder firstMr = new MessageRecorder();
        MessageConsumer consumer = new MessageConsumer();
        MessageRecorder secondMr = new MessageRecorder();
        cor.addMember(firstMr);
        cor.addMember(consumer);
        cor.addMember(secondMr);

        // Check messages are delivered in order
        int prevFirstMrCount = firstMr.messageCount;
        int prevConsumerCount = consumer.messageCount;
        int prevSecondMrCount = secondMr.messageCount;
        cor.getPostbox().handleMessage(
                new PCGenMessage(this));
        assertEquals(
                prevFirstMrCount + 1, firstMr.messageCount, "Single extra message expected by first handler");
        assertEquals(
                prevConsumerCount + 1, consumer.messageCount, "Single extra message expected by consumer");
        assertEquals(
                prevSecondMrCount, secondMr.messageCount, "No extra message expected by second handler");
        assertTrue(
                firstMr.lastMessageOrder < consumer.lastMessageOrder,
                "First recorder should have received message before consumer (different source)");

        // Check source receives message last
        prevFirstMrCount = firstMr.messageCount;
        prevConsumerCount = consumer.messageCount;
        prevSecondMrCount = secondMr.messageCount;
        cor.getPostbox().handleMessage(
                new PCGenMessage(firstMr));
        assertEquals(
                prevConsumerCount + 1, consumer.messageCount, "Single extra message expected by consumer");
        assertEquals(
                prevSecondMrCount, secondMr.messageCount, "No extra message expected by second handler");
        assertEquals(
                prevFirstMrCount, firstMr.messageCount, "No extra message expected by first handler");
    }

    /**
     * Check that the postbox used by MessageHandlerManager send out messages in the right order.
     */
    @Test
    public void testPostboxMessageDistributionConsumption()
    {
        MessageHandlerManager cor = new MessageHandlerManager();
        assertNotNull(cor.getPostbox(), "Postbox should be available");

        MessageRecorder firstMr = new MessageRecorder();
        MessageRecorder secondMr = new MessageRecorder();
        cor.addMember(firstMr);
        cor.addMember(secondMr);

        // Check messages are delivered in order
        int prevFirstMrCount = firstMr.messageCount;
        int prevSecondMrCount = secondMr.messageCount;
        cor.getPostbox().handleMessage(
                new PCGenMessage(this));
        assertEquals(
                prevFirstMrCount + 1, firstMr.messageCount, "Single extra message expected by first handler");
        assertEquals(
                prevSecondMrCount + 1, secondMr.messageCount, "Single extra message expected by second handler");
        assertTrue(
                firstMr.lastMessageOrder < secondMr.lastMessageOrder,
                "First recorder should have received message before second (different source)");

        // Check source receives message last
        prevFirstMrCount = firstMr.messageCount;
        prevSecondMrCount = secondMr.messageCount;
        cor.getPostbox().handleMessage(
                new PCGenMessage(firstMr));
        assertEquals(
                prevFirstMrCount + 1, firstMr.messageCount, "Single extra message expected by first handler");
        assertEquals(
                prevSecondMrCount + 1, secondMr.messageCount, "Single extra message expected by second handler");
        assertTrue(
                firstMr.lastMessageOrder > secondMr.lastMessageOrder,
                "Second recorder should have received message before first (source)");
    }

    /**
     * The Class {@code MessageRecorder} is a message handler that
     * simply tracks the messages is receives, allowing testing of message distribution.
     */
    private static class MessageRecorder implements PCGenMessageHandler
    {
        static int messageOrderCounter = 0;

        int messageCount = 0;
        int lastMessageOrder = 0;
        PCGenMessage lastMsg = null;

        @Override
        public void handleMessage(PCGenMessage msg)
        {
            lastMsg = msg;
            messageCount++;
            lastMessageOrder = messageOrderCounter++;
        }

    }

    /**
     * The Class {@code MessageConsumer} will consume any message it receives.
     */
    private static class MessageConsumer extends MessageRecorder
    {

        @Override
        public void handleMessage(PCGenMessage msg)
        {
            super.handleMessage(msg);
            msg.consume();
        }
    }
}
