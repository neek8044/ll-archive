package net.legacylauncher.bootstrap.ui.message;

import net.legacylauncher.bootstrap.ui.UserInterface;
import net.legacylauncher.bootstrap.util.OS;

import javax.swing.*;
import java.io.File;
import java.net.URL;

public class Button {
    private final String text;
    private final Runnable action;

    public Button(String text, Runnable action) {
        this.text = text;
        this.action = action;
    }

    JButton toSwingButton() {
        JButton button = new JButton();
        button.setText(text);
        button.addActionListener(e -> Button.this.action.run());
        return button;
    }

    public static Button openLink(String text, final URL url) {
        return new Button(text, () -> {
            if (!OS.openUrl(url)) {
                UserInterface.showError(UserInterface.getLString(
                        "button.url.fail",
                        "Couldn't open the link. Please copy & paste it to the browser yourself."
                ), url.toString());
            }
        });
    }

    public static Button openFile(String text, final File file) {
        return new Button(text, () -> {
            if (!OS.openPath(file)) {
                UserInterface.showError(UserInterface.getLString(
                        "button.file.fail",
                        "Couldn't open the file. Please open it yourself."
                ), file.getAbsolutePath());
            }
        });
    }
}
