# Justifications par rapport aux attributs de qualité

## Évolutivité

L'application est découpée en modules à responsabilités distinctes.

Le moteur de recommandation est notamment isolé du catalogue et du module de ventes.
Une nouvelle stratégie de recommandation peut donc être ajoutée sans modifier la logique métier des ventes.
model_version dans product_embeddings permet également de faire évoluer le modèle ML.

## Robustesse

Les contraintes de base de données (NOT NULL, UNIQUE, FK), les validations métier du service de ventes et Spring Security empêchent plusieurs états incohérents.
Les erreurs de l'API peuvent être centralisées via un @RestControllerAdvice, ce qui correspond également à l'US6 sur la normalisation des erreurs.

## Testabilité

Les responsabilités sont séparées entre controllers, services et repositories.
La logique métier peut donc être testée indépendamment : tests unitaires des services avec repositories mockés, tests d'intégration sur la persistence et tests API sur les controllers.
C'est cohérent avec la Q5, qui demande différents niveaux de tests.
