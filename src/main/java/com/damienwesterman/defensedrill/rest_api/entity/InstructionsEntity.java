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

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "instructions")
@IdClass(InstructionsEntity.InstructionId.class)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
@ToString
public class InstructionsEntity {
    @Id
    @NotNull
    private Long drillId;

    @Id
    // TODO: Check these, because validation happens at two places, during repository checks AND at controller time, so maybe have separate DTOs with separate constraints
    // @NotNull -> TBD by its index position in DrillEntity.instructions list
    private Long number;

    @Column
    @NotEmpty
    @Size(max = 511)
    private String description;

    @Column
    @NotEmpty
    @Size(max = 4095)
    /** Pipeling delimited string of steps */
    private String steps;// TODO: make getters/setters for this for lists

    @Column
    @Size(max = 127)
    /** Video ID correlates to the Jellyfin Item ID */
    private String videoId;

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @Builder
    @EqualsAndHashCode
    @ToString
    public static class InstructionId implements Serializable {
        private Long drillId;
        private Long number;
    }
}
