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
package pcgen.cdom.formula.scope;

import java.util.Optional;

import pcgen.base.util.FormatManager;
import pcgen.core.PCStat;
import pcgen.rules.context.LoadContext;

/**
 * Defines a Scope that covers variables local to PCStat objects
 */
public class StatScope implements PCGenScope
{

	/**
	 * The parent of this scope (once loaded)
	 */
	private Optional<PCGenScope> parent;

	@Override
	public String getName()
	{
		return "STAT";
	}

	@Override
	public Optional<PCGenScope> getParentScope()
	{
		return parent;
	}

	/**
	 * Sets the parent PCGenScope for this PCStatScope.
	 * 
	 * @param parent
	 *            The parent PCGenScope for this PCStatScope
	 */
	public void setParent(PCGenScope parent)
	{
		this.parent = Optional.of(parent);
	}

	@Override
	public FormatManager<PCStat> getFormatManager(LoadContext context)
	{
		return context.getReferenceContext().getManufacturer(PCStat.class);
	}
}
