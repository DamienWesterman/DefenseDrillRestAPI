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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.transaction.TransactionSystemException;

import com.damienwesterman.defensedrill.rest_api.entity.DrillEntity;
import com.damienwesterman.defensedrill.rest_api.entity.InstructionsEntity;
import com.damienwesterman.defensedrill.rest_api.exception.DatabaseInsertException;
import com.damienwesterman.defensedrill.rest_api.repository.DrillRepo;
import com.damienwesterman.defensedrill.rest_api.service.DrillService;

import jakarta.validation.ConstraintViolationException;

@ExtendWith(MockitoExtension.class)
public class DrillServiceTest {
    @Mock
    private DrillRepo repo;
    @Mock
    private DrillEntity drill;
    @Mock
    private InstructionsEntity instructions1;
    @Mock
    private InstructionsEntity instructions2;

    private DrillService service;

    @BeforeEach
    public void setup() {
        service = new DrillService(repo);
    }

    // Save serves as both a create and update
    @Test
    public void test_save_createWithNoInstructions_callsRepoOnce() {
        when(drill.getInstructions()).thenReturn(List.of());
        assertDoesNotThrow(() -> service.save(drill));
        verify(repo, times(1)).save(drill);
    }

    @Test
    public void test_save_createWithOneInstructions_callsRepoTwice() {
        List<InstructionsEntity> instructions = new ArrayList<InstructionsEntity>(List.of(instructions1));
        when(drill.getInstructions()).thenReturn(instructions);
        when(repo.save(drill)).thenReturn(drill);
        assertDoesNotThrow(() -> service.save(drill));
        verify(repo, times(2)).save(drill);
    }

    @Test
    public void test_save_createWithTwoInstructions_callsRepoTwice() {
        List<InstructionsEntity> instructions = new ArrayList<InstructionsEntity>(List.of(instructions1, instructions2));
        when(drill.getInstructions()).thenReturn(instructions);
        when(repo.save(drill)).thenReturn(drill);
        assertDoesNotThrow(() -> service.save(drill));
        verify(repo, times(2)).save(drill);
    }

    @Test
    public void test_save_givenBadEntityConstraintViolation_ThrowsException() {
        // Repos throw ConstraintViolationException when DAO constraint violations are detected
        when(repo.save(drill)).thenThrow(new ConstraintViolationException("ConstraintViolationImpl{interpolatedMessage='must not be empty', propertyPath=name, rootBeanClass=class", null));
        assertThrows(DatabaseInsertException.class, () -> service.save(drill));
    }

    @Test
    public void test_save_givenBadEntityNonUnique_ThrowsException() {
        // Repos throw DataIntegrityViolationException when db detects constraint violations
        when(repo.save(drill)).thenThrow(new DataIntegrityViolationException("constraint_drills_unique_name"));
        assertThrows(DatabaseInsertException.class, () -> service.save(drill));
    }

    @Test
    public void test_save_givenBadInstructionEntity_ThrowsVioaltion() {
        // Repos throw TransactionSystemException when DAO constraint violations are detected in an Instructions insert
        when(repo.save(drill)).thenThrow(new TransactionSystemException("ConstraintViolationImpl{interpolatedMessage='must not be empty', propertyPath=name, rootBeanClass=class"));
        assertThrows(DatabaseInsertException.class, () -> service.save(drill));

        // Repos throw DataIntegrityViolationException when db detects constraint violations - namely foreign key exception for Instructions. Tested elsewhere
    }

    @Test
    public void test_save_givenNonExistentCategories_ThrowsViolation() {
        // Repos throw InvalidDataAccessApiUsageException when given an unsaved internal entity
        when(repo.save(drill)).thenThrow(new InvalidDataAccessApiUsageException("org.springframework.dao.InvalidDataAccessApiUsageException: org.hibernate.TransientObjectException: object references an unsaved transient instance - save the transient instance before flushing: com.damienwesterman.defensedrill.rest_api.entity.CategoryEntity", null));
        assertThrows(DatabaseInsertException.class, () -> service.save(drill));
    }

    @Test
    public void test_find_byId_callsRepoFindById() {
        when(repo.findById(0L)).thenReturn(Optional.of(drill));
        assertEquals(drill, service.find(0L).get());
        verify(repo, times(1)).findById(0L);
    }

    @Test
    public void test_find_byName_callsRepoFindByName() {
        String name = "NAME";
        when(repo.findByNameIgnoreCase(name)).thenReturn(Optional.of(drill));
        assertEquals(drill, service.find(name).get());
        verify(repo, times(1)).findByNameIgnoreCase(name);
    }

    @Test
    public void test_findAll_callsRepoFindAll() {
        List<DrillEntity> drills = List.of(drill);
        when(repo.findAll()).thenReturn(drills);
        assertEquals(drills, service.findAll());
        verify(repo, times(1)).findAll();
    }

    @Test
    public void test_delete_callsDeleteById() {
        service.delete(0L);
        verify(repo, times(1)).deleteById(0L);
    }
}
