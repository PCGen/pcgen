/*
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
 */
package pcgen.core;

import java.io.Serializable;
import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMObject;

/**
 * {@code TextProperty}.
 */
public abstract class TextProperty extends PObject implements Serializable, Comparable<Object>
{
    /**
     * Constructor
     */
    public TextProperty()
    {
        // Empty Constructor
    }

    /**
     * Constructor
     *
     * @param name
     */
    public TextProperty(final String name)
    {
        setName(name);
    }

    @Override
    public int compareTo(final Object obj)
    {
        if (obj instanceof TextProperty)
        {
            TextProperty tp = (TextProperty) obj;
            return getKeyName().compareTo(tp.getKeyName());
        } else if (obj instanceof CDOMObject)
        {
            CDOMObject pObj = (CDOMObject) obj;
            return getKeyName().compareToIgnoreCase(pObj.getKeyName());
        }

        return getKeyName().compareToIgnoreCase(obj.toString());
    }

    /**
     * Get the property text (name, value pair)
     *
     * @return the property text (name, value pair)
     */
    public String getText()
    {
        return getDisplayName();
    }

    /**
     * Get the parsed text (%CHOICEs replaced)
     *
     * @param pc
     * @return Get the parsed text (%CHOICEs replaced)
     */
    public String getParsedText(final PlayerCharacter pc, final VariableContainer varOwner, CDOMObject qualOwner)
    {
        return getParsedText(pc, getText(), varOwner, qualOwner);
    }

    protected String getParsedText(final PlayerCharacter pc, final String fullDesc, final VariableContainer varOwner,
            CDOMObject qOwner)
    {
        if (fullDesc == null || fullDesc.equals(""))
        {
            return "";
        }

        String source = qOwner.getQualifiedKey();
        String retString = "";
        if (qualifies(pc, qOwner))
        {
            // full desc will look like "description|var1|var2|var3|..."
            StringTokenizer varTok = new StringTokenizer(fullDesc, "|");
            // take the description as the first token
            final String description = varTok.nextToken();
            if (varTok.hasMoreTokens())
            {
                // Create an array of all of the variables
                boolean atLeastOneNonZero = false;
                int[] varValue = null;
                if (varTok.countTokens() != 0)
                {
                    varValue = new int[varTok.countTokens()];

                    for (int j = 0;j < varValue.length;++j)
                    {
                        final String varToken = varTok.nextToken();
                        //final int value = pc.getVariable(varToken, true, true, "", "", 0).intValue();
                        final int value = varOwner.getVariableValue(varToken, source, pc).intValue();
                        if (value != 0)
                        {
                            atLeastOneNonZero = true;
                        }
                        varValue[j] = value;
                    }
                }

                if (atLeastOneNonZero)
                {
                    final StringBuilder newAbility = new StringBuilder(100);
                    varTok = new StringTokenizer(description, "%", true);
                    int varCount = 0;

                    while (varTok.hasMoreTokens())
                    {
                        final String nextTok = varTok.nextToken();

                        if ("%".equals(nextTok))
                        {
                            if ((varValue != null) && (varCount < varValue.length))
                            {
                                newAbility.append(varValue[varCount++]);
                            } else
                            {
                                newAbility.append('%');
                            }
                        } else
                        {
                            newAbility.append(nextTok);
                        }
                    }
                    retString = newAbility.toString();
                } else
                {
                    retString = "";
                }
            } else
            {
                retString = description;
            }
        }
        return retString;
    }
}
