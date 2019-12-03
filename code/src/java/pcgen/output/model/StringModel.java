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

import java.util.Objects;
import java.util.function.Supplier;

import freemarker.template.TemplateScalarModel;

/**
 * A StringModel wraps a String object into a TemplateScalarModel
 */
public class StringModel implements TemplateScalarModel, Supplier<String>
{
	/**
	 * The underlying String object
	 */
	private final String string;

	/**
	 * Constructs a new StringModel with the given underlying String
	 * 
	 * @param s
	 *            The String this StringModel wraps
	 */
	public StringModel(String s)
	{
		Objects.requireNonNull(s, "String cannot be null");
		this.string = s;
	}

	@Override
	public String getAsString() {
		return string;
	}

	@Override
	public String get()
	{
		return string;
	}

}
