# Actualization (Conceptual — No Code Change)

This note describes how the refactored `AlignAction` relates to SOLID principles and Clean Architecture. **No implementation is proposed here.**

---

## SOLID Examples from This Refactoring

### Open-Closed Principle (OCP)

Before the refactoring, adding a seventh alignment direction required copying ~11 lines of boilerplate into a new `alignFigures` override. After the refactoring, a new direction is added by:

1. Extending `AlignAction`.
2. Implementing the one-line `getAlignmentTransform` hook.

The template-method skeleton is **closed** for modification; the feature set is **open** for extension.

### Single Responsibility Principle (SRP)

Post-refactoring, `AlignAction` has a clearly separated responsibility:

- `alignFigures` (template method): iteration, guard, undo-wiring — the *how to apply*.
- `getAlignmentTransform` (hook): the geometry — the *what to apply*.

Each subclass has exactly one reason to change: its alignment formula.

### Liskov Substitution Principle (LSP)

Each subclass is substitutable for `AlignAction` wherever an alignment action is needed. The `final` modifier on `alignFigures` enforces that no subclass can break the loop contract or bypass the undo-wiring, strengthening the LSP guarantee.

---

## Clean Architecture Mapping

| Clean Architecture layer | JHotDraw concept | Class(es) |
|---|---|---|
| **Entities** (enterprise business rules) | The drawing domain — shapes and their geometry | `Figure`, `Drawing`, `RectangleFigure`, `TransformEdit` |
| **Use Cases** (application business rules) | The alignment operation — what "align West" means | `AlignAction` and its subclasses |
| **Interface Adapters** | Swing UI wiring | `ButtonFactory` (jhotdraw-gui), `AlignToolBar` (jhotdraw-samples) |
| **Frameworks & Drivers** | Swing event loop, OS windowing | `DrawingView` implementation, `JComponent` hierarchy |

The refactoring keeps the dependency rule intact: `AlignAction` depends only on the `Figure` and `DrawingView` interfaces (Entities / Use Cases layer), never on concrete Swing components. The `DrawingViewStub` in the tests confirms that the Use Case layer is independently testable without any Frameworks layer.
