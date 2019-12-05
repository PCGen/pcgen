/*
 * Copyright 2004 (C) Frugal <frugal@purplewombat.co.uk>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.       See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.core.utils;

/**
 * A Wrapper for messages in the PCGen System
 */
public class MessageWrapper
{

    private final Object message;
    private final String title;
    private final MessageType messageType;
    private final Object parent;

    /**
     * Constructor
     *
     * @param message
     * @param title
     * @param messageType
     * @param parent
     */
    public MessageWrapper(final Object message, final String title, final MessageType messageType, final Object parent)
    {
        this.message = message;
        this.title = title;
        this.messageType = messageType;
        this.parent = parent;
    }

    /**
     * Constructor
     *
     * @param message
     * @param title
     * @param messageType
     */
    public MessageWrapper(final Object message, final String title, final MessageType messageType)
    {
        this.message = message;
        this.title = title;
        this.messageType = messageType;
        this.parent = null;
    }

    /**
     * @return Returns the message.
     */
    public Object getMessage()
    {
        return message;
    }

    /**
     * @return Returns the messageType.
     */
    public MessageType getMessageType()
    {
        return messageType;
    }

    /**
     * @return Returns the title.
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * @return Returns the parent.
     */
    public Object getParent()
    {
        return parent;
    }

}
