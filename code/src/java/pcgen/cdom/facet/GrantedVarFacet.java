/*
 * Copyright (c) Thomas Parker, 2018.
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.cdom.facet;

import pcgen.base.formula.base.ScopeInstance;
import pcgen.base.formula.base.VariableID;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.base.AbstractSourcedListFacet;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.cdom.helper.BridgeListener;
import pcgen.rules.context.LoadContext;

/**
 * This Facet controls items granted from variables
 */
public class GrantedVarFacet extends AbstractSourcedListFacet<CharID, CDOMObject>
		implements DataFacetChangeListener<CharID, CDOMObject>
{

	/**
	 * The source facet to watch for the addition of new objects
	 */
	private CDOMObjectSourceFacet cdomSourceFacet;

	/**
	 * The Scope Facet
	 */
	private ScopeFacet scopeFacet;

	/**
	 * The global LoadContextFacet used to get VariableIDs
	 */
	private final LoadContextFacet loadContextFacet = FacetLibrary.getFacet(LoadContextFacet.class);

	/**
	 * The VariableStore Facet
	 */
	private VariableStoreFacet variableStoreFacet;

	@Override
	public void dataAdded(DataFacetChangeEvent<CharID, CDOMObject> dfce)
	{
		CDOMObject cdo = dfce.getCDOMObject();
		String[] grantedVariables = cdo.getGrantedVariableArray();
		if (grantedVariables.length == 0)
		{
			return;
		}
		Object source = dfce.getSource();
		CharID id = dfce.getCharID();
		ScopeInstance inst = scopeFacet.get(id, cdo);
		for (String variableName : grantedVariables)
		{
			LoadContext context = loadContextFacet.get(id.getDatasetID()).get();
			VariableID<?> varID = context.getVariableContext()
				.getVariableID(inst, variableName);
			processAdd(id, varID, source);
		}
	}

	private <T> void processAdd(CharID id, VariableID<T> varID, Object source)
	{
		T value = variableStoreFacet.getValue(id, varID);
		variableStoreFacet.get(id).addVariableListener(varID, new BridgeListener(id, this));
		/*
		 * CONSIDER This is a hard-coding based on array - the format manager, which is
		 * available from the VariableID, might want to provide more insight. Currently,
		 * this isn't possible, but it's something to think about with the FormatManager
		 * objects going forward...
		 */
		if (value.getClass().isArray())
		{
			for (Object o : (Object[]) value)
			{
				add(id, (CDOMObject) o, source);
			}
		}
		else
		{
			add(id, (CDOMObject) value, source);
		}
	}

	@Override
	public void dataRemoved(DataFacetChangeEvent<CharID, CDOMObject> dfce)
	{
		CDOMObject cdo = dfce.getCDOMObject();
		String[] list = cdo.getGrantedVariableArray();
		Object source = dfce.getSource();
		CharID id = dfce.getCharID();
		ScopeInstance inst = scopeFacet.get(id, cdo);
		for (String s : list)
		{
			LoadContext loadContext = loadContextFacet.get(id.getDatasetID()).get();
			VariableID<?> varID = loadContext.getVariableContext().getVariableID(inst, s);
			processRemove(id, varID, source);
		}
	}

	private <T> void processRemove(CharID id, VariableID<T> varID, Object source)
	{
		variableStoreFacet.get(id).removeVariableListener(varID, new BridgeListener(id, this));
		T value = variableStoreFacet.getValue(id, varID);
		if (value.getClass().isArray())
		{
			for (Object o : (Object[]) value)
			{
				remove(id, (CDOMObject) o, source);
			}
		}
		else
		{
			remove(id, (CDOMObject) value, source);
		}
	}

	public void setCdomSourceFacet(CDOMObjectSourceFacet cdomSourceFacet)
	{
		this.cdomSourceFacet = cdomSourceFacet;
	}

	public void setScopeFacet(ScopeFacet scopeFacet)
	{
		this.scopeFacet = scopeFacet;
	}

	public void setVariableStoreFacet(VariableStoreFacet variableStoreFacet)
	{
		this.variableStoreFacet = variableStoreFacet;
	}

	/**
	 * Initializes the connections for SpellsFacet to other facets.
	 * 
	 * This method is automatically called by the Spring framework during initialization
	 * of the SpellsFacet.
	 */
	public void init()
	{
		cdomSourceFacet.addDataFacetChangeListener(this);
	}
}
