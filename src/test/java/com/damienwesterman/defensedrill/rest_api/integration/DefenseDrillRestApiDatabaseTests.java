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

package com.damienwesterman.defensedrill.rest_api.integration;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.transaction.TransactionSystemException;

import com.damienwesterman.defensedrill.rest_api.entity.CategoryEntity;
import com.damienwesterman.defensedrill.rest_api.entity.DrillEntity;
import com.damienwesterman.defensedrill.rest_api.entity.InstructionsEntity;
import com.damienwesterman.defensedrill.rest_api.entity.SubCategoryEntity;
import com.damienwesterman.defensedrill.rest_api.repository.CategoryRepo;
import com.damienwesterman.defensedrill.rest_api.repository.DrillRepo;
import com.damienwesterman.defensedrill.rest_api.repository.InstructionsRepo;
import com.damienwesterman.defensedrill.rest_api.repository.SubCategoryRepo;

import jakarta.validation.ConstraintViolationException;

@SpringBootTest
public class DefenseDrillRestApiDatabaseTests {
    @Autowired
    private DrillRepo drillRepo;
    // Will test categoryRepo and SubCategoryRepo simultaneously because they are the same
    @Autowired
    private CategoryRepo categoryRepo;
    @Autowired
    private SubCategoryRepo subCategoryRepo;
    @Autowired
    private InstructionsRepo instructionsRepo;

    /*
     * A few of the tests (like testing constraints) are only tested for creating new entities.
     * We are assuming the constraints are set up properly if they passed and will not test the
     * updating existing entities.
     */

    @BeforeEach
    public void setup() {
        // Clear the database, this is assumed to work fine
        drillRepo.deleteAll();
        categoryRepo.deleteAll();
        subCategoryRepo.deleteAll();
        instructionsRepo.deleteAll();
    }

    @Test
    public void test_drillRepo_queryDbWorksWithEmptyDb_returns0() {
        assertEquals(0, drillRepo.findAll().size());
    }

    @Test
    public void test_categoryRepos_queryDbWorksWithEmptyDb_returns0() {
        assertEquals(0, categoryRepo.findAll().size());
        assertEquals(0, subCategoryRepo.findAll().size());
    }

    @Test
    public void test_instructionsRepo_queryDbWorksWithEmptyDb_returns0() {
        assertEquals(0, instructionsRepo.findAll().size());
    }

    @Test
    public void test_drillRepo_save_succeedsWithDrillNameOnly() {
        DrillEntity drill = DrillEntity.builder()
                                .id(null)
                                .name("New Drill")
                                .categories(null)
                                .subCategories(null)
                                .relatedDrills(null)
                                .instructions(null)
                                .build();

        DrillEntity returnedDrill = drillRepo.save(drill);

        assertEquals(1, drillRepo.findAll().size());
        assertEquals(0, returnedDrill.getName().compareTo(drill.getName()));
    }

    @Test
    public void test_categoryRepos_save_succeedsWithNameAndDescription() {
        // CategoryEntity
        CategoryEntity category = CategoryEntity.builder()
                                    .id(null)
                                    .name("New Category Name")
                                    .description("New Category Description")
                                    .build();

        CategoryEntity returnedCategory = categoryRepo.save(category);

        assertEquals(1, categoryRepo.findAll().size());
        assertEquals(0, returnedCategory.getName().compareTo(category.getName()));

        // SubCategoryEntity
        SubCategoryEntity subCategory = SubCategoryEntity.builder()
                                            .id(null)
                                            .name("New SubCategory Name")
                                            .description("New SubCategory Description")
                                            .build();

        SubCategoryEntity returnedSubCategory = subCategoryRepo.save(subCategory);

        assertEquals(1, subCategoryRepo.findAll().size());
        assertEquals(0, returnedSubCategory.getName().compareTo(subCategory.getName()));
    }

    @Test
    public void test_instructionsRepo_save_succeedsWithNecessaryFields() {
        // Need to have an existing drill
        DrillEntity drill = DrillEntity.builder()
                                .id(null)
                                .name("New Drill")
                                .categories(null)
                                .subCategories(null)
                                .relatedDrills(null)
                                .instructions(null)
                                .build();
        Long drillId = drillRepo.save(drill).getId();
        InstructionsEntity instructions = InstructionsEntity.builder()
                                            .drillId(drillId)
                                            .number(0L)
                                            .description("Instructions Description")
                                            .steps("Step1|Step2|Step3")
                                            .videoId(null)
                                            .build();

        InstructionsEntity returnedInstructions = instructionsRepo.save(instructions);

        assertEquals(1, instructionsRepo.findAll().size());
        assertEquals(0, returnedInstructions.getDescription().compareTo(instructions.getDescription()));
    }

