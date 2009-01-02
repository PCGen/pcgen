package plugin.lsttokens.editcontext.testsupport;

import org.junit.Test;

import pcgen.cdom.base.CDOMObject;
import pcgen.persistence.PersistenceLayerException;

public abstract class AbstractFormulaIntegrationTestCase<T extends CDOMObject>
		extends AbstractIntegrationTestCase<T>
{

	@Test
	public void testRoundRobinSimple() throws PersistenceLayerException
	{
		verifyCleanStart();
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "1");
		commit(modCampaign, tc, "2");
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinOverwrite() throws PersistenceLayerException
	{
		verifyCleanStart();
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "1");
		commit(testCampaign, tc, "2");
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinIdentical() throws PersistenceLayerException
	{
		verifyCleanStart();
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "Identical");
		commit(modCampaign, tc, "Identical");
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinFormula() throws PersistenceLayerException
	{
		verifyCleanStart();
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "1");
		commit(modCampaign, tc, "Formula");
		completeRoundRobin(tc);
	}

	public abstract boolean isNegativeAllowed();

	@Test
	public void testRoundRobinNegative() throws PersistenceLayerException
	{
		if (isNegativeAllowed())
		{
			verifyCleanStart();
			TestContext tc = new TestContext();
			commit(testCampaign, tc, "Formula");
			commit(modCampaign, tc, "-2");
			completeRoundRobin(tc);
		}
	}

	@Test
	public void testRoundRobinNoSet() throws PersistenceLayerException
	{
		verifyCleanStart();
		TestContext tc = new TestContext();
		emptyCommit(testCampaign, tc);
		commit(modCampaign, tc, "ModForm");
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinNoReset() throws PersistenceLayerException
	{
		verifyCleanStart();
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "StartForm");
		emptyCommit(modCampaign, tc);
		completeRoundRobin(tc);
	}
}
