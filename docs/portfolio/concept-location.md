# Concept Location

## Feature: Align Selected Figures

Concept location was performed by text search for "align" in class names and resource-bundle keys, followed by call-hierarchy tracing from `actionPerformed`.

## Domain Class Table

| Domain Class | Module | Responsibility in this feature |
|---|---|---|
| `AlignAction` | `jhotdraw-core` (`draw.action`) | Abstract base; owns `actionPerformed`, `getSelectionBounds`, and (after refactoring) the template method `alignFigures` and hook `getAlignmentTransform`. |
| `AlignAction.North` | `jhotdraw-core` | Aligns top edges to `selectionBounds.y`. |
| `AlignAction.South` | `jhotdraw-core` | Aligns bottom edges to `selectionBounds.y + selectionBounds.height`. |
| `AlignAction.East` | `jhotdraw-core` | Aligns right edges to `selectionBounds.x + selectionBounds.width`. |
| `AlignAction.West` | `jhotdraw-core` | Aligns left edges to `selectionBounds.x`. |
| `AlignAction.Horizontal` | `jhotdraw-core` | Centers figures horizontally within the selection. |
| `AlignAction.Vertical` | `jhotdraw-core` | Centers figures vertically within the selection. |
| `Figure` | `jhotdraw-core` (`draw.figure`) | Target of alignment: `willChange()`, `transform(AffineTransform)`, `changed()`, `getBounds()`, `isTransformable()`. |
| `RectangleFigure` | `jhotdraw-core` | Concrete figure used in characterization tests. |
| `DrawingView` | `jhotdraw-core` | Provides `getSelectedFigures()` and `getSelectionCount()`; accessed via `getView()` in `AbstractSelectedAction`. |
| `TransformEdit` | `jhotdraw-core` (`draw.event`) | Undoable edit recording a single figure transform; created for each moved figure. |
| `CompositeEdit` | `jhotdraw-utils` | Groups the per-figure `TransformEdit`s into a single undoable operation. |

## Entry Points Found in Other Modules

| Location | Role |
|---|---|
| `jhotdraw-gui` — `ButtonFactory` | Creates toolbar/menu-item buttons wired to `AlignAction` subclass instances. |
| `jhotdraw-samples` — `AlignToolBar` | Sample application toolbar that instantiates and displays the six align actions. |
