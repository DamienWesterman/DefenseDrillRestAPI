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

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.damienwesterman.defensedrill.rest_api.exception.DatabaseInsertException;

import lombok.extern.slf4j.Slf4j;

// TODO: DOC COMMENTS
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private static final String KEY_ERROR = "error";
    private static final String KEY_MESSAGE = "message";

    @Override
    @Nullable
    protected ResponseEntity<Object> handleMethodArgumentNotValid(@NonNull MethodArgumentNotValidException ex,
            @NonNull HttpHeaders headers, @NonNull HttpStatusCode status, @NonNull WebRequest request) {
        Map<String, String> errorBody = new HashMap<>();
        errorBody.put(KEY_ERROR, "Malformed Argument");
        StringBuilder errorMessage = new StringBuilder();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errorMessage.append(Character.toUpperCase(error.getField().charAt(0)));
            errorMessage.append(error.getField().substring(1));
            errorMessage.append(' ');
            errorMessage.append(error.getDefaultMessage());
            errorMessage.append(". ");
        });
        errorBody.put(KEY_MESSAGE, errorMessage.toString());

        return ResponseEntity.badRequest().body(errorBody);
    }

    @ExceptionHandler(DatabaseInsertException.class)
    public ResponseEntity<Map<String, String>> handleDatabaseInsertException(DatabaseInsertException die) {
        Map<String, String> errorBody = new HashMap<>();
        errorBody.put(KEY_ERROR, "Database Insert Error");
        errorBody.put(KEY_MESSAGE, die.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorBody);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, String>> handleNoSuchElementException(NoSuchElementException nsee) {
        Map<String, String> errorBody = new HashMap<>();
        errorBody.put(KEY_ERROR, "Resource Not Found");
        errorBody.put(KEY_MESSAGE, nsee.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorBody);
    }

    @ExceptionHandler(IndexOutOfBoundsException.class)
    public ResponseEntity<Map<String, String>> handleIndexOutOfBoundsException(IndexOutOfBoundsException ioobe) {
        Map<String, String> errorBody = new HashMap<>();
        errorBody.put(KEY_ERROR, "Index Out Of Bounds");
        errorBody.put(KEY_MESSAGE, ioobe.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorBody);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception e) {
        log.error("Unhandled Error", e);

        Map<String, String> errorBody = new HashMap<>();
        errorBody.put(KEY_ERROR, "Unknonw Error");
        errorBody.put(KEY_MESSAGE, "An unexpected error has occurred.");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorBody);
    }
}
