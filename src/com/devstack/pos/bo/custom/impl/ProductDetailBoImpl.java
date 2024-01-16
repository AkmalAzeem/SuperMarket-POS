package com.devstack.pos.bo.custom.impl;

import com.devstack.pos.bo.custom.ProductDetailBo;
import com.devstack.pos.dao.DaoFactory;
import com.devstack.pos.dao.custom.ProductDetailDao;
import com.devstack.pos.dto.ProductDetailDto;
import com.devstack.pos.dto.ProductDetailJoinDto;
import com.devstack.pos.dto.ProductDto;
import com.devstack.pos.entity.ProductDetail;
import com.devstack.pos.enums.DaoType;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductDetailBoImpl implements ProductDetailBo {
    ProductDetailDao dao = DaoFactory.getInstance().getDao(DaoType.PRODUCT_DETAIL);
    @Override
    public boolean saveProductDetail(ProductDetailDto dto) throws SQLException, ClassNotFoundException {
        return dao.save(
                new ProductDetail(
                        dto.getCode(), dto.getBarcode(), dto.getQtyOnHand(), dto.getSellingPrice(), dto.getBuyingPrice(),
                        dto.isDiscountAvailability(), dto.getShowPrice(), dto.getProductCode()
                )
        );
    }

    @Override
    public List<ProductDetailDto> findAllProductDetails(int productCode) throws SQLException, ClassNotFoundException {
        List<ProductDetailDto> dtos = new ArrayList<>();
        for (ProductDetail d :dao.findAllProductDetails(productCode)
             ) {
            dtos.add(
                    new ProductDetailDto(
                            d.getCode(),d.getBarcode(), d.getQtyOnHand(),d.getSellingPrice(),d.getBuyingPrice(),d.isDiscountAvailability()
                            ,d.getShowPrice(),d.getProductCode()
                    )
            );

        }
        return dtos;
    }

    @Override
    public ProductDetailDto findProductDetails(String code) throws SQLException, ClassNotFoundException {
        ProductDetail d = dao.findProductDetails(code);
        if (d!=null){
            return new ProductDetailDto(
                    d.getCode(),d.getBarcode(), d.getQtyOnHand(),d.getSellingPrice(),d.getBuyingPrice(),d.isDiscountAvailability()
                    ,d.getShowPrice(),d.getProductCode()
            );
        }
        return null;
    }

    @Override
    public ProductDetailJoinDto findProductJoinDetail(String code) throws SQLException, ClassNotFoundException {
        return dao.findProductDetailJoinData(code);
    }
}
