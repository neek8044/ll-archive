package ru.turikhay.tlauncher.ui.scenes;

import ru.turikhay.tlauncher.configuration.Configuration;
import ru.turikhay.tlauncher.ui.MainPane;
import ru.turikhay.tlauncher.ui.SideNotifier;
import ru.turikhay.tlauncher.ui.block.Blocker;
import ru.turikhay.tlauncher.ui.login.LoginForm;
import ru.turikhay.tlauncher.ui.notice.MainNoticePanel;
import ru.turikhay.tlauncher.ui.notice.NoticePanel;
import ru.turikhay.tlauncher.ui.settings.SettingsPanel;
import ru.turikhay.tlauncher.ui.swing.extended.ExtendedPanel;
import ru.turikhay.util.Direction;
import ru.turikhay.util.SwingUtil;
import ru.turikhay.util.U;
import ru.turikhay.util.async.ExtendedThread;
import ru.turikhay.util.async.LoopedThread;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.atomic.AtomicReference;

public class DefaultScene extends PseudoScene {
    public static final Dimension LOGIN_SIZE = new Dimension(250, 240);
    public static final Dimension SETTINGS_SIZE = new Dimension(500, 520);
    public static final int EDGE_INSETS = 10;
    public static final int INSETS = 15;
    public final SideNotifier notifier;
    public final LoginForm loginForm;
    public final SettingsPanel settingsForm;
    private DefaultScene.SidePanel sidePanel;
    private ExtendedPanel sidePanelComp;
    private Direction lfDirection;
    //public final NoticePanel infoPanel;
    public final MainNoticePanel noticePanel;

    // $FF: synthetic field
    private static int[] $SWITCH_TABLE$ru$turikhay$util$Direction;
    // $FF: synthetic field
    private static int[] $SWITCH_TABLE$ru$turikhay$tlauncher$ui$scenes$DefaultScene$SidePanel;

    public DefaultScene(MainPane main) {
        super(main);
        notifier = main.notifier;
        settingsForm = new SettingsPanel(this);
        settingsForm.setSize(SwingUtil.magnify(SETTINGS_SIZE));
        settingsForm.setVisible(false);
        add(settingsForm);
        loginForm = new LoginForm(this);
        loginForm.setSize(SwingUtil.magnify(LOGIN_SIZE));
        add(loginForm);
        noticePanel = new MainNoticePanel(this);
        add(noticePanel);
        //infoPanel = new NoticePanel(this);
        //add(infoPanel);
        updateDirection();
    }

    public void setShown(boolean shown, boolean animate) {
        super.setShown(shown, animate);
        if(shown) {
            if(getMainPane().getRootFrame().getNotices().isHidden(noticePanel.getNotice())) {
                getMainPane().getRootFrame().getNotices().selectRandom();
            }
            noticePanel.redraw();
        }
    }

    public void onResize() {
        if (parent != null) {
            setBounds(0, 0, parent.getWidth(), parent.getHeight());
            updateCoords();
        }
    }

    private void updateCoords() {
        int w = getWidth();
        int h = getHeight();
        int hw = w / 2;
        int hh = h / 2;
        int lf_w = loginForm.getWidth();
        int lf_h = loginForm.getHeight();
        int lf_x;
        int lf_y;
        int n_x;
        if (sidePanel == null) {
            switch ($SWITCH_TABLE$ru$turikhay$util$Direction()[lfDirection.ordinal()]) {
                case 1:
                case 4:
                case 7:
                    lf_x = 10;
                    break;
                case 2:
                case 5:
                case 8:
                    lf_x = hw - lf_w / 2;
                    break;
                case 3:
                case 6:
                case 9:
                    lf_x = w - lf_w - 10;
                    break;
                default:
                    throw new RuntimeException("unknown direction:" + lfDirection);
            }

            switch ($SWITCH_TABLE$ru$turikhay$util$Direction()[lfDirection.ordinal()]) {
                case 1:
                case 2:
                case 3:
                    lf_y = 10;
                    break;
                case 4:
                case 5:
                case 6:
                    lf_y = hh - lf_h / 2;
                    break;
                case 7:
                case 8:
                case 9:
                    lf_y = h - 10 - lf_h;
                    break;
                default:
                    throw new RuntimeException("unknown direction:" + lfDirection);
            }
        } else {
            n_x = sidePanelComp.getWidth();
            int n_y = sidePanelComp.getHeight();
            int bw = lf_w + n_x + 15;
            int hbw = bw / 2;
            int sp_x;
            int sp_y;
            if (w > bw) {
                switch ($SWITCH_TABLE$ru$turikhay$util$Direction()[lfDirection.ordinal()]) {
                    case 1:
                    case 4:
                    case 7:
                        lf_x = 10;
                        sp_x = lf_x + lf_w + 15;
                        break;
                    case 2:
                    case 5:
                    case 8:
                        lf_x = hw - hbw;
                        sp_x = lf_x + 15 + n_x / 2;
                        break;
                    case 3:
                    case 6:
                    case 9:
                        lf_x = w - 10 - lf_w;
                        sp_x = lf_x - 15 - n_x;
                        break;
                    default:
                        throw new RuntimeException("unknown direction:" + lfDirection);
                }

                switch ($SWITCH_TABLE$ru$turikhay$util$Direction()[lfDirection.ordinal()]) {
                    case 1:
                    case 2:
                    case 3:
                        sp_y = 10;
                        lf_y = 10;
                        break;
                    case 4:
                    case 5:
                    case 6:
                        lf_y = hh - lf_h / 2;
                        sp_y = hh - n_y / 2;
                        break;
                    case 7:
                    case 8:
                    case 9:
                        lf_y = h - 10 - lf_h;
                        sp_y = h - 10 - n_y;
                        break;
                    default:
                        throw new RuntimeException("unknown direction:" + lfDirection);
                }
            } else {
                lf_x = w * 2;
                lf_y = 0;
                sp_x = hw - n_x / 2;
                sp_y = hh - n_y / 2;
            }

            sidePanelComp.setLocation(sp_x, sp_y);
        }

        byte n_y1 = 10;
        switch ($SWITCH_TABLE$ru$turikhay$util$Direction()[lfDirection.ordinal()]) {
            case 1:
            case 4:
            case 7:
                n_x = getMainPane().getWidth() - 10 - notifier.getWidth();
                break;
            case 2:
            case 3:
            case 5:
            case 6:
            default:
                n_x = 10;
        }

        notifier.setLocation(n_x, n_y1);
        loginForm.setLocation(lf_x, lf_y);
        noticePanel.onResize();
    }

