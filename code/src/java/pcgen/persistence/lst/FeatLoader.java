/*
 * FeatLoader.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 * Created on February 22, 2002, 10:29 PM
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package pcgen.persistence.lst;

import pcgen.core.Ability;
import pcgen.core.Constants;
import pcgen.core.Globals;
import pcgen.core.PObject;
import pcgen.persistence.PersistenceLayerException;
import pcgen.util.Logging;

/**
 *
 * @author  David Rice <david-pcgen@jcuz.com>
 * @version $Revision$
 */
public final class FeatLoader extends AbilityLoader
{
	private boolean defaultFeatsLoaded = false;

	/** Creates a new instance of FeatLoader */
	public FeatLoader()
	{
		super();
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#parseLine(pcgen.core.PObject, java.lang.String, pcgen.persistence.lst.CampaignSourceEntry)
	 */
	@Override
	public PObject parseLine(PObject target, String lstLine, CampaignSourceEntry source)
		throws PersistenceLayerException
	{
		Ability feat = (Ability) target;

		if (feat == null)
		{
			feat = new Ability();
		}

		feat.setCategory(Constants.FEAT_CATEGORY);

		return super.parseLine(feat, lstLine, source);
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#loadLstFile(pcgen.persistence.lst.CampaignSourceEntry)
	 */
	protected void loadLstFile(CampaignSourceEntry sourceEntry)
	{
		super.loadLstFile(sourceEntry);

		if (!defaultFeatsLoaded)
		{
			loadDefaultFeats(sourceEntry);
		}
	}

	/**
	 * This method loads the default feats with the first feat source.
	 * @param firstSource CampaignSourceEntry first loaded by this loader
	 */
	private void loadDefaultFeats(CampaignSourceEntry firstSource)
	{
		if (Globals.getAbilityKeyed("FEAT", Constants.s_INTERNAL_WEAPON_PROF) == null)
		{

			/* Add catch-all feat for weapon proficiencies that cannot be granted as part
			 * of a Feat eg. Simple weapons should normally be applied to the Simple
			 * Weapon Proficiency feat, but it does not allow multiples (either all or
			 * nothing).  So monk class weapons will get dumped into this bucket.  */

			String aLine = Constants.s_INTERNAL_WEAPON_PROF +
				"\tOUTPUTNAME:Weapon Proficiency\tTYPE:General\tCATEGORY:FEAT" +
				"\tVISIBLE:NO\tMULT:YES\tSTACK:YES\tDESC:You attack with this" +
				" specific weapon normally, non-proficiency incurs a -4 to"    +
					" hit penalty.\tSOURCELONG:PCGen Internal";
			try
			{
				parseLine(null, aLine, firstSource);
			}
			catch (PersistenceLayerException ple)
			{
				Logging.errorPrint(
					"Unable to parse the internal default feats '" +
					aLine + "': " + ple.getMessage());
			}
			defaultFeatsLoaded = true;
		}
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#getObjectKeyed(java.lang.String)
	 */
	@Override
	protected PObject getObjectKeyed(final String aKey)
	{
		return Globals.getAbilityKeyed(Constants.FEAT_CATEGORY, aKey);
	}
}
