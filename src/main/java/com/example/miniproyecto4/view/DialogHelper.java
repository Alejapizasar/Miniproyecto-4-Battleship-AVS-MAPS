package com.example.miniproyecto4.view;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;

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
    // NOTE: there are two near-duplicate stylesheets in this project -
    // cssguide/battleship.css (loaded by PlayerView.fxml) does NOT define
    // the .themed-alert rules, while css/battleship2game.css (loaded by
    // GameView.fxml) does, including ".content.label { -fx-wrap-text:
    // true; }". Alerts are their own Stage and never inherit the calling
    // scene's stylesheet, so DialogHelper must explicitly load the
    // complete one here - otherwise the content Label never wraps and
    // long messages (e.g. from PlayerController) render clipped with an
    // ellipsis instead of wrapping to multiple lines.
    private static final String STYLESHEET_PATH = "/com/example/miniproyecto4/css/battleship2game.css";

    // Unicode glyphs standing in for the icon, styled as a round badge by
    // .alert-icon in battleship.css - no extra image assets needed, and
    // each one reads as "naval" instead of the generic OS info/warning/
    // error icon Alert shows by default.
    private static final String INFO_GLYPH = "\u2693";    // anchor
    private static final String WARNING_GLYPH = "\u26A0"; // warning triangle
    private static final String ERROR_GLYPH = "\u2620";   // skull and crossbones

    private DialogHelper()
    {
    }

    // Every Alert opens in its own Stage/Scene, so the main app's
    // stylesheet is not inherited automatically - it has to be attached
    // to the DialogPane by hand, once per alert, along with a type-
    // specific style class (see .themed-alert / .alert-warning / etc.
    // in battleship.css) so warnings, errors and info popups each get
    // their own accent color instead of all looking identical.
    private static void applyTheme(Alert alert, String typeStyleClass, String glyph)
    {
        alert.getDialogPane().getStylesheets().add(
                DialogHelper.class.getResource(STYLESHEET_PATH).toExternalForm());
        alert.getDialogPane().getStyleClass().addAll("themed-alert", typeStyleClass);
        // typeStyleClass is "alert-info" / "alert-warning" / "alert-error";
        // battleship.css defines the matching icon badge as
        // "alert-icon-info" / "alert-icon-warning" / "alert-icon-error".
        String iconStyleClass = "alert-icon-" + typeStyleClass.substring("alert-".length());
        alert.setGraphic(DialogHelper.buildIcon(glyph, iconStyleClass));

        // The .content.label CSS rule alone was losing to JavaFX's own
        // default dialog styling in practice, leaving the message a dull
        // gray instead of white. Forcing it inline guarantees it wins,
        // regardless of stylesheet load order/specificity quirks.
        javafx.scene.Node contentLabel = alert.getDialogPane().lookup(".content.label");
        if (contentLabel != null)
        {
            contentLabel.setStyle("-fx-text-fill: white;");
        }
    }

    // Replaces JavaFX's default AlertType icon (the small OS-style i / !
    // / x graphic) with a round badge showing a themed glyph instead.
    private static Label buildIcon(String glyph, String iconStyleClass)
    {
        Label icon = new Label(glyph);
        icon.getStyleClass().addAll("alert-icon", iconStyleClass);
        return icon;
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
        DialogHelper.applyTheme(alert, "alert-info", DialogHelper.INFO_GLYPH);
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
        DialogHelper.applyTheme(alert, "alert-warning", DialogHelper.WARNING_GLYPH);
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
        DialogHelper.applyTheme(alert, "alert-error", DialogHelper.ERROR_GLYPH);
        alert.showAndWait();
    }
}