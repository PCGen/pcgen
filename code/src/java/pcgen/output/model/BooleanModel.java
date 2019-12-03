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
package pcgen.output.model;

import java.util.function.Supplier;

import freemarker.template.TemplateBooleanModel;

/**
 * A BooleanModel wraps a boolean object into a TemplateScalarModel
 */
public class BooleanModel implements TemplateBooleanModel, Supplier<Boolean>
{
	/**
	 * The underlying boolean object
	 */
	private final boolean bool;

	/**
	 * Constructs a new BooleanModel with the given underlying boolean
	 * 
	 * @param b
	 *            The boolean this BooleanModel wraps
	 */
	public BooleanModel(boolean b)
	{
		this.bool = b;
	}

	@Override
	public boolean getAsBoolean() {
		return bool;
	}

	@Override
	public Boolean get()
	{
		return bool;
	}

}
