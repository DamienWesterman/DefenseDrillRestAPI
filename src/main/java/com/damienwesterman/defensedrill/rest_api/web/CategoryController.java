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

package com.damienwesterman.defensedrill.rest_api.web;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.damienwesterman.defensedrill.rest_api.entity.CategoryEntity;
import com.damienwesterman.defensedrill.rest_api.service.CategorySerivce;

import lombok.RequiredArgsConstructor;

// TODO: DOC COMMENTS
@RestController
@RequestMapping(CategoryController.ENDPOINT)
@RequiredArgsConstructor
public class CategoryController {
    public final static String ENDPOINT = "/category";

    private final CategorySerivce service;

    @GetMapping
    public ResponseEntity<List<CategoryEntity>> getAll() {
        List<CategoryEntity> categories = service.findAll();

        if (categories.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(categories);
    }

    @PostMapping
    public ResponseEntity<CategoryEntity> insertNewCategory(@RequestBody CategoryEntity category) {
        // Does not check @Valid, this is done down the line with custom error messages
        CategoryEntity createdCategory = service.save(category);
        return ResponseEntity
            .created(URI.create(ENDPOINT + "/" + createdCategory.getId()))
            .body(createdCategory);
    }
}
