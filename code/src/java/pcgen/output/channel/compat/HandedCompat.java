package pcgen.output.channel.compat;

import java.util.Arrays;

import pcgen.base.format.HandedManager;
import pcgen.base.util.FormatManager;
import pcgen.base.util.Indirect;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.Handed;
import pcgen.cdom.formula.ListChannelAdapter;
import pcgen.cdom.formula.VariableChannel;
import pcgen.cdom.util.CControl;
import pcgen.cdom.util.ControlUtilities;
import pcgen.core.Globals;
import pcgen.facade.util.DefaultListFacade;
import pcgen.facade.util.WriteableListFacade;
import pcgen.output.channel.ChannelUtilities;
import pcgen.rules.context.LoadContext;

/**
 * HandedCompat contains utility methods for communication of the PCs Handed value through
 * a channel.
 */
public final class HandedCompat
{

	static final FormatManager<Handed> HANDED_MANAGER = new HandedManager();

	private HandedCompat()
	{
		//Do not construct utility class
	}

	/**
	 * Returns the Default value for Handed.
	 * 
	 * @return The Default value for Handed
	 */
	public static Handed getDefaultHanded()
	{
		return Handed.Right;
	}

	/**
	 * Returns a ListFacade of the available Handeds for a PC.
	 * 
	 * @return A ListFacade of the available Handeds for a PC.
	 */
	public static WriteableListFacade<Handed> getAvailableHandedness(CharID id)
	{
		LoadContext context = Globals.getContext();
		WriteableListFacade<Handed> availHandeds;
		String channelName = ControlUtilities.getControlToken(context,
			CControl.AVAILHANDEDNESS);
		if (channelName == null)
		{
			availHandeds = new DefaultListFacade<>();
			for (Handed availableHanded : Handed.values())
			{
				availHandeds.addElement(availableHanded);
			}
		}
		else
		{
			VariableChannel<Handed[]> varChannel =
					(VariableChannel<Handed[]>) context.getVariableContext()
						.getGlobalChannel(id, channelName);
			availHandeds = new ListChannelAdapter(varChannel);
		}
		return availHandeds;
	}

	/**
	 * Returns an array of the dataset Handed objects.
	 * 
	 * @return An array of the dataset Handed objects
	 */
	public static Handed[] getGameModeHandeds()
	{
		return Handed.values();
	}

	/**
	 * Returns an Indirect containing the Handed of the specified name.
	 * 
	 * @param context
	 *            The LoadContext in which the Handed should be resolved
	 * @param name
	 *            The name of the Handed
	 * @return An Indirect containing the Handed of the specified name
	 */
	public static Indirect<Handed> getHandedReference(LoadContext context,
		String name)
	{
		@SuppressWarnings("unchecked")
		FormatManager<Handed> formatManager =
				(FormatManager<Handed>) context.getReferenceContext().getFormatManager("HANDED");
		return formatManager.convertIndirect(name);
	}

	/**
	 * Gets the current Handed for the PC represented by the given CharID.
	 * 
	 * @param id
	 *            The CharID representing the PC for which the Handed should be returned
	 * @return The current Handed for the PC represented by the given CharID
	 */
	public static Handed getCurrentHandedness(CharID id)
	{
		String channelName = ControlUtilities
			.getControlToken(Globals.getContext(), CControl.HANDEDINPUT);
		return (Handed) ChannelUtilities.readGlobalChannel(id, channelName);
	}

	/**
	 * Sets the current Handed for the PC represented by the given CharID.
	 * 
	 * @param id
	 *            The CharID representing the PC for which the Handed should be set
	 * @param deity
	 *            The Handed which should be set
	 */
	public static void setCurrentHandedness(CharID id, Handed Handed)
	{
		String channelName = ControlUtilities
			.getControlToken(Globals.getContext(), CControl.HANDEDINPUT);
		ChannelUtilities.setGlobalChannel(id, channelName, Handed);
	}

	/**
	 * Retrieve a Handed object to match the name ({@link #name()}) or localized name
	 * (output by {@link #toString()}). The localized lookup is kept for legacy purpose
	 * when the localized name was saved in the character files (instead of the
	 * {@link #name()}).
	 * 
	 * Note: This will dump stack if there is not a matching Handed value, as the
	 * Handed.valueOf(x) call for the existing Enumeration will fail. This is consistent
	 * with the existing design (as it can't really go wrong) and isn't needed long term
	 * because the load system ensures data is internally consistent.
	 * 
	 * @param name
	 *            The localized display name of the Handed.
	 * @return The matching Handed.
	 */
	public static Handed getHandedByName(String name)
	{
		return Arrays.stream(getGameModeHandeds())
			.filter(hand -> hand.toString().equals(name))
			.findFirst()
			.orElse(Handed.valueOf(name));
	}
}
