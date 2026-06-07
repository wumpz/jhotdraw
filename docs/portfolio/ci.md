# CI Pipeline

## Pipeline Location

`.github/workflows/maven.yml` — updated in commit `8c2d40c3`.

## Configuration

```yaml
name: Java CI with Maven

on:
  push:
    branches: [ "**" ]
  pull_request:
    branches: [ "**" ]

jobs:
  build:
    runs-on: ubuntu-latest
    strategy:
      matrix:
        java-version: [ 17, 24, 25 ]
    steps:
      - uses: actions/checkout@v4
      - name: Set up JDK ${{ matrix.java-version }}
        uses: actions/setup-java@v4
        with:
          java-version: ${{ matrix.java-version }}
          distribution: 'temurin'
          cache: maven
      - name: Build with Maven
        run: mvn -B clean install
```

## What the Pipeline Verifies

`mvn -B clean install` runs, in order:

1. **Spotless** (validate phase) — enforces Palantir Java Format (Google style, 120 chars).
2. **Checkstyle** (process-classes phase) — enforces import hygiene, brace style, tab characters, etc.
3. **Compile** — all 10 modules.
4. **Test** — JUnit 5 tests including the 6 characterization tests and 1 BDD scenario added on this branch.
5. **Install** — installs artifacts to local repository for inter-module resolution.

The matrix runs on JDK 17, 24, and 25 (Temurin distribution), matching the project's multi-JDK build introduced in an earlier commit.

## Trigger

Triggers on every push and every pull request, for all branches. This means the feature branch `feature/align-form-template-method` is covered without requiring a merge to `develop` first.
