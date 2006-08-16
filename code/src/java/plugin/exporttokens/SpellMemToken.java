/*
 * SpellMemToken.java
 * Copyright 2004 (C) James Dempsey <jdempsey@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.     See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on Jul 16, 2004
 *
 * $Id$
 *
 */
package plugin.exporttokens;

import pcgen.core.*;
import pcgen.core.character.CharacterSpell;
import pcgen.core.character.SpellInfo;
import pcgen.core.spell.Spell;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.Token;

import java.util.*;

/**
 * <code>SpellMemToken</code> displays information about the spells
 * in the character spellbooks..
 *
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */

// SPELLMEM.x.x.x.x.LABEL classNum.bookNum.level.spellnumber
// LABEL is TIMES,NAME,RANGE,etc. if not supplied it defaults to NAME
public class SpellMemToken extends Token
{
	/** token name */
	public static final String TOKENNAME = "SPELLMEM";

	/**
	 * @see pcgen.io.exporttoken.Token#getTokenName()
	 */
	public String getTokenName()
	{
		return TOKENNAME;
	}

	/**
	 * @see pcgen.io.exporttoken.Token#getToken(java.lang.String, pcgen.core.PlayerCharacter, pcgen.io.ExportHandler)
	 */
	public String getToken(String tokenSource, PlayerCharacter aPC, ExportHandler eh)
	{
		StringBuffer retValue = new StringBuffer();

		// New Token syntax - SPELLMEM.x instead of SPELLMEMx
		final StringTokenizer aTok = new StringTokenizer(tokenSource, ".");

		aTok.nextToken();
		final int classNum;

		classNum = Integer.parseInt(aTok.nextToken());

		final int bookNum = Integer.parseInt(aTok.nextToken());
		final int spellLevel = Integer.parseInt(aTok.nextToken());
		final int spellNumber = Integer.parseInt(aTok.nextToken());
		boolean found = false;
		String aLabel = "NAME";

		if (aTok.hasMoreTokens())
		{
			aLabel = aTok.nextToken();
		}

		String altLabel = "";

		if (aTok.hasMoreTokens())
		{
			altLabel = aTok.nextToken();
		}

		final PObject aObject = aPC.getSpellClassAtIndex(classNum);

		if ((aObject == null) && eh != null && eh.getExistsOnly()
			&& (classNum != -1))
		{
			eh.setNoMoreItems(true);
		}

		String bookName = Globals.getDefaultSpellBook();

		if (bookNum > 0)
		{
			bookName = aPC.getSpellBooks().get(bookNum);
		}

		if ((aObject != null) || (classNum == -1))
		{
			if (classNum == -1)
			{
				bookName = Globals.getDefaultSpellBook();
			}

			CharacterSpell selectedCSpell = null;

			if (!"".equals(bookName))
			{
				Spell aSpell = null;

				if (classNum == -1)
				{
					// List of all the character's spells (including SLAs)
					final List<CharacterSpell> charSpellList = new ArrayList<CharacterSpell>();

					// For each class
					for ( PCClass pcClass : aPC.getClassList() )
					{
						// Get the spells provided by the class
						List<CharacterSpell> aList = pcClass.getSpellSupport().getCharacterSpell(null, bookName, spellLevel);

						for ( CharacterSpell cs : aList )
						{
							// Add to the list if they are not there already
							if (!charSpellList.contains(cs))
							{
								charSpellList.add(cs);
							}
						}
					}

					// Sort the list
					Collections.sort(charSpellList);

					// Set cs to the spell asked for
					if (spellNumber < charSpellList.size())
					{
						selectedCSpell = charSpellList.get(spellNumber);
						aSpell = selectedCSpell.getSpell();
						found = true;
					}
				}
				else if (aObject != null)
				{
					// List of spells provided by this PObject
					final List<CharacterSpell> charSpells = aObject.getSpellSupport().getCharacterSpell(null, bookName, spellLevel);

					if (spellNumber < charSpells.size())
					{
						selectedCSpell = charSpells.get(spellNumber);
						aSpell = selectedCSpell.getSpell();
						found = true;
					}
				}
				else if (eh != null && eh.getInLabel() && eh.getCheckBefore())
				{
					eh.setCanWrite(false);
				}

				// We never found the requested spell
				if (selectedCSpell == null)
				{
					if (eh != null && eh.getExistsOnly())
					{
						eh.setNoMoreItems(true);
					}

					return retValue.toString();
				}

				// Get the SpellInfo for the selected spell
				final SpellInfo si = selectedCSpell.getSpellInfoFor(bookName, spellLevel, -1);

				if (found && (aSpell != null) && (si != null))
				{
					if ("NAME".equals(aLabel))
					{
						retValue.append(aSpell.getOutputName() + si.toString());
					}
					else if ("OUTPUTNAME".equals(aLabel))
					{
						retValue.append(aSpell.getOutputName() + si.toString());
					}
					else if ("PPCOST".equals(aLabel))
					{
						if (si.getActualPPCost() != -1)
						{
							retValue.append(si.getActualPPCost());
						}
					}
					else if ("TIMES".equals(aLabel))
					{
						if (si.getTimes() == -1)
						{
							retValue.append("At Will");
						}
						else
						{
							retValue.append(String.valueOf(si.getTimes()));
						}
					}
					else if (aSpell != null)
					{
						if ("RANGE".equals(aLabel))
						{
							retValue.append(aPC.getSpellRange(aSpell, selectedCSpell.getOwner().getKeyName(), si));
						}
						else if ("BASEPPCOST".equals(aLabel))
						{
							retValue.append(aSpell.getPPCost());
						}
						else if ("CASTERLEVEL".equals(aLabel))
						{
							retValue.append(aPC.getCasterLevelForSpell(aSpell, selectedCSpell.getOwner().getKeyName()));
						}
						else if ("CASTINGTIME".equals(aLabel))
						{
							retValue.append(aSpell.getCastingTime());
						}
						else if ("COMPONENTS".equals(aLabel))
						{
							retValue.append(aSpell.getComponentList());
						}
						else if ("COST".equals(aLabel))
						{
							retValue.append(aSpell.getCost().toString());
						}
						else if ("DC".equals(aLabel))
						{
							int dc = aSpell.getDCForPlayerCharacter(aPC, si);
							retValue.append(String.valueOf(dc));
						}
						else if ("DURATION".equals(aLabel))
						{
							String mString = aPC.parseSpellString(aSpell, aSpell.getDuration(), selectedCSpell.getOwner());
							retValue.append(mString);
						}
						else if ("DESC".equals(aLabel) || "EFFECT".equals(aLabel))
						{
							String mString = aPC.parseSpellString(aSpell, aSpell.getDescription(), selectedCSpell.getOwner());
							retValue.append(mString);
						}
						else if ("TARGET".equals(aLabel) || "EFFECTYPE".equals(aLabel))
						{
							String mString = aPC.parseSpellString(aSpell, aSpell.getTarget(), selectedCSpell.getOwner());
							retValue.append(mString);
						}
						else if ("SAVEINFO".equals(aLabel))
						{
							retValue.append(aSpell.getSaveInfo());
						}
						else if ("SCHOOL".equals(aLabel))
						{
							retValue.append(aSpell.getSchool());
						}
						else if ("SOURCELEVEL".equals(aLabel))
						{
							retValue.append(replaceTokenSpellMemSourceLevel(aSpell, aPC));
						}
						else if ("SOURCE".equals(aLabel))
						{
							retValue.append(aSpell.getDefaultSourceString());
						}
						else if ("SOURCESHORT".equals(aLabel))
						{
							final SourceEntry se = aSpell.getSourceEntry();
							retValue.append(se.getSourceBook().getShortName(8));
						}
						else if ("SOURCEPAGE".equals(aLabel))
						{
							final SourceEntry se = aSpell.getSourceEntry();
							retValue.append(se.getPageNumber());
						}
						else if ("SUBSCHOOL".equals(aLabel))
						{
							retValue.append(aSpell.getSubschool());
						}
						else if ("DESCRIPTOR".equals(aLabel))
						{
							retValue.append(aSpell.descriptor());
						}
						else if ("FULLSCHOOL".equals(aLabel))
						{
							String aTemp = aSpell.getSchool();

							if ((aSpell.getSubschool().length() > 0)
								&& (!"NONE".equals(aSpell.getSubschool().trim().toUpperCase())))
							{
								aTemp += (" (" + aSpell.getSubschool() + ')');
							}

							if (aSpell.descriptor().length() > 0)
							{
								aTemp += (" [" + aSpell.descriptor() + ']');
							}

							retValue.append(aTemp);
						}
						else if ("SR".equals(aLabel))
						{
							retValue.append(aSpell.getSpellResistance());
						}
						else if ("CLASS".equals(aLabel))
						{
							retValue.append(aObject.getOutputName());
						}
						else if ("DCSTAT".equals(aLabel))
						{
							if (aObject instanceof PCClass)
							{
								PCClass aClass = (PCClass) aObject;
								retValue.append(aClass.getSpellBaseStat());
							}
						}
						else if ("TYPE".equals(aLabel))
						{
							PCClass aClass = null;

							if (aObject instanceof PCClass)
							{
								aClass = (PCClass) aObject;
							}

							if (aClass != null)
							{
								retValue.append(aClass.getSpellType());
							}
						}
						else if (aLabel.startsWith("DESCRIPTION"))
						{
							final String sString = ExportHandler
								.getItemDescription("SPELL", aSpell.getKeyName(),
									aSpell.getDescription(), aPC);

							if (altLabel.length() > 0)
							{
								retValue.append(Token
									.replaceWithDelimiter(sString, altLabel));
							}
							else
							{
								retValue.append(sString);
							}
						}
						else if (aLabel.startsWith("BONUSSPELL"))
						{
							String sString = "*";

							if (aLabel.length() > 10)
							{
								sString = aLabel.substring(10);
							}

							retValue
								.append(getBonusSpellValue(aPC, spellLevel,
									sString, altLabel, aObject, bookName,
									selectedCSpell,	aSpell));
						}
						else if ("XPCOST".equals(aLabel))
						{
							retValue.append(aSpell.getXPCost());
						}
					}
				}
				else if (eh != null && eh.getExistsOnly())
				{
					eh.setNoMoreItems(true);
				}
			}
			else if (eh != null && eh.getExistsOnly())
			{
				eh.setNoMoreItems(true);
			}
		}

		return retValue.toString();
	}


