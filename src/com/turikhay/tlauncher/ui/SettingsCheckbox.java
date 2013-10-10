package com.turikhay.tlauncher.ui;

import com.turikhay.tlauncher.exceptions.ParseException;
import com.turikhay.tlauncher.util.StringUtil;

public class SettingsCheckbox extends LocalizableCheckbox implements SettingsField {
   private static final long serialVersionUID = -9013976214526482171L;
   private String settingspath;

   public SettingsCheckbox(String path, String settingspath) {
      super(path);
      this.settingspath = settingspath;
   }

   public String getValue() {
      return String.valueOf(this.getState());
   }

   public boolean isValueValid() {
      return true;
   }

   public void setValue(String value) {
      try {
         this.setState(StringUtil.parseBoolean(value));
      } catch (ParseException var3) {
      }

   }

   public String getSettingsPath() {
      return this.settingspath;
   }

   public void setToDefault() {
      this.setValue((String)null);
   }
}
