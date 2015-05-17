/*
 * 
 * Copyright (c) 2007 Tom Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.auto;

import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;

import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMList;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.SimpleAssociatedObject;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.enumeration.Nature;
import pcgen.cdom.list.AbilityList;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.cdom.reference.CDOMGroupRef;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.PCTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import plugin.lsttokens.AutoLst;
import plugin.lsttokens.testsupport.AbstractSelectionTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.pretokens.parser.PreClassParser;
import plugin.pretokens.parser.PreRaceParser;
import plugin.pretokens.writer.PreClassWriter;
import plugin.pretokens.writer.PreRaceWriter;

public class FeatTokenTest extends
		AbstractSelectionTokenTestCase<CDOMObject, Ability>
{
	private static final AbilityCategory FEAT = AbilityCategory.FEAT;
	private static final Nature AUTOMATIC = Nature.AUTOMATIC;

	PreClassParser preclass = new PreClassParser();
	PreClassWriter preclasswriter = new PreClassWriter();
	PreRaceParser prerace = new PreRaceParser();
	PreRaceWriter preracewriter = new PreRaceWriter();

	@Override
	@Before
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
		TokenRegistration.register(preclass);
		TokenRegistration.register(preclasswriter);
		TokenRegistration.register(prerace);
		TokenRegistration.register(preracewriter);
	}

	static AutoLst token = new AutoLst();
	static FeatToken subtoken = new FeatToken();
	static CDOMTokenLoader<CDOMObject> loader = new CDOMTokenLoader<CDOMObject>();

	@Override
	protected Ability construct(LoadContext loadContext, String one)
	{
		Ability obj = loadContext.getReferenceContext().constructCDOMObject(Ability.class, one);
		loadContext.getReferenceContext().reassociateCategory(AbilityCategory.FEAT, obj);
		return obj;
	}

	@Override
	protected CDOMObject constructTyped(LoadContext loadContext, String one)
	{
		Ability obj = loadContext.getReferenceContext().constructCDOMObject(Ability.class, one);
		loadContext.getReferenceContext().reassociateCategory(AbilityCategory.FEAT, obj);
		return obj;
	}

	@Override
	public char getJoinCharacter()
	{
		return '|';
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

	@Override
	public CDOMSecondaryToken<?> getSubToken()
	{
		return subtoken;
	}

	@Override
	public Class<Ability> getTargetClass()
	{
		return Ability.class;
	}

	@Override
	public boolean isAllLegal()
	{
		return false;
	}

	@Override
	public boolean isTypeLegal()
	{
		return true;
	}

	@Test
	public void testEmpty()
	{
		// Just to get Eclipse to recognize this as a JUnit 4.0 Test Case
	}

	@Override
	public boolean allowsParenAsSub()
	{
		return false;
	}

	@Override
	public boolean allowsFormula()
	{
		return false;
	}

	@Test
	public void testInvalidEmptyPre() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse(getSubTokenName() + '|' + "TestWP1|"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidLateClear() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse(getSubTokenName() + '|' + "TestWP1|.CLEAR"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidOnlyPre() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse(getSubTokenName() + '|' + "PRERACE:1,Dwarf"));
		assertNoSideEffects();
	}

	@Test
	public void testRoundRobinOnePre() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		construct(secondaryContext, "TestWP1");
		construct(secondaryContext, "TestWP2");
		runRoundRobin(getSubTokenName() + '|' + "TestWP1|PRERACE:1,Dwarf");
	}

	@Test
	public void testRoundRobinTwoPre() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		construct(secondaryContext, "TestWP1");
		construct(secondaryContext, "TestWP2");
		runRoundRobin(getSubTokenName() + '|'
				+ "TestWP1|PRECLASS:1,Fighter=3|PRERACE:1,Dwarf");
	}

	@Test
	public void testRoundRobinDupeTwoPrereqs() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		construct(secondaryContext, "TestWP1");
		construct(secondaryContext, "TestWP2");
		runRoundRobin(getSubTokenName() + '|' + "TestWP1|PRERACE:1,Dwarf",
				getSubTokenName() + '|' + "TestWP1|PRERACE:1,Human");
	}

	@Test
	public void testInvalidInputBadPrerequisite()
			throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(secondaryContext, "TestWP1");
		assertFalse(parse(getSubTokenName() + '|' + "TestWP1|PREFOO:1,Human"));
		assertNoSideEffects();
	}

	@Test
	public void testRoundRobinDupe() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(secondaryContext, "TestWP1");
		runRoundRobin(getSubTokenName() + '|' + "TestWP1|TestWP1");
	}

	@Test
	public void testRoundRobinListParen() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(secondaryContext, "TestWP1");
		runRoundRobin(getSubTokenName() + '|' + "TestWP1 (%LIST)");
	}

	@Override
	protected ConsolidationRule getConsolidationRule()
	{
		return new ConsolidationRule()
		{

            @Override
			public String[] getAnswer(String... strings)
			{
				return new String[] { "FEAT|TestWP1|TestWP1|TestWP2|TestWP2|TestWP3" };
			}
		};
	}

	@Test
	public void testUnparseNull() throws PersistenceLayerException
	{
		primaryProf.removeAllFromList(getListReference());
		assertNull(getToken().unparse(primaryContext, primaryProf));
	}

	private CDOMReference<? extends CDOMList<?>> getListReference()
	{
		return AbilityList.getAbilityListReference(FEAT, AUTOMATIC);
	}

	@Test
	public void testUnparseSingle() throws PersistenceLayerException
	{
		Ability wp1 = construct(primaryContext, "TestWP1");
		addToList(CDOMDirectSingleRef.getRef(wp1));
		String[] unparsed = getToken().unparse(primaryContext, primaryProf);
		expectSingle(unparsed, "TestWP1");
	}

	@Test
	public void testUnparseMultiple() throws PersistenceLayerException
	{
		Ability wp1 = construct(primaryContext, "TestWP1");
		addToList(CDOMDirectSingleRef.getRef(wp1));
		Ability wp2 = construct(primaryContext, "TestWP2");
		addToList(CDOMDirectSingleRef.getRef(wp2));
		String[] unparsed = getToken().unparse(primaryContext, primaryProf);
		expectSingle(unparsed, "TestWP1" + getJoinCharacter() + "TestWP2");
	}

	@Test
	public void testUnparseDupe() throws PersistenceLayerException
	{
		Ability wp1 = construct(primaryContext, "TestWP1");
		addToList(CDOMDirectSingleRef.getRef(wp1));
		addToList(CDOMDirectSingleRef.getRef(wp1));
		String[] unparsed = getToken().unparse(primaryContext, primaryProf);
		expectSingle(unparsed, "TestWP1" + getJoinCharacter() + "TestWP1");
	}

	@Test
	public void testUnparseNullInList() throws PersistenceLayerException
	{
		addToList(null);
		try
		{
			getToken().unparse(primaryContext, primaryProf);
			fail();
		}
		catch (NullPointerException e)
		{
			// Yep!
		}
	}

	@Test
	public void testUnparseType() throws PersistenceLayerException
	{
		if (isTypeLegal())
		{
			CDOMGroupRef<Ability> tr = getTypeReference();
			addToList(tr);
			String[] unparsed = getToken().unparse(primaryContext, primaryProf);
			expectSingle(unparsed, tr.getLSTformat(false));
		}
	}

	protected CDOMGroupRef<Ability> getTypeReference()
	{
		return primaryContext.getReferenceContext().getCDOMTypeReference(getTargetClass(), FEAT,
				"Type1");
	}

	@Test
	public void testUnparseAll() throws PersistenceLayerException
	{
		if (isTypeLegal())
		{
			CDOMGroupRef<Ability> allReference = getAllReference();
			addToList(allReference);
			String[] unparsed = getToken().unparse(primaryContext, primaryProf);
			expectSingle(unparsed, getAllString());
		}
	}

	protected CDOMGroupRef<Ability> getAllReference()
	{
		return primaryContext.getReferenceContext().getCDOMAllReference(getTargetClass(), FEAT);
	}

	/*
	 * TODO Need to figure out who owns this responsibility
	 */
	// @Test
	// public void testUnparseGenericsFail() throws PersistenceLayerException
	// {
	// CDOMReference listKey = getListReference();
	// SimpleAssociatedObject sao = new SimpleAssociatedObject();
	// sao.setAssociation(AssociationKey.TOKEN, getToken().getTokenName()
	// + ":" + subtoken.getTokenName());
	// primaryProf.putToList(listKey, CDOMDirectSingleRef
	// .getRef(primaryContext.ref.constructCDOMObject(Domain.class,
	// "DomainItem")), sao);
	// doCustomAssociations(sao);
	// try
	// {
	// getToken().unparse(primaryContext, primaryProf);
	// fail();
	// }
	// catch (ClassCastException e)
	// {
	// // Yep!
	// }
	// }

	protected AssociatedPrereqObject addToList(CDOMReference<Ability> val)
	{
		SimpleAssociatedObject sao = new SimpleAssociatedObject();
		sao.setAssociation(AssociationKey.TOKEN, getToken().getTokenName()
				+ ":" + subtoken.getTokenName());
		primaryProf.putToList(getListReference(), val, sao);
		doCustomAssociations(sao);
		return sao;
	}

	protected void doCustomAssociations(AssociatedPrereqObject apo)
	{
		apo.setAssociation(AssociationKey.NATURE, AUTOMATIC);
		apo.setAssociation(AssociationKey.CATEGORY, FEAT);
	}

	@Override
	protected void expectSingle(String[] unparsed, String expected)
	{
		super.expectSingle(unparsed, "FEAT|" + expected);
	}

}
