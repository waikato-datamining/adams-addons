/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.example.customerservice.server;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.annotation.Resource;
import javax.xml.ws.WebServiceContext;

import adams.core.License;
import adams.core.annotation.ThirdPartyCopyright;

import com.example.customerservice.Customer;
import com.example.customerservice.CustomerService;
import com.example.customerservice.CustomerType;
import com.example.customerservice.NoSuchCustomer;
import com.example.customerservice.NoSuchCustomerException;
import com.example.customerservice.flow.CustomerServiceWS;

@ThirdPartyCopyright(
    author = "Apache CXF",
    license = License.APACHE2,
    note = "'WSDL first' example",
    url = "http://cxf.apache.org/docs/sample-projects.html"
)
public class CustomerServiceImpl implements CustomerService {
    
    /**
     * The WebServiceContext can be used to retrieve special attributes like the 
     * user principal. Normally it is not needed
     */
    @Resource
    WebServiceContext wsContext;

    /** the ADAMS owner. */
    protected CustomerServiceWS m_Owner;

    /**
     * Initializes the service.
     */
    public CustomerServiceImpl(CustomerServiceWS owner) {
      super();
      m_Owner = owner;
    }

    public List<Customer> getCustomersByName(String name) throws NoSuchCustomerException {
        if ("None".equals(name)) {
            NoSuchCustomer noSuchCustomer = new NoSuchCustomer();
            noSuchCustomer.setCustomerName(name);
            m_Owner.log("Did not find any matching customer for name=" + name, name);
            return new ArrayList<Customer>();
        }

        List<Customer> customers = new ArrayList<Customer>();
        for (int c = 0; c < 3; c++) {
            Customer cust = new Customer();
            cust.setCustomerId(c);
            cust.setName(name);
            cust.getAddress().add("Pine Street " + (200 + c));
            Date bDate = new GregorianCalendar(2009, 01, 01 + c).getTime();
            cust.setBirthDate(bDate);
            cust.setNumOrders(1);
            cust.setRevenue(10000);
            cust.setTest(new BigDecimal(1.5));
            cust.setType(CustomerType.BUSINESS);
            customers.add(cust);
        }

        return customers;
    }

    public void updateCustomer(Customer customer) {
        m_Owner.log("update request was received", customer.getName());
        System.out.println("update request was received");
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            // Nothing to do here
        }
        m_Owner.log("Customer was updated", customer.getName());
    }

}
