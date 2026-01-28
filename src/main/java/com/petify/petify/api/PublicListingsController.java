package com.petify.petify.api;
import com.petify.petify.repo.PublicListingCardView;
import com.petify.petify.repo.PublicListingRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/public")
public class PublicListingsController {
    private final PublicListingRepository repo;

    public PublicListingsController(PublicListingRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/listings")
    public List<PublicListingCardView> getListings() {
        return repo.findActiveListingCards();
    }
}
