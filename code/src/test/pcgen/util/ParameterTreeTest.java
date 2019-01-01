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
        is(t1.getContents(), strEq("Test Node1"), "New ParameterTree has correct contents");
		assertNull("New ParameterTree has null left subtree", t1.getLeftTree());
		assertNull("New ParameterTree has null right subtree", t1.getRightTree());

		final ParameterTree t2 = new ParameterTree("Test Node2");
		t2.setLeftTree(t1);
		is(t2.getContents(), strEq("Test Node2"), "New ParameterTree has correct contents");
		is(t2.getLeftTree().getContents(), strEq("Test Node1"), "New ParameterTree has null left subtree");
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

		is(t1.getContents(), strEq(s), "New ParamterTree has correct contents");
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

		is(t1.getContents(), strEq("TYPE=Foo"), "New ParamterTree has correct contents");
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

		is(t1.getContents(), strEq("TYPE=Foo"), "New ParamterTree has correct contents");
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

		is(t1.getContents(), strEq("[or]"),                      "New ParamterTree has correct contents");
		is(t1.getLeftTree().getContents(),  strEq("TYPE=Foo"), "New ParamterTree has correct left tree contents");
		assertNull("New ParamterTree has correct left tree, left tree contents", t1.getLeftTree().getLeftTree());
		assertNull("New ParamterTree has correct left tree, right tree contents", t1.getLeftTree().getRightTree());

		is(t1.getRightTree().getContents(), strEq("TYPE=Bar"), "New ParamterTree has correct right tree contents");
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

		is(t1.getContents(), strEq("[or]"),                      "New ParamterTree has correct contents");
		is(t1.getLeftTree().getContents(),  strEq("TYPE=Foo"), "New ParamterTree has correct left tree contents");
		assertNull("New ParamterTree has correct left tree, left tree contents", t1.getLeftTree().getLeftTree());
		assertNull("New ParamterTree has correct left tree, right tree contents", t1.getLeftTree().getRightTree());

		is(t1.getRightTree().getContents(), strEq("TYPE=Bar"), "New ParamterTree has correct right tree contents");
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
		is(t.getContents(), strEq("[and]"),   "t1 ParamterTree has correct contents");
		is(tl.getContents(), strEq("[or]"),  "tl ParamterTree has correct contents");

		// expected leaf nodes
		is(tr.getContents(), strEq("String3"),  "tr ParamterTree has correct contents");
		is(tll.getContents(), strEq("TYPE=Foo"), "tll ParamterTree has correct contents");
		is(tlr.getContents(), strEq("TYPE=Bar"), "tlr ParamterTree has correct contents");

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

		is(t.getContents(),  strEq("[or]"),  "t  has correct contents '[or]'");
		is(tr.getContents(), strEq("[and]"),  "tr has correct contents '[and]'");

		// expected leaf nodes
		Assert.assertNotNull("tl  not null", tl);
		Assert.assertNotNull("trl  not null", trl);
		Assert.assertNotNull("trr  not null", trr);

		is(tl.getContents(),  strEq("TYPE=Foo"), "tl  has correct contents 'TYPE=Foo'");
		is(trl.getContents(), strEq("TYPE=Bar"), "trl has correct contents 'TYPE=Bar'");
		is(trr.getContents(), strEq("String3"),  "trr has correct contents 'String3'");

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

		is(t.getContents(),   strEq("[or]"),  "t  has correct contents '[or]'");
		is(tr.getContents(),  strEq("[and]"),  "tr has correct contents '[and]'");
		is(trl.getContents(), strEq("[or]"),  "trl has correct contents '[or]'");

		// expected leaf nodes
		Assert.assertNotNull("tl not null", tl);
		Assert.assertNotNull("trr not null", trr);

		Assert.assertNotNull("trll not null", trll);
		Assert.assertNotNull("trlr not null", trlr);

		is(tl.getContents(),  strEq("TYPE=Foo"), "tl  has correct contents 'TYPE=Foo'");
		is(trr.getContents(), strEq("TYPE=Bar"), "trr has correct contents 'TYPE=Bar'");

		is(trll.getContents(), strEq("CATEGORY=FEAT"), "trl has correct contents 'CATEGORY=FEAT'");
		is(trlr.getContents(), strEq("NATURE=AUTO"), "trl has correct contents 'NATURE=AUTO'");

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

		is(t.getContents(),    strEq("[or]"), "t    has correct contents '[or]'");
		is(tr.getContents(),   strEq("[and]"), "tr   has correct contents '[and]'");
		is(trl.getContents(),  strEq("[or]"), "trl  has correct contents '[or]'");
		is(trll.getContents(), strEq("[or]"), "trll has correct contents '[or]'");

		// expected leaf nodes
		Assert.assertNotNull("tl not null", tl);
		Assert.assertNotNull("trr not null", trr);

		Assert.assertNotNull("trlr not null", trlr);
		Assert.assertNotNull("trlll not null", trlll);
		Assert.assertNotNull("trllr not null", trllr);

		is(tl.getContents(), strEq("TYPE=Foo"), "tl  has correct contents 'TYPE=Foo'");
		is(trr.getContents(), strEq("TYPE=Bar"), "trr has correct contents 'TYPE=Bar'");

		is(trlr.getContents(), strEq("CATEGORY=SA"),   "trlr has correct contents 'CATEGORY=SA'");
		is(trlll.getContents(), strEq("CATEGORY=FEAT"), "trlr has correct contents 'CATEGORY=FEAT'");
		is(trllr.getContents(), strEq("NATURE=AUTO"),   "trlr has correct contents 'NATURE=AUTO'");

		
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
