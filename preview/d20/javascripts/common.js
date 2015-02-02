function rollDice(numDice,dieSize){
	var result=0;
	for (dieCount=0; dieCount<numDice; dieCount++) {
		result = result + (Math.round(Math.random() * dieSize) % dieSize + 1);
	}
	return result;
}
function roll(numDice,dieSize,bonus,caption) {
	result = rollDice(numDice,dieSize);

	var totalResult =0;
	totalResult += minOne(result + bonus);
	var msg = caption + "Result";
	msg += ": " + result + processBonus( bonus) + "="+ totalResult;

	alert(msg);
}
function processBonus(damage)
{
	if (damage >= 0)
	{
		return "+" + damage;
	}
	else
	{
		return damage;
	}
}
function minOne(roll)
{
	if (roll <= 0)
	{
		return parseInt(1);
	}
	return roll;
}
function rollAttack(toHit,damage,crit,mult) {
	var attackValues = toHit.split("/");
	var dieRoll = 0;
	var critVals = crit.split("-");
	var msg = "Attack Result:\n";

	for (i = 0; i < attackValues.length; i++)
	{
		dieRoll = (Math.round(Math.random() * 20) % 20 + 1);
		//dieRoll = 20;
		var critDiceRole = 0;
		var rollTotal =0;
		
		msg += "     Attack " + (i+1)+ ": " + parseInt(dieRoll) + processBonus( parseInt(attackValues[i])) + "=" + minOne(parseInt(parseInt(dieRoll)+parseInt(attackValues[i])));
		
		var totalDamage = 0;
		var baseDamage  =0;
		var dmgSplit = damage.split("d");
		var dice = dmgSplit[0];
		var remainder = dmgSplit[1];
		var modDamage =0;
		var critBaseDamage;
		var critTotalDamage;
		var dieSize = 0;
		var bonusDmg = 0;

		if (remainder.indexOf("+")!=-1)
		{
			var bonusSplit = remainder.split("+");
			dieSize 	= bonusSplit[0];
			bonusDmg 	= bonusSplit[1];
		}
		else if(remainder.indexOf("-")!=-1)
		{
			var bonusSplit = remainder.split("-");
			dieSize 	= bonusSplit[0];
			bonusDmg 	= parseInt(-bonusSplit[1]);
		}
		else
		{
			dieSize = remainder;
		}

		baseDamage = rollDice(dice,dieSize);
		modDamage = bonusDmg;
		totalDamage = minOne((parseInt(baseDamage) + parseInt(modDamage)));
		msg += "    Damage: " + baseDamage +  processBonus(modDamage) +  "=" + totalDamage + " \n";

		if (dieRoll >= critVals[0])
		{
			var fullDamage =0;
			var critbaseDamage =  rollDice(parseInt(mult)-1 ,dieSize) ;
			critDiceRole =  (Math.round(Math.random() * 20) % 20 + 1);

			critmodDamage = parseInt(bonusDmg) * (parseInt(mult)-1 );
			critTotalDamage =  critbaseDamage  +  critmodDamage ;
			
			fullDamage = minOne(critTotalDamage + totalDamage);
			


			msg += "     Critical Attack " + (i+1) + ": " + critDiceRole  + processBonus(parseInt(attackValues[i])) + "=" + minOne(parseInt(critDiceRole)+parseInt(attackValues[i]));
			msg += "     Critical Damage: " + parseInt(critbaseDamage) + processBonus(critmodDamage )  +  "=" + critTotalDamage + " \n";
			msg += "     Damage Total: " + totalDamage +  processBonus( critTotalDamage) +  "=" + fullDamage  + " \n";
		}
		if(i+1 < attackValues.length)
		{
			msg += "-----------------------------------------------------------------------------\n";
		}
	}
	alert(msg);
}
function rollTurning(checkBonus,turnDice,turnDieSize,turnBonus,turnLvl,caption) {
	var checkResult = rollDice(1,20,checkBonus);
	var maxHD = 0;
	if (checkResult <= 0) {
		maxHD = turnLvl - 4;
	} else if (checkResult <= 3) {
		maxHD = turnLvl - 3;
	} else if (checkResult <= 6) {
		maxHD = turnLvl - 2;
	} else if (checkResult <= 9) {
		maxHD = turnLvl - 1;
	} else if (checkResult <= 12) {
		maxHD = turnLvl;
	} else if (checkResult <= 15) {
		maxHD = turnLvl + 1;
	} else if (checkResult <= 18) {
		maxHD = turnLvl + 2;
	} else if (checkResult <= 21) {
		maxHD = turnLvl + 3;
	} else {
		maxHD = turnLvl + 4;
	}
	var totalHD = rollDice(turnDice,turnDieSize,turnBonus);
	var msg = caption;
	msg += ": " + totalHD + " total HD (max " + maxHD + " HD)";
	if (turnLvl / 2 >= 1) {
		msg += "\n\t(Destroy " + turnLvl/2 + " HD or lower)";
	}
	alert(msg);
}
function displaySpellInfo(name,school,subschool,descriptors,levels,comps,castTime, range,targetArea,duration,save,spellRes,desc) {
	var buf = "<b>" + name + "</b><br/> " + school;
	if (subschool != null && subschool.length > 0) {
		buf += " " + subschool;
	}
	if (descriptors != null && descriptors.length > 0) {
		buf += "[" + descriptors + "]";
	}
	buf += "<br />";
	buf += "<b>Level:</b> " + levels + "<br />";
	buf += "<b>Components:</b> " + comps + "<br />";
	buf += "<b>Casting Time:</b> " + castTime + "<br />";
	buf += "<b>Range:</b> " + range + "<br />";
	buf += "<b>Effect:</b> " + targetArea + "<br />";
	buf += "<b>Duration:</b> " + duration + "<br />";
	buf += "<b>Saving Throw:</b> " + save + "<br />";
	buf += "<b>Spell Resistance:</b> " + spellRes + "<br />";
	buf += "<br />" + desc;
	document.getElementById("spellDisplay").innerHTML=buf;
}