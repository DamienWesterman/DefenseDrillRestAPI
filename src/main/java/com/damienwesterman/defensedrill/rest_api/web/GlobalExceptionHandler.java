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

import java.util.NoSuchElementException;

import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.damienwesterman.defensedrill.rest_api.exception.DatabaseInsertException;
import com.damienwesterman.defensedrill.rest_api.web.dto.ErrorMessageDTO;

import lombok.extern.slf4j.Slf4j;

/**
 * Error handling controller for all exceptions.
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @Override
    @Nullable
    protected ResponseEntity<Object> handleMethodArgumentNotValid(@NonNull MethodArgumentNotValidException ex,
            @NonNull HttpHeaders headers, @NonNull HttpStatusCode status, @NonNull WebRequest request) {
        // Ex. user provides bad jakarta constraints and fails validation
        StringBuilder errorMessage = new StringBuilder();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errorMessage.append(Character.toUpperCase(error.getField().charAt(0)));
            errorMessage.append(error.getField().substring(1));
            errorMessage.append(' ');
            errorMessage.append(error.getDefaultMessage());
            errorMessage.append(". ");
        });

        return ResponseEntity.badRequest()
            .body(ErrorMessageDTO.builder()
                    .error("Malformed Argument")
                    .message(errorMessage.toString())
                    .build());
    }

    @Override
    @Nullable
    protected ResponseEntity<Object> handleTypeMismatch(@NonNull TypeMismatchException ex, @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status, @NonNull WebRequest request) {
        // Ex. user provides a String for a Long path ID variable
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorMessageDTO.builder()
                    .error("Type Mismatch")
                    .message(ex.getLocalizedMessage())
                    .build());
    }

    @Override
    @Nullable
    protected ResponseEntity<Object> handleHttpMessageNotReadable(@NonNull HttpMessageNotReadableException ex,
            @NonNull HttpHeaders headers, @NonNull HttpStatusCode status, @NonNull WebRequest request) {
        // Ex. user neglects to provide correct body arguments and fails validation
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorMessageDTO.builder()
                .error("Missing Body")
                .message("A body is required for this request.")
                .build());
    }

    @ExceptionHandler(DatabaseInsertException.class)
    public ResponseEntity<ErrorMessageDTO> handleDatabaseInsertException(DatabaseInsertException die) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorMessageDTO.builder()
                    .error("Database Insert Error")
                    .message(die.getMessage())
                    .build());
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorMessageDTO> handleNoSuchElementException(NoSuchElementException nsee) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ErrorMessageDTO.builder()
                    .error("Resource Not Found")
                    .message(nsee.getMessage())
                    .build());
    }

    @ExceptionHandler(IndexOutOfBoundsException.class)
    public ResponseEntity<ErrorMessageDTO> handleIndexOutOfBoundsException(IndexOutOfBoundsException ioobe) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ErrorMessageDTO.builder()
                    .error("Index Out Of Bounds")
                    .message(ioobe.getMessage())
                    .build());
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ErrorMessageDTO> handleLockingFailureException(ObjectOptimisticLockingFailureException oolfe) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ErrorMessageDTO.builder()
                    .error("Update Conflict")
                    .message("Old data: please refresh and try again")
                    .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessageDTO> handleGenericException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorMessageDTO.builder()
                    .error("Unknonw Error")
                    .message("An unexpected error has occurred.")
                    .build());
    }
}
