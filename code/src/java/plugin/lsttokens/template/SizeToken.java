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
package plugin.lsttokens.template;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.formula.FixedSizeFormula;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.core.PCTemplate;
import pcgen.core.SizeAdjustment;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.DeferredToken;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.Logging;

/**
 * Class deals with SIZE Token
 */
public class SizeToken extends AbstractNonEmptyToken<PCTemplate>
        implements CDOMPrimaryToken<PCTemplate>, DeferredToken<PCTemplate>
{

    @Override
    public String getTokenName()
    {
        return "SIZE";
    }

    @Override
    protected ParseResult parseNonEmptyToken(LoadContext context, PCTemplate template, String value)
    {
        context.getObjectContext().put(template, StringKey.SIZEFORMULA, value);
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, PCTemplate template)
    {
        String res = context.getObjectContext().getString(template, StringKey.SIZEFORMULA);
        if (res == null)
        {
            return null;
        }
        return new String[]{res};
    }

    @Override
    public Class<PCTemplate> getTokenClass()
    {
        return PCTemplate.class;
    }

    @Override
    public boolean process(LoadContext context, PCTemplate template)
    {
        String value = template.get(StringKey.SIZEFORMULA);
        if (value == null)
        {
            return true;
        }
        SizeAdjustment size =
                context.getReferenceContext().silentlyGetConstructedCDOMObject(SizeAdjustment.class, value);
        Formula sizeFormula;
        if (size == null)
        {
            sizeFormula = FormulaFactory.getFormulaFor(value);
        } else
        {
            sizeFormula = new FixedSizeFormula(CDOMDirectSingleRef.getRef(size));
        }
        if (!sizeFormula.isValid())
        {
            Logging.errorPrint("Size in " + getTokenName() + " was not valid: " + sizeFormula.toString(), context);
            return false;
        }
        context.getObjectContext().put(template, FormulaKey.SIZE, sizeFormula);
        return false;
    }

    @Override
    public Class<PCTemplate> getDeferredTokenClass()
    {
        return PCTemplate.class;
    }
}
