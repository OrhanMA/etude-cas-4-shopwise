# Justifications par rapport aux attributs de qualité

## Évolutivité

L'application est découpée en modules à responsabilités distinctes.

Le moteur de recommandation est notamment isolé du catalogue et du module de ventes.
Une nouvelle stratégie de recommandation peut donc être ajoutée sans modifier la logique métier des ventes.
L'interface `RecommendationStrategy` permet de remplacer le modèle et
`RecommendationDataProvider` sépare la source de données. Il n'existe pas de table
d'embeddings ni de version persistée du modèle dans l'implémentation actuelle.

## Robustesse

Les contraintes de base de données (NOT NULL, UNIQUE, FK), les validations métier du service de ventes et Spring Security empêchent plusieurs états incohérents.
Les erreurs applicatives sont centralisées via `@RestControllerAdvice`. Les
filtres de sécurité gèrent les refus d'authentification et d'autorisation.

## Testabilité

Les responsabilités sont séparées entre controllers, services et repositories.
La logique métier peut donc être testée indépendamment : tests unitaires des services avec repositories mockés, tests d'intégration sur la persistence et tests API sur les controllers.
C'est cohérent avec la Q5, qui demande différents niveaux de tests.
