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
import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.enumeration.SubClassCategory;
import pcgen.core.PCClass;
import pcgen.core.PCTemplate;
import pcgen.core.Race;
import pcgen.core.SubClass;
import pcgen.gui2.converter.event.TokenProcessEvent;
import pcgen.gui2.converter.event.TokenProcessorPlugin;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.ParseResult;

public class FavClassConvertPlugin extends AbstractToken implements TokenProcessorPlugin
{
    public static final Class<PCClass> PCCLASS_CLASS = PCClass.class;
    public static final Class<SubClass> SUBCLASS_CLASS = SubClass.class;

    @Override
    public String process(TokenProcessEvent tpe)
    {
        String value = tpe.getValue();
        if (!value.startsWith(Constants.LST_CHOOSE_COLON))
        {
            // Don't consume, force the default processor to do the work...
            return null;
        }

        String choices = value.substring(7);
        ParseResult pr = checkNonEmpty(choices);
        if (!pr.passed())
        {
            return "Empty";
        }
        pr = checkForIllegalSeparator('|', choices);
        if (!pr.passed())
        {
            return "Illegal Separator";
        }
        boolean foundAny = false;
        boolean foundOther = false;

        StringTokenizer tok = new StringTokenizer(choices, Constants.PIPE);

        List<CDOMReference<? extends PCClass>> refList = new ArrayList<>();
        LoadContext context = tpe.getContext();
        while (tok.hasMoreTokens())
        {
            CDOMReference<? extends PCClass> ref;
            String token = tok.nextToken();
            if (Constants.LST_ALL.equalsIgnoreCase(token) || Constants.LST_ANY.equalsIgnoreCase(token))
            {
                foundAny = true;
                ref = context.getReferenceContext().getCDOMAllReference(PCCLASS_CLASS);
            } else
            {
                foundOther = true;
                int dotLoc = token.indexOf('.');
                if (dotLoc == -1)
                {
                    // Primitive
                    ref = context.getReferenceContext().getCDOMReference(PCCLASS_CLASS, token);
                } else
                {
                    // SubClass
                    String parent = token.substring(0, dotLoc);
                    String subclass = token.substring(dotLoc + 1);
                    SubClassCategory scc = SubClassCategory.getConstant(parent);
                    ref = context.getReferenceContext().getManufacturerId(scc).getReference(subclass);
                }
            }
            refList.add(ref);
        }
        if (foundAny && foundOther)
        {
            return "Non-sensical " + getTokenName() + ": Contains ANY and a specific reference: " + value;
        }

        String name = tpe.getPrimary().get(StringKey.CONVERT_NAME);
        // TODO Need a method of guaranteeing this name is unique?
        String templName = "Race " + name + " Favored Class";
        tpe.append("TEMPLATE:");
        tpe.append(templName);
        PCTemplate templ = new PCTemplate();
        context.unconditionallyProcess(templ, "FAVOREDCLASS", "%LIST");
        StringBuilder chooseValue = new StringBuilder();
        chooseValue.append("CLASS|");
        boolean first = true;
        for (CDOMReference<? extends PCClass> ref : refList)
        {
            if (!first)
            {
                chooseValue.append(Constants.COMMA);
            }
            first = false;
            String prefix = ref.getPersistentFormat();
            if (prefix.startsWith("SUBCLASS="))
            {
                chooseValue.append(prefix.substring(9));
                chooseValue.append('.');
            }
            chooseValue.append(ref.getLSTformat(false));
        }

        context.unconditionallyProcess(templ, "CHOOSE", chooseValue.toString());
        templ.setName(templName);
        tpe.inject(templ);
        tpe.consume();
        return null;
    }

    @Override
    public Class<? extends CDOMObject> getProcessedClass()
    {
        return Race.class;
    }

    @Override
    public String getProcessedToken()
    {
        return "FAVCLASS";
    }

    @Override
    public String getTokenName()
    {
        return getProcessedToken();
    }
}
