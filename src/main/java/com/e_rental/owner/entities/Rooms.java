package com.e_rental.owner.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "rooms")
public class Rooms {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "room_id")
    private Long roomId;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    private User users;

    @Column(name = "address")
    private String address;

    @Column(name = "ward_id")
    private Integer wardId;

    @Column(name = "district_id")
    private Integer districtId;

    @Column(name = "city_id")
    private Integer cityId;

    @Column(name = "acreage")
    private Double acreage;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "price")
    private Integer price;

    @Column(name = "picture_url")
    private String pictureUrl;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @CreationTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "modified_at")
    @CreationTimestamp
    private LocalDateTime modifiedAt;

    @Column(name = "modified_by")
    @CreationTimestamp
    private LocalDateTime modifiedBy;
}
