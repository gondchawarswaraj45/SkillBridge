package com.freelancing.ui.common;

import com.freelancing.model.common.CalendarEvent;
import com.freelancing.model.common.User;

import com.freelancing.config.AppTheme;
import com.freelancing.db.DatabaseManager;
import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * SkillBridge Interactive Calendar View.
 * Displays project milestones, client meetings, deadlines, and reminders in a responsive monthly grid.
 * Adheres strictly to dual-theme design (zero white in light theme) and dynamic animations.
 */
public class CalendarView extends BorderPane {

    private final DatabaseManager db;
    private final User currentUser;
    private YearMonth currentMonth;
    private LocalDate selectedDate;
    private CalendarEvent.EventType currentFilter = null;

    private Label monthYearLabel;
    private GridPane calendarGrid;
    private VBox dayDetailPanel;
    private ComboBox<String> filterCombo;

    public CalendarView(User user) {
        this.db = DatabaseManager.getInstance();
        this.currentUser = user;
        this.currentMonth = YearMonth.now();
        this.selectedDate = LocalDate.now();

        buildUI();
        seedSampleCalendarEventsIfEmpty();
        renderCalendar();
    }

    private void buildUI() {
        setStyle("-fx-background-color: " + AppTheme.getBgDark() + ";");

        // Top Header
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(18, 24, 18, 24));
        header.setStyle("-fx-background-color: " + AppTheme.getBgPanel() + "; -fx-border-color: " + AppTheme.getBorderColor() + "; -fx-border-width: 0 0 1 0;");

