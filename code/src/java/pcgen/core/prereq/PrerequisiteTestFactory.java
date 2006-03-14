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
 *
 * Created on November 28, 2003
 *
 * Current Ver: $Revision$ Last Editor: $Author$ Last Edited: $Date$
 *
 */
package pcgen.core.prereq;

import java.util.HashMap;
import java.util.Map;

import pcgen.util.Logging;
import pcgen.util.PropertyFactory;

/**
 * @author wardc
 *
 */
public class PrerequisiteTestFactory {
	private static PrerequisiteTestFactory instance = null;
	private static Map testLookup = new HashMap();

	/**
	 * @return Returns the instance.
	 */
	public static PrerequisiteTestFactory getInstance() {
		if (instance == null)
			instance = new PrerequisiteTestFactory();
		return instance;
	}

	private PrerequisiteTestFactory() {
	}

	public static void register(final PrerequisiteTest testClass) {
		final String kindHandled = testClass.kindHandled();
			final Object test = testLookup.get(kindHandled);
			if (test != null) {
				Logging.errorPrint(
					PropertyFactory.getFormattedString("PrerequisiteTestFactory.error.already_registered", //$NON-NLS-1$
						testClass.getClass().getName(),
						kindHandled,
						test.getClass().getName() ));
			}
			testLookup.put(kindHandled.toUpperCase(), testClass);
	}

	public PrerequisiteTest getTest(final String kind) {
		PrerequisiteTest test;
		if (kind == null)
		{
			test = new PreMult();
		}
		else
		{
			test = (PrerequisiteTest) testLookup.get(kind.toUpperCase());
			if (test==null) {
				Logging.errorPrintLocalised("PrerequisiteTestFactory.error.cannot_find_test", kind); //$NON-NLS-1$
			}
		}
		return test;
	}



}
