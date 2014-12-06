package ru.turikhay.tlauncher.ui.swing.extended;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.ListCellRenderer;

import ru.turikhay.tlauncher.ui.TLauncherFrame;
import ru.turikhay.tlauncher.ui.converter.StringConverter;
import ru.turikhay.tlauncher.ui.swing.DefaultConverterCellRenderer;
import ru.turikhay.tlauncher.ui.swing.SimpleComboBoxModel;
import ru.turikhay.util.Reflect;

public class ExtendedComboBox<T> extends JComboBox<T> {
	private static final long serialVersionUID = -4509947341182373649L;
	private StringConverter<T> converter;

	public ExtendedComboBox(ListCellRenderer<T> renderer) {
		setModel(new SimpleComboBoxModel<T>());
		setRenderer(renderer);
		setOpaque(false);
		setFont(getFont().deriveFont(TLauncherFrame.fontSize));

		Reflect.cast(getEditor().getEditorComponent(), JComponent.class).setOpaque(false);
	}

	public ExtendedComboBox(StringConverter<T> converter) {
		this(new DefaultConverterCellRenderer<T>(converter));
		this.converter = converter;
	}

	public ExtendedComboBox() {
		this((ListCellRenderer<T>) null);
	}

	public SimpleComboBoxModel<T> getSimpleModel() {
		return (SimpleComboBoxModel<T>) getModel();
	}

	public T getValueAt(int i) {
		Object value = getItemAt(i);
		return returnAs(value);
	}

	public T getSelectedValue() {
		Object selected = getSelectedItem();
		return returnAs(selected);
	}

	public void setSelectedValue(T value) {
		setSelectedItem(value);
	}

	public void setSelectedValue(String string) {
		T value = convert(string);
		if (value == null)
			return;

		setSelectedValue(value);
	}

	public StringConverter<T> getConverter() {
		return converter;
	}

	public void setConverter(StringConverter<T> converter) {
		this.converter = converter;
	}

	protected String convert(T obj) {
		T from = returnAs(obj);

		if (converter != null)
			return converter.toValue(from);
		return from == null ? null : from.toString();
	}

	protected T convert(String from) {
		if (converter == null)
			return null;
		return converter.fromString(from);
	}

	@SuppressWarnings("unchecked")
	private T returnAs(Object obj) {
		try {
			return (T) obj;
		} catch (ClassCastException ce) {
			return null;
		}
	}

}
