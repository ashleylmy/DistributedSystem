import com.google.gson.Gson;
import com.rabbitmq.client.*;
import model.Purchase;
import model.PurchaseRecord;
import model.ResponseMsg;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@WebServlet(name = "PurchaseServlet", value = "/PurchaseServlet")
public class PurchaseServlet extends HttpServlet {
    private final static String QUEUE_NAME = "threadExQ1";
    private ObjectPool<Channel> pool;
    ConnectionFactory factory;
    Connection conn = null;

    public void init() {
        factory = new ConnectionFactory();
        factory.setHost("18.206.212.169");
        factory.setPort(5672);
        factory.setConnectionTimeout(10000);
        factory.setUsername("user1");
        factory.setPassword("user1");
//        factory.setHost("localhost");
        try {
            conn = factory.newConnection();
            pool = new GenericObjectPool<>(new ChannelFactory(conn));
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
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
        Channel channel=null;
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
            String[] urlParts = urlPath.split("/");
            int storeID= Integer.parseInt(urlParts[1]);
            int custID= Integer.parseInt(urlParts[3]);
            String date= urlParts[5];
            //write the new purchase to rabbitMQ exchanger
            String purchaseReq=gson.toJson(new PurchaseRecord(storeID, custID, date,purchaseDetails.getItems()));
            try {
                //get channel from channel pool
                channel = pool.borrowObject();
                channel.queueDeclare(QUEUE_NAME, true, false, false, null);
                // publish the purchase
                channel.basicPublish("",QUEUE_NAME, null, purchaseReq.getBytes("UTF-8"));
                System.out.println("[x]Purchase add to the queue");
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            } finally{
                if(channel != null){
                    try {
                        pool.returnObject(channel);
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    }
                }
            }
            responseMsg.setMessage("Write successful to RabbitMQ");
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
