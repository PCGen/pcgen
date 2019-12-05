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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Locale;
import java.util.Map;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.DataSetID;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.ObjectWrapperFacet;
import pcgen.output.publish.OutputDB;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.junit.jupiter.api.BeforeEach;

public abstract class AbstractOutputTestCase
{
    protected DataSetID dsid;
    protected CharID id;

    @BeforeEach
    public void setUp() throws Exception
    {
        Locale.setDefault(Locale.US);
        dsid = DataSetID.getID();
        FacetLibrary.getFacet(ObjectWrapperFacet.class).initialize(dsid);
        id = CharID.getID(dsid);
    }

    protected void processThroughFreeMarker(String testString,
            String expectedResult)
    {
        try
        {
            Configuration c = new Configuration(Configuration.VERSION_2_3_28);
            Template t = new Template("test", testString, c);
            StringWriter sw = new StringWriter();
            BufferedWriter bw = new BufferedWriter(sw);
            Map<String, Object> input = OutputDB.buildDataModel(id);
            t.process(input, bw);
            String s = sw.getBuffer().toString();
            assertEquals(expectedResult, s);
        } catch (IOException | TemplateException e)
        {
            e.printStackTrace();
            fail(e.getLocalizedMessage());
        }
    }


}
