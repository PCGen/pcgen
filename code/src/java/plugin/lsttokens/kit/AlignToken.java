/*
 * Copyright 2006 (C) Aaron Divinsky <boomer70@yahoo.com>
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

package plugin.lsttokens.kit;

import java.util.List;
import java.util.StringTokenizer;

import pcgen.cdom.base.Constants;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.cdom.reference.ReferenceUtilities;
import pcgen.core.PCAlignment;
import pcgen.core.kit.KitAlignment;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Handles the ALIGN tag for a Kit. Also will handle any Common tags on the
 * ALIGN line.
 */
public class AlignToken extends AbstractTokenWithSeparator<KitAlignment> implements CDOMPrimaryToken<KitAlignment>
{
    private static final Class<PCAlignment> ALIGNMENT_CLASS = PCAlignment.class;

    /**
     * Gets the name of the tag this class will parse.
     *
     * @return Name of the tag this class handles
     */
    @Override
    public String getTokenName()
    {
        return "ALIGN";
    }

    @Override
    public Class<KitAlignment> getTokenClass()
    {
        return KitAlignment.class;
    }

    @Override
    protected char separator()
    {
        return '|';
    }

    @Override
    protected ParseResult parseTokenWithSeparator(LoadContext context, KitAlignment kitAlignment, String value)
    {
        StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);

        while (tok.hasMoreTokens())
        {
            String tokText = tok.nextToken();
            CDOMSingleRef<PCAlignment> ref = context.getReferenceContext().getCDOMReference(ALIGNMENT_CLASS, tokText);
            kitAlignment.addAlignment(ref);
        }
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, KitAlignment kitAlignment)
    {
        List<CDOMSingleRef<PCAlignment>> alignments = kitAlignment.getAlignments();
        if (alignments == null)
        {
            return null;
        }
        return new String[]{ReferenceUtilities.joinLstFormat(alignments, Constants.PIPE)};
    }
}
