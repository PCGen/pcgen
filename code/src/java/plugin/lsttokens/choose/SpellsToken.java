/*
 * Copyright 2007 (C) Thomas Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.choose;

import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.AssociationListKey;
import pcgen.core.spell.Spell;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractQualifiedChooseToken;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.Logging;

/**
 * New chooser plugin, handles Spells.
 */
public class SpellsToken extends AbstractQualifiedChooseToken<Spell>
{
    private static final Class<Spell> SPELL_CLASS = Spell.class;

    @Override
    public String getTokenName()
    {
        return "SPELLS";
    }

    @Override
    public String getParentToken()
    {
        return "CHOOSE";
    }

    @Override
    public Class<CDOMObject> getTokenClass()
    {
        return CDOMObject.class;
    }

    @Override
    protected String getDefaultTitle()
    {
        return "Spell choice";
    }

    @Override
    protected AssociationListKey<Spell> getListKey()
    {
        return AssociationListKey.getKeyFor(SPELL_CLASS, "CHOOSE*SPELL");
    }

    @Override
    public Spell decodeChoice(LoadContext context, String s)
    {
        return context.getReferenceContext().silentlyGetConstructedCDOMObject(SPELL_CLASS, s);
    }

    @Override
    public String encodeChoice(Spell choice)
    {
        return choice.getKeyName();
    }

    @Override
    protected ParseResult parseTokenWithSeparator(LoadContext context, CDOMObject obj, String value)
    {
        return super.parseTokenWithSeparator(context, context.getReferenceContext().getManufacturer(SPELL_CLASS), obj,
                processMagicalWords(value));
    }

    private String processMagicalWords(String value)
    {
        StringTokenizer st = new StringTokenizer(value, Constants.PIPE, true);
        StringBuilder sb = new StringBuilder(value.length() + 40);
        while (st.hasMoreTokens())
        {
            String tok = st.nextToken();
            if ("DOMAIN.".regionMatches(true, 0, tok, 0, 7))
            {
                final String profKey = tok.substring(7);
                Logging.errorPrint("CHOOSE:SPELLS|DOMAIN is deprecated, " + "has been changed to DOMAINLIST=");
                sb.append("DOMAINLIST=").append(profKey);
            } else if (Constants.LST_CLASS_DOT.regionMatches(true, 0, tok, 0, 6))
            {
                final String profKey = tok.substring(6);
                Logging.errorPrint("CHOOSE:SPELLS|CLASS is deprecated, " + "has been changed to CLASSLIST=");
                sb.append("CLASSLIST=").append(profKey);
            } else if ("DOMAIN=".regionMatches(true, 0, tok, 0, 7))
            {
                final String profKey = tok.substring(7);
                Logging.errorPrint("CHOOSE:SPELLS|DOMAIN is deprecated, " + "has been changed to DOMAINLIST=");
                sb.append("DOMAINLIST=").append(profKey);
            } else if (Constants.LST_CLASS_EQUAL.regionMatches(true, 0, tok, 0, 6))
            {
                final String profKey = tok.substring(6);
                Logging.errorPrint("CHOOSE:SPELLS|CLASS is deprecated, " + "has been changed to CLASSLIST=");
                sb.append("CLASSLIST=").append(profKey);
            } else if (Constants.LST_ANY.regionMatches(true, 0, tok, 0, 3))
            {
                final String remainder = tok.length() > 3 ? tok.substring(3) : "";
                Logging.errorPrint("CHOOSE:SPELLS|ANY is deprecated, " + "has been changed to ALL");
                sb.append("ALL").append(remainder);
            } else
            {
                sb.append(tok);
            }
        }
        return sb.toString();
    }

    @Override
    protected String getPersistentFormat()
    {
        return "SPELL";
    }

}
