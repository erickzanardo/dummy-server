package org.dummy.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.ScriptableObject;

public class DummyServicesHandler extends AbstractHandler {

    private ImporterTopLevel global;
    private String servicesFolder;

    public DummyServicesHandler(String servicesFolder) {
        this.servicesFolder = servicesFolder;
        Context cx = Context.enter();

        global = new ImporterTopLevel(cx);

        String services = readFile("services.js");

        Script servicesScript = cx.compileString(services, "services.js", 1, null);
        servicesScript.exec(cx, global);

        Context.exit();
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {

        String method = req.getMethod().toLowerCase();
        String requestURI = req.getRequestURI();
        try {
            Context cx = Context.enter();

            String scriptString = readScriptFile(requestURI);
            if (scriptString != null) {

                ScriptableObject scriptableObject = (ScriptableObject) cx.newObject(global);
                scriptableObject.setParentScope(global);

                Script script = cx.compileString(scriptString, requestURI, 1, null);
                script.exec(cx, scriptableObject);

                Function f = (Function) global.get("doService");

                f.call(cx, global, scriptableObject,
                        new Object[] { scriptableObject.get("service"), req, resp, method });
                resp.setStatus(HttpServletResponse.SC_OK);
                baseRequest.setHandled(true);
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            if (e.getMessage() != null && e.getMessage().startsWith("unsupported")) {
                throw new UnsupportedOperationException("Method " + method + " not allowed in this url " + requestURI);
            } else {
                throw new RuntimeException(e);
            }
        } finally {
            Context.exit();
        }

    }

    private String readFile(String path) {
        InputStream resourceAsStream = DummyServicesHandler.class.getResourceAsStream(path);
        return readStream(resourceAsStream);
    }

    private static String readStream(InputStream resourceAsStream) {
        if (resourceAsStream == null) {
            throw new RuntimeException("File not found to import");
        }

        BufferedReader br = null;
        StringBuilder ret = new StringBuilder();

        try {
            String line;
            br = new BufferedReader(new InputStreamReader(resourceAsStream));

            while ((line = br.readLine()) != null) {
                ret.append(line).append(System.getProperty("line.separator"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)
                    br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return ret.toString();
    }

    private String readScriptFile(String requestURI) {
        String url = requestURI;
        while (url.indexOf('/') != -1) {
            url = url.replace('/', '\\');
        }
        String path = servicesFolder + url + ".js";
        File file = new File(path);

        if (file.exists()) {
            try {
                FileInputStream inputStream = new FileInputStream(file);
                return readStream(inputStream);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        return null;
    }

}
