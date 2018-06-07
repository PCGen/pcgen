/*
 * Copyright (c) 2014-15 Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.output.testsupport;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Locale;
import java.util.Map;

import junit.framework.TestCase;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.DataSetID;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.ObjectWrapperFacet;
import pcgen.output.publish.OutputDB;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public abstract class AbstractOutputTestCase extends TestCase
{
	protected DataSetID dsid;
	protected CharID id;
	
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		Locale.setDefault(Locale.US);
		dsid = DataSetID.getID();
		FacetLibrary.getFacet(ObjectWrapperFacet.class).initialize(dsid);
		id = CharID.getID(dsid);
	}

	public void processThroughFreeMarker(String testString,
		String expectedResult)
	{
		try
		{
			Configuration c = new Configuration();
			Template t = new Template("test", testString, c);
			StringWriter sw = new StringWriter();
			BufferedWriter bw = new BufferedWriter(sw);
			Map<String, Object> input = OutputDB.buildDataModel(id);
			t.process(input, bw);
			String s = sw.getBuffer().toString();
			assertEquals(expectedResult, s);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			fail(e.getLocalizedMessage());
		}
		catch (TemplateException e)
		{
			e.printStackTrace();
			fail(e.getLocalizedMessage());
		}
	}


}
