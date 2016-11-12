/**
 * TodoFacade.java
 * Copyright 2010 James Dempsey
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
 * Created on 14/06/2010 5:12:24 PM
 *
 * $Id: TodoFacade.java 12156 2010-06-14 10:03:19Z jdempsey $
 */
package pcgen.facade.core;

import pcgen.util.enumeration.Tab;

/**
 * The interface {@code TodoFacade} defines what methods must be provided
 * to support a Todo entry for a character. 
 *
 * <br>
 * 
 * @author James Dempsey &lt;jdempsey@users.sourceforge.net&gt;
 */
public interface TodoFacade extends Comparable<TodoFacade>
{
	/** Event constant to indicate a request to change tabs. */
	public static String SWITCH_TABS = "SwitchTabs";
	
	/**
	 * @return The message to be displayed. Is normally a key to localised 
	 * message, starting with in_ but may also be plain text.   
	 */
	public String getMessageKey();
	
	/**
	 * @return The character tab on which the task can be completed.
	 */
	public Tab getTab();
	
	/**
	 * @return The internal name of the field where the task can be completed.
	 */
	public String getFieldName();
	
	/**
	 * @return The internal name of the sub tab where the task can be completed.
	 */
	public String getSubTabName();
}
