package plugin.lsttokens.race;

import java.net.URISyntaxException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pcgen.core.Race;
import pcgen.core.SizeAdjustment;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.CDOMTokenLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractTokenTestCase;

public class SizeTokenTest extends AbstractTokenTestCase<Race>
{

	static SizeToken token = new SizeToken();
	static CDOMTokenLoader<Race> loader = new CDOMTokenLoader<Race>(Race.class);

	@Override
	public Class<Race> getCDOMClass()
	{
		return Race.class;
	}

	@Override
	public CDOMLoader<Race> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMPrimaryToken<Race> getToken()
	{
		return token;
	}

	@Override
	@Before
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
		SizeAdjustment ps = primaryContext.ref.constructCDOMObject(
				SizeAdjustment.class, "Small");
		primaryContext.ref.registerAbbreviation(ps, "S");
		SizeAdjustment pm = primaryContext.ref.constructCDOMObject(
				SizeAdjustment.class, "Medium");
		primaryContext.ref.registerAbbreviation(pm, "M");
		SizeAdjustment ss = secondaryContext.ref.constructCDOMObject(
				SizeAdjustment.class, "Small");
		secondaryContext.ref.registerAbbreviation(ss, "S");
		SizeAdjustment sm = secondaryContext.ref.constructCDOMObject(
				SizeAdjustment.class, "Medium");
		secondaryContext.ref.registerAbbreviation(sm, "M");
	}

	@Override
	@After
	public void tearDown() throws Exception
	{
		super.tearDown();
	}

	@Test
	public void testInvalidNotASize()
	{
		assertFalse(token.parse(primaryContext, primaryProf, "W"));
		assertNoSideEffects();
	}

	@Test
	public void testRoundRobinS() throws PersistenceLayerException
	{
		runRoundRobin("S");
	}

	@Test
	public void testRoundRobinM() throws PersistenceLayerException
	{
		runRoundRobin("M");
	}

}
