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
package plugin.converter;

import java.util.ArrayList;
import java.util.List;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.CDOMObject;
import pcgen.gui2.converter.event.TokenProcessEvent;
import pcgen.gui2.converter.event.TokenProcessorPlugin;
import pcgen.rules.persistence.token.AbstractPreEqualConvertPlugin;

public class PreVisionInvertedConvertPlugin implements TokenProcessorPlugin
{

    public static final String SET_ANY = "Set unspecified values to ANY (identify as 'present')";
    public static final String SET_ZERO_ANY = "Set zero values to ANY (identify as 'present')";
    public static final String SET_ZERO_ONE = "Set zero values to 1 (identify as 'possessing distance')";

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
        String rest = formula.substring(commaLoc + 1);
        try
        {
            Integer.parseInt(num);
        } catch (NumberFormatException nfe)
        {
            return '\'' + num + "' in " + tpe.getKey() + " is not a valid integer";
        }
        // Work rest here:
        commaLoc = rest.indexOf(',');
        if (commaLoc == -1)
        {
            // Unambiguous for y=z, now test for y or y=0
            int equalLoc = rest.indexOf('=');
            if (equalLoc == -1)
            {
                // No equals
                tpe.append(getPrefix(tpe, num));
                tpe.append(rest);
                tpe.append("=ANY");
            } else
            {
                processZero(formula, tpe, num, rest);
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
                int eqLoc = last.indexOf('=');
                String equal = last.substring(eqLoc + 1);
                tpe.append(getPrefix(tpe, num));
                tpe.append(StringUtil.join(list, '=' + equal + ','));
            } else if (withEquals > 0 && withoutEquals)
            {
                StringBuilder onebase = new StringBuilder();
                StringBuilder leftbase = new StringBuilder();
                StringBuilder rightbase = new StringBuilder();
                boolean needComma = false;
                Integer lastValue = null;
                for (int i = 0;i < strings.length;i++)
                {
                    if (needComma)
                    {
                        onebase.append(',');
                        leftbase.append(',');
                        rightbase.append(',');
                    }
                    needComma = true;
                    String tok = strings[i];
                    int equalLoc = tok.indexOf('=');
                    onebase.append(tok);
                    leftbase.append(tok);
                    rightbase.append(tok);
                    if (equalLoc == -1)
                    {
                        onebase.append("=ANY");
                        leftbase.append('=');
                        if (lastValue == null)
                        {
                            leftbase.append("ANY");
                        } else
                        {
                            leftbase.append(lastValue);
                        }
                        rightbase.append('=');
                        rightbase.append(getNextValue(strings, i));
                    } else
                    {
                        lastValue = Integer.decode(tok.substring(equalLoc + 1));
                    }
                }
                List<String> descr = new ArrayList<>();
                List<String> choice = new ArrayList<>();
                processChoices(tpe, num, descr, choice, onebase.toString(), SET_ANY);
                processChoices(tpe, num, descr, choice, rightbase.toString(), AbstractPreEqualConvertPlugin.FLOW_LEFT);
                processChoices(tpe, num, descr, choice, leftbase.toString(), AbstractPreEqualConvertPlugin.FLOW_RIGHT);
                String result = null;
                boolean match = true;
                for (String c : choice)
                {
                    if (result == null)
                    {
                        result = c;
                    } else if (!result.equals(c))
                    {
                        match = false;
                        break;
                    }
                }
                if (match)
                {
                    tpe.append(result);
                } else
                {
                    String decision = tpe.getDecider().getConversionDecision(
                            "Resolve ambiguity for " + getProcessedToken() + ':' + formula, descr, choice, 0);
                    tpe.append(decision);
                }
            } else if (withEquals > 0) // && !withoutEquals
            {
                processZero(formula, tpe, num, strings);
            } else if (withoutEquals) // && withEquals == 0
            {
                tpe.append(getPrefix(tpe, num));
                tpe.append(StringUtil.join(list, "=ANY,"));
                tpe.append("=ANY");
            }
        }
        tpe.consume();
        return null;
    }

    private void processChoices(TokenProcessEvent tpe, String num, List<String> descr, List<String> choice,
            String leftbaseResult, String d)
    {
        List<String> leftChoices = createZeroChoices(tpe, num, leftbaseResult.split(","));
        if (leftChoices.get(0).equals(leftChoices.get(1)))
        {
            descr.add(leftbaseResult + " ... " + d);
            choice.add(leftbaseResult);
        } else
        {
            descr.add(leftChoices.get(0) + " ... " + d + " and " + SET_ZERO_ONE);
            choice.add(leftChoices.get(0));
            descr.add(leftChoices.get(1) + " ... " + d + " and " + SET_ZERO_ANY);
            choice.add(leftChoices.get(1));
        }
    }

    private void processZero(String formula, TokenProcessEvent tpe, String num, String... base)
    {
        List<String> choice = createZeroChoices(tpe, num, base);
        String oneChoice = choice.get(0);
        String zeroChoice = choice.get(1);
        if (oneChoice.equals(zeroChoice))
        {
            tpe.append(oneChoice);
        } else
        {
            List<String> descr = new ArrayList<>();
            descr.add(oneChoice + " ... " + SET_ZERO_ONE);
            descr.add(zeroChoice + " ... " + SET_ZERO_ANY);
            String decision = tpe.getDecider().getConversionDecision(
                    "Resolve ambiguity for " + getProcessedToken() + ':' + formula, descr, choice, 0);
            tpe.append(decision);
        }
    }

    private List<String> createZeroChoices(TokenProcessEvent tpe, String num, String... base)
    {
        StringBuilder one = getPrefix(tpe, num);
        StringBuilder any = getPrefix(tpe, num);
        boolean needComma = false;
        for (String tok : base)
        {
            // need to check zero...

            int equalLoc = tok.indexOf('=');
            String target = tok.substring(equalLoc + 1);
            if (needComma)
            {
                one.append(',');
                any.append(',');
            }
            needComma = true;

            if (!"ANY".equals(target) && Integer.parseInt(target) == 0)
            {
                String start = tok.substring(0, equalLoc);
                one.append(start);
                any.append(start);
                one.append("=1");
                any.append("=ANY");
            } else
            {
                one.append(tok);
                any.append(tok);
            }
        }
        String oneResult = one.toString();
        String anyResult = any.toString();
        List<String> choice = new ArrayList<>();
        choice.add(oneResult);
        choice.add(anyResult);
        return choice;
    }

    private StringBuilder getPrefix(TokenProcessEvent tpe, String num)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(tpe.getKey());
        sb.append(':');
        sb.append(num);
        sb.append(',');
        return sb;
    }

    private String getNextValue(String[] strings, int i)
    {
        for (int j = i + 1;j < strings.length;j++)
        {
            String tok = strings[j];
            int equalLoc = tok.indexOf('=');
            if (equalLoc != -1)
            {
                return tok.substring(equalLoc + 1);
            }
        }
        return "ANY";
    }

    @Override
    public Class<? extends CDOMObject> getProcessedClass()
    {
        return CDOMObject.class;
    }

    @Override
    public String getProcessedToken()
    {
        return "!PREVISION";
    }
}
