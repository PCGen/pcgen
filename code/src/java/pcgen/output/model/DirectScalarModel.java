/*
 * Copyright 2015 (C) Tom Parker <thpr@users.sourceforge.net>
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

import pcgen.output.base.NamedModel;
import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;

/**
 * A DirectScalarModel is a NamedModel wrapper around an existing, known,
 * TemplateModel
 */
public class DirectScalarModel implements NamedModel, TemplateScalarModel
{

	/**
	 * The name for this DirectScalarModel
	 */
	private String name;

	/**
	 * The underlying TemplateScalarModel for this DirectScalarModel
	 */
	private TemplateScalarModel model;

	/**
	 * Constructs a new DirectScalarModel with the given name and underlying
	 * TemplateModel
	 * 
	 * @param name
	 *            The name for this DirectScalarModel
	 * @param model
	 *            The underlying TemplateScalarModel for this DirectScalarModel
	 */
	public DirectScalarModel(String name, TemplateScalarModel model)
	{
		if (name == null)
		{
			throw new IllegalArgumentException("Name cannot be null");
		}
		if (model == null)
		{
			throw new IllegalArgumentException("Model cannot be null");
		}
		this.name = name;
		this.model = model;
	}

	/**
	 * @see pcgen.output.base.NamedModel#getModelName()
	 */
	@Override
	public String getModelName()
	{
		return name;
	}

	/**
	 * @see freemarker.template.TemplateScalarModel#getAsString()
	 */
	@Override
	public String getAsString() throws TemplateModelException
	{
		return model.getAsString();
	}

	/**
	 * Returns a new NamedModel with the given name and which will return the
	 * given content.
	 * 
	 * @param name
	 *            The name for the NamedModel to be returned
	 * @param content
	 *            The content for the NamedModel to be returned
	 * @return A NamedModel containing the given name and content
	 */
	public static NamedModel getModel(String name, String content)
	{
		try
		{
			TemplateScalarModel tsm =
					(TemplateScalarModel) ObjectWrapper.DEFAULT_WRAPPER
						.wrap(content);
			return new DirectScalarModel(name, tsm);
		}
		catch (TemplateModelException e)
		{
			throw new InternalError(e.getMessage());
		}
	}

}
