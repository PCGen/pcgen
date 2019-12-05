/*
 * Copyright (c) 2006 Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.formula;

import pcgen.core.Equipment;
import pcgen.core.PlayerCharacter;

/**
 * A Formula is a mathematical formula which requires a context to resolve.
 */
public interface Formula
{
    /**
     * Resolves the formula relative to the given PlayerCharacter and source
     * object. Variables are taken from the active objects on the
     * PlayerCharacter, and any required context beyond the PlayerCharacter is
     * taken from the source object. Formatting of the source String may vary.
     * <p>
     * It is assumed the PlayerCharacter is not null. If no PlayerCharacter
     * exists in the current context, use resolveStatic. If the formula is not
     * static and there is no PlayerCharacter in the context, then the Formula
     * cannot be resolved.
     *
     * @param pc     The (non-null) PlayerCharacter relative to which the Formula
     *               should be resolved.
     * @param source The source object of the Formula, for purposes of resolution.
     * @return The Number indicating the result of the Formula when resolved in
     * the given context.
     */
    Number resolve(PlayerCharacter pc, String source);

    /**
     * Resolves the static formula relative to the given source object.
     * <p>
     * If this method is called on a Formula for which isStatic is false, then
     * this method reserves the right to throw an exception of some form.
     *
     * @return The Number indicating the result of the (static) Formula.
     */
    Number resolveStatic();

    /**
     * Resolves the formula relative to the given Equipment, EquipmentHead,
     * PlayerCharacter and source object. Variables are taken from the active
     * objects on the Equipment and PlayerCharacter, and any required context
     * beyond the PlayerCharacter is taken from the source object. Formatting of
     * the source String may vary.
     *
     * @param equipment The Equipment relative to which the Formula should be
     *                  resolved.
     * @param primary   True if the primary head of the given Equipment should be used
     *                  for resolution, false if the secondary head should be used for
     *                  resolution.
     * @param pc        The PlayerCharacter relative to which the Formula should be
     *                  resolved.
     * @param source    The source object of the Formula, for purposes of resolution.
     * @return The Number indicating the result of the Formula when resolved in
     * the given context.
     */
    Number resolve(Equipment equipment, boolean primary, PlayerCharacter pc, String source);

    /**
     * Returns true if the underlying Formula is known to be static.
     * <p>
     * Implementation of this method may vary, and a static Formula may return
     * true or false to this method. The only requirement is that if true is
     * returned, then the Formula must be static.
     *
     * @return true if the underlying Formula is known to be static; false
     * otherwise
     */
    boolean isStatic();

    /**
     * Returns true if the underlying Formula is valid. An invalid Formula may
     * return null or an Exception from either resolve method. (All other
     * behavior is not well defined if the formula returns false from
     * isValid()).
     *
     * @return true if the underlying Formula is valid; false otherwise
     */
    boolean isValid();

}
