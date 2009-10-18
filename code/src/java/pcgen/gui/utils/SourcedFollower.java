package pcgen.gui.utils;

import pcgen.cdom.base.CDOMObject;
import pcgen.core.FollowerOption;

public class SourcedFollower
{

	public final CDOMObject owner;
	public final FollowerOption option;
	
	public SourcedFollower(FollowerOption fo, CDOMObject cdo)
	{
		option = fo;
		owner = cdo;
	}
}
