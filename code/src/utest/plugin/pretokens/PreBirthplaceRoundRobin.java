package plugin.pretokens;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import plugin.pretokens.parser.PreBirthplaceParser;
import plugin.pretokens.writer.PreBirthPlaceWriter;

public class PreBirthplaceRoundRobin extends AbstractStringRoundRobin
{

	public static void main(String args[])
	{
		TestRunner.run(PreBirthplaceRoundRobin.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreBirthplaceRoundRobin.class);
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		TokenRegistration.register(new PreBirthplaceParser());
		TokenRegistration.register(new PreBirthPlaceWriter());
	}

	@Override
	public String getBaseString()
	{
		return "BIRTHPLACE";
	}

}
