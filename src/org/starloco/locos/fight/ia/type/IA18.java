package org.starloco.locos.fight.ia.type;

import org.starloco.locos.common.SocketManager;
import org.starloco.locos.fight.Fight;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.fight.ia.AbstractIA;
import org.starloco.locos.fight.ia.util.Function;
import org.starloco.locos.kernel.Constant;

public class IA18 extends AbstractIA
{
  boolean kPair=false, kImpair=false, hasGlyphed=false;

  public IA18(Fight fight, Fighter fighter, byte count)
  {
    super(fight,fighter,count);
  }

  @Override
  public void apply()
  {
    if(!this.stop&&this.fighter.canPlay()&&this.count>0)
    {
      Fighter kimbo=null;

      for(Fighter fighter : this.fight.getFighters(3))
      {
        if(fighter.getMob()!=null)
        {
          if(fighter.getMob().getTemplate().getId()==1045)
          {
            if(fighter.haveState(Constant.ITEM_TYPE_MALEDICTION))
            {
              fighter.setState(Constant.ITEM_TYPE_MALEDICTION,0,this.fighter.getId());
              kPair=true;
              this.fighter.setState(Constant.ITEM_TYPE_MALEDICTION,0,this.fighter.getId());
              this.fighter.setState(Constant.ITEM_TYPE_MALEDICTION,1,this.fighter.getId());
            }
            if(fighter.haveState(Constant.ITEM_TYPE_BENEDICTION))
            {
              fighter.setState(Constant.ITEM_TYPE_BENEDICTION,0,this.fighter.getId());
              kImpair=true;
              this.fighter.setState(Constant.ITEM_TYPE_BENEDICTION,0,this.fighter.getId());
              this.fighter.setState(Constant.ITEM_TYPE_BENEDICTION,1,this.fighter.getId());
            }
            kimbo=fighter;
          }
        }
      }

      if(kPair&&!hasGlyphed)
      {
        int attack=Function.getInstance().attackIfPossibleDisciplepair(this.fight,this.fighter,kimbo);

        if(attack!=0)
        {
          hasGlyphed=true;
          this.fight.getAllGlyphs().stream().filter(entry -> entry.getCell().getId()==this.fighter.getCell().getId()).forEach(entry -> {
            this.fighter.addBuff(128,1,1,1,true,3500,"",this.fighter,true);
            SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this.fight,7,78,this.fighter.getId()+"",this.fighter.getId()+","+1+","+1);
          });
          Function.getInstance().moveFarIfPossible(this.fight,this.fighter);
        }
      }
      else if(kImpair&&!hasGlyphed)
      {
        int attack=Function.getInstance().attackIfPossibleDiscipleimpair(this.fight,this.fighter,kimbo);

        if(attack!=0)
        {
          hasGlyphed=true;
          this.fight.getAllGlyphs().stream().filter(entry -> entry.getCell().getId()==this.fighter.getCell().getId()).forEach(entry -> {
            this.fighter.addBuff(128,1,1,1,true,3501,"",this.fighter,true);
            SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this.fight,7,78,this.fighter.getId()+"",this.fighter.getId()+","+1+","+1);
          });
          Function.getInstance().moveFarIfPossible(this.fight,this.fighter);
        }
      }
      else
      {
        this.stop=true;
      }

      addNext(this::decrementCount,1000);
    }
    else
    {
      this.stop=true;
    }
  }
}