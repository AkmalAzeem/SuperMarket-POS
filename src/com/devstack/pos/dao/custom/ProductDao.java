package com.devstack.pos.dao.custom;

import com.devstack.pos.dao.CrudDao;
import com.devstack.pos.entity.Product;

import java.sql.SQLException;

public interface ProductDao extends CrudDao<Product, Integer> {

    public int getLastProductId() throws SQLException, ClassNotFoundException;
}
