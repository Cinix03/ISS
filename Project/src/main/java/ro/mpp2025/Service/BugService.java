package ro.mpp2025.Service;

import ro.mpp2025.Domain.Bug;
import ro.mpp2025.Domain.Status;
import ro.mpp2025.Domain.User;
import ro.mpp2025.Domain.Role;
import ro.mpp2025.Repository.IBugRepository;
import ro.mpp2025.Utils.Observer;
import ro.mpp2025.Utils.Subject;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class BugService implements Observer {
    private final IBugRepository bugRepository;
    private ArrayList<Subject> subjects;

    public BugService(IBugRepository bugRepository) {
        this.bugRepository = bugRepository;
        subjects = new ArrayList<>();
    }

    /**
     * Creates a new bug with validation and sets initial status.
     */
    public Bug createBug(Bug bug) {
        try {
            validateNewBug(bug);
            bugRepository.save(bug);
            notification();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return bug;
    }

    /**
     * Retrieves a bug by its ID.
     */
    public Bug getBug(int id) {
        return bugRepository.findById(id);
    }

    /**
     * Retrieves all bugs in the system.
     */
    public List<Bug> getAllBugs() {
        return bugRepository.findAll();
    }

    /**
     * Deletes a bug by its ID. Only admins can delete.
     */
    public Bug deleteBug(int id, User actor) {
        if (actor.getRole() != Role.Admin) {
            throw new RuntimeException("Only admins can delete bugs");
        }
        return bugRepository.deleteBug(id);
    }

    /**
     * Assigns a bug to a user. Only Admins or Programmers can assign.
     */
    public Bug assignBug(int id, User assigner, User assignee) {
        if (assigner.getRole() != Role.Tester && assignee.getRole() != Role.Programmer) {
            throw new RuntimeException("Only Programmer can assign a bug");
        }
        Bug bug = bugRepository.findById(id);
        if (bug == null) {
            throw new RuntimeException("Bug not found");
        }
        if(bug.getStatus()==Status.Finished)
        {
            throw new RuntimeException("Bug is finished");
        }
        if(assigner.getRole() == Role.Programmer) {
            bug.setStatus(Status.InProgress);
        }
        bug.setAssignedTo(assignee);
        bugRepository.update(bug);
        notification();
        return bug;
    }

    /**
     * Changes status of a bug with role-based permission and valid transition checks.
     */
    public Bug changeStatus(int id, User actor, Status newStatus) {
        Bug bug = bugRepository.findById(id);
        if (bug == null) {
            throw new RuntimeException("Bug not found");
        }
        if (!canChangeStatus(actor, bug)) {
            throw new RuntimeException("You do not have permission to change status");
        }
        if(newStatus == Status.Refused)
        {
            bug.setAssignedTo(null);
        }
        validateStatusTransition(bug.getStatus(), newStatus);
        bug.setStatus(newStatus);
        bugRepository.update(bug);
        notification();
        return bug;
    }

    // -- Validation Helpers --
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
        // Set initial status if not already set
        bug.setStatus(Status.Pending);
    }

    private boolean canChangeStatus(User actor, Bug bug) {
        // Admins or assigned user can change status
        return actor.getRole() == Role.Admin || actor.equals(bug.getAssignedTo());
    }

    private void validateStatusTransition(Status oldStatus, Status newStatus) {
        Set<Status> allowed = switch (oldStatus) {
            case Pending -> EnumSet.of(Status.InProgress, Status.Refused);
            case InProgress -> EnumSet.of(Status.Finished, Status.Refused);
            case Refused -> EnumSet.of(Status.Refused);
            default -> EnumSet.noneOf(Status.class);
        };
        if (!allowed.contains(newStatus)) {
            throw new RuntimeException("Invalid status transition from " + oldStatus + " to " + newStatus);
        }
    }

    @Override
    public void notification() {
        for(Subject subject : subjects) {
            subject.update();
        }
    }

    @Override
    public void addSubject(Subject subject) {
        subjects.add(subject);
    }
}
