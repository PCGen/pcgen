/*
 * Copyright 2013 (C) James Dempsey <jdempsey@users.sourceforge.net>
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
 *
 *
 */
package pcgen.persistence.lst;

import pcgen.core.system.MigrationRule;

/**
 * The Interface {@code MigrationLstToken} defines a token
 * as being callable by the MigrationLoader.
 */
public interface MigrationLstToken extends LstToken
{
    /**
     * Parse the token value
     *
     * @param migrationRule The migration rule we are building up.
     * @param value         The value of the token (not including the token string)
     * @param gameModeName  The name of the game mode the rule is for.
     * @return true if successful
     */
    boolean parse(MigrationRule migrationRule, String value, String gameModeName);

}
