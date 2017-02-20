/*
 * CompanionModLoader.java
 * Copyright 2012 (C) Tom Parker
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
package pcgen.persistence.lst;

import java.net.URI;

import pcgen.core.character.CompanionMod;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;

/**
 * Loads the level based Mount and Familiar benefits
 *
 **/
public class CompanionModLoader extends SimpleLoader<CompanionMod> 
{

	private static int COMPANION_MOD_ID = 1;

	public CompanionModLoader()
	{
		super(CompanionMod.class);
	}

	@Override
	protected CompanionMod getLoadable(LoadContext context, String firstToken,
		URI sourceURI) throws PersistenceLayerException
	{
		String name = processFirstToken(context, firstToken);
		if (name == null)
		{
			return null;
		}
		//Always create a new CompanionMod (no Copy Mod or Forget)
		//But we need to create a unique name (and do it with something that is unique-ish)
		//Note there is currently no risk of name conflict here since they cannot be uniquely named
		String uniqueName = "COMPANIONMOD_" + COMPANION_MOD_ID++;
		CompanionMod mod = super.getLoadable(context, uniqueName, sourceURI);
		//Process the first token since it's not really a name...
		LstUtils.processToken(context, mod, sourceURI, firstToken);
		return mod;
	}

}
