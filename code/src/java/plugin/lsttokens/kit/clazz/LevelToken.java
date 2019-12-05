/*
 * Copyright 2006 (C) Aaron Divinsky <boomer70@yahoo.com>
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

package plugin.lsttokens.kit.clazz;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.FormulaFactory;
import pcgen.core.kit.KitClass;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * LevelToken (a component of Kits)
 */
public class LevelToken extends AbstractNonEmptyToken<KitClass> implements CDOMPrimaryToken<KitClass>
{

    /**
     * Gets the name of the tag this class will parse.
     *
     * @return Name of the tag this class handles
     */
    @Override
    public String getTokenName()
    {
        return "LEVEL";
    }

    @Override
    public Class<KitClass> getTokenClass()
    {
        return KitClass.class;
    }

    @Override
    protected ParseResult parseNonEmptyToken(LoadContext context, KitClass kitClass, String value)
    {
        Formula formula = FormulaFactory.getFormulaFor(value);
        if (!formula.isValid())
        {
            return new ParseResult.Fail("Formula in " + getTokenName() + " was not valid: " + formula.toString());
        }
        kitClass.setLevel(formula);
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, KitClass kitClass)
    {
        Formula f = kitClass.getLevel();
        if (f == null)
        {
            return null;
        }
        return new String[]{f.toString()};
    }
}
