package com.devstack.pos.dao.custom;

import com.devstack.pos.dao.CrudDao;
import com.devstack.pos.entity.Customer;

import java.sql.SQLException;
import java.util.List;

public interface CustomerDao extends CrudDao<Customer, String>{

    public List<Customer> searchCustomer(String searchText) throws SQLException, ClassNotFoundException;

    }
