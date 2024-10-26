package com.digitinarytask.customer.repository;

import com.digitinarytask.customer.domain.entity.Address;
import com.digitinarytask.customer.repository.custom.AddressRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long>, AddressRepositoryCustom {
}
