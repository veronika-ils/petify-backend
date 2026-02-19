package com.petify.petify.domain;

import java.io.Serializable;
import java.util.Objects;

public class FavoriteListingId implements Serializable {

    private Long client;
    private Long listing;

    public FavoriteListingId() {}

    public FavoriteListingId(Long client, Long listing) {
        this.client = client;
        this.listing = listing;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FavoriteListingId that)) return false;
        return Objects.equals(client, that.client)
                && Objects.equals(listing, that.listing);
    }

    @Override
    public int hashCode() {
        return Objects.hash(client, listing);
    }
}

