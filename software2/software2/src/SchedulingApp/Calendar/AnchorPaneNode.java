package SchedulingApp.Calendar;

import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import java.time.LocalDate;

public class AnchorPaneNode extends AnchorPane {

    //date associated with pane
    private LocalDate date;

    //creates anchor pane node
    public AnchorPaneNode (Node... children) {
        super(children);
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalDate getDate() {
        return date;
    }
}
