# BUGS ACTUEL

- IA Gourlo à revoir n'invoque pas tout le temps surtout quand il est au cac d'un ennemi
- IA Minotot manque attaque Lancer de Tofu
- IA Gardienne des Egouts, n'attaque pas, aspire dans le vide, lance invisibilité à la fin de son tour et ne fuis pas
- Kapota la Fraise n'attaque pas
- Craqueleur Légendaire ne fait pas peau de silex ni pierre de topaze sur invoc
- Dragtula l'Ancien ne fait rien
- Rat blanc Racasse ne fonctionne pas comme offi
- Rat Noir Kackitu ne fonctionne pas comme offi
- Chaferfu Lancier n'attaque qu'une seule fois
- Corbac Apprivoisé ne marche pas correctement
- Ia dragoss flingué
- Bwork élémentaux ia flingué
- Corailleur Magistral en roue libre
- IA CM Bug (Attaque bien avec Complicité et Embranchement mais pas les deux autres et ne fait plus rien avec un ennemi au Cac)
- Tofu Ventripotent ne fait rien
- Pince de Corail refaire EffectTarget
- Coraillement refaire EffectTarget
- Refaire IA Kreuvet La Bwork Ingénieuse

# FIX

### **16/03/2023**
- Fix effet de sort appliqué alors que la cible était morte
- Fix EffectTarget du Sort Emmental
- Fix EffectTarget du Sort Roblochon ancestral
- Fix EffectTarget du Sort Fleur des Iles
- Fix EffectTarget du Sort Ebranlement
- Fix Animation du Sort Flèche Persécutrice
- Fix NullExpointerException in Fight.java -> onFighterDie line 2948
- Fix SetToniqueInterface si le joueur n'a pas validé son tonique pendant un reboot ou déco / reco
- Un joueur ne peut plus recevoir un tonique s'il n'est pas dans le gladiatrool
- Fix commande joueur .restat
- Fix onMovementObject réequipe correcte l'item après avoir enlevé l'ancien

### **15/03/2023**
- Fix 2 NullExpointerException in Player.java
- Fix 1 NullExpointerException in Fight.java
- Fix Shortcut Items (Add / Move / Remove)

### **14/03/2023**
- Correction changement de personnage
- Correction durée de Gain de Pa du Rasboul Majeur
- Fix Sorts DragOeuf Immature + Archi
- Fix EffectTarget du Sort Ruse Kitsoune
- Fix EffectTarget du Sort Communion Elémentale
- Fix EffectTarget du Sort Graines Magiques
- Fix EffectTarget du Sort Buvette
- Fix EffectTarget du Sort Kalik
- Fix EffectTarget du Sort Meupette Choh
- Fix EffectTarget du Sort Blop Zone

### **13/03/2023**
- Correction Sort Bitouf des Plaines

### **12/03/2023**
- Fix Bug effet de Sort +do parfois ça ajoute +dom par déco reco
- Fix Bug effet de Sort -Pa le packet est envoyé trop de fois
- Fix IA Kimbo
- Fix applySpellEffect 181 (Invocations)
- Correction level 5 Fantôme Ardent & Fantôme Brave
- Fix refreshBuff in Fight
- Fix vol de PA Infini
- Fix restauration des sorts en sortant du Gladiatrool
- Fix des Effets Item Classe à ne pas ajouter 2 fois

### **11/03/2023**
- Fix IA Lapino (Ne buffait pas tout le temps son invocateur)
- Fix IA Kaskapointe la Couverte (Ne faisait rien)
- Fix IA Minotot (IA 80, manque Attaque Sort Tofu)
- Fix EffectTarget Transpékisation
- Ajout de la Fonction ifCanMove pour savoir si un monstre peu se déplacer
- Ajout de la Fonction ifCanAttack pour savoir si un monstre peu attaquer
- Ajout de la Fonction ifCanAttackWithSpell pour savoir si un monstre peu attaquer une cible précise avec un sort précis
- Ajout de try catch dans les functions de la Classe Function pour éviter les crash d'IA