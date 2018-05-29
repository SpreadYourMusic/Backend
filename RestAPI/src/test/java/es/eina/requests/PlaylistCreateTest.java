package es.eina.requests;

import es.eina.TestBase;
import es.eina.cache.SongListCache;
import es.eina.cache.UserCache;
import es.eina.sql.SQLUtils;
import es.eina.sql.entities.EntitySongList;
import es.eina.sql.entities.EntityUser;
import es.eina.utils.SongListsUtils;
import es.eina.utils.UserUtils;
import org.json.JSONObject;
import org.junit.*;

import java.sql.Date;

public class PlaylistCreateTest extends TestBase {

    private EntityUser user;

    @BeforeClass
    public static void start(){
        openDB();
    }

    @AfterClass
    public static void stop(){
        closeDB();
    }

    @Before
    public void setupTest(){
        openSession();
        user = UserUtils.addUser(s,"test-user", "a@a.net", "123456", "Username :D", "Random BIO", new Date(0), "ES");
        closeSession();
    }

    @After
    public void endTest(){
        openSession();
        UserCache.deleteUser(s, UserCache.getUser(s, user.getId()));
        closeSession();
    }

    //obj = performTest(new UserSongListRequests().create(user.getNick(), user.getToken().getToken(), "Title"));

    @Test
    public void testErrorsInvalidArgs(){
        JSONObject obj = performTest(new UserSongListRequests().create("", user.getToken().getToken(), "Title"));
        Assert.assertEquals("invalidArgs", obj.getString("error"));
        obj = performTest(new UserSongListRequests().create(null, user.getToken().getToken(), "Title"));
        Assert.assertEquals("invalidArgs", obj.getString("error"));

        obj = performTest(new UserSongListRequests().create(user.getNick(), "", "Title"));
        Assert.assertEquals("invalidArgs", obj.getString("error"));
        obj = performTest(new UserSongListRequests().create(user.getNick(), null, "Title"));
        Assert.assertEquals("invalidArgs", obj.getString("error"));

        obj = performTest(new UserSongListRequests().create(user.getNick(), user.getToken().getToken(), ""));
        Assert.assertEquals("invalidArgs", obj.getString("error"));
        obj = performTest(new UserSongListRequests().create(user.getNick(), user.getToken().getToken(), null));
        Assert.assertEquals("invalidArgs", obj.getString("error"));

    }

    @Test
    public void testErrorsUnknownUser(){
        JSONObject obj = performTest(new UserSongListRequests().create("invalid-user", user.getToken().getToken(), "Title"));
        Assert.assertEquals("unknownUser", obj.getString("error"));
    }

    @Test
    public void testErrorsInvalidToken(){
        JSONObject obj = performTest(new UserSongListRequests().create(user.getNick(), "invalid-token", "Title"));
        Assert.assertEquals("invalidToken", obj.getString("error"));
    }

    @Test
    public void testErrorsOK(){
        JSONObject obj = performTest(new UserSongListRequests().create(user.getNick(), user.getToken().getToken(), "Title"));

        openSession();
        Assert.assertEquals(1, SQLUtils.getRowCount(s,"song_list", "id = " + obj.getJSONObject("list").getLong("id")));
        closeSession();
        Assert.assertEquals("ok", obj.getString("error"));
    }
}
