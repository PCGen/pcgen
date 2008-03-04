package plugin.pretokens;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import plugin.pretokens.parser.PreArmorTypeParser;
import plugin.pretokens.writer.PreArmorTypeWriter;

public class PreArmorTypeRoundRobin extends AbstractBasicRoundRobin
{

	public static void main(String args[])
	{
		TestRunner.run(PreArmorTypeRoundRobin.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreArmorTypeRoundRobin.class);
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		TokenRegistration.register(new PreArmorTypeParser());
		TokenRegistration.register(new PreArmorTypeWriter());
	}

	@Override
	public String getBaseString()
	{
		return "ARMORTYPE";
	}

	@Override
	public boolean isTypeAllowed()
	{
		return true;
	}

	public void testList()
	{
		this.runRoundRobin("PREARMORTYPE:1,LIST");
	}

}
