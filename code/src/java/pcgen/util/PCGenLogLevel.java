/*
 * Copyright 2007 (C) James Dempsey
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

package pcgen.util;

import java.util.logging.Level;

/**
 * {@code PCGenLogLevel} defines PCGen's custom logging levels.
 */
public final class PCGenLogLevel extends Level
{
    /**
     * Logging level for LST errors such as syntax errors.
     */
    public static final PCGenLogLevel LST_ERROR = new PCGenLogLevel("LSTERROR", 950);
    /**
     * Logging level for LST warnings such as deprectaed syntax use.
     */
    public static final PCGenLogLevel LST_WARNING = new PCGenLogLevel("LSTWARN", 850);
    /**
     * Logging level for LST information such as references to missing items in PRE or CHOOSE tags.
     */
    public static final PCGenLogLevel LST_INFO = new PCGenLogLevel("LSTINFO", 750);

    private PCGenLogLevel(String arg0, int arg1)
    {
        super(arg0, arg1);
    }

}
