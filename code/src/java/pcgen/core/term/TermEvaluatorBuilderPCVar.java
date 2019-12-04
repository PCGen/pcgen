/**
 * Copyright 2008 Andrew Wilson
 * <nuance@users.sourceforge.net>.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created 03 August 2008
 */

package pcgen.core.term;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pcgen.cdom.base.Constants;
import pcgen.core.AbilityCategory;
import pcgen.util.Logging;
import pcgen.util.TermUtilities;

/**
 * {@code EvaluatorFactoryPCVar}
 *
 * This individual enumerations in this class are each responsible for making
 * and returning an object that implements the TermEvaluator interface.
 * Each enumeration has a regular expression that matches one of the
 * "hardcoded" internal variables that every PC has a value for.  They also
 * have an array of string keys that enumerate every string that the regular
 * expression can match (this is not as bad as it sounds since each can only
 * match at most eight strings).  The array of string is used to populate a
 * {@code Map<String, Enum>}
 */

public enum TermEvaluatorBuilderPCVar implements TermEvaluatorBuilder
{
	COMPLETE_PC_ACCHECK("AC{1,2}HECK", new String[]{"ACCHECK", "ACHECK"}, true)
	{

		@Override
		public TermEvaluator getTermEvaluator(final String expressionString, final String src,
			final String matchedSection)
		{

			return new PCACcheckTermEvaluator(expressionString);
		}
	},

	COMPLETE_PC_ARMORACCHECK("ARMORAC{1,2}HECK", new String[]{"ARMORACCHECK", "ARMORACHECK"}, true)
	{

		@Override
		public TermEvaluator getTermEvaluator(final String expressionString, final String src,
			final String matchedSection)
		{

			return new PCArmourACcheckTermEvaluator(expressionString);
		}
	},

	COMPLETE_PC_BAB("BAB", new String[]{"BAB"}, true)
	{

		@Override
		public TermEvaluator getTermEvaluator(final String expressionString, final String src,
			final String matchedSection)
		{

			return new PCBABTermEvaluator(expressionString);
		}
	},

	COMPLETE_PC_BASECR("BASECR", new String[]{"BASECR"}, true)
	{

		@Override
		public TermEvaluator getTermEvaluator(final String expressionString, final String src,
			final String matchedSection)
		{

			return new PCBaseCRTermEvaluator(expressionString);
		}
	},

	COMPLETE_PC_BASEHD("BASEHD", new String[]{"BASEHD"}, true)
	{

		@Override
		public TermEvaluator getTermEvaluator(final String expressionString, final String src,
			final String matchedSection)
		{

			return new PCBaseHDTermEvaluator(expressionString);
		}
	},

	COMPLETE_PC_BASESPELLSTAT("BASESPELLSTAT", new String[]{"BASESPELLSTAT"}, true)
	{

		@Override
		public TermEvaluator getTermEvaluator(final String expressionString, final String src,
			final String matchedSection)
		{

			String source = (src.startsWith("CLASS:")) ? src.substring(6) : "";

			return new PCBaseSpellStatTermEvaluator(expressionString, source);
		}
	},

	COMPLETE_PC_CASTERLEVEL("(?:CASTERLEVEL\\.TOTAL|CASTERLEVEL)", new String[]{"CASTERLEVEL", "CASTERLEVEL.TOTAL"},
			true)
	{

		@Override
		public TermEvaluator getTermEvaluator(final String expressionString, final String src,
			final String matchedSection)
		{

			if ("CASTERLEVEL".equals(matchedSection))
			{
				if (src.startsWith("RACE:"))
				{
					return new PCCasterLevelRaceTermEvaluator(expressionString, src.substring(5));
				}
				else if (src.startsWith("CLASS:"))
				{
					return new PCCasterLevelClassTermEvaluator(expressionString, src.substring(6));
				}
				else
				{
					return new PCCasterLevelTotalTermEvaluator(expressionString);
				}
			}
			return new PCCasterLevelTotalTermEvaluator(expressionString);
		}
	},

	COMPLETE_PC_COUNT_ATTACKS("COUNT\\[ATTACKS\\]", new String[]{"COUNT[ATTACKS]"}, true)
	{

		@Override
		public TermEvaluator getTermEvaluator(final String expressionString, final String src,
			final String matchedSection)
		{

			return new PCCountAttacksTermEvaluator(expressionString);
		}
	},

	COMPLETE_PC_COUNT_CHECKS("COUNT\\[CHECKS\\]", new String[]{"COUNT[CHECKS]"}, true)
	{

		@Override
		public TermEvaluator getTermEvaluator(final String expressionString, final String src,
			final String matchedSection)
		{
			return new PCCountChecksTermEvaluator(expressionString);
		}
	},

	COMPLETE_PC_COUNT_CLASSES("COUNT\\[CLASSES\\]", new String[]{"COUNT[CLASSES]"}, true)
	{

		@Override
		public TermEvaluator getTermEvaluator(final String expressionString, final String src,
			final String matchedSection)
		{

			return new PCCountClassesTermEvaluator(expressionString);
		}
	},

	COMPLETE_PC_COUNT_CONTAINERS("COUNT\\[CONTAINERS\\]", new String[]{"COUNT[CONTAINERS]"}, true)
	{

		@Override
		public TermEvaluator getTermEvaluator(final String expressionString, final String src,
			final String matchedSection)
		{

			return new PCCountContainersTermEvaluator(expressionString);
		}
	},

	COMPLETE_PC_COUNT_DOMAINS("COUNT\\[DOMAINS\\]", new String[]{"COUNT[DOMAINS]"}, true)
	{

		@Override
		public TermEvaluator getTermEvaluator(final String expressionString, final String src,
			final String matchedSection)
		{

			return new PCCountDomainsTermEvaluator(expressionString);
		}
	},

