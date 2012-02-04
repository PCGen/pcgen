/**
 * DescriptionFacadeImpl.java
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
 * Created on 06/10/2011 7:59:35 PM
 *
 * $Id$
 */
package pcgen.gui2.facade;

import pcgen.core.ChronicleEntry;
import pcgen.core.PlayerCharacter;
import pcgen.core.facade.ChronicleEntryFacade;
import pcgen.core.facade.DescriptionFacade;
import pcgen.core.facade.util.DefaultListFacade;
import pcgen.core.facade.util.ListFacade;

/**
 * The Class <code>DescriptionFacadeImpl</code> is an implementation of 
 * the DescriptionFacade interface for the new user interface. It is 
 * intended to provide a full implementation of the new ui/core 
 * interaction layer.
 *
 * <br/>
 * Last Editor: $Author$
 * Last Edited: $Date$
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */
public class DescriptionFacadeImpl implements DescriptionFacade
{
	private PlayerCharacter theCharacter;
	private DefaultListFacade<ChronicleEntryFacade> chronicleEntries;

	/**
	 * @param pc
	 */
	public DescriptionFacadeImpl(PlayerCharacter pc)
	{
		theCharacter = pc;
		chronicleEntries = new DefaultListFacade<ChronicleEntryFacade>();
		for (ChronicleEntryFacade entry : theCharacter.getChronicleEntries())
		{
			chronicleEntries.addElement(entry);
		}
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.DescriptionFacade#createChronicleEntry()
	 */
	public ChronicleEntryFacade createChronicleEntry()
	{
		ChronicleEntry chronicleEntry = new ChronicleEntry();
		theCharacter.addChronicleEntry(chronicleEntry);
		chronicleEntries.addElement(chronicleEntry);
		return chronicleEntry;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.DescriptionFacade#removeChronicleEntry(pcgen.core.ChronicleEntry)
	 */
	public void removeChronicleEntry(ChronicleEntryFacade chronicleEntry)
	{
		if (chronicleEntry instanceof ChronicleEntry)
		{
			theCharacter.removeChronicleEntry((ChronicleEntry) chronicleEntry);
		}
		chronicleEntries.removeElement(chronicleEntry);
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.DescriptionFacade#getChronicleEntries()
	 */
	public ListFacade<ChronicleEntryFacade> getChronicleEntries()
	{
		return chronicleEntries;
	}

}
