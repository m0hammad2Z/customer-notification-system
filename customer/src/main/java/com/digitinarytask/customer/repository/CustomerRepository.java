package com.digitinarytask.customer.repository;

import com.digitinarytask.customer.domain.entity.Customer;
import com.digitinarytask.customer.repository.custom.CustomerRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long>, CustomerRepositoryCustom {

}
