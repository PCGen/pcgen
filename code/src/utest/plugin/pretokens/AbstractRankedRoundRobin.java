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
package plugin.pretokens;

import org.junit.jupiter.api.Test;

public abstract class AbstractRankedRoundRobin extends AbstractPreRoundRobin
{

    public abstract String getBaseString();

    public abstract boolean isTypeAllowed();

    protected boolean isSubAllowed()
    {
        return true;
    }

    public abstract boolean isAnyAllowed();

    protected String getPrefix()
    {
        return "";
    }

    @Test
    public void testBasic()
    {
        runRoundRobin("PRE" + getBaseString() + ":1," + getPrefix() + "Foo=1");
    }

    @Test
    public void testMultiple()
    {
        runRoundRobin("PRE" + getBaseString() + ":1," + getPrefix()
                + "Foo=1,Bar=2");
    }

    @Test
    public void testNoCombineSub()
    {
        runRoundRobin("PREMULT:1,[PRE" + getBaseString() + ":1," + getPrefix()
                + "Foo=1,Bar=2],[PRE" + getBaseString() + ":2," + getPrefix()
                + "Goo=3,Hot=4]");
    }

    @Test
    public void testCombineSub()
    {
        runSimpleRoundRobin("PREMULT:2,[!PRE" + getBaseString() + ":1,"
                + getPrefix() + "Foo=1],[!PRE" + getBaseString() + ":1,"
                + getPrefix() + "Goo=2]", "!PRE" + getBaseString() + ":1,"
                + getPrefix() + "Foo=1,Goo=2");
    }

    @Test
    public void testCombineSubNegative()
    {
        runSimpleRoundRobin("!PREMULT:2,[!PRE" + getBaseString() + ":1,"
                + getPrefix() + "Foo=1],[!PRE" + getBaseString() + ":1,"
                + getPrefix() + "Goo=2]", "PRE" + getBaseString() + ":1,"
                + getPrefix() + "Foo=1,Goo=2");
    }

    @Test
    public void testNoCombineSubNegative()
    {
        runRoundRobin("PREMULT:1,[!PRE" + getBaseString() + ":1," + getPrefix()
                + "Foo=1],[!PRE" + getBaseString() + ":1," + getPrefix() + "Goo=3]");
    }

    @Test
    public void testNoCombineMult()
    {
        runRoundRobin("PREMULT:2,[PRE" + getBaseString() + ":1," + getPrefix()
                + "Foo=1,Bar=2],[PRE" + getBaseString() + ":1," + getPrefix()
                + "Goo=3,Hot=4]");
    }

    @Test
    public void testHigher()
    {
        runRoundRobin("PRE" + getBaseString() + ":1," + getPrefix() + "Foo=3");
    }

    @Test
    public void testBothMultiple()
    {
        runRoundRobin("PRE" + getBaseString() + ":2," + getPrefix()
                + "Foo=3,Bar=5,Goo=6");
    }

    @Test
    public void testType()
    {
        if (isTypeAllowed())
        {
            runRoundRobin("PRE" + getBaseString() + ":1," + getPrefix()
                    + "TYPE.Foo=3");
        }
    }

    @Test
    public void testTypeMultipleCount()
    {
        if (isTypeAllowed())
        {
            runRoundRobin("PRE" + getBaseString() + ":2," + getPrefix()
                    + "TYPE.Foo=3");
        }
    }

    @Test
    public void testAny()
    {
        if (isAnyAllowed())
        {
            runRoundRobin("PRE" + getBaseString() + ":1," + getPrefix()
                    + "ANY=3");
        }
    }

    @Test
    public void testMultipleType()
    {
        if (isTypeAllowed())
        {
            runRoundRobin("PRE" + getBaseString() + ":1," + getPrefix()
                    + "TYPE.Bar=3,TYPE.Foo=2");
        }
    }

