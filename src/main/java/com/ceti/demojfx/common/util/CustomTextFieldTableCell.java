package com.ceti.demojfx.common.util;

import java.util.HashMap;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.Cell;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
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
		setConverter(converter);
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
				textField = createTextField(this, getConverter());
			}

			startEdit(this, getConverter(), null, null, textField);
		}
	}

	@Override
	public void cancelEdit() {
		super.cancelEdit();
		cancelEdit(this, getConverter(), null);
	}

	@Override
	public void updateItem(T item, boolean empty) {
		super.updateItem(item, empty);

		if (textField == null && alwaysViewTextField) {
			textField = createTextField(this, getConverter());
		}

		updateItem(this, getConverter(), null, null, textField, alwaysViewTextField);
		if (colorFun != null) {
			Color color = colorFun.call(getIndex());
			setStyle("-fx-text-fill:" + toRGBCode(color) + ";");
			if (textField != null) {
				textField.setStyle("-fx-text-inner-color:" + toRGBCode(color) + ";");
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

	// Function of javafx.scene.control.cell.CellUtils (not public -> need to copy)
	private static <T> String getItemText(Cell<T> cell, StringConverter<T> converter) {
		return converter == null ? cell.getItem() == null ? "" : cell.getItem().toString()
				: converter.toString(cell.getItem());
	}

	static <T> TextField createTextField(final Cell<T> cell, final StringConverter<T> converter) {

		final TextField textField = new TextField(getItemText(cell, converter));

		if (converter == null) {
			throw new IllegalStateException("StringConverter is null");
		}

		textField.setOnAction(event -> {
			// Because when enter -> lose focus -> only need focus listener
			cell.cancelEdit();
			event.consume();
		});

		textField.focusedProperty().addListener((arg0, oldValue, newValue) -> {
			if (!newValue && oldValue && !textField.getText().isBlank()) {
				cell.commitEdit(converter.fromString(textField.getText()));
			}
		});

		textField.setOnKeyReleased(t -> {
			if (t.getCode() == KeyCode.ESCAPE) {
				cell.cancelEdit();
				t.consume();
			}
		});
		return textField;
	}

	static <T> void startEdit(final Cell<T> cell, final StringConverter<T> converter, final HBox hbox,
			final Node graphic, final TextField textField) {
		if (textField != null) {
			textField.setText(getItemText(cell, converter));
		}
		cell.setText(null);

		if (graphic != null) {
			hbox.getChildren().setAll(graphic, textField);
			cell.setGraphic(hbox);
		} else {
			cell.setGraphic(textField);
		}

		textField.selectAll();
		textField.requestFocus();
	}

	static <T> void cancelEdit(Cell<T> cell, final StringConverter<T> converter, Node graphic) {
		cell.setText(getItemText(cell, converter));
		cell.setGraphic(graphic);
	}

	static <T> void updateItem(final Cell<T> cell, final StringConverter<T> converter, final HBox hbox,
			final Node graphic, final TextField textField, Boolean alwaysViewTextField) {
		if (cell.isEmpty()) {
			cell.setText(null);
			cell.setGraphic(null);
		} else {
			if (cell.isEditing() || alwaysViewTextField) {
				if (textField != null) {
					textField.setText(getItemText(cell, converter));
				}
				cell.setText(null);

				if (graphic != null) {
					hbox.getChildren().setAll(graphic, textField);
					cell.setGraphic(hbox);
				} else {
					cell.setGraphic(textField);
				}
			} else {
				cell.setText(getItemText(cell, converter));
				cell.setGraphic(graphic);
			}
		}
	}

	static String toRGBCode(Color color) {
		return String.format("#%02X%02X%02X", (int) (color.getRed() * 255), (int) (color.getGreen() * 255),
				(int) (color.getBlue() * 255));
	}
}
