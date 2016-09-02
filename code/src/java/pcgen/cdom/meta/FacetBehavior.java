/*
 * Copyright (c) Thomas Parker, 2013.
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
package pcgen.cdom.meta;

import java.util.Objects;

public final class FacetBehavior
{

	public static final FacetBehavior MODEL = new FacetBehavior("Model");
	public static final FacetBehavior INPUT = new FacetBehavior("Input");
	public static final FacetBehavior CONDITIONAL = new FacetBehavior("Conditional");
	public static final FacetBehavior CONDITIONAL_GRANTED = new FacetBehavior("Conditional-Granted");
//	public static final CorePerspective SELECTION = new CorePerspective("Selection");
//	public static final CorePerspective CONDITIONAL_SELECTION = new CorePerspective("Conditional Selection");

	private String type;

	private FacetBehavior(String type)
	{
		this.type = Objects.requireNonNull(type);
	}

	@Override
	public String toString()
	{
		return type;
	}

}
