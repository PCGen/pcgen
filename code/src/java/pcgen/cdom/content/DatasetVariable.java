/*
 * Copyright 2014 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.content;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pcgen.base.formula.base.LegalScope;
import pcgen.base.util.FormatManager;

/**
 * A DatasetVariable is a variable within the new formula system, as defined by
 * the data.
 */
public class DatasetVariable extends UserContent
{

	/**
	 * A Pattern indicating a legal name for a variable
	 */
	private static final Pattern ISLEGAL = Pattern
		.compile("\\A[A-Za-z]\\w*\\z");

	/**
	 * The format of this variable.
	 */
	private FormatManager<?> format;

	/**
	 * The scope in which the variable is legal.
	 */
	private LegalScope scope;

	@Override
	public String getDisplayName()
	{
		return getKeyName();
	}

	/**
	 * Sets the scope in which the variable is legal.
	 * 
	 * @param scope
	 *            The scope in which the variable is legal
	 */
	public void setScope(LegalScope scope)
	{
		this.scope = Objects.requireNonNull(scope);
	}

	/**
	 * Returns the scope in which the variable is legal.
	 * 
	 * @return The scope in which the variable is legal
	 */
	public LegalScope getScope()
	{
		return scope;
	}

	/**
	 * Returns true if the given proposed name matches the legal pattern of
	 * variable names. The pattern the name must match is: (1) Must start with a
	 * letter (2) Must have only A-Z a-z 0-9 and underscore.
	 * 
	 * @param proposedName
	 *            the proposed name of a variable
	 * @return true if the given proposed name matches the legal pattern of
	 *         variable names; false otherwise
	 */
	public static boolean isLegalName(String proposedName)
	{
		Matcher m = ISLEGAL.matcher(proposedName);
		return m.find();
	}

	/**
	 * Returns the Format for this DatasetVariable.
	 * 
	 * @return The Format for this DatasetVariable
	 */
	public FormatManager<?> getFormat()
	{
		return format;
	}

	/**
	 * Sets the Format for this DatasetVariable.
	 * 
	 * @param format
	 *            The Format for this DatasetVariable
	 */
	public void setFormat(FormatManager<?> format)
	{
		this.format = Objects.requireNonNull(format);
	}
}
