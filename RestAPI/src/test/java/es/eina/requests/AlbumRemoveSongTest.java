package es.eina.requests;

import es.eina.TestBase;
import es.eina.cache.UserCache;
import es.eina.sql.SQLUtils;
import es.eina.sql.entities.EntityAlbum;
import es.eina.sql.entities.EntitySong;
import es.eina.sql.entities.EntityUser;
import es.eina.sql.utils.HibernateUtils;
import es.eina.utils.AlbumUtils;
import es.eina.utils.SongUtils;
import es.eina.utils.UserUtils;
import org.json.JSONObject;
import org.junit.*;

import java.sql.Date;

public class AlbumRemoveSongTest extends TestBase {

    private EntityUser user;
    private EntityAlbum album;
    private EntitySong song;

    @BeforeClass
    public static void start() {
        openDB();
    }

    @AfterClass
    public static void stop() {
        closeDB();
    }

    @Before
    public void setupTest() {
        openSession();
        user = UserUtils.addUser(s, "test-user", "a@a.net", "123456", "Username :D", "Random BIO", new Date(0), "ES");
        album = AlbumUtils.createAlbum(s, user, "Random Album", 1900);
        song = SongUtils.addSong(s, album, "Random Song", "O1");
        closeSession();
    }

    @After
    public void endTest() {
        openSession();
        HibernateUtils.deleteFromDB(s, album);
        HibernateUtils.deleteFromDB(s, song);
        HibernateUtils.deleteFromDB(s, user);
        closeSession();
    }

    @Test
    public void testErrorsInvalidArgs() {
        JSONObject obj = performTest(new AlbumRequests().removeSongFromAlbum("", user.getToken().getToken(), album.getAlbumId(), song.getId()));
        Assert.assertEquals("invalidArgs", obj.getString("error"));
        obj = performTest(new AlbumRequests().removeSongFromAlbum(null, user.getToken().getToken(), album.getAlbumId(), song.getId()));
        Assert.assertEquals("invalidArgs", obj.getString("error"));

        obj = performTest(new AlbumRequests().removeSongFromAlbum(user.getNick(), "", album.getAlbumId(), song.getId()));
        Assert.assertEquals("invalidArgs", obj.getString("error"));
        obj = performTest(new AlbumRequests().removeSongFromAlbum(user.getNick(), null, album.getAlbumId(), song.getId()));
        Assert.assertEquals("invalidArgs", obj.getString("error"));
    }

    @Test
    public void testErrorsUnknownUser() {
        JSONObject obj = performTest(new AlbumRequests().removeSongFromAlbum("invalid-user", user.getToken().getToken(), album.getAlbumId(), song.getId()));
        Assert.assertEquals("unknownUser", obj.getString("error"));
    }

    @Test
    public void testErrorsInvalidToken() {
        JSONObject obj = performTest(new AlbumRequests().removeSongFromAlbum(user.getNick(), "invalid+" + user.getToken().getToken(), album.getAlbumId(), song.getId()));
        Assert.assertEquals("invalidToken", obj.getString("error"));
    }

    @Test
    public void testErrorsInvalidAlbum() {
        JSONObject obj = performTest(new AlbumRequests().removeSongFromAlbum(user.getNick(), user.getToken().getToken(), -1, song.getId()));
        Assert.assertEquals("invalidAlbum", obj.getString("error"));
    }
    @Test
    public void testErrorsUnknownAlbum() {
        JSONObject obj = performTest(new AlbumRequests().removeSongFromAlbum(user.getNick(), user.getToken().getToken(), Long.MAX_VALUE, song.getId()));
        Assert.assertEquals("unknownAlbum", obj.getString("error"));
    }

    @Test
    public void testErrorsInvalidSong() {
        JSONObject obj = performTest(new AlbumRequests().removeSongFromAlbum(user.getNick(), user.getToken().getToken(), album.getAlbumId(), -1));
        Assert.assertEquals("invalidSong", obj.getString("error"));
    }

    @Test
    public void testErrorsUnknownSong() {
        JSONObject obj = performTest(new AlbumRequests().removeSongFromAlbum(user.getNick(), user.getToken().getToken(), album.getAlbumId(), Long.MAX_VALUE));
        Assert.assertEquals("unknownSong", obj.getString("error"));
    }

    @Test
    public void testErrorsNotAuthor() {
        openSession();
        EntityUser second = UserUtils.addUser(s, "second-user", "a@a.es", "1234", "SecUser", "", new Date(0), "O1");
        closeSession();
        Assert.assertNotNull(second);
        JSONObject obj = performTest(new AlbumRequests().removeSongFromAlbum(second.getNick(), second.getToken().getToken(), album.getAlbumId(), song.getId()));
        Assert.assertEquals("notAuthor", obj.getString("error"));
        openSession();
        UserCache.deleteUser(s, second);
        closeSession();
    }

    @Test
    public void testOK() {

        JSONObject obj = performTest(new AlbumRequests().removeSongFromAlbum(user.getNick(), user.getToken().getToken(), album.getAlbumId(), song.getId()));

        openSession();
        Assert.assertEquals(0, SQLUtils.getRowCount(s, "song", "id = " + song.getId() + " and album_id = " + album.getAlbumId()));
        closeSession();
        Assert.assertEquals("ok", obj.getString("error"));
    }

    @Test
    public void testAlreadyAdded() {

        JSONObject obj = performTest(new AlbumRequests().removeSongFromAlbum(user.getNick(), user.getToken().getToken(), album.getAlbumId(), song.getId()));
        Assert.assertEquals("ok", obj.getString("error"));

        obj = performTest(new AlbumRequests().removeSongFromAlbum(user.getNick(), user.getToken().getToken(), album.getAlbumId(), song.getId()));
        Assert.assertEquals("alreadyRemoved", obj.getString("error"));
    }
}
