package plugin.lsttokens.testsupport;

import java.util.Set;

import pcgen.base.util.ListSet;
import pcgen.core.Deity;
import pcgen.core.Domain;
import pcgen.core.Equipment;
import pcgen.core.Language;
import pcgen.core.PCClass;
import pcgen.core.PCTemplate;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.Skill;
import pcgen.core.WeaponProf;

public class TransparentPlayerCharacter extends PlayerCharacter
{

	public Set<Equipment> equipmentSet = new ListSet<Equipment>();
	public Set<WeaponProf> weaponProfSet = new ListSet<WeaponProf>();
	public Set<PCTemplate> templateSet = new ListSet<PCTemplate>();
	public Set<Skill> skillSet = new ListSet<Skill>();
	public Deity deity = null;
	public Set<Domain> domainSet = new ListSet<Domain>();
	public Set<Language> languageSet = new ListSet<Language>();
	public Race race = null;
	public Set<PCClass> classSet = new ListSet<PCClass>();
	public int spellcastinglevel = -1;
	public Set<Skill> classSkillSet = new ListSet<Skill>();
	public Set<Skill> crossClassSkillSet = new ListSet<Skill>();
	
	@Override
	public Set<Equipment> getEquipmentSet()
	{
		return equipmentSet;
	}

	@Override
	public Set<WeaponProf> getWeaponProfSet()
	{
		return weaponProfSet;
	}

	@Override
	public Set<PCTemplate> getTemplateSet()
	{
		return templateSet;
	}

	@Override
	public Set<Skill> getSkillSet()
	{
		return (skillSet == null) ? new ListSet<Skill>() : skillSet;
	}

	@Override
	public boolean isClassSkill(PCClass cl, Skill sk)
	{
		return classSkillSet.contains(sk);
	}

	@Override
	public boolean isCrossClassSkill(PCClass cl, Skill sk)
	{
		return crossClassSkillSet.contains(sk);
	}

	@Override
	public Deity getDeity()
	{
		return deity;
	}

	@Override
	public Set<Domain> getDomainSet()
	{
		return domainSet;
	}

	@Override
	public Set<Language> getLanguageSet()
	{
		return languageSet;
	}

	@Override
	public Race getRace()
	{
		return race;
	}

	@Override
	public Set<PCClass> getClassSet()
	{
		return classSet;
	}

	@Override
	public boolean isSpellCaster(int level)
	{
		return (level <= spellcastinglevel);
	}
}
