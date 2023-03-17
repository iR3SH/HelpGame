package org.starloco.locos.fight.ia.util;

import org.starloco.locos.area.map.GameCase;
import org.starloco.locos.area.map.GameMap;
import org.starloco.locos.common.Formulas;
import org.starloco.locos.common.PathFinding;
import org.starloco.locos.common.SocketManager;
import org.starloco.locos.fight.Fight;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.fight.spells.LaunchedSpell;
import org.starloco.locos.fight.spells.Spell;
import org.starloco.locos.fight.spells.Spell.SortStats;
import org.starloco.locos.fight.spells.SpellEffect;
import org.starloco.locos.fight.traps.Glyph;
import org.starloco.locos.game.action.GameAction;
import org.starloco.locos.game.world.World;
import org.starloco.locos.kernel.Constant;
//import org.starloco.locos.fight.ia.util.AstarPathfinding;
//import org.starloco.locos.kernel.Main;
//import org.starloco.locos.fight.ia.util.AstarPathfinding;
import org.starloco.locos.kernel.Config;
import org.starloco.locos.kernel.Logging;

import java.util.*;

public class Function {
	
	
	private final static Function instance=new Function();

	  public static Function getInstance()
	  {
	    return instance;
	  }

	  public int attackIfPossiblerat(Fight fight, Fighter fighter, Fighter target, boolean loin)// 0 = Rien, 5 = EC, 666 = NULL, 10 = SpellNull ou ActionEnCour ou Can'tCastSpell, 0 = AttaqueOK
	  {
	    if(fight==null||fighter==null)
	      return 0;
	    SortStats SS=null;
	    for(Map.Entry<Integer, SortStats> entry : fighter.getMob().getSpells().entrySet())
	    {
	      SortStats a=entry.getValue();
	      if(a.getSpellID()==489&&loin)
	        SS=a;
	      if(a.getSpellID()==646&&!loin)
	        SS=a;
	    }
	    if(target==null)
	      return 666;
	    int attack=fight.tryCastSpell(fighter,SS,target.getCell().getId());
	    if(attack!=0)
	      return attack;
	    return 0;
	  }

    public boolean TPIfPossiblesphinctercell(Fight fight, Fighter fighter, Fighter target)// 0 = Rien, 5 = EC, 666 = NULL, 10 = SpellNull ou ActionEnCour ou Can'tCastSpell, 0 = AttaqueOK
    {
      if(fight==null||fighter==null||target==null)
        return false;

      SortStats spell=null;
      for(SortStats s : fighter.getMob().getSpells().values())
      {
        if(s.getSpellID()==1016)
          spell=s;
      }

      if(spell!=null)
      {
        int cell=getMaxCellForTP(fight,fighter,target,spell.getMaxPO());
        if(fight.canCastSpell1(fighter,spell,fight.getMap().getCase(cell),-1))
        {
          fight.tryCastSpell(fighter,spell,cell);
          return true;
        }
        else
        {
          byte count=0;
          List<Integer> cells=new ArrayList<>();

          while(count!=4)
          {
            int nearestCell=PathFinding.getAvailableCellArround(fight,target.getCell().getId(),cells);

            if(nearestCell==0)
              break;
            if(fight.canCastSpell1(fighter,spell,fight.getMap().getCase(nearestCell),-1))
            {
              fight.tryCastSpell(fighter,spell,nearestCell);
              return true;
            }
            else
            {
              cells.add(nearestCell);
            }
            count++;
          }
        }

      }

      return false;
    }

    public int attackIfPossiblesphinctercell(Fight fight, Fighter fighter, Fighter target)// 0 = Rien, 5 = EC, 666 = NULL, 10 = SpellNull ou ActionEnCour ou Can'tCastSpell, 0 = AttaqueOK
    {
      if(fight==null||fighter==null)
        return -1;
      SortStats SS=null;
      for(Map.Entry<Integer, SortStats> entry : fighter.getMob().getSpells().entrySet())
      {
        SortStats a=entry.getValue();
        if(a.getSpellID()==1017)
          SS=a;
      }
      if(target==null)
        return 666;
      int attack=fight.tryCastSpell(fighter,SS,target.getCell().getId());
      if(attack!=0)
        return attack;
      return 0;
    }

    public boolean tryTurtleInvocation(Fight fight, Fighter fighter)
    {
      if(fight==null||fighter==null)
        return false;

      SortStats spell=null;
      for(SortStats s : fighter.getMob().getSpells().values())
      {
        if(s.getSpellID()==1018)
        {
          spell=s;
          break;
        }
      }

      if(spell!=null)
      {
        for(Fighter target : fight.getFighters(3))
        {
          if(target.getTeam()==fighter.getTeam())
            continue;
          List<Integer> cells=new ArrayList<>();
          int nearestCell=PathFinding.getAvailableCellArround(fight,target.getCell().getId(),cells);

          if(nearestCell==0)
            break;
          if(fight.canCastSpell1(fighter,spell,fight.getMap().getCase(nearestCell),-1))
          {
            fight.tryCastSpell(fighter,spell,nearestCell);
            return true;
          }
          else
          {
            cells.add(nearestCell);
          }
        }
      }

      List<SortStats> spells=new ArrayList<>();
      spells.add(spell);
      return Function.instance.invocIfPossible(fight,fighter,spells);
    }

    public Fighter getNearest(Fight fight, Fighter fighter)
    {
      if(fight==null||fighter==null)
        return null;
      int dist=1000;
      Fighter curF=null;
      for(Fighter f : fight.getFighters(3))
      {
        if(f.isDead())
          continue;
        if(f==fighter)
          continue;
        int d=PathFinding.getDistanceBetween(fight.getMap(),fighter.getCell().getId(),f.getCell().getId());
        if(d<dist)
        {
          dist=d;
          curF=f;
        }
      }
      return curF;
    }

    public int attackIfPossibleAll(Fight fight, Fighter fighter, Fighter target)// 0 = Rien, 5 = EC, 666 = NULL, 10 = SpellNull ou ActionEnCour ou Can'tCastSpell, 0 = AttaqueOK
    {
      if(fight==null||fighter==null)
        return 0;
      SortStats SS=getBestSpellForTarget(fight,fighter,target,fighter.getCell().getId());
      if(target==null)
        return 0;
      int attack=fight.tryCastSpell(fighter,SS,target.getCell().getId());
      if(attack!=0)
        return attack;
      return 0;
    }

