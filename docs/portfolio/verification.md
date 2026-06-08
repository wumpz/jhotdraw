# Verification

## Test Suite

### Characterization Tests (`AlignActionTest`) — commit `75c7f7bb`

Six JUnit 5 + AssertJ tests, one per alignment direction, located at:  
`jhotdraw-core/src/test/java/org/jhotdraw/draw/action/AlignActionTest.java`

Test figures (x, y, width, height):

| Figure | Before |
|---|---|
| f1 | (10, 20, 50, 30) |
| f2 | (40, 60, 60, 40) |
| f3 | (80, 10, 30, 50) |

Selection bounds: x=10, y=10, w=100, h=90.

| Test | Direction | Assertion |
|---|---|---|
| `alignNorth_allTopEdgesEqualSelectionTop` | North | `f.getBounds().y == 10` for all three figures |
| `alignSouth_allBottomEdgesEqualSelectionBottom` | South | `f.getBounds().y + height == 100` |
| `alignWest_allLeftEdgesEqualSelectionLeft` | West | `f.getBounds().x == 10` |
| `alignEast_allRightEdgesEqualSelectionRight` | East | `f.getBounds().x + width == 110` |
| `alignHorizontal_allCentersXEqualSelectionCenterX` | Horizontal | `f.getBounds().x + width/2 == 60` |
| `alignVertical_allCentersYEqualSelectionCenterY` | Vertical | `f.getBounds().y + height/2 == 55` |

### BDD Scenario (`AlignWestScenarioTest`) — commit `e7138fa7`

JGiven (jgiven-junit5 1.3.1) + AssertJ, located at:  
`jhotdraw-core/src/test/java/org/jhotdraw/draw/action/AlignWestScenarioTest.java`

```
Scenario: figures at different x positions are aligned to the leftmost x
  Given several figures at different x positions
  When  align west is invoked
  Then  all figures share the leftmost x coordinate
```

## Behaviour-Preservation Argument

The characterization tests were written **before** touching `AlignAction` (commit `75c7f7bb`) and run **green against the original code**. The same tests were run again after the refactoring (commit `3fabd2a2`) and remain **green without any test modification**. This is the standard characterization-test argument for behaviour preservation: if the observable outputs (figure bounds) are identical under the same inputs, behaviour has not changed.

```
Before refactoring:  Tests run: 6, Failures: 0, Errors: 0  ✓
After refactoring:   Tests run: 6, Failures: 0, Errors: 0  ✓
BDD scenario:        Tests run: 1, Failures: 0, Errors: 0  ✓
```

---

## Refactoring 2 — `BezierFigure.contains`

### Characterization Tests (`BezierFigureContainsTest`) — commit `1057d270`

Seven JUnit 5 + AssertJ tests, located at:  
`jhotdraw-core/src/test/java/org/jhotdraw/draw/figure/BezierFigureContainsTest.java`

Fixture figures:

| Figure | Description |
|---|---|
| `closedSquare` | Closed BezierFigure, nodes at (10,10), (110,10), (110,110), (10,110) |
| `openLine` | Open BezierFigure, nodes at (0,0) and (100,0) |

| Test | Scenario | Expected |
|---|---|---|
| `closedFigure_pointDeepInside_returnsTrue` | (60,60) inside square | `true` |
| `closedFigure_pointClearlyOutside_returnsFalse` | (300,300) far outside | `false` |
| `closedFigure_pointOnCornerNode_returnsTrue` | (10,10) on a corner | `true` |
| `closedFigure_pointJustOutsideButWithinStrokeTolerance_returnsTrue` | (60,9) 1px outside edge | `true` |
| `closedFigure_pointFarOutsideStrokeTolerance_returnsFalse` | (60,−50) 50px outside | `false` |
| `openFigure_pointOnPathMidpoint_returnsTrue` | (50,0) on the outline | `true` |
| `openFigure_pointFarFromPath_returnsFalse` | (50,200) far away | `false` |

### Behaviour-Preservation Argument (Refactoring 2)

The 7 characterization tests were written at commit `1057d270`, run **green against the original code**, and run again after the refactoring at `98e9223b`—**green without any test modification**. The public method shrank from 45 lines to 11, but all observable hit-test results are unchanged.

```
Before refactoring:  Tests run: 7, Failures: 0, Errors: 0  ✓
After refactoring:   Tests run: 7, Failures: 0, Errors: 0  ✓
Full suite (core):   Tests run: 21, Failures: 0, Errors: 0 ✓
```

## Test Infrastructure Notes

`DrawingView` is a large Swing-coupled interface (~55 methods). Rather than wiring a real `DefaultDrawingView` (which requires a Swing component tree), the tests use a `DrawingViewStub` inner class that implements only `getSelectedFigures()` meaningfully; all other methods are either no-ops or throw `UnsupportedOperationException`. The action subclass is constructed with `null` editor (safe: `AbstractSelectedAction.setEditor(null)` skips event-handler registration) and has `fireUndoableEditHappened` overridden to a no-op, removing the `Drawing` dependency.
