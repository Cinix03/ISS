package ro.mpp2025.Repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ro.mpp2025.Domain.Bug;

import java.util.Optional;

public interface BugRepoNew extends JpaRepository<Bug, Integer> {
    // — save(Bug)             : moștenit din JpaRepository
    // — findById(Integer)     : moștenit, returnează Optional<Bug>
    // — findAll()             : moștenit

    /**
     * Șterge și returnează bug-ul (sau null dacă nu există).
     */
    @Transactional
    default Bug deleteBug(int id) {
        Optional<Bug> maybe = findById(id);
        maybe.ifPresent(this::delete);
        return maybe.orElse(null);
    }

    /**
     * Update-ul este doar un save() în JPA:
     * repo.save(bug) va face INSERT dacă id==null sau UPDATE dacă id!=null.
     */
}