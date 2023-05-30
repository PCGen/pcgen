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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.StringWriter;
import java.util.Collection;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.inst.ObjectCache;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriterFactory;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriterInterface;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.rules.context.EditorLoadContext;
import pcgen.rules.context.LoadContext;
import pcgen.util.Logging;
import plugin.lsttokens.testsupport.TokenRegistration;

import org.junit.jupiter.api.BeforeEach;
import plugin.pretokens.parser.PreMultParser;

public abstract class AbstractPreRoundRobin
{
	@BeforeEach
	void setUp() throws Exception
	{
		TokenRegistration.clearTokens();
		TokenRegistration.register(new PreMultParser()); // Used in many nested tests
	}

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

	public static void runSimpleRoundRobin(String s, String d)
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
			/*
			 * Now try new system
			 */
			LoadContext context = new EditorLoadContext();
			CDOMObject obj = new ObjectCache();
			int colonLoc = s.indexOf(':');
			String key = s.substring(0, colonLoc);
			String value = s.substring(colonLoc + 1);
			if (context.processToken(obj, key, value))
			{
				context.commit();
			}
			else
			{
				context.rollback();
				Logging.replayParsedMessages();
				fail();
			}
			Logging.clearParseMessages();
			Collection<String> output = context.unparse(obj);
			assertArrayEquals(new String[]{d}, output.toArray());
		}
		catch (PersistenceLayerException e)
		{
			e.printStackTrace();
			fail(e::getLocalizedMessage);
		}
	}

}
