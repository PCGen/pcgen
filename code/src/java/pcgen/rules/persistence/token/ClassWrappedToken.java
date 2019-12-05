/*
 * Copyright 2008 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.rules.persistence.token;

import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.inst.PCClassLevel;
import pcgen.core.PCClass;
import pcgen.core.SubClass;
import pcgen.core.SubstitutionClass;
import pcgen.rules.context.LoadContext;

/**
 * ClassWrappedToken provides compatibility for previously allowed bad behavior
 * in data files.
 * <p>
 * Many Class tokens in PCGen versions up to 5.14 ignored the class level, so
 * they are technically Class tags and not CLASSLEVEL tags. Yet, PCGen 5.14
 * allows those tags to appear on class level lines. This is a bit deceptive to
 * users in that the effect will always be on the class, and not appear on the
 * specified level.
 * <p>
 * Unfortunately, one cannot simply remove support for using CLASS tokens on
 * CLASSLEVEL lines, because if they are used at level 1, then they are
 * equivalent to appearing on a CLASS line. Certainly, the data monkeys use it
 * that way. For example, Blackguard in RSRD advanced uses EXCHANGELEVEL on the
 * first level line.
 * <p>
 * Therefore the entire ClassWrappedToken system is a workaround for data
 * monkeys using CLASS tokens on CLASSLEVEL lines, and therefore it should only
 * work on level one, otherwise expectations for when the token will take
 * effect are not set.
 */
public class ClassWrappedToken implements CDOMCompatibilityToken<PCClassLevel>
{

    private static int wrapIndex = Integer.MIN_VALUE;

    private static final Integer ONE = 1;

    private final CDOMToken<PCClass> wrappedToken;

    private final int priority = wrapIndex++;

    @Override
    public Class<PCClassLevel> getTokenClass()
    {
        return PCClassLevel.class;
    }

    public ClassWrappedToken(CDOMToken<PCClass> tok)
    {
        wrappedToken = tok;
    }

    @Override
    public ParseResult parseToken(LoadContext context, PCClassLevel obj, String value)
    {
        if (ONE.equals(obj.get(IntegerKey.LEVEL)))
        {
            PCClass parent = (PCClass) obj.get(ObjectKey.TOKEN_PARENT);
            if (parent instanceof SubClass || parent instanceof SubstitutionClass)
            {
                return new ParseResult.Fail("Data used token: " + value + " which is a Class token, "
                        + "but it was used in a class level for a " + parent.getClass().getSimpleName());
            }
            return wrappedToken.parseToken(context, parent, value);
        }
        return new ParseResult.Fail("Data used token: " + value + " which is a Class token, "
                + "but it was used in a class level line other than level 1");
    }

    @Override
    public String getTokenName()
    {
        return wrappedToken.getTokenName();
    }

    @Override
    public int compatibilityLevel()
    {
        return 5;
    }

    @Override
    public int compatibilityPriority()
    {
        return priority;
    }

    @Override
    public int compatibilitySubLevel()
    {
        return 14;
    }

}
