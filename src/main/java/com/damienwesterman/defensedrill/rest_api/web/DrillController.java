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
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.damienwesterman.defensedrill.rest_api.entity.DrillEntity;
import com.damienwesterman.defensedrill.rest_api.service.DrillService;
import com.damienwesterman.defensedrill.rest_api.web.dto.DrillCreateDTO;
import com.damienwesterman.defensedrill.rest_api.web.dto.DrillResponseDTO;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

// TODO: DOC COMMENTS
// TODO: Swagger comments (DTOs as well? Other DTOs/@Data objects? / Jakarta constraint messages on the DTOs)
@RestController
@RequestMapping(DrillController.ENDPOINT)
@RequiredArgsConstructor
public class DrillController {
    public static final String ENDPOINT = "/drill";
    private final DrillService service;

    @GetMapping
    public ResponseEntity<List<DrillResponseDTO>> getAll() {
        List<DrillEntity> drills = service.findAll();

        if (drills.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(
            drills.stream()
                .map(DrillResponseDTO::new)
                .collect(Collectors.toList())
        );
    }

    @PostMapping
    public ResponseEntity<DrillResponseDTO> insertNewDrill(@RequestBody @Valid DrillCreateDTO drill) {
        DrillEntity createdDrill = service.save(drill.toEntity());
        return ResponseEntity
            .created(URI.create(ENDPOINT + "/" + createdDrill.getId()))
            .body(new DrillResponseDTO(createdDrill));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<DrillResponseDTO> getDrillByName(@PathVariable String name) {
        return service.find(name)
                    .map(foundDrill -> ResponseEntity.ok(new DrillResponseDTO(foundDrill)))
                    .orElse(ResponseEntity.notFound().build());
    }
}
