// src/main/java/ro/mpp2025/Repository/IBugRepository.java
package ro.mpp2025.Repository;

import ro.mpp2025.Domain.Bug;

import java.util.List;

public interface IBugRepository {
    void save(Bug bug);
    Bug findById(int id);
    Bug deleteBug(int id);
    List<Bug> findAll();
    Bug update(Bug bug);
}
