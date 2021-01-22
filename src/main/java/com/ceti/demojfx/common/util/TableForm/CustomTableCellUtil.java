package com.ceti.demojfx.common.util.TableForm;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Cell;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.Skin;
import javafx.scene.control.TextField;
import javafx.scene.control.skin.ComboBoxListViewSkin;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;

public class CustomTableCellUtil {

	/***************************************************************************
	 * * General convenience * *
	 **************************************************************************/

	public final static StringConverter<?> defaultStringConverter = new StringConverter<Object>() {
		@Override
		public String toString(Object t) {
			return t == null ? null : t.toString();
		}

		@Override
		public Object fromString(String string) {
			return (Object) string;
		}
	};

	public static <T> String getItemText(Cell<T> cell, StringConverter<T> converter) {
		return converter == null ? cell.getItem() == null ? "" : cell.getItem().toString()
				: converter.toString(cell.getItem());
	}

	public static String toRGBCode(Color color) {
		return String.format("#%02X%02X%02X", (int) (color.getRed() * 255), (int) (color.getGreen() * 255),
				(int) (color.getBlue() * 255));
	}

	/***************************************************************************
	 * * TextField convenience * *
	 **************************************************************************/

	public static <T> TextField createTextField(final Cell<T> cell, final StringConverter<T> converter) {

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

	public static <T> void startEdit(final Cell<T> cell, final StringConverter<T> converter, final HBox hbox,
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

	public static <T> void cancelEdit(Cell<T> cell, final StringConverter<T> converter, Node graphic) {
		cell.setText(getItemText(cell, converter));
		cell.setGraphic(graphic);
	}

	public static <T> void updateItem(final Cell<T> cell, final StringConverter<T> converter, final HBox hbox,
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

	/***************************************************************************
	 * * ComboBox convenience * *
	 **************************************************************************/

	public static <T> void updateItem(final Cell<T> cell, final StringConverter<T> converter, final HBox hbox,
			final Node graphic, final ComboBox<T> comboBox, Boolean alwaysViewComboBox) {
		if (cell.isEmpty()) {
			cell.setText(null);
			cell.setGraphic(null);
		} else {
			if (cell.isEditing() || alwaysViewComboBox) {
				if (comboBox != null) {
					comboBox.getSelectionModel().select(cell.getItem());
				}
				cell.setText(null);

				if (graphic != null) {
					hbox.getChildren().setAll(graphic, comboBox);
					cell.setGraphic(hbox);
				} else {
					cell.setGraphic(comboBox);
				}
			} else {
				cell.setText(getItemText(cell, converter));
				cell.setGraphic(graphic);
			}
		}
	};

	public static <T> ComboBox<T> createComboBox(final Cell<T> cell, final ObservableList<T> items,
			final ObjectProperty<StringConverter<T>> converter) {

		ComboBox<T> comboBox = new ComboBox<T>(items);
		comboBox.converterProperty().bind(converter);
		comboBox.setMaxWidth(Double.MAX_VALUE);

		// Add a listener to the ListView within it:
		// when the user mouse clicks on a on an item,
		// that is immediately committed and the cell exits the editing mode.
		boolean success = listenToComboBoxSkin(comboBox, cell);
		if (!success) {
			comboBox.skinProperty().addListener(new InvalidationListener() {
				@Override
				public void invalidated(Observable observable) {
					boolean successInListener = listenToComboBoxSkin(comboBox, cell);
					if (successInListener) {
						comboBox.skinProperty().removeListener(this);
					}
				}
			});
		}

		return comboBox;
	}

	public static <T> boolean listenToComboBoxSkin(final ComboBox<T> comboBox, final Cell<T> cell) {
		Skin<?> skin = comboBox.getSkin();
		if (skin != null && skin instanceof ComboBoxListViewSkin) {
			ComboBoxListViewSkin cbSkin = (ComboBoxListViewSkin) skin;
			Node popupContent = cbSkin.getPopupContent();
			if (popupContent != null && popupContent instanceof ListView) {
				popupContent.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> cell.commitEdit(comboBox.getValue()));
				return true;
			}
		}
		return false;
	}
}
