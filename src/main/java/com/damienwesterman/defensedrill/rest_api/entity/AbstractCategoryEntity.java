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

package com.damienwesterman.defensedrill.rest_api.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Abstract superclass for {@link CategoryEntity} and {@link SubCategoryEntity}.
 */
@MappedSuperclass
@Data
@NoArgsConstructor
@SuperBuilder
public abstract class AbstractCategoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // @NotNull -> This can (and should) be null when creating a new entity
    @Schema(
        description = "Database generated ID.",
        example = "12345"
    )
    protected Long id;

    @Column(unique = true)
    @NotEmpty
    @Size(min = 1, max = 255)
    @Schema(
        description = "Consice category name.",
        example = "Strikes"
    )
    protected String name;

    @Column
    @NotEmpty
    @Size(min = 1, max = 511)
    @Schema(
        description = "Detailed category description.",
        example = "Using your arms and hands to strike your opponent."
    )
    protected String description;
}
