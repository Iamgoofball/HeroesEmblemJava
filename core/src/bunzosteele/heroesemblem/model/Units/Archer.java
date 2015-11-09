package bunzosteele.heroesemblem.model.Units;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;

import bunzosteele.heroesemblem.model.AiHelper;
import bunzosteele.heroesemblem.model.BattleState;
import bunzosteele.heroesemblem.model.CombatHelper;
import bunzosteele.heroesemblem.model.MovementHelper;
import bunzosteele.heroesemblem.model.Battlefield.Tile;
import bunzosteele.heroesemblem.model.Units.Abilities.PowerShot;
import bunzosteele.heroesemblem.model.Units.Abilities.Snipe;

public class Archer extends Unit
{
	public Archer(final int team, final String name, final int attack, final int defense, final int evasion, final int accuracy, final int movement, final int maximumHealth, final int maximumRange, final int minimumRange, final int ability, final int cost, final int id, final float gameSpeed, final boolean isMale, final String backStory) throws IOException
	{
		super(team, name, attack, defense, evasion, accuracy, movement, maximumHealth, maximumRange, minimumRange, cost, id, gameSpeed, isMale, backStory);
		this.type = UnitType.Archer;
		if (ability == 1)
		{
			this.ability = new PowerShot();
		} else if (ability == 2)
		{
			this.ability = new Snipe();
		} else
		{
			this.ability = null;
		}
	}

	@Override
	public int GetTileScore(Tile tile, BattleState state)
	{
		int score = 0;
		int costToCombat = AiHelper.GetCostToCombat(tile, state, this);
		score += (800 - costToCombat * 8);
		if(costToCombat == 0){
			HashSet<Unit> attackableUnits = CombatHelper.GetAttackableTargets(tile.x, tile.y, this, state);
			if(attackableUnits.size() > 0){
				score += 50;
				score += this.attack;
				for(Unit unit : attackableUnits){
					score += 10 - state.GetTileForUnit(unit).defenseModifier;
					if(unit.currentHealth + state.GetTileForUnit(unit).defenseModifier <= this.attack){
						score += 30;
					}					
				}
			}
		}
		
		HashSet<Unit> threateningUnits = AiHelper.GetUnitsThatCanAttackTile(state, tile);
		for(Unit unit : threateningUnits){
			if(this.currentHealth / (float) this.maximumHealth <= .5){
				score -= 15;
			}else{
				score -= 10;
			}
		}
		
		score += tile.defenseModifier * 5;
		score += tile.evasionModifier;
		score += tile.altitude * 10;

		return score;
	}
}
