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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

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
    public void test_get_root_withNoItemsInDB_returnsStatus204() throws Exception {
        when(service.findAll()).thenReturn(List.of());

        mockMvc.perform(get(CategoryController.ENDPOINT))
            .andExpect(status().isNoContent());
    }

    @Test
    public void test_get_root_withTwoItemsInDB_returnListOfCategoryDao() throws Exception {
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
    public void test_post_root_invalidArgumentWithNoObject() throws Exception {
        mockMvc.perform(post(CategoryController.ENDPOINT))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void test_post_root_invalidArgumentWithEmptyObject() throws Exception {
        String errorMessage = "Everything null";
        when(service.save(any())).thenThrow(new DatabaseInsertException(errorMessage));
                mockMvc.perform(post(CategoryController.ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{}"))
            .andExpect(status().isBadRequest())
            .andExpect(content().string(errorMessage));
    }

    @Test
    public void test_post_root_shouldSucceedWithCorrectFields() throws Exception {
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

    // TODO: FAILS JAKARTA CONSTRAINTS
    // TODO: FAILS UNIQUE CONSTRAINTS

    @Test
    public void test_put_root_shouldFail() throws Exception {
        mockMvc.perform(put(CategoryController.ENDPOINT))
            .andExpect(status().isMethodNotAllowed());
    }

    @Test
    public void test_delete_root_shouldFail() throws Exception {
        mockMvc.perform(delete(CategoryController.ENDPOINT))
            .andExpect(status().isMethodNotAllowed());
    }

    // TODO: GET TESTS ON /name? (error handling etc.)
    // TODO: PUT/POST TESTS ON /name? (fail)
    // TODO: DELETE TESTS ON /name? (always 204)
    // TODO: GET TESTS ON /id (error handling etc.)
    // TODO: PUT TEST ON /id (should work)
    // TODO: DELETE TESTS ON /id (should work, 204)
    // TODO: POST REQUEST ON /id (fail)
}
