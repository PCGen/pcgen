/*
 * Copyright 2005 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.enumeration;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import pcgen.base.util.CaseInsensitiveMap;
import pcgen.cdom.base.Constants;
import pcgen.core.Globals;

/**
 * This is a Typesafe enumeration of legal Integer Characteristics of an object.
 * It is designed to act as an index to a specific integers within a CDOMObject.
 * <p>
 * An optional "default value" can be provided (the default is provided when
 * getSafe(IntegerKey) is called in CDOMObject). The default "default value" is
 * zero.
 */
@SuppressWarnings({"PMD.ClassWithOnlyPrivateConstructorsShouldBeFinal", "checkstyle:FinalClass"})
public class IntegerKey
{

    /**
     * This Map contains the mappings from Strings to the Type Safe Constant
     */
    private static final CaseInsensitiveMap<IntegerKey> typeMap = new CaseInsensitiveMap<>();

    /**
     * @deprecated due to EQACCHECK CodeControl
     */
    @Deprecated
    public static final IntegerKey AC_CHECK = getConstant("AC_CHECK");

    public static final IntegerKey ADD_SPELL_LEVEL = getConstant("ADD_SPELL_LEVEL");

    public static final IntegerKey BASE_QUANTITY = getConstant("BASE_QUANTITY", 1);

    public static final IntegerKey BONUS_CLASS_SKILL_POINTS = getConstant("BONUS_CLASS_SKILL_POINTS");

    public static final IntegerKey CASTING_THRESHOLD = getConstant("CASTING_THRESHOLD");

    public static final IntegerKey COST = getConstant("COST");

    /**
     * @deprecated due to CRITMULT CodeControl
     */
    @Deprecated
    public static final IntegerKey CRIT_MULT = getConstant("CRIT_MULT");

    /**
     * @deprecated due to CRITRANGE CodeControl
     */
    @Deprecated
    public static final IntegerKey CRIT_RANGE = getConstant("CRIT_RANGE");

    /**
     * @deprecated due to EDR CodeControl
     */
    @Deprecated
    public static final IntegerKey EDR = getConstant("EDR");

    public static final IntegerKey HANDS = getConstant("HANDS", 1);

    /**
     * @deprecated due to HANDS CodeControl
     */
    @Deprecated
    public static final IntegerKey CREATURE_HANDS = getConstant("CREATURE_HANDS", 2);

    public static final IntegerKey HIT_DIE = getConstant("HIT_DIE");

    public static final IntegerKey KNOWN_SPELLS_FROM_SPECIALTY = getConstant("KNOWN_SPELLS_FROM_SPECIALTY");

    /**
     * @deprecated due to LEGS CodeControl
     */
    @Deprecated
    public static final IntegerKey LEGS = getConstant("LEGS", 2);

    public static final IntegerKey LEVEL = getConstant("LEVEL");

    public static final IntegerKey LEVEL_LIMIT = getConstant("LEVEL_LIMIT", Constants.NO_LEVEL_LIMIT);

    public static final IntegerKey LEVELS_PER_FEAT = getConstant("LEVELS_PER_FEAT");

    public static final IntegerKey MAX_CHARGES = getConstant("MAX_CHARGES");

    /**
     * @deprecated due to MAXDEX CodeControl
     */
    @Deprecated
    public static final IntegerKey MAX_DEX = getConstant("MAX_DEX", Constants.MAX_MAXDEX);

    public static final IntegerKey MIN_CHARGES = getConstant("MIN_CHARGES");

    public static final IntegerKey NONPP = getConstant("NONPP");

    public static final IntegerKey NUM_PAGES = getConstant("NUM_PAGES");

    public static final IntegerKey PLUS = getConstant("PLUS");

    public static final IntegerKey PROHIBIT_COST = getConstant("PROHIBIT_COST");

    /**
     * @deprecated due to RANGE code control
     */
    @Deprecated
    public static final IntegerKey RANGE = getConstant("RANGE");

    public static final IntegerKey CAMPAIGN_RANK = getConstant("CAMPAIGN_RANK", 9);

    /**
     * @deprecated due to PCREACH and EQREACH CodeControl
     */
    @Deprecated
    public static final IntegerKey REACH = getConstant("REACH", 5);

