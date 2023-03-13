package dev.turtywurty.turtylib.book;

public enum PagePosition {
    //@formatter:off
    TOP_LEFT(0, 0), TOP_MIDDLE(0.5, 0), TOP_RIGHT(1, 0),
    ABOVE_MIDDLE_LEFT(0, 0.25), ABOVE_MIDDLE(0.5, 0.25), ABOVE_MIDDLE_RIGHT(1, 0.25),
    MIDDLE_LEFT(0, 0.5), MIDDLE(0.5, 0.5), MIDDLE_RIGHT(1, 0.5),
    BELOW_MIDDLE_LEFT(0, 0.75), BELOW_MIDDLE(0.5, 0.75), BELOW_MIDDLE_RIGHT(1, 0.75),
    BOTTOM_LEFT(0, 1), BOTTOM_MIDDLE(0.5, 1), BOTTOM_RIGHT(1, 1);
    //@formatter:on
    
    public final double x, y;

    PagePosition(double x, double y) {
        this.x = x;
        this.y = y;
    }
}
