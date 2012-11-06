package plugin.pcgtracker;

import gmgen.plugin.PlayerCharacterOutput;
import pcgen.core.PlayerCharacter;

import javax.swing.DefaultListModel;

public class PCGTrackerModel extends DefaultListModel
{
	/**
	 * Creates an instance of a <code>PCGTrackerModel</code>.  This class holds
	 * all the characters that are loaded.
	 */
	public PCGTrackerModel()
	{
		super();
	}

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
			StringBuffer outbuf = new StringBuffer();
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
			outbuf.append(pcOut.getRaceName()).append(" ");
			outbuf.append(pcOut.getClasses()).append(" ");
			outbuf.append(pcOut.getGender()).append(")");

			return outbuf.toString();
		}
	}
}
