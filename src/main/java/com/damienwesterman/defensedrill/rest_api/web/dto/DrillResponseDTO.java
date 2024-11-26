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

package com.damienwesterman.defensedrill.rest_api.web.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.damienwesterman.defensedrill.rest_api.entity.CategoryEntity;
import com.damienwesterman.defensedrill.rest_api.entity.DrillEntity;
import com.damienwesterman.defensedrill.rest_api.entity.SubCategoryEntity;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

// TODO: Doc Comments - to be used for returned drills, NO validation so only should go out
@Getter
public class DrillResponseDTO {
    private final Long id;
    private final String name;
    @JsonProperty("categories")
    private final List<Long> categoryIds;
    @JsonProperty("sub_categories")
    private final List<Long> subCategoryIds;
    @JsonProperty("related_drills")
    private final List<Long> relatedDrillIds;

    public DrillResponseDTO(DrillEntity drill) {
        this.id = drill.getId();
        this.name = drill.getName();
        this.categoryIds = drill.getCategories().stream()
                                .map(CategoryEntity::getId)
                                .collect(Collectors.toList());
        this.subCategoryIds = drill.getSubCategories().stream()
                                .map(SubCategoryEntity::getId)
                                .collect(Collectors.toList());
        this.relatedDrillIds = drill.getRelatedDrills();
    }
}
