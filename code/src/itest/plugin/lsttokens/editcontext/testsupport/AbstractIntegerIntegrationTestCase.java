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
package plugin.lsttokens.editcontext.testsupport;

import static org.junit.Assert.assertTrue;

import java.net.URISyntaxException;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriterInterface;
import pcgen.persistence.lst.prereq.PrerequisiteParserInterface;
import plugin.bonustokens.Feat;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.pretokens.parser.PreHDParser;
import plugin.pretokens.parser.PreLevelParser;
import plugin.pretokens.parser.PreMultParser;
import plugin.pretokens.writer.PreHDWriter;
import plugin.pretokens.writer.PreLevelWriter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public abstract class AbstractIntegerIntegrationTestCase<T extends CDOMObject>
		extends AbstractIntegrationTestCase<T>
{

	private static final PrerequisiteParserInterface premult = new PreMultParser();
	private static final PrerequisiteParserInterface prehd = new PreHDParser();
	private static final PrerequisiteWriterInterface prehdwriter = new PreHDWriter();
	private static final PrerequisiteParserInterface prelevel = new PreLevelParser();
	private static final PrerequisiteWriterInterface prelevelwriter = new PreLevelWriter();

	public abstract boolean isZeroAllowed();

	public abstract boolean isNegativeAllowed();

	public abstract boolean isPositiveAllowed();

	public abstract boolean doesOverwrite();

	@Override
	@BeforeEach
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
		TokenRegistration.register(premult);
		TokenRegistration.register(prehd);
		TokenRegistration.register(prehdwriter);
		TokenRegistration.register(prelevel);
		TokenRegistration.register(prelevelwriter);
		TokenRegistration.register(Feat.class);
	}

	@Test
	public void testArchitectire()
	{
		assertTrue(isPositiveAllowed() || isNegativeAllowed());
	}

	@Test
	public void testRoundRobinSimple() throws PersistenceLayerException
	{
		if (isPositiveAllowed())
		{
			verifyCleanStart();
			TestContext tc = new TestContext();
			commit(testCampaign, tc, "1");
			commit(modCampaign, tc, "2");
			completeRoundRobin(tc);
		}
	}

	@Test
	public void testRoundRobinSimpleOverwrite()
		throws PersistenceLayerException
	{
		if (isPositiveAllowed() && doesOverwrite())
		{
			verifyCleanStart();
			TestContext tc = new TestContext();
			commit(testCampaign, tc, "1");
			commit(testCampaign, tc, "2");
			completeRoundRobin(tc);
		}
	}

	@Test
	public void testRoundRobinNegativeOverwrite()
		throws PersistenceLayerException
	{
		if (isNegativeAllowed() && doesOverwrite())
		{
			verifyCleanStart();
			TestContext tc = new TestContext();
			commit(testCampaign, tc, "-1");
			commit(testCampaign, tc, "-2");
			completeRoundRobin(tc);
		}
	}

	@Test
	public void testRoundRobinSimpleAppend() throws PersistenceLayerException
	{
		if (isPositiveAllowed() && !doesOverwrite())
		{
			verifyCleanStart();
			TestContext tc = new TestContext();
			commit(testCampaign, tc, "1");
			commit(testCampaign, tc, "2");
			tc.putText(testCampaign.getURI(), "1", "2");
			completeRoundRobin(tc);
		}
	}

	@Test
	public void testRoundRobinNegativeAppend() throws PersistenceLayerException
	{
		if (isNegativeAllowed() && !doesOverwrite())
		{
			verifyCleanStart();
			TestContext tc = new TestContext();
			commit(testCampaign, tc, "-1");
			commit(testCampaign, tc, "-2");
			tc.putText(testCampaign.getURI(), "-1", "-2");
			completeRoundRobin(tc);
		}
	}

	@Test
	public void testRoundRobinIdentical() throws PersistenceLayerException
	{
		if (isPositiveAllowed())
		{
			verifyCleanStart();
			TestContext tc = new TestContext();
			commit(testCampaign, tc, "1");
			commit(modCampaign, tc, "1");
			completeRoundRobin(tc);
		}
	}

	@Test
	public void testRoundRobinZero() throws PersistenceLayerException
	{
		if (isZeroAllowed())
		{
			verifyCleanStart();
			TestContext tc = new TestContext();
			if (isNegativeAllowed())
			{
				commit(testCampaign, tc, "-4");
			}
			else
			{
				commit(testCampaign, tc, "1");
			}
			commit(modCampaign, tc, "0");
			completeRoundRobin(tc);
		}
	}

	@Test
	public void testRoundRobinNegative() throws PersistenceLayerException
	{
		if (isNegativeAllowed())
		{
			verifyCleanStart();
			TestContext tc = new TestContext();
			commit(testCampaign, tc, "-1");
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
		if (isPositiveAllowed())
		{
			commit(modCampaign, tc, "2");
		}
		else if (isNegativeAllowed())
		{
			commit(modCampaign, tc, "-3");
		}
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinNoReset() throws PersistenceLayerException
	{
		verifyCleanStart();
		TestContext tc = new TestContext();
		if (isPositiveAllowed())
		{
			commit(testCampaign, tc, "3");
		}
		else if (isNegativeAllowed())
		{
			commit(testCampaign, tc, "-2");
		}
		emptyCommit(modCampaign, tc);
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinSimpleClear() throws PersistenceLayerException
	{
		if (isClearAllowed())
		{
			verifyCleanStart();
			TestContext tc = new TestContext();
			commit(testCampaign, tc, Constants.LST_DOT_CLEAR);
			if (isPositiveAllowed())
			{
				commit(modCampaign, tc, "3");
			}
			else if (isNegativeAllowed())
			{
				commit(modCampaign, tc, "-2");
			}
			completeRoundRobin(tc);
		}
	}

	@Test
	public void testRoundRobinClearSet() throws PersistenceLayerException
	{
		if (isClearAllowed())
		{
			verifyCleanStart();
			TestContext tc = new TestContext();
			if (isPositiveAllowed())
			{
				commit(testCampaign, tc, Constants.LST_DOT_CLEAR, "3");
			}
			else if (isNegativeAllowed())
			{
				commit(testCampaign, tc, Constants.LST_DOT_CLEAR, "-2");
			}
			completeRoundRobin(tc);
		}
	}

	@Test
	public void testRoundRobinClearNoSet() throws PersistenceLayerException
	{
		if (isClearAllowed())
		{
			verifyCleanStart();
			TestContext tc = new TestContext();
			emptyCommit(testCampaign, tc);
			commit(modCampaign, tc, Constants.LST_DOT_CLEAR);
			completeRoundRobin(tc);
		}
	}

	@Test
	public void testRoundRobinClearNoReset() throws PersistenceLayerException
	{
		if (isClearAllowed())
		{
			verifyCleanStart();
			TestContext tc = new TestContext();
			commit(testCampaign, tc, Constants.LST_DOT_CLEAR);
			emptyCommit(modCampaign, tc);
			completeRoundRobin(tc);
		}
	}

	protected abstract boolean isClearAllowed();
}
