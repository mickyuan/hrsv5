package hrds.utils.key;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class primayKeyGenerTest {
    @Test
    public void getNextKey(){
        for (int i = 0; i < 10; i++) {
            String key = PrimayKeyGener.getNextId();
            assertThat(key,allOf(notNullValue()));
            assertThat(key.length(),is(10));
        }
    }

    @Test
    public void getOrderNO(){
        String key =  PrimayKeyGener.getOrderNO();
        assertThat(key,allOf(notNullValue()));
        assertThat(key.length(),is(18));
    }
    @Test
    public void getRandomStr(){
        String key =  PrimayKeyGener.getRandomStr();
        assertThat(key,allOf(notNullValue()));
        assertThat(key.length(),is(6));
    }
    @Test
    public void getRandomTime(){
        String key =  PrimayKeyGener.getRandomTime();
        assertThat(key,allOf(notNullValue()));
        assertThat(key.length(),is(12));
    }
    @Test
    public void getOperId(){
        String key =  PrimayKeyGener.getOperId();
        assertThat(key,allOf(notNullValue()));
        assertThat(key.length(),is(4));
    }
}