    @Test
    public void test_categoryRepos_save_emptyFields_fails() {
        // CategoryEntity
        CategoryEntity category = CategoryEntity.builder()
                                    .id(null)
                                    .name(null)
                                    .description("New Category Description")
                                    .build();

        // null name
        assertThrows(ConstraintViolationException.class,
            () -> categoryRepo.save(category));

        // Empty name
        category.setName("");
        assertThrows(ConstraintViolationException.class,
        () -> categoryRepo.save(category));

        // null description
        category.setName("New Category Name");
        category.setDescription(null);
        assertThrows(ConstraintViolationException.class,
            () -> categoryRepo.save(category));

        // Empty description
        category.setDescription("");
        assertThrows(ConstraintViolationException.class,
            () -> categoryRepo.save(category));

        // SubCategoryEntity
        SubCategoryEntity subCategory = SubCategoryEntity.builder()
                                            .id(null)
                                            .name(null)
                                            .description("New SubCategory Description")
                                            .build();

        // null name
        assertThrows(ConstraintViolationException.class,
            () -> subCategoryRepo.save(subCategory));

        // Empty name
        subCategory.setName("");
        assertThrows(ConstraintViolationException.class,
        () -> subCategoryRepo.save(subCategory));

        // null description
        subCategory.setName("New SubCategory Name");
        subCategory.setDescription(null);
        assertThrows(ConstraintViolationException.class,
            () -> subCategoryRepo.save(subCategory));

        // Empty description
        subCategory.setDescription("");
        assertThrows(ConstraintViolationException.class,
            () -> subCategoryRepo.save(subCategory));
    }

    @Test
    public void test_categoryRepos_save_nonUniqueName_fails() {
        String sameName = "Same Name";
        // CategoryEntity
        CategoryEntity category = CategoryEntity.builder()
                                    .id(null)
                                    .name(sameName)
                                    .description("New Category Description")
                                    .build();
        assertDoesNotThrow(() -> categoryRepo.save(category));

        CategoryEntity duplicateCategory = CategoryEntity.builder()
                                    .id(null)
                                    .name(sameName)
                                    .description("New Category Description")
                                    .build();
        assertThrows(DataIntegrityViolationException.class,
            () -> categoryRepo.save(duplicateCategory));

        // SubCategoryEntity
        SubCategoryEntity subCategory = SubCategoryEntity.builder()
                                            .id(null)
                                            .name(sameName)
                                            .description("New SubCategory Description")
                                            .build();

        assertDoesNotThrow(() -> subCategoryRepo.save(subCategory));
        SubCategoryEntity duplicateSubCategory = SubCategoryEntity.builder()
                                            .id(null)
                                            .name(sameName)
                                            .description("New SubCategory Description")
                                            .build();
        assertThrows(DataIntegrityViolationException.class,
            () -> subCategoryRepo.save(duplicateSubCategory));
    }

    @Test
    public void test_instructionsRepo_save_succeedsWithSameDrillIdDifferentNumber() {
        // Need to have an existing drill
        DrillEntity drill = DrillEntity.builder()
                                .id(null)
                                .name("New Drill")
                                .categories(null)
                                .subCategories(null)
                                .relatedDrills(null)
                                .instructions(null)
                                .build();
        Long drillId = drillRepo.save(drill).getId();
        InstructionsEntity instructions1 = InstructionsEntity.builder()
                                            .drillId(drillId)
                                            .number(0L)
                                            .description("Instructions Description")
                                            .steps("Step1|Step2|Step3")
                                            .videoId(null)
                                            .build();
        InstructionsEntity instructions2 = InstructionsEntity.builder()
                                            .drillId(drillId)
                                            .number(1L)
                                            .description("Instructions Description")
                                            .steps("Step1|Step2|Step3")
                                            .videoId(null)
                                            .build();

        assertDoesNotThrow(() -> instructionsRepo.save(instructions1));
        assertDoesNotThrow(() -> instructionsRepo.save(instructions2));
        assertEquals(2, instructionsRepo.findAll().size());
    }

