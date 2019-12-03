/*
 * Copyright 2018 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.formula.scope;

import java.util.Optional;

import pcgen.base.formula.base.LegalScope;
import pcgen.base.util.FormatManager;
import pcgen.rules.context.LoadContext;

/**
 * A PCGenScope is an expanded LegalScope for use in PCGen that can also provide the
 * FormatManager related to the Scope.
 */
public interface PCGenScope extends LegalScope
{
	@Override
    Optional<PCGenScope> getParentScope();

	/**
	 * Returns the FormatManager used to process objects that are within this PCGenScope.
	 * 
	 * @param context
	 *            The LoadContext used to resolve the actual FormatManager
	 * @return The FormatManager used to process objects that are within this PCGenScope
	 */
    Optional<FormatManager<?>> getFormatManager(LoadContext context);
}
