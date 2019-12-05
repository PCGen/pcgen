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
 *
 *
 */
package pcgen.pluginmgr;

import javax.swing.event.EventListenerList;

import pcgen.pluginmgr.messages.ComponentAddedMessage;
import pcgen.pluginmgr.messages.ComponentRemovedMessage;

/**
 * The Class {@code MessageHandlerManager} records the list of message handlers
 * and ensures that they get advised of any messages in order.
 */

public class MessageHandlerManager
{
    private final PCGenMessageHandler postbox;
    private final EventListenerList chain;

    public MessageHandlerManager()
    {
        chain = new EventListenerList();
        postbox = new PCGenMessagePostbox();
    }

    public void addMember(PCGenMessageHandler plugin)
    {
        // Add the plugin to the chain of responsibility.
        chain.add(PCGenMessageHandler.class, plugin);
        postbox.handleMessage(new ComponentAddedMessage(this, plugin));
    }

    public void removeMember(PCGenMessageHandler plugin)
    {
        postbox.handleMessage(new ComponentRemovedMessage(this, plugin));
        // Remove the plugin from the chain of responsibility.
        chain.remove(PCGenMessageHandler.class, plugin);
    }

    /**
     * @return the postbox to be used to despatch messages
     */
    public PCGenMessageHandler getPostbox()
    {
        return postbox;
    }

    /* ------------------------------------------------------------- */

    /**
     * The Class {@code PCGenMessagePostbox} distributes PCGenMessages
     * to all handlers registered in the parent ChainOfResponsibility. The handlers
     * are advised in order (with the source of the message advised last) however
     * messages may be consumed in which case no further handlers are advised of
     * the message.
     */
    private final class PCGenMessagePostbox implements PCGenMessageHandler
    {

        @Override
        public void handleMessage(PCGenMessage msg)
        {
            // Guaranteed to return a non-null array
            Object[] listeners = chain.getListenerList();
            boolean sourceListening = false;
            // Process the listeners first to last, notifying
            // those that are interested in this event
            for (int i = 0;i < listeners.length - 1;i += 2)
            {
                if (listeners[i] == PCGenMessageHandler.class)
                {
                    PCGenMessageHandler handler = (PCGenMessageHandler) listeners[i + 1];
                    if (handler == msg.getSource())
                    {
                        sourceListening = true;
                    } else
                    {
                        handler.handleMessage(msg);
                        if (msg.isConsumed())
                        {
                            // Consumed, so we don't send the message to any more handlers
                            break;
                        }
                    }
                }
            }

            // The source gets advised of the message last, but only if it is listening
            if (!msg.isConsumed() && sourceListening)
            {
                ((PCGenMessageHandler) msg.getSource()).handleMessage(msg);
            }
        }

    }
}
