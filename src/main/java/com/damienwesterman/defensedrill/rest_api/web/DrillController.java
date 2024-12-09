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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.damienwesterman.defensedrill.rest_api.entity.DrillEntity;
import com.damienwesterman.defensedrill.rest_api.entity.InstructionsEntity;
import com.damienwesterman.defensedrill.rest_api.service.CategorySerivce;
import com.damienwesterman.defensedrill.rest_api.service.DrillService;
import com.damienwesterman.defensedrill.rest_api.service.SubCategorySerivce;
import com.damienwesterman.defensedrill.rest_api.web.dto.DrillCreateDTO;
import com.damienwesterman.defensedrill.rest_api.web.dto.DrillResponseDTO;
import com.damienwesterman.defensedrill.rest_api.web.dto.DrillUpdateDTO;
import com.damienwesterman.defensedrill.rest_api.web.dto.InstructionsDTO;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

// TODO: Swagger comments (DTOs as well? Other DTOs/@Data objects? / Jakarta constraint messages on the DTOs)
/**
 * Controller responsible for CRUD operations for {@link DrillEntity} objects with validation.
 * Extends to control over {@link InstructionsEntity} held within the DrillEntity.
 */
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
    @GetMapping
    public ResponseEntity<List<DrillResponseDTO>> getAll() {
        List<DrillEntity> drills = drillService.findAll();

        if (drills.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(
            drills.stream()
                .map(DrillResponseDTO::new)
                .collect(Collectors.toList())
        );
    }

    // TODO: Swagger comments that this is essentially just the name and to use the other endpoint for the lists of things.
    /**
     * Endpoint to insert a new DrillEntity into the database.
     * <br><br>
     * This essentially creates an empty drill with only a name. To include any realted drills,
     * categories, subCategories, or instructions, see
     * {@link #updateDrillById(Long, DrillUpdateDTO)}.
     *
     * @param drill
     * @return
     */
    @PostMapping
    public ResponseEntity<DrillResponseDTO> insertNewDrill(@RequestBody @Valid DrillCreateDTO drill) {
        DrillEntity createdDrill = drillService.save(drill.toEntity());
        return ResponseEntity
            .created(URI.create(ENDPOINT + "/" + createdDrill.getId()))
            .body(new DrillResponseDTO(createdDrill));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<DrillResponseDTO> getDrillByName(@PathVariable String name) {
        return drillService.find(name)
                    .map(foundDrill -> ResponseEntity.ok(new DrillResponseDTO(foundDrill)))
                    .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<DrillResponseDTO> getDrillById(@PathVariable Long id) {
        return drillService.find(id)
                    .map(foundDrill -> ResponseEntity.ok(new DrillResponseDTO(foundDrill)))
                    .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/id/{id}")
    @Transactional
    public ResponseEntity<DrillResponseDTO> updateDrillById(
        @PathVariable Long id, @RequestBody @Valid DrillUpdateDTO drill) {
        if (null == drill.getId() || 0 != drill.getId().compareTo(id)) {
            return ResponseEntity.badRequest().build();
        }

        if (!drillService.find(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }

        DrillEntity drillToUpdate = drill.toEntity();

        // Set Categories
        if (null != drill.getCategoryIds() && 0 < drill.getCategoryIds().size()) {
            // IDs that are not in the database are ignored
            drillToUpdate.setCategories(categorySerivce.findAll(drill.getCategoryIds()));
        }

        // Set SubCategories
        if (null != drill.getSubCategoryIds() && 0 < drill.getSubCategoryIds().size()) {
            // IDs that are not in the database are ignored
            drillToUpdate.setSubCategories(subCategorySerivce.findAll(drill.getSubCategoryIds()));
        }

        DrillEntity updatedDrill = drillService.save(drillToUpdate);

        return ResponseEntity.ok(new DrillResponseDTO(updatedDrill));
    }

    @DeleteMapping("/id/{id}")
    public ResponseEntity<String> deleteDrillById(@PathVariable Long id) {
        drillService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/id/{id}/how-to")
    public ResponseEntity<List<String>> getInstructionsByDrillId(@PathVariable Long id) {
        Optional<DrillEntity> optDrill = drillService.find(id);

        if (!optDrill.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        DrillEntity drill = optDrill.get();

        if (drill.getInstructions().isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(
            drill.getInstructions().stream()
                .map(InstructionsEntity::getDescription)
                .collect(Collectors.toList())
        );
    }

    @GetMapping("/id/{id}/how-to/{number}")
    public ResponseEntity<InstructionsDTO> getInstructionDetails(
            @PathVariable Long id, @PathVariable Long number) {
        Optional<DrillEntity> optDrill = drillService.find(id);

        if (!optDrill.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(
            new InstructionsDTO(
                optDrill.get().getInstructions().get(number.intValue())
            )
        );
    }
}
