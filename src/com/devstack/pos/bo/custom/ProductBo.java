package com.devstack.pos.bo.custom;

import com.devstack.pos.bo.SuperBo;
import com.devstack.pos.dao.custom.ProductDao;
import com.devstack.pos.dto.CustomerDto;
import com.devstack.pos.dto.ProductDto;

import java.sql.SQLException;
import java.util.List;

public interface ProductBo extends SuperBo {
    public boolean saveProduct(ProductDto dto) throws SQLException, ClassNotFoundException;
    public boolean updateProduct(CustomerDto dto);
    public boolean deleteProduct(int code);
    public boolean findProduct(int code);
    public List<ProductDto> findAllProducts() throws SQLException, ClassNotFoundException;
    public int getLastProductId() throws SQLException, ClassNotFoundException;
}
