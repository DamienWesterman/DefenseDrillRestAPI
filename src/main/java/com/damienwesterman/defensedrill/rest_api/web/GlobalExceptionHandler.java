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

package com.damienwesterman.defensedrill.rest_api.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.damienwesterman.defensedrill.rest_api.exception.DatabaseInsertException;

import lombok.extern.slf4j.Slf4j;

// TODO: DOC COMMENTS
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(DatabaseInsertException.class)
    public ResponseEntity<String> handleDatabaseInsertException(DatabaseInsertException die) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(die.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception e) {
        log.error("Unhandled Error", e);
        return ResponseEntity.internalServerError().body("An unexpected error has occurred.");
    }
}
