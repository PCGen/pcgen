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
package pcgen.cdom.helper;

import pcgen.cdom.base.CDOMObject;
import pcgen.core.PlayerCharacter;

/**
 * A AbstractSimpleProfProvider is an object that provides proficiency based on
 * a single proficiency.
 * <p>
 * Classes that extend this class are primary used at runtime to grant
 * individual proficiencies based on application of a CHOOSE to an AUTO: token
 * using %LIST.
 *
 * @param <T> The type of Proficiency (CDOMObject) that this
 *            AbstractSimpleProfProvider provides
 */
public abstract class AbstractSimpleProfProvider<T extends CDOMObject> implements ProfProvider<T>
{

    /**
     * The single proficiency that this AbstractSimpleProfProvider provides
     */
    private final T prof;

    /**
     * Constructs a new AbstractSimpleProfProvider that provides proficiency
     * based on a the given proficiency
     *
     * @param proficiency The proficiency that this AbstractSimpleProfProvider provides
     */
    public AbstractSimpleProfProvider(T proficiency)
    {
        prof = proficiency;
    }

    /**
     * Returns true if this AbstractSimpleProfProvider provides the given
     * proficiency.
     *
     * @param proficiency The proficiency to be tested to see if this
     *                    AbstractSimpleProfProvider provides the given proficiency
     * @return true if this AbstractSimpleProfProvider provides the given
     * proficiency; false otherwise.
     */
    @Override
    public boolean providesProficiency(T proficiency)
    {
        return prof.equals(proficiency);
    }

    /**
     * Returns true, as AbstractSimpleProfProvider is not a conditional object
     * (never contains Prerequisites)
     *
     * @param playerCharacter The <tt>PlayerCharacter</tt> to test (ignored)
     * @return true, as AbstractSimpleProfProvider is not a conditional object
     */
    @Override
    @SuppressWarnings("PMD.EmptyMethodInAbstractClassShouldBeAbstract")
    public boolean qualifies(PlayerCharacter playerCharacter, Object owner)
    {
        return true;
    }

    /**
     * Returns false, as AbstractSimpleProfProvider never grants proficiency
     * based on Equipment TYPE (AbstractSimpleProfProvider only contains a
     * single proficiency object)
     *
     * @param type The TYPE of Equipment to be tested to see if this
     *             AbstractSimpleProfProvider provides proficiency with the given
     *             Equipment TYPE (ignored)
     * @return false, as AbstractSimpleProfProvider never grants proficiency
     * based on Equipment TYPE
     */
    @Override
    @SuppressWarnings("PMD.EmptyMethodInAbstractClassShouldBeAbstract")
    public boolean providesEquipmentType(String type)
    {
        return false;
    }

    /**
     * Returns the LST format for this AbstractSimpleProfProvider. Provided
     * primarily to allow the Token/Loader system to properly unparse the
     * AbstractSimpleProfProvider.
     *
     * @return The LST format of this AbstractSimpleProfProvider
     */
    @Override
    public String getLstFormat()
    {
        return prof.getKeyName();
    }

    protected boolean hasSameProf(AbstractSimpleProfProvider<T> aspp)
    {
        return prof.equals(aspp.prof);
    }
}
