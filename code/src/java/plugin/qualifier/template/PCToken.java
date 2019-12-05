/*
 * Copyright 2007 (C) Thomas Parker <thpr@users.sourceforge.net>
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
package plugin.qualifier.template;

import java.util.Collection;

import pcgen.core.PCTemplate;
import pcgen.core.PlayerCharacter;
import pcgen.rules.persistence.token.AbstractPCQualifierToken;

public class PCToken extends AbstractPCQualifierToken<PCTemplate>
{

    @Override
    protected Collection<PCTemplate> getPossessed(PlayerCharacter pc)
    {
        return pc.getDisplay().getTemplateSet();
    }

    @Override
    public Class<? super PCTemplate> getReferenceClass()
    {
        return PCTemplate.class;
    }

}
