/**
 * DescriptionFacade.java
 * Copyright James Dempsey, 2011
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
 * Created on 06/10/2011 7:53:48 PM
 *
 * $Id$
 */
package pcgen.core.facade;

import pcgen.core.facade.util.ListFacade;

/**
 * The Class <code>DescriptionFacade</code> tracks descriptive entries about the character, 
 * interfacing between the user interface and the core.
 *
 * <br/>
 * Last Editor: $Author$
 * Last Edited: $Date$
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */
public interface DescriptionFacade
{
	
	/**
	 * Remove a chronicle entry.
	 * @param chronicleEntry The entry to be removed.
	 */
	public void removeChronicleEntry(ChronicleEntryFacade chronicleEntry);
	
	/**
	 * Retrieve the set of the character's chronicle entries.
	 * @return The character's chronicle entries.
	 */
	public ListFacade<ChronicleEntryFacade> getChronicleEntries();

	/**
	 * Create a new Chronicle Entry and add it to the character's list.
	 * @return The new ChronicleEntry.
	 */
	public ChronicleEntryFacade createChronicleEntry();
	
}
