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
package pcgen.output.model;

import java.util.Objects;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.CDOMWrapperInfoFacet;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.output.base.OutputActor;

import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;

/**
 * A CDOMObjectModel is a wrapper around a CDOMObject which serves as a
 * TemplateHashModel to expose the appropriate objects within the CDOMObject.
 */
public class CDOMObjectModel implements TemplateHashModel, TemplateScalarModel
{
	private static final CDOMWrapperInfoFacet WRAPPER_FACET = FacetLibrary.getFacet(CDOMWrapperInfoFacet.class);

	/**
	 * The underlying CDOMObject, from which information is retrieved
	 */
	private final CDOMObject cdo;

	private final CharID id;

	/**
	 * Constructs a new CDOMObjectModel from the given CharID and CDOMObject.
	 * 
	 * @param id
	 *            The CharID identifying the PlayerCharacter on which this
	 *            CDOMObjectModel is processing information
	 * @param cdo
	 *            The underlying CDOMObject, from which information is retrieved
	 * @throws IllegalArgumentException
	 *             if either argument is null
	 */
	public CDOMObjectModel(CharID id, CDOMObject cdo)
	{
		Objects.requireNonNull(id, "CharID may not be null");
		Objects.requireNonNull(cdo, "CDOMObject may not be null");
		this.id = id;
		this.cdo = cdo;
	}

	@Override
	public TemplateModel get(String key) throws TemplateModelException
	{
		//TODO may not be entirely correct - really about ... what? formula scope??
		Class<? extends CDOMObject> cl = cdo.getClass();
		return proc(cl, key);
	}

	private <T> TemplateModel proc(Class<T> cl, String key) throws TemplateModelException
	{
		/*
		 * What if it didn't previously exist (e.g. cl==SubClass.class)...
		 * shouldn't be able to get here really (in that case)
		 */
		OutputActor<? super T> actor = WRAPPER_FACET.getActor(id.getDatasetID(), cl, key);
		if (actor == null)
		{
			throw new TemplateModelException(
				"object of type " + cdo.getClass().getSimpleName() + " did not have output of type " + key);
		}
		@SuppressWarnings("unchecked")
		T obj = (T) cdo;
		return actor.process(id, obj);
	}

	@Override
	public boolean isEmpty()
    {
		//Never empty because we have "key"
		return false;
	}

	@Override
	public String getAsString()
    {
		return cdo.getDisplayName();
	}

}
