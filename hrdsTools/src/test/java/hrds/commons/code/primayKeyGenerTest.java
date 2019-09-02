package hrds.commons.code;

import hrds.commons.utils.key.PrimayKeyGener;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

public class primayKeyGenerTest {
    @Test
    public void getNextKey(){
        for (int i = 0; i < 10; i++) {
            String key = hrds.commons.utils.key.PrimayKeyGener.getNextId();
            MatcherAssert.assertThat(key, Matchers.allOf(Matchers.notNullValue()));
            MatcherAssert.assertThat(key.length(), Matchers.is(10));
        }
    }

    @Test
    public void getOrderNO(){
        String key =  hrds.commons.utils.key.PrimayKeyGener.getOrderNO();
        MatcherAssert.assertThat(key, Matchers.allOf(Matchers.notNullValue()));
        MatcherAssert.assertThat(key.length(), Matchers.is(18));
    }
    @Test
    public void getRandomStr(){
        String key =  hrds.commons.utils.key.PrimayKeyGener.getRandomStr();
        MatcherAssert.assertThat(key, Matchers.allOf(Matchers.notNullValue()));
        MatcherAssert.assertThat(key.length(), Matchers.is(6));
    }
    @Test
    public void getRandomTime(){
        String key =  hrds.commons.utils.key.PrimayKeyGener.getRandomTime();
        MatcherAssert.assertThat(key, Matchers.allOf(Matchers.notNullValue()));
        MatcherAssert.assertThat(key.length(), Matchers.is(12));
    }
    @Test
    public void getOperId(){
        String key =  PrimayKeyGener.getOperId();
        MatcherAssert.assertThat(key, Matchers.allOf(Matchers.notNullValue()));
        MatcherAssert.assertThat(key.length(), Matchers.is(4));
    }
}
