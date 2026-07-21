package com.example.miniproyecto4.view;

import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;

/**
 * A single cell of a Battleship board, drawn entirely with JavaFX 2D shapes
 * (no images). Each instance is a small {@link StackPane} that can be added
 * directly into a {@code GridPane} column/row, and redrawn on demand by
 * calling {@link #setState(CellState)}.
 *
 * <p>This class is intentionally the only place in the UI layer that knows
 * how to draw a cell, so the board colors/shapes can be tuned in one spot
 * without touching the controller (single responsibility).</p>
 *
 * @author Alejandro Valencia Sandoval
 */
public class BoardCellView extends StackPane
{
    private static final double CELL_SIZE = 32.0;

    private static final Color WATER_DARK = Color.web("#0B3D5C");
    private static final Color WATER_LIGHT = Color.web("#2E7DAF");
    private static final Color HULL_COLOR = Color.web("#4A4A4A");
    private static final Color HULL_BORDER = Color.web("#8B7043");
    private static final Color MISS_MARK = Color.web("#D9E6EF");
    private static final Color SUNK_OVERLAY = Color.web("#1A1A1A");

    private CellState state;

    public BoardCellView()
    {
        this.state = CellState.WATER;
        this.setPrefSize(CELL_SIZE, CELL_SIZE);
        this.setMinSize(CELL_SIZE, CELL_SIZE);
        this.setAlignment(Pos.CENTER);
        this.render();
    }

    /**
     * Updates the logical state of the cell and redraws its graphic.
     *
     * @param newState the new state to represent visually
     */
    public void setState(CellState newState)
    {
        this.state = newState;
        this.render();
    }

    public CellState getState()
    {
        return this.state;
    }

    // Clears the current children and rebuilds the shapes for the current state.
    private void render()
    {
        this.getChildren().clear();
        this.getChildren().add(this.buildWater());

        switch (this.state)
        {
            case SHIP:
                this.getChildren().add(this.buildShipHull());
                break;
            case MISS:
                this.getChildren().add(this.buildMissMark());
                break;
            case HIT:
                this.getChildren().add(this.buildShipHull());
                this.getChildren().add(this.buildHitBurst());
                break;
            case SUNK:
                this.getChildren().add(this.buildShipHull());
                this.getChildren().add(this.buildSunkOverlay());
                break;
            case WATER:
            default:
                break;
        }
    }

    // Base tile: a soft blue gradient plus two faint wave lines for texture.
    private StackPane buildWater()
    {
        StackPane layer = new StackPane();

        Rectangle background = new Rectangle(CELL_SIZE, CELL_SIZE);
        background.setFill(new RadialGradient(0, 0, 0.5, 0.5, 0.9, true,
                CycleMethod.NO_CYCLE,
                new Stop(0, WATER_LIGHT),
                new Stop(1, WATER_DARK)));

        Polyline waveOne = new Polyline(
                4, 10, 10, 14, 16, 10, 22, 14, 28, 10);
        waveOne.setStroke(Color.web("#8FD0F0", 0.35));
        waveOne.setStrokeWidth(1.2);
        waveOne.setFill(null);

        Polyline waveTwo = new Polyline(
                4, 20, 10, 24, 16, 20, 22, 24, 28, 20);
        waveTwo.setStroke(Color.web("#8FD0F0", 0.25));
        waveTwo.setStrokeWidth(1.2);
        waveTwo.setFill(null);

        layer.getChildren().addAll(background, waveOne, waveTwo);
        return layer;
    }

    // A rounded hull segment representing part of a ship on this cell.
    private Rectangle buildShipHull()
    {
        Rectangle hull = new Rectangle(CELL_SIZE * 0.72, CELL_SIZE * 0.72);
        hull.setArcWidth(8);
        hull.setArcHeight(8);
        hull.setFill(HULL_COLOR);
        hull.setStroke(HULL_BORDER);
        hull.setStrokeWidth(1.5);
        hull.setEffect(new DropShadow(4, Color.BLACK));
        return hull;
    }

    // "Agua": an X mark over the water, per the assignment's own notation.
    private javafx.scene.Group buildMissMark()
    {
        Line lineA = new Line(-8, -8, 8, 8);
        Line lineB = new Line(-8, 8, 8, -8);
        for (Line line : new Line[] { lineA, lineB })
        {
            line.setStroke(MISS_MARK);
            line.setStrokeWidth(2.5);
            line.setStrokeLineCap(StrokeLineCap.ROUND);
        }
        return new javafx.scene.Group(lineA, lineB);
    }

    // "Tocado": a small fiery burst drawn from layered circles.
    private javafx.scene.Group buildHitBurst()
    {
        Circle outer = new Circle(9);
        outer.setFill(new RadialGradient(0, 0, 0.5, 0.5, 1, true,
                CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#FFD65C")),
                new Stop(0.5, Color.web("#F26B2B")),
                new Stop(1, Color.web("#B3241C", 0.0))));

        Circle core = new Circle(4);
        core.setFill(Color.web("#FFF2C4"));

        return new javafx.scene.Group(outer, core);
    }

    // "Hundido": darkens the whole hull and marks it with a bold red X.
    private javafx.scene.Group buildSunkOverlay()
    {
        Rectangle darken = new Rectangle(CELL_SIZE, CELL_SIZE);
        darken.setFill(Color.color(SUNK_OVERLAY.getRed(), SUNK_OVERLAY.getGreen(),
                SUNK_OVERLAY.getBlue(), 0.55));

        Line lineA = new Line(-10, -10, 10, 10);
        Line lineB = new Line(-10, 10, 10, -10);
        for (Line line : new Line[] { lineA, lineB })
        {
            line.setStroke(Color.web("#C62828"));
            line.setStrokeWidth(3);
            line.setStrokeLineCap(StrokeLineCap.ROUND);
        }

        return new javafx.scene.Group(darken, lineA, lineB);
    }
}
