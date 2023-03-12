# BUGS ACTUEL

- IA Gourlo à revoir n'invoque pas tout le temps surtout quand il est au cac d'un ennemi
- IA Minotot manque attaque Lancer de Tofu
- IA Gardienne des Egouts, n'attaque pas, aspire dans le vide, lance invisibilité à la fin de son tour et ne fuis pas




# FIX

### **11/03/2023**
- Fix IA Lapino (Ne buffait pas tout le temps son invocateur)
- Fix IA Kaskapointe la Couverte (Ne faisait rien)
- Fix IA Minotot (IA 80, manque Attaque Sort Tofu)
- Fix EffectTarget Transpékisation
- Ajout de la Fonction ifCanMove pour savoir si un monstre peu se déplacer
- Ajout de la Fonction ifCanAttack pour savoir si un monstre peu attaquer
- Ajout de la Fonction ifCanAttackWithSpell pour savoir si un monstre peu attaquer une cible précise avec un sort précis
- Ajout de try catch dans les functions de la Classe Function pour éviter les crash d'IA