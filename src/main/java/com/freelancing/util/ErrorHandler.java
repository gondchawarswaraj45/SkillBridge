package com.freelancing.util;

import javafx.application.Platform;
import javafx.scene.control.Alert;

/**
 * Global error handler for the SkillBridge platform.
 * Provides user-friendly error dialogs and exception logging.
 */
public class ErrorHandler {

    private ErrorHandler() {}

    /** Set up global uncaught exception handler */
    public static void initialize() {
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            LoggingUtil.error("UNCAUGHT", "Unhandled exception in thread " + thread.getName(), throwable);
            Platform.runLater(() -> showErrorDialog(
                "Unexpected Error",
                "An unexpected error occurred. The application will try to continue.",
                throwable.getMessage()
            ));
        });
    }

    /** Show user-friendly error dialog (in-scene modal overlay) */
    public static void showErrorDialog(String title, String header, String details) {
        try {
            com.freelancing.ui.common.UIComponents.showAlert(
                Alert.AlertType.ERROR,
                title != null ? title : "Error",
                header,
                details != null ? details : "No additional details available."
            );
        } catch (Exception e) {
            System.err.println("Failed to show error dialog: " + e.getMessage());
        }
    }

    /** Show warning dialog (in-scene modal overlay) */
    public static void showWarningDialog(String title, String header, String details) {
        try {
            com.freelancing.ui.common.UIComponents.showAlert(
                Alert.AlertType.WARNING,
                title != null ? title : "Warning",
                header,
                details
            );
        } catch (Exception e) {
            System.err.println("Failed to show warning dialog: " + e.getMessage());
        }
    }

    /** Handle an exception with logging and optional UI notification */
    public static void handle(String context, Exception e, boolean showDialog) {
        LoggingUtil.error(context, e.getMessage(), e);
        if (showDialog) {
            showErrorDialog("Error: " + context, e.getMessage(),
                "Please try again. If the problem persists, contact support.");
        }
    }

    /** Handle an exception with logging only (no UI) */
    public static void handleSilently(String context, Exception e) {
        LoggingUtil.error(context, e.getMessage(), e);
    }

    /** Handle a validation error (in-scene modal overlay) */
    public static void showValidationError(String fieldName, String message) {
        try {
            com.freelancing.ui.common.UIComponents.showAlert(
                Alert.AlertType.WARNING,
                "Validation Error",
                "Invalid " + fieldName,
                message
            );
        } catch (Exception e) {
            System.err.println("Failed to show validation error: " + e.getMessage());
        }
    }

    /** Handle a network/connectivity error */
    public static void showNetworkError(String serviceName) {
        showErrorDialog("Connection Error",
            "Unable to connect to " + serviceName,
            "Please check your internet connection and try again.");
    }
}