    public int moveToAttackIfPossibleAll(Fight fight, Fighter fighter)
    {
      if(fight==null||fighter==null)
        return -1;
      Fighter target=getNearest(fight,fighter);
      int distMin=PathFinding.getDistanceBetween(fight.getMap(),fighter.getCell().getId(),target.getCell().getId());
      ArrayList<SortStats> sorts=getLaunchableSort(fighter,fight,distMin);
      if(sorts==null)
        return -1;
      ArrayList<Integer> cells=PathFinding.getListCaseFromFighter(fight,fighter,fighter.getCell().getId(),sorts);
      if(cells==null)
        return -1;
      int CellDest=0;
      SortStats bestSS=null;
      int[] bestInvok= { 1000, 0, 0, 0, -1 };
      int[] bestFighter= { 1000, 0, 0, 0, -1 };
      int targetCell=-1;
      for(int i : cells)
      {
        for(SortStats S : sorts)
        {
          if(fight.canCastSpell1(fighter,S,target.getCell(),i))
          {
            int dist=PathFinding.getDistanceBetween(fight.getMapOld(),fighter.getCell().getId(),target.getCell().getId());
            if(!PathFinding.isNextTo(fighter.getFight().getMap(),fighter.getCell().getId(),target.getCell().getId()))
            {
              if(target.isInvocation())
              {
                if(dist<bestInvok[0])
                {
                  bestInvok[0]=dist;
                  bestInvok[1]=i;
                  bestInvok[2]=1;
                  bestInvok[3]=1;
                  bestInvok[4]=target.getCell().getId();
                  bestSS=S;
                }

              }
              else
              {
                if(dist<bestFighter[0])
                {
                  bestFighter[0]=dist;
                  bestFighter[1]=i;
                  bestFighter[2]=1;
                  bestFighter[3]=0;
                  bestFighter[4]=target.getCell().getId();
                  bestSS=S;
                }

              }
            }
            else
            {
              if(dist<bestFighter[0])
              {
                bestFighter[0]=dist;
                bestFighter[1]=i;
                bestFighter[2]=1;
                bestFighter[3]=0;
                bestFighter[4]=target.getCell().getId();
                bestSS=S;
              }
            }
          }
        }
      }
      if(bestFighter[1]!=0)
      {
        CellDest=bestFighter[1];
        targetCell=bestFighter[4];
      }
      else if(bestInvok[1]!=0)
      {
        CellDest=bestInvok[1];
        targetCell=bestInvok[4];
      }
      else
        return -1;
      if(CellDest==0)
        return -1;
      if(CellDest==fighter.getCell().getId())
        return targetCell+bestSS.getSpellID()*1000;

      ArrayList<GameCase> path=new AstarPathfinding(fight.getMapOld(),fight,fighter.getCell().getId(),CellDest).getShortestPath();

      if(path==null)
        return -1;
      String pathstr="";
      try
      {
        int curCaseID=fighter.getCell().getId();
        int curDir=0;
        path.add(fight.getMapOld().getCase(CellDest));
        for(GameCase c : path)
        {
          if(curCaseID==c.getId())
            continue; // Empêche le d == 0
          char d=PathFinding.getDirBetweenTwoCase(curCaseID,c.getId(),fight.getMap(),true);
          if(d==0)
            return -1;//Ne devrait pas arriver :O
          if(curDir!=d)
          {
            if(path.indexOf(c)!=0)
              pathstr+=World.world.getCryptManager().cellID_To_Code(curCaseID);
            pathstr+=d;
          }
          curCaseID=c.getId();
        }
        if(curCaseID!=fighter.getCell().getId())
          pathstr+=World.world.getCryptManager().cellID_To_Code(curCaseID);
      }
      catch(Exception e)
      {
        e.printStackTrace();
      }
      //Création d'une GameAction
      GameAction GA=new GameAction(0,1,"");
      GA.args=pathstr;
      fight.onFighterDeplace(fighter,GA);

      return targetCell+bestSS.getSpellID()*1000;
    }
    
    
    public int attackIfPossiblesacrifier(Fight fight, Fighter fighter, Fighter target)// 0 = Rien, 5 = EC, 666 = NULL, 10 = SpellNull ou ActionEnCour ou Can'tCastSpell, 0 = AttaqueOK
    {
      if(fight==null||fighter==null)
        return 0;
      SortStats SS=null;
      for(Map.Entry<Integer, SortStats> entry : fighter.getMob().getSpells().entrySet())
      {
        SortStats a=entry.getValue();
        if(a.getSpellID()==233)
          SS=a;
      }
      if(target==null)
        return 666;
      int attack=fight.tryCastSpell(fighter,SS,target.getCell().getId());
      if(attack!=0)
        return attack;
      return 0;
    }
    
    
    public int IfPossibleRasboulvulner(Fight fight, Fighter fighter, Fighter target)// 0 = Rien, 5 = EC, 666 = NULL, 10 = SpellNull ou ActionEnCour ou Can'tCastSpell, 0 = AttaqueOK
    {
      if(fight==null||fighter==null)
        return 0;
      SortStats SS=null;
      for(Map.Entry<Integer, SortStats> entry : fighter.getMob().getSpells().entrySet())
      {
        SortStats a=entry.getValue();
        if(a.getSpellID()==1039)
          SS=a;
      }
      if(target==null)
        return 666;
      if(fighter.getPa()<14||!LaunchedSpell.cooldownGood(fighter,1039))
        return 0;
      int attack=fight.tryCastSpell(fighter,SS,target.getCell().getId());

      if(attack!=0)
      {
        SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight,7,1039,target.getId()+"",target.getId()+",+"+1);
        return attack;
      }
      return 0;
    }


    public static boolean invoctantaIfPossible(Fight fight, Fighter fighter)
    {
        if (fight == null || fighter == null)
            return false;
        if (fighter.nbInvocation() >= 4)
            return false;
        Fighter nearest = getInstance().getNearestEnnemy(fight, fighter);
        if (nearest == null)
            return false;
        final int startCell = fighter.getCell().getId();
        final int limit = 30;
        byte coef =0;
        SortStats spell = null;
        if (fighter.haveState(36))
        {
            spell = World.world.getSort(1110).getStatsByLevel(5);
            fighter.setState(360,0,fighter.getId());
        }
        if (fighter.haveState(37))
        {
            spell = World.world.getSort(1109).getStatsByLevel(5);
            fighter.setState(37,0,fighter.getId());
        }
        if (fighter.haveState(38))
        {
            spell = World.world.getSort(1108).getStatsByLevel(5);
            fighter.setState(380,0,fighter.getId());
        }
        if (fighter.haveState(35))
        {
            spell = World.world.getSort(1107).getStatsByLevel(5);
            fighter.setState(35,0,fighter.getId());
        }
        
        int nearestCell = startCell;
        
        while (++coef < limit)
        {
        	//Method Debuggé
            nearestCell = PathFinding.getNearestCellAroundKrala(fight.getMap(), startCell, nearest.getCell().getId(), coef);
            if(nearestCell != startCell) break;
        }
        if (nearestCell == startCell)
            return false;
        
        if (spell == null)
            return false;
        int invoc = fight.tryCastSpell(fighter, spell, nearestCell);
        if (invoc != 0)
            return false;
        return true;
    }
    
    public static Fighter getEnnemyWithDistance(final Fight fight, final Fighter fighter, final int min, int max, final List<Fighter> fighters) {
        if (fight == null || fighter == null) {
            return null;
        }
        Fighter target = null;
        for (int i = 0; i <= 1 && target == null; ++i) {
            for (final Fighter f : fight.getFighters(3)) {
                int distance;
                if (i == 0 && (f.isInvocation() && !fighter.isInvocation() || f.isStatique) || f.isDead() 
                		|| fighters != null && fighters.contains(f) || f.isHide() || f.getTeam2() == fighter.getTeam2() 
                		|| (distance = PathFinding.getDistanceBetween(fight.getMap(), fighter.getCell().getId(), f.getCell().getId())) >= max 
                		|| distance <= min) continue;
                max = distance;
                target = f;
            }
        }
        return target;
    }
    public static SortStats findSpell(final Fighter fighter, final int id) {
        for(final SortStats spell : fighter.getMob().getSpells().values()) {
            if(spell != null && spell.getSpellID() == id)
                return spell;
        }
        return null;
    }
    
    public boolean buffIfPossibleKrala(Fight fight, Fighter fighter, Fighter target)
    {
      if(fight==null||fighter==null)
        return false;
      if(target==null)
        return false;
      SortStats SS=null;
      if(fighter.haveState(31)&&fighter.haveState(32)&&fighter.haveState(33)&&fighter.haveState(34))
      {
        SS=World.world.getSort(1106).getStatsByLevel(5);
      }
      if(SS==null)
        return false;
      int buff=fight.tryCastSpell(fighter,SS,target.getCell().getId());
      if(buff!=0)
        return false;
      return true;
    }

    public boolean buffIfPossibleMinotot(Fight fight, Fighter fighter, Fighter target, SortStats spell)
    {
        try {
            if (fight == null || fighter == null)
                return false;
            if (target == null)
                return false;
            if (spell == null)
                return false;
            if (fighter.getCurPa(fight) < 15)
                return false;
            if (fighter.hasBuff(108)) {
                SpellEffect spellEffect = null;
                for (SpellEffect effect : spell.getEffects()) {
                    if (effect.getEffectID() == 108) {
                        spellEffect = effect;
                    }
                }
                if (spellEffect != null) {
                    Spell spellToCheck = World.world.getSort(spellEffect.getSpell());
                    if (spellToCheck != null) {
                        if (spellToCheck.getStatsByLevel(spell.getLevel()) == spell) {
                            return false;
                        }
                    }
                }
            }
            int buff = fight.tryCastSpell(fighter, spell, target.getCell().getId());
            if (buff != 0)
                return false;
            return true;
        }
        catch (Exception ex)
        {
            Logging.getInstance().write("Error", ex.getMessage() + "\n" + Arrays.toString(ex.getStackTrace()));
            return false;
        }
    }

    public boolean buffIfPossibleKitsou(Fight fight, Fighter fighter, Fighter target)
    {
      if(fight==null||fighter==null)
        return false;
      if(target==null)
        return false;
      SortStats SS=null;
      SS=World.world.getSort(521).getStatsByLevel(5);
      if(SS==null)
        return false;
      int buff=fight.tryCastSpell(fighter,SS,target.getCell().getId());
      if(buff!=0)
        return false;
      return true;
    }
    
    public boolean buffIfPossibleTortu(Fight fight, Fighter fighter, Fighter target)
    {
      if(fight==null||fighter==null)
        return false;
      if(target==null)
        return false;
      SortStats SS=null;
      for(Map.Entry<Integer, SortStats> entry : fighter.getMob().getSpells().entrySet())
      {
        SortStats a=entry.getValue();
        if(a.getSpellID()==1019)
          SS=a;
      }
      if(SS==null)
        return false;
      int buff=fight.tryCastSpell(fighter,SS,target.getCell().getId());
      if(buff!=0)
        return false;
      return true;
    }

    public int tpIfPossibleTynril(Fight fight, Fighter fighter, Fighter target)// 0 = Rien, 5 = EC, 666 = NULL, 10 = SpellNull ou ActionEnCour ou Can'tCastSpell, 0 = AttaqueOK
    {
      if(fight==null||fighter==null)
        return 0;
      SortStats SS=null;
      for(Map.Entry<Integer, SortStats> entry : fighter.getMob().getSpells().entrySet())
      {
        SortStats a=entry.getValue();
        if(a.getSpellID()==1060)
          SS=a;
      }
      if(target==null)
        return 666;
      int attack=fight.tryCastSpell(fighter,SS,target.getCell().getId());
      if(attack!=0)
        return attack;
      return 0;
    }

    public int pmgongon(Fight fight, Fighter fighter, Fighter target)// 0 = Rien, 5 = EC, 666 = NULL, 10 = SpellNull ou ActionEnCour ou Can'tCastSpell, 0 = AttaqueOK
    {
      if(fight==null||fighter==null)
        return 0;
      SortStats SS=null;
      for(Map.Entry<Integer, SortStats> entry : fighter.getMob().getSpells().entrySet())
      {
        SortStats a=entry.getValue();
        if(a.getSpellID()==284)
          SS=a;
      }
      if(target==null)
        return 0;
      int attack=fight.tryCastSpell(fighter,SS,target.getCell().getId());
      if(attack!=0)
        return attack;
      return 0;
    }

    public int tpIfPossibleRasboul(Fight fight, Fighter fighter, Fighter target) // 0 = success, 5 = EC, 10 = error, 666 = NULL
    {
      if(fight==null||fighter==null)
        return 10;
      SortStats SS=null;
      for(Map.Entry<Integer, SortStats> entry : fighter.getMob().getSpells().entrySet())
      {
        SortStats a=entry.getValue();
        if(a.getSpellID()==1041)
          SS=a;
      }
      if(target==null)
        return 666;
      return fight.tryCastSpell(fighter,SS,target.getCell().getId());
    }

    public boolean HealIfPossiblefriend(Fight fight, Fighter f, Fighter target)//boolean pour choisir entre auto-soin ou soin allié
    {
      if(fight==null||f==null||target==null)
        return false;
      if(f.isDead())
        return false;
      SortStats SS=null;

      Fighter curF=null;
      int PDVPERmin=100;
      SortStats curSS=null;
      for(Fighter F : fight.getFighters(3))
      {
        if(f.isDead())
          continue;
        if(F==f)
          continue;
        if(F.isDead())
          continue;
        if(F.getTeam()==f.getTeam())
        {
          int PDVPER=(F.getPdv()*100)/F.getPdvMax();
          if(PDVPER<PDVPERmin&&PDVPER<95)
          {
            int infl=0;
            if(f.isCollector())
            {
              for(Map.Entry<Integer, SortStats> ss : World.world.getGuild(f.getCollector().getGuildId()).getSpells().entrySet())
              {
                if(ss.getValue()==null)
                  continue;
                if(infl<calculInfluenceHeal(ss.getValue())&&calculInfluenceHeal(ss.getValue())!=0&&fight.canCastSpell1(f,ss.getValue(),F.getCell(),-1))//Si le sort est plus interessant
                {
                  infl=calculInfluenceHeal(ss.getValue());
                  curSS=ss.getValue();
                }
              }
            }
            else
            {
              for(Map.Entry<Integer, SortStats> ss : f.getMob().getSpells().entrySet())
              {
                if(infl<calculInfluenceHeal(ss.getValue())&&calculInfluenceHeal(ss.getValue())!=0&&fight.canCastSpell1(f,ss.getValue(),F.getCell(),-1))//Si le sort est plus interessant
                {
                  infl=calculInfluenceHeal(ss.getValue());
                  curSS=ss.getValue();
                }
              }
            }
            if(curSS!=SS&&curSS!=null)
            {
              curF=F;
              SS=curSS;
              PDVPERmin=PDVPER;
            }
          }
        }
      }
      target=curF;
      if(target==null)
        return false;
      if(target.isFullPdv())
        return false;
      if(SS==null)
        return false;
      int heal=fight.tryCastSpell(f,SS,target.getCell().getId());
      if(heal!=0)
        return false;

      return true;
    }

    public int tpIfPossibleKaskargo(Fight fight, Fighter fighter, Fighter target)// 0 = Rien, 5 = EC, 666 = NULL, 10 = SpellNull ou ActionEnCour ou Can'tCastSpell, 0 = AttaqueOK
    {
      if(fight==null||fighter==null)
        return 0;
      SortStats SS=null;
      for(Map.Entry<Integer, SortStats> entry : fighter.getMob().getSpells().entrySet())
      {
        SortStats a=entry.getValue();
        if(a.getSpellID()==445)
          SS=a;
      }
      if(target==null)
        return 666;
      int attack=fight.tryCastSpell(fighter,SS,target.getCell().getId());
      if(attack!=0)
        return attack;
      return 0;
    }

    public int attackIfPossibleKaskargo(Fight fight, Fighter fighter, Fighter target)// 0 = Rien, 5 = EC, 666 = NULL, 10 = SpellNull ou ActionEnCour ou Can'tCastSpell, 0 = AttaqueOK
    {
      if(fight==null||fighter==null)
        return 0;

      SortStats spellStat=null;
      int cell=PathFinding.getCaseBetweenEnemy(fighter.getCell().getId(),fight.getMap(),fight);

      for(SortStats spellStats : fighter.getMob().getSpells().values())
        if(spellStats.getSpellID()==949)
        {
          spellStat=spellStats;
          break;
        }

      int i=10;
      while(i>0)
      {
        for(Glyph glyph : fight.getAllGlyphs())
          if(glyph!=null&&glyph.getCell().getId()==cell)
            cell=PathFinding.getCaseBetweenEnemy(fighter.getCell().getId(),fight.getMap(),fight);
        i--;
      }

      if(target==null)
        return 666;
      int attack=fight.tryCastSpell(fighter,spellStat,cell);
      if(attack!=0)
        return attack;
      return 0;
    }

    public int attackIfPossiblePeki(Fight fight, Fighter fighter, Fighter target)// 0 = Rien, 5 = EC, 666 = NULL, 10 = SpellNull ou ActionEnCour ou Can'tCastSpell, 0 = AttaqueOK
    {
      if(fight==null||fighter==null||target==null)
        return 0;
      SortStats SS=null;
      int cell=0;
      for(Map.Entry<Integer, SortStats> S : fighter.getMob().getSpells().entrySet())
      {
        int cellID=target.getCell().getId();
        if(S.getValue().getSpellID()==1280)
        {
          cell=cellID;
          SS=S.getValue();
        }
      }
      int attack=fight.tryCastSpell(fighter,SS,cell);

      if(attack!=0)
        return 2000;
      return 0;
    }
    public int attackIfPossibleMinotot(Fight fight, Fighter fighter, Fighter target)// 0 = Rien, 5 = EC, 666 = NULL, 10 = SpellNull ou ActionEnCour ou Can'tCastSpell, 0 = AttaqueOK
    {
        try {
            if (fight == null || fighter == null || target == null)
                return 0;
            SortStats SS = null;
            int cell = 0;
            for (Map.Entry<Integer, SortStats> S : fighter.getMob().getSpells().entrySet()) {
                int cellID = target.getCell().getId();
                if (S.getValue().getSpellID() == 812) {
                    cell = cellID;
                    SS = S.getValue();
                }
            }
            if (SS != null) {
                int attack = fight.tryCastSpell(fighter, SS, cell);
                if (attack != 0)
                    return 2000;
            }
            return 0;
        }
        catch (Exception ex)
        {
            Logging.getInstance().write("Error", ex.getMessage() + "\n" + Arrays.toString(ex.getStackTrace()));
            return 2000;
        }
    }

    public int attackIfPossibleRN(Fight fight, Fighter fighter, Fighter target)// 0 = Rien, 5 = EC, 666 = NULL, 10 = SpellNull ou ActionEnCour ou Can'tCastSpell, 0 = AttaqueOK
    {
      if(fight==null||fighter==null||target==null)
        return -1;
      SortStats SS=null;
      int cell=0;
      for(Map.Entry<Integer, SortStats> S : fighter.getMob().getSpells().entrySet())
      {
        int cellID=target.getCell().getId();
        if(S.getValue().getSpellID()==1006)
        {
          cell=cellID;
          SS=S.getValue();
        }
      }
      if(!fight.canCastSpell1(fighter,SS,fight.getMap().getCase(cell),-1))
        return -1;
      int attack=fight.tryCastSpell(fighter,SS,cell);

      if(attack!=0)
        return 2000;
      return -1;
    }

    public int attackIfPossibleBuveur(Fight fight, Fighter fighter, Fighter target)// 0 = Rien, 5 = EC, 666 = NULL, 10 = SpellNull ou ActionEnCour ou Can'tCastSpell, 0 = AttaqueOK
    {
      if(fight==null||fighter==null)
        return -1;
      SortStats SS=null;
      int cell=0;
      for(Map.Entry<Integer, SortStats> S : fighter.getMob().getSpells().entrySet())
      {
        int cellID=fighter.getCell().getId();
        if(S.getValue().getSpellID()==808)
        {
          cell=cellID;
          SS=S.getValue();
        }
      }
      if(target==null)
        return 666;
      int attack=fight.tryCastSpell(fighter,SS,cell);

      if(attack!=0)
        return 800;
      return -1;
    }

    public int attackIfPossibleWobot(Fight fight, Fighter fighter)// 0 = Rien, 5 = EC, 666 = NULL, 10 = SpellNull ou ActionEnCour ou Can'tCastSpell, 0 = AttaqueOK
    {
      if(fight==null||fighter==null)
        return -1;
      SortStats SS=null;
      int cell=0;
      for(Map.Entry<Integer, SortStats> S : fighter.getMob().getSpells().entrySet())
      {
        int cellID=PathFinding.getCaseBetweenEnemy(fighter.getCell().getId(),fight.getMap(),fight);
        if(S.getValue().getSpellID()==335)
        {
          cell=cellID;
          SS=S.getValue();
        }
      }
      int attack=fight.tryCastSpell(fighter,SS,cell);

      if(attack!=0)
        return 800;
      return -1;
    }
    
    public int attackIfPossibleTynril(Fight fight, Fighter fighter, Fighter target)// 0 = Rien, 5 = EC, 666 = NULL, 10 = SpellNull ou ActionEnCour ou Can'tCastSpell, 0 = AttaqueOK
    {
      if(fight==null||fighter==null)
        return 0;
      SortStats SS=null;
      if(target==null)
        return 666;
      if(fighter.getMob().getTemplate().getId()==1087)
      {//ahuri  /faiblesse terre
        if(target.hasBuff(215))
        {
          SS=findSpell(fighter,1059);
        }
        else
        {
          SS=findSpell(fighter,1058);
        }
      }
      else if(fighter.getMob().getTemplate().getId()==1085)
      {//deconcerter  /faiblesse eau
        if(target.hasBuff(216))
        {
          SS=findSpell(fighter,1059);
        }
        else
        {
          SS=findSpell(fighter,1058);
        }
      }
      else if(fighter.getMob().getTemplate().getId()==1072)
      {//consterner  /faiblesse air
        if(target.hasBuff(217))
        {
          SS=findSpell(fighter,1059);
        }
        else
        {
          SS=findSpell(fighter,1058);
        }
      }
      else if(fighter.getMob().getTemplate().getId()==1086)
      {//perfide  /faiblesse feu
        if(target.hasBuff(218))
        {
          SS=findSpell(fighter,1059);
        }
        else
        {
          SS=findSpell(fighter,1058);
        }
      }

      if(fight.canCastSpell1(fighter,SS,target.getCell(),-1))
      {
        return fight.tryCastSpell(fighter,SS,target.getCell().getId());
      }
      return 0;
    }

    public boolean moveNearIfPossible(Fight fight, Fighter F, Fighter T)
    {
      if(fight==null)
        return false;
      if(F==null)
        return false;
      if(T==null)
        return false;
      if(F.getCurPm(fight)<=0)
        return false;
      GameMap map=fight.getMap();
      if(map==null)
        return false;
      GameCase cell=F.getCell();
      if(cell==null)
        return false;
      GameCase cell2=T.getCell();
      if(cell2==null)
        return false;
      if(PathFinding.isNextTo(map,cell.getId(),cell2.getId()))
        return false;

      int cellID=PathFinding.getNearestCellAround(map,cell2.getId(),cell.getId(),null);
      //On demande le chemin plus court
      //Mais le chemin le plus court ne prend pas en compte les bords de map.
      if(cellID==-1)
      {
        Map<Integer, Fighter> ennemys=getLowHpEnnemyList(fight,F);
        for(Map.Entry<Integer, Fighter> target : ennemys.entrySet())
        {
          int cellID2=PathFinding.getNearestCellAround(map,target.getValue().getCell().getId(),cell.getId(),null);
          if(cellID2!=-1)
          {
            cellID=cellID2;
            break;
          }
        }
      }

      ArrayList<GameCase> path=new AstarPathfinding(fight.getMapOld(),fight,cell.getId(),cell2.getId()).getShortestPath();

      if(path==null||path.isEmpty())
        return false;
      ArrayList<GameCase> finalPath=new ArrayList<GameCase>();
      for(int a=0;a<F.getCurPm(fight);a++)
      {
        if(path.size()==a)
          break;
        finalPath.add(path.get(a));
      }
      String pathstr="";
      try
      {
        int curCaseID=cell.getId();
        int curDir=0;
        for(GameCase c : finalPath)
        {
          char d=PathFinding.getDirBetweenTwoCase(curCaseID,c.getId(),map,true);
          if(d==0)
            return false;//Ne devrait pas arriver :O
          if(curDir!=d)
          {
            if(finalPath.indexOf(c)!=0)
              pathstr+=World.world.getCryptManager().cellID_To_Code(curCaseID);
            pathstr+=d;
          }
          curCaseID=c.getId();
        }
        if(curCaseID!=cell.getId())
          pathstr+=World.world.getCryptManager().cellID_To_Code(curCaseID);
      }
      catch(Exception e)
      {
        e.printStackTrace();
      }
      //Création d'une GameAction
      GameAction GA=new GameAction(0,1,"");
      GA.args=pathstr;
      boolean result=fight.onFighterDeplace(F,GA);

      return result;
    }

    
    public int getMaxCellForTP(Fight fight, Fighter F, Fighter T, int dist)
    {
      if(fight==null||F==null||T==null||dist<1)
        return -1;

      GameMap map=fight.getMap();
      GameCase cell=F.getCell(),cell2=T.getCell();

      if(map==null||cell==null||cell2==null||PathFinding.isNextTo(map,cell.getId(),cell2.getId()))
        return -1;

      ArrayList<GameCase> path=new AstarPathfinding(fight.getMapOld(),fight,cell.getId(),cell2.getId()).getShortestPath(-1);

      if(path==null||path.isEmpty())
        return -1;

      int cellId=-1;

      for(int a=0;a<dist;a++)
      {
        if(path.size()==a)
          break;
        cellId=path.get(a).getId();
      }

      return cellId;
    }
    
    
    public int attackIfPossibleDiscipleimpair(Fight fight, Fighter fighter, Fighter target)// 0 = Rien, 5 = EC, 666 = NULL, 10 = SpellNull ou ActionEnCour ou Can'tCastSpell, 0 = AttaqueOK
    {
      if(fight==null||fighter==null)
        return 0;
      SortStats SS=null;
      for(Map.Entry<Integer, SortStats> entry : fighter.getMob().getSpells().entrySet())
      {
        SortStats a=entry.getValue();
        if(a.getSpellID()==3501)
          SS=a;
      }
      if(target==null)
        return 666;
      int attack=fight.tryCastSpell(fighter,SS,target.getCell().getId());
      if(attack!=0)
        return attack;
      return 0;
    }

    public int attackBondIfPossible(Fight fight, Fighter fighter, Fighter target)// 0 = Rien, 5 = EC, 666 = NULL, 10 = SpellNull ou ActionEnCour ou Can'tCastSpell, 0 = AttaqueOK
    {
      if(fight==null||fighter==null)
        return 0;
      int cell=0;
      SortStats SS2=null;

      if(target==null)
        return 0;
      for(Map.Entry<Integer, SortStats> S : fighter.getMob().getSpells().entrySet())
      {
        int cellID=PathFinding.getCaseBetweenEnemy(target.getCell().getId(),fight.getMap(),fight);
        boolean effet4=false;
        boolean effet6=false;

        for(SpellEffect f : S.getValue().getEffects())
        {
          if(f.getEffectID()==4)
            effet4=true;
          if(f.getEffectID()==6)
          {
            effet6=true;
            effet4=true;
          }
        }
        if(effet4==false)
          continue;
        if(effet6==false)
        {
          cell=cellID;
          SS2=S.getValue();
        }
        else
        {
          cell=target.getCell().getId();
          SS2=S.getValue();
        }
      }
      if(cell>=15&&cell<=463&&SS2!=null)
      {
        int attack=fight.tryCastSpell(fighter,SS2,cell);
        if(attack!=0)
          return SS2.getSpell().getDuration();
      }
      else
      {
        if(target==null||SS2==null)
          return 0;
        int attack=fight.tryCastSpell(fighter,SS2,cell);
        if(attack!=0)
          return SS2.getSpell().getDuration();
      }
      return 0;
    }

    public int attackIfPossibleDisciplepair(Fight fight, Fighter fighter, Fighter target)
    {// 0 = Rien, 5 = EC, 666 = NULL, 10 = SpellNull ou ActionEnCour ou Can'tCastSpell, 0 = AttaqueOK
      if(fight==null||fighter==null)
        return 0;
      SortStats SS=null;

      for(SortStats spellStats : fighter.getMob().getSpells().values())
        if(spellStats.getSpellID()==3500)
          SS=spellStats;

      if(target==null)
        return 666;

      int attack=fight.tryCastSpell(fighter,SS,target.getCell().getId());

      if(attack!=0)
        return attack;
      return 0;
    }

    public int moveFarIfPossible(Fight fight, Fighter F)
    {
      if(fight==null||F==null)
        return 0;
      if(fight.getMap()==null)
        return 0;
      int nbrcase=0;
      //On créer une liste de distance entre ennemi et de cellid, nous permet de savoir si un ennemi est collé a nous
      int dist[]= { 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000 },cell[]= { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
      for(int i=0;i<10;i++)//on repete 10 fois pour les 10 joueurs ennemis potentielle
      {
        for(Fighter f : fight.getFighters(3))
        {

          if(f.isDead())
            continue;
          if(f==F||f.getTeam()==F.getTeam())
            continue;
          int cellf=f.getCell().getId();
          if(cellf==cell[0]||cellf==cell[1]||cellf==cell[2]||cellf==cell[3]||cellf==cell[4]||cellf==cell[5]||cellf==cell[6]||cellf==cell[7]||cellf==cell[8]||cellf==cell[9])
            continue;
          int d=0;
          d=PathFinding.getDistanceBetween(fight.getMap(),F.getCell().getId(),f.getCell().getId());
          if(d<dist[i])
          {
            dist[i]=d;
            cell[i]=cellf;
          }
          if(dist[i]==1000)
          {
            dist[i]=0;
            cell[i]=F.getCell().getId();
          }
        }
      }
      //if(dist[0] == 0)return false;//Si ennemi "collé"

      int dist2[]= { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
      int PM=F.getCurPm(fight),caseDepart=F.getCell().getId(),destCase=F.getCell().getId();
      ArrayList<Integer> caseUse=new ArrayList<Integer>();
      caseUse.add(caseDepart); // On ne revient pas a sa position de départ
      for(int i=0;i<=PM;i++)//Pour chaque PM on analyse la meilleur case a prendre. C'est a dire la plus éliognée de tous.
      {
        if(destCase>0)
          caseDepart=destCase;
        int curCase=caseDepart;

        /** En +15 **/
        curCase+=15;
        int infl=0,inflF=0;
        for(int a=0;a<10&&dist[a]!=0;a++)
        {
          dist2[a]=PathFinding.getDistanceBetween(fight.getMap(),curCase,cell[a]);//pour chaque ennemi on calcul la nouvelle distance depuis cette nouvelle case (curCase)
          if(dist2[a]>dist[a])//Si la cellule (curCase) demander et plus distante que la précedente de l'ennemi alors on dirrige le mouvement vers elle
            infl++;
        }

        if(infl>inflF&&curCase>=15&&curCase<=463&&testCotes(destCase,curCase)&&fight.getMap().getCase(curCase).isWalkable(false,true,-1)&&fight.getMap().getCase(curCase).getFighters().isEmpty()&&!caseUse.contains(curCase))//Si l'influence (infl) est la plus forte en comparaison avec inflF on garde la case si celle-ci est valide
        {
          inflF=infl;
          destCase=curCase;
        }
        /** En +15 **/

        /** En +14 **/
        curCase=caseDepart+14;
        infl=0;

        for(int a=0;a<10&&dist[a]!=0;a++)
        {
          dist2[a]=PathFinding.getDistanceBetween(fight.getMap(),curCase,cell[a]);
          if(dist2[a]>dist[a])
            infl++;
        }

        if(infl>inflF&&curCase>=15&&curCase<=463&&testCotes(destCase,curCase)&&fight.getMap().getCase(curCase).isWalkable(false,true,-1)&&fight.getMap().getCase(curCase).getFighters().isEmpty()&&!caseUse.contains(curCase))
        {
          inflF=infl;
          destCase=curCase;
        }
        /** En +14 **/

        /** En -15 **/
        curCase=caseDepart-15;
        infl=0;
        for(int a=0;a<10&&dist[a]!=0;a++)
        {
          dist2[a]=PathFinding.getDistanceBetween(fight.getMap(),curCase,cell[a]);
          if(dist2[a]>dist[a])
            infl++;
        }

        if(infl>inflF&&curCase>=15&&curCase<=463&&testCotes(destCase,curCase)&&fight.getMap().getCase(curCase).isWalkable(false,true,-1)&&fight.getMap().getCase(curCase).getFighters().isEmpty()&&!caseUse.contains(curCase))
        {
          inflF=infl;
          destCase=curCase;
        }
        /** En -15 **/

        /** En -14 **/
        curCase=caseDepart-14;
        infl=0;
        for(int a=0;a<10&&dist[a]!=0;a++)
        {
          dist2[a]=PathFinding.getDistanceBetween(fight.getMap(),curCase,cell[a]);
          if(dist2[a]>dist[a])
            infl++;
        }

        if(infl>inflF&&curCase>=15&&curCase<=463&&testCotes(destCase,curCase)&&fight.getMap().getCase(curCase).isWalkable(false,true,-1)&&fight.getMap().getCase(curCase).getFighters().isEmpty()&&!caseUse.contains(curCase))
        {
          inflF=infl;
          destCase=curCase;
        }
        /** En -14 **/
        caseUse.add(destCase);
      }
      if(destCase<15||destCase>463||destCase==F.getCell().getId()||!fight.getMap().getCase(destCase).isWalkable(false,true,-1))
        return 0;

      if(F.getPm()<=0)
        return 0;

      ArrayList<GameCase> path=new AstarPathfinding(fight.getMap(),fight,F.getCell().getId(),destCase).getShortestPath(-1);

      if(path==null)
        return 0;
      ArrayList<GameCase> finalPath=new ArrayList<GameCase>();
      for(int a=0;a<F.getCurPm(fight);a++)
      {
        if(path.size()==a)
          break;
        finalPath.add(path.get(a));
      }
      String pathstr="";
      try
      {
        int curCaseID=F.getCell().getId();
        int curDir=0;
        for(GameCase c : finalPath)
        {
          char d=PathFinding.getDirBetweenTwoCase(curCaseID,c.getId(),fight.getMap(),true);
          if(d==0)
            return 0;//Ne devrait pas arriver :O
          if(curDir!=d)
          {
            if(finalPath.indexOf(c)!=0)
              pathstr+=World.world.getCryptManager().cellID_To_Code(curCaseID);
            pathstr+=d;
          }
          curCaseID=c.getId();

          nbrcase=nbrcase+1;
        }
        if(curCaseID!=F.getCell().getId())
          pathstr+=World.world.getCryptManager().cellID_To_Code(curCaseID);
      }
      catch(Exception e)
      {
        e.printStackTrace();
      }
      //Création d'une GameAction
      GameAction GA=new GameAction(0,1,"");
      GA.args=pathstr;

      if(!fight.onFighterDeplace(F,GA))
        return 0;

      return nbrcase*Config.getInstance().AIMovementCellDelay+Config.getInstance().AIMovementFlatDelay;
    }

    public static boolean testCotes(int cellWeAre, int cellWego)//Nous permet d'interdire le déplacement du bord vers des cellules hors map
    {
        if (cellWeAre == 15 || cellWeAre == 44 || cellWeAre == 73
                || cellWeAre == 102 || cellWeAre == 131 || cellWeAre == 160
                || cellWeAre == 189 || cellWeAre == 218 || cellWeAre == 247
                || cellWeAre == 276 || cellWeAre == 305 || cellWeAre == 334
                || cellWeAre == 363 || cellWeAre == 392 || cellWeAre == 421
                || cellWeAre == 450)
        {
            if (cellWego == cellWeAre + 14 || cellWego == cellWeAre - 15)
                return false;
        }
        if (cellWeAre == 28 || cellWeAre == 57 || cellWeAre == 86
                || cellWeAre == 115 || cellWeAre == 144 || cellWeAre == 173
                || cellWeAre == 202 || cellWeAre == 231 || cellWeAre == 260
                || cellWeAre == 289 || cellWeAre == 318 || cellWeAre == 347
                || cellWeAre == 376 || cellWeAre == 405 || cellWeAre == 434
                || cellWeAre == 463)
        {
            if (cellWego == cellWeAre + 15 || cellWego == cellWeAre - 14)
                return false;
        }

        if (cellWeAre >= 451 && cellWeAre <= 462)
        {
            if (cellWego == cellWeAre + 15 || cellWego == cellWeAre + 14)
                return false;
        }
        if (cellWeAre >= 16 && cellWeAre <= 27)
        {
            if (cellWego == cellWeAre - 15 || cellWego == cellWeAre - 14)
                return false;
        }
        return true;
    }
    

    public boolean invocIfPossible(Fight fight, Fighter fighter)
    {
      if(fight==null||fighter==null)
        return false;
      if(fighter.nbInvocation()>=fighter.getTotalStats().getEffect(Constant.STATS_ADD_SUM))
        return false;
      Fighter nearest=getNearestEnnemy(fight,fighter);
      if(nearest==null)
        return false;
      int nearestCell=fighter.getCell().getId();
      int limit=30;
      int _loc0_=0;
      SortStats spell=null;
      while((spell=getInvocSpell(fight,fighter,nearestCell))==null&&_loc0_++<limit)
      {
        nearestCell=PathFinding.getCaseBetweenEnemy(fighter.getCell().getId(),fight.getMap(),fight);
      }
      if(nearestCell==-1)
        return false;
      if(spell==null)
        return false;
      int invoc=fight.tryCastSpell(fighter,spell,nearestCell);
      if(invoc!=0)
        return false;
      return true;
    }
    public boolean invocIfPossibleKimbo(Fight fight, Fighter fighter, SortStats spell)
    {
        if(fight==null||fighter==null)
            return false;
        if(fighter.nbInvocation()>=fighter.getTotalStats().getEffect(Constant.STATS_ADD_SUM))
            return false;
        Fighter nearest=getNearestEnnemy(fight,fighter);
        if(nearest==null)
            return false;
        int nearestCell=-1;
        int limit=30;
        int _loc0_=0;
        if(spell==null)
            return false;
        ArrayList<GameCase> possibleCases = PathFinding.getCellListFromAreaString2(fight.getMap(), nearest.getCell().getId(), fighter.getCell().getId(), spell.getPorteeType(), spell.getMinPO()-1, false);
        if(possibleCases.size() > 0)
        {
            for(GameCase gameCase : possibleCases)
            {
                if(gameCase != null) {
                    if (gameCase.isWalkable(false) && gameCase.getFirstFighter() == null) {
                        int distBetweenCasterAndCell = PathFinding.getDistanceBetween(fight.getMap(), gameCase.getId(), fighter.getCell().getId());
                        if (nearestCell == -1 && distBetweenCasterAndCell == spell.getMaxPO()) {
                            nearestCell = gameCase.getId();
                        }
                        else {
                            int distNew = PathFinding.getDistanceBetween(fight.getMap(), gameCase.getId(), nearest.getCell().getId());
                            int distActuBestCell = PathFinding.getDistanceBetween(fight.getMap(), nearestCell, nearest.getCell().getId());
                            if (distNew < distActuBestCell && distBetweenCasterAndCell == spell.getMaxPO()) {
                                nearestCell = gameCase.getId();
                            }
                        }
                    }
                }
            }
        }
        if(nearestCell==-1)
            return false;
        int invoc=fight.tryCastSpell(fighter,spell,nearestCell);
        if(invoc!=0)
            return false;
        return true;
    }
    
    public boolean checkIfInvocPossible(Fight fight, Fighter fighter, List<SortStats> Spelllist)
	{
    	SortStats spell=null;
    	int nearestCell=fighter.getCell().getId();
        int limit=10;
        int _loc0_=0;
        while((spell=getInvocSpellDopeul(fight,fighter,nearestCell,Spelllist))==null&&_loc0_++<limit)
        {
          nearestCell=PathFinding.getCaseBetweenEnemy(fighter.getCell().getId(),fight.getMap(),fight);
        }
        if(nearestCell==-1)
          return false;
        if(spell==null)
          return false;
        if(fighter.nbInvocation()>=fighter.getTotalStats().getEffect(Constant.STATS_ADD_SUM))
            return false;
        else
          return true;
	}
    
    public boolean invocIfPossible(Fight fight, Fighter fighter, List<SortStats> Spelllist)
    {
      if(fight==null||fighter==null)
        return false;
      if(fighter.nbInvocation()>=fighter.getTotalStats().getEffect(Constant.STATS_ADD_SUM))
        return false;
      Fighter nearest=getNearestEnnemy(fight,fighter);
      if(nearest==null)
        return false;
      int nearestCell=fighter.getCell().getId();
      int limit=10;
      int _loc0_=0;
      SortStats spell=null;
      while((spell=getInvocSpellDopeul(fight,fighter,nearestCell,Spelllist))==null&&_loc0_++<limit)
      {
        nearestCell=PathFinding.getCaseBetweenEnemy(fighter.getCell().getId(),fight.getMap(),fight);
      }
      if(nearestCell==-1)
        return false;
      if(spell==null)
        return false;
      int invoc=fight.tryCastSpell(fighter,spell,nearestCell);
      if(invoc!=0)
        return false;
      return true;
    }

    public boolean invocIfPossibleloin(Fight fight, Fighter fighter, List<SortStats> Spelllist)
    {
      if(fight==null||fighter==null)
        return false;
      if(fighter.nbInvocation()>=fighter.getTotalStats().getEffect(Constant.STATS_ADD_SUM))
        return false;
      Fighter nearest=getNearestEnnemy(fight,fighter);
      if(nearest==null)
        return false;
      int nearestCell=fighter.getCell().getId();
      int limit=10;
      int _loc0_=0;
      SortStats spell=null;
      while((spell=getInvocSpellDopeul(fight,fighter,nearestCell,Spelllist))==null&&_loc0_++<limit)
      {
        nearestCell=PathFinding.getNearestCellAround(fight.getMap(),nearestCell,nearest.getCell().getId(),null);
      }
      if(nearestCell==-1)
        return false;
      if(spell==null)
        return false;
      int invoc=fight.tryCastSpell(fighter,spell,nearestCell);
      if(invoc!=0)
        return false;
      return true;
    }

    public SortStats getInvocSpell(Fight fight, Fighter fighter, int nearestCell)
    {
      if(fight==null||fighter==null)
        return null;
      if(fighter.getMob()==null)
        return null;
      if(fight.getMap()==null)
        return null;
      if(fight.getMap().getCase(nearestCell)==null)
        return null;
      for(Map.Entry<Integer, SortStats> SS : fighter.getMob().getSpells().entrySet())
      {
        if(!fight.canCastSpell1(fighter,SS.getValue(),fight.getMap().getCase(nearestCell),-1))
          continue;
        for(SpellEffect SE : SS.getValue().getEffects())
          if(SE.getEffectID()==181)
            return SS.getValue();
      }
      return null;
    }

    public SortStats getInvocSpellDopeul(Fight fight, Fighter fighter, int nearestCell, List<SortStats> Spelllist)
    {
      if(fight==null||fighter==null)
        return null;
      if(fighter.getMob()==null)
        return null;
      if(fight.getMap()==null)
        return null;
      if(fight.getMap().getCase(nearestCell)==null)
        return null;
      for(SortStats SS : Spelllist)
      {
        if(!fight.canCastSpell1(fighter,SS,fight.getMap().getCase(nearestCell),-1))
          continue;
        for(SpellEffect SE : SS.getEffects())
        {
          if(SE.getEffectID()==181)
            return SS;
        }
      }
      return null;
    }

    public int HealIfPossible(Fight fight, Fighter f, boolean autoSoin, int PDVPERmin)//boolean pour choisir entre auto-soin ou soin allié
    {
      if(fight==null||f==null)
        return 0;
      if(f.isDead())
        return 0;
      if(autoSoin&&(f.getPdv()*100)/f.getPdvMax()>95)
        return 0;
      Fighter target=null;
      SortStats SS=null;
      if(autoSoin)
      {
        int PDVPER=(f.getPdv()*100)/f.getPdvMax();
        if(PDVPER<PDVPERmin&&PDVPER<95)
        {
          target=f;
          SS=getHealSpell(fight,f,target);
        }
      }
      else
      //sélection joueur ayant le moins de pv
      {
        Fighter curF=null;
        //int PDVPERmin = 100;
        SortStats curSS=null;
        for(Fighter F : fight.getFighters(3))
        {
          if(f.isDead())
            continue;
          if(F==f)
            continue;
          if(F.isDead())
            continue;
          if(F.getTeam()==f.getTeam())
          {
            int PDVPER=(F.getPdv()*100)/F.getPdvMax();
            if(PDVPER<PDVPERmin&&PDVPER<95)
            {
              int infl=0;
              if(f.isCollector())
              {
                for(Map.Entry<Integer, SortStats> ss : World.world.getGuild(f.getCollector().getGuildId()).getSpells().entrySet())
                {
                  if(ss.getValue()==null)
                    continue;
                  if(infl<calculInfluenceHeal(ss.getValue())&&calculInfluenceHeal(ss.getValue())!=0&&fight.canCastSpell1(f,ss.getValue(),F.getCell(),-1))//Si le sort est plus interessant
                  {
                    infl=calculInfluenceHeal(ss.getValue());
                    curSS=ss.getValue();
                  }
                }
              }
              else
              {
                for(Map.Entry<Integer, SortStats> ss : f.getMob().getSpells().entrySet())
                {
                  if(infl<calculInfluenceHeal(ss.getValue())&&calculInfluenceHeal(ss.getValue())!=0&&fight.canCastSpell1(f,ss.getValue(),F.getCell(),-1))//Si le sort est plus interessant
                  {
                    infl=calculInfluenceHeal(ss.getValue());
                    curSS=ss.getValue();
                  }
                }
              }
              if(curSS!=SS&&curSS!=null)
              {
                curF=F;
                SS=curSS;
                PDVPERmin=PDVPER;
              }
            }
          }
        }
        target=curF;
      }
      if(target==null)
        return 0;
      if(target.isFullPdv())
        return 0;
      if(SS==null)
        return 0;
      int heal=fight.tryCastSpell(f,SS,target.getCell().getId());
      if(heal!=0)
        return SS.getSpell().getDuration();

      return 0;
    }

    public int HealIfPossible(Fight fight, Fighter f)//boolean pour choisir entre auto-soin ou soin allié
    {
      if(fight==null||f==null)
        return 0;
      if(f.isDead())
        return 0;
      Fighter target=null;
      SortStats SS=null;
      target=f;
      SS=World.world.getSort(587).getStatsByLevel(f.getLvl());
      if(SS==null)
        return 0;
      int heal=fight.tryCastSpell(f,SS,target.getCell().getId());
      if(heal!=0)
        return SS.getSpell().getDuration();
      return 0;
    }

    public int HealIfPossible(Fight fight, Fighter f, Fighter A)//boolean pour choisir entre auto-soin ou soin allié
    {
      if(fight==null||f==null||A==null)
        return 0;
      if(f.isDead())
        return 0;
      SortStats SS=null;
      SS=World.world.getSort(210).getStatsByLevel(f.getLvl());
      if(SS==null)
        return 0;
      int heal=fight.tryCastSpell(f,SS,A.getCell().getId());
      if(heal!=0)
        return SS.getSpell().getDuration();
      return 0;
    }


    public boolean HealIfPossible(Fight fight, Fighter f, boolean autoSoin)//boolean pour choisir entre auto-soin ou soin allié
    {
      if(fight==null||f==null)
        return false;
      if(f.isDead())
        return false;
      if(autoSoin&&(f.getPdv()*100)/f.getPdvMax()>95)
        return false;
      Fighter target=null;
      SortStats SS=null;
      if(autoSoin)
      {
        target=f;
        SS=getHealSpell(fight,f,target);
      }
      else
      //sélection joueur ayant le moins de pv
      {
        Fighter curF=null;
        int PDVPERmin=100;
        SortStats curSS=null;
        for(Fighter F : fight.getFighters(3))
        {
          if(f.isDead())
            continue;
          if(F==f)
            continue;
          if(F.isDead())
            continue;
          if(F.getTeam()==f.getTeam())
          {
            int PDVPER=(F.getPdv()*100)/F.getPdvMax();
            if(PDVPER<PDVPERmin&&PDVPER<95)
            {
              int infl=0;
              if(f.isCollector())
              {
                for(Map.Entry<Integer, SortStats> ss : World.world.getGuild(f.getCollector().getGuildId()).getSpells().entrySet())
                {
                  if(ss.getValue()==null)
                    continue;
                  if(infl<calculInfluenceHeal(ss.getValue())&&calculInfluenceHeal(ss.getValue())!=0&&fight.canCastSpell1(f,ss.getValue(),F.getCell(),-1))//Si le sort est plus interessant
                  {
                    infl=calculInfluenceHeal(ss.getValue());
                    curSS=ss.getValue();
                  }
                }
              }
              else
              {
                for(Map.Entry<Integer, SortStats> ss : f.getMob().getSpells().entrySet())
                {
                  if(infl<calculInfluenceHeal(ss.getValue())&&calculInfluenceHeal(ss.getValue())!=0&&fight.canCastSpell1(f,ss.getValue(),F.getCell(),-1))//Si le sort est plus interessant
                  {
                    infl=calculInfluenceHeal(ss.getValue());
                    curSS=ss.getValue();
                  }
                }
              }
              if(curSS!=SS&&curSS!=null)
              {
                curF=F;
                SS=curSS;
                PDVPERmin=PDVPER;
              }
            }
          }
        }
        target=curF;
      }
      if(target==null)
        return false;
      if(target.isFullPdv())
        return false;
      if(SS==null)
        return false;
      int heal=fight.tryCastSpell(f,SS,target.getCell().getId());
      if(heal!=0)
        return false;

      return true;
    }

    public boolean buffIfPossible(Fight fight, Fighter fighter, Fighter target)
    {
        try {
            if (fight == null || fighter == null)
                return false;
            if (target == null)
                return false;
            SortStats SS = getBuffSpell(fight, fighter, target);
            if (SS == null)
                return false;
            int buff = fight.tryCastSpell(fighter, SS, target.getCell().getId());
            if (buff != 0)
                return false;
            return true;
        }
        catch (Exception ex)
        {
            Logging.getInstance().write("Error", ex.getMessage() + "\n" + Arrays.toString(ex.getStackTrace()));
            return false;
        }
    }

    public SortStats getBuffSpell(Fight fight, Fighter F, Fighter T)
    {
      if(fight==null||F==null)
        return null;
      int infl=-1500000;
      SortStats ss=null;
      if(F.isCollector())
      {
        for(Map.Entry<Integer, SortStats> SS : World.world.getGuild(F.getCollector().getGuildId()).getSpells().entrySet())
        {
          if(SS.getValue()==null)
            continue;
          if(infl<calculInfluence(SS.getValue(),F,T)&&calculInfluence(SS.getValue(),F,T)>0&&fight.canCastSpell1(F,SS.getValue(),T.getCell(),-1))//Si le sort est plus interessant
          {
            infl=calculInfluence(SS.getValue(),F,T);
            ss=SS.getValue();
          }
        }
      }
      else
      {
        for(Map.Entry<Integer, SortStats> SS : F.getMob().getSpells().entrySet())
        {
          int inf=calculInfluence(SS.getValue(),F,T);
          if(infl<inf&&SS.getValue().getSpell().getType()==1&&fight.canCastSpell1(F,SS.getValue(),T.getCell(),-1))//Si le sort est plus interessant
          {
            infl=calculInfluence(SS.getValue(),F,T);
            ss=SS.getValue();
          }
        }
      }
      return ss;
    }
    
    // Le teste doit être fait lorsque la PO du sort est valide
    public boolean checkIfBuffAvailable(Fight fight, Fighter fighter, Fighter target, List<SortStats> Spelllist)
    {
    	SortStats SS=getBuffSpellDopeul(fight,fighter,target,Spelllist);
        if(SS==null)
        	return false;
        else
        	return true;
    }

    public boolean buffIfPossible(Fight fight, Fighter fighter, Fighter target, List<SortStats> Spelllist)
    {
      if(fight==null||fighter==null)
        return false;
      if(target==null)
        return false;
      SortStats SS=getBuffSpellDopeul(fight,fighter,target,Spelllist);
      if(SS==null)
        return false;
      int buff=fight.tryCastSpell(fighter,SS,target.getCell().getId());
      if(buff!=0)
        return true;
      return false;
    }

    public SortStats getBuffSpellDopeul(Fight fight, Fighter F, Fighter T, List<SortStats> Spelllist)
    {
      if(fight==null||F==null)
        return null;
      int infl=-1500000;
      SortStats ss=null;
      for(SortStats SS : Spelllist)
      {
        int inf=calculInfluence(SS,F,T);

        if(infl<inf&&SS.getSpell().getType()==1&&fight.canCastSpell1(F,SS,T.getCell(),-1))//Si le sort est plus interessant
        {
          infl=calculInfluence(SS,F,T);
          ss=SS;
        }
      }
      return ss;
    }

    public SortStats getHealSpell(Fight fight, Fighter F, Fighter T)
    {
      if(fight==null||F==null)
        return null;
      int infl=0;
      SortStats ss=null;
      if(F.isCollector())
      {
        for(Map.Entry<Integer, SortStats> SS : World.world.getGuild(F.getCollector().getGuildId()).getSpells().entrySet())
        {
          if(SS.getValue()==null)
            continue;
          if(infl<calculInfluenceHeal(SS.getValue())&&calculInfluenceHeal(SS.getValue())!=0&&fight.canCastSpell1(F,SS.getValue(),T.getCell(),-1))//Si le sort est plus interessant
          {
            infl=calculInfluenceHeal(SS.getValue());
            ss=SS.getValue();
          }
        }
      }
      else
      {
        for(Map.Entry<Integer, SortStats> SS : F.getMob().getSpells().entrySet())
        {
          if(SS.getValue()==null)
            continue;
          if(infl<calculInfluenceHeal(SS.getValue())&&calculInfluenceHeal(SS.getValue())!=0&&fight.canCastSpell1(F,SS.getValue(),T.getCell(),-1))//Si le sort est plus interessant
          {
            infl=calculInfluenceHeal(SS.getValue());
            ss=SS.getValue();
          }
        }
      }
      return ss;
    }

    public int moveautourIfPossible(Fight fight, Fighter F, Fighter T)
    {
      if(fight==null||F==null||T==null)
        return 0;
      if(F.getCurPm(fight)<=0)
        return 0;
      GameMap map=fight.getMap();
      if(map==null)
        return 0;
      GameCase cell=F.getCell();
      if(cell==null)
        return 0;
      GameCase cell2=T.getCell();
      if(cell2==null)
        return 0;
      if(PathFinding.isNextTo(map,cell.getId(),cell2.getId()))
        return 0;
      int nbrcase=0;
      int cellID=PathFinding.getNearestCellAroundGA(map,cell2.getId(),cell.getId(),null);

      if(cellID==-1)
      {
        Map<Integer, Fighter> ennemys=getLowHpEnnemyList(fight,F);
        for(Map.Entry<Integer, Fighter> target : ennemys.entrySet())
        {
          int cellID2=PathFinding.getNearestCellAroundGA(map,target.getValue().getCell().getId(),cell.getId(),null);
          if(cellID2!=-1)
          {
            cellID=cellID2;
            break;
          }
        }
      }

      ArrayList<GameCase> path=new AstarPathfinding(fight.getMapOld(),fight,cell.getId(),cellID).getShortestPath(-1);

      if(path==null||path.isEmpty())
        return 0;

      ArrayList<GameCase> finalPath=new ArrayList<GameCase>();
      for(int a=0;a<F.getCurPm(fight);a++)
      {
        if(path.size()==a)
          break;
        finalPath.add(path.get(a));
      }

      String pathstr="";
      try
      {
        int curCaseID=cell.getId();
        int curDir=0;
        for(GameCase c : finalPath)
        {
          char d=PathFinding.getDirBetweenTwoCase(curCaseID,c.getId(),map,true);
          if(d==0)
            return 0;//Ne devrait pas arriver :O
          if(curDir!=d)
          {
            if(finalPath.indexOf(c)!=0)
              pathstr+=World.world.getCryptManager().cellID_To_Code(curCaseID);
            pathstr+=d;
          }
          curCaseID=c.getId();

          nbrcase=nbrcase+1;
        }
        if(curCaseID!=cell.getId())
          pathstr+=World.world.getCryptManager().cellID_To_Code(curCaseID);
      }
      catch(Exception e)
      {
        e.printStackTrace();
      }
      //Création d'une GameAction
      GameAction GA=new GameAction(0,1,"");
      GA.args=pathstr;
      if(!fight.onFighterDeplace(F,GA))
        return 0;

      return nbrcase*Config.getInstance().AIMovementCellDelay+Config.getInstance().AIMovementFlatDelay;
    }



    public int moveIfPossiblecontremur(Fight fight, Fighter F, Fighter T)
    {
      if(fight==null)
        return 0;
      if(F==null)
        return 0;
      if(T==null)
        return 0;
      if(F.getCurPm(fight)<=0)
        return 0;
      GameMap map=fight.getMap();
      if(map==null)
        return 0;
      GameCase cell=F.getCell();
      if(cell==null)
        return 0;
      GameCase cell2=T.getCell();
      if(cell2==null)
        return 0;
      if(PathFinding.isNextTo(map,cell.getId(),cell2.getId()))
        return 0;
      int nbrcase=0;

      int cellID=PathFinding.getNearenemycontremur(map,cell2.getId(),cell.getId(),null);
      //On demande le chemin plus court
      //Mais le chemin le plus court ne prend pas en compte les bords de map.
      if(cellID==-1)
      {
        Map<Integer, Fighter> ennemys=getLowHpEnnemyList(fight,F);
        for(Map.Entry<Integer, Fighter> target : ennemys.entrySet())
        {
          int cellID2=PathFinding.getNearestCellAroundGA(map,target.getValue().getCell().getId(),cell.getId(),null);
          if(cellID2!=-1)
          {
            cellID=cellID2;
            break;
          }
        }
      }

      ArrayList<GameCase> path=new AstarPathfinding(fight.getMapOld(),fight,cell.getId(),cellID).getShortestPath(-1);

      if(path==null||path.isEmpty())
        return 0;

      ArrayList<GameCase> finalPath=new ArrayList<GameCase>();
      for(int a=0;a<F.getCurPm(fight);a++)
      {
        if(path.size()==a)
          break;
        finalPath.add(path.get(a));
      }
      String pathstr="";
      try
      {
        int curCaseID=cell.getId();
        int curDir=0;
        for(GameCase c : finalPath)
        {
          char d=PathFinding.getDirBetweenTwoCase(curCaseID,c.getId(),map,true);
          if(d==0)
            return 0;//Ne devrait pas arriver :O
          if(curDir!=d)
          {
            if(finalPath.indexOf(c)!=0)
              pathstr+=World.world.getCryptManager().cellID_To_Code(curCaseID);
            pathstr+=d;
          }
          curCaseID=c.getId();

          nbrcase=nbrcase+1;
        }
        if(curCaseID!=cell.getId())
          pathstr+=World.world.getCryptManager().cellID_To_Code(curCaseID);
      }
      catch(Exception e)
      {
        e.printStackTrace();
      }
      //Création d'une GameAction
      GameAction GA=new GameAction(0,1,"");
      GA.args=pathstr;
      if(!fight.onFighterDeplace(F,GA))
        return 0;

      return nbrcase*Config.getInstance().AIMovementCellDelay+Config.getInstance().AIMovementFlatDelay;
    }

    public int movediagIfPossible(Fight fight, Fighter F, Fighter T)
    {
      if(fight==null)
        return 0;
      if(F==null)
        return 0;
      if(T==null)
        return 0;
      if(F.getCurPm(fight)<=0)
        return 0;
      GameMap map=fight.getMap();
      if(map==null)
        return 0;
      GameCase cell=F.getCell();
      if(cell==null)
        return 0;
      GameCase cell2=T.getCell();
      if(cell2==null)
        return 0;
      if(PathFinding.isNextTo(map,cell.getId(),cell2.getId()))
        return 0;
      int nbrcase=0;

      int cellID=PathFinding.getNearestCellDiagGA(map,cell2.getId(),cell.getId(),null);
      //On demande le chemin plus court
      //Mais le chemin le plus court ne prend pas en compte les bords de map.
      if(cellID==-1)
      {
        Map<Integer, Fighter> ennemys=getLowHpEnnemyList(fight,F);
        for(Map.Entry<Integer, Fighter> target : ennemys.entrySet())
        {
          int cellID2=PathFinding.getNearestCellDiagGA(map,target.getValue().getCell().getId(),cell.getId(),null);
          if(cellID2!=-1)
          {
            cellID=cellID2;
            break;
          }
        }
      }

      ArrayList<GameCase> path=new AstarPathfinding(fight.getMapOld(),fight,cell.getId(),cellID).getShortestPath(-1);
      if(path==null||path.isEmpty())
        return 0;

      ArrayList<GameCase> finalPath=new ArrayList<GameCase>();
      for(int a=0;a<F.getCurPm(fight);a++)
      {
        if(path.size()==a)
          break;
        finalPath.add(path.get(a));
      }
      String pathstr="";
      try
      {
        int curCaseID=cell.getId();
        int curDir=0;
        for(GameCase c : finalPath)
        {
          char d=PathFinding.getDirBetweenTwoCase(curCaseID,c.getId(),map,true);
          if(d==0)
            return 0;//Ne devrait pas arriver :O
          if(curDir!=d)
          {
            if(finalPath.indexOf(c)!=0)
              pathstr+=World.world.getCryptManager().cellID_To_Code(curCaseID);
            pathstr+=d;
          }
          curCaseID=c.getId();

          nbrcase=nbrcase+1;
        }
        if(curCaseID!=cell.getId())
          pathstr+=World.world.getCryptManager().cellID_To_Code(curCaseID);
      }
      catch(Exception e)
      {
        e.printStackTrace();
      }
      //Création d'une GameAction
      GameAction GA=new GameAction(0,1,"");
      GA.args=pathstr;
      if(!fight.onFighterDeplace(F,GA))
        return 0;

      return nbrcase*Config.getInstance().AIMovementCellDelay+Config.getInstance().AIMovementFlatDelay;
    }

    public int moveenfaceIfPossible(Fight fight, Fighter F, Fighter T, int dist)
    {
      if(fight==null)
        return 0;
      if(F==null)
        return 0;
      if(T==null)
        return 0;
      if(F.getCurPm(fight)<=0)
        return 0;
      GameMap map=fight.getMap();
      if(map==null)
        return 0;
      GameCase cell=F.getCell();
      if(cell==null)
        return 0;
      GameCase cell2=T.getCell();
      if(cell2==null)
        return 0;
      if(PathFinding.isNextTo(map,cell.getId(),cell2.getId()))
        return 0;
      int nbrcase=0;

      int targetCell=PathFinding.getNearestligneGA(fight,cell2.getId(),cell.getId(),null,dist);
      if(!PathFinding.checkLoS(fight.getMap(),targetCell,T.getCell().getId(),null,false))
        targetCell=-1;

      if(targetCell==-1)
      {
        Map<Integer, Fighter> ennemys=getLowHpEnnemyList(fight,F);
        for(Map.Entry<Integer, Fighter> target : ennemys.entrySet())
        {
          int tempTargetCell=PathFinding.getNearestligneGA(fight,target.getValue().getCell().getId(),cell.getId(),null,dist);
          if(!PathFinding.checkLoS(fight.getMap(),tempTargetCell,T.getCell().getId(),null,false))
            tempTargetCell=-1;
          if(tempTargetCell!=-1)
          {
            targetCell=tempTargetCell;
            break;
          }
        }
      }

      System.out.println("targetCell: "+targetCell);
      if(targetCell==-1)
        return 0;

      ArrayList<GameCase> path=new AstarPathfinding(fight.getMapOld(),fight,cell.getId(),targetCell).getShortestPath(0); //0pour en ligne
      if(path==null||path.isEmpty())
        return 0;

      ArrayList<GameCase> finalPath=new ArrayList<GameCase>();
      boolean ligneok=false;
      for(int a=0;a<F.getCurPm(fight);a++)
      {
        if(path.size()==a||ligneok==true)
          break;
        if(PathFinding.casesAreInSameLine(fight.getMap(),path.get(a).getId(),T.getCell().getId(),'z',70)&&PathFinding.checkLoS(fight.getMap(),path.get(a).getId(),T.getCell().getId(),null,false))
          ligneok=true;
        finalPath.add(path.get(a));
      }
      if(ligneok==false)
        return 0;

      String pathstr="";
      try
      {
        int curCaseID=cell.getId();
        int curDir=0;
        for(GameCase c : finalPath)
        {
          char d=PathFinding.getDirBetweenTwoCase(curCaseID,c.getId(),map,true);
          if(d==0)
            return 0;
          if(curDir!=d)
          {
            if(finalPath.indexOf(c)!=0)
              pathstr+=World.world.getCryptManager().cellID_To_Code(curCaseID);
            pathstr+=d;
          }
          curCaseID=c.getId();

          nbrcase=nbrcase+1;
        }
        if(curCaseID!=cell.getId())
          pathstr+=World.world.getCryptManager().cellID_To_Code(curCaseID);
      }
      catch(Exception e)
      {
        e.printStackTrace();
      }
      GameAction GA=new GameAction(0,1,"");
      GA.args=pathstr;
      if(!fight.onFighterDeplace(F,GA))
        return 0;

      return nbrcase*Config.getInstance().AIMovementCellDelay+Config.getInstance().AIMovementFlatDelay;
    }

    public int movecacIfPossible(Fight fight, Fighter F, Fighter T)
    {
        try {
            if (fight == null)
                return 0;
            if (F == null)
                return 0;
            if (T == null)
                return 0;
            if (F.getCurPm(fight) <= 0)
                return 0;
            GameMap map = fight.getMap();
            if (map == null)
                return 0;
            GameCase cell = F.getCell();
            if (cell == null)
                return 0;
            GameCase cell2 = T.getCell();
            if (cell2 == null)
                return 0;
            if (PathFinding.isNextTo(map, cell.getId(), cell2.getId()))
                return 0;
            int nbrcase = 0;

            int cellID = PathFinding.getNearestCellAround(map, cell2.getId(), cell.getId(), null);
            if (cellID == -1) {
                Map<Integer, Fighter> ennemys = getLowHpEnnemyList(fight, F);
                for (Map.Entry<Integer, Fighter> target : ennemys.entrySet()) {
                    int cellID2 = PathFinding.getNearestCellAround(map, target.getValue().getCell().getId(), cell.getId(), null);
                    if (cellID2 != -1) {
                        cellID = cellID2;
                        break;
                    }
                }
            }

            ArrayList<GameCase> path = new AstarPathfinding(fight.getMapOld(), fight, cell.getId(), cellID).getShortestPath(-1);

            if (path == null || path.isEmpty())
                return 0;

            ArrayList<GameCase> finalPath = new ArrayList<GameCase>();
            for (int a = 0; a < F.getCurPm(fight); a++) {
                if (path.size() == a)
                    break;
                finalPath.add(path.get(a));
            }
            String pathstr = "";
            try {
                int curCaseID = cell.getId();
                int curDir = 0;
                for (GameCase c : finalPath) {
                    char d = PathFinding.getDirBetweenTwoCase(curCaseID, c.getId(), map, true);
                    if (d == 0)
                        return 0;//Ne devrait pas arriver :O
                    if (curDir != d) {
                        if (finalPath.indexOf(c) != 0)
                            pathstr += World.world.getCryptManager().cellID_To_Code(curCaseID);
                        pathstr += d;
                    }
                    curCaseID = c.getId();

                    nbrcase = nbrcase + 1;
                }
                if (curCaseID != cell.getId())
                    pathstr += World.world.getCryptManager().cellID_To_Code(curCaseID);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //Création d'une GameAction
            GameAction GA = new GameAction(0, 1, "");
            GA.args = pathstr;
            if (!fight.onFighterDeplace(F, GA))
                return 0;

            return nbrcase * Config.getInstance().AIMovementCellDelay + Config.getInstance().AIMovementFlatDelay;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return 0;
        }
    }

    public Fighter getNearestFriendInvoc(Fight fight, Fighter fighter)
    {
      if(fight==null||fighter==null)
        return null;
      int dist=1000;
      Fighter curF=null;
      for(Fighter f : fight.getFighters(3))
      {
        if(f.isDead())
          continue;
        if(f==fighter)
          continue;
        if(f.getTeam2()==fighter.getTeam2()&&f.isInvocation())//Si c'est un ami et si c'est une invocation
        {
          int d=PathFinding.getDistanceBetween(fight.getMap(),fighter.getCell().getId(),f.getCell().getId());
          if(d<dist)
          {
            dist=d;
            curF=f;
          }
        }
      }
      return curF;
    }

    public Fighter getNearestFriendNoInvok(Fight fight, Fighter fighter)
    {
      if(fight==null||fighter==null)
        return null;
      int dist=1000;
      Fighter curF=null;
      for(Fighter f : fight.getFighters(3))
      {
        if(f.isDead())
          continue;
        if(f==fighter)
          continue;
        if(f.getTeam2()==fighter.getTeam2()&&!f.isInvocation())//Si c'est un ami et si c'est une invocation
        {
          int d=PathFinding.getDistanceBetween(fight.getMap(),fighter.getCell().getId(),f.getCell().getId());
          if(d<dist)
          {
            dist=d;
            curF=f;
          }
        }
      }
      return curF;
    }

    public Fighter getNearestFriend(Fight fight, Fighter fighter)
    {
      if(fight==null||fighter==null)
        return null;
      int dist=1000;
      Fighter curF=null;
      for(Fighter f : fight.getFighters(3))
      {
        if(f.isDead())
          continue;
        if(f==fighter)
          continue;
        if(f.getTeam()==fighter.getTeam())//Si c'est un ami
        {
          int d=PathFinding.getDistanceBetween(fight.getMap(),fighter.getCell().getId(),f.getCell().getId());
          if(d<dist)
          {
            dist=d;
            curF=f;
          }
        }
      }
      return curF;
    }

    public Fighter getNearestEnnemy(Fight fight, Fighter fighter)
    {
      if(fight==null||fighter==null)
        return null;
      int dist=1000;
      Fighter curF=null;
      for(Fighter f : fight.getFighters(3))
      {
        if(f.isDead())
          continue;
        if(f.getTeam2()!=fighter.getTeam2())//Si c'est un ennemis
        {
          int d=PathFinding.getDistanceBetween(fight.getMap(),fighter.getCell().getId(),f.getCell().getId());
          if(d<dist)
          {
            dist=d;
            curF=f;
          }
        }
      }
      return curF;
    }
    
    
    public Fighter getNearestEnnemyNotListed(Fight fight, Fighter fighter, ArrayList<Fighter> fightList)
    {
      if(fight==null||fighter==null)
        return null;
      int dist=1000;
      Fighter curF=null;
      for(Fighter f : fight.getFighters(3))
      {
        if(f.isDead()||fightList.contains(f))
          continue;
        if(f.getTeam2()!=fighter.getTeam2())//Si c'est un ennemis
        {
          int d=PathFinding.getDistanceBetween(fight.getMap(),fighter.getCell().getId(),f.getCell().getId());
          if(d<dist)
          {
            dist=d;
            curF=f;
          }
        }
      }
      return curF;
    }
    
    
    public Fighter getNearestEnnemyNotListedLos(Fight fight, Fighter fighter, ArrayList<Fighter> fightList)
    {
      if(fight==null||fighter==null)
        return null;
      int dist=1000;
      Fighter curF=null;
      for(Fighter f : fight.getFighters(3))
      {
        if(f.isDead()||fightList.contains(f))
          continue;
        if(f.getTeam()!=fighter.getTeam())//Si c'est un ennemis
        {
          if(PathFinding.casesAreInSameLine(fight.getMap(),fighter.getCell().getId(),f.getCell().getId(),'z',70))
            if(!PathFinding.checkLoS(fight.getMap(),fighter.getCell().getId(),f.getCell().getId(),null,false))
              continue;

          int d=PathFinding.getDistanceBetween(fight.getMap(),fighter.getCell().getId(),f.getCell().getId());
          if(d<dist)
          {
            dist=d;
            curF=f;
          }
        }
      }
      return curF;
    }

    public Fighter getNearestEnnemynbrcasemax(Fight fight, Fighter fighter, int distmin, int distmax)
    {
      if(fight==null||fighter==null)
        return null;
      Fighter curF=null;
      for(Fighter f : fight.getFighters(3))
      {
        if(f.isDead())
          continue;
        if(f.getMob()!=null&&f.getMob().getTemplate()!=null)
        {
          boolean ok=false;
          for(int i : Constant.STATIC_INVOCATIONS)
            if(i==f.getMob().getTemplate().getId())
              ok=true;
          if(ok)
            continue;
        }

        if(f.getTeam2()!=fighter.getTeam2())//Si c'est un ennemis
        {
          int d=PathFinding.getDistanceBetween(fight.getMap(),fighter.getCell().getId(),f.getCell().getId());
          if(d<distmax)
          {
            if(d>distmin)
            {
              distmax=d;
              curF=f;
            }
          }
        }
      }
      return curF;
    }
    
    public Fighter getNearEnnemylignenbrcasemax(Fight fight, Fighter fighter, int distmin, int distmax)
    {
      if(fight==null||fighter==null)
        return null;
      Fighter curF=null;

      for(Fighter f : fight.getFighters(3))
      {
        if(f.isDead())
          continue;
        if(f.getTeam2()!=fighter.getTeam2())
          curF=PathFinding.getNearestligneenemy(fight.getMap(),fighter.getCell().getId(),f,distmax);

      }
      return curF;
    }

    public Fighter getNearEnnemylignenbrcasemaxNotListed(Fight fight, Fighter fighter, int distmin, int distmax, ArrayList<Fighter> fightList)
    {
      if(fight==null||fighter==null)
        return null;
      Fighter curF=null;

      for(Fighter f : fight.getFighters(3))
      {
        if(f.isDead()||fightList.contains(f))
          continue;
        if(f.getTeam2()!=fighter.getTeam2())
          curF=PathFinding.getNearestligneenemy(fight.getMap(),fighter.getCell().getId(),f,distmax);
      }
      return curF;
    }

    public Fighter getNearestEnnemymurnbrcasemax(Fight fight, Fighter fighter, int distmin, int distmax)
    {
      if(fight==null||fighter==null)
        return null;
      Fighter curF=null;
      for(Fighter f : fight.getFighters(3))
      {
        if(f.isDead())
          continue;
        if(f.getTeam2()!=fighter.getTeam2())//Si c'est un ennemis
        {
          int d=PathFinding.getDistanceBetween(fight.getMap(),fighter.getCell().getId(),f.getCell().getId());
          if(d<distmax)
          {
            if(d>distmin)
            {
              if(PathFinding.getNearenemycontremur2(fight.getMap(),f.getCell().getId(),fighter.getCell().getId(),null,fighter)==-1)
                continue;
              distmax=d;
              curF=f;
            }
          }
        }
      }
      if(curF==null)
      {
        for(Fighter f : fight.getFighters(3))
        {
          if(f.isDead())
            continue;
          if(f.getTeam2()!=fighter.getTeam2())//Si c'est un ennemis
          {
            int d=PathFinding.getDistanceBetween(fight.getMap(),fighter.getCell().getId(),f.getCell().getId());
            if(d<distmax)
            {
              if(d>distmin)
              {
                distmax=d;
                curF=f;
              }
            }
          }
        }
      }
      return curF;
    }

    public Fighter getNearestAllnbrcasemax(Fight fight, Fighter fighter, int distmin, int distmax)
    {
      if(fight==null||fighter==null)
        return null;
      Fighter curF=null;
      for(Fighter f : fight.getFighters(3))
      {
        if(f.isDead())
          continue;

        int d=PathFinding.getDistanceBetween(fight.getMap(),fighter.getCell().getId(),f.getCell().getId());
        if(d<distmax)
        {
          if(d>distmin)
          {
            distmax=d;
            curF=f;
          }
        }
      }
      return curF;
    }

    public Fighter getNearestAminbrcasemax(Fight fight, Fighter fighter, int distmin, int distmax)
    {
      if(fight==null||fighter==null)
        return null;
      Fighter curF=null;
      for(Fighter f : fight.getFighters(3))
      {
        if(f.isDead())
          continue;
        if(f.getTeam2()==fighter.getTeam2())//Si c'est un ennemis
        {
          int d=PathFinding.getDistanceBetween(fight.getMap(),fighter.getCell().getId(),f.getCell().getId());
          if(d<distmax)
          {
            if(d>distmin)
            {
              distmax=d;
              curF=f;
            }
          }
        }
      }
      return curF;
    }

    public Fighter getNearestAminoinvocnbrcasemax(Fight fight, Fighter fighter, int distmin, int distmax)
    {
      if(fight==null||fighter==null)
        return null;
      Fighter curF=null;
      for(Fighter f : fight.getFighters(3))
      {
        if(f.isDead())
          continue;
        if(f.getTeam2()==fighter.getTeam2()&&f.isInvocation()==false)//Si c'est un ennemis
        {
          int d=PathFinding.getDistanceBetween(fight.getMap(),fighter.getCell().getId(),f.getCell().getId());
          if(d<distmax)
          {
            if(d>distmin)
            {
              distmax=d;
              curF=f;
            }
          }
        }
      }
      return curF;
    }
    
    //v2.8 - unconditional moving to summoner
    public Fighter getSummoner(Fight fight, Fighter fighter, int maxDistance)
    {
      if(fight==null||fighter==null||fighter.getInvocator()==null)
        return null;
      Fighter curF=null;
      for(Fighter f : fight.getFighters(3))
      {
        if(f.isDead())
          continue;
        if(f==fighter)
          continue;
        if(fighter.getInvocator()==f)
        {
          int d=PathFinding.getDistanceBetween(fight.getMap(),fighter.getCell().getId(),f.getCell().getId());
          if(d<maxDistance)
            curF=f;
          break;
        }
      }
      return curF;
    }

    public Fighter getNearestinvocateurnbrcasemax(Fight fight, Fighter fighter, int distmin, int distmax)
    {
      if(fight==null||fighter==null)
        return null;
      Fighter curF=null;
      for(Fighter f : fight.getFighters(3))
      {
        if(f.isDead())
          continue;
        if(f.getTeam2()==fighter.getTeam2()&&!f.isInvocation()&&f==fighter.getInvocator())//Si c'est un ennemis
        {
          int d=PathFinding.getDistanceBetween(fight.getMap(),fighter.getCell().getId(),f.getCell().getId());
          if(d<distmax)
          {
            if(d>distmin)
            {
              distmax=d;
              curF=f;
            }
          }
        }
      }
      if(curF==null)
        for(Fighter f : fight.getFighters(3))
        {
          if(f.isDead())
            continue;
          if(f.getTeam2()==fighter.getTeam2()&&!f.isInvocation())//Si c'est un ennemis
          {
            int d=PathFinding.getDistanceBetween(fight.getMap(),fighter.getCell().getId(),f.getCell().getId());
            if(d<distmax)
            {
              if(d>distmin)
              {
                distmax=d;
                curF=f;
              }
            }
          }
        }
      return curF;
    }

    public Fighter getNearestInvocnbrcasemax(Fight fight, Fighter fighter, int distmin, int distmax)
    {
      if(fight==null||fighter==null)
        return null;
      Fighter curF=null;
      for(Fighter f : fight.getFighters(3))
      {
        if(f.isDead())
          continue;
        if(f.getTeam2()==fighter.getTeam2()&&f.isInvocation())//Si c'est un ennemis
        {
          int d=PathFinding.getDistanceBetween(fight.getMap(),fighter.getCell().getId(),f.getCell().getId());
          if(d<distmax)
          {
            if(d>distmin)
            {
              distmax=d;
              curF=f;
            }
          }
        }
      }
      return curF;
    }

    public Map<Integer, Fighter> getLowHpEnnemyList(Fight fight, Fighter fighter)
    {
      if(fight==null||fighter==null)
        return null;
      Map<Integer, Fighter> list=new HashMap<Integer, Fighter>();
      Map<Integer, Fighter> ennemy=new HashMap<Integer, Fighter>();
      for(Fighter f : fight.getFighters(3))
      {
        if(f.isDead())
          continue;
        if(f==fighter)
          continue;
        if(f.getTeam2()!=fighter.getTeam2())
        {
          ennemy.put(f.getId(),f);
        }
      }
      int i=0,i2=ennemy.size();
      int curHP=10000;
      Fighter curEnnemy=null;

      while(i<i2)
      {
        curHP=200000;
        curEnnemy=null;
        for(Map.Entry<Integer, Fighter> t : ennemy.entrySet())
        {
          if(t.getValue().getPdv()<curHP)
          {
            curHP=t.getValue().getPdv();
            curEnnemy=t.getValue();
          }
        }
        list.put(curEnnemy.getId(),curEnnemy);
        ennemy.remove(curEnnemy.getId());
        i++;
      }
      return list;
    }
    
    
    public int attackIfPossible(Fight fight, Fighter fighter, List<SortStats> Spell)// 0 = Rien, 5 = EC, 666 = NULL, 10 = SpellNull ou ActionEnCour ou Can'tCastSpell, 0 = AttaqueOK
    {
      if(fight==null||fighter==null)
        return -1;
      Map<Integer, Fighter> ennemyList=getLowHpEnnemyList(fight,fighter);
      SortStats SS=null;
      Fighter target=null;

      for(Map.Entry<Integer, Fighter> t : ennemyList.entrySet())
      {
        SS=getBestSpellForTargetDopeul(fight,fighter,t.getValue(),fighter.getCell().getId(),Spell);

        if(SS!=null)
        {
          target=t.getValue();
          break;
        }
      }

      int curTarget=0,cell=0;
      SortStats SS2=null;

      for(SortStats S : Spell)
      {
        int targetVal=getBestTargetZone(fight,fighter,S,fighter.getCell().getId(),false);
        if(targetVal==-1||targetVal==0)
          continue;
        int nbTarget=targetVal/1000;
        int cellID=targetVal-nbTarget*1000;
        if(nbTarget>curTarget)
        {
          curTarget=nbTarget;
          cell=cellID;
          SS2=S;
        }
      }

      if(curTarget>0&&cell>=15&&cell<=463&&SS2!=null)
      {
        int attack=fight.tryCastSpell(fighter,SS2,cell);
        if(attack==0)
          return SS2.getSpell().getDuration();
      }

      else
      {
        if(target==null||SS==null)
          return -1;
        int attack=fight.tryCastSpell(fighter,SS,target.getCell().getId());
        if(attack==0)
          return SS.getSpell().getDuration();
      }
      return -1;
    }

    public int attackIfPossibleglyph(Fight fight, Fighter fighter, Fighter f, List<SortStats> Spell)// 0 = Rien, 5 = EC, 666 = NULL, 10 = SpellNull ou ActionEnCour ou Can'tCastSpell, 0 = AttaqueOK
    {
      if(fight==null||fighter==null||f==null)
        return 0;
      int curTarget=0,cell=0;
      SortStats SS2=null;
      for(SortStats S : Spell)
      {
        if(PathFinding.casesAreInSameLine(fight.getMap(),fighter.getCell().getId(),f.getCell().getId(),'z',70))
        {

          cell=PathFinding.newCaseAfterPush(fight,fighter.getCell(),f.getCell(),-1);
          if(fight.canCastSpell1(fighter,S,fight.getMap().getCase(cell),-1))
          {
            SS2=S;
            curTarget=100;
          }
        }

        if(S.getSpellID()==2037)
        {
          cell=PathFinding.getCaseBetweenEnemy(f.getCell().getId(),fight.getMap(),fight);
          SS2=S;
          if(fight.canCastSpell1(fighter,SS2,fight.getMap().getCase(cell),-1))
            curTarget=100;
        }
      }
      if(curTarget>0&&cell>=15&&cell<=463&&SS2!=null)
      {
        int attack=fight.tryCastSpell(fighter,SS2,cell);
        if(attack!=0)
          return SS2.getSpell().getDuration();
      }
      return 0;
    }

    public int attackIfPossibleCM1(Fight fight, Fighter fighter, List<SortStats> Spell)// 0 = Rien, 5 = EC, 666 = NULL, 10 = SpellNull ou ActionEnCour ou Can'tCastSpell, 0 = AttaqueOK
    {
      if(fight==null||fighter==null)
        return 0;
      int curTarget=0,cell=0;
      SortStats SS2=null;
      Map<Integer, Fighter> ennemyList=getLowHpEnnemyList(fight,fighter);
      for(SortStats S : Spell)
      {
        if(S.getSpellID()==483)
          continue;

        if(S.getSpellID()==977)
          for(Map.Entry<Integer, Fighter> f : ennemyList.entrySet())
            if(f.getValue()!=null)
              if(!f.getValue().isInvocation())
                if(PathFinding.casesAreInSameLine(fight.getMap(),fighter.getCell().getId(),f.getValue().getCell().getId(),'z',12))
                {

                  cell=fighter.getCell().getId();
                  if(fight.canCastSpell1(fighter,S,fight.getMap().getCase(cell),-1))
                  {
                    SS2=S;
                    curTarget=100;
                  }
                }
        if(S.getSpellID()==484)
          for(Map.Entry<Integer, Fighter> f : ennemyList.entrySet())
            if(f.getValue()!=null)
              if(!f.getValue().isInvocation())
                if(PathFinding.casesAreInSameLine(fight.getMap(),fighter.getCell().getId(),f.getValue().getCell().getId(),'z',4))
                {

                  cell=fighter.getCell().getId();
                  if(fight.canCastSpell1(fighter,S,fight.getMap().getCase(cell),-1))
                  {
                    SS2=S;
                    curTarget=100;
                  }
                }
      }
      if(curTarget>0&&cell>=15&&cell<=463&&SS2!=null)
      {
        int attack=fight.tryCastSpell(fighter,SS2,cell);
        if(attack!=0)
          return SS2.getSpell().getDuration();
      }
      return 0;
    }

    public int attackIfPossiblevisee(Fight fight, Fighter fighter, Fighter target, List<SortStats> Spell)// 0 = Rien, 5 = EC, 666 = NULL, 10 = SpellNull ou ActionEnCour ou Can'tCastSpell, 0 = AttaqueOK
    {
      if(fight==null||fighter==null)
        return -1;
      SortStats SS=null;

      SS=getBestSpellForTargetDopeul(fight,fighter,target,fighter.getCell().getId(),Spell);
      if(target==null||SS==null)
        return 0;
      int attack=fight.tryCastSpell(fighter,SS,target.getCell().getId());
      if(attack!=0)
        return SS.getSpell().getDuration();
      return -1;
    }

    public int attackAllIfPossible(Fight fight, Fighter fighter, List<SortStats> Spell)// 0 = Rien, 5 = EC, 666 = NULL, 10 = SpellNull ou ActionEnCour ou Can'tCastSpell, 0 = AttaqueOK
    {
      if(fight==null||fighter==null)
        return 0;
      Fighter ennemy=getNearestAllnbrcasemax(fight,fighter,0,2);
      SortStats SS=null;
      Fighter target=null;
      SS=getBestSpellForTargetDopeul(fight,fighter,ennemy,fighter.getCell().getId(),Spell);

      if(SS!=null)
      {
        target=ennemy;
      }
      int curTarget=0,cell=0;
      SortStats SS2=null;

      for(SortStats S : Spell)
      {
        int targetVal=getBestTargetZone(fight,fighter,S,fighter.getCell().getId(),false);
        if(targetVal==-1||targetVal==0)
          continue;
        int nbTarget=targetVal/1000;
        int cellID=targetVal-nbTarget*1000;
        if(nbTarget>curTarget)
        {
          curTarget=nbTarget;
          cell=cellID;
          SS2=S;
        }
      }
      if(curTarget>0&&cell>=15&&cell<=463&&SS2!=null)
      {
        int attack=fight.tryCastSpell(fighter,SS2,cell);
        if(attack!=0)
          return SS2.getSpell().getDuration();
      }
      else
      {
        if(target==null||SS==null)
          return 0;
        int attack=fight.tryCastSpell(fighter,SS,target.getCell().getId());
        if(attack!=0)
          return SS.getSpell().getDuration();
      }
      return 0;
    }

    public int moveToAction(Fight fight, Fighter current, Fighter target, short action, ArrayList<Integer> noSpell, int index, boolean inLine)
    {
        try {
            if (fight == null || current == null)
                return 0;
            Map<Integer, Fighter> ennemyList = getLowHpEnnemyList(fight, current);

            if (current.getCurPm(fight) <= 0)
                return 2;

            boolean canAttack = false;

            ArrayList<GameCase> path = new AstarPathfinding(fight.getMapOld(), fight, current.getCell().getId(), getNearestEnnemy(fight, current).getCell().getId()).getShortestPath(-1);

            int caseLaunch = -1;
            int newCase = -1;
            int bestNbTarget = 0;
            int _loc1_ = 1;
            int targetVal = 0;
            int nbTarget = 0;
            int cellID = -1;
            GameCase newCell = current.getCell();
            SortStats bestSort = null;
            do {
                if (newCell != null) {
                    for (Map.Entry<Integer, Fighter> t : ennemyList.entrySet()) {
                        bestSort = getBestSpellForTarget(fight, current, t.getValue(), current.getCell().getId());
                        if (bestSort != null) {
                            target = t.getValue();
                            break;
                        }
                    }
                    if (target == null)
                        continue;
                    for (SortStats SS : current.getMob().getSpells().values()) {
                        if (noSpell.contains(SS.getSpellID()))
                            continue;
                        targetVal = getBestTargetZone(fight, current, SS, newCell.getId(), inLine);
                        if (targetVal != 0) {
                            nbTarget = targetVal / 1000;
                            cellID = targetVal - nbTarget * 1000;
                        } else {
                            cellID = target.getCell().getId();
                            nbTarget = 1;
                        }
                        if (fight.canCastSpell1(current, SS, fight.getMapOld().getCase(cellID), newCell.getId())) {
                            if (nbTarget > bestNbTarget) {
                                //canAttack = true;
                                bestSort = SS;
                                caseLaunch = cellID;
                                bestNbTarget = nbTarget;
                                newCase = newCell.getId();
                            }
                        }
                    }
                }
                newCell = path.get(_loc1_ - 1);
            } while (_loc1_++ < path.size() && _loc1_ <= current.getCurPm(fight) && !canAttack);

            if (caseLaunch != -1)
                canAttack = true;
            else if (newCase == -1 && index == 1)
                return 3;

            boolean result = true;
            if (newCase != current.getCell().getId()) {
                String pathstr = "";
                try {
                    int curCaseID = current.getCell().getId();
                    int curDir = 0;
                    for (GameCase c : path) {
                        if (curCaseID == c.getId())
                            continue; // Empêche le d == 0
                        char d = PathFinding.getDirBetweenTwoCase(curCaseID, c.getId(), fight.getMap(), true);
                        if (d == 0)
                            return 0;// Ne devrait pas arriver :O
                        if (curDir != d) {
                            if (path.indexOf(c) != 0)
                                pathstr += World.world.getCryptManager().cellID_To_Code(curCaseID);
                            pathstr += d;
                        }
                        curCaseID = c.getId();
                        if (c.getId() == newCase)
                            break;
                    }
                    if (curCaseID != current.getCell().getId())
                        pathstr += World.world.getCryptManager().cellID_To_Code(curCaseID);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Création d'une GameAction
                GameAction GA = new GameAction(0, 1, "");
                GA.args = pathstr;
                result = fight.onFighterDeplace(current, GA);
            }
            if (result && canAttack) {
                if (fight.canCastSpell1(current, bestSort, fight.getMapOld().getCase(caseLaunch), current.getCell().getId())) {
                    fight.tryCastSpell(current, bestSort, caseLaunch);
                    return 1;
                }
            } else if (result && !canAttack)
                return 1;

            return 3;
        }
        catch (Exception ex)
        {
            Logging.getInstance().write("Error", ex.getMessage() + "\n" + Arrays.toString(ex.getStackTrace()));
            return 3;
        }
    }

    public int moveToAttackIfPossible(Fight fight, Fighter fighter)
    {
        try {
            if (fight == null || fighter == null)
                return -1;
            GameMap m = fight.getMap();
            if (m == null)
                return -1;

            GameCase _c = fighter.getCell();
            if (_c == null)
                return -1;

            Fighter ennemy = getNearestEnnemy(fight, fighter);
            if (ennemy == null)
                return -1;

            int distMin = PathFinding.getDistanceBetween(m, _c.getId(), ennemy.getCell().getId());
            ArrayList<SortStats> sorts = getLaunchableSort(fighter, fight, distMin);
            if (sorts == null)
                return -1;
            ArrayList<Integer> cells = PathFinding.getListCaseFromFighter(fight, fighter, fighter.getCell().getId(), sorts);
            if (cells == null)
                return -1;
            ArrayList<Fighter> targets = getPotentialTarget(fight, fighter, sorts);
            if (targets == null)
                return -1;
            int CellDest = 0;
            SortStats bestSS = null;
            int[] bestInvok = {1000, 0, 0, 0, -1};
            int[] bestFighter = {1000, 0, 0, 0, -1};
            int targetCell = -1;
            for (int i : cells) {
                for (SortStats S : sorts) {
                    int targetVal = getBestTargetZone(fight, fighter, S, i, false);
                    if (targetVal > 0) {
                        int nbTarget = targetVal / 1000;
                        int cellID = targetVal - nbTarget * 1000;
                        if (fight.getMapOld().getCase(cellID) != null && nbTarget > 0) {
                            if (fight.canCastSpell1(fighter, S, fight.getMapOld().getCase(cellID), i)) {
                                int dist = PathFinding.getDistanceBetween(fight.getMapOld(), fighter.getCell().getId(), i);
                                if (dist < bestFighter[0] || bestFighter[2] < nbTarget) {

                                    bestFighter[0] = dist;
                                    bestFighter[1] = i;
                                    bestFighter[2] = nbTarget;
                                    bestFighter[4] = cellID;
                                    bestSS = S;
                                }
                            }
                        }
                    } else {
                        for (Fighter T : targets) {
                            if (fight.canCastSpell1(fighter, S, T.getCell(), i)) {
                                int dist = PathFinding.getDistanceBetween(fight.getMapOld(), fighter.getCell().getId(), T.getCell().getId());
                                if (!PathFinding.isCACwithEnnemy(fighter, targets)) {
                                    if (T.isInvocation()) {
                                        if (dist < bestInvok[0]) {
                                            bestInvok[0] = dist;
                                            bestInvok[1] = i;
                                            bestInvok[2] = 1;
                                            bestInvok[3] = 1;
                                            bestInvok[4] = T.getCell().getId();
                                            bestSS = S;
                                        }

                                    } else {
                                        if (dist < bestFighter[0]) {
                                            bestFighter[0] = dist;
                                            bestFighter[1] = i;
                                            bestFighter[2] = 1;
                                            bestFighter[3] = 0;
                                            bestFighter[4] = T.getCell().getId();
                                            bestSS = S;
                                        }

                                    }
                                } else {
                                    if (dist < bestFighter[0]) {
                                        bestFighter[0] = dist;
                                        bestFighter[1] = i;
                                        bestFighter[2] = 1;
                                        bestFighter[3] = 0;
                                        bestFighter[4] = T.getCell().getId();
                                        bestSS = S;
                                    }
                                }
                            }
                            //}
                        }
                    }
                }
            }
            if (bestFighter[1] != 0) {
                CellDest = bestFighter[1];
                targetCell = bestFighter[4];
            } else if (bestInvok[1] != 0) {
                CellDest = bestInvok[1];
                targetCell = bestInvok[4];
            } else
                return -1;
            if (CellDest == 0)
                return -1;
            if (CellDest == fighter.getCell().getId())
                return targetCell + bestSS.getSpellID() * 1000;

            ArrayList<GameCase> path = new AstarPathfinding(fight.getMapOld(), fight, fighter.getCell().getId(), CellDest).getShortestPath(-1);

            if (path == null)
                return -1;
            String pathstr = "";
            try {
                int curCaseID = fighter.getCell().getId();
                int curDir = 0;
                path.add(fight.getMapOld().getCase(CellDest));
                for (GameCase c : path) {
                    if (curCaseID == c.getId())
                        continue; // Empêche le d == 0
                    char d = PathFinding.getDirBetweenTwoCase(curCaseID, c.getId(), m, true);
                    if (d == 0)
                        return -1;//Ne devrait pas arriver :O
                    if (curDir != d) {
                        if (path.indexOf(c) != 0)
                            pathstr += World.world.getCryptManager().cellID_To_Code(curCaseID);
                        pathstr += d;
                    }
                    curCaseID = c.getId();
                }
                if (curCaseID != fighter.getCell().getId())
                    pathstr += World.world.getCryptManager().cellID_To_Code(curCaseID);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //Création d'une GameAction
            GameAction GA = new GameAction(0, 1, "");
            GA.args = pathstr;
            fight.onFighterDeplace(fighter, GA);

            return targetCell + bestSS.getSpellID() * 1000;
        }catch (Exception ex)
        {
            Logging.getInstance().write("Error", ex.getMessage() + "\n" + Arrays.toString(ex.getStackTrace()));
            return -1;
        }
    }
    public String moveToAttackIfPossible2(Fight fight, Fighter fighter)
    {
        try {
            if (fight == null || fighter == null)
                return "";
            GameMap m = fight.getMap();
            if (m == null)
                return "";

            GameCase _c = fighter.getCell();
            if (_c == null)
                return "";

            Fighter ennemy = getNearestEnnemy(fight, fighter);
            if (ennemy == null)
                return "";

            int distMin = PathFinding.getDistanceBetween(m, _c.getId(), ennemy.getCell().getId());
            ArrayList<SortStats> sorts = getLaunchableSort(fighter, fight, distMin);
            if (sorts == null)
                return "";
            ArrayList<Integer> cells = PathFinding.getListCaseFromFighter(fight, fighter, fighter.getCell().getId(), sorts);
            if (cells == null)
                return "";
            ArrayList<Fighter> targets = getPotentialTarget(fight, fighter, sorts);
            if (targets == null)
                return "";
            int CellDest = 0;
            SortStats bestSS = null;
            int[] bestInvok = {1000, 0, 0, 0, -1};
            int[] bestFighter = {1000, 0, 0, 0, -1};
            int targetCell = -1;
            for (int i : cells) {
                for (SortStats S : sorts) {
                    int targetVal = getBestTargetZone(fight, fighter, S, i, false);
                    if (targetVal > 0) {
                        int nbTarget = targetVal / 1000;
                        int cellID = targetVal - nbTarget * 1000;
                        if (fight.getMapOld().getCase(cellID) != null && nbTarget > 0) {
                            if (fight.canCastSpell1(fighter, S, fight.getMapOld().getCase(cellID), i)) {
                                int dist = PathFinding.getDistanceBetween(fight.getMapOld(), fighter.getCell().getId(), i);
                                if (dist < bestFighter[0] || bestFighter[2] < nbTarget) {

                                    bestFighter[0] = dist;
                                    bestFighter[1] = i;
                                    bestFighter[2] = nbTarget;
                                    bestFighter[4] = cellID;
                                    bestSS = S;
                                }
                            }
                        }
                    } else {
                        for (Fighter T : targets) {
                            if (fight.canCastSpell1(fighter, S, T.getCell(), i)) {
                                int dist = PathFinding.getDistanceBetween(fight.getMapOld(), fighter.getCell().getId(), T.getCell().getId());
                                if (!PathFinding.isCACwithEnnemy(fighter, targets)) {
                                    if (T.isInvocation()) {
                                        if (dist < bestInvok[0]) {
                                            bestInvok[0] = dist;
                                            bestInvok[1] = i;
                                            bestInvok[2] = 1;
                                            bestInvok[3] = 1;
                                            bestInvok[4] = T.getCell().getId();
                                            bestSS = S;
                                        }

                                    } else {
                                        if (dist < bestFighter[0]) {
                                            bestFighter[0] = dist;
                                            bestFighter[1] = i;
                                            bestFighter[2] = 1;
                                            bestFighter[3] = 0;
                                            bestFighter[4] = T.getCell().getId();
                                            bestSS = S;
                                        }

                                    }
                                } else {
                                    if (dist < bestFighter[0]) {
                                        bestFighter[0] = dist;
                                        bestFighter[1] = i;
                                        bestFighter[2] = 1;
                                        bestFighter[3] = 0;
                                        bestFighter[4] = T.getCell().getId();
                                        bestSS = S;
                                    }
                                }
                            }
                            //}
                        }
                    }
                }
            }
            if (bestFighter[1] != 0) {
                CellDest = bestFighter[1];
                targetCell = bestFighter[4];
            } else if (bestInvok[1] != 0) {
                CellDest = bestInvok[1];
                targetCell = bestInvok[4];
            } else
                return "";
            if (CellDest == 0)
                return "";
            if (CellDest == fighter.getCell().getId())
                return targetCell + ";" + bestSS.getSpellID();

            ArrayList<GameCase> path = new AstarPathfinding(fight.getMapOld(), fight, fighter.getCell().getId(), CellDest).getShortestPath(-1);

            if (path == null)
                return "";
            String pathstr = "";
            try {
                int curCaseID = fighter.getCell().getId();
                int curDir = 0;
                path.add(fight.getMapOld().getCase(CellDest));
                for (GameCase c : path) {
                    if (curCaseID == c.getId())
                        continue; // Empêche le d == 0
                    char d = PathFinding.getDirBetweenTwoCase(curCaseID, c.getId(), m, true);
                    if (d == 0)
                        return "";//Ne devrait pas arriver :O
                    if (curDir != d) {
                        if (path.indexOf(c) != 0)
                            pathstr += World.world.getCryptManager().cellID_To_Code(curCaseID);
                        pathstr += d;
                    }
                    curCaseID = c.getId();
                }
                if (curCaseID != fighter.getCell().getId())
                    pathstr += World.world.getCryptManager().cellID_To_Code(curCaseID);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //Création d'une GameAction
            GameAction GA = new GameAction(0, 1, "");
            GA.args = pathstr;
            fight.onFighterDeplace(fighter, GA);

            return targetCell + ";" + bestSS.getSpellID();
        }catch (Exception ex)
        {
            Logging.getInstance().write("Error", ex.getMessage() + "\n" + Arrays.toString(ex.getStackTrace()));
            return "";
        }
    }

    public ArrayList<SortStats> getLaunchableSort(Fighter fighter, Fight fight, int distMin)
    {
      if(fight==null||fighter==null)
        return null;
      ArrayList<SortStats> sorts=new ArrayList<SortStats>();
      if(fighter.getMob()==null)
        return null;
      for(Map.Entry<Integer, SortStats> S : fighter.getMob().getSpells().entrySet())
      {
        if(S.getValue().getSpellID()==479)
          continue;
        if(S.getValue().getPACost()>fighter.getCurPa(fight))//si PA insuffisant
          continue;
        //if(S.getValue().getMaxPO() + fighter.getCurPM(fight) < distMin && S.getValue().getMaxPO() != 0)// si po max trop petite
        //continue;
        if(!LaunchedSpell.cooldownGood(fighter,S.getValue().getSpellID()))// si cooldown ok
          continue;
        if(S.getValue().getMaxLaunchbyTurn()-LaunchedSpell.getNbLaunch(fighter,S.getValue().getSpellID())<=0&&S.getValue().getMaxLaunchbyTurn()>0)// si nb tours ok
          continue;
        if(S.getValue().getSpell().getType()!=0)// si sort pas d'attaque
          continue;
        sorts.add(S.getValue());
      }
      ArrayList<SortStats> finalS=TriInfluenceSorts(fighter,sorts,fight);

      return finalS;
    }

    public ArrayList<SortStats> TriInfluenceSorts(Fighter fighter, ArrayList<SortStats> sorts, Fight fight)
    {
      if(fight==null||fighter==null)
        return null;
      if(sorts==null)
        return null;

      ArrayList<SortStats> finalSorts=new ArrayList<SortStats>();
      Map<Integer, SortStats> copie=new HashMap<Integer, SortStats>();
      for(SortStats S : sorts)
      {
        copie.put(S.getSpellID(),S);
      }

      int curInfl=0;
      int curID=0;

      while(copie.size()>0)
      {
        curInfl=-1;
        curID=0;
        for(Map.Entry<Integer, SortStats> S : copie.entrySet())
        {
          int infl=getInfl(fight,S.getValue());
          if(infl>curInfl)
          {
            curID=S.getValue().getSpellID();
            curInfl=infl;
          }
        }
        finalSorts.add(copie.get(curID));
        copie.remove(curID);
      }

      return finalSorts;
    }

    public ArrayList<Fighter> getPotentialTarget(Fight fight, Fighter fighter, ArrayList<SortStats> sorts)
    {
      if(fight==null||fighter==null)
        return null;
      ArrayList<Fighter> targets=new ArrayList<Fighter>();
      int distMax=0;
      for(SortStats S : sorts)
      {
        if(S.getMaxPO()>distMax)
          distMax=S.getMaxPO();
      }
      distMax+=fighter.getCurPm(fight)+3;
      Map<Integer, Fighter> potentialsT=getLowHpEnnemyList(fight,fighter);
      for(Map.Entry<Integer, Fighter> T : potentialsT.entrySet())
      {
        int dist=PathFinding.getDistanceBetween(fight.getMap(),fighter.getCell().getId(),T.getValue().getCell().getId());
        if(dist<distMax)
        {

          targets.add(T.getValue());
        }
      }
      return targets;
    }


    public int getInfl(Fight fight, SortStats SS)
    {
      if(fight==null)
        return 0;
      int inf=0;
      for(SpellEffect SE : SS.getEffects())
      {
        switch(SE.getEffectID())
        {
          case 96:
          case 97:
          case 98:
          case 99:
            inf+=500*Formulas.getMiddleJet(SE.getJet());
            break;
          case 131:
            inf+=300;
            break;

          default:
            inf+=Formulas.getMiddleJet(SE.getJet());
            break;
        }
      }
      return inf;
    }

    public SortStats getBestSpellForTarget(Fight fight, Fighter F, Fighter T, int launch)
    {
      if(fight==null||F==null||T==null)
        return null;
      int inflMax=0;
      SortStats ss=null;
      if(F.isCollector())
      {
        for(Map.Entry<Integer, SortStats> SS : World.world.getGuild(F.getCollector().getGuildId()).getSpells().entrySet())
        {
          if(SS.getValue()==null)
            continue;
          int curInfl=0,Infl1=0,Infl2=0;
          int PA=6;
          int usedPA[]= { 0, 0 };
          if(!fight.canCastSpell1(F,SS.getValue(),F.getCell(),T.getCell().getId()))
            continue;
          curInfl=calculInfluence(SS.getValue(),F,T);
          if(curInfl==0)
            continue;
          if(curInfl>inflMax)
          {
            ss=SS.getValue();
            usedPA[0]=ss.getPACost();
            Infl1=curInfl;
            inflMax=Infl1;
          }

          for(Map.Entry<Integer, SortStats> SS2 : World.world.getGuild(F.getCollector().getGuildId()).getSpells().entrySet())
          {
            if(SS2.getValue()==null)
              continue;
            if((PA-usedPA[0])<SS2.getValue().getPACost())
              continue;
            if(!fight.canCastSpell1(F,SS2.getValue(),F.getCell(),T.getCell().getId()))
              continue;
            curInfl=calculInfluence(SS2.getValue(),F,T);
            if(curInfl==0)
              continue;
            if((Infl1+curInfl)>inflMax)
            {
              ss=SS.getValue();
              usedPA[1]=SS2.getValue().getPACost();
              Infl2=curInfl;
              inflMax=Infl1+Infl2;
            }
            for(Map.Entry<Integer, SortStats> SS3 : World.world.getGuild(F.getCollector().getGuildId()).getSpells().entrySet())
            {
              if(SS3.getValue()==null)
                continue;
              if((PA-usedPA[0]-usedPA[1])<SS3.getValue().getPACost())
                continue;
              if(!fight.canCastSpell1(F,SS3.getValue(),F.getCell(),T.getCell().getId()))
                continue;
              curInfl=calculInfluence(SS3.getValue(),F,T);
              if(curInfl==0)
                continue;
              if((curInfl+Infl1+Infl2)>inflMax)
              {
                ss=SS.getValue();
                inflMax=curInfl+Infl1+Infl2;
              }
            }
          }
        }
      }
      else
      {
        for(Map.Entry<Integer, SortStats> SS : F.getMob().getSpells().entrySet())
        {
          if(SS==null)
            continue;
          if(SS.getValue().getSpell().getType()!=0)
            continue;
          int curInfl=0,Infl1=0,Infl2=0;
          int PA=F.getMob().getPa();
          int usedPA[]= { 0, 0 };
          if(!fight.canCastSpell1(F,SS.getValue(),T.getCell(),launch))
            continue;
          curInfl=getInfl(fight,SS.getValue());
          //if(curInfl == 0)continue;
          if(curInfl>inflMax)
          {
            ss=SS.getValue();
            usedPA[0]=ss.getPACost();
            Infl1=curInfl;
            inflMax=Infl1;
          }

          for(Map.Entry<Integer, SortStats> SS2 : F.getMob().getSpells().entrySet())
          {
            if(SS2.getValue().getSpell().getType()!=0)
              continue;
            if((PA-usedPA[0])<SS2.getValue().getPACost())
              continue;
            if(!fight.canCastSpell1(F,SS2.getValue(),T.getCell(),launch))
              continue;
            curInfl=getInfl(fight,SS2.getValue());
            //if(curInfl == 0)continue;
            if((Infl1+curInfl)>inflMax)
            {
              ss=SS.getValue();
              usedPA[1]=SS2.getValue().getPACost();
              Infl2=curInfl;
              inflMax=Infl1+Infl2;
            }
            for(Map.Entry<Integer, SortStats> SS3 : F.getMob().getSpells().entrySet())
            {
              if(SS3.getValue().getSpell().getType()!=0)
                continue;
              if((PA-usedPA[0]-usedPA[1])<SS3.getValue().getPACost())
                continue;
              if(!fight.canCastSpell1(F,SS3.getValue(),T.getCell(),launch))
                continue;

              curInfl=getInfl(fight,SS3.getValue());
              //if(curInfl == 0)continue;
              if((curInfl+Infl1+Infl2)>inflMax)
              {
                ss=SS.getValue();
                inflMax=curInfl+Infl1+Infl2;
              }
            }
          }
        }
      }
      return ss;
    }

    public SortStats getBestSpellForTargetDopeul(Fight fight, Fighter F, Fighter T, int launch, List<SortStats> listspell)
    {
      if(fight==null||F==null)
        return null;
      int inflMax=0;
      SortStats ss=null;

      for(SortStats SS : listspell)
      {
        if(SS.getSpell().getType()!=0)
          continue;
        int curInfl=0,Infl1=0,Infl2=0;
        int PA=F.getMob().getPa();
        int usedPA[]= { 0, 0 };
        if(!fight.canCastSpell1(F,SS,T.getCell(),launch))
          continue;
        curInfl=getInfl(fight,SS);
        if(curInfl==0)
          continue;
        if(curInfl>inflMax)
        {
          ss=SS;
          usedPA[0]=ss.getPACost();
          Infl1=curInfl;
          inflMax=Infl1;
        }

        for(SortStats SS2 : listspell)
        {
          if(SS2.getSpell().getType()!=0)
            continue;
          if((PA-usedPA[0])<SS2.getPACost())
            continue;
          if(!fight.canCastSpell1(F,SS2,T.getCell(),launch))
            continue;
          curInfl=getInfl(fight,SS2);
          if(curInfl==0)
            continue;
          if((Infl1+curInfl)>inflMax)
          {
            ss=SS;
            usedPA[1]=SS2.getPACost();
            Infl2=curInfl;
            inflMax=Infl1+Infl2;
          }
          for(SortStats SS3 : listspell)
          {
            if(SS3.getSpell().getType()!=0)
              continue;
            if((PA-usedPA[0]-usedPA[1])<SS3.getPACost())
              continue;
            if(!fight.canCastSpell1(F,SS3,T.getCell(),launch))
              continue;

            curInfl=getInfl(fight,SS3);
            if(curInfl==0)
              continue;
            if((curInfl+Infl1+Infl2)>inflMax)
            {
              ss=SS;
              inflMax=curInfl+Infl1+Infl2;
            }
          }
        }
      }
      return ss;
    }

    public SortStats getBestSpellForTargetDopeulglyph(Fight fight, Fighter F, Fighter T, int launch, Map<Integer, SortStats> listspell)
    {
      if(fight==null||F==null)
        return null;
      int inflMax=0;
      SortStats ss=null;
      launch=PathFinding.getRandomcelllignepomax(fight.getMap(),F.getCell().getId(),T.getCell().getId(),null,5);

      for(Map.Entry<Integer, SortStats> SS : listspell.entrySet())
      {
        if(SS.getValue().getSpell().getType()!=0)
          continue;
        int curInfl=0,Infl1=0,Infl2=0;
        int PA=F.getMob().getPa();
        int usedPA[]= { 0, 0 };
        if(!fight.canCastSpell1(F,SS.getValue(),T.getCell(),launch))
          continue;
        curInfl=getInfl(fight,SS.getValue());
        if(curInfl==0)
          continue;
        if(curInfl>inflMax)
        {
          ss=SS.getValue();
          usedPA[0]=ss.getPACost();
          Infl1=curInfl;
          inflMax=Infl1;
        }

        for(Map.Entry<Integer, SortStats> SS2 : listspell.entrySet())
        {
          if(SS2.getValue().getSpell().getType()!=0)
            continue;
          if((PA-usedPA[0])<SS2.getValue().getPACost())
            continue;
          if(!fight.canCastSpell1(F,SS2.getValue(),T.getCell(),launch))
            continue;
          curInfl=getInfl(fight,SS2.getValue());
          if(curInfl==0)
            continue;
          if((Infl1+curInfl)>inflMax)
          {
            ss=SS.getValue();
            usedPA[1]=SS2.getValue().getPACost();
            Infl2=curInfl;
            inflMax=Infl1+Infl2;
          }
          for(Map.Entry<Integer, SortStats> SS3 : listspell.entrySet())
          {
            if(SS3.getValue().getSpell().getType()!=0)
              continue;
            if((PA-usedPA[0]-usedPA[1])<SS3.getValue().getPACost())
              continue;
            if(!fight.canCastSpell1(F,SS3.getValue(),T.getCell(),launch))
              continue;

            curInfl=getInfl(fight,SS3.getValue());
            if(curInfl==0)
              continue;
            if((curInfl+Infl1+Infl2)>inflMax)
            {
              ss=SS.getValue();
              inflMax=curInfl+Infl1+Infl2;
            }
          }
        }
      }
      return ss;
    }

    public int getBestTargetZone(Fight fight, Fighter fighter, SortStats spell, int launchCell, boolean line)
    {
      if(fight==null||fighter==null)
        return 0;
      if(spell.getPorteeType().isEmpty()||(spell.getPorteeType().charAt(0)=='P'&&spell.getPorteeType().charAt(1)=='a')||spell.isLineLaunch()&&line==false)
      {
        return 0;
      }
      ArrayList<GameCase> possibleLaunch=new ArrayList<GameCase>();
      int CellF=-1;
      if(spell.getMaxPO()!=0)
      {
        char arg1='C';
        char[] table= { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v' };
        char arg2='a';
        if(spell.getMaxPO()>20)
        {
          arg2='u';
        }
        else
        {
          arg2=table[spell.getMaxPO()];
        }
        String args=Character.toString(arg1)+Character.toString(arg2);
        possibleLaunch=PathFinding.getCellListFromAreaString(fight.getMap(),launchCell,launchCell,args,0,false);
      }
      else
      {
        possibleLaunch.add(fight.getMap().getCase(launchCell));
      }

      if(possibleLaunch==null)
      {
        return -1;
      }
      int nbTarget=0;
      for(GameCase cell : possibleLaunch)
      {
        try
        {
          if(!fight.canCastSpell1(fighter,spell,cell,launchCell))
            continue;
          int curTarget=0;
          ArrayList<GameCase> cells=PathFinding.getCellListFromAreaString(fight.getMap(),cell.getId(),launchCell,spell.getPorteeType(),0,false);
          for(GameCase c : cells)
          {
            if(c==null)
              continue;
            if(c.getFirstFighter()==null)
              continue;
            if(c.getFirstFighter().getTeam2()!=fighter.getTeam2())
              curTarget++;
          }
          if(curTarget>nbTarget)
          {
            nbTarget=curTarget;
            CellF=cell.getId();
          }
        }
        catch(Exception e)
        {
          e.printStackTrace();
        }
      }
      if(nbTarget>0&&CellF!=-1)
        return CellF+nbTarget*1000;
      else
        return 0;
    }

    public int calculInfluenceHeal(SortStats ss)
    {
      int inf=0;
      for(SpellEffect SE : ss.getEffects())
      {
        if(SE.getEffectID()!=108)
          return 0;
        inf+=100*Formulas.getMiddleJet(SE.getJet());
      }

      return inf;
    }

    public int calculInfluence(SortStats ss, Fighter C, Fighter T)
    {
      int infTot=0;
      for(SpellEffect SE : ss.getEffects())
      {
        int inf=0;
        switch(SE.getEffectID())
        {
          case 5:
            inf=500*Formulas.getMiddleJet(SE.getJet());
            break;
          case 89:
            inf=200*Formulas.getMiddleJet(SE.getJet());
            break;
          case 91:
            inf=150*Formulas.getMiddleJet(SE.getJet());
            break;
          case 92:
            inf=150*Formulas.getMiddleJet(SE.getJet());
            break;
          case 93:
            inf=150*Formulas.getMiddleJet(SE.getJet());
            break;
          case 94:
            inf=150*Formulas.getMiddleJet(SE.getJet());
            break;
          case 95:
            inf=150*Formulas.getMiddleJet(SE.getJet());
            break;
          case 96:
            inf=100*Formulas.getMiddleJet(SE.getJet());
            break;
          case 97:
            inf=100*Formulas.getMiddleJet(SE.getJet());
            break;
          case 98:
            inf=100*Formulas.getMiddleJet(SE.getJet());
            break;
          case 99:
            inf=100*Formulas.getMiddleJet(SE.getJet());
            break;
          case 100:
            inf=100*Formulas.getMiddleJet(SE.getJet());
            break;
          case 101:
            inf=1000*Formulas.getMiddleJet(SE.getJet());
            break;
          case 127:
            inf=1000*Formulas.getMiddleJet(SE.getJet());
            break;
          case 84:
            inf=1500*Formulas.getMiddleJet(SE.getJet());
            break;
          case 77:
            inf=1500*Formulas.getMiddleJet(SE.getJet());
            break;
          case 111:
            inf=-1000*Formulas.getMiddleJet(SE.getJet());
            break;
          case 128:
            inf=-1000*Formulas.getMiddleJet(SE.getJet());
            break;
          case 121:
            inf=-100*Formulas.getMiddleJet(SE.getJet());
            break;
          case 131:
            inf=300;
            break;
          case 132:
            inf=2000;
            break;
          case 138:
            inf=-50*Formulas.getMiddleJet(SE.getJet());
            break;
          case 150:
            inf=-2000;
            break;
          case 168:
            inf=1000*Formulas.getMiddleJet(SE.getJet());
            break;
          case 169:
            inf=1000*Formulas.getMiddleJet(SE.getJet());
            break;
          case 210:
            inf=-300*Formulas.getMiddleJet(SE.getJet());
            break;
          case 211:
            inf=-300*Formulas.getMiddleJet(SE.getJet());
            break;
          case 212:
            inf=-300*Formulas.getMiddleJet(SE.getJet());
            break;
          case 213:
            inf=-300*Formulas.getMiddleJet(SE.getJet());
            break;
          case 214:
            inf=-300*Formulas.getMiddleJet(SE.getJet());
            break;
          case 215:
            inf=300*Formulas.getMiddleJet(SE.getJet());
            break;
          case 216:
            inf=300*Formulas.getMiddleJet(SE.getJet());
            break;
          case 217:
            inf=300*Formulas.getMiddleJet(SE.getJet());
            break;
          case 218:
            inf=300*Formulas.getMiddleJet(SE.getJet());
            break;
          case 219:
            inf=300*Formulas.getMiddleJet(SE.getJet());
            break;
          case 265:
            inf=-250*Formulas.getMiddleJet(SE.getJet());
          case 765://Sacrifice
            inf=-1000;
            break;
          case 786://Arbre de vie
            inf=-1000;
            break;
          case 106: // Renvoie de sort
            inf=-900;
            break;
        }

        if(C.getTeam()==T.getTeam())
          infTot-=inf;
        else
          infTot+=inf;
      }
      return infTot;
    }
    
    
    public int calculInfluence(SpellEffect SE, Fighter C, Fighter T)
    {
      int inf=0;
      switch(SE.getEffectID())
      {
        case 9:
          inf=-50*SE.getValue();
          break;
        case 77:
          inf=180*SE.getValue();
          break;
        case 78:
          inf=-90*SE.getValue();
          break;
        case 79:
          inf=30*SE.getValue();
          break;
        case 82:
          inf=3*SE.getValue();
          break;
        case 84:
          inf=200*SE.getValue();
          break;
        case 85:
          inf=40*SE.getValue();
          break;
        case 86:
          inf=40*SE.getValue();
          break;
        case 87:
          inf=40*SE.getValue();
          break;
        case 88:
          inf=40*SE.getValue();
          break;
        case 89:
          inf=40*SE.getValue();
          break;
        case 91:
          inf=20*SE.getValue();
          break;
        case 92:
          inf=20*SE.getValue();
          break;
        case 93:
          inf=20*SE.getValue();
          break;
        case 94:
          inf=20*SE.getValue();
          break;
        case 95:
          inf=20*SE.getValue();
          break;
        case 96:
          inf=10*SE.getValue();
          break;
        case 97:
          inf=10*SE.getValue();
          break;
        case 98:
          inf=810*SE.getValue();
          break;
        case 99:
          inf=10*SE.getValue();
          break;
        case 100:
          inf=10*SE.getValue();
          break;
        case 101:
          inf=100*SE.getValue();
          break;
        case 105:
          inf=-20*SE.getValue();
          break;
        case 106:
          inf=-400*SE.getValue();
          break;
        case 107:
          inf=-30*SE.getValue();
          break;
        case 108:
          inf=10*SE.getValue();
          break;
        case 109:
          inf=-10*SE.getValue();
          break;
        case 110:
          inf=-1*SE.getValue();
          break;
        case 111:
          inf=-100*SE.getValue();
          break;
        case 112:
          inf=-20*SE.getValue();
          break;
        case 114:
          inf=-200*SE.getValue();
          break;
        case 115:
          inf=-20*SE.getValue();
          break;
        case 116:
          inf=50*SE.getValue();
          break;
        case 117:
          inf=-50*SE.getValue();
          break;
        case 118:
          inf=-1*SE.getValue();
          break;
        case 119:
          inf=-1*SE.getValue();
          break;
        case 120:
          inf=-100*SE.getValue();
          break;
        case 121:
          inf=-20*SE.getValue();
          break;
        case 122:
          inf=-20*SE.getValue();
          break;
        case 123:
          inf=-1*SE.getValue();
          break;
        case 124:
          inf=-2*SE.getValue();
          break;
        case 125:
          inf=-1*SE.getValue();
          break;
        case 126:
          inf=-1*SE.getValue();
          break;
        case 127:
          inf=90*SE.getValue();
          break;
        case 128:
          inf=-90*SE.getValue();
          break;
        case 132:
          inf=20*SE.getValue();
          break;
        case 138:
          inf=-2*SE.getValue();
          break;
        case 140:
          inf=500*SE.getValue();
          break;
        case 141:
          inf=1000*SE.getValue();
          break;
        case 142:
          inf=-15*SE.getValue();
          break;
        case 143:
          inf=-10*SE.getValue();
          break;
        case 144:
          inf=20*SE.getValue();
          break;
        case 145:
          inf=20*SE.getValue();
          break;
        case 152:
          inf=1*SE.getValue();
          break;
        case 153:
          inf=1*SE.getValue();
          break;
        case 154:
          inf=1*SE.getValue();
          break;
        case 155:
          inf=1*SE.getValue();
          break;
        case 156:
          inf=2*SE.getValue();
          break;
        case 157:
          inf=1*SE.getValue();
          break;
        case 160:
          inf=-5*SE.getValue();
          break;
        case 161:
          inf=-5*SE.getValue();
          break;
        case 162:
          inf=5*SE.getValue();
          break;
        case 163:
          inf=5*SE.getValue();
          break;
        case 164:
          inf=-10*SE.getValue();
          break;
        case 165:
          inf=-2*SE.getValue();
          break;
        case 168:
          inf=150*SE.getValue();
          break;
        case 169:
          inf=135*SE.getValue();
          break;
        case 171:
          inf=20*SE.getValue();
          break;
        case 178:
          inf=-3*SE.getValue();
          break;
        case 179:
          inf=3*SE.getValue();
          break;
        case 182:
          inf=-20*SE.getValue();
          break;
        case 183:
          inf=-5*SE.getValue();
          break;
        case 184:
          inf=-5*SE.getValue();
          break;
        case 186:
          inf=-10*SE.getValue();
          break;
        case 210:
          inf=-8*SE.getValue();
          break;
        case 211:
          inf=-8*SE.getValue();
          break;
        case 212:
          inf=-8*SE.getValue();
          break;
        case 213:
          inf=-8*SE.getValue();
          break;
        case 214:
          inf=-8*SE.getValue();
          break;
        case 215:
          inf=8*SE.getValue();
          break;
        case 216:
          inf=8*SE.getValue();
          break;
        case 217:
          inf=8*SE.getValue();
          break;
        case 218:
          inf=8*SE.getValue();
          break;
        case 219:
          inf=8*SE.getValue();
          break;
        case 220:
          inf=-16*SE.getValue();
          break;
        case 240:
          inf=-6*SE.getValue();
          break;
        case 241:
          inf=-6*SE.getValue();
          break;
        case 242:
          inf=-6*SE.getValue();
          break;
        case 243:
          inf=-6*SE.getValue();
          break;
        case 244:
          inf=-6*SE.getValue();
          break;
        case 265:
          inf=-13*SE.getValue();
          break;
        case 293:
          inf=-8*SE.getValue();
          break;
        case 765:
          inf=100*SE.getValue();
          break;
        case 781:
          inf=-80*SE.getValue();
          break;
        case 782:
          inf=80*SE.getValue();
          break;
        case 1008:
          inf=-5*SE.getValue();
          break;
        case 1018:
          inf=20*SE.getValue();
          break;
        case 1019:
          inf=20*SE.getValue();
          break;
        case 1020:
          inf=20*SE.getValue();
          break;
        case 1021:
          inf=20*SE.getValue();
          break;
        case 1022:
          inf=4*SE.getValue();
          break;
        case 1023:
          inf=4*SE.getValue();
          break;
        case 1024:
          inf=4*SE.getValue();
          break;
        case 1025:
          inf=4*SE.getValue();
          break;
        case 1026:
          inf=20*SE.getValue();
          break;
        case 1028:
          inf=300*SE.getValue();
          break;
        case 1029:
          inf=20*SE.getValue();
          break;
      }
      if(C.getTeam()==T.getTeam())
        inf=-inf;
      return inf;
    }
    public boolean ifCanAttack(Fight fight, Fighter fighter)
    {
        int minPaForSpell = 0;

        for(SortStats spell : fighter.getMob().getSpells().values()){
            if(minPaForSpell > spell.getPACost())
                minPaForSpell = spell.getPACost();
        }

        return fighter.getCurPa(fight) >= minPaForSpell;
    }

    public boolean ifCanAttackWithSpell(Fight fight, Fighter fighter, Fighter target, SortStats spell)
    {
        try {
            int distance = PathFinding.getDistanceBetween(fight.getMap(), fighter.getCell().getId(), target.getCell().getId());
            boolean resultPM = false, resultPA = fighter.getCurPa(fight) >= spell.getPACost(), finalResult = false;

            if (distance <= 1 && spell.getMaxPO() >= distance) {
                resultPM = true;
            }
            else {
                int range = spell.getMaxPO() + fighter.getCurPm(fight);
                if(spell.getMaxPO() == 0){
                    range++;
                }
                if (range >= distance) {
                    resultPM = true;
                }
            }

            if (resultPM && resultPA)
                finalResult = true;

            return finalResult;
        }catch (Exception ex)
        {
            Logging.getInstance().write("Error", ex.getMessage() + "\n" + Arrays.toString(ex.getStackTrace()));
            return false;
        }
    }

    public boolean ifCanMove(Fight fight, Fighter fighter)
    {
        return fighter.getCurPm(fight) > 0;
    }
}
