/*
 * Copyright (c) 2008 Tom Parker <thpr@users.sourceforge.net>
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package plugin.lsttokens.pcclass;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.PCClass;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteOperator;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.DeferredToken;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.Logging;

/**
 * Class deals with PRERACETYPE Token
 */
public class PreracetypeToken extends AbstractNonEmptyToken<PCClass>
        implements CDOMPrimaryToken<PCClass>, DeferredToken<PCClass>
{

    @Override
    public String getTokenName()
    {
        return "PRERACETYPE";
    }

    @Override
    protected ParseResult parseNonEmptyToken(LoadContext context, PCClass pcc, String value)
    {
        Prerequisite p = new Prerequisite();
        p.setKind("RACETYPE");
        p.setOperand("1");
        p.setKey(value);
        p.setOperator(PrerequisiteOperator.GTEQ);
        context.getObjectContext().put(pcc, ObjectKey.PRERACETYPE, p);
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, PCClass obj)
    {
        Prerequisite prereq = context.getObjectContext().getObject(obj, ObjectKey.PRERACETYPE);
        if (prereq == null)
        {
            return null;
        }
        return new String[]{prereq.getKey()};
    }

    @Override
    public Class<PCClass> getTokenClass()
    {
        return PCClass.class;
    }

    @Override
    public boolean process(LoadContext context, PCClass obj)
    {
        Prerequisite prereq = obj.get(ObjectKey.PRERACETYPE);
        if (prereq != null)
        {
            if (!obj.isMonster())
            {
                Logging.errorPrint("PCClass " + obj.getKeyName() + " is not a Monster, but used PRERACETYPE");
                return false;
            }
            obj.addPrerequisite(prereq);
        }
        return true;
    }

    @Override
    public Class<PCClass> getDeferredTokenClass()
    {
        return getTokenClass();
    }

}
