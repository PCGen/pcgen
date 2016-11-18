/*
 * CompanionNotLoaded.java
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
 * Created on 25/03/2012 11:07:05 AM
 *
 * $Id$
 */
package pcgen.gui2.facade;

import java.io.File;

import pcgen.facade.core.CompanionFacade;
import pcgen.facade.util.DefaultReferenceFacade;
import pcgen.facade.core.RaceFacade;
import pcgen.facade.util.ReferenceFacade;

/**
 * The Class {@code CompanionNotLoaded} represents a characters's companion
 * (familiar, animal companion, mount etc) that is not currently loaded.
 *
 * <br>
 * 
 * @author James Dempsey &lt;jdempsey@users.sourceforge.net&gt;
 */
public class CompanionNotLoaded implements CompanionFacade
{

	private final DefaultReferenceFacade<String> nameRef;
	private final DefaultReferenceFacade<File> fileRef;
	private final DefaultReferenceFacade<RaceFacade> raceRef;
	private final String companionType;

	/**
	 * Create a new instance of CompanionNotLoaded
	 * @param name the name of the companion.
	 * @param file The character file for the companion.
	 * @param race The race of the companion.
	 * @param compType The type of companion.
	 */
	public CompanionNotLoaded(String name, File file, RaceFacade race, String compType)
	{
		this.nameRef = new DefaultReferenceFacade<>(name);
		this.fileRef = new DefaultReferenceFacade<>(file);
		this.raceRef = new DefaultReferenceFacade<>(race);
		this.companionType = compType;
	}
	
	@Override
	public ReferenceFacade<String> getNameRef()
	{
		return nameRef;
	}

	@Override
	public ReferenceFacade<File> getFileRef()
	{
		return fileRef;
	}

	@Override
	public ReferenceFacade<RaceFacade> getRaceRef()
	{
		return raceRef;
	}

	@Override
	public String getCompanionType()
	{
		return companionType;
	}

}