	COMPLETE_PC_COUNT_FEATSNATUREALL("COUNT\\[FEATSALL(?:\\.ALL|\\.HIDDEN|\\.VISIBLE)?\\]",
			new String[]{"COUNT[FEATSALL]", "COUNT[FEATSALL.ALL]", "COUNT[FEATSALL.HIDDEN]", "COUNT[FEATSALL.VISIBLE]"},
			true)
	{

		@Override
		public TermEvaluator getTermEvaluator(final String expressionString, final String src,
			final String matchedSection)
		{

			boolean visible = !(expressionString.endsWith("HIDDEN]"));

			boolean hidden = (expressionString.endsWith("HIDDEN]") || expressionString.endsWith(".ALL]"));

			AbilityCategory abCat = AbilityCategory.FEAT;

			return new PCCountAbilitiesNatureAllTermEvaluator(expressionString, abCat, visible, hidden);
		}
	},

	COMPLETE_PC_COUNT_FEATSNATUREAUTO("COUNT\\[FEATSAUTO(?:\\.ALL|\\.HIDDEN|\\.VISIBLE)?\\]", new String[]{
		"COUNT[FEATSAUTO]", "COUNT[FEATSAUTO.ALL]", "COUNT[FEATSAUTO.HIDDEN]", "COUNT[FEATSAUTO.VISIBLE]"}, true)
	{

		@Override
		public TermEvaluator getTermEvaluator(final String expressionString, final String src,
			final String matchedSection)
		{

			boolean visible = !(expressionString.endsWith("HIDDEN]"));

			boolean hidden = (expressionString.endsWith("HIDDEN]") || expressionString.endsWith("ALL]"));

			AbilityCategory abCat = AbilityCategory.FEAT;

			return new PCCountAbilitiesNatureAutoTermEvaluator(expressionString, abCat, visible, hidden);
		}
	},

	COMPLETE_PC_COUNT_FEATSNATURENORMAL("COUNT\\[FEATS(?:\\.ALL|\\.HIDDEN|\\.VISIBLE)?\\]",
			new String[]{"COUNT[FEATS]", "COUNT[FEATS.ALL]", "COUNT[FEATS.HIDDEN]", "COUNT[FEATS.VISIBLE]"}, true)
	{

		@Override
		public TermEvaluator getTermEvaluator(final String expressionString, final String src,
			final String matchedSection)
		{

			boolean visible = !(expressionString.endsWith("HIDDEN]"));

			boolean hidden = (expressionString.endsWith("HIDDEN]") || expressionString.endsWith("ALL]"));

			AbilityCategory abCat = AbilityCategory.FEAT;

			return new PCCountAbilitiesNatureNormalTermEvaluator(expressionString, abCat, visible, hidden);
		}
	},

	COMPLETE_PC_COUNT_FEATSNATUREVIRTUAL("COUNT\\[VFEATS(?:\\.ALL|\\.HIDDEN|\\.VISIBLE)?\\]",
			new String[]{"COUNT[VFEATS]", "COUNT[VFEATS.ALL]", "COUNT[VFEATS.HIDDEN]", "COUNT[VFEATS.VISIBLE]"}, true)
	{

		@Override
		public TermEvaluator getTermEvaluator(final String expressionString, final String src,
			final String matchedSection)
		{

			boolean visible = !(expressionString.endsWith("HIDDEN]"));

			boolean hidden = (expressionString.endsWith("HIDDEN]") || expressionString.endsWith("ALL]"));

			AbilityCategory abCat = AbilityCategory.FEAT;

			return new PCCountAbilitiesNatureVirtualTermEvaluator(expressionString, abCat, visible, hidden);
		}
	},

	COMPLETE_PC_COUNT_FOLLOWERS("COUNT\\[FOLLOWERS\\]", new String[]{"COUNT[FOLLOWERS]"}, true)
	{

		@Override
		public TermEvaluator getTermEvaluator(final String expressionString, final String src,
			final String matchedSection)
		{

			return new PCCountFollowersTermEvaluator(expressionString);
		}
	},

	COMPLETE_PC_COUNT_LANGUAGES("COUNT\\[LANGUAGES\\]", new String[]{"COUNT[LANGUAGES]"}, true)
	{

		@Override
		public TermEvaluator getTermEvaluator(final String expressionString, final String src,
			final String matchedSection)
		{

			return new PCCountLanguagesTermEvaluator(expressionString);
		}
	},

	COMPLETE_PC_COUNT_MISC_COMPANIONS("COUNT\\[MISC\\.COMPANIONS\\]", new String[]{"COUNT[MISC.COMPANIONS]"}, true)
	{

		@Override
		public TermEvaluator getTermEvaluator(final String expressionString, final String src,
			final String matchedSection)
		{

			return new PCCountMiscCompanionsTermEvaluator(expressionString);
		}
	},

	COMPLETE_PC_COUNT_MISC_FUNDS("COUNT\\[MISC\\.FUNDS\\]", new String[]{"COUNT[MISC.FUNDS]"}, true)
	{

		@Override
		public TermEvaluator getTermEvaluator(final String expressionString, final String src,
			final String matchedSection)
		{

			return new PCCountMiscFundsTermEvaluator(expressionString);
		}
	},

	COMPLETE_PC_COUNT_MISC_MAGIC("COUNT\\[MISC\\.MAGIC\\]", new String[]{"COUNT[MISC.MAGIC]"}, true)
	{

		@Override
		public TermEvaluator getTermEvaluator(final String expressionString, final String src,
			final String matchedSection)
		{

			return new PCCountMiscMagicTermEvaluator(expressionString);
		}
	},

