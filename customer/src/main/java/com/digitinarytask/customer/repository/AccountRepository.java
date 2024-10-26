package com.digitinarytask.customer.repository;

import com.digitinarytask.customer.domain.entity.Account;
import com.digitinarytask.customer.repository.custom.AccountRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long>, AccountRepositoryCustom {

}
