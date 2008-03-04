package plugin.pretokens;

import java.io.StringWriter;

import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriterFactory;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriterInterface;
import pcgen.persistence.lst.prereq.PreParserFactory;
import junit.framework.TestCase;

public class AbstractPreRoundRobin extends TestCase
{
	public void runRoundRobin(String s)
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
			assertEquals(s, w.toString());
		}
		catch (PersistenceLayerException e)
		{
			fail(e.getLocalizedMessage());
		}
	}
}
