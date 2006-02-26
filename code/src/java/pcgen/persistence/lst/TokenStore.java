package pcgen.persistence.lst;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pcgen.util.Logging;

/**
 * A Store of LST tokens, has a map and list representation
 */
public class TokenStore {
	private static TokenStore inst;
	private HashMap tokenTypeMap;
	private final List tokenTypeList;

	private TokenStore() {
		tokenTypeMap = new HashMap();
		tokenTypeList = new ArrayList();
		populateTokenTypeList();
	}
	
	/**
	 * Create an instance of TokenStore and return it.
	 * @return an instance of TokenStore and return it.
	 */
	public static TokenStore inst() {
		if(inst == null) {
			inst = new TokenStore();
		}
		return inst;
	}
	
	private void populateTokenTypeList() {
		//Campaign data
		tokenTypeList.add(GlobalLstToken.class);
		tokenTypeList.add(AbilityLstToken.class);
		tokenTypeList.add(CampaignLstToken.class);
		tokenTypeList.add(PCClassLstToken.class);
		tokenTypeList.add(CompanionModLstToken.class);
		tokenTypeList.add(DeityLstToken.class);
		tokenTypeList.add(DomainLstToken.class);
		tokenTypeList.add(EquipmentLstToken.class);
		tokenTypeList.add(EquipmentModifierLstToken.class);
		tokenTypeList.add(LanguageLstToken.class);
		tokenTypeList.add(RaceLstToken.class);
		tokenTypeList.add(SkillLstToken.class);
		tokenTypeList.add(SpellLstToken.class);
		tokenTypeList.add(SubClassLstToken.class);
		tokenTypeList.add(WeaponProfLstToken.class);
		
		//miscinfo.lst
		tokenTypeList.add(GameModeLstToken.class);
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
	}

	/**
	 * Add the new token to the token map
	 * @param newToken
	 */
	public void addToTokenMap(LstToken newToken) {
		for(int i = 0; i < tokenTypeList.size(); i++) {
			Class tokInterface = (Class)tokenTypeList.get(i);
			if(tokInterface.isAssignableFrom(newToken.getClass())) {
				Map tokenMap = getTokenMap(tokInterface);
				Object test = tokenMap.put(newToken.getTokenName(), newToken);

				if (test != null) {
					Logging.errorPrint("More than one " + tokInterface.getName() + " has the same token name: '" + newToken.getTokenName() + "'");
				}
			}
		}
	}

	/**
	 * Get the token map
	 * @param tokInterface
	 * @return the token map
	 */
	public Map getTokenMap(Class tokInterface) {
		Map tokenMap = (Map)tokenTypeMap.get(tokInterface);
		if(tokenMap == null) {
			tokenMap = new HashMap();
			tokenTypeMap.put(tokInterface, tokenMap);
		}
		return tokenMap;
	}
}
