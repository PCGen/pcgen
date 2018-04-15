/*
 * Copyright 2016 (C) Tom Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.base.format.inttest;

import junit.framework.TestCase;
import pcgen.base.format.ArrayFormatManager;
import pcgen.base.format.BooleanManager;
import pcgen.base.format.NumberManager;
import pcgen.base.format.StringManager;
import pcgen.base.format.compound.Compound;
import pcgen.base.format.compound.CompoundFormatManager;
import pcgen.base.util.Indirect;

public class FormatManagerIntTest extends TestCase
{
	private final NumberManager numberManager = new NumberManager();
	private final BooleanManager booleanManager = new BooleanManager();
	private final StringManager stringManager = new StringManager();

	public void testInvalidConvertSimple()
	{
		CompoundFormatManager<Number> compoundManager =
				new CompoundFormatManager<>(numberManager, '|');
		compoundManager.addSecondary(booleanManager, "Allowed", false);
		compoundManager.addSecondary(stringManager, "Level", true);
		ArrayFormatManager<Compound> manager =
				new ArrayFormatManager<>(compoundManager, '\n', ',');
		try
		{
			manager.convert("|");
			fail("Should not be able to convert null instructions");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			manager.convert("3");
			fail("Should not be able to convert instructions"
				+ " missing a required association");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
	}

	public void testInvalidConvertSeparatorIssues()
	{
		CompoundFormatManager<Number> compoundManager =
				new CompoundFormatManager<>(numberManager, '|');
		compoundManager.addSecondary(booleanManager, "Allowed", false);
		compoundManager.addSecondary(stringManager, "Level", true);
		ArrayFormatManager<Compound> manager =
				new ArrayFormatManager<>(compoundManager, '\n', ',');
		try
		{
			manager.convert("3|LEVEL=Hard|");
			fail("Should not be able to convert end with separator instructions");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			manager.convert("3|LEVEL=Hard||ALLOWED=false");
			fail("Should not be able to convert instructions"
				+ " with an double separator");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			manager.convert("3||LEVEL=Hard|ALLOWED=false");
			fail("Should not be able to convert instructions"
				+ " with an double separator");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			manager.convert("3,,4|LEVEL=Hard|ALLOWED=false");
			fail("Should not be able to convert instructions"
				+ " with an double separator");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			manager.convert(",3,4|LEVEL=Hard|ALLOWED=false");
			fail(
				"Should not be able to convert instructions" + " with leading separator");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			manager.convert("3,4,|LEVEL=Hard|ALLOWED=false");
			fail("Should not be able to convert instructions"
				+ " with trailing separator");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
	}

	public void testInvalidConvertAssociationIssues()
	{
		CompoundFormatManager<Number> compoundManager =
				new CompoundFormatManager<>(numberManager, '|');
		compoundManager.addSecondary(booleanManager, "Allowed", false);
		compoundManager.addSecondary(stringManager, "Level", true);
		ArrayFormatManager<Compound> manager =
				new ArrayFormatManager<>(compoundManager, '\n', ',');
		try
		{
			manager.convert("3|LEVEL=Hard|SOUND=Bell");
			fail("Should not be able to convert instructions"
				+ " with an undefined association");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
	}

	public void testInvalidConvertIndirect()
	{
		CompoundFormatManager<Number> compoundManager =
				new CompoundFormatManager<>(numberManager, '|');
		compoundManager.addSecondary(booleanManager, "Allowed", false);
		compoundManager.addSecondary(stringManager, "Level", true);
		ArrayFormatManager<Compound> manager =
				new ArrayFormatManager<>(compoundManager, '\n', ',');
		try
		{
			manager.convertIndirect("|");
			fail("Should not be able to convert null instructions");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			manager.convertIndirect("3");
			fail("Should not be able to convert instructions"
				+ " missing a required association");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			manager.convert("3|LEVEL=Hard|SOUND=Bell");
			fail("Should not be able to convert instructions"
				+ " with an undefined association");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			manager.convertIndirect("3|LEVEL=Hard|");
			fail("Should not be able to convert end with separator instructions");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			manager.convertIndirect("3|LEVEL=Hard||ALLOWED=false");
			fail("Should not be able to convert instructions"
				+ " with an double separator");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			manager.convertIndirect("3||LEVEL=Hard|ALLOWED=false");
			fail("Should not be able to convert instructions"
				+ " with an double separator");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			manager.convertIndirect("3,,4|LEVEL=Hard|ALLOWED=false");
			fail("Should not be able to convert instructions"
				+ " with an double separator");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			manager.convertIndirect(",3,4|LEVEL=Hard|ALLOWED=false");
			fail(
				"Should not be able to convert instructions" + " with leading separator");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			manager.convertIndirect("3,4,|LEVEL=Hard|ALLOWED=false");
			fail("Should not be able to convert instructions"
				+ " with trailing separator");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
	}

	public void testConvertNoArray()
	{
		CompoundFormatManager<Number> compoundManager =
				new CompoundFormatManager<>(numberManager, '|');
		compoundManager.addSecondary(booleanManager, "Allowed", false);
		compoundManager.addSecondary(stringManager, "Level", true);
		ArrayFormatManager<Compound> manager =
				new ArrayFormatManager<>(compoundManager, '\n', ',');
		Compound[] c = manager.convert("3|LEVEL=Hard");
		assertEquals(1, c.length);
		assertEquals("3", c[0].getPrimaryUnconverted());
		assertEquals("Hard", c[0].getSecondary("Level").getUnconverted());
		assertEquals("Hard", c[0].getSecondary("LEVEL").getUnconverted());
		/*
		 * For now unassigned optional items return null
		 * 
		 * There is an interesting debate to be had here depending on how optional items
		 * are used
		 */
		assertNull(c[0].getSecondary("Allowed"));
		assertEquals("3|LEVEL=Hard", manager.unconvert(c));
		Compound[] c2 = manager.convert("4|LEVEL=Easy|ALLOWED=False");
		assertEquals(1, c2.length);
		assertEquals("4", c2[0].getPrimaryUnconverted());
		assertEquals("Easy", c2[0].getSecondary("Level").getUnconverted());
		assertEquals("false", c2[0].getSecondary("ALLOWED").getUnconverted());
		assertEquals("4|ALLOWED=false|LEVEL=Easy", manager.unconvert(c2));
	}

	public void testConvertCompoundArray()
	{
		CompoundFormatManager<Number> compoundManager =
				new CompoundFormatManager<>(numberManager, '|');
		compoundManager.addSecondary(booleanManager, "Allowed", false);
		compoundManager.addSecondary(stringManager, "Level", true);
		ArrayFormatManager<Compound> manager =
				new ArrayFormatManager<>(compoundManager, '\n', ',');
		Compound[] c = manager.convert("3,4|LEVEL=Hard");
		assertEquals(2, c.length);
		assertEquals("3", c[0].getPrimaryUnconverted());
		assertEquals("Hard", c[0].getSecondary("Level").getUnconverted());
		assertEquals("4", c[1].getPrimaryUnconverted());
		assertEquals("Hard", c[1].getSecondary("Level").getUnconverted());
		/*
		 * For now unassigned optional items return null
		 * 
		 * There is an interesting debate to be had here depending on how optional items
		 * are used
		 */
		assertNull(c[0].getSecondary("Allowed"));
		assertEquals("3,4|LEVEL=Hard", manager.unconvert(c));
		Compound[] c2 = manager.convert("4,5|LEVEL=Easy|ALLOWED=False");
		assertEquals(2, c2.length);
		assertEquals("4", c2[0].getPrimaryUnconverted());
		assertEquals("Easy", c2[0].getSecondary("Level").getUnconverted());
		assertEquals("false", c2[0].getSecondary("ALLOWED").getUnconverted());
		assertEquals("4,5|ALLOWED=false|LEVEL=Easy", manager.unconvert(c2));
	}

