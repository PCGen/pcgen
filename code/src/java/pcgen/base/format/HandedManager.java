/*
 * Copyright 2018 (C) Thomas Parker <thpr@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.base.format;

import java.util.Arrays;
import java.util.Optional;

import pcgen.base.util.BasicIndirect;
import pcgen.base.util.FormatManager;
import pcgen.base.util.Indirect;
import pcgen.cdom.enumeration.Handed;

/**
 * A FormatManager for Handed to help with compatibility.
 */
public class HandedManager implements FormatManager<Handed>
{

    /**
     * Retrieve a Handed object to match the name ({@link #name()}) or localized name
     * (output by {@link #toString()}). The localized lookup is kept for legacy purpose
     * when the localized name was saved in the character files (instead of the
     * {@link #name()}).
     * <p>
     * Note: This will dump stack if there is not a matching Handed value, as the
     * Handed.valueOf(x) call for the existing Enumeration will fail. This is consistent
     * with the existing design (as it can't really go wrong) and isn't needed long term
     * because the load system ensures data is internally consistent.
     *
     * @param inputStr The name (localized or not) of the Handed.
     * @return The matching Handed.
     */
    @Override
    public Handed convert(String inputStr)
    {
        return Arrays.stream(Handed.values())
                .filter(hand -> hand.toString().equals(inputStr))
                .findFirst()
                .orElse(Handed.valueOf(inputStr));
    }

    @Override
    public Indirect<Handed> convertIndirect(String inputStr)
    {
        return new BasicIndirect<>(this, convert(inputStr));
    }

    @Override
    public boolean isDirect()
    {
        return true;
    }

    @Override
    public String unconvert(Handed Handed)
    {
        return Handed.name();
    }

    @Override
    public Class<Handed> getManagedClass()
    {
        return Handed.class;
    }

    @Override
    public String getIdentifierType()
    {
        return "Handed";
    }

    @Override
    public Optional<FormatManager<?>> getComponentManager()
    {
        return Optional.empty();
    }

}
