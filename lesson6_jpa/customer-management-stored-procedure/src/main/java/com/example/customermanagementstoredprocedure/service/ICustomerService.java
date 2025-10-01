package com.example.customermanagementstoredprocedure.service;

import com.example.customermanagementstoredprocedure.model.Customer;

public interface ICustomerService {
    boolean saveWithStoredProcedure(Customer customer);
}
