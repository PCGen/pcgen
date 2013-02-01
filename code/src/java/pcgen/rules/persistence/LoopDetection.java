/*
 * Copyright 2013 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.rules.persistence;

import java.util.Collection;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Set;

import pcgen.base.graph.core.DirectionalGraph;
import pcgen.base.graph.core.DirectionalSetMapGraph;
import pcgen.base.graph.core.IdentityDirectionalGraphEdge;
import pcgen.base.graph.visitor.DirectedDepthFirstTraverseAlgorithm;
import pcgen.base.graph.visitor.GraphCycleDetected;
import pcgen.base.lang.UnreachableError;
import pcgen.base.util.WrappedMapSet;
import pcgen.cdom.base.Loadable;
import pcgen.cdom.reference.ReferenceManufacturer;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.GrantingToken;
import pcgen.util.Logging;

public class LoopDetection
{

	private final LoadContext context;

	private final DirectionalGraph<Loadable, IdentityDirectionalGraphEdge<Loadable>> graph;
	
	private boolean unconstructed = false;
	
	private final Set<String> loops = new HashSet<String>();

	public LoopDetection(LoadContext lc)
	{
		context = lc;
		try
		{
			graph =
					new DirectionalSetMapGraph<Loadable, IdentityDirectionalGraphEdge<Loadable>>(
						IdentityHashMap.class);
		}
		catch (InstantiationException e)
		{
			throw new UnreachableError(e);
		}
		catch (IllegalAccessException e)
		{
			throw new UnreachableError(e);
		}
	}

	public <T extends Loadable> void initializeObjects()
	{
		Collection<? extends ReferenceManufacturer> mfgs =
				context.ref.getAllManufacturers();
		for (ReferenceManufacturer<? extends T> rm : mfgs)
		{
			for (T po : rm.getAllObjects())
			{
				graph.addNode(po);
			}
		}
	}

	public void buildConnections()
	{
		for (GrantingToken<?, ?> token : TokenLibrary.getGrantingTokens())
		{
			buildConnectionsForToken(token);
		}
	}

	private <T extends Loadable, G extends Loadable> void buildConnectionsForToken(
		GrantingToken<T, G> token)
	{
		Class<T> cl = token.getGrantorClass();
		Collection<? extends ReferenceManufacturer> mfgs =
				context.ref.getAllManufacturers();
		for (ReferenceManufacturer<? extends T> rm : mfgs)
		{
			if (cl.isAssignableFrom(rm.getReferenceClass()))
			{
				for (T po : rm.getAllObjects())
				{
					try
					{
						Collection<? extends G> granted = token.getGranted(po);
						for (G target : granted)
						{
							if (target == null)
							{
								if (!unconstructed)
								{
									unconstructed = true;
									Logging
										.errorPrint("Loop Detection limited due to unconstructed reference(s)");
								}
							}
							else
							{
								IdentityDirectionalGraphEdge<Loadable> edge =
										new IdentityDirectionalGraphEdge<Loadable>(
											po, target);
								graph.addEdge(edge);
							}
						}
					}
					catch (java.lang.IllegalStateException iae)
					{
						Logging
							.errorPrint("Loop Detection limited due to resolution failure: "
								+ iae.getMessage());
					}
				}
			}
		}
	}

	public <T extends Loadable> boolean detectLoops()
	{
		boolean looped = false;
		Collection<? extends ReferenceManufacturer> mfgs =
				context.ref.getAllManufacturers();
		Set<Loadable> completed = new WrappedMapSet<Loadable>(IdentityHashMap.class);
		for (ReferenceManufacturer<? extends T> rm : mfgs)
		{
			for (T po : rm.getAllObjects())
			{
				if (!completed.contains(po))
				{
					DirectedDepthFirstTraverseAlgorithm<Loadable, IdentityDirectionalGraphEdge<Loadable>> alg =
							new DirectedDepthFirstTraverseAlgorithm<Loadable, IdentityDirectionalGraphEdge<Loadable>>(
								graph);
					try
					{
						alg.traverseFromNode(po);
					}
					catch (GraphCycleDetected gcd)
					{
						Collection<?> nodes = gcd.getNodes();
						Object loopitem = gcd.getLoopItem();
						StringBuilder sb = new StringBuilder(250);
						sb.append("Found Loop: ");
						boolean active = false;
						for (Object node : nodes)
						{
							if (active)
							{
								sb.append("->");
								sb.append(node.getClass().getSimpleName());
								sb.append(":");
								sb.append(node);
							}
							if (node == loopitem)
							{
								sb.append(node.getClass().getSimpleName());
								sb.append(":");
								sb.append(node);
								active = true;
							}
						}
						sb.append("->");
						sb.append(loopitem.getClass().getSimpleName());
						sb.append(":");
						sb.append(loopitem);
						String str = sb.toString();
						if (!loops.contains(str))
						{
							Logging.errorPrint(str);
							loops.add(str);
						}
						looped = true;
					}
					completed.addAll(alg.getVisitedNodes());
				}
			}
		}
		return looped;
	}
}
