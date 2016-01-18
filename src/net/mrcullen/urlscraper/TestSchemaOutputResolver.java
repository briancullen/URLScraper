package net.mrcullen.urlscraper;

import java.io.IOException;

import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

public class TestSchemaOutputResolver extends SchemaOutputResolver {

	@Override
	public Result createOutput(String namespaceUri, String suggestedFileName)
			throws IOException {
		// TODO Auto-generated method stub
		StreamResult result = new StreamResult(System.out);
		result.setSystemId("out"); 
		return result;
	}

}
