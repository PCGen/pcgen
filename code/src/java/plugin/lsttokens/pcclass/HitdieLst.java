/*
 * Created on Sep 2, 2005
 *
 */
package plugin.lsttokens.pcclass;

import pcgen.core.PCClass;
import pcgen.persistence.lst.PCClassLstToken;

/**
 * @author djones4
 *
 */
public class HitdieLst implements PCClassLstToken
{

	public String getTokenName()
	{
		return "HITDIE";
	}

	public boolean parse(PCClass pcclass, String value, int level)
	{
		pcclass.putHitDieLock(value, level);
		return true;
	}
}
