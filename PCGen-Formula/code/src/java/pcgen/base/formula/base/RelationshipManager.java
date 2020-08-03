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
 * A RelationshipManager tracks relationships of DefinedScope objects.
 * 
 * Note: If a complete set of DefinedScope objects is not loaded (meaning some of the
 * parents are not themselves loaded), then certain behaviors (like getScope) are not
 * guaranteed to properly behave.
 * 
 * Relationship between two DefinedScope objects extends to two forms of ambiguity: (1)
 * When in a known scope, a variable name should be unique (regardless of being in the
 * local scope or implied from a parent scope) (2) When in an unknown child scope, the
 * presence of a variable should imply the full parent scope hierarchy. Said another way,
 * in any given context of interpreting a variable name, there should always be one and
 * only one possible interpretation. Given potential overlaps, this can be a subtle
 * problem. The following defines some rules for when a relationship exists between two
 * scopes, and thus they can't share a variable.
 * 
 * If a variable name is defined for an existing parent or child (both recursively) of a
 * DefinedScope, then adding that variable name to that DefinedScope should be prohibited.
 * Otherwise, ambiguity (1) above would be violated.
 * 
 * If a variable name is defined for any relative of a peer scope, it should also fail.
 * A.C and B.C are peer scopes of C, therefore, if a variable is defined in A.C, then it
 * can never be defined in B, B.C, or B.D or it can be considered ambiguous. This
 * prohibition follows from ambiguity (2) above.
 * 
 * Note that this latter rule also creates interesting dependencies between global scopes.
 * If a variable E exists in scopes F and G, and there are peer subscopes F.H and G.H,
 * then any variable defined in F cannot be defined in G. This is because when that
 * variable name is encountered, in an object defined by H, that object should be able to
 * determine whether it is being interpreted in F.H or G.H. This also follows from (2)
 * above.
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
	 * @return true if the two DefinedScope objects are related; false otherwise
	 */
	public boolean isRelated(ImplementedScope firstScope, ImplementedScope secondScope);
}
