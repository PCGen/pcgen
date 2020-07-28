/*
 * Copyright 2016 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.solver;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import pcgen.base.formula.base.DynamicDependency;
import pcgen.base.formula.base.ScopeInstanceFactory;
import pcgen.base.formula.base.VarScoped;
import pcgen.base.formula.base.VariableID;
import pcgen.base.formula.base.VariableLibrary;
import pcgen.base.graph.base.DirectionalEdge;
import pcgen.base.graph.inst.DefaultDirectionalGraphEdge;
import pcgen.base.graph.inst.DefaultGraphEdge;

/**
 * A DynamicEdge is a specific form of Graph Edge designed to support DynamicDependency
 * objects.
 * 
 * A DynamicEdge is designed to be a static object, meaning that it represents the current
 * state of the DynamicDependency. If the value of the control variable is changed, then
 * this DynamicEdge should be discarded, and replaced with the result returned by the
 * createReplacement method.
 */
public class DynamicEdge extends DefaultGraphEdge<Object>
		implements DirectionalEdge<Object>
{

	/**
	 * The underlying DynamicDependency object that this DynamicEdge is representing.
	 */
	private final DynamicDependency dynamicDep;

	/**
	 * The dynamic variable name.
	 */
	private final String varName;

	/**
	 * Constructs a new DynamicEdge with the given Control Variable, target edge, and
	 * underlying DynamicDependency.
	 * 
	 * @param source
	 *            VariableID that contains the variable that controls the contents of the
	 *            targetEdge
	 * @param targetEdge
	 *            The GraphEdge that has the actual dependency link given the current
	 *            state of the
	 * @param dynamicDep
	 *            The underlying DynamicDependency that has the information about the
	 *            original dependency
	 */
	public DynamicEdge(VariableID<?> source,
		DefaultDirectionalGraphEdge<VariableID<?>> targetEdge, DynamicDependency dynamicDep)
	{
		super(source, targetEdge);
		varName = targetEdge.getNodeAt(0).getName();
		this.dynamicDep = Objects.requireNonNull(dynamicDep);
	}

	@Override
	public DynamicEdge createReplacementEdge(Object gn1, Object gn2)
	{
		throw new UnsupportedOperationException(
			"Replacement unsupported for a DynamicEdge");
	}

	/**
	 * Creates a replacement DynamicEdge for this DynamicEdge, with the target edge
	 * containing the same target and the new source (based on the given VarScoped
	 * object).
	 * 
	 * @param varLibrary
	 *            The VariableLibrary to be used to resolve the VariableID of the
	 *            (dynamic) source
	 * @param siFactory
	 *            The ScopeInstanceFactory to be used to resolve the scope instance used
	 *            to determine the VariableID of the (dynamic) source
	 * @param vs
	 *            The (new) VarScoped object that is the source of the dynamic variable
	 * @param targetVar
	 *            The target (resulting) variable of the calculation, for linking the
	 *            dependency
	 * @return A replacement DynamicEdge for this DynamicEdge, with the target edge
	 *         containing the same target and the new source
	 */
	public DynamicEdge createReplacement(VariableLibrary varLibrary,
		ScopeInstanceFactory siFactory, VarScoped vs, VariableID<?> targetVar)
	{
		VariableID<?> input =
				dynamicDep.generateSource(varLibrary, siFactory, vs, getSourceName());
		DefaultDirectionalGraphEdge<VariableID<?>> edge =
				new DefaultDirectionalGraphEdge<>(input, targetVar);
		return new DynamicEdge((VariableID<?>) getNodeAt(0), edge, dynamicDep);
	}

	@Override
	public boolean isSource(Object node)
	{
		return getNodeAt(0).equals(node);
	}

	@Override
	public boolean isSink(Object node)
	{
		return getNodeAt(1).equals(node);
	}

	/**
	 * Returns a List of the source Nodes of this DynamicEdge. Will always return a List
	 * of length one containing only the second Node.
	 * 
	 * Ownership of the returned List is transferred to the calling Object. No reference
	 * to the List Object is maintained by DynamicEdge. However, the Nodes contained in
	 * the List are returned BY REFERENCE, and modification of the returned Nodes will
	 * modify the Nodes contained within the DynamicEdge.
	 * 
	 * @return A List of length one containing the Sink Node of this DynamicEdge
	 */
	@Override
	public List<Object> getSinkNodes()
	{
		return Collections.singletonList(getNodeAt(1));
	}

	/**
	 * Returns a List of the source Nodes of this DynamicEdge. Will always return a List
	 * of length one containing only the first Node.
	 * 
	 * Ownership of the returned List is transferred to the calling Object. No reference
	 * to the List Object is maintained by DynamicEdge. However, the Nodes contained in
	 * the List are returned BY REFERENCE, and modification of the returned Nodes will
	 * modify the Nodes contained within the DynamicEdge.
	 * 
	 * @return A List of length one containing the Source Node of this DynamicEdge
	 */
	@Override
	public List<Object> getSourceNodes()
	{
		return Collections.singletonList(getNodeAt(0));
	}

	@Override
	public String toString()
	{
		return "DynamicEdge: " + getNodeAt(0) + " -> (" + getNodeAt(1) + ')';
	}

	/**
	 * Returns the DefaultDirectionalGraphEdge that is the "dynamic" edge for the
	 * dependency graph.
	 * 
	 * @return the DefaultDirectionalGraphEdge that is the "dynamic" edge for the
	 *         dependency graph
	 */
	@SuppressWarnings("unchecked")
	public DefaultDirectionalGraphEdge<VariableID<?>> getTargetEdge()
	{
		return (DefaultDirectionalGraphEdge<VariableID<?>>) getNodeAt(1);
	}

	/**
	 * Returns true if this DynamicEdge represents the given DynamicDependency.
	 * 
	 * @param dep
	 *            The DynamicDependency to be tested
	 * @return true if this DynamicEdge represents the given DynamicDependency; false
	 *         otherwise
	 */
	public boolean isDependency(DynamicDependency dep)
	{
		return dynamicDep.equals(dep);
	}

	/**
	 * Returns the variable name of the source variable of the target edge of this DynamicEdge.
	 * 
	 * @return The variable name of the source variable of the target edge of this DynamicEdge
	 */
	public String getSourceName()
	{
		return varName;
	}
}
