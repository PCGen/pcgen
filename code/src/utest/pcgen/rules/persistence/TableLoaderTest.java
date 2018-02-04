package pcgen.rules.persistence;

import java.net.URI;

import pcgen.base.format.NumberManager;
import pcgen.base.format.StringManager;
import pcgen.cdom.format.table.DataTable;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.ConsolidatedListCommitStrategy;
import pcgen.rules.context.LoadContext;
import pcgen.rules.context.RuntimeLoadContext;
import pcgen.rules.context.RuntimeReferenceContext;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class TableLoaderTest
{

	private LoadContext context;
	private URI uri;
	private TableLoader loader;

	@Before
	public void setUp() throws Exception
	{
		uri = new URI("file:/Test%20Case");
		context = new RuntimeLoadContext(RuntimeReferenceContext.createRuntimeReferenceContext(),
			new ConsolidatedListCommitStrategy());
		loader = new TableLoader();
	}

	@Test
	public void testNoNames()
	{
		try
		{
			loader.loadLstString(context, uri, "STARTTABLE:A\nENDTABLE:A");
			fail("Expected Failure");
		}
		catch (PersistenceLayerException e)
		{
			//Yes
		}
	}

	@Test
	public void testNoFormats()
	{
		try
		{
			loader.loadLstString(context, uri,
				"STARTTABLE:A\nName1,Name2ENDTABLE:A");
			fail("Expected Failure");
		}
		catch (PersistenceLayerException e)
		{
			//Yes
		}
	}

	@Test
	public void testNoData()
	{
		try
		{
			loader.loadLstString(context, uri,
				"STARTTABLE:A\nName1,Name2\nSTRING,NUMBER\nENDTABLE:A");
			fail("Expected Failure");
		}
		catch (PersistenceLayerException e)
		{
			//Yes
		}
	}

	@Test
	public void testTwoStartAtData()
	{
		try
		{
			loader.loadLstString(context, uri,
				"STARTTABLE:A\nName\nSTRING\nSTARTTABLE:B");
			fail("Expected Failure");
		}
		catch (PersistenceLayerException e)
		{
			//Yes
		}
	}

	@Test
	public void testTwoStartAtFormat()
	{
		try
		{
			loader.loadLstString(context, uri,
				"STARTTABLE:A\nName\nSTARTTABLE:B");
			fail("Expected Failure");
		}
		catch (PersistenceLayerException e)
		{
			//Yes
		}
	}

	@Test
	public void testTwoStart()
	{
		try
		{
			loader.loadLstString(context, uri, "STARTTABLE:A\nSTARTTABLE:B");
			fail("Expected Failure");
		}
		catch (PersistenceLayerException e)
		{
			//Yes
		}
	}

	@Test
	public void testNoEnd()
	{
		try
		{
			loader.loadLstString(context, uri, "STARTTABLE:A\nName\nSTRING\n");
			fail("Expected Failure");
		}
		catch (PersistenceLayerException e)
		{
			//Yes
		}
	}

	@Test
	public void testNoStart()
	{
		try
		{
			loader.loadLstString(context, uri, "\nName\nSTRING\n");
			fail("Expected Failure");
		}
		catch (PersistenceLayerException e)
		{
			//Yes
		}
	}

	@Test
	public void testResetOnEnd()
	{
		try
		{
			loader.loadLstString(context, uri,
				"STARTTABLE:A\nName\nSTRING\nFoo\nENDTABLE:A\nBar");
			fail("Expected Failure");
		}
		catch (PersistenceLayerException e)
		{
			//Yes
		}
	}

	@Test
	public void testBadEnd()
	{
		try
		{
			loader.loadLstString(context, uri,
				"STARTTABLE:A\nName\nSTRING\nFoo\nENDTABLE:A,BCD");
			fail("Expected Failure");
		}
		catch (PersistenceLayerException e)
		{
			//Yes
		}
	}

	@Test
	public void testBadStart()
	{
		try
		{
			loader.loadLstString(context, uri,
				"STARTTABLE:A,,,Problem\nName\nSTRING\nFoo\nENDTABLE:A\n");
			fail("Expected Failure");
		}
		catch (PersistenceLayerException e)
		{
			//Yes
		}
	}

	@Test
	public void testSkippedName()
	{
		try
		{
			loader.loadLstString(context, uri,
				"STARTTABLE:A\nName,,Value2\nSTRING,NUMBER,NUMBER\nFoo,1,2\nENDTABLE:A\n");
			fail("Expected Failure");
		}
		catch (PersistenceLayerException e)
		{
			//Yes
		}
	}

	@Test
	public void testMissingName()
	{
		try
		{
			loader.loadLstString(context, uri,
				"STARTTABLE:A\nName,\nSTRING,NUMBER\nFoo,1\nENDTABLE:A\n");
			fail("Expected Failure");
		}
		catch (PersistenceLayerException e)
		{
			//Yes
		}
	}

	@Test
	public void testMissingName2()
	{
		try
		{
			//Doesn't need to have the comma
			loader.loadLstString(context, uri,
				"STARTTABLE:A\nName\nSTRING,NUMBER\nFoo,1\nENDTABLE:A\n");
			fail("Expected Failure");
		}
		catch (PersistenceLayerException e)
		{
			//Yes
		}
	}

	@Test
	public void testSkippedFormat()
	{
		try
		{
			loader.loadLstString(context, uri,
				"STARTTABLE:A\nName,Value1,Value2\nSTRING,,NUMBER\nFoo,1,2\nENDTABLE:A\n");
			fail("Expected Failure");
		}
		catch (PersistenceLayerException e)
		{
			//Yes
		}
	}

	@Test
	public void testMissingFormat()
	{
		try
		{
			loader.loadLstString(context, uri,
				"STARTTABLE:A\nName,Value\nSTRING,\nFoo,1\nENDTABLE:A\n");
			fail("Expected Failure");
		}
		catch (PersistenceLayerException e)
		{
			//Yes
		}
	}

	@Test
	public void testMissingFormat2()
	{
		try
		{
			//Doesn't need to have the comma
			loader.loadLstString(context, uri,
				"STARTTABLE:A\nName,Value\nSTRING\nFoo,1\nENDTABLE:A\n");
			fail("Expected Failure");
		}
		catch (PersistenceLayerException e)
		{
			//Yes
		}
	}

	@Test
	public void testTooMuchData()
	{
		try
		{
			loader.loadLstString(context, uri,
				"STARTTABLE:A\nName,Value\nSTRING,NUMBER\nFoo,1,2\nENDTABLE:A\n");
			fail("Expected Failure");
		}
		catch (PersistenceLayerException | IndexOutOfBoundsException | IllegalArgumentException e)
		{
			//Yes
		}
	}

	@Test
	public void testBadFormat()
	{
		try
		{
			loader.loadLstString(context, uri,
				"STARTTABLE:A\nName,Value\nSTRING,NIMBLER\nFoo,1\nENDTABLE:A\n");
			fail("Expected Failure");
		}
		catch (NullPointerException | PersistenceLayerException | IllegalArgumentException e)
		{
			//Yes
		}
	}

	@Test
	public void testDataIncorrectFormat()
	{
		try
		{
			loader.loadLstString(context, uri,
				"STARTTABLE:A\nName,Value\nSTRING,NUMBER\nFoo,Bar\nENDTABLE:A\n");
			fail("Expected Failure");
		}
		catch (PersistenceLayerException | IllegalArgumentException e)
		{
			//Yes
		}
	}

	//TODO Blank Data in middle of Row?
	//TODO Blank Data at end of row?

	@Test
	public void testBasic()
	{
		try
		{
			loader.loadLstString(context, uri,
				"#Let me tell you about this table\n\n,,,\n"
					+ "STARTTABLE:A\n\n,,,\n"
					+ "#It's the story of a parsing test\n"
					+ "Name,Value,\n\n,,,\n"
					+ "\"#And testing tolerance\",For lots of things\n"
					+ "STRING,NUMBER,,\n\n,,,\n\n" + "#Because really....\n"
					+ "This,1\n\n" + "#Why call the comments?\n,,,\n"
					+ "\"That\",\"2\"\n"
					+ "\"The \"\"Other\"\"\",\"3\"\n,,,\n\n" + "ENDTABLE:A\n"
					+ "#They seem to just take up a lot of space");
			DataTable a = context.getReferenceContext()
				.silentlyGetConstructedCDOMObject(DataTable.class, "A");
			assertEquals(2, a.getColumnCount());
			assertEquals(new StringManager(), a.getFormat(0));
			assertEquals(new NumberManager(), a.getFormat(1));
			assertEquals("This", a.get("Name", 0));
			assertEquals("That", a.get("Name", 1));
			assertEquals("The \"Other\"", a.get("Name", 2));
			assertEquals(1, a.get("Value", 0));
			assertEquals(2, a.get("Value", 1));
			assertEquals(3, a.get("Value", 2));
			assertEquals(2, a.lookupExact("That", "Value"));
		}
		catch (PersistenceLayerException e)
		{
			fail("Did not Expect Failure: " + e.getLocalizedMessage());
		}
	}

	@Test
	public void testIndependence()
	{
		try
		{
			loader.loadLstString(context, uri,
				"STARTTABLE:A\n\n,,,\n"
					+ "Name,Value,\n\n,,,\n" + "STRING,NUMBER,,\n\n,,,\n\n"
					+ "This,1\n\n" + "\"That\",\"2\"\n" + "ENDTABLE:A\n"
					+ "#What comments?\n,,,\n"
					+ "STARTTABLE:B\n\n,,,\n"
					+ "Name,Value,\n\n,,,\n" + "STRING,NUMBER,,\n\n,,,\n\n"
					+ "\"The \"\"Other\"\"\",\"3\"\n,,,\n\n" + "ENDTABLE:B\n");
			DataTable a = context.getReferenceContext()
				.silentlyGetConstructedCDOMObject(DataTable.class, "A");
			DataTable b = context.getReferenceContext()
				.silentlyGetConstructedCDOMObject(DataTable.class, "B");
			assertEquals(2, a.getColumnCount());
			assertEquals(new StringManager(), a.getFormat(0));
			assertEquals(new NumberManager(), a.getFormat(1));
			assertEquals("This", a.get("Name", 0));
			assertEquals("That", a.get("Name", 1));
			assertEquals("The \"Other\"", b.get("Name", 0));
			assertEquals(1, a.get("Value", 0));
			assertEquals(2, a.get("Value", 1));
			assertEquals(3, b.get("Value", 0));
		}
		catch (PersistenceLayerException e)
		{
			fail("Did not Expect Failure: " + e.getLocalizedMessage());
		}
	}

	@Test
	public void testDuplicateIndependence()
	{
		try
		{
			loader.loadLstString(context, uri,
				"STARTTABLE:A\n\n,,,\n"
					+ "Name,Value,\n\n,,,\n" + "STRING,NUMBER,,\n\n,,,\n\n"
					+ "This,1\n\n" + "\"That\",\"2\"\n" + "ENDTABLE:A\n"
					+ "#What comments?\n,,,\n"
					+ "STARTTABLE:A\n\n,,,\n"
					+ "Name,Value,\n\n,,,\n" + "STRING,NUMBER,,\n\n,,,\n\n"
					+ "\"The \"\"Other\"\"\",\"3\"\n,,,\n\n" + "ENDTABLE:A\n");
			DataTable a = context.getReferenceContext()
				.silentlyGetConstructedCDOMObject(DataTable.class, "A");
			assertEquals(2, a.getColumnCount());
			assertEquals(new StringManager(), a.getFormat(0));
			assertEquals(new NumberManager(), a.getFormat(1));
			assertEquals("This", a.get("Name", 0));
			assertEquals("That", a.get("Name", 1));
			assertEquals(1, a.get("Value", 0));
			assertEquals(2, a.get("Value", 1));
			context.getReferenceContext().forget(a);
			DataTable b = context.getReferenceContext()
				.silentlyGetConstructedCDOMObject(DataTable.class, "A");
			assertEquals("The \"Other\"", b.get("Name", 0));
			assertEquals(3, b.get("Value", 0));
		}
		catch (PersistenceLayerException e)
		{
			fail("Did not Expect Failure: " + e.getLocalizedMessage());
		}
	}

	@Test
	public void testMismatchedFormat()
	{
		try
		{
			loader.loadLstString(context, uri,
				"STARTTABLE:A\n\n,,,\n"
					+ "Name,Value,\n\n,,,\n" + "STRING,NUMBER,,\n\n,,,\n\n"
					+ "This,1\n\n" + "\"That\",\"2\"\n" + "ENDTABLE:A\n"
					+ "#What comments?\n,,,\n"
					+ "STARTTABLE:B\n\n,,,\n"
					+ "Name,Value,\n\n,,,\n" + "STRING,STRING,,\n\n,,,\n\n"
					+ "\"The \"\"Other\"\"\",\"3\"\n,,,\n\n" + "ENDTABLE:A\n");
			fail("Expected Failure: Mismatched Formats");
		}
		catch (PersistenceLayerException e)
		{
			//yep
		}
	}
}
