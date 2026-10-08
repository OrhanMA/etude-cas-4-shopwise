# Shopwise 

Shopwise est une application de vente pour les commerces de proximité.

Pour les explications et justifications demandées, j'ai fait des fichiers markdown séparés pour que ce fichier reste digestible.

## Responsabilité de chaque module et leurs interactions

La responsabilité des modules est décrite dans `markdown-files/Responsabilité-modules.md`.

## Attributs de qualité

Justification dans `markdown-files/Attributs-qualité.md`

## Sécurité et normalisation des erreurs

Les justifications des choix réalisés pour les US 4 à 6 — autorisation par rôle,
authentification JWT et normalisation des réponses d’erreur — sont détaillées dans
[`markdown-files/Choix-securite-auth-et-normalisation.md`](markdown-files/Choix-securite-auth-et-normalisation.md).

## Système de recommendation 

J'ai opté pour un système hybride qui va utiliser 3 axes :
- Popularité des produits
- Similitude des produits basée sur les catégories liées
- Machine Learning pour identifier les tendances d'achats

L'importance des sources de recommendations pèseront dans l'ordre suivant pour les recommendations : 
Tendances ML > Similitude catégories > Popularité.

Le classement implémenté utilise des niveaux successifs : réseau, catégories,
puis popularité. Une pondération commune nécessiterait de calibrer les scores.

La décision du système hybride est justifié par le fait qu'une boutique avec un petit historique de ventes ou bien un produit encore peu vendu ne permettent pas d'identifier une recommandation.
Dans ces cas, les recommandations basées sur les catégories et la popularité permettront de prendre le relais.


# Références

## US8 — Évolutivité et évaluation

Le package `recommendation` isole l'API, le service hybride, les données et
l'apprentissage. `RecommendationStrategy` reçoit des paniers indexés indépendants
de JPA et produit des scores. `NeuralRecommendationStrategy` est injectée par
Spring : pour remplacer le modèle, fournir une autre implémentation et sélectionner
le bean par profil ou qualifier. `RecommendationDataProvider` permet de remplacer
la source sans changer l'API. Son adaptateur JPA charge produits/catégories et
ventes/lignes avec jointures, sans requête supplémentaire par panier.
L'interface de données transporte encore les entités métier : une source externe
doit les adapter. Un instantané immuable indépendant serait une évolution utile.

```text
API -> service hybride -> fournisseur de données -> JPA
                       -> stratégie -> réseau neuronal
                       -> catégories / popularité
```

Le nombre d'époques est configurable par `app.recommendations.epochs`, entre 1
et 1000 (150 par défaut). Aucun modèle mutable n'est partagé entre demandes.
Le test d'évolution remplace la stratégie et la source, puis ajoute une vente
pour vérifier la prise en compte des nouvelles données.

L'évaluation utilise 32 paniers synthétiques d'entraînement et quatre paniers
réservés représentant huit prédictions. Les associations des paniers réservés
existent auparavant dans l'entraînement : cette expérience démontre la reproduction
d'un motif, pas la découverte de comportements nouveaux. Elle compare le succès
dans les cinq premiers résultats avec une référence de popularité, calculée sur
les seules données d'entraînement (égalité des fréquences, départage par ID).
Résultat de ce jeu déterministe : réseau 8/8 (100 %), popularité 6/8 (75 %).
Les métriques sont affichées lors des tests. Aucun gain commercial n'est démontré.

Avec peu de données, le réseau risque de mémoriser les exemples plutôt que de
généraliser. Les pistes d'amélioration comprennent une évaluation chronologique
sur davantage de ventes, des nouveaux motifs et produits, une validation distincte
pour les hyperparamètres, l'arrêt anticipé, le versionnement du modèle,
l'entraînement hors requête et un cache invalidé après modification des données.

## US7 — Recommandations neuronales hybrides

`GET /api/recommendations?productId=1&limit=5` nécessite un JWT valide.
`productId` est facultatif : sans produit, le classement repose sur les quantités
vendues. `limit` vaut 5 par défaut, avec des bornes de 1 à 20. Les résultats
contiennent `productId`, `name`, `price`, `source` et `score`. Le produit source
est exclu ; les produits sont uniques et les égalités sont départagées par ID.
Les paramètres invalides donnent 400, un produit inconnu 404 et un token absent
ou invalide 401.

Le modèle Java est un réseau entièrement connecté : entrée multi-hot représentant
les produits du panier, couche cachée tanh (4 à 16 neurones), sorties sigmoid.
Chaque panier d'au moins deux produits distincts produit un exemple par produit
masqué. Le réseau apprend à retrouver ce produit avec une perte d'entropie croisée
binaire et une rétropropagation, durant 150 époques, au taux 0,05, avec pénalisation
L2 de 0,001 sur les poids. La graine 42 rend l'entraînement reproductible.
Les quantités ne multiplient pas les occurrences dans un panier ; elles servent
au classement de popularité. Les sorties ne sont pas des probabilités calibrées.

Pour un produit connu de l'entraînement, le réseau classe les autres produits
connus. Les produits restants sont proposés par similitude Jaccard des catégories,
puis par quantités vendues, puis par ID. Sans panier exploitable pour le produit,
les mêmes compléments prennent le relais. L'historique est relu et le modèle est
réentraîné à chaque requête : solution pédagogique pour un petit catalogue,
coûteuse pour un grand historique. Aucune identité client n'entre dans le modèle.

Avec peu de ventes, le risque de surapprentissage est élevé : le réseau peut
mémoriser les exemples sans généraliser. Le test d'apprentissage démontre une
association apprise, pas une efficacité commerciale. Une évaluation sur des ventes
réservées avant l'entraînement est nécessaire pour mesurer la généralisation.

Tests : dans `app_eval`, utiliser Java 21 puis `mvn test`. Les tests couvrent
l'apprentissage reproductible, l'historique insuffisant, l'API sur base H2,
l'authentification et les erreurs, en complément de la suite existante.

Le diagramme de base de données a été créé avec [drawdb.app](https://drawdb.app/)

Le schéma des modules a été créé avec [Excalidraw](https://excalidraw.com/)