    @Test
    public void test_instructionsRepo_save_emptyFields_fails() {
        // Need to have an existing drill
        DrillEntity drill = DrillEntity.builder()
                                .id(null)
                                .name("New Drill")
                                .categories(null)
                                .subCategories(null)
                                .relatedDrills(null)
                                .instructions(null)
                                .build();
        Long drillId = drillRepo.save(drill).getId();
        InstructionsEntity instructions = InstructionsEntity.builder()
                                            .drillId(drillId)
                                            .number(0L)
                                            .description("Instructions Description")
                                            .steps("Step1|Step2|Step3")
                                            .videoId(null)
                                            .build();

        /*
         * Due to the nature of the composite key @IdClass, we will get TransactionSystemException
         * rather than ConstraintViolationException.
         */

        // Empty drill
        instructions.setDrillId(null);
        assertThrows(TransactionSystemException.class,
            () -> instructionsRepo.save(instructions));

        // TODO: uncomment?
        // // Empty number
        // instructions.setDrillId(drillId);
        // instructions.setNumber(null);
        // assertThrows(ConstraintViolationException.class,
        //     () -> instructionsRepo.save(instructions));

        // Empty description
        instructions.setNumber(1L);
        instructions.setDescription(null);
        assertThrows(TransactionSystemException.class,
            () -> instructionsRepo.save(instructions));
        instructions.setDescription("");
        assertThrows(TransactionSystemException.class,
            () -> instructionsRepo.save(instructions));

        // Empty steps
        instructions.setDescription("Not null");
        instructions.setSteps(null);
        assertThrows(TransactionSystemException.class,
            () -> instructionsRepo.save(instructions));
        instructions.setSteps("");
        assertThrows(TransactionSystemException.class,
            () -> instructionsRepo.save(instructions));
    }

    @Test
    public void test_instructionsRepo_save_nonExistentDrill_fails() {
        // Need to have an existing drill
        DrillEntity drill = DrillEntity.builder()
                                .id(null)
                                .name("New Drill")
                                .categories(null)
                                .subCategories(null)
                                .relatedDrills(null)
                                .instructions(null)
                                .build();
        Long drillId = drillRepo.save(drill).getId();
        Long badDrillId = drillId + 1;
        InstructionsEntity instructions = InstructionsEntity.builder()
                                            .drillId(badDrillId)
                                            .number(0L)
                                            .description("Instructions Description")
                                            .steps("Step1|Step2|Step3")
                                            .videoId(null)
                                            .build();

        assertThrows(DataIntegrityViolationException.class,
            () -> instructionsRepo.save(instructions));
    }

    /*
    // This one would pass because having the same drillid and number just equates to the same entity
    // since that is the composit key
    @Test
    public void test_instructionsRepo_save_duplicateDrillidAndNumber_fails()
    */

    @Test
    public void test_drillRepo_save_emptyName_fails() {
        DrillEntity drill = DrillEntity.builder()
                                .id(null)
                                .name(null)
                                .categories(null)
                                .subCategories(null)
                                .relatedDrills(null)
                                .instructions(null)
                                .build();

        // Null name
        assertThrows(ConstraintViolationException.class,
            () -> drillRepo.save(drill));

        // Empty name
        drill.setName("");
        assertThrows(ConstraintViolationException.class,
        () -> drillRepo.save(drill));
    }

    @Test
    public void test_drillRepo_save_duplicateName_fails() {
        String duplicateName = "New Drill";

        DrillEntity drill1 = DrillEntity.builder()
                                .id(null)
                                .name(duplicateName)
                                .categories(null)
                                .subCategories(null)
                                .relatedDrills(null)
                                .instructions(null)
                                .build();
        assertDoesNotThrow(() -> drillRepo.save(drill1));

        DrillEntity drill2 = DrillEntity.builder()
                                .id(null)
                                .name(duplicateName)
                                .categories(null)
                                .subCategories(null)
                                .relatedDrills(null)
                                .instructions(null)
                                .build();
        assertThrows(DataIntegrityViolationException.class, 
            () -> drillRepo.save(drill2));
    }

    @Test
    public void test_drillRepo_save_saveWithExistingCategories_succeeds() {
        // CategoryEntity
        CategoryEntity category = CategoryEntity.builder()
                                    .id(null)
                                    .name("New Category Name")
                                    .description("New Category Description")
                                    .build();
        CategoryEntity returnedCategory = categoryRepo.save(category);

        // SubCategoryEntity
        SubCategoryEntity subCategory = SubCategoryEntity.builder()
                                            .id(null)
                                            .name("New SubCategory Name")
                                            .description("New SubCategory Description")
                                            .build();
        SubCategoryEntity returnedSubCategory = subCategoryRepo.save(subCategory);

        DrillEntity drill = DrillEntity.builder()
                                .id(null)
                                .name("New Drill")
                                .categories(List.of(returnedCategory))
                                .subCategories(List.of(returnedSubCategory))
                                .relatedDrills(null)
                                .instructions(null)
                                .build();

        DrillEntity returnedDrill = drillRepo.save(drill);

        assertEquals(1, drillRepo.findAll().size());
        assertEquals(1, returnedDrill.getCategories().size());
        assertEquals(1, returnedDrill.getSubCategories().size());
    }