	COMPLETE_PC_COUNT_MOVE("COUNT\\[MOVE\\]", new String[]{"COUNT[MOVE]"}, true)
	{

		@Override
		public TermEvaluator getTermEvaluator(final String expressionString, final String src,
			final String matchedSection)
		{

			return new PCCountMoveTermEvaluator(expressionString);
		}
	},

	COMPLETE_PC_COUNT_NOTES("COUNT\\[NOTES\\]", new String[]{"COUNT[NOTES]"}, true)
	{

		@Override
		public TermEvaluator getTermEvaluator(final String expressionString, final String src,
			final String matchedSection)
		{

			return new PCCountNotesTermEvaluator(expressionString);
		}
	},

	COMPLETE_PC_COUNT_RACESUBTYPES("COUNT\\[RACESUBTYPES\\]", new String[]{"COUNT[RACESUBTYPES]"}, true)
	{

		@Override
		public TermEvaluator getTermEvaluator(final String expressionString, final String src,
			final String matchedSection)
		{

			return new PCCountRaceSubTypesTermEvaluator(expressionString);
		}
	},

	COMPLETE_PC_COUNT_SA("COUNT\\[SA\\]", new String[]{"COUNT[SA]"}, true)
	{

		@Override
		public TermEvaluator getTermEvaluator(final String expressionString, final String src,
			final String matchedSection)
		{

			return new PCCountSABTermEvaluator(expressionString);
		}
	},

	COMPLETE_PC_COUNT_SKILLS("COUNT\\[SKILLS(?:\\.SELECTED|\\.RANKS|\\.NONDEFAULT|\\.USABLE|\\.ALL)?\\]",
			new String[]{"COUNT[SKILLS]", "COUNT[SKILLS.SELECTED]", "COUNT[SKILLS.RANKS]", "COUNT[SKILLS.NONDEFAULT]",
				"COUNT[SKILLS.USABLE]", "COUNT[SKILLS.ALL]"},
			true)
	{

		@Override
		public TermEvaluator getTermEvaluator(final String expressionString, final String src,
			final String matchedSection)
		{

			String filterToken = null;
			int start = expressionString.indexOf('.');
			if (start > 0)
			{
				int end = expressionString.indexOf(']', start);
				filterToken = expressionString.substring(start + 1, end);
			}

			return new PCCountSkillsTermEvaluator(expressionString, filterToken);
		}
	},

	COMPLETE_PC_COUNT_SPELLCLASSES("COUNT\\[SPELLCLASSES\\]", new String[]{"COUNT[SPELLCLASSES]"}, true)
	{

		@Override
		public TermEvaluator getTermEvaluator(final String expressionString, final String src,
			final String matchedSection)
		{

			return new PCCountSpellClassesTermEvaluator(expressionString);
		}
	},

	COMPLETE_PC_COUNT_SPELLRACE("COUNT\\[SPELLRACE\\]", new String[]{"COUNT[SPELLRACE]"}, true)
	{

		@Override
		public TermEvaluator getTermEvaluator(final String expressionString, final String src,
			final String matchedSection)
		{

			return new PCCountSpellRaceTermEvaluator(expressionString);
		}
	},

	COMPLETE_PC_COUNT_STATS("COUNT\\[STATS\\]", new String[]{"COUNT[STATS]"}, true)
	{

		@Override
		public TermEvaluator getTermEvaluator(final String expressionString, final String src,
			final String matchedSection)
		{

			return new PCCountStatsTermEvaluator(expressionString);
		}
	},

	COMPLETE_PC_COUNT_TEMPBONUSNAMES("COUNT\\[TEMPBONUSNAMES\\]", new String[]{"COUNT[TEMPBONUSNAMES]"}, true)
	{

		@Override
		public TermEvaluator getTermEvaluator(final String expressionString, final String src,
			final String matchedSection)
		{

			return new PCCountTempBonusNamesTermEvaluator(expressionString);
		}
	},

	COMPLETE_PC_COUNT_TEMPLATES("COUNT\\[TEMPLATES\\]", new String[]{"COUNT[TEMPLATES]"}, true)
	{

		@Override
		public TermEvaluator getTermEvaluator(final String expressionString, final String src,
			final String matchedSection)
		{

			return new PCCountTemplatesTermEvaluator(expressionString);
		}
	},

	COMPLETE_PC_COUNT_VISIBLETEMPLATES("COUNT\\[VISIBLETEMPLATES\\]", new String[]{"COUNT[VISIBLETEMPLATES]"}, true)
	{

		@Override
		public TermEvaluator getTermEvaluator(final String expressionString, final String src,
			final String matchedSection)
		{

			return new PCCountVisibleTemplatesTermEvaluator(expressionString);
		}
	},

	COMPLETE_PC_COUNT_VISION("COUNT\\[VISION\\]", new String[]{"COUNT[VISION]"}, true)
	{

		@Override
		public TermEvaluator getTermEvaluator(final String expressionString, final String src,
			final String matchedSection)
		{

			return new PCCountVisionTermEvaluator(expressionString);
		}
	},

	COMPLETE_PC_ENCUMBERANCE("ENCUMBERANCE", new String[]{"ENCUMBERANCE"}, true)
	{

		@Override
		public TermEvaluator getTermEvaluator(final String expressionString, final String src,
			final String matchedSection)
		{

			return new PCEncumberanceTermEvaluator(expressionString);
		}
	},

	COMPLETE_PC_HD("HD", new String[]{"HD"}, true)
	{

		@Override
		public TermEvaluator getTermEvaluator(final String expressionString, final String src,
			final String matchedSection)
		{

			return new PCHDTermEvaluator(expressionString);
		}
	},

