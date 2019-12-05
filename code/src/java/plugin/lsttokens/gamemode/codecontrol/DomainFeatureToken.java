/*
 * Copyright 2018 (C) Thomas Parker <thpr@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */
package plugin.lsttokens.gamemode.codecontrol;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.inst.CodeControl;
import pcgen.cdom.util.CControl;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.AbstractYesNoToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * CodeControl Token for the Domain Feature.
 */
public class DomainFeatureToken extends AbstractNonEmptyToken<CodeControl>
        implements CDOMPrimaryToken<CodeControl>
{
    @Override
    public String getTokenName()
    {
        return CControl.DOMAINFEATURE;
    }

    @Override
    protected ParseResult parseNonEmptyToken(LoadContext context,
            CodeControl cdo, String value)
    {
        ObjectKey<Boolean> objectKey =
                ObjectKey.getKeyFor(Boolean.class, '*' + getTokenName());
        return AbstractYesNoToken.parseYesNoToObjectKey(context, cdo, value,
                getTokenName(), objectKey);
    }

    @Override
    public Class<CodeControl> getTokenClass()
    {
        return CodeControl.class;
    }

    @Override
    public String[] unparse(LoadContext context, CodeControl obj)
    {
        //Dynamic build of ObjectKey prevents this
        throw new UnsupportedOperationException(
                "Cannot unparse DOMAINFEATURE code control");
    }
}
