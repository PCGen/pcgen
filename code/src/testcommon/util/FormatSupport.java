/*
 * (c) Copyright 2019 Thomas Parker thpr@users.sourceforge.net
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */
package util;

import java.util.Objects;

import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.math.OrderedPair;
import pcgen.base.util.FormatManager;
import pcgen.rules.context.LoadContext;
import pcgen.rules.context.VariableContext;

/**
 * A utility class to support formats in the test system.
 */
public class FormatSupport
{
	/**
	 * Add an object called "None" as the default for the given format.
	 * 
	 * @param context
	 *            The LoadContext in which the default should be set
	 * @param fmtManager
	 *            The FormatManager for the format to be set
	 */
	public static <T> void addNoneAsDefault(LoadContext context,
		FormatManager<T> fmtManager)
	{
		T noneObject = fmtManager.convert("NONE");
		Objects.requireNonNull(noneObject,
			"NONE was never constructed for " + fmtManager.getIdentifierType());
		context.getVariableContext().addDefault(fmtManager, () -> noneObject);
	}

	/**
	 * Adds the "basic" defaults so that tests can be safely run.
	 * 
	 * @param context
	 *            The LoadContext in which the defaults should be set
	 */
	public static void addBasicDefaults(LoadContext context)
	{
		VariableContext varContext = context.getVariableContext();
		varContext.addDefault(FormatUtilities.ORDEREDPAIR_MANAGER,
			() -> new OrderedPair(0, 0));
		varContext.addDefault(FormatUtilities.NUMBER_MANAGER, () -> 0);
		varContext.addDefault(FormatUtilities.STRING_MANAGER, () -> "");
		varContext.addDefault(FormatUtilities.BOOLEAN_MANAGER, () -> false);
	}

}
