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
