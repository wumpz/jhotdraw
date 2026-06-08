# Refactoring

## Smell: Duplicated Code in Sibling Subclasses

Before the refactoring, the six `AlignAction` subclasses each had an `alignFigures` override that was structurally identical except for the per-figure offset calculation. The shared skeleton (~11 lines each):

```java
// BEFORE — repeated verbatim in all six subclasses (West shown)
@Override
protected void alignFigures(
    Collection<Figure> selectedFigures, Rectangle2D.Double selectionBounds) {
  double x = selectionBounds.x;
  for (Figure f : getView().getSelectedFigures()) {
    if (f.isTransformable()) {
      f.willChange();
      Rectangle2D.Double b = f.getBounds();
      AffineTransform tx = new AffineTransform();
      tx.translate(x - b.x, 0);        // ← only this line differs per subclass
      f.transform(tx);
      f.changed();
      fireUndoableEditHappened(new TransformEdit(f, tx));
    }
  }
}
```

## Refactoring Applied: Form Template Method (Kerievsky)

**Pattern:** GoF Template Method.  
**Technique:** Extract Method + Pull Up Method (Fowler / Kerievsky "Form Template Method").

### Steps

1. Identified the invariant skeleton (loop, guard, `willChange`/`changed`, `fireUndoableEditHappened`) shared by all six subclasses.
2. Identified the variant: the `AffineTransform` construction (a pure function of `figureBounds` and `selectionBounds`).
3. Introduced the hook in `AlignAction`:
   ```java
   protected abstract AffineTransform getAlignmentTransform(
       Rectangle2D.Double figureBounds, Rectangle2D.Double selectionBounds);
   ```
4. Made `alignFigures` a `final` concrete template method in `AlignAction`, calling the hook:
   ```java
   // AFTER — single skeleton in AlignAction
   protected final void alignFigures(
       Collection<Figure> selectedFigures, Rectangle2D.Double selectionBounds) {
     for (Figure f : getView().getSelectedFigures()) {
       if (f.isTransformable()) {
         f.willChange();
         AffineTransform tx = getAlignmentTransform(f.getBounds(), selectionBounds);
         f.transform(tx);
         f.changed();
         fireUndoableEditHappened(new TransformEdit(f, tx));
       }
     }
   }
   ```
5. Reduced each subclass to a one-line `getAlignmentTransform` implementation, e.g.:
   ```java
   // AFTER — West subclass (all six follow the same one-liner pattern)
   @Override
   protected AffineTransform getAlignmentTransform(
       Rectangle2D.Double b, Rectangle2D.Double sel) {
     AffineTransform tx = new AffineTransform();
     tx.translate(sel.x - b.x, 0);
     return tx;
   }
   ```

## Commits

| Commit | Message |
|---|---|
| `75c7f7bb` | test: characterization tests for AlignAction alignment behaviour |
| `3fabd2a2` | refactor: Form Template Method on AlignAction (remove duplicated alignment code) |

---

## Smell 2: Long Method + Conditional Complexity in `BezierFigure.contains`

`BezierFigure` is one of the core drawing primitives transformed by `AlignAction` (the action calls `f.transform(tx)` on every selected figure, and after a transform the drawing subsystem calls `contains()` for subsequent hit tests). The method `contains(Point2D.Double p, double scaleDenominator)` suffered from two overlapping smells:

| Metric | Value | Guideline |
|---|---|---|
| Line count | 45 lines | ≤ 15 (Fowler) |
| Conditional branches | 10 | ≤ 4–5 |

### Smell A: Long Method (Fowler §3 "Long Method")

A method should fit on one screen and express a single intent. At 45 lines the method is three times the guideline. The cognitive load is compounded because the reader must track two different checking regimes (filled/closed vs open) simultaneously within the same scope.

### Smell B: Conditional Complexity / Decompose Conditional (Fowler §9)

The method opens with a compound boolean guard and then re-checks `isClosed()` twice more inside the same body—once inside the first `if` branch and once as a separate second `if (!isClosed())` block. This repetition is a signal that the two regimes (filled-or-closed area check vs open-path outline check) should be separated into named units.

```java
// BEFORE — 45 lines, 10 conditional branches (representative excerpt)
public boolean contains(Point2D.Double p, double scaleDenominator) {
    double tolerance = Math.max(1f, 2 * AttributeKeys.getPerpendicularHitGrowth(this, scaleDenominator));
    if (isClosed() || attr().get(FILL_COLOR) != null && attr().get(UNCLOSED_PATH_FILLED)) {
        if (path.contains(p)) { return true; }
        double grow = tolerance;
        GrowStroke gs = new GrowStroke(grow,
            AttributeKeys.getStrokeTotalWidth(this, scaleDenominator) * attr().get(STROKE_MITER_LIMIT));
        if (gs.createStrokedShape(path).contains(p)) {
            return true;
        } else {
            if (isClosed()) { return false; }   // ← isClosed() checked again
        }
    }
    if (!isClosed()) {                          // ← and again
        if (getCappedPath(scaleDenominator).outlineContains(p, tolerance)) { return true; }
        if (attr().get(START_DECORATION) != null) { ... }
        if (attr().get(END_DECORATION)   != null) { ... }
    }
    return false;
}
```

## Refactoring Applied: Extract Method + Decompose Conditional (Fowler §6, §9)

### Steps

1. **Decompose Conditional** — replaced the compound opener `isClosed() || (FILL_COLOR != null && UNCLOSED_PATH_FILLED)` with the named predicate `isFilledOrClosed()`.  The name immediately communicates _why_ the first block executes.

2. **Extract Method** — pulled each checking regime into a focused private helper:

   | Extracted method | Responsibility |
   |---|---|
   | `isFilledOrClosed()` | Guard predicate: is this path treated as a filled area? |
   | `containsInFilledRegion(p, scale, tolerance)` | Hit-test interior + stroked outline of the filled/closed shape |
   | `containsOnOpenPath(p, scale, tolerance)` | Hit-test capped outline + end decorations of an open path |

3. The public method shrank from 45 lines to 11, each line readable on its own:

```java
// AFTER — 11 lines, intent immediately clear
public boolean contains(Point2D.Double p, double scaleDenominator) {
    double tolerance = Math.max(1f, 2 * AttributeKeys.getPerpendicularHitGrowth(this, scaleDenominator));
    if (isFilledOrClosed()) {
        if (containsInFilledRegion(p, scaleDenominator, tolerance)) { return true; }
        if (isClosed()) { return false; }
    }
    return !isClosed() && containsOnOpenPath(p, scaleDenominator, tolerance);
}

private boolean isFilledOrClosed() {
    return isClosed() || (attr().get(FILL_COLOR) != null && attr().get(UNCLOSED_PATH_FILLED));
}
```

### Note on Primitive Obsession

The helper methods take `(Point2D.Double p, double scaleDenominator, double tolerance)`. Two doubles travelling together could be wrapped in a `HitProbe` value object (Introduce Parameter Object), but with only two primitive parameters this is below the threshold that justifies a new class.

## Commits (Refactoring 2)

| Commit | Message |
|---|---|
| `1057d270` | test: characterization tests for BezierFigure.contains |
| `98e9223b` | refactor: Extract Method + Decompose Conditional on BezierFigure.contains |
