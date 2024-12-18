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

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO to send descriptive error messages back to caller.
 * <br><br>
 * This is an outbound request only.
 * <br><br>
 * NOTE: Any changes here must also be reflected in the MVC repo.
 */
@Schema(
    name = "ErrorMessage",
    description = "Error message containing user friendly message."
)
@Builder
@Getter
@Setter
public class ErrorMessageDTO {
    @Schema(
        description = "Error type.",
        example = "Malformed Argument"
    )
    private String error;

    @Schema(
        description = "Detailed user friendly error message.",
        example = "Name must not be empty."
    )
    private String message;
}
