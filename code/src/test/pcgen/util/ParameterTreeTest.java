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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.regex.Matcher;

import org.junit.jupiter.api.Test;
import org.nfunk.jep.ParseException;

class ParameterTreeTest
{
    /**
     * Test method for {@link pcgen.util.ParameterTree#ParameterTree(java.lang.String)}.
     */
    @Test
    public final void testParameterTree()
    {
        final ParameterTree t1 = new ParameterTree("Test Node1");
        assertEquals("Test Node1", t1.getContents(), "New ParameterTree has correct contents");
        assertNull(t1.getLeftTree(), "New ParameterTree has null left subtree");
        assertNull(t1.getRightTree(), "New ParameterTree has null right subtree");

        final ParameterTree t2 = new ParameterTree("Test Node2");
        t2.setLeftTree(t1);
        assertEquals("Test Node2", t2.getContents(), "New ParameterTree has correct contents");
        assertEquals("Test Node1", t2.getLeftTree().getContents(), "New ParameterTree has null left subtree");
        assertNull(t1.getRightTree(), "New ParameterTree has null right subtree");
    }

    @Test
    public final void testMakeTree1() throws ParseException
    {
        final String s = "TYPE=Foo";
        final Matcher mat = ParameterTree.pat.matcher(s);
        mat.find();
        ParameterTree t1 = ParameterTree.makeTree(s);
        assertEquals(s, t1.getContents(), "New ParamterTree has correct contents");
    }

    @Test
    public final void testMakeTree2() throws ParseException
    {
        final String s = "(TYPE=Foo)";
        final Matcher mat = ParameterTree.pat.matcher(s);
        mat.find();

        ParameterTree t1 = ParameterTree.makeTree(s);
        assertEquals("TYPE=Foo", t1.getContents(), "New ParamterTree has correct contents");
    }

    @Test
    public final void testMakeTree3() throws ParseException
    {
        final String s = "((TYPE=Foo))";
        final Matcher mat = ParameterTree.pat.matcher(s);
        mat.find();
        ParameterTree t1 = ParameterTree.makeTree(s);
        assertEquals("TYPE=Foo", t1.getContents(), "New ParamterTree has correct contents");
    }

    @Test
    public final void testMakeTree4() throws ParseException
    {
        final String s = "TYPE=Foo[or]TYPE=Bar";
        final Matcher mat = ParameterTree.pat.matcher(s);
        mat.find();

        ParameterTree t1 = ParameterTree.makeTree(s);

        assertEquals("[or]", t1.getContents(), "New ParamterTree has correct contents");
        assertEquals("TYPE=Foo", t1.getLeftTree().getContents(),
                "New ParamterTree has correct left tree contents");
        assertNull(t1.getLeftTree().getLeftTree(),
                "New ParamterTree has correct left tree, left tree contents");
        assertNull(t1.getLeftTree().getRightTree(),
                "New ParamterTree has correct left tree, right tree contents");

        assertEquals("TYPE=Bar", t1.getRightTree().getContents(),
                "New ParamterTree has correct right tree contents");
        assertNull(t1.getRightTree().getLeftTree(),
                "New ParamterTree has correct left tree, left tree contents");
        assertNull(t1.getRightTree().getRightTree(),
                "New ParamterTree has correct left tree, right tree contents");
    }


    @Test
    public final void testMakeTree5() throws ParseException
    {
        final String s = "(TYPE=Foo[or]TYPE=Bar)";
        final Matcher mat = ParameterTree.pat.matcher(s);
        mat.find();

        ParameterTree t1 = ParameterTree.makeTree(s);

        assertEquals("[or]", t1.getContents(), "New ParamterTree has correct contents");
        assertEquals("TYPE=Foo", t1.getLeftTree().getContents(),
                "New ParamterTree has correct left tree contents");
        assertNull(t1.getLeftTree().getLeftTree(),
                "New ParamterTree has correct left tree, left tree contents");
        assertNull(t1.getLeftTree().getRightTree(),
                "New ParamterTree has correct left tree, right tree contents");

        assertEquals("TYPE=Bar", t1.getRightTree().getContents(),
                "New ParamterTree has correct right tree contents");
        assertNull(t1.getRightTree().getLeftTree(),
                "New ParamterTree has correct left tree, left tree contents");
        assertNull(t1.getRightTree().getRightTree(),
                "New ParamterTree has correct left tree, right tree contents");
    }

