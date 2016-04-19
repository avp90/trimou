package org.trimou.handlebars;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.trimou.AbstractTest;
import org.trimou.MustacheExceptionAssert;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.exception.MustacheProblem;
import org.trimou.util.ImmutableList;

/**
 *
 * @author Martin Kouba
 */
public class RepeatHelperTest extends AbstractTest {

    @Test
    public void testHelper() throws InterruptedException {
        MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .registerHelpers(
                        HelpersBuilder.empty().addRepeat().addInvoke().build())
                .build();
        assertEquals(
                "mememe", engine
                        .compileMustache("repeat_01",
                                "{{#repeat times=3}}me{{/repeat}}")
                        .render(null));
        assertEquals(
                "", engine
                        .compileMustache("repeat_02",
                                "{{#repeat while=this}}me{{/repeat}}")
                        .render(false));
        MustacheExceptionAssert.expect(MustacheProblem.RENDER_GENERIC_ERROR)
                .check(new Runnable() {
                    @Override
                    public void run() {
                        engine.compileMustache("repeat_03",
                                "{{#repeat while='this' limit=3}}me{{/repeat}}")
                                .render(true);
                    }
                });
        assertEquals("foobar",
                engine.compileMustache("repeat_04",
                        "{{#with this.iterator}}{{#repeat while=hasNext}}{{next}}{{/repeat}}{{/with}}")
                        .render(ImmutableList.of("foo", "bar")));
        assertEquals("barfoo",
                engine.compileMustache("repeat_05",
                        "{{#invoke this.size on=this m='listIterator'}}{{#repeat while=hasPrevious}}{{previous}}{{/repeat}}{{/invoke}}")
                        .render(ImmutableList.of("foo", "bar")));
    }

    @Test
    public void testValidation() {
        MustacheExceptionAssert
                .expect(MustacheProblem.COMPILE_HELPER_VALIDATION_FAILURE)
                .check(new Runnable() {
                    @Override
                    public void run() {
                        MustacheEngineBuilder.newBuilder()
                                .registerHelpers(HelpersBuilder.empty()
                                        .addRepeat().build())
                                .build().compileMustache("repeat_validation_01",
                                        "{{repeat}}");
                    }
                }).check(new Runnable() {
                    @Override
                    public void run() {
                        MustacheEngineBuilder.newBuilder()
                                .registerHelpers(HelpersBuilder.empty()
                                        .addRepeat().build())
                                .build().compileMustache("repeat_validation_02",
                                        "{{#repeat foo='bar'}}{{/repeat}}");
                    }
                }).check(new Runnable() {
                    @Override
                    public void run() {
                        MustacheEngineBuilder.newBuilder()
                                .registerHelpers(HelpersBuilder.empty()
                                        .addRepeat().build())
                                .build().compileMustache("repeat_validation_03",
                                        "{{#repeat times='a'}}{{/repeat}}");
                    }
                });
    }

}
