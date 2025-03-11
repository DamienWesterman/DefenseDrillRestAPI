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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.damienwesterman.defensedrill.rest_api.entity.DrillEntity;
import com.damienwesterman.defensedrill.rest_api.entity.InstructionsEntity;
import com.damienwesterman.defensedrill.rest_api.exception.DatabaseInsertException;
import com.damienwesterman.defensedrill.rest_api.repository.DrillRepo;

import lombok.RequiredArgsConstructor;

/**
 * Service class for interacting with {@link DrillEntity} objects in the database.
 */
@Service
@RequiredArgsConstructor
public class DrillService {
    private final DrillRepo repo;

    /**
     * Save a DrillEntity into the database.
     * <br><br>
     * This can be used for create OR update operations.
     *
     * @param drill Entity to save.
     * @return The saved entity.
     * @throws DatabaseInsertException Thrown when there is any issue saving the entity.
     */
    @NonNull
    public DrillEntity save(@NonNull DrillEntity drill) throws DatabaseInsertException {
        // Saving null lists in the database can cause issues
        if(null == drill.getCategories()) {
            drill.setCategories(List.of());
        }
        if (null == drill.getSubCategories()) {
            drill.setSubCategories(List.of());
        }
        if (null == drill.getRelatedDrills()) {
            drill.setRelatedDrills(List.of());
        }
        /*
         * Compiler is generating a warning for each call to drill.getInstructions(). We can safely
         * ignore this because of this first null check here.
         */
        if (null == drill.getInstructions()) {
            drill.setInstructions(List.of());
        }

        if (drill.getInstructions().isEmpty()) {
            return ErrorMessageUtils.trySave(drill, repo);
        }

        /*
         * Instructions cannot be saved until they have a valid drill ID. So we need
         * to remove them for the initial save, retrieve the drill ID, then update the
         * saved drill to include the instructions.
        */
        List<InstructionsEntity> instructions = new ArrayList<>(drill.getInstructions());
        drill.getInstructions().clear();
        DrillEntity returnedDrill = ErrorMessageUtils.trySave(drill, repo);

        instructions.forEach(instructionsEntity ->
            instructionsEntity.setDrillId(returnedDrill.getId())
        );
        returnedDrill.getInstructions().addAll(instructions);

        // Update the existing drill with the instructions
        return ErrorMessageUtils.trySave(returnedDrill, repo);
    }

    /**
     * Find an entity in the database by ID - if it exists.
     *
     * @param id ID of the DrillEntity.
     * @return Optional containing the returned entity - if it exists.
     */
	public Optional<DrillEntity> find(@NonNull Long id) {
        return repo.findById(id);
	}

    /**
     * Find an entity in the database by name (case insensitive) - if it exists.
     *
     * @param name Name of the DrillEntity.
     * @return Optional containing the returned entity - if it exists.
     */
    public Optional<DrillEntity> find(@NonNull String name) {
        return repo.findByNameIgnoreCase(name);
    }

    /**
     * Return all entities in the database sorted alphabetically by name.
     *
     * @return List of DrillEntity objects.
     */
    @NonNull
    public List<DrillEntity> findAll() {
        return repo.findAll(Sort.by(Sort.Direction.ASC, "name"));
    }

    /**
     * Return all entities in the database that are in the list of IDs sorted
     * alphabetically by name.
     *
     * @param ids List of Drill IDs.
     * @return List of Drill objects.
     */
    @NonNull
    public List<DrillEntity> findAll(@NonNull List<Long> ids) {
        List<DrillEntity> ret = repo.findAllById(ids);
        ret.sort(
            (drill1, drill2) -> drill1.getName().compareToIgnoreCase(drill2.getName())
        );
        return ret;
    }

    /**
     * Return all entities in the database that were update after the given timestamp.
     *
     * @param timestamp UTC milliseconds since epoch.
     * @return List of Drill objects.
     */
    @NonNull
    public List<DrillEntity> findAll(Long timestamp) {
        return repo.findByUpdateTimestampGreaterThan(timestamp, Sort.by(Sort.Direction.ASC, "name"));
    }

    /**
     * Delete an entity from the database by its ID - if it exists.
     *
     * @param id ID of the AbstractCategoryEntity.
     */
    public void delete(@NonNull Long id) {
        repo.deleteById(id);
    }
}
