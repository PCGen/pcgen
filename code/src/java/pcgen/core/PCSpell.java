package pcgen.core;

import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteUtilities;

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
	private TimeUnit timeUnit = null;

	public PCSpell() {
		super();
	}

	/**
	 * @return String
	 */
	public String getPCCText() {
		final StringBuffer sBuff = new StringBuffer();
		sBuff.append(spellbook);
		if(timeUnit != null && !timeUnit.equals("")) {
			sBuff.append("|TIMEUNIT=");
			sBuff.append(timeUnit.getKeyName());
		}
		if(timesPerDay != null && !timesPerDay.equals("")) {
			sBuff.append("|TIMES=");
			sBuff.append(timesPerDay);
		}
		if(casterLevelFormula != null && !casterLevelFormula.equals("")) {
			sBuff.append("|CASTERLEVEL=");
			sBuff.append(casterLevelFormula);
		}
		sBuff.append('|');
		sBuff.append(keyName);
		if(dcFormula != null && !dcFormula.equals("")) {
			sBuff.append(",");
			sBuff.append(dcFormula);
		}

		final List<Prerequisite> preReqs = getPreReqList();

		sBuff
			.append(PrerequisiteUtilities.getPrerequisitePCCText(preReqs, "|"));

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
	 * Sets the time unit.
	 * 
	 * @param timeUnit the new time unit
	 */
	public void setTimeUnit(TimeUnit timeUnit)
	{
		this.timeUnit  = timeUnit;
	}

	/**
	 * @return the timeUnit
	 */
	public TimeUnit getTimeUnit()
	{
		return timeUnit;
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
