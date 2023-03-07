package org.starloco.locos.fight.ia.type;

//import java.util.ArrayList;

import org.starloco.locos.common.PathFinding;
import org.starloco.locos.fight.Fight;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.fight.ia.AbstractNeedSpell;
import org.starloco.locos.fight.ia.util.Function;
import org.starloco.locos.fight.spells.Spell;
//import org.starloco.locos.fight.spells.Spell.SortStats;
//import org.starloco.locos.fight.spells.SpellEffect;
//Based on IA 65 Dopeul Cra
public class IA103 extends AbstractNeedSpell
{
  private byte attack=0;
  private byte move=0;
  private byte hasMovedFar=0;
  private byte hasMovedDiag=0;

  public IA103(Fight fight, Fighter fighter, byte count)
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

	      for(Spell.SortStats spellStats : this.highests)
	        if(spellStats.getMaxPO()>maxPo)
	          maxPo=spellStats.getMaxPO();
	      Fighter ennemy=Function.getInstance().getNearestEnnemy(this.fight,this.fighter);
	      Fighter C=Function.getInstance().getNearestEnnemynbrcasemax(this.fight,this.fighter,0,maxPo+1);//po max+ 1;
	      Fighter A=Function.getInstance().getNearestAminoinvocnbrcasemax(this.fight,this.fighter,0,4);// pomax +1; 0 pour détecter correctement
	      if(C!=null&&C.isHide())
	        C=null;
	      //** edit C==null replace with !PathFinding.checkLoS(fight.getMap(), fighter.getCell().getId(), C.getCell().getId(), C)
	      if(this.fighter.getCurPa(this.fight)>0&&!action&&A!=null)
	      {
	    	//Function.getInstance().movediagIfPossible(this.fight,this.fighter,A);	
    		// Évite les déplacement inutiles vers l'invocateur quand aucun sort n'est disponible
    		if(Function.getInstance().checkIfBuffAvailable(this.fight,this.fighter,this.fighter,this.buffs)&&!PathFinding.isCACwithEnnemy(this.fighter,ennemy)) // Vérification de la disponibilité sur soi-même
    				{
				    	Function.getInstance().moveautourIfPossible(this.fight,this.fighter,A); // se place en face
				    	move++;
    				}
	        if(Function.getInstance().buffIfPossible(this.fight,this.fighter,A,this.buffs))
	        {
	          time=1000;
	          action=true;
	        }
	        //Test Heal (impact pas le féca)
	        /*
	        if(Function.getInstance().HealIfPossible(this.fight,this.fighter,true))
	        {
	          time=1000;
	          action=true;
	        }
	        */
	      }
	      if(this.fighter.getCurPm(this.fight)>0&&C==null&&this.attack==0)
	      {
	    	int value=0;
	        value=Function.getInstance().movediagIfPossible(this.fight,this.fighter,ennemy);
	        if(value!=0)
	        {
	          time=value;
	          //action=true;
	          C=Function.getInstance().getNearestEnnemynbrcasemax(this.fight,this.fighter,0,maxPo+1);
	        }
	      }
	      if(this.fighter.getCurPa(this.fight)>0&&!action)
	      {
	        if(Function.getInstance().invocIfPossible(this.fight,this.fighter,this.invocations))
	        {
	          time=600;
	          action=true;
	        }
	      }
	      if(this.fighter.getCurPa(this.fight)>0&&!action)
	      {
	        if(Function.getInstance().buffIfPossible(this.fight,this.fighter,this.fighter,this.buffs))
	        {
	          time=400;
	          action=true;
	        }
	      }
	      if(this.fighter.getCurPa(this.fight)>0&&C!=null&&!action)
	      {
	    	 //insert condition on Los
	    	  int value=-1;
	    	  //int mapt=(int) fight.getMap().getId();
	    	  //int celltf=fighter.getCell().getId();
	    	  //int celltc=C.getCell().getId();
	    	  //int idCible = C.getId();
	    	  if(PathFinding.checkLoS(fight.getMap(), fighter.getCell().getId(), C.getCell().getId(), C))
	    	  {
	    		 for(int i=0; i<3; i++) // Attaque 3 fois de suite
	    		 {
	    			 value=Function.getInstance().attackIfPossible(this.fight,this.fighter,this.highests);
	    		 }
	    	  }
	    	  else {
	    		  value=Function.getInstance().movediagIfPossible(this.fight,this.fighter,ennemy);
	    		  move++;}
	        if(value!=-1)
	        {
	          time=value;
	          action=true;
	          this.attack++;
	        } else if(this.fighter.getCurPm(this.fight)>0&&this.attack==0&&move==0)
	        {
	          if(hasMovedDiag==0)
	          {
		          value=Function.getInstance().movediagIfPossible(this.fight,this.fighter,ennemy);
		          hasMovedDiag++;
	          }
	          if(value!=0)
	          {
	            time=value;
	            action=true;
	            Function.getInstance().getNearestEnnemynbrcasemax(this.fight,this.fighter,0,maxPo+1);
	          }
	        }
	      }
	      if(this.fighter.getCurPm(this.fight)>0&&!action&&this.attack>0)
	      {
	    	if(hasMovedFar==0)
	    	{
		        int value=Function.getInstance().moveFarIfPossible(this.fight,this.fighter);
		        hasMovedFar++;
		        if(value!=0)
		          time=value;
	    	}
	      }

	      if(this.fighter.getCurPa(this.fight)==0&&this.fighter.getCurPm(this.fight)==0)
	        this.stop=true;
	      addNext(this::decrementCount,time);
	    } else
	    {
	      this.stop=true;
	    }
	  }
}