package com.freelancing.ui.common;

import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.List;

/**
 * WindowResizeHelper enables smooth, border-drag resizing for undecorated JavaFX stages.
 * Supports N, S, E, W and corner resizing (NW, NE, SW, SE) with min-size constraints.
 * 100% annotation-free.
 */
public class WindowResizeHelper {

    private static final double BORDER_MARGIN = 6.0;

    private final Stage stage;
    private Cursor activeCursor = Cursor.DEFAULT;

    private double startScreenX = 0;
    private double startScreenY = 0;
    private double startStageX = 0;
    private double startStageY = 0;
    private double startStageWidth = 0;
    private double startStageHeight = 0;

    private WindowResizeHelper(Stage stage) {
        this.stage = stage;
        initSceneListeners();
    }

    public static WindowResizeHelper attach(Stage stage) {
        if (stage == null) return null;
        return new WindowResizeHelper(stage);
    }

    private void initSceneListeners() {
        if (stage.getScene() != null) {
            attachToScene(stage.getScene());
        }
        stage.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                attachToScene(newScene);
            }
        });
    }

    private void attachToScene(Scene scene) {
        scene.addEventFilter(MouseEvent.MOUSE_MOVED, this::handleMouseMoved);
        scene.addEventFilter(MouseEvent.MOUSE_PRESSED, this::handleMousePressed);
        scene.addEventFilter(MouseEvent.MOUSE_DRAGGED, this::handleMouseDragged);
        scene.addEventFilter(MouseEvent.MOUSE_RELEASED, this::handleMouseReleased);
    }

    private boolean isMaximized() {
        if (stage == null) return false;
        if (stage.isMaximized()) return true;
        try {
            List<Screen> screens = Screen.getScreensForRectangle(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight());
            Screen screen = screens != null && !screens.isEmpty() ? screens.get(0) : Screen.getPrimary();
            Rectangle2D bounds = screen.getVisualBounds();
            return stage.getX() <= bounds.getMinX() + 2
                    && stage.getY() <= bounds.getMinY() + 2
                    && stage.getWidth() >= bounds.getWidth() - 4
                    && stage.getHeight() >= bounds.getHeight() - 4;
        } catch (Exception ignored) {
            return false;
        }
    }

    private Cursor calculateCursor(MouseEvent e) {
        if (isMaximized()) {
            return Cursor.DEFAULT;
        }

        Scene scene = stage.getScene();
        if (scene == null) return Cursor.DEFAULT;

        double x = e.getSceneX();
        double y = e.getSceneY();
        double w = scene.getWidth();
        double h = scene.getHeight();

        boolean left = x <= BORDER_MARGIN;
        boolean right = x >= w - BORDER_MARGIN;
        boolean top = y <= BORDER_MARGIN;
        boolean bottom = y >= h - BORDER_MARGIN;

        if (top && left) return Cursor.NW_RESIZE;
        if (top && right) return Cursor.NE_RESIZE;
        if (bottom && left) return Cursor.SW_RESIZE;
        if (bottom && right) return Cursor.SE_RESIZE;
        if (top) return Cursor.N_RESIZE;
        if (bottom) return Cursor.S_RESIZE;
        if (left) return Cursor.W_RESIZE;
        if (right) return Cursor.E_RESIZE;

        return Cursor.DEFAULT;
    }

    private void handleMouseMoved(MouseEvent e) {
        if (isMaximized()) {
            if (stage.getScene() != null && stage.getScene().getCursor() != Cursor.DEFAULT) {
                stage.getScene().setCursor(Cursor.DEFAULT);
            }
            return;
        }

        Cursor cursor = calculateCursor(e);
        if (stage.getScene() != null) {
            stage.getScene().setCursor(cursor);
        }
    }

    private void handleMousePressed(MouseEvent e) {
        if (isMaximized()) {
            activeCursor = Cursor.DEFAULT;
            return;
        }

        activeCursor = calculateCursor(e);
        if (activeCursor != Cursor.DEFAULT) {
            startScreenX = e.getScreenX();
            startScreenY = e.getScreenY();
            startStageX = stage.getX();
            startStageY = stage.getY();
            startStageWidth = stage.getWidth();
            startStageHeight = stage.getHeight();
            e.consume();
        }
    }

    private void handleMouseDragged(MouseEvent e) {
        if (activeCursor == Cursor.DEFAULT || isMaximized()) {
            return;
        }

        double dx = e.getScreenX() - startScreenX;
        double dy = e.getScreenY() - startScreenY;

        double minW = stage.getMinWidth() > 0 ? stage.getMinWidth() : 960;
        double minH = stage.getMinHeight() > 0 ? stage.getMinHeight() : 640;

        // Resize horizontally
        if (activeCursor == Cursor.E_RESIZE || activeCursor == Cursor.NE_RESIZE || activeCursor == Cursor.SE_RESIZE) {
            double newW = Math.max(minW, startStageWidth + dx);
            stage.setWidth(newW);
        } else if (activeCursor == Cursor.W_RESIZE || activeCursor == Cursor.NW_RESIZE || activeCursor == Cursor.SW_RESIZE) {
            double targetW = startStageWidth - dx;
            if (targetW >= minW) {
                stage.setX(startStageX + dx);
                stage.setWidth(targetW);
            } else {
                stage.setX(startStageX + (startStageWidth - minW));
                stage.setWidth(minW);
            }
        }

        // Resize vertically
        if (activeCursor == Cursor.S_RESIZE || activeCursor == Cursor.SW_RESIZE || activeCursor == Cursor.SE_RESIZE) {
            double newH = Math.max(minH, startStageHeight + dy);
            stage.setHeight(newH);
        } else if (activeCursor == Cursor.N_RESIZE || activeCursor == Cursor.NW_RESIZE || activeCursor == Cursor.NE_RESIZE) {
            double targetH = startStageHeight - dy;
            if (targetH >= minH) {
                stage.setY(startStageY + dy);
                stage.setHeight(targetH);
            } else {
                stage.setY(startStageY + (startStageHeight - minH));
                stage.setHeight(minH);
            }
        }

        e.consume();
    }

    private void handleMouseReleased(MouseEvent e) {
        if (activeCursor != Cursor.DEFAULT) {
            activeCursor = Cursor.DEFAULT;
            if (stage.getScene() != null) {
                stage.getScene().setCursor(calculateCursor(e));
            }
            e.consume();
        }
    }
}
