/*
 * TemplateToken.java
 * Copyright 2003 (C) Devon Jones <soulcatcher@evilsoft.org>
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
package plugin.exporttokens.deprecated;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.enumeration.FactKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.helper.CNAbilitySelection;
import pcgen.core.PCStat;
import pcgen.core.PCTemplate;
import pcgen.core.PlayerCharacter;
import pcgen.core.SpecialAbility;
import pcgen.core.analysis.BonusCalc;
import pcgen.core.analysis.OutputNameFormatting;
import pcgen.core.display.CharacterDisplay;
import pcgen.core.display.NonAbilityDisplay;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.Token;

/**
 * Deals with returning the values for the TEMPALTE Token
 * and it's Sub Tokens
 * <p>
 * TEMPLATE
 * TEMPLATE.x.NAME
 * TEMPLATE.x.OUTPUTNAME
 * TEMPLATE.x.SA
 * TEMPLATE.x.FEAT
 * TEMPLATE.x.SR
 * TEMPLATE.x.CR
 * TEMPLATE.x.DR
 * TEMPLATE.x.xxxMOD
 */
public class TemplateToken extends Token
{
    /**
     * Token name
     */
    public static final String TOKENNAME = "TEMPLATE";

    @Override
    public String getTokenName()
    {
        return TOKENNAME;
    }

    @Override
    public String getToken(String tokenSource, PlayerCharacter pc, ExportHandler eh)
    {
        String retString = "";
        PCTemplate template;

        CharacterDisplay display = pc.getDisplay();
        List<PCTemplate> tl = display.getOutputVisibleTemplateList();

        StringTokenizer aTok = new StringTokenizer(tokenSource, ".");
        aTok.nextToken();

        int indexOfTemplate;
        indexOfTemplate = Integer.parseInt(aTok.nextToken());

        String aLabel = (aTok.hasMoreTokens()) ? aTok.nextToken() : "NAME";

        if ((indexOfTemplate > -1) && (indexOfTemplate < tl.size()))
        {
            template = tl.get(indexOfTemplate);

            if ("NAME".equals(aLabel))
            {
                retString = getOutputNameToken(template);
            } else if ("OUTPUTNAME".equals(aLabel))
            {
                retString = getOutputNameToken(template);
            } else if ("APPLIEDNAME".equals(aLabel))
            {
                retString = getAppliedName(template);
            } else if ("SA".equals(aLabel))
            {
                retString = getSAToken(template, pc);
            } else if ("FEAT".equals(aLabel))
            {
                retString = getFeatToken(template, pc);
            } else if ("SR".equals(aLabel))
            {
                retString = Integer.toString(getSRToken(template, display));
            } else if ("CR".equals(aLabel))
            {
                // If the CR ends in .0, remove that for display purposes
                retString = Float.toString(getCRToken(template, display));
                String decimalPlaceValue = retString.substring(retString.length() - 2);
                if (decimalPlaceValue.equals(".0"))
                {
                    retString = retString.substring(0, retString.length() - 2);
                }
                return retString;
            } else if ("DR".equals(aLabel))
            {
                retString = display.calcDR();
            } else
            {
                retString = getModToken(pc, template, aLabel);
            }
        }

        return retString;
    }

    private String getAppliedName(PCTemplate template)
    {
        FactKey<String> fk = FactKey.valueOf("AppliedName");
        String retValue = template.getResolved(fk);
        if (retValue == null)
        {
            retValue = template.toString();
        }
        return retValue;
    }

