package ro.mpp2025.Service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.mpp2025.Domain.Bug;
import ro.mpp2025.Domain.Status;
import ro.mpp2025.Domain.User;
import ro.mpp2025.Domain.Role;
import ro.mpp2025.Repository.BugRepoNew;
import ro.mpp2025.Utils.Observer;
import ro.mpp2025.Utils.Subject;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class BugService implements Observer {
    private final BugRepoNew bugRepository;
    private final List<Subject> subjects = new ArrayList<>();

    public BugService(BugRepoNew bugRepository) {
        this.bugRepository = bugRepository;
    }

    /** Creează un bug nou cu validări și setează statusul inițial */
    @Transactional
    public Bug createBug(Bug bug) {
        validateNewBug(bug);
        Bug saved = bugRepository.save(bug);
        notification();
        return saved;
    }

    /** Returnează bug-ul după ID sau null */
    @Transactional(readOnly = true)
    public Bug getBug(int id) {
        return bugRepository.findById(id).orElse(null);
    }

    /** Returnează lista tuturor bug-urilor */
    @Transactional(readOnly = true)
    public List<Bug> getAllBugs() {
        return bugRepository.findAll();
    }

    /** Șterge bug-ul dacă actorul este Admin */
    @Transactional
    public Bug deleteBug(int id, User actor) {
        if (actor.getRole() != Role.Admin) {
            throw new RuntimeException("Only admins can delete bugs");
        }
        return bugRepository.deleteBug(id);
    }

    /**
     * Atribuie bug-ul unui utilizator.
     * Doar Testeri sau Programatori pot atribui.
     */
    @Transactional
    public Bug assignBug(int id, User assigner, User assignee) {
        if (assigner.getRole() != Role.Tester && assignee.getRole() != Role.Programmer) {
            throw new RuntimeException("Only Programmer can assign a bug");
        }

        Bug bug = bugRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bug not found"));

        if (bug.getStatus() == Status.Finished) {
            throw new RuntimeException("Bug is already finished");
        }
        // dacă cel care atribuie e programator, îl pune în lucru
        if (assigner.getRole() == Role.Programmer) {
            bug.setStatus(Status.InProgress);
        }
        bug.setAssignedTo(assignee);
        Bug updated = bugRepository.save(bug);
        notification();
        return updated;
    }

    /**
     * Schimbă statusul unui bug cu verificări de permisiuni și tranziții
     */
    @Transactional
    public Bug changeStatus(int id, User actor, Status newStatus) {
        Bug bug = bugRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bug not found"));

        if (!canChangeStatus(actor, bug)) {
            throw new RuntimeException("You do not have permission to change status");
        }
        if (newStatus == Status.Refused) {
            bug.setAssignedTo(null);
        }
        validateStatusTransition(bug.getStatus(), newStatus);

        bug.setStatus(newStatus);
        Bug updated = bugRepository.save(bug);
        notification();
        return updated;
    }

    // --- Helpers ---

    private void validateNewBug(Bug bug) {
        if (bug.getName() == null || bug.getName().isBlank()) {
            throw new RuntimeException("Bug name is required");
        }
        if (bug.getDescription() == null || bug.getDescription().isBlank()) {
            throw new RuntimeException("Bug description is required");
        }
        if (bug.getReportedBy() == null || !bug.getReportedBy().isActivated()) {
            throw new RuntimeException("Reporting user must be activated");
        }
        bug.setStatus(Status.Pending);
    }

    private boolean canChangeStatus(User actor, Bug bug) {
        return actor.getRole() == Role.Admin || actor.equals(bug.getAssignedTo());
    }

    private void validateStatusTransition(Status oldStatus, Status newStatus) {
        Set<Status> allowed = switch (oldStatus) {
            case Pending     -> EnumSet.of(Status.InProgress, Status.Refused);
            case InProgress  -> EnumSet.of(Status.Finished, Status.Refused);
            case Refused     -> EnumSet.of(Status.Refused);
            default          -> EnumSet.noneOf(Status.class);
        };
        if (!allowed.contains(newStatus)) {
            throw new RuntimeException(
                    "Invalid status transition from " + oldStatus + " to " + newStatus);
        }
    }

    @Override
    public void notification() {
        subjects.forEach(Subject::update);
    }

    @Override
    public void addSubject(Subject subject) {
        subjects.add(subject);
    }
}
