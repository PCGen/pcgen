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

import pcgen.base.formula.base.VariableID;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.base.AbstractSourcedListFacet;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.cdom.facet.model.VarScopedFacet;
import pcgen.cdom.formula.PCGenScoped;
import pcgen.cdom.formula.VariableUtilities;
import pcgen.cdom.helper.BridgeListener;

/**
 * This Facet controls items granted from variables
 */
public class GrantedVarFacet extends AbstractSourcedListFacet<CharID, PCGenScoped>
		implements DataFacetChangeListener<CharID, PCGenScoped>
{

	/**
	 * The source facet to watch for the addition of new objects
	 */
	private VarScopedFacet varScopedFacet;

	/**
	 * The VariableStore Facet
	 */
	private VariableStoreFacet variableStoreFacet;

	@Override
	public void dataAdded(DataFacetChangeEvent<CharID, PCGenScoped> dfce)
	{
		PCGenScoped cdo = dfce.getCDOMObject();
		String[] grantedVariables = cdo.getGrantedVariableArray();
		if (grantedVariables.length == 0)
		{
			return;
		}
		Object source = dfce.getSource();
		CharID id = dfce.getCharID();
		for (String variableName : grantedVariables)
		{
			VariableID<?> varID =
					VariableUtilities.getGlobalVariableID(id, variableName);
			processAdd(id, varID, source);
		}
	}

	private <T> void processAdd(CharID id, VariableID<T> varID, Object source)
	{
		T value = variableStoreFacet.getValue(id, varID);
		variableStoreFacet.get(id).addVariableListener(varID, new BridgeListener(id, this, source));
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
				add(id, (PCGenScoped) o, source);
			}
		}
		else
		{
			add(id, (PCGenScoped) value, source);
		}
	}

	@Override
	public void dataRemoved(DataFacetChangeEvent<CharID, PCGenScoped> dfce)
	{
		PCGenScoped cdo = dfce.getCDOMObject();
		String[] list = cdo.getGrantedVariableArray();
		Object source = dfce.getSource();
		CharID id = dfce.getCharID();
		for (String varName : list)
		{
			VariableID<?> varID =  VariableUtilities.getGlobalVariableID(id, varName);
			processRemove(id, varID, source);
		}
	}

	private <T> void processRemove(CharID id, VariableID<T> varID, Object source)
	{
		variableStoreFacet.get(id).removeVariableListener(varID, new BridgeListener(id, this, source));
		T value = variableStoreFacet.getValue(id, varID);
		if (value.getClass().isArray())
		{
			for (Object o : (Object[]) value)
			{
				remove(id, (PCGenScoped) o, source);
			}
		}
		else
		{
			remove(id, (PCGenScoped) value, source);
		}
	}

	public void setVarScopedFacet(VarScopedFacet varScopedFacet)
	{
		this.varScopedFacet = varScopedFacet;
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
		varScopedFacet.addDataFacetChangeListener(this);
	}
}
