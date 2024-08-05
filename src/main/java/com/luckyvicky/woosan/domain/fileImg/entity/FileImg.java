package com.luckyvicky.woosan.domain.fileImg.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class FileImg {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type", nullable = false, length = 255)
    private String type;

    @Column(name = "target_id", nullable = false)
    private Long targetId;

    @Column(name = "uuid", nullable = false, length = 255)
    private String uuid;

    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;

    @Column(name = "ord")
    private Integer ord;

    @Column(name = "path", nullable = false, length = 255)
    private String path;

}