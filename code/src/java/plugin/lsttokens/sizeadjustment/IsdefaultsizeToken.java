/*
 * Copyright 2009 (C) Thomas Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.sizeadjustment;

import java.util.Collection;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.SizeAdjustment;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractYesNoToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.PostValidationToken;
import pcgen.util.Logging;

/**
 * Class deals with ISDEFAULTSIZE Token
 */
public class IsdefaultsizeToken extends AbstractYesNoToken<SizeAdjustment>
        implements CDOMPrimaryToken<SizeAdjustment>, PostValidationToken<SizeAdjustment>
{

    @Override
    public String getTokenName()
    {
        return "ISDEFAULTSIZE";
    }

    @Override
    protected ObjectKey<Boolean> getObjectKey()
    {
        return ObjectKey.IS_DEFAULT_SIZE;
    }

    @Override
    public Class<SizeAdjustment> getTokenClass()
    {
        return SizeAdjustment.class;
    }

    @Override
    public boolean process(LoadContext context, Collection<? extends SizeAdjustment> obj)
    {
        boolean returnValue = true;
        SizeAdjustment found = null;
        for (SizeAdjustment s : context.getReferenceContext().getConstructedCDOMObjects(SizeAdjustment.class))
        {
            if (s.getSafe(ObjectKey.IS_DEFAULT_SIZE))
            {
                if (found != null)
                {
                    Logging.errorPrint("Found more than one size claiming to be default: " + found.getKeyName()
                            + " and " + s.getKeyName());
                    returnValue = false;
                }
                found = s;
            }
        }
        if (found == null)
        {
            Logging.errorPrint("Did not find a default size");
            returnValue = false;
        }
        return returnValue;
    }

    @Override
    public Class<SizeAdjustment> getValidationTokenClass()
    {
        return SizeAdjustment.class;
    }

    @Override
    public int getPriority()
    {
        return 0;
    }
}
