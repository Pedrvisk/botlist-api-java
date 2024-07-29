package xyz.mdbots.api.Data;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import java.time.LocalDateTime;
import jakarta.persistence.Id;
import lombok.Data;

/**
 *
 * @author Pedrovisk
 * @timestamp 2024-07-26 15:32:49
 */
@Data
@Entity(name = "bots")
public class BotModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String botId;

    @Column(nullable = false)
    private String ownerId;
    private String username;
    private String avatarUrl;
    private String bannerUrl;
    private String shortDescription;
    private boolean isVerifiedBot = false;
    private boolean isSlashCommands = false;

    @Column
    private String description;
    private String authorId;
    private String authorUsername;
    private boolean isPending = true;
    private boolean isPromoted = false;
    private int totalVotes = 0;
    private LocalDateTime lastVoteAt;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public void setDescription(String description) {
        if (description.length() > 140) {
            throw new IllegalArgumentException("A descrição do bot não pode ser maior que 140 caracteres");
        }

        this.description = description;
    }
}
