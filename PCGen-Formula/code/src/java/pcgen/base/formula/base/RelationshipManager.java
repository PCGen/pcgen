/*
 * Copyright 2015-20 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.formula.base;

/**
 * A RelationshipManager tracks relationships of ImplementedScope objects.
 * 
 * Note: If a complete set of ImplementedScope objects is not loaded (meaning some of the
 * parents are not themselves loaded), then certain behaviors (like getScope) are not
 * guaranteed to properly behave.
 * 
 * Relationship between two ImplementedScope objects extends to two forms of ambiguity:
 * (1) When in a known scope, a variable name should be unique (regardless of being in the
 * local scope or implied from a parent scope) (2) When in an unknown child scope, the
 * presence of a variable should imply the full parent scope hierarchy. Said another way,
 * in any given context of interpreting a variable name, there should always be one and
 * only one possible interpretation. Given potential overlaps, this can be a subtle
 * problem. The following defines some rules for when a relationship exists between two
 * scopes, and thus they can't share a variable.
 * 
 * If a variable name is defined for an existing parent or child (both recursively) of a
 * ImplementedScope, then adding that variable name to that ImplementedScope should be
 * prohibited. Otherwise, ambiguity (1) above would be violated.
 */
public interface RelationshipManager
{
	/**
	 * Returns true if two scopes are related. They are related if the presence of a
	 * matching variable name would produce an ambiguity (as described in the description
	 * of this interface).
	 * 
	 * @param firstScope
	 *            The first scope to be checked
	 * @param secondScope
	 *            The second scope to be checked
	 * @return true if the two ImplementedScope objects are related; false otherwise
	 */
	public boolean isRelated(ImplementedScope firstScope, ImplementedScope secondScope);
}
