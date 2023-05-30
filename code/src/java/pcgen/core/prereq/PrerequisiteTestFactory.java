/*
 * PreerquisiteTestFactory.java Copyright 2003 (C) Chris Ward
 * <frugal@purplewombat.co.uk>
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2.1 of the License, or (at your
 * option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation,
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.core.prereq;

import java.util.HashMap;
import java.util.Map;

import pcgen.system.LanguageBundle;
import pcgen.system.PluginLoader;
import pcgen.util.Logging;
import plugin.pretokens.test.PreMultTester;

public final class PrerequisiteTestFactory implements PluginLoader
{
	private static PrerequisiteTestFactory instance = null;
	private final Map<String, PrerequisiteTest> TEST_LOOKUP = new HashMap<>();

	/**
	 * @return Returns the instance.
	 */
	public static PrerequisiteTestFactory getInstance()
	{
		if (instance == null)
		{
			instance = new PrerequisiteTestFactory();
		}
		return instance;
	}

	/** Private default constructor */
	private PrerequisiteTestFactory()
	{
		// Do Nothing
	}

	/**
	 * Registers this PrerequisiteTest as handling a kind of prereq
	 * @param testClass PrerequisiteTest to register.
	 */
	public void register(final PrerequisiteTest testClass)
	{
		final String kindHandled = testClass.kindHandled();
		final PrerequisiteTest test = TEST_LOOKUP.get(kindHandled);
		if (test != null)
		{
			Logging.errorPrint(
				LanguageBundle.getFormattedString("PrerequisiteTestFactory.error.already_registered", //$NON-NLS-1$
				testClass.getClass().getName(), kindHandled, test.getClass().getName()));
		}
		TEST_LOOKUP.put(kindHandled.toUpperCase(), testClass);
	}

	/**
	 * Returns the appropriate PrerequisiteTest class for the kind of prereq
	 * passed in.
	 * @param kind The kind of prereq this is (e.g. CLASS)
	 * @return PrerequisiteTest for this kind
	 */
	public PrerequisiteTest getTest(final String kind)
	{
		PrerequisiteTest test;
		if (kind == null)
		{
			test = new PreMultTester();
		}
		else
		{
			test = TEST_LOOKUP.get(kind.toUpperCase());
			if (test == null)
			{
				Logging.errorPrintLocalised("PrerequisiteTestFactory.error.cannot_find_test", kind); //$NON-NLS-1$
			}
		}
		return test;
	}

	@Override
	public void loadPlugin(Class<?> clazz) throws Exception
	{
		register((PrerequisiteTest) clazz.newInstance());
	}

	@Override
	public Class<?>[] getPluginClasses()
	{
		return new Class[]{PrerequisiteTest.class};
	}

	public static void clear()
	{
		if (instance != null)
		{
			instance.TEST_LOOKUP.clear();
		}
	}
}
