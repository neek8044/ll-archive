package ru.turikhay.tlauncher.ui;

import ru.turikhay.tlauncher.TLauncher;
import ru.turikhay.tlauncher.configuration.Configuration;
import ru.turikhay.tlauncher.configuration.LangConfiguration;
import ru.turikhay.tlauncher.configuration.SimpleConfiguration;
import ru.turikhay.tlauncher.ui.alert.Alert;
import ru.turikhay.tlauncher.ui.block.Blocker;
import ru.turikhay.tlauncher.ui.loc.Localizable;
import ru.turikhay.tlauncher.ui.loc.LocalizableMenuItem;
import ru.turikhay.tlauncher.ui.logger.Logger;
import ru.turikhay.tlauncher.ui.notice.NoticeManager;
import ru.turikhay.tlauncher.ui.swing.Dragger;
import ru.turikhay.tlauncher.ui.swing.extended.ExtendedComponentAdapter;
import ru.turikhay.tlauncher.ui.theme.Theme;
import ru.turikhay.util.IntegerArray;
import ru.turikhay.util.SwingUtil;
import ru.turikhay.util.U;
import ru.turikhay.util.async.ExtendedThread;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;

public class TLauncherFrame extends JFrame {
    public static final Dimension minSize = new Dimension(570, 570);
    public static final Dimension maxSize = new Dimension(1920, 1080);
    public static final float minFontSize = 12, maxFontSize = 18;
    private static float fontSize = 12f;
    public static double magnifyDimensions = 1.;
    private final TLauncherFrame instance = this;
    private final TLauncher tlauncher;
    private final Configuration settings;
    private final LangConfiguration lang;
    private final int[] windowSize;
    private final Point maxPoint;
    public final MainPane mp;
    private String brand;
    private SimpleConfiguration proofr, uiConfig;
    private final NoticeManager notices;

    public static float getFontSize() {
        return fontSize;
    }

    public static void setFontSize(float size) {
        fontSize = size;
        magnifyDimensions = Math.sqrt(fontSize / 12f);
    }

