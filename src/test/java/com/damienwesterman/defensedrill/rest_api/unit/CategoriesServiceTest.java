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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import com.damienwesterman.defensedrill.rest_api.entity.CategoryEntity;
import com.damienwesterman.defensedrill.rest_api.entity.SubCategoryEntity;
import com.damienwesterman.defensedrill.rest_api.exception.DatabaseInsertException;
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

    // Save serves as both a create and update
    @Test
    public void test_save_usesCorrectRepoDependingOnEntityType() {
        assertDoesNotThrow(() -> service.save(categoryEntity));
        verify(categoryRepo, times(1)).save(categoryEntity);

        assertDoesNotThrow(() -> service.save(subCategoryEntity));
        verify(subCategoryRepo, times(1)).save(subCategoryEntity);
    }

    @Test
    public void test_save_givenBadEntityConstraintViolation_ThrowsException() {
        // Repos throw ConstraintViolationException when DAO constraint violations are detected
        when(categoryRepo.save(categoryEntity)).thenThrow(new ConstraintViolationException("ConstraintViolationImpl{interpolatedMessage='must not be empty', propertyPath=name, rootBeanClass=class", null));
        assertThrows(DatabaseInsertException.class, () -> service.save(categoryEntity));

        when(subCategoryRepo.save(subCategoryEntity)).thenThrow(new ConstraintViolationException("ConstraintViolationImpl{interpolatedMessage='must not be empty', propertyPath=name, rootBeanClass=class", null));
        assertThrows(DatabaseInsertException.class, () -> service.save(subCategoryEntity));
    }

    @Test
    public void test_save_givenBadEntityNonUnique_throwsException() {
        // Repos throw DataIntegrityViolationException when db detects constraint violations
        when(categoryRepo.save(categoryEntity)).thenThrow(new DataIntegrityViolationException("constraint_drills_unique_name"));
        assertThrows(DatabaseInsertException.class, () -> service.save(categoryEntity));

        when(subCategoryRepo.save(subCategoryEntity)).thenThrow(new DataIntegrityViolationException("constraint_drills_unique_name"));
        assertThrows(DatabaseInsertException.class, () -> service.save(subCategoryEntity));
    }

    @Test
    public void test_find_byId_callsCorrectRepoFind() {
        when(categoryRepo.findById(0L)).thenReturn(Optional.of(categoryEntity));
        assertEquals(categoryEntity, service.findCategory(0L).get());
        verify(categoryRepo, times(1)).findById(0L);

        when(subCategoryRepo.findById(0L)).thenReturn(Optional.of(subCategoryEntity));
        assertEquals(subCategoryEntity, service.findSubCategory(0L).get());
        verify(subCategoryRepo, times(1)).findById(0L);
    }

    @Test
    public void test_find_byName_callsCorrectRepoFind() {
        String name = "NAME";
        when(categoryRepo.findByNameIgnoreCase(name)).thenReturn(Optional.of(categoryEntity));
        assertEquals(categoryEntity, service.findCategory(name).get());
        verify(categoryRepo, times(1)).findByNameIgnoreCase(name);

        when(subCategoryRepo.findByNameIgnoreCase(name)).thenReturn(Optional.of(subCategoryEntity));
        assertEquals(subCategoryEntity, service.findSubCategory(name).get());
        verify(subCategoryRepo, times(1)).findByNameIgnoreCase(name);
    }

    @Test
    public void test_findAll_callsCorrectRepoFind() {
        List<CategoryEntity> categoryList = List.of(categoryEntity);
        when(categoryRepo.findAll()).thenReturn(categoryList);
        assertEquals(categoryList, service.findAllCategories());
        verify(categoryRepo, times(1)).findAll();

        List<SubCategoryEntity> subCategoryList = List.of(subCategoryEntity);
        when(subCategoryRepo.findAll()).thenReturn(subCategoryList);
        assertEquals(subCategoryList, service.findAllSubCategories());
        verify(subCategoryRepo, times(1)).findAll();
    }

    // TODO: Test all parts of the CRUD/repo functionalities
}
