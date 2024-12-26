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

import org.springframework.lang.NonNull;

import com.damienwesterman.defensedrill.rest_api.entity.DrillEntity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for related drills of {@link DrillEntity} types.
 * <br><br>
 * This DTO should only be outbound, <b><i>NEVER</b></i> inbound requests as it contains no
 * input validation.
 * <br><br>
 * NOTE: Any changes here must also be reflected in the MVC repo.
 */
@Schema(
    name = "RelatedDrill",
    description = "Name and ID of a related Drill."
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DrillRelatedDTO {
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

    /**
     * Parameterized constructor using a DrillEntity object.
     *
     * @param drill DrillEntity object to represent in a DTO.
     */
    public DrillRelatedDTO(@NonNull DrillEntity drill) {
        this.id = drill.getId();
        this.name = drill.getName();
    }
}
