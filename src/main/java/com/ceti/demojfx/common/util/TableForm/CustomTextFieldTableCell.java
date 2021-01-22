package com.ceti.demojfx.common.util.TableForm;

import java.util.HashMap;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import javafx.util.StringConverter;

public class CustomTextFieldTableCell<S, T> extends TableCell<S, T> {

	private TextField textField;
	private ObjectProperty<StringConverter<T>> converter = new SimpleObjectProperty<StringConverter<T>>(this,
			"converter");
	private Callback<HashMap<String, Object>, Integer> commitEditFun;
	private Callback<Integer, Boolean> allowUpdateFun;
	private Callback<Integer, Color> colorFun;
	private Boolean alwaysViewTextField = false;

	public CustomTextFieldTableCell() {
		this(null);
	}

	public CustomTextFieldTableCell(StringConverter<T> converter) {
		this.getStyleClass().add("text-field-table-cell");
		setConverter(converter != null ? converter : (StringConverter<T>) CustomTableCellUtil.defaultStringConverter);
	}

	public final ObjectProperty<StringConverter<T>> converterProperty() {
		return converter;
	}

	public final void setConverter(StringConverter<T> value) {
		converterProperty().set(value);
	}

	public final StringConverter<T> getConverter() {
		return converterProperty().get();
	}

	public final void setCommitEditFun(Callback<HashMap<String, Object>, Integer> commitEditFun) {
		this.commitEditFun = commitEditFun;
	}

	public final void setAllowUpdateFun(Callback<Integer, Boolean> allowUpdateFun) {
		this.allowUpdateFun = allowUpdateFun;
	}

	public final void setColorFun(Callback<Integer, Color> colorFun) {
		this.colorFun = colorFun;
	}

	public final void setAlwaysViewTextField(Boolean alwaysViewTextField) {
		this.alwaysViewTextField = alwaysViewTextField;
	}

	@Override
	public void startEdit() {
		if (!isEditable() || !getTableView().isEditable() || !getTableColumn().isEditable()) {
			return;
		}
		if (allowUpdateFun != null && !allowUpdateFun.call(getIndex())) {
			return;
		}
		super.startEdit();

		if (isEditing()) {
			if (textField == null) {
				textField = CustomTableCellUtil.createTextField(this, getConverter());
			}

			CustomTableCellUtil.startEdit(this, getConverter(), null, null, textField);
		}
	}

	@Override
	public void cancelEdit() {
		super.cancelEdit();
		CustomTableCellUtil.cancelEdit(this, getConverter(), null);
	}

	@Override
	public void updateItem(T item, boolean empty) {
		super.updateItem(item, empty);

		if (textField == null && alwaysViewTextField) {
			textField = CustomTableCellUtil.createTextField(this, getConverter());
			if (!isEditable() || !getTableView().isEditable() || !getTableColumn().isEditable()) {
				textField.setEditable(false);
			}
			if (allowUpdateFun != null && !allowUpdateFun.call(getIndex())) {
				textField.setEditable(false);
			}
		}

		CustomTableCellUtil.updateItem(this, getConverter(), null, null, textField, alwaysViewTextField);
		if (colorFun != null) {
			Color color = colorFun.call(getIndex());
			setStyle("-fx-text-fill:" + CustomTableCellUtil.toRGBCode(color) + ";");
			if (textField != null) {
				textField.setStyle("-fx-text-inner-color:" + CustomTableCellUtil.toRGBCode(color) + ";");
			}
		}
	}

	@Override
	public void commitEdit(T newValue) {
		super.commitEdit(newValue);

		if (commitEditFun == null) {
			throw new IllegalStateException("Commit Edit Function is null");
		}

		int index = getTableRow().getIndex();
		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("index", index);
		param.put("value", newValue);
		commitEditFun.call(param);

		updateItem(newValue, false);
	}
}
