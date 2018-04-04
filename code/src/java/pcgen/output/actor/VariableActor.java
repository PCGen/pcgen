/*
 * Copyright 2014-15 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.output.actor;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import pcgen.base.formula.base.ScopeInstance;
import pcgen.base.formula.base.VariableID;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.LoadContextFacet;
import pcgen.cdom.facet.ObjectWrapperFacet;
import pcgen.cdom.facet.ScopeFacet;
import pcgen.cdom.facet.VariableStoreFacet;
import pcgen.output.base.OutputActor;

/**
 * A VariableActor is designed to process an interpolation and convert that into
 * a TemplateModel representing the contents of the specific variable being
 * requested.
 * 
 * Note that the actual name of the interpolation is stored externally to this
 * Actor (in CDOMObjectWrapperInfo to be precise)
 * 
 * @param <T>
 *            The Type of object stored in the variable underlying this
 *            VariableActor
 */
public class VariableActor<T> implements OutputActor<CDOMObject>
{
	/**
	 * The global LoadContextFacet used to get VariableIDs
	 */
	private final LoadContextFacet loadContextFacet =
			FacetLibrary.getFacet(LoadContextFacet.class);

	/**
	 * The global VariableStore Facet used to get VariableID values
	 */
	private final VariableStoreFacet variableStoreFacet = FacetLibrary
		.getFacet(VariableStoreFacet.class);

	/**
	 * The global ScopeFacet used to get VariableScopes
	 */
	private final ScopeFacet scopeFacet = FacetLibrary
		.getFacet(ScopeFacet.class);

	/**
	 * The global ObjectWrapperFacet used to wrap the current value of a
	 * variable
	 */
	private final ObjectWrapperFacet wrapperFacet = FacetLibrary
		.getFacet(ObjectWrapperFacet.class);

	/**
	 * The underlying Variable Name for this VariableActor
	 */
	private final String varName;

	/**
	 * Constructs a new VariableActor with the given variable name.
	 * 
	 * @param varName
	 *            The variable name of the variable that underlies this
	 *            VariableActor
	 */
	public VariableActor(String varName)
	{
		if (varName == null)
		{
			throw new IllegalArgumentException("Variable Name cannot be null");
		}
		this.varName = varName;
	}

	@Override
	public TemplateModel process(CharID id, CDOMObject obj)
		throws TemplateModelException
	{
		ScopeInstance varScope = scopeFacet.getGlobalScope(id);
		VariableID<?> varID = loadContextFacet.get(id.getDatasetID()).get()
			.getVariableContext().getVariableID(varScope, varName);
		Object value = variableStoreFacet.getValue(id, varID);
		return wrapperFacet.wrap(id, value);
	}
}
