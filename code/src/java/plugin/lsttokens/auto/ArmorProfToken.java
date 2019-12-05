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
package plugin.lsttokens.auto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.ChooseDriver;
import pcgen.cdom.base.ChooseSelectionActor;
import pcgen.cdom.base.Constants;
import pcgen.cdom.content.ConditionalSelectionActor;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.helper.ArmorProfProvider;
import pcgen.core.ArmorProf;
import pcgen.core.Equipment;
import pcgen.core.PlayerCharacter;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.TokenUtilities;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.Logging;

public class ArmorProfToken extends AbstractNonEmptyToken<CDOMObject>
        implements CDOMSecondaryToken<CDOMObject>, ChooseSelectionActor<ArmorProf>
{

    private static final Class<ArmorProf> ARMORPROF_CLASS = ArmorProf.class;

    private static final Class<Equipment> EQUIPMENT_CLASS = Equipment.class;

    @Override
    public String getParentToken()
    {
        return "AUTO";
    }

    @Override
    public String getTokenName()
    {
        return "ARMORPROF";
    }

    private String getFullName()
    {
        return getParentToken() + Constants.COLON + getTokenName();
    }

    @Override
    protected ParseResult parseNonEmptyToken(LoadContext context, CDOMObject obj, String value)
    {
        String armorProf;
        Prerequisite prereq = null; // Do not initialize, null is significant!
        boolean isPre = false;
        if (value.indexOf('[') == -1)
        {
            // Supported version of PRExxx using |.  Needs to be at the front of the
            // Parsing code because many objects expect the pre to have been determined
            // Ahead of time.  Until deprecated code is removed, it will have to stay
            // like this.
            armorProf = value;
            StringTokenizer tok = new StringTokenizer(armorProf, Constants.PIPE);
            while (tok.hasMoreTokens())
            {
                String token = tok.nextToken();
                if (PreParserFactory.isPreReqString(token))
                {
                    if (isPre)
                    {
                        String errorText =
                                "Invalid " + getTokenName() + ": " + value + "  PRExxx must be at the END of the Token";
                        Logging.errorPrint(errorText);
                        return new ParseResult.Fail(errorText);
                    }
                    prereq = getPrerequisite(token);
                    if (prereq == null)
                    {
                        return new ParseResult.Fail("Error generating Prerequisite " + prereq + " in " + getFullName());
                    }
                    int preStart = value.indexOf(token) - 1;
                    armorProf = value.substring(0, preStart);
                    isPre = true;
                }
            }
        } else
        {
            return new ParseResult.Fail(
                    "Use of [] for Prerequisites has been removed. " + "Please use | based standard");
        }

        ParseResult pr = checkSeparatorsAndNonEmpty('|', armorProf);
        if (!pr.passed())
        {
            return pr;
        }

        boolean foundAny = false;
        boolean foundOther = false;

        StringTokenizer tok = new StringTokenizer(armorProf, Constants.PIPE);

        List<CDOMReference<ArmorProf>> armorProfs = new ArrayList<>();
        List<CDOMReference<Equipment>> equipTypes = new ArrayList<>();

        while (tok.hasMoreTokens())
        {
            String aProf = tok.nextToken();

            if (Constants.LST_PERCENT_LIST.equals(aProf))
            {
                foundOther = true;
                ChooseSelectionActor<ArmorProf> cra;
                if (prereq == null)
                {
                    cra = this;
                } else
                {
                    ConditionalSelectionActor<ArmorProf> cca = new ConditionalSelectionActor<>(this);
                    cca.addPrerequisite(prereq);
                    cra = cca;
                }
                context.getObjectContext().addToList(obj, ListKey.NEW_CHOOSE_ACTOR, cra);
            } else if (Constants.LST_ALL.equalsIgnoreCase(aProf))
            {
                foundAny = true;
                armorProfs.add(context.getReferenceContext().getCDOMAllReference(ARMORPROF_CLASS));
            } else if (aProf.startsWith("ARMORTYPE.") || aProf.startsWith("ARMORTYPE="))
            {
                foundOther = true;
                CDOMReference<Equipment> ref =
                        TokenUtilities.getTypeReference(context, EQUIPMENT_CLASS, "ARMOR." + aProf.substring(10));
                if (ref == null)
                {
                    return ParseResult.INTERNAL_ERROR;
                }
                equipTypes.add(ref);
            } else
            {
                foundOther = true;
                armorProfs.add(context.getReferenceContext().getCDOMReference(ARMORPROF_CLASS, aProf));
            }
        }

        if (foundAny && foundOther)
        {
            return new ParseResult.Fail(
                    "Non-sensical " + getFullName() + ": Contains ANY and a specific reference: " + value);
        }

        if (!armorProfs.isEmpty() || !equipTypes.isEmpty())
        {
            ArmorProfProvider pp = new ArmorProfProvider(armorProfs, equipTypes);
            if (prereq != null)
            {
                pp.addPrerequisite(prereq);
            }
            context.getObjectContext().addToList(obj, ListKey.AUTO_ARMORPROF, pp);
        }

        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, CDOMObject obj)
    {
        Changes<ArmorProfProvider> changes = context.getObjectContext().getListChanges(obj, ListKey.AUTO_ARMORPROF);
        Changes<ChooseSelectionActor<?>> listChanges =
                context.getObjectContext().getListChanges(obj, ListKey.NEW_CHOOSE_ACTOR);
        Collection<ArmorProfProvider> added = changes.getAdded();
        Set<String> set = new TreeSet<>();
        Collection<ChooseSelectionActor<?>> listAdded = listChanges.getAdded();
        boolean foundAny = false;
        boolean foundOther = false;
        if (listAdded != null && !listAdded.isEmpty())
        {
            for (ChooseSelectionActor<?> cra : listAdded)
            {
                if (cra.getSource().equals(getTokenName()))
                {
                    try
                    {
                        set.add(cra.getLstFormat());
                        foundOther = true;
                    } catch (PersistenceLayerException e)
                    {
                        context.addWriteMessage("Error writing Prerequisite: " + e);
                        return null;
                    }
                }
            }
        }
        if (added != null)
        {
            for (ArmorProfProvider spp : added)
            {
                StringBuilder sb = new StringBuilder();
                sb.append(spp.getLstFormat());
                if (spp.hasPrerequisites())
                {
                    sb.append('|');
                    sb.append(getPrerequisiteString(context, spp.getPrerequisiteList()));
                }
                String ab = sb.toString();
                boolean isUnconditionalAll = Constants.LST_ALL.equals(ab);
                foundAny |= isUnconditionalAll;
                foundOther |= !isUnconditionalAll;
                set.add(ab);
            }
        }
        if (foundAny && foundOther)
        {
            context
                    .addWriteMessage("Non-sensical " + getFullName() + ": Contains ANY and a specific reference: " + set);
            return null;
        }
        if (set.isEmpty())
        {
            //okay
            return null;
        }
        return set.toArray(new String[0]);
    }

    @Override
    public Class<CDOMObject> getTokenClass()
    {
        return CDOMObject.class;
    }

    @Override
    public void applyChoice(ChooseDriver obj, ArmorProf ap, PlayerCharacter pc)
    {
        pc.addArmorProf(obj, ap);
    }

    @Override
    public void removeChoice(ChooseDriver obj, ArmorProf ap, PlayerCharacter pc)
    {
        pc.removeArmorProf(obj, ap);
    }

    @Override
    public Class<ArmorProf> getChoiceClass()
    {
        return ARMORPROF_CLASS;
    }

    @Override
    public String getSource()
    {
        return getTokenName();
    }

    @Override
    public String getLstFormat()
    {
        return Constants.LST_PERCENT_LIST;
    }
}
