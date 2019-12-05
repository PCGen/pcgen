/*
 * Copyright (c) 2009 Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.rules.persistence.token;

import java.util.ArrayList;
import java.util.List;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.CDOMObject;
import pcgen.gui2.converter.event.TokenProcessEvent;
import pcgen.gui2.converter.event.TokenProcessorPlugin;

public abstract class AbstractPreEqualConvertPlugin implements TokenProcessorPlugin
{
    public static final String FLOW_LEFT =
            "Set unspecified values to next identified value " + "(items queue until set/equals sign flows left)";
    public static final String FLOW_RIGHT = "Set unspecified values to previous identified value "
            + "(equals sign holds on unspecified items until redefined)";
    public static final String SET_ONE = "Set unspecified values to one (identify as 'present')";

    // Just process over these magical tokens for now
    @Override
    public String process(TokenProcessEvent tpe)
    {
        String formula = tpe.getValue();

        int commaLoc = formula.indexOf(',');
        if (commaLoc == -1)
        {
            return "Prerequisite " + tpe.getKey() + " must have a count: " + formula;
        }
        if (commaLoc == formula.length() - 1)
        {
            return "Prerequisite " + tpe.getKey() + " can not have only a count: " + formula;
        }
        String num = formula.substring(0, commaLoc);

        try
        {
            Integer.parseInt(num);
        } catch (NumberFormatException nfe)
        {
            return "'" + num + "' in " + tpe.getKey() + " is not a valid integer";
        }

        String rest = formula.substring(commaLoc + 1);
        commaLoc = rest.indexOf(',');
        if (commaLoc == -1)
        {
            doPrefix(tpe, num);
            int equalLoc = rest.indexOf('=');
            if (equalLoc == -1)
            {
                // No equals
                tpe.append(rest);
                tpe.append("=1");
            } else
            {
                // unambiguous
                tpe.append(rest);
            }
        } else
        {
            String[] strings = rest.split(",");
            int withEquals = 0;
            boolean withoutEquals = false;
            boolean lastWithEquals = false;
            List<String> list = new ArrayList<>();
            for (String tok : strings)
            {
                int equalLoc = tok.indexOf('=');
                if (equalLoc == -1)
                {
                    withoutEquals = true;
                    lastWithEquals = false;
                } else
                {
                    withEquals++;
                    lastWithEquals = true;
                }
                list.add(tok);
            }
            if (withEquals == 1 && lastWithEquals)
            {
                // Propagate item to all...
                String last = strings[strings.length - 1];
                int equalLoc = last.indexOf('=');
                String equal = last.substring(equalLoc + 1);
                doPrefix(tpe, num);
                tpe.append(StringUtil.join(list, "=" + equal + ","));
            } else if (withEquals > 0 && withoutEquals)
            {
                StringBuilder one = new StringBuilder();
                StringBuilder left = new StringBuilder();
                StringBuilder right = new StringBuilder();
                one.append(tpe.getKey());
                one.append(':');
                one.append(num);
                one.append(',');
                left.append(tpe.getKey());
                left.append(':');
                left.append(num);
                left.append(',');
                right.append(tpe.getKey());
                right.append(':');
                right.append(num);
                right.append(',');
                boolean needComma = false;
                Integer lastValue = null;
                for (int i = 0;i < strings.length;i++)
                {
                    if (needComma)
                    {
                        one.append(',');
                        left.append(',');
                        right.append(',');
                    }
                    needComma = true;
                    String tok = strings[i];
                    int equalLoc = tok.indexOf('=');
                    one.append(tok);
                    left.append(tok);
                    right.append(tok);
                    if (equalLoc == -1)
                    {
                        one.append("=1");
                        right.append('=');
                        if (lastValue == null)
                        {
                            right.append('1');
                        } else
                        {
                            right.append(lastValue);
                        }
                        left.append('=');
                        left.append(getNextValue(strings, i));
                    } else
                    {
                        lastValue = Integer.decode(tok.substring(equalLoc + 1));
                    }
                }
                List<String> descr = new ArrayList<>();
                String oneResult = one.toString();
                String rightResult = right.toString();
                String leftResult = left.toString();
                if (oneResult.equals(leftResult) && leftResult.equals(rightResult))
                {
                    tpe.append(oneResult);
                } else
                {
                    descr.add(oneResult + " ... " + SET_ONE);
                    descr.add(rightResult + " ... " + FLOW_RIGHT);
                    descr.add(leftResult + " ... " + FLOW_LEFT);
                    List<String> choice = new ArrayList<>();
                    choice.add(oneResult);
                    choice.add(rightResult);
                    choice.add(leftResult);
                    String decision = tpe.getDecider().getConversionDecision(
                            "Resolve ambiguity for " + getProcessedToken() + ":" + formula, descr, choice, 0);
                    tpe.append(decision);
                }
            } else if (withEquals > 0) // && !withoutEquals
            {
                doPrefix(tpe, num);
                tpe.append(StringUtil.join(list, ","));
            } else if (withoutEquals) // && withEquals == 0
            {
                doPrefix(tpe, num);
                tpe.append(StringUtil.join(list, "=1,"));
                tpe.append("=1");
            }
        }
        tpe.consume();
        return null;
    }

    private void doPrefix(TokenProcessEvent tpe, String num)
    {
        tpe.append(tpe.getKey());
        tpe.append(':');
        tpe.append(num);
        tpe.append(',');
    }

    private int getNextValue(String[] strings, int i)
    {
        for (int j = i + 1;j < strings.length;j++)
        {
            String tok = strings[j];
            int equalLoc = tok.indexOf('=');
            if (equalLoc != -1)
            {
                return Integer.parseInt(tok.substring(equalLoc + 1));
            }
        }
        return 1;
    }

    @Override
    public Class<? extends CDOMObject> getProcessedClass()
    {
        return CDOMObject.class;
    }
}
