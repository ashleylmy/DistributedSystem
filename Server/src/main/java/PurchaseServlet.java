

import com.google.gson.Gson;
import io.swagger.client.model.ResponseMsg;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@WebServlet(name = "PurchaseServlet", value = "/PurchaseServlet")
public class PurchaseServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        res.setStatus(HttpServletResponse.SC_OK);
        res.getWriter().write("purchase page");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        String urlPath = req.getPathInfo();
        PrintWriter out=res.getWriter();
        ResponseMsg responseMsg = new ResponseMsg();
        Gson gson=new Gson();
        // check we have a URL!
        if (urlPath == null || urlPath.isEmpty()) {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            responseMsg.setMessage("Data not found");
            out.write(gson.toJson(responseMsg));
            return ;
        }

//        String[] urlParts = urlPath.split("/");
        // and now validate url path and return the response status code
        // (and maybe also some value if input is valid)

        if (!isUrlValid(urlPath)) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseMsg.setMessage("Invalid inputs");
            out.write(gson.toJson(responseMsg));
        } else {
            res.setStatus(HttpServletResponse.SC_OK);
            String requestData = req.getReader().lines().collect(Collectors.joining());
            // do any sophisticated processing with urlParts which contains all the url params
            responseMsg.setMessage("Write successful");
            out.write(gson.toJson(responseMsg));
            out.write(requestData);
        }
        out.flush();
    }

    private boolean isUrlValid(String urlPath) {
        // TODO: validate the request url path according to the API spec
        // urlPath= " /{storeID}/customer/{custID}/date/{date}"
        // urlParts = [, 1, seasons, 2019, day, 1, skier, 123]
        String regex="/"+"[0-9]+"+"/customer/"+"[0-9]+"+"/date/"+"[1-2]\\d{3}"+"(1[0-2]|0[1-9])"+"(3[01]|[0-2][1-9]|[12]0)";
        Pattern p= Pattern.compile(regex);
        Matcher m= p.matcher(urlPath);
        return m.matches();
    }

}