	COMPLETE_PC_HP("HP", new String[]{"HP"}, true)
	{

		@Override
		public TermEvaluator getTermEvaluator(final String expressionString, final String src,
			final String matchedSection)
		{

			return new PCHPTermEvaluator(expressionString);
		}
	},

	COMPLETE_PC_MAXCASTABLE("MAXCASTABLE", new String[]{"MAXCASTABLE"}, true)
	{

		@Override
		public TermEvaluator getTermEvaluator(final String expressionString, final String src,
			final String matchedSection) throws TermEvaulatorException
		{
			if (src.startsWith("CLASS:"))
			{
				return new PCMaxCastableClassTermEvaluator(expressionString, src.substring(6));
			}
			else if (src.startsWith("DOMAIN:"))
			{
				return new PCMaxCastableDomainTermEvaluator(expressionString, src.substring(7));
			}
			else if (src.startsWith("SPELLTYPE:"))
			{
				return new PCMaxCastableSpellTypeTermEvaluator(expressionString, src.substring(10));
			}
			else if ("ANY".equals(src))
			{
				return new PCMaxCastableAnyTermEvaluator(expressionString);
			}

			String sB = "MAXCASTABLE is not usable in " +
					src;
			throw new TermEvaulatorException(sB, null);
		}
	},

	COMPLETE_PC_MOVEBASE("MOVEBASE", new String[]{"MOVEBASE"}, true)
	{

		@Override
		public TermEvaluator getTermEvaluator(final String expressionString, final String src,
			final String matchedSection)
		{

			return new PCMoveBaseTermEvaluator(expressionString);
		}
	},

	COMPLETE_PC_PC_HEIGHT("PC\\.HEIGHT", new String[]{"PC.HEIGHT"}, false)
	{

		@Override
		public TermEvaluator getTermEvaluator(final String expressionString, final String src,
			final String matchedSection)
		{

			return new PCHeightTermEvaluator(expressionString);
		}
	},

	COMPLETE_PC_PC_WEIGHT("PC\\.WEIGHT", new String[]{"PC.WEIGHT"}, false)
	{

		@Override
		public TermEvaluator getTermEvaluator(final String expressionString, final String src,
			final String matchedSection)
		{

			return new PCWeightTermEvaluator(expressionString);
		}
	},

	COMPLETE_PC_PROFACCHECK("PROFACCHECK", new String[]{"PROFACCHECK"}, true)
	{

		@Override
		public TermEvaluator getTermEvaluator(final String expressionString, final String src,
			final String matchedSection)
		{

			String source = (src.startsWith("EQ:")) ? src.substring(3) : "";

			return new PCProfACCheckTermEvaluator(expressionString, source);
		}
	},

	COMPLETE_PC_RACESIZE("RACESIZE", new String[]{"RACESIZE"}, true)
	{

		@Override
		public TermEvaluator getTermEvaluator(final String expressionString, final String src,
			final String matchedSection)
		{

			return new PCRaceSizeTermEvaluator(expressionString);
		}
	},

	COMPLETE_PC_RACIALHDSIZE("RACIALHDSIZE", new String[]{"RACIALHDSIZE"}, true)
	{

		@Override
		public TermEvaluator getTermEvaluator(final String expressionString, final String src,
			final String matchedSection)
		{

			return new PCRacialHDSizeTermEvaluator(expressionString);
		}
	},

	COMPLETE_PC_SCORE("SCORE", new String[]{"SCORE"}, true)
	{

		@Override
		public TermEvaluator getTermEvaluator(final String expressionString, final String src,
			final String matchedSection)
		{

			try
			{
				int i = Integer.parseInt(src);
				return new FixedTermEvaluator(i);
			}
			catch (NumberFormatException e)
			{
				//OK
			}
			String source = (src.startsWith("STAT:")) ? src.substring(5) : "";

			return new PCScoreTermEvaluator(expressionString, source);
		}
	},

	COMPLETE_PC_SHIELDACCHECK("SHIELDAC{1,2}HECK", new String[]{"SHIELDACCHECK", "SHIELDACHECK"}, true)
	{

		@Override
		public TermEvaluator getTermEvaluator(final String expressionString, final String src,
			final String matchedSection)
		{

			return new PCShieldACcheckTermEvaluator(expressionString);
		}
	},

	COMPLETE_PC_SIZEMOD("SIZEMOD|SIZE", new String[]{"SIZEMOD", "SIZE"}, true)
	{

		@Override
		public TermEvaluator getTermEvaluator(final String expressionString, final String src,
			final String matchedSection)
		{

			if ("SIZEMOD".equals(matchedSection))
			{
				return new PCSizeModEvaluatorTermEvaluator(expressionString);
			}
			else
			{
				return new PCSizeTermEvaluator(expressionString);
			}
		}
	},

	COMPLETE_PC_SPELLBASESTAT("SPELLBASESTATSCORE|SPELLBASESTAT", new String[]{"SPELLBASESTAT", "SPELLBASESTATSCORE"},
			true)
	{

		@Override
		public TermEvaluator getTermEvaluator(final String expressionString, final String src,
			final String matchedSection)
		{

			String source = (src.startsWith("CLASS:")) ? src.substring(6) : "";

			if (expressionString.endsWith("SCORE"))
			{
				return new PCSPellBaseStatScoreEvaluatorTermEvaluator(expressionString, source);
			}
			return new PCSPellBaseStatTermEvaluator(expressionString, source);
		}
	},

	COMPLETE_PC_SPELLLEVEL("SPELLLEVEL", new String[]{"SPELLLEVEL"}, true)
	{

		@Override
		public TermEvaluator getTermEvaluator(final String expressionString, final String src,
			final String matchedSection)
		{

			return new PCSpellLevelTermEvaluator(expressionString);
		}
	},