    /**
     * @deprecated due to EQREACH CodeControl
     */
    @Deprecated
    public static final IntegerKey REACH_MULT = getConstant("REACH_MULT", 1);

    public static final IntegerKey SLOTS = getConstant("SLOTS", 1);

    /**
     * @deprecated due to EQSPELLFAILURE code control
     */
    @Deprecated
    public static final IntegerKey SPELL_FAILURE = getConstant("SPELL_FAILURE");

    public static final IntegerKey START_FEATS = getConstant("START_FEATS");

    public static final IntegerKey XP_COST = getConstant("XP_COST");

    public static final IntegerKey CONSECUTIVE = getConstant("CONSECUTIVE");

    public static final IntegerKey MAX_LEVEL = getConstant("MAX_LEVEL");

    public static final IntegerKey LEVEL_INCREMENT = getConstant("LEVEL_INCREMENT");

    public static final IntegerKey START_LEVEL = getConstant("START_LEVEL");

    public static final IntegerKey HD_MIN = getConstant("HD_MIN");

    public static final IntegerKey HD_MAX = getConstant("HD_MAX");

    public static final IntegerKey UMULT = getConstant("UMULT");

    public static final IntegerKey CONTAINER_REDUCE_WEIGHT = getConstant("CONTAINER_REDUCE_WEIGHT");

    public static final IntegerKey MIN_VALUE = getConstant("MIN_VALUE", 0);

    public static final IntegerKey MAX_VALUE = getConstant("MAX_VALUE", 1000);

    //Input value for SizeAdjustment ordering (in LST file)
    public static final IntegerKey SIZENUM = getConstant("SIZENUM");

    //Derived value for SizeAdjustment ordering (derived to be sequential)
    public static final IntegerKey SIZEORDER = getConstant("SIZEORDER");

    /*
     * TODO Okay, this is a hack. This should probably be a FormulaKey rather
     * than an IntegerKey in order to properly handle this strange delegation.
     * This works, but really doesn't meet the prescription of what one would
     * expect out of an IntegerKey
     */
    public static final IntegerKey INITIAL_SKILL_MULT;

    static
    {
        INITIAL_SKILL_MULT = new IntegerKey("INITIAL_SKILL_MULT", 0)
        {
            @Override
            public int getDefault()
            {
                return Globals.getSkillMultiplierForLevel(1);
            }

        };
        typeMap.put(INITIAL_SKILL_MULT.toString(), INITIAL_SKILL_MULT);
    }

    /**
     * This is used to provide a unique ordinal to each constant in this class
     */
    private static int ordinalCount = 0;

    /**
     * The name of this Constant
     */
    private final String fieldName;

    private final int defaultValue;

    /**
     * The ordinal of this Constant
     */
    private final int ordinal;

    private IntegerKey(String name, int def)
    {
        Objects.requireNonNull(name, "Name for IntegerKey cannot be null");
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
    public int getOrdinal()
    {
        return ordinal;
    }

    public int getDefault()
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
    public static IntegerKey getConstant(String name)
    {
        IntegerKey key = typeMap.get(name);
        if (key == null)
        {
            key = new IntegerKey(name, 0);
            typeMap.put(name, key);
        }
        return key;
    }

    /**
     * Returns the constant for the given String (the search for the constant is
     * case insensitive). If the constant does not already exist, a new Constant
     * is created with the given String as the name of the Constant.
     *
     * @param name         The name of the constant to be returned
     * @param defaultValue The value to be used as the default value if the IntegerKey is
     *                     not set
     * @return The Constant for the given name
     */
    public static IntegerKey getConstant(String name, int defaultValue)
    {
        IntegerKey key = typeMap.get(name);
        if (key == null)
        {
            key = new IntegerKey(name, defaultValue);
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
     * @throws IllegalArgumentException if the given String is not a previously defined IntegerKey
     */
    public static IntegerKey valueOf(String name)
    {
        IntegerKey key = typeMap.get(name);
        if (key == null)
        {
            throw new IllegalArgumentException(name + " is not a previously defined IntegerKey");
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
    public static Collection<IntegerKey> getAllConstants()
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
