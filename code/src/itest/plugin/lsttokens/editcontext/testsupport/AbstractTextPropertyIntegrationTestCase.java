package plugin.lsttokens.editcontext.testsupport;

import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pcgen.cdom.base.CDOMObject;
import pcgen.persistence.PersistenceLayerException;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.pretokens.parser.PreClassParser;
import plugin.pretokens.parser.PreLevelParser;
import plugin.pretokens.writer.PreClassWriter;
import plugin.pretokens.writer.PreLevelWriter;

public abstract class AbstractTextPropertyIntegrationTestCase<T extends CDOMObject>
		extends AbstractIntegrationTestCase<T>
{

	private static boolean classSetUpFired = false;

	@BeforeClass
	public static final void localClassSetUp() throws URISyntaxException,
		PersistenceLayerException
	{
		TokenRegistration.register(new PreLevelParser());
		TokenRegistration.register(new PreClassParser());
		TokenRegistration.register(new PreLevelWriter());
		TokenRegistration.register(new PreClassWriter());
		classSetUpFired = true;
	}

	@Override
	@Before
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
		if (!classSetUpFired)
		{
			localClassSetUp();
		}
	}

	@Test
	public void testRoundRobinSimple() throws PersistenceLayerException
	{
		verifyCleanStart();
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "Languedoc-Roussillon");
		commit(modCampaign, tc, "Niederösterreich");
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinIdentical() throws PersistenceLayerException
	{
		verifyCleanStart();
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "Niederösterreich");
		commit(modCampaign, tc, "Niederösterreich");
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinAdd() throws PersistenceLayerException
	{
		verifyCleanStart();
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "Rheinhessen|VarOne|VarTwo");
		commit(modCampaign, tc,
			"Rheinhessen|VarOne|VarTwo|PRECLASS:1,Fighter=1|PRELEVEL:MIN=5");
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinRemove() throws PersistenceLayerException
	{
		verifyCleanStart();
		TestContext tc = new TestContext();
		commit(testCampaign, tc,
			"Rheinhessen|VarOne|VarTwo|PRECLASS:1,Fighter=1|PRELEVEL:MIN=5");
		commit(modCampaign, tc, "Rheinhessen");
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinTrickRemove() throws PersistenceLayerException
	{
		verifyCleanStart();
		TestContext tc = new TestContext();
		commit(testCampaign, tc,
			"Rheinhessen(% of % or %)|VarOne|VarTwo|VarThree");
		commit(modCampaign, tc, "Rheinhessen");
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinNoSet() throws PersistenceLayerException
	{
		verifyCleanStart();
		TestContext tc = new TestContext();
		emptyCommit(testCampaign, tc);
		commit(modCampaign, tc, "Rheinhessen (% of %)|VarOne|VarTwo|PRELEVEL:MIN=5");
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinNoReset() throws PersistenceLayerException
	{
		verifyCleanStart();
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "Rheinhessen|VarOne|VarTwo|!PRELEVEL:MIN=5");
		emptyCommit(modCampaign, tc);
		completeRoundRobin(tc);
	}

}
