import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Task {
    private final StringProperty name = new SimpleStringProperty();
    private final BooleanProperty done = new SimpleBooleanProperty(false);

    public Task(String name) {
        this.name.set(name);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String newName) {
        name.set(newName);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public boolean isDone() {
        return done.get();
    }

    public void setDone(boolean value) {
        done.set(value);
    }

    public BooleanProperty doneProperty() {
        return done;
    }

    @Override
    public String toString() {
        return name.get();
    }
}
