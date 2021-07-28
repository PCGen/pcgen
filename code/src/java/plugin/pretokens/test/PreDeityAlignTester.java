/*
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package plugin.pretokens.test;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.cdom.util.CControl;
import pcgen.core.Deity;
import pcgen.core.Globals;
import pcgen.core.PCAlignment;
import pcgen.core.PlayerCharacter;
import pcgen.core.prereq.AbstractPrerequisiteTest;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteTest;
import pcgen.output.channel.ChannelUtilities;
import pcgen.system.LanguageBundle;
import pcgen.util.Logging;

/**
 * Prerequisite test that the character has a deity with the correct alignment.
 */
public class PreDeityAlignTester extends AbstractPrerequisiteTest implements PrerequisiteTest
{

	@Override
	public int passes(final Prerequisite prereq, final PlayerCharacter pc, CDOMObject source)
	{

		//
		// If game mode doesn't support alignment, then pass the prereq
		//
		int runningTotal = 0;

		if (!pc.isFeatureEnabled(CControl.ALIGNMENTFEATURE))
		{
			runningTotal = 1;
		}
		else
		{
			CDOMSingleRef<PCAlignment> deityAlign = null;
			Deity deity = (Deity) ChannelUtilities
				.readControlledChannel(pc.getCharID(), CControl.DEITYINPUT);
			if (deity != null)
			{
				deityAlign = deity.get(ObjectKey.ALIGNMENT);
			}
			if (deityAlign != null)
			{
				String desiredAlignIdentifier = prereq.getOperand();
				PCAlignment desiredAlign = getPCAlignment(desiredAlignIdentifier);

				if (desiredAlign.equals(deityAlign.get()))
				{
					runningTotal = 1;
				}
			}
		}

		return countedTotal(prereq, runningTotal);
	}

	private static PCAlignment getPCAlignment(String desiredAlignIdentifier)
	{
		PCAlignment desiredAlign = Globals.getContext().getReferenceContext()
			.silentlyGetConstructedCDOMObject(PCAlignment.class, desiredAlignIdentifier);
		if (desiredAlign == null)
		{
			Logging.errorPrint("Unable to find alignment that matches: " + desiredAlignIdentifier);
		}
		return desiredAlign;
	}

	/**
	 * Get the type of prerequisite handled by this token.
	 * @return the type of prerequisite handled by this token.
	 */
	@Override
	public String kindHandled()
	{
		return "DEITYALIGN"; //$NON-NLS-1$
	}

	@Override
	public String toHtmlString(final Prerequisite prereq)
	{
		return LanguageBundle.getFormattedString(
			"PreDeityAlign.toHtml", prereq.getOperator().toDisplayString(), //$NON-NLS-1$
			getPCAlignment(prereq.getOperand()).getKeyName());
	}

}
