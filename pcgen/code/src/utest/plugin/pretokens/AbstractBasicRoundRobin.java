/*
 * Copyright (c) 2008 Tom Parker <thpr@users.sourceforge.net>
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
package plugin.pretokens;


public abstract class AbstractBasicRoundRobin extends AbstractPreRoundRobin
{

	public abstract String getBaseString();

	public abstract boolean isTypeAllowed();

	protected static boolean isSubAllowed()
	{
		return true;
	}

	public String getPrefix()
	{
		return "";
	}

	public void testBasic()
	{
		runRoundRobin("PRE" + getBaseString() + ":1," + getPrefix() + "Foo");
	}

	public void testMultiple()
	{
		runRoundRobin("PRE" + getBaseString() + ":1," + getPrefix()
				+ "Spot,Listen");
	}

	public void testNoCombineSub()
	{
		runRoundRobin("PREMULT:1,[PRE" + getBaseString() + ":1," + getPrefix()
				+ "Foo,Bar],[PRE" + getBaseString() + ":2," + getPrefix()
				+ "Spot,Listen]");
	}

	public void testNoCombineSubNegative()
	{
		runRoundRobin("PREMULT:1,[!PRE" + getBaseString() + ":1," + getPrefix()
				+ "Foo],[!PRE" + getBaseString() + ":1," + getPrefix()
				+ "Spot]");
	}

	public void testCombineSub()
	{
		runSimpleRoundRobin("PREMULT:2,[!PRE" + getBaseString() + ":1,"
				+ getPrefix() + "Foo],[!PRE" + getBaseString() + ":1,"
				+ getPrefix() + "Spot]", "!PRE" + getBaseString() + ":1,"
				+ getPrefix() + "Foo,Spot");
	}

	public void testCombineSubNegative()
	{
		runSimpleRoundRobin("!PREMULT:2,[!PRE" + getBaseString() + ":1,"
				+ getPrefix() + "Foo],[!PRE" + getBaseString() + ":1,"
				+ getPrefix() + "Spot]", "PRE" + getBaseString() + ":1,"
				+ getPrefix() + "Foo,Spot");
	}

	public void testNoCombineMult()
	{
		runRoundRobin("PREMULT:2,[PRE" + getBaseString() + ":1," + getPrefix()
				+ "Foo,Bar],[PRE" + getBaseString() + ":1," + getPrefix()
				+ "Spot,Listen]");
	}

	public void testMultipleCount()
	{
		runRoundRobin("PRE" + getBaseString() + ":2," + getPrefix() + "Foo,Bar");
	}

	public void testType()
	{
		if (isTypeAllowed())
		{
			runRoundRobin("PRE" + getBaseString() + ":1," + getPrefix()
					+ "TYPE=Foo");
		}
	}

	public void testTypeMultipleCount()
	{
		if (isTypeAllowed())
		{
			runRoundRobin("PRE" + getBaseString() + ":2," + getPrefix()
					+ "TYPE=Foo");
		}
	}

	public void testMultipleType()
	{
		if (isTypeAllowed())
		{
			runRoundRobin("PRE" + getBaseString() + ":1," + getPrefix()
					+ "TYPE=Bar,TYPE=Foo");
		}
	}

	public void testTypeAnd()
	{
		if (isTypeAllowed())
		{
			runRoundRobin("PRE" + getBaseString() + ":1," + getPrefix()
					+ "TYPE=Foo.Bar");
		}
	}

	public void testComplex()
	{
		if (isTypeAllowed())
		{
			runRoundRobin("PRE" + getBaseString() + ":3," + getPrefix()
					+ "Foo,TYPE=Bar");
		}
	}


	public void testBasicSub()
	{
		runRoundRobin("PRE" + getBaseString() + ":1," + getPrefix() + "Foo (Bar)");
	}

	public void testMultipleSub()
	{
		runRoundRobin("PRE" + getBaseString() + ":1," + getPrefix()
				+ "Spot (Bar),Listen (Goo)");
	}

	public void testNoCombineSubSub()
	{
		runRoundRobin("PREMULT:1,[PRE" + getBaseString() + ":1," + getPrefix()
				+ "Foo (Bar),Bar (Goo)],[PRE" + getBaseString() + ":2," + getPrefix()
				+ "Spot (Check),Listen (For)]");
	}

	public void testNoCombineSubNegativeSub()
	{
		runRoundRobin("PREMULT:1,[!PRE" + getBaseString() + ":1," + getPrefix()
				+ "Foo (Bar)],[!PRE" + getBaseString() + ":1," + getPrefix()
				+ "Spot (Goo)]");
	}

	public void testCombineSubSub()
	{
		runSimpleRoundRobin("PREMULT:2,[!PRE" + getBaseString() + ":1,"
				+ getPrefix() + "Foo (Bar)],[!PRE" + getBaseString() + ":1,"
				+ getPrefix() + "Spot (Check)]", "!PRE" + getBaseString() + ":1,"
				+ getPrefix() + "Foo (Bar),Spot (Check)");
	}

	public void testCombineSubNegativeSub()
	{
		runSimpleRoundRobin("!PREMULT:2,[!PRE" + getBaseString() + ":1,"
				+ getPrefix() + "Foo (Bar)],[!PRE" + getBaseString() + ":1,"
				+ getPrefix() + "Spot (Check)]", "PRE" + getBaseString() + ":1,"
				+ getPrefix() + "Foo (Bar),Spot (Check)");
	}

	public void testNoCombineMultSub()
	{
		runRoundRobin("PREMULT:2,[PRE" + getBaseString() + ":1," + getPrefix()
				+ "Foo (Bar),Bar (Goo)],[PRE" + getBaseString() + ":1," + getPrefix()
				+ "Spot (Har),Listen (Check)]");
	}

	public void testMultipleCountSub()
	{
		runRoundRobin("PRE" + getBaseString() + ":2," + getPrefix() + "Foo (Goo),Bar (Hoo)");
	}

	public void testComplexSub()
	{
		if (isTypeAllowed() && isSubAllowed())
		{
			runRoundRobin("PRE" + getBaseString() + ":3," + getPrefix()
					+ "Foo (Goo),TYPE=Bar");
		}
	}
}
