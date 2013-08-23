package uk.co.wireweb.rift.json.view;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Daniel Johansson
 * @since 2013-08-23 12:33
 */
public class JsonViewTest {

    @Mock
    private HttpServletRequest mockHttpServletRequest;
    @Mock
    private HttpServletResponse mockHttpServletResponse;
    private JsonView jsonView;
    private ByteArrayOutputStream byteArrayOutputStream;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        byteArrayOutputStream = new ByteArrayOutputStream();
        when(mockHttpServletResponse.getWriter()).thenReturn(new PrintWriter(byteArrayOutputStream));
    }

    @Test
    public void execute_shouldSetcorrectResponseHeaders() throws Exception {
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("length", "15");

        jsonView = new JsonView(jsonObject);
        jsonView.execute(mockHttpServletRequest, mockHttpServletResponse);

        verify(mockHttpServletResponse).setContentType("application/json");
        verify(mockHttpServletResponse).setContentLength(15);
    }

    @Test
    public void execute_shouldCorrectlySerializeJsonElement() throws Exception {
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", "json view");
        jsonObject.addProperty("number", 45);

        jsonView = new JsonView(jsonObject);
        jsonView.execute(mockHttpServletRequest, mockHttpServletResponse);

        assertThat(new String(byteArrayOutputStream.toByteArray())).isEqualTo("{\"name\":\"json view\",\"number\":45}");
    }

    @Test
    public void execute_shouldCorrectlySerializeObject() throws Exception {
        jsonView = new JsonView(new TestObjectToSerialize("json view", 45));
        jsonView.execute(mockHttpServletRequest, mockHttpServletResponse);

        assertThat(new String(byteArrayOutputStream.toByteArray())).isEqualTo("{\"name\":\"json view\",\"number\":45}");
    }

    private static class TestObjectToSerialize {

        @Expose
        private String name;
        @Expose
        private int number;

        public TestObjectToSerialize(final String name, final int number) {
            this.name = name;
            this.number = number;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }
    }
}
