/**
 * Copyright James Dempsey, 2011
 * <p>
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * <p>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.persistence.lst;

import java.net.URI;

import pcgen.core.GameMode;

/**
 * The Interface {@code EquipIconLstToken} defines a token
 * as being callable by the EquipIconLoader.
 */
public interface EquipIconLstToken extends LstToken
{
    /**
     * Parse the token
     *
     * @param gameMode
     * @param value
     * @return true if successful
     */
    boolean parse(GameMode gameMode, String value, URI source);

}
