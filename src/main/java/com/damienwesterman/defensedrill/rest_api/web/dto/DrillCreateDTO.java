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

import com.damienwesterman.defensedrill.rest_api.entity.DrillEntity;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

// TODO: Doc comments / usage
@Data
public class DrillCreateDTO {
    @NotEmpty
    @Size(min = 1, max = 255)
    private String name;

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
