# Change Request

## User Story

**As** a developer maintaining JHotDraw,  
**I want** the six `AlignAction` subclasses to share a single loop skeleton,  
**So that** adding a new alignment direction requires only implementing one method (the per-figure offset), not copying the full iteration and undo-wiring boilerplate.

## Acceptance Criteria

1. `AlignAction.alignFigures()` is a single, final template method in the abstract base class.
2. Each subclass (North, South, East, West, Horizontal, Vertical) implements exactly one protected method: `getAlignmentTransform(figureBounds, selectionBounds)`.
3. All six alignment directions produce the same observable figure positions as before the refactoring (verified by the characterization tests).
4. No feature additions; behaviour-preserving only.

## Feature in Scope

**Align selected figures** — `org.jhotdraw.draw.action.AlignAction` and its six static inner subclasses in module `jhotdraw-core`.
