/*
 * 
 * Copyright (c) 2010 Tom Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.choose;

import java.net.URISyntaxException;
import java.util.Arrays;

import org.junit.Test;

import pcgen.cdom.base.BasicChooseInformation;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.ChooseInformation;
import pcgen.cdom.base.Constants;
import pcgen.cdom.choiceset.SimpleChoiceSet;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.PCTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.ChooseLst;
import plugin.lsttokens.testsupport.AbstractCDOMTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;
import plugin.lsttokens.testsupport.TokenRegistration;

public class StringTokenTest extends AbstractCDOMTokenTestCase<CDOMObject>
{

	static ChooseLst token = new ChooseLst();
	static StringToken subtoken = new StringToken();
	static CDOMTokenLoader<CDOMObject> loader = new CDOMTokenLoader<>();

	@Override
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
		TokenRegistration.register(subtoken);
	}

	private String getSubTokenName()
	{
		return "STRING";
	}

	@Override
	public Class<PCTemplate> getCDOMClass()
	{
		return PCTemplate.class;
	}

	@Override
	public CDOMLoader<CDOMObject> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMPrimaryToken<CDOMObject> getToken()
	{
		return token;
	}

	@Test
	public void testInvalidInputEmptyString() throws PersistenceLayerException
	{
		assertFalse(parse(""));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputOnlySubToken() throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenName()));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputOnlySubTokenPipe()
			throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenName() + '|'));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputJoinOnly() throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenName() + "|,"));
		assertNoSideEffects();
	}

	protected boolean requiresLiteral()
	{
		return false;
	}

	@Test
	public void testInvalidListEndPipe() throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenName() + '|' + "TestWP1|"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidListEndComma() throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenName() + '|' + "TestWP1,"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidListStartPipe() throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenName() + '|' + "|TestWP1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidListStartComma() throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenName() + '|' + ",TestWP1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidListDoubleJoinPipe()
			throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenName() + '|' + "TestWP2||TestWP1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidListDoubleJoinComma()
			throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenName() + '|' + "TYPE=Foo,,!TYPE=Bar"));
		assertNoSideEffects();
	}

	@Test
	public void testValidInputs() throws PersistenceLayerException
	{
		assertTrue(parse(getSubTokenName() + '|' + "TestWP1"));
		assertCleanConstruction();
		assertTrue(parse(getSubTokenName() + '|' + "TestWP1|TestWP2"));
		assertCleanConstruction();
	}

	@Test
	public void testRoundRobinOne() throws PersistenceLayerException
	{
		runRoundRobin(getSubTokenName() + '|' + "TestWP1");
	}

	@Test
	public void testRoundRobinParen() throws PersistenceLayerException
	{
		runRoundRobin(getSubTokenName() + '|' + "TestWP1 (Test)");
	}

	@Test
	public void testRoundRobinThree() throws PersistenceLayerException
	{
		runRoundRobin(getSubTokenName() + '|' + "TestWP1|TestWP2|TestWP3");
	}

	@Test
	public void testInputInvalidAddsBasicNoSideEffect()
			throws PersistenceLayerException
	{
		assertTrue(parse(getSubTokenName() + '|' + "TestWP1|TestWP2"));
		assertTrue(parseSecondary(getSubTokenName() + '|' + "TestWP1|TestWP2"));
		assertFalse(parse(getSubTokenName() + '|' + "TestWP3||TestWP4"));
		assertNoSideEffects();
	}

	@Test
	public void testUnparseNull() throws PersistenceLayerException
	{
		primaryProf.put(getObjectKey(), null);
		assertNull(getToken().unparse(primaryContext, primaryProf));
	}

	private ObjectKey<ChooseInformation<?>> getObjectKey()
	{
		return ObjectKey.CHOOSE_INFO;
	}

	@Test
	public void testUnparseLegal() throws PersistenceLayerException
	{
		assertGoodChoose("TestWP1|TestWP2");
	}

	private void assertGoodChoose(String value)
	{
		parseForUnparse(value);
		String[] unparse = getToken().unparse(primaryContext, primaryProf);
		assertNotNull(unparse);
		assertEquals(1, unparse.length);
		assertEquals(unparse[0], getSubTokenName() + "|" + value);
	}

	private void parseForUnparse(String value)
	{
		SimpleChoiceSet<String> scs = new SimpleChoiceSet<>(Arrays
				.asList(value.split("\\|")), Constants.PIPE);
		assertTrue(scs.getGroupingState().isValid());
		BasicChooseInformation<String> cs = new BasicChooseInformation<>(getSubTokenName(), scs);
		cs.setTitle("Choose an Item");
		primaryProf.put(ObjectKey.CHOOSE_INFO, cs);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testUnparseGenericsFail() throws PersistenceLayerException
	{
		ObjectKey objectKey = getObjectKey();
		primaryProf.put(objectKey, new Object());
		try
		{
			getToken().unparse(primaryContext, primaryProf);
			fail();
		}
		catch (ClassCastException e)
		{
			// Yep!
		}
	}

	@Override
	protected String getAlternateLegalValue()
	{
		return "STRING|Foo|Zoo";
	}

	@Override
	protected ConsolidationRule getConsolidationRule()
	{
		return ConsolidationRule.OVERWRITE;
	}

	@Override
	protected String getLegalValue()
	{
		return "STRING|Bar|Baz";
	}

	@Test
	public void testInvalidInputNoBrackets() throws PersistenceLayerException
	{
		assertFalse(parse("STRING|Sorry No [Brackets]"));
		assertNoSideEffects();
	}

}
