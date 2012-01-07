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
 * @author  David Rice <david-pcgen@jcuz.com>
 * @version $Revision$
 */
public final class SpellLoader extends LstObjectFileLoader<Spell>
{
	/** Creates a new instance of SpellLoader */
	public SpellLoader()
	{
		super();
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#parseLine(LoadContext, pcgen.core.PObject, java.lang.String, pcgen.persistence.lst.CampaignSourceEntry)
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
			spell.setName(colToken.nextToken());
			spell.put(ObjectKey.SOURCE_CAMPAIGN, source.getCampaign());
			spell.setSourceURI(source.getURI());
			if (isnew)
			{
				context.addStatefulInformation(spell);
				context.ref.importObject(spell);
			}
		}

		while (colToken.hasMoreElements())
		{
			final String token = colToken.nextToken().trim();
			final int colonLoc = token.indexOf(':');
			if (colonLoc == -1)
			{
				Logging.errorPrint("Invalid Token - does not contain a colon: '"
						+ token + "' in spell " + spell.getDisplayName() + " of " + source);
				continue;
			}
			else if (colonLoc == 0)
 			{
				Logging.errorPrint("Invalid Token - starts with a colon: "
						+ token);
				continue;
 			}

			String key = token.substring(0, colonLoc);
			String value = (colonLoc == token.length() - 1) ? null : token
					.substring(colonLoc + 1);
			if (context.processToken(spell, key, value))
			{
				context.commit();
			}
			else
			{
				context.rollback();
				Logging.replayParsedMessages();
 			}
			Logging.clearParseMessages();
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
	 * @see pcgen.persistence.lst.LstObjectFileLoader#finishObject(CDOMObject)
	 */
	@Override
	protected void finishObject(CDOMObject cdo)
	{
		// TODO - This code is broken now.  I think it always was though.
		//		Object obj = Globals.getSpellMap().get(target.getKeyName());
		//		if (obj == null)
		//		{
		//			Globals.addToSpellMap( target.getKeyName(), target );
		//		}
		//		else
		//		{
		//			ArrayList aList;
		//			if (obj instanceof ArrayList)
		//				aList = (ArrayList)obj;
		//			else
		//			{
		//				aList = new ArrayList();
		//				aList.add(obj);
		//			}
		//			boolean match = false;
		//			for (Iterator i = aList.iterator(); i.hasNext();)
		//			{
		//				Spell aSpell = (Spell)i.next();
		//				Object a = aSpell.getLevelInfo(null);
		//				Object b = ((Spell)target).getLevelInfo(null);
		//				if ((a==null && b==null) || (a!=null && a.equals(b)))
		//				{
		//					match = true;
		//				}
		//			}
		//			if (!match)
		//			{
		//				final Spell aSpell = Globals.getSpellKeyed(target.getKeyName());
		//				if (aSpell == null)
		//				{
		//					aList.add(target);
		//					Globals.addToSpellMap( target.getKeyName(), aList );
		//				}
		//				else if (!target.equals(aSpell))
		//				{
		//					if (SettingsHandler.isAllowOverride())
		//					{
		//						if (target.getSourceDateValue() > aSpell.getSourceDateValue())
		//						{
		//							Globals.getSpellMap().remove(aSpell.getKeyName());
		//							Globals.addToSpellMap( target.getKeyName(), target );
		//						}
		//					}
		//				}
		//			}
		//		}
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#performForget(LoadContext, pcgen.core.PObject)
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
		final Object obj = Globals.getSpellMap().get(cdo.getKeyName());
		if (obj == null)
		{
			Globals.addToSpellMap(cdo.getKeyName(), cdo);
		}
		else
		{
			final List<Spell> spellList;
			if (obj instanceof Spell)
			{
				spellList = new ArrayList<Spell>();
				Globals.removeFromSpellMap(((Spell) obj).getKeyName());
				Globals.addToSpellMap(cdo.getKeyName(), spellList);
				spellList.add((Spell) obj);
			}
			else
			{
				spellList = (List<Spell>) obj;
			}
			spellList.add((Spell) cdo);
		}
	}
}
