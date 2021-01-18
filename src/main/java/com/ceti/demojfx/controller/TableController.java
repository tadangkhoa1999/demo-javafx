package com.ceti.demojfx.controller;

import java.net.URL;
import java.util.ResourceBundle;

import com.ceti.demojfx.common.util.CustomTextFieldTableCell;
import com.ceti.demojfx.model.DemoTableData;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.util.converter.DefaultStringConverter;
import javafx.util.converter.IntegerStringConverter;

public class TableController implements Initializable {

	@FXML
	public TableView<DemoTableData> tbDemoTable;

	@FXML
	public TableColumn<DemoTableData, Integer> clNo;

	@FXML
	public TableColumn<DemoTableData, String> clName;

	@FXML
	public TableColumn<DemoTableData, Integer> clAge;

	@FXML
	public TableColumn<DemoTableData, Float> clAmount;

	@FXML
	public TableColumn<DemoTableData, String> clStatus;

	@FXML
	public TableColumn<DemoTableData, Void> clOption;

	@FXML
	public Pagination pnPagination;

	@FXML
	public Button btnCheck;

	private Integer page = 0;
	private Integer size = 20;
	private Integer totalItems = 55;

	public TableController() {

	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		clNo.setCellFactory(col -> {
			TableCell<DemoTableData, Integer> cell = new TableCell<DemoTableData, Integer>() {
				@Override
				protected void updateItem(Integer data, boolean empty) {
					super.updateItem(data, empty);
					if (empty) {
						setGraphic(null);
					} else {
						setGraphic(new Text("" + (page * size + getIndex() + 1)));
					}
				}
			};
			return cell;
		});
		clNo.setMinWidth(40);
		clNo.setMaxWidth(40);

		clName.setCellValueFactory(new PropertyValueFactory<>("name"));
		clName.setCellFactory(cell -> {
			CustomTextFieldTableCell<DemoTableData, String> customCell = new CustomTextFieldTableCell<DemoTableData, String>();
			customCell.setConverter(new DefaultStringConverter());
			customCell.setCommitEditFun((param) -> {
				DemoTableData data = tbDemoTable.getItems().get((int) param.get("index"));
				data.setName((String) param.get("value"));
				return 1;
			});
			customCell.setAlwaysViewTextField(true);
			return customCell;
		});

		clAge.setCellValueFactory(new PropertyValueFactory<>("age"));
		clAge.setCellFactory(cell -> {
			CustomTextFieldTableCell<DemoTableData, Integer> customCell = new CustomTextFieldTableCell<DemoTableData, Integer>();
			customCell.setConverter(new IntegerStringConverter());
			customCell.setCommitEditFun((param) -> {
				DemoTableData data = tbDemoTable.getItems().get((int) param.get("index"));
				data.setAge((int) param.get("value"));
				return 1;
			});
			customCell.setAlwaysViewTextField(true);
			return customCell;
		});

		setDataToTable();
		pnPagination.currentPageIndexProperty().addListener((observable, oldValue, newValue) -> {
			page = newValue.intValue();
			setDataToTable();
			tbDemoTable.refresh();
		});
	}

	private void setDataToTable() {

		tbDemoTable.getItems().clear();
		for (int i = page * size + 1; i < (page + 1) * size + 1 && i < totalItems; i++) {
			DemoTableData data = new DemoTableData();
			data.setName("name " + i);
			tbDemoTable.getItems().add(data);
		}

		int totalPage = (int) (Math.ceil(totalItems * 1.0 / size));
		if (totalPage != 0) {
			pnPagination.setVisible(true);
			pnPagination.setPageCount(totalPage);
		} else {
			pnPagination.setVisible(false);
		}
	}

	@FXML
	public void onCheckClick(ActionEvent event) {
		System.out.println("======================================");
		for (DemoTableData data : tbDemoTable.getItems()) {
			System.out.println(data);
		}
		System.out.println("======================================");
	}
}
