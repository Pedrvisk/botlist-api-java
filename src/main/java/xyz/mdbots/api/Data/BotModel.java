package xyz.mdbots.api.Data;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity(name = "bots")
public class BotModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty("bot_id")
    @Column(nullable = false, unique = true, name = "bot_id")
    private String botId;

    @JsonProperty("owner_id")
    @Column(nullable = false, name = "owner_id")
    private String ownerId;

    @Column(nullable = false)
    private String username;

    @JsonProperty("avatar_url")
    @Column(nullable = false, name = "avatar_url")
    private String avatarUrl;

    @JsonProperty("banner_url")
    @Column(nullable = false, name = "banner_url")
    private String bannerUrl;

    @JsonProperty("short_description")
    @Column(nullable = false, name = "short_description", length = 140)
    private String shortDescription;

    @JsonProperty("is_verified_bot")
    @Column(name = "is_verified_bot")
    private boolean isVerifiedBot = false;

    @JsonProperty("is_slash_commands")
    @Column(name = "is_slash_commands")
    private boolean isSlashCommands = false;

    @Column(length = 1024)
    private String description;

    @JsonProperty("author_id")
    @Column(name = "author_id")
    private String authorId;

    @JsonProperty("author_username")
    @Column(name = "author_username")
    private String authorUsername;

    @JsonProperty("is_pending")
    @Column(name = "is_pending")
    private boolean isPending = true;

    @JsonProperty("is_promoted")
    @Column(name = "is_promoted")
    private boolean isPromoted = false;

    @JsonProperty("total_votes")
    @Column(name = "total_votes")
    private int totalVotes = 0;

    @JsonProperty("lastvote_at")
    @Column(name = "lastvote_at")
    private LocalDateTime lastVoteAt;

    @CreationTimestamp
    @JsonProperty("created_at")
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @JsonProperty("updated_at")
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public void setShortDescription(String shortDescription) {
        if (shortDescription.length() > 140 || shortDescription.length() < 70) {
            throw new IllegalArgumentException(
                    "A short descrição do bot não pode ser maior que 140 ou menor que 70 caracteres");
        }

        this.shortDescription = shortDescription;
    }

    public void setDescription(String description) {
        if (description.length() > 1024) {
            throw new IllegalArgumentException("A descrição do bot não pode ser maior que 1024 caracteres");
        }

        this.description = description;
    }
}
