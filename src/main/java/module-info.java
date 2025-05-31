module org.example.ksr_linguistic_summarization {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires net.synedra.validatorfx;
    requires java.sql;
    requires static lombok;
    requires jdk.jshell;
    requires com.fasterxml.jackson.databind;

    opens org.example.ksr_linguistic_summarization to javafx.fxml;
    exports org.example.ksr_linguistic_summarization;
    exports org.example.ksr_linguistic_summarization.view;
    opens org.example.ksr_linguistic_summarization.view to javafx.fxml;
    exports org.example.ksr_linguistic_summarization.logic.functions;
    opens org.example.ksr_linguistic_summarization.logic.functions to javafx.fxml;
    exports org.example.ksr_linguistic_summarization.logic.set;
    opens org.example.ksr_linguistic_summarization.logic.set to javafx.fxml;
    opens org.example.ksr_linguistic_summarization.logic.utils to com.fasterxml.jackson.databind;
    opens org.example.ksr_linguistic_summarization.logic.utils.linguistic_variable to com.fasterxml.jackson.databind;
}