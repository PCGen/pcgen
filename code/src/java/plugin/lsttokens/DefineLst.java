/*
 * Created on Sep 2, 2005
 *
 */
package plugin.lsttokens;

import pcgen.core.PObject;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.GlobalLstToken;

/**
 * @author djones4
 *
 */
public class DefineLst implements GlobalLstToken {

	public String getTokenName() {
		return "DEFINE";
	}

	public boolean parse(PObject obj, String value, int anInt) throws PersistenceLayerException {
		String[] tokens = value.split("\\|");
		if (tokens.length != 2) {
			throw new PersistenceLayerException("Unable to parse the Define 'DEFINE:"
					+ value
					+ "'. All defines are of the form DEFINE:variable|defaultValue.");
		}
		obj.addVariable(anInt, tokens[0], tokens[1]);
		return true;
	}
}

