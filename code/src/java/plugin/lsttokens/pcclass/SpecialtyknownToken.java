package plugin.lsttokens.pcclass;

import java.util.StringTokenizer;

import pcgen.core.PCClass;
import pcgen.persistence.lst.PCClassLstToken;

/**
 * Class deals with SPECIALTYKNOWN Token
 */
public class SpecialtyknownToken implements PCClassLstToken {

	public String getTokenName() {
		return "SPECIALTYKNOWN";
	}

	public boolean parse(PCClass pcclass, String value, int level) {
		StringTokenizer st = new StringTokenizer(value, ",");
		int[] array = new int[st.countTokens()];
		
		int index = 0;
		while (st.hasMoreTokens()) {
			array[index++] = Integer.parseInt(st.nextToken());
		}
		
		pcclass.addSpecialtyKnown(level, array);
		return true;
	}
}
