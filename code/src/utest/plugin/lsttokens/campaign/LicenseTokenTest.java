/*
 * Copyright (c) 2007 Tom Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.campaign;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.junit.Test;

import pcgen.cdom.enumeration.ListKey;
import pcgen.core.Campaign;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractCDOMTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;

public class LicenseTokenTest extends AbstractCDOMTokenTestCase<Campaign>
{

	static LicenseToken token = new LicenseToken();
	static CDOMTokenLoader<Campaign> loader = new CDOMTokenLoader<>();

	@Override
	public Class<Campaign> getCDOMClass()
	{
		return Campaign.class;
	}

	@Override
	public CDOMLoader<Campaign> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMPrimaryToken<Campaign> getToken()
	{
		return token;
	}

	public ListKey<?> getListKey()
	{
		return ListKey.LICENSE;
	}

	@Override
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
		URI uri = new URI("http://www.sourceforge.net");
		primaryContext.setSourceURI(uri);
		secondaryContext.setSourceURI(uri);
	}

	@Test
	public void testValidInputSimple() throws PersistenceLayerException
	{
		ListKey<?> listKey = getListKey();
		if (listKey != null)
		{
			List<?> coll;
			assertTrue(parse("Rheinhessen"));
			coll = primaryProf.getListFor(listKey);
			assertEquals(1, coll.size());
			assertTrue(coll.contains("Rheinhessen"));
			assertCleanConstruction();
		}
	}

	@Test
	public void testValidInputNonEnglish() throws PersistenceLayerException
	{
		ListKey<?> listKey = getListKey();
		if (listKey != null)
		{
			List<?> coll;
			assertTrue(parse("Niederösterreich"));
			coll = primaryProf.getListFor(listKey);
			assertEquals(1, coll.size());
			assertTrue(coll.contains("Niederösterreich"));
			assertCleanConstruction();
		}
	}

	@Test
	public void testValidInputSpace() throws PersistenceLayerException
	{
		ListKey<?> listKey = getListKey();
		if (listKey != null)
		{
			List<?> coll;
			assertTrue(parse("Finger Lakes"));
			coll = primaryProf.getListFor(listKey);
			assertEquals(1, coll.size());
			assertTrue(coll.contains("Finger Lakes"));
			assertCleanConstruction();
		}
	}

	@Test
	public void testValidInputHyphen() throws PersistenceLayerException
	{
		ListKey<?> listKey = getListKey();
		if (listKey != null)
		{
			List<?> coll;
			assertTrue(parse("Languedoc-Roussillon"));
			coll = primaryProf.getListFor(listKey);
			assertEquals(1, coll.size());
			assertTrue(coll.contains("Languedoc-Roussillon"));
			assertCleanConstruction();
		}
	}

	@Test
	public void testValidInputY() throws PersistenceLayerException
	{
		ListKey<?> listKey = getListKey();
		if (listKey != null)
		{
			List<?> coll;
			assertTrue(parse("Yarra Valley"));
			coll = primaryProf.getListFor(listKey);
			assertEquals(1, coll.size());
			assertTrue(coll.contains("Yarra Valley"));
			assertCleanConstruction();
		}
	}

	@Test
	public void testValidInputList() throws PersistenceLayerException
	{
		ListKey<?> listKey = getListKey();
		if (listKey != null)
		{
			List<?> coll;
			assertTrue(parse("Niederösterreich"));
			assertTrue(parse("Finger Lakes"));
			coll = primaryProf.getListFor(listKey);
			assertEquals(2, coll.size());
			assertTrue(coll.contains("Niederösterreich"));
			assertTrue(coll.contains("Finger Lakes"));
			assertCleanConstruction();
		}
	}

	@Test
	public void testValidInputMultList() throws PersistenceLayerException
	{
		ListKey<?> listKey = getListKey();
		if (listKey != null)
		{
			List<?> coll;
			assertTrue(parse("Niederösterreich"));
			assertTrue(parse("Finger Lakes"));
			assertTrue(parse("Languedoc-Roussillon"));
			assertTrue(parse("Rheinhessen"));
			coll = primaryProf.getListFor(listKey);
			assertEquals(4, coll.size());
			assertTrue(coll.contains("Niederösterreich"));
			assertTrue(coll.contains("Finger Lakes"));
			assertTrue(coll.contains("Languedoc-Roussillon"));
			assertTrue(coll.contains("Rheinhessen"));
			assertCleanConstruction();
		}
	}

	@Test
	public void testInvalidListEmpty() throws PersistenceLayerException
	{
		assertFalse(parse(""));
		assertNull(primaryProf.getListFor(getListKey()));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidEmpty() throws PersistenceLayerException
	{
		assertFalse(parse(""));
		assertNull(primaryProf.getListFor(getListKey()));
		assertNoSideEffects();
	}

	@Test
	public void testRoundRobinBase() throws PersistenceLayerException
	{
		runRoundRobin("Rheinhessen");
	}

	@Test
	public void testRoundRobinWithSpace() throws PersistenceLayerException
	{
		runRoundRobin("Finger Lakes");
	}

	@Test
	public void testRoundRobinNonEnglishAndN() throws PersistenceLayerException
	{
		runRoundRobin("Niederösterreich");
	}

	@Test
	public void testRoundRobinHyphen() throws PersistenceLayerException
	{
		runRoundRobin("Languedoc-Roussillon");
	}

	@Test
	public void testRoundRobinY() throws PersistenceLayerException
	{
		runRoundRobin("Yarra Valley");
	}

	@Test
	public void testRoundRobinThree() throws PersistenceLayerException
	{
		runRoundRobin("Languedoc-Roussillon", "Rheinhessen", "Yarra Valley");
	}

	@Test
	public void testRoundRobinThreeDupe() throws PersistenceLayerException
	{
		runRoundRobin("Languedoc-Roussillon", "Rheinhessen", "Rheinhessen");
	}

	@Test
	public void testInvalidListFileEmpty() throws PersistenceLayerException
	{
		assertFalse(parse("FILE="));
		assertNull(primaryProf.getListFor(getListKey()));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidFileEmpty() throws PersistenceLayerException
	{
		assertFalse(parse("FILE="));
		assertNull(primaryProf.getListFor(getListKey()));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidNotACSE() throws PersistenceLayerException
	{
		assertFalse(parse("FILE=Not|aCSE"));
		assertNull(primaryProf.getListFor(getListKey()));
		assertNoSideEffects();
	}

	@Test
	public void testRoundRobinFileBase() throws PersistenceLayerException
	{
		runRoundRobin("FILE=@data/Rheinhessen");
	}

	@Test
	public void testRoundRobinFileURL() throws PersistenceLayerException
	{
		runRoundRobin("FILE=http://pcgen.sourceforge.net/Finger%20Lakes");
	}

	@Test
	public void testRoundRobinNonEnglishAndNFile()
			throws PersistenceLayerException
	{
		runRoundRobin("FILE=@data/Niederösterreich");
	}

	@Test
	public void testRoundRobinHyphenFile() throws PersistenceLayerException
	{
		runRoundRobin("FILE=@data/Languedoc-Roussillon");
	}

	@Test
	public void testRoundRobinYFile() throws PersistenceLayerException
	{
		runRoundRobin("FILE=@data/Yarra Valley");
	}

	@Test
	public void testRoundRobinThreeFile() throws PersistenceLayerException
	{
		runRoundRobin("FILE=@data/Languedoc-Roussillon",
				"FILE=@data/Rheinhessen", "FILE=@data/Yarra Valley");
	}

	@Test
	public void testRoundRobinThreeFileDupe() throws PersistenceLayerException
	{
		runRoundRobin("FILE=@data/Languedoc-Roussillon",
				"FILE=@data/Rheinhessen", "FILE=@data/Rheinhessen");
	}

	@Override
	protected String getAlternateLegalValue()
	{
		return "FILE=@data/Languedoc-Roussillon";
	}

	@Override
	protected String getLegalValue()
	{
		return "FILE=@data/Yarra Valley";
	}

	@Override
	protected ConsolidationRule getConsolidationRule()
	{
		return ConsolidationRule.SEPARATE;
	}
}