    public DefaultScene.SidePanel getSidePanel() {
        return sidePanel;
    }

    public void setSidePanel(DefaultScene.SidePanel side) {
        if (sidePanel != side) {
            boolean noSidePanel = side == null;
            if (sidePanelComp != null) {
                sidePanelComp.setVisible(false);
            }

            sidePanel = side;
            sidePanelComp = noSidePanel ? null : getSidePanelComp(side);
            if (!noSidePanel) {
                sidePanelComp.setVisible(true);
            }

            noticePanel.setVisible(noSidePanel);
            //noticePanel.setVisible(noSidePanel);
            updateCoords();
        }
    }

    public void toggleSidePanel(DefaultScene.SidePanel side) {
        if (sidePanel == side) {
            side = null;
        }

        setSidePanel(side);
    }

    public ExtendedPanel getSidePanelComp(DefaultScene.SidePanel side) {
        if (side == null) {
            throw new NullPointerException("side");
        } else {
            switch (side) {
                case SETTINGS:
                    return settingsForm;
                default:
                    throw new RuntimeException("unknown side:" + side);
            }
        }
    }

    public Direction getLoginFormDirection() {
        return lfDirection;
    }

    public void updateDirection() {
        loadDirection();
        updateCoords();
    }

    private void loadDirection() {
        Configuration config = getMainPane().getRootFrame().getConfiguration();
        Direction loginFormDirection = config.getDirection("gui.direction.loginform");
        if (loginFormDirection == null) {
            loginFormDirection = Direction.CENTER;
        }

        lfDirection = loginFormDirection;
    }

    // $FF: synthetic method
    static int[] $SWITCH_TABLE$ru$turikhay$util$Direction() {
        int[] var10000 = $SWITCH_TABLE$ru$turikhay$util$Direction;
        if ($SWITCH_TABLE$ru$turikhay$util$Direction != null) {
            return var10000;
        } else {
            int[] var0 = new int[Direction.values().length];

            try {
                var0[Direction.BOTTOM.ordinal()] = 8;
            } catch (NoSuchFieldError var9) {
            }

            try {
                var0[Direction.BOTTOM_LEFT.ordinal()] = 7;
            } catch (NoSuchFieldError var8) {
            }

            try {
                var0[Direction.BOTTOM_RIGHT.ordinal()] = 9;
            } catch (NoSuchFieldError var7) {
            }

            try {
                var0[Direction.CENTER.ordinal()] = 5;
            } catch (NoSuchFieldError var6) {
            }

            try {
                var0[Direction.CENTER_LEFT.ordinal()] = 4;
            } catch (NoSuchFieldError var5) {
            }

            try {
                var0[Direction.CENTER_RIGHT.ordinal()] = 6;
            } catch (NoSuchFieldError var4) {
            }

            try {
                var0[Direction.TOP.ordinal()] = 2;
            } catch (NoSuchFieldError var3) {
            }

            try {
                var0[Direction.TOP_LEFT.ordinal()] = 1;
            } catch (NoSuchFieldError var2) {
            }

            try {
                var0[Direction.TOP_RIGHT.ordinal()] = 3;
            } catch (NoSuchFieldError var1) {
            }

            $SWITCH_TABLE$ru$turikhay$util$Direction = var0;
            return var0;
        }
    }

    public enum SidePanel {
        SETTINGS;

        public final boolean requiresShow;

        SidePanel(boolean requiresShow) {
            this.requiresShow = requiresShow;
        }

        SidePanel() {
            this(false);
        }
    }
}
