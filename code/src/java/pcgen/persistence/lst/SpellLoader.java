/*
 * SpellLoader.java
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

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Globals;
import pcgen.core.spell.Spell;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.rules.context.LoadContext;
import pcgen.util.Logging;

/**
 *
 * @author  David Rice &lt;david-pcgen@jcuz.com&gt;
 * @version $Revision$
 */
public final class SpellLoader extends LstObjectFileLoader<Spell>
{

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#parseLine(LoadContext, CDOMObject, String, SourceEntry)
	 */
	@Override
	public Spell parseLine(LoadContext context, Spell aSpell,
		String lstLine, SourceEntry source) throws PersistenceLayerException
	{
		Spell spell = aSpell;

		boolean isnew = false;
		if (spell == null)
		{
			spell = new Spell();
			isnew = true;
		}

		final StringTokenizer colToken =
				new StringTokenizer(lstLine, SystemLoader.TAB_DELIM);

		if (colToken.hasMoreTokens())
		{
			spell.setName(colToken.nextToken().intern());
			spell.put(ObjectKey.SOURCE_CAMPAIGN, source.getCampaign());
			spell.setSourceURI(source.getURI());
			if (isnew)
			{
				context.addStatefulInformation(spell);
				context.getReferenceContext().importObject(spell);
			}
		}

		while (colToken.hasMoreElements())
		{
			LstUtils.processToken(context, spell, source, colToken.nextToken());
		}

		completeObject(context, source, spell);
		return null;
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#getObjectKeyed(LoadContext, java.lang.String)
	 */
	@Override
	protected Spell getObjectKeyed(LoadContext context, final String aKey)
	{
		/*
		 * TODO Wowzers.  This means that the MasterList info needs to be "cloned" when this is .COPY'd
		 * 
		 * This is from Spell.java
		 */
//		if (levelInfo != null)
//		{
//			aSpell.levelInfo = new HashMap<String, Integer>(levelInfo);
//		}

		return Globals.getSpellKeyed(aKey);
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#performForget(LoadContext, CDOMObject)
	 */
	@Override
	protected void performForget(LoadContext context, final Spell objToForget)
	{
		super.performForget(context, objToForget);
		Globals.removeFromSpellMap(objToForget.getKeyName());
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#addGlobalObject(CDOMObject)
	 */
	@Override
	protected void addGlobalObject(final CDOMObject cdo)
	{
		if (cdo instanceof Spell)
		{
			final Spell spell = Globals.getSpellMap().get(cdo.getKeyName());
			if (spell == null)
			{
				Globals.addToSpellMap(cdo.getKeyName(), (Spell) cdo);
			}
			else
			{
				final List<Spell> spellList = new ArrayList<Spell>();
				Globals.removeFromSpellMap(spell.getKeyName());
				spellList.add((Spell) spell);
			}
		}
		else
		{
			Logging.errorPrint("Non-spell object passed: " + cdo);
		}
	}
}
