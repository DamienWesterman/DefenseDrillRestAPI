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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.damienwesterman.defensedrill.rest_api.entity.CategoryEntity;
import com.damienwesterman.defensedrill.rest_api.entity.DrillEntity;
import com.damienwesterman.defensedrill.rest_api.entity.InstructionsEntity;
import com.damienwesterman.defensedrill.rest_api.entity.SubCategoryEntity;
import com.damienwesterman.defensedrill.rest_api.exception.DatabaseInsertException;
import com.damienwesterman.defensedrill.rest_api.service.CategorySerivce;
import com.damienwesterman.defensedrill.rest_api.service.DrillService;
import com.damienwesterman.defensedrill.rest_api.service.SubCategorySerivce;
import com.damienwesterman.defensedrill.rest_api.web.dto.DrillCreateDTO;
import com.damienwesterman.defensedrill.rest_api.web.dto.DrillResponseDTO;
import com.damienwesterman.defensedrill.rest_api.web.dto.DrillUpdateDTO;
import com.damienwesterman.defensedrill.rest_api.web.dto.ErrorMessageDTO;
import com.damienwesterman.defensedrill.rest_api.web.dto.InstructionsDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controller responsible for CRUD operations for {@link DrillEntity} objects with validation.
 * Extends to control over {@link InstructionsEntity} held within the DrillEntity.
 */
@Tag(
    name = "Drill Controller",
    description = "Offers all CRUD operations for Drills in the database."
)
@RestController
@RequestMapping(DrillController.ENDPOINT)
@RequiredArgsConstructor
public class DrillController {
    public static final String ENDPOINT = "/drill";
    private final DrillService drillService;
    private final CategorySerivce categorySerivce;
    private final SubCategorySerivce subCategorySerivce;

