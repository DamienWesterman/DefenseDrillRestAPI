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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

@SuppressWarnings("null")
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
        assertEquals(0, drillRepo.count());
    }

    @Test
    public void test_categoryRepos_queryDbWorksWithEmptyDb_returns0() {
        assertEquals(0, categoryRepo.count());
        assertEquals(0, subCategoryRepo.count());
    }

    @Test
    public void test_instructionsRepo_queryDbWorksWithEmptyDb_returns0() {
        assertEquals(0, instructionsRepo.count());
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

        assertEquals(1, drillRepo.count());
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

        assertEquals(1, categoryRepo.count());
        assertEquals(0, returnedCategory.getName().compareTo(category.getName()));

        // SubCategoryEntity
        SubCategoryEntity subCategory = SubCategoryEntity.builder()
                                            .id(null)
                                            .name("New SubCategory Name")
                                            .description("New SubCategory Description")
                                            .build();

        SubCategoryEntity returnedSubCategory = subCategoryRepo.save(subCategory);

        assertEquals(1, subCategoryRepo.count());
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

        assertEquals(1, instructionsRepo.count());
        assertEquals(0, returnedInstructions.getDescription().compareTo(instructions.getDescription()));
    }

    @Test
    public void test_drillRepo_findByNameAndFindById_succeedsWithExistingDrill() {
        String drillName = "NEW DRILL";
        DrillEntity drill = DrillEntity.builder()
                                .id(null)
                                .name(drillName)
                                .categories(null)
                                .subCategories(null)
                                .relatedDrills(null)
                                .instructions(null)
                                .build();

        DrillEntity savedDrill = drillRepo.save(drill);

        assertTrue(drillRepo.findById(savedDrill.getId()).isPresent());
        assertTrue(drillRepo.findByNameIgnoreCase(savedDrill.getName()).isPresent());
        assertTrue(drillRepo.findByNameIgnoreCase(savedDrill.getName().toLowerCase()).isPresent());
    }

    @Test
    public void test_categoriesRepo_findByNameAndFindById_succeedsWithExistingCategories() {
        // CategoryEntity
        String categoryName = "NEW CATEGORY";
        CategoryEntity category = CategoryEntity.builder()
                                    .id(null)
                                    .name(categoryName)
                                    .description("New Category Description")
                                    .build();

        CategoryEntity savedCategory = categoryRepo.save(category);

        assertTrue(categoryRepo.findById(savedCategory.getId()).isPresent());
        assertTrue(categoryRepo.findByNameIgnoreCase(savedCategory.getName()).isPresent());
        assertTrue(categoryRepo.findByNameIgnoreCase(savedCategory.getName().toLowerCase()).isPresent());

        // SubCategoryEntity
        String subCategoryName = "NEW SUB CATEGORY";
        SubCategoryEntity subCategory = SubCategoryEntity.builder()
                                            .id(null)
                                            .name(subCategoryName)
                                            .description("New SubCategory Description")
                                            .build();

        SubCategoryEntity savedSubCategory = subCategoryRepo.save(subCategory);

        assertTrue(subCategoryRepo.findById(savedSubCategory.getId()).isPresent());
        assertTrue(subCategoryRepo.findByNameIgnoreCase(savedSubCategory.getName()).isPresent());
        assertTrue(subCategoryRepo.findByNameIgnoreCase(savedSubCategory.getName().toLowerCase()).isPresent());
    }

    @Test
    public void test_instructionsRepo_findByDrillIdAndNumber_succeedsWithExistingInstructions() {
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

        InstructionsEntity savedInstructions = instructionsRepo.save(instructions);

        assertTrue(
            instructionsRepo.findById(
                new InstructionsEntity.InstructionId(drillId, savedInstructions.getNumber()))
            .isPresent());
    }

    @Test
    public void test_drillRepo_findByNameAndFindById_failsWithNonExistantDrill() {
        String drillName = "NEW DRILL";
        DrillEntity drill = DrillEntity.builder()
                                .id(null)
                                .name(drillName)
                                .categories(null)
                                .subCategories(null)
                                .relatedDrills(null)
                                .instructions(null)
                                .build();

        DrillEntity savedDrill = drillRepo.save(drill);

        assertFalse(drillRepo.findById(savedDrill.getId() + 1L).isPresent());
        assertFalse(drillRepo.findByNameIgnoreCase(savedDrill.getName() + " NOPE").isPresent());
        assertFalse(drillRepo.findByNameIgnoreCase(savedDrill.getName().toLowerCase() + " NOPE").isPresent());
    }

    @Test
    public void test_categoriesRepo_findByNameAndFindById_failsWithNonExistantCategories() {
        // CategoryEntity
        String categoryName = "NEW CATEGORY";
        CategoryEntity category = CategoryEntity.builder()
                                    .id(null)
                                    .name(categoryName)
                                    .description("New Category Description")
                                    .build();

        CategoryEntity savedCategory = categoryRepo.save(category);

        assertFalse(categoryRepo.findById(savedCategory.getId() + 1).isPresent());
        assertFalse(categoryRepo.findByNameIgnoreCase(savedCategory.getName() + " NOPE").isPresent());
        assertFalse(categoryRepo.findByNameIgnoreCase(savedCategory.getName().toLowerCase() + " NOPE").isPresent());

        // SubCategoryEntity
        String subCategoryName = "NEW SUB CATEGORY";
        SubCategoryEntity subCategory = SubCategoryEntity.builder()
                                            .id(null)
                                            .name(subCategoryName)
                                            .description("New SubCategory Description")
                                            .build();

        SubCategoryEntity savedSubCategory = subCategoryRepo.save(subCategory);

        assertFalse(subCategoryRepo.findById(savedSubCategory.getId() + 1).isPresent());
        assertFalse(subCategoryRepo.findByNameIgnoreCase(savedSubCategory.getName() + " NOPE").isPresent());
        assertFalse(subCategoryRepo.findByNameIgnoreCase(savedSubCategory.getName().toLowerCase() + " NOPE").isPresent());
    }

    @Test
    public void test_instructionsRepo_findByDrillIdAndNumber_failsWithNonExistantInstructions() {
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

        InstructionsEntity savedInstructions = instructionsRepo.save(instructions);

        assertFalse(
            instructionsRepo.findById(
                new InstructionsEntity.InstructionId(drillId + 1, savedInstructions.getNumber()))
            .isPresent());
        assertFalse(
            instructionsRepo.findById(
                new InstructionsEntity.InstructionId(drillId, savedInstructions.getNumber() + 1))
            .isPresent());
            assertFalse(
        instructionsRepo.findById(
            new InstructionsEntity.InstructionId(drillId + 1, savedInstructions.getNumber() + 1))
        .isPresent());
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
        assertEquals(2, instructionsRepo.count());
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

        // // Empty number
        instructions.setDrillId(drillId);
        instructions.setNumber(null);
        assertThrows(TransactionSystemException.class,
            () -> instructionsRepo.save(instructions));

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

        assertEquals(1, drillRepo.count());
        assertEquals(1, returnedDrill.getCategories().size());
        assertEquals(1, returnedDrill.getSubCategories().size());
    }

    @Test
    public void test_drillRepo_save_nonExistentCategory_fails() {
        CategoryEntity category = CategoryEntity.builder()
                                    .id(null)
                                    .name("New Category Name")
                                    .description("New Category Description")
                                    .build();

        DrillEntity drill = DrillEntity.builder()
                                .id(null)
                                .name("New Drill")
                                .categories(null)
                                .subCategories(null)
                                .relatedDrills(null)
                                .instructions(null)
                                .build();

        drill.setCategories(List.of(category));
        assertThrows(InvalidDataAccessApiUsageException.class,
            () -> drillRepo.save(drill));
    }

    @Test
    public void test_drillRepo_save_nonExistentSubCategory_fails() {
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

        drill.setCategories(null);
        drill.setSubCategories(List.of(subCategory));
        assertThrows(InvalidDataAccessApiUsageException.class,
            () -> drillRepo.save(drill));
    }

    @Test
    public void test_drillRepo_save_updateWithNewCategories_succeeds() {
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
                                .categories(new ArrayList<>())
                                .subCategories(new ArrayList<>())
                                .relatedDrills(null)
                                .instructions(null)
                                .build();

        drill.getCategories().add(returnedCategory);
        drill.getSubCategories().add(returnedSubCategory);
        DrillEntity returnedDrill = drillRepo.save(drill);

        assertEquals(1, drillRepo.count());
        assertEquals(1, returnedDrill.getCategories().size());
        assertEquals(1, returnedDrill.getSubCategories().size());


        // CategoryEntity
        CategoryEntity category2 = CategoryEntity.builder()
                                    .id(null)
                                    .name("New Category Name 2")
                                    .description("New Category Description 2")
                                    .build();
        CategoryEntity returnedCategory2 = categoryRepo.save(category2);

        // SubCategoryEntity
        SubCategoryEntity subCategory2 = SubCategoryEntity.builder()
                                            .id(null)
                                            .name("New SubCategory Name 2")
                                            .description("New SubCategory Description 2")
                                            .build();
        SubCategoryEntity returnedSubCategory2 = subCategoryRepo.save(subCategory2);

        returnedDrill = drillRepo.findById(returnedDrill.getId()).get();
        returnedDrill.getCategories().add(returnedCategory2);
        returnedDrill.getSubCategories().add(returnedSubCategory2);
        DrillEntity returnedDrill2 = drillRepo.save(returnedDrill);

        assertEquals(2, returnedDrill2.getCategories().size());
        assertEquals(2, returnedDrill2.getSubCategories().size());
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
    public void test_drillRepo_save_updateWithInvalidInstructions_throwsException() {
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
                                            .number(null)
                                            .description("Instructions Description")
                                            .steps("Step1|Step2|Step3")
                                            .videoId(null)
                                            .build();

        drill.setInstructions(new ArrayList<>());
        drill.getInstructions().add(instructions);

        assertThrows(TransactionSystemException.class, () -> drillRepo.save(drill));

        drill.getInstructions().remove(instructions);
        instructions.setNumber(0L);
        instructions.setDrillId(drillId + 1);
        drill.getInstructions().add(instructions);

        assertThrows(DataIntegrityViolationException.class, () -> drillRepo.save(drill));
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
        assertEquals(1, instructionsRepo.count());
    }

    @Test
    public void test_drillRepo_delete_deleteDrill_doesNotDeleteCategories() {
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

        assertEquals(1, drillRepo.count());
        assertEquals(1, categoryRepo.count());
        assertEquals(1, subCategoryRepo.count());

        drillRepo.delete(returnedDrill);

        assertEquals(0, drillRepo.count());
        assertEquals(1, categoryRepo.count());
        assertEquals(1, subCategoryRepo.count());
    }

    @Test
    public void test_drillRepo_delete_deleteDrill_doesDeleteInstructions() {
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

        assertEquals(1, drillRepo.count());
        assertEquals(1, instructionsRepo.count());

        drillRepo.delete(returnedEntity);

        assertEquals(0, drillRepo.count());
        assertEquals(0, instructionsRepo.count());
    }

    @Test
    public void test_categoriesRepo_delete_deleteCategories_doesNotDeleteDrill() {
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

        drillRepo.save(drill);

        assertEquals(1, drillRepo.count());
        assertEquals(1, categoryRepo.count());
        assertEquals(1, subCategoryRepo.count());

        categoryRepo.delete(returnedCategory);
        subCategoryRepo.delete(returnedSubCategory);

        assertEquals(1, drillRepo.count());
        assertEquals(0, categoryRepo.count());
        assertEquals(0, subCategoryRepo.count());
    }

    @Test
    public void test_categoriesRepo_delete_deleteCategories_removesFromDrillList() {
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

        assertEquals(1, returnedDrill.getCategories().size());
        assertEquals(1, returnedDrill.getSubCategories().size());

        categoryRepo.delete(returnedCategory);
        subCategoryRepo.delete(returnedSubCategory);
        DrillEntity returnedDrill2 = drillRepo.findById(returnedDrill.getId()).get();

        assertEquals(0, returnedDrill2.getCategories().size());
        assertEquals(0, returnedDrill2.getSubCategories().size());
    }

    @Test
    public void test_instructionsRepo_delete_deleteInstructions_doesNotDeleteDrill() {
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

        assertEquals(1, instructionsRepo.count());
        assertEquals(1, drillRepo.count());

        instructionsRepo.delete(returnedInstructions);

        assertEquals(0, instructionsRepo.count());
        assertEquals(1, drillRepo.count());
    }

    @Test
    public void test_instructionsRepo_delete_deleteInstructions_removeFromDrillList() {
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
        assertEquals(1, drillRepo.findById(drillId).get().getInstructions().size());

        instructionsRepo.delete(returnedInstructions);
        assertEquals(0, drillRepo.findById(drillId).get().getInstructions().size());
    }
}
