package pcgen.output.channel.compat;

import pcgen.base.format.HandedManager;
import pcgen.base.util.FormatManager;
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

    /**
     * A HandedManager for common use.
     */
    public static final FormatManager<Handed> HANDED_MANAGER = new HandedManager();

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
        } else
        {
            VariableChannel<Handed[]> varChannel =
                    (VariableChannel<Handed[]>) context.getVariableContext()
                            .getGlobalChannel(id, channelName);
            availHandeds = new ListChannelAdapter(varChannel);
        }
        return availHandeds;
    }

    /**
     * Gets the current Handed for the PC represented by the given CharID.
     *
     * @param id The CharID representing the PC for which the Handed should be returned
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
     * @param id     The CharID representing the PC for which the Handed should be set
     * @param handed The Handed which should be set
     */
    public static void setCurrentHandedness(CharID id, Handed handed)
    {
        String channelName = ControlUtilities
                .getControlToken(Globals.getContext(), CControl.HANDEDINPUT);
        ChannelUtilities.setGlobalChannel(id, channelName, handed);
    }
}
