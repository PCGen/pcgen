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

import java.util.Optional;

/**
 * ScopeInstance identifies an instance of a scope in which a particular part of
 * a formula (usually a variable) is valid.
 * 
 * Combined with ImplementedScope, this effectively provides a concept similar to a
 * local variable scope in many programming languages.
 * 
 * ScopeInstance provides the ability to not only distinguish the different
 * scopes, but also to define the relationship between the scopes (each scope
 * can identify scopes it can draw upon for variables).
 * 
 * It is perhaps important to recognize that a ScopeInstance is simply an
 * identifier. This means it does not contain information about what the scope
 * contains (it does not, for example, have a list of legal variables for the
 * scope).
 * 
 * ImplementedScope was the template of sorts for actual ScopeInstance objects. You
 * must have a ScopeInstance to create a VariableID. (Creating a ScopeInstance
 * is must like creating a new instance of a class in Java, where the local
 * variables and methods can be processed).
 * 
 * In general a VariableID HAS-A ScopeInstance, not a ScopeInstance HAS-A list
 * of VariableIDs. (The exact reasoning for this relates to how VariableIDs are
 * constructed, as well as the fact that they can be "destroyed" by losing a
 * reference to the VariableID, in which case we do not want the contractual
 * requirement on a developer to have to call back to the scope in order to
 * clean up a collection).
 */
public interface ScopeInstance extends Identified
{

	/**
	 * Returns the ImplementedScope that serves as a "template" for this ScopeInstance.
	 * 
	 * @return The ImplementedScope that serves as a "template" for this ScopeInstance
	 */
	public ImplementedScope getImplementedScope();

	/**
	 * Returns the ScopeInstance that serves as a "parent" for this ScopeInstance.
	 * 
	 * Empty is a legal return value for a "master" scope.
	 * 
	 * @return The ScopeInstance that serves as a "parent" for this ScopeInstance
	 */
	public Optional<ScopeInstance> getParentScope();

	/**
	 * Returns the owning object for this ScopeInstance.
	 * 
	 * @return The owning object for this ScopeInstance
	 */
	public VarScoped getOwningObject();

}
