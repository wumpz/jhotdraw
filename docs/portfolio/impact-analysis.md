# Impact Analysis

## Scope of the Refactoring

The change is limited to `AlignAction.java` in `jhotdraw-core`. No public API contracts change: the six subclass constructors are identical; `actionPerformed` is unchanged; `alignFigures` remains `protected` with the same signature.

## Affected Packages / Modules

| Package / Module | # Classes directly touched | Notes |
|---|---|---|
| `jhotdraw-core` — `org.jhotdraw.draw.action` | 1 (`AlignAction`) | The only edited file. New abstract method `getAlignmentTransform` is added; `alignFigures` is made `final`; six inner classes lose their `alignFigures` override and gain `getAlignmentTransform`. |
| `jhotdraw-core` — test | 2 new (`AlignActionTest`, `AlignWestScenarioTest`) | New test files; no production code changed. |
| `jhotdraw-core` — `pom.xml` | 1 | Added `jgiven-junit5` test-scope dependency. |
| `jhotdraw-gui` — `ButtonFactory` | 0 | Creates action instances via constructors that are unchanged. |
| `jhotdraw-samples` — `AlignToolBar` | 0 | Also uses unchanged constructors. |
| All other modules | 0 | No dependency on `AlignAction` internals. |

## Scatter Notes

The feature is **scattered** across three modules:

- `jhotdraw-core`: the action logic (primary locus of change).
- `jhotdraw-gui`: the UI wiring (`ButtonFactory`).
- `jhotdraw-samples`: the sample application toolbar (`AlignToolBar`).

Only `jhotdraw-core` is modified by this refactoring; the other two are impacted by reading only (they call unchanged constructors).

## Risk Assessment

Low risk. The refactoring is behaviour-preserving (same observable figure positions), touches exactly one production file, and is verified by 7 automated tests (6 characterization + 1 BDD scenario) that ran green before and after the change.
