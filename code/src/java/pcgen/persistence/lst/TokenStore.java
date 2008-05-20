package pcgen.persistence.lst;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pcgen.util.Logging;

/**
 * A Store of LST tokens, has a map and list representation
 */
public class TokenStore
{
	private static TokenStore inst;
	private HashMap<Class<? extends LstToken>, Map<String, LstToken>> tokenTypeMap;
	private final List<Class<? extends LstToken>> tokenTypeList;

	private TokenStore()
	{
		tokenTypeMap =
				new HashMap<Class<? extends LstToken>, Map<String, LstToken>>();
		tokenTypeList = new ArrayList<Class<? extends LstToken>>();
		populateTokenTypeList();
	}

	/**
	 * Create an instance of TokenStore and return it.
	 * @return an instance of TokenStore and return it.
	 */
	public static TokenStore inst()
	{
		if (inst == null)
		{
			inst = new TokenStore();
		}
		return inst;
	}

	private void populateTokenTypeList()
	{
		//Campaign data
		tokenTypeList.add(GlobalLstToken.class);
		tokenTypeList.add(AbilityLstToken.class);
		tokenTypeList.add(CampaignLstToken.class);
		tokenTypeList.add(PCClassLstToken.class);
		tokenTypeList.add(CompanionModLstToken.class);
		tokenTypeList.add(DomainLstToken.class);
		tokenTypeList.add(EquipmentLstToken.class);
		tokenTypeList.add(EquipmentModifierLstToken.class);
		tokenTypeList.add(LanguageLstToken.class);
		tokenTypeList.add(RaceLstToken.class);
		tokenTypeList.add(PCTemplateLstToken.class);
		tokenTypeList.add(SkillLstToken.class);
		tokenTypeList.add(SpellLstToken.class);
		tokenTypeList.add(SourceLstToken.class);
		tokenTypeList.add(SubClassLstToken.class);
		tokenTypeList.add(SubstitutionClassLstToken.class);
		//Kits
		tokenTypeList.add(KitLstToken.class);
		tokenTypeList.add(BaseKitLstToken.class);
		tokenTypeList.add(KitAbilityLstToken.class);
		tokenTypeList.add(KitClassLstToken.class);
		tokenTypeList.add(KitDeityLstToken.class);
		tokenTypeList.add(KitFundsLstToken.class);
		tokenTypeList.add(KitGearLstToken.class);
		tokenTypeList.add(KitLevelAbilityLstToken.class);
		tokenTypeList.add(KitProfLstToken.class);
		tokenTypeList.add(KitSkillLstToken.class);
		tokenTypeList.add(KitSpellsLstToken.class);
		tokenTypeList.add(KitStartpackLstToken.class);
		tokenTypeList.add(KitTableLstToken.class);

		//miscinfo.lst
		tokenTypeList.add(GameModeLstToken.class);
		tokenTypeList.add(AbilityCategoryLstToken.class);
		tokenTypeList.add(BaseDiceLstToken.class);
		tokenTypeList.add(EqSizePenaltyLstToken.class);
		tokenTypeList.add(RollMethodLstToken.class);
		tokenTypeList.add(TabLstToken.class);
		tokenTypeList.add(UnitSetLstToken.class);
		tokenTypeList.add(WieldCategoryLstToken.class);

		//statsandchecks.lst
		tokenTypeList.add(StatsAndChecksLstToken.class);
		tokenTypeList.add(PCAlignmentLstToken.class);
		tokenTypeList.add(BonusSpellLstToken.class);
		tokenTypeList.add(PCCheckLstToken.class);
		tokenTypeList.add(PCStatLstToken.class);

		//sizeAdjustment.ts
		tokenTypeList.add(SizeAdjustmentLstToken.class);

		//rules.js
		tokenTypeList.add(RuleCheckLstToken.class);

		//pointbuymethod.lst
		tokenTypeList.add(PointBuyLstToken.class);
		tokenTypeList.add(PointBuyMethodLstToken.class);
		tokenTypeList.add(PointBuyStatLstToken.class);

		//level.lst
		tokenTypeList.add(LevelLstToken.class);

		//equipmentslots.lst
		tokenTypeList.add(EquipSlotLstToken.class);

		//load.lst
		tokenTypeList.add(LoadInfoLstToken.class);

		//paperinfo.lst
		tokenTypeList.add(PaperInfoLstToken.class);

		//sponsors.lst
		tokenTypeList.add(SponsorLstToken.class);
		
		//subtokens
		tokenTypeList.add(EqModChooseLstToken.class);
		tokenTypeList.add(ChooseLstToken.class);
		tokenTypeList.add(AutoLstToken.class);
		tokenTypeList.add(AddLstToken.class);
		tokenTypeList.add(RemoveLstToken.class);

		//install.lst
		tokenTypeList.add(InstallLstToken.class);
	}

	/**
	 * Add the new token to the token map
	 * @param newToken
	 */
	public void addToTokenMap(LstToken newToken)
	{
		for (Class<? extends LstToken> tokClass : tokenTypeList)
		{
			if (tokClass.isAssignableFrom(newToken.getClass()))
			{
				Map<String, LstToken> tokenMap = getTokenMap(tokClass);
				LstToken test = tokenMap.put(newToken.getTokenName(), newToken);

				if (test != null)
				{
					Logging.errorPrint("More than one " + tokClass.getName()
						+ " has the same token name: '"
						+ newToken.getTokenName() + "'");
				}
			}
		}
	}

	/**
	 * Get the token map
	 * @param tokInterface
	 * @return the token map
	 */
	public Map<String, LstToken> getTokenMap(
		Class<? extends LstToken> tokInterface)
	{
		Map<String, LstToken> tokenMap = tokenTypeMap.get(tokInterface);
		if (tokenMap == null)
		{
			tokenMap = new HashMap<String, LstToken>();
			tokenTypeMap.put(tokInterface, tokenMap);
		}
		return tokenMap;
	}
}
