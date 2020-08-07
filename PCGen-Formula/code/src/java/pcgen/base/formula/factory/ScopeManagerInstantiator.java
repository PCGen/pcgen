package pcgen.base.formula.factory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import pcgen.base.formula.base.ImplementedScope;
import pcgen.base.formula.inst.ImplementedScopeLibrary;
import pcgen.base.formula.inst.SimpleImplementedScope;
import pcgen.base.graph.base.Graph;
import pcgen.base.graph.inst.DefaultDirectionalGraphEdge;
import pcgen.base.graph.search.AncestorSearch;

/**
 * A ScopeManagerInstantiator is responsible for creating the ImplementedScope objects the
 * ImplementedScopeLibrary.
 */
public class ScopeManagerInstantiator
{
	/**
	 * The Graph of dependencies that this ScopeManagerInstantiator will use as reference
	 * for creating the ImplementedScope objects.
	 */
	private final Graph<String, DefaultDirectionalGraphEdge<String>> dependencies;

	/**
	 * An AncestorSearch that is used to identify the "ancestor" scopes (those that a
	 * scope can draw from).
	 */
	private final AncestorSearch<String, DefaultDirectionalGraphEdge<String>> ancestors;

	/**
	 * The cache of built SimpleImplementedScope objects
	 */
	private final Map<String, SimpleImplementedScope> map = new HashMap<>();

	/**
	 * Constructs a new ScopeManagerInstantiator that will operate based on the given
	 * graph.
	 * 
	 * @param dependencies
	 *            The Graph of dependencies that this ScopeManagerInstantiator will use as
	 *            reference for creating the ImplementedScope objects
	 */
	public ScopeManagerInstantiator(
		Graph<String, DefaultDirectionalGraphEdge<String>> dependencies)
	{
		this.dependencies = dependencies;
		ancestors = new AncestorSearch<>(dependencies);
	}

	/**
	 * Instantiate the ImplementedScopeLibrary based on the information in the Graph
	 * originally provided to this ScopeManagerInstantiator.
	 * 
	 * @return An ImplementedScopeLibrary based on the information in the Graph originally
	 *         provided to this ScopeManagerInstantiator
	 */
	public ImplementedScopeLibrary instantiate()
	{
		dependencies.getAdjacentEdges(ShadowingScopeManager.GLOBAL_PARENT)
			.stream()
			.map(e -> e.getNodeAt(1))
			.forEach(s -> getImplementedScope(s, true));
		dependencies.getNodeList().stream()
			.filter(s -> !map.containsKey(s))
			.forEach(s -> getImplementedScope(s, false));
		ImplementedScopeLibrary isl = new ImplementedScopeLibrary();
		map.values().stream().filter(this::notGlobal).forEach(isl::addScope);
		return isl;
	}

	private boolean notGlobal(ImplementedScope scope)
	{
		return !scope.getName().equals(ShadowingScopeManager.GLOBAL_PARENT);
	}

	private SimpleImplementedScope getImplementedScope(String name, boolean globalIfCreated)
	{
		SimpleImplementedScope scope = map.get(name);
		if (scope == null)
		{
			scope = buildNewScope(name, globalIfCreated);
			map.put(name, scope);
		}
		return scope;
	}

	private SimpleImplementedScope buildNewScope(String name,
		boolean globalIfCreated)
	{
		ancestors.clear();
		ancestors.traverseFromNode(name);
		Set<String> visited = ancestors.getVisitedNodes();
		SimpleImplementedScope scope =
				new SimpleImplementedScope(name, globalIfCreated);
		for (String ancestor : visited)
		{
			if (!ancestor.equals(scope.getName()))
			{
				scope.drawsFrom(getImplementedScope(ancestor, globalIfCreated));
			}
		}
		return scope;
	}
}