    @Test
    public final void testMakeTree6() throws ParseException
    {
        final String s = "(TYPE=Foo[or]TYPE=Bar[and]String3)";
        final Matcher mat = ParameterTree.pat.matcher(s);
        mat.find();

        ParameterTree t = ParameterTree.makeTree(s);

        final ParameterTree tl = t.getLeftTree();
        final ParameterTree tr = t.getRightTree();
        final ParameterTree tll = tl.getLeftTree();
        final ParameterTree tlr = tl.getRightTree();

        // expected branch nodes
        assertEquals("[and]", t.getContents(), "t1 ParamterTree has correct contents");
        assertEquals("[or]", tl.getContents(), "tl ParamterTree has correct contents");

        // expected leaf nodes
        assertEquals("String3", tr.getContents(), "tr ParamterTree has correct contents");
        assertEquals("TYPE=Foo", tll.getContents(), "tll ParamterTree has correct contents");
        assertEquals("TYPE=Bar", tlr.getContents(), "tlr ParamterTree has correct contents");

        // check that leaves really are leaves
        assertNull(tr.getLeftTree(), "tr left tree is null (i.e. is a leaf node)");
        assertNull(tr.getRightTree(), "tr right tree is null (i.e. is a leaf node)");

        assertNull(tll.getLeftTree(), "tll left tree is null (i.e. is a leaf node)");
        assertNull(tll.getRightTree(), "tll right tree is null (i.e. is a leaf node)");

        assertNull(tlr.getLeftTree(), "tlr left tree is null (i.e. is a leaf node)");
        assertNull(tlr.getRightTree(), "tlr right tree is null (i.e. is a leaf node)");
    }

    @Test
    public final void testMakeTree7() throws ParseException
    {
//		verbose = true;
//		Logging.errorPrint("\n\n --- Start Test Make tree 7 --- \n\n");

        final String s = "TYPE=Foo[or](TYPE=Bar[and]String3)";
        final Matcher mat = ParameterTree.pat.matcher(s);
        mat.find();

        ParameterTree t = ParameterTree.makeTree(s);

        final ParameterTree tl = t.getLeftTree();
        final ParameterTree tr = t.getRightTree();
        final ParameterTree trl = tr.getLeftTree();
        final ParameterTree trr = tr.getRightTree();

        // expected branch nodes
        assertThat("t  not null", t, notNullValue());
        assertThat("tr  not null", tr, notNullValue());

        assertEquals("[or]", t.getContents(), "t  has correct contents '[or]'");
        assertEquals("[and]", tr.getContents(), "tr has correct contents '[and]'");

        // expected leaf nodes
        assertNotNull(tl, "tl  not null");
        assertNotNull(trl, "trl  not null");
        assertNotNull(trr, "trr  not null");

        assertEquals("TYPE=Foo", tl.getContents(), "tl  has correct contents 'TYPE=Foo'");
        assertEquals("TYPE=Bar", trl.getContents(), "trl has correct contents 'TYPE=Bar'");
        assertEquals("String3", trr.getContents(), "trr has correct contents 'String3'");

        // check that leaves really are leaves
        assertNull(tl.getLeftTree(), "tl left tree is null (i.e. is a leaf node)");
        assertNull(tl.getRightTree(), "tl right tree is null (i.e. is a leaf node)");

        assertNull(trl.getLeftTree(), "trl left tree is null (i.e. is a leaf node)");
        assertNull(trl.getRightTree(), "trl right tree is null (i.e. is a leaf node)");

        assertNull(trr.getLeftTree(), "trr left tree is null (i.e. is a leaf node)");
        assertNull(trr.getRightTree(), "trr right tree is null (i.e. is a leaf node)");
    }


