package com.maruhxn.lossion.domain.topic.dao;

import com.maruhxn.lossion.domain.topic.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Boolean existsByName(String name);
}
