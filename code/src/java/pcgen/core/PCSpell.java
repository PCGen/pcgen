package pcgen.core;

import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriter;
import pcgen.util.Logging;

import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;

/**
 * This class is a spell granted by a class, template, etc. These objects are
 * typically created by a SPELL tag in an LST file, but may be added from other
 * sources as well.
 *
 * @author sage_sam
 */
public class PCSpell extends PObject {

	// spellbook
	private String spellbook = null;

	// name: inherited
	// number of times/day
	private String timesPerDay = null;
	private String casterLevelFormula = null;
	private String dcFormula = null;

	public PCSpell() {
		super();
	}

	/**
	 * @return String
	 */
	public String getPCCText() {
		final StringBuffer sBuff = new StringBuffer();
		sBuff.append(spellbook);
		if(timesPerDay != null && !timesPerDay.equals("")) {
			sBuff.append("|TIMES=");
			sBuff.append(timesPerDay);
		}
		if(casterLevelFormula != null && !casterLevelFormula.equals("")) {
			sBuff.append("|CASTERLEVEL=");
			sBuff.append(casterLevelFormula);
		}
		sBuff.append('|');
		sBuff.append(name);
		if(dcFormula != null && !dcFormula.equals("")) {
			sBuff.append(",");
			sBuff.append(dcFormula);
		}

		final List preReqs = getPreReqList();

		if ((preReqs != null) && (preReqs.size() > 0)) {
			final StringWriter writer = new StringWriter();
			final PrerequisiteWriter preReqWriter = new PrerequisiteWriter();
			for (Iterator preReqIter = preReqs.iterator(); preReqIter.hasNext();) {
				final Prerequisite preReq = (Prerequisite) preReqIter.next();
				try {
					preReqWriter.write(writer, preReq);
				} catch (PersistenceLayerException e) {
					Logging.errorPrint("Failed to encode prereq: ", e);
				}
				if (preReqIter.hasNext()) {
					writer.write("|");
				}
			}
			sBuff.append('|');
			sBuff.append(writer.toString());
		}

		return sBuff.toString();
	}

	/**
	 * Sets the spellbook.
	 *
	 * @param spellbook
	 *          The spellbook to set
	 */
	public void setSpellbook(final String spellbook) {
		this.spellbook = spellbook;
	}

	/**
	 * Returns the spellbook.
	 *
	 * @return String
	 */
	public String getSpellbook() {
		return spellbook;
	}

	/**
	 * Sets the timesPerDay.
	 *
	 * @param timesPerDay
	 *          The timesPerDay to set
	 */
	public void setTimesPerDay(final String timesPerDay) {
		this.timesPerDay = timesPerDay;
	}

	/**
	 * Returns the timesPerDay.
	 *
	 * @return String
	 */
	public String getTimesPerDay() {
		return timesPerDay;
	}


	/**
	 * @return Returns the casterLevelFormula.
	 */
	public String getCasterLevelFormula() {
		return casterLevelFormula;
	}

	/**
	 * @param casterLevelFormula The casterLevelFormula to set.
	 */
	public void setCasterLevelFormula(final String casterLevelFormula) {
		this.casterLevelFormula = casterLevelFormula;
	}

	/**
	 * @return Returns the dcFormula.
	 */
	public String getDcFormula() {
		return dcFormula;
	}

	/**
	 * @param dcFormula The dcFormula to set.
	 */
	public void setDcFormula(final String dcFormula) {
		this.dcFormula = dcFormula;
	}
}