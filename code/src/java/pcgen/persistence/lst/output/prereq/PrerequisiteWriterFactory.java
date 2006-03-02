/*
 * PrerequisiteWriterFactory.java
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
 * Created on 18-Dec-2003
 *
 * Current Ver: $Revision: 1.8 $
 *
 * Last Editor: $Author: binkley $
 *
 * Last Edited: $Date: 2005/10/18 20:23:56 $
 *
 */
package pcgen.persistence.lst.output.prereq;

import java.util.HashMap;
import java.util.Map;

import pcgen.persistence.PersistenceLayerException;
import pcgen.util.Logging;

public class PrerequisiteWriterFactory
{
	private static PrerequisiteWriterFactory instance = null;
	private static Map parserLookup = new HashMap();

	private PrerequisiteWriterFactory()
	{
	}

	/**
	 * @return PrerequisiteWriterFactory
	 */
	public static PrerequisiteWriterFactory getInstance()
	{
		if (instance == null){
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
		PrerequisiteWriterInterface test = null;
		if (kind == null)
		{
			test = new PrerequisiteMultWriter();
		}
		else
		{
			test = (PrerequisiteWriterInterface) parserLookup.get(kind.toLowerCase());
			if (test == null) {
				Logging.errorPrintLocalised("PrerequisiteTestFactory.error.cannot_find_test", kind); //$NON-NLS-1$
			}
		}
		return test;
	}


	public static void register(PrerequisiteWriterInterface testClass) throws PersistenceLayerException
	{
		String kindHandled = testClass.kindHandled();

		Object test = parserLookup.get(kindHandled.toLowerCase());

		if (test != null)
		{
			throw new PersistenceLayerException("Error registering '" + testClass.getClass().getName()
					+ "' as test '" + kindHandled + "'. The test is already registered to '"
					+ test.getClass().getName() + "'");
		}

		parserLookup.put(kindHandled.toLowerCase(), testClass);
	}



}
