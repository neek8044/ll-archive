package com.turikhay.tlauncher.ui;


import javax.swing.JButton;

import com.turikhay.tlauncher.settings.Settings;

public class LocalizableButton extends JButton implements LocalizableComponent {
	private static final long serialVersionUID = 1L;
	static Settings l;
	
	private String path, r0, r1;
	private Object w0, w1;
	
	public LocalizableButton(){
		init();
	}
	public LocalizableButton(String path){
		this();
		this.setLabel(path);
	}
	public LocalizableButton(String path, String replace, Object with){
		this();
		this.setLabel(path, replace, with);
	}
	
	public void setLabel(String path){
		this.path = path;
		super.setText((l == null)? path : l.get(path));
	}
	
    public void setLabel(String path, String replace, Object with){
    	this.path = path; u(); r0 = replace; w0 = with;
    	super.setText((l == null)? path : l.get(path, replace, with));
    }
	
    public void setLabel(String path, String replace0, Object with0, String replace1, Object with1){
    	this.path = path; u(); r0 = replace0; w0 = with0; r1 = replace1; w1 = with1;
    	super.setText((l == null)? path : l.get(path, replace0, with0, replace1, with1));
    }
	
	public String getLangPath(){
		return path;
	}
	
	public void updateLocale() {
		this.setLabel(path, r0, w0, r1, w1);
	}
	
	private void u(){
		r0 = r1 = null;
		w0 = w1 = null;
	}
	
	private void init(){
        setOpaque(false);
	}
}
