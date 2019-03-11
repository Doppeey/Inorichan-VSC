package EventsAndCommands.UtilityCommands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.mashape.unirest.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;

public class TranslateCommand extends Command {

    public TranslateCommand() {
        this.name = "translate";
        this.help = "translates using google translator";
    }

    @Override
    protected void execute(CommandEvent event) {

        String args = event.getArgs();
            String returnString = "";

            try {
                String textEncoded= URLEncoder.encode(args, "utf-8");
                String url = "https://translate.googleapis.com/translate_a/single?client=gtx&sl=auto&tl=en&dt=t&q=" + textEncoded;
                var httpclient = new DefaultHttpClient();
                CloseableHttpResponse response = httpclient.execute(new HttpGet(url));
                StatusLine statusLine = response.getStatusLine();
                if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    String responseString = out.toString();
                    out.close();

                    String aJsonString = responseString;
                    aJsonString = aJsonString.replace("[", "");
                    aJsonString = aJsonString.replace("]", "");
                    aJsonString = aJsonString.substring(1);
                    int plusIndex = aJsonString.indexOf('"');
                    aJsonString = aJsonString.substring(0, plusIndex);

                    returnString = aJsonString;
                } else{
                    response.getEntity().getContent().close();
                    throw new IOException(statusLine.getReasonPhrase());
                }
            } catch(Exception e) {
                returnString = e.getMessage();
            }

            event.reply(returnString);

        }

    }

