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
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.mockito.ArgumentMatchers.argThat;
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
    @MockitoBean
    CategorySerivce service;

    // Control things
    CategoryEntity category1;

    final Long ID_1 = 1L;
    final String NAME_1 = "Name 1";
    final String DESCRIPTION_1 = "Despcription 1";
    final Long TIMESTAMP_1 = 12345L;

    @BeforeEach
    public void setup() {
        category1 = CategoryEntity.builder()
                        .id(ID_1)
                        .updateTimestamp(TIMESTAMP_1)
                        .name(NAME_1)
                        .description(DESCRIPTION_1)
                        .build();
    }

    @Test
    public void test_rootEndpoint_get_withNoItemsInDB_returnsStatus204() throws Exception {
        when(service.findAll()).thenReturn(List.of());

        mockMvc.perform(get(CategoryController.ENDPOINT))
            .andExpect(status().isNoContent());
    }

    @Test
    public void test_rootEndpoint_get_withTwoItemsInDB_returnList() throws Exception {
        Long id2 = 2L;
        Long timestamp2 = 23456L;
        String name2 = "Name 2";
        String description2 = "Despcription 2";
        CategoryEntity category2 = CategoryEntity.builder()
                        .id(id2)
                        .updateTimestamp(timestamp2)
                        .name(name2)
                        .description(description2)
                        .build();
        when(service.findAll()).thenReturn(List.of(category1, category2));

        mockMvc.perform(get(CategoryController.ENDPOINT))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].id").value(ID_1))
            .andExpect(jsonPath("$[0].name").value(NAME_1))
            .andExpect(jsonPath("$[0].description").value(DESCRIPTION_1))
            .andExpect(jsonPath("$[1].id").value(id2))
            .andExpect(jsonPath("$[1].name").value(name2))
            .andExpect(jsonPath("$[1].description").value(description2));
    }

    @Test
    public void test_rootEndpoint_post_invalidArgumentWithNoObject() throws Exception {
        mockMvc.perform(post(CategoryController.ENDPOINT))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").exists())
            .andExpect(jsonPath("$.message").exists())
            .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void test_rootEndpoint_post_invalidArgumentWithEmptyObject() throws Exception {
        mockMvc.perform(post(CategoryController.ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").exists())
            .andExpect(jsonPath("$.message").exists())
            .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void test_rootEndpoint_post_invalidArgumentWithWrongObject() throws Exception {
        mockMvc.perform(post(CategoryController.ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"wrong\":\"field\"}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").exists())
            .andExpect(jsonPath("$.message").exists())
            .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void test_rootEndpoint_post_shouldSucceedWithCorrectFields() throws Exception {
        CategoryEntity entityToSave = CategoryEntity.builder()
                                        .id(null)
                                        .name(NAME_1)
                                        .description(DESCRIPTION_1)
                                        .build();
        // Have to be specific with this, as we cannot control what updateTimestamp will be
        when(service.save(categoryMatcher())).thenReturn(category1);

        mockMvc.perform(post(CategoryController.ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(entityToSave)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(ID_1))
            .andExpect(jsonPath("$.name").value(NAME_1))
            .andExpect(jsonPath("$.description").value(DESCRIPTION_1))
            .andExpect(header().string("Location", CategoryController.ENDPOINT + "/" + category1.getId()));

        // Have to be specific with this, as we cannot control what updateTimestamp will be
        verify(service).save(categoryMatcher());
    }

    @Test
    public void test_rootEndpoint_post_jakartaConstraintViolation_fails() throws Exception {
        // Any jakarta constraint violation should do, empty name is good
        CategoryEntity entityToSave = CategoryEntity.builder()
                                        .id(null)
                                        .updateTimestamp(TIMESTAMP_1)
                                        .name("")
                                        .description(DESCRIPTION_1)
                                        .build();

        mockMvc.perform(post(CategoryController.ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(entityToSave)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").exists())
            .andExpect(jsonPath("$.message").exists())
            .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void test_rootEndpoint_post_uniqueConstraintViolation_fails() throws Exception {
        // Have to be specific with this, as we cannot control what updateTimestamp will be
        when(service.save(categoryMatcher())).thenThrow(new DatabaseInsertException("Unique Cosntraint Violation"));
        mockMvc.perform(post(CategoryController.ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(category1)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").exists())
            .andExpect(jsonPath("$.message").exists())
            .andDo(MockMvcResultHandlers.print());
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
    public void test_updateEndpoint_get_shouldSucceed_withMatchingCategories() throws Exception {
        when(service.findAll(TIMESTAMP_1)).thenReturn(List.of(category1));

        mockMvc.perform(get(CategoryController.ENDPOINT + "/update?updateTimestamp=" + TIMESTAMP_1))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].id").value(ID_1))
            // Make sure that timestamp is not sent
            .andExpect(jsonPath("$[0].updateTimestamp").doesNotExist())
            .andExpect(jsonPath("$[0].name").value(NAME_1))
            .andExpect(jsonPath("$[0].description").value(DESCRIPTION_1));
    }

    @Test
    public void test_updateEndpoint_get_shouldSReturn204_withNoMatchingCategories() throws Exception {
        when(service.findAll(TIMESTAMP_1)).thenReturn(List.of());

        mockMvc.perform(get(CategoryController.ENDPOINT + "/update?updateTimestamp=" + TIMESTAMP_1))
            .andExpect(status().isNoContent());
    }

    @Test
    public void test_idEndpoint_get_succeedsWithExistingId() throws Exception {
        when(service.find(ID_1)).thenReturn(Optional.of(category1));

        mockMvc.perform(get(CategoryController.ENDPOINT + "/id/" + ID_1))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(ID_1))
            .andExpect(jsonPath("$.name").value(NAME_1))
            .andExpect(jsonPath("$.description").value(DESCRIPTION_1));

        verify(service).find(ID_1);
    }

    @Test
    public void test_idEndpoint_get_returns404WithNonExistentId() throws Exception {
        when(service.find(ID_1)).thenReturn(Optional.empty());

        mockMvc.perform(get(CategoryController.ENDPOINT + "/id/" + ID_1))
            .andExpect(status().isNotFound());

        verify(service).find(ID_1);
    }

    @Test
    public void test_idEndpoint_put_invalidArgumentWithNoObject() throws Exception {
        mockMvc.perform(put(CategoryController.ENDPOINT + "/id/" + ID_1))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").exists())
            .andExpect(jsonPath("$.message").exists())
            .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void test_idEndpoint_put_invalidArgumentWithEmptyObject() throws Exception {
        mockMvc.perform(put(CategoryController.ENDPOINT + "/id/" + ID_1)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").exists())
            .andExpect(jsonPath("$.message").exists())
            .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void test_idEndpoint_put_invalidArgumentWithWrongObject() throws Exception {
        mockMvc.perform(put(CategoryController.ENDPOINT + "/id/" + ID_1)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"wrong\":\"field\"}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").exists())
            .andExpect(jsonPath("$.message").exists())
            .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void test_idEndpoint_put_shouldSucceedWithCorrectFieldsAndExistingId() throws Exception {
        // Have to be specific with this, as we cannot control what updateTimestamp will be
        when(service.save(categoryMatcher())).thenReturn(category1);
        when(service.find(ID_1)).thenReturn(Optional.of(category1));

        mockMvc.perform(put(CategoryController.ENDPOINT + "/id/" + ID_1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(category1)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(ID_1))
            .andExpect(jsonPath("$.name").value(NAME_1))
            .andExpect(jsonPath("$.description").value(DESCRIPTION_1));

        // Have to be specific with this, as we cannot control what updateTimestamp will be
        verify(service).save(categoryMatcher());
    }

    @Test
    public void test_idEndpoint_put_entityIdNull_stillSucceedsEverythingElseValid() throws Exception {
        CategoryEntity entityToSend = CategoryEntity.builder()
                                        .id(null)
                                        .updateTimestamp(TIMESTAMP_1)
                                        .name(NAME_1)
                                        .description(DESCRIPTION_1)
                                        .build();
        // Have to be specific with this, as we cannot control what updateTimestamp will be
        when(service.save(categoryMatcher())).thenReturn(category1);
        when(service.find(ID_1)).thenReturn(Optional.of(category1));

        mockMvc.perform(put(CategoryController.ENDPOINT + "/id/" + ID_1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(entityToSend)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(ID_1))
            .andExpect(jsonPath("$.name").value(NAME_1))
            .andExpect(jsonPath("$.description").value(DESCRIPTION_1));

        // Have to be specific with this, as we cannot control what updateTimestamp will be
        verify(service, times(1)).save(categoryMatcher());
    }

    @Test
    public void test_idEndpoint_put_nonExistentIdFails() throws Exception {
        when(service.save(category1)).thenReturn(category1);
        when(service.find(ID_1)).thenReturn(Optional.empty());

        mockMvc.perform(put(CategoryController.ENDPOINT + "/id/" + ID_1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(category1)))
            .andExpect(status().isNotFound());

        verify(service, times(0)).save(category1);
    }

    @Test
    public void test_idEndpoint_put_idMismatchFromPathAndBody() throws Exception {
        mockMvc.perform(put(CategoryController.ENDPOINT + "/id/" + (ID_1 + 1))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(category1)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.id").doesNotExist())
            .andExpect(jsonPath("$.name").doesNotExist())
            .andExpect(jsonPath("$.description").doesNotExist())
            .andExpect(jsonPath("$.error").exists())
            .andExpect(jsonPath("$.message").exists())
            .andDo(MockMvcResultHandlers.print());

        verify(service, times(0)).save(category1);
    }

    @Test
    public void test_idEndpoint_put_jakartaCosntraintViolation_fails() throws Exception {
        category1.setName("");

        mockMvc.perform(put(CategoryController.ENDPOINT + "/id/" + ID_1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(category1)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.id").doesNotExist())
            .andExpect(jsonPath("$.name").doesNotExist())
            .andExpect(jsonPath("$.description").doesNotExist())
            .andExpect(jsonPath("$.error").exists())
            .andExpect(jsonPath("$.message").exists())
            .andDo(MockMvcResultHandlers.print());

        verify(service, times(0)).save(category1);
    }

    @Test
    public void test_idEndpoint_put_uniqueConstraintViolation_fails() throws Exception {
        // Have to be specific with this, as we cannot control what updateTimestamp will be
        when(service.save(categoryMatcher())).thenThrow(new DatabaseInsertException("Unique Cosntraint Violation"));
        when(service.find(ID_1)).thenReturn(Optional.of(category1));

        mockMvc.perform(put(CategoryController.ENDPOINT + "/id/" + ID_1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(category1)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.id").doesNotExist())
            .andExpect(jsonPath("$.name").doesNotExist())
            .andExpect(jsonPath("$.description").doesNotExist())
            .andExpect(jsonPath("$.error").exists())
            .andExpect(jsonPath("$.message").exists())
            .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void test_idEndpoint_post_fails() throws Exception {
        mockMvc.perform(post(CategoryController.ENDPOINT + "/id/" + ID_1))
            .andExpect(status().isMethodNotAllowed());
    }

    @Test
    public void test_idEndpoint_delete_alwaysSucceeds204() throws Exception {
        mockMvc.perform(delete(CategoryController.ENDPOINT + "/id/" + ID_1))
            .andExpect(status().isNoContent());

        verify(service, times(1)).delete(ID_1);
    }

    @Test
    public void test_nameEndpoint_get_succeedsWithExistingName() throws Exception {
        when(service.find(NAME_1)).thenReturn(Optional.of(category1));

        mockMvc.perform(get(CategoryController.ENDPOINT + "/name/" + NAME_1))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(ID_1))
            .andExpect(jsonPath("$.name").value(NAME_1))
            .andExpect(jsonPath("$.description").value(DESCRIPTION_1));

        verify(service).find(NAME_1);
    }

    @Test
    public void test_nameEndpoint_get_returns404WithNonExistentName() throws Exception {
        when(service.find(NAME_1)).thenReturn(Optional.empty());

        mockMvc.perform(get(CategoryController.ENDPOINT + "/name/" + NAME_1))
            .andExpect(status().isNotFound());

        verify(service).find(NAME_1);
    }

    @Test
    public void test_nameEndpoint_put_fails() throws Exception {
        mockMvc.perform(put(CategoryController.ENDPOINT + "/name/" + NAME_1))
            .andExpect(status().isMethodNotAllowed());
    }

    @Test
    public void test_nameEndpoint_post_fails() throws Exception {
        mockMvc.perform(post(CategoryController.ENDPOINT + "/name/" + NAME_1))
            .andExpect(status().isMethodNotAllowed());
    }

    @Test
    public void test_nameEndpoint_delete_fails() throws Exception {
        mockMvc.perform(delete(CategoryController.ENDPOINT + "/name/" + NAME_1))
            .andExpect(status().isMethodNotAllowed());
    }

    private CategoryEntity categoryMatcher() {
        return argThat(entity -> {
            return NAME_1.equals(entity.getName())
                && DESCRIPTION_1.equals(entity.getDescription());
        });
    }
}
