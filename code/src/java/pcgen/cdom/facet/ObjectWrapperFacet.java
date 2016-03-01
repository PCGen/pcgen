/*
 * Copyright (c) Thomas Parker, 2014.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.cdom.facet;

import pcgen.cdom.base.DataSetInitializedFacet;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.DataSetID;
import pcgen.cdom.facet.base.AbstractListFacet;
import pcgen.output.base.PCGenObjectWrapper;
import pcgen.output.wrapper.AgeSetWrapper;
import pcgen.output.wrapper.CDOMObjectWrapper;
import pcgen.output.wrapper.CDOMReferenceWrapper;
import pcgen.output.wrapper.CNAbilitySelectionWrapper;
import pcgen.output.wrapper.CategoryWrapper;
import pcgen.output.wrapper.EnumWrapper;
import pcgen.output.wrapper.OrderedPairWrapper;
import pcgen.output.wrapper.SimpleWrapperDelegate;
import pcgen.output.wrapper.TypeSafeConstantWrapper;
import pcgen.rules.context.LoadContext;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 * ObjectWrapperFacet stores information on the Wrappers available to Actors
 * when wrapping objects into TemplateModel objects for FreeMarker.
 */
public class ObjectWrapperFacet extends
		AbstractListFacet<DataSetID, PCGenObjectWrapper> implements
		DataSetInitializedFacet
{

	private DataSetInitializationFacet datasetInitializationFacet;

	@Override
	public void initialize(LoadContext context)
	{
		initialize(context.getDataSetID());
	}

	/**
	 * Initializes this Facet for the given DataSetID.
	 * 
	 * @param dsID
	 *            The DataSetID for which this Facet should be initialized
	 */
	public void initialize(DataSetID dsID)
	{
		add(dsID, new SimpleWrapperDelegate());
		add(dsID, new CDOMObjectWrapper());
		add(dsID, new CDOMReferenceWrapper());
		add(dsID, new TypeSafeConstantWrapper());
		add(dsID, new CNAbilitySelectionWrapper());
		add(dsID, new CategoryWrapper());
		add(dsID, new EnumWrapper());
		add(dsID, new OrderedPairWrapper());
		add(dsID, new AgeSetWrapper());
	}

	/**
	 * Wraps the given object into a TemplateModel, using the ObjectWrapper
	 * objects previously added to this ObjectWrapperLibrary using the add()
	 * method.
	 * 
	 * Each ObjectWrapper which has been added to the ObjectWrapperLibrary is
	 * given the chance, in the order they were added to the
	 * ObjectWrapperLibrary, to wrap the object into a TemplateModel. The
	 * results of the first successful ObjectWrapper are returned.
	 * 
	 * @param toWrap
	 *            The Object to be wrapped
	 * @return The TemplateModel produced by an ObjectWrapper contained in this
	 *         ObjectWrapperLibrary
	 * @throws TemplateModelException
	 *             if no ObjectWrapper in this ObjectWrapperLibrary can wrap the
	 *             given object into a TemplateModel
	 */
	public TemplateModel wrap(CharID id, Object toWrap)
		throws TemplateModelException
	{
		for (PCGenObjectWrapper ow : getSet(id.getDatasetID()))
		{
			try
			{
				return ow.wrap(id, toWrap);
			}
			catch (TemplateModelException e)
			{
				//No worries, Try the next one
			}
		}
		String info = (toWrap == null) ? "null" : toWrap.getClass().getCanonicalName();
		throw new TemplateModelException("Unable to find wrapping for "
			+ info);
	}

	public void setDataSetInitializationFacet(
		DataSetInitializationFacet datasetInitializationFacet)
	{
		this.datasetInitializationFacet = datasetInitializationFacet;
	}

	public void init()
	{
		datasetInitializationFacet.addDataSetInitializedFacet(this);
	}
}
