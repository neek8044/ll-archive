package net.legacylauncher.ui.notice;

import net.legacylauncher.LegacyLauncher;
import net.legacylauncher.stats.Stats;
import net.legacylauncher.ui.images.Images;
import net.legacylauncher.ui.loc.LocalizableMenuItem;
import net.legacylauncher.ui.scenes.DefaultScene;
import net.legacylauncher.ui.swing.ResizeableComponent;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;

public class MainNoticePanel extends NoticePanel implements ResizeableComponent {
    private static final int SAFE_ZONE = 10;

    private final DefaultScene defaultScene;

    private final LocalizableMenuItem hideNotice = new LocalizableMenuItem("notice.action.hide");

    {
        Images.getIcon16("eye-slash").setup(hideNotice);
        hideNotice.addActionListener(e -> {
            if (getNotice() != null) {
                manager.setHidden(getNotice(), true);
                manager.selectRandom();
            }
        });
    }

    private final LocalizableMenuItem hideAllNotices = new LocalizableMenuItem("notice.action.hide-all");

    {
        Images.getIcon16("remove").setup(hideAllNotices);
        hideAllNotices.addActionListener(e -> {
            manager.setAllHidden();
            manager.selectNotice(null, true);
        });
    }

    private final LocalizableMenuItem openNoticeScene = new LocalizableMenuItem("notice.action.scene");

    {
        Images.getIcon16("expand").setup(openNoticeScene);
        openNoticeScene.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                defaultScene.setNoticeSidePanelEnabled(true);
                defaultScene.getMainPane().defaultScene.setSidePanel(DefaultScene.SidePanel.NOTICES);
            }
        });
    }

    public MainNoticePanel(DefaultScene scene) {
        super(scene.getMainPane().getRootFrame().getNotices());
        this.defaultScene = scene;

        registerExtraItems(openNoticeScene, null, hideNotice);
        scene.getMainPane().getRootFrame().getNotices().addListener(this, true);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                noticeSeen();
            }
        });
    }

    private void noticeSeen() {
        if (isVisible() && getNotice() != null && (LegacyLauncher.getInstance() == null || LegacyLauncher.getInstance().getMinecraftLauncher() == null || !LegacyLauncher.getInstance().getMinecraftLauncher().isWorking())) {
            Stats.noticeViewed(getNotice());
        }
    }

    protected void updateNotice() {
        super.updateNotice();

        if (LegacyLauncher.getInstance() != null && LegacyLauncher.getInstance().isReady()) {
            onResize();
        }

        noticeSeen();

        List<Notice> l = manager.getForCurrentLocale();
        openNoticeScene.setEnabled(l != null && l.size() > 3);
    }

    @Override
    public void onResize() {
        if (getNotice() == null) {
            return;
        }

        Point loginFormLocation = defaultScene.loginForm.getLocation();
        Dimension loginFormSize = defaultScene.loginForm.getSize();

        final int
                fullWidth = getWidth(),
                height = getHeight();

        int x = loginFormLocation.x + loginFormSize.width / 2 - fullWidth / 2;

        if (x + fullWidth > defaultScene.getWidth() - SAFE_ZONE) {
            x = defaultScene.getWidth() - fullWidth - SAFE_ZONE;
        } else if (x < SAFE_ZONE) {
            x = SAFE_ZONE;
        }

        int y;
        switch (defaultScene.getLoginFormDirection()) {
            case TOP_LEFT:
            case TOP:
            case TOP_RIGHT:
            case CENTER_LEFT:
            case CENTER:
            case CENTER_RIGHT:
                y = loginFormLocation.y + loginFormSize.height + SAFE_ZONE;
                break;
            case BOTTOM_LEFT:
            case BOTTOM:
            case BOTTOM_RIGHT:
                y = loginFormLocation.y - height - 10;
                break;
            default:
                throw new IllegalArgumentException();
        }

        if (y + height > defaultScene.getHeight() - SAFE_ZONE) {
            y = defaultScene.getHeight() - height - SAFE_ZONE;
        } else if (y < SAFE_ZONE) {
            y = SAFE_ZONE;
        }

        setLocation(x, y);
    }
}
