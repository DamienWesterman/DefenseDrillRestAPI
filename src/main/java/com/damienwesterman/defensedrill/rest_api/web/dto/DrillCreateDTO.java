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

import org.springframework.lang.NonNull;

import com.damienwesterman.defensedrill.rest_api.entity.DrillEntity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO for creating a {@link DrillEntity}.
 * <br><br>
 * Used for incoming requests. Basically only contains a String name with validation.
 */
@Schema(
    name = "DrillCreateObject",
    description = "Object to create a new Drill."
)
@Data
public class DrillCreateDTO {
    @Schema(
        description = "Name of the Drill. Must be unique.",
        example = "Round Kick"
    )
    @NotEmpty
    @Size(min = 1, max = 255)
    private String name;

    /**
     * Convert the DTO into its equivalent {@link DrillEntity} representation.
     *
     * @return DrillEntity object.
     */
    @NonNull
    public DrillEntity toEntity() {
        return DrillEntity.builder()
                .name(this.name)
                .categories(new ArrayList<>())
                .subCategories(new ArrayList<>())
                .relatedDrills(new ArrayList<>())
                .instructions(new ArrayList<>())
                .build();
    }
}
