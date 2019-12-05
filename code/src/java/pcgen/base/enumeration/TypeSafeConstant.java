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
 *
 */
package pcgen.base.enumeration;

/**
 * A TypeSafeConstant is an object which will provide a unique integer
 * identifier to every instance of that class (not unique to every
 * TypeSafeConstant).
 * <p>
 * As an example, If Classes A and B both extend TypeSafeConstant, then there
 * may be an instance of Class A that has ordinal 1 and an instance of Class B
 * that has ordinal 1, but only one instance of each class may have any ordinal.
 * (Two instances of Class A may not share ordinal 1 and still respect the
 * TypeSafeConstant interface)
 * <p>
 * This emulates behavior by the enum system in Java 1.5+. Note that the
 * identifier should be unique per virtual machine; thus serialization and other
 * forms of persistence of classes must ensure the uniqueness of identifiers of
 * a TypeSafeConstant.
 * <p>
 * It is considered good behavior for a TypeSafeConstant to begin the ordinal
 * count at zero. There may be an expectation by classes that use
 * TypeSafeConstants that expect this behavior. In that case, such behavioral
 * limitations should be documented in the TypeSafeConstant-using class.
 */
@FunctionalInterface
public interface TypeSafeConstant
{

    /**
     * Returns the unique ordinal for the instance of the TypeSafeConstant
     * class.
     *
     * @return An integer identifier unique to this instance of the class.
     */
    int getOrdinal();

}
