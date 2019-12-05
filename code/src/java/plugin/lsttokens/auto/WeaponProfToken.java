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
import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.ChooseDriver;
import pcgen.cdom.base.ChooseSelectionActor;
import pcgen.cdom.base.Constants;
import pcgen.cdom.content.ConditionalSelectionActor;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.helper.WeaponProfProvider;
import pcgen.cdom.reference.CDOMGroupRef;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.PlayerCharacter;
import pcgen.core.QualifiedObject;
import pcgen.core.WeaponProf;
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

public class WeaponProfToken extends AbstractNonEmptyToken<CDOMObject>
        implements CDOMSecondaryToken<CDOMObject>, ChooseSelectionActor<WeaponProf>
{

    private static final Class<WeaponProf> WEAPONPROF_CLASS = WeaponProf.class;

    @Override
    public String getParentToken()
    {
        return "AUTO";
    }

    @Override
    public String getTokenName()
    {
        return "WEAPONPROF";
    }

    private String getFullName()
    {
        return getParentToken() + Constants.COLON + getTokenName();
    }

    @Override
    protected ParseResult parseNonEmptyToken(LoadContext context, CDOMObject obj, String value)
    {
        String weaponProfs;
        Prerequisite prereq = null; // Do not initialize, null is significant!
        boolean isPre = false;
        if (value.indexOf('[') == -1)
        {
            // Supported version of PRExxx using |.  Needs to be at the front of the
            // Parsing code because many objects expect the pre to have been determined
            // Ahead of time.  Until deprecated code is removed, it will have to stay
            // like this.
            weaponProfs = value;
            StringTokenizer tok = new StringTokenizer(weaponProfs, Constants.PIPE);
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
                    weaponProfs = value.substring(0, preStart);
                    isPre = true;

                    ParseResult fail = checkForLoopPrereqs(prereq, context);
                    if (fail != null)
                    {
                        return fail;
                    }
                }
            }
        } else
        {
            return new ParseResult.Fail(
                    "Use of [] for Prerequisites has been removed. " + "Please use | based standard");
        }

        ParseResult pr = checkForIllegalSeparator('|', weaponProfs);
        if (!pr.passed())
        {
            return pr;
        }

        boolean foundAny = false;
        boolean foundOther = false;

        StringTokenizer tok = new StringTokenizer(weaponProfs, Constants.PIPE);
        WeaponProfProvider wpp = new WeaponProfProvider();

        while (tok.hasMoreTokens())
        {
            String token = tok.nextToken();
            if (Constants.LST_PERCENT_LIST.equals(token))
            {
                foundOther = true;
                ChooseSelectionActor<WeaponProf> cra;
                if (prereq == null)
                {
                    cra = this;
                } else
                {
                    ConditionalSelectionActor<WeaponProf> cca = new ConditionalSelectionActor<>(this);
                    cca.addPrerequisite(prereq);
                    cra = cca;
                }
                context.getObjectContext().addToList(obj, ListKey.NEW_CHOOSE_ACTOR, cra);
            } else if ("DEITYWEAPONS".equals(token))
            {
                foundOther = true;
                context.getObjectContext().put(obj, ObjectKey.HAS_DEITY_WEAPONPROF,
                        new QualifiedObject<>(Boolean.TRUE, prereq));
            } else
            {
                if (Constants.LST_ALL.equalsIgnoreCase(token))
                {
                    foundAny = true;
                    CDOMGroupRef<WeaponProf> allRef =
                            context.getReferenceContext().getCDOMAllReference(WEAPONPROF_CLASS);
                    wpp.addWeaponProfAll(allRef);
                } else
                {
                    foundOther = true;
                    if (token.startsWith(Constants.LST_TYPE_DOT) || token.startsWith(Constants.LST_TYPE_EQUAL))
                    {
                        CDOMGroupRef<WeaponProf> rr =
                                TokenUtilities.getTypeReference(context, WEAPONPROF_CLASS, token.substring(5));
                        if (rr == null)
                        {
                            return ParseResult.INTERNAL_ERROR;
                        }
                        wpp.addWeaponProfType(rr);
                    } else
                    {
                        CDOMSingleRef<WeaponProf> ref =
                                context.getReferenceContext().getCDOMReference(WEAPONPROF_CLASS, token);
                        wpp.addWeaponProf(ref);
                    }
                }
            }
        }

        if (foundAny && foundOther)
        {
            return new ParseResult.Fail(
                    "Non-sensical " + getFullName() + ": Contains ANY and a specific reference: " + value);
        }
        if (!wpp.isEmpty())
        {
            if (prereq != null)
            {
                wpp.addPrerequisite(prereq);
            }
            context.getObjectContext().addToList(obj, ListKey.WEAPONPROF, wpp);
        }
        return ParseResult.SUCCESS;
    }

    /**
     * Check for a prereq that will cause a loop later when evaluating the
     * weapon proficiency.
     *
     * @param prereq The prerequisite to be checked.
     * @return A ParseResult.Fail if there is a possible, loop, or null if all is ok.
     */
    private ParseResult checkForLoopPrereqs(Prerequisite prereq, LoadContext context)
    {
        if ("WEAPONPROF".equalsIgnoreCase(prereq.getKind()))
        {
            if (prereq.getKey().startsWith("TYPE"))
            {
                return new ParseResult.Fail("AUTO:WEAPONPROF may not use PREWEAPONPROF requirements "
                        + " other than specific named proficiencies.");
            }
        }

        for (Prerequisite childPrereq : prereq.getPrerequisites())
        {
            ParseResult res = checkForLoopPrereqs(childPrereq, context);
            if (res != null)
            {
                return res;
            }
        }
        return null;
    }

    @Override
    public String[] unparse(LoadContext context, CDOMObject obj)
    {
        List<String> list = new ArrayList<>();
        Changes<ChooseSelectionActor<?>> listChanges =
                context.getObjectContext().getListChanges(obj, ListKey.NEW_CHOOSE_ACTOR);
        Changes<WeaponProfProvider> changes = context.getObjectContext().getListChanges(obj, ListKey.WEAPONPROF);
        QualifiedObject<Boolean> deityweap = context.getObjectContext().getObject(obj, ObjectKey.HAS_DEITY_WEAPONPROF);
        Collection<WeaponProfProvider> added = changes.getAdded();
        Collection<ChooseSelectionActor<?>> listAdded = listChanges.getAdded();
        boolean foundAny = false;
        boolean foundOther = false;
        if (listAdded != null && !listAdded.isEmpty())
        {
            foundOther = true;
            for (ChooseSelectionActor<?> cra : listAdded)
            {
                if (cra.getSource().equals(getTokenName()))
                {
                    try
                    {
                        list.add(cra.getLstFormat());
                    } catch (PersistenceLayerException e)
                    {
                        context.addWriteMessage("Error writing Prerequisite: " + e);
                        return null;
                    }
                }
            }
        }
        if (deityweap != null && deityweap.getRawObject())
        {
            foundOther = true;
            StringBuilder sb = new StringBuilder();
            sb.append("DEITYWEAPONS");
            if (deityweap.hasPrerequisites())
            {
                sb.append('|');
                sb.append(context.getPrerequisiteString(deityweap.getPrerequisiteList()));
            }
            list.add(sb.toString());
        }
        if (added != null)
        {
            for (WeaponProfProvider wpp : added)
            {
                if (!wpp.isValid())
                {
                    context.addWriteMessage(
                            "Non-sensical " + "WeaponProfProvider in " + getFullName() + ": Had invalid contents");
                    return null;
                }
                StringBuilder sb = new StringBuilder(wpp.getLstFormat());
                List<Prerequisite> prereqs = wpp.getPrerequisiteList();
                if (prereqs != null && !prereqs.isEmpty())
                {
                    if (prereqs.size() > 1)
                    {
                        context.addWriteMessage("Error: " + obj.getClass().getSimpleName()
                                + " had more than one Prerequisite for " + getFullName());
                        return null;
                    }
                    sb.append('|');
                    sb.append(context.getPrerequisiteString(prereqs));
                }
                String lstFormat = sb.toString();
                boolean isUnconditionalAll = Constants.LST_ALL.equals(lstFormat);
                foundAny |= isUnconditionalAll;
                foundOther |= !isUnconditionalAll;
                list.add(lstFormat);
            }
        }
        if (foundAny && foundOther)
        {
            context
                    .addWriteMessage("Non-sensical " + getFullName() + ": Contains ANY and a specific reference: " + list);
            return null;
        }
        if (list.isEmpty())
        {
            // Empty indicates no Token
            return null;
        }

        return list.toArray(new String[0]);
    }

    @Override
    public Class<CDOMObject> getTokenClass()
    {
        return CDOMObject.class;
    }

    @Override
    public void applyChoice(ChooseDriver obj, WeaponProf wp, PlayerCharacter pc)
    {
        pc.addWeaponProf(obj, wp);
    }

    @Override
    public void removeChoice(ChooseDriver obj, WeaponProf wp, PlayerCharacter pc)
    {
        pc.removeWeaponProf(obj, wp);
    }

    @Override
    public Class<WeaponProf> getChoiceClass()
    {
        return WEAPONPROF_CLASS;
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