    /**
     * Retrieve the list of the keynames of any feats
     * that the PC qualifies for at the supplied level and
     * hit dice.
     *
     * @param pc
     * @return a list of feats
     */
    public static List<CNAbilitySelection> feats(PlayerCharacter pc, PCTemplate pct)
    {
        final List<CNAbilitySelection> feats = new ArrayList<>();

        for (PCTemplate rlt : pct.getSafeListFor(ListKey.REPEATLEVEL_TEMPLATES))
        {
            for (PCTemplate lt : rlt.getSafeListFor(ListKey.LEVEL_TEMPLATES))
            {
                Collection<? extends CNAbilitySelection> featList = pc.getTemplateFeatList(lt);
                if (featList != null)
                {
                    feats.addAll(featList);
                }
            }
        }
        for (PCTemplate lt : pct.getSafeListFor(ListKey.LEVEL_TEMPLATES))
        {
            Collection<? extends CNAbilitySelection> featList = pc.getTemplateFeatList(lt);
            if (featList != null)
            {
                feats.addAll(featList);
            }
        }

        for (PCTemplate lt : pct.getSafeListFor(ListKey.HD_TEMPLATES))
        {
            Collection<? extends CNAbilitySelection> featList = pc.getTemplateFeatList(lt);
            if (featList != null)
            {
                feats.addAll(featList);
            }
        }

        Collection<? extends CNAbilitySelection> featList = pc.getTemplateFeatList(pct);
        if (featList != null)
        {
            feats.addAll(featList);
        }

        return feats;
    }

    /**
     * Get value of CR Sub Token
     *
     * @param template
     * @param display
     * @return value of CR Sub Token
     */
    public static float getCRToken(PCTemplate template, CharacterDisplay display)
    {
        return template.getCR(display.getTotalLevels(), display.totalHitDice());
    }

    /**
     * Get value of FEAT sub token
     *
     * @param template
     * @param pc
     * @return value of FEAT sub token
     */
    private static String getFeatToken(PCTemplate template, PlayerCharacter pc)
    {
        List<CNAbilitySelection> fList = feats(pc, template);
        return StringUtil.join(fList, ", ");
    }

    /**
     * Get value of MOD sub token
     *
     * @param pc
     * @param template
     * @param aLabel
     * @return value of MOD sub token
     */
    public static String getModToken(PlayerCharacter pc, PCTemplate template, String aLabel)
    {
        StringBuilder retString = new StringBuilder();

        for (PCStat stat : pc.getDisplay().getStatSet())
        {
            String modName = stat.getKeyName() + "MOD";

            if (aLabel.equals(modName))
            {
                if (NonAbilityDisplay.isNonAbilityForObject(stat, template))
                {
                    retString.append('*');
                } else
                {
                    retString.append(BonusCalc.getStatMod(template, stat, pc));
                }

                break;
            }
        }

        return retString.toString();
    }

    /**
     * Get value of OUTPUTNAME sub token
     *
     * @param template
     * @return value of OUTPUTNAME sub token
     */
    public static String getOutputNameToken(PCTemplate template)
    {
        return OutputNameFormatting.getOutputName(template);
    }

    /**
     * Get value of SA sub token
     *
     * @param template
     * @param pc
     * @return value of SA sub token
     */
    public static String getSAToken(PCTemplate template, PlayerCharacter pc)
    {
        CharacterDisplay display = pc.getDisplay();
        List<SpecialAbility> saList = new ArrayList<>();
        saList.addAll(display.getResolvedUserSpecialAbilities(template));
        saList.addAll(display.getResolvedSpecialAbilities(template));
        List<PCTemplate> subList = new ArrayList<>(template.getConditionalTemplates(display.getTotalLevels(), display.totalHitDice()));
        for (PCTemplate subt : subList)
        {
            saList.addAll(display.getResolvedUserSpecialAbilities(subt));
            saList.addAll(display.getResolvedSpecialAbilities(subt));
        }
        List<String> saDescList = new ArrayList<>();
        for (SpecialAbility sa : saList)
        {
            if (!sa.qualifies(pc, template))
            {
                continue;
            }
            final String saText = sa.getParsedText(pc, pc, template);
            if (saText != null && !saText.equals(""))
            {
                saDescList.add(saText);
            }
        }
        return StringUtil.join(saDescList, ", ");
    }

    /**
     * Get value of SR Sub token
     *
     * @param template
     * @param display
     * @return value of SR Sub token
     */
    public static int getSRToken(PCTemplate template, CharacterDisplay display)
    {
        return display.getTemplateSR(template, display.getTotalLevels(), display.totalHitDice());
    }
}
