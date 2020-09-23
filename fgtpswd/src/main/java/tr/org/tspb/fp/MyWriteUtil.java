package tr.org.tspb.fp;

import com.google.common.io.CharStreams;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class MyWriteUtil {

    private static MyWriteUtil SINGLETON;
    private String style = null;
    private final String PARAM_UPDATE_PSWD_TOKEN = "updatePswdToken";

    private ResourceBundle messages;
    private static final String MESSAGE_PATH = "tr.org.tspb.fp.config";

    private MyWriteUtil() {
    }

    private MyWriteUtil(String clientCode) {

        Locale trLocale = new Locale("tr", "TR");

        switch (clientCode) {
            case "TSPB":
                messages = ResourceBundle.getBundle(MESSAGE_PATH, trLocale);
                break;
            case "TDUB":
                messages = ResourceBundle.getBundle("tr.org.tspb.fp.tdub.config", trLocale);
                break;
            default:
                messages = ResourceBundle.getBundle(MESSAGE_PATH, trLocale);
        }

        InputStream styleInputStream = MyWriteUtil.class.getResourceAsStream("style.css");
        try (final Reader reader = new InputStreamReader(styleInputStream)) {
            style = CharStreams.toString(reader);
        } catch (IOException ioe) {

        }
    }

    public static MyWriteUtil instance(String clientCode) {
        SINGLETON = new MyWriteUtil(clientCode);
        return SINGLETON;
    }

    public static MyWriteUtil instance() {
        return SINGLETON;
    }

    public void writeWelcomePage(PrintWriter out, String desc, String err, String action, String homeaddr) {

        printHead(out);
        startBody(out);
        openContainer(out);
        printTop(out, homeaddr, messages.getString("module_name"));

        if (desc != null) {
            infomMessage(out, desc);
        }

        successMessage(out, messages.getString("type_your_email"));

        if (err != null) {
            dangerMessage(out, err);
        }

        out.println("<form   method='POST' action='" + action + "'>");
        out.println("     <div class='row'>");
        out.println("           <div class='col-md-2'></div>");
        out.println("           <div class='col-md-8'>");
        out.println("              <input type='text' name='email' style='width:100%;text-align:center;'><br/><br/>");
        out.println("           </div>");
        out.println("           <div class='col-md-2'></div>");
        out.println("     </div>");

        out.println("     <div class='form-group'>");
        out.println("       <div class='col-md-6'>");
        out.println("         <div class='g-recaptcha' data-sitekey='6LcQzG4UAAAAACk87rF4aMb07mwTiZnldOiqWh8s'></div>");
        out.println("       </div>");
        out.println("       <div class='col-md-6'>");
        out.println("            <button style='background:#0074C8;color: #FFF;font-size: 15px;width: 100%;height: 75px;' type='submit'>");
        out.println("                <span>gönder</span>");
        out.println("            </button>");
        out.println("       </div>");
        out.println("     </div>");
        out.println("</form>");

        closeContainer(out);

        out.println("</body>");
        out.println("</html>");

    }

    public void writeUpdatePswd(PrintWriter out, String updatePswdToken, boolean err, String action, String homeaddr) {

        printHead(out);
        startBody(out);
        openContainer(out);
        printTop(out, homeaddr, messages.getString("module_name"));

        dangerMessage(out, messages.getString("security_alert"));

        StringBuilder sb = new StringBuilder(messages.getString("time_left"));
        sb.append(" : <span id='id-uys-update-pswd-counter' style='color:red;font-size:32px;'>");
        sb.append(String.valueOf(FgtPswdTokenManager.shortTermTokenTime + 1));
        sb.append("</span>");

        infomMessage(out, sb.toString());

        if (err) {
            dangerMessage(out, messages.getString("wrong_change_pswd"));
        }

        out.println(String.format("<form class='form-horizontal' method='POST' action='%s'>", action));
        out.println("     <div class='form-group'>");
        out.println("       <label class='col-md-4 control-label'>yeni parola</label>");
        out.println("       <div class='col-md-6'>");
        out.println("         <input id='re-password-field' type='password' class='form-control' name='pswd' value=''>");
        out.println("         <span toggle='#re-password-field' class='fa fa-fw fa-eye field-icon toggle-re-password'></span>");
        out.println("       </div>");
        out.println("     </div>");
        out.println("     <div class='form-group'>");
        out.println("       <label class='col-md-4 control-label'>yeni parola (tekrar)</label>");
        out.println("       <div class='col-md-6'>");
        out.println("         <input id='password-field' type='password' class='form-control' name='re-pswd' value=''>");
        out.println("         <span toggle='#password-field' class='fa fa-fw fa-eye field-icon toggle-password'></span>");
        out.println("       </div>");
        out.println("     </div>");

        out.println("     <div class='form-group'>");
        out.println("       <div class='col-md-6'>");
        out.println("         <div class='g-recaptcha' data-sitekey='6LcQzG4UAAAAACk87rF4aMb07mwTiZnldOiqWh8s'></div>");
        out.println("       </div>");
        out.println("       <div class='col-md-6'>");
        out.println("            <button id='id-update-pswd-submit-btn' style='background:#0074C8;color: #FFF;font-size: 15px;width: 100%;height: 75px;' type='submit'>");
        out.println("                <span>gönder</span>");
        out.println("            </button>");
        out.println("       </div>");
        out.println("     </div>");

        out.println(String.format("<input type='hidden' name='%s' value='%s'>", PARAM_UPDATE_PSWD_TOKEN, updatePswdToken));

        out.println("</form>");

        closeContainer(out);

        scriptPswdToggle(out);

        scriptCounter(out);

        out.println("</body>");
        out.println("</html>");

    }

    public void writeUpdatePswdOk(PrintWriter out, String homeaddr) {
        printHeadNoCaptcha(out);
        startBody(out);
        openContainer(out);
        printTop(out, homeaddr, messages.getString("module_name"));
        successMessage(out, "Parola güncellendi");
        closeContainer(out);
        out.println("</body>");
        out.println("</html>");
    }

    public void writeUpdatePswdFail(PrintWriter out, String url, String err, String homeaddr) {
        printHeadNoCaptcha(out);
        startBody(out);
        openContainer(out);
        printTop(out, homeaddr, messages.getString("module_name"));
        dangerMessage(out, "Parola güncelleme işlemi başarısız");
        dangerMessage(out, err);
        closeContainer(out);
        out.println("</body>");
        out.println("</html>");
    }

    public void writeBlocked(PrintWriter out, String homeaddr) {
        printHeadNoCaptcha(out);
        startBody(out);
        openContainer(out);
        printTop(out, homeaddr, messages.getString("module_name"));
        dangerMessage(out, "Girişiniz Engellendi");
        successMessage(out, "Sisteme giriş için TSPB : Üye İşleri Müdürlüğü ile irtibata geçiniz");
        closeContainer(out);
        out.println("</body>");
        out.println("</html>");
    }

    public void writeInvalidTokenInfo(PrintWriter out, String homeaddr) {
        printHeadNoCaptcha(out);
        startBody(out);
        openContainer(out);
        printTop(out, homeaddr, messages.getString("module_name"));
        dangerMessage(out, messages.getString("update_token_invalid"));
        closeContainer(out);
        out.println("</body>");
        out.println("</html>");
    }

    public void writeInvalidShortTermTokenInfo(PrintWriter out, String homeaddr) {
        printHeadNoCaptcha(out);

        startBody(out);

        openContainer(out);

        printTop(out, homeaddr, messages.getString("module_name"));

        dangerMessage(out, messages.getString("update_token_expired"));

        closeContainer(out);
        out.println("</body>");
        out.println("</html>");
    }

    public void writeNotifyUser(PrintWriter out, String emailContent, String homeaddr) {

        printHeadNoCaptcha(out);

        startBody(out);

        openContainer(out);

        printTop(out, homeaddr, messages.getString("module_name"));
        successMessage(out, messages.getString("update_link_sent"));
        infomMessage(out, messages.getString("check_inbox"));
        warnMessage(out, messages.getString("expired_period"));

        closeContainer(out);
        out.println("</body>");
        out.println("</html>");
    }

    public String createEmailContent(String link) {
        StringBuilder sb = new StringBuilder();
        sb.append("Sayın Kullanıcımız;");
        sb.append("<br/><br/>");
        sb.append("Sistemimizde kayıtlı olan e-posta adresiniz üzerinden parola güncelleme işlemi talebinde bulunuldu.");
        sb.append("<br/><br/>");
        sb.append("Eğer bu işlem sizin tarafınızdan yapılmadı ise bu e-posta içindeki talimatları dikkate almayınız.");
        sb.append("<br/><br/>");
        sb.append("Parolanızı güncellemek için aşağıdaki bağlantıyı kullanınız.");
        sb.append("<br/><br/>");
        sb.append("Güncelleme bağlantısı tek seferlik olup geçerlilik süresi 1 saattir.");
        sb.append("<br/><br/>");
        sb.append(String.format("<a href='%s'>%s</a>", link, link));
        return sb.toString();
    }

    private void dangerMessage(PrintWriter out, String msg) {
        out.println("<div class='row'>");
        out.println("  <div class='col-md-12'>");
        out.println("    <div class='alert alert-danger'>");
        out.println(msg);
        out.println("    </div>");
        out.println("  </div>");
        out.println("</div>");
    }

    private void warnMessage(PrintWriter out, String msg) {
        out.println("<div class='row'>");
        out.println("  <div class='col-md-12'>");
        out.println("    <div class='alert alert-warning'>");
        out.println(msg);
        out.println("    </div>");
        out.println("  </div>");
        out.println("</div>");
    }

    private void infomMessage(PrintWriter out, String msg) {
        out.println("<div class='row'>");
        out.println("  <div class='col-md-12'>");
        out.println("    <div class='alert alert-info'>");
        out.println(msg);
        out.println("    </div>");
        out.println("  </div>");
        out.println("</div>");
    }

    private void successMessage(PrintWriter out, String msg) {
        out.println("<div class='row'>");
        out.println("  <div class='col-md-12'>");
        out.println("    <div class='alert alert-success'>");
        out.println(msg);
        out.println("    </div>");
        out.println("  </div>");
        out.println("</div>");
    }

    private void linkBootstrup(PrintWriter out) {
        //out.println("<link rel='stylesheet' href='https://use.fontawesome.com/releases/v5.5.0/css/all.css' integrity='sha384-B4dIYHKNBt8Bc12p+WXckhzcICo0wtJAoU8YZTY5qE0Id1GSseTk6S+L3BlXeVIU' crossorigin='anonymous'>");
        //out.println("<link rel='stylesheet' href='https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css'>");
        out.println("<link rel='stylesheet' href='https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap.min.css'>");
        out.println("<script src='https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js'></script>");
        out.println("<script src='https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/js/bootstrap.min.js'></script>");
    }

    private void styleDivs(PrintWriter out) {
        out.println("<style>");
        out.println(style);
        out.println("</style>");
    }

    private void scriptJquery(PrintWriter out) {
        out.println("<script src='https://code.jquery.com/jquery-3.3.1.min.js'	integrity='sha256-FgpCb/KJQlLNfOu91ta32o/NMZxltwRo8QtmkMRdAu8='		  crossorigin='anonymous'></script>");
    }

    private void scriptRecaptcha(PrintWriter out) {
        out.println(" ");
        out.println("<script src=\"https://www.google.com/recaptcha/api.js?hl=tr\"></script>");
        out.println("<script type=\"text/javascript\">");
        out.println("var RecaptchaOptions = {lang: 'tr',hl: 'tr'};");
        out.println("grecaptcha.render('html_element', RecaptchaOptions);");
        out.println("</script>");
    }

    private void scriptCounter(PrintWriter out) {
        out.println("<script>");
        out.println(" function init() {");
        out.println("     var n = " + String.valueOf(FgtPswdTokenManager.shortTermTokenTime + 1) + ";");
        out.println("     e = document.getElementById('id-uys-update-pswd-counter');");
        out.println("     btn = document.getElementById('id-update-pswd-submit-btn');");
        out.println("     var intervalId = setInterval(function() {");
        out.println("       if(n>0){ ");
        out.println("            e.innerHTML = --n ;");
        out.println("       }else{");
        out.println("            clearInterval(intervalId);");
        out.println("            btn.click();");
        out.println("       }");
        out.println("     }, 1000);");
        out.println(" }");
        out.println("window.onload = init;");
        out.println("</script>");
    }

    private void scriptPswdToggle(PrintWriter out) {
        out.println("<script>");

        out.println("$('.toggle-password').click(function() {");
        out.println("  $(this).toggleClass('fa-eye fa-eye-slash');");
        out.println("  var input = $($(this).attr('toggle'));");
        out.println("  if (input.attr('type') == 'password') {");
        out.println("    input.attr('type', 'text');");
        out.println("  } else {");
        out.println("    input.attr('type', 'password');");
        out.println("  }");
        out.println("});");

        out.println("$('.toggle-re-password').click(function() {");
        out.println("  $(this).toggleClass('fa-eye fa-eye-slash');");
        out.println("  var input = $($(this).attr('toggle'));");
        out.println("  if (input.attr('type') == 'password') {");
        out.println("    input.attr('type', 'text');");
        out.println("  } else {");
        out.println("    input.attr('type', 'password');");
        out.println("  }");
        out.println("});");

        out.println("</script>");
    }

    private String createLi(String msg) {
        return new StringBuilder()
                .append("           <li style='color: #0074C8;font-weight: bold;font-size: 14px;'>")
                .append("<span>")
                .append(msg)
                .append("</span>")
                .append("</li>").toString();
    }

    private void printTop(PrintWriter out, String homeAddr, String stepName) {

        String project_name = messages.getString("project_name");

        out.println("<div class='row'>");
        out.println("    <div class='col-md-1'>");
        out.println("        <img id='img_logo' src='" + messages.getString("logo") + "'/>");
        out.println("        <h5><a href=''><span class='title-style'>" + messages.getString("website") + "</span></a></h5>");
        out.println("    </div>");
        out.println("    <div class='col-md-7'></div>");
        out.println("    <div class='col-md-4 text-right'>");
        out.println("      <a href=" + (homeAddr == null ? "" : homeAddr) + "><h5>" + project_name + "</h5></a>");
        out.println("      <h5>" + stepName + "</h5>");
        out.println("    </div>");
        out.println("</div>");

        out.println("<div class='row'>");
        out.println("  <div class='col-md-12'>");
        out.println("     <hr style='color: #0074C8;height: 2px;background-color: #0074C8;'>");
        out.println("  </div>");
        out.println("</div>");
    }

    private void homeLink(PrintWriter out, String url) {
        out.println("<div class='row'>");
        out.println(String.format("<a class='col-md-12 text-center' href='%s'>%s</a>", url, "Üye Yönetim Sistemi"));
        out.println("</div>");
    }

    private void printHeadNoCaptcha(PrintWriter out) {
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<title>TSPB : Parola Hatırlat</title>");
        linkBootstrup(out);
        scriptJquery(out);
        styleDivs(out);
        out.println("</head>");
    }

    private void printHead(PrintWriter out) {
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<title>TSPB : Parola Hatırlat</title>");
        linkBootstrup(out);
        scriptJquery(out);
        scriptRecaptcha(out);
        styleDivs(out);
        out.println("</head>");
    }

    private void closeContainer(PrintWriter out) {
        out.println("</div>");
        out.println("</div>");
        out.println("</div>");
        out.println("<div class='col-md-2'></div>");
        out.println("</div>");//row
        out.println("</div>");//container
    }

    private void startBody(PrintWriter out) {
        out.println("<body>");
    }

    private void openContainer(PrintWriter out) {
        out.println("<div class='container'>");
        out.println("<div class='row'>");
        out.println("<div class='col-md-2'></div>");
        out.println("<div class='col-md-8'>");
        out.println("<div class='panel panel-default myinnerdiv'>");
        out.println("<div class='panel-body'>");
    }

}
