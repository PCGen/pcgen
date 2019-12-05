/*
 * Copyright 2016 (C) Tom Parker <thpr@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package plugin.lsttokens.datacontrol;

import pcgen.cdom.content.DatasetVariable;
import pcgen.cdom.formula.scope.DynamicScope;
import pcgen.cdom.formula.scope.PCGenScope;
import pcgen.cdom.inst.Dynamic;
import pcgen.cdom.inst.DynamicCategory;
import pcgen.cdom.reference.ReferenceManufacturer;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with DYNAMICSCOPE Token
 */
public class DynamicScopeToken extends AbstractNonEmptyToken<DynamicCategory>
        implements CDOMPrimaryToken<DynamicCategory>
{

    @Override
    public String getTokenName()
    {
        return "DYNAMICSCOPE";
    }

    @Override
    protected ParseResult parseNonEmptyToken(LoadContext context, DynamicCategory obj, String value)
    {
        if (!DatasetVariable.isLegalName(value))
        {
            return new ParseResult.Fail(value + " is not a valid scope name in " + getTokenName());
        }
        obj.setName(value);
        ReferenceManufacturer<Dynamic> mfg = context.getReferenceContext().getManufacturerId(obj);
        PCGenScope scope = new DynamicScope(obj, mfg);
        context.getVariableContext().registerScope(scope);
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, DynamicCategory obj)
    {
        return new String[]{obj.getName()};
    }

    @Override
    public Class<DynamicCategory> getTokenClass()
    {
        return DynamicCategory.class;
    }

}
