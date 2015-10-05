package bunzosteele.heroesemblem.model.Units;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;

import bunzosteele.heroesemblem.model.AiHelper;
import bunzosteele.heroesemblem.model.BattleState;
import bunzosteele.heroesemblem.model.CombatHelper;
import bunzosteele.heroesemblem.model.Battlefield.Tile;
import bunzosteele.heroesemblem.model.Units.Abilities.Heal;
import bunzosteele.heroesemblem.model.Units.Abilities.Rebirth;
import bunzosteele.heroesemblem.model.Units.Abilities.Scholar;

public class Priest extends Unit
{
	public Priest(final int team, final String name, final int attack, final int defense, final int evasion, final int accuracy, final int movement, final int maximumHealth, final int maximumRange, final int minimumRange, final int ability, final int cost, final int id) throws IOException
	{
		super(team, name, attack, defense, evasion, accuracy, movement, maximumHealth, maximumRange, minimumRange, cost, id);
		this.type = UnitType.Priest;
		if (ability == 1)
		{
			this.ability = new Rebirth();
		} else if (ability == 2)
		{
			this.ability = new Scholar();
		} else
		{
			this.ability = new Heal();
		}
	}

	@Override
	public int GetTileScore(Tile tile, BattleState state)
	{
		int score = 0;
		int costToCombat = AiHelper.GetCostToCombat(tile, state, this);
		score += costToCombat;
		if(costToCombat == 0){
			HashSet<Unit> attackableUnits = CombatHelper.GetAttackableTargets(tile.x, tile.y, this, state);
			if(attackableUnits.size() > 0){
				score += 20;
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
				score -= 20;
			}else{
				score -= 10;
			}
		}
		
		for(Unit ally : state.CurrentPlayerUnits()){
			Tile allyTile = state.GetTileForUnit(ally);
			if((Math.abs(tile.x - allyTile.x) + Math.abs(tile.y - allyTile.y)) == 1){
				score += 10;
				if((ally.maximumHealth - ally.currentHealth) > 0 ){
					score += (ally.maximumHealth - ally.currentHealth) * 10;
				}
			}
		}
		
		score += tile.defenseModifier * 5;
		score += tile.accuracyModifier;

		return score;
	}
}
