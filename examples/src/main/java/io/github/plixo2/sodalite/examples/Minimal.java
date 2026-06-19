import io.github.plixo2.sodalite.category.init.Init;
import io.github.plixo2.sodalite.category.messagebox.MessageBox;

/// A minimal example that shows a message box.
void main() {
    try {
        // Provide metadata about your app
        // "This is not required, but strongly encouraged"
        Init.setAppMetaData("Minimal", "0.0.1", "com.example.sodalite");

        MessageBox.showSimpleInfo(
                "Hello, Sodalite!",
                "This is a simple message box."
        );
    } finally {
        Init.quit();
    }
}