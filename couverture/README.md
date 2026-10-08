# Rapports de couverture

Mesure du 8 octobre 2026, Java 21, JaCoCo 0.8.13, commande `mvn clean verify`
depuis `app_eval` : 32 tests, zéro échec, zéro erreur, aucun test ignoré.

| Mesure | Couvert | Total | Taux |
|---|---:|---:|---:|
| Lignes | 572 | 830 | 68,92 % |
| Branches | 117 | 154 | 75,97 % |
| Instructions | 2 494 | 3 442 | 72,46 % |

Ouvrir `backend/index.html` localement. `backend/jacoco.xml` et `backend/jacoco.csv`
fournissent les données exploitables automatiquement. Les classes générées et
les DTO sont inclus. Les parcours HTTP externes ne sont pas comptés dans ce rapport.
La couverture frontend est non applicable au périmètre backend de cette étude.

Pour actualiser le rapport après une évolution :

```bash
cd app_eval
mvn clean verify
cd ..
cp -R app_eval/target/site/jacoco/. couverture/backend/
```

La couverture est un indicateur de code exercé, pas une preuve de correction ou
de sécurité exhaustive. Le README décrit aussi les tests HTTP reproductibles.
