package com.example.miniproyecto4.view;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;

/**
 * Utility class that provides a consistent way of displaying
 * informational, warning, and error dialogs throughout the
 * application. All dialogs share the same visual style and
 * behavior to ensure a uniform user experience.
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

    /**
     * Prevents instantiation of this utility class.
     */
    private DialogHelper()
    {
    }

    /**
     * Applies the common visual theme and icon to the specified alert.
     *
     * @param alert the alert to customize.
     * @param typeStyleClass the CSS style class associated with the alert type.
     * @param glyph the Unicode character displayed as the dialog icon.
     */
    private static void applyTheme(Alert alert, String typeStyleClass, String glyph)
    {
        alert.getDialogPane().getStylesheets().add(
                DialogHelper.class.getResource(STYLESHEET_PATH).toExternalForm());
        alert.getDialogPane().getStyleClass().addAll("themed-alert", typeStyleClass);

        String iconStyleClass = "alert-icon-" + typeStyleClass.substring("alert-".length());
        alert.setGraphic(DialogHelper.buildIcon(glyph, iconStyleClass));

        javafx.scene.Node contentLabel = alert.getDialogPane().lookup(".content.label");
        if (contentLabel != null)
        {
            contentLabel.setStyle("-fx-text-fill: white;");
        }
    }

    /**
     * Creates the graphic icon displayed in a dialog.
     *
     * @param glyph the Unicode character used as the icon.
     * @param iconStyleClass the CSS style class applied to the icon.
     * @return a styled label representing the dialog icon.
     */
    private static Label buildIcon(String glyph, String iconStyleClass)
    {
        Label icon = new Label(glyph);
        icon.getStyleClass().addAll("alert-icon", iconStyleClass);
        return icon;
    }

    /**
     * Displays an informational dialog.
     *
     * @param title the title of the dialog.
     * @param message the message displayed to the user.
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
     * Displays a warning dialog.
     *
     * @param title the title of the dialog.
     * @param message the warning message displayed to the user.
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
     * Displays an error dialog including the message of the
     * underlying exception when available.
     *
     * @param title the title of the dialog.
     * @param message the error message displayed to the user.
     * @param cause the exception associated with the error, or {@code null}.
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