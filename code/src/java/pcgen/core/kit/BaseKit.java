/*
 * Copyright 2001 (C) Greg Bingleman <byngl@hotmail.com>
 * Copyright 2003 (C) Jonas Karlson <jujutsunerd@sf.net>
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
 */
package pcgen.core.kit;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.ConcretePrereqObject;
import pcgen.cdom.base.Loadable;
import pcgen.cdom.helper.OptionBound;
import pcgen.core.Kit;
import pcgen.core.PlayerCharacter;

/**
 * Common code for the kits.
 */
public abstract class BaseKit extends ConcretePrereqObject implements Loadable
{

    private URI sourceURI;
    List<OptionBound> bounds;

    public void setOptionBounds(Formula min, Formula max)
    {
        if (bounds == null)
        {
            bounds = new ArrayList<>();
        }
        bounds.add(new OptionBound(min, max));
    }

    /**
     * Returns true if the value is in the option range for this item
     *
     * @param pc  Charater the kit is being applied to. Used to eval formulas
     * @param val the Select value
     * @return true if the value is an option
     */
    public boolean isOption(PlayerCharacter pc, int val)
    {
        if (bounds != null)
        {
            for (OptionBound bound : bounds)
            {
                if (bound.isOption(pc, val))
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Is this an optional kit task which should only be actioned if it matches
     * a select value?
     *
     * @return true if this is an optional task, false if it should always be actioned.
     */
    public boolean isOptional()
    {
        return bounds != null;
    }

    /**
     * Evaluate the EVAL expression
     *
     * @param aPC
     * @param aValue
     * @return the answer
     */
    public static String eval(PlayerCharacter aPC, String aValue)
    {
        String ret = aValue;

        int evalInd = aValue.indexOf("EVAL(");
        if (evalInd == -1)
        {
            return aValue;
        }

        while (evalInd != -1)
        {
            String evalStr = aValue.substring(evalInd);
            StringBuilder modString = new StringBuilder();

            modString.append(aValue.substring(0, evalInd));

            int nestingLevel = 1;
            int startInd = 4;
            int endInd = startInd + 1;
            while (endInd < aValue.length() - 1)
            {
                char c = evalStr.charAt(endInd);
                if (c == '(')
                {
                    nestingLevel++;
                } else if (c == ')')
                {
                    nestingLevel--;
                    if (nestingLevel == 0)
                    {
                        // We found our matching paren
                        break;
                    }
                }
                endInd++;
            }
            if (nestingLevel != 0)
            {
                // We don't have a valid expression. Maybe someone else will
                // handle it.
                return aValue;
            }
            evalStr = evalStr.substring(5, endInd);
            int val = aPC.getVariableValue(evalStr, "").intValue();
            String evalVal = String.valueOf(val);
            modString.append(evalVal);
            modString.append(aValue.substring(endInd + 5));
            ret = modString.toString();

            evalInd = ret.indexOf("EVAL(");
        }

        return ret;
    }

    /**
     * Test applying a kit
     *
     * @param aKit     The owning kit for this item
     * @param aPC      The character the kit is being applied to
     * @param warnings A list of warnings generated while attempting to apply the kit
     * @return true if OK
     */
    public abstract boolean testApply(Kit aKit, PlayerCharacter aPC, List<String> warnings);

    /**
     * Apply Kit
     *
     * @param aPC The character to apply the kit to.
     */
    public abstract void apply(PlayerCharacter aPC);

    /**
     * Get object name
     *
     * @return object name
     */
    public abstract String getObjectName();

    public Collection<OptionBound> getBounds()
    {
        return bounds == null ? null : Collections.unmodifiableList(bounds);
    }

    @Override
    public URI getSourceURI()
    {
        return sourceURI;
    }

    @Override
    public void setSourceURI(URI source)
    {
        sourceURI = source;
    }

    @Override
    public String getDisplayName()
    {
        return null;
    }

    @Override
    public void setName(String name)
    {
        //TODO illegal?
    }

    @Override
    public String getKeyName()
    {
        return null;
    }

    @Override
    public boolean isInternal()
    {
        return false;
    }

    @Override
    public boolean isType(String type)
    {
        return false;
    }

}