    @Test
    public void test_drillRepo_save_nonExistentCategories_fails() {
        // CategoryEntity
        CategoryEntity category = CategoryEntity.builder()
                                    .id(null)
                                    .name("New Category Name")
                                    .description("New Category Description")
                                    .build();

        // SubCategoryEntity
        SubCategoryEntity subCategory = SubCategoryEntity.builder()
                                            .id(null)
                                            .name("New SubCategory Name")
                                            .description("New SubCategory Description")
                                            .build();

        DrillEntity drill = DrillEntity.builder()
                                .id(null)
                                .name("New Drill")
                                .categories(null)
                                .subCategories(null)
                                .relatedDrills(null)
                                .instructions(null)
                                .build();

        // Non existent category
        drill.setCategories(List.of(category));
        assertThrows(InvalidDataAccessApiUsageException.class,
            () -> drillRepo.save(drill));

        // Non existent sub category
        drill.setCategories(null);
        drill.setSubCategories(List.of(subCategory));
        assertThrows(InvalidDataAccessApiUsageException.class,
            () -> drillRepo.save(drill));
    }

    @Test
    public void test_drillRepo_save_updateWithNewCategories_succeeds() {
        // TODO: FINISH ME
    }

    /*
    // Yeah this wouldn't work because Instructions can't exist without a Drill
    @Test
    public void test_drillRepo_save_saveWithExistingInstructions_succeeds()

    // This one won't work because the drill needs to exist first, can't do it simultaneously
    @Test
    public void test_drillRepo_save_saveWithNonExistentInstructions_succeeds()
    */

    @Test
    public void test_drillRepo_save_updateWithMoreInstructions_succeeds() {
        DrillEntity drill = DrillEntity.builder()
                                .id(null)
                                .name("New Drill")
                                .categories(null)
                                .subCategories(null)
                                .relatedDrills(null)
                                .instructions(null)
                                .build();
        Long drillId = drillRepo.save(drill).getId();
        InstructionsEntity instructions = InstructionsEntity.builder()
                                            .drillId(drillId)
                                            .number(0L)
                                            .description("Instructions Description")
                                            .steps("Step1|Step2|Step3")
                                            .videoId(null)
                                            .build();

        drill.setInstructions(new ArrayList<>());
        drill.getInstructions().add(instructions);
        DrillEntity returnedEntity = drillRepo.save(drill);

        assertEquals(1, returnedEntity.getInstructions().size());
    }

    @Test
    public void test_drillRepo_save_updateWithLessInstructions_succeeds() {
        DrillEntity drill = DrillEntity.builder()
                                .id(null)
                                .name("New Drill")
                                .categories(null)
                                .subCategories(null)
                                .relatedDrills(null)
                                .instructions(null)
                                .build();
        Long drillId = drillRepo.save(drill).getId();
        InstructionsEntity instructions1 = InstructionsEntity.builder()
                                            .drillId(drillId)
                                            .number(0L)
                                            .description("Instructions Description 1")
                                            .steps("Step1|Step2|Step3")
                                            .videoId(null)
                                            .build();
        InstructionsEntity instructions2 = InstructionsEntity.builder()
                                            .drillId(drillId)
                                            .number(1L)
                                            .description("Instructions Description 2")
                                            .steps("Step1|Step2|Step3")
                                            .videoId(null)
                                            .build();

        instructionsRepo.save(instructions1);
        instructionsRepo.save(instructions2);
        DrillEntity returnedEntity = drillRepo.findById(drillId).get();

        assertEquals(2, returnedEntity.getInstructions().size());

        returnedEntity.getInstructions().remove(instructions2);
        DrillEntity returnedEntity2 = drillRepo.save(returnedEntity);

        assertEquals(1, returnedEntity2.getInstructions().size());
        assertEquals(1, instructionsRepo.findAll().size());
    }

    @Test
    public void test_drillRepo_delete_deleteDrill_doesNotDeleteCategories() {
        // TODO: FINISH ME
    }

    @Test
    public void test_drillRepo_delete_deleteDrill_doesDeleteInstructions() {
        // TODO: FINISH ME
    }

    // TODO: Test the find methods, make sure findByNameIgnoreCase exists too in all relevant repos
}
