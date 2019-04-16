<#ftl encoding="UTF-8" strip_whitespace=true >
<?xml version="1.0" encoding="iso-8859-1"?>
<root version="3.3" release="17|CoreRPG:3">
	<character>
      <abilities>
		<@loop from=0 to=pcvar('COUNT[STATS]-1') ; stat , stat_has_next>
		<${pcstring('STAT.${stat}.LONGNAME')?lower_case}>
         <score type="number">${pcstring('STAT.${stat}')}</score>
         <bonus type="number">${pcstring('STAT.${stat}.MOD')}</bonus>
      </${pcstring('STAT.${stat}.LONGNAME')?lower_case}>
      </@loop>
      </abilities>
      <name type="string">${pcstring('NAME')}</name>
      <deity type="string">${pcstring('DEITY')}</deity>
      <alignment type="string">${pcstring('ALIGNMENT')}</alignment>
      <classes>
      <@loop from=0 to=pcvar('COUNT[CLASSES]-1') ; class , class_has_next>
         <id-${(class + 1)?left_pad(5, "0")}>
            <name type="string">${pcstring('CLASS.${class}')}</name>
            <level type="number">${pcstring('CLASS.${class}.LEVEL')}</level>
            <shortcut type="windowreference">
				   <class>class</class>
				   <recordname>reference.class.${pcstring('CLASS.${class}')?lower_case}@*</recordname>
			   </shortcut>
            <#if pcstring('CLASS.${class}')?contains("Envoy")>
            <skillranks type="number">8</skillranks>
            <#elseif pcstring('CLASS.${class}')?contains("Mechanic")>
            <skillranks type="number">4</skillranks>
            <#elseif pcstring('CLASS.${class}')?contains("Mystic")>
            <skillranks type="number">6</skillranks>
            <#elseif pcstring('CLASS.${class}')?contains("Operative")>
            <skillranks type="number">8</skillranks>
            <#elseif pcstring('CLASS.${class}')?contains("Solarian")>
            <skillranks type="number">4</skillranks>
            <#elseif pcstring('CLASS.${class}')?contains("Soldier")>
            <skillranks type="number">4</skillranks>
            <#elseif pcstring('CLASS.${class}')?contains("Technomancer")>
            <skillranks type="number">4</skillranks>
            </#if>
         </id-${(class + 1)?left_pad(5, "0")}>
      </@loop>
      </classes>
      <size type="string">${pcstring('SIZELONG')}</size>
      <race type="string">${pcstring('RACE')}</race>
      <racelink type="windowreference">
			<class>race</class>
			<recordname>reference.race.${pcstring('RACE')?lower_case}@*</recordname>
		</racelink>
      <theme type="string">${pcstring('ABILITYALL.Theme.VISIBLE.0.TYPE=Theme.ASPECT.Title')?lower_case?cap_first}</theme>
      <themelink type="windowreference">
			<class>theme</class>
			<recordname>reference.theme.${pcstring('ABILITYALL.Theme.VISIBLE.0.TYPE=Theme.ASPECT.Title')?lower_case}@*</recordname>
		</themelink>
      <speed>
      <@loop from=0 to=pcvar('COUNT[MOVE]-1') ; movement , movement_has_next>
			<base type="number">${pcstring('MOVE.${movement}.RATE')}</base>
      </@loop>
		</speed>
      <skilllist>
      <#assign xAcr = 0>
      <#assign xAth = 0>
      <#assign xBlu = 0>
      <#assign xCom = 0>
      <#assign xCul = 0>
      <#assign xDip = 0>
      <#assign xDis = 0>
      <#assign xEng = 0>
      <#assign xInt = 0>
      <#assign xLif = 0>
      <#assign xMed = 0>
      <#assign xMys = 0>
      <#assign xPer = 0>
      <#assign xPhy = 0>
      <#assign xPil = 0>
      <#assign xSen = 0>
      <#assign xSle = 0>
      <#assign xSte = 0>
      <#assign xSur = 0>
      <#assign skillCount = 0>
      <@loop from=0 to=pcvar('count("SKILLSIT", "VIEW=VISIBLE_EXPORT")')-1; skill , skill_has_next >
      <#assign skillCount = skill + 1>
      <id-${(skill + 1)?left_pad(5, "0")}>
         <label type="string">${pcstring('SKILLSIT.${skill}')}</label>
         <ranks type="number">${pcstring('SKILLSIT.${skill}.RANK')?replace("\\.0", "", "rf")}</ranks>
         <total type="number">${pcstring('SKILLSIT.${skill}.TOTAL')}</total>
         <stat type="number">${pcstring('SKILLSIT.${skill}.ABMOD')}</stat>
         <#if pcstring('SKILLSIT.${skill}')?contains("Acrobatics")>
         <@loop from=0 to=pcvar('COUNT[CLASSES]-1') ; class , class_has_next>
         <#if pcstring('CLASS.${class}')?contains("Envoy")>
         <state type="number">1</state>
         <#elseif pcstring('CLASS.${class}')?contains("Operative")>
         <state type="number">1</state>
         <#elseif pcstring('CLASS.${class}')?contains("Solarian")>
         <state type="number">1</state>
         <#elseif pcstring('CLASS.${class}')?contains("Soldier")>
         <state type="number">1</state>
         <#else>
         <state type="number">0</state>
         </#if>
         </@loop>
         </#if>
         <#if pcstring('SKILLSIT.${skill}')?contains("Athletics")>
         <#assign xAth = 1>
         <@loop from=0 to=pcvar('COUNT[CLASSES]-1') ; class , class_has_next>
         <#if pcstring('CLASS.${class}')?contains("Envoy")>
         <state type="number">1</state>
         <#elseif pcstring('CLASS.${class}')?contains("Mechanic")>
         <state type="number">1</state>
         <#elseif pcstring('CLASS.${class}')?contains("Operative")>
         <state type="number">1</state>
         <#elseif pcstring('CLASS.${class}')?contains("Solarian")>
         <state type="number">1</state>
         <#elseif pcstring('CLASS.${class}')?contains("Soldier")>
         <state type="number">1</state>
         <#else>
         <state type="number">0</state>
         </#if>
         </@loop>
         </#if>
         <#if pcstring('SKILLSIT.${skill}')?contains("Computers")>
         <#assign xCom = 1>
         <@loop from=0 to=pcvar('COUNT[CLASSES]-1') ; class , class_has_next>
         <#if pcstring('CLASS.${class}')?contains("Envoy")>
         <state type="number">1</state>
         <#elseif pcstring('CLASS.${class}')?contains("Mechanic")>
         <state type="number">1</state>
         <#elseif pcstring('CLASS.${class}')?contains("Operative")>
         <state type="number">1</state>
         <#elseif pcstring('CLASS.${class}')?contains("Technomancer")>
         <state type="number">1</state>
         <#else>
         <state type="number">0</state>
         </#if>
         </@loop>
         </#if>
         <#if pcstring('SKILLSIT.${skill}')?contains("Bluff")>
         <#assign xBlu = 1>
         <@loop from=0 to=pcvar('COUNT[CLASSES]-1') ; class , class_has_next>
         <#if pcstring('CLASS.${class}')?contains("Envoy")>
         <state type="number">1</state>
         <#elseif pcstring('CLASS.${class}')?contains("Mystic")>
         <state type="number">1</state>
         <#elseif pcstring('CLASS.${class}')?contains("Operative")>
         <state type="number">1</state>
         <#else>
         <state type="number">0</state>
         </#if>
         </@loop>
         </#if>
         <#if pcstring('SKILLSIT.${skill}')?contains("Culture")>
         <#assign xCul = 1>
         <@loop from=0 to=pcvar('COUNT[CLASSES]-1') ; class , class_has_next>
         <#if pcstring('CLASS.${class}')?contains("Envoy")>
         <state type="number">1</state>
         <#elseif pcstring('CLASS.${class}')?contains("Mystic")>
         <state type="number">1</state>
         <#elseif pcstring('CLASS.${class}')?contains("Operative")>
         <state type="number">1</state>
         <#else>
         <state type="number">0</state>
         </#if>
         </@loop>
         </#if>
         <#if pcstring('SKILLSIT.${skill}')?contains("Diplomacy")>
         <#assign xDip = 1>
         <@loop from=0 to=pcvar('COUNT[CLASSES]-1') ; class , class_has_next>
         <#if pcstring('CLASS.${class}')?contains("Envoy")>
         <state type="number">1</state>
         <#elseif pcstring('CLASS.${class}')?contains("Mystic")>
         <state type="number">1</state>
         <#elseif pcstring('CLASS.${class}')?contains("Solarian")>
         <state type="number">1</state>
         <#else>
         <state type="number">0</state>
         </#if>
         </@loop>
         </#if>
         <#if pcstring('SKILLSIT.${skill}')?contains("Disguise")>
         <#assign xDis = 1>
         <@loop from=0 to=pcvar('COUNT[CLASSES]-1') ; class , class_has_next>
         <#if pcstring('CLASS.${class}')?contains("Envoy")>
         <state type="number">1</state>
         <#elseif pcstring('CLASS.${class}')?contains("Mystic")>
         <state type="number">1</state>
         <#elseif pcstring('CLASS.${class}')?contains("Operative")>
         <state type="number">1</state>
         <#else>
         <state type="number">0</state>
         </#if>
         </@loop>
         </#if>
         <#if pcstring('SKILLSIT.${skill}')?contains("Engineering")>
         <#assign xEng = 1>
         <@loop from=0 to=pcvar('COUNT[CLASSES]-1') ; class , class_has_next>
         <#if pcstring('CLASS.${class}')?contains("Envoy")>
         <state type="number">1</state>
         <#elseif pcstring('CLASS.${class}')?contains("Mechanic")>
         <state type="number">1</state>
         <#elseif pcstring('CLASS.${class}')?contains("Ooperative")>
         <state type="number">1</state>
         <#elseif pcstring('CLASS.${class}')?contains("Soldier")>
         <state type="number">1</state>
         <#elseif pcstring('CLASS.${class}')?contains("Technomancer")>
         <state type="number">1</state>
         <#else>
         <state type="number">0</state>
         </#if>
         </@loop>
         </#if>
         <#if pcstring('SKILLSIT.${skill}')?contains("Intimidate")>
         <#assign xInt = 1>
         <@loop from=0 to=pcvar('COUNT[CLASSES]-1') ; class , class_has_next>
         <#if pcstring('CLASS.${class}')?contains("Envoy")>
         <state type="number">1</state>
         <#elseif pcstring('CLASS.${class}')?contains("Mystic")>
         <state type="number">1</state>
         <#elseif pcstring('CLASS.${class}')?contains("Operative")>
         <state type="number">1</state>
         <#elseif pcstring('CLASS.${class}')?contains("Solarian")>
         <state type="number">1</state>
         <#elseif pcstring('CLASS.${class}')?contains("Soldier")>
         <state type="number">1</state>
         <#else>
         <state type="number">0</state>
         </#if>
         </@loop>
         </#if>
         <#if pcstring('SKILLSIT.${skill}')?contains("Life Science")>
         <#assign xLif = 1>
         <@loop from=0 to=pcvar('COUNT[CLASSES]-1') ; class , class_has_next>
         <#if pcstring('CLASS.${class}')?contains("Mystic")>
         <state type="number">1</state>
         <#elseif pcstring('CLASS.${class}')?contains("Technomancer")>
         <state type="number">1</state>
         <#else>
         <state type="number">0</state>
         </#if>
         </@loop>
         </#if>
         <#if pcstring('SKILLSIT.${skill}')?contains("Medicine")>
         <#assign xMed = 1>
         <@loop from=0 to=pcvar('COUNT[CLASSES]-1') ; class , class_has_next>
         <#if pcstring('CLASS.${class}')?contains("Envoy")>
         <state type="number">1</state>
         <#elseif pcstring('CLASS.${class}')?contains("Mechanic")>
         <state type="number">1</state>
         <#elseif pcstring('CLASS.${class}')?contains("Mystic")>
         <state type="number">1</state>
         <#elseif pcstring('CLASS.${class}')?contains("Operative")>
         <state type="number">1</state>
         <#elseif pcstring('CLASS.${class}')?contains("Soldier")>
         <state type="number">1</state>
         <#else>
         <state type="number">0</state>
         </#if>
         </@loop>
         </#if>
         <#if pcstring('SKILLSIT.${skill}')?contains("Mysticism")>
         <#assign xMys = 1>
         <@loop from=0 to=pcvar('COUNT[CLASSES]-1') ; class , class_has_next>
         <#if pcstring('CLASS.${class}')?contains("Mystic")>
         <state type="number">1</state>
         <#elseif pcstring('CLASS.${class}')?contains("Solarian")>
         <state type="number">1</state>
         <#elseif pcstring('CLASS.${class}')?contains("Technomancer")>
         <state type="number">1</state>
         <#else>
         <state type="number">0</state>
         </#if>
         </@loop>
         </#if>
         <#if pcstring('SKILLSIT.${skill}')?contains("Perception")>
         <#assign xPer = 1>
         <@loop from=0 to=pcvar('COUNT[CLASSES]-1') ; class , class_has_next>
         <#if pcstring('CLASS.${class}')?contains("Envoy")>
         <state type="number">1</state>
         <#elseif pcstring('CLASS.${class}')?contains("Mechanic")>
         <state type="number">1</state>
         <#elseif pcstring('CLASS.${class}')?contains("Mystic")>
         <state type="number">1</state>
         <#elseif pcstring('CLASS.${class}')?contains("Operative")>
         <state type="number">1</state>
         <#elseif pcstring('CLASS.${class}')?contains("Solarian")>
         <state type="number">1</state>
         <#else>
         <state type="number">0</state>
         </#if>
         </@loop>
         </#if>
         <#if pcstring('SKILLSIT.${skill}')?contains("Physical Science")>
         <#assign xPhy = 1>
         <@loop from=0 to=pcvar('COUNT[CLASSES]-1') ; class , class_has_next>
         <#if pcstring('CLASS.${class}')?contains("Mechanic")>
         <state type="number">1</state>
         <#elseif pcstring('CLASS.${class}')?contains("Solarian")>
         <state type="number">1</state>
         <#elseif pcstring('CLASS.${class}')?contains("Technomancer")>
         <state type="number">1</state>
         <#else>
         <state type="number">0</state>
         </#if>
         </@loop>
         </#if>
         <#if pcstring('SKILLSIT.${skill}')?contains("Piloting")>
         <#assign xPil = 1>
         <@loop from=0 to=pcvar('COUNT[CLASSES]-1') ; class , class_has_next>
         <#if pcstring('CLASS.${class}')?contains("Envoy")>
         <state type="number">1</state>
         <#elseif pcstring('CLASS.${class}')?contains("Mechanic")>
         <state type="number">1</state>
         <#elseif pcstring('CLASS.${class}')?contains("Operative")>
         <state type="number">1</state>
         <#elseif pcstring('CLASS.${class}')?contains("Soldier")>
         <state type="number">1</state>
         <#elseif pcstring('CLASS.${class}')?contains("Technomancer")>
         <state type="number">1</state>
         <#else>
         <state type="number">0</state>
         </#if>
         </@loop>
         </#if>
         <#if pcstring('SKILLSIT.${skill}')?contains("Sense Motive")>
         <#assign xSen = 1>
         <@loop from=0 to=pcvar('COUNT[CLASSES]-1') ; class , class_has_next>
         <#if pcstring('CLASS.${class}')?contains("Envoy")>
         <state type="number">1</state>
         <#elseif pcstring('CLASS.${class}')?contains("Mystic")>
         <state type="number">1</state>
         <#elseif pcstring('CLASS.${class}')?contains("Operative")>
         <state type="number">1</state>
         <#elseif pcstring('CLASS.${class}')?contains("Solarian")>
         <state type="number">1</state>
         <#else>
         <state type="number">0</state>
         </#if>
         </@loop>
         </#if>
         <#if pcstring('SKILLSIT.${skill}')?contains("Sleight of Hand")>
         <#assign xSle = 1>
         <@loop from=0 to=pcvar('COUNT[CLASSES]-1') ; class , class_has_next>
         <#if pcstring('CLASS.${class}')?contains("Envoy")>
         <state type="number">1</state>
         <#elseif pcstring('CLASS.${class}')?contains("Operative")>
         <state type="number">1</state>
         <#elseif pcstring('CLASS.${class}')?contains("Technomancer")>
         <state type="number">1</state>
         <#else>
         <state type="number">0</state>
         </#if>
         </@loop>
         </#if>
         <#if pcstring('SKILLSIT.${skill}')?contains("Stealth")>
         <#assign xSte = 1>
         <@loop from=0 to=pcvar('COUNT[CLASSES]-1') ; class , class_has_next>
         <#if pcstring('CLASS.${class}')?contains("Envoy")>
         <state type="number">1</state>
         <#elseif pcstring('CLASS.${class}')?contains("Operative")>
         <state type="number">1</state>
         <#elseif pcstring('CLASS.${class}')?contains("Solarian")>
         <state type="number">1</state>
         <#else>
         <state type="number">0</state>
         </#if>
         </@loop>
         </#if>
         <#if pcstring('SKILLSIT.${skill}')?contains("Survival")>
         <#assign xSur = 1>
         <@loop from=0 to=pcvar('COUNT[CLASSES]-1') ; class , class_has_next>
         <#if pcstring('CLASS.${class}')?contains("Mystic")>
         <state type="number">1</state>
         <#elseif pcstring('CLASS.${class}')?contains("Operative")>
         <state type="number">1</state>
         <#elseif pcstring('CLASS.${class}')?contains("Soldier")>
         <state type="number">1</state>
         <#else>
         <state type="number">0</state>
         </#if>
         </@loop>
         </#if>
         <#if pcstring('SKILLSIT.${skill}.ABILITY')?contains("STR")>
         <statname type="string">strength</statname>
         <#elseif pcstring('SKILLSIT.${skill}.ABILITY')?contains("DEX")>
         <statname type="string">dexterity</statname>
         <#elseif pcstring('SKILLSIT.${skill}.ABILITY')?contains("CON")>
         <statname type="string">constitution</statname>
         <#elseif pcstring('SKILLSIT.${skill}.ABILITY')?contains("INT")>
         <statname type="string">intelligence</statname>
         <#elseif pcstring('SKILLSIT.${skill}.ABILITY')?contains("WIS")>
         <statname type="string">wisdom</statname>
         <#elseif pcstring('SKILLSIT.${skill}.ABILITY')?contains("CHA")>
         <statname type="string">charisma</statname>
         </#if>
      </id-${(skill + 1)?left_pad(5, "0")}>
      </@loop>
      <#if xMed == 0>
      <#assign skillCount += 1>
      <id-${(skillCount)?left_pad(5, "0")}>
         <label type="string">Medicine</label>
         <ranks type="number">0</ranks>
         <total type="number">0</total>
         <stat type="number">0</stat>
         <@loop from=0 to=pcvar('COUNT[CLASSES]-1') ; class , class_has_next>
         <#if pcstring('CLASS.${class}')?contains("Envoy")>
         <state type="number">1</state>
         <#elseif pcstring('CLASS.${class}')?contains("Mechanic")>
         <state type="number">1</state>
         <#elseif pcstring('CLASS.${class}')?contains("Mystic")>
         <state type="number">1</state>
         <#elseif pcstring('CLASS.${class}')?contains("Operative")>
         <state type="number">1</state>
         <#elseif pcstring('CLASS.${class}')?contains("Soldier")>
         <state type="number">1</state>
         <#else>
         <state type="number">0</state>
         </#if>
         </@loop>
      </id-${(skillCount)?left_pad(5, "0")}>
      </#if>
      <#if xCom == 0>
      <#assign skillCount += 1>
      <id-${(skillCount)?left_pad(5, "0")}>
         <label type="string">Computers</label>
         <ranks type="number">0</ranks>
         <total type="number">0</total>
         <stat type="number">0</stat>
         <@loop from=0 to=pcvar('COUNT[CLASSES]-1') ; class , class_has_next>
         <#if pcstring('CLASS.${class}')?contains("Envoy")>
         <state type="number">1</state>
         <#elseif pcstring('CLASS.${class}')?contains("Mechanic")>
         <state type="number">1</state>
         <#elseif pcstring('CLASS.${class}')?contains("Operative")>
         <state type="number">1</state>
         <#elseif pcstring('CLASS.${class}')?contains("Technomancer")>
         <state type="number">1</state>
         <#else>
         <state type="number">0</state>
         </#if>
         </@loop>
      </id-${(skillCount)?left_pad(5, "0")}>
      </#if>
      <#if xCul == 0>
      <#assign skillCount += 1>
      <id-${(skillCount)?left_pad(5, "0")}>
         <label type="string">Culture</label>
         <ranks type="number">0</ranks>
         <total type="number">0</total>
         <stat type="number">0</stat>
         <@loop from=0 to=pcvar('COUNT[CLASSES]-1') ; class , class_has_next>
         <#if pcstring('CLASS.${class}')?contains("Envoy")>
         <state type="number">1</state>
         <#elseif pcstring('CLASS.${class}')?contains("Mystic")>
         <state type="number">1</state>
         <#elseif pcstring('CLASS.${class}')?contains("Operative")>
         <state type="number">1</state>
         <#else>
         <state type="number">0</state>
         </#if>
         </@loop>
      </id-${(skillCount)?left_pad(5, "0")}>
      </#if>
      <#if xEng == 0>
      <#assign skillCount += 1>
      <id-${(skillCount)?left_pad(5, "0")}>
         <label type="string">Engineering</label>
         <ranks type="number">0</ranks>
         <total type="number">0</total>
         <stat type="number">0</stat>
         <@loop from=0 to=pcvar('COUNT[CLASSES]-1') ; class , class_has_next>
         <#if pcstring('CLASS.${class}')?contains("Envoy")>
         <state type="number">1</state>
         <#elseif pcstring('CLASS.${class}')?contains("Mechanic")>
         <state type="number">1</state>
         <#elseif pcstring('CLASS.${class}')?contains("Ooperative")>
         <state type="number">1</state>
         <#elseif pcstring('CLASS.${class}')?contains("Soldier")>
         <state type="number">1</state>
         <#elseif pcstring('CLASS.${class}')?contains("Technomancer")>
         <state type="number">1</state>
         <#else>
         <state type="number">0</state>
         </#if>
         </@loop>
      </id-${(skillCount)?left_pad(5, "0")}>
      </#if>
      <#if xLif == 0>
      <#assign skillCount += 1>
      <id-${(skillCount)?left_pad(5, "0")}>
         <label type="string">Life Science</label>
         <ranks type="number">0</ranks>
         <total type="number">0</total>
         <stat type="number">0</stat>
         <@loop from=0 to=pcvar('COUNT[CLASSES]-1') ; class , class_has_next>
         <#if pcstring('CLASS.${class}')?contains("Mystic")>
         <state type="number">1</state>
         <#elseif pcstring('CLASS.${class}')?contains("Technomancer")>
         <state type="number">1</state>
         <#else>
         <state type="number">0</state>
         </#if>
         </@loop>
      </id-${(skillCount)?left_pad(5, "0")}>
      </#if>
      <#if xMys == 0>
      <#assign skillCount += 1>
      <id-${(skillCount)?left_pad(5, "0")}>
         <label type="string">Mysticism</label>
         <ranks type="number">0</ranks>
         <total type="number">0</total>
         <stat type="number">0</stat>
         <@loop from=0 to=pcvar('COUNT[CLASSES]-1') ; class , class_has_next>
         <#if pcstring('CLASS.${class}')?contains("Mystic")>
         <state type="number">1</state>
         <#elseif pcstring('CLASS.${class}')?contains("Solarian")>
         <state type="number">1</state>
         <#elseif pcstring('CLASS.${class}')?contains("Technomancer")>
         <state type="number">1</state>
         <#else>
         <state type="number">0</state>
         </#if>
         </@loop>
      </id-${(skillCount)?left_pad(5, "0")}>
      </#if>
      <#if xPhy == 0>
      <#assign skillCount += 1>
      <id-${(skillCount)?left_pad(5, "0")}>
         <label type="string">Physical Science</label>
         <ranks type="number">0</ranks>
         <total type="number">0</total>
         <stat type="number">0</stat>
         <@loop from=0 to=pcvar('COUNT[CLASSES]-1') ; class , class_has_next>
         <#if pcstring('CLASS.${class}')?contains("Mechanic")>
         <state type="number">1</state>
         <#elseif pcstring('CLASS.${class}')?contains("Solarian")>
         <state type="number">1</state>
         <#elseif pcstring('CLASS.${class}')?contains("Technomancer")>
         <state type="number">1</state>
         <#else>
         <state type="number">0</state>
         </#if>
         </@loop>
      </id-${(skillCount)?left_pad(5, "0")}>
      </#if>
      <#if xSle == 0>
      <#assign skillCount += 1>
      <id-${(skillCount)?left_pad(5, "0")}>
         <label type="string">Sleight of Hand</label>
         <ranks type="number">0</ranks>
         <total type="number">0</total>
         <stat type="number">0</stat>
         <@loop from=0 to=pcvar('COUNT[CLASSES]-1') ; class , class_has_next>
         <#if pcstring('CLASS.${class}')?contains("Envoy")>
         <state type="number">1</state>
         <#elseif pcstring('CLASS.${class}')?contains("Operative")>
         <state type="number">1</state>
         <#elseif pcstring('CLASS.${class}')?contains("Technomancer")>
         <state type="number">1</state>
         <#else>
         <state type="number">0</state>
         </#if>
         </@loop>
      </id-${(skillCount)?left_pad(5, "0")}>
      </#if>
      </skilllist>
      <featlist>
      <@loop from=0 to=pcvar('COUNT[FEATSALL.ALL]-1') ; feat , feat_has_next>
      <id-${(feat + 1)?left_pad(5, "0")}>
         <name type="string">${pcstring('FEATALL.${feat}')}</name>
         <summary type="formattedtext">
            <p>${pcstring('FEATALL.${feat}.DESC')}</p>
			   </summary>
         <benefit type="formattedtext">
				    <p>${pcstring('FEATALL.${feat}.BENEFIT')?keep_before("Normal")?replace("<para>", "")?replace("</para>", "")}</p>
			   </benefit>
         <normal type="formattedtext">
				    <p>${pcstring('FEATALL.${feat}.BENEFIT')?keep_after("Normal: ")?replace("<para>", "")?replace("</para>", "")}</p>
			   </normal>
         <special type="formattedtext">
				    <p>${pcstring('FEATALL.${feat}.BENEFIT')?keep_after("Special: ")?replace("<para>", "")?replace("</para>", "")}</p>
			   </special>
         <type type="string">${pcstring('FEATALL.${feat}.TYPE')?lower_case?cap_first}</type>
      </id-${(feat + 1)?left_pad(5, "0")}>
      </@loop>
      </featlist>
      <hp>
			  <total type="number">${pcstring('HP')}</total>
		  </hp>
      <homeworld type="string">${pcstring('ABILITYALL.Internal.VISIBLE.0.TYPE=Home Planet')}</homeworld>
      
      
      
   </character>
</root>