    /**
     * Endpoint to return all DrillEntity objects.
     *
     * @return ResponseEntity with List of the DrillEntity objects.
     */
    @Operation(
        summary = "Retrieve all Drills.",
        description = "Returns a list of all Drills in the database."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Drills exist in the database and were returned."),
        @ApiResponse(responseCode = "204", description = "No Drills exist in the database.",
            content = @Content(/* No Content */))
    })
    @GetMapping
    public ResponseEntity<List<DrillResponseDTO>> getAll() {
        List<DrillEntity> drills = drillService.findAll();

        if (drills.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        // Map of Drills by their ID for quick lookup
        Map<Long, DrillEntity> drillMap = drills.stream()
            .collect(Collectors.toMap(DrillEntity::getId, Function.identity()));

        return ResponseEntity.ok(
            drills.stream()
                // Extract the related drills from the map to create each DrillResponseDTO
                .map(drill -> {
                    List<DrillEntity> relatedDrills;

                    /*
                    * Compiler is generating a warning for each call to drill.getRelatedDrills().
                    * We can safely ignore this because of this first null check here.
                    */
                    if (null == drill.getRelatedDrills()) {
                        relatedDrills = List.of();
                    } else {
                        relatedDrills = new ArrayList<>(drill.getRelatedDrills().size());
                        relatedDrills = drill.getRelatedDrills().stream()
                            .map(relatedId -> {
                                return drillMap.get(relatedId);
                            })
                            .collect(Collectors.toList());
                    }
                    return new DrillResponseDTO(drill, relatedDrills);
                })
                .collect(Collectors.toList())
        );
    }

    /**
     * Endpoint to insert a new DrillEntity into the database.
     * <br><br>
     * This essentially creates an empty drill with only a name. To include any related drills,
     * categories, subCategories, or instructions, see
     * {@link #updateDrillById(Long, DrillUpdateDTO)}.
     *
     * @param drill Drill to insert into the database.
     * @return Response entity containing the created Drill.
     */
    @Operation(
        summary = "Insert a New Drill.",
        description = "Create a new Drill in the database with the given name. Returns the newly "
            + "created Drill. To add categories, instructions, etc. use PUT /drill/id/{id} using "
            + "the returned drill ID."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Drill was created successfully."),
        @ApiResponse(responseCode = "400", description = "Issue with request, check returned error message for details.",
            content = @Content(schema = @Schema(implementation = ErrorMessageDTO.class)))
    })
    @PostMapping
    public ResponseEntity<DrillResponseDTO> insertNewDrill(@RequestBody @Valid DrillCreateDTO drill) {
        DrillEntity createdDrill = drillService.save(drill.toEntity());
        return ResponseEntity
            .created(URI.create(ENDPOINT + "/" + createdDrill.getId()))
            .body(new DrillResponseDTO(createdDrill));
    }

    /**
     * Endpoint to find a DrillEntity by its name. Case insensitive.
     *
     * @param name Name of the DrillEntity
     * @return ResponseEntity containing the found entity;
     */
    @Operation(
        summary = "Find a Drill by its name.",
        description = "Search to see if a Drill exists by the given name. Case insensitive."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Drill was found by the given name."),
        @ApiResponse(responseCode = "404", description = "No Drill exists with the given name.",
            content = @Content(/* No Content */))
    })
    @GetMapping("/name/{name}")
    @Transactional
    public ResponseEntity<DrillResponseDTO> getDrillByName(@PathVariable String name) {
        return drillService.find(name)
                    .map(foundDrill -> {
                        List<Long> relatedDrills = foundDrill.getRelatedDrills();
                        if (null == relatedDrills || relatedDrills.isEmpty()) {
                            return ResponseEntity.ok(
                                new DrillResponseDTO(foundDrill)
                            );
                        } else {
                            return ResponseEntity.ok(new DrillResponseDTO(
                                foundDrill,
                                drillService.findAll(relatedDrills)));
                        }
                    })
                    .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Endpoint to find a DrillEntity by its ID.
     *
     * @param id ID of the DrillEntity.
     * @return ResponseEntity containing the found entity.
     */
    @Operation(
        summary = "Find a Drill by its ID.",
        description = "Search to see if a Drill exists by the given ID."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Drill was found by the given ID."),
        @ApiResponse(responseCode = "404", description = "No Drill exists with the given ID.",
            content = @Content(/* No Content */))
    })
    @GetMapping("/id/{id}")
    @Transactional
    public ResponseEntity<DrillResponseDTO> getDrillById(@PathVariable Long id) {
        return drillService.find(id)
                    .map(foundDrill -> {
                        List<Long> relatedDrills = foundDrill.getRelatedDrills();
                        if (null == relatedDrills || relatedDrills.isEmpty()) {
                            return ResponseEntity.ok(
                                new DrillResponseDTO(foundDrill)
                            );
                        } else {
                            return ResponseEntity.ok(new DrillResponseDTO(
                                foundDrill,
                                drillService.findAll(relatedDrills)));
                        }
                    })
                    .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Endpoint to update a DrillEntity by its ID.
     *
     * @param id ID of the entity to update.
     * @param drill Entity to update.
     * @return ReponseEntity with the updated entity.
     */
    @Operation(
        summary = "Update a Drill by its ID.",
        description = "Update a drill's contents (category, instructions, etc.) by its ID. "
            + "Returns the newly updated Drill."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Drill was updated successfully."),
        @ApiResponse(responseCode = "400", description = "Issue with request, check returned error message for details.",
            content = @Content(schema = @Schema(implementation = ErrorMessageDTO.class))),
        @ApiResponse(responseCode = "404", description = "No Drill exists with the given ID.",
            content = @Content(/* No Content */))
    })
    @PutMapping("/id/{id}")
    public ResponseEntity<DrillResponseDTO> updateDrillById(
        @PathVariable Long id, @RequestBody @Valid DrillUpdateDTO drill) {
        if (drillService.find(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Validate steps ('|' must not be used)
        if (null != drill.getInstructions() && !drill.getInstructions().isEmpty()) {
            for (InstructionsDTO instructions : drill.getInstructions()) {
                for (String step : instructions.getSteps()) {
                    if (step.contains("|")) {
                        throw new DatabaseInsertException("Invalid character: '|'");
                    }
                }
            }
        }

        DrillEntity drillToUpdate = drill.toEntity(id);

        // Set Categories
        if (null != drill.getCategoryIds() && !drill.getCategoryIds().isEmpty()) {
            // IDs that are not in the database are ignored
            drillToUpdate.setCategories(categorySerivce.findAll(drill.getCategoryIds()));
        }

        // Set SubCategories
        if (null != drill.getSubCategoryIds() && !drill.getSubCategoryIds().isEmpty()) {
            // IDs that are not in the database are ignored
            drillToUpdate.setSubCategories(subCategorySerivce.findAll(drill.getSubCategoryIds()));
        }

        DrillEntity updatedDrill = drillService.save(drillToUpdate);

        // Null check here, ignore the warnings
        if (null == updatedDrill.getRelatedDrills() || updatedDrill.getRelatedDrills().isEmpty()) {
            return ResponseEntity.ok(
                new DrillResponseDTO(updatedDrill)
            );
        } else {
            return ResponseEntity.ok(
                new DrillResponseDTO(
                    updatedDrill,
                    drillService.findAll(updatedDrill.getRelatedDrills())
                ));
        }
    }

    /**
     * Endpoint to delete a DrillEntity by its ID.
     *
     * @param id ID of the entity to delete.
     * @return Empty ResponseEntity.
     */
    @Operation(
        summary = "Delete a Drill by its ID.",
        description = "Remove a Drill from the database using the Drill ID."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Deletion was successful or ID did not exist anyway.",
            content = @Content(/* No Content */))
    })
    @DeleteMapping("/id/{id}")
    public ResponseEntity<String> deleteDrillById(@PathVariable Long id) {
        drillService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Endpoint to add a category to a list of drills by their IDs.
     *
     * @param categoryId Category ID to add to the drills.
     * @param drillIds List of Drill IDs.
     * @return Empty ResonseEntity.
     */
    @Operation(
        summary = "Add a Category to a list of Drills.",
        description = "Add a Category to each Drill in a list of given Drill IDs."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Additions were successful.",
            content = @Content(/* No Content */)),
        @ApiResponse(responseCode = "400", description = "Issue with request, check returned error message for details.",
            content = @Content(schema = @Schema(implementation = ErrorMessageDTO.class))),
        @ApiResponse(responseCode = "404", description = "No Category exists with the given ID.",
            content = @Content(/* No Content */))
    })
    @PatchMapping("/add_category/{categoryId}")
    @Transactional
    public ResponseEntity<String> addCategoryToListOfDrills(@PathVariable Long categoryId,
            @RequestBody List<Long> drillIds) {
        /*
         * This makes it okay to use transactional even with the save() later, because there
         * should be no issues with the save as the category should exist
         */
        Optional<CategoryEntity> optCategory = categorySerivce.find(categoryId);
        if (optCategory.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if (null != drillIds && !drillIds.isEmpty()) {
            CategoryEntity category = optCategory.get();

            for (DrillEntity drill : drillService.findAll(drillIds)) {
                if (null == drill.getCategories()) {
                    drill.setCategories(List.of(category));
                } else {
                    // Make sure that we do not save duplicates, will result in DataIntegrityViolationException
                    if (drill.getCategories().contains(category)) {
                        continue;
                    }

                    drill.getCategories().add(category);
                }

                drillService.save(drill);
            }
        }

        return ResponseEntity.noContent().build();
    }

    /**
     * Endpoint to add a sub-category to a list of drills by their IDs.
     *
     * @param subCategoryId SubCategory ID to add to the drills.
     * @param drillIds List of Drill IDs.
     * @return Empty ResonseEntity.
     */
    @Operation(
        summary = "Add a Sub-Category to a list of Drills.",
        description = "Add a Sub-Category to each Drill in a list of given Drill IDs."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Additions were successful.",
            content = @Content(/* No Content */)),
        @ApiResponse(responseCode = "400", description = "Issue with request, check returned error message for details.",
            content = @Content(schema = @Schema(implementation = ErrorMessageDTO.class))),
        @ApiResponse(responseCode = "404", description = "No Sub-Category exists with the given ID.",
            content = @Content(/* No Content */))
    })
    @PatchMapping("/add_sub_category/{subCategoryId}")
    @Transactional
    public ResponseEntity<String> addSubCategoryToListOfDrills(@PathVariable Long subCategoryId,
            @RequestBody List<Long> drillIds) {
        /*
         * This makes it okay to use transactional even with the save() later, because there
         * should be no issues with the save as the subCategory should exist
         */
        Optional<SubCategoryEntity> optSubCategory = subCategorySerivce.find(subCategoryId);
        if (optSubCategory.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if (null != drillIds && !drillIds.isEmpty()) {
            SubCategoryEntity subCategory = optSubCategory.get();

            for (DrillEntity drill : drillService.findAll(drillIds)) {
                if (null == drill.getSubCategories()) {
                    drill.setSubCategories(List.of(subCategory));
                } else {
                    // Make sure that we do not save duplicates, will result in DataIntegrityViolationException
                    if (drill.getSubCategories().contains(subCategory)) {
                        continue;
                    }

                    drill.getSubCategories().add(subCategory);
                }

                drillService.save(drill);
            }
        }

        return ResponseEntity.noContent().build();
    }

    /**
     * Endpoint to return a list of instruction descriptions of a DrillEntity by the drill's ID.
     *
     * @param id ID of the DrillEntity to retrieve the list of instruction descriptions.
     * @return ResponseEntity with a List of Strings.
     */
    @Operation(
        summary = "Retrieve Instructions by a Drill's ID.",
        description = "Retrieve a list of a Drill's instructions using its ID. Returns only the description."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "A list of Instructions were found for this Drill ID."),
        @ApiResponse(responseCode = "204", description = "The Drill has no associated Instructions.",
            content = @Content(/* No Content */)),
        @ApiResponse(responseCode = "404", description = "No Drill exists with the given ID.",
            content = @Content(/* No Content */))
    })
    @GetMapping("/id/{id}/how-to")
    public ResponseEntity<List<String>> getInstructionsByDrillId(@PathVariable Long id) {
        Optional<DrillEntity> optDrill = drillService.find(id);

        if (optDrill.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        DrillEntity drill = optDrill.get();

        /*
         * Compiler is generating a warning for each call to drill.getInstructions(). We can safely
         * ignore this because of this first null check here.
         */
        if (null == drill.getInstructions() || drill.getInstructions().isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(
            drill.getInstructions().stream()
                .map(InstructionsEntity::getDescription)
                .collect(Collectors.toList())
        );
    }

    /**
     * Endpoint to retrieve InstructionEntity details of a given DrillEntity.
     * <br><br>
     * Specify the instructions to retrieve by the DrillEntity's ID and the index of the
     * instructions in the drill's instructions list, retrieved in another endpoint.
     *
     * @param id ID of the DrillEntity.
     * @param number The number of the instructions relative to the drill's instructions list.
     * @return ResponseEntity containing the instruction details.
     * @see {@link #getInstructionsByDrillId(Long)}
     */
    @Operation(
        summary = "Retrieve Instruction details by Drill ID and Instruction number.",
        description = "Retrieve details of an Instruction using the Drill ID and the Instruction "
            + "number. The Instruction number corresponds to its array position in the list of "
            + "Instruction descriptions in /drill/id/{id}/how-to."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Instruction details were found.",
            content = @Content(schema = @Schema(implementation = InstructionsDTO.class))),
        @ApiResponse(responseCode = "404", description = "Instructions were not found. Check error "
                + "message for specifics on what went wrong.",
            content = @Content(schema = @Schema(implementation = ErrorMessageDTO.class)))
    })
    @GetMapping("/id/{id}/how-to/{number}")
    public ResponseEntity<Object> getInstructionDetails(
            @PathVariable Long id, @PathVariable Long number) {
        Optional<DrillEntity> optDrill = drillService.find(id);

        if (optDrill.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorMessageDTO.builder()
                .error("Drill not found")
                .message("Drill ID " + id + " does not exist")
                .build());
        }

        DrillEntity drill = optDrill.get();

        /*
         * Compiler is generating a warning for each call to drill.getInstructions(). We can safely
         * ignore this because of this first null check here.
         */
        if (null == drill.getInstructions() || drill.getInstructions().isEmpty()
                || number >= drill.getInstructions().size()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorMessageDTO.builder()
                .error("Instructions not found")
                .message("Instructions number " + number + " does not exist")
                .build());
        }

        return ResponseEntity.ok(
            new InstructionsDTO(
                drill.getInstructions().get(number.intValue())
            )
        );
    }
}
