package pcgen.base.formula.factory;

import java.util.HashMap;
import java.util.Map;

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
		for (String node : dependencies.getNodeList())
		{
			SimpleImplementedScope scope = getImplementedScope(node);
			ancestors.clear();
			ancestors.traverseFromNode(node);
			for (String ancestor : ancestors.getVisitedNodes())
			{
				scope.drawsFrom(getImplementedScope(ancestor));
			}
		}
		ImplementedScopeLibrary isl = new ImplementedScopeLibrary();
		map.values().stream().forEach(isl::addScope);
		return isl;
	}

	private SimpleImplementedScope getImplementedScope(String name)
	{
		SimpleImplementedScope scope = map.get(name);
		if (scope == null)
		{
			scope = new SimpleImplementedScope(name);
			map.put(name, scope);
		}
		return scope;
	}
}