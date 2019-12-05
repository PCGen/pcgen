/*
 * Copyright 2005 (C) Aaron Divinsky <boomer70@yahoo.com>
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
package pcgen.core.kit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import pcgen.base.formula.Formula;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.Deity;
import pcgen.core.Domain;
import pcgen.core.Globals;
import pcgen.core.Kit;
import pcgen.core.PlayerCharacter;
import pcgen.core.analysis.DomainApplication;

/**
 * Deal with Dieties via Kits
 */
public class KitDeity extends BaseKit
{
    private CDOMSingleRef<Deity> theDeityRef;
    private Formula choiceCount;

    private List<CDOMSingleRef<Domain>> theDomains = null;

    // These members store the state of an instance of this class.  They are
    // not cloned.
    private Deity theDeity = null;
    private List<Domain> domainsToAdd = null;

    /**
     * Add the domain
     *
     * @param ref
     */
    public void addDomain(final CDOMSingleRef<Domain> ref)
    {
        if (theDomains == null)
        {
            theDomains = new ArrayList<>(3);
        }
        theDomains.add(ref);
    }

    /**
     * Get domains
     *
     * @return list of domains
     */
    public List<CDOMSingleRef<Domain>> getDomains()
    {
        if (theDomains == null)
        {
            return null;
        }

        return Collections.unmodifiableList(theDomains);
    }

    @Override
    public String toString()
    {
        StringBuilder buf = new StringBuilder();

        buf.append(theDeityRef.getLSTformat(false));

        if (theDomains != null && !theDomains.isEmpty())
        {
            buf.append(" (");
            if (choiceCount != null)
            {
                buf.append(choiceCount);
                buf.append(" of ");
            }
            for (Iterator<CDOMSingleRef<Domain>> i = theDomains.iterator();i.hasNext();)
            {
                buf.append(i.next());
                if (i.hasNext())
                {
                    buf.append(", ");
                }
            }
            buf.append(")");
        }

        return buf.toString();
    }

    @Override
    public boolean testApply(Kit aKit, PlayerCharacter aPC, List<String> warnings)
    {
        domainsToAdd = null;

        theDeity = theDeityRef.get();

        if (!aPC.canSelectDeity(theDeity))
        {
            warnings.add("DEITY: Cannot select deity \"" + theDeity.getDisplayName() + "\"");
            return false;
        }
        aPC.setDeity(theDeity);

        if (theDomains == null || theDomains.isEmpty())
        {
            // nothing else to do.
            return true;
        }

        if (aPC.getMaxCharacterDomains() <= 0)
        {
            warnings.add("DEITY: Not allowed to choose a domain");

            return true;
        }
        int numberOfChoices;
        if (choiceCount == null)
        {
            numberOfChoices = theDomains.size();
        } else
        {
            numberOfChoices = choiceCount.resolve(aPC, "").intValue();
        }

        //
        // Can't choose more entries than there are...
        //
        if (numberOfChoices > theDomains.size())
        {
            numberOfChoices = theDomains.size();
        }

        if (numberOfChoices == 0)
        {
            // No choices allowed, we are done.
            return true;
        }

        List<CDOMSingleRef<Domain>> xs;
        if (numberOfChoices == theDomains.size())
        {
            xs = theDomains;
        } else
        {
            //
            // Force user to make enough selections
            //
            while (true)
            {
                xs = Globals.getChoiceFromList("Choose Domains", theDomains, new ArrayList<>(), numberOfChoices, aPC);

                if (!xs.isEmpty())
                {
                    break;
                }
            }
        }
        //
        // Add to list of things to add to the character
        //
        for (CDOMSingleRef<Domain> ref : xs)
        {
            Domain domain = ref.get();
            if (!domain.qualifies(aPC, domain))
            {
                warnings.add("DEITY: Not qualified for domain \"" + domain.getDisplayName() + "\"");
                continue;
            }
            if (aPC.getDomainCount() >= aPC.getMaxCharacterDomains())
            {
                warnings.add("DEITY: No more allowed domains");

                return false;
            }
            if (!aPC.hasDefaultDomainSource())
            {
                warnings.add("DEITY: Cannot add domain \"" + domain.getDisplayName()
                        + "\" as the character does not have a domain " + "source yet.");
                return false;
            }
            if (domainsToAdd == null)
            {
                domainsToAdd = new ArrayList<>();
            }
            domainsToAdd.add(domain);

            aPC.addDomain(domain);
            DomainApplication.applyDomain(aPC, domain);
        }
        aPC.calcActiveBonuses();
        return true;
    }

    @Override
    public void apply(PlayerCharacter aPC)
    {
        if (theDeity == null)
        {
            return;
        }
        aPC.setDeity(theDeity);

        if (domainsToAdd != null)
        {
            for (Domain domain : domainsToAdd)
            {
                aPC.addDomain(domain);
                DomainApplication.applyDomain(aPC, domain);
            }
        }
        aPC.calcActiveBonuses();

        theDeity = null;
        domainsToAdd = null;
    }

    @Override
    public String getObjectName()
    {
        return "Deity";
    }

    public void setCount(Formula formula)
    {
        choiceCount = formula;
    }

    public Formula getCount()
    {
        return choiceCount;
    }

    public void setDeity(CDOMSingleRef<Deity> ref)
    {
        theDeityRef = ref;
    }

    public CDOMSingleRef<Deity> getDeityRef()
    {
        return theDeityRef;
    }
}
