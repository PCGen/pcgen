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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.nfunk.jep.ParseException;

public class ParameterTree
{
    String contents;
    ParameterTree left = null;
    ParameterTree right = null;
    public static final String OR_STRING = "[or]";
    public static final String AND_STRING = "[and]";
    static String orPatString = "\\[or\\]";
    static String andPatString = "\\[and\\]";

    private static String leftBracket = "(";
    private static String leftPatString = "\\(";
    private static String rightPatString = "\\)";

    static String patString = '(' + leftPatString + '|' + rightPatString + '|' + orPatString + '|' + andPatString + ')';

    static Pattern pat = Pattern.compile(patString);

    // the grouping pattern & matcher
    private static final String PAREN_STRING = '(' + leftPatString + '|' + rightPatString + ')';
    private static final Pattern PAREN_PATTERN = Pattern.compile(PAREN_STRING);

    // the opertor pattern & matcher
    private static final String OPERATOR_STRING = '(' + orPatString + '|' + andPatString + ')';
    private static final Pattern OPERATOR_PATTERN = Pattern.compile(OPERATOR_STRING);

    private static int getIndexOfClosingParen(final String s, final int start) throws ParseException
    {
        final Matcher aMat = PAREN_PATTERN.matcher(s);

        aMat.find(start);
        int level = 1;

        while (level > 0)
        {
            if (!aMat.find())
            {
                throw new ParseException("unbalanced parenthesis in " + s);
            }

            if (leftBracket.equalsIgnoreCase(aMat.group()))
            {
                level++;
            } else
            {
                level--;
            }
        }

        return aMat.end();
    }

    public static ParameterTree makeTree(final String source) throws ParseException
    {
        final Matcher mat = ParameterTree.pat.matcher(source);

        if (mat.find())
        {
            return makeTree(source, false);
        } else
        {
            return new ParameterTree(source);
        }
    }

    private static ParameterTree makeTree(final String source, final boolean operatorNeeded) throws ParseException
    {
        final Matcher pM = PAREN_PATTERN.matcher(source);
        final boolean hasP = pM.find();

        ParameterTree t;

        if (hasP)
        {
            final String pre = source.substring(0, pM.start());
            final int end = getIndexOfClosingParen(source, pM.start());

            if (pre.isEmpty())
            {

                final String inside = source.substring(pM.end(), end - 1);
                t = makeTree(inside, false);

            } else
            {

                t = toTree(pre, operatorNeeded);

                final Matcher rM = OPERATOR_PATTERN.matcher(t.getContents());

                if (rM.find())
                {

                    if (t.getRightTree() == null)
                    {
                        // Since we found an operator in the root of the tree, but
                        // the right subtree is null, then the parens must contain
                        // a complete expression (or the whole thing is illegal)
                        // so make a tree from the contents and append it here
                        // remember to strip off the outer parens.
                        final String inside = source.substring(pM.end(), end - 1);
                        t.setRightTree(makeTree(inside, false));
                    } else
                    {
                        // The root of the tree generated from the first section of the
                        // string has something in its right sub tree. That means the
                        // parenthesised expression is a part of that string (since
                        // if it ended with an operator that would be in a separate
                        // "root")
                        final StringBuilder rNodeContents = new StringBuilder();
                        rNodeContents.append(t.getRightTree().getContents());
                        rNodeContents.append(source.substring(pM.start(), end));
                    }

                } else
                {
                    // root of the generated tree doesn't contain an operator, so
                    // the paren expression should be tacked onto it.
                    final String parenExp = source.substring(pM.end() - 1, end);
                    t.setContents(t.getContents() + parenExp);
                }
            }

            if (end < source.length())
            {
                final String sEnd = source.substring(end);
                final ParameterTree r = makeTree(sEnd, true);

                ParameterTree c = r;

                final Matcher cM = OPERATOR_PATTERN.matcher(r.getContents());

                if (!cM.find())
                {
                    throw new ParseException("expected \"" + source.substring(end) + "\" to begin with an operator");
                }

                while (c.getLeftTree() != null)
                {
                    c = c.getLeftTree();
                }

                c.setLeftTree(t);
                t = r;
            }
        } else
        {
            t = toTree(source, operatorNeeded);
        }

        return t;
    }

    private static ParameterTree toTree(final String source, final boolean operatorNeeded) throws ParseException
    {
        String s = source;
        // the opertor matcher
        Matcher oM = OPERATOR_PATTERN.matcher(s);
        ParameterTree cT = new ParameterTree(""); //current Tree

        boolean hasO = oM.find();

        // this is for operators, obviously
        if (hasO && operatorNeeded)
        {
            if (oM.start() != 0)
            {
                throw new ParseException("expected \"" + s + "\" to begin with an operator");
            } else
            {
                cT = new ParameterTree(oM.group());
                final int end = oM.end();
                s = s.substring(end);
                oM = OPERATOR_PATTERN.matcher(s);
                hasO = oM.find();
            }
        }

        int start = 0;

        while (hasO)
        {

            final String pre = s.substring(start, oM.start());

            final ParameterTree P = new ParameterTree(pre); // pre Tree
            final ParameterTree R = new ParameterTree(oM.group()); // root Tree - must be an operator (it matched)

            // is the "root" of the current tree an operator
            final Matcher cM = OPERATOR_PATTERN.matcher(cT.getContents());

            if (cM.find())
            {
                cT.setRightTree(P);
                R.setLeftTree(cT);
                // right branch of R is null
            } else
            {
                R.setLeftTree(P);
                // can discard current tree, it's empty (first iteration)
            }

            // root becomes new current tree
            cT = R;

            start = oM.end();
            hasO = oM.find();
        }

        // no more operators, but string is not empty
        if (start < s.length())
        {

            final ParameterTree p = new ParameterTree(s.substring(start));
            final Matcher cM = OPERATOR_PATTERN.matcher(cT.getContents());

            if (cM.find())
            {
                // current tree has operator in root
                cT.setRightTree(p);
            } else
            {
                // current tree is the default empty tree created above
                cT = p;
            }
        }

        return cT;
    }

    /**
     * @param data The value that will end up in the node of the tree
     */
    ParameterTree(final String data)
    {
        super();
        this.contents = data;
    }

    public void setContents(final String data)
    {
        this.contents = data;
    }

    /**
     * @return the Contents
     */
    public String getContents()
    {
        return contents;
    }

    /**
     * @return the left subtree
     */
    public ParameterTree getLeftTree()
    {
        return left;
    }

    /**
     * @param l the ParameterTree to add as the left sub tree
     */
    public void setLeftTree(final ParameterTree l)
    {
        left = l;
    }

    /**
     * @return the right subtree
     */
    public ParameterTree getRightTree()
    {
        return right;
    }

    /**
     * @param r the ParameterTree to add as the right sub tree
     */
    public void setRightTree(final ParameterTree r)
    {
        right = r;
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder(200);

        sb.append('[');
        sb.append(contents);
        sb.append(' ');

        if (left != null)
        {
            sb.append(left.toString());
        }

        if (right != null)
        {
            sb.append(right.toString());
        }

        sb.append(']');

        return sb.toString();
    }
}
