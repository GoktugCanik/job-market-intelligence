package com.canik.jmi.technology;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

import com.canik.jmi.job.Job;


@Entity
@Table(name = "technologies")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class Technology {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(length = 100)
    private String category;

    @ManyToMany(mappedBy = "technologies")
    @Builder.Default
    private Set<Job> jobs = new HashSet<>();
}
