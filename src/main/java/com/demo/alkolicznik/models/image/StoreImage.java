package com.demo.alkolicznik.models.image;

import com.demo.alkolicznik.models.Store;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.NaturalId;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity(name = "StoreImage")
@Table(name = "store_image")
@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
public class StoreImage extends ImageModel implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "store_generator")
    @SequenceGenerator(name = "store_generator", sequenceName = "storeImgIdSeq", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @Column(name = "store_name", unique = true)
    @NaturalId
    private String storeName;

    @OneToMany(mappedBy = "image")
    @JsonIgnore
    @ToString.Exclude
    private Set<Store> stores = new HashSet<>();

    public StoreImage(String imageUrl, String remoteId) {
        super(imageUrl, remoteId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof StoreImage)) {
            return false;
        }
        StoreImage that = (StoreImage) o;
        return Objects.equals(super.imageUrl, that.imageUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(imageUrl);
    }
}
