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

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * The Class {@code MessageHandlerManagerTest} checks that MessageHandlerManager is
 * working correctly.
 */

public class MessageHandlerManagerTest
{

	/**
	 * Test method for {@link pcgen.pluginmgr.MessageHandlerManager#addMember(pcgen.pluginmgr.PCGenMessageHandler)}.
	 */
	@Test
	public void testAddMember()
	{
		MessageHandlerManager cor = new MessageHandlerManager();
		MessageRecorder mr = new MessageRecorder();
		assertEquals("No messages exepected before add", 0, mr.messageCount);
		cor.addMember(mr);
		assertNotNull("MessageHandlerManager should have sent out message",  mr.lastMsg);
		assertEquals("MessageHandlerManager should have sent out message", cor, mr.lastMsg.getSource());
		assertEquals("One messages expected after add", 1, mr.messageCount);

		int prevCount = mr.messageCount;
		mr.lastMsg = null;
		PCGenMessage msg = new PCGenMessage(this);
		cor.getPostbox().handleMessage(msg);
		assertEquals("Expected message to be delivered", msg, mr.lastMsg);
		assertEquals("Expected one further message", prevCount+1, mr.messageCount);
	}

	/**
	 * Test method for {@link pcgen.pluginmgr.MessageHandlerManager#removeMember(pcgen.pluginmgr.PCGenMessageHandler)}.
	 */
	@Test
	public void testRemoveMember()
	{
		MessageHandlerManager cor = new MessageHandlerManager();
		MessageRecorder mr = new MessageRecorder();
		assertEquals("No messages exepected before add", 0, mr.messageCount);
		cor.addMember(mr);
		int prevCount = mr.messageCount;
		mr.lastMsg = null;
		cor.removeMember(mr);
		assertNotNull("MessageHandlerManager should have sent out message",  mr.lastMsg);
		assertEquals("MessageHandlerManager should have sent out message", cor, mr.lastMsg.getSource());
		assertEquals("One extra message expected after add", prevCount+1, mr.messageCount);
		
		mr.lastMsg = null;
		cor.getPostbox().handleMessage(new PCGenMessage(this));
		assertNull("Expected no further messages to removed handler",  mr.lastMsg);
		assertEquals("Expected no further messages to removed handler", prevCount+1, mr.messageCount);
	}

	/**
	 * Check that the postbox used by MessageHandlerManager send out messages in the right order.
	 */
	@Test
	public void testPostboxMessageDistributionOrder()
	{
		MessageHandlerManager cor = new MessageHandlerManager();
		assertNotNull("Postbox should be available", cor.getPostbox());
		
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
		assertEquals("Single extra message expected by first handler",
			prevFirstMrCount + 1, firstMr.messageCount);
		assertEquals("Single extra message expected by consumer",
			prevConsumerCount + 1, consumer.messageCount);
		assertEquals("No extra message expected by second handler",
			prevSecondMrCount, secondMr.messageCount);
		assertTrue(
			"First recorder should have received message before consumer (different source)",
			firstMr.lastMessageOrder < consumer.lastMessageOrder);

		// Check source receives message last
		prevFirstMrCount = firstMr.messageCount;
		prevConsumerCount = consumer.messageCount;
		prevSecondMrCount = secondMr.messageCount;
		cor.getPostbox().handleMessage(
			new PCGenMessage(firstMr));
		assertEquals("Single extra message expected by consumer",
			prevConsumerCount + 1, consumer.messageCount);
		assertEquals("No extra message expected by second handler",
			prevSecondMrCount, secondMr.messageCount);
		assertEquals("No extra message expected by first handler",
			prevFirstMrCount, firstMr.messageCount);
	}

	/**
	 * Check that the postbox used by MessageHandlerManager send out messages in the right order.
	 */
	@Test
	public void testPostboxMessageDistributionConsumption()
	{
		MessageHandlerManager cor = new MessageHandlerManager();
		assertNotNull("Postbox should be available", cor.getPostbox());
		
		MessageRecorder firstMr = new MessageRecorder();
		MessageRecorder secondMr = new MessageRecorder();
		cor.addMember(firstMr);
		cor.addMember(secondMr);

		// Check messages are delivered in order
		int prevFirstMrCount = firstMr.messageCount;
		int prevSecondMrCount = secondMr.messageCount;
		cor.getPostbox().handleMessage(
			new PCGenMessage(this));
		assertEquals("Single extra message expected by first handler",
			prevFirstMrCount + 1, firstMr.messageCount);
		assertEquals("Single extra message expected by second handler",
			prevSecondMrCount + 1, secondMr.messageCount);
		assertTrue(
			"First recorder should have received message before second (different source)",
			firstMr.lastMessageOrder < secondMr.lastMessageOrder);

		// Check source receives message last
		prevFirstMrCount = firstMr.messageCount;
		prevSecondMrCount = secondMr.messageCount;
		cor.getPostbox().handleMessage(
			new PCGenMessage(firstMr));
		assertEquals("Single extra message expected by first handler",
			prevFirstMrCount + 1, firstMr.messageCount);
		assertEquals("Single extra message expected by second handler",
			prevSecondMrCount + 1, secondMr.messageCount);
		assertTrue(
			"Second recorder should have received message before first (source)",
			firstMr.lastMessageOrder > secondMr.lastMessageOrder);
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
