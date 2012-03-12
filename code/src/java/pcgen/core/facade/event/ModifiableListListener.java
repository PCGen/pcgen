/*
 * ModifiableListListener.java
 * Copyright James Dempsey, 2012
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
 * Created on 12/03/2012 4:52:47 PM
 *
 * $Id$
 */
package pcgen.core.facade.event;

/**
 * ModifiableListListener is a ListListener that can process notifications 
 * that a list entry has been changed in some significant way.
 *
 * <br/>
 * Last Editor: $Author$
 * Last Edited: $Date$
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */
public interface ModifiableListListener<E> extends ListListener<E>
{

	/**
	 * Signals that an element in the list was changed in some way and needs 
	 * to be refreshed.
	 * @param e The event being advised.
	 */
	void elementModified(ListEvent<E> e);

}