	COMPLETE_PC_TL("TL", new String[]{"TL"}, true)
	{

		@Override
		public TermEvaluator getTermEvaluator(final String expressionString, final String src,
			final String matchedSection)
		{

			return new PCTLTermEvaluator(expressionString);
		}
	},

	COMPLETE_PC_FAVCLASSLEVEL("FAVCLASSLEVEL", new String[]{"FAVCLASSLEVEL"}, true)
	{

		@Override
		public TermEvaluator getTermEvaluator(final String expressionString, final String src,
			final String matchedSection)
		{

			return new PCFavClassLevelTermEvaluator(expressionString);
		}
	},

	PC_CAST_ATWILL("ATWILL", new String[]{"ATWILL"}, true)
	{

		@Override
		public TermEvaluator getTermEvaluator(final String expressionString, final String src,
			final String matchedSection)
		{
			return new PCCastTimesAtWillTermEvaluator(expressionString);
		}
	},

	START_PC_BL("BL[.=]?", new String[]{"BL.", "BL=", "BL"}, false)
	{

		@Override
		public TermEvaluator getTermEvaluator(final String expressionString, final String src,
			final String matchedSection) {

			String classString;

			if (matchedSection.length() == expressionString.length())
			{
				classString = (src.startsWith("CLASS:")) ? src.substring(6) : "";
			}
			else
			{
				classString = expressionString.substring(matchedSection.length());
			}

			return new PCBLTermEvaluator(expressionString, classString);
		}
	},

	START_PC_CL_BEFORELEVEL("CL;BEFORELEVEL[.=]", new String[]{"CL;BEFORELEVEL.", "CL;BEFORELEVEL="}, false)
	{

		@Override
		public TermEvaluator getTermEvaluator(final String expressionString, final String src,
			final String matchedSection) throws TermEvaulatorException
		{

			if (!src.startsWith("CLASS:"))
			{
				String sB = matchedSection +
						" may only be used in a Class";
				throw new TermEvaulatorException(sB);
			}

			int i;

			try
			{
				i = Integer.parseInt(expressionString.substring(15));
			}
			catch (NumberFormatException e)
			{
				String sB = "Badly formed formula "
						+ expressionString
						+ " in "
						+ src
						+ " should have an integer following "
						+ matchedSection;
				throw new TermEvaulatorException(sB, e);
			}

			return new PCCLBeforeLevelTermEvaluator(expressionString, src.substring(6), i);
		}
	},

	START_PC_CLASSLEVEL("CLASSLEVEL[.=]", new String[]{"CLASSLEVEL.", "CLASSLEVEL="}, false)
	{

		@Override
		public TermEvaluator getTermEvaluator(final String expressionString, final String src,
			final String matchedSection) {

			String exp = expressionString.replace('{', '(').replace('}', ')');

			String classString = exp.substring(11);

			return new PCCLTermEvaluator(expressionString, classString);
		}
	},

	START_PC_CLASS("CLASS[.=]", new String[]{"CLASS.", "CLASS="}, false)
	{

		@Override
		public TermEvaluator getTermEvaluator(final String expressionString, final String src,
			final String matchedSection) {

			return new PCHasClassTermEvaluator(expressionString, expressionString.substring(6));
		}
	},

	START_PC_CL("CL[.=]?", new String[]{"CL.", "CL=", "CL"}, false)
	{

		@Override
		public TermEvaluator getTermEvaluator(final String expressionString, final String src,
			final String matchedSection) throws TermEvaulatorException
		{

			String classString;

			if (expressionString.length() == 2)
			{
				if (!src.startsWith("CLASS:"))
				{
					String sB = matchedSection +
							" may only be used in a Class";
					throw new TermEvaulatorException(sB);
				}

				classString = (src.startsWith("CLASS:")) ? src.substring(6) : "";
			}
			else
			{
				classString = expressionString.substring(3);
			}

			return new PCCLTermEvaluator(expressionString, classString);
		}
	},

	START_PC_COUNT_EQTYPE("COUNT\\[EQTYPE\\.?", new String[]{"COUNT[EQTYPE", "COUNT[EQTYPE."}, false)
	{

		@Override
		public TermEvaluator getTermEvaluator(final String expressionString, final String src,
			final String matchedSection) throws TermEvaulatorException
		{

			// The types string inside the brackets
			String typesString =
					TermUtilities.extractContentsOfBrackets(expressionString, src, matchedSection.length());

			// In the case of the empty string, the split will give us a one
			// element array containing only the empty string (which is the
			// desired result)
			String[] fullTypes = typesString.split("\\.", -1);

			int merge = "MERGENONE".equals(fullTypes[0]) ? Constants.MERGE_NONE
				: "MERGELOC".equals(fullTypes[0]) ? Constants.MERGE_LOCATION : Constants.MERGE_ALL;

			int first = (merge == Constants.MERGE_ALL) ? 0 : 1;

			String[] types;
			if (fullTypes.length > first)
			{
				TermUtilities.checkEqTypeTypesArray(expressionString, fullTypes, first);

				int len = fullTypes.length - first;
				types = new String[len];
				System.arraycopy(fullTypes, first, types, 0, len);
			}
			else
			{
				types = new String[]{""};
			}

			return new PCCountEqTypeTermEvaluator(expressionString, types, merge);
		}
	},

