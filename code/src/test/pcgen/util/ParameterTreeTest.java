/*
 * Copyright 2007 (C) Andrew Wilson <nuance@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.     See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package pcgen.util;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.regex.Matcher;

import pcgen.PCGenTestCase;

import org.junit.Assert;
import org.junit.Test;
import org.nfunk.jep.ParseException;

/**
 * {@code ParameterTreeTest} is ...
 */
public class ParameterTreeTest extends PCGenTestCase 
{
	/**
	 * Test method for {@link pcgen.util.ParameterTree#ParameterTree(java.lang.String)}.
	 */
	@Test
	public final void testParameterTree()
	{
        final ParameterTree t1 = new ParameterTree("Test Node1");
        assertEquals("New ParameterTree has correct contents", "Test Node1", t1.getContents());
		assertNull("New ParameterTree has null left subtree", t1.getLeftTree());
		assertNull("New ParameterTree has null right subtree", t1.getRightTree());

		final ParameterTree t2 = new ParameterTree("Test Node2");
		t2.setLeftTree(t1);
		assertEquals("New ParameterTree has correct contents", "Test Node2", t2.getContents());
		assertEquals("New ParameterTree has null left subtree", "Test Node1", t2.getLeftTree().getContents());
		assertNull("New ParameterTree has null right subtree", t1.getRightTree());
	}

	@Test
	public final void testMakeTree1()
	{
		final String s = "TYPE=Foo";
		final Matcher mat = ParameterTree.pat.matcher(s);
		mat.find();

		ParameterTree t1 = new ParameterTree("Foo");
		try
		{
			t1 = ParameterTree.makeTree(s);
		}
		catch (ParseException e)
		{
			e.printStackTrace();
			fail("Threw a parse exception");
		}

		assertEquals("New ParamterTree has correct contents", s, t1.getContents());
	}

	@Test
	public final void testMakeTree2()
	{
		final String s = "(TYPE=Foo)";
		final Matcher mat = ParameterTree.pat.matcher(s);
		mat.find();

		ParameterTree t1 = new ParameterTree("Foo");
		try
		{
			t1 = ParameterTree.makeTree(s);
		}
		catch (ParseException e)
		{
			e.printStackTrace();
			fail("Threw a parse exception");
		}

		assertEquals("New ParamterTree has correct contents", "TYPE=Foo", t1.getContents());
	}

	@Test
	public final void testMakeTree3()
	{
		final String s = "((TYPE=Foo))";
		final Matcher mat = ParameterTree.pat.matcher(s);
		mat.find();

		ParameterTree t1 = new ParameterTree("Foo");
		try
		{
			t1 = ParameterTree.makeTree(s);
		}
		catch (ParseException e)
		{
			e.printStackTrace();
			fail("Threw a parse exception");
		}

		assertEquals("New ParamterTree has correct contents", "TYPE=Foo", t1.getContents());
	}

	@Test
	public final void testMakeTree4()
	{
		final String s = "TYPE=Foo[or]TYPE=Bar";
		final Matcher mat = ParameterTree.pat.matcher(s);
		mat.find();

		ParameterTree t1 = new ParameterTree("Foo");
		try
		{
			t1 = ParameterTree.makeTree(s);
		}
		catch (ParseException e)
		{
			e.printStackTrace();
			fail("Threw a parse exception");
		}

		assertEquals("New ParamterTree has correct contents", "[or]", t1.getContents());
		assertEquals("New ParamterTree has correct left tree contents", "TYPE=Foo", t1.getLeftTree().getContents());
		assertNull("New ParamterTree has correct left tree, left tree contents", t1.getLeftTree().getLeftTree());
		assertNull("New ParamterTree has correct left tree, right tree contents", t1.getLeftTree().getRightTree());

		assertEquals("New ParamterTree has correct right tree contents", "TYPE=Bar", t1.getRightTree().getContents());
		assertNull("New ParamterTree has correct left tree, left tree contents", t1.getRightTree().getLeftTree());
		assertNull("New ParamterTree has correct left tree, right tree contents", t1.getRightTree().getRightTree());
	}


