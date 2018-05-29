package es.eina.sql.entities;

import org.json.JSONObject;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity(name="song_list")
@Table(name="user_song_lists")
public class EntitySongList extends EntityBase{

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "creation_time",nullable = false)
    private Long creationTime;


    @ManyToOne
    @JoinColumn(name = "author_id")
    private EntityUser author;

    @ManyToMany
    @JoinTable(
            name = "song_list_songs",
            joinColumns = { @JoinColumn(name = "list_id")},
            inverseJoinColumns = {@JoinColumn(name = "song_id")}
    )
    Set<EntitySong> songs = new HashSet<>();

    /**
     * DO NOT use this method as it can only be used by Hibernate
     */
    public EntitySongList(){}

    public EntitySongList(String title, EntityUser user) {
        this.title = title;
        this.author = user;
        this.creationTime = System.currentTimeMillis();
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public EntityUser getAuthor() {
        return author;
    }

    public Long getCreationTime() {
        return creationTime;
    }

    public void addSong(EntitySong song){
        this.songs.add(song);
    }
    public void removeSong(EntitySong song){
        this.songs.remove(song);
    }

    public JSONObject toJSON(){
        JSONObject obj = new JSONObject();
        obj.put("id", id);
        obj.put("title", title);
        obj.put("creation_time", creationTime);
        obj.put("author", author.getId());
        obj.put("amount", songs.size());

        return obj;
    }
}
