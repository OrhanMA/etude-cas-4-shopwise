# Shopwise 

Shopwise est une application de vente pour les commerces de proximité.

Pour les explications et justifications demandées, j'ai fait des fichiers markdown séparés pour que ce fichier reste digestible.

## Responsabilité de chaque module et leurs interactions

La responsabilité des modules est décrite dans `markdown-files/Responsabilité-modules.md`.

## Attributs de qualité

Justification dans `markdown-files/Attributs-qualite.md`

## Système de recommendation 

J'ai opté pour un système hybride qui va utiliser 3 axes :
- Popularité des produits
- Similitude des produits basée sur les catégories liées
- Machine Learning pour identifier les tendances d'achats

L'importance des sources de recommendations pèseront dans l'ordre suivant pour les recommendations : 
Tendances ML > Similitude catégories > Popularité.

Je vais opter pour un calcul de recommendations avec des poids pour faire ressortir les produits à recommander.

La décision du système hybride est justifié par le fait qu'une boutique avec un petit historique de ventes ou bien un produit encore peu vendu ne permettent pas d'identifier une recommandation.
Dans ces cas, les recommandations basées sur les catégories et la popularité permettront de prendre le relais.