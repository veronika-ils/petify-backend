package com.petify.petify.domain;

import jakarta.persistence.*;


@Entity
@Table(name = "favorite_listings")
@IdClass(FavoriteListingId.class)
public class FavoriteListing {

    @Id
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Id
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "listing_id", nullable = false)
    private Listing listing;

    public FavoriteListing() {}

    public FavoriteListing(Client client, Listing listing) {
        this.client = client;
        this.listing = listing;
    }

    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }

    public Listing getListing() { return listing; }
    public void setListing(Listing listing) { this.listing = listing; }
}
