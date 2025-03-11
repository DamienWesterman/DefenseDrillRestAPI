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

import java.net.URI;
import java.time.Instant;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.damienwesterman.defensedrill.rest_api.entity.AbstractCategoryEntity;
import com.damienwesterman.defensedrill.rest_api.repository.AbstractCategoryRepo;
import com.damienwesterman.defensedrill.rest_api.service.AbstractCategoryService;
import com.damienwesterman.defensedrill.rest_api.web.dto.ErrorMessageDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Abstract superclass for {@link CategoryController} and {@link SubCategoryController}.
 * <br><br>
 * Responsible for CRUD operations for {@link AbstractCategoryEntity} objects with validation.
 */
@RequiredArgsConstructor
public abstract class AbstractCategoryController
        <E extends AbstractCategoryEntity,
        S extends AbstractCategoryService<E, ? extends AbstractCategoryRepo<E>>> {
    protected final S service;

    /**
     * Endpoint to return all AbstractCategoryEntity objects.
     *
     * @return ResponseEntity with List of the AbstractCategoryEntity objects.
     */
    @Operation(
        summary = "Retrieve all categories.",
        description = "Returns a list of all categories in the database."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Categories exist in the database and were returned."),
        @ApiResponse(responseCode = "204", description = "No categories exist in the database.",
            content = @Content(/* No Content */))
    })
    @GetMapping
    public ResponseEntity<List<E>> getAll() {
        List<E> abstractCategories = service.findAll();

        if (abstractCategories.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(abstractCategories);
    }

    /**
     * Endpoint to return all AbstractCategoryEntity objects that were updated after the given UTC time.
     *
     * @param updateTimestamp UTC milliseconds since epoch.
     * @return ResponseEntity with list of AbstractCategoryEntity objects.
     */
    @Operation(
        summary = "Retrieve all categories updated after a specified time.",
        description = "Returns a list of categories that were updated after the given timestamp. "
            + "The timestamp must be given in milliseconds since epoch in UTC."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Categories have been updated since the given time and were returned."),
        @ApiResponse(responseCode = "204", description = "No categories have been updated since the given time.",
            content = @Content(/* No Content */))
    })
    @GetMapping("/update")
    public ResponseEntity<List<E>> getAllAfterTimestamp(
                @RequestParam Long updateTimestamp) {
        List<E> abstractCategories = service.findAll(updateTimestamp);

        if (abstractCategories.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(abstractCategories);
    }

    /**
     * Endpoint to insert a new AbstractCategoryEntity into the database. With validation.
     *
     * @param abstractCategory Entity to create.
     * @return ResponseEntity containing the created entity.
     */
    @Operation(
        summary = "Insert a new category.",
        description = "Create a new category in the database. Returns the newly created category."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Category was created successfully."),
        @ApiResponse(responseCode = "400", description = "Issue with request, check returned error message for details.",
            content = @Content(schema = @Schema(implementation = ErrorMessageDTO.class)))
    })
    @PostMapping
    public ResponseEntity<E> insertNewAbstractCategory(@RequestBody @Valid E abstractCategory) {
        abstractCategory.setUpdateTimestamp(Instant.now().toEpochMilli());
        E createdAbstractCategory = service.save(abstractCategory);
        return ResponseEntity
            .created(URI.create(getEndpoint() + "/" + createdAbstractCategory.getId()))
            .body(createdAbstractCategory);
    }

    /**
     * Endpoint to find an AbstractCategoryEntity by its ID.
     *
     * @param id ID of the AbstractCategoryEntity.
     * @return ResponseEntity containing the found entity.
     */
    @Operation(
        summary = "Find a category by its ID.",
        description = "Search to see if a category exists by the given ID."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Category was found by the given ID."),
        @ApiResponse(responseCode = "404", description = "No category exists with the given ID.",
            content = @Content(/* No Content */))
    })
    @GetMapping("/id/{id}")
    public ResponseEntity<E> getAbstractCategoryById(@PathVariable Long id) {
        return service.find(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Endpoint to update an AbstractCategoryEntity by its ID.
     *
     * @param id ID of the entity to update.
     * @param abstractCategory Entity to udpate.
     * @return ResponseEntity with the updated entity.
     */
    @Operation(
        summary = "Update a category by its ID",
        description = "Update a category. ID must equal the path ID or be left null in the object. "
            + "Returns the newly updated category."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Category was updated successfully."),
        @ApiResponse(responseCode = "400", description = "Issue with request, check returned error message for details.",
            content = @Content(schema = @Schema(implementation = ErrorMessageDTO.class))),
        @ApiResponse(responseCode = "404", description = "No category exists with the given ID.",
            content = @Content(/* No Content */))
    })
    @PutMapping("/id/{id}")
    public ResponseEntity<Object> updateAbstractCategoryById(
            @PathVariable Long id, @RequestBody @Valid E abstractCategory) {
        if (null != abstractCategory.getId() && !abstractCategory.getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorMessageDTO.builder()
                .error("ID Mismatch")
                .message("ID provided in path does not match ID provided in request body.")
                .build());
        }

        if (service.find(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if (null == abstractCategory.getId()) {
            abstractCategory.setId(id);
        }

        abstractCategory.setUpdateTimestamp(Instant.now().toEpochMilli());

        E updatedAbstractCategory = service.save(abstractCategory);
        return ResponseEntity.ok(updatedAbstractCategory);
    }

    /**
     * Endpoint to delete an AbstractCategoryEntity by its ID.
     *
     * @param id ID of the entity to delete.
     * @return Empty ResponseEntity.
     */
    @Operation(
        summary = "Delete a category by its ID.",
        description = "Remove a category from the database using the category ID."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Deletion was successful or ID did not exist anyway.",
            content = @Content(/* No Content */))
    })
    @DeleteMapping("/id/{id}")
    public ResponseEntity<String> deleteAbstractCategoryById(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Endpoint to find an AbstractCategoryEntity by its name. Case insensitive.
     *
     * @param name Name of the AbstractCategoryEntity.
     * @return ResponseEntity containing the found entity.
     */
    @Operation(
        summary = "Find a category by its name.",
        description = "Search to see if a category exists by the given name. Case insensitive."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Category was found by the given name."),
        @ApiResponse(responseCode = "404", description = "No category exists with the given name.",
            content = @Content(/* No Content */))
    })
    @GetMapping("/name/{name}")
    public ResponseEntity<E> getAbstractCategoryByName(@PathVariable String name) {
        return service.find(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    protected abstract String getEndpoint();
}
