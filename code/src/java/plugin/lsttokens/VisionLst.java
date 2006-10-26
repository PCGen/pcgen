/*
 * Created on Sep 2, 2005
 *
 */
package plugin.lsttokens;

import java.util.StringTokenizer;

import pcgen.core.PCClass;
import pcgen.core.PObject;
import pcgen.core.Vision;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.util.Logging;
import pcgen.util.enumeration.VisionType;

/**
 * <code>VisionLst</code> handles the processing of the VISION tag in LST
 * code.
 * 
 * Last Editor: $Author$ Last Edited: $Date: 2006-03-14 17:16:52 -0500
 * (Tue, 14 Mar 2006) $
 * 
 * @author Devon Jones
 * @version $Revision$
 */
public class VisionLst implements GlobalLstToken {

	/**
	 * @see pcgen.persistence.lst.LstToken#getTokenName()
	 */
	public String getTokenName() {
		return "VISION";
	}

	/**
	 * @see pcgen.persistence.lst.GlobalLstToken#parse(pcgen.core.PObject,
	 *      java.lang.String, int)
	 */
	public boolean parse(PObject obj, String value, int anInt) {
		final StringTokenizer aTok = new StringTokenizer(value, "|");

		while (aTok.hasMoreTokens()) {
			String visionString = aTok.nextToken();

			if (".CLEAR".equals(visionString)) {
				obj.clearVisionList();
				continue;
			}

			if (visionString.indexOf(',') >= 0) {
				Logging
						.errorPrint("Use of comma in VISION Tag is deprecated.  Use .CLEAR.[Vision] instead.");
				final StringTokenizer visionTok = new StringTokenizer(
						visionString, ",");
				String numberTok = visionTok.nextToken();
				if (numberTok == "2") {
					visionString = ".CLEAR." + visionTok.nextToken();
				} else if (numberTok == "0") {
					visionString = ".SET." + visionTok.nextToken();
				} else {
					visionString = visionTok.nextToken();
				}
			}

			Vision vis = null;
			if (visionString.startsWith(".CLEAR.")) {
				obj.removeVisionType(VisionType.getVisionType(visionString
						.substring(7)));
			} else if (visionString.startsWith(".SET.")) {
				obj.clearVisionList();
				vis = getVision(anInt, visionString.substring(5));
			} else {
				vis = getVision(anInt, visionString);
			}
			
			if (vis != null) {
				if (anInt > -9) {
					((PCClass) obj).addVision(anInt, vis);
				} else {
					obj.addVision(vis);
				}
			}
			
		}
		return true;
	}

	private Vision getVision(int anInt, String visionType) {
		// expecting value in form of Darkvision (60')
		final StringTokenizer cTok = new StringTokenizer(visionType, "(')");
		final String aKey = cTok.nextToken().trim(); // e.g. Darkvision
		String aVal = "0";
		if (cTok.hasMoreTokens()) {
			aVal = cTok.nextToken(); // e.g. 60
		}
		return new Vision(VisionType.getVisionType(aKey), aVal);
	}
}
