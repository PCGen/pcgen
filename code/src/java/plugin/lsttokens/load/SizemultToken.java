/*
 * Copyright (c) 2010 Tom Parker <thpr@users.sourceforge.net>
 * Copyright 2006 (C) Devon Jones <soulcatcher@evilsoft.org>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package plugin.lsttokens.load;

import java.math.BigDecimal;

import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.SizeAdjustment;
import pcgen.core.system.LoadInfo;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.rules.persistence.token.PostDeferredToken;

/**
 * {@code SizemultToken}
 */
public class SizemultToken extends AbstractTokenWithSeparator<LoadInfo>
        implements CDOMPrimaryToken<LoadInfo>, PostDeferredToken<LoadInfo>
{

    @Override
    public String getTokenName()
    {
        return "SIZEMULT";
    }

    @Override
    protected ParseResult parseTokenWithSeparator(LoadContext context, LoadInfo info, String value)
    {
        int pipeLoc = value.indexOf('|');
        if (pipeLoc == -1)
        {
            return new ParseResult.Fail(getTokenName() + " requires a pipe, found : " + value);
        }
        if (pipeLoc != value.lastIndexOf('|'))
        {
            return new ParseResult.Fail(getTokenName() + " requires only one pipe, found : " + value);
        }
        String sizeName = value.substring(0, pipeLoc);
        String multiplierString = value.substring(pipeLoc + 1);

        CDOMSingleRef<SizeAdjustment> size =
                context.getReferenceContext().getCDOMReference(SizeAdjustment.class, sizeName);
        /*
         * TODO Any way to handle the situation of the sizeName being
         * misspelled, etc? (old system did just first character)
         */
        try
        {
            BigDecimal multiplier = new BigDecimal(multiplierString);
            if (multiplier.compareTo(BigDecimal.ZERO) <= 0)
            {
                return new ParseResult.Fail(
                        getTokenName() + " requires a positive multiplier : " + multiplierString + " in value: " + value);
            }
            info.addSizeAdjustment(size, multiplier);
        } catch (NumberFormatException nfe)
        {
            return new ParseResult.Fail(
                    getTokenName() + " misunderstood multiplier : " + multiplierString + " in value: " + value);
        }
        return ParseResult.SUCCESS;
    }

    @Override
    protected char separator()
    {
        return '|';
    }

    @Override
    public String[] unparse(LoadContext context, LoadInfo info)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<LoadInfo> getTokenClass()
    {
        return LoadInfo.class;
    }

    @Override
    public Class<LoadInfo> getDeferredTokenClass()
    {
        return LoadInfo.class;
    }

    @Override
    public int getPriority()
    {
        return 0;
    }

    @Override
    public boolean process(LoadContext context, LoadInfo info)
    {
        info.resolveSizeAdjustmentMap();
        return true;
    }
}
