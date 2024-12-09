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

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.damienwesterman.defensedrill.rest_api.entity.CategoryEntity;
import com.damienwesterman.defensedrill.rest_api.service.CategorySerivce;

// TODO: Swagger Comments (on the DTOs?)
/**
 * Controller responsible for CRUD operations for {@link CategoryEntity} objects with validation.
 */
@RestController
@RequestMapping(CategoryController.ENDPOINT)
public class CategoryController extends AbstractCategoryController<CategoryEntity, CategorySerivce> {
    public final static String ENDPOINT = "/category";

    public CategoryController(CategorySerivce categorySerivce) {
        super(categorySerivce);
    }

    @Override
    protected String getEndpoint() {
        return ENDPOINT;
    }
}
