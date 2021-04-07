import com.google.gson.Gson;
import model.Purchase;
import model.PurchaseRecord;
import model.ResponseMsg;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Servlet handles request to get report for certain item
 * The get request will return top 5 stores for item sales for this assignment
 */

@WebServlet(name = "ItemServlet", value = "/ItemServlet")
public class ItemServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        String urlPath = req.getPathInfo();
        PrintWriter out=res.getWriter();
        ResponseMsg responseMsg = new ResponseMsg();
        Gson gson=new Gson();
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
            //get the store id
            String itemID="Item"+urlPath.substring(1);
            String response="";
            //send request to rpc client
            try{
                RPCClient rpcClient=new RPCClient();
                System.out.println(" [x] Requesting " + itemID);
                response=rpcClient.call(itemID);
                System.out.println(" [.] Got '" + response + "'");
            } catch (TimeoutException | IOException | InterruptedException e) {
                e.printStackTrace();
            }
            response= "{\"stores\": " + response + "}";
            res.getWriter().println(response);
            res.setStatus(HttpServletResponse.SC_OK);
        }
        out.write(gson.toJson(responseMsg));
        out.flush();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    private boolean isUrlValid(String urlPath) {
        // urlPath= " /{itemID}"
        String regex="/"+"[0-9]+";
        Pattern p= Pattern.compile(regex);
        Matcher m= p.matcher(urlPath);
        return m.matches();
    }
}
