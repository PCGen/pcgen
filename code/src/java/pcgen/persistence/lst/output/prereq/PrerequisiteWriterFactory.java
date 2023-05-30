/*
 *
 * Copyright 2004 (C) Frugal <frugal@purplewombat.co.uk>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.       See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 *
 *
 *
 *
 */
package pcgen.persistence.lst.output.prereq;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import pcgen.persistence.PersistenceLayerException;
import pcgen.system.PluginLoader;
import pcgen.util.Logging;
import plugin.pretokens.writer.PreMultWriter;

/**
 * A Factory for PreReq Writing
 */
public final class PrerequisiteWriterFactory implements PluginLoader
{
	private static PrerequisiteWriterFactory instance = null;
	private Map<String, PrerequisiteWriterInterface> parserLookup = new HashMap<>();

	private PrerequisiteWriterFactory()
	{
		// Do Nothing
	}

	/**
	 * @return PrerequisiteWriterFactory
	 */
	public static PrerequisiteWriterFactory getInstance()
	{
		if (instance == null)
		{
			instance = new PrerequisiteWriterFactory();
		}
		return instance;
	}

	/**
	 * @param kind
	 * @return PrerequisiteWriterInterface
	 */
	public PrerequisiteWriterInterface getWriter(String kind)
	{
		PrerequisiteWriterInterface test;
		if (kind == null)
		{
			test = new PreMultWriter();
		}
		else
		{
			test = parserLookup.get(kind.toLowerCase());
			if (test == null)
			{
				Logging.errorPrintLocalised("PrerequisiteTestFactory.error.cannot_find_test", kind); //$NON-NLS-1$
			}
		}
		return test;
	}

	/**
	 * Register the test class with the factory .
	 *
	 * @param testClass the test class
	 * @throws PersistenceLayerException the persistence layer exception
	 */
	public void register(PrerequisiteWriterInterface testClass) throws PersistenceLayerException
	{
		String kindHandled = testClass.kindHandled();

		Object test = parserLookup.get(kindHandled.toLowerCase());

		if (test != null)
		{
			throw new PersistenceLayerException("Error registering '" + testClass.getClass().getName() + "' as test '"
				+ kindHandled + "'. The test is already registered to '" + test.getClass().getName() + "'");
		}

		parserLookup.put(kindHandled.toLowerCase(), testClass);
	}

	@Override
	public void loadPlugin(Class<?> clazz) throws PersistenceLayerException, InstantiationException,
		IllegalAccessException, NoSuchMethodException, InvocationTargetException
	{
		register((PrerequisiteWriterInterface) clazz.getConstructor().newInstance());
	}

	@Override
	public Class[] getPluginClasses()
	{
		return new Class[]{PrerequisiteWriterInterface.class};
	}

	public static void clear()
	{
		if (instance != null)
		{
			instance.parserLookup.clear();
		}
	}
}
