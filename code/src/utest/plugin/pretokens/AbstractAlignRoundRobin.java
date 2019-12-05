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

public abstract class AbstractAlignRoundRobin extends AbstractPreRoundRobin
{
    public abstract String getBaseString();

    public void testSimple()
    {
        runRoundRobin("PRE" + getBaseString() + ":LG");
    }

    public void testMultiple()
    {
        runRoundRobin("PRE" + getBaseString() + ":LG,LN,LE");
    }

    public void testNoCompress()
    {
        runRoundRobin("PREMULT:2,[PRE" + getBaseString() + ":NG],[PRE"
                + getBaseString() + ":LG]");
    }

}
