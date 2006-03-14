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
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import pcgen.core.Globals;
import pcgen.core.PObject;
import pcgen.core.SettingsHandler;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.spell.Spell;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.util.Logging;

/**
 *
 * @author  David Rice <david-pcgen@jcuz.com>
 * @version $Revision$
 */
public final class SpellLoader extends LstObjectFileLoader
{
	/** Creates a new instance of SpellLoader */
	public SpellLoader()
	{
		super();
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#parseLine(pcgen.core.PObject, java.lang.String, pcgen.persistence.lst.CampaignSourceEntry)
	 */
	public PObject parseLine(PObject target, String lstLine, CampaignSourceEntry source)
		throws PersistenceLayerException
	{
		Spell spell = (Spell) target;

		if (spell == null)
		{
			spell = new Spell();
		}

		int i = 0;
		final StringTokenizer colToken = new StringTokenizer(lstLine, SystemLoader.TAB_DELIM);
		
		Map tokenMap = TokenStore.inst().getTokenMap(SpellLstToken.class);

		while (colToken.hasMoreElements())
		{
			final String colString = colToken.nextToken().trim();
			final int idxColon = colString.indexOf(':');
			String key = "";
			try
			{
				key = colString.substring(0, idxColon);
			}
			catch(Exception e) {
				// TODO Handle Exception
			}
			SpellLstToken token = (SpellLstToken) tokenMap.get(key);

			// The very first one is the Name
			if (i == 0)
			{
				if ((!colString.equals(spell.getName())) && (colString.indexOf(".MOD") < 0))
				{
					finishObject(spell);
					spell = new Spell();
					spell.setName(colString);
					spell.setSourceCampaign(source.getCampaign());
					spell.setSourceFile(source.getFile());
				}

				i++;

				continue;
			}

			else if (token != null)
			{
				final String value = colString.substring(idxColon + 1).trim();
				LstUtils.deprecationCheck(token, spell, value);
				if (!token.parse(spell, value))
				{
					Logging.errorPrint("Error parsing spell " + spell.getName() + ':' + source.getFile() + ':' + colString + "\"");
				}
			}
			else if (PObjectLoader.parseTag(spell, colString))
			{
				continue;
			}
			else
			{
				Logging.errorPrint("Illegal spell info '" + colString + "' in " + source.getFile());
			}
		}

		return spell;
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#getObjectNamed(java.lang.String)
	 */
	protected PObject getObjectNamed(String baseName)
	{
		return Globals.getSpellNamed(baseName);
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#finishObject(pcgen.core.PObject)
	 */
	protected void finishObject(PObject target)
	{
		if (includeObject(target))
		{
			Object obj = Globals.getSpellMap().get(target.getName());
			if (obj == null)
			{
				Globals.getSpellMap().put(target.getName(), target);
			}
			else
			{
				ArrayList aList;
				if (obj instanceof ArrayList)
					aList = (ArrayList)obj;
				else
				{
					aList = new ArrayList();
					aList.add(obj);
				}
				boolean match = false;
				for (Iterator i = aList.iterator(); i.hasNext();)
				{
					Spell aSpell = (Spell)i.next();
					Object a = aSpell.getLevelInfo(null);
					Object b = ((Spell)target).getLevelInfo(null);
					if ((a==null && b==null) || (a!=null && a.equals(b)))
					{
						match = true;
					}
				}
				if (!match)
				{
					final Spell aSpell = Globals.getSpellKeyed(target.getKeyName());
					if (aSpell == null)
					{
						aList.add(target);
						Globals.getSpellMap().put(target.getName(), aList);
					}
					else if (!target.equals(aSpell))
					{
						if (SettingsHandler.isAllowOverride())
						{
							if (target.getSourceDateValue() > aSpell.getSourceDateValue())
							{
								Globals.getSpellMap().remove(aSpell.getKeyName());
								Globals.getSpellMap().put(target.getName(), target);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#performForget(pcgen.core.PObject)
	 */
	protected void performForget(PObject objToForget)
	{
		Globals.getSpellMap().remove(objToForget.getName());
	}

	/**
	 * @param spell
	 * @param typeString should be CLASS or DOMAIN
	 * @param listString should be name,name,name=level|name,name=level|etc
	 * where name is the name of a class or domain and
	 * level is an integer for this spell's level for the named class/domain
	 * @throws PersistenceLayerException
	 */
	public static void setLevelList(Spell spell, final String typeString, String listString) throws PersistenceLayerException
	{
		if (listString.equals(".CLEAR"))
		{
			spell.clearLevelInfo();

			return;
		}

		String preReqTag = null;
		final int i = listString.lastIndexOf('[');
		int j = listString.lastIndexOf(']');

		if (j < i)
		{
			j = listString.length();
		}

		if (i >= 0)
		{
			preReqTag = listString.substring(i + 1, j);
			listString = listString.substring(0, i);
		}

		final StringTokenizer aTok = new StringTokenizer(listString, "|", false);

		while (aTok.hasMoreTokens())
		{
			final String aList = aTok.nextToken(); // could be name=x or name,name=x

			final StringTokenizer bTok = new StringTokenizer(aList, "=", false);

			while (bTok.hasMoreTokens())
			{
				final String nameList = bTok.nextToken();

				if (!bTok.hasMoreTokens())
				{
					throw new PersistenceLayerException("Badly formed spell "+typeString+" data: " + listString);
				}

				final String aLevel = bTok.nextToken();
				final StringTokenizer cTok = new StringTokenizer(nameList, ",", false);

				while (cTok.hasMoreTokens())
				{
					final String aClass = cTok.nextToken();

					if (preReqTag != null)
					{
						PreParserFactory preFactory = PreParserFactory.getInstance();
						Prerequisite prerequisite = preFactory.parse( preReqTag );
						spell.addPreReqMapEntry(typeString + "|" + aClass, prerequisite);
					}

					spell.setLevelInfo(typeString + "|" + aClass, aLevel);
				}
			}
		}
	}
}
