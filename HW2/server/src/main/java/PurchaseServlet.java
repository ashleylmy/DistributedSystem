import com.google.gson.Gson;
import io.swagger.client.model.Purchase;
import io.swagger.client.model.ResponseMsg;
import model.PurchaseRecord;

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
        if (!isUrlValid(urlPath)) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseMsg.setMessage("Invalid inputs");
        } else {
            //read the purchase items content from request body
            Purchase purchaseDetails= gson.fromJson(req.getReader(), Purchase.class);
            PurchaseDao purchaseDao= new PurchaseDao();
            //get url info: urlParts = [, storeID, customer, customerID, date, dateString]
            String[] urlParts = urlPath.split("/");
            int storeID= Integer.parseInt(urlParts[1]);
            int custID= Integer.parseInt(urlParts[3]);
            String date= urlParts[5];
            //write the new purchase to the database
            purchaseDao.createPurchase(new PurchaseRecord(storeID, custID, date,purchaseDetails.getItems()));
            responseMsg.setMessage("Write successful on new server");
            res.setStatus(HttpServletResponse.SC_OK);
        }
        out.write(gson.toJson(responseMsg));
        out.flush();
    }

    private boolean isUrlValid(String urlPath) {
        // TODO: validate the request url path according to the API spec
        // urlPath= " /{storeID}/customer/{custID}/date/{date}"
        String regex="/"+"[0-9]+"+"/customer/"+"[0-9]+"+"/date/"+"[1-2]\\d{3}"+"(1[0-2]|0[1-9])"+"(3[01]|[0-2][1-9]|[12]0)";
        Pattern p= Pattern.compile(regex);
        Matcher m= p.matcher(urlPath);
        return m.matches();
    }

}