	@Test
	public final void testMakeTree5()
	{
		final String s = "(TYPE=Foo[or]TYPE=Bar)";
		final Matcher mat = ParameterTree.pat.matcher(s);
		mat.find();

		ParameterTree t1 = new ParameterTree("Foo");
		try
		{
			t1 = ParameterTree.makeTree(s);
		}
		catch (ParseException e)
		{
			e.printStackTrace();
			fail("Threw a parse exception");
		}

		assertEquals("New ParamterTree has correct contents", "[or]", t1.getContents());
		assertEquals("New ParamterTree has correct left tree contents", "TYPE=Foo", t1.getLeftTree().getContents());
		assertNull("New ParamterTree has correct left tree, left tree contents", t1.getLeftTree().getLeftTree());
		assertNull("New ParamterTree has correct left tree, right tree contents", t1.getLeftTree().getRightTree());

		assertEquals("New ParamterTree has correct right tree contents", "TYPE=Bar", t1.getRightTree().getContents());
		assertNull("New ParamterTree has correct left tree, left tree contents", t1.getRightTree().getLeftTree());
		assertNull("New ParamterTree has correct left tree, right tree contents", t1.getRightTree().getRightTree());
	}

	@Test
	public final void testMakeTree6()
	{
		final String s = "(TYPE=Foo[or]TYPE=Bar[and]String3)";
		final Matcher mat = ParameterTree.pat.matcher(s);
		mat.find();

		ParameterTree t = new ParameterTree("Foo");
		try
		{
			t = ParameterTree.makeTree(s);
		}
		catch (ParseException e)
		{
			e.printStackTrace();
			fail("Threw a parse exception");
		}

		final ParameterTree tl  = t.getLeftTree();
		final ParameterTree tr  = t.getRightTree();
		final ParameterTree tll = tl.getLeftTree();
		final ParameterTree tlr = tl.getRightTree();

		// expected branch nodes
		assertEquals("t1 ParamterTree has correct contents", "[and]", t.getContents());
		assertEquals("tl ParamterTree has correct contents", "[or]", tl.getContents());

		// expected leaf nodes
		assertEquals("tr ParamterTree has correct contents", "String3", tr.getContents());
		assertEquals("tll ParamterTree has correct contents", "TYPE=Foo", tll.getContents());
		assertEquals("tlr ParamterTree has correct contents", "TYPE=Bar", tlr.getContents());

		// check that leaves really are leaves
		assertNull("tr left tree is null (i.e. is a leaf node)", tr.getLeftTree());
		assertNull("tr right tree is null (i.e. is a leaf node)", tr.getRightTree());

		assertNull("tll left tree is null (i.e. is a leaf node)", tll.getLeftTree());
		assertNull("tll right tree is null (i.e. is a leaf node)", tll.getRightTree());

		assertNull("tlr left tree is null (i.e. is a leaf node)", tlr.getLeftTree());
		assertNull("tlr right tree is null (i.e. is a leaf node)", tlr.getRightTree());
	}

