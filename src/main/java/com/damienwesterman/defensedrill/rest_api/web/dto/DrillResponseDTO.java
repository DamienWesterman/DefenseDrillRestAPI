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
import org.springframework.lang.Nullable;

import com.damienwesterman.defensedrill.rest_api.entity.CategoryEntity;
import com.damienwesterman.defensedrill.rest_api.entity.DrillEntity;
import com.damienwesterman.defensedrill.rest_api.entity.SubCategoryEntity;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for responses of {@link DrillEntity} types.
 * <br><br>
 * This DTO should only be outbound, <b><i>NEVER</b></i> inbound requests as it contains no
 * input validation.
 * <br><br>
 * NOTE: Any changes here must also be reflected in the MVC repo.
 */
@Schema(
    name = "DrillInfo",
    description = "All information relating to a Drill."
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DrillResponseDTO {
    @Schema(
        description = "Database generated ID.",
        example = "12345"
    )
    private Long id;

    @Schema(
        description = "Name of the Drill.",
        example = "Round Kick"
    )
    private String name;

    @Schema(
        description = "List of Category IDs the Drill belongs to."
    )
    private List<CategoryEntity> categories;

    @Schema(
        description = "List of SubCategory IDs the Drill belongs to."
    )
    @JsonProperty("sub_categories")
    private List<SubCategoryEntity> subCategories;

    @Schema(
        description = "List of Drill IDs this Drill mentions."
    )
    @JsonProperty("related_drills")
    private List<DrillRelatedDTO> relatedDrills;

    @Schema(
        description = "List of Instructional how-tos to perform this Drill."
    )
    private List<InstructionsDTO> instructions;

    /**
     * Parameterized constructor using a DrillEntity object.
     *
     * @param drill DrillEntity object to represent in a DTO.
     */
    public DrillResponseDTO(@NonNull DrillEntity drill) {
        this(drill, null);
    }

    /**
     * Parameterized constructor using a DrillEntity object.
     *
     * @param drill DrillEntity object to represent in a DTO.
     * @param relatedDrills List of related Drills.
     */
    public DrillResponseDTO(@NonNull DrillEntity drill, @Nullable List<DrillEntity> relatedDrills) {
        this.id = drill.getId();
        this.name = drill.getName();

        if (null == drill.getCategories()) {
            this.categories = new ArrayList<>();
        } else {
            this.categories = drill.getCategories();
        }

        if (null == drill.getSubCategories()) {
            this.subCategories = new ArrayList<>();
        } else {
            this.subCategories = drill.getSubCategories();
        }

        if (null == relatedDrills) {
            this.relatedDrills = new ArrayList<>();
        } else {
            this.relatedDrills = relatedDrills.stream()
                                    .map(DrillRelatedDTO::new)
                                    .collect(Collectors.toList());
        }

        if (null == drill.getInstructions()) {
            this.instructions = new ArrayList<>();
        } else {
            this.instructions = drill.getInstructions().stream()
                                    .map(InstructionsDTO::new)
                                    .collect(Collectors.toList());
        }
    }
}
