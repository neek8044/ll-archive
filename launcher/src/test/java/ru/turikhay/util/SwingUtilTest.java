package ru.turikhay.util;

import org.testng.annotations.Test;

import javax.swing.*;
import java.awt.*;

import static org.testng.Assert.*;

/**
 * Created by turikhay on 10.07.2017.
 */
public class SwingUtilTest {
    @Test
    public void testFitString() throws Exception {
        JFrame frame = new JFrame();
        Label l = new Label();
        frame.add(l);
        frame.pack();
        frame.setVisible(true);
        l.setText("biiiiiiiiiiiiiiiiiiiiiiiiig dick");
        U.log(SwingUtil.fitString(l.getFontMetrics(l.getFont()), l.getText().toCharArray(), 50));
    }
}