package com.devstack.pos.controller;

import com.devstack.pos.bo.BoFactory;
import com.devstack.pos.bo.custom.ProductDetailBo;
import com.devstack.pos.dto.CustomerDto;
import com.devstack.pos.dto.ProductDetailDto;
import com.devstack.pos.enums.BoType;
import com.devstack.pos.util.QrDataGenerator;
import com.devstack.pos.view.tm.ProductDetailTm;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.QRCodeWriter;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.apache.commons.codec.binary.Base64;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;

public class NewBatchFormController {
    public TextArea txtSelectedProdDescription;
    public ImageView barcodeImage;
    public TextField txtQty;
    public TextField txtBuyingPrice;
    public TextField txtProductCode;
    public RadioButton rBtnYes;
    public TextField txtSellingPrice;
    public TextField txtShowPrice;
    public AnchorPane context;
    String uniqueData = null;
    BufferedImage bufferedImage =null;
    Stage stage = null;

    private ProductDetailBo productDetailBo = BoFactory.getInstance().getBo(BoType.PRODUCT_DETAIL);


    public void initialize() throws WriterException {

    }

    private void setQRCode() throws WriterException {
        uniqueData = QrDataGenerator.generate(25);
        //---------------------------Gen QR

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        bufferedImage =
                MatrixToImageWriter.toBufferedImage(
                        qrCodeWriter.encode(
                                uniqueData,
                                BarcodeFormat.QR_CODE,
                                160, 160
                        )
                );

        //---------------------------Gen QR
        Image image = SwingFXUtils.toFXImage(bufferedImage, null);
        barcodeImage.setImage(image);
    }

    public void setDetails(int code, String description, Stage stage, boolean state, ProductDetailTm tm) {
        this.stage = stage;
        if (state){
            try {
                ProductDetailDto productDetails = productDetailBo.findProductDetails(tm.getCode());
                if (productDetails!=null){
                    txtQty.setText(String.valueOf(productDetails.getQtyOnHand()));
                    txtBuyingPrice.setText(String.valueOf(productDetails.getBuyingPrice()));
                    txtSellingPrice.setText(String.valueOf(productDetails.getSellingPrice()));
                    txtShowPrice.setText(String.valueOf(productDetails.getShowPrice()));
                    rBtnYes.setSelected(productDetails.isDiscountAvailability());

                    byte[] data = Base64.decodeBase64(productDetails.getBarcode());
                    barcodeImage.setImage(
                            new Image(new ByteArrayInputStream(data))
                    );

                }
                else {
                    stage.close();
                }
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            try {
                setQRCode();
            } catch (WriterException e) {
                throw new RuntimeException(e);
            }
        }
        txtProductCode.setText(String.valueOf(code));
        txtSelectedProdDescription.setText(description);
    }
    public void btnBackToHomeOnAction(ActionEvent actionEvent) throws IOException {
        setUi("ProductMainForm");
    }

    public void saveBatchOnAction(ActionEvent actionEvent) throws IOException {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                javax.imageio.ImageIO.write(bufferedImage, "png",baos);
                byte[] arr = baos.toByteArray();


        ProductDetailDto dto = new ProductDetailDto(
                uniqueData, Base64.encodeBase64String(arr),Integer.parseInt(txtQty.getText()),Double.parseDouble(txtSellingPrice.getText())
                ,Double.parseDouble(txtBuyingPrice.getText()),rBtnYes.isSelected()?true:false,Double.parseDouble(txtShowPrice.getText())
                ,Integer.parseInt(txtProductCode.getText())
        );
        try {
            if (
            productDetailBo.saveProductDetail(dto)
            ){
                new Alert(Alert.AlertType.CONFIRMATION, "Batch Saved!").show();
                Thread.sleep(3000);
                this.stage.close();
            }
            else {
                new Alert(Alert.AlertType.WARNING, "Try Again!").show();
            }
        } catch (SQLException | ClassNotFoundException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void setUi(String url) throws IOException {
        Stage stage = (Stage) context.getScene().getWindow();
        stage.setScene(
                new Scene(FXMLLoader.load(getClass().getResource("../view/" + url + ".fxml")))
        );
        stage.centerOnScreen();
    }
}
