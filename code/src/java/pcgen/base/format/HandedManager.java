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

import java.util.Optional;

import pcgen.base.util.BasicIndirect;
import pcgen.base.util.FormatManager;
import pcgen.base.util.Indirect;
import pcgen.cdom.enumeration.Handed;
import pcgen.output.channel.compat.HandedCompat;

/**
 * A FormatManager for Handed to help with compatibility.
 */
public class HandedManager implements FormatManager<Handed>
{

	@Override
	public Handed convert(String inputStr)
	{
		return HandedCompat.getHandedByName(inputStr);
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