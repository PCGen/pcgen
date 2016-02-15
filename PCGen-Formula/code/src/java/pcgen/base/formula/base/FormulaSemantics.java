/*
 * Copyright 2015 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.formula.base;

import java.util.HashMap;
import java.util.Map;

/**
 * A FormulaSemantics is a class to capture Formula semantics.
 * 
 * This is designed, among other things, to report on whether a formula is
 * valid, and if valid the semantics of the Formula (what format it will
 * return).
 * 
 * In order to capture specific dependencies, a specific set of semantics
 * information should be loaded into this FormulaSemantics.
 * 
 * If a formula is valid, then this should contain a FormulaValidity for which
 * the isValid() method will return true. In such a case, this should not
 * contain a FormulaInvalidReport. When valid, this should contain a
 * FormulaFormat.
 * 
 * If a formula is not valid, then a FormulaSemantics must contain a
 * FormulaInvalidReport. This value should indicate with some precision the
 * issue with the Formula. Note that if there is more than one issue, only one
 * issue needs to be returned (fast fail is acceptable).
 */
public class FormulaSemantics
{

	/**
	 * The map of Semantics information.
	 */
	private Map<SemanticsKey<?>, Object> map = new HashMap<>();

	/**
	 * Inserts a new manager into this FormulaSemantics for the given
	 * SemanticsKey. The given object manages a set of semantic information for
	 * a Formula.
	 * 
	 * @param <T>
	 *            The class of the manager identified by the given SemanticsKey
	 * @param key
	 *            The SemanticsKey used to identify the manager of the semantic
	 *            information
	 * @param manager
	 *            The class that manages the semantics represented by the given
	 *            SemanticsKey
	 * @return The previous manager of the semantics represented by the given
	 *         SemanticsKey
	 */
	public <T> T setInfo(SemanticsKey<T> key, T manager)
	{
		return key.cast(map.put(key, manager));
	}

	/**
	 * Returns the object managing the semantics represented by the given
	 * SemanticsKey.
	 * 
	 * @param <T>
	 *            The class of the manager identified by the given SemanticsKey
	 * @param key
	 *            The SemanticsKey used to identify the manager of the semantics
	 * @return The object managing the semantics represented by the given
	 *         SemanticsKey
	 */
	public <T> T getInfo(SemanticsKey<T> key)
	{
		return key.cast(map.get(key));
	}

	/**
	 * Removes the managing object for the given SemanticsKey, returning that
	 * object to the caller. No further connection to the returned object is
	 * maintained by this FormulaSemantics for the given SemanticsKey.
	 * 
	 * @param <T>
	 *            The class of the manager identified by the given SemanticsKey
	 * @param key
	 *            The SemanticsKey used to identify the manager of the semantics
	 * @return The object managing the semantics represented by the given
	 *         SemanticsKey
	 */
	public <T> T removeInfo(SemanticsKey<T> key)
	{
		return key.cast(map.remove(key));
	}
}
