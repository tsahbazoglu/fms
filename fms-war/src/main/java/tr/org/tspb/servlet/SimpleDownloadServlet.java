package tr.org.tspb.servlet;

import java.io.*;
import java.net.URLDecoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import tr.org.tspb.common.services.BaseService;
import tr.org.tspb.service.RepositoryService;

/**
 *
 * @author Telman Şahbazoğlu
 */
@WebServlet(name = "SimpleDownloadServlet", urlPatterns = {"/fmsdownload/*"}
)
public class SimpleDownloadServlet extends HttpServlet {

    @Inject
    private RepositoryService repositoryService;

    @Inject
    private BaseService baseService;

    private String basePath;

    @Override
    public void init() throws ServletException {

        // Get base path (path to get all resources from) as init parameter.
        //this.basePath = getServletContext().getRealPath(getInitParameter("basePath"));
        this.basePath = baseService.getProperties().getTmpDownloadPath();
    }

    /**
     * Process GET request.
     *
     * @param request
     * @param response
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException
     * @see HttpServlet#doGet(HttpServletRequest, HttpServletResponse).
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        safeProcessRequest(request, response);
    }

    private void safeProcessRequest(HttpServletRequest request, HttpServletResponse response) {
        try {
            processRequest(request, response);
        } catch (IOException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String requestedFile = request.getPathInfo();

        // Check if file is actually supplied to the request URL.
        if (requestedFile == null) {
            // Do your thing if the file is not supplied to the request URL.
            // Throw an exception, or send 404, or show default/warning page, or just ignore it.
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // URL-decode the file name (might contain spaces and on) and prepare file object.
        File file = new File(basePath, URLDecoder.decode(requestedFile, "UTF-8"));

        // Check if file actually exists in filesystem.
        if (!file.exists()) {
            // Do your thing if the file appears to be non-existing.
            // Throw an exception, or send 404, or show default/warning page, or just ignore it.
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        response.reset();
        response.setHeader("Content-Disposition", "attachment" + ";filename=\"" + file.getName() + "\"");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setHeader("Pragma", "No-cache");

        OutputStream out;
        try (InputStream in = new FileInputStream(file)) {
            out = response.getOutputStream();
            byte[] buffer = new byte[4096];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
        }
        out.flush();

    }
}
