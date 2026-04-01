# PCGen - Build & Run

## Prérequis

- JDK 25 (Temurin) - Gradle le télécharge automatiquement via le plugin Foojay

## Commandes

| Action | Commande |
|---|---|
| Build (sans tests) | `./gradlew build -x test` |
| Lancer l'application | `./gradlew run` |
| Build + tous les tests | `./gradlew clean build slowtest` |
| Tests data uniquement | `./gradlew datatest` |
| Tests d'intégration | `./gradlew inttest` |

`./gradlew run` compile automatiquement avant de lancer.
