/*
 * Copyright 2008 (C) James Dempsey
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
package plugin.jepcommands;

import static org.junit.Assert.assertEquals;

import java.util.Stack;

import pcgen.core.GameMode;
import pcgen.core.SettingsHandler;
import pcgen.core.SystemCollections;
import pcgen.persistence.GameModeFileLoader;

import org.junit.Before;
import org.junit.Test;
import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommandI;

/**
 * The Class {@code IsgamemodeCommandTest} is responsible for checking
 * that IsgamemodeCommand is working correctly. 
 * 
 * 
 */
public class IsgamemodeCommandTest
{
	@Before
	public void setUp()
	{
		final GameMode gamemode = new GameMode("3.5");
		GameModeFileLoader.addDefaultTabInfo(gamemode);
		SystemCollections.addToGameModeList(gamemode);
		SettingsHandler.setGame("3.5");
	}
    /**
     * Run isgamemode.
     * 
     * @param stack the stack
     */
    private static void runIsgamemode(final Stack<Object> stack)
    {
	    try
        {
	        final PostfixMathCommandI pCommand = new IsgamemodeCommand();
	        pCommand.run(stack);
        }
        catch (ParseException ignored)
        {
        }
    }

    /**
     * Test is game mode true.
     */
    @Test
    public void testIsGameModeTrue()
    {
        final Stack<Object>         s = new Stack<>();

        s.push("3.5");

        runIsgamemode(s);

        final Integer result = (Integer) s.pop();

        assertEquals("isgamemode(\"3.5\") returns 1", Integer.valueOf(1), result);
    }

    /**
     * Test is game mode false.
     */
    @Test
    public void testIsGameModeFalse()
    {
        final Stack<Object>         s = new Stack<>();

        s.push("3e");

        runIsgamemode(s);

        final Integer result = (Integer) s.pop();

	    assertEquals("isgamemode(\"3e\") returns 0", Integer.valueOf(0), result);
    }
}
