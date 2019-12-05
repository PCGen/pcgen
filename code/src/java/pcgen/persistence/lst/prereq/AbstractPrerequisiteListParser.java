/*
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
 * Copyright 2003 (C) Chris Ward <frugal@purplewombat.co.uk>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	   See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 *
 *
 */
package pcgen.persistence.lst.prereq;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteOperator;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.Logging;

import org.apache.commons.lang3.StringUtils;

/**
 * Abstract PRE parser, provides common parsing for many PRE tokens.
 */
public abstract class AbstractPrerequisiteListParser extends AbstractPrerequisiteParser
        implements PrerequisiteParserInterface
{

    protected void convertKeysToSubKeys(Prerequisite prereq, String kind)
    {
        if (prereq == null)
        {
            return;
        }
        if (prereq.getKind() != null && prereq.getKind().equalsIgnoreCase(kind))
        {
            String key = prereq.getKey().trim();

            int index = key.indexOf('(');
            int endIndex = key.lastIndexOf(')');

            if ((index >= 0) && (endIndex == key.length() - 1))
            {
                String subKey = key.substring(index + 1, endIndex).trim();
                key = key.substring(0, index).trim();

                prereq.setKey(key);
                prereq.setSubKey(subKey);
            }
        }

        for (Prerequisite element : prereq.getPrerequisites())
        {
            convertKeysToSubKeys(element, kind);
        }
    }

    /**
     * Parse the pre req list.
     *
     * @param kind            The kind of the prerequisite (less the "PRE" prefix)
     * @param formula         The body of the prerequisite.
     * @param invertResult    Whether the prerequisite should invert the result.
     * @param overrideQualify if set true, this prerequisite will be enforced in spite
     *                        of any "QUALIFY" tag that may be present.
     * @return PreReq
     * @throws PersistenceLayerException
     */
    @Override
    public Prerequisite parse(String kind, String formula, boolean invertResult, boolean overrideQualify)
            throws PersistenceLayerException
    {

        Prerequisite prereq = super.parse(kind, formula, invertResult, overrideQualify);
        parsePrereqListType(prereq, kind, formula);

        if (invertResult)
        {
            prereq.setOperator(prereq.getOperator().invert());
        }
        return prereq;
    }

    /*
     * Parses a PRE type, some examples below:
     *
     * CLASS:1,Spellcaster=3
     * <prereq kind="class" key="Spellcaster" min="3" />
     *
     * SKILL:1,Heal=5
     * <prereq kind="skill" key="Heal" min="5" />
     *
     * FEAT:1,TYPE=Necromantic
     * <prereq kind="feat" key="TYPE=Necromantic" />
     *
     * SKILL:2,Knowledge (Anthropology),Knowledge (Biology),Knowledge
     * (Chemistry)=5
     * <prereq min="2">
     *   <prereq kind="skill" key="Knowledge (Anthropology)" min="5" />
     *   <prereq kind="skill" key="Knowledge (Biology)" min="5" />
     *   <prereq kind="skill" key="Knowledge (Chemistry)" min="5" />
     * </prereq>
     *
     * FEAT:2,CHECKMULT,Spell Focus
     * <prereq kind="feat" count-multiples="true" key="feat.spell_focus" />
     *
     * FEAT:2,CHECKMULT,Spell Focus,[Spell Focus(Enchantment)]
     * <prereq min="2">
     *  <prereq kind="feat" key="feat.spell_focus" count-multiples="true" min="2"/>
     *   <prereq kind="feat" key="feat.spell_focus_enchantment" logical="not" />
     * </prereq>
     *
     * STAT:1,DEX=9,STR=13
     * <prereq operator="gteq" op1="1">
     *   <prereq kind="stat" key="dex" operator="gteq" op1="9" />
     *   <prereq kind="stat" key="str" operator="gteq" op1="13" />
     * </prereq>
     */
    protected void parsePrereqListType(Prerequisite prereq, String kind, String formula)
            throws PersistenceLayerException
    {
        // Sanity checking
        ParseResult parseResult = checkForIllegalSeparator(kind, ',', formula);
        if (!parseResult.passed())
        {
            throw new PersistenceLayerException(parseResult.toString());
        }
        if (!allowsNegate() && (formula.indexOf('[') >= 0 || formula.indexOf(']') >= 0))
        {
            throw new PersistenceLayerException("Prerequisite " + kind + " can not contain []: " + formula);
        }
        if (formula.indexOf('|') >= 0)
        {
            throw new PersistenceLayerException("Prerequisite " + kind + " can not contain |: " + formula);
        }

        String[] elements = formula.split(",");
        int numRequired;
        try
        {
            numRequired = Integer.parseInt(elements[0]);
            if (elements.length == 1)
            {
                throw new PersistenceLayerException("Prerequisite " + kind + " can not have only a count: " + formula);
            }
        } catch (NumberFormatException nfe)
        {
            throw new PersistenceLayerException("'" + elements[0] + "' is not a valid integer", nfe);
        }

        // Examine the last element to see if it is of the form "foo=n"
        int elementsLength = elements.length;
        for (int i = elementsLength - 1;i >= 0;--i)
        {
            if ("CHECKMULT".equalsIgnoreCase(elements[i]))
            {
                prereq.setCountMultiples(true);
                --elementsLength;
            }
        }

        // Token now contains all of the possible matches,
        // min contains the target number (if there is one)
        // number contains the number of 'tokens' that be at least 'min'
        if (elementsLength > 2)
        {
            // we have more than one option, so use a group
            prereq.setOperator(PrerequisiteOperator.GTEQ);
            prereq.setOperand(Integer.toString(numRequired));
            prereq.setKind(null);
            boolean hasKeyValue = false;
            boolean hasKeyOnly = false;
            int min = -99;
            for (int i = 1;i < elements.length;i++)
            {
                String thisElement = elements[i];
                if ("CHECKMULT".equals(thisElement))
                {
                    continue;
                }
                boolean warnIgnored = isNoWarnElement(thisElement);
                Prerequisite subreq = new Prerequisite();
                subreq.setKind(kind.toLowerCase());
                subreq.setCountMultiples(true);
                if (thisElement.indexOf('=') >= 0)
                {
                    // The element is either of the form "TYPE=foo" or "DEX=9"
                    // if it is the later, we need to extract the '9'
                    subreq.setOperator(PrerequisiteOperator.GTEQ);
                    String[] tokens = thisElement.split("=");
                    try
                    {
                        int valueIndx = tokens.length - 1;
                        min = Integer.parseInt(tokens[valueIndx]);
                        subreq.setOperand(Integer.toString(min));
                        String requirementKey = getRequirementKey(tokens);
                        subreq.setKey(requirementKey);
                        // now back fill all of the previous prereqs with this minimum
                        for (Prerequisite p : new ArrayList<>(prereq.getPrerequisites()))
                        {
                            if (p.getOperand().equals("-99"))
                            {
                                p.setOperand(Integer.toString(min));
                                // If this requirement has already been added, we don't want to repeat it.
                                if (p.getKey().equals(requirementKey))
                                {
                                    prereq.removePrerequisite(p);
                                }
                            }
                        }
                        if (!warnIgnored)
                        {
                            hasKeyValue = true;
                        }
                    } catch (NumberFormatException nfe)
                    {
                        subreq.setKey(thisElement);
                        if (!warnIgnored)
                        {
                            hasKeyOnly = true;
                        }
                    }
                } else
                {
                    if (requiresValue())
                    {
                        throw new PersistenceLayerException(
                                "Prerequisites of kind " + kind + " require a target value, e.g. Key=Value");
                    }
                    String assumed = getAssumedValue();
                    if (assumed == null)
                    {
                        subreq.setOperand(Integer.toString(min));
                    } else
                    {
                        Logging.deprecationPrint("Old syntax detected: " + "Prerequisites of kind " + kind
                                + " now require a target value, " + "e.g. Key=Value.  Assuming Value=" + assumed);
                        subreq.setOperand(assumed);
                    }
                    if (!warnIgnored)
                    {
                        hasKeyOnly = true;
                    }
                    subreq.setKey(thisElement);
                    subreq.setOperator(PrerequisiteOperator.GTEQ);
                }
                subreq.setOperand(Integer.toString(min));
                prereq.addPrerequisite(subreq);
            }
            for (Prerequisite element : prereq.getPrerequisites())
            {
                if (element.getOperand().equals("-99"))
                {
                    element.setOperand("1");
                }
            }
            if (hasKeyOnly && hasKeyValue)
            {
                Logging.deprecationPrint("You are using a deprecated syntax of PRE" + kind + ":" + formula
                        + " ... Each item in the list should have a target value, e.g.: PRE" + kind
                        + ":1,First=99,Second=5");
            }
        } else
        {
            // We only have a number of prereqs to pass, and a single prereq so we do not want a
            // wrapper prereq around a list of 1 element.
            // i.e. 1,Alertness, or 2,TYPE=ItemCreation, or 1,Reflex=7 or 3,Knowledge%=2 or 4,TYPE.Craft=5
            Prerequisite subreq = prereq;
            if (elementsLength > 1)
            {
                for (int i = 1;i < elements.length;++i)
                {
                    if ("CHECKMULT".equalsIgnoreCase(elements[i]))
                    {
                        continue;
                    }

                    if (elements[i].indexOf('=') >= 0)
                    {
                        // i.e. TYPE=ItemCreation or Reflex=7
                        String[] tokens = elements[i].split("=");
                        int valueIdx = tokens.length - 1;
                        try
                        {
                            // i.e. Reflex=7 or TYPE.Craft=5
                            int iOper = Integer.parseInt(tokens[valueIdx]);
                            if (numRequired != 1)
                            {
                                //
                                // If we would lose the required number of matches,
                                // then make this a PREMULT
                                //
                                prereq.setOperator(PrerequisiteOperator.GTEQ);
                                prereq.setOperand(Integer.toString(numRequired));
                                prereq.setKind(null);
                                subreq = new Prerequisite();
                                prereq.addPrerequisite(subreq);
                                subreq.setCountMultiples(true);
                            }
                            subreq.setOperand(Integer.toString(iOper));
                            String requirementKey = getRequirementKey(tokens);
                            subreq.setKey(requirementKey);
                        } catch (NumberFormatException nfe)
                        {
                            if (tokens[valueIdx].equals("ANY"))
                            {
                                if (isAnyLegal())
                                {
                                    subreq.setOperand(tokens[valueIdx]);
                                    subreq.setKey(getRequirementKey(tokens));
                                } else
                                {
                                    throw new PersistenceLayerException(
                                            "Prerequisites of kind " + kind + " do not support 'ANY'", nfe);
                                }
                            } else
                            {
                                // i.e. TYPE=ItemCreation
                                subreq.setOperand(elements[0]);
                                subreq.setKey(elements[i]);
                            }
                        }
                    } else
                    {
                        if (requiresValue())
                        {
                            throw new PersistenceLayerException(
                                    "Prerequisites of kind " + kind + " require a target value, e.g. Key=Value");
                        }
                        String assumed = getAssumedValue();
                        if (assumed == null)
                        {
                            subreq.setOperand(elements[0]);
                        } else
                        {
                            Logging.deprecationPrint("Old syntax detected: " + "Prerequisites of kind " + kind
                                    + " now require a target value, " + "e.g. Key=Value.  Assuming Value=" + assumed);
                            subreq.setOperand(assumed);
                        }
                        subreq.setKey(elements[i]);
                    }
                }
            } else
            {
                subreq.setOperand(elements[0]);
            }
            subreq.setKind(kind.toLowerCase());
            subreq.setOperator(PrerequisiteOperator.GTEQ);
        }
    }

    private String getRequirementKey(String[] tokens)
    {
        String reqKey;
        if (tokens.length == 2)
        {
            reqKey = tokens[0];
        } else
        {
            List<String> parts = new ArrayList<>(Arrays.asList(tokens));
            parts.remove(parts.size() - 1);
            reqKey = StringUtils.join(parts, "=");
        }
        return reqKey;
    }

    protected boolean isNoWarnElement(String thisElement)
    {
        return false;
    }

    protected boolean isAnyLegal()
    {
        return true;
    }

    protected String getAssumedValue()
    {
        return null;
    }

    protected boolean requiresValue()
    {
        return false;
    }

    /**
     * @return Does this PREreq kind allow []  for negation
     */
    protected boolean allowsNegate()
    {
        return false;
    }

    /**
     * Flag each Prerequisite created to indicate that no character is
     * required to successfully test the Prerequisite. The function is
     * recursive to handle a single Prerequisite that gets split out
     * into a premult.
     *
     * @param prereq the new no need for char
     */
    protected void setNoNeedForChar(Prerequisite prereq)
    {
        if (prereq == null)
        {
            return;
        }
        prereq.setCharacterRequired(false);

        for (Prerequisite element : prereq.getPrerequisites())
        {
            setNoNeedForChar(element);
        }
    }
}
