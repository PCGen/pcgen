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
 */
package plugin.exporttokens;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.base.util.HashMapToList;
import pcgen.cdom.base.CDOMList;
import pcgen.cdom.enumeration.FactKey;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.SourceFormat;
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.Ability;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.core.analysis.OutputNameFormatting;
import pcgen.core.character.CharacterSpell;
import pcgen.core.character.SpellInfo;
import pcgen.core.spell.Spell;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.Token;
import pcgen.util.Delta;

/**
 * {@code SpellMemToken} displays information about the spells
 * in the character spellbooks..
 */

// SPELLMEM.x.x.x.x.LABEL classNum.bookNum.level.spellnumber
// LABEL is TIMES,NAME,RANGE,etc. if not supplied it defaults to NAME
public class SpellMemToken extends Token
{
    /**
     * token name
     */
    public static final String TOKENNAME = "SPELLMEM";

    @Override
    public String getTokenName()
    {
        return TOKENNAME;
    }

    @Override
    public String getToken(String tokenSource, PlayerCharacter aPC, ExportHandler eh)
    {
        StringBuilder retValue = new StringBuilder();

        // New Token syntax - SPELLMEM.x instead of SPELLMEMx
        final StringTokenizer aTok = new StringTokenizer(tokenSource, ".");

        aTok.nextToken();
        final int classNum;

        classNum = Integer.parseInt(aTok.nextToken());

        final int bookNum = Integer.parseInt(aTok.nextToken());
        final int spellLevel = Integer.parseInt(aTok.nextToken());
        final int spellNumber = Integer.parseInt(aTok.nextToken());
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

        if ((aObject == null) && (eh != null) && eh.getExistsOnly() && (classNum != -1))
        {
            eh.setNoMoreItems(true);
        }

        String bookName = Globals.getDefaultSpellBook();

        if (bookNum > 0)
        {
            bookName = aPC.getDisplay().getSpellBookNames().get(bookNum);
        }

        if ((aObject != null) || (classNum == -1))
        {
            if (classNum == -1)
            {
                bookName = Globals.getDefaultSpellBook();
            }

            if ((bookName != null) && !bookName.isEmpty())
            {
                Spell aSpell = null;

                CharacterSpell selectedCSpell = null;
                if (classNum == -1)
                {
                    // List of all the character's spells (including SLAs)
                    final List<CharacterSpell> charSpellList = new ArrayList<>();

                    // For each class
                    for (PCClass pcClass : aPC.getDisplay().getClassSet())
                    {
                        // Get the spells provided by the class
                        List<CharacterSpell> aList = aPC.getCharacterSpells(pcClass, null, bookName, spellLevel);

                        // Add to the list if they are not there already
                        aList.stream()
                                .filter(cs -> !charSpellList.contains(cs))
                                .forEach(charSpellList::add);
                    }

                    // Sort the list
                    Collections.sort(charSpellList);

                    // Set cs to the spell asked for
                    if (spellNumber < charSpellList.size())
                    {
                        selectedCSpell = charSpellList.get(spellNumber);
                        aSpell = selectedCSpell.getSpell();
                    }
                } else if (aObject != null)
                {
                    // List of spells provided by this PObject
                    final List<CharacterSpell> charSpells = aPC.getCharacterSpells(aObject, null, bookName, spellLevel);

                    if (spellNumber < charSpells.size())
                    {
                        selectedCSpell = charSpells.get(spellNumber);
                        aSpell = selectedCSpell.getSpell();
                    }
                }

                // We never found the requested spell
                if (selectedCSpell == null)
                {
                    if ((eh != null) && eh.getExistsOnly())
                    {
                        eh.setNoMoreItems(true);
                    }

                    return retValue.toString();
                }

                // Get the SpellInfo for the selected spell
                final SpellInfo si = selectedCSpell.getSpellInfoFor(bookName, spellLevel);

                if ((aSpell != null) && (si != null))
                {
                    if ("NAME".equals(aLabel) || "OUTPUTNAME".equals(aLabel))
                    {
                        retValue.append(OutputNameFormatting.getOutputName(aSpell)).append(si);
                    } else if ("BASENAME".equals(aLabel))
                    {
                        retValue.append(OutputNameFormatting.getOutputName(aSpell));
                    } else if ("APPLIEDNAME".equals(aLabel))
                    {
                        retValue.append(getAppliedName(si));
                    } else if ("PPCOST".equals(aLabel))
                    {
                        if (si.getActualPPCost() != -1)
                        {
                            retValue.append(si.getActualPPCost());
                        }
                    } else if ("TIMES".equals(aLabel))
                    {
                        if (si.getTimes() == SpellInfo.TIMES_AT_WILL)
                        {
                            retValue.append("At Will");
                        } else
                        {
                            retValue.append(si.getTimes());
                        }
                    } else if ("TIMEUNIT".equals(aLabel))
                    {
                        retValue.append(si.getTimeUnit());
                    } else
                    // if (aSpell != null) can't be null
                    {
                        if ("RANGE".equals(aLabel))
                        {
                            retValue.append(aPC.getSpellRange(selectedCSpell, si));
                        } else if ("CASTERLEVEL".equals(aLabel))
                        {
                            retValue.append(aPC.getCasterLevelForSpell(selectedCSpell));
                        } else if ("CASTINGTIME".equals(aLabel))
                        {
                            retValue.append(aSpell.getListAsString(ListKey.CASTTIME));
                        } else if ("COMPONENTS".equals(aLabel))
                        {
                            retValue.append(aSpell.getListAsString(ListKey.COMPONENTS));
                        } else if ("CONCENTRATION".equals(aLabel))
                        {
                            if (!SettingsHandler.getGame().getSpellBaseConcentration().isEmpty())
                            {
                                int concentration = aPC.getConcentration(aSpell, selectedCSpell, si);
                                retValue.append(Delta.toString(concentration));
                            }
                        } else if ("COST".equals(aLabel))
                        {
                            retValue.append(aSpell.getSafe(ObjectKey.COST));
                        } else if ("DC".equals(aLabel))
                        {
                            String SaveInfo = aSpell.getListAsString(ListKey.SAVE_INFO);
                            if (!"".equals(SaveInfo) && !"None".equals(SaveInfo) && !"No".equals(SaveInfo))
                            {
                                int dc = aPC.getDC(aSpell, selectedCSpell, si);
                                retValue.append(dc);
                            }
                        } else if ("DURATION".equals(aLabel))
                        {
                            String mString =
                                    aPC.parseSpellString(selectedCSpell, aSpell.getListAsString(ListKey.DURATION));
                            retValue.append(mString);
                        } else if ("DESC".equals(aLabel) || "EFFECT".equals(aLabel))
                        {
                            String mString = aPC.parseSpellString(selectedCSpell, aPC.getDescription(aSpell));
                            retValue.append(mString);
                        } else if ("TARGET".equals(aLabel) || "EFFECTYPE".equals(aLabel))
                        {
                            String mString =
                                    aPC.parseSpellString(selectedCSpell, aSpell.getSafe(StringKey.TARGET_AREA));
                            retValue.append(mString);
                        } else if ("SAVEINFO".equals(aLabel))
                        {
                            retValue.append(aSpell.getListAsString(ListKey.SAVE_INFO));
                        } else if ("SCHOOL".equals(aLabel))
                        {
                            retValue.append(aSpell.getListAsString(ListKey.SPELL_SCHOOL));
                        } else if ("SOURCELEVEL".equals(aLabel))
                        {
                            retValue.append(replaceTokenSpellMemSourceLevel(aSpell, aPC));
                        } else if ("SOURCE".equals(aLabel))
                        {
                            retValue.append(SourceFormat.getFormattedString(aSpell, Globals.getSourceDisplay(), true));
                        } else if ("SOURCESHORT".equals(aLabel))
                        {
                            retValue.append(SourceFormat.formatShort(aSpell, 8));
                        } else if ("SOURCEPAGE".equals(aLabel))
                        {
                            retValue.append(aSpell.get(StringKey.SOURCE_PAGE));
                        } else if ("SOURCEWEB".equals(aLabel))
                        {
                            String aTemp = aSpell.get(StringKey.SOURCE_WEB);

                            if ((aTemp != null) && !aTemp.isEmpty())
                            {
                                retValue.append(aTemp);
                            }
                        } else if ("SOURCELINK".equals(aLabel))
                        {
                            String aTemp = aSpell.get(StringKey.SOURCE_LINK);

                            if ((aTemp != null) && !aTemp.isEmpty())
                            {
                                retValue.append(aTemp);
                            }
                        } else if ("SUBSCHOOL".equals(aLabel))
                        {
                            retValue.append(aSpell.getListAsString(ListKey.SPELL_SUBSCHOOL));
                        } else if ("DESCRIPTOR".equals(aLabel))
                        {
                            retValue.append(aSpell.getListAsString(ListKey.SPELL_DESCRIPTOR));
                        } else if ("FULLSCHOOL".equals(aLabel))
                        {
                            String aTemp = aSpell.getListAsString(ListKey.SPELL_SCHOOL);

                            if ((!aSpell.getListAsString(ListKey.SPELL_SUBSCHOOL).isEmpty())
                                    && (!"NONE".equalsIgnoreCase(aSpell.getListAsString(ListKey.SPELL_SUBSCHOOL).trim())))
                            {
                                aTemp += (" (" + aSpell.getListAsString(ListKey.SPELL_SUBSCHOOL) + ')');
                            }

                            if (!aSpell.getListAsString(ListKey.SPELL_DESCRIPTOR).isEmpty())
                            {
                                aTemp += (" [" + aSpell.getListAsString(ListKey.SPELL_DESCRIPTOR) + ']');
                            }

                            retValue.append(aTemp);
                        } else if ("SR".equals(aLabel))
                        {
                            retValue.append(aSpell.getListAsString(ListKey.SPELL_RESISTANCE));
                        } else if ("SRSHORT".equals(aLabel))
                        {
                            if ("No".equals(aSpell.getListAsString(ListKey.SPELL_RESISTANCE)))
                            {
                                retValue.append('N');
                            } else
                            {
                                retValue.append('Y');
                            }
                        } else if ("CLASS".equals(aLabel))
                        {
                            retValue.append(OutputNameFormatting.getOutputName(aObject));
                        } else if ("DCSTAT".equals(aLabel))
                        {
                            if (aObject instanceof PCClass)
                            {
                                PCClass aClass = (PCClass) aObject;
                                retValue.append(aClass.getSpellBaseStat());
                            }
                        } else if ("TYPE".equals(aLabel))
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
                        } else if (aLabel.startsWith("DESCRIPTION"))
                        {
                            final String sString = aPC.getDescription(aSpell);

                            if (altLabel.isEmpty())
                            {
                                retValue.append(sString);
                            } else
                            {
                                retValue.append(sString.replaceAll("\r?\n", altLabel));
                            }
                        } else if (aLabel.startsWith("BONUSSPELL"))
                        {
                            String sString = "*";

                            if (aLabel.length() > 10)
                            {
                                sString = aLabel.substring(10);
                            }

                            retValue.append(getBonusSpellValue(aPC, spellLevel, sString, altLabel, aObject, bookName,
                                    selectedCSpell, aSpell));
                        } else if ("XPCOST".equals(aLabel))
                        {
                            retValue.append(aSpell.getSafe(IntegerKey.XP_COST));
                        } else if ("CT".equals(aLabel))
                        {
                            retValue.append(aSpell.getSafe(IntegerKey.CASTING_THRESHOLD));
                        }
                    }
                } else if ((eh != null) && eh.getExistsOnly())
                {
                    eh.setNoMoreItems(true);
                }
            } else if ((eh != null) && eh.getExistsOnly())
            {
                eh.setNoMoreItems(true);
            }
        }

