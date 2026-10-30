package com.mishra;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class HtmlTest {
  @Test
  void escapesMarkupAndQuotes() {
    assertEquals(
        "&lt;script&gt;&quot;a&quot; &amp; &#39;b&#39;", Html.escape("<script>\"a\" & 'b'"));
    assertEquals("", Html.escape(null));
  }
}