	START_PC_COUNT_EQUIPMENT("COUNT\\[EQUIPMENT\\.?", new String[]{"COUNT[EQUIPMENT.", "COUNT[EQUIPMENT"}, false)
	{

		@Override
		public TermEvaluator getTermEvaluator(final String expressionString, final String src,
			final String matchedSection) throws TermEvaulatorException
		{

			// The types string inside the brackets
			String typesString =
					TermUtilities.extractContentsOfBrackets(expressionString, src, matchedSection.length());

			// In the case of the empty string, the split will give us a one
			// element array containing only the empty string (which is the
			// desired result)
			String[] fullTypes = typesString.split("\\.");

			int merge = "MERGENONE".equals(fullTypes[0]) ? Constants.MERGE_NONE
				: "MERGELOC".equals(fullTypes[0]) ? Constants.MERGE_LOCATION : Constants.MERGE_ALL;

			int first = (merge == Constants.MERGE_ALL) ? 0 : 1;

			String[] types;
			if (fullTypes.length > first)
			{
				TermUtilities.checkEquipmentTypesArray(expressionString, fullTypes, first);

				int len = fullTypes.length - first;
				types = new String[len];
				System.arraycopy(fullTypes, first, types, 0, len);
			}
			else
			{
				types = new String[]{""};
			}

			return new PCCountEquipmentTermEvaluator(expressionString, types, merge);
		}
	},

	START_PC_COUNT_FEATTYPE("COUNT\\[(?:FEATAUTOTYPE|FEATNAME|FEATTYPE|VFEATTYPE)[.=]",
			new String[]{"COUNT[FEATAUTOTYPE.", "COUNT[FEATAUTOTYPE=", "COUNT[FEATNAME.", "COUNT[FEATNAME=",
				"COUNT[FEATTYPE.", "COUNT[FEATTYPE=", "COUNT[VFEATTYPE.", "COUNT[VFEATTYPE="},
			false)
	{

		@Override
		public TermEvaluator getTermEvaluator(final String expressionString, final String src,
			final String matchedSection) throws TermEvaulatorException
		{

			Matcher subtokenMat = SUBTOKEN_PAT.matcher(expressionString);

			if (!subtokenMat.find())
			{
				String sB = "Impossible error while parsing \"" +
						expressionString +
						"\" in " +
						src;
				throw new TermEvaulatorException(sB);
			}

			int start =
					expressionString.startsWith("COUNT[FEATA") ? 19 : expressionString.startsWith("COUNT[V") ? 16 : 15;

			// The types string inside the brackets
			String typesString = TermUtilities.extractContentsOfBrackets(expressionString, src, start);

			// In the case of the empty string, the split will give us a one
			// element array containing only the empty string (which is the
			// desired result)
			String[] types = typesString.split("\\.", -1);

			boolean visible = !(expressionString.endsWith("HIDDEN]"));

			boolean hidden = (expressionString.endsWith("HIDDEN]") || expressionString.endsWith("ALL]"));

			if ("ALL".equals(types[types.length - 1]) || "HIDDEN".equals(types[types.length - 1])
				|| "VISIBLE".equals(types[types.length - 1]))
			{
				if (types.length > 1)
				{
					int len = types.length - 1;
					String[] t = new String[len];
					System.arraycopy(types, 0, t, 0, len);
					types = t;
				}
				else
				{
					types = new String[]{""};
				}
			}

			AbilityCategory abCat = AbilityCategory.FEAT;

			if ("FEATAUTOTYPE".equals(subtokenMat.group()))
			{
				return new PCCountAbilitiesTypeNatureAutoTermEvaluator(expressionString, abCat, types, visible, hidden);
			}
			else if ("FEATNAME".equals(subtokenMat.group()))
			{
				return new PCCountAbilityNameTermEvaluator(expressionString, abCat, types[0], visible, hidden);
			}
			else if ("FEATTYPE".equals(subtokenMat.group()))
			{
				return new PCCountAbilitiesTypeNatureAllTermEvaluator(expressionString, abCat, types, visible, hidden);
			}
			else
			{
				return new PCCountAbilitiesTypeNatureVirtualTermEvaluator(expressionString, abCat, types, visible,
					hidden);
			}
		}
	},

	START_PC_COUNT_FOLLOWERTYPE("COUNT\\[FOLLOWERTYPE\\.", new String[]{"COUNT[FOLLOWERTYPE."}, false)
	{

		@Override
		public TermEvaluator getTermEvaluator(final String expressionString, final String src,
			final String matchedSection) throws TermEvaulatorException
		{

			// The types string inside the brackets
			String typesString = TermUtilities.extractContentsOfBrackets(expressionString, src, 19);

			String[] types = typesString.split("\\.", 3);

			if (types.length == 1)
			{
				// This covers COUNT[FOLLOWERTYPE.Animal Companions] syntax
				return new PCCountFollowerTypeTermEvaluator(expressionString, types[0]);
			}
			else if (types.length == 2)
			{
				String sB = "Badly formed formula " +
						expressionString +
						" in " +
						src;
				throw new TermEvaulatorException(sB);
			}
			else
			{
				Matcher numMat = NUM_PAT.matcher(types[1]);

				if (!numMat.find())
				{
					String sB = "Badly formed formula " +
							expressionString +
							" in " +
							src;
					throw new TermEvaulatorException(sB);
				}

				String newCount = "COUNT[" + types[2] + "]";

				// This will do COUNT[FOLLOWERTYPE.Animal Companions.0.xxx],
				// returning the same as COUNT[xxx] if applied to the right follower
				return new PCCountFollowerTypeTransitiveTermEvaluator(expressionString, types[0],
					Integer.valueOf(numMat.group()), newCount);
			}
		}
	},

	START_PC_COUNT_SKILLTYPE("COUNT\\[SKILLTYPE[.=]", new String[]{"COUNT[SKILLTYPE.", "COUNT[SKILLTYPE="}, false)
	{

		@Override
		public TermEvaluator getTermEvaluator(final String expressionString, final String src,
			final String matchedSection) throws TermEvaulatorException
		{

			String type = TermUtilities.extractContentsOfBrackets(expressionString, src, 16);

			return new PCSkillTypeTermEvaluator(expressionString, type);
		}
	},

