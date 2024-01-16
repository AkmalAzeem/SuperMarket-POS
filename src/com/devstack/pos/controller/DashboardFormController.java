package com.devstack.pos.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class DashboardFormController {

    public AnchorPane context;

    public void btnCustomerOnAction(ActionEvent actionEvent) throws IOException {
        setUi("CustomerForm");
    }

    public void btnOrderDetailsOnAction(ActionEvent actionEvent) throws IOException {
        setUi("PlaceOrderForm");
    }

    public void btnIncomeReportOnAction(ActionEvent actionEvent) {
    }

    public void btnProductOnAction(ActionEvent actionEvent) throws IOException {
        setUi("ProductMainForm");
    }

    public void btnPlaceOrderOnAction(ActionEvent actionEvent) throws IOException {
        setUi("PlaceOrderForm");
    }
    private void setUi(String url) throws IOException {
        Stage stage = (Stage) context.getScene().getWindow();
        stage.setScene(
                new Scene(FXMLLoader.load(getClass().getResource("../view/"+url+".fxml")))
        );
        stage.centerOnScreen();
    }
}
