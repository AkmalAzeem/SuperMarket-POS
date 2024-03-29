package com.devstack.pos.controller;

import com.devstack.pos.bo.BoFactory;
import com.devstack.pos.bo.custom.CustomerBo;
import com.devstack.pos.bo.custom.LoyaltyCardBo;
import com.devstack.pos.bo.custom.OrderDetailBo;
import com.devstack.pos.bo.custom.ProductDetailBo;
import com.devstack.pos.dto.*;
import com.devstack.pos.enums.BoType;
import com.devstack.pos.enums.CardType;
import com.devstack.pos.util.QrDataGenerator;
import com.devstack.pos.util.UserSessionData;
import com.devstack.pos.view.tm.CartTm;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.QRCodeWriter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.apache.commons.codec.binary.Base64;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class PlaceOrderFormController {
    public AnchorPane context;
    public ToggleGroup mode;
    public TextField txtEmail;
    public TextField txtName;
    public TextField txtContact;
    public TextField txtSalary;
    public Hyperlink urlNewLoyalty;
    public Hyperlink lblLoyaltyType;
    public TextField txtBarcode;
    public TextField txtDescription;
    public TextField txtSellingPrice;
    public TextField txtDiscount;
    public TextField txtShowPrice;
    public TextField txtQtyOnHand;
    public Hyperlink lblDiscountAv;
    public TextField txtBuyingPrice;
    public TextField txtQty;
    public TableView<CartTm> tblCart;
    public TableColumn colCode;
    public TableColumn colDescription;
    public TableColumn colSellingPrice;
    public TableColumn colSelDiscount;
    public TableColumn colSelShowPrice;
    public TableColumn colCelQty;
    public TableColumn colSelTotalCost;
    public TableColumn colSelOperation;
    public Hyperlink txtTotal;

    CustomerBo bo = BoFactory.getInstance().getBo(BoType.CUSTOMER);
    private ProductDetailBo productDetailBo = BoFactory.getInstance().getBo(BoType.PRODUCT_DETAIL);
    private OrderDetailBo orderDetailBo = BoFactory.getInstance().getBo(BoType.ORDER_DETAIL);
    private LoyaltyCardBo loyaltyCardBo = BoFactory.getInstance().getBo(BoType.LOYALTY_CARD);

    public void initialize(){
        colCode.setCellValueFactory(new PropertyValueFactory<>("code"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colSellingPrice.setCellValueFactory(new PropertyValueFactory<>("sellingPrice"));
        colSelDiscount.setCellValueFactory(new PropertyValueFactory<>("discount"));
        colSelShowPrice.setCellValueFactory(new PropertyValueFactory<>("showPrice"));
        colCelQty.setCellValueFactory(new PropertyValueFactory<>("qty"));
        colSelTotalCost.setCellValueFactory(new PropertyValueFactory<>("totalCost"));
        colSelOperation.setCellValueFactory(new PropertyValueFactory<>("btn"));
    }

    public void btnBackToHomeOnAction(ActionEvent actionEvent) throws IOException {
        setUi("DashboardForm", false);
    }

    public void btnNewCustomerOnAction(ActionEvent actionEvent) throws IOException {
        setUi("CustomerForm", true);
    }

    public void btnNewProductOnAction(ActionEvent actionEvent) throws IOException {
        setUi("ProductMainForm", true);
    }

    public void btnCompleteOrderOnAction(ActionEvent actionEvent) {
        ArrayList<ItemDetailDto> dtoList = new ArrayList<>();
        double discount = 0;
        for (CartTm tm : tms
             ) {
            dtoList.add(new ItemDetailDto(tm.getCode(),tm.getQty(), tm.getDiscount(), tm.getTotalCost()));
            discount+= tm.getDiscount();
        }
        OrderDetailDto dto = new OrderDetailDto(
                new Random().nextInt(100001),
                new Date(),
                Double.parseDouble(txtTotal.getText().split(" /=")[0]),
                txtEmail.getText(),
                discount,
                UserSessionData.email,
                dtoList
        );
        try {
            if(orderDetailBo.makeOrder(dto)){
                new Alert(Alert.AlertType.CONFIRMATION, "Saved!").show();
                clearFields();
            }
            else {
                new Alert(Alert.AlertType.WARNING, "Try Again!").show();
            }
        }
        catch (SQLException e){
            e.printStackTrace();
            new Alert(Alert.AlertType.WARNING, "Can't Find Product").show();
        }
    }

    private void clearFields() {
    }

    private void setUi(String url, boolean state) throws IOException {
        Stage stage = null;
        Scene scene = new Scene(FXMLLoader.load(getClass().getResource("../view/" + url + ".fxml")));
        if (state){
            stage = new Stage();
            stage.setScene(scene);
            stage.show();
        }
        else {
            stage = (Stage) context.getScene().getWindow();
            stage.setScene(scene);
            stage.centerOnScreen();
        }
    }

    public void searchCustomerOnAction(ActionEvent actionEvent) {
        try {
            CustomerDto customer = bo.findCustomer(txtEmail.getText());
            if (customer!=null){
                txtName.setText(customer.getName());
                txtSalary.setText(String.valueOf(customer.getSalary()));
                txtContact.setText(customer.getContact());

                fetchLoyaltyCardData(txtEmail.getText());   
            }
            else {
                 new Alert(Alert.AlertType.WARNING, "Can't Find Customer").show();
            }
        } catch (SQLException | ClassNotFoundException e) {
            new Alert(Alert.AlertType.WARNING, "Can't Find Customer").show();
            throw new RuntimeException(e);
        }
    }

    private void fetchLoyaltyCardData(String email) {
        urlNewLoyalty.setText("+ New Loyalty");
        urlNewLoyalty.setVisible(true);
    }

    public void newLoyaltyOnAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        try {
            double salary = Double.parseDouble(txtSalary.getText());

            CardType type = null;
            if (salary>=100000){
                type = CardType.PLATINUM;
            } else if (salary>=50000) {
                type= CardType.GOLD;
            }else {
                type =CardType.SILVER;
            }

            String uniqueData = QrDataGenerator.generate(25);
            //---------------------------Gen QR

            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BufferedImage bufferedImage =
                    MatrixToImageWriter.toBufferedImage(
                            qrCodeWriter.encode(
                                    uniqueData,
                                    BarcodeFormat.QR_CODE,
                                    160, 160
                            )
                    );
            //---------------------------Gen QR

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            javax.imageio.ImageIO.write(bufferedImage, "png",baos);
            byte[] arr = baos.toByteArray();

            if (urlNewLoyalty.getText().equals("+ New Loyalty")){
                boolean isLoyaltyCardAssign = loyaltyCardBo.saveLoyaltyData(
                        new LoyaltyCardDto(new Random().nextInt(10001),type, Base64.encodeBase64String(arr),
                                txtEmail.getText())
                );
                if (isLoyaltyCardAssign){
                    new Alert(Alert.AlertType.CONFIRMATION, "Saved").show();
                    urlNewLoyalty.setText("Show Loyalty Card Info");
                }
                else {
                    new Alert(Alert.AlertType.WARNING, "Try Again Shortly!").show();
                }
            }else {

            }
        } catch (SQLException | ClassNotFoundException e) {
            new Alert(Alert.AlertType.WARNING, "Try Again Shortly!").show();
            throw new RuntimeException(e);
        } catch (WriterException | IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void loadProductOnAction(ActionEvent actionEvent) {
        try {
            ProductDetailJoinDto p = productDetailBo.findProductJoinDetail(
                    txtBarcode.getText()
            );
            if (p!=null){
                txtDescription.setText(p.getDescription());
                txtSellingPrice.setText(String.valueOf(p.getDto().getSellingPrice()));
                txtDiscount.setText(String.valueOf(0));
                txtShowPrice.setText(String.valueOf(p.getDto().getShowPrice()));
                txtQtyOnHand.setText(String.valueOf(p.getDto().getQtyOnHand()));
                txtBuyingPrice.setText(String.valueOf(p.getDto().getBuyingPrice()));
                txtQty.requestFocus();
            }
            else {
                new Alert(Alert.AlertType.WARNING, "Can't Find Product").show();
            }
        } catch (SQLException | ClassNotFoundException e) {
            new Alert(Alert.AlertType.WARNING, "Can't Find Product").show();
            throw new RuntimeException(e);
        }
    }
    ObservableList<CartTm> tms = FXCollections.observableArrayList();
    public void AddToCart(ActionEvent actionEvent) {
        int qty= Integer.parseInt(txtQty.getText());
        double discount = 250;
        double sellingPrice = (Double.parseDouble(txtSellingPrice.getText())-discount);
        double totalCost = qty*sellingPrice;

        CartTm selectedCartTm = isExists(txtBarcode.getText());
        if (selectedCartTm != null){
            selectedCartTm.setQty(qty + selectedCartTm.getQty());
            selectedCartTm.setTotalCost(totalCost + selectedCartTm.getTotalCost());
            tblCart.refresh();
        }
        else {
            Button btn = new Button("Remove");
            CartTm tm = new CartTm(txtBarcode.getText(),txtDescription.getText(),Double.parseDouble(txtDiscount.getText()),
                    sellingPrice,Double.parseDouble(txtShowPrice.getText()),qty,totalCost,btn);
            btn.setOnAction((e) -> {
                tms.remove(tm);
                tblCart.refresh();
                setTotal();
            });

            tms.add(tm);
            clear();
            tblCart.setItems(tms);
            setTotal();
        }
    }

    private void clear() {
        txtDescription.clear();
        txtSellingPrice.clear();
        txtDiscount.clear();
        txtShowPrice.clear();
        txtQtyOnHand.clear();
        txtBuyingPrice.clear();
        txtQty.clear();
        txtBarcode.clear();
        txtBarcode.requestFocus();
    }

    private CartTm isExists(String code){
        for (CartTm tm: tms
             ) {
            if (tm.getCode().equals(code)){
                return tm;
            }
        }
        return null;
    }
    private void setTotal(){
        double total = 0;
        for (CartTm tm: tms
             ) {
            total+=tm.getTotalCost();
        }
        txtTotal.setText((total+ " /="));
    }
}
