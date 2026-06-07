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
