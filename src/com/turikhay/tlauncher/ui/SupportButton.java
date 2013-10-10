package com.turikhay.tlauncher.ui;

import com.turikhay.tlauncher.TLauncher;
import com.turikhay.tlauncher.settings.Settings;
import com.turikhay.tlauncher.util.AsyncThread;
import com.turikhay.tlauncher.util.U;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.net.URL;
import net.minecraft.launcher_.OperatingSystem;

public class SupportButton extends ImageButton implements LocalizableComponent {
   private static final long serialVersionUID = 7903730373496194592L;
   private final SupportButton instance = this;
   private final LoginForm lf;
   private final Settings l;
   private String path;
   private URL url;
   private URI uri;
   private final Image vk = loadImage("vk.png");
   private final Image mail = loadImage("mail.png");

   SupportButton(LoginForm loginform) {
      this.lf = loginform;
      this.l = this.lf.l;
      this.image = this.selectImage();
      this.rotation = ImageButton.ImageRotation.CENTER;
      this.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            SupportButton.this.instance.openURL();
            SupportButton.this.lf.defocus();
         }
      });
      this.updateURL();
   }

   public void openURL() {
      AsyncThread.execute(new Runnable() {
         public void run() {
            if (!OperatingSystem.openLink(SupportButton.this.uri)) {
               Alert.showError(SupportButton.this.l.get("support.error.title"), SupportButton.this.l.get("support.error"), (Object)SupportButton.this.path);
            }

         }
      });
   }

   private Image selectImage() {
      String locale = TLauncher.getInstance().getSettings().getLocale().toString();
      switch(locale.hashCode()) {
      case 108860863:
         if (locale.equals("ru_RU")) {
            return this.vk;
         }
         break;
      case 111333589:
         if (locale.equals("uk_UA")) {
            return this.vk;
         }
      }

      return this.mail;
   }

   private void updateURL() {
      this.path = this.l.get("support.url");
      this.url = U.makeURL(this.path);
      this.uri = U.makeURI(this.url);
   }

   public void updateLocale() {
      this.image = this.selectImage();
      this.updateURL();
   }
}
