# Choix de sécurité, d'authentification et de normalisation

Justification des choix pour les US 4, 5 et 6.

## US 4 - Autorisation par rôle

Les opérations sensibles sur les produits et les ventes sont réservées aux
administrateurs. Les règles sont déclarées dans `SecurityConfig` avec une
`SecurityFilterChain` et `authorizeHttpRequests` :

- `POST`, `PUT` et `DELETE` sur `/api/products/**` exigent le rôle `ADMIN` ;
- `POST`, `PUT` et `DELETE` sur `/api/sales/**` exigent le rôle `ADMIN` ;
- un utilisateur authentifié avec le rôle `USER` reçoit `403 Forbidden` ;
- un utilisateur sans authentification reçoit `401 Unauthorized`.

Le choix de `hasRole("ADMIN")` rend les règles lisibles et évite de disperser les
vérifications de rôle dans les contrôleurs. Spring Security convertit le rôle
applicatif `ADMIN` en autorité `ROLE_ADMIN` lors du chargement du `UserDetails`.

## US 5 - Authentification JWT

L'API utilise un token JWT Bearer et une session stateless :

1. le client envoie son email et son mot de passe à `POST /api/auth/login` ;
2. `AuthenticationManager` utilise `CustomUserDetailsService` pour charger
   l'utilisateur depuis la base ;
3. le mot de passe est comparé avec son hash BCrypt ;
4. un JWT signé est généré avec l'email comme sujet et le rôle comme claim ;
5. le client renvoie `Authorization: Bearer <token>` sur les requêtes suivantes ;
6. le Resource Server vérifie la signature et l'expiration du JWT.

JWT est adapté à une API stateless : le serveur ne conserve pas de session entre
deux requêtes. La durée d'expiration est configurable avec
`app.security.jwt.expiration-seconds` et le secret avec `JWT_SECRET`.

Les mots de passe ne sont jamais stockés en clair. `BCryptPasswordEncoder` est
utilisé lors de la création et de la modification d'un utilisateur. Le seed H2
contient uniquement des hashes BCrypt de démonstration.

## US 6 - Normalisation des erreurs

Les erreurs applicatives utilisent un contrat commun :

```json
{
  "status": 404,
  "message": "Sale not found",
  "timestamp": "2026-09-10T19:00:00Z",
  "details": null
}
```

`details` contient les erreurs de validation par champ lorsque cela est nécessaire :

```json
{
  "status": 400,
  "message": "Validation failed",
  "timestamp": "2026-09-10T19:00:00Z",
  "details": {
    "quantity": "must be greater than 0"
  }
}
```

La classe `ApiError` centralise ce format. `CustomExceptionHandler` le renvoie pour
les erreurs de validation, les identifiants invalides, les ressources inexistantes
et les erreurs inattendues. Les handlers configurés dans `SecurityConfig` utilisent
le même couple `status` / `message` pour les erreurs `401` et `403` produites par
les filtres Spring Security.

| Situation | Code HTTP | Message |
|---|---:|---|
| Requête invalide | 400 | `Validation failed` |
| Token absent ou invalide | 401 | `Authentication required` |
| Identifiants invalides | 401 | `Invalid credentials` |
| Rôle insuffisant | 403 | `Access denied: ADMIN role required` |
| Ressource inexistante | 404 | message métier, par exemple `Sale not found` |
| Erreur inattendue | 500 | `An unexpected error occurred` |

## Limites et évolutions

Pour une application en production, il faudra faire les modifications suivantes :

- remplacer le secret HMAC de démonstration par un secret géré par un coffre de
  secrets ou une paire de clés asymétriques ;
- ajouter la rotation des clés JWT et un refresh token ;
- ne pas exposer l'API CRUD utilisateur directement en production ;
- compléter les tests par des scénarios de token expiré et falsifié.
