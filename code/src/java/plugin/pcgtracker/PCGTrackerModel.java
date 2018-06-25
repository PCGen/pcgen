/*
 * Copyright 2003 (C) Devon Jones
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
 package plugin.pcgtracker;

import gmgen.plugin.PlayerCharacterOutput;
import pcgen.core.PlayerCharacter;

import javax.swing.DefaultListModel;

public class PCGTrackerModel extends DefaultListModel
{
	public void add(PlayerCharacter pc)
	{
		if (pc != null)
		{
			addElement(new LoadedPC(pc));
		}
	}

    @Override
	public Object get(int i)
	{
		LoadedPC lpc = (LoadedPC) elementAt(i);

		return lpc.getPC();
	}

	public PlayerCharacter get(Object o)
	{
		if (contains(o))
		{
			LoadedPC lpc = (LoadedPC) o;

			return lpc.getPC();
		}

		return null;
	}

	public void remove(PlayerCharacter pc)
	{
		for (int i = 0; i < size(); i++)
		{
			LoadedPC lpc = (LoadedPC) elementAt(i);

			if (lpc.getPC() == pc)
			{
				removeElement(lpc);
			}
		}
	}

	private static class LoadedPC
	{
		private PlayerCharacter pc;

		public LoadedPC(PlayerCharacter pc)
		{
			this.pc = pc;
		}

		public PlayerCharacter getPC()
		{
			return pc;
		}

		@Override
		public String toString()
		{
			StringBuilder outbuf = new StringBuilder();
			PlayerCharacterOutput pcOut = new PlayerCharacterOutput(pc);

			if (pc.isDirty())
			{
				outbuf.append("* ");
			}
			else
			{
				outbuf.append("  ");
			}

			outbuf.append(pcOut.getName()).append(" (");
			outbuf.append(pcOut.getRaceName()).append(' ');
			outbuf.append(pcOut.getClasses()).append(' ');
			outbuf.append(pcOut.getGender()).append(')');

			return outbuf.toString();
		}
	}
}