//	public void testUnconvertInvalid()
//	{
//		CompoundFormatManager<String> manager2 =
//				new CompoundFormatManager<>(stringManager, '|');
//		manager.addComponent(booleanManager, "Allowed", false);
//		manager.addComponent(numberManager, "Level", true);
//		try
//		{
//			manager2.unconvert(c2);
//			fail("Should not be able to unconvert incompatible Compound");
//		}
//		catch (IllegalArgumentException e)
//		{
//			//ok
//		}
//	}

	public void testConvertIndirectNoArray()
	{
		CompoundFormatManager<Number> compoundManager =
				new CompoundFormatManager<>(numberManager, '|');
		compoundManager.addSecondary(booleanManager, "Allowed", false);
		compoundManager.addSecondary(stringManager, "Level", true);
		ArrayFormatManager<Compound> manager =
				new ArrayFormatManager<>(compoundManager, '\n', ',');
		Indirect<Compound[]> in = manager.convertIndirect("3|LEVEL=Hard");
		Compound[] c = in.get();
		assertEquals(1, c.length);
		assertEquals("3", c[0].getPrimaryUnconverted());
		assertEquals("Hard", c[0].getSecondary("Level").getUnconverted());
		assertEquals("Hard", c[0].getSecondary("LEVEL").getUnconverted());
		/*
		 * For now unassigned optional items return null
		 * 
		 * There is an interesting debate to be had here depending on how optional items
		 * are used
		 */
		assertNull(c[0].getSecondary("Allowed"));
		assertEquals("3|LEVEL=Hard", manager.unconvert(c));
		assertEquals("3|LEVEL=Hard", in.getUnconverted());
		Indirect<Compound[]> in2 = manager.convertIndirect("4|LEVEL=Easy|ALLOWED=False");
		Compound[] c2 = in2.get();
		assertEquals(1, c2.length);
		assertEquals("4", c2[0].getPrimaryUnconverted());
		assertEquals("Easy", c2[0].getSecondary("Level").getUnconverted());
		assertEquals("false", c2[0].getSecondary("ALLOWED").getUnconverted());
		assertEquals("4|ALLOWED=false|LEVEL=Easy", manager.unconvert(c2));
		assertEquals("4|ALLOWED=false|LEVEL=Easy", in2.getUnconverted());
	}

	public void testConvertIndirectCompoundArray()
	{
		CompoundFormatManager<Number> compoundManager =
				new CompoundFormatManager<>(numberManager, '|');
		compoundManager.addSecondary(booleanManager, "Allowed", false);
		compoundManager.addSecondary(stringManager, "Level", true);
		ArrayFormatManager<Compound> manager =
				new ArrayFormatManager<>(compoundManager, '\n', ',');
		Indirect<Compound[]> i = manager.convertIndirect("3,4|LEVEL=Hard");
		Compound[] c = i.get();
		assertEquals(2, c.length);
		assertEquals("3", c[0].getPrimaryUnconverted());
		assertEquals("Hard", c[0].getSecondary("Level").getUnconverted());
		assertEquals("4", c[1].getPrimaryUnconverted());
		assertEquals("Hard", c[1].getSecondary("Level").getUnconverted());
		/*
		 * For now unassigned optional items return null
		 * 
		 * There is an interesting debate to be had here depending on how optional items
		 * are used
		 */
		assertNull(c[0].getSecondary("Allowed"));
		assertEquals("3,4|LEVEL=Hard", manager.unconvert(c));
		assertEquals("3,4|LEVEL=Hard", i.getUnconverted());
		Indirect<Compound[]> i2 = manager.convertIndirect("4,5|LEVEL=Easy|ALLOWED=False");
		Compound[] c2 = i2.get();
		assertEquals(2, c2.length);
		assertEquals("4", c2[0].getPrimaryUnconverted());
		assertEquals("Easy", c2[0].getSecondary("Level").getUnconverted());
		assertEquals("false", c2[0].getSecondary("ALLOWED").getUnconverted());
		assertEquals("4,5|ALLOWED=false|LEVEL=Easy", manager.unconvert(c2));
		assertEquals("4,5|ALLOWED=false|LEVEL=Easy", i2.getUnconverted());
	}

}
