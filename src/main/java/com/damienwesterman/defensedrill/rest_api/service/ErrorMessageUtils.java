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

package com.damienwesterman.defensedrill.rest_api.service;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.transaction.TransactionSystemException;

import com.damienwesterman.defensedrill.rest_api.exception.DatabaseInsertException;

import jakarta.validation.ConstraintViolationException;

/**
 * Utility class providing static methods for converting database operation exceptions into a
 * user friendly error message.
 */
/* package-private */ class ErrorMessageUtils {
    /**
     * Map of named database constraints to user friendly messages.
     * <br><br>
     * Must reflect all constraint names in db/migration.
     */
    private final static Map<String, String> constraintErrorMessageMap = Map.ofEntries(
        Map.entry(  "constraint_drills_unique_name",          "Name already exists."),
        Map.entry(  "constraint_categories_unique_name",      "Name already exists."),
        Map.entry(  "constraint_sub_categories_unique_name",  "Name already exists."),
        Map.entry(  "constraint_dcjoin_fk_drill_id",          "Drill does not exist."),
        Map.entry(  "constraint_dcjoin_fk_category_id",       "Category does not exist."),
        Map.entry(  "constraint_dscjoin_fk_drill_id",         "Drill does not exist."),
        Map.entry(  "constraint_dscjoin_fk_sub_category_id",  "Sub-Category does not exist."),
        Map.entry(  "constraint_rd_fk_primary_drill",         "Primary Drill does not exist."),
        Map.entry(  "constraint_rd_fk_related_drill",         "Related Drill does not exist."),
        Map.entry(  "constraint_instructions_fk_drill_id",    "Drill does not exist.")
    );
    private final static String GENERIC_ERROR_MESSAGE = "An error has occurred.";

    /**
     * Private Constructor.
     */
    private ErrorMessageUtils() { }

    /**
     * Wrapper function to call {@link JpaRepository#save(Object)}. Handles any exception the database might
     * throw and re-throws it with a user friendly error message.
     *
     * @param <E> E - Database Entity.
     * @param <R> R - {@link JpaRepository} for E entity.
     * @param entity The entity to attempt to save.
     * @param repo Repository to use for the save operation.
     * @return The saved entity.
     * @throws DatabaseInsertException Thrown when there is any issue saving the entity.
     */
    @NonNull
    public static <E, R extends JpaRepository<E, Long>> E trySave(
            @NonNull E entity, @NonNull R repo) throws DatabaseInsertException {
        try {
            return repo.save(entity);
        } catch (ConstraintViolationException |
                    DataIntegrityViolationException |
                    InvalidDataAccessApiUsageException |
                    TransactionSystemException e) {
            throw new DatabaseInsertException(
                exceptionToErrorMessage(e), e
            );
        }
    }

    /**
     * Convert an exception into a user friendly error message.
     *
     * @param e Thrown exception.
     * @return User friendly error message string.
     */
    @NonNull
    private static String exceptionToErrorMessage(@NonNull Exception e) {
        if (e instanceof DataIntegrityViolationException) {
            return sqlExceptionToString(e.getLocalizedMessage());
        } else if (e instanceof ConstraintViolationException || e instanceof TransactionSystemException) {
            // TransactionSystemException occurs on top of a ConstraintViolationException, so they are handled the same
            return jakartaExceptionToErrorMessage(e.getLocalizedMessage());
        } else if (e instanceof InvalidDataAccessApiUsageException) {
            return transientExceptionToErrorMessage(e.getLocalizedMessage());
        }

        return GENERIC_ERROR_MESSAGE;
    }

    /**
     * Convert a database generated exception into a user friendly error message.
     *
     * @param exception {@link DataIntegrityViolationException} or {@link TransactionSystemException} string error message.
     * @return User friendly error message string.
     */
    @NonNull
    private static String sqlExceptionToString(String exception) {
        // Need to parse the exception to find the sql named constraint that was violated
        for (String constraintKey : constraintErrorMessageMap.keySet()) {
            if (exception.contains(constraintKey)) {
                // Found the constraint that was violated
                return constraintErrorMessageMap.get(constraintKey);
            }
        }

        return GENERIC_ERROR_MESSAGE;
    }

    /**
     * Convert a jakarta generated exception into a user friendly error message.
     *
     * @param exception {@link ConstraintViolationException} string error message.
     * @return User friendly error message string.
     */
    @NonNull
    private static String jakartaExceptionToErrorMessage(String exception) {
        StringBuilder errorMessage = new StringBuilder();

        // Regular expression pattern to match the property path and interpolated message
        Pattern pattern = Pattern.compile("interpolatedMessage='(.*?)',.*?propertyPath=(.*?),");
        Matcher matcher = pattern.matcher(exception);

        while (matcher.find()) {
            String constraintViolation = matcher.group(1).trim();
            String propertyInViolation = matcher.group(2).trim();

            // Format the error string and capitalize the first letter
            propertyInViolation = Character.toUpperCase(propertyInViolation.charAt(0)) + propertyInViolation.substring(1);
            errorMessage.append(propertyInViolation).append(" ").append(constraintViolation).append(". ");
        }

        if (0 < errorMessage.length()) {
            return errorMessage.toString();
        } else {
            return GENERIC_ERROR_MESSAGE;
        }
    }

    /**
     * Convert a TransientException into a user friendly error message.
     * <br><br>
     * This exception occurs when an entity with a foreign key is saved with an entity that
     * does not exist yet.
     *
     * @param exception {@link InvalidDataAccessApiUsageException} string error message.
     * @return User friendly error message string.
     */
    @NonNull
    private static String transientExceptionToErrorMessage(String exception) {
        Set<String> errorMessagesSet = new HashSet<>();

        // Use regex to extract the entities that are causing the problem
        Pattern pattern = Pattern.compile("com.damienwesterman.defensedrill.rest_api.entity.(.*?)Entity");
        Matcher matcher = pattern.matcher(exception);

        while (matcher.find()) {
            String entity = matcher.group(1).trim();
            errorMessagesSet.add(entity);
        }

        if (!errorMessagesSet.isEmpty()) {
            StringBuilder errorMessage = new StringBuilder();
            errorMessage.append("Entity does not exist in database: ");
            errorMessage.append(String.join(", ", errorMessagesSet));

            return errorMessage.toString();
        } else {
            return GENERIC_ERROR_MESSAGE;
        }
    }
}
