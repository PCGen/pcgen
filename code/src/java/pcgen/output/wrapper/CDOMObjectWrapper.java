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
package pcgen.output.wrapper;

import java.util.HashMap;
import java.util.Map;

import pcgen.cdom.base.CDOMObject;
import pcgen.output.actor.KeyActor;
import pcgen.output.actor.SourceActor;
import pcgen.output.base.OutputActor;
import pcgen.output.model.CDOMObjectModel;
import pcgen.output.model.CDOMObjectWrapperInfo;
import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModelException;

/**
 * A CategoryWrapper is an ObjectWrapper capable of producing a TemplateModel
 * for CDOMObject objects.
 */
public final class CDOMObjectWrapper implements ObjectWrapper
{

	/**
	 * The singleton instance of CDOMObjectWrapper
	 */
	private static final CDOMObjectWrapper INSTANCE = new CDOMObjectWrapper();

	/**
	 * The Map of Classes to CDOMObjectWrapperInfo that stores information about
	 * those classes.
	 */
	private final Map<Class<?>, CDOMObjectWrapperInfo> infoMap =
			new HashMap<Class<?>, CDOMObjectWrapperInfo>();

	static
	{
		triggerLoad();
	}

	/**
	 * The singleton constructor
	 */
	private CDOMObjectWrapper()
	{
		//Singleton
	}

	/**
	 * Loads a new OutputActor into the CDOMObjectWrapperInfo for the given
	 * class.
	 * 
	 * If the CDOMObjectWrapperInfo already has an OutputActor for the given
	 * name, then this method will not add the given OutputActor and will return
	 * false.
	 * 
	 * @param cl
	 *            The Class for which the given OutputActor will be loaded
	 * @param name
	 *            The name of the interpolation to be used for the given
	 *            OutputActor
	 * @param oa
	 *            The OutputActor to be loaded into the CDOMObjectWrapperInfo
	 * @return true if the given name and OutputActor were successfully loaded;
	 *         false otherwise
	 */
	public <T extends CDOMObject> boolean load(Class<T> cl, String name,
		OutputActor<CDOMObject> oa)
	{
		return getWrapperInfo(cl).load(name, oa);
	}

	private <T> CDOMObjectWrapperInfo getWrapperInfo(Class<T> cl)
	{
		if (cl == null)
		{
			//Handle parent of Object.class since this is recursive
			return null;
		}
		CDOMObjectWrapperInfo info = infoMap.get(cl);
		if (info == null)
		{
			Class<? super T> superclass = cl.getSuperclass();
			info = new CDOMObjectWrapperInfo(getWrapperInfo(superclass));
			infoMap.put(cl, info);
		}
		return info;
	}

	/**
	 * @see freemarker.template.ObjectWrapper#wrap(java.lang.Object)
	 */
	@Override
	public TemplateHashModel wrap(Object o) throws TemplateModelException
	{
		if (o instanceof CDOMObject)
		{
			CDOMObject cdo = (CDOMObject) o;
			//TODO may not be entirely correct - really about ... what? formula scope??
			Class<?> cl = cdo.getClass();
			/*
			 * Must use getWrapperInfo in case it didn't previously exist (e.g.
			 * cl==SubClass.class)
			 */
			CDOMObjectWrapperInfo info = getWrapperInfo(cl);
			return new CDOMObjectModel(info, cdo);
		}
		throw new TemplateModelException("Object was not a CDOMObject");
	}

	/**
	 * Returns the (singleton) instance of CDOMObjectWrapper.
	 * 
	 * @return The (singleton) instance of CDOMObjectWrapper
	 */
	public static CDOMObjectWrapper getInstance()
	{
		return INSTANCE;
	}

	/**
	 * For clearing information between loads of data & tests
	 */
	public void clear()
	{
		infoMap.clear();
		triggerLoad();
	}
	
	private static void triggerLoad()
	{
		INSTANCE.load(CDOMObject.class, "source", new SourceActor());
		INSTANCE.load(CDOMObject.class, "key", new KeyActor());
	}
}
