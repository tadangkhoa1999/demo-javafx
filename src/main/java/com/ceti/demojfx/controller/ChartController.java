package com.ceti.demojfx.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import com.sun.javafx.charts.Legend;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class ChartController implements Initializable {

	@FXML
	public BorderPane chartPane;

	@FXML
	public HBox hboxLegend;

	BarChart<String, Number> barChart;

	private CategoryAxis xAxis;
	private NumberAxis yAxis;

	private XYChart.Series<String, Number> seriesDuKien = new XYChart.Series<String, Number>();
	private XYChart.Series<String, Number> seriesThucTe = new XYChart.Series<String, Number>();

	private List<String> listMonth;
	private List<Float> listDuKien;
	private List<Float> listThucTe;
	private List<Float> listBu;

	public ChartController() {
		listDuKien = Arrays.asList(new Float[] { (float) 200, (float) 300, (float) 250, (float) 100, (float) 170,
				(float) 200, (float) 230, (float) 200, (float) 400, (float) 400, (float) 400, (float) 200 });

		listThucTe = Arrays.asList(new Float[] { (float) 150, (float) 400, (float) 350, (float) 150, (float) 150,
				(float) 100, (float) 230, (float) 250, (float) 300, (float) 300, (float) 300, (float) 180 });

		listBu = new ArrayList<Float>();
		listBu.add((float) 0);
		for (int i = 0; i < listDuKien.size() - 1; i++) {
			listBu.add(listThucTe.get(i) - (listDuKien.get(i) - listBu.get(i)));
		}

		listMonth = new ArrayList<String>();
		for (int i = 0; i < listDuKien.size(); i++) {
			listMonth.add("Tháng " + (i + 1));
		}
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		xAxis = new CategoryAxis();
		xAxis.setLabel("Tháng");
		xAxis.setCategories(FXCollections.<String>observableArrayList(listMonth));

		yAxis = new NumberAxis();
		yAxis.setLabel("Giá trị");
		yAxis.setAutoRanging(false);
		yAxis.setTickUnit(25);

		barChart = new BarChart<String, Number>(xAxis, yAxis);
		barChart.setTitle("Biểu đồ mẫu");

		seriesDuKien.setName("Dự kiến");
		seriesThucTe.setName("Thực tế");

		barChart.getData().add(seriesDuKien);
		barChart.getData().add(seriesThucTe);

		for (int i = 0; i < listDuKien.size(); i++) {

			seriesDuKien.getData().add(new Data<>("Tháng " + (i + 1), listDuKien.get(i)));
			seriesThucTe.getData().add(new Data<>("Tháng " + (i + 1), listThucTe.get(i)));
			final float bu = listBu.get(i);
			final float duKien = listDuKien.get(i);

			if (listBu.get(i) > 0 && listDuKien.get(i) - listBu.get(i) < yAxis.getLowerBound() + 50) {
				yAxis.setLowerBound(listDuKien.get(i) - listBu.get(i) - 50);
			} else if (listBu.get(i) < 0 && listDuKien.get(i) - listBu.get(i) > yAxis.getUpperBound() - 50) {
				yAxis.setUpperBound(listDuKien.get(i) - listBu.get(i) + 50);
			} else if (listDuKien.get(i) > yAxis.getUpperBound() - 50) {
				yAxis.setUpperBound(listDuKien.get(i) + 50);
			} else if (listThucTe.get(i) > yAxis.getUpperBound() - 50) {
				yAxis.setUpperBound(listThucTe.get(i) + 50);
			}

			Data<String, Number> data = seriesDuKien.getData().get(i);
			Rectangle r = new Rectangle();
			r.setFill(Color.RED);

			Node node = data.getNode();
			Parent parent = node.getParent();
			Group parentGroup = (Group) parent;
			parentGroup.getChildren().add(r);

			node.boundsInParentProperty().addListener(new ChangeListener<Bounds>() {
				@Override
				public void changed(ObservableValue<? extends Bounds> ov, Bounds oldBounds, Bounds bounds) {
					r.setWidth(bounds.getWidth() * 0.7);
					r.setHeight(bounds.getHeight() * (Math.abs(bu) / duKien));
					r.setX(bounds.getMinX() + bounds.getWidth() / 2 - r.prefWidth(-1) / 2);
					r.setY(bounds.getMinY() + Math.min(Math.signum(bu) * r.getHeight(), 0));
				}
			});
		}

		// set first bar color
		for (Node n : barChart.lookupAll(".default-color0.chart-bar")) {
			n.setStyle("-fx-bar-fill: green;");
		}
		// second bar color
		for (Node n : barChart.lookupAll(".default-color1.chart-bar")) {
			n.setStyle("-fx-bar-fill: blue;");
		}

		Rectangle r = new Rectangle();
		r.setWidth(20);
		r.setHeight(20);
		r.setFill(Color.GREEN);
		hboxLegend.getChildren().add(r);
		hboxLegend.getChildren().add(new Text("Dự kiến"));

		r = new Rectangle();
		r.setWidth(20);
		r.setHeight(20);
		r.setFill(Color.BLUE);
		hboxLegend.getChildren().add(r);
		hboxLegend.getChildren().add(new Text("Thực tế"));

		r = new Rectangle();
		r.setWidth(20);
		r.setHeight(20);
		r.setFill(Color.RED);
		hboxLegend.getChildren().add(r);
		hboxLegend.getChildren().add(new Text("Bù"));

		barChart.setLegendVisible(false);

		for (Node n : barChart.getChildrenUnmodifiable()) {
			if (n instanceof Legend) {
				final Legend legend = (Legend) n;
			}
		}

		chartPane.setCenter(barChart);
	}

}
