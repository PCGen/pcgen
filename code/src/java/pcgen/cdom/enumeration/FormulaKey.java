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
package pcgen.cdom.enumeration;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import pcgen.base.enumeration.TypeSafeConstant;
import pcgen.base.formula.Formula;
import pcgen.base.util.CaseInsensitiveMap;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.formula.FixedSizeFormula;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.core.SizeAdjustment;
import pcgen.core.analysis.SizeUtilities;

/**
 * This Class is a Type Safe Constant. It is designed to act as an index to a
 * specific Formula item within a CDOMObject.
 * <p>
 * *Important*: This should NOT be used to store items from the DEFINE: token,
 * as those are Variables that should be stored using VariableKey
 */
@SuppressWarnings({"PMD.ClassWithOnlyPrivateConstructorsShouldBeFinal", "checkstyle:FinalClass"})
public class FormulaKey implements TypeSafeConstant
{

    /**
     * This Map contains the mappings from Strings to the Type Safe Constant
     */
    private static CaseInsensitiveMap<FormulaKey> typeMap = new CaseInsensitiveMap<>();

    /**
     * This is used to provide a unique ordinal to each constant in this class
     */
    private static int ordinalCount = 0;

    public static final FormulaKey LEVEL_ADJUSTMENT = getConstant("LEVEL_ADJUSTMENT");

    public static final FormulaKey START_SKILL_POINTS = getConstant("START_SKILL_POINTS");

    public static final FormulaKey COST = getConstant("COST");

    public static final FormulaKey BASECOST = getConstant("BASECOST");

    public static final FormulaKey PAGE_USAGE = getConstant("PAGE_USAGE");

    public static final FormulaKey CR = getConstant("CR");

    public static final FormulaKey CRMOD = getConstant("CRMOD");

    public static final FormulaKey SELECT = getConstant("SELECT", FormulaFactory.ONE);

    public static final FormulaKey NUMCHOICES = getConstant("NUMCHOICES");

    public static final FormulaKey SIZE;

    public static final FormulaKey STAT_MOD = getConstant("STAT_MOD");

    public static final FormulaKey SKILL_POINTS_PER_LEVEL = getConstant("SKILL_POINTS_PER_LEVEL");

    /*
     * TODO Okay, this is a hack.
     */

    static
    {
        SIZE = new FormulaKey("SIZE", FormulaFactory.ZERO)
        {
            @Override
            public Formula getDefault()
            {
                SizeAdjustment def = SizeUtilities.getDefaultSizeAdjustment();
                CDOMDirectSingleRef<SizeAdjustment> ref = CDOMDirectSingleRef.getRef(def);
                return new FixedSizeFormula(ref);
            }

        };
        typeMap.put(SIZE.toString(), SIZE);
    }

    /**
     * The name of this Constant
     */
    private final String fieldName;

    private final Formula defaultValue;

    /**
     * The ordinal of this Constant
     */
    private final int ordinal;

    private FormulaKey(String name, Formula def)
    {
        Objects.requireNonNull(name, "Name for FormulaKey cannot be null");
        Objects.requireNonNull(def, "Formula for FormulaKey cannot be null");
        ordinal = ordinalCount++;
        fieldName = name;
        defaultValue = def;
    }

    /**
     * Converts this Constant to a String (returns the name of this Constant)
     *
     * @return The string representation (name) of this Constant
     */
    @Override
    public String toString()
    {
        return fieldName;
    }

    /**
     * Gets the ordinal of this Constant
     */
    @Override
    public int getOrdinal()
    {
        return ordinal;
    }

    public Formula getDefault()
    {
        return defaultValue;
    }

    /**
     * Returns the constant for the given String (the search for the constant is
     * case insensitive). If the constant does not already exist, a new Constant
     * is created with the given String as the name of the Constant.
     *
     * @param name The name of the constant to be returned
     * @return The Constant for the given name
     */
    public static FormulaKey getConstant(String name)
    {
        FormulaKey key = typeMap.get(name);
        if (key == null)
        {
            key = new FormulaKey(name, FormulaFactory.ZERO);
            typeMap.put(name, key);
        }
        return key;
    }

    /**
     * Returns the constant for the given String (the search for the constant is
     * case insensitive). If the constant does not already exist, a new Constant
     * is created with the given String as the name of the Constant.
     *
     * @param name    The name of the constant to be returned
     * @param formula The Formula to be used as the default value if the FormulaKey
     *                is not set
     * @return The Constant for the given name
     */
    public static FormulaKey getConstant(String name, Formula formula)
    {
        FormulaKey key = typeMap.get(name);
        if (key == null)
        {
            key = new FormulaKey(name, formula);
            typeMap.put(name, key);
        }
        return key;
    }

    /**
     * Returns the constant for the given String (the search for the constant is
     * case insensitive). If the constant does not already exist, an
     * IllegalArgumentException is thrown.
     *
     * @param name The name of the constant to be returned
     * @return The Constant for the given name
     * @throws IllegalArgumentException if the given String is not a previously defined FormulaKey
     */
    public static FormulaKey valueOf(String name)
    {
        FormulaKey key = typeMap.get(name);
        if (key == null)
        {
            throw new IllegalArgumentException(name + " is not a previously defined FormulaKey");
        }
        return key;
    }

    /**
     * Returns a Collection of all of the Constants in this Class.
     * <p>
     * This collection maintains a reference to the Constants in this Class, so
     * if a new Constant is created, the Collection returned by this method will
     * be modified. (Beware of ConcurrentModificationExceptions)
     *
     * @return a Collection of all of the Constants in this Class.
     */
    public static Collection<FormulaKey> getAllConstants()
    {
        return Collections.unmodifiableCollection(typeMap.values());
    }

    /**
     * Clears all of the Constants in this Class (forgetting the mapping from
     * the String to the Constant).
     */
    /*
     * CONSIDER Need to consider the ramifications of this on TypeSafeMap, since
     * this does not (and really cannot) reset the ordinal count... Does this
     * method need to be renamed, such that it is clearConstantMap? - Tom
     * Parker, Feb 28, 2007
     */
    public static void clearConstants()
    {
        typeMap.clear();
    }

}
