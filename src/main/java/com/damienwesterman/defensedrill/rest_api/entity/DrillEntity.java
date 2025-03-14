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

package com.damienwesterman.defensedrill.rest_api.entity;

import java.util.List;

import org.springframework.lang.Nullable;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Database Entity to contain all the information for a Drill.
 */
@Entity
@Table(name = "drills")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DrillEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // @NotNull -> This can (and should) be null when creating a new entity
    private Long id;

    /** UTC timestamp of last update in milliseconds since epoch */
    @NotNull
    private Long updateTimestamp;

    @Column(unique = true)
    @NotEmpty
    @Size(min = 1, max = 255)
    private String name;

    @Nullable
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "drill_category_join",
        joinColumns = @JoinColumn(name = "drill_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<CategoryEntity> categories;

    @Nullable
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "drill_sub_category_join",
        joinColumns = @JoinColumn(name = "drill_id"),
        inverseJoinColumns = @JoinColumn(name = "sub_category_id")
    )
    private List<SubCategoryEntity> subCategories;

    @Nullable
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "related_drills",
        joinColumns = @JoinColumn(name = "primary_drill_id")
    )
    @Column(name = "related_drill_id")
    private List<Long> relatedDrills;

    @Nullable
    @OneToMany(
        mappedBy = "drillId",
        cascade = CascadeType.ALL,
        fetch = FetchType.EAGER,
        orphanRemoval = true
    )
    private List<InstructionsEntity> instructions;
}
