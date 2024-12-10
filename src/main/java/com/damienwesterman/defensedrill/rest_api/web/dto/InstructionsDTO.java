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

import org.springframework.lang.NonNull;

import com.damienwesterman.defensedrill.rest_api.entity.InstructionsEntity;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * DTO for all requests involving {@link InstructionsEntity}.
 * <br><br>
 * Used for incoming and outgoing requests to represent instructionsEntity objects.
 */
@Data
@RequiredArgsConstructor
public class InstructionsDTO {
    @NotEmpty
    @Size(min = 1, max = 511)
    private String description;

    @NotEmpty
    private List<String> steps;

    @JsonProperty("video_id")
    private String videoId;

    /**
     * Convert the DTO into its equivalent {@link InstructionsEntity} representation.
     *
     * @param instructions InstructionsEntity object.
     */
    public InstructionsDTO(@NonNull InstructionsEntity instructions) {
        this.description = instructions.getDescription();
        this.steps = instructions.getStepsAsList();
        this.videoId = instructions.getVideoId();
    }
}
