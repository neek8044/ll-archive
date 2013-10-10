package com.turikhay.tlauncher.ui;

public class ArgsField extends LocalizableTextField implements SettingsField {
   private static final long serialVersionUID = -5279771273100196802L;
   private String settingspath;

   ArgsField(SettingsForm sf, String placeholder, String settingspath) {
      super(sf, placeholder, (String)null, 0);
      this.settingspath = settingspath;
   }

   protected boolean check(String text) {
      return true;
   }

   public boolean isValueValid() {
      return true;
   }

   public String getSettingsPath() {
      return this.settingspath;
   }

   public void setToDefault() {
      this.setValue((String)null);
   }
}
