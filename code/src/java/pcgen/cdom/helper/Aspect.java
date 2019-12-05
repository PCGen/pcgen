/*
 * Copyright 2008 (C) James Dempsey
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
package pcgen.cdom.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.ConcretePrereqObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.content.CNAbility;
import pcgen.cdom.enumeration.AspectName;
import pcgen.cdom.enumeration.MapKey;
import pcgen.core.Ability;
import pcgen.core.PlayerCharacter;
import pcgen.io.EntityEncoder;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriter;
import pcgen.util.Logging;

/**
 * The Class {@code Aspect} represents a generic name field for
 * abilities. It is a name/value characteristic allowing substitution of
 * values.
 *
 * <p>Variable substitution is performed by replacing a placeholder indicated
 * by %# with the #th variable in the variable list.  For example, the string
 * <br>{@code "This is %1 variable %3 %2"}
 * <br>would be replaced with the string &quot;This is a variable substitution
 * string&quot; if the variable list was &quot;a&quot;,&quot;string&quot;,
 * &quot;substitution&quot;.
 */
public class Aspect extends ConcretePrereqObject
{
    /**
     * The name of the name stored in this Aspect.
     */
    private final AspectName key;

    private final List<String> theComponents = new ArrayList<>();
    private List<String> theVariables = null;

    private static final String VAR_NAME = "%NAME"; //$NON-NLS-1$
    private static final String VAR_LIST = "%LIST"; //$NON-NLS-1$

    private static final String VAR_MARKER = "$$VAR:"; //$NON-NLS-1$

    /**
     * Instantiates a new aspect.
     *
     * @param name    the name of the aspect
     * @param aString the aspect string
     */
    public Aspect(final String name, final String aString)
    {
        Objects.requireNonNull(name, "Name for Aspect cannot be null");
        Objects.requireNonNull(aString, "Value for Aspect cannot be null");
        this.key = AspectName.getConstant(name);

        parseAspectString(aString);
    }

    /**
     * Instantiates a new aspect.
     *
     * @param key     the name of the aspect
     * @param aString the aspect string
     */
    public Aspect(final AspectName key, final String aString)
    {
        Objects.requireNonNull(key, "Key for Aspect cannot be null");
        Objects.requireNonNull(aString, "Value for Aspect cannot be null");
        this.key = key;

        parseAspectString(aString);
    }

    /**
     * Parse an aspect definition string and populate the Aspect with
     * the contents. This drives the processing to split the description
     * from the parameters and to identify the references to the
     * parameters.
     *
     * @param aString The aspect definition string.
     */
    private void parseAspectString(final String aString)
    {
        int currentInd = 0;
        int percentInd = -1;
        while ((percentInd = aString.indexOf('%', currentInd)) != -1)
        {
            final String preText = aString.substring(currentInd, percentInd);
            if (!preText.isEmpty())
            {
                theComponents.add(preText);
            }
            if (percentInd == aString.length() - 1)
            {
                theComponents.add("%"); //$NON-NLS-1$
                return;
            }
            if (aString.charAt(percentInd + 1) == '{')
            {
                // This is a bracketed placeholder.  The replacement parameter
                // is contained within the {}
                currentInd = aString.indexOf('}', percentInd + 1) + 1;
                final String replacement = aString.substring(percentInd + 1, currentInd);
                // For the time being we will only support numerics here.
                try
                {
                    Integer.parseInt(replacement);
                } catch (NumberFormatException nfe)
                {
                    Logging.errorPrintLocalised("Errors.Description.InvalidVariableReplacement", //$NON-NLS-1$
                            replacement);
                }
                theComponents.add(VAR_MARKER + replacement);
            } else if (aString.charAt(percentInd + 1) == '%')
            {
                // This is an escape sequence so we can actually print a %
                currentInd = percentInd + 2;
                theComponents.add("%"); //$NON-NLS-1$
            } else
            {
                // In this case we have an unbracketed placeholder.  We will
                // walk the string until such time as we no longer have a number
                currentInd = percentInd + 1;
                while (currentInd < aString.length())
                {
                    final char val = aString.charAt(currentInd);
                    try
                    {
                        Integer.parseInt(String.valueOf(val));
                        currentInd++;
                    } catch (NumberFormatException nfe)
                    {
                        break;
                    }
                }
                if (currentInd > percentInd + 1)
                {
                    theComponents.add(VAR_MARKER + aString.substring(percentInd + 1, currentInd));
                } else
                {
                    // We broke out of the variable finding loop without finding
                    // even a single integer.  Assume we have a DESC field that
                    // is using a % unescaped.
                    theComponents.add(aString.substring(percentInd, percentInd + 1));
                }
            }
        }
        theComponents.add(aString.substring(currentInd));
    }

    /**
     * Adds a variable to use in variable substitution.
     *
     * @param aVariable
     */
    public void addVariable(final String aVariable)
    {
        if (theVariables == null)
        {
            theVariables = new ArrayList<>();
        }
        theVariables.add(aVariable);
    }

