/*
 * Copyright (c) 2010 Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.test;

/**
 * An InequalityTester is a method of checking two objects for equality.
 * <p>
 * This is done with a system other than .equals() since there may be exceptions
 * to true equality that we need to handle
 */
@FunctionalInterface
public interface InequalityTester
{
    /**
     * Tests the equality between two objects using this InequalityTester
     *
     * @param o1       The first object to be tested for equality
     * @param o2       The second object to be tested for equality
     * @param location The location of the objects, to assist with debugging
     * @return A String indicating how the items are not equal; null if equal
     */
    @SuppressWarnings("PMD.JUnit4TestShouldUseTestAnnotation")
    String testEquality(Object o1, Object o2, String location);
}