    @Test
    public final void testMakeTree8() throws ParseException
    {
//		verbose = true;
//		Logging.errorPrint("\n\n --- Start Test Make tree 8 --- \n\n");

        final String s = "TYPE=Foo[or]((CATEGORY=FEAT[or]NATURE=AUTO)[and]TYPE=Bar)";
        final Matcher mat = ParameterTree.pat.matcher(s);
        mat.find();

        ParameterTree t = ParameterTree.makeTree(s);

        final ParameterTree tl = t.getLeftTree();
        final ParameterTree tr = t.getRightTree();

        final ParameterTree trl = tr.getLeftTree();
        final ParameterTree trr = tr.getRightTree();

        final ParameterTree trll = trl.getLeftTree();
        final ParameterTree trlr = trl.getRightTree();

        assertNotNull(t, "t not null");
        assertNotNull(tr, "tr not null");
        assertNotNull(trl, "trl not null");

        assertEquals("[or]", t.getContents(), "t  has correct contents '[or]'");
        assertEquals("[and]", tr.getContents(), "tr has correct contents '[and]'");
        assertEquals("[or]", trl.getContents(), "trl has correct contents '[or]'");

        // expected leaf nodes
        assertNotNull(tl, "tl not null");
        assertNotNull(trr, "trr not null");

        assertNotNull(trll, "trll not null");
        assertNotNull(trlr, "trlr not null");

        assertEquals("TYPE=Foo", tl.getContents(), "tl  has correct contents 'TYPE=Foo'");
        assertEquals("TYPE=Bar", trr.getContents(), "trr has correct contents 'TYPE=Bar'");

        assertEquals("CATEGORY=FEAT", trll.getContents(), "trl has correct contents 'CATEGORY=FEAT'");
        assertEquals("NATURE=AUTO", trlr.getContents(), "trl has correct contents 'NATURE=AUTO'");

        // check that leaves really are leaves
        assertNull(tl.getLeftTree(), "tl left tree is null (i.e. is a leaf node)");
        assertNull(tl.getRightTree(), "tl right tree is null (i.e. is a leaf node)");

        assertNull(trr.getLeftTree(), "trr left tree is null (i.e. is a leaf node)");
        assertNull(trr.getRightTree(), "trr right tree is null (i.e. is a leaf node)");

        assertNull(trll.getLeftTree(), "trl left tree is null (i.e. is a leaf node)");
        assertNull(trll.getRightTree(), "trl right tree is null (i.e. is a leaf node)");

        assertNull(trlr.getLeftTree(), "trll left tree is null (i.e. is a leaf node)");
        assertNull(trlr.getRightTree(), "trlr right tree is null (i.e. is a leaf node)");
    }

    @Test
    public final void testMakeTree9() throws ParseException
    {
        final String s = "TYPE=Foo[or]((CATEGORY=FEAT[or]NATURE=AUTO[or]CATEGORY=SA)[and]TYPE=Bar)";
        final Matcher mat = ParameterTree.pat.matcher(s);
        mat.find();

        ParameterTree t = ParameterTree.makeTree(s);

        final ParameterTree tl = t.getLeftTree();
        final ParameterTree tr = t.getRightTree();

        final ParameterTree trl = tr.getLeftTree();
        final ParameterTree trr = tr.getRightTree();

        final ParameterTree trll = trl.getLeftTree();
        final ParameterTree trlr = trl.getRightTree();

        final ParameterTree trlll = trll.getLeftTree();
        final ParameterTree trllr = trll.getRightTree();

        // expected branch nodes
        assertNotNull(t, "t not null");
        assertNotNull(tr, "tr not null");
        assertNotNull(trl, "trl not null");
        assertNotNull(trll, "trll not null");

        assertEquals("[or]", t.getContents(), "t    has correct contents '[or]'");
        assertEquals("[and]", tr.getContents(), "tr   has correct contents '[and]'");
        assertEquals("[or]", trl.getContents(), "trl  has correct contents '[or]'");
        assertEquals("[or]", trll.getContents(), "trll has correct contents '[or]'");

        // expected leaf nodes
        assertNotNull(tl, "tl not null");
        assertNotNull(trr, "trr not null");

        assertNotNull(trlr, "trlr not null");
        assertNotNull(trlll, "trlll not null");
        assertNotNull(trllr, "trllr not null");

        assertEquals("TYPE=Foo", tl.getContents(), "tl  has correct contents 'TYPE=Foo'");
        assertEquals("TYPE=Bar", trr.getContents(), "trr has correct contents 'TYPE=Bar'");

        assertEquals("CATEGORY=SA", trlr.getContents(), "trlr has correct contents 'CATEGORY=SA'");
        assertEquals("CATEGORY=FEAT", trlll.getContents(), "trlr has correct contents 'CATEGORY=FEAT'");
        assertEquals("NATURE=AUTO", trllr.getContents(), "trlr has correct contents 'NATURE=AUTO'");


        // check that leaves really are leaves
        assertNull(tl.getLeftTree(), "tl left tree is null (i.e. is a leaf node)");
        assertNull(tl.getRightTree(), "tl right tree is null (i.e. is a leaf node)");

        assertNull(trr.getLeftTree(), "trr left tree is null (i.e. is a leaf node)");
        assertNull(trr.getRightTree(), "trr right tree is null (i.e. is a leaf node)");

        assertNull(trlr.getLeftTree(), "trll left tree is null (i.e. is a leaf node)");
        assertNull(trlr.getRightTree(), "trlr right tree is null (i.e. is a leaf node)");

        assertNull(trlll.getLeftTree(), "trlll left tree is null (i.e. is a leaf node)");
        assertNull(trlll.getRightTree(), "trlll right tree is null (i.e. is a leaf node)");

        assertNull(trllr.getLeftTree(), "trlll left tree is null (i.e. is a leaf node)");
        assertNull(trllr.getRightTree(), "trlll right tree is null (i.e. is a leaf node)");
    }
}
