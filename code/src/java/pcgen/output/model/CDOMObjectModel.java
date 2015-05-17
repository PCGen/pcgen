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

import pcgen.cdom.base.CDOMObject;
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
	/**
	 * The CDOMObjectWrapperInfo which contains the information mapping
	 * interpolations to ObjectActors for the class of the underlying CDOMObject
	 */
	private final CDOMObjectWrapperInfo info;

	/**
	 * The underlying CDOMObject, from which information is retrieved
	 */
	private final CDOMObject d;

	/**
	 * Constructs a new CDOMObjectModel from the given CDOMObjectWrapperInfo and
	 * CDOMObject.
	 * 
	 * @param info
	 *            The CDOMObjectWrapperInfo which contains the information
	 *            mapping interpolations to ObjectActors for the class of the
	 *            given CDOMObject
	 * @param cdo
	 *            The underlying CDOMObject, from which information is retrieved
	 * @throws IllegalArgumentException
	 *             if either argument is null
	 */
	public CDOMObjectModel(CDOMObjectWrapperInfo info, CDOMObject cdo)
	{
		if (info == null)
		{
			throw new IllegalArgumentException(
				"CDOMObjectWrapperInfo may not be null");
		}
		if (cdo == null)
		{
			throw new IllegalArgumentException("CDOMObject may not be null");
		}
		this.info = info;
		this.d = cdo;
	}

	/**
	 * @see freemarker.template.TemplateHashModel#get(java.lang.String)
	 */
	@Override
	public TemplateModel get(String key) throws TemplateModelException
	{
		OutputActor<CDOMObject> actor = info.get(key);
		if (actor == null)
		{
			throw new TemplateModelException("object of type "
				+ d.getClass().getSimpleName()
				+ " did not have output of type " + key);
		}
		return actor.process(d);
	}

	/**
	 * @see freemarker.template.TemplateHashModel#isEmpty()
	 */
	@Override
	public boolean isEmpty() throws TemplateModelException
	{
		//Never empty because we have "key"
		return false;
	}

	/**
	 * @see freemarker.template.TemplateScalarModel#getAsString()
	 */
	@Override
	public String getAsString() throws TemplateModelException
	{
		return d.getDisplayName();
	}

}
