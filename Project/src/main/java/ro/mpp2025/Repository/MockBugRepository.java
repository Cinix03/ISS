package ro.mpp2025.Repository;

import ro.mpp2025.Domain.Bug;
import ro.mpp2025.Domain.Status;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * In-memory mock implementation of IBugRepository for service-layer testing.
 */
public class MockBugRepository implements IBugRepository {
    private final Map<Integer, Bug> bugsById = new HashMap<>();
    private final AtomicInteger idSequence = new AtomicInteger(1);
    private final IUserRepository userRepo;

    public MockBugRepository(IUserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public void save(Bug bug) {
        if (bug.getId() == 0) {
            bug.setId(idSequence.getAndIncrement());
        }
        Bug copy = copyBug(bug);
        bugsById.put(copy.getId(), copy);
    }

    @Override
    public Bug findById(int id) {
        Bug b = bugsById.get(id);
        return b != null ? copyBug(b) : null;
    }

    @Override
    public Bug deleteBug(int id) {
        Bug removed = bugsById.remove(id);
        return removed != null ? copyBug(removed) : null;
    }

    @Override
    public List<Bug> findAll() {
        List<Bug> list = new ArrayList<>();
        for (Bug b : bugsById.values()) {
            list.add(copyBug(b));
        }
        return list;
    }

    @Override
    public Bug update(Bug bug) {
        if (!bugsById.containsKey(bug.getId())) {
            return null;
        }
        // update fields
        Bug stored = bugsById.get(bug.getId());
        stored.setName(bug.getName());
        stored.setDescription(bug.getDescription());
        stored.setStatus(bug.getStatus());
        stored.setReportedBy(bug.getReportedBy());
        stored.setAssignedTo(bug.getAssignedTo());
        return copyBug(stored);
    }

    /**
     * Utility to clone a Bug instance.
     */
    private Bug copyBug(Bug original) {
        Bug b = new Bug();
        b.setId(original.getId());
        b.setName(original.getName());
        b.setDescription(original.getDescription());
        b.setStatus(original.getStatus());
        // Users: can copy by fetching fresh instances if needed
        b.setReportedBy(original.getReportedBy());
        b.setAssignedTo(original.getAssignedTo());
        return b;
    }
}