        return retValue.toString();
    }

    private static String getAppliedName(final SpellInfo si)
    {
        List<Ability> featList = si.getFeatList();
        if ((featList == null) || featList.isEmpty())
        {
            return "";
        }

        final StringBuilder aBuf = new StringBuilder(50);
        for (int i = 0;i < featList.size();i++)
        {
            Object an = featList.get(i).getResolved(FactKey.valueOf("AppliedName"));
            aBuf.append((an == null) ? "" : an);
            if (i < featList.size())
            {
                aBuf.append(' ');
            }
        }

        return aBuf.toString();
    }

    /**
     * Display an * is the spell is a domain/specialty spell,
     * display ** if it is ONLY a domain/specialty spell. A value
     * may also be supplied that will be displayed for non-specialty spells.
     *
     * @param aPC        The character being processed.
     * @param spellLevel The level of the spell.
     * @param sString    The indicator to use for domain/specialty spells.
     * @param altLabel   The indicator to use for non domain/specialty spells.
     * @param aObject    The class containing the spell.
     * @param bookName   The name of the spell book.
     * @param cs         The spell as it applies to the character
     * @param aSpell     The generic spell.
     * @return The annotation string indicating domain/specialty status
     */
    private static String getBonusSpellValue(PlayerCharacter aPC, final int spellLevel, String sString,
            String altLabel,
            final PObject aObject, String bookName, CharacterSpell cs, Spell aSpell)
    {
        StringBuilder retValue = new StringBuilder();

        if ((aObject != null) && (cs != null) && cs.isSpecialtySpell(aPC) && (aObject instanceof PCClass))
        {
            final List<CharacterSpell> charSpells = aPC.getCharacterSpells(aObject, aSpell, bookName, spellLevel);
            boolean isDomainOnly = charSpells.stream().allMatch(cSpell -> cSpell.isSpecialtySpell(aPC));

            if (isDomainOnly)
            {
                retValue.append(sString);
            } else
            {
                retValue.append(sString).append(sString);
            }
        } else
        {
            retValue.append(altLabel);
        }

        return retValue.toString();
    }

    private static String replaceTokenSpellMemSourceLevel(Spell aSpell, PlayerCharacter aPC)
    {
        final HashMapToList<CDOMList<Spell>, Integer> tempHash = aPC.getSpellLevelInfo(aSpell);
        String tempSource;
        final Collection<String> levelSet = new TreeSet<>();

        for (CDOMList<Spell> spellList : tempHash.getKeySet())
        {
            String classKey = spellList.getKeyName();
            for (Integer lvl : tempHash.getListFor(spellList))
            {
                PCClass pcc = Globals.getContext().getReferenceContext().silentlyGetConstructedCDOMObject(PCClass.class,
                        classKey);
                if (pcc != null)
                {
                    classKey = pcc.getAbbrev();
                }
                levelSet.add(classKey + lvl);
            }
        }

        tempSource = String.join(", ", levelSet);

        return tempSource;
    }

}
