package org.starloco.locos.fight;

import org.starloco.locos.client.Player;
import org.starloco.locos.common.Formulas;
import org.starloco.locos.common.PathFinding;
import org.starloco.locos.common.SocketManager;
import org.starloco.locos.fight.spells.Spell;
import org.starloco.locos.fight.spells.SpellEffect;
import org.starloco.locos.game.GameClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Challenge {

    private int Type, xpWin, dropWin, Arg = 0;
    private boolean challengeAlive = false, challengeWin = false;
    private String looseBy = "", Args = "", lastActions = "";
    private Fight fight;
    private Fighter _cible;
    private List<Fighter> _ordreJeu = new ArrayList<>();

    public Challenge(Fight fight, int Type, int xp, int drop) {
        this.challengeAlive = true;
        this.fight = fight;
        this.Type = Type;
        this.xpWin = xp;
        this.dropWin = drop;
        this._ordreJeu.clear();
        this._ordreJeu.addAll(fight.getOrderPlaying());
    }

    public int getType() {
        return this.Type;
    }

    public boolean getAlive() {
        return challengeAlive;
    }

    public int getXp() {
        return xpWin;
    }

    public int getDrop() {
        return dropWin;
    }

    public boolean getWin() {
        return challengeWin;
    }

    public boolean loose() {
        return looseBy.isEmpty();
    }

    public String getPacketEndFight() {
        return (this.challengeWin ? "OK" + Type : "KO" + Type);
    }

    private void challengeWin() {
        challengeWin = true;
        challengeAlive = false;
        SocketManager.GAME_SEND_CHALLENGE_FIGHT(fight, 1, "OK" + Type);
    }

    public void challengeLoose(Fighter fighter) {
        String name = "";
        if (fighter != null && fighter.getPersonnage() != null)
            name = fighter.getPersonnage().getName();
        looseBy = name;
        challengeWin = false;
        challengeAlive = false;
        SocketManager.GAME_SEND_CHALLENGE_FIGHT(fight, 7, "KO" + Type);
        SocketManager.GAME_SEND_Im_PACKET_TO_CHALLENGE(fight, 1, "0188;" + name);
    }

    public void challengeSpecLoose(Player player) {
        SocketManager.GAME_SEND_CHALLENGE_PERSO(player, "KO" + Type);
        SocketManager.GAME_SEND_Im_PACKET_TO_CHALLENGE_PERSO(player, "0188;"
                + looseBy);
    }

    public String parseToPacket() {
        StringBuilder packet = new StringBuilder();
        packet.append(Type).append(";").append(_cible != null ? "1" : "0").append(";").append(_cible != null ? (Integer.valueOf(_cible.getId())) : "").append(";").append(xpWin).append(";0;").append(dropWin).append(";0;");
        if (!challengeAlive) {
            if (challengeWin)
                packet.append("").append(Type);
            else
                packet.append("").append(Type);
        }
        return packet.toString();
    }

    public void showCibleToPerso(Player p) {
        if (!challengeAlive || _cible == null || _cible.getCell() == null
                || p == null)
            return;
        ArrayList<GameClient> Pws = new ArrayList<>();
        Pws.add(p.getGameClient());
        SocketManager.GAME_SEND_FIGHT_SHOW_CASE(Pws, _cible.getId(), _cible.getCell().getId());
    }

    public void showCibleToFight() {
        if (!challengeAlive || _cible == null || _cible.getCell() == null)
            return;
        ArrayList<GameClient> Pws = new ArrayList<>();
        for (Fighter fighter : fight.getFighters(1)) {
            if (fighter.hasLeft())
                continue;
            if (fighter.getPersonnage() == null
                    || !fighter.getPersonnage().isOnline())
                continue;
            Pws.add(fighter.getPersonnage().getGameClient());
        }
        SocketManager.GAME_SEND_FIGHT_SHOW_CASE(Pws, _cible.getId(), _cible.getCell().getId());
    }

    public void fightStart() {//Définit les cibles au début du combat
        if (!challengeAlive)
            return;
        switch (Type) {
            case 3://Désigné Volontaire
            case 4://Sursis
            case 32://Elitiste
            case 35://Tueur à gages
                if (_cible == null && _ordreJeu.size() > 0)//Si aucun cible n'est choise on en choisie une
                {
                    List<Fighter> Choix = new ArrayList<Fighter>();
                    Choix.addAll(_ordreJeu);
                    Collections.shuffle(Choix);//Mélange l'ArrayList
                    for (Fighter f : Choix) {
                        if (f.getPersonnage() != null)
                            continue;
                        if (f.getMob() != null && f.getTeam2() == 2
                                && !f.isDead() && !f.isInvocation())
                            _cible = f;
                    }
                }
                showCibleToFight();//On le montre a tous les joueurs
                break;
            case 10://Cruel
                int levelMin = 2000;
                for (Fighter fighter : fight.getFighters(2))//La cible sera le niveau le plus faible
                {
                    if (fighter.isInvocation())
                        continue;
                    if (fighter.getPersonnage() == null
                            && fighter.getMob() != null
                            && fighter.getLvl() < levelMin
                            && fighter.getInvocator() == null) {
                        levelMin = fighter.getLvl();
                        _cible = fighter;
                    }
                }
                if (_cible != null)
                    showCibleToFight();
                break;
            case 25://Ordonné
                int levelMax = 0;
                for (Fighter fighter : fight.getFighters(2))//la cible sera le niveau le plus élevé
                {
                    if (fighter.isInvocation() || fighter.isDouble())
                        continue;
                    if (fighter.getPersonnage() == null
                            && fighter.getMob() != null
                            && fighter.getInvocator() == null
                            && fighter.getLvl() > levelMax) {
                        levelMax = fighter.getLvl();
                        this._cible = fighter;
                    }
                }
                if (_cible != null)
                    showCibleToFight();
                break;
        }
    }

    public void fightEnd() {//Vérifie la validité des challenges en fin de combat (si nécessaire)
        if (!challengeAlive)
            return;
        switch (Type) {
            case 44://Partage
            case 46://Chacun son monstre
                for (Fighter fighter : fight.getFighters(1)) {
                    if (!Args.contains(String.valueOf(fighter.getId()))) {
                        challengeLoose(fighter);
                        return;
                    }
                }
                break;
        }
		
        if (!fight.verifIfTeamIsDead()) { // Check if all the monsters (mobs) are already dead, if not then send challenge lose packet to the players
            fight.getFighters(1).forEach(this::challengeLoose);
        } else {
            challengeWin();
        }
    }

    public void onFighterDie(Fighter fighter) {
        if (!challengeAlive)
            return;
        switch (Type) {
            case 33: // survivant
            case 49: // Protégez vos mules
                if (fighter.getPersonnage() != null)
                    challengeLoose(fight.getFighterByOrdreJeu());
                break;
            case 44://Partage
                if (fighter.getPersonnage() != null)
                    if (!Args.contains(String.valueOf(fighter.getId())))
                        challengeLoose(fighter);
                break;
        }
    }

    public void onFighterAttacked(Fighter caster, Fighter target) {
        if (!challengeAlive)
            return;
        switch (Type) {
            case 17:// Intouchable
                if (target.getTeam() == 0 && !target.isInvocation()) {
                    if (target.getBuff(9) == null) // Si dérobade
                        challengeLoose(target);
                    break;
                }
                break;
            case 31: // Focus
                if (caster.getTeam() == 0 && target.getTeam() == 1) {
                    if (Args.isEmpty())
                        Args += "" + target.getId();
                    else if (!Args.contains("" + target.getId()))
                        challengeLoose(caster);
                }
                break;
        }
    }

    public void onFightersAttacked(ArrayList<Fighter> targets, Fighter caster,
                                   SpellEffect SE, int spell, boolean isTrap) {
        int effectID = SE.getEffectID();
        if (!challengeAlive)
            return;
        String DamagingEffects = "|82|85|86|87|88|89|91|92|93|94|95|96|97|98|99|100|141|";
        String HealingEffects = "|108|";
        String MPEffects = "|77|127|169|";
        String APEffects = "|84|101|";
        String OPEffects = "|116|320|";
        switch (Type) {
            case 18: // Incurable
                if ((caster.getTeam() == 0) && !caster.isInvocation() && HealingEffects.contains("|" + effectID + "|"))
                    targets.stream().filter(fighter -> fighter.getTeam() == 0).forEach(fighter -> challengeLoose(caster));
                break;
            case 19: // Mains propres
                if(HealingEffects.contains("|" + effectID + "|"))
                    return;
                switch(spell) {
                    case 79:
                    case 77:
                    case 73:
                    case 71:
                    case 66:
                    case 65:
                    case 80:
                        return;
                }
                if (caster.getTeam() != 0)
                    return;
                if (caster.isInvocation())
                    return;
                if (SE.getTurn() > 0)
                    return;
                if (isTrap)
                    return;

                for (Fighter target : targets) {
                    if (target.getTeam() == 1 && !target.isInvocation()) {
                        challengeLoose(caster);
                        break;
                    }
                }

                break;
            case 20: // Elémentaire
                if ((caster.getTeam() == 0)
                        && DamagingEffects.contains("|" + effectID + "|")
                        && effectID != 141) {
                    switch (spell) {
                        case 126://Mot stimulant
                        case 149://Mutilation
                        case 106://Roue de la fortune
                        case 111://Contrecoup
                        case 108://Esprit félin
                        case 435://Transfert de vie
                        case 135://Mot de sacrifice
                        case 123://Mot drainant
                            return;
                    }
                    if (Arg == 0) {
                        Arg = effectID;
                        break;
                    }
                    if (Arg != effectID) {
                        String eau = "85 91 96", terre = "86 92 97", air = "87 93 98", feu = "88 94 99", neutre = "89 95 100";
                        if (eau.contains(String.valueOf(Arg))
                                && eau.contains(String.valueOf(effectID))) {
                            break;
                        } else if (terre.contains(String.valueOf(Arg))
                                && terre.contains(String.valueOf(effectID))) {
                            break;
                        } else if (air.contains(String.valueOf(Arg))
                                && air.contains(String.valueOf(effectID))) {
                            break;
                        } else if (feu.contains(String.valueOf(Arg))
                                && feu.contains(String.valueOf(effectID))) {
                            break;
                        } else if (neutre.contains(String.valueOf(Arg))
                                && neutre.contains(String.valueOf(effectID))) {
                            break;
                        }
                        challengeLoose(caster);
                        break;
                    }
                }
                break;
            case 21: // Circulez !
                if ((caster.getTeam() == 0) && MPEffects.contains("|" + effectID + "|")) {
                    for (Fighter target : targets) {
                        if (target.getTeam() == 1) {
                            challengeLoose(caster);
                            break;
                        }
                    }
                }
                break;
            case 22: // Le temps qui court !
                if ((caster.getTeam() == 0)
                        && APEffects.contains("|" + effectID + "|")) {
                    for (Fighter target : targets) {
                        if (target.getTeam() == 1) {
                            challengeLoose(caster);
                            break;
                        }
                    }
                }
                break;
            case 23: // Perdu de vue !
                if ((caster.getTeam() == 0)
                        && OPEffects.contains("|" + effectID + "|")) {
                    for (Fighter target : targets) {
                        if (target.getTeam() == 1) {
                            challengeLoose(caster);
                            break;
                        }
                    }
                }
                break;
            case 32: // Elitiste
            case 34: // Imprévisible
                if ((caster.getTeam() == 0)
                        && DamagingEffects.contains("|" + effectID + "|")) {
                    for (Fighter target : targets) {
                        if (target.getTeam() == 1) {
                            if (_cible == null
                                    || _cible.getId() != target.getId())
                                challengeLoose(caster);
                        }
                    }
                }
                break;
            case 38: // Blitzkrieg
                if ((caster.getTeam() == 0) && DamagingEffects.contains("|" + effectID + "|")) {
                    for (Fighter target : targets) {
                        if (target.getTeam() == 1) {
                            StringBuilder id = new StringBuilder();
                            id.append(";").append(target.getId()).append(",");
                            if (!this.Args.contains(id.toString())) {
                                id.append(caster.getId());
                                this.Args += id.toString();
                            }
                        }
                    }
                }
                break;
            case 43: // Abnégation
                if ((caster.getTeam() == 0) && HealingEffects.contains("|" + effectID + "|") && caster.getInvocator() == null)
                    for (Fighter target : targets)
                        if (target.getId() == caster.getId())
                            challengeLoose(caster);
                break;
            case 45: // Duel
            case 46: // Chacun son monstre
                if ((caster.getTeam() == 0)
                        && DamagingEffects.contains("|" + effectID + "|")) {
                    for (Fighter target : targets) {
                        if (target.getTeam() == 1) {
                            if (!Args.contains(";" + target.getId() + ","))
                                Args += ";" + target.getId() + ","
                                        + caster.getId() + ";";
                            else if (Args.contains(";" + target.getId() + ",")
                                    && !Args.contains(";" + target.getId()
                                    + "," + caster.getId() + ";"))
                                challengeLoose(target);
                        }
                    }
                }
                break;
            case 47: // Contamination
                if (DamagingEffects.contains("|" + effectID + "|"))
                    targets.stream().filter(target -> target.getTeam() == 0 && target.getPdv() != target.getPdvMax())
                            .filter(target -> !Args.contains(";" + target.getId() + ",")).forEach(target -> Args += ";" + target.getId() + "," + "3;");
                break;
        }
    }

    public void onMobDie(Fighter mob, Fighter killer) {
        if (mob.getMob() == null)
            return;
        if (mob.getPersonnage() != null)
            return;
        if (mob.getTeam() != 1)
            return;
        if (mob.isInvocation() && mob.getInvocator().getPersonnage() != null)
            return;

        boolean isKiller = (killer.getId() != mob.getId());

        if (!challengeAlive)
            return;

        switch (Type) {
            case 3: // Désigné Volontaire
                if (_cible == null)
                    return;
                if(mob.isInvocation()) return;

                if (mob.getInvocator() != null)
                    if (mob.getInvocator().getId() == _cible.getId())
                        return;

                if (_cible.getId() != mob.getId()) {
                    challengeLoose(fight.getFighterByOrdreJeu());
                } else {
                    challengeWin();
                }
                _cible = null;
                break;

            case 4: // Sursis
                if (_cible == null)
                    return;

                if (_cible.getId() == mob.getId() && !fight.verifIfTeamIsDead()) {
                    challengeLoose(fight.getFighterByOrdreJeu());
                }
                break;

            case 28: // Ni Pioutes ni Soumises
                if (isKiller && killer.getPersonnage() != null)
                    if (killer.getPersonnage().getSexe() == 0) {
                        challengeLoose(fight.getFighterByOrdreJeu());
                    }
                break;

            case 29: // Ni Pious ni Soumis
                if (isKiller && killer.getPersonnage() != null) {
                    if (killer.getPersonnage().getSexe() == 1) {
                        challengeLoose(fight.getFighterByOrdreJeu());
                    }
                }
                break;

            case 31: // Focus
                if (killer.getMob() != null || killer == mob || mob.getLevelUp())
                    break;
                if (Args.contains("" + mob.getId()))
                    Args = "";
                else
                    challengeLoose(killer);
                break;

            case 32: // Elitiste
                if (_cible.getId() == mob.getId())
                    challengeWin();
                break;

            case 34: // Imprévisible
                _cible = null;
                break;
            case 42: // Deux pour le prix d'un
                if (mob.isInvocation() || killer.isInvocation())
                    return;
                Args += (Args.isEmpty() ? killer.getId() : ";" + killer.getId());
                break;
            case 44: // Partage
            case 46: // Chacun son monstre
                if (isKiller)
                    Args += (Args.isEmpty() ? killer.getId() : ";" + killer.getId());
                break;
            case 30: // Les petits d'abord
            case 48: // Les mules d'abord
                if (mob.isInvocation() || mob.isDouble())
                    return;
                if (mob.getId() != killer.getId()) {
                    int lvlMin = 5000;
                    for (Fighter f : fight.getFighters2(1)) {
                        if (f.isInvocation())
                            continue;
                        if (f.getLvl() < lvlMin)
                            lvlMin = f.getLvl();
                    }
                    if (killer.getLvl() > lvlMin)
                        challengeLoose(fight.getFighterByOrdreJeu());
                }
                break;

            case 35: // Tueur à gages
                if (_cible == null)
                    return;
                if (_cible.getId() != mob.getId()) {
                    if (!mob.isInvocation())
                        challengeLoose(fight.getFighterByOrdreJeu());
                } else {
                    try {
                        _cible = null;
                        ArrayList<Fighter> fighters = new ArrayList<Fighter>(fight.getFighters(2));
                        for (Iterator<Fighter> it = fighters.iterator(); it.hasNext(); ) {
                            Fighter f = it.next();
                            if (f.isInvocation() || f.isDead()
                                    || f.getPersonnage() != null)
                                it.remove();
                        }
                        Collections.sort(fighters);
                        for (Fighter f : fighters) {
                            if (!f.isInvocation() && !f.isDead()
                                    && f.getPersonnage() == null) {
                                _cible = f;
                                break;
                            }
                        }
                        showCibleToFight();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;

            case 10: // Cruel
                if (_cible == null)
                    return;
                if (_cible.isInvocation() || _cible.isDouble()
                        || mob.getPersonnage() != null)
                    return;
                if (_cible.getId() != mob.getId()
                        && _cible.getLvl() != mob.getLvl()) {
                    if (mob.getLvl() > _cible.getLvl())
                        challengeLoose(fight.getFighterByOrdreJeu());
                } else {
                    try {
                        int levelMin = 2000;
                        for (Fighter fighter : fight.getFighters(2)) {
                            if (fighter.isInvocation() || fighter.isDouble()
                                    || fighter.getPersonnage() != null || fighter.isDead())
                                continue;
                            if (fighter.getPersonnage() == null
                                    && fighter.getLvl() < levelMin) {
                                levelMin = fighter.getLvl();
                                _cible = fighter;
                            }
                        }
                        if (_cible != null)
                            showCibleToFight();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;

            case 25: // Ordonné
                if (_cible == null)
                    return;
                if (killer.isMob() || mob.isInvocation() || mob.isDouble() || mob.getPersonnage() != null)
                    return;

                if (_cible.getId() != mob.getId()) {
                    if (mob.getLvl() < _cible.getLvl())
                        challengeLoose(fight.getFighterByOrdreJeu());
                } else {
                    try {
                        int levelMax = 0;
                        for (Fighter fighter : fight.getFighters(2)) {
                            if (fighter.isInvocation() || fighter.isDouble()
                                    || fighter.getPersonnage() != null || fighter.isDead())
                                continue;
                            if (fighter.getLvl() > levelMax) {
                                levelMax = fighter.getLvl();
                                _cible = fighter;
                            }
                        }
                        if (_cible != null)
                            showCibleToFight();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    public void onPlayerMove(Fighter fighter) {
        if (!challengeAlive)
            return;
        switch (Type) {
            case 1: // Zombie
                if (this.fight.getCurFighterUsedPm() > 1) // Si l'on a utilisé plus d'un PM
                    //if (fighter.getPm() - fighter.getCurPm(fight) > 1)
                    challengeLoose(fight.getFighterByOrdreJeu());
                break;
        }
    }

    public void onPlayerAction(Fighter fighter, int actionID) {
        if (!challengeAlive || fighter.getTeam() == 1)
            return;
        StringBuilder action = new StringBuilder();
        action.append(";").append(fighter.getId());
        action.append(",").append(actionID).append(";");
        switch (Type) {
            case 6: // Versatile
            case 5: // Econome
                if (lastActions.contains(action.toString()))
                    challengeLoose(fight.getFighterByOrdreJeu());
                lastActions += action.toString();
                break;

            case 24: // Borné
                if (!lastActions.contains(action.toString())
                        && lastActions.contains(";" + fighter.getId() + ","))
                    challengeLoose(fight.getFighterByOrdreJeu());
                lastActions += action.toString();
                break;
        }

    }

    public void onPlayerCac(Fighter fighter) {

        if (!challengeAlive)
            return;

        switch (Type) {
            case 11: // Mystique
                challengeLoose(fight.getFighterByOrdreJeu());
                break;
            case 6: // Versatile
            case 5: // Econome
                StringBuilder action = new StringBuilder();
                action.append(";").append(fighter.getId());
                action.append(",").append("cac").append(";");
                if (lastActions.contains(action.toString()))
                    challengeLoose(fight.getFighterByOrdreJeu());
                lastActions += action.toString();
                break;
        }
    }

    public void onPlayerSpell(Fighter fighter, Spell.SortStats spellStats) {
        if (!challengeAlive)
            return;
        if (fighter.getPersonnage() == null)
            return;
        switch (Type) {
            case 9: // Barbare
                challengeLoose(fight.getFighterByOrdreJeu());
                break;
            case 14: // Casino Royal (sort #101)
                if (fighter.getPersonnage() != null)
                    if (spellStats.getSpellID() == 101)
                        Args = "cast";
                break;
        }
    }

    public void onPlayerStartTurn(Fighter fighter) {
        if (!challengeAlive)
            return;
        switch (Type) {
            case 2: // Statue
                if (fighter.getPersonnage() == null)
                    return;
                Arg = fighter.getCell().getId();
                break;
            case 6: // Versatile
                lastActions = "";
                break;
            case 14: // Casino Royal (sort #101)
                if (fighter.getPersonnage() != null)
                    if (fighter.canLaunchSpell(101))
                        Args = "ok";
                    else Args = "cant";
                break;
            case 34: // Imprévisible
                if (fighter.getTeam() == 1)
                    return;
                try {
                    int noBoucle = 0, GUID = 0;
                    _cible = null;
                    while (_cible == null) {
                        if (_ordreJeu.size() > 0) {
                            GUID = Formulas.getRandomValue(0, _ordreJeu.size() - 1);
                            Fighter f = _ordreJeu.get(GUID);
                            if (f.getPersonnage() == null && !f.isDead())
                                _cible = f;
                            noBoucle++;
                            if (noBoucle > 150)
                                return;
                        }
                    }
                    showCibleToFight();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case 38: // Blitzkrieg
                if (fighter.getTeam() == 1 && Args.contains(";" + fighter.getId() + ",")) {
                    if (fighter.isDead()) return;

                    int id = 0;

                    for (String string : this.Args.split(";")) {
                        if (string.contains("" + fighter.getId())) {
                            for (String test : string.split(","))
                                id = Integer.parseInt(test);
                            break;
                        }
                    }

                    for (Fighter target : this.fight.getFighters(1))
                        if (target.getId() == id)
                            if(fighter.getPdv() != fighter.getPdvMax())
                                challengeLoose(target);
                }
                break;
            case 47: // Contamination
                if (fighter.getTeam() == 0) {
                    String str = ";" + fighter.getId() + ",";
                    if (Args.contains(str + "1;"))
                        challengeLoose(fighter);
                    else if (Args.contains(str + "2;"))
                        Args += str + "1;";
                    else if (Args.contains(str + "3;"))
                        Args += str + "2;";
                }
                break;
        }
    }

    public void onPlayerEndTurn(Fighter fighter) {
        if (!challengeAlive)
            return;

        boolean hasFailed = false;
        ArrayList<Fighter> fighters = PathFinding.getFightersAround(fighter.getCell().getId(), fight.getMap());

        switch (Type) {
            case 1: // Zombie
                if (this.fight.getCurFighterUsedPm() <= 0) // Si l'on a pas bougé
                    challengeLoose(fighter);
                break;

            case 2: // Statue
                if (fighter.getPersonnage() != null)
                    if (fighter.getCell().getId() != Arg)
                        challengeLoose(fighter);
                break;

            case 7: // Jardinier (sort #367)
                if (fighter.getPersonnage() != null)
                    if (fighter.canLaunchSpell(367))
                        challengeLoose(fighter);
                break;

            case 8: // Nomade
                if (this.fight.getCurFighterPm() != 0)
                    challengeLoose(fighter);
                break;

            case 12: // Fossoyeur (sort #373)
                if (fighter.getPersonnage() != null)
                    if (fighter.canLaunchSpell(373))
                        challengeLoose(fighter);
                break;

            case 14: // Casino Royal (sort #101)
                if (fighter.getPersonnage() != null)
                    if(Args.equals("ok"))
                        challengeLoose(fighter);
                break;

            case 15: // Araknophile (sort #370)
                if (fighter.getPersonnage() != null)
                    if (fighter.canLaunchSpell(370))
                        challengeLoose(fighter);
                break;

            case 36: // Hardi
                hasFailed = true;
                if (!fighters.isEmpty())
                    for (Fighter f : fighters)
                        if (f.getTeam() != fighter.getTeam())
                            hasFailed = false;
                break;

            case 37: // Collant
                hasFailed = true;
                if (!fighters.isEmpty())
                    for (Fighter f : fighters)
                        if (f.getTeam() == fighter.getTeam())
                            hasFailed = false;
                break;

            case 39: // Anachorète
                if (!fighters.isEmpty())
                    fighters.stream().filter(f -> f.getTeam() == fighter.getTeam()).forEach(f -> challengeLoose(fighter));
                break;

            case 40: // Pusillanime
                if (!fighters.isEmpty())
                    fighters.stream().filter(f -> f.getTeam() != fighter.getTeam()).forEach(f -> challengeLoose(fighter));
                break;

            case 41: // Pétulant
                if (this.fight.getCurFighterPa() != 0)
                    challengeLoose(fighter);
                break;

            case 42: // Deux pour le prix d'un
                if (!Args.isEmpty())
                    if (!(Args.split(";").length % 2 == 0))
                        hasFailed = true;
                Args = "";
                break;
        }
        if (hasFailed) challengeLoose(fighter);
    }
}
