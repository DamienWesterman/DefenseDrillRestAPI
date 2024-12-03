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

package com.damienwesterman.defensedrill.rest_api.endToEnd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.damienwesterman.defensedrill.rest_api.entity.CategoryEntity;
import com.damienwesterman.defensedrill.rest_api.entity.DrillEntity;
import com.damienwesterman.defensedrill.rest_api.entity.InstructionsEntity;
import com.damienwesterman.defensedrill.rest_api.entity.SubCategoryEntity;
import com.damienwesterman.defensedrill.rest_api.repository.CategoryRepo;
import com.damienwesterman.defensedrill.rest_api.repository.DrillRepo;
import com.damienwesterman.defensedrill.rest_api.repository.InstructionsRepo;
import com.damienwesterman.defensedrill.rest_api.repository.SubCategoryRepo;
import com.damienwesterman.defensedrill.rest_api.web.DrillController;
import com.damienwesterman.defensedrill.rest_api.web.dto.DrillCreateDTO;
import com.damienwesterman.defensedrill.rest_api.web.dto.DrillResponseDTO;
import com.damienwesterman.defensedrill.rest_api.web.dto.DrillUpdateDTO;
import com.damienwesterman.defensedrill.rest_api.web.dto.InstructionsDTO;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EndToEndTest {
    // TODO: Make sure that inserting, updating, adding, and removing instructions AND steps always properly indexes and re-numbers them
    // TODO: Anything that was mocked/stubbed using mock.when().then() should be tested here
    @Autowired
    TestRestTemplate restTemplate;
    @Autowired
    DrillRepo drillRepo;
    @Autowired
    InstructionsRepo instructionsRepo;
    @Autowired
    CategoryRepo categoryRepo;
    @Autowired
    SubCategoryRepo subCategoryRepo;

    DrillEntity drill1;
    CategoryEntity category1;
    SubCategoryEntity subCategory1;
    InstructionsEntity instructions1;
    DrillCreateDTO dtoToSend;

    final String DRILL_NAME_1 = "Drill Name 1";
    final String CATEGORY_NAME_1 = "Category Name 1";
    final String SUB_CATEGORY_NAME_1 = "Sub-Category Name 1";
    final String CATEGORY_DESCRIPTION_1 = "Category Description 1";
    final String SUB_CATEGORY_DESCRIPTION_1 = "Sub-Category Description 1";
    final String INSTRUCTIONS_DESCRIPTION_1 = "Instructions Description 1";
    final String STEP_ONE = "One";
    final String STEP_TWO = "Two";
    final String STEP_THREE = "Three";
    final String INSTRUCTION_STEPS_1 = String.join("|", List.of(STEP_ONE, STEP_TWO, STEP_THREE));
    final String VIDEO_ID_1 = "Video ID 1";

    @BeforeEach
    public void setup() {
        drillRepo.deleteAll();;
        instructionsRepo.deleteAll();
        categoryRepo.deleteAll();
        subCategoryRepo.deleteAll();

        drill1 = DrillEntity.builder()
                            .id(null)
                            .name(DRILL_NAME_1)
                            .categories(new ArrayList<>())
                            .subCategories(new ArrayList<>())
                            .instructions(new ArrayList<>())
                            .relatedDrills(new ArrayList<>())
                            .build();
        category1 = CategoryEntity.builder()
                            .id(null)
                            .name(CATEGORY_NAME_1)
                            .description(CATEGORY_DESCRIPTION_1)
                            .build();
        subCategory1 = SubCategoryEntity.builder()
                            .id(null)
                            .name(SUB_CATEGORY_NAME_1)
                            .description(SUB_CATEGORY_DESCRIPTION_1)
                            .build();
        instructions1 = InstructionsEntity.builder()
                            .drillId(null)
                            .number(null)
                            .description(INSTRUCTIONS_DESCRIPTION_1)
                            .steps(INSTRUCTION_STEPS_1)
                            .videoId(VIDEO_ID_1)
                            .build();
        dtoToSend = new DrillCreateDTO();
        dtoToSend.setName(DRILL_NAME_1);
    }

    @Test
    public void test_drill_databaseSavesProperly() {
        ResponseEntity<DrillResponseDTO> response =
            restTemplate.postForEntity(URI.create(DrillController.ENDPOINT), dtoToSend, DrillResponseDTO.class);
        System.out.println(response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(1, drillRepo.count());
        assertEquals(DRILL_NAME_1, drillRepo.findAll().get(0).getName());
    }

    @SuppressWarnings("null")
    @Test
    public void test_drill_databaseReturnsProperly() {
        drillRepo.save(drill1);
        ResponseEntity<DrillResponseDTO[]> response =
            restTemplate.getForEntity(URI.create(DrillController.ENDPOINT), DrillResponseDTO[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(DRILL_NAME_1, response.getBody()[0].getName());
    }

    @Test
    public void test_drill_addingACategory_databaseReturnsProperly() {
        Long categoryId = categoryRepo.save(category1).getId();
        DrillEntity savedDrill = drillRepo.save(drill1);
        DrillUpdateDTO updatedDrill = new DrillUpdateDTO();
        updatedDrill.setId(savedDrill.getId());
        updatedDrill.setName(savedDrill.getName());
        updatedDrill.setCategoryIds(List.of(categoryId));

        // Sanity check
        assertEquals(0, drillRepo.findAll().get(0).getCategories().size());

        ResponseEntity<DrillResponseDTO> response =
            restTemplate.exchange(
                URI.create(DrillController.ENDPOINT + "/id/" + savedDrill.getId()),
                HttpMethod.PUT,
                new HttpEntity<>(updatedDrill),
                DrillResponseDTO.class
            );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, drillRepo.findAll().get(0).getCategories().size());
        assertEquals(
            CATEGORY_NAME_1,
            drillRepo.findAll().get(0).getCategories().get(0).getName()
        );
    }

    @Test
    public void test_drill_addingRelatedDrill_databaseReturnsProperly() {
        DrillEntity relatedDrill = DrillEntity.builder()
                                    .name("Related Name")
                                    .build();
        Long relatedDrillId = drillRepo.save(relatedDrill).getId();
        DrillEntity savedDrill = drillRepo.save(drill1);
        DrillUpdateDTO updatedDrill = new DrillUpdateDTO();
        updatedDrill.setId(savedDrill.getId());
        updatedDrill.setName(savedDrill.getName());
        updatedDrill.setRelatedDrills(List.of(relatedDrillId));

        // Sanity check
        assertEquals(0, drillRepo.findById(savedDrill.getId()).get().getRelatedDrills().size());

        ResponseEntity<DrillResponseDTO> response =
            restTemplate.exchange(
                URI.create(DrillController.ENDPOINT + "/id/" + savedDrill.getId()),
                HttpMethod.PUT,
                new HttpEntity<>(updatedDrill),
                DrillResponseDTO.class
            );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, drillRepo.findById(savedDrill.getId()).get().getRelatedDrills().size());
        assertEquals(
            relatedDrillId,
            drillRepo.findById(savedDrill.getId()).get().getRelatedDrills().get(0)
        );
    }

    @Test
    public void test_instructions_databaseSavesProperly() {
        DrillEntity savedDrill = drillRepo.save(drill1);
        DrillUpdateDTO updatedDrill = new DrillUpdateDTO();
        updatedDrill.setId(savedDrill.getId());
        updatedDrill.setName(savedDrill.getName());
        updatedDrill.setInstructions(List.of(new InstructionsDTO(instructions1)));

        // Sanity check
        assertEquals(0, instructionsRepo.count());

        ResponseEntity<DrillResponseDTO> response =
            restTemplate.exchange(
                URI.create(DrillController.ENDPOINT + "/id/" + savedDrill.getId()),
                HttpMethod.PUT,
                new HttpEntity<>(updatedDrill),
                DrillResponseDTO.class
            );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, instructionsRepo.count());
        assertEquals(INSTRUCTIONS_DESCRIPTION_1, instructionsRepo.findAll().get(0).getDescription());
    }

    @Test
    public void test_instructions_databaseReturesProperly() {
        fail();
    }

    @Test
    public void test_categories_databaseSavesProperly() {
        fail();
    }

    @Test
    public void test_categories_databaseReturnsProperly() {
        fail();
    }

    @Test
    public void test_subCategories_databaseSavesProperly() {
        // No need to implement until SubCategoryController diverges from CategoryController
    }

    @Test
    public void test_subCategories_databaseReturnsProperly() {
        // No need to implement until SubCategoryController diverges from CategoryController
    }
}
