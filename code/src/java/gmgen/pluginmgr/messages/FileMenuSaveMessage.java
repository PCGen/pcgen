/*
 * FileMenuSaveMessage.java
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
 * Created on 18/02/2014 8:22:48 am
 *
 * $Id$
 */
package gmgen.pluginmgr.messages;

import pcgen.pluginmgr.PCGenMessage;

/**
 * The Class <code>FileMenuSaveMessage</code> encapsulates an event 
 * that the GMGen file save menu item was selected. 
 *
 * <br>
 * Last Editor: $Author$
 * Last Edited: $Date$
 * 
 * @author James Dempsey &lt;jdempsey@users.sourceforge.net&gt;
 * @version $Revision$
 */
@SuppressWarnings("serial")
public class FileMenuSaveMessage extends PCGenMessage
{

	/**
	 * Create a new instance of FileMenuSaveMessage
	 * @param source The source of the message.
	 */
	public FileMenuSaveMessage(Object source)
	{
		super(source);
	}

}
