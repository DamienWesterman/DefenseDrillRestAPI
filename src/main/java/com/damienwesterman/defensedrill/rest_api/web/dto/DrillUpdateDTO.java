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
import com.damienwesterman.defensedrill.rest_api.entity.DrillEntity;
import com.damienwesterman.defensedrill.rest_api.entity.InstructionsEntity;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

// TODO: DOC COMMENTS / USAGE
@Data
public class DrillUpdateDTO {
    @NotNull
    private Long id;

    @NotEmpty
    @Size(min = 1, max = 255)
    private String name;

    @Nullable
    @JsonProperty("categories")
    private List<Long> categoryIds;

    @Nullable
    @JsonProperty("sub_categories")
    private List<Long> subCategoryIds;

    @Nullable
    @JsonProperty("related_drills")
    private List<Long> relatedDrills;

    @Nullable
    private List<InstructionsDTO> instructions;

    // TODO: NOTE THAT THIS DOES NOT SET categories or subCategories list
    public DrillEntity toEntity() {
        // Set up the list of InstructionEntity objects
        List<InstructionsEntity> instructionEntities = new ArrayList<>();
        if (null != this.instructions && 0 < this.instructions.size()) {
            for (int i = 0; i < this.instructions.size(); i++) {
                instructionEntities.add(InstructionsEntity.builder()
                    .drillId(this.id)
                    .number((long) i)
                    .description(instructions.get(i).getDescription())
                    .steps(null)
                    .videoId(instructions.get(i).getVideoId())
                    .build());
                instructionEntities.get(i)
                    .setStepsFromList(instructions.get(i).getSteps());
            }
        }

        return DrillEntity.builder()
            .id(this.id)
            .name(this.name)
            .categories(new ArrayList<>())
            .subCategories(new ArrayList<>())
            .relatedDrills(this.relatedDrills)
            .instructions(instructionEntities)
            .build();
    }
}
