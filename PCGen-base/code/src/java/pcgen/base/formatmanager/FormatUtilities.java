/*
 * Copyright 2016 (C) Tom Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.base.formatmanager;

import java.util.Objects;

import pcgen.base.format.BooleanManager;
import pcgen.base.format.NumberManager;
import pcgen.base.format.OrderedPairManager;
import pcgen.base.format.StringManager;
import pcgen.base.math.OrderedPair;
import pcgen.base.util.FormatManager;

/**
 * FormatUtilities are utility methods for Format objects.
 */
public final class FormatUtilities
{

	/**
	 * An instance of a NumberManager, for widespread reuse
	 */
	public static final NumberManager NUMBER_MANAGER = new NumberManager();

	/**
	 * An instance of a StringManager, for widespread reuse
	 */
	public static final StringManager STRING_MANAGER = new StringManager();

	/**
	 * An instance of a BooleanManager, for widespread reuse
	 */
	public static final BooleanManager BOOLEAN_MANAGER = new BooleanManager();

	/**
	 * An instance of a OrderedPairManager, for widespread reuse
	 */
	public static final OrderedPairManager ORDEREDPAIR_MANAGER =
			new OrderedPairManager();

	/**
	 * An instance of a Number.class, for widespread reuse
	 */
	public static final Class<Number> NUMBER_CLASS = Number.class;

	/**
	 * An instance of a String.class, for widespread reuse
	 */
	public static final Class<String> STRING_CLASS = String.class;

	/**
	 * An instance of a Boolean.class, for widespread reuse
	 */
	public static final Class<Boolean> BOOLEAN_CLASS = Boolean.class;

	/**
	 * An instance of a OrderedPair.class, for widespread reuse
	 */
	public static final Class<OrderedPair> ORDEREDPAIR_CLASS = OrderedPair.class;

	private FormatUtilities()
	{
		//Don't construct utility class
	}

	/**
	 * Initializes the given SimpleFormatManagerLibrary with the known FormatManager
	 * objects in the base library.
	 * 
	 * @param library
	 *            The SimpleFormatManagerLibrary to be loaded with the known FormatManager
	 *            objects
	 */
	public static void loadDefaultFormats(SimpleFormatManagerLibrary library)
	{
		library.addFormatManager(NUMBER_MANAGER);
		library.addFormatManager(STRING_MANAGER);
		library.addFormatManager(BOOLEAN_MANAGER);
		library.addFormatManager(ORDEREDPAIR_MANAGER);
	}

	/**
	 * Initializes the given SimpleFormatManagerLibrary with default FormatManagerFactory
	 * objects in the base library.
	 * 
	 * @param library
	 *            The SimpleFormatManagerLibrary to be loaded with default
	 *            FormatManagerFactory objects
	 */
	public static void loadDefaultFactories(SimpleFormatManagerLibrary library)
	{
		library.addFormatManagerBuilder(new CompoundFormatFactory(',', '|'));
		library.addFormatManagerBuilder(new ArrayFormatFactory('\n', ','));
		library.addFormatManagerBuilder(new OptionalFormatFactory());
	}

	/**
	 * Returns the given FormatManager if it is a valid FormatManager. Validity means
	 * basic adherence to the FormatManager interface, meaning getIdentifierType(),
	 * getManagedClass(), and getComponentManager() may not return null.
	 * 
	 * @param fmtManager
	 *            The FormatManager to be checked to ensure it is valid
	 * @return The given FormatManager if it is valid.
	 * @throws NullPointerException
	 *             if the given FormatManager is not valid
	 */
	public static FormatManager<?> isValid(FormatManager<?> fmtManager)
	{
		String fmIdent = fmtManager.getIdentifierType();
		Class<?> fmFormat = fmtManager.getManagedClass();
		Objects.requireNonNull(fmIdent,
			"Cannot use a FormatManager with no identifier (was nominally for Class: "
				+ fmFormat + ")");
		Objects.requireNonNull(fmFormat,
			"Cannot use a FormatManager with no format (was nominally for Identifier: "
				+ fmIdent + ")");
		Objects.requireNonNull(fmtManager.getComponentManager(),
			"Cannot use a FormatManager with null component manager, "
				+ "must be Optional.empty() (was nominally for Identifier: "
				+ fmIdent + ")");
		return fmtManager;
	}
}
