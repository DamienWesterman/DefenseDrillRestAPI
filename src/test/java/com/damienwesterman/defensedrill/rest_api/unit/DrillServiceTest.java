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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.damienwesterman.defensedrill.rest_api.entity.CategoryEntity;
import com.damienwesterman.defensedrill.rest_api.entity.DrillEntity;
import com.damienwesterman.defensedrill.rest_api.entity.InstructionsEntity;
import com.damienwesterman.defensedrill.rest_api.entity.SubCategoryEntity;
import com.damienwesterman.defensedrill.rest_api.repository.DrillRepo;
import com.damienwesterman.defensedrill.rest_api.service.DrillService;

@ExtendWith(MockitoExtension.class)
public class DrillServiceTest {
    @Mock
    private DrillRepo repo;
    @Mock
    private DrillEntity drill1;
    @Mock
    private DrillEntity drill2;
    @Mock
    private InstructionsEntity instructions1;
    @Mock
    private InstructionsEntity instructions2;
    @Mock
    private CategoryEntity category;
    @Mock
    private SubCategoryEntity subCategory;

    private DrillService service;

    @BeforeEach
    public void setup() {
        service = new DrillService(repo);
    }

    // Save serves as both a create and update
    @Test
    public void test_save_createWithNoInstructions_callsRepoOnce() {
        // TODO: FINISH ME
    }

    @Test
    public void test_save_createWithInstructions_callsRepoTwice() {
        // TODO: FINISH ME
    }

    @Test
    public void test_save_givenBadEntityConstraintViolation_ThrowsException() {
        // TODO: FINISH ME
    }

    @Test
    public void test_save_givenBadEntityNonUnique_ThrowsException() {
        // TODO: FINISH ME
    }

    @Test
    public void test_save_givenBadInstructionEntity_ThrowsVioaltion() {
        // TODO: FINISH ME
    }

    @Test
    public void test_save_givenNonExistentCategories_ThrowsViolation() {
        // TODO: FINISH ME InvalidDataAccessApiUsageException for non existent categories
    }

    @Test
    public void test_save_updateWithNoInstructions_callsRepoOnce() {
        // TODO: FINISH ME
    }

    @Test
    public void test_save_updateWithInstructions_callsRepoOnce() {
        // TODO: FINISH ME
    }

    // TODO: tests - find, delete
}
