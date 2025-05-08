package ro.mpp2025.Domain;
import lombok.Data;

@Data
public class Bug {
    private int id;
    private String name;
    private String description;
    private Status status;
    private User reportedBy;
    private User assignedTo;
}
