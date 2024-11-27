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

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.damienwesterman.defensedrill.rest_api.entity.CategoryEntity;
import com.damienwesterman.defensedrill.rest_api.entity.DrillEntity;
import com.damienwesterman.defensedrill.rest_api.entity.InstructionsEntity;
import com.damienwesterman.defensedrill.rest_api.entity.SubCategoryEntity;
import com.damienwesterman.defensedrill.rest_api.exception.DatabaseInsertException;
import com.damienwesterman.defensedrill.rest_api.service.CategorySerivce;
import com.damienwesterman.defensedrill.rest_api.service.DrillService;
import com.damienwesterman.defensedrill.rest_api.service.SubCategorySerivce;
import com.damienwesterman.defensedrill.rest_api.web.DrillController;
import com.damienwesterman.defensedrill.rest_api.web.dto.DrillCreateDTO;
import com.damienwesterman.defensedrill.rest_api.web.dto.DrillUpdateDTO;
import com.damienwesterman.defensedrill.rest_api.web.dto.InstructionsDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(DrillController.class)
@AutoConfigureMockMvc
public class DrillControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    DrillService drillService;
    @MockBean
    CategorySerivce categorySerivce;
    @MockBean
    SubCategorySerivce subCategorySerivce;

    // TODO: Check what is needed in the end
    DrillEntity drill1;
    CategoryEntity category1;
    SubCategoryEntity subCategory1;
    InstructionsEntity instructions1;
    DrillCreateDTO dtoToSend;

    final Long DRILL_ID_1 = 1L;
    final Long CATEGORY_ID_1 = 11L;
    final Long SUB_CATEGORY_ID_1 = 111L;
    final Long NUMBER_1 = 0L;
    final Long RELATED_DRILL_ID = 2L;
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
        drill1 = DrillEntity.builder()
                            .id(DRILL_ID_1)
                            .name(DRILL_NAME_1)
                            .categories(new ArrayList<>())
                            .subCategories(new ArrayList<>())
                            .instructions(new ArrayList<>())
                            .relatedDrills(new ArrayList<>())
                            .build();
        category1 = CategoryEntity.builder()
                            .id(CATEGORY_ID_1)
                            .name(CATEGORY_NAME_1)
                            .description(CATEGORY_DESCRIPTION_1)
                            .build();
        subCategory1 = SubCategoryEntity.builder()
                            .id(SUB_CATEGORY_ID_1)
                            .name(SUB_CATEGORY_NAME_1)
                            .description(SUB_CATEGORY_DESCRIPTION_1)
                            .build();
        instructions1 = InstructionsEntity.builder()
                            .drillId(DRILL_ID_1)
                            .number(NUMBER_1)
                            .description(INSTRUCTIONS_DESCRIPTION_1)
                            .steps(INSTRUCTION_STEPS_1)
                            .videoId(VIDEO_ID_1)
                            .build();
        dtoToSend = new DrillCreateDTO();
        dtoToSend.setName(DRILL_NAME_1);
    }

    @Test
    public void test_rootEndpoint_get_withNoItemsInDB_returnsStatus204() throws Exception {
        when(drillService.findAll()).thenReturn(List.of());

        mockMvc.perform(get(DrillController.ENDPOINT))
            .andExpect(status().isNoContent());
    }

    // This one also serves to check the format of the returned object`
    @Test
    public void test_rootEndpoint_get_withOneItemInDB_returnListOfOneDrills() throws Exception {
        drill1.getCategories().add(category1);
        drill1.getSubCategories().add(subCategory1);
        drill1.getRelatedDrills().add(RELATED_DRILL_ID);
        drill1.getInstructions().add(instructions1);
        when(drillService.findAll()).thenReturn(List.of(drill1));

        mockMvc.perform(get(DrillController.ENDPOINT))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].id").value(DRILL_ID_1))
            .andExpect(jsonPath("$[0].name").value(DRILL_NAME_1))
            .andExpect(jsonPath("$[0].categories").isArray())
            .andExpect(jsonPath("$[0].categories.length()").value(1))
            // For categories, we only want to return their ID, and NOTHING else
            .andExpect(jsonPath("$[0].categories[0]").value(CATEGORY_ID_1))
            .andExpect(jsonPath("$[0].categories[0].name").doesNotExist())
            .andExpect(jsonPath("$[0].sub_categories").isArray())
            .andExpect(jsonPath("$[0].sub_categories.length()").value(1))
            // For sub-categories, we only want to return their ID, and NOTHING else
            .andExpect(jsonPath("$[0].sub_categories[0]").value(SUB_CATEGORY_ID_1))
            .andExpect(jsonPath("$[0].sub_categories[0].name").doesNotExist())
            .andExpect(jsonPath("$[0].related_drills").isArray())
            .andExpect(jsonPath("$[0].related_drills.length()").value(1))
            // For related drills, we only want to return their ID, and NOTHING else
            .andExpect(jsonPath("$[0].related_drills[0]").value(RELATED_DRILL_ID))
            .andExpect(jsonPath("$[0].related_drills[0].name").doesNotExist())
            // We DO NOT want to include any instructions at this point, that is for a different endpoint
            .andExpect(jsonPath("$[0].instructions").doesNotExist());
    }

    @Test
    public void test_rootEndpoint_post_invalidArgumentWithNoObject() throws Exception {
        mockMvc.perform(post(DrillController.ENDPOINT))
            .andExpect(status().isBadRequest());
        verify(drillService, times(0)).save(any());
    }

    @Test
    public void test_rootEndpoint_post_invalidArgumentWithEmptyObject() throws Exception {
        mockMvc.perform(post(DrillController.ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isBadRequest());
        verify(drillService, times(0)).save(any());
    }

    @Test
    public void test_rootEndpoint_post_invalidArgumentWithWrongObject() throws Exception {
        mockMvc.perform(post(DrillController.ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"wrong\":\"field\"}"))
            .andExpect(status().isBadRequest());
        verify(drillService, times(0)).save(any());
    }

    @Test
    public void test_rootEndpoint_post_shouldSucceedWithCorrectFields() throws Exception {
        DrillEntity entityToSave = DrillEntity.builder()
                                    .name(DRILL_NAME_1)
                                    .build();
        when(drillService.save(entityToSave)).thenReturn(drill1);

        mockMvc.perform(post(DrillController.ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoToSend)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(DRILL_ID_1))
            .andExpect(jsonPath("$.name").value(DRILL_NAME_1));

        verify(drillService, times(1)).save(entityToSave);
    }

    @Test
    public void test_rootEndpoint_post_jakartaConstraintViolation_fails() throws Exception {
        dtoToSend.setName("");

        mockMvc.perform(post(DrillController.ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoToSend)))
            .andExpect(status().isBadRequest());

        verify(drillService, times(0)).save(any());
    }

    @Test
    public void test_rootEndpoint_post_uniqueConstraintViolation_fails() throws Exception {
        when(drillService.save(any())).thenThrow(new DatabaseInsertException("Unique Cosntraint Violation"));

        mockMvc.perform(post(DrillController.ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoToSend)))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void test_rootEndpointput_put_shouldFail() throws Exception {
        mockMvc.perform(put(DrillController.ENDPOINT))
            .andExpect(status().isMethodNotAllowed());
    }

    @Test
    public void test_rootEndpointdelete_delete_shouldFail() throws Exception {
        mockMvc.perform(delete(DrillController.ENDPOINT))
            .andExpect(status().isMethodNotAllowed());
    }

    @Test
    public void test_nameEndpoint_get_succeedsWithExistingName() throws Exception {
        when(drillService.find(DRILL_NAME_1)).thenReturn(Optional.of(drill1));

        mockMvc.perform(get(DrillController.ENDPOINT + "/name/" + DRILL_NAME_1))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(DRILL_ID_1))
            .andExpect(jsonPath("$.name").value(DRILL_NAME_1));

        verify(drillService, times(1)).find(DRILL_NAME_1);
    }

    @Test
    public void test_nameEndpoint_get_returns404WithNonExistentName() throws Exception {
        when(drillService.find(DRILL_NAME_1)).thenReturn(Optional.empty());

        mockMvc.perform(get(DrillController.ENDPOINT + "/name/" + DRILL_NAME_1))
            .andExpect(status().isNotFound());

        verify(drillService).find(DRILL_NAME_1);
    }

    @Test
    public void test_nameEndpoint_put_fails() throws Exception {
        mockMvc.perform(put(DrillController.ENDPOINT + "/name/" + DRILL_NAME_1))
            .andExpect(status().isMethodNotAllowed());
    }

    @Test
    public void test_nameEndpoint_post_fails() throws Exception {
        mockMvc.perform(post(DrillController.ENDPOINT + "/name/" + DRILL_NAME_1))
            .andExpect(status().isMethodNotAllowed());
    }

    @Test
    public void test_nameEndpoint_delete_fails() throws Exception {
        mockMvc.perform(delete(DrillController.ENDPOINT + "/name/" + DRILL_NAME_1))
            .andExpect(status().isMethodNotAllowed());
    }

    @Test
    public void test_idEndpoint_get_succeedsWithExistingId() throws Exception {
        when(drillService.find(DRILL_ID_1)).thenReturn(Optional.of(drill1));

        mockMvc.perform(get(DrillController.ENDPOINT + "/id/" + DRILL_ID_1))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(DRILL_ID_1))
            .andExpect(jsonPath("$.name").value(DRILL_NAME_1));

        verify(drillService, times(1)).find(DRILL_ID_1);
    }

    @Test
    public void test_idEndpoint_get_returns404WithNonExistentId() throws Exception {
        when(drillService.find(DRILL_ID_1)).thenReturn(Optional.empty());

        mockMvc.perform(get(DrillController.ENDPOINT + "/id/" + DRILL_ID_1))
            .andExpect(status().isNotFound());

        verify(drillService, times(1)).find(DRILL_ID_1);
    }

    @Test
    public void test_idEndpoint_put_invalidArgumentWithNoObject() throws Exception {
        when(drillService.find(DRILL_ID_1)).thenReturn(Optional.of(drill1));

        mockMvc.perform(put(DrillController.ENDPOINT + "/id/" + DRILL_ID_1))
            .andExpect(status().isBadRequest());

        verify(drillService, times(0)).find(DRILL_ID_1);
    }

    @Test
    public void test_idEndpoint_put_invalidArgumentWithEmptyObject() throws Exception {
        when(drillService.find(DRILL_ID_1)).thenReturn(Optional.of(drill1));

        mockMvc.perform(put(DrillController.ENDPOINT + "/id/" + DRILL_ID_1)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isBadRequest());

        verify(drillService, times(0)).find(DRILL_ID_1);
    }

    @Test
    public void test_idEndpoint_put_invalidArgumentWithWrongObject() throws Exception {
        when(drillService.find(DRILL_ID_1)).thenReturn(Optional.of(drill1));

        mockMvc.perform(put(DrillController.ENDPOINT + "/id/" + DRILL_ID_1)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"bad\":\"argument\"}"))
            .andExpect(status().isBadRequest());

        verify(drillService, times(0)).find(DRILL_ID_1);
    }

    @Test
    public void test_idEndpoint_put_shouldSucceedWithCorrectFieldsAndExistingId() throws Exception {
        when(drillService.find(DRILL_ID_1)).thenReturn(Optional.of(drill1));

        // Set up drill transfer object
        DrillUpdateDTO drillToUpdate = new DrillUpdateDTO();
        drillToUpdate.setId(drill1.getId());
        drillToUpdate.setName(drill1.getName());
        drillToUpdate.setCategoryIds(List.of(category1.getId()));
        drillToUpdate.setSubCategoryIds(List.of(subCategory1.getId()));
        drillToUpdate.setRelatedDrills(List.of(RELATED_DRILL_ID));
        InstructionsDTO instructionsToAdd = new InstructionsDTO();
        instructionsToAdd.setDescription(instructions1.getDescription());
        instructionsToAdd.setSteps(instructions1.getStepsAsList());
        instructionsToAdd.setVideoId(instructions1.getVideoId());
        drillToUpdate.setInstructions(List.of(instructionsToAdd));

        // Set up drill return value
        drill1.getCategories().add(category1);
        drill1.getSubCategories().add(subCategory1);
        drill1.getRelatedDrills().add(RELATED_DRILL_ID);
        drill1.getInstructions().add(instructions1);
        when(categorySerivce.findAll(List.of(CATEGORY_ID_1))).thenReturn(List.of(category1));
        when(subCategorySerivce.findAll(List.of(SUB_CATEGORY_ID_1))).thenReturn(List.of(subCategory1));
        when(drillService.save(drill1)).thenReturn(drill1);

        mockMvc.perform(put(DrillController.ENDPOINT + "/id/" + DRILL_ID_1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(drillToUpdate)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(DRILL_ID_1))
            .andExpect(jsonPath("$.name").value(DRILL_NAME_1))
            .andExpect(jsonPath("$.categories").isArray())
            .andExpect(jsonPath("$.categories.length()").value(1))
            // For categories, we only want to return their ID, and NOTHING else
            .andExpect(jsonPath("$.categories[0]").value(CATEGORY_ID_1))
            .andExpect(jsonPath("$.categories[0].name").doesNotExist())
            .andExpect(jsonPath("$.sub_categories").isArray())
            .andExpect(jsonPath("$.sub_categories.length()").value(1))
            // For sub-categories, we only want to return their ID, and NOTHING else
            .andExpect(jsonPath("$.sub_categories[0]").value(SUB_CATEGORY_ID_1))
            .andExpect(jsonPath("$.sub_categories[0].name").doesNotExist())
            .andExpect(jsonPath("$.related_drills").isArray())
            .andExpect(jsonPath("$.related_drills.length()").value(1))
            // For related drills, we only want to return their ID, and NOTHING else
            .andExpect(jsonPath("$.related_drills[0]").value(RELATED_DRILL_ID))
            .andExpect(jsonPath("$.related_drills[0].name").doesNotExist())
            // We DO NOT want to include any instructions at this point, that is for a different endpoint
            .andExpect(jsonPath("$.instructions").doesNotExist());

        verify(drillService, times(1)).find(DRILL_ID_1);
        verify(drillService, times(1)).save(drill1);
    }

    @Test
    public void test_idEndpoint_put_nonExistentIdFails() throws Exception {
        when(drillService.find(DRILL_ID_1)).thenReturn(Optional.empty());
        DrillUpdateDTO drillToUpdate = new DrillUpdateDTO();
        drillToUpdate.setId(drill1.getId());
        drillToUpdate.setName(drill1.getName());

        mockMvc.perform(put(DrillController.ENDPOINT + "/id/" + DRILL_ID_1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(drillToUpdate)))
            .andExpect(status().isNotFound());

        verify(drillService, times(0)).save(any());
    }

    @Test
    public void test_idEndpoint_put_entityIdNull() throws Exception {
        mockMvc.perform(put(DrillController.ENDPOINT + "/id/" + DRILL_ID_1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new DrillUpdateDTO())))
            .andExpect(status().isBadRequest());

        verify(drillService, times(0)).save(any());
    }

    @Test
    public void test_idEndpoint_put_idMismatchFromPathAndBody() throws Exception {
        when(drillService.find(DRILL_ID_1)).thenReturn(Optional.empty());
        DrillUpdateDTO drillToUpdate = new DrillUpdateDTO();
        drillToUpdate.setId(drill1.getId() + 1);
        drillToUpdate.setName(drill1.getName());

        mockMvc.perform(put(DrillController.ENDPOINT + "/id/" + DRILL_ID_1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(drillToUpdate)))
            .andExpect(status().isBadRequest());

        verify(drillService, times(0)).save(any());
    }

    @Test
    public void test_idEndpoint_put_jakartaCosntraintViolation_fails() throws Exception {
        fail();
    }

    @Test
    public void test_idEndpoint_put_uniqueConstraintViolation_fails() throws Exception {
        fail();
    }

    @Test
    public void test_idEndpoint_put_badInstructionsViolation_fails() throws Exception {
        fail();
    }

    @Test
    public void test_idEndpoint_put_badForeignKeyViolation_fails() throws Exception {
        fail();
    }

    @Test
    public void test_idEndpoint_post_fails() throws Exception {
        mockMvc.perform(post(DrillController.ENDPOINT + "/id/" + DRILL_ID_1))
            .andExpect(status().isMethodNotAllowed());
    }

    @Test
    public void test_idEndpoint_delete_alwaysSucceeds204() throws Exception {
        mockMvc.perform(delete(DrillController.ENDPOINT + "/id/" + DRILL_ID_1))
            .andExpect(status().isNoContent());

            verify(drillService, times(1)).delete(DRILL_ID_1);
    }

    // TODO: /id/{id}/how-to
    // TODO: /id/{id}/how-to/{number}
    // TODO: /id/{id}/video
}