	START_PC_COUNT_SPELLBOOKS("COUNT\\[SPELLBOOKS", new String[]{"COUNT[SPELLBOOKS"}, false)
	{

		@Override
		public TermEvaluator getTermEvaluator(final String expressionString, final String src,
			final String matchedSection) throws TermEvaulatorException
		{

			TermUtilities.extractContentsOfBrackets(expressionString, src, 16);

			return new PCCountSpellbookTermEvaluator(expressionString);
		}
	},

	START_PC_COUNT_SPELLSINBOOK("COUNT\\[SPELLSINBOOK", new String[]{"COUNT[SPELLSINBOOK"}, false)
	{

		@Override
		public TermEvaluator getTermEvaluator(final String expressionString, final String src,
			final String matchedSection) throws TermEvaulatorException
		{

			return new PCCountSpellsInbookTermEvaluator(expressionString,
				TermUtilities.extractContentsOfBrackets(expressionString, src, 19));
		}
	},

	START_PC_COUNT_SPELLSKNOWN("COUNT\\[SPELLSKNOWN", new String[]{"COUNT[SPELLSKNOWN"}, false)
	{

		@Override
		public TermEvaluator getTermEvaluator(final String expressionString, final String src,
			final String matchedSection) throws TermEvaulatorException
		{

			// The spells string inside the brackets
			String spellsString = TermUtilities.extractContentsOfBrackets(expressionString, src, 17);

			// make an array with one element in case we need it in the
			// catch block.  The string could legitimatey be empty in which
			// case a numberFormatException will be thrown
			int[] nums = {-1};

			if (spellsString.length() > 1 && spellsString.startsWith("."))
			{
				String s = spellsString.substring(1);
				try
				{
					nums = TermUtilities.convertToIntegers(expressionString, s, matchedSection.length(), 2);
				}
				catch (NumberFormatException e)
				{
					// the -1 means get them all (i.e. no filtering by class
					//  or spellbook)
					nums[0] = -1;
				}
			}

			return new PCCountSpellsKnownTermEvaluator(expressionString, nums);
		}
	},

	START_PC_COUNT_SPELLSLEVELSINBOOK("COUNT\\[SPELLSLEVELSINBOOK", new String[]{"COUNT[SPELLSLEVELSINBOOK"}, false)
	{

		@Override
		public TermEvaluator getTermEvaluator(final String expressionString, final String src,
			final String matchedSection) throws TermEvaulatorException
		{

			String intString = TermUtilities.extractContentsOfBrackets(expressionString, src, 24);

			int[] nums = new int[]{-1};

			if (intString.length() > 1 && intString.startsWith("."))
			{
				String s = intString.substring(1);
				try
				{
					nums = TermUtilities.convertToIntegers(expressionString, s, matchedSection.length(), 2);
				}
				catch (NumberFormatException e)
				{
					// the -1 means get them all (i.e. no filtering by class
					//  or spellbook)
					nums[0] = -1;
				}
			}
			else
			{
				String sB = "Badly formed formula " +
						expressionString +
						" following " +
						matchedSection +
						" should be 2 " +
						"integers separated by dots";
				throw new TermEvaulatorException(sB);
			}

			return new PCCountSpellsLevelsInBookTermEvaluator(expressionString, nums);
		}
	},

	START_PC_COUNT_SPELLTIMES("COUNT\\[SPELLTIMES", new String[]{"COUNT[SPELLTIMES"}, false)
	{

		@Override
		public TermEvaluator getTermEvaluator(final String expressionString, final String src,
			final String matchedSection) throws TermEvaulatorException
		{

			String intString = TermUtilities.extractContentsOfBrackets(expressionString, src, 16);

			int[] nums = new int[]{-1};

			if (intString.length() > 1 && intString.startsWith("."))
			{
				String s = intString.substring(1);
				try
				{
					nums = TermUtilities.convertToIntegers(expressionString, s, matchedSection.length(), 4);
				}
				catch (NumberFormatException e)
				{
					// the -1 means get them all (i.e. no filtering by class
					//  or spellbook)
					nums[0] = -1;
				}
			}
			else
			{
				String sB = "Badly formed formula " +
						expressionString +
						" following " +
						matchedSection +
						" should be 4 " +
						"integers separated by dots";
				throw new TermEvaulatorException(sB);
			}

			return new PCCountSpellTimesTermEvaluator(expressionString, nums);
		}
	},

	START_PC_EQTYPE("EQTYPE", new String[]{"EQTYPE"}, false)
	{

		@Override
		public TermEvaluator getTermEvaluator(final String expressionString, final String src,
			final String matchedSection) {

			return new PCEqTypeTermEvaluator(expressionString);
		}
	},

	START_PC_HASDEITY("HASDEITY:", new String[]{"HASDEITY:"}, false)
	{

		@Override
		public TermEvaluator getTermEvaluator(final String expressionString, final String src,
			final String matchedSection) {

			return new PCHasDeityTermEvaluator(expressionString, expressionString.substring(9));
		}
	},

	START_PC_HASFEAT("HASFEAT:", new String[]{"HASFEAT:"}, false)
	{

		@Override
		public TermEvaluator getTermEvaluator(final String expressionString, final String src,
			final String matchedSection) {

			return new PCHasFeatTermEvaluator(expressionString, expressionString.substring(8));
		}
	},

	START_PC_MAXLEVEL("MAXLEVEL", new String[]{"MAXLEVEL"}, true)
	{

		@Override
		public TermEvaluator getTermEvaluator(final String expressionString, final String src,
			final String matchedSection)
		{

			if (src.startsWith("CLASS:") || src.startsWith("CLASS|"))
			{
				return new PCMaxLevelTermEvaluator(expressionString, src.substring(6));
			}
			else
			{
				Logging.errorPrint("MAXLEVEL term called without a CLASS source");
				return new PCMaxLevelTermEvaluator(expressionString, "");
			}
		}
	},

