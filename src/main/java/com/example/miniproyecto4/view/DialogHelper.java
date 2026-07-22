package com.example.miniproyecto4.view;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * Tiny static helper so every controller shows validation/error dialogs
 * with the exact same look, instead of duplicating the same five lines
 * of {@link Alert} boilerplate in every {@code catch} block across the
 * project. This is what several {@code // TODO: show an Alert} comments
 * left throughout the controllers were waiting for.
 *
 * @author Alejandro Valencia Sandoval
 * @author Maria Alejandra Pizarro Sarria
 */
public final class DialogHelper
{
    private DialogHelper()
    {
    }

    /**
     * Shows a blocking informational dialog confirming that something the
     * player explicitly asked for (e.g. "Guardar partida") actually
     * succeeded.
     *
     * @param title   short dialog title
     * @param message plain-language confirmation
     */
    public static void showInfo(String title, String message)
    {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Shows a blocking warning dialog for a recoverable, expected problem
     * (invalid ship placement, cell already shot, incomplete fleet, etc.):
     * something the player did that the game correctly rejected, but that
     * they need to be told about to understand why nothing happened.
     *
     * @param title   short dialog title
     * @param message plain-language explanation of what went wrong
     */
    public static void showWarning(String title, String message)
    {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Shows a blocking error dialog for an unexpected technical failure
     * (I/O, serialization, navigation), where {@code cause} usually comes
     * from one of this project's own checked exceptions.
     *
     * @param title   short dialog title
     * @param message plain-language explanation of what went wrong
     * @param cause   the underlying exception, only used for its message
     */
    public static void showError(String title, String message, Throwable cause)
    {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        String detail = cause != null && cause.getMessage() != null
                ? message + System.lineSeparator() + System.lineSeparator() + cause.getMessage()
                : message;
        alert.setContentText(detail);
        alert.showAndWait();
    }
}
