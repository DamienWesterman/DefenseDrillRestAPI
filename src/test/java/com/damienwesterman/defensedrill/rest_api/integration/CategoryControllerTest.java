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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import com.damienwesterman.defensedrill.rest_api.entity.CategoryEntity;
import com.damienwesterman.defensedrill.rest_api.exception.DatabaseInsertException;
import com.damienwesterman.defensedrill.rest_api.service.CategorySerivce;
import com.damienwesterman.defensedrill.rest_api.web.CategoryController;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(CategoryController.class)
@AutoConfigureMockMvc
public class CategoryControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    CategorySerivce service;

    // Control things
    CategoryEntity category1;
    CategoryEntity category2;

    Long id1 = 1L;
    String name1 = "Name 1";
    String description1 = "Despcription 1";
    Long id2 = 2L;
    String name2 = "Name 2";
    String description2 = "Despcription 2";

    @BeforeEach
    public void setup() {
        category1 = CategoryEntity.builder()
                        .id(id1)
                        .name(name1)
                        .description(description1)
                        .build();

        category2 = CategoryEntity.builder()
                        .id(id2)
                        .name(name2)
                        .description(description2)
                        .build();
    }

    @Test
    public void test_rootEndpoint_get_withNoItemsInDB_returnsStatus204() throws Exception {
        when(service.findAll()).thenReturn(List.of());

        mockMvc.perform(get(CategoryController.ENDPOINT))
            .andExpect(status().isNoContent());
    }

    @Test
    public void test_rootEndpoint_get_withTwoItemsInDB_returnListOfCategoryDao() throws Exception {
        when(service.findAll()).thenReturn(List.of(category1, category2));

        mockMvc.perform(get(CategoryController.ENDPOINT))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].id").value(id1))
            .andExpect(jsonPath("$[0].name").value(name1))
            .andExpect(jsonPath("$[0].description").value(description1))
            .andExpect(jsonPath("$[1].id").value(id2))
            .andExpect(jsonPath("$[1].name").value(name2))
            .andExpect(jsonPath("$[1].description").value(description2));
    }

    @Test
    public void test_rootEndpoint_post_invalidArgumentWithNoObject() throws Exception {
        mockMvc.perform(post(CategoryController.ENDPOINT))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void test_rootEndpoint_post_invalidArgumentWithEmptyObject() throws Exception {
        mockMvc.perform(post(CategoryController.ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void test_rootEndpoint_post_invalidArgumentWithWrongObject() throws Exception {
        mockMvc.perform(post(CategoryController.ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"wrong\":\"field\"}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void test_rootEndpoint_post_shouldSucceedWithCorrectFields() throws Exception {
        CategoryEntity entityToSave = CategoryEntity.builder()
                                        .id(null)
                                        .name(name1)
                                        .description(description1)
                                        .build();
        when(service.save(entityToSave)).thenReturn(category1);

        mockMvc.perform(post(CategoryController.ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(entityToSave)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(id1))
            .andExpect(jsonPath("$.name").value(name1))
            .andExpect(jsonPath("$.description").value(description1))
            .andExpect(header().string("Location", CategoryController.ENDPOINT + "/" + category1.getId()));

        verify(service).save(entityToSave);
    }

    @Test
    public void test_rootEndpoint_post_jakartaConstraintViolation_fails() throws Exception {
        // Any jakarta constraint violation should do, empty name is good
        CategoryEntity entityToSave = CategoryEntity.builder()
                                        .id(null)
                                        .name("")
                                        .description(description1)
                                        .build();

        mockMvc.perform(post(CategoryController.ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(entityToSave)))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void test_rootEndpoint_post_uniqueConstraintViolation_fails() throws Exception {
        when(service.save(category1)).thenThrow(new DatabaseInsertException("Unique Cosntraint Violation"));
        mockMvc.perform(post(CategoryController.ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(category1)))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void test_rootEndpointput_put_shouldFail() throws Exception {
        mockMvc.perform(put(CategoryController.ENDPOINT))
            .andExpect(status().isMethodNotAllowed());
    }

    @Test
    public void test_rootEndpointdelete_delete_shouldFail() throws Exception {
        mockMvc.perform(delete(CategoryController.ENDPOINT))
            .andExpect(status().isMethodNotAllowed());
    }

    @Test
    public void test_idEndpoint_get_succeedsWithExistingId() throws Exception {
        when(service.find(id1)).thenReturn(Optional.of(category1));

        mockMvc.perform(get(CategoryController.ENDPOINT + "/" + id1))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(id1))
            .andExpect(jsonPath("$.name").value(name1))
            .andExpect(jsonPath("$.description").value(description1));

        verify(service).find(id1);
    }

    @Test
    public void test_idEndpoint_get_returns404WithNonExistentId() throws Exception {
        when(service.find(id1)).thenReturn(Optional.empty());

        mockMvc.perform(get(CategoryController.ENDPOINT + "/" + id1))
            .andExpect(status().isNotFound());

        verify(service).find(id1);
    }

    @Test
    public void test_idEndpoint_put_invalidArgumentWithNoObject() throws Exception {
        mockMvc.perform(put(CategoryController.ENDPOINT + "/" + id1))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void test_idEndpoint_put_invalidArgumentWithEmptyObject() throws Exception {
        mockMvc.perform(put(CategoryController.ENDPOINT + "/" + id1)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void test_idEndpoint_put_invalidArgumentWithWrongObject() throws Exception {
        mockMvc.perform(put(CategoryController.ENDPOINT + "/" + id1)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"wrong\":\"field\"}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void test_idEndpoint_put_shouldSucceedWithCorrectFieldsAndExistingId() throws Exception {
        when(service.save(category1)).thenReturn(category1);
        when(service.find(id1)).thenReturn(Optional.of(category1));

        mockMvc.perform(put(CategoryController.ENDPOINT + "/" + id1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(category1)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(id1))
            .andExpect(jsonPath("$.name").value(name1))
            .andExpect(jsonPath("$.description").value(description1));

        verify(service).save(category1);
    }

    @Test
    public void test_idEndpoint_put_nonExistentIdFails() throws Exception {
        when(service.save(category1)).thenReturn(category1);
        when(service.find(id1)).thenReturn(Optional.empty());

        mockMvc.perform(put(CategoryController.ENDPOINT + "/" + id1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(category1)))
            .andExpect(status().isNotFound());

        verify(service, times(0)).save(category1);
    }

    @Test
    public void test_idEndpoint_put_idMismatchFromPathAndBody() throws Exception {
        // TODO: FINISH
        fail();
    }

    @Test
    public void test_idEndpoint_put_jakartaCosntraintViolation_fails() throws Exception {
        // TODO: FINISH
        fail();
    }

    @Test
    public void test_idEndpoint_put_uniqueConstraintViolation_fails() throws Exception {
        // TODO: FINISH
        fail();
    }

    @Test
    public void test_idEndpoint_post_fails() throws Exception {
        // TODO: FINISH
        fail();
    }

    @Test
    public void test_idEndpoint_delete_alwaysSucceeds204() throws Exception {
        // TODO: FINISH
        fail();
    }

    // TODO: GET TESTS ON /name? (error handling etc.)
    // TODO: PUT/POST TESTS ON /name? (fail)
    // TODO: DELETE TESTS ON /name? (always 204)
}
