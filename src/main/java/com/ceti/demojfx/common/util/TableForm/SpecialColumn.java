package com.ceti.demojfx.common.util.TableForm;

import org.apache.poi.ss.formula.functions.T;

import javafx.beans.property.IntegerProperty;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.text.Text;

public class SpecialColumn {
	public static <T> void setIndexColumn(TableColumn<T, Integer> clIndex, IntegerProperty page, int size) {
		clIndex.setCellFactory(col -> {
			TableCell<T, Integer> cell = new TableCell<T, Integer>() {
				@Override
				protected void updateItem(Integer data, boolean empty) {
					super.updateItem(data, empty);
					if (empty) {
						setGraphic(null);
					} else {
						setGraphic(new Text("" + (page.intValue() * size + getIndex() + 1)));
					}
				}
			};
			return cell;
		});
		clIndex.setMinWidth(40);
		clIndex.setMaxWidth(40);
	}
}
