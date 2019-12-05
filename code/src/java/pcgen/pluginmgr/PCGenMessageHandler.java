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

import java.util.EventListener;

/**
 * The template {@code PCGenMessageHandler} defines an object
 * that can act on a PCGenMessage.
 */
@FunctionalInterface
public interface PCGenMessageHandler extends EventListener
{
    /**
     * Allow the implementor a chance to process a message. The message may be consumed,
     * in which case no further classes will be asked to handle the message.
     *
     * @param msg The message
     */
    void handleMessage(PCGenMessage msg);
}
