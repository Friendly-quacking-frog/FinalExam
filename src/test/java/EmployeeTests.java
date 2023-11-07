import com.google.gson.JsonObject;
import org.example.Employee;
import org.example.ResponseHandler;
import org.junit.jupiter.api.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EmployeeTests {

    static String userToken; //User token for POST and PATH requests
    static int COMPANY_ID; //ID of created company for tests
    static int EMPLOYEE_ID; //ID of created employee

    static ResponseHandler handler = new ResponseHandler();

    @BeforeEach
    void beforeEach(){
        //Before execution of each test, get user token by login
        //Get user token
        userToken = handler.getUserToken();
    }

    @BeforeAll
    static void beforeAll() {
        //Before execution of each test, get user token by login
        //Get user token
        userToken = handler.getUserToken();
        //Create a test company for employee API tests
        String params = "{\"name\": \"FQF_Test\", \"description\": \"FQF_Test\"}";
        JsonObject jsonObject = handler.postJsonResponseFromRequest("company", params, userToken);
        COMPANY_ID = handler.getFieldFromResponse("id", jsonObject).getAsInt();
    }

    @AfterAll
    static void afterAll() {
        //Delete created company after completion of tests
        handler.getJsonResponseFromRequest("company/delete/" + COMPANY_ID, userToken);
    }


    @Test
    @Order(1)
    @DisplayName("Check response code of retrieving employee list of new company")
    void getEmployeeListCodeTest() {
        //Send request to get list of employees of company and check response code
        JsonObject jsonObject =
                handler.getJsonResponseFromRequest("employee?company=" + COMPANY_ID, null);
        int result = handler.getCodeFromResponse(jsonObject);
        assert result == 200;
    }

    @Test
    @Order(2)
    @DisplayName("Check body of response of retrieving employee list of new company")
    void getEmployeeListBodyTest() {
        //Send request to get list of employees of company and check response body
        JsonObject jsonObject =
                handler.getJsonResponseFromRequest("employee?company=" + COMPANY_ID, null);
        String result = jsonObject.get("response").getAsString();
        assert result.equals("[]");
    }

    @Test
    @Order(3)
    @DisplayName("Check response code of request with incorrect company id")
    void getEmployeeWrongListCode() {
        //Send botched request to get list of employees and make sure we get an error
        JsonObject jsonObject =
                handler.getJsonResponseFromRequest("employee?company=garbage", null);
        int result = handler.getCodeFromResponse(jsonObject);
        assert result == 500;
    }

    @Test
    @Order(4)
    @DisplayName("Response code of request for creating an employee")
    void testCodeCreateEmployeeTest() {
        //Create an instance of employee class to convert it into JSON
        Employee employee = new Employee(COMPANY_ID);
        //Converting to JSON
        String params = handler.EmployeeToJson(employee);
        //Sending request
        JsonObject jsonObject = handler.postJsonResponseFromRequest("employee", params, userToken);
        //Get response code and make sure it's correct
        int code = handler.getCodeFromResponse(jsonObject);
        assert code == 201;
    }

    @Test
    @Order(5)
    @DisplayName("Response body of request for creating an employee")
    void testBodyCreateEmployeeTest() {
        //Create an instance of employee class to convert it into JSON
        Employee employee = new Employee(COMPANY_ID);
        //Converting to JSON
        String params = handler.EmployeeToJson(employee);
        //Sending request
        JsonObject jsonObject = handler.postJsonResponseFromRequest("employee", params, userToken);
        //Get response code and make sure it's correct
        EMPLOYEE_ID = handler.getBodyFromResponse(jsonObject).get("id").getAsInt();
        assert EMPLOYEE_ID > 0;
    }

    @Test
    @Order(6)
    @DisplayName("Response code of incorrect request for creating an employee")
    void testCodeCreateWrongEmployeeTest() {
        //Create an instance of employee class to convert it into JSON
        Employee employee = new Employee(0);
        //Converting to JSON
        String params = handler.EmployeeToJson(employee);
        //Sending request
        JsonObject jsonObject = handler.postJsonResponseFromRequest("employee", params, userToken);
        //Get response code and make sure it's correct
        int code = handler.getCodeFromResponse(jsonObject);
        assert code == 500;
    }

    @Test
    @Order(7)
    @DisplayName("Get code of response from requesting employee data by id")
    void testCodeGetEmployeeById(){
        //Send request to get list of employees of company and check response code
        JsonObject jsonObject =
                handler.getJsonResponseFromRequest("employee/" + EMPLOYEE_ID, null);
        int result = handler.getCodeFromResponse(jsonObject);
        assert result == 200;
    }

    @Test
    @Order(8)
    @DisplayName("Check body of response of retrieving employee list of new company")
    void testBodyGetEmployeeById() {
        //Send request to get list of employees of company and check response body
        JsonObject jsonObject =
                handler.getJsonResponseFromRequest("employee/" + EMPLOYEE_ID, null);
        String result = jsonObject.get("response").getAsString();
        //Create instance of TestEmployee from request
        Employee testEmployee = handler.JsonToEmployee(result);
        //compare id and companyId from request with those in tests
        assert testEmployee.getId() == EMPLOYEE_ID;
        assert testEmployee.getCompanyId() == COMPANY_ID;
    }

    @Test
    @Order(9)
    @DisplayName("Get code of response from requesting employee data by wrong id")
    void testCodeGetWrongEmployeeById(){
        //Send request to get list of employees of company and check response code
        JsonObject jsonObject =
                handler.getJsonResponseFromRequest("employee/garbage", null);
        int result = jsonObject.get("statusCode").getAsInt();
        assert result == 500;
    }

    @Test
    @Order(10)
    @DisplayName("Get code of response from patching employee data by id")
    void testCodePatchEmployeeById(){
        //Make JSON with parameters
        String params = "{\"lastName\": \"Testing\", \"email\": \"Another@email.com\"," +
                "\"url\": \"url.net\", \"phone\": \"phone\", \"isActive\": false}";
        //Send request to get list of employees of company and check response code
        JsonObject jsonObject =
                handler.patchJsonResponseFromRequest("employee/" + EMPLOYEE_ID, params, userToken);
        int result = handler.getCodeFromResponse(jsonObject);
        assert result == 200;
    }

    @Test
    @Order(11)
    @DisplayName("Get body of respone from patching employee data by id")
    void testBodyPatchEmployeeById(){
        //Make JSON with parameters
        String params = "{\"lastName\": \"Testing\", \"email\": \"Another@email.com\"," +
                "\"url\": \"url.net\", \"phone\": \"string\", \"isActive\": false}";
        //Send request to get list of employees of company and check response code
        JsonObject jsonObject =
                handler.patchJsonResponseFromRequest("employee/" + "245", params, userToken);
        Employee result = handler.JsonToEmployee(jsonObject.get("response").getAsString());
        //Check that correct employee data was changed
        assert result.getId() == 245;
        //Check that fields were changed
        //Example response on Swagger drastically differs from actual response,
        //Half of fields are missing from actual response!
        assert result.getEmail().equals("Another@email.com");
        assert result.getUrl().equals("url.net");
    }

    @Test
    @Order(12)
    @DisplayName("Get code from incorrect request to patch employee data")
    void testCodeWrongPatchEmployeeById(){
        //Make JSON with parameters
        String params = "{\"lastName\": \"Testing\", \"email\": \"Another@email.com\"," +
                "\"url\": \"phone\", \"phone\": \"url.net\"}";
        //Send request to get list of employees of company and check response code
        JsonObject jsonObject =
                handler.patchJsonResponseFromRequest("employee/" + 99999999, params, userToken);
        int result = handler.getCodeFromResponse(jsonObject);
        assert result == 500;
    }
}
