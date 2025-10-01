package com.example.customermanagementstoredprocedure.repository;

import com.example.customermanagementstoredprocedure.model.Customer;

public interface ICustomerRepository {
    boolean saveWithStoredProcedure(Customer customer);
}
