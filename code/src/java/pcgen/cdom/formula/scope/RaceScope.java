/*
 * Copyright 2019 (C) Tom Parker <thpr@users.sourceforge.net>
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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import pcgen.base.formula.base.ImplementedScope;
import pcgen.base.util.FormatManager;
import pcgen.core.Race;
import pcgen.rules.context.LoadContext;

/**
 * Defines a Scope that covers variables local to Race objects
 */
public class RaceScope implements PCGenScope
{

	/**
	 * The scopes this scope draws from (once loaded)
	 */
	private List<ImplementedScope> drawsFrom = Collections.emptyList();

	@Override
	public String getName()
	{
		return "PC.RACE";
	}

	@Override
	public boolean isGlobal()
	{
		return false;
	}

	@Override
	public List<ImplementedScope> drawsFrom()
	{
		return drawsFrom;
	}

	/**
	 * Sets the parent PCGenScope for this RaceScope.
	 *
	 * @param parent
	 *            The parent PCGenScope for this RaceScope
	 */
	public void setParent(PCGenScope parent)
	{
		this.drawsFrom = List.of(parent);
	}

	@Override
	public Optional<FormatManager<?>> getFormatManager(LoadContext context)
	{
		return Optional.of(context.getReferenceContext().getManufacturer(Race.class));
	}
}
