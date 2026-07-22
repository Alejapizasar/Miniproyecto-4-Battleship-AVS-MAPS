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
 * Factory Method que centraliza la construcción de las figuras 2D de un
 * barco completo (proa + cubierta + popa), en lugar de dibujar solo un
 * "hull" genérico por casilla. Se apoya en {@link BoardCellView} solo
 * para conocer el tamaño de celda, pero no depende de JavaFX layout: el
 * llamador decide dónde poner cada segmento devuelto.
 *
 * <p>Este es el punto único donde se decide "cómo se ve" cada tamaño de
 * barco, siguiendo el principio de responsabilidad única (SRP) y abierto
 * a extensión (OCP): agregar un nuevo tamaño de barco solo implica
 * agregar un caso nuevo, sin tocar el resto del código de la UI.</p>
 *
 * @author Alejandro Valencia Sandoval
 */
public final class ShipShapeFactory
{
    private static final double CELL_SIZE = 32.0;

    private ShipShapeFactory()
    {
    }

    /**
     * Construye un segmento de barco para una única casilla dentro de un
     * barco de tamaño {@code shipSize}.
     *
     * @param shipSize        tamaño total del barco (1 a 4)
     * @param segmentIndex    posición de este segmento dentro del barco (0-based)
     * @param orientation     orientación actual del barco
     * @return un {@link Group} listo para insertarse dentro de un {@link BoardCellView}
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

        // La punta debe mirar hacia afuera del barco: si este segmento es
        // la proa (extremo inicial), la punta usa la forma "trasera" del
        // switch de abajo, y viceversa para la popa. Por eso se compara
        // contra isStern y no contra isBow.
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
