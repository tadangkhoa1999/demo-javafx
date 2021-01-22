package com.ceti.demojfx.controller;

import java.net.URL;
import java.util.Arrays;
import java.util.Random;
import java.util.ResourceBundle;

import com.ceti.demojfx.common.util.TableForm.CustomComboBoxTableCell;
import com.ceti.demojfx.common.util.TableForm.CustomTextFieldTableCell;
import com.ceti.demojfx.common.util.TableForm.SpecialColumn;
import com.ceti.demojfx.model.DemoTableData;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
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
	public TableColumn<DemoTableData, String> clOption;

	@FXML
	public Pagination pnPagination;

	@FXML
	public Button btnCheck;

	private IntegerProperty page = new SimpleIntegerProperty(0);
	private Integer size = 20;
	private Integer totalItems = 55;

	public TableController() {

	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		SpecialColumn.setIndexColumn(clNo, page, size);

		clName.setCellValueFactory(new PropertyValueFactory<>("name"));
		clName.setCellFactory(cell -> {
			CustomTextFieldTableCell<DemoTableData, String> customCell = new CustomTextFieldTableCell<DemoTableData, String>();
			customCell.setCommitEditFun((param) -> {
				DemoTableData data = tbDemoTable.getItems().get((int) param.get("index"));
				data.setName((String) param.get("value"));
				return 1;
			});
			return customCell;
		});

		clAge.setCellValueFactory(new PropertyValueFactory<>("age"));
		clAge.setEditable(false);
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

		clAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));

		clStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
		clStatus.setCellFactory(cell -> {
			CustomComboBoxTableCell<DemoTableData, String> customCell = new CustomComboBoxTableCell<DemoTableData, String>();
			customCell.setGetItemsFun((page) -> {
				return Arrays.asList("A", "B", "C", "D", "E");
			});
			customCell.setCommitEditFun((param) -> {
				DemoTableData data = tbDemoTable.getItems().get((int) param.get("index"));
				data.setStatus((String) param.get("value"));
				return 1;
			});
			return customCell;
		});

		setDataToTable();
		pnPagination.currentPageIndexProperty().addListener((observable, oldValue, newValue) -> {
			page.set(newValue.intValue());
			setDataToTable();
			tbDemoTable.refresh();
		});
	}

	private void setDataToTable() {
		tbDemoTable.getItems().clear();
		for (int i = page.intValue() * size + 1; i < (page.intValue() + 1) * size + 1 && i < totalItems; i++) {
			DemoTableData data = new DemoTableData();
			data.setName("name " + i);
			data.setAge(randInt(20, 35));
			data.setAmount((float) 0);
			data.setStatus(Arrays.asList("A", "B", "C").get(i % 3));
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
		System.out.println();
		for (DemoTableData data : tbDemoTable.getItems()) {
			System.out.println(data);
		}
	}

	public static int randInt(int min, int max) {
		Random rand = new Random();
		int randomNum = rand.nextInt((max - min) + 1) + min;

		return randomNum;
	}
}
