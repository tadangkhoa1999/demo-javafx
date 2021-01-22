package com.ceti.demojfx.common.util.TableForm;

import java.util.HashMap;
import java.util.List;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.util.Callback;
import javafx.util.StringConverter;

public class CustomComboBoxTableCell<S, T> extends TableCell<S, T> {

	private final ObservableList<T> items;
	private ComboBox<T> comboBox;
	private ObjectProperty<StringConverter<T>> converter = new SimpleObjectProperty<StringConverter<T>>(this,
			"converter");
	private Callback<HashMap<String, Object>, Integer> commitEditFun;
	private Boolean alwaysViewComboBox = true;
	// page for loadmore
	private int page = 0;
	private Callback<Integer, List<T>> getItemsFun;

	public CustomComboBoxTableCell() {
		this(null, FXCollections.<T>observableArrayList());
	}

	@SafeVarargs
	public CustomComboBoxTableCell(T... items) {
		this(null, FXCollections.observableArrayList(items));
	}

	public CustomComboBoxTableCell(List<T> items) {
		this(null, FXCollections.observableArrayList(items));
	}

	public CustomComboBoxTableCell(StringConverter<T> converter, ObservableList<T> items) {
		this.getStyleClass().add("combo-box-table-cell");
		this.items = items;
		setConverter(converter != null ? converter : (StringConverter<T>) CustomTableCellUtil.defaultStringConverter);
	}

	public final ObjectProperty<StringConverter<T>> converterProperty() {
		return converter;
	}

	public final StringConverter<T> getConverter() {
		return converterProperty().get();
	}

	public final void setConverter(StringConverter<T> value) {
		converterProperty().set(value);
	}

	public final void setCommitEditFun(Callback<HashMap<String, Object>, Integer> commitEditFun) {
		this.commitEditFun = commitEditFun;
	}

	public final void setAlwaysViewComboBox(Boolean alwaysViewComboBox) {
		this.alwaysViewComboBox = alwaysViewComboBox;
	}

	public final void setGetItemsFun(Callback<Integer, List<T>> getItemsFun) {
		this.getItemsFun = getItemsFun;
		this.items.setAll(getItemsFun.call(page));
	}

	@Override
	public void startEdit() {
		if (!isEditable() || !getTableView().isEditable() || !getTableColumn().isEditable()) {
			return;
		}

		if (comboBox == null) {
			comboBox = CustomTableCellUtil.createComboBox(this, items, converterProperty());
		}

		comboBox.getSelectionModel().select(getItem());

		super.startEdit();
		setText(null);
		setGraphic(comboBox);
	}

	@Override
	public void cancelEdit() {
		super.cancelEdit();

		setText(getConverter().toString(getItem()));
		setGraphic(null);
	}

	@Override
	public void updateItem(T item, boolean empty) {
		super.updateItem(item, empty);

		if (comboBox == null && alwaysViewComboBox) {
			comboBox = CustomTableCellUtil.createComboBox(this, items, converterProperty());
			comboBox.getSelectionModel().select(getItem());
		}

		CustomTableCellUtil.updateItem(this, getConverter(), null, null, comboBox, alwaysViewComboBox);
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
