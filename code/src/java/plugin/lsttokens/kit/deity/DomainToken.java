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

package plugin.lsttokens.kit.deity;

import java.util.Collection;
import java.util.StringTokenizer;

import pcgen.cdom.base.Constants;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.cdom.reference.ReferenceUtilities;
import pcgen.core.Domain;
import pcgen.core.kit.KitDeity;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * DOMAIN Token for KitDeity
 */
public class DomainToken extends AbstractTokenWithSeparator<KitDeity> implements CDOMPrimaryToken<KitDeity>
{

    private static final Class<Domain> DOMAIN_CLASS = Domain.class;

    /**
     * Gets the name of the tag this class will parse.
     *
     * @return Name of the tag this class handles
     */
    @Override
    public String getTokenName()
    {
        return "DOMAIN";
    }

    @Override
    public Class<KitDeity> getTokenClass()
    {
        return KitDeity.class;
    }

    @Override
    protected char separator()
    {
        return '|';
    }

    @Override
    protected ParseResult parseTokenWithSeparator(LoadContext context, KitDeity kitDeity, String value)
    {
        StringTokenizer pipeTok = new StringTokenizer(value, Constants.PIPE);
        while (pipeTok.hasMoreTokens())
        {
            String tokString = pipeTok.nextToken();
            CDOMSingleRef<Domain> ref = context.getReferenceContext().getCDOMReference(DOMAIN_CLASS, tokString);
            kitDeity.addDomain(ref);
        }
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, KitDeity kitDeity)
    {
        Collection<CDOMSingleRef<Domain>> domains = kitDeity.getDomains();
        if (domains == null || domains.isEmpty())
        {
            return null;
        }
        return new String[]{ReferenceUtilities.joinLstFormat(domains, Constants.PIPE)};
    }
}
