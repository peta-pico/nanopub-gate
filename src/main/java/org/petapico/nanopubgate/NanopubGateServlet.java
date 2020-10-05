package org.petapico.nanopubgate;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.nanopub.Nanopub;
import org.nanopub.NanopubImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NanopubGateServlet extends HttpServlet {

	private static final long serialVersionUID = 0L;

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		setGeneralHeaders(resp);
		Nanopub np = null;
		try {
			np = new NanopubImpl(req.getInputStream(), Rio.getParserFormatForMIMEType(req.getContentType()).orElse(RDFFormat.TRIG));
		} catch (Exception ex) {
			resp.sendError(400, "Error reading nanopub: " + ex.getMessage());
		}
		if (np != null) {
			// TODO
		}
	}

	@Override
	protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		super.doOptions(req, resp);
		setGeneralHeaders(resp);
	}

	@Override
	public void init() throws ServletException {
		logger.info("Init");
		// Code to be run at start-up can go here
	}

	private void setGeneralHeaders(HttpServletResponse resp) {
		resp.setHeader("Access-Control-Allow-Origin", "*");
	}

}
