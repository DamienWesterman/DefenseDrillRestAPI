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

import java.util.List;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.damienwesterman.defensedrill.rest_api.entity.DrillEntity;
import com.damienwesterman.defensedrill.rest_api.entity.InstructionsEntity;
import com.damienwesterman.defensedrill.rest_api.exception.DatabaseInsertException;
import com.damienwesterman.defensedrill.rest_api.repository.DrillRepo;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

// TODO: FINISH ME DOC COMMENTS
// TODO: NonNull annotations
@Service
@RequiredArgsConstructor
public class DrillService {
    private final DrillRepo repo;
    // TODO: FINISH ME

    /**
     * 
     * @param drill
     * @return
     * @throws DatabaseInsertException Thrown when there is any issue saving the entity.
     */
    @Transactional
    public DrillEntity save(@NonNull DrillEntity drill) throws DatabaseInsertException {
        if (0 == drill.getInstructions().size()) {
            return ErrorMessageUtils.trySave(repo, drill);
        }

        /*
         * Instructions cannot be saved until they have a valid drill ID. So we need
         * to remove them for the initial save, retrieve the drill ID, then update the
         * saved drill to include the instructions.
        */
        List<InstructionsEntity> instructions = drill.getInstructions();
        drill.getInstructions().clear();
        DrillEntity returnedDrill = ErrorMessageUtils.trySave(repo, drill);

        instructions.forEach(instructionsEntity ->
            instructionsEntity.setDrillId(returnedDrill.getId())
        );
        returnedDrill.setInstructions(instructions);

        // Update the existing drill with the instructions
        return ErrorMessageUtils.trySave(repo, returnedDrill);
    }
}
