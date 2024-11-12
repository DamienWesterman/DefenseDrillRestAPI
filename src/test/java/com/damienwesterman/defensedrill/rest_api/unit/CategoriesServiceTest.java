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