	@Test
	public final void testMakeTree7()
	{
//		verbose = true;
//		Logging.errorPrint("\n\n --- Start Test Make tree 7 --- \n\n");

		final String s = "TYPE=Foo[or](TYPE=Bar[and]String3)";
		final Matcher mat = ParameterTree.pat.matcher(s);
		mat.find();

		ParameterTree t = new ParameterTree("Foo");
		try
		{
			t = ParameterTree.makeTree(s);
		}
		catch (ParseException e)
		{
			e.printStackTrace();
			fail("Threw a parse exception");
		}

		final ParameterTree tl  = t.getLeftTree();
		final ParameterTree tr  = t.getRightTree();
		final ParameterTree trl = tr.getLeftTree();
		final ParameterTree trr = tr.getRightTree();

		// expected branch nodes
		assertThat("t  not null", t, notNullValue());
		assertThat("tr  not null", tr, notNullValue());

		assertEquals("t  has correct contents '[or]'", "[or]", t.getContents());
		assertEquals("tr has correct contents '[and]'", "[and]", tr.getContents());

		// expected leaf nodes
		Assert.assertNotNull("tl  not null", tl);
		Assert.assertNotNull("trl  not null", trl);
		Assert.assertNotNull("trr  not null", trr);

		assertEquals("tl  has correct contents 'TYPE=Foo'", "TYPE=Foo", tl.getContents());
		assertEquals("trl has correct contents 'TYPE=Bar'", "TYPE=Bar", trl.getContents());
		assertEquals("trr has correct contents 'String3'", "String3", trr.getContents());

		// check that leaves really are leaves
		assertNull("tl left tree is null (i.e. is a leaf node)", tl.getLeftTree());
		assertNull("tl right tree is null (i.e. is a leaf node)", tl.getRightTree());

		assertNull("trl left tree is null (i.e. is a leaf node)", trl.getLeftTree());
		assertNull("trl right tree is null (i.e. is a leaf node)", trl.getRightTree());

		assertNull("trr left tree is null (i.e. is a leaf node)", trr.getLeftTree());
		assertNull("trr right tree is null (i.e. is a leaf node)", trr.getRightTree());
	}


	@Test
	public final void testMakeTree8()
	{
//		verbose = true;
//		Logging.errorPrint("\n\n --- Start Test Make tree 8 --- \n\n");

		final String s = "TYPE=Foo[or]((CATEGORY=FEAT[or]NATURE=AUTO)[and]TYPE=Bar)";
		final Matcher mat = ParameterTree.pat.matcher(s);
		mat.find();

		ParameterTree t = new ParameterTree("Foo");
		try
		{
			t = ParameterTree.makeTree(s);
		}
		catch (ParseException e)
		{
			e.printStackTrace();
			fail("Threw a parse exception");
		}


		final ParameterTree tl  = t.getLeftTree();
		final ParameterTree tr  = t.getRightTree();

		final ParameterTree trl = tr.getLeftTree();
		final ParameterTree trr = tr.getRightTree();

		final ParameterTree trll = trl.getLeftTree();
		final ParameterTree trlr = trl.getRightTree();

		Assert.assertNotNull("t not null", t);
		Assert.assertNotNull("tr not null", tr);
		Assert.assertNotNull("trl not null", trl);

		assertEquals("t  has correct contents '[or]'", "[or]", t.getContents());
		assertEquals("tr has correct contents '[and]'", "[and]", tr.getContents());
		assertEquals("trl has correct contents '[or]'", "[or]", trl.getContents());

		// expected leaf nodes
		Assert.assertNotNull("tl not null", tl);
		Assert.assertNotNull("trr not null", trr);

		Assert.assertNotNull("trll not null", trll);
		Assert.assertNotNull("trlr not null", trlr);

		assertEquals("tl  has correct contents 'TYPE=Foo'", "TYPE=Foo", tl.getContents());
		assertEquals("trr has correct contents 'TYPE=Bar'", "TYPE=Bar", trr.getContents());

		assertEquals("trl has correct contents 'CATEGORY=FEAT'", "CATEGORY=FEAT", trll.getContents());
		assertEquals("trl has correct contents 'NATURE=AUTO'", "NATURE=AUTO", trlr.getContents());

		// check that leaves really are leaves
		assertNull("tl left tree is null (i.e. is a leaf node)", tl.getLeftTree());
		assertNull("tl right tree is null (i.e. is a leaf node)", tl.getRightTree());

		assertNull("trr left tree is null (i.e. is a leaf node)", trr.getLeftTree());
		assertNull("trr right tree is null (i.e. is a leaf node)", trr.getRightTree());

		assertNull("trl left tree is null (i.e. is a leaf node)", trll.getLeftTree());
		assertNull("trl right tree is null (i.e. is a leaf node)", trll.getRightTree());

		assertNull("trll left tree is null (i.e. is a leaf node)", trlr.getLeftTree());
		assertNull("trlr right tree is null (i.e. is a leaf node)", trlr.getRightTree());
	}

