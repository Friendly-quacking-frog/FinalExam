import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jdk.jfr.Description;
import org.example.Company;
import org.example.ResponseHandler;
import org.junit.jupiter.api.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CompanyTests {
    static String userToken; //User token for POST and PATH requests
    static int COMPANY_ID; //ID of created company for tests
    static int SECONDARY_COMPANY_ID; //ID of second created company
    static ResponseHandler handler = new ResponseHandler();

    @BeforeEach
    void beforeEach(){
        //Before execution of each test, get user token by login
        //Get user token
        userToken = handler.getUserToken();
    }

    @Test
    @Order(1)
    @DisplayName("Check response code of retrieving company list")
    void getCompanyListCodeTest() {
        //Send request to get list of companies and check response code
        JsonObject jsonObject =
                handler.getJsonResponseFromRequest("company", null);
        int result = handler.getCodeFromResponse(jsonObject);
        assert result == 200;
    }

    @Test
    @Order(2)
    @DisplayName("Check response body of retrieving company list")
    void getCompanyListBodyTest(){
        //Send request to get list of companies and check response body
        JsonObject jsonObject =
                handler.getJsonResponseFromRequest("company", null);
        String result = String.valueOf(handler.getBodyFromResponse(jsonObject));
        assert result != "";
    }

    @Test
    @Order(3)
    @DisplayName("Check response of retrieving filtered company list")
    void getCompanyListBodyTestWithCondition(){
        //Send request to get filtered list of companies and check filter
        JsonObject jsonObject =
                handler.getJsonResponseFromRequest("company?active=true", null);
        //Create a Gson object to convert JSON to array
        //Create ot here since it's used only once
        JsonArray result = handler.getArrayFromResponse(jsonObject);
        Gson gson = new Gson();
        //Convert JSON to array of companies
        Company[] companies = gson.fromJson(result, Company[].class);
        //Check that all companies are active
        for (Company company: companies) {
            assert company.isActive;
        }
    }

    @Test
    @Order(4)
    @DisplayName("Check response code of creating a company")
    void postCompanyCheckCode() {
        //Send request to create a company and check returned code
        Company company = new Company();
        String params = handler.CompanyToJson(company);
        JsonObject jsonObject =
                handler.postJsonResponseFromRequest("company", params, userToken);
        int code = handler.getCodeFromResponse(jsonObject);
        //Get ID of new company
        int companyId = handler.getFieldFromResponse("id", jsonObject).getAsInt();
        //Save ID to delete company later for another test
        SECONDARY_COMPANY_ID = companyId;
        //Check response code
        assert code == 201;
    }

    @Test
    @Order(5)
    @DisplayName("Check response body of creating a company")
    void postCompanyCheckBody() {
        //Send request to create a company and check returned id
        Company company = new Company();
        String params = handler.CompanyToJson(company);
        JsonObject jsonObject =
                handler.postJsonResponseFromRequest("company", params, userToken);
        //Get ID of new company
        int companyId = handler.getFieldFromResponse("id", jsonObject).getAsInt();
        //Save ID to work with it later
        COMPANY_ID = companyId;
        //Check ID is normal
        assert companyId != 0;
    }

    @Test
    @Order(6)
    @DisplayName("Check response code of creating an incrrect company")
    void postIncorrectCompanyCheckCode() {
        //Send incorrect request to create a company and check returned code
        JsonObject jsonObject =
                handler.postJsonResponseFromRequest("company", "", userToken);
        int code = handler.getCodeFromResponse(jsonObject);
        assert code == 500;
    }

    @Test
    @Order(7)
    @DisplayName("Check response code of getting a certain company")
    void getCompanyCheckCode(){
        //Send request to get data of certain company and check response code
        JsonObject jsonObject =
                handler.getJsonResponseFromRequest("company/"+COMPANY_ID, null);
        int result = handler.getCodeFromResponse(jsonObject);
        assert result == 200;
    }

    @Test
    @Order(8)
    @DisplayName("Check response code of getting a certain company by wrong id")
    @Description("This test will fail until api properly returns code 404 when prompted with wrong company ID such as -1")
    void getIncorrectCompanyCheckCode(){
        //Send request to get data of certain company and check response code
        //This test will fail until api properly returns code 404 when prompted
        //with wrong company ID such as -1
        JsonObject jsonObject =
                handler.getJsonResponseFromRequest("company/-1", null);
        int result = handler.getCodeFromResponse(jsonObject);
        assert result == 404;
    }

    @Test
    @Order(8)
    @DisplayName("Check response body of getting a certain company")
    void getCompanyCheckBody(){
        //Send request to get data of certain company and check response code
        JsonObject jsonObject =
                handler.getJsonResponseFromRequest("company/"+COMPANY_ID, null);
        //Get ID of company in response
        int result = handler.getFieldFromResponse("id", jsonObject).getAsInt();
        //Check that ID
        assert result == COMPANY_ID;
        //Get company from response
        JsonObject body = handler.getBodyFromResponse(jsonObject);
        Company company = handler.JsonToCompany(body);
        //Check that company equals to basic company from constructor
        //since that is what we send in one of previous requests
        //Checking fields separately
        //Checking name
        assert company.getName().equals((new Company()).getName());
        //Checking description
        assert company.getDescription().equals((new Company()).getDescription());
    }

    @Test
    @Order(9)
    @DisplayName("Check response code of patching a certain company")
    @Description("Successful call of patch endpoint will return code 200, not 202 as said in docs; therefore this test will fail until it is fixed")
    void patchCompanyCheckCode(){
        //Send request to patch data of certain company and check response code
        Company company = new Company();
        company.description = "new Description";
        String params = handler.CompanyToJson(company);
        JsonObject jsonObject =
                handler.patchJsonResponseFromRequest("company/"+COMPANY_ID, params,userToken);
        int result = handler.getCodeFromResponse(jsonObject);
        //Correct patch will return code 200
        //Not 202 as said in swagger
        assert result == 202;
    }

    @Test
    @Order(10)
    @DisplayName("Check response body of patching a certain company")
    void patchCompanyCheckBody(){
        //Send request to patch data of certain company and check response code
        Company company = new Company();
        company.description = "new Description";
        String params = handler.CompanyToJson(company);
        JsonObject jsonObject =
                handler.patchJsonResponseFromRequest("company/"+COMPANY_ID, params,userToken);
        //Check that request changed correct company
        int id = handler.getFieldFromResponse("id", jsonObject).getAsInt();
        //Check that ID
        assert id == COMPANY_ID;
        //Check data from patched company
        JsonObject body = handler.getBodyFromResponse(jsonObject);
        Company result = handler.JsonToCompany(body);
        //Check that company equals to basic company from constructor
        //since that is what we send in one of previous requests
        //Checking fields separately
        //Checking name
        assert result.getName().equals(company.getName());
        //Checking description
        assert result.getDescription().equals(company.getDescription());
    }

    @Test
    @Order(11)
    @DisplayName("Check response code of patching company by wrong ID")
    @Description("Request returns code 200, although somewhere deep in response body there is status 404, but not in the response code")
    void patchWrongCompanyCheckCode(){
        //Send request to patch data of certain company and check response code
        Company company = new Company();
        company.description = "new Description";
        String params = handler.CompanyToJson(company);
        JsonObject jsonObject =
                handler.patchJsonResponseFromRequest("company/9999", params,userToken);
        int result = handler.getCodeFromResponse(jsonObject);
        //Somehow finds company with non-existing id
        assert result == 404;
    }

    @Test
    @Order(12)
    @DisplayName("Check response code of patching status of a company")
    @Description("Successful call of patch endpoint will return code 200, not 201 as said in docs; therefore this test will fail until it is fixed")
    void patchCompanyStatusCheckCode(){
        //Send request to patch data of certain company and check response code
        //Setting company status as inactive
        String params = "{\"isActive\":false}";
        JsonObject jsonObject =
                handler.patchJsonResponseFromRequest("company/status/"+COMPANY_ID, params,userToken);
        int result = handler.getCodeFromResponse(jsonObject);
        //Correct path will return code 200
        //Not 201 as said in swagger
        assert result == 201;
    }

    @Test
    @Order(13)
    @DisplayName("Check response body of patching status of a company")
    void patchCompanyStatusCheckBody(){
        //Send request to patch data of certain company and check response code
        //Setting company status as inactive
        String params = "{\"isActive\":false}";
        JsonObject jsonObject =
                handler.patchJsonResponseFromRequest("company/status/"+COMPANY_ID, params,userToken);
        //Check that request changed correct company
        int id = handler.getFieldFromResponse("id", jsonObject).getAsInt();
        //Check that ID
        assert id == COMPANY_ID;
        //Check new status of that company
        assert !handler.getFieldFromResponse("isActive", jsonObject).getAsBoolean();
    }

    @Test
    @Order(14)
    @DisplayName("Check response code of patching company status by wrong ID")
    @Description("Request returns code 200, should return 404")
    void patchStatusWrongCompanyCheckCode(){
        //Send request to patch data of certain company and check response code
        //Setting company status as inactive
        String params = "{\"isActive\":false}";
        JsonObject jsonObject =
                handler.patchJsonResponseFromRequest("company/status/9999", params,userToken);
        int result = handler.getCodeFromResponse(jsonObject);
        //Somehow finds company with non-existing id
        assert result == 404;
    }

    @Test
    @Order(15)
    @DisplayName("Check response code of deleting a company")
    void deleteCompanyCheckCode(){
        //Send request to patch data of certain company and check response code
        //Delete a company that was created in earlier test
        JsonObject jsonObject =
                handler.getJsonResponseFromRequest("company/delete/"+COMPANY_ID, userToken);
        //Get response code
        int result = handler.getCodeFromResponse(jsonObject);
        //Check response code
        assert result == 200;
    }

    @Test
    @Order(16)
    @DisplayName("Check response body of deleting a company")
    @Description("Fails because returned body is empty, but shouldn't be")
    void deleteCompanyCheckBody(){
        //Send request to patch data of certain company and check response code
        //Delete a company that was created in earlier test
        JsonObject jsonObject =
                handler.getJsonResponseFromRequest("company/delete/"+SECONDARY_COMPANY_ID, userToken);
        //Get ID of company in response
        int result = handler.getFieldFromResponse("id", jsonObject).getAsInt();
        //Check that ID
        assert result == COMPANY_ID;
        //Get company from response
        JsonObject body = handler.getBodyFromResponse(jsonObject);
        Company company = handler.JsonToCompany(body);
        //Check that company equals to basic company from constructor
        //since that is what we send in one of previous requests
        //Checking fields separately
        //Checking name
        assert company.getName().equals((new Company()).getName());
        //Checking description
        assert company.getDescription().equals((new Company()).getDescription());
    }

    @Test
    @Order(17)
    @DisplayName("Check response code of deleting company status by wrong ID")
    @Description("Request returns code 200, should return 404")
    void deleteWrongCompanyCheckCode(){
        //Send request to patch data of certain company and check response code
        //Setting company status as inactive
        JsonObject jsonObject =
                handler.getJsonResponseFromRequest("company/delete/9999" ,userToken);
        int result = handler.getCodeFromResponse(jsonObject);
        //Somehow finds company with non-existing id
        assert result == 404;
    }
}
