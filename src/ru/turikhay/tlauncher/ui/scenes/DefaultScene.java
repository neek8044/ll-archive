package ru.turikhay.tlauncher.ui.scenes;

import java.awt.Component;
import ru.turikhay.tlauncher.ui.MainPane;
import ru.turikhay.tlauncher.ui.animate.Animator;
import ru.turikhay.tlauncher.ui.block.Blocker;
import ru.turikhay.tlauncher.ui.info.InfoPanel;
import ru.turikhay.tlauncher.ui.login.LoginForm;
import ru.turikhay.tlauncher.ui.settings.SettingsPanel;
import ru.turikhay.tlauncher.ui.swing.ResizeableComponent;

public class DefaultScene extends PseudoScene {
   private static final long serialVersionUID = -1460877989848190921L;
   private final int LOGINFORM_WIDTH = 250;
   private final int LOGINFORM_HEIGHT = 240;
   private final int SETTINGSFORM_WIDTH = 500;
   private final int SETTINGSFORM_HEIGHT = 485;
   private final int MARGIN = 25;
   public final LoginForm loginForm;
   public final SettingsPanel settingsForm = new SettingsPanel(this);
   private final InfoPanel infoPanel;
   private boolean settings;

   public DefaultScene(MainPane main) {
      super(main);
      this.settingsForm.setSize(this.SETTINGSFORM_WIDTH, this.SETTINGSFORM_HEIGHT);
      this.add(this.settingsForm);
      this.loginForm = new LoginForm(this);
      this.loginForm.setSize(this.LOGINFORM_WIDTH, this.LOGINFORM_HEIGHT);
      this.add(this.loginForm);
      this.infoPanel = new InfoPanel(this);
      this.infoPanel.setSize(200, 35);
      this.add(this.infoPanel);
      this.setSettings(false, false);
   }

   public void onResize() {
      if (this.parent != null) {
         this.setBounds(0, 0, this.parent.getWidth(), this.parent.getHeight());
         this.setSettings(this.settings, false);
      }
   }

   void setSettings(boolean shown, boolean update) {
      if (this.settings != shown || !update) {
         if (shown) {
            this.settingsForm.unblock("");
         } else {
            this.settingsForm.block("");
         }

         int w = this.getWidth();
         int h = this.getHeight();
         int hw = w / 2;
         int hh = h / 2;
         int lf_x;
         int lf_y;
         int sf_x;
         int sf_y;
         int hbw;
         if (shown) {
            int bw = this.LOGINFORM_WIDTH + this.SETTINGSFORM_WIDTH + this.MARGIN;
            hbw = bw / 2;
            lf_x = hw - hbw;
            lf_y = hh - this.LOGINFORM_HEIGHT / 2;
            sf_x = hw - hbw + this.SETTINGSFORM_WIDTH / 2 + this.MARGIN;
            sf_y = hh - this.SETTINGSFORM_HEIGHT / 2;
         } else {
            lf_x = hw - this.LOGINFORM_WIDTH / 2;
            lf_y = hh - this.LOGINFORM_HEIGHT / 2;
            sf_x = w * 2;
            sf_y = hh - this.SETTINGSFORM_HEIGHT / 2;
         }

         Animator.move(this.loginForm, lf_x, lf_y);
         Animator.move(this.settingsForm, sf_x, sf_y);
         this.infoPanel.setShown(!shown, false);
         Component[] var14;
         int var13 = (var14 = this.getComponents()).length;

         for(hbw = 0; hbw < var13; ++hbw) {
            Component comp = var14[hbw];
            if (comp instanceof ResizeableComponent) {
               ((ResizeableComponent)comp).onResize();
            }
         }

         this.settings = shown;
      }
   }

   public void setSettings(boolean shown) {
      this.setSettings(shown, true);
   }

   public void toggleSettings() {
      this.setSettings(!this.settings);
   }

   public boolean isSettingsShown() {
      return this.settings;
   }

   public void block(Object reason) {
      Blocker.block(reason, this.loginForm, this.settingsForm);
   }

   public void unblock(Object reason) {
      Blocker.unblock(reason, this.loginForm, this.settingsForm);
   }
}
