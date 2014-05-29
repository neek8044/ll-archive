package ru.turikhay.tlauncher.ui.listener;

import java.io.File;
import java.net.URI;
import java.util.Iterator;
import ru.turikhay.tlauncher.TLauncher;
import ru.turikhay.tlauncher.configuration.LangConfiguration;
import ru.turikhay.tlauncher.minecraft.crash.Crash;
import ru.turikhay.tlauncher.minecraft.crash.CrashSignatureContainer;
import ru.turikhay.tlauncher.minecraft.launcher.MinecraftException;
import ru.turikhay.tlauncher.minecraft.launcher.MinecraftListener;
import ru.turikhay.tlauncher.ui.alert.Alert;
import ru.turikhay.tlauncher.ui.loc.Localizable;
import ru.turikhay.util.OS;
import ru.turikhay.util.U;

public class MinecraftUIListener implements MinecraftListener {
   private final TLauncher t;
   private final LangConfiguration lang;

   public MinecraftUIListener(TLauncher tlauncher) {
      this.t = tlauncher;
      this.lang = this.t.getLang();
   }

   public void onMinecraftPrepare() {
   }

   public void onMinecraftAbort() {
   }

   public void onMinecraftLaunch() {
      this.t.hide();
      this.t.getVersionManager().asyncRefresh();
      if (this.t.getUpdater() != null) {
         this.t.getUpdater().asyncFindUpdate();
      }

   }

   public void onMinecraftClose() {
      if (this.t.getLauncher().isLaunchAssist()) {
         this.t.show();
      }
   }

   public void onMinecraftCrash(Crash crash) {
      String p = "crash.";
      String title = Localizable.get(p + "title");
      String report = crash.getFile();
      if (!crash.isRecognized()) {
         Alert.showLocError(title, p + "unknown", (Object)null);
      } else {
         Iterator var6 = crash.getSignatures().iterator();

         while(var6.hasNext()) {
            CrashSignatureContainer.CrashSignature sign = (CrashSignatureContainer.CrashSignature)var6.next();
            String path = sign.getPath();
            String message = p + path;
            String url = message + ".url";
            URI uri = U.makeURI(url);
            if (uri != null) {
               if (Alert.showLocQuestion(title, message, report)) {
                  OS.openLink(uri);
               }
            } else {
               Alert.showLocMessage(title, message, report);
            }
         }
      }

      if (report != null) {
         if (Alert.showLocQuestion(p + "store")) {
            U.log("Removing crash report...");
            File file = new File(report);
            if (!file.exists()) {
               U.log("File is already removed. LOL.");
            } else {
               if (!file.delete()) {
                  U.log("Can't delete crash report file. Okay.");
                  Alert.showLocMessage(p + "store.failed");
                  return;
               }

               U.log("Yay, crash report file doesn't exist by now.");
            }

            Alert.showLocMessage(p + "store.success");
         }

      }
   }

   public void onMinecraftError(Throwable e) {
      Alert.showLocError("launcher.error.title", "launcher.error.unknown", e);
   }

   public void onMinecraftKnownError(MinecraftException e) {
      Alert.showError(this.lang.get("launcher.error.title"), this.lang.get("launcher.error." + e.getLangPath(), e.getLangVars()), e);
   }
}
