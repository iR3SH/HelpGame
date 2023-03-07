package org.starloco.locos.fight.ia.type;

import java.util.ArrayList;

import org.starloco.locos.common.PathFinding;
import org.starloco.locos.fight.Fight;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.fight.ia.AbstractNeedSpell;
import org.starloco.locos.fight.ia.util.Function;
import org.starloco.locos.fight.spells.Spell;
import org.starloco.locos.fight.spells.SpellEffect;

// IA Drogonnet Rouge adapted/improved

public class IA2 extends AbstractNeedSpell
{
  private byte attack=0;
  private ArrayList<Fighter> attacked=new ArrayList<Fighter>();
  private ArrayList<Fighter> tryAttacked=new ArrayList<Fighter>();
  Spell.SortStats dragofire=this.fighter.getMob().getSpells().get(477);
  private byte movedFar=0;
  private byte movedEnFace=0;

  public IA2(Fight fight, Fighter fighter, byte count)
  {
    super(fight,fighter,count);
  }

  @Override
  public void apply()
  {
    if(!this.stop&&this.fighter.canPlay()&&this.count>0)
    {
      int time=100,maxPo=1;
      boolean action=false;
      Fighter dispellTarget=Function.getInstance().getNearestEnnemy(this.fight,this.fighter);
      Fighter dragoFireTarget=Function.getInstance().getNearestEnnemyNotListedLos(this.fight,this.fighter,attacked);
      ArrayList<Fighter> targets=fight.getFighters(this.fighter.getOtherTeam());

      Fighter A=Function.getInstance().getSummoner(fight,this.fighter,63);

      for(Spell.SortStats spellStats : this.highests)
        if(spellStats.getMaxPO()>maxPo)
          maxPo=spellStats.getMaxPO();

      int enemyBuffInfluence=0;
      if(dispellTarget!=null)
      {
        ArrayList<SpellEffect> buffs=new ArrayList<SpellEffect>();
        buffs.addAll(dispellTarget.getFightBuff());
        for(SpellEffect SE : buffs)
          enemyBuffInfluence+=Function.getInstance().calculInfluence(SE,this.fighter,dispellTarget);
      }

      int summonerBuffInfluence=0;
      if(A!=null)
      {
        ArrayList<SpellEffect> buffs=new ArrayList<SpellEffect>();
        buffs.addAll(A.getFightBuff());
        for(SpellEffect SE : buffs)
          summonerBuffInfluence+=Function.getInstance().calculInfluence(SE,this.fighter,A);
      }

      if(dispellTarget!=null)
        if(dispellTarget.isHide())
          dispellTarget=null;
      if(dragoFireTarget!=null)
        if(dragoFireTarget.isHide())
          dragoFireTarget=null;

      if(summonerBuffInfluence<=-200)
      {
        if(this.fighter.getCurPa(this.fight)>0) //dispell summoner
        {
          if(Function.getInstance().buffIfPossible(this.fight,this.fighter,A,this.buffs))
          {
            this.attack++;
            time=1000;
            action=true;
          }
        }

        if(this.fighter.getCurPm(this.fight)>0&&!action) //move to summoner
        {
          int value=Function.getInstance().moveautourIfPossible(this.fight,this.fighter,A);
          if(value!=0)
          {
            tryAttacked.clear();
            time=value;
            action=true;
          }
        }
      }
      else if(enemyBuffInfluence<=-200)
      {
        if(this.fighter.getCurPa(this.fight)>0&&!action&&dispellTarget!=null) //dispell enemy
        {
          if(Function.getInstance().buffIfPossible(this.fight,this.fighter,dispellTarget,this.buffs))
          {
            this.attack++;
            time=1000;
            action=true;
          }
        }

        if(this.fighter.getCurPm(this.fight)>0&&!action) //move to summoner
        {
          int value=Function.getInstance().moveautourIfPossible(this.fight,this.fighter,dispellTarget);
          if(value!=0)
          {
            tryAttacked.clear();
            time=value;
            action=true;
          }
        }
      }

      if(!action&&this.fighter.getCurPa(this.fight)>=dragofire.getPACost()) //dragofire if possible
      {
        for(Fighter f : targets)
          if(!tryAttacked.contains(f)&&!attacked.contains(f)&&!f.isDead())
          {
        	  boolean fire = false;
          // Si pas de ligne de vue on fait le tour
          if(!PathFinding.checkLoS(fight.getMap(), fighter.getCell().getId(), f.getCell().getId(), f))
        	{
        	  Function.getInstance().moveautourIfPossible(this.fight,this.fighter,dispellTarget);
        	}
            fire=fight.canCastSpell2(fighter,dragofire,fighter.getCell(),f.getCell());
            if(fire)
            {
              if(this.fight.tryCastSpell(this.fighter,dragofire,f.getCell().getId())==0)
              {
                this.attacked.add(f);
                this.attack++;
                time=dragofire.getSpell().getDuration();
                action=true;
              }
              else
                time=1500;
              tryAttacked.clear();
              break;
            }
            else
              tryAttacked.add(f);
          }
      }

      if(this.fighter.getCurPm(this.fight)>0&&this.fighter.getCurPa(this.fight)>=dragofire.getPACost()&&!action) //move into line
      {
        for(Fighter f : targets)
          if(!f.isDead())
          {
        	if(movedEnFace==0)
        	{
        		int value=Function.getInstance().moveenfaceIfPossible(this.fight,this.fighter,f,maxPo+1);
        		if(value!=0)
        		{
        			System.out.println("moved into line");
        			tryAttacked.clear();
        			time=value+500;
        			action=true;
        			break;
        		}
        	}
          }
        movedEnFace++;
      }

      if(this.fighter.getCurPm(this.fight)>0&&dragoFireTarget!=null&&this.fighter.getCurPa(this.fight)>=dragofire.getPACost()&&!action) //move close if blocked
      {
        int value=Function.getInstance().moveautourIfPossible(this.fight,this.fighter,dragoFireTarget);
        if(value!=0)
        {
          System.out.println("moved close");
          tryAttacked.clear();
          time=value+500;
          action=true;
        }
      }

      if(this.fighter.getCurPm(this.fight)>0&&this.attack>0&&!action)
      {
    	if(movedFar==0)
    	{
        int value=Function.getInstance().moveFarIfPossible(this.fight,this.fighter);
        movedFar++;
        if(value!=0)
          time=value+1500;
    	}
      }

      if(this.fighter.getCurPa(this.fight)==0&&this.fighter.getCurPm(this.fight)==0)
        this.stop=true;
      addNext(this::decrementCount,time);
    }
    else
    {
      this.stop=true;
    }
  }
}