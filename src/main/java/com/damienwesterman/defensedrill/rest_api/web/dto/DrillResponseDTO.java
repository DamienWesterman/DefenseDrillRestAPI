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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.lang.NonNull;

import com.damienwesterman.defensedrill.rest_api.entity.CategoryEntity;
import com.damienwesterman.defensedrill.rest_api.entity.DrillEntity;
import com.damienwesterman.defensedrill.rest_api.entity.SubCategoryEntity;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for responses of {@link DrillEntity} types.
 * <br><br>
 * This DTO should only be outbound, <b><i>NEVER</b></i> inbound requests as it contains no
 * input validation.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DrillResponseDTO {
    private Long id;
    private String name;
    @JsonProperty("categories")
    private List<Long> categoryIds;
    @JsonProperty("sub_categories")
    private List<Long> subCategoryIds;
    @JsonProperty("related_drills")
    private List<Long> relatedDrillIds;

    /**
     * Parameterized constructor using a DrillEntity object.
     *
     * @param drill DrillEntity object to represent in a DTO.
     */
    public DrillResponseDTO(@NonNull DrillEntity drill) {
        this.id = drill.getId();
        this.name = drill.getName();

        if (null == drill.getCategories()) {
            this.categoryIds = new ArrayList<>();
        } else {
            this.categoryIds = drill.getCategories().stream()
                                    .map(CategoryEntity::getId)
                                    .collect(Collectors.toList());
        }

        if (null == drill.getSubCategories()) {
            this.subCategoryIds = new ArrayList<>();
        } else {
            this.subCategoryIds = drill.getSubCategories().stream()
                                    .map(SubCategoryEntity::getId)
                                    .collect(Collectors.toList());
        }

        this.relatedDrillIds = drill.getRelatedDrills();
    }
}