	@Test
	public final void testMakeTree9()
	{
//		verbose = true;
//		Logging.errorPrint("\n\n --- Start Test Make tree 9 --- \n\n");

		final String s = "TYPE=Foo[or]((CATEGORY=FEAT[or]NATURE=AUTO[or]CATEGORY=SA)[and]TYPE=Bar)";
		final Matcher mat = ParameterTree.pat.matcher(s);
		mat.find();

		ParameterTree t = new ParameterTree("Foo");
		try
		{
			t = ParameterTree.makeTree(s);
		}
		catch (ParseException e)
		{
			e.printStackTrace();
			Assert.fail("Threw a parse exception");
		}


		final ParameterTree tl    = t.getLeftTree();
		final ParameterTree tr    = t.getRightTree();

		final ParameterTree trl   = tr.getLeftTree();
		final ParameterTree trr   = tr.getRightTree();

		final ParameterTree trll  = trl.getLeftTree();
		final ParameterTree trlr  = trl.getRightTree();

		final ParameterTree trlll = trll.getLeftTree();
		final ParameterTree trllr = trll.getRightTree();

		// expected branch nodes
		Assert.assertNotNull("t not null", t);
		Assert.assertNotNull("tr not null", tr);
		Assert.assertNotNull("trl not null", trl);
		Assert.assertNotNull("trll not null", trll);

		assertEquals("t    has correct contents '[or]'", "[or]", t.getContents());
		assertEquals("tr   has correct contents '[and]'", "[and]", tr.getContents());
		assertEquals("trl  has correct contents '[or]'", "[or]", trl.getContents());
		assertEquals("trll has correct contents '[or]'", "[or]", trll.getContents());

		// expected leaf nodes
		Assert.assertNotNull("tl not null", tl);
		Assert.assertNotNull("trr not null", trr);

		Assert.assertNotNull("trlr not null", trlr);
		Assert.assertNotNull("trlll not null", trlll);
		Assert.assertNotNull("trllr not null", trllr);

		assertEquals("tl  has correct contents 'TYPE=Foo'", "TYPE=Foo", tl.getContents());
		assertEquals("trr has correct contents 'TYPE=Bar'", "TYPE=Bar", trr.getContents());

		assertEquals("trlr has correct contents 'CATEGORY=SA'", "CATEGORY=SA", trlr.getContents());
		assertEquals("trlr has correct contents 'CATEGORY=FEAT'", "CATEGORY=FEAT", trlll.getContents());
		assertEquals("trlr has correct contents 'NATURE=AUTO'", "NATURE=AUTO", trllr.getContents());

		
		// check that leaves really are leaves
		assertNull("tl left tree is null (i.e. is a leaf node)", tl.getLeftTree());;
		assertNull("tl right tree is null (i.e. is a leaf node)", tl.getRightTree());;

		assertNull("trr left tree is null (i.e. is a leaf node)", trr.getLeftTree());;
		assertNull("trr right tree is null (i.e. is a leaf node)", trr.getRightTree());;

		assertNull("trll left tree is null (i.e. is a leaf node)", trlr.getLeftTree());;
		assertNull("trlr right tree is null (i.e. is a leaf node)", trlr.getRightTree());

		assertNull("trlll left tree is null (i.e. is a leaf node)", trlll.getLeftTree());
		assertNull("trlll right tree is null (i.e. is a leaf node)", trlll.getRightTree());

		assertNull("trlll left tree is null (i.e. is a leaf node)", trllr.getLeftTree());
		assertNull("trlll right tree is null (i.e. is a leaf node)", trllr.getRightTree());
	}
}
