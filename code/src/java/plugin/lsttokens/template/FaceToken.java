package plugin.lsttokens.template;

import pcgen.core.PCTemplate;
import pcgen.persistence.lst.PCTemplateLstToken;

/**
 * Class deals with FACE Token
 */
public class FaceToken implements PCTemplateLstToken {

	public String getTokenName() {
		return "FACE";
	}

	public boolean parse(PCTemplate template, String value) {
		int commaLoc = value.indexOf(",");
		if(commaLoc > -1) {
			double width;
			double height;
			try {
				width = Double.parseDouble(value.substring(0, commaLoc - 1).trim());
			}
			catch (NumberFormatException nfe) {
				width = 5;
			}
			
			try {
				height = Double.parseDouble(value.substring(commaLoc + 1).trim());
			}
			catch (NumberFormatException ne) {
				height = 5;
			}
			template.setFace(width, height);
		}
		else {
			double width;
			try {
				width = Double.parseDouble(value);
			}
			catch (NumberFormatException nfe) {
				width = 5;
			}
			template.setFace(width, 0);
		}
		return true;
	}
}


