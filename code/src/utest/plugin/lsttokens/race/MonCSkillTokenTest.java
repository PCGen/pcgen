package plugin.lsttokens.race;

import org.junit.Test;

import pcgen.core.Race;
import pcgen.core.Skill;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.CDOMTokenLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractListTokenTestCase;

public class MonCSkillTokenTest extends AbstractListTokenTestCase<Race, Skill>
{
	static MoncskillToken token = new MoncskillToken();
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
	public char getJoinCharacter()
	{
		return '|';
	}

	@Override
	public Class<Skill> getTargetClass()
	{
		return Skill.class;
	}

	@Override
	public boolean isTypeLegal()
	{
		return true;
	}

	@Override
	public boolean isAllLegal()
	{
		return true;
	}

	@Override
	public boolean isClearDotLegal()
	{
		return true;
	}

	@Override
	public boolean isClearLegal()
	{
		return true;
	}

	@Test
	public void testRoundRobinList() throws PersistenceLayerException
	{
		runRoundRobin("LIST");
	}

	@Test
	public void testRoundRobinPattern() throws PersistenceLayerException
	{
		runRoundRobin("Pattern%");
	}

	@Test
	public void testInvalidInputAllList() throws PersistenceLayerException
	{
		assertFalse(parse("ALL" + getJoinCharacter() + "LIST"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputAllPattern() throws PersistenceLayerException
	{
		assertFalse(parse("ALL" + getJoinCharacter() + "Pattern%"));
		assertNoSideEffects();
	}

}