    public TLauncherFrame(TLauncher t) {
        tlauncher = t;
        settings = t.getSettings();
        lang = t.getLang();
        windowSize = settings.getLauncherWindowSize();
        maxPoint = new Point();
        notices = new NoticeManager(this, t.getBootConfig());
        SwingUtil.initFontSize((int) getFontSize());
        SwingUtil.setFavicons(this);
        setupUI();
        updateUILocale();
        setWindowSize();
        setWindowTitle();
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                instance.setVisible(false);
                TLauncher.kill();
            }
        });
        setDefaultCloseOperation(3);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowIconified(WindowEvent e) {
                mp.background.pauseBackground();
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
                mp.background.startBackground();
            }

            public void windowActivated(WindowEvent e) {
                mp.background.startBackground();
            }

            public void windowDeactivated(WindowEvent e) {
                mp.background.pauseBackground();
            }
        });
        addComponentListener(new ExtendedComponentAdapter(this) {
            public void onComponentResized(ComponentEvent e) {
                updateMaxPoint();
                Dragger.update();
                boolean lock = getExtendedState() != 0;
                Blocker.setBlocked(mp.defaultScene.settingsForm.launcherResolution, "extended", lock);
                if (!lock) {
                    IntegerArray arr = new IntegerArray(getWidth(), getHeight());
                    mp.defaultScene.settingsForm.launcherResolution.setValue(arr);
                    settings.set("gui.size", arr);
                }
            }

            public void componentShown(ComponentEvent e) {
                instance.validate();
                instance.repaint();
                instance.toFront();
                mp.background.startBackground();
            }

            public void componentHidden(ComponentEvent e) {
                mp.background.pauseBackground();
            }
        });
        addWindowStateListener(new WindowStateListener() {
            public void windowStateChanged(WindowEvent e) {
                int newState = TLauncherFrame.getExtendedStateFor(e.getNewState());
                if (newState != -1) {
                    settings.set("gui.window", newState);
                }
            }
        });
        mp = new MainPane(this);
        add(mp);
        log("Packing main frame...");
        pack();
        log("Resizing main pane...");
        mp.onResize();
        mp.background.loadBackground();
        updateMaxPoint();
        Dragger.ready(settings, maxPoint);
        if (TLauncher.getInstance().isDebug()) {
            new TLauncherFrame.TitleUpdaterThread();
        } else {
            setWindowTitle();
        }

        setVisible(true);
        int windowState = getExtendedStateFor(settings.getInteger("gui.window"));
        if (windowState == 0) {
            setLocationRelativeTo(null);
        } else {
            setExtendedState(windowState);
        }
    }

    public TLauncher getLauncher() {
        return tlauncher;
    }

    public NoticeManager getNotices() {
        return notices;
    }

    public Point getMaxPoint() {
        return maxPoint;
    }

    public Configuration getConfiguration() {
        return settings;
    }

    public void updateLocales() {
        try {
            tlauncher.reloadLocale();
        } catch (Exception var2) {
            log("Cannot reload settings!", var2);
            return;
        }

        Logger.updateLocale();
        LocalizableMenuItem.updateLocales();
        updateUILocale();
        notices.updateLocale();
        Localizable.updateContainer(this);
        setWindowTitle();
    }

    public void updateTitle() {
        StringBuilder brandBuilder = new StringBuilder()
                .append(TLauncher.getVersion().getNormalVersion())
                .append(" [").append(TLauncher.getBrand()).append("]");

        if (TLauncher.getInstance().isDebug()) {
            brandBuilder.append(" [DEBUG]");
        }

        if (TLauncher.isBeta()) {
            brandBuilder.append(" [BETA]");
        }

        brand = brandBuilder.toString();
    }

    public void setWindowTitle() {
        updateTitle();
        String title;
        if (TLauncher.getInstance().isDebug()) {
            title = String.format("TLauncher %s [%s]", brand, U.memoryStatus());
        } else {
            title = String.format("TLauncher %s", brand);
        }

        setTitle(title);
    }

    private void setWindowSize() {
        int width = windowSize[0] > maxSize.width ? maxSize.width : windowSize[0];
        int height = windowSize[1] > maxSize.height ? maxSize.height : windowSize[1];
        Dimension curSize = new Dimension(width, height);
        setMinimumSize(minSize);
        setPreferredSize(curSize);
    }

    private void setupUI() {
        UIManager.put("FileChooser.newFolderErrorSeparator", ": ");
        UIManager.put("FileChooser.readOnly", Boolean.FALSE);
        UIManager.put("TabbedPane.contentOpaque", Boolean.valueOf(false));
        UIManager.put("TabbedPane.contentBorderInsets", new Insets(0, 0, 0, 0));
        UIManager.put("TabbedPane.tabInsets", new Insets(0, 8, 6, 8));

        String themeFile = settings.get("gui.theme");
        String name = null;
        InputStream in;

        try {
            if (themeFile != null && new File(themeFile).isFile()) {
                name = themeFile;
                in = new FileInputStream(themeFile);
            } else {
                name = "modern";
                in = Theme.class.getResourceAsStream("theme.properties");
            }

            Theme.loadTheme(name, in);
        } catch (Exception e) {
            Alert.showError("Could not load theme: " + name, e);
        }
    }

    private void updateUILocale() {
        if (uiConfig == null) {
            try {
                uiConfig = new SimpleConfiguration(getClass().getResource("/lang/_ui.properties"));
            } catch (Exception var4) {
                return;
            }
        }

        Iterator var2 = uiConfig.getKeys().iterator();

        while (var2.hasNext()) {
            String key = (String) var2.next();
            String value = uiConfig.get(key);
            if (value != null) {
                UIManager.put(key, lang.get(value));
            }
        }

    }

    private void updateMaxPoint() {
        maxPoint.x = getWidth();
        maxPoint.y = getHeight();
    }

    public void setSize(int width, int height) {
        if (getWidth() != width || getHeight() != height) {
            if (getExtendedState() == 0) {
                boolean show = isVisible();
                if (show) {
                    setVisible(false);
                }

                super.setSize(width, height);
                if (show) {
                    setVisible(true);
                    setLocationRelativeTo(null);
                }

            }
        }
    }

    public void setSize(Dimension d) {
        setSize(d.width, d.height);
    }

    private static int getExtendedStateFor(int state) {
        switch (state) {
            case 0:
            case 2:
            case 4:
            case 6:
                return state;
            case 1:
            case 3:
            case 5:
            default:
                return -1;
        }
    }

    public static URL getRes(String uri) {
        return TLauncherFrame.class.getResource(uri);
    }

    private static void log(Object... o) {
        U.log("[Frame]", o);
    }

    private class TitleUpdaterThread extends ExtendedThread {
        TitleUpdaterThread() {
            super("TitleUpdater");
            updateTitle();
            start();
        }

        public void run() {
            while (isDisplayable()) {
                U.sleepFor(100L);
                setWindowTitle();
            }

            TLauncherFrame.log("Title updater is shut down.");
            interrupt();
        }
    }
}