        Label title = new Label("📅 Schedule & Milestone Calendar");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        title.setTextFill(Color.web(AppTheme.getTextPrimary()));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Filter ComboBox
        Label filterLbl = new Label("Filter:");
        filterLbl.setTextFill(Color.web(AppTheme.getTextMuted()));
        filterLbl.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 13));

        filterCombo = UIComponents.createStyledComboBox("All Types", "Milestones", "Deadlines", "Meetings", "Payments", "Reminders");
        filterCombo.setOnAction(e -> {
            String val = filterCombo.getValue();
            if ("All Types".equals(val)) currentFilter = null;
            else if ("Milestones".equals(val)) currentFilter = CalendarEvent.EventType.MILESTONE;
            else if ("Deadlines".equals(val)) currentFilter = CalendarEvent.EventType.DEADLINE;
            else if ("Meetings".equals(val)) currentFilter = CalendarEvent.EventType.MEETING;
            else if ("Payments".equals(val)) currentFilter = CalendarEvent.EventType.PAYMENT;
            else if ("Reminders".equals(val)) currentFilter = CalendarEvent.EventType.REMINDER;
            renderCalendar();
        });

        Button addEventBtn = UIComponents.createPrimaryButton("+ Add Reminder");
        addEventBtn.setOnAction(e -> showAddEventDialog());

        header.getChildren().addAll(title, spacer, filterLbl, filterCombo, addEventBtn);
        setTop(header);

        // Center Content: Calendar + Sidebar
        HBox centerBox = new HBox(16);
        centerBox.setPadding(new Insets(20));
        HBox.setHgrow(centerBox, Priority.ALWAYS);

        // Left: Calendar Grid Card
        VBox calContainer = new VBox(14);
        calContainer.setStyle("-fx-background-color: " + AppTheme.getBgCard() + "; -fx-border-color: " + AppTheme.getBorderColor() + "; -fx-border-radius: 12; -fx-background-radius: 12; -fx-padding: 16;");
        HBox.setHgrow(calContainer, Priority.ALWAYS);

        // Month Navigation
        HBox navBox = new HBox(12);
        navBox.setAlignment(Pos.CENTER_LEFT);

        Button prevBtn = UIComponents.createSecondaryButton("◀ Prev");
        prevBtn.setOnAction(e -> {
            currentMonth = currentMonth.minusMonths(1);
            updateMonthLabel();
            renderCalendar();
        });

        Button nextBtn = UIComponents.createSecondaryButton("Next ▶");
        nextBtn.setOnAction(e -> {
            currentMonth = currentMonth.plusMonths(1);
            updateMonthLabel();
            renderCalendar();
        });

        Button todayBtn = UIComponents.createSecondaryButton("Today");
        todayBtn.setOnAction(e -> {
            currentMonth = YearMonth.now();
            selectedDate = LocalDate.now();
            updateMonthLabel();
            renderCalendar();
        });

        monthYearLabel = new Label();
        monthYearLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        monthYearLabel.setTextFill(Color.web(AppTheme.getTextPrimary()));
        updateMonthLabel();

        navBox.getChildren().addAll(prevBtn, nextBtn, todayBtn, monthYearLabel);
        calContainer.getChildren().add(navBox);

        // Days of week header
        GridPane dayOfWeekGrid = new GridPane();
        dayOfWeekGrid.setHgap(8);
        String[] days = {"SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"};
        for (int i = 0; i < 7; i++) {
            Label dayLbl = new Label(days[i]);
            dayLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
            dayLbl.setTextFill(Color.web(AppTheme.getTextMuted()));
            dayLbl.setAlignment(Pos.CENTER);
            dayLbl.setMaxWidth(Double.MAX_VALUE);
            GridPane.setHgrow(dayLbl, Priority.ALWAYS);
            dayOfWeekGrid.add(dayLbl, i, 0);

            ColumnConstraints colConst = new ColumnConstraints();
            colConst.setPercentWidth(100.0 / 7.0);
            dayOfWeekGrid.getColumnConstraints().add(colConst);
        }
        calContainer.getChildren().add(dayOfWeekGrid);

        // Calendar Grid with equal percentage column constraints
        calendarGrid = new GridPane();
        calendarGrid.setHgap(8);
        calendarGrid.setVgap(8);
        for (int i = 0; i < 7; i++) {
            ColumnConstraints colConst = new ColumnConstraints();
            colConst.setPercentWidth(100.0 / 7.0);
            calendarGrid.getColumnConstraints().add(colConst);
        }
        VBox.setVgrow(calendarGrid, Priority.ALWAYS);
        calContainer.getChildren().add(calendarGrid);

        // Right: Day Detail Panel (matching full calendar container height)
        dayDetailPanel = new VBox(12);
        dayDetailPanel.setPrefWidth(320);
        dayDetailPanel.setMinWidth(280);
        dayDetailPanel.setStyle("-fx-background-color: " + AppTheme.getBgCard() + "; -fx-border-color: " + AppTheme.getBorderColor() + "; -fx-border-radius: 12; -fx-background-radius: 12; -fx-padding: 16;");
        VBox.setVgrow(dayDetailPanel, Priority.ALWAYS);

        ScrollPane detailScroll = UIComponents.createScrollPane(dayDetailPanel);
        detailScroll.setFitToHeight(true);
        detailScroll.setFitToWidth(true);

        centerBox.getChildren().addAll(calContainer, detailScroll);
        setCenter(centerBox);

        com.freelancing.util.AnimationUtil.applyFadeZoom(this, 300);
    }

    private void updateMonthLabel() {
        monthYearLabel.setText(currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")));
    }

    private void renderCalendar() {
        calendarGrid.getChildren().clear();
        calendarGrid.getRowConstraints().clear();

        LocalDate firstDay = currentMonth.atDay(1);
        int dayOfWeekOffset = firstDay.getDayOfWeek().getValue() % 7; // Sunday = 0
        int daysInMonth = currentMonth.lengthOfMonth();
        int totalRows = (int) Math.ceil((dayOfWeekOffset + daysInMonth) / 7.0);

        for (int r = 0; r < totalRows; r++) {
            RowConstraints rc = new RowConstraints();
            rc.setPercentHeight(100.0 / totalRows);
            rc.setVgrow(Priority.ALWAYS);
            calendarGrid.getRowConstraints().add(rc);
        }

        com.freelancing.util.AnimationUtil.runAsync(
            () -> {
                List<CalendarEvent> allEvents = new ArrayList<>(db.getCalendarEvents().values());
                if (currentUser != null) {
                    allEvents = allEvents.stream()
                            .filter(ev -> ev.getUserId() == null || ev.getUserId().equals(currentUser.getId()) || "admin".equals(currentUser.getUsername()))
                            .collect(Collectors.toList());
                }
                if (currentFilter != null) {
                    allEvents = allEvents.stream()
                            .filter(ev -> ev.getType() == currentFilter)
                            .collect(Collectors.toList());
                }
                Map<String, List<CalendarEvent>> eventsByDate = new HashMap<>();
                for (CalendarEvent ev : allEvents) {
                    if (ev.getDate() != null) {
                        eventsByDate.computeIfAbsent(ev.getDate(), k -> new ArrayList<>()).add(ev);
                    }
                }
                return eventsByDate;
            },
            eventsByDate -> {
                calendarGrid.getChildren().clear();
                int row = 0;
                int col = dayOfWeekOffset;

                for (int day = 1; day <= daysInMonth; day++) {
                    LocalDate date = currentMonth.atDay(day);
                    String dateStr = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
                    List<CalendarEvent> dayEvents = eventsByDate.getOrDefault(dateStr, Collections.emptyList());

                    VBox cell = createDayCell(date, dayEvents);
                    calendarGrid.add(cell, col, row);
                    GridPane.setHgrow(cell, Priority.ALWAYS);
                    GridPane.setVgrow(cell, Priority.ALWAYS);

                    col++;
                    if (col > 6) {
                        col = 0;
                        row++;
                    }
                }
                com.freelancing.util.AnimationUtil.applyFadeZoom(calendarGrid, 200);
                updateDayDetailPanel();
            }
        );
    }

    private VBox createDayCell(LocalDate date, List<CalendarEvent> dayEvents) {
        boolean dark = AppTheme.isDarkMode();
        boolean isToday = date.equals(LocalDate.now());
        boolean isSelected = date.equals(selectedDate);

        VBox cell = new VBox(4);
        cell.setPadding(new Insets(6));
        cell.setMinHeight(75);
        cell.setAlignment(Pos.TOP_LEFT);

        String bgCol = isSelected ? (dark ? "#2D3748" : "#BACADE")
                : (isToday ? (dark ? "#1F2937" : "#CBD9EB") : AppTheme.getBgInput());
        String borderCol = isSelected ? AppTheme.COLOR_PRIMARY
                : (isToday ? AppTheme.COLOR_ACCENT : AppTheme.getBorderColor());

        cell.setStyle("-fx-background-color: " + bgCol + "; -fx-border-color: " + borderCol + "; "
                + "-fx-border-radius: 8; -fx-background-radius: 8; -fx-cursor: hand;");

        // Day Number
        Label numLbl = new Label(String.valueOf(date.getDayOfMonth()));
        numLbl.setFont(Font.font("Segoe UI", isToday || isSelected ? FontWeight.BOLD : FontWeight.NORMAL, 12));
        numLbl.setTextFill(Color.web(isToday ? AppTheme.COLOR_PRIMARY : AppTheme.getTextPrimary()));
        cell.getChildren().add(numLbl);

        // Event tags (up to 2 shown, then +N more)
        int shown = 0;
        for (CalendarEvent ev : dayEvents) {
            if (shown >= 2) {
                Label moreLbl = new Label("+" + (dayEvents.size() - 2) + " more");
                moreLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 10));
                moreLbl.setTextFill(Color.web(AppTheme.COLOR_PRIMARY));
                cell.getChildren().add(moreLbl);
                break;
            }
            Label evBadge = new Label(ev.getTitle());
            evBadge.setMaxWidth(110);
            evBadge.setEllipsisString("…");
            evBadge.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 10));
            evBadge.setTextFill(Color.WHITE);
            String badgeBg = ev.getColor() != null ? ev.getColor() : "#6366F1";
            evBadge.setStyle("-fx-background-color: " + badgeBg + "; -fx-padding: 1 5; -fx-background-radius: 4;");
            cell.getChildren().add(evBadge);
            shown++;
        }

        // Hover scale + Selection
        cell.setOnMouseEntered(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(120), cell);
            st.setToX(1.03); st.setToY(1.03); st.play();
        });
        cell.setOnMouseExited(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(120), cell);
            st.setToX(1.0); st.setToY(1.0); st.play();
        });
        cell.setOnMouseClicked(e -> {
            selectedDate = date;
            renderCalendar();
        });

        return cell;
    }

    private void updateDayDetailPanel() {
        dayDetailPanel.getChildren().clear();

        Label header = new Label("Schedule for " + selectedDate.format(DateTimeFormatter.ofPattern("EEE, MMM dd, yyyy")));
        header.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        header.setTextFill(Color.web(AppTheme.getTextPrimary()));
        dayDetailPanel.getChildren().add(header);

        String dateStr = selectedDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
        List<CalendarEvent> dayEvents = db.getCalendarEvents().values().stream()
                .filter(ev -> dateStr.equals(ev.getDate()))
                .filter(ev -> currentUser == null || ev.getUserId() == null || ev.getUserId().equals(currentUser.getId()) || "admin".equals(currentUser.getUsername()))
                .collect(Collectors.toList());

        if (dayEvents.isEmpty()) {
            Label noEv = new Label("No events or bookings scheduled for this date.");
            noEv.setWrapText(true);
            noEv.setTextFill(Color.web(AppTheme.getTextMuted()));
            noEv.setFont(Font.font("Segoe UI", 13));
            dayDetailPanel.getChildren().add(noEv);

            Button quickAdd = UIComponents.createPrimaryButton("+ Add Item");
            quickAdd.setOnAction(e -> showAddEventDialog());
            dayDetailPanel.getChildren().add(quickAdd);
            return;
        }

        for (CalendarEvent ev : dayEvents) {
            VBox evCard = new VBox(6);
            evCard.setPadding(new Insets(10));
            evCard.setStyle("-fx-background-color: " + AppTheme.getBgInput() + "; -fx-border-color: "
                    + (ev.getColor() != null ? ev.getColor() : AppTheme.COLOR_PRIMARY)
                    + "; -fx-border-radius: 8; -fx-background-radius: 8; -fx-border-width: 0 0 0 4;");

            HBox row1 = new HBox(8);
            row1.setAlignment(Pos.CENTER_LEFT);

            Label title = new Label(ev.getTitle());
            title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
            title.setTextFill(Color.web(AppTheme.getTextPrimary()));

            Region sp = new Region();
            HBox.setHgrow(sp, Priority.ALWAYS);

            Label typeTag = new Label(ev.getType() != null ? ev.getType().name() : "EVENT");
            typeTag.setFont(Font.font("Segoe UI", FontWeight.BOLD, 10));
            typeTag.setTextFill(Color.WHITE);
            typeTag.setStyle("-fx-background-color: " + (ev.getColor() != null ? ev.getColor() : "#6366F1") + "; -fx-padding: 2 6; -fx-background-radius: 6;");

            row1.getChildren().addAll(title, sp, typeTag);

            Label timeLbl = new Label("⏰ " + (ev.getTime() != null ? ev.getTime() : "All Day"));
            timeLbl.setFont(Font.font("Segoe UI", 12));
            timeLbl.setTextFill(Color.web(AppTheme.getTextMuted()));

            evCard.getChildren().addAll(row1, timeLbl);

            if (ev.getDescription() != null && !ev.getDescription().isEmpty()) {
                Label desc = new Label(ev.getDescription());
                desc.setWrapText(true);
                desc.setFont(Font.font("Segoe UI", 12));
                desc.setTextFill(Color.web(AppTheme.getTextMuted()));
                evCard.getChildren().add(desc);
            }

            dayDetailPanel.getChildren().add(evCard);
        }
    }

    private void showAddEventDialog() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(12);
        grid.setPadding(new Insets(20));

        TextField titleField = UIComponents.createTextField("Title / Name");
        DatePicker datePicker = new DatePicker(selectedDate);
        datePicker.setStyle(AppTheme.getComboboxStyle());

        TextField timeField = UIComponents.createTextField("18:00");
        ComboBox<String> typeCombo = UIComponents.createStyledComboBox("MILESTONE", "DEADLINE", "MEETING", "PAYMENT", "REMINDER");
        TextArea descArea = UIComponents.createTextArea("Notes & details...");
        descArea.setPrefRowCount(3);

        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Date:"), 0, 1);
        grid.add(datePicker, 1, 1);
        grid.add(new Label("Time:"), 0, 2);
        grid.add(timeField, 1, 2);
        grid.add(new Label("Type:"), 0, 3);
        grid.add(typeCombo, 1, 3);
        grid.add(new Label("Details:"), 0, 4);
        grid.add(descArea, 1, 4);

        UIComponents.showModalOverlay("Add Schedule Item", grid, "Save Event", () -> {
            if (!titleField.getText().trim().isEmpty()) {
                CalendarEvent ce = new CalendarEvent();
                ce.setId("cal_" + UUID.randomUUID().toString().substring(0, 8));
                ce.setUserId(currentUser != null ? currentUser.getId() : "guest");
                ce.setTitle(titleField.getText().trim());
                ce.setDate(datePicker.getValue() != null ? datePicker.getValue().toString() : selectedDate.toString());
                ce.setTime(timeField.getText().trim());
                ce.setDescription(descArea.getText().trim());
                ce.setType(CalendarEvent.EventType.valueOf(typeCombo.getValue()));
                ce.setColor(getColorForType(ce.getType()));

                db.getCalendarEvents().put(ce.getId(), ce);
                db.saveData();
                renderCalendar();
            }
        });
    }

    private String getColorForType(CalendarEvent.EventType type) {
        if (type == null) return "#6366F1";
        switch (type) {
            case MILESTONE: return "#10B981";
            case DEADLINE: return "#EF4444";
            case MEETING: return "#3B82F6";
            case PAYMENT: return "#F59E0B";
            case REMINDER: return "#8B5CF6";
            default: return "#6366F1";
        }
    }

    private void seedSampleCalendarEventsIfEmpty() {
        if (db.getCalendarEvents().isEmpty()) {
            LocalDate today = LocalDate.now();
            CalendarEvent e1 = new CalendarEvent("cal_demo1", currentUser != null ? currentUser.getId() : "usr_client1",
                    "Sprint 1 Milestone Delivery", today.plusDays(5).toString(), "18:00", CalendarEvent.EventType.MILESTONE, "#10B981");
            e1.setDescription("Deliverable handover for Sprint 1 backend architecture.");

            CalendarEvent e2 = new CalendarEvent("cal_demo2", currentUser != null ? currentUser.getId() : "usr_client1",
                    "Client Architecture Sync", today.plusDays(2).toString(), "14:00", CalendarEvent.EventType.MEETING, "#3B82F6");
            e2.setDescription("Weekly client check-in to review milestone progress.");

            CalendarEvent e3 = new CalendarEvent("cal_demo3", currentUser != null ? currentUser.getId() : "usr_client1",
                    "Milestone Escrow Deposit", today.plusDays(10).toString(), "11:00", CalendarEvent.EventType.PAYMENT, "#F59E0B");

            db.getCalendarEvents().put(e1.getId(), e1);
            db.getCalendarEvents().put(e2.getId(), e2);
            db.getCalendarEvents().put(e3.getId(), e3);
        }
    }
}
