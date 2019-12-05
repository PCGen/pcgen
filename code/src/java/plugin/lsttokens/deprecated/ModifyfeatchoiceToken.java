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
package plugin.lsttokens.deprecated;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.ChoiceActor;
import pcgen.cdom.base.ChoiceSet;
import pcgen.cdom.base.ChooseInformation;
import pcgen.cdom.base.ConcreteTransitionChoice;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.TransitionChoice;
import pcgen.cdom.choiceset.ModifyChoiceDecorator;
import pcgen.cdom.choiceset.ReferenceChoiceSet;
import pcgen.cdom.content.CNAbility;
import pcgen.cdom.content.TabInfo;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.reference.ReferenceManufacturer;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.chooser.CDOMChooserFacadeImpl;
import pcgen.facade.core.ChooserFacade.ChooserTreeViewType;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.TokenUtilities;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.chooser.ChooserFactory;
import pcgen.util.enumeration.Tab;

/**
 * Deals with the MODIFYFEATCHOICE token
 */
public class ModifyfeatchoiceToken extends AbstractTokenWithSeparator<Ability>
        implements CDOMPrimaryToken<Ability>, ChoiceActor<CNAbility>
{

    public static final Class<Ability> ABILITY_CLASS = Ability.class;

    @Override
    public String getTokenName()
    {
        return "MODIFYFEATCHOICE";
    }

    @Override
    protected char separator()
    {
        return '|';
    }

    @Override
    protected ParseResult parseTokenWithSeparator(LoadContext context, Ability ability, String value)
    {
        StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);

        List<CDOMReference<Ability>> refs = new ArrayList<>();
        ReferenceManufacturer<Ability> rm = context.getReferenceContext().getManufacturerId(AbilityCategory.FEAT);

        while (tok.hasMoreTokens())
        {
            String token = tok.nextToken();

            CDOMReference<Ability> ref = TokenUtilities.getTypeOrPrimitive(rm, token);
            if (ref == null)
            {
                return ParseResult.INTERNAL_ERROR;
            }

            refs.add(ref);
        }

        ReferenceChoiceSet<Ability> rcs = new ReferenceChoiceSet<>(refs);
        ModifyChoiceDecorator gfd = new ModifyChoiceDecorator(rcs);
        ChoiceSet<CNAbility> cs = new ChoiceSet<>(getTokenName(), gfd);

        TabInfo ti =
                context.getReferenceContext().silentlyGetConstructedCDOMObject(TabInfo.class, Tab.ABILITIES.toString());
        String singularName = ti.getResolvedName();
        if (singularName.endsWith("s"))
        {
            singularName = singularName.substring(0, singularName.length() - 1);
        }
        cs.setTitle("Select a " + singularName + " to modify");
        TransitionChoice<CNAbility> tc = new ConcreteTransitionChoice<>(cs, FormulaFactory.ONE);
        tc.setRequired(false);
        context.getObjectContext().put(ability, ObjectKey.MODIFY_CHOICE, tc);
        tc.setChoiceActor(this);

        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, Ability ability)
    {
        TransitionChoice<CNAbility> mc = context.getObjectContext().getObject(ability, ObjectKey.MODIFY_CHOICE);
        if (mc == null)
        {
            // Zero indicates no Token
            return null;
        }
        return new String[]{StringUtil.replaceAll(mc.getChoices().getLSTformat(), ",", Constants.PIPE)};
    }

    @Override
    public Class<Ability> getTokenClass()
    {
        return Ability.class;
    }

    @Override
    public void applyChoice(CDOMObject owner, CNAbility choice, PlayerCharacter pc)
    {
        // build a list of available choices and choices already made.
        processApplication(pc, choice, choice.getChooseInfo());
    }

    private <T> void processApplication(PlayerCharacter pc, CNAbility choice, ChooseInformation<T> chooseInfo)
    {
        List<T> available = new ArrayList<>(chooseInfo.getSet(pc));
        List<? extends T> selected = chooseInfo.getChoiceActor().getCurrentlySelected(choice, pc);

        final int currentSelections = selected.size();
        final List<T> origSelections = new ArrayList<>(selected);

        //
        // If nothing to choose, or nothing selected, then leave
        //
        if (available.isEmpty() || (currentSelections == 0))
        {
            return;
        }

        Globals.sortChooserLists(available, selected);

        CDOMChooserFacadeImpl<T> chooserFacade =
                new CDOMChooserFacadeImpl<>("Modify selections for " + choice, available, selected, 0);
        chooserFacade.setDefaultView(ChooserTreeViewType.NAME);
        ChooserFactory.getDelegate().showGeneralChooser(chooserFacade);
        final int selectedSize = chooserFacade.getFinalSelected().size();

        if (selectedSize != currentSelections)
        {
            // need to have the same number of selections when finished
            return;
        }

        List<T> add = new ArrayList<>(chooserFacade.getFinalSelected());
        add.removeAll(origSelections);
        List<T> remove = new ArrayList<>(origSelections);
        remove.removeAll(chooserFacade.getFinalSelected());

        for (T selection : remove)
        {
            chooseInfo.removeChoice(pc, choice, selection);
        }
        for (T selection : add)
        {
            chooseInfo.removeChoice(pc, choice, selection);
        }
    }

    @Override
    public boolean allow(CNAbility choice, PlayerCharacter pc, boolean allowStack)
    {
        return true;
    }
}
