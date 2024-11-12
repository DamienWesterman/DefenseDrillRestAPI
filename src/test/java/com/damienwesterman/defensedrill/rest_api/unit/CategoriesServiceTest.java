/****************************\
 *      ________________      *
 *     /  _             \     *
 *     \   \ |\   _  \  /     *
 *      \  / | \ / \  \/      *
 *      /  \ | / | /  /\      *
 *     /  _/ |/  \__ /  \     *
 *     \________________/     *
 *                            *
 \****************************/
/*
 * Copyright 2024 Damien Westerman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.damienwesterman.defensedrill.rest_api.unit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import com.damienwesterman.defensedrill.rest_api.entity.CategoryEntity;
import com.damienwesterman.defensedrill.rest_api.entity.SubCategoryEntity;
import com.damienwesterman.defensedrill.rest_api.repository.CategoryRepo;
import com.damienwesterman.defensedrill.rest_api.repository.SubCategoryRepo;
import com.damienwesterman.defensedrill.rest_api.service.CategoriesService;

import jakarta.validation.ConstraintViolationException;

@ExtendWith(MockitoExtension.class)
public class CategoriesServiceTest {
    @Mock
    private CategoryRepo categoryRepo;
    @Mock
    private SubCategoryRepo subCategoryRepo;
    @Mock
    private CategoryEntity categoryEntity;
    @Mock
    private SubCategoryEntity subCategoryEntity;

    private CategoriesService service;

    @BeforeEach
    public void setup() {
        service = new CategoriesService(categoryRepo, subCategoryRepo);
    }

    // TODO: Change implementation to return void and throw new specific exception
    @Test
    public void test_save_usesCorrectRepoDependingOnEntityType() {
        assertTrue(service.save(categoryEntity));
        verify(categoryRepo, times(1)).save(categoryEntity);

        assertTrue(service.save(subCategoryEntity));
        verify(subCategoryRepo, times(1)).save(subCategoryEntity);
    }

    @Test
    public void test_save_givenBadEntityConstraintViolation_returnsFalse() {
        // Repos throw ConstraintViolationException when DAO constraint violations are detected
        when(categoryRepo.save(categoryEntity)).thenThrow(ConstraintViolationException.class);
        assertFalse(service.save(categoryEntity));

        when(subCategoryRepo.save(subCategoryEntity)).thenThrow(ConstraintViolationException.class);
        assertFalse(service.save(subCategoryEntity));
    }

    @Test
    public void test_save_givenBadEntityNonUnique_returnsFalse() {
        // Repos throw DataIntegrityViolationException when db detects constraint violations
        when(categoryRepo.save(categoryEntity)).thenThrow(DataIntegrityViolationException.class);
        assertFalse(service.save(categoryEntity));

        when(subCategoryRepo.save(subCategoryEntity)).thenThrow(DataIntegrityViolationException.class);
        assertFalse(service.save(subCategoryEntity));
    }
}