    @Test
    public void testTypeAnd()
    {
        if (isTypeAllowed())
        {
            runRoundRobin("PRE" + getBaseString() + ":1," + getPrefix()
                    + "TYPE.Foo.Bar=3");
        }
    }

    @Test
    public void testComplex()
    {
        if (isTypeAllowed())
        {
            runRoundRobin("PRE" + getBaseString() + ":3," + getPrefix()
                    + "Foo=3,TYPE.Bar=4");
        }
    }

    @Test
    public void testBasicSub()
    {
        if (isSubAllowed())
        {
            runRoundRobin("PRE" + getBaseString() + ":1," + getPrefix()
                    + "Foo (Bar)=1");
        }
    }

    @Test
    public void testMultipleSub()
    {
        if (isSubAllowed())
        {
            runRoundRobin("PRE" + getBaseString() + ":1," + getPrefix()
                    + "Foo (Bar)=1,Bar (Goo)=2");
        }
    }

    @Test
    public void testNoCombineSubSub()
    {
        if (isSubAllowed())
        {
            runRoundRobin("PREMULT:1,[PRE" + getBaseString() + ":1,"
                    + getPrefix() + "Foo (Bar)=1,Bar (Har)=2],[PRE"
                    + getBaseString() + ":2," + getPrefix()
                    + "Goo (gle)=3,Hot (Cakes)=4]");
        }
    }

    @Test
    public void testCombineSubSub()
    {
        if (isSubAllowed())
        {
            runSimpleRoundRobin("PREMULT:2,[!PRE" + getBaseString() + ":1,"
                    + getPrefix() + "Foo (Bar)=1],[!PRE" + getBaseString() + ":1,"
                    + getPrefix() + "Goo (gle)=2]", "!PRE" + getBaseString()
                    + ":1," + getPrefix() + "Foo (Bar)=1,Goo (gle)=2");
        }
    }

    @Test
    public void testCombineSubNegativeSub()
    {
        if (isSubAllowed())
        {
            runSimpleRoundRobin("!PREMULT:2,[!PRE" + getBaseString() + ":1,"
                    + getPrefix() + "Foo (Bar)=1],[!PRE" + getBaseString() + ":1,"
                    + getPrefix() + "Goo (gle)=2]", "PRE" + getBaseString() + ":1,"
                    + getPrefix() + "Foo (Bar)=1,Goo (gle)=2");
        }
    }

    @Test
    public void testNoCombineSubNegativeSub()
    {
        if (isSubAllowed())
        {
            runRoundRobin("PREMULT:1,[!PRE" + getBaseString() + ":1,"
                    + getPrefix() + "Foo (Bar)=1],[!PRE" + getBaseString() + ":1,"
                    + getPrefix() + "Goo (gle)=3]");
        }
    }

    @Test
    public void testNoCombineMultSub()
    {
        if (isSubAllowed())
        {
            runRoundRobin("PREMULT:2,[PRE" + getBaseString() + ":1,"
                    + getPrefix() + "Foo (Bar)=1,Bar (Hoo)=2],[PRE"
                    + getBaseString() + ":1," + getPrefix() + "Goo (gle)=3,Hot=4]");
        }
    }

    @Test
    public void testHigherSub()
    {
        if (isSubAllowed())
        {
            runRoundRobin("PRE" + getBaseString() + ":1," + getPrefix()
                    + "Foo (Bar)=3");
        }
    }

    @Test
    public void testBothMultipleSub()
    {
        if (isSubAllowed())
        {
            runRoundRobin("PRE" + getBaseString() + ":2," + getPrefix()
                    + "Foo (Har)=3,Bar (Hoo)=5,Goo (gle)=6");
        }
    }

    @Test
    public void testSubComplex()
    {
        if (isTypeAllowed() && isSubAllowed())
        {
            runRoundRobin("PRE" + getBaseString() + ":3," + getPrefix()
                    + "Foo (Bar)=3,TYPE.Bar=4");
        }
    }

}
