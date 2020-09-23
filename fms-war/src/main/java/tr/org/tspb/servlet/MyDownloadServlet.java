package tr.org.tspb.servlet;

import java.io.*;
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
import tr.org.tspb.dao.MyFile;

/**
 *
 * @author Telman Şahbazoğlu
 */
@WebServlet(name = "MyDownloadServlet", urlPatterns = {"/myDownloadServlet/*"}
)
public class MyDownloadServlet extends HttpServlet {

    @Inject
    private RepositoryService repositoryService;

    @Inject
    private BaseService baseService;

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

        String objectIdStr = request.getPathInfo().substring(1);
        MyFile file = repositoryService
                .findFileAsMyFileInputStream(baseService.getProperties().getUploadTable(), objectIdStr);

        response.reset();
        response.setHeader("Content-Disposition", "attachment" + ";filename=\"" + file.getName() + "\"");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setHeader("Pragma", "No-cache");

        OutputStream out;
        try (InputStream in = file.getInputStream()) {
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