	/**
	 * Display an * is the spell is a domain/specialty spell,
	 * display ** if it is ONLY a domain/specialty spell. A value
	 * may also be supplied that will be displayed for non-specialty spells.
	 *
	 * @param aPC The character being processed.
	 * @param spellLevel The level of the spell.
	 * @param sString The indicator to use for domain/specialty spells.
	 * @param altLabel The indicator to use for non domain/specialty spells.
	 * @param aObject The class containing the spell.
	 * @param bookName The name of the spell book.
	 * @param cs The spell as it applies to the character
	 * @param aSpell The generic spell.
	 * @return The annotation string indicating domain/specialty status
	 */
	private String getBonusSpellValue(PlayerCharacter aPC, final int spellLevel, String sString, String altLabel, final PObject aObject, String bookName, CharacterSpell cs, Spell aSpell)
	{
		final List<String> dList = new ArrayList<String>();
		StringBuffer retValue = new StringBuffer();

		if ((aObject != null) && (cs != null) && cs.isSpecialtySpell()
			&& (aObject instanceof PCClass))
		{
			for ( CharacterDomain cd : aPC.getCharacterDomainList() )
			{
				if ((cd != null) && (cd.getDomain() != null)
					&& cd.isFromPCClass(aObject.getKeyName()))
				{
					dList.add(cd.getDomain().getKeyName());
				}
			}

			final List<CharacterSpell> charSpells = aObject.getSpellSupport().getCharacterSpell(aSpell, bookName, spellLevel);
			boolean isDomainOnly = true;

			for ( CharacterSpell cSpell : charSpells )
			{
				if (!cSpell.isSpecialtySpell())
				{
					isDomainOnly = false;

					break;
				}
			}

			if (isDomainOnly)
			{
				retValue.append(sString);
			}
			else
			{
				retValue.append(sString + sString);
			}
		}
		else
		{
			retValue.append(altLabel);
		}

		return retValue.toString();
	}

	private static String replaceTokenSpellMemSourceLevel(Spell aSpell, PlayerCharacter aPC)
	{
		final Map<String, Integer> tempHash = aSpell.getLevelInfo(aPC);
		StringBuffer tempSource = new StringBuffer();
		final Set<String> levelSet = new TreeSet<String>();

		for ( String classKey : tempHash.keySet() )
		{
			Integer classLevel = tempHash.get(classKey);

			if (classKey.startsWith("CLASS|"))
			{
				classKey = classKey.substring(6);
				if (! "ALL".equals(classKey))
				{
					if (Globals.getClassKeyed(classKey) != null)
					{
						classKey = Globals.getClassKeyed(classKey).getAbbrev();
					}
					else
					{
						classKey = null;
					}
				}
			}
			else
			{
				classKey = classKey.substring(7);
			}

			if (classKey != null)
			{
				levelSet.add(classKey + classLevel.toString());
			}
		}

		for ( String levelString : levelSet )
		{
			if (tempSource.length() > 0)
			{
				tempSource.append(", ");
			}

			tempSource.append(levelString);
		}

		return tempSource.toString();
	}

}
