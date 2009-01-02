/*
 * Copyright (c) 2008 Tom Parker <thpr@users.sourceforge.net>
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
package plugin.pretokens;

import java.io.StringWriter;

import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriterFactory;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriterInterface;
import pcgen.persistence.lst.prereq.PreParserFactory;
import junit.framework.TestCase;

public abstract class AbstractPreRoundRobin extends TestCase
{
	public final void runRoundRobin(String s)
	{
		runPositiveRoundRobin(s);
		runNegativeRoundRobin(s);
	}

	public void runPositiveRoundRobin(String s)
	{
		runSimpleRoundRobin(s, s);
	}

	public void runNegativeRoundRobin(String s)
	{
		runSimpleRoundRobin("!" + s, "!" + s);
	}

	public void runSimpleRoundRobin(String s, String d)
	{
		try
		{
			Prerequisite p = PreParserFactory.getInstance().parse(s);
			PrerequisiteWriterInterface writer = PrerequisiteWriterFactory
					.getInstance().getWriter(p.getKind());
			if (writer == null)
			{
				fail("Could not find Writer for: " + p.getKind());
			}
			StringWriter w = new StringWriter();
			writer.write(w, p);
			assertEquals(d, w.toString());
		}
		catch (PersistenceLayerException e)
		{
			fail(e.getLocalizedMessage());
		}
	}
}
