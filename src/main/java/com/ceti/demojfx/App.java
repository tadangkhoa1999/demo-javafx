package com.ceti.demojfx;

import static com.itextpdf.text.pdf.BaseFont.EMBEDDED;
import static com.itextpdf.text.pdf.BaseFont.IDENTITY_H;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Document.OutputSettings.Syntax;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.ceti.demojfx.controller.ChartController;
import com.itextpdf.text.DocumentException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

	public void start(Stage stage) throws IOException {
//		testPdf(stage);

		ChartController chartController = new ChartController();
		Scene scene = new Scene(loadFXML("chartScreen.fxml", chartController));
		stage.setTitle("Chart Test");
		stage.setScene(scene);
		stage.show();
	}

	private void testPdf(Stage stage) throws IOException {

		List<String> listPdfTest = Arrays.asList("00-Danh-sach-hop-dong-nhan-hang.html",
				"01-Bang-ke-hoach-kiem-tra-noi-o-dinh-ky.html", "02-Thong-bao-kiem-tra-noi-bo.html",
				"03-Bien-ban-kiem-tra.html", "04-Bao-cao-ket-qua-kiem-tra.html", "05-Thong-bao-xu-ly-vi-pham.html",
				"06-Thong-bao-xu-ly-vi-pham.html", "07-Phieu-tham-tra-qua-trinh-khac-phuc.html",
				"08-Bao-cao-giai-trinh-cua-co-so.html", "09-Thong-bao-dieu-chinh.html",
				"10-Ke-hoach-kiem-tra-noi-bo-dot-xuat.html");

		for (String inFileName : listPdfTest) {
			stage.setTitle("Hello World Application");

			String html = setParam(inFileName);
			String xhtml = htmlToXhtml(html);
			String outputName = inFileName.split("//.")[0] + ".pdf";
			try {
				xhtmlToPdf(xhtml, inFileName, outputName);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

//			String demoHtmlPath = "demo_" + Instant.now().getEpochSecond() + ".html";
//			File demoHtmlFile = new File(App.class.getResource("html/").getPath() + demoHtmlPath);
//			FileWriter writer = new FileWriter(demoHtmlFile);
//			writer.write(html);
//			writer.flush();
//			writer.close();
//
//			WebView browser = new WebView();
//			WebEngine webEngine = browser.getEngine();
//			webEngine.load(App.class.getResource("html/" + demoHtmlPath).toExternalForm());
//
//			Button btn = new Button("Print");
//			btn.setOnAction((t) -> {
//				try {
//					String xhtml = htmlToXhtml(html);
//					String outputName = inFileName.split("//.")[0] + ".pdf";
//					xhtmlToPdf(xhtml, inFileName, outputName);
//					System.out.println("OK");
//				} catch (IOException | DocumentException ex) {
//					Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
//				}
//			});
//
//			VBox vBox = new VBox();
//			vBox.getChildren().addAll(btn, browser);
//			Scene scene = new Scene(vBox);
//			stage.setScene(scene);
//			stage.setWidth(1000);
//			stage.setHeight(1000);
//			stage.show();
//
//			demoHtmlFile.delete();
		}
	}

	private static String setParam(String inFileName) {

		ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
		templateResolver.setTemplateMode(TemplateMode.HTML);
		templateResolver.setCharacterEncoding("UTF-8");
		templateResolver.setPrefix("/com/ceti/demojfx/html/");
		templateResolver.setSuffix(".html");

		TemplateEngine templateEngine = new TemplateEngine();
		templateEngine.setTemplateResolver(templateResolver);

		Context context = new Context();
		context.setVariable("customerName", "Ta Dang Khoa");

		return templateEngine.process(inFileName, context);
	}

	private static String htmlToXhtml(String html) throws IOException {

		InputStream is = new ByteArrayInputStream(html.getBytes(StandardCharsets.UTF_8));
		Document document = Jsoup.parse(is, "UTF-8", "");
		document.outputSettings().syntax(Syntax.xml);

		return document.html();
	}

	private static void xhtmlToPdf(String xhtml, String inFileName, String outFileName)
			throws IOException, DocumentException {
		File output = new File(outFileName);
		ITextRenderer iTextRenderer = new ITextRenderer();
		iTextRenderer.getFontResolver().addFont(App.class.getResource("font/vuArial.ttf").toString(), IDENTITY_H,
				EMBEDDED);
		iTextRenderer.setDocumentFromString(xhtml, App.class.getResource("html/" + inFileName).toString());
		iTextRenderer.layout();
		try (OutputStream os = new FileOutputStream(output)) {
			iTextRenderer.createPDF(os);
		}
	}

	public static Parent loadFXML(String fxml, Object controller) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader();
		fxmlLoader.setLocation(App.class.getResource("screen/" + fxml));
		fxmlLoader.setController(controller);
		return fxmlLoader.load();
	}

	public static void main(String[] args) {
		launch();
	}

}
