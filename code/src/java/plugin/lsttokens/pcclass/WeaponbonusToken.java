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
package plugin.lsttokens.pcclass;

import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.ChoiceSet;
import pcgen.cdom.base.ConcretePersistentTransitionChoice;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.PersistentChoiceActor;
import pcgen.cdom.base.PersistentTransitionChoice;
import pcgen.cdom.choiceset.ReferenceChoiceSet;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.reference.ReferenceUtilities;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.WeaponProf;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.TokenUtilities;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.DeferredToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with WEAPONBONUS Token
 */
public class WeaponbonusToken extends AbstractTokenWithSeparator<PCClass>
        implements CDOMPrimaryToken<PCClass>, DeferredToken<PCClass>, PersistentChoiceActor<WeaponProf>
{

    private static final Class<WeaponProf> WEAPONPROF_CLASS = WeaponProf.class;

    @Override
    public String getTokenName()
    {
        return "WEAPONBONUS";
    }

    @Override
    protected char separator()
    {
        return '|';
    }

    @Override
    protected ParseResult parseTokenWithSeparator(LoadContext context, PCClass pcc, String value)
    {
        StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);
        boolean foundAny = false;
        boolean foundOther = false;

        while (tok.hasMoreTokens())
        {
            String tokText = tok.nextToken();
            if (Constants.LST_ALL.equals(tokText))
            {
                foundAny = true;
                CDOMReference<WeaponProf> ref = context.getReferenceContext().getCDOMAllReference(WEAPONPROF_CLASS);
                context.getObjectContext().addToList(pcc, ListKey.WEAPONBONUS, ref);
            } else
            {
                foundOther = true;
                CDOMReference<WeaponProf> ref = TokenUtilities.getTypeOrPrimitive(context, WEAPONPROF_CLASS, tokText);
                if (ref == null)
                {
                    return new ParseResult.Fail("  Error was encountered while parsing " + getTokenName());
                }
                context.getObjectContext().addToList(pcc, ListKey.WEAPONBONUS, ref);
            }
        }
        if (foundAny && foundOther)
        {
            return new ParseResult.Fail(
                    "Non-sensical " + getTokenName() + ": Contains ANY and a specific reference: " + value);
        }
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, PCClass pcc)
    {
        Changes<CDOMReference<WeaponProf>> changes =
                context.getObjectContext().getListChanges(pcc, ListKey.WEAPONBONUS);
        Collection<CDOMReference<WeaponProf>> added = changes.getAdded();
        if (added == null || added.isEmpty())
        {
            // Zero indicates no add
            return null;
        }
        return new String[]{ReferenceUtilities.joinLstFormat(added, Constants.PIPE)};
    }

    @Override
    public Class<PCClass> getTokenClass()
    {
        return PCClass.class;
    }

    @Override
    public boolean process(LoadContext context, PCClass obj)
    {
        List<CDOMReference<WeaponProf>> weaponbonus = obj.getListFor(ListKey.WEAPONBONUS);
        if (weaponbonus != null)
        {
            ReferenceChoiceSet<WeaponProf> rcs = new ReferenceChoiceSet<>(weaponbonus);
            ChoiceSet<WeaponProf> cs = new ChoiceSet<>(getTokenName(), rcs);
            cs.setTitle("Bonus WeaponProf Choice");
            PersistentTransitionChoice<WeaponProf> tc =
                    new ConcretePersistentTransitionChoice<>(cs, FormulaFactory.ONE);
            context.getObjectContext().addToList(obj, ListKey.ADD, tc);
            tc.setChoiceActor(this);
        }
        return true;
    }

    @Override
    public Class<PCClass> getDeferredTokenClass()
    {
        return PCClass.class;
    }

    @Override
    public void applyChoice(CDOMObject owner, WeaponProf choice, PlayerCharacter pc)
    {
        pc.addWeaponBonus(owner, choice);
    }

    @Override
    public boolean allow(WeaponProf item, PlayerCharacter pc, boolean allowStack)
    {
        return true;
    }

    @Override
    public String encodeChoice(WeaponProf choice)
    {
        return choice.getKeyName();
    }

    @Override
    public WeaponProf decodeChoice(LoadContext context, String s)
    {
        return context.getReferenceContext().silentlyGetConstructedCDOMObject(WeaponProf.class, s);
    }

    @Override
    public void restoreChoice(PlayerCharacter pc, CDOMObject owner, WeaponProf choice)
    {
        pc.addWeaponBonus(owner, choice);
    }

    @Override
    public void removeChoice(PlayerCharacter pc, CDOMObject owner, WeaponProf choice)
    {
        pc.removeWeaponBonus(owner, choice);
    }
}
