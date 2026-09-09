### Module Catalogue

Responsable de la gestion des produits et des catégories.

Il doit notamment :

* créer, modifier, consulter et supprimer les produits ;
* gérer les catégories ;
* gérer l’association entre produits et catégories ;
* fournir les données des produits aux autres modules, notamment au module de recommandation.

Il ne doit pas contenir la logique des ventes ni celle de ML.

### Module Ventes

Responsable de l’enregistrement et de la consultation des ventes.

Il doit notamment :

* créer une vente ;
* enregistrer les produits vendus, leurs quantités et leurs prix unitaires ;
* calculer automatiquement le montant total ;
* consulter la liste des ventes ;
* consulter le détail d’une vente ;
* fournir l’historique des ventes au module de recommandation.

Ce module dépend du catalogue pour vérifier que les produits existent, par contre, il ne doit pas connaître l’implémentation interne du moteur de recommandation.

### Module Recommandation

Responsable de la génération des recommandations des produits.

Il doit notamment :

* exploiter l’historique des ventes ;
* exploiter les informations du catalogue et des catégories ;
* utiliser les embeddings générés par le modèle ML ;
* calculer un score de similarité entre produits ;
* combiner éventuellement plusieurs signaux : ML, catégories, popularité ;
* retourner les produits les plus pertinents via l’API ;
* permettre de changer ou faire évoluer l’algorithme sans modifier les autres modules.

Cela correspond à l’US8, qui demande que le module de recommandation soit isolé et évolutif.

### Module Authentification / Sécurité

Responsable de l’identification des utilisateurs et du contrôle des accès.

Il doit notamment :

* authentifier un utilisateur à partir de son email et de son mot de passe ;
* vérifier le hash du mot de passe ;
* générer et valider les tokens JWT ;
* identifier le rôle de l’utilisateur ;
* autoriser ou refuser l’accès aux endpoints protégés ;
* renvoyer `401` pour un utilisateur non authentifié ;
* renvoyer `403` pour un utilisateur authentifié, mais non autorisé.

Cela correspond aux US4 et 5.

### Couche API / Controllers

Responsable de l’exposition des fonctionnalités via HTTP.

Son rôle est de :

* recevoir les requêtes HTTP ;
* valider les données d’entrée ;
* appeler le bon service métier ;
* transformer le résultat en réponse HTTP ;
* retourner les bons codes de statut.

Exemple :

```text
POST /api/sales
        ↓
SalesController
        ↓
SalesService
        ↓
SaleRepository
```

Le controller ne doit pas contenir directement la logique métier.

### Couche Persistence / Repositories

Responsable de l’accès à la base de données.

Elle doit :

* lire et écrire les entités ;
* gérer les requêtes vers PostgreSQL ;
* fournir les données aux services métier ;
* masquer les détails de persistance aux couches supérieures.

Exemple :

```text
SalesService
      ↓
SaleRepository
      ↓
PostgreSQL
```

### Gestion des erreurs

Responsable de la gestion des erreurs dont :

* centraliser les exceptions ;
* convertir les erreurs métier en codes HTTP cohérents ;
* retourner un JSON homogène ;
* éviter que chaque controller gère ses erreurs différemment.

Grâce à ça on peut respecter l'US6, qui exige un format d’erreur standardisé.
