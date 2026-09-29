package com.sece.expenseclaim.repository;
import com.sece.expenseclaim.entity.ExpenseItem;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ExpenseItemRepository extends JpaRepository<ExpenseItem,Long>{}