    /**
     * Gets the name of the aspect.
     *
     * @return the aspect name
     */
    public String getName()
    {
        return key.toString();
    }

    /**
     * Gets the key of the aspect.
     *
     * @return the aspect key
     */
    public AspectName getKey()
    {
        return key;
    }

    /**
     * Gets the name string after having substituting all variables.
     *
     * @param aPC       The PlayerCharacter used to evaluate formulas.
     * @param abilities the abilities for which the Aspect text should be compiled
     * @return The fully substituted description string.
     */
    public String getAspectText(final PlayerCharacter aPC, List<CNAbility> abilities)
    {
        final StringBuilder buf = new StringBuilder(50);

        if ((abilities == null) || (abilities.isEmpty()))
        {
            return "";
        }
        Ability sampleAbilityObject = abilities.get(0).getAbility();
        if (!qualifies(aPC, sampleAbilityObject))
        {
            return "";
        }
        for (final String comp : theComponents)
        {
            if (comp.startsWith(VAR_MARKER))
            {
                final int ind = Integer.parseInt(comp.substring(VAR_MARKER.length()));
                if (theVariables == null || ind > theVariables.size())
                {
                    buf.append(Constants.EMPTY_STRING);
                    continue;
                }
                final String var = theVariables.get(ind - 1);
                if (var.equals(VAR_NAME))
                {
                    buf.append(sampleAbilityObject.getOutputName());
                } else if (var.equals(VAR_LIST))
                {
                    List<String> assocList = new ArrayList<>();
                    for (CNAbility cna : abilities)
                    {
                        assocList.addAll(aPC.getAssociationList(cna));
                    }
                    String joinString;
                    if (assocList.size() == 2)
                    {
                        joinString = " and ";
                    } else
                    {
                        joinString = ", ";
                    }
                    Collections.sort(assocList);
                    buf.append(StringUtil.join(assocList, joinString));
                } else if (var.startsWith("\"")) //$NON-NLS-1$
                {
                    buf.append(var.substring(1, var.length() - 1));
                } else
                {
                    buf.append(aPC.getVariableValue(var, "Aspect").intValue()); //$NON-NLS-1$
                }
            } else
            {
                buf.append(comp);
            }
        }
        return buf.toString();
    }

    /**
     * Gets the Aspect tag in PCC format.
     *
     * @return A String in LST file format for this description.
     */
    public String getPCCText()
    {
        final StringBuilder buf = new StringBuilder();

        for (final String str : theComponents)
        {
            if (str.startsWith(VAR_MARKER))
            {
                final int ind = Integer.parseInt(str.substring(VAR_MARKER.length()));
                buf.append('%').append(ind);
            } else if (str.equals("%"))
            {
                //reescape
                buf.append("%%");
            } else
            {
                buf.append(EntityEncoder.encode(str));
            }
        }
        if (theVariables != null)
        {
            for (final String var : theVariables)
            {
                buf.append(Constants.PIPE);
                buf.append(var);
            }
        }

        if (hasPrerequisites())
        {
            buf.append(Constants.PIPE);
            buf.append(new PrerequisiteWriter().getPrerequisiteString(getPrerequisiteList(), Constants.PIPE));
        }

        return buf.toString();
    }

    @Override
    public String toString()
    {
        return getPCCText();
    }

    @Override
    public int hashCode()
    {
        return theComponents.size() + 7 * (theVariables == null ? 0 : theVariables.size());
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }
        if (!(obj instanceof Aspect))
        {
            return false;
        }
        Aspect other = (Aspect) obj;
        if (theVariables == null && other.theVariables != null)
        {
            return false;
        }
        return theComponents.equals(other.theComponents)
                && (theVariables == null || theVariables.equals(other.theVariables));
    }

    public static String printAspect(PlayerCharacter pc, AspectName key, List<CNAbility> abilities, boolean printName)
    {
        if (abilities.isEmpty())
        {
            return "";
        }
        Ability sampleAbilityObject = abilities.get(0).getAbility();
        StringBuilder buff = new StringBuilder(50);
        List<Aspect> aspects = sampleAbilityObject.get(MapKey.ASPECT, key);
        Aspect aspect = lastPassingAspect(aspects, pc, sampleAbilityObject);
        if (aspect != null)
        {
            if (printName)
            {
                buff.append(aspect.getName()).append(": ");
            }
            buff.append(aspect.getAspectText(pc, abilities));
        }
        return buff.toString();
    }

    public static String printAspect(PlayerCharacter pc, AspectName key, List<CNAbility> abilities)
    {
        return printAspect(pc, key, abilities, true);
    }

    public static String printAspectValue(PlayerCharacter pc, AspectName key, List<CNAbility> abilities)
    {
        return printAspect(pc, key, abilities, false);
    }

    public static Aspect lastPassingAspect(List<Aspect> aspects, PlayerCharacter pc, Ability a)
    {
        Aspect retAspect = null;
        if (aspects != null)
        {
            for (Aspect testAspect : aspects)
            {
                if (testAspect.qualifies(pc, a))
                {
                    retAspect = testAspect;
                }
            }
        }
        return retAspect;
    }
}
