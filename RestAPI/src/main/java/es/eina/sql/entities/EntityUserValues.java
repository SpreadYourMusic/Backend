package es.eina.sql.entities;

import javax.persistence.*;

@Entity(name="userData")
@Table(name="user_values")
public class EntityUserValues extends EntityBase{

    @Id
    @Column(name = "user_id")
    private long userId;

    @Column(name = "admin")
    private boolean admin;

    @Column(name = "verified")
    private boolean verified;

    @OneToOne
    @JoinColumn(name = "user_id")
    private EntityUser user;

    /**
     * DO NOT use this method as it can only be used by Hibernate
     */
    public EntityUserValues(){
        update();
    }

    public EntityUserValues(EntityUser user){
        this(user, false, false);
    }

    public EntityUserValues(EntityUser user, boolean admin, boolean verified){
        this.user = user;
        this.userId = user.getId();
        this.admin = admin;
        this.verified = verified;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public boolean isAdmin() {
        return admin;
    }

    public EntityUser getUser() {
        return user;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public boolean isVerified() {
        return verified;
    }

    public boolean cleanUp() {
        return verified || admin;
    }
}
