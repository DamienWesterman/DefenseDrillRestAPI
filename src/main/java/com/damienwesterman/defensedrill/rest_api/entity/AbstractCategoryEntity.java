package com.damienwesterman.defensedrill.rest_api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@MappedSuperclass
@Data
@NoArgsConstructor
@SuperBuilder
public abstract class AbstractCategoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // @NotNull -> This can (and should) be null when creating a new entity
    protected Long id;

    @Column(unique = true)
    @NotEmpty
    @Size(max = 255)
    protected String name;

    @Column
    @NotEmpty
    @Size(max = 511)
    protected String description;
}
