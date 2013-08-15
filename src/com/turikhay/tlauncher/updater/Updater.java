package com.turikhay.tlauncher.updater;

import com.turikhay.tlauncher.TLauncher;
import com.turikhay.tlauncher.TLauncherException;
import com.turikhay.tlauncher.downloader.Downloadable;
import com.turikhay.tlauncher.downloader.Downloader;
import com.turikhay.tlauncher.handlers.DownloadableHandler;
import com.turikhay.tlauncher.settings.Settings;
import com.turikhay.tlauncher.util.FileUtil;
import com.turikhay.tlauncher.util.MinecraftUtil;
import com.turikhay.tlauncher.util.U;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import jsmooth.Wrapper;

public class Updater {
   public final String link = "https://dl.dropboxusercontent.com/u/6204017/minecraft/tlauncher/update.ini";
   public final Updater.Package type;
   private final Downloader d;
   private final URL url;
   private List listeners = new ArrayList();
   private Downloadable update_download;
   private Settings update_settings;
   private double found_version;
   private URL found_link;
   private Downloadable launcher_download;
   private File launcher_destination;
   private File replace;

   public Updater(TLauncher t) {
      this.d = t.downloader;
      this.type = Wrapper.isAvailable() ? Updater.Package.EXE : Updater.Package.JAR;
      this.replace = FileUtil.getRunningJar();

      try {
         this.url = new URL("https://dl.dropboxusercontent.com/u/6204017/minecraft/tlauncher/update.ini");
      } catch (MalformedURLException var3) {
         throw new TLauncherException("Cannot create update link!", var3);
      }

      U.log("Updater enabled. Package type: " + this.type);
   }

   public Updater(TLauncher t, Updater.Package type) {
      this.d = t.downloader;
      this.type = type;

      try {
         this.url = new URL("https://dl.dropboxusercontent.com/u/6204017/minecraft/tlauncher/update.ini");
      } catch (MalformedURLException var4) {
         throw new TLauncherException("Cannot create update link!", var4);
      }
   }

   public void addListener(UpdaterListener l) {
      this.listeners.add(l);
   }

   public void removeListener(UpdaterListener l) {
      this.listeners.remove(l);
   }

   public void findUpdate() {
      try {
         this.findUpdate_();
      } catch (Exception var2) {
         this.onUpdateNotifierError(var2);
      }

   }

   private void findUpdate_() throws IOException {
      this.onUpdaterRequests();
      this.update_download = new Downloadable(this.url);
      HttpURLConnection connection = this.update_download.makeConnection();
      int code = connection.getResponseCode();
      switch(code) {
      case 200:
         InputStream is = connection.getInputStream();
         this.update_settings = new Settings(is);
         connection.disconnect();
         this.found_version = this.update_settings.getDouble("last-version");
         if (this.found_version <= 0.0D) {
            throw new IllegalStateException("Settings file is invalid!");
         } else {
            if (0.14D >= this.found_version) {
               this.noUpdateFound();
               return;
            }

            String current_link = this.update_settings.get(this.type.toLowerCase());
            this.found_link = new URL(current_link);
            this.onUpdateFound(this.type == Updater.Package.JAR);
            return;
         }
      default:
         throw new IllegalStateException("Response code (" + code + ") is not supported by Updater!");
      }
   }

   public void downloadUpdate() {
      this.onUpdaterDownloads();
      this.launcher_destination = new File(MinecraftUtil.getWorkingDirectory(), "tlauncher.updated");
      this.launcher_destination.deleteOnExit();
      this.launcher_download = new Downloadable(this.found_link, this.launcher_destination);
      this.launcher_download.setHandler(new DownloadableHandler() {
         public void onStart() {
         }

         public void onCompleteError() {
            Updater.this.onUpdateError(Updater.this.launcher_download.getError());
         }

         public void onComplete() {
            Updater.this.onUpdateDownloaded();
         }
      });
      this.d.add(this.launcher_download);
      this.d.launch();
   }

   public void saveUpdate() {
      try {
         this.saveUpdate_();
      } catch (Exception var2) {
         this.onProcessError(var2);
      }

   }

   private void saveUpdate_() throws IOException {
      FileInputStream in = new FileInputStream(this.launcher_destination);
      FileOutputStream out = new FileOutputStream(this.replace);
      byte[] buffer = new byte[65536];

      for(int curread = in.read(buffer); curread > 0; curread = in.read(buffer)) {
         out.write(buffer, 0, curread);
      }

      in.close();
      out.close();
      System.exit(0);
   }

   public double getFoundVersion() {
      return this.found_version;
   }

   public URL getFoundLink() {
      return this.found_link;
   }

   public URI getFoundLinkAsURI() {
      try {
         return this.found_link.toURI();
      } catch (URISyntaxException var2) {
         var2.printStackTrace();
         return null;
      }
   }

   private void onUpdaterRequests() {
      Iterator var2 = this.listeners.iterator();

      while(var2.hasNext()) {
         UpdaterListener l = (UpdaterListener)var2.next();
         l.onUpdaterRequesting(this);
      }

   }

   private void onUpdateNotifierError(Throwable e) {
      Iterator var3 = this.listeners.iterator();

      while(var3.hasNext()) {
         UpdaterListener l = (UpdaterListener)var3.next();
         l.onUpdaterRequestError(this, e);
      }

   }

   private void noUpdateFound() {
      Iterator var2 = this.listeners.iterator();

      while(var2.hasNext()) {
         UpdaterListener l = (UpdaterListener)var2.next();
         l.onUpdaterNotFoundUpdate(this);
      }

   }

   private void onUpdateFound(boolean canBeInstalledAutomatically) {
      Iterator var3 = this.listeners.iterator();

      while(var3.hasNext()) {
         UpdaterListener l = (UpdaterListener)var3.next();
         l.onUpdaterFoundUpdate(this, canBeInstalledAutomatically);
      }

   }

   private void onUpdaterDownloads() {
      Iterator var2 = this.listeners.iterator();

      while(var2.hasNext()) {
         UpdaterListener l = (UpdaterListener)var2.next();
         l.onUpdaterDownloading(this);
      }

   }

   private void onUpdateDownloaded() {
      Iterator var2 = this.listeners.iterator();

      while(var2.hasNext()) {
         UpdaterListener l = (UpdaterListener)var2.next();
         l.onUpdaterDownloadSuccess(this);
      }

   }

   private void onUpdateError(Throwable e) {
      Iterator var3 = this.listeners.iterator();

      while(var3.hasNext()) {
         UpdaterListener l = (UpdaterListener)var3.next();
         l.onUpdaterDownloadError(this, e);
      }

   }

   private void onProcessError(Throwable e) {
      Iterator var3 = this.listeners.iterator();

      while(var3.hasNext()) {
         UpdaterListener l = (UpdaterListener)var3.next();
         l.onUpdaterProcessError(this, e);
      }

   }

   public static enum Package {
      EXE,
      JAR;

      public String toLowerCase() {
         return this.name().toLowerCase();
      }
   }
}
