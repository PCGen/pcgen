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
package pcgen.output.library;

import java.util.ArrayList;
import java.util.List;

import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 * An ObjectWrapperLibrary is a library of ObjectWrapper objects, customized for
 * PCGen.
 * 
 * When the ObjectWrapperLibrary instance is asked to wrap an object, each
 * ObjectWrapper which has been added to the ObjectWrapperLibrary is given the
 * chance, in order, to wrap the object into a TemplateModel.
 */
public final class ObjectWrapperLibrary
{

	/**
	 * The singleton instance of ObjectWrapperLibrary
	 */
	private static final ObjectWrapperLibrary INSTANCE =
			new ObjectWrapperLibrary();

	/**
	 * The List of ObjectWrapper objects contained by this ObjectWrapperLibrary
	 */
	private final List<ObjectWrapper> list = new ArrayList<ObjectWrapper>();

	/**
	 * Private constructor for the singleton
	 */
	private ObjectWrapperLibrary()
	{
		//Singleton
	}

	/**
	 * Adds the given ObjectWrapper to this ObjectWrapperLibrary.
	 * 
	 * @param ow
	 *            The ObjectWrapper to be added to this ObjectWrapperLibrary
	 * @throws IllegalArgumentException
	 *             if the argument is null
	 */
	public void add(ObjectWrapper ow)
	{
		if (ow == null)
		{
			throw new IllegalArgumentException(
				"ObjectWrapper to add must not be null");
		}
		list.add(ow);
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
	 * @param o
	 *            The Object to be wrapped
	 * @return The TemplateModel produced by an ObjectWrapper contained in this
	 *         ObjectWrapperLibrary
	 * @throws TemplateModelException
	 *             if no ObjectWrapper in this ObjectWrapperLibrary can wrap the
	 *             given object into a TemplateModel
	 */
	public TemplateModel wrap(Object o) throws TemplateModelException
	{
		for (ObjectWrapper ow : list)
		{
			try
			{
				return ow.wrap(o);
			}
			catch (TemplateModelException e)
			{
				//No worries, Try the next one
			}
		}
		throw new TemplateModelException("Unable to find wrapping for "
			+ o.getClass());
	}

	/**
	 * Returns the singleton instance of ObjectWrapperLibrary
	 * 
	 * @return The (singleton) instance of ObjectWrapperLibrary
	 */
	public static ObjectWrapperLibrary getInstance()
	{
		return INSTANCE;
	}

}
