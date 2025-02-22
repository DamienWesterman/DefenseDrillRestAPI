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

import com.damienwesterman.defensedrill.rest_api.entity.SubCategoryEntity;
import com.damienwesterman.defensedrill.rest_api.service.SubCategorySerivce;

import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controller responsible for CRUD operations for {@link SubCategoryEntity} objects with validation.
 */
@Tag(
    name = "SubCategory Controller",
    description = "Offers all CRUD operations for SubCategories in the database."
)
@RestController
@RequestMapping(SubCategoryController.ENDPOINT)
public class SubCategoryController extends AbstractCategoryController<SubCategoryEntity, SubCategorySerivce> {
    public final static String ENDPOINT = "/sub_category";

    public SubCategoryController(SubCategorySerivce subCategorySerivce) {
        super(subCategorySerivce);
    }

    @Override
    protected String getEndpoint() {
        return ENDPOINT;
    }
}