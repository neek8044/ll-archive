package net.legacylauncher.ui.login.buttons;

import net.legacylauncher.stats.Stats;
import net.legacylauncher.ui.LegacyLauncherFrame;
import net.legacylauncher.ui.block.Blockable;
import net.legacylauncher.ui.block.Blocker;
import net.legacylauncher.ui.images.DelayedIcon;
import net.legacylauncher.ui.images.Images;
import net.legacylauncher.ui.loc.LocalizableButton;
import net.legacylauncher.ui.loc.LocalizableMenuItem;
import net.legacylauncher.ui.login.LoginForm;
import net.legacylauncher.ui.notice.Notice;
import net.legacylauncher.ui.notice.NoticeManager;
import net.legacylauncher.ui.notice.NoticeManagerListener;
import net.legacylauncher.ui.notice.NoticePopup;
import net.legacylauncher.ui.swing.extended.BorderPanel;
import net.legacylauncher.util.SwingUtil;
import net.minecraft.launcher.updater.VersionSyncInfo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class PlayButton extends BorderPanel implements Blockable, LoginForm.LoginStateListener, NoticeManagerListener {
    private static final long serialVersionUID = 6944074583143406549L;
    private PlayButton.PlayButtonState state;
    private final LoginForm loginForm;

    private final LocalizableButton button, promotedNoticeButton;
    private final NoticePopup promotedNoticePopup = new NoticePopup();
    private final LocalizableMenuItem
            hideNotice = new LocalizableMenuItem("notice.action.hide"),
            hidePromoted = new LocalizableMenuItem("notice.promoted.hide.here");

    {
        Images.getIcon16("eye-slash").setup(hideNotice);
        hideNotice.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (promotedNoticePopup.getNotice() != null) {
                    NoticeManager manager = loginForm.scene.getMainPane().getRootFrame().getNotices();
                    manager.setHidden(promotedNoticePopup.getNotice(), true);
                    manager.selectRandom();
                }
            }
        });
        promotedNoticePopup.registerItem(hideNotice);

        Images.getIcon16("eye-slash").setup(hidePromoted);
        hidePromoted.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                NoticeManager manager = loginForm.scene.getMainPane().getRootFrame().getNotices();
                manager.setPromotedAllowed(false);
            }
        });
        promotedNoticePopup.registerItem(hidePromoted);
    }

    private int mouseX, mouseY;
    private final JPopupMenu wrongButtonMenu = new JPopupMenu();

    {
        LocalizableMenuItem wrongButtonItem = new LocalizableMenuItem("loginform.wrongbutton");
        wrongButtonItem.setEnabled(false);
        wrongButtonItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                wrongButtonMenu.setVisible(false);
            }
        });
        wrongButtonMenu.add(wrongButtonItem);
    }

    PlayButton(LoginForm lf) {
        loginForm = lf;
        button = new LocalizableButton();
        button.addActionListener(e -> {
            switch (state) {
                case CANCEL:
                    loginForm.stopLauncher();
                    break;
                default:
                    loginForm.startLauncher();
            }

        });
        button.setFont(getFont().deriveFont(Font.BOLD).deriveFont(LegacyLauncherFrame.getFontSize() * 1.5f));
        setCenter(button);

        promotedNoticeButton = new LocalizableButton();
        promotedNoticeButton.setPreferredSize(SwingUtil.magnify(new Dimension(48, 1)));
        promotedNoticeButton.addActionListener(e -> {
            promotedNoticePopup.updateList();
            promotedNoticePopup.show(promotedNoticeButton, promotedNoticeButton.getWidth(), 0);
        });
        onNoticePromoted(null);

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() != MouseEvent.BUTTON1)
                    wrongButtonMenu.show(PlayButton.this, mouseX, mouseY);
            }
        });
        setState(PlayButton.PlayButtonState.PLAY);

        lf.scene.getMainPane().getRootFrame().getNotices().addListener(this, true);
    }

    public PlayButton.PlayButtonState getState() {
        return state;
    }

    public void setState(PlayButton.PlayButtonState state) {
        if (state == null) {
            throw new NullPointerException();
        } else {
            this.state = state;
            button.setText(state.getPath());
            if (state == PlayButton.PlayButtonState.CANCEL) {
                setEnabled(true);
            }

        }
    }

    public void updateState() {
        VersionSyncInfo vs = loginForm.versions.getVersion();
        if (vs != null) {
            boolean installed = vs.isInstalled();
            boolean force = loginForm.checkbox.forceupdate.getState();
            if (!installed) {
                setState(PlayButton.PlayButtonState.INSTALL);
            } else {
                setState(force ? PlayButton.PlayButtonState.REINSTALL : PlayButton.PlayButtonState.PLAY);
            }

        }
    }

    public void loginStateChanged(LoginForm.LoginState state) {
        if (state == LoginForm.LoginState.LAUNCHING) {
            setState(PlayButton.PlayButtonState.CANCEL);
        } else {
            updateState();
            setEnabled(!Blocker.isBlocked(this));
        }

    }

    public void block(Object reason) {
        if (state != PlayButton.PlayButtonState.CANCEL) {
            setEnabled(false);
        }

    }

    public void unblock(Object reason) {
        setEnabled(true);
    }

    @Override
    public void onNoticeSelected(Notice notice) {

    }

    @Override
    public void onNoticePromoted(Notice promotedNotice) {
        if (promotedNotice == null) {
            setEast(null);
        } else {
            setEast(promotedNoticeButton);
            promotedNoticePopup.setNotice(promotedNotice);
            promotedNoticeButton.setIcon(new DelayedIcon(promotedNotice.getImage(), 32, 32));

            Stats.noticeViewed(promotedNotice);
        }

        validate();
        //invalidate();
        repaint();
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        button.setText(enabled ? state.getPath() : PlayButtonState.BLOCKED.getPath());
    }

    public enum PlayButtonState {
        REINSTALL("loginform.enter.reinstall"),
        INSTALL("loginform.enter.install"),
        PLAY("loginform.enter"),
        CANCEL("loginform.enter.cancel"),
        BLOCKED("loginform.enter.blocked");

        private final String path;

        PlayButtonState(String path) {
            this.path = path;
        }

        public String getPath() {
            return path;
        }
    }
}
