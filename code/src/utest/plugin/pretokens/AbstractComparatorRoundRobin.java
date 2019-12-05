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

public abstract class AbstractComparatorRoundRobin extends
        AbstractPreRoundRobin
{

    public abstract String getBaseString();

    public abstract boolean isBaseAllowed();

    @Override
    public void runPositiveRoundRobin(String s)
    {
        super.runPositiveRoundRobin("PRE" + getBaseString() + "GT:" + s);
        super.runPositiveRoundRobin("PRE" + getBaseString() + "GTEQ:" + s);
        super.runPositiveRoundRobin("PRE" + getBaseString() + "LT:" + s);
        super.runPositiveRoundRobin("PRE" + getBaseString() + "LTEQ:" + s);
        super.runPositiveRoundRobin("PRE" + getBaseString() + "NEQ:" + s);
        super.runPositiveRoundRobin("PRE" + getBaseString() + "EQ:" + s);
        if (isBaseAllowed())
        {
            runSimpleRoundRobin("PRE" + getBaseString() + ":" + s, "PRE"
                    + getBaseString() + "GTEQ:" + s);
        }
    }

    @Override
    public void runNegativeRoundRobin(String s)
    {
        runSimpleRoundRobin("!PRE" + getBaseString() + "GT:" + s, "PRE"
                + getBaseString() + "LTEQ:" + s);
        runSimpleRoundRobin("!PRE" + getBaseString() + "GTEQ:" + s, "PRE"
                + getBaseString() + "LT:" + s);
        runSimpleRoundRobin("!PRE" + getBaseString() + "LT:" + s, "PRE"
                + getBaseString() + "GTEQ:" + s);
        runSimpleRoundRobin("!PRE" + getBaseString() + "LTEQ:" + s, "PRE"
                + getBaseString() + "GT:" + s);
        runSimpleRoundRobin("!PRE" + getBaseString() + "NEQ:" + s, "PRE"
                + getBaseString() + "EQ:" + s);
        runSimpleRoundRobin("!PRE" + getBaseString() + "EQ:" + s, "PRE"
                + getBaseString() + "NEQ:" + s);
        if (isBaseAllowed())
        {
            runSimpleRoundRobin("!PRE" + getBaseString() + ":" + s, "PRE"
                    + getBaseString() + "LT:" + s);
        }
    }

}
