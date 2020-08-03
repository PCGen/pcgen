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
package pcgen.base.formula.factory;

import java.util.Objects;

import pcgen.base.formula.inst.ImplementedScopeLibrary;
import pcgen.base.graph.inst.DefaultDirectionalGraphEdge;
import pcgen.base.graph.inst.DirectionalSetMapGraph;
import pcgen.base.graph.search.LoopDetectionAlgorithm;
import pcgen.base.graph.util.GraphUtilities;
import pcgen.base.util.ComplexResult;
import pcgen.base.util.FailureResult;

/**
 * A ShadowingScopeManager is a setup class for DefinedScope used to track the children of
 * DefinedScope objects. It can instantiate an ImplementedScopeLibrary.
 * 
 * Note: If a complete set of DefinedScope objects is not registered (meaning some of the
 * parents are not themselves registered), then instantiate will not succeed.
 */
public class ShadowingScopeManager
{

	/**
	 * The object representing the Global Parent for all scopes.
	 */
	private static final String GLOBAL_PARENT = "**GLOBAL PARENT**";

	/**
	 * The graph of dependencies.
	 */
	private final DirectionalSetMapGraph<String, DefaultDirectionalGraphEdge<String>> dependencies =
			new DirectionalSetMapGraph<>();

	/**
	 * Constructs a new ShadowingScopeManager.
	 */
	public ShadowingScopeManager()
	{
		dependencies.addNode(GLOBAL_PARENT);
	}

	/**
	 * Registers a scope name with this ShadowingScopeManager.
	 * 
	 * @param name
	 *            The name of the scope to be registered with this ShadowingScopeManager
	 */
	public void registerGlobalScope(String name)
	{
		registerScope(GLOBAL_PARENT, name);
		Objects.requireNonNull(name, "DefinedScope must have a name");
	}

	/**
	 * Links a pair of scopes. The given scope will be able to draw upon the parent scope.
	 * 
	 * @param parent
	 *            The parent scope of the given scope to be registered
	 * @param name
	 *            The scope to be registered with this ShadowingScopeManager
	 */
	public void registerScope(String parent, String name)
	{
		if (dependencies.containsNode(parent))
		{
			Objects.requireNonNull(name, "DefinedScope must have a name");
			dependencies.addNode(name);
			dependencies
				.addEdge(new DefaultDirectionalGraphEdge<>(parent, name));
		}
		else
		{
			throw new IllegalArgumentException(
				"Parent Scope: " + parent + " has not been registered");
		}
	}

	/**
	 * Returns true if the given scope name is registered with this ShadowingScopeManager.
	 * 
	 * @param name
	 *            The name of the scope to be checked to see if it is registered
	 * @return true if the given scope name is registered with this ShadowingScopeManager;
	 *         false otherwise
	 */
	public boolean isRegistered(String name)
	{
		return dependencies.containsNode(name);
	}

	/**
	 * Returns a ComplexResult with a possible ImplementedScopeLibrary if the set of
	 * DefinedScope object registered with this ShadowingScopeManager can be implemented.
	 * If the set of DefinedScope objects provided cannot be instantiated into an
	 * ImplementedScopeLibrary, then a List of error messages is returned in the
	 * ComplexResult.
	 * 
	 * @return A ComplexResult with a possible ImplementedScopeLibrary, or error messages
	 *         if the instantiation failed
	 */
	public ComplexResult<ImplementedScopeLibrary> instantiate()
	{
		LoopDetectionAlgorithm<String> loopDetector =
				new LoopDetectionAlgorithm<>(dependencies);
		if (loopDetector.hasLoopFromNode(GLOBAL_PARENT))
		{
			return new FailureResult<>(
				"Can't instantiate Scopes when Loop is present: "
					+ loopDetector.getLoopingNodes());
		}
		return ComplexResult
			.ofSuccess(new ScopeManagerInstantiator(
				GraphUtilities.unmodifiableGraph(dependencies)).instantiate());
	}
}
