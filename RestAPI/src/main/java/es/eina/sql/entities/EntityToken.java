package es.eina.sql.entities;

import es.eina.sql.utils.HibernateUtils;
import es.eina.utils.RandomString;
import org.hibernate.Session;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.transaction.Transactional;

@Entity(name="token")
@Table(name="sessions")
public class EntityToken extends EntityBase{

    private static final RandomString randomTokenGenerator = new RandomString(16);
    private static final long TOKEN_VALID_TIME = 2592000000L;

    @Column(name = "token", nullable = false)
    private String token;

    @Column(name = "time", nullable = false)
    private long time;

    @Column(name = "amount", nullable = false)
    private long amount;

    @Id
    @OneToOne
    @JoinColumn(name = "user_id")
    private EntityUser user;

    /**
     * DO NOT use this method as it can only be used by Hibernate
     */
    public EntityToken(){

    }

    public EntityToken(EntityUser user) {
        this.user = user;
        updateToken();
    }

    public EntityUser getUser() {
        return user;
    }

    public String getToken() {
        return token;
    }

    public long getTime() {
        return time;
    }

    void updateToken() {
        if(time < System.currentTimeMillis()) {
            this.amount++;
            this.time = System.currentTimeMillis() + TOKEN_VALID_TIME;
        }

        if(this.token == null){
            this.token = randomTokenGenerator.nextString();
        }
    }

    public boolean isValid(String token) {
        return time >= System.currentTimeMillis() && this.token.equals(token);
    }

    void removeUser(){
        this.user = null;
    }

    public void removeSession() {
        this.amount--;
    }

    public boolean shouldRemoveToken(){
        return amount <= 0;
    }
}