	START_PC_MODEQUIP("MODEQUIP", new String[]{"MODEQUIP"}, false)
	{

		@Override
		public TermEvaluator getTermEvaluator(final String expressionString, final String src,
			final String matchedSection) {

			return new PCModEquipTermEvaluator(expressionString, expressionString.substring(8));
		}
	},

	START_PC_MOVE("MOVE\\[", new String[]{"MOVE["}, false)
	{

		@Override
		public TermEvaluator getTermEvaluator(final String expressionString, final String src,
			final String matchedSection) throws TermEvaulatorException
		{

			return new PCMovementTermEvaluator(expressionString,
				TermUtilities.extractContentsOfBrackets(expressionString, src, 5));
		}
	},

	START_PC_PC_SIZE("PC\\.SIZE(?:\\.INT)?", new String[]{"PC.SIZE.INT", "PC.SIZE"}, false)
	{

		@Override
		public TermEvaluator getTermEvaluator(final String expressionString, final String src,
			final String matchedSection)
		{

			if (matchedSection.length() == 11)
			{
				if (src.startsWith("EQ:"))
				{
					return new PCSizeIntEQTermEvaluator(expressionString, src.substring(3));
				}
				else
				{
					return new PCSizeIntTermEvaluator(expressionString);
				}
			}
			else
			{
				return new PCSizeTermEvaluator(expressionString);
			}
		}
	},

	START_PC_SKILLRANK("SKILLRANK[.=]", new String[]{"SKILLRANK.", "SKILLRANK="}, false)
	{

		@Override
		public TermEvaluator getTermEvaluator(final String expressionString, final String src,
			final String matchedSection) {

			String skillString = expressionString.substring(10).replace('{', '(').replace('}', ')');

			return new PCSkillRankTermEvaluator(expressionString, skillString);
		}
	},

	START_PC_SKILLTOTAL("SKILLTOTAL[.=]", new String[]{"SKILLTOTAL.", "SKILLTOTAL="}, false)
	{

		@Override
		public TermEvaluator getTermEvaluator(final String expressionString, final String src,
			final String matchedSection) {

			String skillString = expressionString.substring(11).replace('{', '(').replace('}', ')');

			return new PCSkillTotalTermEvaluator(expressionString, skillString);
		}
	},

	START_PC_VARDEFINED("VARDEFINED:", new String[]{"VARDEFINED:"}, false)
	{

		@Override
		public TermEvaluator getTermEvaluator(final String expressionString, final String src,
			final String matchedSection) {

			String varString = expressionString.substring(11);

			return new PCVarDefinedTermEvaluator(expressionString, varString);
		}
	},

	START_PC_WEIGHT("WEIGHT\\.", new String[]{"WEIGHT."}, false)
	{

		@Override
		public TermEvaluator getTermEvaluator(final String expressionString, final String src,
			final String matchedSection) throws TermEvaulatorException
		{

			// The type of weight we want the value for
			String valString = expressionString.substring(7);

            switch (valString) {
                case "CARRIED":
                    return new PCCarriedWeightTermEvaluator(expressionString);
                case "EQUIPPED":
                    // TODO: not carried, equipped!
                    return new PCCarriedWeightTermEvaluator(expressionString);
                case "PC":
                    return new PCWeightTermEvaluator(expressionString);
                case "TOTAL":
                    // total weight of PC and all carried equipment
                    return new PCTotalWeightTermEvaluator(expressionString);
            }

			String sB = "invalid string following WEIGHT. in " +
					expressionString;
			throw new TermEvaulatorException(sB);
		}
	},

	COMPLETE_PC_BONUSLANG("BONUSLANG", new String[]{"BONUSLANG"}, true)
	{
		@Override
		public TermEvaluator getTermEvaluator(final String expressionString, final String src,
			final String matchedSection)
		{
			return new PCBonusLangTermEvaluator(expressionString);
		}
	},

	COMPLETE_PC_HANDS("HANDS", new String[]{"HANDS"}, true)
	{
		@Override
		public TermEvaluator getTermEvaluator(final String expressionString, final String src,
			final String matchedSection)
		{

			return new PCHandsTermEvaluator(expressionString);
		}

	},

	COMPLETE_PC_LEGS("LEGS", new String[]{"LEGS"}, true)
	{
		@Override
		public TermEvaluator getTermEvaluator(final String expressionString, final String src,
			final String matchedSection)
		{

			return new PCLegsTermEvaluator(expressionString);
		}

	};

	static final String SUBTOKEN_STRING = "(FEATAUTOTYPE|FEATNAME|FEATTYPE|VFEATTYPE)";
	static final Pattern SUBTOKEN_PAT = Pattern.compile(SUBTOKEN_STRING);

	static final Pattern NUM_PAT = Pattern.compile("\\d+");

	private final String termConstructorPattern;
	private final String[] termConstructorKeys;
	private final boolean patternMatchesEntireTerm;

	TermEvaluatorBuilderPCVar(String pattern, String[] keys, boolean matchEntireTerm)
	{
		termConstructorPattern = pattern;
		termConstructorKeys = keys;
		patternMatchesEntireTerm = matchEntireTerm;
	}

	@Override
	public String getTermConstructorPattern()
	{
		return termConstructorPattern;
	}

	@Override
	public String[] getTermConstructorKeys()
	{
		return termConstructorKeys;
	}

	@Override
	public boolean isEntireTerm()
	{
		return patternMatchesEntireTerm;
	}
}
