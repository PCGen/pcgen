/*
 * CompanionStub.java
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
 * Created on 21/03/2012 9:52:14 PM
 *
 * $Id$
 */
package pcgen.gui2.facade;

import pcgen.core.facade.CompanionStubFacade;
import pcgen.core.facade.DefaultReferenceFacade;
import pcgen.core.facade.RaceFacade;
import pcgen.core.facade.ReferenceFacade;

/**
 * The Class <code>CompanionStub</code> contains a definition of a possible 
 * companion (i.e. animal companion, familiar, follower etc) for a character.  
 *
 * <br/>
 * Last Editor: $Author$
 * Last Edited: $Date$
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */

public class CompanionStub implements CompanionStubFacade
{

	private final DefaultReferenceFacade<RaceFacade> race;
	private final String companionType;

	/**
	 * Create a new instance of CompanionStub
	 * @param race The race of the possible companion.
	 * @param companionType The type of companion.
	 */
	CompanionStub(RaceFacade race, String companionType)
	{
		this.race = new DefaultReferenceFacade<RaceFacade>(race);
		this.companionType = companionType;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ReferenceFacade<RaceFacade> getRaceRef()
	{
		return race;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getCompanionType()
	{
		return companionType;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString()
	{
		return race.toString();
	}

}
