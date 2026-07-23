package com.example.miniproyecto4.view;

import com.example.miniproyecto4.model.Orientation;

import javafx.scene.Group;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;

/**
 * Factory class responsible for creating the graphical representation
 * of ship segments using JavaFX shapes. It generates the appropriate
 * bow, middle, or stern section according to the ship size, segment
 * position, and orientation.
 *
 * @author Alejandro Valencia Sandoval
 * @author Maria Alejandra Pizarro Sarria
 */
public final class ShipShapeFactory
{
    private static final double CELL_SIZE = 32.0;

    /**
     * Prevents instantiation of this utility class.
     */
    private ShipShapeFactory()
    {
    }

    /**
     * Creates the graphical representation of a ship segment.
     *
     * @param shipSize the total number of segments that compose the ship.
     * @param segmentIndex the position of the segment within the ship.
     * @param orientation the orientation of the ship.
     * @return a JavaFX group containing the shapes that represent the
     *         requested ship segment.
     */
    public static Group buildSegment(int shipSize, int segmentIndex, Orientation orientation)
    {
        boolean isBow = segmentIndex == 0;
        boolean isStern = segmentIndex == shipSize - 1;

        Group segment = new Group();
        segment.getChildren().add(buildDeck(isBow, isStern, orientation));
        segment.getChildren().add(buildDetailLine(orientation));

        segment.setEffect(new DropShadow(3, Color.web("#000000", 0.6)));
        return segment;
    }

    // Cubierta base: rectangulo con esquinas redondeadas en el segmento
    // central, y forma de "proa" en punta si es el primer/ultimo segmento.
    /**
     * Creates the main deck shape for a ship segment.
     *
     * @param isBow indicates whether the segment is the bow.
     * @param isStern indicates whether the segment is the stern.
     * @param orientation the orientation of the ship.
     * @return the JavaFX node representing the deck of the segment.
     */
    private static javafx.scene.Node buildDeck(boolean isBow, boolean isStern, Orientation orientation)
    {
        LinearGradient hullGradient = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#6E6E6E")),
                new Stop(1, Color.web("#3A3A3A")));

        if (!isBow && !isStern)
        {
            Rectangle midHull = new Rectangle(CELL_SIZE * 0.8, CELL_SIZE * 0.8);
            midHull.setArcWidth(4);
            midHull.setArcHeight(4);
            midHull.setFill(hullGradient);
            midHull.setStroke(Color.web("#8B7043"));
            midHull.setStrokeWidth(1.2);
            midHull.setTranslateX(-CELL_SIZE * 0.4);
            midHull.setTranslateY(-CELL_SIZE * 0.4);
            return midHull;
        }

        double half = CELL_SIZE * 0.4;
        Polygon hullPolygon = new Polygon();

        boolean pointsForward = isStern;
        if (orientation == Orientation.HORIZONTAL)
        {
            if (pointsForward)
            {
                hullPolygon.getPoints().addAll(
                        -half, -half * 0.9,
                        half * 0.4, -half,
                        half, 0.0,
                        half * 0.4, half,
                        -half, half * 0.9);
            }
            else
            {
                hullPolygon.getPoints().addAll(
                        half, -half * 0.9,
                        -half * 0.4, -half,
                        -half, 0.0,
                        -half * 0.4, half,
                        half, half * 0.9);
            }
        }
        else
        {
            if (pointsForward)
            {
                hullPolygon.getPoints().addAll(
                        -half * 0.9, -half,
                        -half, half * 0.4,
                        0.0, half,
                        half, half * 0.4,
                        half * 0.9, -half);
            }
            else
            {
                hullPolygon.getPoints().addAll(
                        -half * 0.9, half,
                        -half, -half * 0.4,
                        0.0, -half,
                        half, -half * 0.4,
                        half * 0.9, half);
            }
        }

        hullPolygon.setFill(hullGradient);
        hullPolygon.setStroke(Color.web("#8B7043"));
        hullPolygon.setStrokeWidth(1.2);
        return hullPolygon;
    }

    // Linea fina de "junta de cubierta" para dar sensacion de segmentos unidos.
    /**
     * Creates the decorative line used to visually connect ship
     * segments.
     *
     * @param orientation the orientation of the ship.
     * @return a line representing the deck detail.
     */
    private static javafx.scene.shape.Line buildDetailLine(Orientation orientation)
    {
        javafx.scene.shape.Line line;
        if (orientation == Orientation.HORIZONTAL)
        {
            line = new javafx.scene.shape.Line(-CELL_SIZE * 0.3, 0, CELL_SIZE * 0.3, 0);
        }
        else
        {
            line = new javafx.scene.shape.Line(0, -CELL_SIZE * 0.3, 0, CELL_SIZE * 0.3);
        }
        line.setStroke(Color.web("#B0AFAF", 0.5));
        line.setStrokeWidth(1.0);
        return line;
    }
}