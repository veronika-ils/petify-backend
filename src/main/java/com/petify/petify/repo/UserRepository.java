package com.petify.petify.repo;

import com.petify.petify.domain.User;
import com.petify.petify.domain.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsernameOrEmail(String username, String email);
    @Query(value = """
WITH params AS (
  SELECT
    NOW() - INTERVAL '30 days' AS start_ts,
    NOW() AS end_ts
),
listings_by_user AS (
  SELECT l.owner_id AS user_id, COUNT(*) AS listings_created
  FROM listings l
  JOIN params p ON l.created_at >= p.start_ts AND l.created_at < p.end_ts
  GROUP BY l.owner_id
),
reviews_by_user AS (
  SELECT r.reviewer_id AS user_id,
         COUNT(*) AS reviews_left,
         AVG(r.rating)::numeric(10,2) AS avg_rating_left
  FROM reviews r
  JOIN params p ON r.created_at >= p.start_ts AND r.created_at < p.end_ts
  GROUP BY r.reviewer_id
),
appointments_by_user AS (
  SELECT a.responsible_owner_id AS user_id,
         COUNT(*) AS appointments_total,
         COUNT(*) FILTER (WHERE a.status = 'DONE')      AS appointments_done,
         COUNT(*) FILTER (WHERE a.status = 'NO_SHOW')   AS appointments_no_show,
         COUNT(*) FILTER (WHERE a.status = 'CANCELLED') AS appointments_cancelled
  FROM appointments a
  JOIN params p ON a.date_time >= p.start_ts AND a.date_time < p.end_ts
  GROUP BY a.responsible_owner_id
),
favorites_by_user AS (
  SELECT f.client_id AS user_id, COUNT(*) AS favorites_saved_all_time
  FROM favorite_listings f
  GROUP BY f.client_id
)
SELECT u.user_id
FROM users u
LEFT JOIN listings_by_user l ON l.user_id = u.user_id
LEFT JOIN reviews_by_user rv ON rv.user_id = u.user_id
LEFT JOIN appointments_by_user ap ON ap.user_id = u.user_id
LEFT JOIN favorites_by_user fv ON fv.user_id = u.user_id
WHERE COALESCE(l.listings_created, 0)
    + COALESCE(rv.reviews_left, 0)
    + COALESCE(ap.appointments_total, 0)
    + COALESCE(fv.favorites_saved_all_time, 0) > 0
ORDER BY (
  COALESCE(l.listings_created, 0) * 5
  + COALESCE(rv.reviews_left, 0) * 3
  + COALESCE(ap.appointments_done, 0) * 2
  + COALESCE(fv.favorites_saved_all_time, 0)
  - COALESCE(ap.appointments_no_show, 0) * 2
) DESC
LIMIT 10
""", nativeQuery = true)
    List<Long> findTop10ActiveUserIds();

}
