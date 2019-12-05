/*
 * Copyright 2001 (C) Greg Bingleman <byngl@hotmail.com>
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
package pcgen.core;

import pcgen.facade.core.GameModeDisplayFacade;

/**
 * Handles game modes.
 */
public final class GameModeDisplay implements Comparable<Object>, GameModeDisplayFacade
{
    private GameMode gameMode;

    /**
     * Creates a new instance of GameModeMenuEntry.
     *
     * @param mode the mode name
     */
    public GameModeDisplay(final GameMode mode)
    {
        gameMode = mode;
    }

    /**
     * Get the game mode
     *
     * @return gameMode
     */
    @Override
    public GameMode getGameMode()
    {
        return gameMode;
    }

    /**
     * Get the menu entry of the game mode
     *
     * @return menuEntry
     */
    @Override
    public String toString()
    {
        return gameMode.getDisplayName();
    }

    @Override
    public int compareTo(final Object obj)
    {
        if (obj != null)
        {
            final int iOrder = ((GameModeDisplay) obj).getGameMode().getDisplayOrder();

            if (iOrder < gameMode.getDisplayOrder())
            {
                return 1;
            } else if (iOrder > gameMode.getDisplayOrder())
            {
                return -1;
            }

            //
            // Order matches, so put in alphabetical order
            //
            // should throw a ClassCastException for non-PObjects,
            // like the Comparable interface calls for
            return gameMode.getDisplayName()
                    .compareToIgnoreCase(((GameModeDisplay) obj).getGameMode().getDisplayName());
        }
        return 1;
    }
}
