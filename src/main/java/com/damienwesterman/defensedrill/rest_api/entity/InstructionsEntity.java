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
import java.util.ArrayList;
import java.util.List;

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


/**
 * Database Entity to contain all the information for a single set of Instructions for a drill.
 */
@Entity
@Table(name = "instructions")
@IdClass(InstructionsEntity.InstructionId.class)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InstructionsEntity {
    @Id
    @NotNull
    private Long drillId;

    @Id
    @NotNull
    private Long number;

    @Column
    @NotEmpty
    @Size(min = 1, max = 511)
    private String description;

    @Column
    @NotEmpty
    @Size(min = 1, max = 4095)
    /** Pipe delimited string of steps */
    private String steps;

    @Column
    @Size(max = 127)
    /** Video ID correlates to the Jellyfin Item ID */
    private String videoId;

    public void setStepsFromList(List<String> stepsList) {
        this.steps = String.join("|", stepsList);
    }

    /**
     * Retrieve the steps as a List rather than single string.
     *
     * @return {@code List<String>} of steps. Modifying this returned list
     *         will NOT modify the internal state. {@link #setStepsFromList(List)}
     *         must be called to save any state changes.
     */
    public List<String> getStepsAsList() {
        // Make sure it is a mutable array
        return new ArrayList<String>(List.of(steps.split("\\|")));
    }

    /**
     * Composite ID class used for {@link InstructionsEntity}'s primary key.
     */
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
