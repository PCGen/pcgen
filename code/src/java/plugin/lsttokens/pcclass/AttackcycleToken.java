package plugin.lsttokens.pcclass;

import java.util.StringTokenizer;

import pcgen.core.Constants;
import pcgen.core.PCClass;
import pcgen.persistence.lst.PCClassLstToken;
import pcgen.util.enumeration.AttackType;

/**
 * Class deals with ATTACKCYCLE Token
 */
public class AttackcycleToken implements PCClassLstToken {

	public String getTokenName() {
		return "ATTACKCYCLE";
	}

	public boolean parse(PCClass pcclass, String value, int level) {
		if (value.indexOf('|') == -1)
			return true;

		final StringTokenizer aTok = new StringTokenizer(value, Constants.PIPE);

		while (aTok.hasMoreTokens()) {
			AttackType at = AttackType.getInstance(aTok.nextToken());
			String cycle = aTok.nextToken();
			pcclass.setAttackCycle(at, cycle);
			/*
			 * This is a bit of a hack - it is designed to account for the fact
			 * that the BAB tag in ATTACKCYCLE actually impacts both
			 * ATTACK.MELEE and ATTACK.GRAPPLE ... therefore, one method of
			 * handing this (which is done here) is to actually allow the
			 * pcgen.core code to keep the 4 attack type view (MELEE, RANGED,
			 * UNARMED, GRAPPLE) by simply loading the attackCycle for MELEE
			 * into GRAPPLE. This is done in the hope that this is a more
			 * flexible solution for potential future requirements for other
			 * attack types (rather than treating GRAPPLE as a special case
			 * throughout the core code) - thpr 11/1/06
			 */
			if (at.equals(AttackType.MELEE)) {
				pcclass.setAttackCycle(AttackType.GRAPPLE, cycle);
			}
		}
		return true;
	}
}
